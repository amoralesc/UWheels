package com.abmodel.uwheels.data.repository.ride

import com.abmodel.uwheels.data.FirestorePaths
import com.abmodel.uwheels.data.model.Ride
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseRideRepository internal constructor(
	private val mDatabase: FirebaseDatabase,
	private val mFirestore: FirebaseFirestore
) : RideRepository {

	companion object {
		@Volatile
		private var instance: FirebaseRideRepository? = null

		fun getInstance(): FirebaseRideRepository {
			return instance ?: synchronized(this) {
				instance ?: FirebaseRideRepository(
					Firebase.database,
					Firebase.firestore
				).also { instance = it }
			}
		}

		const val TAG = "FirebaseRideRepository"
	}

	override suspend fun createRide(ride: Ride) {

		mFirestore
			.collection(FirestorePaths.RIDES)
			.add(ride)
			.await()
	}

	@OptIn(ExperimentalCoroutinesApi::class)
	override suspend fun fetchUserRides(userId: String): Flow<Result<List<Ride>>> = callbackFlow {

		val subscription =
			mFirestore
				.collection(FirestorePaths.RIDES)
				.whereArrayContains("subscribers", userId)
				.orderBy("state")
				.addSnapshotListener{ snapshot, error ->
					if (error != null) {
						trySend(Result.failure(error))
						return@addSnapshotListener
					}

					if (snapshot != null) {
						val rides = snapshot.toObjects(Ride::class.java)
						trySend(Result.success(rides))
					}
				}

		awaitClose {
			subscription.remove()
		}
	}
}
