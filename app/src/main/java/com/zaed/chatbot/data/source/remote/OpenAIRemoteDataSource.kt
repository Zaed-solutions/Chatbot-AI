package com.zaed.chatbot.data.source.remote

import android.net.Uri
import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.image.ImageSize
import com.aallam.openai.api.image.ImageURL
import com.aallam.openai.api.model.ModelId
import com.zaed.chatbot.data.model.ChatQuery
import kotlinx.coroutines.flow.Flow

interface OpenAIRemoteDataSource {
    suspend fun sendPrompt(
        chatQuery: ChatQuery,
        isFirst: Boolean = false,
        modelId: ModelId
    ): Flow<Result<ChatCompletion>>

    suspend fun createImage(
        chatQuery: ChatQuery,
        n: Int = 1,
        size: ImageSize = ImageSize.is1024x1024
    ): Flow<Result<List<ImageURL>>>

    fun uploadNewImage(uri: Uri): Flow<Result<String>>

}