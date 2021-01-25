package org.uwm.vcfconverter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class MarkerErrorInfo {

	private final String markerId;
	private final MarkerType type;
	private Map<String, Set<String>> badGenotypeToInd = new HashMap<>();
	
	public MarkerErrorInfo(String id, MarkerType type) {
		this.markerId = id;
		this.type = type;
	}
	
	public void incBadMatch(String individual, String found) {
		badGenotypeToInd.computeIfAbsent(found, _unused -> new HashSet<>()).add(individual);
	}

	public void logMarkerMismatch(BufferedWriter out) throws IOException {
		if (!badGenotypeToInd.isEmpty()) {
			out.write("Marker:" + markerId + " Segregation type " + type + ", but found (type, number of progeny) genotypes: ");
			for (Entry<String, Set<String>> entry : badGenotypeToInd.entrySet()) {
				out.write("(" + entry.getKey() + ", " + entry.getValue().size() + ") ");
			}
			out.write("Progeny ids by genotype found: ");
			for (Entry<String, Set<String>> entry : badGenotypeToInd.entrySet()) {
				List<String> sorted = new ArrayList<>(entry.getValue());
				Collections.sort(sorted);
				out.write("(" + entry.getKey() + ", " + sorted + ") ");
			}
			out.newLine();
		}
	}

}
