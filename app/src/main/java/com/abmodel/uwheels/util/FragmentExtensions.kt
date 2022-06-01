package com.abmodel.uwheels.util

import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment

fun Fragment.hideKeyboard() = ViewCompat.getWindowInsetsController(requireView())
	?.hide(WindowInsetsCompat.Type.ime())

fun Fragment.showMessage(
	@StringRes message: Int,
	length: Int = Toast.LENGTH_SHORT
) {
	Toast.makeText(requireContext(), message, length).show()
}
