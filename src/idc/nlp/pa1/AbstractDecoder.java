package idc.nlp.pa1;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.log4j.Logger;

import com.google.common.base.Preconditions;

public abstract class AbstractDecoder {

	private static final Logger logger = L.getLogger();
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

	protected abstract List<String> processSentence(ArrayList<String> segments);

	private class SentenceProccessor implements Callable<List<String>> {

		private ArrayList<String> segs;
		private int count;

		public SentenceProccessor(ArrayList<String> segs, int count) {
			this.segs = segs;
			this.count = count;
		}

		@Override
		public List<String> call() {

			logger.info("Processing sentence #" + count);
			logger.debug(segs);
			List<String> result = processSentence(segs);
			logger.info("Processing sentence #" + count + " done");
			return result;
			// return baos.toString();
		}

	}

	public void decode() throws IOException {
		ExecutorService es = Executors.newFixedThreadPool(4);
		InputStreamReader isr = new InputStreamReader(input);
		PrintWriter out = new PrintWriter(output);
		List<Future<List<String>>> futures = new ArrayList<>();
		try {
			setup();
			LineIterator li = new LineIterator(isr);
			ArrayList<String> segments = new ArrayList<>();
			int count = 0;
			while (li.hasNext()) {

				String line = li.nextLine().trim();
				if (line.startsWith("#")) {
					continue;
				}
				if (line.isEmpty()) {
					count++;
					futures.add(es.submit(new SentenceProccessor(segments, count)));
					segments = new ArrayList<>();
					continue;
				}
				segments.add(line);
			}
			if (!segments.isEmpty()) {
				count++;
				futures.add(es.submit(new SentenceProccessor(segments, count)));
			}
			es.shutdown();
			for (Future<List<String>> future : futures) {
				try {
					for (String line : future.get()) {
						out.println(line);
					}
				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
				}
				out.println();
				out.flush();
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
