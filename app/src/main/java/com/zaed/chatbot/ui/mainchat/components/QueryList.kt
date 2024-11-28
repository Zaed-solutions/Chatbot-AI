package com.zaed.chatbot.ui.mainchat.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zaed.chatbot.data.model.ChatQuery
import com.zaed.chatbot.ui.mainchat.MainChatUiAction
import com.zaed.chatbot.ui.theme.ChatbotTheme

@Composable
fun QueryList(
    modifier: Modifier = Modifier,
    queries: List<ChatQuery>,
    lazyListState: LazyListState = rememberLazyListState(),
    action: (MainChatUiAction) -> Unit = {}
) {
    LaunchedEffect (queries.size){
        lazyListState.animateScrollToItem(0)
    }
    LazyColumn(
        modifier = modifier,
        state = lazyListState,
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        reverseLayout = true
    ) {
        itemsIndexed(queries, key = { _, query -> query.createdAtEpochSeconds }) { _, query ->
            QueryItem(
                query = query,
                action = action
            )
        }
    }
}

@Composable
fun QueryItem(
    modifier: Modifier = Modifier,
    query: ChatQuery,
    action: (MainChatUiAction) -> Unit = {}
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        MessageItem(
            modifier = Modifier.fillMaxWidth(),
            message = query.prompt,
            hasAttachments = query.promptAttachments.isNotEmpty(),
            attachments = query.promptAttachments,
            animating = false,
        )
        MessageItem(
            isPrompt = false,
            isLoading = query.isLoading,
            action = action,
            message = query.response,
            animating = query.animateResponse,
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
                ChatQuery(
                    prompt = "I need help with my account",
                    response = "Sure! What do you need help with?"
                ),
            )
        )
    }
}