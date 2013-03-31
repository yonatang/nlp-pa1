package idc.nlp.pa1.evaluator;

import idc.nlp.pa1.Model;
import idc.nlp.pa1.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang3.StringUtils;

public class Evaluator {

	private final File taggedFile;
	private final File goldFile;
	private final File outputFile;
	private final Model model;
	private final boolean smoothing;
	private final InputStream taggedStream;
	private final InputStream goldStream;
	private final OutputStream outputStream;
	private final boolean closeStreams;

	public Evaluator(File taggedFile, File goldFile, File outputFile, Model model, boolean smoothing) {
		this.taggedFile = taggedFile;
		this.goldFile = goldFile;
		this.outputFile = outputFile;
		this.model = model;
		this.taggedStream = null;
		this.goldStream = null;
		this.outputStream = null;
		this.smoothing = smoothing;
		closeStreams = true;
	}

	public Evaluator(InputStream taggedStream, InputStream goldStream, OutputStream outputStream, Model model,
			boolean smoothing) {
		this.taggedFile = null;
		this.goldFile = null;
		this.outputFile = null;
		this.model = model;
		this.taggedStream = taggedStream;
		this.goldStream = goldStream;
		this.outputStream = outputStream;
		this.smoothing = smoothing;
		closeStreams = false;
	}

	@SuppressWarnings("resource")
	public void evaluate() throws FileNotFoundException, IOException {
		Reader taggedReader = null;
		Reader goldReader = null;
		PrintWriter pw = null;
		try {
			taggedReader = Utils.readerFromStreamOrFile(taggedStream, taggedFile);
			goldReader = Utils.readerFromStreamOrFile(goldStream, goldFile);
			pw = new PrintWriter(Utils.writerFromStreamOrFile(outputStream, outputFile));

			LineIterator tagged = IOUtils.lineIterator(taggedReader);
			LineIterator golden = IOUtils.lineIterator(goldReader);

			pw.println("#");
			pw.println("# Part-of-Speech Tagging Evaluation");
			pw.println("#");
			pw.println("# Model: " + model.toString() + " (" + model.getNumber() + ")");
			pw.println("# Smoothing: " + (smoothing ? "yes" : "no"));
			pw.println("# Test File: " + (taggedFile != null ? taggedFile.getName() : "N/A"));
			pw.println("# Gold File: " + (goldFile != null ? goldFile.getName() : "N/A"));
			pw.println("#");
			pw.println();
			pw.println("# sent-num\tword-accuracy\tsent-accuracy");

			int allAccurateSents = 0;
			int allAccurateWords = 0;
			int allWordCount = 0;

			int sentCount = 0;
			int sentAccurateWords = 0;
			int sentWordCount = 0;

			StringBuilder output = new StringBuilder();
			while (tagged.hasNext() && golden.hasNext()) {
				String taggedLine = tagged.nextLine().trim();
				String goldenLine = golden.nextLine().trim();
				if ((taggedLine.isEmpty() && !goldenLine.isEmpty()) || (!taggedLine.isEmpty() && goldenLine.isEmpty())) {
					throw new IOException("Files not in sync");
				}
				if (taggedLine.isEmpty()) {
					int sentAccuracy = (sentAccurateWords == sentWordCount) ? 1 : 0;
					double sentWordAccuracy = (double) sentAccurateWords / (double) sentWordCount;
					allAccurateWords += sentAccurateWords;
					allWordCount += sentWordCount;
					allAccurateSents += sentAccuracy;
					pw.println(String.format("%d\t%.16f\t%d", sentCount + 1, sentWordAccuracy, sentAccuracy));

					output.setLength(0);
					sentCount++;
					sentAccurateWords = 0;
					sentWordCount = 0;
					continue;
				}
				if (taggedLine.equals(goldenLine)) {
					sentAccurateWords++;
				}
				sentWordCount++;
			}
			if (tagged.hasNext() || golden.hasNext()) {
				throw new IOException("Files not in sync");
			}
			pw.flush();

			double allWordAccuracy = (double) allAccurateWords / (double) allWordCount;
			double allSentAccuracy = (double) allAccurateSents / (double) sentCount;

			pw.println("# " + StringUtils.repeat('-', 80));
			pw.println(String.format("%s\t%.16f\t%.16f", "macro-avg", allWordAccuracy, allSentAccuracy));
			pw.flush();

		} finally {
			if (closeStreams) {
				IOUtils.closeQuietly(taggedReader);
				IOUtils.closeQuietly(goldReader);
				IOUtils.closeQuietly(pw);
			}
		}
	}
}
