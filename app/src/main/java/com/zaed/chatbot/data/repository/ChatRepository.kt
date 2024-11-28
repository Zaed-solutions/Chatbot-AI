package com.zaed.chatbot.data.repository

import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionChunk
import com.aallam.openai.api.image.ImageSize
import com.aallam.openai.api.image.ImageURL
import com.aallam.openai.api.model.Model
import com.aallam.openai.api.model.ModelId
import com.zaed.chatbot.data.model.ChatHistory
import com.zaed.chatbot.data.model.ChatQuery
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    suspend fun sendPrompt(chatQuery: ChatQuery,modelId: ModelId): Flow<ChatCompletion>
    suspend fun createImage(prompt: String,n: Int = 1, size: ImageSize = ImageSize.is1024x1024): List<ImageURL>
    suspend fun listModels(): List<Model>

    suspend fun getChatById(chatId: String): Flow<Result<List<ChatQuery>>>
    suspend fun getChatHistories(): Flow<Result<List<ChatHistory>>>
    suspend fun deleteChatHistory(chatId: String)
    suspend fun updateChatHistory(chatHistory: ChatHistory)
    suspend fun createChatHistory(chatHistory: ChatHistory)
}