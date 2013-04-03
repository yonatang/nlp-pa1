package idc.nlp.pa1.ngram;

import idc.nlp.pa1.L;
import idc.nlp.pa1.ds.MaxValueFinder;

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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.sun.org.apache.bcel.internal.generic.GETSTATIC;

public class TrigramDecoder extends AbstractNGramDecoder {
	private static final Logger logger = L.getLogger();

	public TrigramDecoder(File input, File output, NGrams ngrams, PosEmissions emissions) throws FileNotFoundException,
			IOException, ParseException {
		super(input, output, ngrams, emissions);
	}

	public TrigramDecoder(InputStream input, OutputStream output, NGrams ngrams, PosEmissions emissions)
			throws ParseException, IOException {
		super(input, output, ngrams, emissions);
	}

	private MaxValueFinder<String,Double> argmax(Set<String> posSet, Map<String,Double> prevV,String u, String v, String seg){
		MaxValueFinder<String,Double> mvf=new MaxValueFinder<>();
		double emisProb=getEmissions().getLogProb(seg, v);
		for (String w:posSet){
			Double prevProb=prevV.get(w+" "+u);
			if (prevProb==null) prevProb=Double.NEGATIVE_INFINITY; 
			double gramProb=getNGramsMap().getLogProb(w+" "+u+" "+v);
			mvf.check(w, prevProb+gramProb+emisProb);
		}
		return mvf;
	}
	
