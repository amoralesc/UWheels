package com.abmodel.uwheels.data.model

enum class RideRequestStatus(
	val status: String
) {
	PENDING("PENDING"),
	ACCEPTED("ACCEPTED"),
	REJECTED("REJECTED");

	override fun toString(): String {
		return status
	}

	companion object {
		fun fromString(status: String): RideRequestStatus {
			return values().firstOrNull { it.status == status } ?: PENDING
		}
	}
}
