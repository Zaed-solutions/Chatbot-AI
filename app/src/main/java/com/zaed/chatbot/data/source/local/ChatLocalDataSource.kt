package com.zaed.chatbot.data.source.local

import com.zaed.chatbot.data.model.ChatQuery
import kotlinx.coroutines.flow.Flow

interface ChatLocalDataSource {
    suspend fun saveChat(chat: ChatQuery): Flow<Result<Boolean>>
    suspend fun getChatById(chatId: String): Flow<Result<List<ChatQuery>>>
}
