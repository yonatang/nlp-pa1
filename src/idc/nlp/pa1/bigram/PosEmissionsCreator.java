package idc.nlp.pa1.bigram;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

public class PosEmissionsCreator {

	private final Map<String, Multiset<String>> wordTagCounter = new HashMap<>();
	private final Multiset<String> globalTagCounter = HashMultiset.create();
	private final boolean smoothing;

	public PosEmissionsCreator(boolean smoothing) {
		this.smoothing = smoothing;
	}

	public void addPos(String seg, String pos) {
		Multiset<String> tagCounter;
		if (!wordTagCounter.containsKey(seg)) {
			tagCounter = HashMultiset.create();
			wordTagCounter.put(seg, tagCounter);
		} else {
			tagCounter = wordTagCounter.get(seg);
		}
		tagCounter.add(pos);
		globalTagCounter.add(pos);
	}

	private double getLogProb(String seg, String pos) {
		Multiset<String> tagCounter = wordTagCounter.get(seg);
		if (tagCounter == null) // SMOOTHING REQUIRED!
			return Double.NEGATIVE_INFINITY;

		int count = tagCounter.count(pos);
		if (count == 0) // AGAIN SMOOTHING
			return Double.NEGATIVE_INFINITY;

		return Math.log10((double) count / (double) globalTagCounter.count(pos));
	}

	public PosEmissions create() {
		PosEmissions emissions = new PosEmissions(smoothing);
		for (Entry<String, Multiset<String>> e : wordTagCounter.entrySet()) {
			String seg = e.getKey();
			StringBuilder sb = new StringBuilder();

			PosEmission emission = new PosEmission(seg);
			emissions.addEmission(emission);
			sb.append(seg).append(" ");
			// Multiset<String> tagCounter = e.getValue();
			for (com.google.common.collect.Multiset.Entry<String> tagEntry : globalTagCounter.entrySet()) {
				String pos = tagEntry.getElement();
				double logProb = getLogProb(seg, pos);
				if (!Double.isInfinite(logProb)) {
					emission.addPos(pos, logProb);
					// sb.append(pos).append(" ").append(Utils.doubleToString(logProb)).append(" ");
				}
			}
			// System.out.println(sb.toString().trim());
		}
		return emissions;
	}
}
