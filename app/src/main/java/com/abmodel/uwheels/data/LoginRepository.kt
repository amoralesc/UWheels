package com.abmodel.uwheels.data

import com.abmodel.uwheels.data.model.LoggedInUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */
class LoginRepository(val dataSource: LoginDataSource) {

	val ref = FirebaseAuth.getInstance()

	//Login normal(correo y contraseña)
	// in-memory cache of the loggedInUser object
	var user: LoggedInUser? = null
		private set

	val isLoggedIn: Boolean
		get() = user != null

	init {
		// If user credentials will be cached in local storage, it is recommended it be encrypted
		// @see https://developer.android.com/training/articles/keystore
		user = null
	}

	fun logout() {
		user = null
		dataSource.logout()
	}

	fun login(username: String, password: String): Result<LoggedInUser> {
		// handle login
		val result = dataSource.login(username, password)

		//val transactionResultValue = ref.createUserWithEmailAndPassword(username, password)
		if (result is Result.Success) {
			setLoggedInUser(result.data)
		}

		return result
	}

	private fun setLoggedInUser(loggedInUser: LoggedInUser) {
		this.user = loggedInUser
		// If user credentials will be cached in local storage, it is recommended it be encrypted
		// @see https://developer.android.com/training/articles/keystore
	}
}