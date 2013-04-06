package idc.nlp.pa1.bigram;

import idc.nlp.pa1.Utils;
import idc.nlp.pa1.ngram.NGramDecoder;
import idc.nlp.pa1.ngram.NGramTrainer;
import idc.nlp.pa1.ngram.NGrams;
import idc.nlp.pa1.ngram.PosEmissions;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.testng.annotations.Test;

@Test
public class NGramDecoderTest {
	public void tt() throws Exception {
		InputStream is = Utils.getStream(this, "trigram-tmp.train");
		ByteArrayOutputStream lex = new ByteArrayOutputStream();
		ByteArrayOutputStream gram = new ByteArrayOutputStream();
		NGramTrainer tt = new NGramTrainer(4, is, true, lex, gram);
		tt.train();
		is.close();
		System.out.println(lex);
		System.out.println(gram);

		ByteArrayInputStream lexIn = new ByteArrayInputStream(lex.toByteArray());
		ByteArrayInputStream gramIn = new ByteArrayInputStream(gram.toByteArray());

		String testSent = "A\nB\nE\nB\nC";

		NGrams ng = new NGrams(gramIn, true);
		PosEmissions em = new PosEmissions(lexIn, true);
		ByteArrayInputStream testIn = new ByteArrayInputStream(testSent.getBytes());
		ByteArrayOutputStream test = new ByteArrayOutputStream();
		NGramDecoder td = new NGramDecoder(4, testIn, test, ng, em);
		td.decode();
		System.out.println(test);
	}
}
