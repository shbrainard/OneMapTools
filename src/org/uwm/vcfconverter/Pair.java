package org.uwm.vcfconverter;

public class Pair {

	private final int lhSide;
	private final int rhSide;
	
	public Pair(int lhSide, int rhSide) {
		this.lhSide = lhSide;
		this.rhSide = rhSide;
	}
	
	public int getLhSide() {
		return lhSide;
	}
	
	public int getRhSide() {
		return rhSide;
	}
	
	@Override
	public String toString() {
		return "Pair [lhSide=" + lhSide + ", rhSide=" + rhSide + "]";
	}	
}
