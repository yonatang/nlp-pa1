package idc.nlp.pa1.bigram;

import idc.nlp.pa1.AbstractDecoder;
import idc.nlp.pa1.L;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class BigramDecoder extends AbstractDecoder {
	private static final Logger logger = L.getLogger();

	private final NGrams ngrams;
	private final PosEmissions emissions;

	public BigramDecoder(File input, File output, NGrams ngrams, PosEmissions emissions) throws FileNotFoundException,
			IOException, ParseException {
		super(input, output);
		this.ngrams = ngrams;
		this.emissions = emissions;
	}

	public BigramDecoder(InputStream input, OutputStream output, NGrams ngrams, PosEmissions emissions)
			throws ParseException, IOException {
		super(input, output);
		this.ngrams = ngrams;
		this.emissions = emissions;
	}

	class MaxPosFinder {
		String maxPos;
		double maxPosLogProb = Double.NEGATIVE_INFINITY;

		public void test(String pos, double logProb) {
			if (maxPos == null || logProb > maxPosLogProb) {
				maxPosLogProb = logProb;
				maxPos = pos;
			}
		}
	}

	private MaxPosFinder argmax(Map<String, Double> prevV, String pos, String seg) {
		MaxPosFinder mpf = new MaxPosFinder();
		double segLogProb;
		if (!pos.equals(NGrams.END))
			segLogProb = emissions.getLogProb(seg, pos);
		else
			segLogProb = 0;
		for (Entry<String, Double> e : prevV.entrySet()) {
			double logProb = e.getValue() + ngrams.getLogProb(Arrays.asList(e.getKey(), pos)) + segLogProb;
			Level l = logProb > Double.NEGATIVE_INFINITY ? Level.DEBUG : Level.TRACE;
			logger.log(l,
					"Prob for P[" + pos + "|" + e.getKey() + "] x P[" + seg + "|" + pos + "] x prevV(" + e.getKey()
							+ ") = " + ngrams.getLogProb(Arrays.asList(e.getKey(), pos)) + " + " + segLogProb + " + "
							+ e.getValue() + " = " + logProb);

			mpf.test(e.getKey(), logProb);
		}
		return mpf;
	}

	@Override
	protected void processSentence(ArrayList<String> segments, PrintWriter out) {
		logger.info("Analyizing sentence " + segments);
		if (segments.isEmpty())
			return;

		Set<String> posSet = ngrams.getNgrams(1);

		Map<Integer, Map<String, Double>> v = new HashMap<>();
		Map<Integer, Map<String, String>> b = new HashMap<>();

		Map<String, Double> v0 = new HashMap<>();
		Map<String, String> b0 = new HashMap<>();
		v.put(0, v0);
		b.put(0, b0);

		String firstSeg = segments.get(0);
		logger.info("### Analyizing " + firstSeg);
		{
			boolean allAreZero = true;
			for (String pos : posSet) {
				double transLogProb = ngrams.getLogProb(Arrays.asList(NGrams.START, pos));
				double segLogProb = emissions.getLogProb(firstSeg, pos);
				double logProb = transLogProb + segLogProb;
				if (logProb > Double.NEGATIVE_INFINITY) {
					logger.debug("Prob for P[" + pos + "|_START] x P[" + firstSeg + "|" + pos + "] = " + segLogProb
							+ " + " + transLogProb + " = " + logProb);
				} else {
					logger.trace("Prob for P[" + pos + "|_START] x P[" + firstSeg + "|" + pos + "] = " + segLogProb
							+ " + " + transLogProb + " = " + logProb);
				}
				if (!Double.isInfinite(logProb))
					allAreZero = false;
				v0.put(pos, logProb);
				b0.put(pos, NGrams.START);
			}
			if (allAreZero) {
				// If we cannot make a useful estimate about the next to
				logger.debug("Didn't find any option for " + firstSeg + ". Tagging it as NN");
				v0.put("NN", 0d);
			}
		}

		for (int i = 1; i < segments.size(); i++) {
			Map<String, Double> currentV = new HashMap<>();
			Map<String, String> currentB = new HashMap<>();
			v.put(i, currentV);
			b.put(i, currentB);

			String seg = segments.get(i);
			logger.info("### Analyizing " + seg);

			if (!emissions.isKnownSeg(seg)) {

			}
			boolean allAreZero = true;
			for (String pos : posSet) {
				MaxPosFinder mpf = argmax(v.get(i - 1), pos, seg);
				if (!Double.isInfinite(mpf.maxPosLogProb))
					allAreZero = false;
				currentV.put(pos, mpf.maxPosLogProb);
				currentB.put(pos, mpf.maxPos);
			}
			if (allAreZero) {
				// If we cannot make a useful estimate about the next to
				logger.debug("Didn't find any option for " + seg + ". Tagging it as NN");
				MaxPosFinder mpfNoNGram = new MaxPosFinder();
				MaxPosFinder mpfWithNGram = new MaxPosFinder();
				for (Entry<String, Double> e : v.get(i - 1).entrySet()) {
					mpfNoNGram.test(e.getKey(), e.getValue());
					mpfWithNGram.test(e.getKey(), ngrams.getLogProb(Arrays.asList(e.getKey(), "NN")));
				}
				currentV.put("NN", 0d);
				if (Double.isInfinite(mpfWithNGram.maxPosLogProb)) {
					// in rare cases this might improve results, when P[S'] >> P[S|S']
					currentB.put("NN", mpfWithNGram.maxPos);
				} else {
					currentB.put("NN", mpfNoNGram.maxPos);
				}
			}
		}

		Map<String, Double> lastV = new HashMap<>();
		Map<String, String> lastB = new HashMap<>();
		MaxPosFinder mpf = argmax(v.get(segments.size() - 1), NGrams.END, "");
		lastV.put(NGrams.END, mpf.maxPosLogProb);
		lastB.put(NGrams.END, mpf.maxPos);
		v.put(segments.size(), lastV);
		b.put(segments.size(), lastB);

		String lastPos = NGrams.END;
		List<String> poses = new ArrayList<>();
		for (int i = segments.size(); i > 0; i--) {
			poses.add(b.get(i).get(lastPos));
			lastPos = b.get(i).get(lastPos);
		}
		for (int i = 0; i < segments.size(); i++) {
			out.println(segments.get(i) + "\t" + poses.get(segments.size() - i - 1));
		}
		out.flush();

	}

}
