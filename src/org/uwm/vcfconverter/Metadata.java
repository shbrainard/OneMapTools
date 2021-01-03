package org.uwm.vcfconverter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Metadata {
   private final Pair parentCols;
   private final int nIndividuals;
   private int nLines = 0;
   private int numFiltered = 0;
   private int numNoMatch = 0;
   private int numFilteredType = 0;
   private final String[] headers;
   private Map<String, Set<String>> badMatch = new HashMap<>();
   private int noParentData = 0;
   
   public Metadata(Pair parentCols, int nIndividuals, String[] headers) {
	   this.parentCols = parentCols;
	   this.nIndividuals = nIndividuals;
	   this.headers = headers;
   }
   
   public String[] getHeaders() {
	   return headers;
   }
   
   public int getNIndividuals() {
	   return nIndividuals;
   }
   
   public void incLines() {
	   nLines++;
   }

   public Pair getParentCols() {
	   return parentCols;
   }

   public int getNLines() {
	   return nLines;
   }
   
   public void incNumFiltered() {
	   numFiltered++;
   }
   
   public void incNoMatch() {
	   numNoMatch++;
   }
   
   public void incFilteredType() {
	   numFilteredType++;
   }
   
   public String getStatus() {
	   return "Filtered due to VCF status: " + numFiltered + " Filtered due to type: " + numFilteredType + " Filtered due to mising parent data: " + noParentData + " Filtered due to unable to parse line: " + numNoMatch;
   }

   public void incBadMatch(int index, String marker, String expected, String found) {
	  badMatch.computeIfAbsent(headers[index], _unused -> new HashSet<>()).add(marker + "[expected: " + expected + ", found: " + found + "]");
   }

   public void logBadMatches(String logFile) throws IOException {
	   try (BufferedWriter buff = new BufferedWriter(new FileWriter(logFile))) {
		   badMatch.forEach((id, marker) -> {
			   try {
				   List<String> sorted = new ArrayList<>(marker);
				   Collections.sort(sorted);
				   buff.write("individual: " + id + " count: " + sorted.size() + " markers: " + sorted);
				   buff.newLine();
			   } catch (IOException e) {
				   throw new RuntimeException(e);
			   }
		   });
	   }
   }

   public void incNoParentData() {
	  noParentData++;
   }
}
