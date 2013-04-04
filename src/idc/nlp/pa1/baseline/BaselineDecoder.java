package idc.nlp.pa1.baseline;

import idc.nlp.pa1.AbstractDecoder;
import idc.nlp.pa1.L;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.google.common.base.Preconditions;

public class BaselineDecoder extends AbstractDecoder {

	private static final Logger logger = L.getLogger();
	private final TagsFrequencies tags;

	public BaselineDecoder(File input, File output, TagsFrequencies tags) throws FileNotFoundException, IOException {
		super(input, output);
		Preconditions.checkNotNull(tags);
		this.tags = tags;
	}

	public BaselineDecoder(InputStream input, OutputStream output, TagsFrequencies tags) throws IOException {
		super(input, output);
		Preconditions.checkNotNull(tags);
		this.tags = tags;
	}

	@Override
	protected List<String> processSentence(ArrayList<String> segments) {
		List<String> result=new ArrayList<>();
		for (String seg : segments) {
			String tag = tags.getBestTag(seg);
			result.add(seg + "\t" + tag);
		}
		return result;
	}

}
