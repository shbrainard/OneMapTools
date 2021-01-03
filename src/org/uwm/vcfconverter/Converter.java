package org.uwm.vcfconverter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Optional;
import java.util.Set;

public class Converter {
	
	private static final String SEP_CHAR = " "; // just in case they ever decide to not make it a space-separated file
	private static final int NUM_NON_DATA_HEADERS = 9;
	private static final int REPORT_FREQUENCY = 1000;

	public static void main(String[] args) throws Exception {
	   ConverterOptions opts = loadOptions(args);

	   Metadata metadata = null;
	   File tempFile = new File(opts.getOutputFile() + "-temp");
	   tempFile.deleteOnExit();
	   try (BufferedWriter out = new BufferedWriter(new FileWriter(tempFile));
			   BufferedWriter verbose = new BufferedWriter(new FileWriter(opts.getInputFile() + ".filtered.log"));
			   BufferedReader in = new BufferedReader(opts.getInputReader())) {
		   String line;
		   String lastHeader = null;
		   
		   while ((line = in.readLine()) != null) {
			   if (line.startsWith("#")) {
				   lastHeader = line;
				   continue;
			   } else if (metadata == null) {
				   metadata = extractMetadata(opts, lastHeader);
			   }
			   String[] lineParts = line.split("\t");
			   if (lineParts.length < 3) {
				   continue; // blank line at end of file
			   }
			   String id = lineParts[2];
			   if (id.equals(".")) {
				   id = lineParts[0] + "-" + lineParts[1] + "-" + metadata.getNLines();
			   }
			   writeMarker(id, lineParts, metadata.getParentCols(), opts, out, metadata, verbose);
			   if (metadata.getNLines() % REPORT_FREQUENCY == 0) {
				   System.out.println("Processed " + metadata.getNLines());
			   }
		   }
	   }
	   
	   prependHeader(opts, metadata);
	   
	   System.out.println("Completed conversion from " + opts.getInputFile() + " to " + opts.getOutputFile() + ".");
	   System.out.println("First parent is " + opts.getFemaleParentName() + ", second parent is " + opts.getMaleParentName() + ".");
	   System.out.println("Filtered data: " + metadata.getStatus());

	   String logFile = opts.getInputFile() + ".bad.log";
	   metadata.logBadMatches(logFile);
	   System.out.println("Individuals and markers failing validation stored at " + logFile);

	   if (opts.logFiltering()) {
		   System.out.println("Filtered marker details stored at " + opts.getInputFile() + ".filtered.log");
	   }
	}

	private static Metadata extractMetadata(ConverterOptions opts, String lastHeader) throws IOException {
		Metadata metadata;
		String[] headers = lastHeader.split("\t");

		   Pair parentCols = getParentCols(headers, opts);

		   int nIndividuals = headers.length - NUM_NON_DATA_HEADERS;
		   nIndividuals -=2; // we're not going to include the parents

		   metadata = new Metadata(parentCols, nIndividuals, headers);
		return metadata;
	}

	private static void prependHeader(ConverterOptions opts, Metadata metadata) throws IOException {
		try (BufferedWriter out = new BufferedWriter(opts.getOutputWriter());
				BufferedReader in = new BufferedReader(new FileReader(opts.getOutputFile() + "-temp"))) {
			out.write("data type " + opts.getDataType());
			out.newLine();

			out.write(metadata.getNIndividuals() + SEP_CHAR + metadata.getNLines() + SEP_CHAR + "0"  + SEP_CHAR + "0" + SEP_CHAR + "0");
			out.newLine();
			StringBuffer names = new StringBuffer();
			for (int i = NUM_NON_DATA_HEADERS; i < metadata.getHeaders().length; i++) {
				String header =  metadata.getHeaders()[i];
				if (!header.equalsIgnoreCase(opts.getFemaleParentName()) && !header.equalsIgnoreCase(opts.getMaleParentName())) {
					names.append(header);
					names.append(SEP_CHAR);
				}
			}
			out.write(names.substring(0, names.length() - 1));
			out.newLine();
			
			String line;
			while ((line = in.readLine()) != null) {
				out.write(line);
				out.newLine();
			}
		}
	}

	private static ConverterOptions loadOptions(String[] args) {
		ConverterOptions opts = ConverterOptions.loadOptions(args);
		System.out.println("Running with options: " + opts);
		return opts;
	}

