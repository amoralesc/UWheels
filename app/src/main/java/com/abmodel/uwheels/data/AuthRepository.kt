package com.abmodel.uwheels.data

import android.content.Context
import com.abmodel.uwheels.data.model.LoggedInUser
import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 * Login repository interface that provides the data access layer for the login
 */
interface AuthRepository {
	fun login(email: String, password: String): Result<LoggedInUser>
	fun logout()
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
	private var _user: LoggedInUser? = null
	private val user: LoggedInUser?
		get() = _user

	init {
		// If user credentials will be cached in local storage, it is recommended it be encrypted
		// @see https://developer.android.com/training/articles/keystore
		_user = null
	}

	override fun login(email: String, password: String): Result<LoggedInUser> {
		// handle login
		val result = dataSource.login(email, password)
		if (result is Result.Success) {
			setLoggedInUser(result.data)
		}
		return result
	}

	override fun logout() {
		_user = null
		dataSource.logout()
	}

	override fun isLoggedIn(): Boolean {
		return _user != null
	}

	override fun getLoggedInUser(): LoggedInUser? {
		return user
	}

	private fun setLoggedInUser(loggedInUser: LoggedInUser) {
		this._user = loggedInUser
		// If user credentials will be cached in local storage, it is recommended it be encrypted
		// @see https://developer.android.com/training/articles/keystore
	}
}
