package com.abmodel.uwheels.data.model

data class RideRequest(
	val user: RideUser = RideUser(),
	val date: CustomDate = CustomDate(),
	val status: String = RideRequestStatus.PENDING.toString()
)
