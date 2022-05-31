package com.abmodel.uwheels.data.model

import android.net.Uri

data class UploadedFile(
	val name: String,
	val mimeType: String,
	var uri: Uri
)
