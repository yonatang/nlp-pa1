package idc.nlp.pa1.ngram;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;

public class NGramTrainer extends AbstractNGramTrainer {

	private final int size;

	public NGramTrainer(int n, InputStream input, boolean smoothing, OutputStream lexStream, OutputStream gramStream) {
		super(input, smoothing, lexStream, gramStream);
		this.size = n;
	}

	public NGramTrainer(int n, File input, boolean smoothing, File lexFile, File gramFile)
			throws FileNotFoundException {
		super(input, smoothing, lexFile, gramFile);
		this.size = n;
	}

	@Override
	public void setup() {
		ngramsCreator = new NGramsCreator(size, smoothing);
		emissionCreator = new PosEmissionsCreator(smoothing);

	}

}
