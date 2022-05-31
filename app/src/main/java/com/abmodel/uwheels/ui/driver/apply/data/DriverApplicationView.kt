package com.abmodel.uwheels.ui.driver.apply.data

@Suppress("ArrayInDataClass")
data class DriverApplicationView(
	val title: String,
	val description: String,
	val supportedFiles: String,
	val mimeTypes: Array<String>,
	val swapToVehicleDetail: Boolean
)