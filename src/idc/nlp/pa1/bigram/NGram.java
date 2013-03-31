package idc.nlp.pa1.bigram;

import idc.nlp.pa1.Utils;

public class NGram implements Comparable<NGram> {
	private final String gram;
	private final int count;
	private final int conditionalCount;
	private final double logProb;
	private final int size;

	public NGram(String gram, int size, int count, int conditionalCount) {
		this.gram = gram;
		this.count = count;
		this.size = size;
		this.conditionalCount = conditionalCount;
		this.logProb = Math.log10((double) count / (double) conditionalCount);
	}

	public NGram(String gram, int size, double logProb) {
		this.gram = gram;
		this.size = size;
		this.count = -1;
		this.conditionalCount = -1;
		this.logProb = logProb;
	}

	public String getGram() {
		return gram;
	}

	public int getCount() {
		return count;
	}

	public int getConditionalCount() {
		return conditionalCount;
	}

	public double getLogProb() {
		return logProb;
	}

	public String toString() {
		return String.format("%s\t%s", Utils.doubleToString(getLogProb()), getGram());
	}

	@Override
	public int compareTo(NGram o) {
		return gram.compareToIgnoreCase(o.gram);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((gram == null) ? 0 : gram.hashCode());
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
		NGram other = (NGram) obj;
		if (gram == null) {
			if (other.gram != null)
				return false;
		} else if (!gram.equals(other.gram))
			return false;
		return true;
	}

}