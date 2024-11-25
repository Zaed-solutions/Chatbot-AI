package com.zaed.chatbot.app.di

import com.zaed.chatbot.data.source.local.SettingsKeyValueStorage
import com.zaed.chatbot.data.source.local.SettingsKeyValueStorageImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val localSourceModule = module {
    single<SettingsKeyValueStorage> { SettingsKeyValueStorageImpl(androidContext()) }
}