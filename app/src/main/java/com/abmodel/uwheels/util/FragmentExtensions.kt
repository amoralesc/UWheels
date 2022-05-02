package com.abmodel.uwheels.util

import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment

fun Fragment.hideKeyboard() = ViewCompat.getWindowInsetsController(requireView())
	?.hide(WindowInsetsCompat.Type.ime())
