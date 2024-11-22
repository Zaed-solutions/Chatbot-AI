package com.zaed.chatbot.ui.mainchat

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.zaed.chatbot.data.ChatQuery
import com.zaed.chatbot.data.MessageAttachment
import com.zaed.chatbot.ui.mainchat.components.ChatModel
import com.zaed.chatbot.ui.mainchat.components.EmptyChat
import com.zaed.chatbot.ui.mainchat.components.MainChatBottomBar
import com.zaed.chatbot.ui.mainchat.components.MainChatTopBar
import com.zaed.chatbot.ui.mainchat.components.QueryList
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
    isChatEmpty: Boolean = true,
    queries: List<ChatQuery> = emptyList(),
    selectedModel: ChatModel = ChatModel.GPT_4O_MINI,
    attachments: List<MessageAttachment> = emptyList()
) {
    Scaffold(
        topBar = {
            MainChatTopBar(
                modifier = Modifier.fillMaxWidth(),
                selectedModel = selectedModel,
                onAction = onAction
            )
        },
        bottomBar = {
             MainChatBottomBar(
                 modifier = Modifier.fillMaxWidth(),
                 onSend = { /*TODO*/ },
                 onUpdateText = {/*TODO*/},
                 attachments = attachments,
                 onRecordVoice = {/*TODO*/},
                 onDeleteAttachment = {/*TODO*/},
                 onUploadImage = {/*TODO*/},
                 onOpenCamera = {/*TODO*/},
                 onUploadFile = {/*TODO*/}
             )
        },
        modifier = modifier,
    ) { innerPadding ->
        AnimatedContent(
            modifier = Modifier.padding(innerPadding),
            targetState = isChatEmpty
        ) { state ->
            when{
                state -> {
                    EmptyChat(
                        modifier = Modifier.fillMaxSize(),
                        selectedModel = selectedModel,
                        onUseAIArtGenerator = { /*TODO*/},
                        onSuggestingClicked = {/*TODO*/})
                }
                else -> {
                    QueryList(
                        queries = queries
                    )
                }
            }

        }
    }

}

@Preview(showSystemUi = true, showBackground = true)
@Composable
private fun MainChatScreenContentPreview() {
    ChatbotTheme {
        MainChatScreenContent()
    }
}