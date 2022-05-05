package com.abmodel.uwheels.ui.shared.search

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import kotlinx.parcelize.Parcelize

@Parcelize
data class CustomAddress(
	val placeId: String,
	val mainText: String,
	val secondaryText: String,
	val latLng: LatLng?
) : Parcelable
