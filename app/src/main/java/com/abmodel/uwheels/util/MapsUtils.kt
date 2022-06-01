package com.abmodel.uwheels.util

import com.abmodel.uwheels.network.maps.response.DirectionsRoute
import com.google.android.gms.maps.model.LatLng
import kotlin.math.*

const val RADIUS_OF_EARTH_KM = 6371.0       // km
const val RADIUS_OF_EARTH_M = 6371000.0     // meters

fun LatLng.distanceTo(other: LatLng, units: String = "M"): Double {
	assert(units == "M" || units == "KM")

	val latDistance = Math.toRadians(this.latitude - other.latitude)
	val lngDistance = Math.toRadians(this.longitude - other.longitude)
	val a = (sin(latDistance / 2) * sin(latDistance / 2)
			+ (cos(Math.toRadians(this.latitude)) * cos(Math.toRadians(other.latitude))
			* sin(lngDistance / 2) * sin(lngDistance / 2)))
	val c = 2 * atan2(sqrt(a), sqrt(1 - a))
	val result = if (units == "M") RADIUS_OF_EARTH_M * c
	else RADIUS_OF_EARTH_KM * c * 0.621371
	return (result * 100.0).roundToLong() / 100.0
}

fun parseLatLng(latLng: LatLng): String {
	return "${latLng.latitude},${latLng.longitude}"
}

fun LatLng.midPointTo(other: LatLng): LatLng {
	return LatLng(
		(this.latitude + other.latitude) / 2,
		(this.longitude + other.longitude) / 2
	)
}

fun DirectionsRoute.decodeRoute(): List<LatLng> {

	val points: MutableList<LatLng> = mutableListOf()
	this.legs.forEach { leg ->
		leg.steps.forEach { step ->
			points.addAll(
				PolyUtil.decode(step.polyline.points)
			)
		}
	}

	return points
}
