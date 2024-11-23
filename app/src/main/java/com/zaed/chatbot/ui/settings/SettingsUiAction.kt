package com.zaed.chatbot.ui.settings

interface SettingsUiAction {
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



}