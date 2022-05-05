package com.abmodel.uwheels.network.maps

import com.squareup.moshi.Json

data class DirectionsGeocodedWaypoint(
	@Json(name = "geocoder_status") val geocoderStatus: String,
	@Json(name = "place_id") val placeId: String,
	@Json(name = "types") val types: List<String>
)

data class LatLngLiteral(
	@Json(name = "lat") val lat: Double,
	@Json(name = "lng") val lng: Double
)

data class Bounds(
	@Json(name = "northeast") val northeast: LatLngLiteral,
	@Json(name = "southwest") val southwest: LatLngLiteral
)

data class TextValueObject(
	@Json(name = "text") val text: String,
	@Json(name = "value") val value: Int
)

data class DirectionsPolyline(
	@Json(name = "points") val points: String
)

data class DirectionsStep(
	@Json(name = "duration") val duration: TextValueObject,
	@Json(name = "end_location") val endLocation: LatLngLiteral,
	@Json(name = "html_instructions") val htmlInstructions: String,
	@Json(name = "polyline") val polyline: DirectionsPolyline,
	@Json(name = "start_location") val startLocation: LatLngLiteral,
	@Json(name = "travel_mode") val travelMode: String
)

data class DirectionsLeg(
	@Json(name = "end_address") val endAddress: String,
	@Json(name = "end_location") val endLocation: LatLngLiteral,
	@Json(name = "start_address") val startAddress: String,
	@Json(name = "start_location") val startLocation: LatLngLiteral,
	@Json(name = "steps") val steps: List<DirectionsStep>,
	@Json(name = "traffic_speed_entry") val trafficSpeedEntry: List<Any>,
	@Json(name = "via_waypoint") val viaWaypoint: List<Any>
)

data class DirectionsRoute(
	@Json(name = "bounds") val bounds: Bounds,
	@Json(name = "copyrights") val copyrights: String,
	@Json(name = "legs") val legs: List<DirectionsLeg>,
	@Json(name = "overview_polyline") val overviewPolyline: DirectionsPolyline,
	@Json(name = "summary") val summary: String,
	@Json(name = "warnings") val warnings: List<String>,
	@Json(name = "waypoint_order") val waypointOrder: List<Int>
)

data class DirectionsResponse(
	@Json(name = "geocoded_waypoints") val directionsGeocodedWaypoints: List<DirectionsGeocodedWaypoint>?,
	@Json(name = "routes") val routes: List<DirectionsRoute>?,
	@Json(name = "status") val status: String?,
	@Json(name = "error_message") val errorMessage: String?
)
