package idc.nlp.pa1.runner;

import idc.nlp.pa1.AbstractTrainer;
import idc.nlp.pa1.CLIUtils;
import idc.nlp.pa1.Model;
import idc.nlp.pa1.baseline.BaselineTrainer;
import idc.nlp.pa1.bigram.BigramTrainer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.io.FilenameUtils;

public class TrainerRunner {

	public TrainerRunner(File trainFile, Model model) {

	}

	/**
	 * @param args
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public static void main(String[] args) throws FileNotFoundException, IOException {
		int model = CLIUtils.parseArgInt(args, 0);
		File file = CLIUtils.parseArgFile(args, 1);

		AbstractTrainer trainer;
		switch (model) {
		case 0:
			File output = CLIUtils.setExtenstion(file, "dis");
			trainer = new BaselineTrainer(file, output);
			break;
		case 2:
			File lexFile = CLIUtils.setExtenstion(file, "lex");
			File gramFile = CLIUtils.setExtenstion(file, "gram");
			trainer = new BigramTrainer(file, false, lexFile, gramFile);
			break;

		default:
			throw new IllegalStateException("Cannot find trainer for " + model);
		}
		long startTime = System.currentTimeMillis();
		trainer.train();
		long duration = System.currentTimeMillis() - startTime;
		System.out.println(String.format("Training of model %d took me %.2f seconds", model, duration / 1000d));

	}

}
