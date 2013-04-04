package idc.nlp.pa1.bigram;

import idc.nlp.pa1.ngram.NGrams;
import idc.nlp.pa1.ngram.NGramsCreator;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Arrays;

import org.apache.commons.io.output.StringBuilderWriter;
import org.apache.commons.io.output.WriterOutputStream;
import org.apache.commons.lang3.StringUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

@Test
public class NGramsTest {

	public void testS() throws Exception {
		NGramsCreator ngc = new NGramsCreator(3, true);
		ngc.addSentence(Arrays.asList("a", "b", "c", "d"));
		ngc.addSentence(Arrays.asList("a", "b", "e", "a"));
		NGrams ng=ngc.create();
		System.out.println(ng.getLogProb(new String[]{"b","c","_END_"}));
	}
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
					String[] iter=StringUtils.split(ngram);
//					Iterable<String> iter = Splitter.on(' ').split(ngram);
					double expectedLog = expectedNg.getLogProb(iter);
					double actualLogProb = ng.getLogProb(iter);
					Assert.assertEquals(actualLogProb, expectedLog, 0.000000000000001);
				}
			}

			for (Integer i : ng.getNGramSizes()) {
				for (String ngram : ng.getNgrams(i)) {
					String[] iter=StringUtils.split(ngram);
//					Iterable<String> iter = Splitter.on(' ').split(ngram);
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
		System.out.println(ng.getLogProb(new String[]{"RB", "yyDOT"}));

	}
}
