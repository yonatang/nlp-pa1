package idc.nlp.pa1.ds;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class PosIteratorFactory {

	private final Map<Integer, String> posCounter;
	private final int size;

	public PosIteratorFactory(int size, Set<String> posSet) {
		Map<Integer, String> map = new HashMap<>();
		int i = 0;
		TreeSet<String> set = new TreeSet<>(posSet);
		for (String pos : set) {
			map.put(i, pos);
			i++;
		}
		this.posCounter = Collections.unmodifiableMap(map);
		this.size = size;
	}

	public PosIterator iterator() {
		return new PosIterator(size, posCounter);
	}

}
