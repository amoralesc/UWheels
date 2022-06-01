package com.abmodel.uwheels.data.repository.ride

import com.abmodel.uwheels.data.model.Ride

interface RideRepository {
	suspend fun createRide(ride: Ride)
	suspend fun getUserRides(userId: String): List<Ride>
}
