package com.zaed.chatbot.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.imePadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.zaed.chatbot.app.navigation.NavigationHost
import com.zaed.chatbot.ui.theme.ChatbotTheme
import com.zaed.chatbot.ui.theme.LocalFontScale

class MainActivity : ComponentActivity() {
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
    CompositionLocalProvider(LocalFontScale provides fontScale) {
        ChatbotTheme {
            val navController = rememberNavController()
            NavigationHost(
                modifier = Modifier.imePadding(),
                fontScale = fontScale,
                onFontScaleChanged = {
                    fontScale = it
                },
                navController = navController
            )
        }
    }
}