package com.zaed.chatbot.app.di

import com.zaed.chatbot.app.MainApplication
import com.zaed.chatbot.data.source.local.ChatLocalDataSource
import com.zaed.chatbot.data.source.local.ChatLocalDataSourceImpl
import com.zaed.chatbot.data.source.local.SettingsKeyValueStorage
import com.zaed.chatbot.data.source.local.SettingsKeyValueStorageImpl
import io.realm.kotlin.Realm
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val localSourceModule = module {
    single<Realm> { MainApplication.realm}
    singleOf(::ChatLocalDataSourceImpl) { bind<ChatLocalDataSource>() }
    singleOf(::SettingsKeyValueStorageImpl) { bind<SettingsKeyValueStorage>() }
}