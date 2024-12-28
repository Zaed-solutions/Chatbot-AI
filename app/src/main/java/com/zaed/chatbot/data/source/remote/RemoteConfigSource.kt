package com.zaed.chatbot.data.source.remote

import kotlinx.coroutines.flow.Flow

interface RemoteConfigSource {
    fun getUserFreeTrialCount(androidId: String): Flow<Int>
    suspend fun incrementUserFreeTrialCount(androidId: String)
    fun getUserImageFreeTrialCount(androidId: String,productId : String): Flow<Int>
    suspend fun decrementUserImageFreeTrialCount(androidId: String)
}
