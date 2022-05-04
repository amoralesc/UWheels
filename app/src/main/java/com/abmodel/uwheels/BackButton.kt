package com.abmodel.uwheels

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.navigation.findNavController
import com.google.android.material.button.MaterialButton

class BackButton @JvmOverloads constructor(
	ctx: Context,
	attributeSet: AttributeSet? = null,
	defStyleAttr: Int = 0
) : MaterialButton(ctx, attributeSet, defStyleAttr) {

	init {
		this.setOnClickListener {
			findNavController().navigateUp()
		}
	}
}
