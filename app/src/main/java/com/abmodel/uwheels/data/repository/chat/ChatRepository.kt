package com.abmodel.uwheels.data.repository.chat

import com.abmodel.uwheels.data.model.Chat
import com.abmodel.uwheels.data.model.Message
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
	suspend fun fetchChats(
		userId: String
	): Flow<Result<List<Chat>>>
	suspend fun fetchChat(
		chatId: String
	): Flow<Result<List<Message>>>
	suspend fun sendMessage(
		chatId: String, message: Message
	)
}
