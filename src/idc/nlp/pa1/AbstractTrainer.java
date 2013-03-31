package idc.nlp.pa1;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Preconditions;

public abstract class AbstractTrainer {

	private InputStream inputStream;

	private boolean closeStream;

	public AbstractTrainer(File input) throws FileNotFoundException {
		this.inputStream = new FileInputStream(input);
		closeStream = true;
	}

	public AbstractTrainer(InputStream input) {
		this.inputStream = input;
		closeStream = false;
	}

	public abstract void setup();

	public abstract void processSentence(ArrayList<SegmentTag> segments);

	public abstract void save() throws IOException;

	public void train() throws FileNotFoundException, IOException {
		InputStreamReader isr = new InputStreamReader(inputStream);
		try {
			setup();
			ArrayList<SegmentTag> sentence = new ArrayList<>();
			LineIterator li = new LineIterator(isr);
			while (li.hasNext()) {
				String line = li.nextLine().trim();
				if (line.isEmpty()) {
					// end of sentence
					processSentence(sentence);
					sentence.clear();
					continue;
				}
				if (line.startsWith("#")) {
					// comment
					continue;
				}
				sentence.add(new SegmentTag(StringUtils.split(line)));
			}
			if (!sentence.isEmpty()) {
				processSentence(sentence);
			}
			save();
		} finally {
			if (closeStream) {
				IOUtils.closeQuietly(isr);
			}
		}
	}

	protected class SegmentTag {
		private String seg;
		private String tag;

		public SegmentTag(String[] segTag) {
			Preconditions.checkArgument(segTag.length == 2, "Segment Tag pair is of bad format");
			this.seg = segTag[0];
			this.tag = segTag[1];
		}

		public String getSeg() {
			return seg;
		}

		public String getTag() {
			return tag;
		}

		@Override
		public String toString() {
			return seg + "=" + tag;
		}
	}

	protected String[] posTagSplit(String line) {
		line = line.trim();
		if (line.startsWith("#"))
			return new String[0];
		if (line.length() == 0)
			return new String[0];
		String[] wordTag = StringUtils.split(line);
		if (wordTag.length != 2)
			return new String[0];
		return wordTag;
	}
}
