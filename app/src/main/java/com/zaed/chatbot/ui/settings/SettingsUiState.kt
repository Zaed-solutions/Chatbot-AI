package com.zaed.chatbot.ui.settings

import com.zaed.chatbot.ui.components.AppLanguage
import com.zaed.chatbot.ui.components.ChatMode

data class SettingsUiState(
    val chatMode: ChatMode = ChatMode.DEFAULT,
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