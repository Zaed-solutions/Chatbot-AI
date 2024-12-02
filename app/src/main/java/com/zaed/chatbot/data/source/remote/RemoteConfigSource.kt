package com.zaed.chatbot.data.source.remote

import kotlinx.coroutines.flow.Flow

interface RemoteConfigSource {
    fun getUserFreeTrialCount(androidId: String): Flow<Int>
    suspend fun incrementUserFreeTrialCount(androidId: String)
}
