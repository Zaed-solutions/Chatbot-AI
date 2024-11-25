package com.zaed.chatbot.app.di

import com.zaed.chatbot.data.source.remote.ChatRemoteDataSource
import com.zaed.chatbot.data.source.remote.ChatRemoteDataSourceImpl
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val remoteSourceModule = module {
    singleOf(::ChatRemoteDataSourceImpl) { bind<ChatRemoteDataSource>() }
}