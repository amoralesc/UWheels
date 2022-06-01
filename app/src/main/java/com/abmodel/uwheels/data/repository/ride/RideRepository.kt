package com.abmodel.uwheels.data.repository.ride

import com.abmodel.uwheels.data.model.Ride
import kotlinx.coroutines.flow.Flow

interface RideRepository {
	suspend fun createRide(ride: Ride)
	suspend fun fetchUserRides(userId: String): Flow<Result<List<Ride>>>
}
