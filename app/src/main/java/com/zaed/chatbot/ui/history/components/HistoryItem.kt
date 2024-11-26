package com.zaed.chatbot.ui.history.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zaed.chatbot.R
import com.zaed.chatbot.ui.theme.ChatbotTheme

@Composable
fun HistoryItem(
    modifier: Modifier = Modifier,
    title: String = "",
    lastResponse: String = "",
    onClick: () -> Unit = {},
    onRenameClicked: () -> Unit = {},
    onDeleteClicked: () -> Unit = {},
) {
    var isOptionsMenuExpanded by remember {
        mutableStateOf(false)
    }
    Surface(
        modifier = modifier,
        onClick = { onClick() },
        shape = MaterialTheme.shapes.large,
        border = BorderStroke(
            1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
        )
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .fillMaxWidth()
                .height(190.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = lastResponse,
                style = MaterialTheme.typography.bodyLarge,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f).padding(top = 8.dp),
            )
            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End
            ) {
                Box(modifier = Modifier.wrapContentSize(Alignment.TopEnd)) {
                    IconButton(
                        onClick = { isOptionsMenuExpanded = !isOptionsMenuExpanded },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreHoriz, contentDescription = null
                        )
                    }

                    DropdownMenu(expanded = isOptionsMenuExpanded,
                        onDismissRequest = { isOptionsMenuExpanded = false }) {
                        DropdownMenuItem(onClick = {
                            onRenameClicked()
                            isOptionsMenuExpanded = false
                        }, text = {
                            Text(
                                text = stringResource(R.string.rename),
                                style = MaterialTheme.typography.labelLarge
                            )
                        }, trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        })
                        DropdownMenuItem(onClick = {
                            onDeleteClicked()
                            isOptionsMenuExpanded = false
                        }, colors = MenuDefaults.itemColors(
                            textColor = MaterialTheme.colorScheme.error,
                            trailingIconColor = MaterialTheme.colorScheme.error
                        ), text = {
                            Text(
                                text = stringResource(R.string.delete),
                                style = MaterialTheme.typography.labelLarge
                            )
                        }, trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        })
                    }
                }
            }
        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
private fun HistoryItemPreview() {
    ChatbotTheme {
        HistoryItem(
            modifier = Modifier.padding(16.dp),
            title = "Test Inquiry",
            lastResponse = "Last responseLast responseLast responseLast responseLast responseLast responseLast responseLast responseLast responseLast responseLast responseLast responseLast responseLast responseLast responseLast responseLast responseLast responseLast responseLast responseLast responseLast responseLast responseLast responseLast responseLast responseLast responseLast responseLast responseLastLast responseLast responseLast responseLast responseLast responseLast responseLast responseLast responseLast responseLast responseLast responseLast responseLast responseLast responseLast responseLast responseLast responseLast responseLast responseLast responseLast responseLast responseLast responseLast responseLast responseLast responseLast responseLast responseLast responseLast responseLast responseLast responseLast responseLast responseLast responseLast responseLast responseLast responseLast responseLast responseLast responseLast response",
            onClick = {},
            onRenameClicked = {},
            onDeleteClicked = {})
    }
}