package com.zaed.chatbot.ui.mainchat

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaed.chatbot.data.model.MessageAttachment
import com.zaed.chatbot.ui.mainchat.components.ChatModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainChatViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(MainChatUiState())
    val uiState = _uiState.asStateFlow()
    fun init(chatId: String){
        if(chatId.isNotBlank()){
            //todo: fetch old chat
        } else {
            //todo generate new id and set it in state
        }
    }
    fun handleAction(action: MainChatUiAction){
        when(action){
            is MainChatUiAction.OnAddAttachment -> addAttachment(action.attachment)
            MainChatUiAction.OnCancelSubscription -> cancelSubscription()
            is MainChatUiAction.OnChangeModel -> changeChatModel(action.model)
            is MainChatUiAction.OnDeleteAttachment -> deleteAttachment(action.attachmentUri)
            MainChatUiAction.OnNewChatClicked -> clearChat()
            MainChatUiAction.OnRestoreSubscription -> restoreSubscription()
            MainChatUiAction.OnSendPrompt -> sendPrompt()
            is MainChatUiAction.OnSendSuggestion -> sendSuggestion(action.suggestionPrompt)
            is MainChatUiAction.OnUpdatePrompt -> updatePrompt(action.text)
            is MainChatUiAction.OnUpgradeSubscription -> upgradeSubscription(action.isFreeTrialEnabled, action.isLifetime)
            else -> Unit
        }
    }

    private fun upgradeSubscription(freeTrialEnabled: Boolean, lifetime: Boolean) {
//        TODO("Not yet implemented")
    }

    private fun updatePrompt(text: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(currentPrompt = text)
            }
        }
    }

    private fun sendSuggestion(prompt: String) {
//        TODO("Not yet implemented")
    }

    private fun sendPrompt() {
//        TODO("Not yet implemented")
    }

    private fun restoreSubscription() {
//        TODO("Not yet implemented")
    }

    private fun clearChat() {
//        TODO("Not yet implemented")
    }

    private fun changeChatModel(model: ChatModel){
        viewModelScope.launch {
            _uiState.update {
                it.copy(selectedModel = model)
            }
        }
    }
    private fun cancelSubscription() {
//        TODO("Not yet implemented")
    }

    private fun addAttachment(attachment: MessageAttachment){
        viewModelScope.launch {
            _uiState.update {
                it.copy(attachments = it.attachments + attachment)
            }
        }
    }
    private fun deleteAttachment(uri: Uri){
        viewModelScope.launch {
            _uiState.update { oldState ->
                oldState.copy(attachments = oldState.attachments.filter { it.uri != uri })
            }
        }
    }
}