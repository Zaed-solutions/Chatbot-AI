package com.zaed.chatbot.ui.mainchat.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.zaed.chatbot.ui.theme.ChatbotTheme

@Composable
fun MainChatBottomBar(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        HorizontalDivider()
        Row (
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {


        }

    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
private fun MainChatBottomBarPreview() {
    ChatbotTheme {
        MainChatBottomBar()
    }
}