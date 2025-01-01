package com.zaed.chatbot.data.repository

import com.android.billingclient.api.Purchase
import com.zaed.chatbot.data.model.ChatQuery
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
    override suspend fun incrementUserFreeTrialCount(androidId: String) {
        return remoteConfigSource.decrementUserFreeTrialCount(androidId)
    }

    override fun getUserFreeTrialAndImageLimit(androidId: String, product: Purchase?): Flow<Pair<Int, Int>> {
        return  remoteConfigSource.getUserFreeTrialAndImageLimit(androidId,product)
    }



    override suspend fun decrementUserImageFreeTrialCount(androidId: String) {
        return remoteConfigSource.decrementUserImageFreeTrialCount(androidId)
    }

    override suspend fun reportMessage(query: ChatQuery): Result<Boolean> {
        return remoteConfigSource.reportMessage(query)
    }
}