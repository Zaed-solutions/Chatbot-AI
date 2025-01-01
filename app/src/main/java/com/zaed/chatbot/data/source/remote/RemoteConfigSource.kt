package com.zaed.chatbot.data.source.remote

import com.android.billingclient.api.Purchase
import com.zaed.chatbot.data.model.ChatQuery
import kotlinx.coroutines.flow.Flow

interface RemoteConfigSource {
    suspend fun decrementUserFreeTrialCount(androidId: String)
    suspend fun decrementUserImageFreeTrialCount(androidId: String)
    fun getUserFreeTrialAndImageLimit(androidId: String, product: Purchase?): Flow<Pair<Int, Int>>
    suspend fun reportMessage(query: ChatQuery): Result<Boolean>
}
