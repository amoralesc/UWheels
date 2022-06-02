package com.abmodel.uwheels.data.model

data class RideUser(
	val uid: String = "",
	val name: String = "",
	val lastName: String = "",
	val rating: Rating = Rating(),
	val photoUrl: String? = null,
) {
	companion object {
		fun fromLoggedInUser(user: LoggedInUser) = RideUser(
			uid = user.uid,
			name = user.name,
			lastName = user.lastName,
			rating =
			if (user.driverMode) user.driverRating
			else user.passengerRating,
			photoUrl = user.photoUrl.toString()
		)
	}
}