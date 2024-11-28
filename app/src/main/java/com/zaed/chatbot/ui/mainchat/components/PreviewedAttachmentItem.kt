package com.zaed.chatbot.ui.mainchat.components

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.zaed.chatbot.data.model.FileType
import com.zaed.chatbot.data.model.MessageAttachment
import com.zaed.chatbot.ui.theme.ChatbotTheme

@Composable
fun PreviewedAttachmentItem(
    modifier: Modifier = Modifier,
    attachment: MessageAttachment = MessageAttachment(),
    size: Dp = 64.dp,
    isRemovable: Boolean = true,
    onDeleteAttachment: (Uri) -> Unit = {},
    onImageClicked: (Uri) -> Unit = {}
) {
    Surface(
        modifier = modifier.size(size),
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(width = 0.5.dp, color = MaterialTheme.colorScheme.surfaceVariant),
        tonalElevation = 0.1.dp,
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            if(attachment.type == FileType.IMAGE){
                Image(
                    painter = rememberAsyncImagePainter(
                        model = attachment.uri,
                        contentScale = ContentScale.Crop,
                        filterQuality = FilterQuality.Low
                    ),
                    contentDescription = "Attachment BackGround",
                    modifier = Modifier.fillMaxSize().clickable {
                        onImageClicked(attachment.uri)
                    }
                )
            } else {
                Column(
                    modifier = Modifier.padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.UploadFile,
                        contentDescription = "File",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        modifier = Modifier.padding(top = 8.dp),
                        text = attachment.name,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
            if(isRemovable){
                IconButton(
                    onClick = { onDeleteAttachment(attachment.uri) },
                    modifier = Modifier
                        .padding(top = 4.dp, end = 4.dp)
                        .size(16.dp)
                        .align(Alignment.TopEnd)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "delete",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier
                            .size(16.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primary,
                                shape = CircleShape
                            )
                            .padding(2.dp)
                    )
                }
            }
        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
private fun PreviewedAttachmentItemPreview() {
    ChatbotTheme {
        PreviewedAttachmentItem(
            attachment = MessageAttachment(
                name =" testtest.pptx",
                type = FileType.ALL
            )
        )
    }
}