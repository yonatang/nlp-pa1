package idc.nlp.pa1.ngram;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import com.google.common.base.Preconditions;

public class PosEmission implements Comparable<PosEmission> {

	private final String seg;
	private final Map<String, Double> posToLogProb = new TreeMap<>();

	PosEmission(String seg) {
		Preconditions.checkNotNull(seg);
		this.seg = seg;
	}

	void addPos(String pos, Double logProb) {
		posToLogProb.put(pos, logProb);
	}

	Map<String, Double> getPosToLogProb() {
		return Collections.unmodifiableMap(posToLogProb);
	}

	public String getSeg() {
		return seg;
	}

	@Override
	public int compareTo(PosEmission o) {
		return seg.compareTo(o.seg);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((seg == null) ? 0 : seg.hashCode());
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
		PosEmission other = (PosEmission) obj;
		if (seg == null) {
			if (other.seg != null)
				return false;
		} else if (!seg.equals(other.seg))
			return false;
		return true;
	}

}
