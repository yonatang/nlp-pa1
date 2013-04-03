package idc.nlp.pa1;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;

import com.google.common.base.Preconditions;

public abstract class AbstractDecoder {

	private final InputStream input;
	private final OutputStream output;
	private final boolean closeStreams;

	public AbstractDecoder(File input, File output) throws FileNotFoundException {
		this.input = new FileInputStream(input);
		this.output = new FileOutputStream(output);
		closeStreams = true;
	}

	public AbstractDecoder(InputStream input, OutputStream output) {
		Preconditions.checkNotNull(input);
		Preconditions.checkNotNull(output);
		this.input = input;
		this.output = output;
		closeStreams = false;
	}

	protected void setup() {
	}

	protected void teardown() {
	}

	protected abstract void processSentence(ArrayList<String> segments, PrintWriter out);

	public void decode() throws IOException {
		InputStreamReader isr = new InputStreamReader(input);
		PrintWriter out = new PrintWriter(output);
		try {
			setup();
			LineIterator li = new LineIterator(isr);
			ArrayList<String> segments = new ArrayList<>();
			while (li.hasNext()) {
				String line = li.nextLine().trim();
				if (line.startsWith("#")) {
					continue;
				}
				if (line.isEmpty()) {
					processSentence(segments, out);
					out.println();
					segments.clear();
					continue;
				}
				segments.add(line);
			}
			if (!segments.isEmpty()) {
				processSentence(segments, out);
				out.println();
			}
			out.flush();
			teardown();
		} finally {

			if (closeStreams) {
				IOUtils.closeQuietly(isr);
				IOUtils.closeQuietly(out);
			}
		}
	}

}
