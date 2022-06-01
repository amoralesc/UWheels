package com.abmodel.uwheels.data.model

data class RideUser(
	val uid: String = "",
	val name: String = "",
	val lastName: String = "",
	val rating: Rating = Rating(),
	val photoUrl: String? = null,
)