package com.zaed.chatbot.ui.activity

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.queryProductDetails
import com.zaed.chatbot.app.navigation.NavigationHost
import com.zaed.chatbot.ui.theme.ChatbotTheme
import com.zaed.chatbot.ui.theme.LocalFontScale
import com.zaed.chatbot.ui.util.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity(), BillingClientStateListener {
    companion object {
        val TAG = "MainActivity"
    }

    private val purchasesUpdatedListener =
        PurchasesUpdatedListener { billingResult, purchases ->
            Log.d(TAG, "Purchases Updated Listener: result: $billingResult, purchases: $purchases ")
        }
    private lateinit var billingClient: BillingClient
    private val viewModel: MainViewModel by inject<MainViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        billingClient = BillingClient.newBuilder(this)
            .setListener(purchasesUpdatedListener)
            .build()
        establishGoogleBillingConnection()
        enableEdgeToEdge()
        viewModel.init(onInitialized = {
            setContent {
                ChatbotTheme {
                    App()
                }
            }
        })
    }

    private fun establishGoogleBillingConnection() {
        billingClient.startConnection(this)
    }

    @Composable
    fun App() {
        val state by viewModel.uiState.collectAsStateWithLifecycle()
        var fontScale by remember { mutableFloatStateOf(state.fontScale) }
        CompositionLocalProvider(LocalFontScale provides fontScale) {
            ChatbotTheme {
                val navController = rememberNavController()
                NavigationHost(modifier = Modifier
                    .systemBarsPadding()
                    .imePadding(),
                    fontScale = fontScale,
                    defaultChatMode = state.chatMode,
                    onFontScaleChanged = {
                        fontScale = it
                        viewModel.handleAction(MainAction.OnSetFontScale(it))
                    },
                    navController = navController,
                    onDefaultChatModeChanged = {
                        viewModel.handleAction(MainAction.OnSetDefaultChatMode(it))
                    },
                    onSubscriptionAction = { action ->
                        when (action) {
                            SubscriptionAction.CancelSubscription -> cancelSubscription()
                            is SubscriptionAction.OnApplyPromoCode -> applyPromoCode(action.promoCode)
                            SubscriptionAction.RestoreSubscription -> restoreSubscription()
                            is SubscriptionAction.UpgradeSubscription -> upgradeSubscription(
                                action.freeTrialEnabled,
                                action.isLifeTime
                            )
                        }
                    }
                )
            }
        }
    }

    private fun upgradeSubscription(freeTrialEnabled: Boolean, lifeTime: Boolean) {
        TODO("Not yet implemented")
    }

    private fun restoreSubscription() {
        TODO("Not yet implemented")
    }

    private fun applyPromoCode(promoCode: String) {
        TODO("Not yet implemented")
    }

    private fun cancelSubscription() {
        TODO("Not yet implemented")
    }

    override fun onBillingServiceDisconnected() {
        establishGoogleBillingConnection()
    }

    override fun onBillingSetupFinished(billingResult: BillingResult) {
        val responseCode = billingResult.responseCode
        if (responseCode == BillingClient.BillingResponseCode.OK) {
            Log.d(TAG, "Billing client connected")
            lifecycleScope.launch {
                querySubscriptions()
            }
        } else {
            Log.d(TAG, "Billing client connection failed")
        }
    }

    private suspend fun querySubscriptions() {
        val queryProductDetailsParams = QueryProductDetailsParams
            .newBuilder()
            .setProductList(
                listOf(
                    QueryProductDetailsParams.Product
                        .newBuilder()
                        .setProductId(Constants.WEEKLY_SUBSCRIPTION_ID)
                        .setProductType(BillingClient.ProductType.SUBS)
                        .build(),
                    QueryProductDetailsParams.Product
                        .newBuilder()
                        .setProductId(Constants.LIFETIME_SUBSCRIPTION_ID)
                        .setProductType(BillingClient.ProductType.SUBS)
                        .build(),
                )
            ).build()
        val productDetailsResult = withContext(Dispatchers.IO) {
            billingClient.queryProductDetails(queryProductDetailsParams)
        }
        val responseCode = productDetailsResult.billingResult.responseCode
        when (responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                Log.d(TAG, "Query product details success")
                if (productDetailsResult.productDetailsList?.isNotEmpty() == true) {
                    viewModel.handleAction(
                        MainAction.OnUpdateProductsList(
                            products = productDetailsResult.productDetailsList ?: emptyList()
                        )
                    )
                } else {
                    Log.d(TAG, "Product details list is empty")
                }
            }

            else -> {
                Log.d(TAG, "Query product details failed: ${productDetailsResult.billingResult.debugMessage}")
                    }
                )
            }
        }
    }
}
