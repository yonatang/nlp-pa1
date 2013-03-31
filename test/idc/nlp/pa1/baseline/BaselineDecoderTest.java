package idc.nlp.pa1.baseline;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

@Test
public class BaselineDecoderTest {

	BaselineTags tags;
	InputStream input;

	@BeforeMethod
	void setup() throws IOException {
		try (InputStream freq = this.getClass().getResourceAsStream("baseline-test.freq");) {
			tags = new BaselineTags(freq);
		}
		input = this.getClass().getResourceAsStream("baseline-test.test");
	}

	@AfterMethod
	void teardown() {
		IOUtils.closeQuietly(input);
	}

	public void test() throws IOException {
		try (ByteArrayOutputStream out = new ByteArrayOutputStream();) {
			BaselineDecoder bd = new BaselineDecoder(input, out, tags);
			bd.decode();
			String actual = out.toString().trim();
			String expected = IOUtils.toString(this.getClass().getResource("baseline-test.gold")).trim();
			assertEquals(actual, expected);
		}
	}
}
