package com.zaed.chatbot.data.source.local

interface SettingsKeyValueStorage {
    suspend fun getChatMode(): String
    suspend fun getFontScale(): Float
    suspend fun setChatMode(chatMode: String)
    suspend fun setFontScale(fontScale: Float)
}