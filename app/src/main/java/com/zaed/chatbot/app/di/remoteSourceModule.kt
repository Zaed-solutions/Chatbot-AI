package com.zaed.chatbot.app.di

import com.aallam.openai.api.http.Timeout
import com.aallam.openai.client.OpenAI
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.zaed.chatbot.data.source.remote.ChatRemoteDataSource
import com.zaed.chatbot.data.source.remote.ChatRemoteDataSourceImpl
import com.zaed.chatbot.data.source.remote.OpenAIRemoteDataSource
import com.zaed.chatbot.data.source.remote.OpenAIRemoteDataSourceImpl
import com.zaed.chatbot.data.source.remote.RemoteConfigSource
import com.zaed.chatbot.data.source.remote.RemoteConfigSourceImpl
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
    singleOf(::RemoteConfigSourceImpl) { bind<RemoteConfigSource>() }
    single<FirebaseStorage> {
        Firebase.storage
    }
    single<FirebaseFirestore> {
        Firebase.firestore
    }
}