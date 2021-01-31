package org.uwm.vcfconverter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

public class ConverterOptionsTest {
	
	@Test
	public void testOptions() throws Exception {
		String[] args = new String[] {"Female_parent=I_fem_par", "male_parent=I_male_par", "vcf_file=inputFile",
				"output_file=outputFile", "data_type=outcross"};
		Set<MarkerType> typesToKeep = new HashSet<>();
		for (MarkerType markerType : MarkerType.values()) {
			typesToKeep.add(markerType);
		}
		typesToKeep.remove(MarkerType.HOMOZYGOUS);
		typesToKeep.remove(MarkerType.EACH_HOMOZYGOUS);
		ConverterOptions opts = ConverterOptions.loadOptions(args);
		assertEquals(opts.getDataType(), "outcross");
		assertEquals(opts.getFemaleParentName(), "I_fem_par");
		assertEquals(opts.getMaleParentName(), "I_male_par");
		assertEquals(opts.getInputFile(), "inputFile");
		assertEquals(opts.getOutputFile(), "outputFile");
		assertEquals(opts.getToKeep(), typesToKeep);
		assertEquals(opts.isOnlyPhased(), false);
		assertEquals(opts.shouldVerify(), false);
		
		args = new String[] {"female_parent=I_fem_par", "male_parent=I_male_par", "vcf_file=inputFile",
				"output_file=outputFile", "data_type=outcross", "only_phased=tRue", "types_to_keep=B3.7,D1.10",
				"verify_uniform_offspring=true"};
		opts = ConverterOptions.loadOptions(args);
		Set<MarkerType> types = new HashSet<>();
		types.add(MarkerType.B37);
		types.add(MarkerType.D110);
		assertEquals(opts.getToKeep(), types);
		assertEquals(opts.isOnlyPhased(), true);
		assertEquals(opts.shouldVerify(), true);
	}
	
	@Test
	public void testOptionsFile() throws Exception {
		File temp = File.createTempFile("test", "file");
		temp.deleteOnExit();
		String[] args = new String[] {"Female_parent=I_fem_par", "male_parent=I_male_par", "vcf_file=inputFile",
				"output_file=outputFile", "data_type=outcross"};
		try (BufferedWriter out = new BufferedWriter(new FileWriter(temp))) {
			for (String arg : args) {
				out.write(arg);
				out.newLine();
			}
		}
		Set<MarkerType> typesToKeep = new HashSet<>();
		for (MarkerType markerType : MarkerType.values()) {
			typesToKeep.add(markerType);
		}
		typesToKeep.remove(MarkerType.HOMOZYGOUS);
		typesToKeep.remove(MarkerType.EACH_HOMOZYGOUS);
		ConverterOptions opts = ConverterOptions.loadOptions(new String[] {temp.getAbsolutePath()});
		assertEquals(opts.getDataType(), "outcross");
		assertEquals(opts.getFemaleParentName(), "I_fem_par");
		assertEquals(opts.getMaleParentName(), "I_male_par");
		assertEquals(opts.getInputFile(), "inputFile");
		assertEquals(opts.getOutputFile(), "outputFile");
		assertEquals(opts.getToKeep(), typesToKeep);
		assertEquals(opts.isOnlyPhased(), false);
		assertEquals(opts.shouldVerify(), false);
		
		args = new String[] {"female_parent=I_fem_par", "male_parent=I_male_par", "vcf_file=inputFile",
				"output_file=outputFile", "data_type=outcross", "only_phased=tRue", "types_to_keep=B3.7,D1.10",
				"verify_uniform_offspring=true"};
		try (BufferedWriter out = new BufferedWriter(new FileWriter(temp))) {
			for (String arg : args) {
				out.write(arg);
				out.newLine();
			}
		}
		opts = ConverterOptions.loadOptions(new String[] {temp.getAbsolutePath()});
		Set<MarkerType> types = new HashSet<>();
		types.add(MarkerType.B37);
		types.add(MarkerType.D110);
		assertEquals(opts.getToKeep(), types);
		assertEquals(opts.isOnlyPhased(), true);
		assertEquals(opts.shouldVerify(), true);
	}
}
