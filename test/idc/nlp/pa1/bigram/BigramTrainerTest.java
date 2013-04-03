package idc.nlp.pa1.bigram;

import idc.nlp.pa1.Utils;
import idc.nlp.pa1.ngram.BigramTrainer;
import idc.nlp.pa1.ngram.NGrams;
import idc.nlp.pa1.ngram.PosEmissions;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;

import static org.testng.Assert.*;

@Test
public class BigramTrainerTest {

	InputStream trainInput;
	InputStream lexStream;
	InputStream gramStream;
	InputStream lexSmoothedStream;

	@BeforeMethod
	void setup() {
		trainInput = Utils.getStream(this, "bigram-test.train");
		lexStream = Utils.getStream(this, "bigram-ns-test.lex");
		lexSmoothedStream = Utils.getStream(this, "bigram-s-test.lex");
		gramStream = Utils.getStream(this, "bigram-ns-test.gram");
	}

	@AfterMethod
	void teardown() {
		IOUtils.closeQuietly(trainInput);
		IOUtils.closeQuietly(lexStream);
		IOUtils.closeQuietly(gramStream);
	}

	public void testOutputNoSmoothing() throws IOException {
		try (ByteArrayOutputStream lex = new ByteArrayOutputStream();
				ByteArrayOutputStream gram = new ByteArrayOutputStream();) {
			BigramTrainer bt = new BigramTrainer(trainInput, false, lex, gram);
			bt.train();
			assertEquals(lex.toString().trim(), IOUtils.toString(lexStream).trim());
			assertEquals(gram.toString().trim(), IOUtils.toString(gramStream).trim());
		}
	}

	public void testOutputWithSmoothing() throws IOException {
		try (ByteArrayOutputStream lex = new ByteArrayOutputStream();
				ByteArrayOutputStream gram = new ByteArrayOutputStream();) {
			BigramTrainer bt = new BigramTrainer(trainInput, true, lex, gram);
			bt.train();
			assertEquals(lex.toString().trim(), IOUtils.toString(lexSmoothedStream).trim());
			// assertEquals(gram.toString().trim(),
			// IOUtils.toString(gramStream).trim());
		}
	}

	public void testLexNoSmoothing() throws FileNotFoundException, IOException, ParseException {
		PosEmissions emissions = new PosEmissions(lexStream, false);
		Map<String, Map<String, Double>> tests = ImmutableMap.<String, Map<String, Double>> of(//
				"BGDWL", ImmutableMap.of("RB", 0d),//
				"NQMH", ImmutableMap.of("NN", -0.1760912590556813d),//
				"NQMI", ImmutableMap.of("NN", -0.4771212547196624d),//
				"THIH", ImmutableMap.of("AUX", 0d, "VB", 0d), "W", ImmutableMap.of("CC", 0d)//
				);
		for (String seg : tests.keySet()) {
			for (Entry<String, Double> e : tests.get(seg).entrySet()) {
				assertEquals(emissions.getLogProb(seg, e.getKey()), e.getValue(), 0.000000000001);
			}
		}
	}

	public void testGram() throws ParseException, IOException {
		NGrams grams = new NGrams(gramStream,false);
		Map<Integer, Integer> dataTest = ImmutableMap.<Integer, Integer> of(1, 5, 2, 7);
		Map<String, Double> gram1 = ImmutableMap.<String, Double> of(//
				"AUX", -1.0791812460476249d,//
				"CC", -0.6020599913279624,//
				"NN", -0.6020599913279624,//
				"RB", -0.6020599913279624,//
				"VB", -0.7781512503836436);

		Map<String, Double> gram2 = ImmutableMap.<String, Double> builder().put("AUX NN", 0d).put("CC RB", 0d)
				.put("NN CC", 0d).put("RB _END_", 0d).put("VB NN", 0d).put("_START_  AUX", -0.4771212547196624)
				.put("_START_ VB", -0.1760912590556813d).build();

		for (Entry<Integer, Integer> test : dataTest.entrySet()) {
			assertEquals((Integer) grams.getNGramsCount(test.getKey()), test.getValue());
		}

		for (Entry<String, Double> test : gram1.entrySet()) {
			assertEquals((Double) grams.getLogProb(Arrays.asList(test.getKey())), test.getValue());
		}
		for (Entry<String, Double> test : gram2.entrySet()) {
			assertEquals((Double) grams.getLogProb(Arrays.asList(StringUtils.split(test.getKey()))), test.getValue());
		}

	}
}
