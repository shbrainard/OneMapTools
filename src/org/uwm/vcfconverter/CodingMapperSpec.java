package org.uwm.vcfconverter;

public class CodingMapperSpec {
	public final CodingMapper mapper;
	public final MarkerType markerType;
	
	public CodingMapperSpec(CodingMapper mapper, MarkerType markerType) {
		this.mapper = mapper;
		this.markerType = markerType;
	}
}
