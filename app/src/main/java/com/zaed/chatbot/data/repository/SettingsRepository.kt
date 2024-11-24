package com.zaed.chatbot.data.repository

import com.zaed.chatbot.ui.mainchat.components.ChatModel

interface SettingsRepository {
    fun setDefaultChatMode(chatModel: String)
}