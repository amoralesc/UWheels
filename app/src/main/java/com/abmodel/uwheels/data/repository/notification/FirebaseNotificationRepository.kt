package com.abmodel.uwheels.data.repository.notification

import android.app.Notification
import com.abmodel.uwheels.data.DatabasePaths
import com.abmodel.uwheels.data.model.CustomNotification
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseNotificationRepository internal constructor(
	private val mDatabase: FirebaseDatabase
) : NotificationRepository {

	companion object {
		@Volatile
		private var instance: FirebaseNotificationRepository? = null

		fun getInstance(): FirebaseNotificationRepository {
			return instance ?: synchronized(this) {
				instance ?: FirebaseNotificationRepository(
					Firebase.database,
				).also { instance = it }
			}
		}

		const val TAG = "FirebaseNotificationRepository"
	}

	@OptIn(ExperimentalCoroutinesApi::class)
	override suspend fun fetchNotifications(userId: String):
			Flow<Result<CustomNotification>> = callbackFlow {

		val ref = mDatabase
			.getReference(DatabasePaths.NOTIFICATIONS)
			.child(userId)

		val listener = object : ValueEventListener {
			override fun onCancelled(error: DatabaseError) {
				trySend(Result.failure(error.toException()))
			}

			override fun onDataChange(snapshot: DataSnapshot) {

				val notification = snapshot.getValue(CustomNotification::class.java)
				if (notification != null) {
					ref.removeValue()
					trySend(Result.success(notification))
				}
			}
		}

		ref.addValueEventListener(listener)

		awaitClose {
			mDatabase.getReference(DatabasePaths.NOTIFICATIONS)
				.child(userId)
				.removeEventListener(listener)
		}
	}

	override suspend fun sendNotification(notification: CustomNotification) {
		mDatabase.getReference(DatabasePaths.NOTIFICATIONS)
			.child(notification.userId)
			.setValue(notification)
			.await()
	}
}