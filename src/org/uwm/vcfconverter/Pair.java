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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + lhSide;
		result = prime * result + rhSide;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Pair other = (Pair) obj;
		if (lhSide != other.lhSide)
			return false;
		if (rhSide != other.rhSide)
			return false;
		return true;
	}
}
