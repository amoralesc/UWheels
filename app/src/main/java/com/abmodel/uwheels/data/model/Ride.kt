package com.abmodel.uwheels.data.model

data class Ride(
	val source: CustomAddress = CustomAddress(),
	val destination: CustomAddress = CustomAddress(),
	val date: CustomDate = CustomDate(),
	val wheelsType: String = WheelsType.CLASSIC_WHEELS.toString(),
	val transportation: String? = null,
	val currentCapacity: Int = 0,
	val totalCapacity: Int = 0,
	val rating: Rating = Rating(),
	val host: RideUser = RideUser(),
	val state: String = RideState.CREATED.toString(),
	val passengers: MutableList<RideUser> = mutableListOf(),
	val subscribers: MutableList<String> = mutableListOf(),
	val price: Double? = null,
	val vehicle: Vehicle? = null,
)
