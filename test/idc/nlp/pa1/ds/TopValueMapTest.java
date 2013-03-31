package idc.nlp.pa1.ds;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Test
public class TopValueMapTest {

	TopValueMap<String, Integer> map;

	@BeforeMethod
	void setup() {
		map = new TopValueMap<>();
	}

	public void shouldReturnMaxValue() {
		map.put("abc", 10);
		map.put("zzz", 5);
		Assert.assertEquals(map.getTopKey(), "abc");
		Assert.assertEquals(map.getTopValue().intValue(), 10);
	}

	public void shouldReturnNull() {
		Assert.assertNull(map.getTopKey());
		Assert.assertNull(map.getTopValue());
	}
	
	@Test(expectedExceptions=UnsupportedOperationException.class)
	public void shouldDisableRemove(){
		map.remove("abc");
	}
}
