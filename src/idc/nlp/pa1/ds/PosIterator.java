package idc.nlp.pa1.ds;

import java.util.Iterator;
import java.util.Map;

public class PosIterator implements Iterator<String[]> {

	private final int size;
	private final Map<Integer, String> posCounter;
	private final int[] varsI;
	private boolean oneBeforeLast = false;
	private boolean hasMore = true;

	PosIterator(int size, Map<Integer,String> posCounter){
		this.size = size;
		varsI = new int[size];
		this.posCounter=posCounter;
		for (int i = 0; i < size; i++) {
			varsI[i] = 0;
		}
	}
//	public PosIterator(int size, Set<String> posSet) {
//		this.size = size;
//		TreeSet<String> set = new TreeSet<>(posSet);
//		posCounter=new HashMap<>();
//		int i = 0;
//		Iterator<String> iter = set.iterator();
//		while (iter.hasNext()) {
//			posCounter.put(i, iter.next());
//			i++;
//		}
//		varsI = new int[size];
//		for (i = 0; i < size; i++) {
//			varsI[i] = 0;
//		}
//	}

	@Override
	public boolean hasNext() {
		if (!hasMore)
			return false;
		if (oneBeforeLast) {
			hasMore = false;
			return false;
		}
		int countSize = posCounter.size() - 1;
		for (int i = 0; i < size; i++) {
			if (varsI[i] < countSize)
				return true;
		}
		oneBeforeLast = true;
		return true;
	}

	@Override
	public String[] next() {
		if (!hasMore){
			throw new IllegalStateException("no more elements");
		}
		String[] vars = new String[size];
		for (int i = 0; i < size; i++) {
			vars[i] = posCounter.get(varsI[i]);
		}

		int i = size - 1;
		while (true) {
			varsI[i]++;
			if (varsI[i] >= posCounter.size() && i > 0) {
				varsI[i] = 0;
			} else {
				break;
			}
			i--;
		}
		return vars;
		//StringUtils.join(vars, ' ');
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("remove not supported");
	}

}
