package com.zaed.chatbot.data.repository

import android.util.Log
import com.zaed.chatbot.data.model.ChatHistory
import com.zaed.chatbot.data.model.ChatQuery
import com.zaed.chatbot.data.source.local.ChatLocalDataSource
import com.zaed.chatbot.data.source.remote.ChatRemoteDataSource
import kotlinx.coroutines.flow.Flow

class ChatRepositoryImpl(
    private val chatRemoteDataSource: ChatRemoteDataSource,
    private val chatLocalDataSource: ChatLocalDataSource
) : ChatRepository {
    override suspend fun sendPrompt(chatQuery: ChatQuery): Flow<Result<ChatQuery>> {
        val result =  chatRemoteDataSource.sendPrompt(chatQuery)
        result.collect{data->
            data.onSuccess {
                Log.d("ChatRepositoryImpl", "sendPrompt: $it")
                chatLocalDataSource.saveChat(it.copy(isLoading = false, animateResponse = false)).collect{}
            }.onFailure {
                Log.d("ChatRepositoryImpl", "sendPrompt: $it")
            }
        }
        return result
    }

    override suspend fun getChatById(chatId: String): Flow<Result<List<ChatQuery>>> {
        return chatLocalDataSource.getChatById(chatId)
    }

    override suspend fun getChatHistories(): Flow<Result<List<ChatHistory>>> {
        return chatLocalDataSource.getChatHistories()
    }

    override suspend fun deleteChatHistory(chatId: String) {
        return chatLocalDataSource.deleteChatHistory(chatId)
    }

    override suspend fun updateChatHistory(chatHistory: ChatHistory) {
        return chatLocalDataSource.updateChatHistory(chatHistory)
    }

    override suspend fun createChatHistory(chatHistory: ChatHistory) {
        return chatLocalDataSource.createChatHistory(chatHistory)
    }

}