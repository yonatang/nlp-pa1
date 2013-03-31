package idc.nlp.pa1;

import java.text.MessageFormat;
import java.text.ParseException;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.TreeMultiset;

public class Test {
	private static final Logger logger=L.getLogger();
	public static void main(String... args) throws ParseException {
		logger.info("TEST");
//		TreeMap<String, Integer> map=new ValueSortedMap<>(Ordering.natural());
//		
//		map.put("aaaa", 30);
//		map.put("bbbbb", 50);
//System.out.println(map.containsKey("aaaaa"));		
//		System.out.println(map.firstKey());
		
		Multiset<Integer> ms=HashMultiset.create();
		Multiset<Integer> ms2=TreeMultiset.create();
		
		ms.add(2);
		ms.add(2);
		ms.add(3);
		ms.add(1);
		
		System.out.println(ms.size());
		
		System.out.println(ms);
		ms2.addAll(ms);
		ms.add(1);
		System.out.println(ms2);
		System.out.println(ms);
		
		System.out.println("\\9f-grams\\".matches("\\\\\\d+\\-grams\\\\"));
		MessageFormat mf=new MessageFormat("\\{0}-grams\\");
		System.out.println(mf.parse("\\92-grams\\")[0]);
		
		System.out.println(StringUtils.split("-123.123123123\t\t  ABC","\t ")[0]);
		

	}
}
