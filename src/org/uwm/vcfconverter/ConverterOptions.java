package org.uwm.vcfconverter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class ConverterOptions {

	private final String femaleParentName;
	private final String maleParentName;
	private final String inputFile;
	private final String outputFile;
	private final Set<MarkerType> toKeep;
	private final String dataType;
	private final boolean onlyPhased;
	private final boolean shouldVerify;
	private final boolean logFiltering;
	
	public static ConverterOptions loadOptions(String[] args) throws IOException {
		Map<String, String> props = new HashMap<>();
		
		if (args.length < 2) {
			loadFromFile(args[0], props);
		} else {
			loadFromCommandLine(args, props);
		}
		Set<MarkerType> typesToKeep = new HashSet<>();
		if (props.containsKey("types_to_keep")) {
			String[] types = props.get("types_to_keep").split(",");
			for (String type : types) {
				for (MarkerType markerType : MarkerType.values()) {
					if (type.toLowerCase().equals(markerType.getDisplayString().toLowerCase())) {
						typesToKeep.add(markerType);
					}
				}
			}
		} else {
			for (MarkerType markerType : MarkerType.values()) {
				typesToKeep.add(markerType);
			}
			typesToKeep.remove(MarkerType.HOMOZYGOUS);
			typesToKeep.remove(MarkerType.EACH_HOMOZYGOUS);
		}
		return new ConverterOptions(props.get("female_parent"), props.get("male_parent"), props.get("vcf_file"), 
				props.get("output_file"), typesToKeep, props.get("data_type"), 
				props.containsKey("only_phased") ? Boolean.parseBoolean(props.get("only_phased")) : false,
				props.containsKey("verify_uniform_offspring") ? Boolean.parseBoolean(props.get("verify_uniform_offspring")) : false,
				props.containsKey("log_filtered_markers") ? Boolean.parseBoolean(props.get("log_filtered_markers")) : false);
	}

	private static void loadFromFile(String file, Map<String, String> props) throws IOException {
		List<String> allPropLines = Files.readAllLines(Paths.get(file));
		for (String arg : allPropLines) {
			String[] parsed = arg.split("=");
			props.put(parsed[0].toLowerCase(), parsed[1]);
		}		
	}

	private static void loadFromCommandLine(String[] args, Map<String, String> props) {
		for (String arg : args) {
			String[] parsed = arg.split("=");
			props.put(parsed[0].toLowerCase(), parsed[1]);
		}		
	}
	
	private ConverterOptions(String femaleParentName, String maleParentName, String inputFile, String outputFile,
			Set<MarkerType> toKeep, String dataType, boolean onlyPhased, boolean shouldVerify, boolean logFiltering) {
		this.femaleParentName = femaleParentName;
		this.maleParentName = maleParentName;
		this.inputFile = inputFile;
		this.outputFile = outputFile;
		this.toKeep = toKeep;
		this.dataType = dataType;
		this.onlyPhased = onlyPhased;
		this.shouldVerify = shouldVerify;
		this.logFiltering = logFiltering;
	}
	
	public String getFemaleParentName() {
		return femaleParentName;
	}

	public String getMaleParentName() {
		return maleParentName;
	}

	public InputStreamReader getInputReader() throws IOException {
		InputStream is = new FileInputStream(inputFile);
		if (inputFile.endsWith(".gz")) {
			is = new GZIPInputStream(is);
		}
		return new InputStreamReader(is);
	}

	public OutputStreamWriter getOutputWriter() throws IOException {
		OutputStream os = new FileOutputStream(outputFile);
		if (outputFile.endsWith(".gz")) {
			os = new GZIPOutputStream(os);
		}
		return new OutputStreamWriter(os);
	}

	public Set<MarkerType> getToKeep() {
		return toKeep;
	}

	public String getDataType() {
		return dataType;
	}

	public boolean isOnlyPhased() {
		return onlyPhased;
	}

	@Override
	public String toString() {
		return "ConverterOptions [femaleParentName=" + femaleParentName + ", maleParentName=" + maleParentName
				+ ", inputFile=" + inputFile + ", outputFile=" + outputFile + ", toKeep=" + toKeep + ", dataType="
				+ dataType + ", onlyPhased=" + onlyPhased + ", shouldVerify=" + shouldVerify + ", logFiltering="
				+ logFiltering + "]";
	}

	public String getInputFile() {
		return inputFile;
	}

	public String getOutputFile() {
		return outputFile;
	}

	public boolean shouldVerify() {
		return shouldVerify;
	}

	public boolean logFiltering() {
		return logFiltering;
	}
	
}