	@Override
	protected void processSentence(ArrayList<String> segments, PrintWriter out) {
		logger.info("Analyizing sentence " + segments);
		if (segments.isEmpty())
			return;

		Set<String> posSet = new HashSet<>(getNGramsMap().getNgrams(1));
		posSet.add(NGrams.START);
		
		Map<Integer, Map<String, Double>> v = new HashMap<>();
		Map<Integer, Map<String, String>> b = new HashMap<>();

		Map<String, Double> v0 = new HashMap<>();
		Map<String, String> b0 = new HashMap<>();
		v.put(0, v0);
		b.put(0, b0);
		
		for (String pos0:posSet){
			for (String pos1:posSet){
				v0.put(pos0+" "+pos1, Double.NEGATIVE_INFINITY);
			}
		}
		v0.put(NGrams.START+" "+NGrams.START, 0d);
		
		boolean allAreZero=false;
		for (int i = 0; i < segments.size(); i++) {
			Map<String, Double> currentV = new HashMap<>();
			Map<String, String> currentB = new HashMap<>();
			v.put(i+1, currentV);
			b.put(i+1, currentB);
			allAreZero=true;
			for (String pos0:posSet){
				for (String pos1:posSet){
					MaxValueFinder<String, Double> mvf=argmax(posSet, v.get(i),pos0,pos1,segments.get(i));
					currentV.put(pos0+" "+pos1, mvf.getTopValue());
					currentB.put(pos0+" "+pos1, mvf.getTopKey());
					if (!Double.isInfinite(mvf.getTopValue())){
						allAreZero=false;
					}
				}
			}
			if (allAreZero){
				break;
			}
		}
		
		if (allAreZero){
			for (int i=0;i<segments.size();i++){
				out.println(segments.get(i)+"\t??");
			}
			return;
		}
		
		MaxValueFinder<String, Double> mvf=new MaxValueFinder<>();
		for (String uPos:posSet){
			for (String vPos:posSet){
				String joint=uPos+" "+vPos;
				mvf.check(joint, getNGramsMap().getLogProb(joint+" "+NGrams.END));
			}
		}
		List<String> y=new ArrayList<>();
		y.add(StringUtils.substringAfter(mvf.getTopKey(), " "));
		y.add(StringUtils.substringBefore(mvf.getTopKey(), " "));
		for (int i=2;i<segments.size();i++){
			String two=y.get(i-1)+" "+y.get(i-2);
//			System.out.println(" "+two);
			String val=b.get(segments.size()-i+2).get(two);
//			System.out.println(" "+val);
			y.add(val);
		}
		Lists.reverse(y);
		for (int i=0;i<y.size();i++){
			out.println(segments.get(i)+"\t"+y.get(i));
		}
//		for ()
//
//		String firstSeg = segments.get(0);
//		logger.info("### Analyizing " + firstSeg);
//		{
//			boolean allAreZero = true;
//			for (String pos : posSet) {
//				double transLogProb = getNGramsMap().getLogProb(Arrays.asList(NGrams.START, NGrams.START, pos));
//				double segLogProb = getEmissions().getLogProb(firstSeg, pos);
//				double logProb = transLogProb + segLogProb;
//				if (logProb > Double.NEGATIVE_INFINITY) {
//					logger.debug("Prob for P[" + pos + "|_START] x P[" + firstSeg + "|" + pos + "] = " + segLogProb
//							+ " + " + transLogProb + " = " + logProb);
//				} else {
//					logger.trace("Prob for P[" + pos + "|_START] x P[" + firstSeg + "|" + pos + "] = " + segLogProb
//							+ " + " + transLogProb + " = " + logProb);
//				}
//				if (!Double.isInfinite(logProb))
//					allAreZero = false;
//				v0.put(pos, logProb);
//				b0.put(pos, NGrams.START+" "+NGrams.START);
//			}
//			if (allAreZero) {
//				// If we cannot make a useful estimate about the next to
//				logger.info("Didn't find any option for " + firstSeg + ". Tagging it as NN");
//				v0.put(NGrams.START + " " + "NN", 0d); // P["NN"|"_START_"]=log(0)=1
//			}
//		}
//
//		for (int i = 1; i < segments.size(); i++) {
//			Map<String, Double> currentV = new HashMap<>();
//			Map<String, String> currentB = new HashMap<>();
//			v.put(i, currentV);
//			b.put(i, currentB);
//
//			String seg = segments.get(i);
//			logger.info("### Analyizing " + seg);
//
//			boolean allAreZero = true;
//			for (String pos : posSet) {
//				for (String pos2 : posSet) {
//					MaxValueFinder<String, Double> mpf = argmax(v.get(i - 2), pos, pos2, seg);
//					if (!Double.isInfinite(mpf.getTopValue()))
//						allAreZero = false;
//					currentV.put(pos, mpf.getTopValue());
//					currentB.put(pos, mpf.getTopKey());
//				}
//				
//			}
//			if (allAreZero) {
//				// If we cannot make a useful estimate about the next to
//				logger.info("Didn't find any option for " + seg + ". Tagging it as NN");
//				MaxValueFinder<String, Double> mpfNoNGram = new MaxValueFinder<>();
//				MaxValueFinder<String, Double> mpfWithNGram = new MaxValueFinder<>();
//				for (Entry<String, Double> e : v.get(i - 1).entrySet()) {
//					// search for the highest probability previous state
//					mpfNoNGram.check(e.getKey(), e.getValue());
//					// search for the best bigram for NN (might not find one, if
//					// there is smoothing is off)
//					mpfWithNGram.check(e.getKey(), getNGramsMap().getLogProb(Arrays.asList(e.getKey(), "NN")));
//				}
//				currentV.put("NN", 0d);
//				if (Double.isInfinite(mpfWithNGram.getTopValue())) {
//					// in rare cases this might improve results, when P[S'] >>
//					// P[S|S']
//					currentB.put("NN", mpfWithNGram.getTopKey());
//				} else {
//					currentB.put("NN", mpfNoNGram.getTopKey());
//				}
//			}
//		}
//
//		Map<String, Double> lastV = new HashMap<>();
//		Map<String, String> lastB = new HashMap<>();
//		MaxValueFinder<String, Double> mpf = argmax(v.get(segments.size() - 1), NGrams.END, "");
//		lastV.put(NGrams.END, mpf.getTopValue());
//		lastB.put(NGrams.END, mpf.getTopKey());
//		v.put(segments.size(), lastV);
//		b.put(segments.size(), lastB);
//
//		String lastPos = NGrams.END;
//		List<String> poses = new ArrayList<>();
//		for (int i = segments.size(); i > 0; i--) {
//			poses.add(b.get(i).get(lastPos));
//			lastPos = b.get(i).get(lastPos);
//		}
//		for (int i = 0; i < segments.size(); i++) {
//			out.println(segments.get(i) + "\t" + poses.get(segments.size() - i - 1));
//		}
//		out.flush();

	}

}
