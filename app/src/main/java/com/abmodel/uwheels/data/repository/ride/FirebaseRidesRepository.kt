package com.abmodel.uwheels.data.repository.ride

import android.util.Log
import com.abmodel.uwheels.data.DatabasePaths
import com.abmodel.uwheels.data.FirestorePaths
import com.abmodel.uwheels.data.model.*
import com.abmodel.uwheels.data.repository.notification.FirebaseNotificationRepository
import com.abmodel.uwheels.util.compareDates
import com.abmodel.uwheels.util.difference
import com.abmodel.uwheels.util.distanceTo
import com.abmodel.uwheels.util.toLatLng
import com.google.common.graph.ElementOrder.sorted
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.stream.Collectors

class FirebaseRidesRepository internal constructor(
	private val mDatabase: FirebaseDatabase,
	private val mFirestore: FirebaseFirestore
) : RidesRepository {

	companion object {
		@Volatile
		private var instance: FirebaseRidesRepository? = null

		fun getInstance(): FirebaseRidesRepository {
			return instance ?: synchronized(this) {
				instance ?: FirebaseRidesRepository(
					Firebase.database,
					Firebase.firestore
				).also { instance = it }
			}
		}

		const val TAG = "FirebaseRideRepository"
	}

	private suspend fun getRide(rideId: String): DocumentSnapshot? {

		return mFirestore
			.collection(FirestorePaths.RIDES)
			.document(rideId)
			.get()
			.await()
	}

	override suspend fun createRide(ride: Ride) {

		val chatRef =
			mDatabase
				.getReference(DatabasePaths.CHATS)
				.push()

		val rideDocument =
			mFirestore
				.collection(FirestorePaths.RIDES)
				.document()
		ride.id = rideDocument.id
		ride.chatId = chatRef.key!!

		rideDocument.set(ride).await()

		// Create the chat of the ride
		val chat = Chat(
			id = chatRef.key!!,
			rideId = rideDocument.id,
			name = ride.wheelsType,
			date = ride.date,
			source = ride.source,
			destination = ride.destination,
		)

		chatRef.setValue(chat).await()
	}

	override suspend fun startRide(rideId: String, startedDate: CustomDate?) {

		mFirestore
			.collection(FirestorePaths.RIDES)
			.document(rideId)
			.update(
				"status", RideStatus.ACTIVE.toString(),
				"startedDate", startedDate
			)
			.await()

		val ride = getRide(rideId)!!
			.toObject(Ride::class.java)!!

		ride.subscribers.forEach {
			FirebaseNotificationRepository.getInstance().sendNotification(
				CustomNotification(
					it,
					"The ride has started",
					"${ride.host.name} has started the ride",
				)
			)
		}
	}

	override suspend fun finishRide(rideId: String, finishedDate: CustomDate?) {

		val ride = getRide(rideId)!!
			.toObject(Ride::class.java)!!

		if (ride.startedDate != null && finishedDate != null) {
			ride.duration = ride.startedDate.difference(finishedDate)
		}
		ride.status = RideStatus.COMPLETED.toString()

		mFirestore
			.collection(FirestorePaths.RIDES)
			.document(rideId)
			.set(ride)
			.await()

		// Delete the associated chat
		deleteChat(ride.chatId)
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

	override suspend fun rateUser(userId: String, rating: Double) {

		val user = mFirestore
			.collection(FirestorePaths.USERS)
			.document(userId)
			.get()
			.await()

		val currentRating = (user.get("rating") as HashMap<*, *>).let {
			val value = if (it["value"] is Number) {
				(it["value"] as Number).toDouble()
			} else {
				throw IllegalStateException("Rating value is not a number")
			}

			Rating(value, it["count"] as Long)
		}
		val newRating = Rating(
			(currentRating.value * currentRating.count + rating) / (currentRating.count + 1),
			currentRating.count + 1
		)

		mFirestore
			.collection(FirestorePaths.USERS)
			.document(userId)
			.update(
				"rating", newRating
			)
			.await()
	}

	@OptIn(ExperimentalCoroutinesApi::class)
	override suspend fun fetchUserRides(
		userId: String, hosted: Boolean, wheelsType: WheelsType?
	): Flow<Result<List<Ride>>> = callbackFlow {

		val listener =
			EventListener<QuerySnapshot> { snapshot, error ->
				if (error != null) {
					trySend(Result.failure(error))
				}

				if (snapshot != null) {
					val rides = snapshot.toObjects(Ride::class.java)
					rides.stream()
						.sorted { ride1, ride2 ->
							ride1.date.compareDates(ride2.date)
						}
						.collect(Collectors.toList())
						.let {
							trySend(Result.success(it))
						}
				}
			}

		val subscription = when {
			hosted && wheelsType != null -> {
				mFirestore
					.collection(FirestorePaths.RIDES)
					.whereEqualTo("host.uid", userId)
					.whereEqualTo("wheelsType", wheelsType.toString())
					.orderBy("status")
					.addSnapshotListener(listener)
			}
			wheelsType != null -> {
				mFirestore
					.collection(FirestorePaths.RIDES)
					.whereArrayContains("subscribers", userId)
					.whereEqualTo("wheelsType", wheelsType.toString())
					.orderBy("status")
					.addSnapshotListener(listener)
			}
			else -> {
				mFirestore
					.collection(FirestorePaths.RIDES)
					.whereArrayContains("subscribers", userId)
					.orderBy("status")
					.addSnapshotListener(listener)
			}
		}

		awaitClose {
			subscription.remove()
			Log.d(TAG, "Closing user rides flow")
		}
	}

	@OptIn(ExperimentalCoroutinesApi::class)
	override suspend fun searchRides(
		query: SearchRideQuery
	): Flow<Result<List<Ride>>> = callbackFlow {

		val listener =
			EventListener<QuerySnapshot> { snapshot, error ->
				if (error != null) {
					trySend(Result.failure(error))
				}

				if (snapshot != null) {
					val rides = snapshot.toObjects(Ride::class.java)

					// Filter rides according to query parameters
					rides.stream()
						.filter { ride ->
							!ride.subscribers.contains(query.userId) &&
									ride.date.difference(query.date) < query.maxTimeDifference &&
									ride.source.latLng!!.toLatLng()
										.distanceTo(query.source.latLng!!.toLatLng()) < query.maxDistance &&
									ride.destination.latLng!!.toLatLng()
										.distanceTo(query.destination.latLng!!.toLatLng()) < query.maxDistance
						}
						.sorted { ride1, ride2 ->
							ride1.date.compareDates(ride2.date)
						}
						.collect(Collectors.toList())
						.let { filteredRides ->
							trySend(Result.success(filteredRides))
						}
				}
			}

		val subscription = mFirestore
			.collection(FirestorePaths.RIDES)
			.whereEqualTo("status", RideStatus.OPEN.toString())
			.orderBy("wheelsType")
			.addSnapshotListener(listener)

		awaitClose {
			subscription.remove()
			Log.d(TAG, "Closing search rides flow")
		}
	}

	override suspend fun requestRide(rideId: String, request: RideRequest) {
		val ride = getRide(rideId)!!
			.toObject(Ride::class.java)!!

		if (request.user.uid in ride.subscribers) {
			Log.d(TAG, "User is already subscribed to ride")
			return
		}

		ride.requests.add(request)
		ride.subscribers.add(request.user.uid)

		mFirestore
			.collection(FirestorePaths.RIDES)
			.document(rideId)
			.set(ride)
			.await()

		FirebaseNotificationRepository.getInstance().sendNotification(
			CustomNotification(
				ride.host.uid,
				"New request for ride",
				"${request.user.name} has requested to join your ride"
			)
		)
	}

	override suspend fun acceptRideRequest(rideId: String, request: RideRequest) {
		val ride = getRide(rideId)!!
			.toObject(Ride::class.java)!!

		FirebaseNotificationRepository.getInstance().sendNotification(
			CustomNotification(
				request.user.uid,
				"Ride request accepted",
				"${ride.host.name} has accepted your request to join their ride"
			)
		)

		ride.requests.removeIf { it.user.uid == request.user.uid }
		addPassengerToRide(ride, request)
	}

	override suspend fun rejectRideRequest(rideId: String, request: RideRequest) {
		val ride = getRide(rideId)!!
			.toObject(Ride::class.java)!!

		ride.requests.removeIf { it.user.uid == request.user.uid }
		ride.subscribers.remove(request.user.uid)

		mFirestore
			.collection(FirestorePaths.RIDES)
			.document(rideId)
			.set(ride)
			.await()
	}

	private suspend fun addPassengerToRide(ride: Ride, request: RideRequest) {

		// The above for loop can be replaced with the following line
		if (ride.passengers.any { passenger -> passenger.uid == request.user.uid }) {
			Log.d(TAG, "User is already a passenger")
			return
		}

		ride.passengers.add(request.user)
		if (request.user.uid !in ride.subscribers)
			ride.subscribers.add(request.user.uid)
		ride.currentCapacity++
		if (ride.currentCapacity == ride.totalCapacity)
			ride.status = RideStatus.FULL.toString()

		if (ride.status != RideStatus.FULL.toString()) {
			mFirestore
				.collection(FirestorePaths.RIDES)
				.document(ride.id)
				.set(ride)
				.await()

		} else {
			rideIsFull(ride)
		}
	}

	private suspend fun rideIsFull(ride: Ride) {

		// Remove all requests and their subscribers
		ride.requests.stream()
			.forEach { request ->
				ride.subscribers.removeIf { it == request.user.uid }
			}
		ride.requests.clear()

		mFirestore
			.collection(FirestorePaths.RIDES)
			.document(ride.id)
			.set(ride)
			.await()

		// TODO: NOTIFY USERS
	}

	override suspend fun removePassengerFromRide(rideId: String, passengerId: String) {

		val ride = getRide(rideId)!!
			.toObject(Ride::class.java)!!

		ride.passengers.removeIf { it.uid == passengerId }
		ride.subscribers.remove(passengerId)
		ride.currentCapacity--
		if (ride.status == RideStatus.FULL.toString()) {
			ride.status = RideStatus.OPEN.toString()
		}

		mFirestore
			.collection(FirestorePaths.RIDES)
			.document(rideId)
			.set(ride)
			.await()
	}
}
