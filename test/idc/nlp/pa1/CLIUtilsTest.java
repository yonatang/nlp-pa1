package idc.nlp.pa1;

import static org.testng.Assert.*;

import java.io.File;

import org.testng.annotations.Test;

@Test
public class CLIUtilsTest {

	public void shouldSwapExt(){
		assertEquals(new File("c:/a/b.txt"), CLIUtils.setExtenstion(new File("c:/a/b.bak"), "txt"));
		assertEquals(new File("c:/a/b.txt"), CLIUtils.setExtenstion(new File("c:/a/b"), "txt"));
	}
}
