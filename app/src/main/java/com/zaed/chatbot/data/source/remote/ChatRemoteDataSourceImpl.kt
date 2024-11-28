package com.zaed.chatbot.data.source.remote

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.zaed.chatbot.data.model.ChatQuery
import com.zaed.chatbot.ui.util.createTempFileFromUri

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ChatRemoteDataSourceImpl : ChatRemoteDataSource {
    override suspend fun sendPrompt(chatQuery: ChatQuery): Flow<Result<ChatQuery>> = flow {
        val apiKey = "AIzaSyCbx7jQ7Y_ncVOKUNdvvA9XZ5IP4m_MO8g"
        val generativeModel =
            GenerativeModel(
                // Specify a Gemini model appropriate for your use case
                modelName = "gemini-1.5-flash",
                // Access your API key as a Build Configuration variable (see "Set up your API key" above)
                apiKey = apiKey
            )

        val prompt = chatQuery.prompt
        val response = generativeModel.generateContent(
            content {
                text(prompt.ifBlank { "?" })
            }
        )
        emit(Result.success(chatQuery.copy(response = response.text.toString())))
    }
}