package com.abmodel.uwheels.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CustomLatLng(
	var latitude: Double = 0.0,
	var longitude: Double = 0.0
) : Parcelable

@Parcelize
data class CustomAddress(
	val placeId: String? = null,
	val mainText: String = "",
	val secondaryText: String = "",
	val latLng: CustomLatLng? = null
) : Parcelable
