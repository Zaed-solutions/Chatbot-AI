package com.zaed.chatbot.ui.mainchat.components

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.zaed.chatbot.data.model.MessageAttachment

@Composable
fun PreviewedAttachments(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(horizontal = 8.dp),
    attachmentSize: Dp = 64.dp,
    isAttachmentRemovable: Boolean = true,
    attachments: List<MessageAttachment>,
    onImageClicked: (Uri) -> Unit = {},
    onDeleteAttachment: (Uri) -> Unit = {},

) {
    LazyRow(
        modifier = modifier,
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items(attachments) { attachment ->
            PreviewedAttachmentItem(
                modifier = Modifier.animateItem(),
                size = attachmentSize,
                isRemovable = isAttachmentRemovable,
                attachment = attachment,
                onImageClicked = { onImageClicked(it) },
                onDeleteAttachment = { onDeleteAttachment(it) }
            )
        }
    }
}