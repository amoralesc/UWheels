package com.abmodel.uwheels.ui.shared.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abmodel.uwheels.R
import com.abmodel.uwheels.data.repository.auth.FirebaseAuthRepository
import com.abmodel.uwheels.ui.shared.data.FormResult
import com.abmodel.uwheels.util.isEmailValid
import com.abmodel.uwheels.util.isPasswordValid
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

	private val authRepository = FirebaseAuthRepository.getInstance()

	private val _loginResult = MutableLiveData<FormResult>()
	val loginResult: LiveData<FormResult> = _loginResult

	init {
		if (authRepository.isLoggedIn()) {
			// Launch a coroutine to fetch the user
			// [Dispatchers.Main] is used to ensure
			// the user is fetched blocking the ui thread
			viewModelScope.launch(Dispatchers.Main) {
				authRepository.fetchLoggedInUser()
				_loginResult.postValue(FormResult(true))
			}
		} else {
			_loginResult.value = FormResult()
		}
	}

	/**
	 * Attempts to sign in the user through Firebase Authentication.
	 * If the user is successfully signed in, the [FormResult.success] will be true.
	 * If there is an error, the [FormResult.message] will be set.
	 *
	 * @param email the user's email
	 * @param password the user's password
	 */
	fun login(email: String, password: String) {

		viewModelScope.launch(Dispatchers.IO) {
			if (checkLoginForm(email, password)) {

				if (authRepository.login(email, password)) {
					_loginResult.postValue(FormResult(success = true))
				} else {
					_loginResult.postValue(FormResult(message = R.string.login_failed))
				}
			}
		}
	}

	/**
	 * Checks the login form data (email, password).
	 * Sets the [FormResult] to an error message if data is invalid.
	 *
	 * @param email The email address of the user.
	 * @param password The password of the user.
	 * @return true if the data is valid, false otherwise.
	 */
	private fun checkLoginForm(email: String, password: String): Boolean {

		return if (!isEmailValid(email)) {
			_loginResult.value = FormResult(message = R.string.invalid_email)
			false
		} else if (!isPasswordValid(password)) {
			_loginResult.value = FormResult(message = R.string.invalid_password)
			false
		} else {
			true
		}
	}
}
