package com.abmodel.uwheels

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.abmodel.uwheels.R

/**
 * This custom view is used to display an alert bubble with a number inside.
 * The parent is a [FrameLayout] with these children:
 * - An [ImageView] made of a circle icon. It acts as the background of the alert bubble.
 * The icon's tint can be set programmatically. Default tint is red.
 * - A centered [TextView]. It acts as the number inside the alert bubble.
 * The text and color can be set programmatically. Default color is white.
 */
class AlertBubbleView @JvmOverloads constructor(
	ctx: Context,
	attributeSet: AttributeSet? = null,
	defStyleAttr: Int = 0
) : FrameLayout(ctx, attributeSet, defStyleAttr) {

	private var background: ImageView
	private var number: TextView

	init {
		val inflater = LayoutInflater.from(ctx)
		inflater.inflate(R.layout.alert_bubble_view, this)

		background = findViewById(R.id.alert_bubble_background)
		number = findViewById(R.id.alert_bubble_number)
	}

	/**
	 * Sets the background color of the alert bubble.
	 * @param color The color resource to set.
	 */
	fun setBackground(@ColorRes color: Int) {
		background.setColorFilter(
			ContextCompat.getColor(context, color)
		)
	}

	/**
	 * Sets the number inside the alert bubble.
	 * @param number The number to set.
	 */
	fun setNumber(number: Int) {
		this.number.text = number.toString()
	}

	/**
	 * Sets the color of the number inside the alert bubble.
	 * @param color The color resource to set.
	 */
	fun setNumberColor(@ColorRes color: Int) {
		this.number.setTextColor(
			ContextCompat.getColor(context, color)
		)
	}
}
