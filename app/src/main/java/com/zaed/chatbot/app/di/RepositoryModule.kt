package com.zaed.chatbot.app.di

import com.zaed.chatbot.data.repository.SettingsRepository
import com.zaed.chatbot.data.repository.SettingsRepositoryImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val repositoryModule = module {
    singleOf<SettingsRepository>(::SettingsRepositoryImpl)
}