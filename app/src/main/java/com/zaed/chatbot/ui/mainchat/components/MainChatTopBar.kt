package com.zaed.chatbot.ui.mainchat.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Segment
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zaed.chatbot.R
import com.zaed.chatbot.ui.components.gradientBackground
import com.zaed.chatbot.ui.mainchat.MainChatUiAction
import com.zaed.chatbot.ui.theme.ChatbotTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainChatTopBar(
    modifier: Modifier = Modifier,
    selectedModel: ChatModel = ChatModel.GPT_4O_MINI,
    onAction: (MainChatUiAction) -> Unit = {},
    onChangeModel: (ChatModel) -> Unit = {},
    onProClicked: () -> Unit = {}
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pro button infinite transition")
    val animatedAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pro button color animation"
    )
    val gradientColors = listOf(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
    )
    var isModelMenuExpanded by remember { mutableStateOf(false) }
    var isOptionsMenuExpanded by remember { mutableStateOf(false) }
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pro button scale animation"
    )
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            //App Icon and Name
            Icon(
                painterResource(id = R.drawable.ic_openai),
                contentDescription = "App Icon",
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = stringResource(id = R.string.app_name),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp)
            )
            Spacer(modifier = Modifier.weight(1f))
            //Pro Button
            Box(
                modifier = Modifier
                    .scale(scale)
                    .clip(MaterialTheme.shapes.small)
                    .gradientBackground(
                        colors = gradientColors,
                        angle = animatedAngle
                    )
                    .clickable { onProClicked() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.pro),
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                )
            }
            //Model Drop Down Menu
            Box{
                Surface(
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .height(24.dp)
                        .clickable { isModelMenuExpanded = !isModelMenuExpanded },
                    tonalElevation = 4.dp,
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    shape = MaterialTheme.shapes.large,
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(id = selectedModel.nameRes),
                            style = MaterialTheme.typography.labelSmall
                        )
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = isModelMenuExpanded)
                    }
                }
                DropdownMenu(
                    expanded = isModelMenuExpanded,
                    onDismissRequest = { isModelMenuExpanded = false }
                ) {
                    ChatModel.entries.forEach { item ->
                        DropdownMenuItem(
                            onClick = {
                                onChangeModel(item)
                                isModelMenuExpanded = false
                            },
                            text = {
                                ChatModelItem(
                                    isSelected = item == selectedModel,
                                    titleRes = item.nameRes,
                                    subtitleRes = item.descriptionRes,
                                    trailingIconRes = item.iconRes
                                )
                            },
                        )
                    }
                }
            }
            Box(
                modifier = Modifier
                    .wrapContentSize(Alignment.TopEnd)
            ) {
                IconButton(
                    onClick = { isOptionsMenuExpanded = !isOptionsMenuExpanded },
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Segment,
                        contentDescription = null,
                    )
                }

                DropdownMenu(
                    expanded = isOptionsMenuExpanded,
                    onDismissRequest = { isOptionsMenuExpanded = false }
                ) {
                    MenuOption.entries.forEach { item ->
                        DropdownMenuItem(
                            onClick = {
                                onAction(item.action)
                                isOptionsMenuExpanded = false
                            },
                            text = {
                                Row(
                                    modifier = Modifier.width(180.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = stringResource(id = item.nameRes),
                                        style = MaterialTheme.typography.labelLarge,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Icon(
                                        painter = painterResource(id = item.iconRes),
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(16.dp)
                                    )

                                }
                            },
                        )
                    }
                }
            }

        }
        HorizontalDivider(thickness = 0.5.dp, modifier = Modifier.padding(top = 8.dp))
    }
}


@Preview(showSystemUi = true, showBackground = true)
@Composable
private fun MainChatTopBarPreview() {
    ChatbotTheme {
        MainChatTopBar()
    }
}