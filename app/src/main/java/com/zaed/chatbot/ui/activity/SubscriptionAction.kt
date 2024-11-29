package com.zaed.chatbot.ui.activity

sealed interface SubscriptionAction {
    data class UpgradeSubscription(val freeTrialEnabled: Boolean, val isLifeTime: Boolean): SubscriptionAction
    data object RestoreSubscription: SubscriptionAction
    data object ManageSubscription : SubscriptionAction
    data class OnApplyPromoCode(val promoCode: String): SubscriptionAction
}