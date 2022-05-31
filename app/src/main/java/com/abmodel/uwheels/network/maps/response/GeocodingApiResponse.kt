package com.abmodel.uwheels.network.maps.response

import com.squareup.moshi.Json

data class GeocodingLocation(
	val lat: Double,
	val lng: Double
)

data class GeocodingViewport(
	@Json(name = "southwest") val southwest: GeocodingLocation,
	@Json(name = "northeast") val northeast: GeocodingLocation
)

data class GeocodingGeometry(
	@Json(name = "location") val location: GeocodingLocation,
	@Json(name = "location_type") val locationType: String?,
	@Json(name = "viewport") val viewport: GeocodingViewport?
)

data class GeocodingResult(
	@Json(name = "address_components") val addressComponents: List<Any>?,
	@Json(name = "formatted_address") val formattedAddress: String?,
	@Json(name = "geometry") val geometry: GeocodingGeometry,
	@Json(name = "place_id") val placeId: String?,
	@Json(name = "types") val types: List<String>?
)

data class GeocodingResponse(
	@Json(name = "results") val results: List<GeocodingResult>,
	@Json(name = "status") val status: String
)
