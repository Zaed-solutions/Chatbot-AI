package com.zaed.chatbot.data.repository

import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionChunk
import com.zaed.chatbot.data.model.ChatHistory
import com.zaed.chatbot.data.model.ChatQuery
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    suspend fun sendPrompt(chatQuery: ChatQuery): Flow<ChatCompletion>
    suspend fun getChatById(chatId: String): Flow<Result<List<ChatQuery>>>
    suspend fun getChatHistories(): Flow<Result<List<ChatHistory>>>
    suspend fun deleteChatHistory(chatId: String)
    suspend fun updateChatHistory(chatHistory: ChatHistory)
    suspend fun createChatHistory(chatHistory: ChatHistory)
}