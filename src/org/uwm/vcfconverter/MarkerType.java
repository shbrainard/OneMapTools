package org.uwm.vcfconverter;

public enum MarkerType {
	HOMOZYGOUS("Homozygous", "aaaa"),
	B37("B3.7", "abab"), // ab x ab
	B110("B1.10", "abaa"), // ab x aa
	D215("D2.15", "aaab"), // aa x ab
	D214("D2.14", "ccab"); // cc x ab, unlikely to be found in current VCF data
	
	private final String displayString;
	private final String matchingString;
	
	private MarkerType(String displayString, String matchingString) {
		this.displayString = displayString;
		this.matchingString = matchingString;
	}
	
	public String getDisplayString() {
		return displayString;
	}
	
	public String getMatchingString() {
		return matchingString;
	}

}
