package com.zaed.chatbot.app.di

import org.koin.dsl.module

val appModule = module {
    includes(viewModelModule, repositoryModule, localSourceModule)
}