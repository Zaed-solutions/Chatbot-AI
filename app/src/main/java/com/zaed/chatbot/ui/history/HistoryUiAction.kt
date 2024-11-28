package com.zaed.chatbot.ui.history

sealed interface HistoryUiAction {
    data object OnBackClicked : HistoryUiAction
    data class OnChatHistoryClicked(val chatId: String) : HistoryUiAction
    data class OnChatHistoryRenamed(val chatId: String, val newTitle: String) : HistoryUiAction
    data class OnDeleteChatHistoryConfirmed(val chatId: String) : HistoryUiAction

}