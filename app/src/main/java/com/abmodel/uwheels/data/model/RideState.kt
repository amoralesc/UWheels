package com.abmodel.uwheels.data.model

enum class RideState(val state: String) {
	CREATED("CREATED"),
	ACTIVE("ACTIVE"),
	ONROUTE("ONROUTE"),
	ARRIVED("ARRIVED"),
	CANCELLED("CANCELLED"),
	COMPLETED("COMPLETED");

	override fun toString(): String {
		return state
	}

	companion object {
		fun fromString(state: String): RideState {
			return values().first { it.state == state }
		}
	}
}