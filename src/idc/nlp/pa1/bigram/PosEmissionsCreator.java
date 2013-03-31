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

	public PosEmissions create() {
		PosEmissions emissions = new PosEmissions(smoothing);
		Multiset<String> unkPOSes = HashMultiset.create();

		for (Entry<String, Multiset<String>> e : wordTagCounter.entrySet()) {
			String seg = e.getKey();
			boolean singleton = (smoothing && e.getValue().size() == 1);

			Multiset<String> POSes = e.getValue();
			PosEmission emission = null;
			if (!singleton) {
				emission = new PosEmission(seg);
				emissions.addEmission(emission);
			}
			for (String pos : POSes.elementSet()) {
				if (singleton) {
					unkPOSes.add(pos);
				} else {
					Multiset<String> tagCounter = wordTagCounter.get(seg);
					int count = tagCounter.count(pos);
					double logProb = Math.log10((double) count / (double) globalTagCounter.count(pos));
					emission.addPos(pos, logProb);
				}
			}
		}
		if (smoothing) {
			PosEmission emission = new PosEmission(PosEmissions.UNKNOWN_SEG);
			emissions.addEmission(emission);
			for (com.google.common.collect.Multiset.Entry<String> posCount : unkPOSes.entrySet()) {
				emission.addPos(
						posCount.getElement(),
						Math.log10((double) posCount.getCount()
								/ (double) globalTagCounter.count(posCount.getElement())));
			}
		}
		return emissions;
	}
}
