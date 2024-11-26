package com.zaed.chatbot.app.di

import com.zaed.chatbot.ui.activity.MainViewModel
import com.zaed.chatbot.ui.history.HistoryViewModel
import com.zaed.chatbot.ui.mainchat.MainChatViewModel
import com.zaed.chatbot.ui.settings.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
   viewModelOf(::MainChatViewModel)
   viewModelOf(::SettingsViewModel)
   viewModelOf(::MainViewModel)
   viewModelOf(::HistoryViewModel)
}