package com.zaed.chatbot.ui.mainchat.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zaed.chatbot.R
import com.zaed.chatbot.ui.theme.ChatbotTheme

@Composable
fun EmptyChat(
    modifier: Modifier = Modifier,
    selectedModel: ChatModel = ChatModel.GPT_4O_MINI,
    onUseAIArtGenerator: () -> Unit = {},
    onSuggestingClicked: (String) -> Unit = {}
) {
    var selectedSuggestionCategory: SuggestionCategory? by remember {
        mutableStateOf(SuggestionCategory.MAKE_MONEY)
    }
    val welcomeMessageRes = when (selectedModel) {
        ChatModel.GPT_4O_MINI -> R.string.gpt_4o_mini_welcome_message
        ChatModel.AI_ART_GENERATOR -> R.string.art_generator_welcome_message
        ChatModel.GPT_4O -> R.string.gpt_4o_welcome_message
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(vertical = 16.dp),
    ) {
        MessageItem(
            modifier = Modifier.padding(end = 16.dp),
            message = stringResource(welcomeMessageRes),
            hasAttachments = false,
            isPrompt = false
        )
        if (selectedModel != ChatModel.AI_ART_GENERATOR) {
            Button(
                onClick = { onUseAIArtGenerator() },
                shape = MaterialTheme.shapes.medium,
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 44.dp, end = 44.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AddPhotoAlternate,
                    contentDescription = "Add Image",
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = stringResource(R.string.create_image_with_ai_art_generator),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
        if (selectedModel == ChatModel.GPT_4O_MINI) {
            Spacer(modifier = Modifier.weight(1f))
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(SuggestionCategory.entries) { category ->
                    FilterChip(
                        selected = category == selectedSuggestionCategory,
                        onClick = {
                            selectedSuggestionCategory = if (selectedSuggestionCategory == category) {
                                null
                            } else {
                                category
                            }
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        label = {
                            Text(
                                text = stringResource(id = category.titleRes),
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    )
                }
            }
            selectedSuggestionCategory?.let { category ->
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(category.suggestions){ suggestion ->
                        val promptMessage = stringResource(id = suggestion.promptRes)
                        Surface(
                            modifier = Modifier
                                .width(224.dp)
                                .clickable {
                                    onSuggestingClicked(promptMessage)
                                },
                            shape = MaterialTheme.shapes.small,
                            border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)),
                        ) {
                            Column (
                                modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 12.dp),
                            ){
                                Text(
                                    text = stringResource(id = suggestion.titleRes),
                                    style = MaterialTheme.typography.titleMedium,
                                )
                                Text(
                                    text = stringResource(id = suggestion.subtitleRes),
                                    style = MaterialTheme.typography.bodyMedium,
                                )
                            }
                        }

                    }
                }
            }
        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
private fun EmptyChatPreview() {
    ChatbotTheme {
        EmptyChat()
    }
}