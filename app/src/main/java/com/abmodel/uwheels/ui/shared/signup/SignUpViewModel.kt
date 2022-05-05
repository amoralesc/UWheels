package com.abmodel.uwheels.ui.shared.signup

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.abmodel.uwheels.R
import com.abmodel.uwheels.data.AuthRepository
import com.abmodel.uwheels.data.DefaultAuthRepository
import com.abmodel.uwheels.data.Result
import com.abmodel.uwheels.ui.shared.login.LoggedInUserView
import com.abmodel.uwheels.ui.shared.login.LoginResult
import com.abmodel.uwheels.util.isEmailValid
import com.abmodel.uwheels.util.isPasswordValid

class LoginViewModel @JvmOverloads constructor(
	application: Application,
	private val authRepository: AuthRepository =
		DefaultAuthRepository.getInstance(application)
) : AndroidViewModel(application) {

	private val _loginResult = MutableLiveData<LoginResult>()
	val loginResult: LiveData<LoginResult> = _loginResult



	/**
	 * Checks the login form data (email, password).
	 * Sets the [LoginResult] to an error message if data is invalid.
	 *
	 * @param email The email address of the user.
	 * @param password The password of the user.
	 * @return true if the data is valid, false otherwise.
	 */
	private fun checkLoginForm(email: String, password: String): Boolean {

		return if (!isEmailValid(email)) {
			_loginResult.value = LoginResult(error = R.string.invalid_email)
			false
		} else if (!isPasswordValid(password)) {
			_loginResult.value = LoginResult(error = R.string.invalid_password)
			false
		} else {
			true
		}
	}
}
