package com.zaed.chatbot.data.source.local

import android.util.Log
import com.zaed.chatbot.data.model.ChatHistory
import com.zaed.chatbot.data.model.ChatQuery
import com.zaed.chatbot.data.model.toChatQueryEntity
import com.zaed.chatbot.data.source.local.model.ChatHistoryEntity
import com.zaed.chatbot.data.source.local.model.ChatQueryEntity
import com.zaed.chatbot.data.source.local.model.toChatHistory
import com.zaed.chatbot.data.source.local.model.toChatHistoryEntity
import com.zaed.chatbot.data.source.local.model.toChatQuery
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ChatLocalDataSourceImpl(private val realm: Realm) : ChatLocalDataSource {
    private val TAG = "ChatLocalDataSourceImpl"
    override suspend fun saveChat(chat: ChatQuery): Flow<Result<Boolean>> = flow {
        Log.d("ChatLocalDataSource", "saveChat: $chat")
        try {
            Log.d("ChatLocalDataSource", "saveChat: $chat")
            realm.write {
                val result = copyToRealm(toChatQueryEntity(chat))
                Log.d("ChatLocalDataSource", "saveChat: $result")
            }
            emit(Result.success(true))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }

    }

    override suspend fun getChatById(chatId: String): Flow<Result<List<ChatQuery>>> = flow {
        try {
            val chats = realm.query<ChatQueryEntity>("chatId == $0", chatId)
                .find()
                .map { it.toChatQuery() }
            Log.d("ChatLocalDataSource", "getChatById: $chats")
            emit(Result.success(chats))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override suspend fun createChatHistory(chatHistory: ChatHistory) {
        try {
            Log.d("ChatLocalDataSource", "saveChatHistory: $chatHistory")
            realm.write {
                val result = copyToRealm(toChatHistoryEntity(chatHistory))
                Log.d("ChatLocalDataSource", "saveChatHistory result: $result")
            }
        } catch (e: Exception) {
            Log.e(TAG, "saveChatHistory error: ${e.localizedMessage}", )
            e.printStackTrace()
        }
    }

    override suspend fun updateChatHistory(chatHistory: ChatHistory): Flow<Result<Unit>> = flow {
        try {
            Log.d("ChatLocalDataSource", "updateChatHistory: $chatHistory")
            realm.write {
                val existing = query<ChatHistoryEntity>("chatId == $0", chatHistory.chatId).first().find()
                if (existing != null) {
                    existing.apply {
                        lastResponse = chatHistory.lastResponse
                        lastResponseTime = chatHistory.lastResponseTime
                        title = chatHistory.title
                    }
                    Log.d("ChatLocalDataSource", "updateChatHistory updated: $existing")
                } else {
                    throw IllegalArgumentException("Chat history with id ${chatHistory.chatId} not found.")
                }
            }
            emit(Result.success(Unit))
        } catch (e: Exception) {
            Log.e(TAG, "updateChatHistory error: ${e.localizedMessage}", )
            e.printStackTrace()
            emit(Result.failure(e))
        }
    }

    override suspend fun deleteChatHistory(chatId: String):Flow<Result<Unit>> = flow {
        try {
            Log.d("ChatLocalDataSource", "deleteChatHistory: chatId = $chatId")
            realm.write {
                val chatHistoryToDelete = query<ChatHistoryEntity>("chatId == $0", chatId).first().find()
                if (chatHistoryToDelete != null) {
                    delete(chatHistoryToDelete)
                    Log.d("ChatLocalDataSource", "deleteChatHistory: Deleted chat history with id $chatId")
                } else {
                    throw IllegalArgumentException("Chat history with id $chatId not found.")
                }
            }
            emit(Result.success(Unit))
        } catch (e: Exception) {
            Log.e(TAG, "deleteChatHistory: Error deleting chat history", e)
            e.printStackTrace()
            emit(Result.failure(e))
        }
    }

    override suspend fun getChatHistories(): Result<List<ChatHistory>> {
        return try {
            val histories = realm.query<ChatHistoryEntity>()
                .find()
                .map { it.toChatHistory() }
            Log.d("ChatLocalDataSource", "getChatHistories: $histories")
            Result.success(histories)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }
}