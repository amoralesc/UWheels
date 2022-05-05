package com.abmodel.uwheels.ui.shared.login

/**
 * Data validation state of the login form.
 */
data class LoginFormState(
	val emailError: Int? = null,
	val passwordError: Int? = null,
	val isDataValid: Boolean = false
)

/**
 * Authentication result : success (user details) or error message.
 */
data class LoginResult(
	val success: LoggedInUserView? = null,
	val error: Int? = null
)

/**
 * User details post authentication that is exposed to the UI
 */
data class LoggedInUserView(
	val displayName: String
	//... other data fields that may be accessible to the UI
)