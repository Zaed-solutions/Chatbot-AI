package com.zaed.chatbot.ui.mainchat.components

import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.StopCircle
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zaed.chatbot.R
import com.zaed.chatbot.data.model.MessageAttachment
import com.zaed.chatbot.ui.theme.ChatbotTheme

@Composable
fun MainChatBottomBar(
    modifier: Modifier = Modifier,
    isLoading : Boolean = false,
    isAttachmentButtonsVisible: Boolean = true,
    isAnimating: Boolean = false,
    onSend: () -> Unit = {},
    onUpdateText: (String) -> Unit = {},
    attachments: List<MessageAttachment> = emptyList(),
    onRecordVoice: () -> Unit = {},
    onDeleteAttachment: (Uri) -> Unit = {},
    onAddImage: () -> Unit = {},
    onOpenCamera: () -> Unit = {},
    onAddFile: () -> Unit = {},
    onStopAnimation: () -> Unit = {},
    prompt: String,
) {
    var isExpanded by remember { mutableStateOf(false) }
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        HorizontalDivider()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AnimatedContent(targetState = isExpanded to !isAttachmentButtonsVisible, label = "Attachment buttons") { state ->
                when {
                    state.second->{}
                    state.first -> {
                        Row {
                            IconButton(
                                onClick = {
                                    onAddImage()
                                    isExpanded = false
                                }
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_image_1),
                                    contentDescription = "Upload image",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = modifier
                                        .size(32.dp)
                                        .background(
                                            color = MaterialTheme.colorScheme.surfaceContainer,
                                            shape = CircleShape
                                        )
                                        .padding(8.dp)
                                )
                            }
                            IconButton(
                                onClick = {
                                    onOpenCamera()
                                    isExpanded = false
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CameraAlt,
                                    contentDescription = "Open Camera",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = modifier
                                        .size(32.dp)
                                        .background(
                                            color = MaterialTheme.colorScheme.surfaceContainer,
                                            shape = CircleShape
                                        )
                                        .padding(6.dp)
                                )
                            }
                            IconButton(
                                //Todo: add file picker
                                enabled = false,
                                onClick = {
                                    onAddFile()
                                    isExpanded = false
                                },

                            ) {
                                Icon(
                                    imageVector = Icons.Default.FileOpen,
                                    contentDescription = "Upload File",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = modifier
                                        .size(32.dp)
                                        .background(
                                            color = MaterialTheme.colorScheme.surfaceContainer,
                                            shape = CircleShape
                                        )
                                        .padding(6.dp)
                                )
                            }
                        }
                    }

                    else -> {
                        IconButton(onClick = { isExpanded = true }) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add attachment",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = modifier
                                    .size(32.dp)
                                    .background(
                                        color = MaterialTheme.colorScheme.surfaceContainer,
                                        shape = CircleShape
                                    )
                                    .padding(4.dp)
                            )
                        }
                    }
                }

            }
            Surface(
                modifier = Modifier.weight(1f),
                shape = MaterialTheme.shapes.large,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
            ) {
                Row(
                    verticalAlignment = Alignment.Bottom,
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(8.dp),
                        verticalArrangement = Arrangement.Top
                    ) {
                        BasicTextField(
                            value = prompt,
                            onValueChange = {
                                onUpdateText(it)
                            },
                            textStyle = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 32.dp)
                        )
                        PreviewedAttachments(attachments = attachments, onDeleteAttachment = onDeleteAttachment)
                    }
                    AnimatedVisibility(visible = prompt.isBlank()) {
                        IconButton(
                            enabled = prompt.isBlank(),
                            onClick = { onRecordVoice() },
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "Record Voice",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )

                        }
                    }
                }
            }
            IconButton(
                enabled = isAnimating || (!isLoading && prompt.isNotBlank()),
                onClick = {
                    if(isAnimating){
                        onStopAnimation()
                    } else {
                        onSend()
                    }
                },
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary,
                )
            ) {
                Icon(
                    imageVector = if(isAnimating) Icons.Default.StopCircle else Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .size(24.dp)
                )
            }
        }

    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
private fun MainChatBottomBarPreview() {
    ChatbotTheme {
        MainChatBottomBar(
            attachments = listOf(
                MessageAttachment(name = "Test1.txt"),
                MessageAttachment(name = "Test2.txt"),
            ),
            prompt = "prompt"
        )
    }
}