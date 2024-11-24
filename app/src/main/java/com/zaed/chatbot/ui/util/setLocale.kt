package com.zaed.chatbot.ui.util

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.os.LocaleList
import java.util.Locale

fun Context.setLocale(locale: Locale): Context {
    val config = Configuration(resources.configuration)
    Locale.setDefault(locale)
    config.setLocale(locale)

    return createConfigurationContext(config)
}
fun Activity.changeLanguage(languageCode: String) {
    val newLocale = Locale(languageCode)
    val context = this.setLocale(newLocale)
    resources.updateConfiguration(context.resources.configuration, context.resources.displayMetrics)
    recreate()
}