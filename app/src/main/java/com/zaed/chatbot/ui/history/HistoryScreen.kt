package com.zaed.chatbot.ui.history

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zaed.chatbot.R
import com.zaed.chatbot.data.model.ChatHistory
import com.zaed.chatbot.ui.history.components.DeleteChatDialog
import com.zaed.chatbot.ui.history.components.HistoriesList
import com.zaed.chatbot.ui.history.components.RenameHistoryDialog
import org.koin.androidx.compose.koinViewModel

@Composable
fun HistoryScreen(
    modifier: Modifier = Modifier,
    onBackPressed: () -> Unit,
    viewModel: HistoryViewModel = koinViewModel(),
    onNavigateToChat: (String) -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    HistoryScreenContent(
        modifier = modifier,
        onAction = { action ->
            when(action){
                is HistoryUiAction.OnBackClicked -> onBackPressed()
                is HistoryUiAction.OnChatHistoryClicked -> onNavigateToChat(action.chatId)
                else -> viewModel.handleAction(action)
            }
        },
        histories = state.histories
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreenContent(
    modifier: Modifier = Modifier,
    onAction: (HistoryUiAction) -> Unit,
    histories: List<ChatHistory>,
) {
    var clickedChatId by remember {
        mutableStateOf("")
    }
    var isDeleteDialogVisible by remember {
        mutableStateOf(false)
    }
    var isRenameDialogVisible by remember {
        mutableStateOf(false)
    }
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(title = {
                Text(
                    text = stringResource(id = R.string.history),
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.titleLarge,
                    fontSize = 20.sp,
                )
            }, actions = {
                IconButton(onClick = { onAction(HistoryUiAction.OnBackClicked) }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        tint = MaterialTheme.colorScheme.outlineVariant,
                        contentDescription = "Back"
                    )
                }
            })
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier.padding(innerPadding)
        ) {
            HistoriesList(histories = histories,
                onHistoryClicked = { chatId -> onAction(HistoryUiAction.OnChatHistoryClicked(chatId)) },
                onDeleteHistoryClicked = { chatId ->
                    clickedChatId = chatId
                    isDeleteDialogVisible = true
                },
                onRenameHistoryClicked = { chatId ->
                    clickedChatId = chatId
                    isRenameDialogVisible = true
                })
            //Dialogs
            AnimatedVisibility(visible = isDeleteDialogVisible) {
                DeleteChatDialog(
                    onDismiss = { isDeleteDialogVisible = false },
                    onDelete = { onAction(HistoryUiAction.OnDeleteChatHistoryConfirmed(clickedChatId)) }
                )
            }
            AnimatedVisibility(
                visible = isRenameDialogVisible) {
                RenameHistoryDialog(
                    onDismiss = { isRenameDialogVisible = false },
                    onRename = { newName -> onAction(HistoryUiAction.OnChatHistoryRenamed(clickedChatId, newName)) }
                )
            }
        }
    }
}