package com.zaed.chatbot.ui.mainchat.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zaed.chatbot.data.ChatQuery
import com.zaed.chatbot.ui.theme.ChatbotTheme

@Composable
fun QueryList(
    modifier: Modifier = Modifier,
    queries: List<ChatQuery>,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        reverseLayout = true
    ) {
        items(queries) { query ->
            QueryItem(query = query)
        }
    }
}

@Composable
fun QueryItem(
    modifier: Modifier = Modifier,
    query: ChatQuery,
) {
    Column (
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        MessageItem(
            modifier = Modifier.fillMaxWidth(),
            message = query.prompt,
            hasAttachments = query.promptAttachments.isNotEmpty(),
            attachments = query.promptAttachments
        )
        MessageItem(
            isPrompt = false,
            message = query.response,
            hasAttachments = query.responseAttachments.isNotEmpty(),
            attachments = query.responseAttachments
        )
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
private fun QueryListPreview() {
    ChatbotTheme {
        QueryList(
            queries = listOf(
                ChatQuery(prompt = "Hello there!", response = "Hello! How can I help you?"),
                ChatQuery(prompt = "I need help with my account", response = "Sure! What do you need help with?"),
            )
        )
    }
}