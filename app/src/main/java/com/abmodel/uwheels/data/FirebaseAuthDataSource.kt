package com.abmodel.uwheels.data

import android.util.Log
import androidx.annotation.MainThread
import com.abmodel.uwheels.ui.shared.login.LoginFragment
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import java.io.IOException

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class FirebaseAuthDataSource {

	private val mAuth = FirebaseAuth.getInstance()

	fun login(email: String, password: String): Result<AuthResult> {
		try {
			Log.d(LoginFragment.TAG, "Trying to login with email: $email and password: $password")

			val task =
				mAuth.signInWithEmailAndPassword(email, password)
					.addOnCompleteListener {
						if (it.isSuccessful) {
							Log.d(LoginFragment.TAG, "Login successful")
						} else {
							Log.d(LoginFragment.TAG, "Login failed")
						}
					}

			return Result.Success(task.result)

		} catch (e: Exception) {
			return Result.Error(e)
		}
	}

	fun isLoggedIn(): Boolean {
		val currentUser = mAuth.currentUser
		if (currentUser != null) {
			return true
		}
		return false
	}

	fun signUp(email: String, password: String): Result<AuthResult> {
		return try {
			val user =
				mAuth.createUserWithEmailAndPassword(email, password)
					.addOnCompleteListener { task ->
						if (!task.isSuccessful) {
							throw Exception(task.exception)
						}
					}
					.addOnFailureListener {
						throw it
					}
			Result.Success(user.result)

		} catch (e: Throwable) {
			Result.Error(Exception("Error signing up", e))
		}
	}

	fun logout() {
		mAuth.signOut()
	}
}
