package com.zaed.chatbot.ui.mainchat

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainChatViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(MainChatUiState())
    val uiState = _uiState.asStateFlow()

    fun handleAction(action: MainChatUiAction){
        when(action){
            else -> {}
        }
    }

}