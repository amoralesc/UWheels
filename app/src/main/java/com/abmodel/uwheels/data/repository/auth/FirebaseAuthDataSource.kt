package com.abmodel.uwheels.data.repository.auth

import android.net.Uri
import android.util.Log
import com.abmodel.uwheels.data.FirestorePaths
import com.abmodel.uwheels.data.Result
import com.abmodel.uwheels.data.StoragePaths
import com.abmodel.uwheels.data.model.LoggedInUser
import com.abmodel.uwheels.data.model.UploadedFile
import com.abmodel.uwheels.data.model.firebase.UserDocument
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class FirebaseAuthDataSource {

	companion object {
		@Volatile
		private var instance: FirebaseAuthDataSource? = null

		fun getInstance() = instance ?: synchronized(this) {
			instance ?: FirebaseAuthDataSource().also { instance = it }
		}

		const val TAG = "FirebaseAuthDataSource"
	}

	private val mAuth = Firebase.auth
	private val mDatabase = Firebase.firestore
	private val mStorage = Firebase.storage

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
			"phone" to phone,
			"isDriver" to false,
			"driverMode" to false,
		)
		mDatabase.collection(FirestorePaths.USERS).document(uid).set(user)
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

	suspend fun getCurrentUser(): LoggedInUser {
		val firebaseUser = mAuth.currentUser
		val databaseUser = mDatabase
			.collection(FirestorePaths.USERS)
			.document(firebaseUser?.uid ?: "")
			.get()
			.await()
			.data

		return LoggedInUser(
			firebaseUser?.uid ?: "",
			firebaseUser?.email,
			firebaseUser?.displayName,
			databaseUser?.get("name") as String?,
			databaseUser?.get("lastName") as String?,
			databaseUser?.get("phone") as String?,
			firebaseUser?.photoUrl,
			databaseUser?.get("isDriver") as Boolean,
			databaseUser["driverMode"] as Boolean
		)
	}

	suspend fun makeUserDriver(userId: String) {
		mDatabase
			.collection(FirestorePaths.USERS)
			.document(userId)
			.update("isDriver", true)
			.await()
	}

	suspend fun setDriverMode(userId: String, mode: Boolean) {
		mDatabase
			.collection(FirestorePaths.USERS)
			.document(userId)
			.update("driverMode", mode)
			.await()
	}

	suspend fun updateUser(
		userId: String, name: String?, lastName: String?,
		phone: String?, uploadedPhotoFile: UploadedFile?
	) {
		// Get the user from the database
		val databaseUser = mDatabase
			.collection(FirestorePaths.USERS)
			.document(userId)
			.get()
			.await()
			.toObject(UserDocument::class.java)!!

		// Update the user
		databaseUser.name = name ?: databaseUser.name
		databaseUser.lastName = lastName ?: databaseUser.lastName
		databaseUser.phone = phone ?: databaseUser.phone

		// Update the user in the database
		mDatabase
			.collection(FirestorePaths.USERS)
			.document(userId)
			.set(databaseUser)
			.await()

		// If the photoUri is not null, upload the photo to the storage
		if (uploadedPhotoFile != null) {
			val photoUri = mStorage.reference
				.child(StoragePaths.USERS)
				.child(userId)
				.child(StoragePaths.IMG)
				.child(uploadedPhotoFile.name)
				.putFile(uploadedPhotoFile.uri)
				.await()
				.storage
				.downloadUrl
				.await()

			uploadedPhotoFile.uri = photoUri
		}

		// Update the FirebaseUser
		val firebaseUser = mAuth.currentUser!!
		firebaseUser.updateProfile(
			UserProfileChangeRequest.Builder()
				.setDisplayName(name ?: firebaseUser.displayName)
				.setPhotoUri(uploadedPhotoFile?.uri ?: firebaseUser.photoUrl)
				.build()
		)
			.await()
	}
}
