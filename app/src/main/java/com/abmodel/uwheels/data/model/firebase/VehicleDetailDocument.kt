package com.abmodel.uwheels.data.model.firebase

data class VehicleDetailDocument(
	var make: String? = null,
	var model: String? = null,
	var year: Int? = null,
	var plate: String? = null,
	var capacity: Int? = null,
)
