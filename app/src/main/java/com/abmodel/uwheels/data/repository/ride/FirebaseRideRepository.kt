package com.abmodel.uwheels.data.repository.ride

import com.abmodel.uwheels.data.model.Ride

class FirebaseRideRepository internal constructor(
	private val dataSource: FirebaseRideDataSource
) : RideRepository {

	companion object {
		@Volatile
		private var instance: FirebaseRideRepository? = null

		fun getInstance(): FirebaseRideRepository {
			return instance ?: synchronized(this) {
				instance ?: FirebaseRideRepository(
					FirebaseRideDataSource.getInstance()
				).also { instance = it }
			}
		}

		const val TAG = "FirebaseRideRepository"
	}

	override suspend fun createRide(ride: Ride) {
		dataSource.createRide(ride)
	}

	override suspend fun getUserRides(userId: String): List<Ride> {
		TODO("Not yet implemented")
	}
}
