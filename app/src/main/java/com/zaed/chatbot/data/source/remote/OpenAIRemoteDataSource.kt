package com.zaed.chatbot.data.source.remote

import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionChunk
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.image.ImageSize
import com.aallam.openai.api.image.ImageURL
import com.aallam.openai.api.model.Model
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.zaed.chatbot.data.model.ChatQuery
import kotlinx.coroutines.flow.Flow

interface OpenAIRemoteDataSource {
    suspend fun sendPrompt(chatQuery: ChatQuery,modelId: ModelId): Flow<ChatCompletion>


    suspend fun createImage(
        chatQuery: ChatQuery,
        n: Int = 1,
        size: ImageSize = ImageSize.is1024x1024
    ): List<ImageURL>

    suspend fun listModels(): List<Model>
}