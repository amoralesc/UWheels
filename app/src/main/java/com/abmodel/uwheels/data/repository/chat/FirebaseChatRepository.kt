package com.abmodel.uwheels.data.repository.chat

import android.util.Log
import com.abmodel.uwheels.data.DatabasePaths
import com.abmodel.uwheels.data.FirestorePaths
import com.abmodel.uwheels.data.model.Chat
import com.abmodel.uwheels.data.model.Message
import com.abmodel.uwheels.data.model.Ride
import com.abmodel.uwheels.data.model.RideStatus
import com.abmodel.uwheels.data.repository.ride.FirebaseRidesRepository
import com.abmodel.uwheels.util.compareDates
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.stream.Collectors

class FirebaseChatRepository internal constructor(
	private val mDatabase: FirebaseDatabase,
	private val mFirestore: FirebaseFirestore
) : ChatRepository {

	companion object {
		@Volatile
		private var instance: FirebaseChatRepository? = null

		fun getInstance(): FirebaseChatRepository {
			return instance ?: synchronized(this) {
				instance ?: FirebaseChatRepository(
					Firebase.database,
					Firebase.firestore
				).also { instance = it }
			}
		}

		const val TAG = "FirebaseChatRepository"
	}

	@OptIn(ExperimentalCoroutinesApi::class)
	override suspend fun fetchChats(userId: String): Flow<Result<List<Chat>>> = callbackFlow {

		val listener =
			EventListener<QuerySnapshot> { snapshot, error ->
				if (error != null) {
					trySend(Result.failure(error))
				}

				if (snapshot != null) {
					val rides = snapshot.toObjects(Ride::class.java)
					rides.stream()
						.filter { ride ->
							ride.requests.stream()
								.noneMatch { request -> request.user.uid == userId }
						}
						.sorted { ride1, ride2 ->
							ride1.date.compareDates(ride2.date)
						}
						.map {
							Chat.fromRide(it)
						}
						.collect(Collectors.toList())
						.let {
							trySend(Result.success(it))
						}
				}
			}

		val subscription =
			mFirestore
				.collection(FirestorePaths.RIDES)
				.whereArrayContains("subscribers", userId)
				.whereIn(
					"status", listOf(
						RideStatus.OPEN.toString(),
						RideStatus.FULL.toString(),
						RideStatus.ACTIVE.toString(),
					)
				)
				.addSnapshotListener(listener)

		awaitClose {
			subscription.remove()
			Log.d(FirebaseRidesRepository.TAG, "Closing user chats flow")
		}
	}

	@OptIn(ExperimentalCoroutinesApi::class)
	override suspend fun fetchChat(chatId: String): Flow<Result<List<Message>>> = callbackFlow {

		val subscription =
			mDatabase
				.getReference(DatabasePaths.CHATS)
				.child(chatId)
				.child(DatabasePaths.MESSAGES)
				.addValueEventListener(object : ValueEventListener {
					override fun onCancelled(error: DatabaseError) {
						trySend(Result.failure(error.toException()))
					}

					override fun onDataChange(snapshot: DataSnapshot) {
						val messages = snapshot.children.map {
							Log.d(TAG, "Message: ${it.value}")
							val data = it.value as Map<*, *>
							Message(
								uid = data["uid"] as String,
								name = data["name"] as String,
								message = data["message"] as String,
								date = data["date"] as String
							)
						}
						trySend(Result.success(messages))
					}
				})

		awaitClose {
			Log.d(FirebaseRidesRepository.TAG, "Closing user chats flow")
		}
	}

	override suspend fun sendMessage(chatId: String, message: Message) {

		mDatabase
			.getReference(DatabasePaths.CHATS)
			.child(chatId)
			.child(DatabasePaths.MESSAGES)
			.push()
			.setValue(message)
			.await()
	}
}
