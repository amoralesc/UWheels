package com.abmodel.uwheels.data.repository.ride

import com.abmodel.uwheels.data.DatabasePaths
import com.abmodel.uwheels.data.model.Ride
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class FirebaseRideDataSource {

	companion object {
		@Volatile
		private var instance: FirebaseRideDataSource? = null
		fun getInstance() = instance ?: synchronized(this) {
			instance ?: FirebaseRideDataSource().also { instance = it }
		}

		const val TAG = "FirebaseRideDataSource"
	}

	private val mFirestore = Firebase.firestore
	private val mDatabase = Firebase.database

	suspend fun createRide(ride: Ride) {
		mDatabase
			.getReference(DatabasePaths.RIDES)
			.push()
			.setValue(ride)
			.await()
	}
}