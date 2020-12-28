package org.uwm.vcfconverter;

import java.util.HashMap;
import java.util.Map;

public class Metadata {
   private final Pair parentCols;
   private final int nIndividuals;
   private int nLines = 0;
   private int numFiltered = 0;
   private int numNoMatch = 0;
   private int numFilteredType = 0;
   private final String[] headers;
   private Map<Integer, Integer> badMatch = new HashMap<>();
   
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
	   return "Filtered due to VCF status: " + numFiltered + " Filtered due to type: " + numFilteredType + " Filtered due to no match: " + numNoMatch
			   + "\nBad match counts: " + badMatch;
   }

   public void incBadMatch(int index) {
	   badMatch.put(index, badMatch.computeIfAbsent(index, _unused -> 0) + 1);
   }
}
