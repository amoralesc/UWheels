package com.abmodel.uwheels.data.model

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import kotlinx.parcelize.Parcelize

@Parcelize
data class CustomAddress(
	val placeId: String? = null,
	val mainText: String = "",
	val secondaryText: String = "",
	val latLng: LatLng? = null
) : Parcelable
