package idc.nlp.pa1.runner;

import idc.nlp.pa1.AbstractDecoder;
import idc.nlp.pa1.CLIUtils;
import idc.nlp.pa1.baseline.BaselineDecoder;
import idc.nlp.pa1.baseline.BaselineTags;
import idc.nlp.pa1.bigram.BigramDecoder;
import idc.nlp.pa1.bigram.NGrams;
import idc.nlp.pa1.bigram.PosEmissions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;

import org.apache.commons.io.FilenameUtils;

public class DecoderRunner {

	public static void main(String... args) throws ParseException, FileNotFoundException, IOException {
		int model = CLIUtils.parseArgInt(args, 0);
		File input = CLIUtils.parseArgFile(args, 1);
		File param1 = CLIUtils.parseArgFile(args, 2);
		File output = new File(FilenameUtils.concat(FilenameUtils.getFullPath(input.getPath()),
				FilenameUtils.getBaseName(input.getName()) + ".tagged"));
		AbstractDecoder decoder;
		if (model == 0) {
			decoder = new BaselineDecoder(input, output, new BaselineTags(param1));
		} else if (model == 2) {
			File param2 = CLIUtils.parseArgFile(args, 3);
			boolean smoothing = CLIUtils.parseArgBool(args, 4, true);
			decoder = new BigramDecoder(input, output, new NGrams(param1), new PosEmissions(param2, smoothing));
		} else {
			throw new IllegalArgumentException("Model " + model + " not supported");
		}

		System.out.println(String.format("Starting to decode using model %d", model));
		long startTime = System.currentTimeMillis();
		decoder.decode();
		long duration = System.currentTimeMillis() - startTime;
		System.out.println(String.format("Decoding using model %d took me %.2f seconds", model, duration / 1000d));
	}
}