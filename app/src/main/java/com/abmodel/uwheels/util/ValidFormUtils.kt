package com.abmodel.uwheels.util

import android.util.Patterns
import java.util.regex.Pattern

/**
 * Checks if the email is valid. It uses [Patterns.EMAIL_ADDRESS] Regex pattern.
 *
 * @param email the email to be checked
 * @return true if the email is valid, false otherwise
 */
fun isEmailValid(email: String): Boolean {
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
fun isPasswordValid(password: String): Boolean {
	val pattern = Pattern.compile(
		"^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{8,}\$"
	)
	return pattern.matcher(password).matches()
}
