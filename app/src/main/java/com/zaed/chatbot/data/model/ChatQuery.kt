package com.zaed.chatbot.data.model

import kotlinx.datetime.Clock

data class ChatQuery(
    val chatId: String = "",
    val createdAtEpochSeconds: Long = Clock.System.now().epochSeconds,
    val prompt: String = "",
    val response: String = "",
    val isLoading: Boolean = false,
    val promptAttachments: List<MessageAttachment> = emptyList(),
    val responseAttachments: List<MessageAttachment> = emptyList()
)
