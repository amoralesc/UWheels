package com.abmodel.uwheels.data.repository.auth

import android.net.Uri
import android.util.Log
import com.abmodel.uwheels.data.Result
import com.abmodel.uwheels.data.model.LoggedInUser
import com.google.firebase.auth.AuthResult

/**
 * Auth repository interface that provides the data access layer
 * for the authentication related operations.
 */
interface AuthRepository {
	suspend fun login(email: String, password: String): Boolean
	suspend fun signUp(
		email: String, password: String, name: String,
		lastName: String, phone: String, photoUri: Uri?
	): Boolean
	suspend fun fetchLoggedInUser()
	fun logout()
	fun isLoggedIn(): Boolean
	fun getLoggedInUser(): LoggedInUser
	fun isDriver(): Boolean
	fun isDriverModeOn(): Boolean
	suspend fun makeUserDriver()
	suspend fun setDriverMode(driverMode: Boolean)
}