	// aa x ab, ab x ab, ab x aa
	private static void writeMarker(String id, String[] data, Pair parentCols, ConverterOptions opts, BufferedWriter out,
			Metadata metadata, BufferedWriter verbose) throws IOException {
		Optional<Pair> firstParentVal = getVal(data, parentCols.getLhSide());
		Optional<Pair> secondParentVal = getVal(data, parentCols.getRhSide());
		if (!firstParentVal.isPresent() || !secondParentVal.isPresent()) {
			metadata.incNoParentData();
			logIfNeeded(opts.logFiltering(), verbose, id, "Missing parent data");
			return;
		}
		if (!data[6].equals("PASS")) {
			metadata.incNumFiltered();
			logIfNeeded(opts.logFiltering(), verbose, id, "VCF filter: " + data[6]);
			return;
		}
		
		Optional<CodingMapperSpec> optMapper = CodingMapper.create(firstParentVal.get(), secondParentVal.get());
		if (!optMapper.isPresent()) {
			metadata.incNoMatch();
			logIfNeeded(opts.logFiltering(), verbose, id, "Unable to parse line");
			return;
		}
		CodingMapper mapper = optMapper.get().mapper;
		MarkerType type =  optMapper.get().markerType;
	
		if (shouldFilter(type, opts)) {
			if (shouldVerify(opts)) {
				runVerification(id, data, parentCols, type, mapper, metadata);
			}
			metadata.incFilteredType();
			logIfNeeded(opts.logFiltering(), verbose, id, "Filtered type " + type);
			return;
		}
		
		StringBuffer output = new StringBuffer("*").append(id).append(SEP_CHAR).append(type.getDisplayString());
		for (int i = NUM_NON_DATA_HEADERS; i < data.length; i++) {
			if (i == parentCols.getLhSide() || i == parentCols.getRhSide()) {
				continue; // don't print the parents
			}
			Optional<Pair> dataVal = getVal(data, i);
			String converted;
			if (dataVal.isPresent()) {
				if (opts.isOnlyPhased() && data[i].charAt(1) == '/') {
					metadata.incNumFiltered();
					logIfNeeded(opts.logFiltering(), verbose, id, "Filtering unphased data");
					return; // skip this marker, it had unphased data
				}
				converted = mapper.map(dataVal.get());
				if (!type.getValidChildren().contains(converted)) {
					metadata.incBadMatch(i, id, type.getValidChildren().toString(), converted);
					converted = "-";
				}
			} else {
				converted = "-";
			}
			output.append(SEP_CHAR).append(converted);
		}
		metadata.incLines();
		out.write(output.toString());
		out.newLine();
	}

	private static void logIfNeeded(boolean logFiltering, BufferedWriter verbose, String id, String reason) throws IOException {
		if (logFiltering) {
			verbose.write(id + ": " + reason);
			verbose.newLine();
		}
	}

	private static boolean shouldVerify(ConverterOptions opts) {
		return opts.shouldVerify();
	}

	private static void runVerification(String id, String[] data, Pair parentCols, MarkerType type, CodingMapper mapper,
			Metadata metadata) {
		Set<String> expected = type.getValidChildren();
		for (int i = NUM_NON_DATA_HEADERS; i < data.length; i++) {
			if (i == parentCols.getLhSide() || i == parentCols.getRhSide()) {
				continue; // don't print the parents
			}
			Optional<Pair> dataVal = getVal(data, i);
			if (dataVal.isPresent()) {
				String converted = mapper.map(dataVal.get());
				if (!expected.contains(converted)) {
					metadata.incBadMatch(i, id, expected.toString(), converted);
				}
			}
		}
	}

	private static boolean shouldFilter(MarkerType type, ConverterOptions opts) {
		return !opts.getToKeep().contains(type);
	}

	private static Optional<Pair> getVal(String[] data, int index) {
		String unparsed = data[index];
		if (unparsed.charAt(0) == '.' || unparsed.charAt(2) == '.') {
			return Optional.empty(); // missing data
		}
		return Optional.of(new Pair(Integer.parseInt("" + unparsed.charAt(0)),  Integer.parseInt("" + unparsed.charAt(2))));
	}

	private static Pair getParentCols(String[] headers, ConverterOptions opts) throws IOException {
		int firstCol = -1;
		int secondCol = -1;
		for (int i = 0; i < headers.length; i++) {
			String header = headers[i];
			if (header.equalsIgnoreCase(opts.getFemaleParentName())) {
				firstCol = i;
			}
			if (header.equalsIgnoreCase(opts.getMaleParentName())) {
				secondCol = i;
			}
		}
		return new Pair(firstCol, secondCol);
	}
}
