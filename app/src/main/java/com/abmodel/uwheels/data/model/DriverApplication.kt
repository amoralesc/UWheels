package com.abmodel.uwheels.data.model

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
	var vehicleDetail: VehicleDetail
)
