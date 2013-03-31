package idc.nlp.pa1.runner;

import idc.nlp.pa1.Model;
import idc.nlp.pa1.evaluator.Evaluator;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class EvaluatorRunner {

	public static void main(String... args) throws FileNotFoundException, IOException {
		try {
			Evaluator eval = new Evaluator(new FileInputStream("exps/heb-pos.tagged"), new FileInputStream(
					"exps/heb-pos.gold"), System.out, Model.BASELINE, false);
			eval.evaluate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
