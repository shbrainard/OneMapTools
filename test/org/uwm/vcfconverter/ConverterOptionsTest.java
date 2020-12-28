package org.uwm.vcfconverter;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
				"output_file=outputFile", "data_type=outcross", "only_phased=tRue", "types_to_keep=B3.7,B1.10",
				"verify_uniform_offspring=true"};
		opts = ConverterOptions.loadOptions(args);
		Set<MarkerType> types = new HashSet<>();
		types.add(MarkerType.B37);
		types.add(MarkerType.B110);
		assertEquals(opts.getToKeep(), types);
		assertEquals(opts.isOnlyPhased(), true);
		assertEquals(opts.shouldVerify(), true);
	}
}
