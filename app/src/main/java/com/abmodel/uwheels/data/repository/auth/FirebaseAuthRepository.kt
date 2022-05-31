package com.abmodel.uwheels.data.repository.auth

import android.net.Uri
import android.util.Log
import com.abmodel.uwheels.data.Result
import com.abmodel.uwheels.data.model.LoggedInUser
import com.abmodel.uwheels.data.model.UploadedFile
import com.google.firebase.auth.AuthResult

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */
class FirebaseAuthRepository internal constructor(
	private val mAuth: FirebaseAuthDataSource
) : AuthRepository {

	/**
	 * Single instance of the login repository.
	 * The instance is created when the application starts and stays alive
	 * throughout the application lifecycle.
	 */
	companion object {
		@Volatile
		private var instance: FirebaseAuthRepository? = null

		fun getInstance(): FirebaseAuthRepository {
			return instance ?: synchronized(this) {
				instance ?: FirebaseAuthRepository(
					FirebaseAuthDataSource.getInstance()
				).also { instance = it }
			}
		}

		const val TAG = "FirebaseAuthRepository"
	}

	// in-memory cache of the loggedInUser object
	// TODO: Could use Flow to update in real-time
	private var _user: LoggedInUser? = null

	init {
		_user = null
	}

	override suspend fun login(email: String, password: String): Boolean {
		val result = mAuth.login(email, password)
		Log.d(TAG, "Login: $result")

		if (result is Result.Success<AuthResult> && result.data.user != null) {
			fetchLoggedInUser()
			return true
		}
		return false
	}

	override suspend fun signUp(
		email: String, password: String, name: String,
		lastName: String, phone: String, photoUri: Uri?
	): Boolean {
		val result = mAuth.signUp(
			email, password, name, lastName, phone, photoUri
		)
		Log.d(TAG, "Sign up: $result")

		if (result is Result.Success<AuthResult> && result.data.user != null) {
			fetchLoggedInUser()
			return true
		}
		return false
	}

	override suspend fun fetchLoggedInUser() {
		mAuth.getCurrentUser().run {
			_user = this
		}
	}

	override fun logout() {
		mAuth.logout()
		_user = null
	}

	override fun isLoggedIn(): Boolean {
		return mAuth.isLoggedIn()
	}

	override fun getLoggedInUser(): LoggedInUser {
		return _user!!
	}

	override fun isDriver(): Boolean {
		return _user!!.isDriver
	}

	override fun isDriverModeOn(): Boolean {
		return _user!!.driverMode
	}

	override suspend fun makeUserDriver() {
		_user!!.isDriver = true
		mAuth.makeUserDriver(_user!!.uid)
	}

	override suspend fun setDriverMode(driverMode: Boolean) {
		_user!!.driverMode = driverMode
		mAuth.setDriverMode(_user!!.uid, driverMode)
	}

	override suspend fun updateUser(
		newName: String?,
		newLastName: String?,
		newPhone: String?,
		newPhotoFile: UploadedFile?
	) {
		mAuth.updateUser(_user!!.uid, newName, newLastName, newPhone, newPhotoFile)
	}
}
