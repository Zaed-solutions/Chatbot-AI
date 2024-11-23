package com.zaed.chatbot.ui.mainchat.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.CatchingPokemon
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zaed.chatbot.R
import com.zaed.chatbot.data.model.MessageAttachment
import com.zaed.chatbot.ui.theme.ChatbotTheme

@Composable
fun MessageItem(
    modifier: Modifier = Modifier,
    isPrompt: Boolean = true,
    isLoading: Boolean = false,
    message: String,
    hasAttachments: Boolean,
    attachments: List<MessageAttachment> = emptyList(),
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = if(isPrompt) R.drawable.ic_profile else R.drawable.ic_openai),
                contentDescription = "Message Icon",
                modifier = Modifier.size(20.dp),
            )
            Text(
                text = stringResource(id = if (isPrompt) R.string.you else R.string.app_name),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
        Text(
            modifier = Modifier.padding(top = 8.dp, start = 28.dp),
            text = message,
            style = MaterialTheme.typography.bodyMedium,
        )
        if (hasAttachments && isPrompt) {
            PreviewedAttachments(
                modifier = Modifier.padding(top = 8.dp, start = 28.dp),
                contentPadding = PaddingValues(0.dp),
                attachments = attachments,
                attachmentSize = 80.dp,
                isAttachmentRemovable = false
            )
        } else if(hasAttachments){
            //TODO: Display response images
        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
private fun MessageItemPreview() {
    ChatbotTheme {
        MessageItem(
            isPrompt = true,
            message = "hello test test test",
            hasAttachments = true,
            attachments = listOf(
                MessageAttachment(name = "Test-1.txt"),
                MessageAttachment(name = "Test-2.txt"),
            )
        )
    }
}