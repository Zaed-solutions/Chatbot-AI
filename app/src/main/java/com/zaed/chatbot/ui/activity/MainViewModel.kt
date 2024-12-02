package com.zaed.chatbot.ui.activity

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.billingclient.api.ProductDetails
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

    fun init(androidId: String, onInitialized: () -> Unit) {
        Log.d(TAG, "init")
        initializePreferences(onInitialized)
        _uiState.update { it.copy(androidId = androidId) }
        getFreeTrialCount()
    }

    private fun getFreeTrialCount() {
        viewModelScope.launch {
            settingsRepo.getUserFreeTrialCount(uiState.value.androidId).collect {result->
                Log.d("MainViewModel", "getFreeTrialCount: $result")
                _uiState.update { it.copy(freeTrialCount = result) }
            }
        }
    }

    fun decrementFreeTrialCount() {
        viewModelScope.launch {
            settingsRepo.incrementUserFreeTrialCount(uiState.value.androidId)
        }
    }

    private fun initializePreferences(onInitialized: () -> Unit) {
        viewModelScope.launch {
            Log.d("MainViewModel", "initializePreferences: ")
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

    private val TAG = "MainActivity"
    fun handleAction(action: MainAction) {
        Log.d(TAG, "handleAction: $action")
        when (action) {
            is MainAction.OnSetDefaultChatMode -> setDefaultMode(action.chatModel)
            is MainAction.OnSetFontScale -> setFontScale(action.fontScale)
            is MainAction.OnUpdateProductsList -> updateProductsList(action.products)
            is MainAction.OnUpdateSubscribedPlan -> updateSubscribedPlan(action.planId)
        }
    }

    private fun updateSubscribedPlan(planId: String) {
        viewModelScope.launch {
            Log.d(TAG, "updateSubscribedPlan: $planId")
            _uiState.update {oldState->
                oldState.copy(
                    subscribedPlan = oldState.products.find { it.productId == planId }, isPro = true
                )
            }
            Log.d(TAG, "updateSubscribedPlan: ${uiState.value.isPro}")
        }
    }

    private fun updateProductsList(products: List<ProductDetails>) {
        viewModelScope.launch {
            Log.d("MainViewModel", "updateProductsList: $products")
            _uiState.update { it.copy(products = products) }
        }
    }


    private fun setFontScale(fontScale: Float) {
        viewModelScope.launch {
            Log.d(TAG, "setFontScale: $fontScale")
            settingsRepo.setDefaultFontScale(fontScale)
        }
    }

    private fun setDefaultMode(chatModel: ChatModel) {
        viewModelScope.launch {
            Log.d(TAG, "setDefaultMode: $chatModel")
            _uiState.value = uiState.value.copy(chatMode = chatModel)
            settingsRepo.setDefaultChatMode(chatModel.name)
        }
    }
}

sealed interface MainAction {
    data class OnSetFontScale(val fontScale: Float) : MainAction
    data class OnSetDefaultChatMode(val chatModel: ChatModel) : MainAction
    data class OnUpdateProductsList(val products: List<ProductDetails>) : MainAction
    data class OnUpdateSubscribedPlan(val planId: String) : MainAction
}

data class MainUiState(
    val fontScale: Float = 1f,
    val chatMode: ChatModel = ChatModel.GPT_4O_MINI,
    val products: List<ProductDetails> = emptyList(),
    val subscribedPlan: ProductDetails? = null,
    val isPro: Boolean = false,
    val androidId: String = "",
    val freeTrialCount: Int = 5

)