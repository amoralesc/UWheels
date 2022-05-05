package com.abmodel.uwheels.ui.driver.apply

import android.net.Uri

data class DriverApplicationView(
	val title: String,
	val description: String,
	val supportedFiles: String,
	val swapToVehicleDetail: Boolean
)

data class UploadedFile(
	val name: String,
	val mime: String,
	val uri: Uri
)

data class VehicleDetail(
	val make: String,
	val model: String,
	val year: Int,
	val plate: String,
	val capacity: Int
)

data class DriverApplicationForm(
	val licenseFiles: MutableList<UploadedFile>,
	val ownershipFiles: MutableList<UploadedFile>,
	val insuranceFiles: MutableList<UploadedFile>,
	val vehiclePics: MutableList<UploadedFile>,
	val vehicleDetail: VehicleDetail
)
