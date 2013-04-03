package idc.nlp.pa1.ngram;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import idc.nlp.pa1.AbstractTrainer;

public abstract class AbstractNGramTrainer extends AbstractTrainer {

	protected final boolean smoothing;
	protected final OutputStream lexStream;
	protected final OutputStream gramStream;
	protected final boolean closeStream;
	protected NGramsCreator ngramsCreator;
	protected PosEmissionsCreator emissionCreator;

	public AbstractNGramTrainer(File input, boolean smoothing, File lexFile, File gramFile)
			throws FileNotFoundException {
		super(input);
		this.smoothing = smoothing;
		this.lexStream = new FileOutputStream(lexFile);
		this.gramStream = new FileOutputStream(gramFile);
		this.closeStream = false;
	}

	public AbstractNGramTrainer(InputStream input, boolean smoothing, OutputStream lexStream, OutputStream gramStream) {
		super(input);
		this.smoothing = smoothing;
		this.lexStream = lexStream;
		this.gramStream = gramStream;
		this.closeStream = true;
	}
	
	public abstract void setup();

	@Override
	public void processSentence(ArrayList<SegmentTag> segments) {
		List<String> tags = Lists.transform(segments, new Function<SegmentTag, String>() {
			public String apply(SegmentTag segTag) {
				return segTag.getTag();
			}
		});
		ngramsCreator.addSentence(tags);
		for (SegmentTag segTag : segments) {
			emissionCreator.addPos(segTag.getSeg(), segTag.getTag());
		}
	}

	@Override
	public void save() throws IOException {
		try {
			ngramsCreator.create().exportNGrams(gramStream);
			emissionCreator.create().exportEmissions(lexStream);
		} finally {
			if (closeStream) {
				IOUtils.closeQuietly(gramStream);
				IOUtils.closeQuietly(lexStream);
			}
		}
	}

}