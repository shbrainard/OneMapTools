package org.uwm.vcfconverter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CodingMapper {
	private final Map<Integer, Character> mapping;
	
	private CodingMapper(Map<Integer, Character> mapping) {
		this.mapping = mapping;
	}

	public static CodingMapper create(Pair p1, Pair p2) {
		// OneMap can't represent all mappings, and there seems to be little rhyme or reason as to what mappings it accepts (although
		// each pair is sorted alphabetically).
		// ab x aa is fine, but not ab x bb, but also cc x ab is fine, but not aa x bc. So, we'll pick a mapping, and see if it
		// corresponds to a valid OneMap type. If not, we'll try a different mapping. For ab x cd, there are 24 combinations,
		// but for most cases there will be fewer, so this shouldn't be computationally nuts.
		Set<Integer> keyVals = new HashSet<>();
		List<List<Integer>> keys = new ArrayList<>();
		for (int val : new int[] {p1.getLhSide(), p1.getRhSide(), p2.getLhSide(), p2.getRhSide()}) {
			if (!keyVals.contains(val)) {
				keyVals.add(val);
				if (keys.isEmpty()) {
					List<Integer> initial = new ArrayList<>();
					initial.add(val);
					keys.add(initial);
				} else {
					List<List<Integer>> newKeys = new ArrayList<>();
					for (List<Integer> ordering : keys) {
						for (int i = 0; i <= ordering.size(); i++) {
							List<Integer> newOrdering = new ArrayList<>(ordering);
							newOrdering.add(i, val);
							newKeys.add(newOrdering);
						}
					}
					keys = newKeys;
				}
			}
		}
		
		for (List<Integer> ordering : keys) {
			Map<Integer, Character> mapping = new HashMap<>();
			for (int val : ordering) {
				mapping.put(val, (char) ('a' + mapping.size()));
			}
			
			CodingMapper mapper = new CodingMapper(mapping);
			String encodedType = mapper.map(p1, p2);
			for (MarkerType type : MarkerType.values()) {
				if (type.getMatchingString().equals(encodedType)) {
					return mapper; // this is a valid mapping
				}
			}
		}
		return null;
	}
	
	public String map(Pair p1, Pair p2) {
		return new StringBuilder().append(map(p1))
				.append(map(p2)).toString();
	}
	
	public char map(int val) {
		if (!mapping.containsKey(val)) {
			throw new IllegalStateException("No mapping for value " + val + " in " + mapping);
		}
		return mapping.get(val);
	}

	public String map(Pair pair) {
		// sort these so we always return ab, not ba
		StringBuilder result = new StringBuilder();
		List<Character> mapped = new ArrayList<>();
		mapped.add(map(pair.getLhSide()));
		mapped.add(map(pair.getRhSide()));
		Collections.sort(mapped);
		for (char ch : mapped) {
			result.append(ch);
		}
		return result.toString();
	}

}
