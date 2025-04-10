package com.zaed.chatbot.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaed.chatbot.data.repository.SettingsRepository
import com.zaed.chatbot.ui.mainchat.components.ChatModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepo: SettingsRepository
):ViewModel() {
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState = _uiState.asStateFlow()
    fun handleAction(action: SettingsUiAction) {
        when(action){
            is SettingsUiAction.OnSetDefaultChatMode -> setDefaultMode(action.chatModel)
            is SettingsUiAction.OnSetFontScale -> setFontScale(action.fontScale)
            else -> Unit
        }
    }


    private fun setFontScale(fontScale: Float) {
        viewModelScope.launch {
            settingsRepo.setDefaultFontScale(fontScale)
        }
    }

    private fun setDefaultMode(chatModel: ChatModel) {
        viewModelScope.launch {
            _uiState.value = uiState.value.copy(chatMode = chatModel)
            settingsRepo.setDefaultChatMode(chatModel.name)
        }
    }
}