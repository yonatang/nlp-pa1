package idc.nlp.pa1.baseline;

import idc.nlp.pa1.ds.TopValueMap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.io.LineIterator;
import org.apache.commons.lang3.StringUtils;

public class BaselineTags {

	public BaselineTags() {
	}

	public BaselineTags(File input) throws FileNotFoundException, IOException {
		try (FileInputStream fis = new FileInputStream(input)) {
			load(fis);
		}
	}

	public BaselineTags(InputStream input) throws IOException {
		load(input);
	}

	private Map<String, TopValueMap<String, Integer>> map = new HashMap<>();

	public void addTag(String word, String tag) {
		TopValueMap<String, Integer> tags;
		if (map.containsKey(word)) {
			tags = map.get(word);
		} else {
			tags = new TopValueMap<>();
			map.put(word, tags);
		}
		if (tags.containsKey(tag)) {
			tags.put(tag, tags.get(tag) + 1);
		} else {
			tags.put(tag, 1);
		}
	}

	public Map<String, Integer> getTags(String word) {
		if (map.containsKey(word)) {
			return Collections.unmodifiableMap(map.get(word));
		}
		return Collections.emptyMap();
	}
	
	public Set<String> getSegments(){
		return map.keySet();
	}

	public String getBestTag(String word) {
		if (!map.containsKey(word)) {
			return "NN";
		}
		return map.get(word).getTopKey();
	}

	public void save(File output) throws IOException {
		try (FileOutputStream fos = new FileOutputStream(output)) {
			save(fos);
		}
	}

	public void save(OutputStream os) throws IOException {
		PrintWriter pw = new PrintWriter(os);

		for (Entry<String, TopValueMap<String, Integer>> entry : map.entrySet()) {
			StringBuilder sb = new StringBuilder();
			sb.append(entry.getKey()).append("\t");
			boolean first = true;
			for (Entry<String, Integer> tags : entry.getValue().entrySet()) {
				if (first) {
					first = false;
				} else {
					sb.append(",");
				}
				sb.append(tags.getKey()).append("=").append(tags.getValue());
			}
			pw.println(sb.toString());
		}
		pw.flush();
	}

	protected void load(InputStream input) throws IOException {
		InputStreamReader isr = new InputStreamReader(input);
		LineIterator li = new LineIterator(isr);
		while (li.hasNext()) {
			String line = li.nextLine().trim();
			if (line.isEmpty() || line.startsWith("#")) {
				continue;
			}
			String[] wordTags = StringUtils.split(line);
			String[] tagsFreqs = StringUtils.split(wordTags[1], ',');
			// map that store a pointer to the highest value entry
			TopValueMap<String, Integer> baselineFreqs = new TopValueMap<>();
			this.map.put(wordTags[0], baselineFreqs);
			for (String tagFreq : tagsFreqs) {
				String[] split = StringUtils.split(tagFreq, '=');
				String tag = split[0];
				Integer freq = Integer.parseInt(split[1]);
				baselineFreqs.put(tag, freq);
			}
		}
	}
}
