package com.abmodel.uwheels.data.repository.ride

import com.abmodel.uwheels.data.DatabasePaths
import com.abmodel.uwheels.data.FirestorePaths
import com.abmodel.uwheels.data.model.*
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.DocumentSnapshot
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

		val chatRef =
			mDatabase
				.getReference(DatabasePaths.CHATS)
				.push()

		ride.chatId = chatRef.key!!
		val rideId =
			mFirestore
				.collection(FirestorePaths.RIDES)
				.add(ride)
				.await()
				.id

		// Create the chat of the ride
		val chat = Chat(
			rideId = rideId,
			name = ride.wheelsType,
			date = ride.date,
			source = ride.source,
			destination = ride.destination,
		)

		chatRef.setValue(chat).await()
	}

	private suspend fun getRide(rideId: String): DocumentSnapshot? {

		return mFirestore
			.collection(FirestorePaths.RIDES)
			.document(rideId)
			.get()
			.await()
	}

	override suspend fun addPassengerToRide(rideId: String, passenger: RideUser) {

		val ride = getRide(rideId)!!
			.toObject(Ride::class.java)!!

		ride.passengers.add(passenger)
		ride.subscribers.add(passenger.uid)

		mFirestore
			.collection(FirestorePaths.RIDES)
			.document(rideId)
			.update(
				"passengers", ride.passengers,
				"subscribers", ride.subscribers
			)
			.await()
	}

	override suspend fun removePassengerFromRide(rideId: String, passengerId: String) {

		val ride = getRide(rideId)!!
			.toObject(Ride::class.java)!!

		ride.passengers.removeIf { it.uid == passengerId }
		ride.subscribers.remove(passengerId)

		mFirestore
			.collection(FirestorePaths.RIDES)
			.document(rideId)
			.update(
				"passengers", ride.passengers,
				"subscribers", ride.subscribers
			)
			.await()
	}

	override suspend fun startRide(rideId: String, startedDate: CustomDate?) {

		mFirestore
			.collection(FirestorePaths.RIDES)
			.document(rideId)
			.update(
				"status", RideStatus.STARTED.toString(),
				"startedDate", startedDate
			)
			.await()
	}

	override suspend fun finishRide(rideId: String, finishedDate: CustomDate?) {

		val ride = getRide(rideId)!!

		mFirestore
			.collection(FirestorePaths.RIDES)
			.document(rideId)
			.update(
				"status", RideStatus.COMPLETED.toString(),
				"finishedDate", finishedDate
			)
			.await()

		// Delete the associated chat
		deleteChat(ride.getString("chatId")!!)
	}

	override suspend fun cancelRide(rideId: String) {

		val ride = getRide(rideId)!!

		mFirestore
			.collection(FirestorePaths.RIDES)
			.document(rideId)
			.update(
				"status", RideStatus.CANCELLED.toString(),
				"subscribers", emptyList<String>()
			)
			.await()

		// Delete the associated chat
		deleteChat(ride.getString("chatId")!!)
	}

	private suspend fun deleteChat(chatId: String) {

		mDatabase
			.getReference(DatabasePaths.CHATS)
			.child(chatId)
			.removeValue()
			.await()
	}

	@OptIn(ExperimentalCoroutinesApi::class)
	override suspend fun fetchUserRides(userId: String): Flow<Result<List<Ride>>> = callbackFlow {

		val subscription =
			mFirestore
				.collection(FirestorePaths.RIDES)
				.whereArrayContains("subscribers", userId)
				.orderBy("state")
				.addSnapshotListener { snapshot, error ->
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
