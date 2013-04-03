package idc.nlp.pa1.ngram;

import idc.nlp.pa1.AbstractDecoder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;

public abstract class AbstractNGramDecoder extends AbstractDecoder {
	private final NGrams ngrams;
	private final PosEmissions emissions;

	public AbstractNGramDecoder(File input, File output, NGrams ngrams, PosEmissions emissions)
			throws FileNotFoundException, IOException, ParseException {
		super(input, output);
		this.ngrams = ngrams;
		this.emissions = emissions;
	}

	public AbstractNGramDecoder(InputStream input, OutputStream output, NGrams ngrams, PosEmissions emissions)
			throws ParseException, IOException {
		super(input, output);
		this.ngrams = ngrams;
		this.emissions = emissions;
	}

	NGrams getNGramsMap() {
		return ngrams;
	}

	PosEmissions getEmissions() {
		return emissions;
	}

}
