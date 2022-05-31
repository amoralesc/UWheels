package com.abmodel.uwheels.data

import android.util.Log
import com.abmodel.uwheels.data.model.LoggedInUser
import com.google.firebase.auth.AuthResult

/**
 * Login repository interface that provides the data access layer for the login
 */
interface AuthRepository {
	suspend fun login(email: String, password: String): Boolean
	suspend fun signUp(
		email: String, password: String, name: String,
		lastName: String, phone: String, photoUri: String?
	): Boolean
	fun logout()
	fun isLoggedIn(): Boolean
	fun getLoggedInUser(): LoggedInUser?
}

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */
class FirebaseAuthRepository internal constructor(
	private val mAuth: FirebaseAuthDataSource,
) : AuthRepository {

	/**
	 * Single instance of the login repository.
	 * The instance is created when the application starts and stays alive
	 * throughout the application lifecycle.
	 */
	companion object {
		private var instance: FirebaseAuthRepository? = null

		fun getInstance(): FirebaseAuthRepository {
			return instance ?: synchronized(this) {
				instance ?: FirebaseAuthRepository(
					FirebaseAuthDataSource()
				).also { instance = it }
			}
		}

		const val TAG = "FirebaseAuthRepository"
	}

	// in-memory cache of the loggedInUser object
	private var _user: LoggedInUser? = null

	init {
		// If user credentials will be cached in local storage, it is recommended it be encrypted
		// @see https://developer.android.com/training/articles/keystore
		_user = null
		isLoggedIn()
	}

	override suspend fun login(email: String, password: String): Boolean {
		val result = mAuth.login(email, password)
		Log.d(TAG, "Login: $result")

		if (result is Result.Success<AuthResult> && result.data.user != null) {
			updateLoggedInUser()
			return true
		}
		return false
	}

	override suspend fun signUp(
		email: String, password: String, name: String,
		lastName: String, phone: String, photoUri: String?
	): Boolean {
		val result = mAuth.signUp(email, password, name)
		Log.d(TAG, "Sign up: $result")

		if (result is Result.Success<AuthResult> && result.data.user != null) {
			updateLoggedInUser()
			return true
		}
		return false
	}

	override fun logout() {
		mAuth.logout()
	}

	override fun isLoggedIn(): Boolean {
		return mAuth.isLoggedIn()
	}

	override fun getLoggedInUser(): LoggedInUser? {
		return _user
	}

	private fun updateLoggedInUser() {
		val firebaseUser = mAuth.getCurrentUser()

		_user = LoggedInUser(
			firebaseUser?.uid ?: "",
			firebaseUser?.email,
			firebaseUser?.displayName,
			"name",
			"lastName",
			"phone",
			firebaseUser?.photoUrl,
		)
	}
}
