package com.abmodel.uwheels.data

import android.net.Uri
import android.util.Log
import com.abmodel.uwheels.data.model.LoggedInUser
import com.google.firebase.auth.AuthResult
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

/**
 * Login repository interface that provides the data access layer for the login
 */
interface AuthRepository {
	suspend fun login(email: String, password: String): Boolean
	suspend fun signUp(
		email: String, password: String, name: String,
		lastName: String, phone: String, photoUri: Uri?
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
	private val mDatabase: FirebaseFirestore
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
					FirebaseAuthDataSource(),
					Firebase.firestore
				).also { instance = it }
			}
		}

		const val TAG = "FirebaseAuthRepository"
	}

	// in-memory cache of the loggedInUser object
	private var _user: LoggedInUser? = null

	init {
		_user = null
		if (isLoggedIn()) {
			updateLoggedInUser()
		}
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
		lastName: String, phone: String, photoUri: Uri?
	): Boolean {
		val result = mAuth.signUp(email, password, name, photoUri)
		Log.d(TAG, "Sign up: $result")

		if (result is Result.Success<AuthResult> && result.data.user != null) {
			createDatabaseUser(
				result.data.user!!.uid,
				name,
				lastName,
				phone
			)
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

	private fun createDatabaseUser(
		uid: String, name: String, lastName: String, phone: String
	) {
		val user = hashMapOf(
			"name" to name,
			"lastName" to lastName,
			"phone" to phone
		)
		mDatabase.collection(FirebasePaths.USERS).document(uid).set(user)
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
