package org.uwm.vcfconverter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class CodingMapper {
	private final Map<Integer, Character> mapping;
	
	// there is one, and only one, instance where the mapping shouldn't be sorted alphabetically
	// this is beyond ridiculous, but such is life (it's not even one *type* that isn't alphabetical, or one child across all types -
	// it's just one specific child of one specific type)
	private final boolean mixThingsUp;
	
	private CodingMapper(Map<Integer, Character> mapping, boolean mixThingsUp) {
		this.mapping = mapping;
		this.mixThingsUp = mixThingsUp;
	}

	public static Optional<CodingMapperSpec> create(Pair p1, Pair p2) {
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
			
			CodingMapper mapper = new CodingMapper(mapping, false);
			String encodedType = mapper.mapNoCollapse(p1) + mapper.mapNoCollapse(p2);
			for (MarkerType type : MarkerType.values()) {
				if (type.getMatchingString().equals(encodedType)) {
					if (type == MarkerType.A2) {
						return Optional.of(new CodingMapperSpec(new CodingMapper(mapping, true), type));
					}
					return Optional.of(new CodingMapperSpec(mapper, type)); // this is a valid mapping
				}
			}
		}
		return Optional.empty();
	}
	
	public char map(int val) {
		if (!mapping.containsKey(val)) {
			return (char) ('a' + mapping.size());
		}
		return mapping.get(val);
	}
	
	private String mapNoCollapse(Pair pair) {
		// sort these so we always return ab, not ba
		// also, aa should be returned as a
		char ch1 = map(pair.getLhSide());
		char ch2 = map(pair.getRhSide());
		return ch1 > ch2 ? "" + ch2 + ch1 :
			"" + ch1 + ch2;
	}

	public String map(Pair pair) {
		// sort these so we always return ab, not ba
		// also, aa should be returned as a
		char ch1 = map(pair.getLhSide());
		char ch2 = map(pair.getRhSide());
		String result = ch1 > ch2 ? "" + ch2 + ch1 :
			ch1 == ch2 ? "" + ch1 : "" + ch1 + ch2;
		if (mixThingsUp && result.equals("ab")) {
			return "ba";
		}
		return result;
	}

}
