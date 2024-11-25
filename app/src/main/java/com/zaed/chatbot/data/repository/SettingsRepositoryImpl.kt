package com.zaed.chatbot.data.repository

import com.zaed.chatbot.data.source.local.SettingsKeyValueStorage

class SettingsRepositoryImpl(
    private val settingsKeyValueStorage: SettingsKeyValueStorage
) : SettingsRepository {

    override suspend fun setDefaultChatMode(chatModel: String) {
        return settingsKeyValueStorage.setChatMode(chatModel)
    }

    override suspend fun setDefaultFontScale(fontScale: Float) {
        return settingsKeyValueStorage.setFontScale(fontScale)
    }


    override suspend fun getChatMode(): String {
        return settingsKeyValueStorage.getChatMode()
    }

    override suspend fun getFontScale(): Float {
        return settingsKeyValueStorage.getFontScale()
    }
}