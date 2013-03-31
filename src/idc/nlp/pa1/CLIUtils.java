package idc.nlp.pa1;

import java.io.File;

import org.apache.commons.io.FilenameUtils;

public class CLIUtils {

	public static void validateArgNumber(String[] args, int number) {
		if (args.length < number + 1) {
			throw new IllegalArgumentException("Missing argument #" + number);
		}
	}

	public static int parseArgInt(String[] args, int number) {
		validateArgNumber(args, number);
		try {
			return Integer.parseInt(args[number]);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Argument #" + number + " is not a number");
		}
	}

	public static File parseArgFile(String[] args, int number) {
		validateArgNumber(args, number);
		File file = new File(args[number]);
		if (!file.exists() || !file.isFile()) {
			throw new IllegalArgumentException("File " + file + " is missing");
		}
		return file;
	}

	public static Model parseArgModel(String[] args, int number) {
		Model m = Model.fromNumber(parseArgInt(args, number));
		if (m == null)
			throw new IllegalArgumentException("Unknown model " + number);
		return m;
	}

	public static File setExtenstion(File file, String ext) {
		String oldExt = FilenameUtils.getExtension(file.getName());
		if (oldExt.isEmpty()) {
			ext = "." + ext;
		}
		String oldPath = file.getPath();
		return new File(oldPath.substring(0, oldPath.length() - oldExt.length()) + ext);
	}

	public static boolean parseArgBool(String[] args, int number, boolean defVal) {
		if (args.length < number + 1) {
			return defVal;
		}
		String arg = args[number];
		switch (arg.toLowerCase()) {
		case "yes":
		case "y":
		case "1":
		case "true":
		case "t":
			return true;
		default:
			return false;
		}
	}
}
