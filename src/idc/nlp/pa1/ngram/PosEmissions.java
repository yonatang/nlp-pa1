package idc.nlp.pa1.ngram;

import idc.nlp.pa1.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.io.LineIterator;
import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Maps;

public class PosEmissions {

	static final String UNKNOWN_SEG = "UNK";

	private final SortedMap<String, PosEmission> emissions = new TreeMap<>();
	private final boolean smoothing;

	public PosEmissions(File lexFile, boolean smoothing) throws FileNotFoundException, IOException, ParseException {
		try (FileInputStream fos = new FileInputStream(lexFile)) {
			importEmissions(fos);
		}
		this.smoothing = smoothing;
	}

	public PosEmissions(InputStream lexStream, boolean smoothing) throws FileNotFoundException, IOException,
			ParseException {
		importEmissions(lexStream);
		this.smoothing = smoothing;
	}

	PosEmissions(boolean smoothing) {
		this.smoothing = smoothing;
	}

	void addEmission(PosEmission e) {
		emissions.put(e.getSeg(), e);
	}

	public double getLogProb(String seg, String pos) {
		if (!emissions.containsKey(seg)) {
			// Word has not seen
			if (smoothing) {
				seg = UNKNOWN_SEG;
			} else {
				return Double.NEGATIVE_INFINITY;
			}
		}
		PosEmission emission = emissions.get(seg);
		if (!emission.getPosToLogProb().containsKey(pos)) {
			// Word has never been tagged as this POS
			// should not be smoothed!
			return Double.NEGATIVE_INFINITY;
		}
		return emission.getPosToLogProb().get(pos);
	}

	public void exportEmissions(OutputStream os) {
		PrintWriter pw = new PrintWriter(os);
		for (String seg : emissions.keySet()) {
			pw.println(emissionToString(emissions.get(seg)));
		}
		pw.flush();
	}

	private void importEmissions(InputStream lexFile) throws FileNotFoundException, IOException, ParseException {
		InputStreamReader isr = new InputStreamReader(lexFile);
		LineIterator li = new LineIterator(isr);
		while (li.hasNext()) {
			String line = li.nextLine().trim();
			if (line.isEmpty() || line.startsWith("#")) {
				continue;
			}
			String parts[] = StringUtils.split(line, "\t ");
			if (parts.length < 3 || parts.length % 2 == 0) {
				throw new ParseException("Line [" + line + "] is of wrong format", 0);
			}
			String seg = parts[0];
			PosEmission emission = new PosEmission(seg);
			for (int i = 1; i < parts.length; i += 2) {
				try {
					String pos = parts[i];
					double logProb = Double.parseDouble(parts[i + 1]);
					emission.addPos(pos, logProb);
				} catch (NumberFormatException e) {
					throw new ParseException("Line [" + line + "] is of wrong format", 0);
				}
			}
			emissions.put(seg, emission);
		}
	}

	public String emissionToString(PosEmission emission) {
		Function<Double, String> dblToString = new Function<Double, String>() {

			@Override
			public String apply(Double input) {
				return Utils.doubleToString(input);
			}
		};
		return emission.getSeg()
				+ "\t"
				+ Joiner.on('\t').withKeyValueSeparator("\t")
						.join(Maps.transformValues(emission.getPosToLogProb(), dblToString));
	}

}
