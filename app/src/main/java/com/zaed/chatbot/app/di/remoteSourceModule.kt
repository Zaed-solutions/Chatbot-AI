package com.zaed.chatbot.app.di

import android.system.Os.bind
import com.aallam.openai.api.http.Timeout
import com.aallam.openai.client.OpenAI
import com.zaed.chatbot.data.source.remote.ChatRemoteDataSource
import com.zaed.chatbot.data.source.remote.ChatRemoteDataSourceImpl
import com.zaed.chatbot.data.source.remote.OpenAIRemoteDataSource
import com.zaed.chatbot.data.source.remote.OpenAIRemoteDataSourceImpl
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import kotlin.time.Duration.Companion.seconds

val remoteSourceModule = module {
    singleOf(::ChatRemoteDataSourceImpl) { bind<ChatRemoteDataSource>() }
    single<OpenAI> {
        OpenAI(
            token = "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",
            timeout = Timeout(socket = 60.seconds)
        )
    }
    singleOf(::OpenAIRemoteDataSourceImpl) { bind<OpenAIRemoteDataSource>() }
}