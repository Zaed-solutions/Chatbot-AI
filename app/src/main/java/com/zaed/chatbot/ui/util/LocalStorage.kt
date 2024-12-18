package com.zaed.chatbot.ui.util

import kotlinx.coroutines.flow.Flow

interface LocalStorage {

    suspend fun saveLanguage(languageCode: String)
    val languageFlow: Flow<String?>
}
