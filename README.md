# OneMapTools
Tools for preparing a VCF file for use in the OneMap pipeline

To run the tool:
Compile the source code into converter.jar
java -cp converter.jar org.uwm.vcfconverter.Converter female_parent=\<header\> male_parent=\<header\> vcf_file=\<vcf file\> output_file=\<output file\> data_type=\<data type\>

other options:
only_phased=true
verify_uniform_offspring=true
types_to_keep=\<comma separated list of types, right now just B3.7, D1.10, and D2.15\>

After generating the one map file, the tool will print how many markers were filtered, and why:
- due to having a filter status other than PASS in the VCF file
- due to being a filtered type (by default, just homozygous aa x aa)
- due to a different failure of the converter to read the line in the VCF file (generally, indicates a bug)
