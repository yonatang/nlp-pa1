package idc.nlp.pa1.ds;

import static org.testng.Assert.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.Ordering;

@Test
public class ValueSortedMapTest {

	private ValueSortedMap<String, Integer> map;

	@BeforeMethod
	void init() {
		map = new ValueSortedMap<>(Ordering.natural());
	}

	public void shouldReturnByOrder() {
		map.put("abc", 99);
		map.put("zzz", 88);
		assertEquals(map.firstKey(), "zzz");
		assertEquals(map.lastKey(), "abc");
	}
	
	public void shouldKnowIfContains(){
		map.put("abc", 555);
		assertTrue(map.containsKey("abc"));
		assertFalse(map.containsKey("efg"));
	}
	
	public void shouldWorkOnEmpty(){
		assertNull(map.firstKey());
	}
}
