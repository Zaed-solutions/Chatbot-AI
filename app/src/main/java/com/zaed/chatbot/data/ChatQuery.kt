package com.zaed.chatbot.data

data class ChatQuery(
    val prompt: String = "",
    val response: String = "",
    val isLoading: Boolean = false,
    val promptAttachments: List<MessageAttachment> = emptyList(),
    val responseAttachments: List<MessageAttachment> = emptyList()
)
