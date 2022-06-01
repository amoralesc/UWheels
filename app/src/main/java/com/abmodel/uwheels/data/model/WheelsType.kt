package com.abmodel.uwheels.data.model

enum class WheelsType(
	val type: String,
	val transportation: Array<String>,
	val capacity: IntRange?,
) {
	CLASSIC_WHEELS(
		"Classic Wheels",
		arrayOf("Car"),
		null
	),
	SHARED_WHEELS(
		"Shared Wheels",
		arrayOf("Taxi", "Uber", "Beat", "Didi", "Cabify"),
		1..4
	),
	WE_WHEELS(
		"We Wheels",
		arrayOf("Transmilenio", "SITP", "Bus"),
		1..10
	);

	override fun toString(): String {
		return type
	}

	companion object {
		fun fromString(type: String): WheelsType {
			return values().first { it.type == type }
		}
	}
}
