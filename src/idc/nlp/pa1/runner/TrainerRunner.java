package idc.nlp.pa1.runner;

import idc.nlp.pa1.AbstractTrainer;
import idc.nlp.pa1.CLIUtils;
import idc.nlp.pa1.baseline.BaselineTrainer;
import idc.nlp.pa1.ngram.BigramTrainer;
import idc.nlp.pa1.ngram.TrigramTrainer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class TrainerRunner {

	public static void main(String[] args) throws FileNotFoundException, IOException {
		int model = CLIUtils.parseArgInt(args, 0);
		File file = CLIUtils.parseArgFile(args, 1);
		boolean smoothing = CLIUtils.parseArgBool(args, 2, true);
		AbstractTrainer trainer;
		switch (model) {
		case 0: {
			File output = CLIUtils.setExtenstion(file, "dis");
			trainer = new BaselineTrainer(file, output);
			break;
		}
		case 2: {
			File lexFile = CLIUtils.setExtenstion(file, "lex");
			File gramFile = CLIUtils.setExtenstion(file, "gram");
			trainer = new BigramTrainer(file, smoothing, lexFile, gramFile);
			break;
		}
		case 3: {
			File lexFile = CLIUtils.setExtenstion(file, "lex");
			File gramFile = CLIUtils.setExtenstion(file, "gram");
			trainer = new TrigramTrainer(file, smoothing, lexFile, gramFile);
			break;
		}
		default:
			throw new IllegalStateException("Cannot find trainer for " + model);
		}
		System.out.println(String.format("Training model %d with%s smoothing...", model, (!smoothing ? "out" : "")));
		long startTime = System.currentTimeMillis();
		trainer.train();
		long duration = System.currentTimeMillis() - startTime;
		System.out.println(String.format("Training of model %d took me %.2f seconds", model, duration / 1000d));

	}

}
