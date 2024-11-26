package com.zaed.chatbot.ui.history

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaed.chatbot.data.repository.ChatRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HistoryViewModel(
    private val chatRepo: ChatRepository
): ViewModel() {
    private val TAG = "HistoryViewModel"
    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState = _uiState.asStateFlow()
    init {
        fetchChatHistories()
    }

    private fun fetchChatHistories() {
        viewModelScope.launch (Dispatchers.IO) {
            chatRepo.getChatHistories().collect { result ->
                result.onSuccess { histories ->
                    _uiState.value = uiState.value.copy(histories = histories)
                }.onFailure { e ->
                    Log.e(TAG, "fetchChatHistories error: $${e.localizedMessage}", )
                    e.printStackTrace()
                }
            }
        }
    }

    fun handleAction(action: HistoryUiAction) {
        when(action){
            is HistoryUiAction.OnChatHistoryRenamed -> renameHistory(action.chatId, action.newTitle)
            is HistoryUiAction.OnDeleteChatHistoryConfirmed -> deleteHistory(action.chatId)
            else -> Unit
        }
    }

    private fun deleteHistory(chatId: String) {
        viewModelScope.launch (Dispatchers.IO) {
            chatRepo.deleteChatHistory(chatId)
        }
    }

    private fun renameHistory(chatId: String, newTitle: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val newHistory = uiState.value.histories.find { it.chatId == chatId }?.copy(title = newTitle)
            newHistory?.let {
                chatRepo.updateChatHistory(it)
            }
        }
    }
}