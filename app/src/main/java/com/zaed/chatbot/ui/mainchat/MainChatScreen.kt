package com.zaed.chatbot.ui.mainchat

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.zaed.chatbot.ui.theme.ChatbotTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun MainChatScreen(
    modifier: Modifier = Modifier,
    viewModel: MainChatViewModel = koinViewModel()
) {

}

@Composable
fun MainChatScreenContent(modifier: Modifier = Modifier) {

}

@Preview(showSystemUi = true, showBackground = true)
@Composable
private fun MainChatScreenContentPreview() {
    ChatbotTheme {
        MainChatScreenContent()
    }
}