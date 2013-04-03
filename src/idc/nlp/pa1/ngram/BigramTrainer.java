package idc.nlp.pa1.ngram;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;

public class BigramTrainer extends AbstractNGramTrainer {

	public BigramTrainer(File input, boolean smoothing, File lexFile, File gramFile) throws FileNotFoundException {
		super(input, smoothing, lexFile, gramFile);
	}

	public BigramTrainer(InputStream input, boolean smoothing, OutputStream lexStream, OutputStream gramStream) {
		super(input, smoothing, lexStream, gramStream);
	}

	@Override
	public void setup() {
		ngramsCreator = new NGramsCreator(2, smoothing);
		emissionCreator = new PosEmissionsCreator(smoothing);
	}

}
