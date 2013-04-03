package idc.nlp.pa1.ds;

import java.util.HashMap;

import com.google.common.base.Preconditions;

public class TopValueMap<K, V extends Comparable<V>> extends HashMap<K, V> {

	private static final long serialVersionUID = -5610992942649082983L;
	
	private MaxValueFinder<K, V> mvf=new MaxValueFinder<>();

	@Override
	public V put(K key, V value) {
		Preconditions.checkNotNull(value);
		mvf.check(key, value);
		return super.put(key, value);
	}
	
	public K getTopKey() {
		return mvf.getTopKey();
	}

	public V getTopValue() {
		return mvf.getTopValue();
	}
	
	@Override
	public void clear() {
		mvf.clear();
		super.clear();
	}
	
	@Override
	public V remove(Object paramObject) {
		throw new UnsupportedOperationException("remove is not supported");
	}
}
