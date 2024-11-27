package com.zaed.chatbot.ui.mainchat

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaed.chatbot.data.model.ChatQuery
import com.zaed.chatbot.data.model.MessageAttachment
import com.zaed.chatbot.data.repository.ChatRepository
import com.zaed.chatbot.ui.mainchat.components.ChatModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class MainChatViewModel(
    private val chatRepository: ChatRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(MainChatUiState())
    val uiState = _uiState.asStateFlow()
    fun init(chatId: String) {
        if (chatId.isNotBlank()) {
            fetchChat(chatId)
        } else {
            _uiState.update {
                it.copy(
                    chatId = UUID.randomUUID().toString(),
                )
            }
        }
    }

    private fun fetchChat(chatId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            chatRepository.getChatById(chatId).collect { result ->
                result.onSuccess {
                    Log.d("MainChatViewModel", "fetchChat: $it")
                    _uiState.update { oldState ->
                        oldState.copy(
                            queries = it.reversed().toMutableList()
                        )
                    }
                }.onFailure {
                    Log.e("MainChatViewModel", "fetchChat: ${it.message}")
                }
            }
        }
    }

    fun handleAction(action: MainChatUiAction) {
        when (action) {
            is MainChatUiAction.OnAddAttachment -> addAttachment(action.attachment)
            is MainChatUiAction.OnChangeModel -> changeChatModel(action.model)
            is MainChatUiAction.OnDeleteAttachment -> deleteAttachment(action.attachmentUri)
            MainChatUiAction.OnNewChatClicked -> clearChat()
            MainChatUiAction.OnSendPrompt -> sendPrompt()
            is MainChatUiAction.OnSendSuggestion -> sendSuggestion(action.suggestionPrompt)
            is MainChatUiAction.OnUpdatePrompt -> updatePrompt(action.text)
            is MainChatUiAction.OnStopAnimation -> stopAnimation()
            else -> Unit
        }
    }
    private fun stopAnimation() {
        viewModelScope.launch {
            _uiState.update { currentState ->
                val updatedQueries = currentState.queries
                if (updatedQueries.isNotEmpty()) {
                    updatedQueries[0] = updatedQueries[0].copy(animateResponse = false)
                }
                currentState.copy(queries = updatedQueries)
            }
        }
    }
    private fun updatePrompt(text: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(currentPrompt = text)
            }
        }
    }

    private fun sendSuggestion(prompt: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update {
                it.copy(currentPrompt = prompt)
            }
            sendPrompt()
        }
    }

    private fun sendPrompt() {
        viewModelScope.launch(Dispatchers.IO) {
            val query = ChatQuery(
                chatId = uiState.value.chatId,
                prompt = uiState.value.currentPrompt,
                response = "",
                promptAttachments = uiState.value.attachments,
                isLoading = true,
                animateResponse = true
            )
            _uiState.update { oldState ->
                oldState.queries.add(0, query)
                oldState.copy(
                    currentPrompt = "",
                    attachments = mutableListOf(),
                    isLoading = true
                )
            }
            chatRepository.sendPrompt(query).collect { result ->
                result.onSuccess { data ->
                    _uiState.update { oldState ->
                        oldState.copy(
                            queries = oldState.queries.map {
                                if (it.isLoading) it.copy(
                                    isLoading = false,
                                    response = data.response,
                                    animateResponse = true,
                                    responseAttachments = data.responseAttachments
                                ) else it.copy(isLoading = false, animateResponse = false)
                            }.toMutableList(), isLoading = false
                        )
                    }
                }
            }
        }
    }

    private fun clearChat() {
//        TODO("Not yet implemented")
    }

    private fun changeChatModel(model: ChatModel) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(selectedModel = model)
            }
        }
    }

    private fun addAttachment(attachment: MessageAttachment) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(attachments = it.attachments + attachment)
            }
        }
    }

    private fun deleteAttachment(uri: Uri) {
        viewModelScope.launch {
            _uiState.update { oldState ->
                oldState.copy(attachments = oldState.attachments.filter { it.uri != uri })
            }
        }
    }
}