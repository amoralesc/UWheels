package com.abmodel.uwheels.data.model.firebase

import com.abmodel.uwheels.data.model.Vehicle

data class UserDocument(
	var driverApplication: DriverApplicationDocument? = null,
	var driverMode: Boolean? = null,
	@field:JvmField var isDriver: Boolean? = null,
	var lastName: String? = null,
	var name: String? = null,
	var phone: String? = null,
	val passengerRating: RatingDocument? = null,
	val driverRating: RatingDocument? = null,
	val vehicles: List<Vehicle>? = null
)
