package com.zaed.chatbot.ui.mainchat.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.halilibo.richtext.commonmark.Markdown
import com.halilibo.richtext.ui.material3.RichText
import com.zaed.chatbot.R
import com.zaed.chatbot.data.model.MessageAttachment
import com.zaed.chatbot.ui.mainchat.MainChatUiAction
import com.zaed.chatbot.ui.theme.ChatbotTheme
import kotlinx.coroutines.delay
import java.text.BreakIterator
import java.text.StringCharacterIterator

@Composable
fun MessageItem(
    modifier: Modifier = Modifier,
    isPrompt: Boolean = true,
    isLoading: Boolean = false,
    message: String,
    animating: Boolean = false,
    action: (MainChatUiAction) -> Unit = {},
    hasAttachments: Boolean,
    attachments: List<MessageAttachment> = emptyList(),
) {
    val clipboardManager = LocalClipboardManager.current
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = if (isPrompt) R.drawable.ic_profile else R.drawable.chatbot_ai),
                contentDescription = "Message Icon",
                modifier = Modifier.size(24.dp),
            )
            Text(
                text = stringResource(id = if (isPrompt) R.string.you else R.string.app_name),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(start = 8.dp)
            )
            AnimatedVisibility(!isPrompt) {
                IconButton({
                    clipboardManager.setText(
                        annotatedString = androidx.compose.ui.text.AnnotatedString(
                            message
                        )
                    )
                }) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy Icon",
                        modifier = Modifier.size(18.dp),
                    )
                }
            }
        }
        if (isLoading) {
            LoadingBubble(
                modifier = Modifier.padding(
                    start = 32.dp,
                    top = 16.dp
                )
            ) // Display animated loading bubble
        } else {
            AnimatedText(text = message, animating = animating, action)
            if (hasAttachments && isPrompt) {
                PreviewedAttachments(
                    modifier = Modifier.padding(top = 8.dp, start = 28.dp),
                    contentPadding = PaddingValues(0.dp),
                    attachments = attachments,
                    attachmentSize = 128.dp,
                    isAttachmentRemovable = false
                )
            } else if (hasAttachments) {
                PreviewedAttachments(
                    modifier = Modifier.padding(top = 8.dp, start = 28.dp),
                    contentPadding = PaddingValues(0.dp),
                    attachments = attachments,
                    onImageClicked = {
                        action(MainChatUiAction.OnImageClicked(it))
                    },
                    attachmentSize = 256.dp,
                    isAttachmentRemovable = false
                )
            }
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
            ),
        )
    }
}


@Composable
fun MarkdownViewer(markdownText: String) {
    RichText(
        modifier = Modifier.padding(16.dp)
    ) {
        Markdown(
            markdownText.trimIndent()
        )
    }
}

fun isArabic(text: String): Boolean {
    val arabicRange = Regex("[\\u0600-\\u06FF]")
    val arabicCount = text.count { arabicRange.matches(it.toString()) }
    val threshold = text.length / 2 // Adjust threshold as needed
    return arabicCount > threshold
}

@Composable
private fun AnimatedText(
    text: String,
    animating: Boolean = true,
    action: (MainChatUiAction) -> Unit
) {

    val breakIterator = remember(text) { BreakIterator.getCharacterInstance() }

    val typingDelayInMs = 30L

    var substringText by remember {
        mutableStateOf("")
    }
    val isArabic = remember(text) { isArabic(text) }

    LaunchedEffect(text) {
        delay(500)
        breakIterator.text = StringCharacterIterator(text)

        var nextIndex = breakIterator.next()

        while (nextIndex != BreakIterator.DONE) {
            substringText = text.subSequence(0, nextIndex).toString()
            nextIndex = breakIterator.next()
            delay(typingDelayInMs)
        }
        action(MainChatUiAction.OnStopAnimation)
    }
    CompositionLocalProvider(LocalLayoutDirection provides if (isArabic) LayoutDirection.Rtl else LayoutDirection.Ltr) {
        RichText(
            modifier = Modifier.fillMaxWidth().padding(start = 32.dp)
        ) {
            Markdown(
                content = if (animating) {
                    substringText.trimIndent()
                } else {
                    text.trimIndent()
                }
            )
        }
    }

}
//}

@Composable
fun LoadingBubble(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(3) { index ->
            PulsatingBubble(delayMillis = index * 200)
        }
    }
}

@Composable
fun PulsatingBubble(
    modifier: Modifier = Modifier,
    delayMillis: Int
) {
    val infiniteTransition = rememberInfiniteTransition()
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, delayMillis = delayMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    // Bubble styling
    Box(
        modifier = modifier
            .size(16.dp) // Size of individual bubble
            .scale(scale)
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.8f), shape = CircleShape)
    )
}

