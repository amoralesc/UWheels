package com.abmodel.uwheels.data.repository.ride

import com.abmodel.uwheels.data.model.*
import kotlinx.coroutines.flow.Flow

interface RidesRepository {
	suspend fun createRide(ride: Ride)
	suspend fun startRide(rideId: String, startedDate: CustomDate? = null)
	suspend fun finishRide(rideId: String, finishedDate: CustomDate? = null)
	suspend fun cancelRide(rideId: String)
	suspend fun rateUser(userId: String, rating: Double)
	suspend fun fetchUserRides(
		userId: String, hosted: Boolean = false, wheelsType: WheelsType? = null
	): Flow<Result<List<Ride>>>
	suspend fun searchRides(query: SearchRideQuery): Flow<Result<List<Ride>>>
	suspend fun requestRide(rideId: String, request: RideRequest)
	suspend fun acceptRideRequest(rideId: String, request: RideRequest)
	suspend fun rejectRideRequest(rideId: String, request: RideRequest)
	suspend fun removePassengerFromRide(rideId: String, passengerId: String)
}
