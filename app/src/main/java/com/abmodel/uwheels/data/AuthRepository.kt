package com.abmodel.uwheels.data

import android.content.Context
import androidx.annotation.MainThread
import com.abmodel.uwheels.data.model.LoggedInUser
import com.google.firebase.auth.AuthResult
import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 * Login repository interface that provides the data access layer for the login
 */
interface AuthRepository {
	fun login(email: String, password: String): Result<AuthResult>
	fun logout()
	fun sigIn(email: String, password: String): Result<AuthResult>
	fun isLoggedIn(): Boolean
	fun getLoggedInUser(): LoggedInUser?
	fun validateEmailAndPassword(email: String, password: String): Boolean
	abstract fun isEmailValid(email: String): Boolean
}

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */
class DefaultAuthRepository internal constructor(
	private val dataSource: FirebaseAuthDataSource,
	private val executor: Executor
) : AuthRepository {

	/**
	 * Single instance of the login repository.
	 * The instance is created when the application starts and stays alive
	 * throughout the application lifecycle.
	 */
	companion object {
		private var instance: DefaultAuthRepository? = null

		fun getInstance(context: Context): DefaultAuthRepository {
			return instance ?: synchronized(this) {
				instance ?: DefaultAuthRepository(
					FirebaseAuthDataSource(),
					Executors.newFixedThreadPool(2)
				).also {
					instance = it
				}
			}
		}
	}

	// in-memory cache of the loggedInUser object
	private var _user: LoggedInUser? = null
	private val user: LoggedInUser?
		get() = _user

	init {
		// If user credentials will be cached in local storage, it is recommended it be encrypted
		// @see https://developer.android.com/training/articles/keystore
		_user = null

		// FABIO CHECKS HERE IF USER IS LOGGED IN
		isLoggedIn()
	}

	override fun login(email: String, password: String): Result<AuthResult> {
		// handle login
		return dataSource.login(email, password)
	}

	override fun logout() {
		dataSource.logout()
	}

	 override fun sigIn(email: String, password: String): Result<AuthResult> {
	 	if(validateEmailAndPassword(email, password)){
	 		return dataSource.sigIn(email, password)
	 	}
	 	return Result.Error(Exception("Invalid email or password"))
	 }

	@MainThread
	override fun isLoggedIn(): Boolean {
		return dataSource.isLoggedIn();
	}

	override fun getLoggedInUser(): LoggedInUser? {
		return user
	}

	override fun validateEmailAndPassword(email: String, password: String): Boolean {
		if(email.isEmpty() && password.isEmpty() && isEmailValid(email)){
			return false
		}
		return true
	}

	override fun isEmailValid(email: String): Boolean {
		if (!email.contains("@") ||
			!email.contains(".") ||
			email.length < 5)
			return false;
		return true;
	}

	private fun setLoggedInUser(loggedInUser: LoggedInUser) {
		this._user = loggedInUser
		// If user credentials will be cached in local storage, it is recommended it be encrypted
		// @see https://developer.android.com/training/articles/keystore
	}
}
