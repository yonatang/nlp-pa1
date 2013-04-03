package idc.nlp.pa1.baseline;

import idc.nlp.pa1.AbstractTrainer;
import idc.nlp.pa1.L;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import org.apache.log4j.Logger;

public class BaselineTrainer extends AbstractTrainer {

	private final static Logger logger = L.getLogger();
	private OutputStream output;

	private TagsFrequencies tags;

	public BaselineTrainer(File input, File output) throws FileNotFoundException {
		super(input);
		this.output = new FileOutputStream(output);
	}

	public BaselineTrainer(InputStream input, OutputStream output) {
		super(input);
		this.output = output;
	}

	@Override
	public void setup() {
		tags = new TagsFrequencies();
	}

	@Override
	public void processSentence(ArrayList<SegmentTag> segments) {
		logger.debug("Processing sentence " + segments);
		for (SegmentTag segTag : segments) {
			tags.addTag(segTag.getSeg(), segTag.getTag());
		}
	}

	@Override
	public void save() throws IOException {
		tags.save(output);
	}

}
