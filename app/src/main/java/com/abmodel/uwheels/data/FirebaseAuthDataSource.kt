package com.abmodel.uwheels.data

import com.abmodel.uwheels.data.model.LoggedInUser
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import java.io.IOException

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class FirebaseAuthDataSource {

	private val mAuth = FirebaseAuth.getInstance()



	fun login(email: String, password: String): Result<AuthResult> {
		try {
			val user =
				mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
					if (!task.isSuccessful) {
						throw IOException(task.exception)
					}
				}

			return Result.Success(user.result)
		}catch (e: Throwable) {
			return Result.Error(IOException("Error logging in", e))
		}
	}

	fun isLoggedIn(): Boolean {
		val currentUser = mAuth.currentUser
		if(currentUser != null) {
			return true
		}
		return false
	}

	fun sigIn(email: String, password: String): Result<AuthResult> {
		try {
			val user = mAuth.createUserWithEmailAndPassword(email, password)
				.addOnCompleteListener { task ->
					if (!task.isSuccessful) {
						throw IOException(task.exception)
					}
				}
			return Result.Success(user.result)
		}catch (e: Throwable) {
			return Result.Error(IOException("Error logging in", e))
		}
	}


	fun logout() {
		mAuth.signOut()
	}
}