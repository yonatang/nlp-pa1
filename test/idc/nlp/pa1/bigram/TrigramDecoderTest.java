package idc.nlp.pa1.bigram;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;

import idc.nlp.pa1.Utils;
import idc.nlp.pa1.ngram.NGrams;
import idc.nlp.pa1.ngram.PosEmissions;
import idc.nlp.pa1.ngram.TrigramDecoder;
import idc.nlp.pa1.ngram.TrigramTrainer;

import org.testng.annotations.Test;

@Test
public class TrigramDecoderTest {

	
	public void tt() throws Exception {
		InputStream is=Utils.getStream(this, "trigram-tmp.train");
		ByteArrayOutputStream lex=new ByteArrayOutputStream();
		ByteArrayOutputStream gram=new ByteArrayOutputStream();
		TrigramTrainer tt=new TrigramTrainer(is, true, lex, gram);
		tt.train();
		is.close();
		System.out.println(lex);
		System.out.println(gram);
		
		ByteArrayInputStream lexIn=new ByteArrayInputStream(lex.toByteArray());
		ByteArrayInputStream gramIn=new ByteArrayInputStream(gram.toByteArray());
		
		String testSent="A\nB\nE\nB\nC";
		
		NGrams ng=new NGrams(gramIn, true);
		PosEmissions em=new PosEmissions(lexIn, true);
		ByteArrayInputStream testIn=new ByteArrayInputStream(testSent.getBytes());
		ByteArrayOutputStream test=new ByteArrayOutputStream();
		TrigramDecoder td=new TrigramDecoder(testIn, test, ng, em);
		td.decode();
		System.out.println(test);
	}
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
		InputStream is = Utils.getStream(this, "trigram-s-test.test");
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		TrigramDecoder tg = new TrigramDecoder(is, out,
				new NGrams(Utils.getStream(this, "trigram-s-test.gram"), true), new PosEmissions(Utils.getStream(this,
						"trigram-s-test.lex"), true));
		tg.decode();
		is.close();
		System.out.println(out);

	}
}
