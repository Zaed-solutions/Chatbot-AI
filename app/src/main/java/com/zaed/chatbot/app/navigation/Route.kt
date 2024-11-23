package com.zaed.chatbot.app.navigation

import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable
    data object DefaultRoute : Route

    @Serializable
    data object SettingsRoute : Route
    @Serializable
    data object ChangeFontScaleRoute : Route
}