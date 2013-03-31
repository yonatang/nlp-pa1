package idc.nlp.pa1;

import java.io.OutputStreamWriter;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

public class L {

	public static Logger getLogger() {
		StackTraceElement st = Thread.currentThread().getStackTrace()[2];
		Logger l = Logger.getLogger(st.getClassName());
		return l;
	}

	static {
		LogManager.getRootLogger().setLevel(Level.WARN);
		ConsoleAppender ca = new ConsoleAppender();
		LogManager.getRootLogger().addAppender(ca);
		ca.setWriter(new OutputStreamWriter(System.out));
//		PatternLayout pl = new PatternLayout("%d %-5p %c{2} - %m (%F:%L)%n");
		PatternLayout pl = new PatternLayout("%-5p %c{1} - %m (%F:%L)%n");
		ca.setLayout(pl);
	}
}
