package com.zaed.chatbot.ui.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
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
import com.google.android.play.core.review.ReviewManagerFactory
import com.zaed.chatbot.app.navigation.NavigationHost
import com.zaed.chatbot.ui.components.NoInternetScreen
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
            Log.d(TAG, "Purchases Updated Listener: response code: $responseCode")
            when(responseCode){
                BillingClient.BillingResponseCode.OK -> {
                    Log.d(TAG, "Query purchases success")
                    if(purchases?.isNotEmpty() == true){
                        purchases.forEach{ purchase ->
                            Log.d(TAG, "Purchase: ${purchase.purchaseState}")
                            lifecycleScope.launch {
                                acknowledgePurchase(purchase)
                            }
                        }
                    }
                }
                BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED->{
                    Log.d(TAG, "Item already owned")
                }
                BillingClient.BillingResponseCode.USER_CANCELED->{
                    Log.d(TAG, "User canceled")
                }
                BillingClient.BillingResponseCode.SERVICE_DISCONNECTED->{
                    Log.d(TAG, "Service disconnected")
                }
                BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE->{
                    Log.d(TAG, "Service unavailable")
                }
                BillingClient.BillingResponseCode.BILLING_UNAVAILABLE->{
                    Log.d(TAG, "Billing unavailable")
                }
                BillingClient.BillingResponseCode.DEVELOPER_ERROR-> {
                    Log.d(TAG, "Developer error")
                }
                BillingClient.BillingResponseCode.ERROR->{
                    Log.d(TAG, "Error")
                }
                BillingClient.BillingResponseCode.ITEM_UNAVAILABLE->{
                    Log.d(TAG, "Item unavailable")
                }
                BillingClient.BillingResponseCode.FEATURE_NOT_SUPPORTED->{
                    Log.d(TAG, "Feature not supported")
                }
                BillingClient.BillingResponseCode.ITEM_NOT_OWNED->{
                    Log.d(TAG, "Item not owned")
                }
                BillingClient.BillingResponseCode.NETWORK_ERROR ->{
                    Log.d(TAG, "Network error")
                }
                else -> {
                    Log.d(TAG, "Query purchases failed: ${billingResult.debugMessage}")
                }
            }
        }
    fun refreshUI() {
        finish()
        startActivity(intent)
    }
    private lateinit var billingClient: BillingClient
    private val viewModel: MainViewModel by inject<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        val androidId: String = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID)
        billingClient = BillingClient.newBuilder(this)
            .setListener(purchasesUpdatedListener)
            .enablePendingPurchases()
            .build()
        establishGoogleBillingConnection()
        enableEdgeToEdge()
        viewModel.init(androidId,onInitialized = {
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
            Log.d("tenoo", "mainActivity: ${state.isPro}")
            ChatbotTheme {
                val navController = rememberNavController()
                AnimatedContent(state.isConnected) { isConnected ->
                    when{
                        isConnected -> {
                            LaunchedEffect (true) {
                                showRateUsDialog()
                            }
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
                                        SubscriptionAction.ManageSubscription -> manageSubscriptions()
                                        is SubscriptionAction.OnApplyPromoCode -> applyPromoCode(action.promoCode)
                                        SubscriptionAction.RestoreSubscription -> restoreSubscription()
                                        is SubscriptionAction.UpgradeSubscription -> upgradeSubscription(
                                            action.product
                                        )
                                    }
                                },
                                isPro = state.isPro,
                                products = state.products,
                                onDecrementFreeTrialCount = { viewModel.decrementFreeTrialCount() },
                                freeTrialCount = state.freeTrialCount,
                                imageFreeTrialCount = state.imageFreeTrialCount,
                                subscriptionName = state.products.firstOrNull()?.name
                            )
                        }
                        else -> {
                            NoInternetScreen(modifier = Modifier.fillMaxSize())
                        }
                    }
                }
            }
        }
    }

    private fun upgradeSubscription(product: ProductDetails) {
        lifecycleScope.launch {
            try {
                val productDetails = billingClient.queryProductDetails(
                    QueryProductDetailsParams.newBuilder()
                        .setProductList(
                            listOf(
                                QueryProductDetailsParams.Product.newBuilder()
                                    .setProductId(
                                        product.productId
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
//        TODO("Not yet implemented")
    }

    private fun manageSubscriptions() {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("https://play.google.com/store/account/subscriptions")
            putExtra("package", this@MainActivity.packageName)
        }
        startActivity(intent)
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
                        .setProductId(Constants.YEARLY_SUBSCRIPTION_ID)
                        .setProductType(BillingClient.ProductType.SUBS)
                        .build(),
                    QueryProductDetailsParams.Product
                        .newBuilder()
                        .setProductId(Constants.MONTHLY_SUBSCRIPTION_ID)
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
                    productDetailsResult.productDetailsList?.forEach {
                        Log.d(TAG, "Product details: ${it.name} ${it.productId}")
                    }
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
        if(!billingClient.isReady){
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
        Log.d(TAG, "Acknowledging purchase: ${purchase.isAcknowledged}")
        if(purchase.purchaseState == Purchase.PurchaseState.PURCHASED){
            if(!purchase.isAcknowledged){
                val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                withContext(Dispatchers.IO){
                    billingClient.acknowledgePurchase(acknowledgePurchaseParams.build()) { billingResult ->
                        val responseCode = billingResult.responseCode
                        Log.d(TAG, "Acknowledged purchase response: $responseCode")
                        when(responseCode){
                            BillingClient.BillingResponseCode.OK -> {
                                Log.d(TAG, "Purchase acknowledged")
                                viewModel.handleAction(MainAction.OnUpdateSubscribedPlan(purchase.products.first()))
                            }
                            else -> {
                                Log.d(TAG, "Purchase acknowledge failed: ${billingResult.debugMessage}")
                            }
                        }
                    }
                }
            } else {
                Log.d(TAG, "Purchase already acknowledged")
                viewModel.handleAction(MainAction.OnUpdateSubscribedPlan(purchase.products.first()))
            }
        }else {
            Log.d(TAG, "Purchase not acknowledged${purchase.purchaseState}")
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
                    Log.d(TAG, "Purchase history list size: ${historyList.size}")
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
        if(!billingClient.isReady){
            Log.e(TAG, "Billing client is not ready")
        }
        val billingResult = billingClient.launchBillingFlow(this, billingFlowParams)
        val responseCode = billingResult.responseCode
        when(responseCode){
            BillingClient.BillingResponseCode.OK -> {
                Log.d(TAG, "Billing flow launched")
                queryPurchases()
            }
            else -> {
                Log.d(TAG, "Billing flow launch failed: ${billingResult.debugMessage}")
            }
        }
    }

    private fun showRateUsDialog(){
        val reviewManager = ReviewManagerFactory.create(applicationContext)
        reviewManager.requestReviewFlow().addOnCompleteListener {
            if(it.isSuccessful){
                reviewManager.launchReviewFlow(this, it.result)
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

