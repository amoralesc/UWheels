package com.abmodel.uwheels.ui.shared.login

import android.app.Application
import android.util.Patterns
import androidx.lifecycle.*
import com.abmodel.uwheels.R
import com.abmodel.uwheels.data.DefaultAuthRepository
import com.abmodel.uwheels.data.AuthRepository
import com.abmodel.uwheels.data.Result
import java.util.regex.Pattern

class LoginViewModel @JvmOverloads constructor(
	application: Application,
	private val authRepository: AuthRepository =
		DefaultAuthRepository.getInstance(application)
) : AndroidViewModel(application) {

	private val _loginResult = MutableLiveData<LoginResult>()
	val loginResult: LiveData<LoginResult> = _loginResult

	fun login(email: String, password: String) {

		if (checkLoginForm(email, password)) {
			// Can be launched in a separate asynchronous job
			val result = authRepository.login(email, password)

			if (result is Result.Success) {
				_loginResult.value =
					LoginResult(success = LoggedInUserView(displayName = result.data.displayName))
			} else {
				_loginResult.value = LoginResult(error = R.string.login_failed)
			}
		}
	}

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

	/**
	 * Checks if the email is valid. It uses ReGex pattern matching.
	 *
	 * @param email the email to be checked
	 * @return true if the email is valid, false otherwise
	 */
	private fun isEmailValid(email: String): Boolean {
		return Patterns.EMAIL_ADDRESS.matcher(email).matches()
	}

	/**
	 * Checks if the password is valid. A password is valid if:
	 * * it is at least 8 characters long.
	 * * it contains at least one digit.
	 * * it contains at least one upper case and one lower case letter.
	 * * it may contain special characters.
	 *
	 * @param password the password to be checked
	 * @return true if the password is valid, false otherwise
	 */
	private fun isPasswordValid(password: String): Boolean {
		val pattern = Pattern.compile(
			"^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{8,}\$"
		)
		return pattern.matcher(password).matches()
	}
}
