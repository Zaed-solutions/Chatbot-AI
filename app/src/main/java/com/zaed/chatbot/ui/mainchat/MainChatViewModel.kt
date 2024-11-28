package com.zaed.chatbot.ui.mainchat

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aallam.openai.api.model.ModelId
import com.zaed.chatbot.data.model.ChatQuery
import com.zaed.chatbot.data.model.MessageAttachment
import com.zaed.chatbot.data.repository.ChatRepository
import com.zaed.chatbot.ui.mainchat.components.ChatModel
import com.zaed.chatbot.ui.util.ConnectivityObserver
import com.zaed.chatbot.ui.util.toMessageAttachments
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class MainChatViewModel(
    private val chatRepository: ChatRepository,
    private val connectivityObserver: ConnectivityObserver
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
    val isConnected = connectivityObserver
        .isConnected
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            false
        )
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
            MainChatUiAction.OnCancelSubscription -> cancelSubscription()
            is MainChatUiAction.OnChangeModel -> changeChatModel(action.model)
            is MainChatUiAction.OnDeleteAttachment -> deleteAttachment(action.attachmentUri)
            MainChatUiAction.OnNewChatClicked -> clearChat()
            MainChatUiAction.OnRestoreSubscription -> restoreSubscription()
            MainChatUiAction.OnSendPrompt -> sendPrompt()
            is MainChatUiAction.OnSendSuggestion -> sendSuggestion(action.suggestionPrompt)
            is MainChatUiAction.OnUpdatePrompt -> updatePrompt(action.text)
            is MainChatUiAction.OnUpgradeSubscription -> upgradeSubscription(
                action.isFreeTrialEnabled,
                action.isLifetime
            )

            is MainChatUiAction.OnStopAnimation -> {
                Log.d("MainChatViewModel", "handleAction: stop animation")
                stopAnimation()
            }

            else -> Unit
        }
    }

    private fun listModels() {
        viewModelScope.launch(Dispatchers.IO) {
            val models = chatRepository.listModels()
            Log.d("MainChatViewModel", "listModels: $models")
        }
    }

    private fun stopAnimation() {
        viewModelScope.launch {
            Log.d("MainChatViewModel1", "${uiState.value.queries}")
            val updatedQueries = _uiState.value.queries
            if (updatedQueries.isNotEmpty()) {
                updatedQueries[0] = updatedQueries[0].copy(animateResponse = false)
            }
            _uiState.update { currentState ->
                currentState.copy(queries = updatedQueries, isAnimating = false)
            }
            Log.d("MainChatViewModel2", "${uiState.value.queries}")
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
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update {
                it.copy(currentPrompt = prompt)
            }
            sendPrompt()
        }
    }

    private fun createImages() {
        viewModelScope.launch(Dispatchers.IO) {
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
                        isLoading = true,
                        imageHitTimes = oldState.imageHitTimes.plus(1),
                        totalHitTimes = oldState.totalHitTimes.plus(1)
                    )
                }
                val result = chatRepository.createImage(
                    prompt = query.prompt,
                    n = 1,
                    size = com.aallam.openai.api.image.ImageSize.is256x256
                )
                _uiState.update { oldState ->
                    oldState.copy(
                        queries = oldState.queries.map {
                            if (it.isLoading) it.copy(
                                isLoading = false,
                                response = "",
                                animateResponse = false,
                                responseAttachments = result.toMessageAttachments()
                            ) else it.copy(isLoading = false, animateResponse = false)
                        }.toMutableList(), isLoading = false, isAnimating = true
                    )

                }

            }


        }
    }

    private fun sendPrompt() {
        when (uiState.value.selectedModel) {
            ChatModel.GPT_4O_MINI -> createText(ChatModel.GPT_4O_MINI.modelId)
            ChatModel.GPT_4O -> createText(ChatModel.GPT_4O.modelId)
            ChatModel.AI_ART_GENERATOR -> createImages()
        }

    }

    private fun createText(modelId: ModelId) {
        val isFirstMessage = uiState.value.queries.isEmpty()
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
                    isLoading = true,
                    textHitTimes = oldState.textHitTimes.plus(1),
                    totalHitTimes = oldState.totalHitTimes.plus(1)
                )
            }

            chatRepository.sendPrompt(query, modelId,isFirstMessage)
                .collect { result ->
                    _uiState.update { oldState ->
                        oldState.copy(
                            queries = oldState.queries.map {
                                if (it.isLoading) it.copy(
                                    isLoading = false,
                                    response = result.choices.first().message.content.orEmpty(),
                                    animateResponse = true,
//                                    responseAttachments = data.responseAttachments
                                ) else it.copy(isLoading = false, animateResponse = false)
                            }.toMutableList(), isLoading = false, isAnimating = true
                        )

                    }

                }
        }
    }

    private fun restoreSubscription() {
//        TODO("Not yet implemented")
    }

    private fun clearChat(){
        viewModelScope.launch {
            _uiState.update {
                MainChatUiState(chatId = UUID.randomUUID().toString())
            }
        }
    }

    private fun changeChatModel(model: ChatModel) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(selectedModel = model)
            }
            Log.d("MainChatViewModel", "changeChatModel: $model")
        }
    }

    private fun cancelSubscription() {
//        TODO("Not yet implemented")
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