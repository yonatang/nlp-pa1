package idc.nlp.pa1;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

import com.google.common.base.Preconditions;

public class Utils {

	public static String doubleToString(double d) {
		return String.format("%.16f", d);
	}

	public static InputStream getStream(Object obj, String resourceName) {
		Preconditions.checkNotNull(obj);
		Preconditions.checkNotNull(resourceName);
		return obj.getClass().getResourceAsStream(resourceName);
	}

	@SuppressWarnings("resource")
	public static Reader readerFromStreamOrFile(InputStream stream, File file) throws FileNotFoundException {
		return new InputStreamReader(stream == null ? new FileInputStream(file) : stream);
	}

	@SuppressWarnings("resource")
	public static Writer writerFromStreamOrFile(OutputStream stream, File file) throws FileNotFoundException {
		return new OutputStreamWriter(stream == null ? new FileOutputStream(file) : stream);
	}

}
