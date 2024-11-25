package com.zaed.chatbot.data.repository

import com.zaed.chatbot.ui.mainchat.components.ChatModel

interface SettingsRepository {
    suspend fun setDefaultChatMode(chatModel: String)
    suspend fun setDefaultFontScale(fontScale: Float)
    suspend fun getChatMode(): String
    suspend fun getFontScale(): Float
}