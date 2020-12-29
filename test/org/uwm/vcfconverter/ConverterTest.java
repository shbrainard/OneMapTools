package org.uwm.vcfconverter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;


public class ConverterTest {

	@Test
	public void testConverter() throws Exception {
		String[] args = new String[] {"Female_parent=I_fem_par", "male_parent=I_male_par", "vcf_file=test_input.txt",
				"output_file=test_output.txt", "data_type=outcross", "verify_uniform_offspring=true"};
		Converter.main(args);
		List<String> lines = Files.readAllLines(Paths.get("test_output.txt"));
		List<String> expected = new ArrayList<>();
		expected.add("data type outcross");
		expected.add("3 3 0 0 0");
		expected.add("I1 I2 I3");
		expected.add("*first B3.7 a ab b");
		expected.add("*second D1.10 a ab a");
		expected.add("*third D2.15 ab ab b");
		
		assertEquals(lines, expected);
	}
}
