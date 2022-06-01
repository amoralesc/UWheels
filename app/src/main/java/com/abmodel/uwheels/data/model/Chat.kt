package com.abmodel.uwheels.data.model

data class Chat(
	val id: String = "",
	val rideId: String = "",
	val name: String = WheelsType.CLASSIC_WHEELS.toString(),
	val date: CustomDate = CustomDate(),
	val source: CustomAddress = CustomAddress(),
	val destination: CustomAddress = CustomAddress(),
)