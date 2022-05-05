package com.abmodel.uwheels.ui.shared.login

import android.app.Application
import android.util.Patterns
import androidx.lifecycle.*
import com.abmodel.uwheels.R
import com.abmodel.uwheels.data.DefaultAuthRepository
import com.abmodel.uwheels.data.AuthRepository
import com.abmodel.uwheels.data.Result

class LoginViewModel @JvmOverloads constructor(
	application: Application,
	private val authRepository: AuthRepository =
		DefaultAuthRepository.getInstance(application)
) : AndroidViewModel(application) {

	private val _loginForm = MutableLiveData<LoginFormState>()
	val loginFormState: LiveData<LoginFormState> = _loginForm

	private val _loginResult = MutableLiveData<LoginResult>()
	val loginResult: LiveData<LoginResult> = _loginResult

	fun login(username: String, password: String) {
		// can be launched in a separate asynchronous job
		val result = authRepository.login(username, password)

		if (result is Result.Success) {
			_loginResult.value =
				LoginResult(success = LoggedInUserView(displayName = result.data.displayName))
		} else {
			_loginResult.value = LoginResult(error = R.string.login_failed)
		}
	}

	fun loginDataChanged(username: String, password: String) {
		if (!isUserNameValid(username)) {
			_loginForm.value = LoginFormState(usernameError = R.string.invalid_username)
		} else if (!isPasswordValid(password)) {
			_loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
		} else {
			_loginForm.value = LoginFormState(isDataValid = true)
		}
	}

	// A placeholder username validation check
	private fun isUserNameValid(username: String): Boolean {
		return if (username.contains("@")) {
			Patterns.EMAIL_ADDRESS.matcher(username).matches()
		} else {
			username.isNotBlank()
		}
	}

	// A placeholder password validation check
	private fun isPasswordValid(password: String): Boolean {
		return password.length > 5
	}
}

/*
/**
 * ViewModel provider factory to instantiate LoginViewModel.
 * Required given LoginViewModel has a non-empty constructor
 */
class LoginViewModelFactory : ViewModelProvider.Factory {

	@Suppress("UNCHECKED_CAST")
	override fun <T : ViewModel> create(modelClass: Class<T>): T {
		if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
			return LoginViewModel(
				loginRepository = LoginRepository(
					dataSource = LoginDataSource()
				)
			) as T
		}
		throw IllegalArgumentException("Unknown ViewModel class")
	}
}
*/