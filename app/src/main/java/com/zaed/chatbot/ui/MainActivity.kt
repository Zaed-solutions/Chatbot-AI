package com.zaed.chatbot.ui

import android.app.Activity
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.os.LocaleListCompat
import androidx.navigation.compose.rememberNavController
import com.zaed.chatbot.app.navigation.NavigationHost
import com.zaed.chatbot.ui.settings.language.Languages
import com.zaed.chatbot.ui.theme.ChatbotTheme
import com.zaed.chatbot.ui.theme.LocalFontScale
import com.zaed.chatbot.ui.util.changeLanguage

import org.intellij.lang.annotations.Language

class MainActivity : ComponentActivity() {
    companion object{
        val TAG = "MainActivity"
        val DEFAULT_CHAT_MODE = "default_chat_mode"
        val DEFAULT_LANGUAGE = "default_language"
        val DEFAULT_FONT_SCALE = "default_font_scale"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ChatbotTheme {
                App()
            }
        }
    }
}

@Composable
fun App() {
    var fontScale by remember { mutableStateOf(1f)}
    val context = LocalContext.current
    CompositionLocalProvider(LocalFontScale provides fontScale) {
        ChatbotTheme {
            val navController = rememberNavController()
            NavigationHost(
                modifier = Modifier.systemBarsPadding().imePadding(),
                fontScale = fontScale,
                onFontScaleChanged = {
                    fontScale = it
                },
                navController = navController,
                onLanguageSelected = {
                    if(context is Activity){
                        context.changeLanguage(it)
                    }
                }
            )
        }
    }
}