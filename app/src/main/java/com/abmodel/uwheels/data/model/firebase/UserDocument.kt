package com.abmodel.uwheels.data.model.firebase

data class UserDocument(
	var driverApplication: DriverApplicationDocument? = null,
	var driverMode: Boolean? = null,
	@field:JvmField var isDriver: Boolean? = null,
	var lastName: String? = null,
	var name: String? = null,
	var phone: String? = null,
)
