package com.zaed.chatbot.data.model

import kotlinx.datetime.Clock

data class ChatHistory(
    val chatId: String = "",
    val lastResponse: String = "",
    val lastResponseTime: Long = Clock.System.now().epochSeconds,
    val title: String = "",
)

