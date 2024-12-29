package com.zaed.chatbot.data.repository

import com.android.billingclient.api.Purchase
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    suspend fun setDefaultChatMode(chatModel: String)
    suspend fun setDefaultFontScale(fontScale: Float)
    suspend fun getChatMode(): String
    suspend fun getFontScale(): Float
    suspend fun incrementUserFreeTrialCount(androidId: String)
    fun getUserFreeTrialAndImageLimit(androidId: String, product: Purchase?): Flow<Pair<Int, Int>>
    suspend fun decrementUserImageFreeTrialCount(androidId: String)

}