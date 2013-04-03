package idc.nlp.pa1.ngram;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Joiner;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multiset.Entry;
import com.google.common.collect.SortedSetMultimap;
import com.google.common.collect.TreeMultimap;

import static idc.nlp.pa1.ngram.NGrams.*;

public class NGramsCreator {

	public NGramsCreator(int n, boolean smoothing) {
		this.N = n;
		this.smoothing = smoothing;
	}

	private final int N;
	private final boolean smoothing;
	private final Multiset<String> allGrams = HashMultiset.create();
	private final Multiset<Integer> gramCount = HashMultiset.create();
	private int unigramsCount = 0;

	public void addSentence(List<String> tags) {
		List<String> newTags = new ArrayList<>();
		// pad with _START_ and _END_ tags
		for (int i = 0; i < N - 1; i++) {
			newTags.add(START);
		}
		newTags.addAll(tags);
		for (int i = 0; i < N - 1; i++) {
			newTags.add(END);
		}

		Joiner joiner = Joiner.on(' ');
		// Loop through the all the unigrams, bigrams... up to N-Grams
		for (int i = 0; i <= newTags.size(); i++) {
			// limit to N grams
			int loopTo = Math.min(newTags.size(), i + N);
			for (int j = i; j < loopTo; j++) {
				// If we just have _START_..._START_ or _END_..._END_ ngram,
				// skip it
				if (j < N - 1 || i >= tags.size() + N - 1)
					continue;

				// Can do better?
				String gram = joiner.join(newTags.subList(i, j + 1));
				if (allGrams.count(gram) == 0 && i < tags.size() + N - 1) {
					gramCount.add(j - i + 1);
				}

				// unigram conditional number counts differently
				if (i == j) {
					unigramsCount++;
				}
				allGrams.add(gram);
			}
		}
		// Store _START_..._START_ tag once per sentence -
		// avoiding multiple counts of _START_ tag
		for (int i = 0; i < N - 1; i++) {
			allGrams.add(StringUtils.repeat(START, " ", i + 1));
		}
	}

	public NGrams create() {
		// Generate and cache _START_..._START_ tags once, in order to remove
		// those from the output
		Map<Integer, String> starts = new HashMap<>();
		Map<Integer, String> ends = new HashMap<>();
		for (int i = 0; i < N; i++) {
			starts.put(i + 1, StringUtils.repeat(START, " ", i + 1));
			ends.put(i + 1, StringUtils.repeat(END, " ", i + 1));
		}

		SortedSetMultimap<Integer, NGram> groupBySize = TreeMultimap.create();
		for (Entry<String> thisGramCount : allGrams.entrySet()) {
			String gram = thisGramCount.getElement();
			int count = thisGramCount.getCount();
			int conditionalCount;
			int gramSize = StringUtils.countMatches(gram, " ") + 1;
			if (gram.equals(starts.get(gramSize)) || gram.equals(ends.get(gramSize))) {
				// this is a gram of the notion "_START_..._START_" or
				// "_END_..._END_". skip that.
				continue;
			}
			if (!gram.contains(" ")) {
				// this is a unigram. As said, the conditional count counts
				// differently in that case
				conditionalCount = unigramsCount;
			} else {
				// this is an N-gram. Conditional count counts normally
				String conditionalPart = StringUtils.substringBeforeLast(gram, " ");
				conditionalCount = allGrams.count(conditionalPart);
			}
			groupBySize.put(gramSize, new NGram(gram, gramSize, count, conditionalCount));
		}
		return new NGrams(groupBySize, gramCount, smoothing);
	}
}
