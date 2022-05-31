package com.abmodel.uwheels.data.model.firebase

data class DriverApplicationDocument(
	var licenseFiles: MutableList<UploadedFileDocument>? = null,
	var ownershipFiles: MutableList<UploadedFileDocument>? = null,
	var insuranceFiles: MutableList<UploadedFileDocument>? = null,
	var vehiclePics: MutableList<UploadedFileDocument>? = null,
	var vehicleDetail: VehicleDetailDocument? = null,
)
