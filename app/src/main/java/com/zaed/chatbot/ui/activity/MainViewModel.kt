package com.zaed.chatbot.ui.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaed.chatbot.data.repository.SettingsRepository
import com.zaed.chatbot.ui.mainchat.components.ChatModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(
    private val settingsRepo: SettingsRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState = _uiState.asStateFlow()

    fun init(onInitialized: () -> Unit) {
        initializePreferences(onInitialized)
    }

    private fun initializePreferences(onInitialized: () -> Unit) {
        viewModelScope.launch {
            val chatMode = settingsRepo.getChatMode()
            val fontScale = settingsRepo.getFontScale()
            _uiState.update {
                it.copy(
                    fontScale = fontScale, chatMode = ChatModel.valueOf(chatMode)
                )
            }
            onInitialized()
        }
    }

    fun handleAction(action: MainAction) {
        when (action) {
            is MainAction.OnSetDefaultChatMode -> setDefaultMode(action.chatModel)
            is MainAction.OnSetFontScale -> setFontScale(action.fontScale)
            is MainAction.OnSubmitPromoCode -> submitPromoCode(action.promoCode)
            else -> Unit
        }
    }

    private fun submitPromoCode(promoCode: String) {
//        TODO("Not yet implemented")
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

sealed interface MainAction {
    data class OnSubmitPromoCode(val promoCode: String) : MainAction
    data class OnSetFontScale(val fontScale: Float) : MainAction
    data class OnSetDefaultChatMode(val chatModel: ChatModel) : MainAction

}

data class MainUiState(
    val fontScale: Float = 1f,
    val chatMode: ChatModel = ChatModel.GPT_4O_MINI,
)