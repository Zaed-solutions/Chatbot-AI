package com.zaed.chatbot.data.repository

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    suspend fun setDefaultChatMode(chatModel: String)
    suspend fun setDefaultFontScale(fontScale: Float)
    suspend fun getChatMode(): String
    suspend fun getFontScale(): Float
    fun getUserFreeTrialCount(androidId: String): Flow<Int>
    suspend fun incrementUserFreeTrialCount(androidId: String)}