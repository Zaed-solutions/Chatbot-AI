package com.zaed.chatbot.ui.mainchat.components

import androidx.annotation.StringRes
import com.zaed.chatbot.ui.mainchat.MainChatUiAction
import com.zaed.chatbot.R

enum class MenuOption(@StringRes val nameRes: Int, val iconRes: Int, val action: MainChatUiAction) {
    NEW_CHAT(R.string.new_chat, R.drawable.ic_create, MainChatUiAction.OnNewChatClicked),
//    PERSONALIZATION(R.string.personalization, R.drawable.ic_profile, MainChatUiAction.OnPersonalizationClicked),
    HISTORY(R.string.history, R.drawable.ic_history, MainChatUiAction.OnHistoryClicked),
    SETTINGS(R.string.settings, R.drawable.ic_settings, MainChatUiAction.OnSettingsClicked);
}