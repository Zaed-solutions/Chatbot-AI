package com.zaed.chatbot.app

import android.app.Application
import com.zaed.chatbot.app.di.appModule
import com.zaed.chatbot.data.source.local.model.ChatHistoryEntity
import com.zaed.chatbot.data.source.local.model.ChatQueryEntity
import com.zaed.chatbot.data.source.local.model.MessageAttachmentEntity
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MainApplication : Application() {
    companion object {
        lateinit var realm: Realm
    }

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@MainApplication)
            modules(appModule)
        }
        realm = Realm.open(
            configuration = RealmConfiguration.create(
                schema = setOf(
                    ChatQueryEntity::class,
                    MessageAttachmentEntity::class,
                    ChatHistoryEntity::class
                )
            )
        )
    }
}