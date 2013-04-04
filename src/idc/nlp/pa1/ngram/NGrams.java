package idc.nlp.pa1.ngram;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Pattern;

import org.apache.commons.io.LineIterator;
import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;

public class NGrams {
	public static final String START = "_START_";
	public static final String END = "_END_";
	public static final Cache<String, Double> ngramCache = CacheBuilder.newBuilder().build();

	private static final Map<Integer, List<Double>> metaparams = ImmutableMap.<Integer, List<Double>> builder()
			.put(1, ImmutableList.of(1d)) //
			.put(2, ImmutableList.of(0.01d, 0.99d))//
			.put(3, ImmutableList.of(0.1d, 0.3d, 0.6d))//
			.build();
	private final boolean smoothing;
	private SortedMap<Integer, SortedMap<String, NGram>> data = new TreeMap<>();

	private Multiset<Integer> ngramCounts = HashMultiset.create();

	NGrams(Multimap<Integer, NGram> inData, Multiset<Integer> ngramCounts, boolean smoothing) {
		Preconditions.checkNotNull(inData);
		Preconditions.checkNotNull(ngramCounts);
		for (Map.Entry<Integer, NGram> e : inData.entries()) {
			if (!data.containsKey(e.getKey())) {
				data.put(e.getKey(), new TreeMap<String, NGram>());
			}
			NGram ngram = e.getValue();
			data.get(e.getKey()).put(ngram.getGram(), ngram);
		}
		this.ngramCounts.addAll(ngramCounts);
		this.smoothing = smoothing;
	}

	public NGrams(File gramFile, boolean smoothing) throws ParseException, FileNotFoundException, IOException {
		Preconditions.checkNotNull(gramFile);
		try (FileInputStream fis = new FileInputStream(gramFile);
				BufferedInputStream bis = new BufferedInputStream(fis);
				InputStreamReader isr = new InputStreamReader(bis);) {
			importNGrams(isr);
		}
		this.smoothing = smoothing;
	}

	public NGrams(InputStream is, boolean smoothing) throws ParseException, IOException {
		Preconditions.checkNotNull(is);
		importNGrams(new InputStreamReader(is));
		this.smoothing = smoothing;
	}

	public Set<String> getNgrams(int n) {
		Preconditions.checkNotNull(data.get(n));
		return data.get(n).keySet();
	}

	public Set<Integer> getNGramSizes() {
		return data.keySet();
	}

	public int getNGramsCount(int n) {
		return ngramCounts.count(n);
	}

	public void exportNGrams(OutputStream os) {
		Preconditions.checkNotNull(os);

		PrintWriter pw = new PrintWriter(os);
		Set<Integer> sizes = data.keySet();
		pw.println("\\data\\");
		for (Integer size : sizes) {
			pw.println(String.format("ngram %d = %d", size, ngramCounts.count(size)));
		}
		for (Integer size : sizes) {
			pw.println();
			pw.println(String.format("\\%d-grams\\", size));
			Map<String, NGram> map = data.get(size);
			for (Map.Entry<String, NGram> entry : map.entrySet()) {
				pw.println(entry.getValue());
			}
		}
		pw.println("\\end\\");
		pw.flush();
	}

	public double getLogProb(String tags){
		Double ret=ngramCache.getIfPresent(tags);
		if (ret!=null) return ret;
		return getLogProb(StringUtils.split(tags));
	}
	public double getLogProb(String[] tags) {
		String joinTags = StringUtils.join(tags, ' ');

		int size = tags.length;

		Map<String, NGram> ngrams = data.get(size);
		if (ngrams == null) {
			throw new IllegalStateException("No data for " + size + "-grams");
		}
		if (!smoothing) {

			if (ngrams.containsKey(joinTags)) {
				return ngrams.get(joinTags).getLogProb();
			}
			return Double.NEGATIVE_INFINITY;
		} else {
			Double ret= ngramCache.getIfPresent(joinTags);
			if (ret != null)
				return ret;
			List<Double> metas = metaparams.get(size);
			double probs = 0;
			int currGrams = 0;
			StringBuilder joint = new StringBuilder();
			for (String tag : tags) {
				currGrams++;
				if (joint.length() == 0) {
					joint.append(tag);
				} else {
					joint.append(" ").append(tag);
				}
				NGram ngram = data.get(currGrams).get(joint.toString());
				if (ngram != null) {
					probs += ngram.getProb() * metas.get(currGrams - 1);
				}
			}
			ret = Math.log10(probs);
			ngramCache.put(joinTags, ret);
			return ret;
		}
	}

	private void importNGrams(InputStreamReader isr) throws ParseException, IOException {

		LineIterator li = new LineIterator(isr);
		// -1 - header, 0 - data, 0< denotes the X-grams part
		int mode = -1;

		// Compile those once
		MessageFormat ngramsFormat = new MessageFormat("\\{0,number,integer}-grams\\");
		Pattern p = Pattern.compile("\\\\\\d+\\-grams\\\\");

		while (li.hasNext()) {
			// This state machine is simple enough to implement directly and not
			// use ANTLR or something similar
			String line = li.nextLine().trim();
			if (line.equals("\\end\\")) {
				break;
			}
			if (mode == -1) {
				// header
				if (line.equals("\\data\\")) {
					mode = 0;
				}
				continue;
			} else if (mode == 0) {
				// \data\ part
				if (line.isEmpty()) {
					continue;
				}
				if (p.matcher(line).matches()) {
					long modeLong = (long) ngramsFormat.parse(line)[0];
					mode = (int) modeLong;
					continue;
				}
				if (line.length() < 10) {
					throw new ParseException("Line [" + line + "] is not in format 'ngram X=Y'", 0);
				}
				line = line.substring(6).trim();
				String[] parts = StringUtils.split(line, "=");
				if (parts.length != 2) {
					throw new ParseException("Line [" + line + "] is not in format 'ngram X=Y'", 0);
				}
				try {
					Integer ngramNum = Integer.parseInt(parts[0].trim());
					Integer ngramSize = Integer.parseInt(parts[1].trim());
					ngramCounts.setCount(ngramNum, ngramSize);
				} catch (NumberFormatException e) {
					throw new ParseException("Line [" + line + "] is not in format 'ngram X=Y'", 0);
				}
			} else {
				// \X-grams\ part
				if (line.isEmpty()) {
					continue;
				}
				if (line.matches("\\\\\\d+\\-grams\\\\")) {
					long modeLong = (long) ngramsFormat.parse(line)[0];
					mode = (int) modeLong;
					continue;
				}
				String[] parts = StringUtils.split(line, '\t');
				if (parts.length < 2) {
					throw new ParseException("Line [" + line + "] is not in format XXX[tab]TAG TAG...", 0);
				}
				double logProb = Double.parseDouble(parts[0]);
				String tag = parts[1];
				NGram gram = new NGram(tag, mode, logProb);
				if (!data.containsKey(mode)) {
					data.put(mode, new TreeMap<String, NGram>());
				}
				data.get(mode).put(gram.getGram(), gram);
			}
		}
	}
}
