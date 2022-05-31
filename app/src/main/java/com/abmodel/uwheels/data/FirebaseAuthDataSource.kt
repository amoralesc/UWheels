package com.abmodel.uwheels.data

import android.net.Uri
import android.util.Log
import com.abmodel.uwheels.data.model.LoggedInUser
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class FirebaseAuthDataSource {

	companion object {
		const val TAG = "FirebaseAuthDataSource"
	}

	private val mAuth = Firebase.auth
	private val mDatabase = Firebase.firestore

	suspend fun login(email: String, password: String): Result<AuthResult> {
		try {
			val task =
				mAuth.signInWithEmailAndPassword(email, password)
					.addOnCompleteListener {
						if (it.isSuccessful) {
							Log.d(TAG, "Login successful")
						} else {
							Log.d(TAG, "Login failed")
						}
					}
			task.await()

			return Result.Success(task.result)
		} catch (e: Exception) {
			return Result.Error(e)
		}
	}

	suspend fun signUp(
		email: String, password: String, name: String,
		lastName: String, phone: String, photoUri: Uri?
	): Result<AuthResult> {
		try {
			val task =
				mAuth.createUserWithEmailAndPassword(email, password)
					.addOnSuccessListener { result ->
						Log.d(TAG, "Sign up successful")

						val user = result.user!!
						user.sendEmailVerification()
						user.updateProfile(
							UserProfileChangeRequest.Builder()
								.setDisplayName(name)
								.setPhotoUri(photoUri)
								.build()
						)

						createDatabaseUser(user.uid, name, lastName, phone)
					}
					.addOnFailureListener {
						Log.e(TAG, "Sign up failed: ${it.message}")
						Log.e(TAG, it.stackTraceToString())
					}
			task.await()

			return Result.Success(task.result)
		} catch (e: Throwable) {
			return Result.Error(Exception("Error signing up", e))
		}
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

	fun logout() {
		mAuth.signOut()
	}

	fun isLoggedIn(): Boolean {
		if (mAuth.currentUser != null) {
			return true
		}
		return false
	}

	fun getCurrentUser(): LoggedInUser {
		val firebaseUser = mAuth.currentUser
		val databaseUser = mDatabase
			.collection(FirebasePaths.USERS)
			.document(firebaseUser?.uid ?: "")
			.get()
			.result
			.data

		return LoggedInUser(
			firebaseUser?.uid ?: "",
			firebaseUser?.email,
			firebaseUser?.displayName,
			databaseUser?.get("name") as String?,
			databaseUser?.get("lastName") as String?,
			databaseUser?.get("phone") as String?,
			firebaseUser?.photoUrl,
		)
	}
}
