package com.zaed.chatbot.app.di

import android.system.Os.bind
import com.zaed.chatbot.data.repository.ChatRepository
import com.zaed.chatbot.data.repository.ChatRepositoryImpl
import com.zaed.chatbot.data.repository.SettingsRepository
import com.zaed.chatbot.data.repository.SettingsRepositoryImpl
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val repositoryModule = module {
    singleOf(::ChatRepositoryImpl) { bind<ChatRepository>() }
    singleOf(::SettingsRepositoryImpl) { bind<SettingsRepository>() }
}