package com.zaed.chatbot.ui.settings

import com.zaed.chatbot.ui.components.AppLanguage
import com.zaed.chatbot.ui.mainchat.components.ChatModel
import java.util.Locale

data class SettingsUiState(
    val chatMode: ChatModel = ChatModel.GPT_4O_MINI,
    val fontSize: FontSize = FontSize.MEDIUM
)


enum class FontSize {
    VERY_SMALL,
    SMALL,
    MEDIUM,
    LARGE,
    VERY_LARGE
}