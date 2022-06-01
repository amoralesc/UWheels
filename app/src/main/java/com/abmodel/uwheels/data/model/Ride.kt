package com.abmodel.uwheels.data.model

data class Ride(
	val source: CustomAddress = CustomAddress(),
	val destination: CustomAddress = CustomAddress(),
	val date: CustomDate = CustomDate(),
	val wheelsType: String = WheelsType.CLASSIC_WHEELS.toString(),
	val transportation: String? = null,
	var currentCapacity: Int = 0,
	val totalCapacity: Int = 0,
	val rating: Rating = Rating(),
	val host: RideUser = RideUser(),
	var status: String = RideStatus.OPEN.toString(),
	val passengers: MutableList<RideUser> = mutableListOf(),
	val subscribers: MutableList<String> = mutableListOf(),
	var chatId: String = "",
	val price: Double? = null,
	val vehicle: Vehicle? = null,
	val startedDate: CustomDate? = null,
	val finishedDate: CustomDate? = null,
	var duration: Long? = null,
)
