package com.zaed.chatbot.app.navigation

import kotlinx.serialization.Serializable

    @Serializable
    data object SettingsRoute
    @Serializable
    object ChangeFontScaleRoute

    @Serializable
    object ChangeChatModeRoute

    @Serializable
    object ChangeLanguageRoute

    @Serializable
    object PromoCodeRoute

    @Serializable
    object FaqSupportRoute


    @Serializable
    object PrivacyPolicyRoute

    @Serializable
    object CommunityGuidelinesRoute
    @Serializable
    class MainChatRoute(val chatId: String = "")

    @Serializable
    object HistoryRoute



