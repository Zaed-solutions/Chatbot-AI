package com.zaed.chatbot.ui.settings

import com.zaed.chatbot.ui.components.AppLanguage
import com.zaed.chatbot.ui.mainchat.components.ChatModel
import java.util.Locale

data class SettingsUiState(
    val isPro: Boolean = false,
    val chatMode: ChatModel = ChatModel.GPT_4O_MINI,
    val monthlyCost: Double = 0.0,
    val lifetimeCost: Double = 0.0,
    val fontSize: FontSize = FontSize.MEDIUM
)


enum class FontSize {
    VERY_SMALL,
    SMALL,
    MEDIUM,
    LARGE,
    VERY_LARGE
}