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
    data object FaqSupportRoute : Route


    @Serializable
    data object PrivacyPolicyRoute : Route

    @Serializable
    data object CommunityGuidelinesRoute : Route

    @Serializable
    data object MainChatRoute : Route

    @Serializable
    data object HistoryRoute : Route
}