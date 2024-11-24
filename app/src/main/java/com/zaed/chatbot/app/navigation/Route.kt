package com.zaed.chatbot.app.navigation

import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable
    data object SettingsRoute : Route
    @Serializable
    data object ChangeFontScaleRoute : Route

    @Serializable
    data object ChangeChatModeRoute : Route

    @Serializable
    data object ChangeLanguageRoute : Route

    @Serializable
    data object PromoCodeRoute : Route

    @Serializable
    data object RateUsRoute : Route

    @Serializable
    data object RestorePurchaseRoute : Route


    @Serializable
    data object FaqSupportRoute : Route

    @Serializable
    data object TermsOfUseRoute : Route

    @Serializable
    data object PrivacyPolicyRoute : Route

    @Serializable
    data object CommunityGuidelinesRoute : Route

    data object MainChatRoute : Route
}