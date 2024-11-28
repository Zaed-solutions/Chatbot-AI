package com.zaed.chatbot.data.source.local

import com.zaed.chatbot.data.model.ChatHistory
import com.zaed.chatbot.data.model.ChatQuery
import kotlinx.coroutines.flow.Flow

interface ChatLocalDataSource {
    suspend fun saveChat(chat: ChatQuery): Flow<Result<Boolean>>
    suspend fun getChatById(chatId: String): Flow<Result<List<ChatQuery>>>
    suspend fun createChatHistory(chatHistory: ChatHistory)
    suspend fun updateChatHistory(chatHistory: ChatHistory): Flow<Result<Unit>>
    suspend fun deleteChatHistory(chatId: String): Flow<Result<Unit>>
    suspend fun getChatHistories():  Result<List<ChatHistory>>
}
