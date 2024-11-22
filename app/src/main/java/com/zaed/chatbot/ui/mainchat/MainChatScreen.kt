package com.zaed.chatbot.ui.mainchat

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.zaed.chatbot.ui.mainchat.components.MainChatBottomBar
import com.zaed.chatbot.ui.mainchat.components.MainChatTopBar
import com.zaed.chatbot.ui.theme.ChatbotTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun MainChatScreen(
    modifier: Modifier = Modifier,
    viewModel: MainChatViewModel = koinViewModel()
) {

}

@Composable
fun MainChatScreenContent(
    modifier: Modifier = Modifier,
    onAction: (MainChatUiAction) -> Unit = {},
) {
    Scaffold(
        topBar = {
            MainChatTopBar()
        },
        bottomBar = {
             MainChatBottomBar()
        },
        modifier = modifier,
    ) { innerPadding ->
        Box(Modifier.padding(innerPadding))
    }

}

@Preview(showSystemUi = true, showBackground = true)
@Composable
private fun MainChatScreenContentPreview() {
    ChatbotTheme {
        MainChatScreenContent()
    }
}