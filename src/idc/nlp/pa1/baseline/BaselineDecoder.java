package idc.nlp.pa1.baseline;

import idc.nlp.pa1.AbstractDecoder;
import idc.nlp.pa1.L;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.google.common.base.Preconditions;

public class BaselineDecoder extends AbstractDecoder {

	private static final Logger logger = L.getLogger();
	private final BaselineTags tags;

	public BaselineDecoder(File input, File output, BaselineTags tags) throws FileNotFoundException, IOException {
		super(input, output);
		Preconditions.checkNotNull(tags);
		this.tags = tags;
	}

	public BaselineDecoder(InputStream input, OutputStream output, BaselineTags tags) throws IOException {
		super(input, output);
		Preconditions.checkNotNull(tags);
		this.tags = tags;
	}

	@Override
	protected void processSentence(ArrayList<String> segments, PrintWriter out) {
		logger.debug("processing sentence " + segments);
		for (String seg : segments) {
			String tag = tags.getBestTag(seg);
			out.println(seg + "\t" + tag);
		}
		out.flush();
	}

}
