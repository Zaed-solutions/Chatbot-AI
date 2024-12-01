package com.zaed.chatbot.ui.activity

import com.android.billingclient.api.ProductDetails

sealed interface SubscriptionAction {
    data class UpgradeSubscription(val product: ProductDetails): SubscriptionAction
    data object RestoreSubscription: SubscriptionAction
    data object ManageSubscription : SubscriptionAction
    data class OnApplyPromoCode(val promoCode: String): SubscriptionAction
}