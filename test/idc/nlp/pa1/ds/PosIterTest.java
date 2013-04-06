package idc.nlp.pa1.ds;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;

@Test
public class PosIterTest {

	private Function<String[], String> f = new Function<String[], String>() {
		public String apply(String[] str) {
			return StringUtils.join(str, ' ');
		}
	};

	public void testSingle() {
		Set<String> set = ImmutableSet.of("a", "b", "c");
		PosIterator pi = new PosIteratorFactory(1, set).iterator();
		Set<String> expected = ImmutableSet.of("a", "b", "c");
		Set<String[]> actual = new HashSet<>();
		while (pi.hasNext()) {
			actual.add(pi.next());
		}
		Set<String> act2 = ImmutableSet.copyOf(Collections2.transform(actual, f));
		Assert.assertEquals(act2, expected);

	}

	public void testDouble() {
		Set<String> set = ImmutableSet.of("a", "b", "c");
		PosIterator pi= new PosIteratorFactory(2, set).iterator(); 
		Set<String[]> actual = new HashSet<>();
		// Function<String, String[]> f = new Function<String, String[]>() {
		// public String[] apply(String str) {
		// return StringUtils.split(str);
		// }
		// };
		Set<String> expected = ImmutableSet.of("a a", "a b", "a c", "b a", "b b", "b c", "c a", "c b", "c c");

		while (pi.hasNext()) {
			actual.add(pi.next());
		}
		Set<String> act2 = ImmutableSet.copyOf(Collections2.transform(actual, f));
		Assert.assertEquals(act2, expected);
	}

	public void testTriple() {
		Set<String> set = ImmutableSet.of("a", "b", "c");
		PosIterator pi = new PosIteratorFactory(3, set).iterator();
		Set<String[]> actual = new HashSet<>();
		Set<String> expected = ImmutableSet.of("a a a", "a a b", "a a c", "a b a", "a b b", "a b c", "a c a", "a c b",
				"a c c", "b a a", "b a b", "b a c", "b b a", "b b b", "b b c", "b c a", "b c b", "b c c", "c a a",
				"c a b", "c a c", "c b a", "c b b", "c b c", "c c a", "c c b", "c c c");
		while (pi.hasNext()) {
			actual.add(pi.next());
		}

		Set<String> act2 = ImmutableSet.copyOf(Collections2.transform(actual, f));
		Assert.assertEquals(act2, expected);
	}
}
