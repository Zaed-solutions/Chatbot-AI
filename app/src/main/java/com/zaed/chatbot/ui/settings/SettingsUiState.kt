package com.zaed.chatbot.ui.settings

import com.zaed.chatbot.ui.components.AppLanguage
import com.zaed.chatbot.ui.mainchat.components.ChatModel

data class SettingsUiState(
    val isPro: Boolean = false,
    val chatMode: ChatModel = ChatModel.GPT_4O_MINI,
    val monthlyCost: Double = 0.0,
    val lifetimeCost: Double = 0.0,
    val language: AppLanguage = AppLanguage.ENGLISH,
    val fontSize: FontSize = FontSize.MEDIUM
)


enum class FontSize {
    VERY_SMALL,
    SMALL,
    MEDIUM,
    LARGE,
    VERY_LARGE
}