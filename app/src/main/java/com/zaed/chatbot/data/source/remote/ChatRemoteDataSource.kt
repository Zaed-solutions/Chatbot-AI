package com.zaed.chatbot.data.source.remote

import com.zaed.chatbot.data.model.ChatQuery
import kotlinx.coroutines.flow.Flow

interface ChatRemoteDataSource {
    suspend fun sendPrompt(chatQuery: ChatQuery): Flow<Result<ChatQuery>>
}
