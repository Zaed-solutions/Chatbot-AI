package com.zaed.chatbot.ui.history.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zaed.chatbot.data.model.ChatHistory

@Composable
fun HistoriesList(
    modifier: Modifier = Modifier,
    histories: List<ChatHistory>,
    onHistoryClicked: (String) -> Unit,
    onDeleteHistoryClicked: (String) -> Unit,
    onRenameHistoryClicked: (String) -> Unit
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(vertical = 16.dp, horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(
            items = histories,
            key = { it.chatId }
        ) { history ->
            HistoryItem(
                modifier = Modifier.animateItem(),
                title = history.title,
                lastResponse = history.lastResponse,
                onClick = { onHistoryClicked(history.chatId) },
                onDeleteClicked =  { onDeleteHistoryClicked(history.chatId) },
                onRenameClicked = { onRenameHistoryClicked(history.chatId) }
            )
        }
    }
}