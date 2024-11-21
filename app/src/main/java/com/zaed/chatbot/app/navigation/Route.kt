package com.zaed.chatbot.app.navigation

import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable
    data object MainChatRoute : Route
}