package com.zaed.chatbot.ui.settings.guidelines

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zaed.chatbot.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityGuidelinesScreen(
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.community_guidelines),
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.Cancel, contentDescription = stringResource(R.string.back))
                    }
                }
            )
        }
    ) {
        Column(modifier = Modifier
            .padding(it)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())) {
            Text(text = stringResource(R.string.community_guide_lines).trimIndent())
        }
    }

}