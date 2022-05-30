package com.abmodel.uwheels.ui.shared.signup

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.abmodel.uwheels.R
import com.abmodel.uwheels.ui.shared.data.FormResult
import com.abmodel.uwheels.util.isEmailValid
import com.abmodel.uwheels.util.isPasswordValid
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

class SignUpViewModel: ViewModel() {

	private val _signUpResult = MutableLiveData<FormResult>()
	val signUpResult: LiveData<FormResult> = _signUpResult

	private val mAuth = FirebaseAuth.getInstance()

	fun signUp(
		name: String, lastName: String, phone: String,
		email: String, password: String, passwordAgain: String
	) {

		if (checkSignUpForm(
				name, lastName, phone,
				email, password, passwordAgain
			)
		) {

			mAuth.createUserWithEmailAndPassword(email, password)
				.addOnCompleteListener { task ->
					if (task.isSuccessful) {
						// Add additional information
						val user = mAuth.currentUser
						user?.updateProfile(
							UserProfileChangeRequest.Builder()
								.setDisplayName("$name $lastName")
								.build()
						)

						Log.d(SignUpFragment.TAG, "Sign up successful")
						_signUpResult.value = FormResult(
							success = true, error = null
						)
					} else {
						Log.d(SignUpFragment.TAG, "Sign up failed")
						Log.d(SignUpFragment.TAG, task.exception?.message.toString())
						_signUpResult.value = FormResult(
							error = R.string.signup_failed
						)
					}
				}
		}
	}

	private fun checkSignUpForm(
		name: String, lastName: String, phone: String,
		email: String, password: String, passwordAgain: String
	): Boolean {

		return if (name.isEmpty() || lastName.isEmpty() || phone.isEmpty() ||
			email.isEmpty() || password.isEmpty() || passwordAgain.isEmpty()
		) {
			_signUpResult.value = FormResult(error = R.string.signup_failed_empty_fields)
			false
		} else if (!isEmailValid(email)) {
			_signUpResult.value = FormResult(error = R.string.invalid_email)
			false
		} else if (!isPasswordValid(password)) {
			_signUpResult.value = FormResult(error = R.string.invalid_password)
			false
		} else if (password != passwordAgain) {
			_signUpResult.value = FormResult(error = R.string.password_mismatch)
			false
		} else {
			true
		}
	}
}
