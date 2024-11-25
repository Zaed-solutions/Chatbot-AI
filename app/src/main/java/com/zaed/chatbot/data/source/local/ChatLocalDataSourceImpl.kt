package com.zaed.chatbot.data.source.local

import android.util.Log
import com.zaed.chatbot.data.model.ChatQuery
import com.zaed.chatbot.data.model.toChatQueryEntity
import com.zaed.chatbot.data.source.local.model.ChatQueryEntity
import com.zaed.chatbot.data.source.local.model.toChatQuery
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ChatLocalDataSourceImpl(val realm: Realm) : ChatLocalDataSource {
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
            // Query Realm for ChatRealmObject by chatId
            val chats = realm.query<ChatQueryEntity>("chatId == $0", chatId)
                .find()
                .map { it.toChatQuery() } // Convert to ChatQuery objects
            Log.d("ChatLocalDataSource", "getChatById: $chats")
            emit(Result.success(chats)) // Emit the result
        } catch (e: Exception) {
            emit(Result.failure(e)) // Emit failure if an error occurs
        }
    }
}