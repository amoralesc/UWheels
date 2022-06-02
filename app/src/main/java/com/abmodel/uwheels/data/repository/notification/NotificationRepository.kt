package com.abmodel.uwheels.data.repository.notification

import com.abmodel.uwheels.data.model.CustomNotification
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
	suspend fun fetchNotifications(userId: String): Flow<Result<CustomNotification>>
	suspend fun sendNotification(notification: CustomNotification)
}
