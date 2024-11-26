package com.zaed.chatbot.ui.history

import com.zaed.chatbot.data.model.ChatHistory

data class HistoryUiState(
    val histories: List<ChatHistory> = emptyList()
)
