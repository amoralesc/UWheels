package com.abmodel.uwheels.data.model

enum class RideStatus(val status: String) {
	OPEN("OPEN"),
	FULL("FULL"),
	ACTIVE("ACTIVE"),
	//ONROUTE("ONROUTE"),
	//ARRIVED("ARRIVED"),
	CANCELLED("CANCELLED"),
	COMPLETED("COMPLETED");

	override fun toString(): String {
		return status
	}

	companion object {
		fun fromString(state: String): RideStatus {
			return values().first { it.status == state }
		}
	}
}