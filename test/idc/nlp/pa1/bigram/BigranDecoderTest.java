package idc.nlp.pa1.bigram;

import idc.nlp.pa1.Utils;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@Test
public class BigranDecoderTest {

	private NGrams nsNgrams;
	private PosEmissions nsEmissions;

	@BeforeClass
	private void setup() throws IOException, ParseException {
		try (InputStream ngramStream = Utils.getStream(this, "bigram-ns-test.gram");
				InputStream lexStream = Utils.getStream(this, "bigram-ns-test.lex");) {
			nsNgrams = new NGrams(ngramStream, false);
			nsEmissions = new PosEmissions(lexStream, false);
		}
	}

	public void testNs() throws FileNotFoundException, IOException, ParseException {
		try (InputStream input = Utils.getStream(this, "bigram-ns-test.test");
				ByteArrayOutputStream baos = new ByteArrayOutputStream();) {
			BigramDecoder bd = new BigramDecoder(input, baos, nsNgrams, nsEmissions);
			bd.decode();
			System.out.println(baos.toString());
		}

	}

}
