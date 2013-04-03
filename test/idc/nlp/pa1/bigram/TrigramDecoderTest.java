package idc.nlp.pa1.bigram;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;

import idc.nlp.pa1.Utils;
import idc.nlp.pa1.ngram.NGrams;
import idc.nlp.pa1.ngram.PosEmissions;
import idc.nlp.pa1.ngram.TrigramDecoder;

import org.testng.annotations.Test;

@Test
public class TrigramDecoderTest {

	public void t() throws FileNotFoundException, IOException, ParseException {
		// InputStream is=Utils.getStream(this, "bigram-test.train");
		// ByteArrayOutputStream lex=new ByteArrayOutputStream();
		// ByteArrayOutputStream gram=new ByteArrayOutputStream();
		// TrigramTrainer t=new TrigramTrainer(is, false, lex, gram);
		// t.train();
		//
		// System.out.println(lex);
		// System.out.println("___");
		// System.out.println(gram);
		InputStream is = Utils.getStream(this, "trigram-ns-test.test");
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		TrigramDecoder tg = new TrigramDecoder(is, out,
				new NGrams(Utils.getStream(this, "trigram-ns-test.gram"), false), new PosEmissions(Utils.getStream(this,
						"trigram-ns-test.lex"), false));
		tg.decode();
		is.close();
		System.out.println(out);

	}
}
