package idc.nlp.pa1.ds;

import java.util.HashMap;

import com.google.common.base.Preconditions;

public class TopValueMap<K, V extends Comparable<V>> extends HashMap<K, V> {

	private static final long serialVersionUID = -5610992942649082983L;
	
	private K topKey = null;
	private V topValue = null;

	@Override
	public V put(K key, V value) {
		Preconditions.checkNotNull(value);
		if (topKey == null || topValue.compareTo(value) < 0) {
			topKey = key;
			topValue = value;
		}
		return super.put(key, value);
	}
	
	public K getTopKey() {
		return topKey;
	}

	public V getTopValue() {
		return topValue;
	}
	
	@Override
	public V remove(Object paramObject) {
		throw new UnsupportedOperationException("remove is not supported");
	}
}
