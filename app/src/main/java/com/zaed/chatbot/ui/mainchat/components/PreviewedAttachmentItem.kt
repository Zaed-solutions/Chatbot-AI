package com.zaed.chatbot.ui.mainchat.components

import android.net.Uri
import android.util.Log
import android.util.Size
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.SubcomposeAsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.disk.DiskCache
import coil.fetch.Fetcher
import coil.request.ImageRequest
import coil.request.Options
import com.zaed.chatbot.data.model.FileType
import com.zaed.chatbot.data.model.MessageAttachment
import com.zaed.chatbot.ui.theme.ChatbotTheme
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okio.Buffer
import okio.BufferedSource
import okio.ForwardingSource
import okio.buffer

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
            if (attachment.type == FileType.IMAGE) {
                ImageWithProgress(
                    imageUrl = attachment.uri.toString(),
                    onImageClicked = { onImageClicked(attachment.uri) }
                )
//                Image(
//                    painter = rememberAsyncImagePainter(
//                        model = attachment.uri,
//                        contentScale = ContentScale.Crop,
//                        filterQuality = FilterQuality.Low
//                    ),
//                    contentDescription = "Attachment BackGround",
//                    modifier = Modifier.fillMaxSize().clickable {
//                        onImageClicked(attachment.uri)
//                    }
//                )
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
            if (isRemovable) {
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
                name = " testtest.pptx",
                type = FileType.ALL
            )
        )
    }
}

@Composable
fun ImageWithProgress(
    imageUrl: String,
    onImageClicked: (String) -> Unit
) {
    var isLoaded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        SubcomposeAsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUrl)
                .listener(
                    onStart = { isLoaded = false },
                    onSuccess = { _, _ -> isLoaded = true },
                    onError = { _, _ -> isLoaded = true }
                ).build(),
            contentDescription = "Attachment Background",
            modifier = Modifier
                .fillMaxSize()
                .clickable { onImageClicked(imageUrl) },
            contentScale = ContentScale.Crop,
            loading = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
//                        .background(Color.Black.copy(alpha = 0.5f)) // Optional dim background
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            },
            error = {
                Text(
                    text = "Failed to load image",
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.Red
                )
            }
        )

        // Image loading state handling
        if (!isLoaded) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
//                    .background(MaterialTheme.colorScheme.primary)
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

