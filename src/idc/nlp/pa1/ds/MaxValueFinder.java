package idc.nlp.pa1.ds;

import com.google.common.base.Preconditions;

public class MaxValueFinder<K, V extends Comparable<V>> {

	K topKey;
	V topValue;

	public boolean check(K key, V value) {
		Preconditions.checkNotNull(value);
		if (topKey == null || topValue.compareTo(value) < 0) {
			topKey = key;
			topValue = value;
			return true;
		}
		return false;
	}

	public K getTopKey() {
		return topKey;
	}

	public V getTopValue() {
		return topValue;
	}

	public void clear() {
		topKey = null;
		topValue = null;
	}

	@Override
	public String toString() {
		return "MaxValueFinder: " + topKey + "=" + topValue;
	}
}
