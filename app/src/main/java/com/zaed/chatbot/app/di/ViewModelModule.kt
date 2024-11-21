package com.zaed.chatbot.app.di

import com.zaed.chatbot.ui.mainchat.MainChatViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
   viewModelOf(::MainChatViewModel)
}