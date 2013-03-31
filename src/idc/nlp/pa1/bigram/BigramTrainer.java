package idc.nlp.pa1.bigram;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.Lists;

import idc.nlp.pa1.AbstractTrainer;

public class BigramTrainer extends AbstractTrainer {

	private final boolean smoothing;
	private final OutputStream lexStream;
	private final OutputStream gramStream;
	private final boolean closeStream;

	private NGramsCreator ngramsCreator;
	private PosEmissionsCreator emissionCreator;

	public BigramTrainer(File input, boolean smoothing, File lexFile, File gramFile) throws FileNotFoundException {
		super(input);
		this.smoothing = smoothing;
		this.lexStream = new FileOutputStream(lexFile);
		this.gramStream = new FileOutputStream(gramFile);
		this.closeStream = false;
	}

	public BigramTrainer(InputStream input, boolean smoothing, OutputStream lexStream, OutputStream gramStream) {
		super(input);
		this.smoothing = smoothing;
		this.lexStream = lexStream;
		this.gramStream = gramStream;
		this.closeStream = true;
	}

	@Override
	public void setup() {
		ngramsCreator = new NGramsCreator(2);
		emissionCreator = new PosEmissionsCreator(smoothing);
	}

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
