package com.zaed.chatbot.data.source.local.model

import com.zaed.chatbot.data.model.ChatHistory
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import kotlinx.datetime.Clock

class ChatHistoryEntity: RealmObject{
    var chatId: String = ""
    var lastResponse: String = ""
    var lastResponseTime: Long = Clock.System.now().epochSeconds
    var title: String = ""
}

fun ChatHistoryEntity.toChatHistory() =
    ChatHistory(
        chatId = chatId,
        lastResponse = lastResponse,
        lastResponseTime = lastResponseTime,
        title = title
    )

fun toChatHistoryEntity(chatHistory: ChatHistory) = ChatHistoryEntity().apply {
    chatId = chatHistory.chatId
    lastResponse = chatHistory.lastResponse
    lastResponseTime = chatHistory.lastResponseTime
    title = chatHistory.title
}
