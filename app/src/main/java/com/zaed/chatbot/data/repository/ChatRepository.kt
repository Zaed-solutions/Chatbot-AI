package com.zaed.chatbot.data.repository

import android.net.Uri
import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionChunk
import com.aallam.openai.api.file.FileId
import com.aallam.openai.api.image.ImageSize
import com.aallam.openai.api.image.ImageURL
import com.aallam.openai.api.model.Model
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.api.thread.Thread
import com.zaed.chatbot.data.model.ChatHistory
import com.zaed.chatbot.data.model.ChatQuery
import com.zaed.chatbot.data.model.MessageAttachment
import kotlinx.coroutines.flow.Flow
import okio.Source

interface ChatRepository {
    suspend fun sendPrompt(chatQuery: ChatQuery,modelId: ModelId,isFirstMessage: Boolean): Flow<Result<ChatCompletion>>
    suspend fun createImage(chatQuery:ChatQuery,n: Int = 1, size: ImageSize , isFirstMessage: Boolean): Flow<Result<List<ImageURL>>>
    suspend fun getChatById(chatId: String): Flow<Result<List<ChatQuery>>>
    suspend fun getChatHistories():  Result<List<ChatHistory>>
    suspend fun deleteChatHistory(chatId: String): Flow<Result<Unit>>
    suspend fun updateChatHistory(chatHistory: ChatHistory): Flow<Result<Unit>>
    suspend fun createChatHistory(chatHistory: ChatHistory)
    fun uploadNewImage(uri: Uri): Flow<Result<String>>

}