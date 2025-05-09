package com.zaed.chatbot.ui.settings

import com.android.billingclient.api.ProductDetails
import com.zaed.chatbot.ui.mainchat.components.ChatModel

sealed interface SettingsUiAction {
    data object OnShareClicked : SettingsUiAction
    data object OnDefaultChatModeClicked : SettingsUiAction
    data object OnLanguageClicked : SettingsUiAction
    data object OnFontSizeClicked : SettingsUiAction
    data object OnPromoCodeClicked : SettingsUiAction
    data object OnRateUsClicked : SettingsUiAction
    data object OnRestorePurchaseClicked : SettingsUiAction
    data object OnFaqSupportClicked : SettingsUiAction
    data object OnTermsOfUseClicked : SettingsUiAction
    data object OnPrivacyPolicyClicked : SettingsUiAction
    data object OnCommunityGuidelinesClicked : SettingsUiAction
    data class OnSetDefaultChatMode(val chatModel: ChatModel): SettingsUiAction
    data object OnBackPressed: SettingsUiAction
    data class OnUpgradeSubscription(val productDetails: ProductDetails):
        SettingsUiAction
    data object OnCancelSubscription: SettingsUiAction
    data object OnPrivacyTermsClicked: SettingsUiAction
    data class OnSetFontScale(val fontScale: Float): SettingsUiAction
    data class OnSubmitPromoCode(val promoCode: String): SettingsUiAction
}