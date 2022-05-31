package com.abmodel.uwheels.data

import android.util.Log
import androidx.annotation.MainThread
import com.abmodel.uwheels.ui.shared.login.LoginFragment
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.tasks.await
import java.io.IOException

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class FirebaseAuthDataSource {

	companion object {
		const val TAG = "FirebaseAuthDataSource"
	}

	private val mAuth = FirebaseAuth.getInstance()

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

	suspend fun signUp(email: String, password: String, displayName: String): Result<AuthResult> {
		try {
			val task =
				mAuth.createUserWithEmailAndPassword(email, password)
					.addOnSuccessListener { result ->
						Log.d(TAG, "Sign up successful")

						val user = result.user
						user?.sendEmailVerification()
						user?.updateProfile(
							UserProfileChangeRequest.Builder()
								.setDisplayName(displayName)
								.build()
						)
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

	fun logout() {
		mAuth.signOut()
	}

	fun isLoggedIn(): Boolean {
		if (mAuth.currentUser != null) {
			return true
		}
		return false
	}

	fun getCurrentUser(): FirebaseUser? {
		return mAuth.currentUser
	}
}
