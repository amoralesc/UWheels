package com.abmodel.uwheels.data.model

import android.net.Uri

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
data class LoggedInUser(
	val userId: String,
	val email: String?,
	val displayName: String?,
	val name: String?,
	val lastName: String?,
	val phone: String?,
	val photoUrl: Uri?,
)