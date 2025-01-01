package com.zaed.chatbot.data.repository

import com.android.billingclient.api.Purchase
import com.zaed.chatbot.data.model.ChatQuery
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    suspend fun setDefaultChatMode(chatModel: String)
    suspend fun setDefaultFontScale(fontScale: Float)
    suspend fun getChatMode(): String
    suspend fun getFontScale(): Float
    suspend fun incrementUserFreeTrialCount(androidId: String)
    fun getUserFreeTrialAndImageLimit(androidId: String, product: Purchase?): Flow<Pair<Int, Int>>
    suspend fun decrementUserImageFreeTrialCount(androidId: String)
    suspend fun reportMessage(query: ChatQuery): Result<Boolean>
}