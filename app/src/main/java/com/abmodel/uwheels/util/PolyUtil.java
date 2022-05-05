package com.abmodel.uwheels.util;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class PolyUtil {
	public static List<LatLng> decode(final String encoded) {
		return com.google.maps.android.PolyUtil.decode(encoded);
	}
}
