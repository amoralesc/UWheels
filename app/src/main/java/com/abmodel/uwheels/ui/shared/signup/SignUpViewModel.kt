package com.abmodel.uwheels.ui.shared.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abmodel.uwheels.R
import com.abmodel.uwheels.data.FirebaseAuthRepository
import com.abmodel.uwheels.ui.shared.data.FormResult
import com.abmodel.uwheels.util.isEmailValid
import com.abmodel.uwheels.util.isPasswordValid
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SignUpViewModel : ViewModel() {

	private val authRepository = FirebaseAuthRepository.getInstance()

	private val _signUpResult = MutableLiveData<FormResult>()
	val signUpResult: LiveData<FormResult> = _signUpResult

	fun signUp(
		name: String, lastName: String, phone: String,
		email: String, password: String, passwordAgain: String
	) {

		viewModelScope.launch(Dispatchers.IO) {
			if (checkSignUpForm(
					name, lastName, phone, email, password, passwordAgain
				)
			) {

				if (authRepository.signUp(
						email, password, name, lastName, phone, null
					)
				) {
					_signUpResult.postValue(FormResult(success = true))
				} else {
					_signUpResult.postValue(FormResult(error = R.string.signup_failed))
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
			_signUpResult.postValue(FormResult(error = R.string.invalid_password))
			false
		} else if (password != passwordAgain) {
			_signUpResult.postValue(FormResult(error = R.string.password_mismatch))
			false
		} else {
			true
		}
	}
}
