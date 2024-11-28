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
    suspend fun sendPrompt(chatQuery: ChatQuery,modelId: ModelId,isFirstMessage: Boolean): Flow<ChatCompletion>
    suspend fun createImage(chatQuery:ChatQuery,n: Int = 1, size: ImageSize , isFirstMessage: Boolean): Flow<List<ImageURL>>
    suspend fun listModels(): List<Model>

    suspend fun getChatById(chatId: String): Flow<Result<List<ChatQuery>>>
    suspend fun getChatHistories():  Result<List<ChatHistory>>
    suspend fun deleteChatHistory(chatId: String): Flow<Result<Unit>>
    suspend fun updateChatHistory(chatHistory: ChatHistory): Flow<Result<Unit>>
    suspend fun createChatHistory(chatHistory: ChatHistory)
}