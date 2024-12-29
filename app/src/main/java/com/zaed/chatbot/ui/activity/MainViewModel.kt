package com.zaed.chatbot.ui.activity

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.zaed.chatbot.data.repository.SettingsRepository
import com.zaed.chatbot.ui.mainchat.components.ChatModel
import com.zaed.chatbot.ui.util.ConnectivityObserver
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

class MainViewModel(
    private val settingsRepo: SettingsRepository,
    private val connectivityObserver: ConnectivityObserver
) : ViewModel() {
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState = _uiState.asStateFlow()

    fun init(androidId: String, onInitialized: () -> Unit) {
        Log.d(TAG, "init")
        initializePreferences(onInitialized)
        _uiState.update { it.copy(androidId = androidId) }
        getImageFreeTrialCount(null)
        checkInternetConnection()
    }

    private fun checkInternetConnection() {
        viewModelScope.launch {
            connectivityObserver.isConnected
                .collect { result ->
                    _uiState.update {
                        it.copy(isConnected = result)
                    }
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
            is MainAction.OnUpdateSubscribedPlan -> updateSubscribedPlan(action.plan)
        }
    }

    private fun updateSubscribedPlan(plan: Purchase?) {
        when(plan!=null){
            true -> {
                viewModelScope.launch {
                    Log.d(TAG, "updateSubscribedPlan: identifier${plan.accountIdentifiers}")
                    Log.d(TAG, "updateSubscribedPlan: orderId${plan.orderId}")
                    Log.d(TAG, "updateSubscribedPlan: purchaseToken${plan.purchaseToken}")
                    _uiState.update { oldState ->
                        oldState.copy(
                            subscribedPlan = oldState.products.find { it.productId == plan.products.first() },
                            isPro = true,
                        )
                    }
                    getImageFreeTrialCount(plan)
                    Log.d(TAG, "updateSubscribedPlan: ${uiState.value.isPro}")
                }
            }
            false -> getImageFreeTrialCount(null)
        }

    }

    private fun getImageFreeTrialCount(product: Purchase?) {
        viewModelScope.launch {
            settingsRepo.getUserFreeTrialAndImageLimit(uiState.value.androidId, product)
                .collect { (freeTrialCount, imageFreeTrialCount) ->
                    Log.d("MainViewModel", "getImageFreeTrialCount: $imageFreeTrialCount")
                    _uiState.update {
                        it.copy(
                            imageFreeTrialCount = imageFreeTrialCount,
                            freeTrialCount = freeTrialCount
                        )
                    }
                }
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
    data class OnUpdateSubscribedPlan(val plan: Purchase?) : MainAction
}

data class MainUiState(
    val isConnected: Boolean = false,
    val fontScale: Float = 1f,
    val chatMode: ChatModel = ChatModel.GPT_4O_MINI,
    val products: List<ProductDetails> = emptyList(),
    val subscribedPlan: ProductDetails? = null,
    val isPro: Boolean = false,
    val androidId: String = "",
    val freeTrialCount: Int = 0,
    val imageFreeTrialCount: Int = 0,
)
@Serializable
data class CurrentUserPurchase(
    val productId: String = "",
    val purchaseToken: String = "",
    val purchaseTime: Long = 0L,
    val title: String = "",
    val imageLimit: Int = 0,
    val freeTrialCount: Int = 0
)