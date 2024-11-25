package com.zaed.chatbot.data.source.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.zaed.chatbot.ui.mainchat.components.ChatModel
import kotlinx.coroutines.flow.firstOrNull

class SettingsKeyValueStorageImpl(
    private val context: Context
) : SettingsKeyValueStorage {
    companion object {
        val CHAT_MODE_KEY = stringPreferencesKey("chat_mode")
        val DEFAULT_CHAT_MODE = ChatModel.GPT_4O_MINI.name
        const val SETTINGS = "settings"
        val FONT_SCALE_KEY = floatPreferencesKey("font_scale")
        const val DEFAULT_FONT_SCALE = 1f
    }

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = SETTINGS)
    override suspend fun getChatMode(): String {
        val preferences = context.dataStore.data.firstOrNull() ?: return DEFAULT_CHAT_MODE
        return preferences[CHAT_MODE_KEY] ?: DEFAULT_CHAT_MODE
    }


    override suspend fun getFontScale(): Float {
        val preferences = context.dataStore.data.firstOrNull() ?: return DEFAULT_FONT_SCALE
        return preferences[FONT_SCALE_KEY] ?: DEFAULT_FONT_SCALE
    }

    override suspend fun setChatMode(chatMode: String) {
        context.dataStore.edit { settings ->
            settings[CHAT_MODE_KEY] = chatMode
        }
    }

    override suspend fun setFontScale(fontScale: Float) {
        context.dataStore.edit { settings ->
            settings[FONT_SCALE_KEY] = fontScale
        }
    }
}