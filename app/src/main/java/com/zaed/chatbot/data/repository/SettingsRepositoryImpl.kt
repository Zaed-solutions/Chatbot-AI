package com.zaed.chatbot.data.repository

import com.zaed.chatbot.data.source.local.SettingsKeyValueStorage
import com.zaed.chatbot.data.source.remote.RemoteConfigSource
import kotlinx.coroutines.flow.Flow

class SettingsRepositoryImpl(
    private val settingsKeyValueStorage: SettingsKeyValueStorage,
    private val remoteConfigSource: RemoteConfigSource
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

    override fun getUserFreeTrialCount(androidId: String): Flow<Int> {
        return remoteConfigSource.getUserFreeTrialCount(androidId)
    }


    override suspend fun incrementUserFreeTrialCount(androidId: String) {
        return remoteConfigSource.incrementUserFreeTrialCount(androidId)
    }

    override fun getUserImageFreeTrialCount(androidId: String,productId : String): Flow<Int> {
        return  remoteConfigSource.getUserImageFreeTrialCount(androidId,productId)
    }

    override suspend fun decrementUserImageFreeTrialCount(androidId: String) {
        return remoteConfigSource.decrementUserImageFreeTrialCount(androidId)
    }
}