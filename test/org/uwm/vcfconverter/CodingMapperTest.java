package org.uwm.vcfconverter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class CodingMapperTest {
	
	@Test
	public void testMapping() throws Exception {
		assertEquals(MarkerType.HOMOZYGOUS, getType(new Pair(0,0), new Pair(0, 0))); 
		assertEquals(MarkerType.EACH_HOMOZYGOUS, getType(new Pair(0,0), new Pair(1,1))); 
		assertEquals(MarkerType.D214, getType(new Pair(0,0), new Pair(1, 2))); 
		assertEquals(MarkerType.D214, getType(new Pair(1,1), new Pair(0, 2))); 
		assertEquals(MarkerType.D110, getType(new Pair(0,1), new Pair(1, 1))); 
		assertEquals(MarkerType.D110, getType(new Pair(1,0), new Pair(0, 0))); 
		assertEquals(MarkerType.D110, getType(new Pair(0,1), new Pair(0, 0))); 
		assertEquals(MarkerType.D110, getType(new Pair(0,2), new Pair(0, 0))); 
		assertEquals(MarkerType.B37, getType(new Pair(1,0), new Pair(0, 1))); 
		assertEquals(MarkerType.B37, getType(new Pair(0,1), new Pair(0, 1))); 
		assertEquals(MarkerType.B37, getType(new Pair(0,2), new Pair(0, 2))); 
		assertEquals(MarkerType.D215, getType(new Pair(0,0), new Pair(0, 1))); 
		assertEquals(MarkerType.D215, getType(new Pair(1,1), new Pair(0, 1))); 
		assertEquals(MarkerType.D215, getType(new Pair(2,2), new Pair(1, 2))); 
		
		assertEquals(MarkerType.A2, getType(new Pair(1,2), new Pair(0, 2))); 
		CodingMapper weirdMapper = CodingMapper.create(new Pair(1,2), new Pair(0, 2)).get().mapper;
		assertEquals("a", weirdMapper.map(new Pair(2,2)));
		assertEquals("ba", weirdMapper.map(new Pair(2,1)));
		assertEquals("bc", weirdMapper.map(new Pair(0,1)));
	}

	private MarkerType getType(Pair p1, Pair p2) {
		return CodingMapper.create(p1, p2).get().markerType;
	}
}
