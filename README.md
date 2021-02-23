# OneMapTools
Tools for preparing a VCF file for use in the OneMap pipeline.  Accepts arguments in standard java.util.Properties format, i.e., one per line: <argumentName>=<argumentValue>.

 *Required values:*
 1) ```female_parent```: ID of the female parent, as encoded in the VCF file
 2) ```male_parent```: ID of the male parent, as encoded in the VCF file
 3) ```vcf_file```: absolute path of the VCF file (can be gzipped) 
 4) ```output_file```: absolute path to use for the OneMap data file and .log files 
 5) ```data_type```: cross type (f2 backcross, f2 intercross, ri self, ri sib, or outcross)

 *Optional values:*
 1) ```only_phased```: retain only markers that are already phased in the VCF file (true/false, default=false)
 2) ```verify_uniform_offspring```: check all aa x aa and aa x bb segregation types, and record violations of expected progeny genotypes (i.e., "aa" and "ab", respectively), in the .log file
 3) ```types_to_keep```: comma separated list of segregation types to retain, when left blank, all segregation types are retained
 4) ```log_filtered_markers```: false by default.  Even when false, some summary statistics about how many markers were filtered and why.  If set to true, separate .log files will be generated for markers that are filtered due to:

- having a filter status other than `PASS` in the VCF file
- being a filtered type (by default, just homozygous aa x aa)
- missing data for a parent
- failure of the converter to read the line in the VCF file (generally, indicates a bug)

bad.log is organized by individuals and badMarkers.log is organized by markers.

To run the tool, compile source code into converter.jar.  Example usage:
```bash
export vcfFile=/PATH/TO/VCF
export femaleParent=femaleID
export maleParent=maleID
export outputFile=/PATH/TO/OUTPUT/FILE
java -cp converter.jar org.uwm.vcfconverter.Converter female_parent=$femaleParent male_parent=$maleParent vcf_file=$vcfFile output_file=$outputFile data_type=outcross verify_uniform_offspring=true log_filtered_markers=true
```

You can also store all defined arguments (e.g., `vcf_file=/PATH/TO_VCF`, etc.) in a .config file, with one variable per line, and pass the path to the .config file as the single argument to converter.jar:

```bash
java -cp converter.jar org.uwm.vcfconverter.Converter example.config
```
