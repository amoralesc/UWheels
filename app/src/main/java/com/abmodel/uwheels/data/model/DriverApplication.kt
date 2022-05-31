package com.abmodel.uwheels.data.model

data class DriverApplication(
	var licenseFiles: MutableList<UploadedFile>,
	var ownershipFiles: MutableList<UploadedFile>,
	var insuranceFiles: MutableList<UploadedFile>,
	var vehiclePics: MutableList<UploadedFile>,
	var vehicleDetail: Vehicle?
)
