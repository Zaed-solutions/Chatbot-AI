package com.zaed.chatbot.app.di

import com.zaed.chatbot.app.MainApplication
import com.zaed.chatbot.ui.util.AndroidConnectivityObserver
import com.zaed.chatbot.ui.util.ConnectivityObserver
import io.realm.kotlin.Realm
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.bind
import org.koin.dsl.module

val networkModule = module {
    single<ConnectivityObserver> {
        AndroidConnectivityObserver(
        context = androidContext()
    )
    }
}