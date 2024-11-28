package com.zaed.chatbot.ui.activity

import android.os.Bundle
import android.util.Log
import android.widget.Toast
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
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchaseHistoryParams
import com.android.billingclient.api.QueryPurchasesParams
import com.android.billingclient.api.queryProductDetails
import com.android.billingclient.api.queryPurchaseHistory
import com.android.billingclient.api.queryPurchasesAsync
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
            val responseCode = billingResult.responseCode
            when(responseCode){
                BillingClient.BillingResponseCode.OK -> {
                    Log.d(TAG, "Query purchases success")
                    if(purchases?.isNotEmpty() == true){
                        purchases.forEach{ purchase ->
                            Log.d(TAG, "Purchase: $purchase")
                            lifecycleScope.launch {
                                acknowledgePurchase(purchase)
                            }
                        }
                    }
                }
                else -> {
                    Log.d(TAG, "Query purchases failed: ${billingResult.debugMessage}")
                }
            }
        }
    private lateinit var billingClient: BillingClient
    private val viewModel: MainViewModel by inject<MainViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        billingClient = BillingClient.newBuilder(this)
            .setListener(purchasesUpdatedListener)
            .enablePendingPurchases()
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
                    },
                    isPro = state.isPro
                )
            }
        }
    }

    private fun upgradeSubscription(freeTrialEnabled: Boolean, lifeTime: Boolean) {
        lifecycleScope.launch {
            try {
                val productDetails = billingClient.queryProductDetails(
                    QueryProductDetailsParams.newBuilder()
                        .setProductList(
                            listOf(
                                QueryProductDetailsParams.Product.newBuilder()
                                    .setProductId(
                                        if (lifeTime) Constants.LIFETIME_SUBSCRIPTION_ID
                                        else Constants.WEEKLY_SUBSCRIPTION_ID
                                    )
                                    .setProductType(BillingClient.ProductType.SUBS)
                                    .build()
                            )
                        )
                        .build()
                ).productDetailsList?.firstOrNull()
                if (productDetails != null) {
                    launchBillingFlow(productDetails)
                } else {
                    Log.d(TAG, "Product details is null")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to upgrade subscription: ${e.message}")
            }
        }
    }

    private fun restoreSubscription() {
        lifecycleScope.launch {
            try {
                queryUserHistory()
                Log.d(TAG, "Subscription restored successfully")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to restore subscription: ${e.message}")
            }
        }
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
                queryPurchases()
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
                Log.d(
                    TAG,
                    "Query product details failed: ${productDetailsResult.billingResult.debugMessage}"
                )
            }
        }
    }
    private suspend fun queryPurchases() {
        if(billingClient.isReady == false){
            Log.e(TAG, "Billing client is not ready")
            return
        }
        val queryPurchasesParams = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.SUBS)
            .build()
        billingClient.queryPurchasesAsync(queryPurchasesParams) { billingResult, productDetailsList ->
            Log.d(TAG, "Query purchases result: $billingResult, purchases: $productDetailsList")
            val responseCode = billingResult.responseCode
            when(responseCode){
                BillingClient.BillingResponseCode.OK -> {
                    Log.d(TAG, "Query purchases success")
                    if(productDetailsList.isNotEmpty()){
                        productDetailsList.forEach{ purchase ->
                            Log.d(TAG, "Purchase: $purchase")
                            lifecycleScope.launch {
                                acknowledgePurchase(purchase)
                            }
                        }
                    }
                }
                else -> {
                    Log.d(TAG, "Query purchases failed: ${billingResult.debugMessage}")
                }
            }
        }
    }
    private suspend fun acknowledgePurchase(purchase: Purchase){
        if(purchase.purchaseState == Purchase.PurchaseState.PURCHASED){
            if(!purchase.isAcknowledged){
                val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                withContext(Dispatchers.IO){
                    billingClient.acknowledgePurchase(acknowledgePurchaseParams.build()) { billingResult ->
                        val responseCode = billingResult.responseCode
                        when(responseCode){
                            BillingClient.BillingResponseCode.OK -> {
                                //TODO: optional save purchase to your backend
                                Toast.makeText(this@MainActivity, "You are now subscribed to the app!", Toast.LENGTH_SHORT).show()
                                viewModel.handleAction(MainAction.OnUpdateSubscribedPlan(purchase.products.first()))
                                Log.d(TAG, "Purchase acknowledged")
                            }
                            else -> {
                                Log.d(TAG, "Purchase acknowledge failed: ${billingResult.debugMessage}")
                            }
                        }
                    }
                }
            } else {
                //TODO: optional save purchase to your backend
            }
        }
    }
    private suspend fun queryUserHistory(){
        withContext(Dispatchers.IO){
            val params = QueryPurchaseHistoryParams.newBuilder()
                .setProductType(BillingClient.ProductType.SUBS)
            val purchaseHistoryResult = billingClient.queryPurchaseHistory(params.build())
            val responseCode = purchaseHistoryResult.billingResult.responseCode
            if(responseCode == BillingClient.BillingResponseCode.OK) {
                val historyList = purchaseHistoryResult.purchaseHistoryRecordList
                if(historyList?.isNotEmpty() == true){
                    historyList.forEach { history ->
                        Log.d(TAG, "Purchase history: $history")
                        history.products.forEach { product ->
                            Log.d(TAG, "Product: $product")
                        }
                    }
                } else {
                    Log.d(TAG, "Purchase history list is empty")
                }
            } else {
                Log.d(TAG, "Query purchase history failed: ${purchaseHistoryResult.billingResult.debugMessage}")
            }
        }
    }

    private suspend fun launchBillingFlow(productDetails: ProductDetails){
        val productDetailsParamsList = listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails)
                .setOfferToken(productDetails.subscriptionOfferDetails?.get(0)?.offerToken.toString())
                .build()
        )
        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build()
        if(billingClient.isReady == false){
            Log.e(TAG, "Billing client is not ready")
        }
        val billingResult = billingClient.launchBillingFlow(this, billingFlowParams)
        val responseCode = billingResult.responseCode
        when(responseCode){
            BillingClient.BillingResponseCode.OK -> {
                Log.d(TAG, "Billing flow launched")
                //TODO: Handle successful purchase
            }
            else -> {
                Log.d(TAG, "Billing flow launch failed: ${billingResult.debugMessage}")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if(billingClient.isReady){
            billingClient.endConnection()
        }
    }
}

