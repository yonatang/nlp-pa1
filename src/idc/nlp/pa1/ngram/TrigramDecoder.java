package idc.nlp.pa1.ngram;

import idc.nlp.pa1.ds.MaxValueFinder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Objects;

public class TrigramDecoder extends AbstractNGramDecoder {

	public TrigramDecoder(File input, File output, NGrams ngrams, PosEmissions emissions) throws FileNotFoundException,
			IOException, ParseException {
		super(input, output, ngrams, emissions);
	}

	public TrigramDecoder(InputStream input, OutputStream output, NGrams ngrams, PosEmissions emissions)
			throws ParseException, IOException {
		super(input, output, ngrams, emissions);
	}

	@Override
	protected List<String> processSentence(ArrayList<String> segments) {
		List<String> result = new ArrayList<>();
		if (segments.isEmpty())
			return Collections.emptyList();

		Set<String> posSet = new HashSet<>(getNGramsMap().getNgrams(1));
		posSet.add(NGrams.START);

		Map<Integer, Map<String, Double>> v = new HashMap<>();
		Map<Integer, Map<String, String>> b = new HashMap<>();

		Map<String, Double> v0 = new HashMap<>();
		Map<String, String> b0 = new HashMap<>();
		v.put(0, v0);
		b.put(0, b0);

		for (String pos0 : posSet) {
			for (String pos1 : posSet) {
				v0.put(pos0 + " " + pos1, Double.NEGATIVE_INFINITY);
			}
		}
		v0.put(NGrams.START + " " + NGrams.START, 0d);

		for (int i = 0; i < segments.size(); i++) {
			Map<String, Double> currentV = new HashMap<>();
			Map<String, String> currentB = new HashMap<>();
			v.put(i + 1, currentV);
			b.put(i + 1, currentB);
			Map<String, Double> prevV = v.get(i);

			for (String ct : posSet) {
				double emissionProb = getEmissions().getLogProb(segments.get(i), ct);
				for (String bt : posSet) {
					for (String at : posSet) {
						String atBt = at + " " + bt;
						String btCt = bt + " " + ct;
						double prevProb = Objects.firstNonNull(prevV.get(atBt), Double.NEGATIVE_INFINITY);
						double prob = getNGramsMap().getLogProb(new String[] { at, bt, ct }) + emissionProb + prevProb;
						if (!currentV.containsKey(btCt) || currentV.get(btCt) < prob) {
							currentV.put(btCt, prob);
							if (!Double.isInfinite(prob)) {
								currentB.put(btCt, at);
							}
						}
					}
				}
			}
		}
		Map<String, Double> lastV = v.get(segments.size());

		MaxValueFinder<String, Double> mvf = new MaxValueFinder<>();
		for (String pos0 : posSet) {
			for (String pos1 : posSet) {
				String joint = pos0 + " " + pos1;
				double lastProb = Objects.firstNonNull(lastV.get(joint), Double.NEGATIVE_INFINITY);
				double prob = getNGramsMap().getLogProb(new String[] { pos0, pos1, NGrams.END }) + lastProb;
				mvf.check(joint, prob);
			}
		}

		LinkedList<String> res = new LinkedList<>();
		res.add(StringUtils.substringAfter(mvf.getTopKey(), " "));
		res.add(StringUtils.substringBefore(mvf.getTopKey(), " "));
		for (int i = 0; i < segments.size() - 2; i++) {
			res.add(b.get(segments.size() - i).get(res.get(i + 1) + " " + res.get(i)));
		}
		java.util.Collections.reverse(res);

		for (int i = 0; i < segments.size(); i++) {
			result.add(segments.get(i) + "\t" + res.get(i));
		}

		return result;
	}

}
