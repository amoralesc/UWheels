package com.abmodel.uwheels.data.model

import android.net.Uri

/**
 * Data class that captures user information for logged in users retrieved from AuthRepository
 */
data class LoggedInUser(
	val uid: String,
	val email: String,
	val displayName: String,
	val name: String,
	val lastName: String,
	val phone: String,
	val photoUrl: Uri?,
	var isDriver: Boolean,
	var driverMode: Boolean,
	val passengerRating: Rating,
	val driverRating: Rating,
	val vehicles: List<Vehicle>
)