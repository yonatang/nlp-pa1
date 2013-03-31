package idc.nlp.pa1.baseline;

import idc.nlp.pa1.Utils;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;

import static org.testng.Assert.*;

@Test
public class BaselineTrainerTest {

	InputStream input;
	InputStream freq;

	@BeforeMethod
	void setup() {
		input = Utils.getStream(this, "baseline-test.train");
		freq = Utils.getStream(this, "baseline-test.freq");
	}

	@AfterMethod
	void teardown() {
		IOUtils.closeQuietly(input);
		IOUtils.closeQuietly(freq);
	}

	public void testOutput() throws FileNotFoundException, IOException {
		try (ByteArrayOutputStream out = new ByteArrayOutputStream();) {
			BaselineTrainer bt = new BaselineTrainer(input, out);
			bt.train();
			String outputStr = out.toString();
			String expectedStr = IOUtils.toString(freq);
			assertEquals(outputStr, expectedStr);
		}
	}

	public void testTagLoadMaps() throws IOException {
		BaselineTags bt = new BaselineTags(freq);
		Map<String, Map<String, Integer>> expected = ImmutableMap.<String, Map<String, Integer>> of(//
				"BGDWL", ImmutableMap.of("RB", 3), //
				"W", ImmutableMap.of("CC", 3),//
				"NQMH", ImmutableMap.of("NN", 2),//
				"THIH", ImmutableMap.of("VB", 2, "AUX", 1), //
				"NQMI", ImmutableMap.of("NN", 1)//
				);

		for (String expSeg : expected.keySet()) {
			assertEquals(bt.getTags(expSeg), expected.get(expSeg));
		}
		for (String actualSeg : expected.keySet()) {
			assertEquals(bt.getTags(actualSeg), expected.get(actualSeg));
		}
	}

	public void testTagLoadBest() throws IOException {
		BaselineTags bt = new BaselineTags(freq);
		Map<String, String> expected = ImmutableMap.<String, String> of(//
				"BGDWL", "RB", //
				"W", "CC",//
				"NQMH", "NN",//
				"THIH", "VB",//
				"NQMI", "NN");

		for (String expSeg : expected.keySet()) {
			assertEquals(bt.getBestTag(expSeg), expected.get(expSeg));
		}
		for (String actualSeg : expected.keySet()) {
			assertEquals(bt.getBestTag(actualSeg), expected.get(actualSeg));
		}
		assertEquals(bt.getBestTag("UNK"), "NN");
		assertEquals(bt.getBestTag("not_a_word&"), "NN");

	}
}
