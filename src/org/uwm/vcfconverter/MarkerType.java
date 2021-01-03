package org.uwm.vcfconverter;

import java.util.HashSet;
import java.util.Set;

public enum MarkerType {
	HOMOZYGOUS("Homozygous", "aaaa", "a"),
	EACH_HOMOZYGOUS("Each homozygous", "aabb", "ab"),
	B37("B3.7", "abab", "a", "ab", "b"), // ab x ab
	D110("D1.10", "abaa", "a", "ab"), // ab x aa
	D215("D2.15", "aaab", "a", "ab"), // aa x ab
	A2("A.2", "abac", "a", "ba", "bc"), // ab x ac, and no, sadly, that 'ba' is not a typo
	A1("A.1", "abcd", "ac", "ad", "bc", "bd"), // ab x cd
	D214("D2.14", "ccab", "ac", "bc"); // cc x ab, unlikely to be found in current VCF data
	
	private final String displayString;
	private final String matchingString;
	private final Set<String> allowedChildren;
	
	private MarkerType(String displayString, String matchingString, String...allowedChildren) {
		this.displayString = displayString;
		this.matchingString = matchingString;
		this.allowedChildren = new HashSet<>();
		for (String child : allowedChildren) {
			this.allowedChildren.add(child);
		}
	}
	
	public String getDisplayString() {
		return displayString;
	}
	
	public String getMatchingString() {
		return matchingString;
	}

	Set<String> getValidChildren() {
		return allowedChildren;
	}

}
