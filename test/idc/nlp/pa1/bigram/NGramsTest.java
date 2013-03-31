package idc.nlp.pa1.bigram;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Arrays;

import org.apache.commons.io.output.StringBuilderWriter;
import org.apache.commons.io.output.WriterOutputStream;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.google.common.base.Splitter;

@Test
public class NGramsTest {

	public void testCreateAndReadNs() throws IOException, ParseException {
		NGramsCreator ngc = new NGramsCreator(3, false);
		ngc.addSentence(Arrays.asList("a", "b", "c", "d"));
		ngc.addSentence(Arrays.asList("a", "b", "e", "a"));
		NGrams ng = ngc.create();
		StringBuilderWriter sbw = new StringBuilderWriter();
		WriterOutputStream wos = new WriterOutputStream(sbw);
		ng.exportNGrams(wos);

		try (InputStream is = this.getClass().getResourceAsStream("abcd-abea.gram");) {
			NGrams expectedNg = new NGrams(is, false);
			for (Integer i : expectedNg.getNGramSizes()) {
				for (String ngram : expectedNg.getNgrams(i)) {
					Iterable<String> iter = Splitter.on(' ').split(ngram);
					double expectedLog = expectedNg.getLogProb(iter);
					double actualLogProb = ng.getLogProb(iter);
					Assert.assertEquals(actualLogProb, expectedLog, 0.000000000000001);
				}
			}

			for (Integer i : ng.getNGramSizes()) {
				for (String ngram : ng.getNgrams(i)) {
					Iterable<String> iter = Splitter.on(' ').split(ngram);
					double expectedLog = expectedNg.getLogProb(iter);
					double actualLogProb = ng.getLogProb(iter);
					Assert.assertEquals(actualLogProb, expectedLog, 0.000000000000001);
				}
			}
		}
	}

	public void testCase1() {
		NGramsCreator ngc = new NGramsCreator(2, false);
		ngc.addSentence(Arrays.asList("yyQUOT", "VB", "NN", "CC", "RB", "yyDOT"));
		NGrams ng = ngc.create();
		System.out.println(ng.getLogProb(Arrays.asList("RB", "yyDOT")));

	}
}
