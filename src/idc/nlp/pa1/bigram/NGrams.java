package idc.nlp.pa1.bigram;

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
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;

public class NGrams {
	public static final String START = "_START_";
	public static final String END = "_END_";

	private SortedMap<Integer, SortedMap<String, NGram>> data = new TreeMap<>();

	private Multiset<Integer> ngramCounts = HashMultiset.create();

	NGrams(Multimap<Integer, NGram> inData, Multiset<Integer> ngramCounts) {
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
	}

	public NGrams(File gramFile) throws ParseException, FileNotFoundException, IOException {
		Preconditions.checkNotNull(gramFile);
		try (FileInputStream fis = new FileInputStream(gramFile);
				BufferedInputStream bis = new BufferedInputStream(fis);
				InputStreamReader isr = new InputStreamReader(bis);) {
			importNGrams(isr);
		}
	}

	public NGrams(InputStream is) throws ParseException, IOException {
		Preconditions.checkNotNull(is);
		importNGrams(new InputStreamReader(is));
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
			// for (NGram gram : data.get(size)) {
			// pw.println(gram);
			// }
		}
		pw.println("\\end\\");
		pw.flush();
	}

	public double getLogProb(Iterable<String> tags) {
		int size = Iterables.size(tags);
		Map<String, NGram> ngrams = data.get(size);
		if (ngrams == null) {
			throw new IllegalStateException("No data for " + size + "-grams");
		}
		String joinTags = StringUtils.join(tags, ' ');
		if (ngrams.containsKey(joinTags)) {
			return ngrams.get(joinTags).getLogProb();
		}
		return Double.NEGATIVE_INFINITY;
	}

	public double getProb(List<String> tags) {
		return Math.pow(10, getLogProb(tags));
	}

	private void importNGrams(InputStreamReader isr) throws ParseException, IOException {

		LineIterator li = new LineIterator(isr);
		// -1 - header, 0 - data, 0< denotes the X-grams part
		int mode = -1;

		// Compile those once
		MessageFormat ngramsFormat = new MessageFormat("\\{0,number,integer}-grams\\");
		Pattern p = Pattern.compile("\\\\\\d+\\-grams\\\\");

		while (li.hasNext()) {
			// This state machine is simple enough to implement and not use
			// ANTLR or something similar
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
