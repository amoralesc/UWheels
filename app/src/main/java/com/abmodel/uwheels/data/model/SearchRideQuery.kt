package com.abmodel.uwheels.data.model

data class SearchRideQuery(
	val userId: String,
	val source: CustomAddress,
	val destination: CustomAddress,
	val date: CustomDate,
	val maxDistance: Double = Double.MAX_VALUE,
	val maxTimeDifference: Long = Long.MAX_VALUE,
)
