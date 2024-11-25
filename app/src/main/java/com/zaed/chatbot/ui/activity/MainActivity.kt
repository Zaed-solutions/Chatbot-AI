package com.zaed.chatbot.ui.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.zaed.chatbot.app.navigation.NavigationHost
import com.zaed.chatbot.ui.theme.ChatbotTheme
import com.zaed.chatbot.ui.theme.LocalFontScale
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    companion object {
        val TAG = "MainActivity"
    }

    private val viewModel: MainViewModel by inject<MainViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        viewModel.init(onInitialized = {
            setContent {
                ChatbotTheme {
                    App()
                }
            }
        })
    }

    @Composable
    fun App() {
        val state by viewModel.uiState.collectAsStateWithLifecycle()
        var fontScale by remember { mutableFloatStateOf(state.fontScale) }
        CompositionLocalProvider(LocalFontScale provides fontScale) {
            ChatbotTheme {
                val navController = rememberNavController()
                NavigationHost(modifier = Modifier
                    .systemBarsPadding()
                    .imePadding(),
                    fontScale = fontScale,
                    defaultChatMode = state.chatMode,
                    onFontScaleChanged = {
                        fontScale = it
                        viewModel.handleAction(MainAction.OnSetFontScale(it))
                    },
                    navController = navController,
                    onDefaultChatModeChanged = {
                        viewModel.handleAction(MainAction.OnSetDefaultChatMode(it))
                    })
            }
        }
    }
}
