package com.abmodel.uwheels.data

import android.content.Context
import android.util.Log
import androidx.annotation.MainThread
import com.abmodel.uwheels.data.model.LoggedInUser
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 * Login repository interface that provides the data access layer for the login
 */
interface AuthRepository {
	fun login(email: String, password: String): Result<AuthResult>
	fun logout()
	fun signUp(email: String, password: String): Result<AuthResult>
	fun isLoggedIn(): Boolean
	fun getLoggedInUser(): LoggedInUser?
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
	private var _user: FirebaseUser? = null
	private val user: FirebaseUser?
		get() = _user

	init {
		// If user credentials will be cached in local storage, it is recommended it be encrypted
		// @see https://developer.android.com/training/articles/keystore
		_user = null

		// FABIO CHECKS HERE IF USER IS LOGGED IN
		isLoggedIn()
	}

	override fun login(email: String, password: String): Result<AuthResult> {
		val result = dataSource.login(email, password)

		Log.d("DefaultAuthRepository", "login: $result")

		if (result is Result.Success<AuthResult>) {
			Log.d("DefaultAuthRepository", "Logged in user: ${result.data.user!!}")
			setLoggedInUser(result.data.user!!)
		}

		return result
	}

	override fun logout() {
		dataSource.logout()
	}

	override fun signUp(email: String, password: String): Result<AuthResult> {
		return dataSource.signUp(email, password)
	}

	@MainThread
	override fun isLoggedIn(): Boolean {
		return dataSource.isLoggedIn()
	}

	override fun getLoggedInUser(): LoggedInUser? {
		return if (isLoggedIn()) {
			LoggedInUser(
				userId = user!!.uid,
				displayName = user!!.displayName
			)
		} else {
			null
		}
	}

	private fun setLoggedInUser(loggedInUser: FirebaseUser) {
		this._user = loggedInUser
		// If user credentials will be cached in local storage, it is recommended it be encrypted
		// @see https://developer.android.com/training/articles/keystore
	}
}
