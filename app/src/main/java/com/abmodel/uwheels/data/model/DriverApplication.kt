package com.abmodel.uwheels.data.model

import android.net.Uri

data class UploadedFile(
	val name: String,
	val mimeType: String,
	var uri: Uri
)

data class VehicleDetail(
	var make: String? = null,
	var model: String? = null,
	var year: Int? = null,
	var plate: String? = null,
	var capacity: Int? = null,
)

data class DriverApplication(
	var licenseFiles: MutableList<UploadedFile>,
	var ownershipFiles: MutableList<UploadedFile>,
	var insuranceFiles: MutableList<UploadedFile>,
	var vehiclePics: MutableList<UploadedFile>,
	val vehicleDetail: VehicleDetail
)
