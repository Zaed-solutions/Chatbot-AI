package com.zaed.chatbot.ui.mainchat

import com.zaed.chatbot.data.model.ChatQuery
import com.zaed.chatbot.data.model.MessageAttachment
import com.zaed.chatbot.ui.mainchat.components.ChatModel

data class MainChatUiState(
    val queries: MutableList<ChatQuery> = mutableListOf(),
    val chatId: String = "",
    val isPro: Boolean = false,
    val isLoading : Boolean = false,
    val currentPrompt: String = "",
    val monthlyCost: Double = 0.0,
    val lifetimeCost: Double = 0.0,
    val selectedModel: ChatModel = ChatModel.GPT_4O_MINI,
    val attachments: List<MessageAttachment> = emptyList(),
)
