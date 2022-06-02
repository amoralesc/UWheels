package com.abmodel.uwheels.data.model

data class Chat(
	val id: String = "",
	val rideId: String = "",
	val name: String = WheelsType.CLASSIC_WHEELS.toString(),
	val date: CustomDate = CustomDate(),
	val source: CustomAddress = CustomAddress(),
	val destination: CustomAddress = CustomAddress(),
	val users: List<RideUser> = listOf(),
) {
	companion object {
		fun fromRide(ride: Ride): Chat {
			return Chat(
				id = ride.chatId,
				rideId = ride.id,
				name = ride.wheelsType,
				date = ride.date,
				source = ride.source,
				destination = ride.destination,
				users = ride.passengers.toMutableList().apply {
					add(ride.host)
				}
			)
		}
	}
}
