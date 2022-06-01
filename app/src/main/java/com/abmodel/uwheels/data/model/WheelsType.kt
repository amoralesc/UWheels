package com.abmodel.uwheels.data.model

enum class WheelsType(val type: String) {
	CLASSIC_WHEELS("Classic Wheels"),
	SHARED_WHEELS("Shared Wheels"),
	WE_WHEELS("We Wheels");

	override fun toString(): String {
		return type
	}

	companion object {
		fun fromString(type: String): WheelsType {
			return values().first { it.type == type }
		}
	}
}
