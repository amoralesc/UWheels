package com.abmodel.uwheels.data.repository.ride

import com.abmodel.uwheels.data.model.CustomDate
import com.abmodel.uwheels.data.model.Ride
import com.abmodel.uwheels.data.model.RideUser
import kotlinx.coroutines.flow.Flow

interface RideRepository {
	suspend fun createRide(ride: Ride)
	suspend fun addPassengerToRide(rideId: String, passenger: RideUser)
	suspend fun removePassengerFromRide(rideId: String, passengerId: String)
	suspend fun startRide(rideId: String, startedDate: CustomDate?)
	suspend fun finishRide(rideId: String, finishedDate: CustomDate?)
	suspend fun cancelRide(rideId: String)
	suspend fun fetchUserRides(userId: String): Flow<Result<List<Ride>>>
}
