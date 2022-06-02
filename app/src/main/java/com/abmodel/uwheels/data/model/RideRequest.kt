package com.abmodel.uwheels.data.model

data class RideRequest(
	val user: RideUser = RideUser(),
	val sentDate: CustomDate = CustomDate(),
	val source: CustomAddress = CustomAddress(),
	val destination: CustomAddress = CustomAddress(),
	val sourceDistance: Double = 0.0,
	val destinationDistance: Double = 0.0,
	val date: CustomDate = CustomDate(),
	val dateDifference: Long = 0,
	val status: String = RideRequestStatus.PENDING.toString()
)
