package com.zaed.chatbot.ui.settings.font

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zaed.chatbot.R
import com.zaed.chatbot.ui.settings.SettingsUiAction
import com.zaed.chatbot.ui.settings.SettingsViewModel
import com.zaed.chatbot.ui.theme.ChatbotTheme
import org.koin.androidx.compose.koinViewModel
import android.icu.text.DecimalFormat

@Composable
fun FontScaleScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = koinViewModel(),
    onFontScaleChanged: (Float) -> Unit,
    fontScale: Float,
    onNavigateBack: () -> Unit
) {
    FontScaleScreenContent(
        modifier = modifier,
        onNavigateBack = onNavigateBack,
        fontScale = fontScale,
        onFontScaleChanged = {
            onFontScaleChanged(it)
            viewModel.handleAction(SettingsUiAction.OnSetFontScale(it))
        }
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun FontScaleScreenContent(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit = {},
    fontScale: Float = 1f,
    onFontScaleChanged: (Float) -> Unit = {}
) {
    Scaffold(modifier = modifier, topBar = {
        CenterAlignedTopAppBar(title = {
            Text(
                text = stringResource(R.string.font_size),
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.titleLarge,
                fontSize = 20.sp,
            )
        }, navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBackIos, contentDescription = "Back")
            }
        })
    }) {
        Column(
            modifier = Modifier
                .padding(it)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(id = R.string.font_size),
                    style = MaterialTheme.typography.titleLarge,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = "x${DecimalFormat("#.0").format(fontScale)}",
                    style = MaterialTheme.typography.titleLarge,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ){
                Icon(
                    imageVector = Icons.Default.TextFields,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp))
                Slider(
                    value = fontScale,
                    onValueChange = onFontScaleChanged,
                    valueRange = 0.8f..1.2f,
                    steps = 3,
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .weight(1f)
                )
                Icon(
                    imageVector = Icons.Default.TextFields,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(28.dp)
                )

            }
            Text(
                text = stringResource(R.string.text_preview),
                fontSize = 16.sp,
                modifier = Modifier.padding(top = 48.dp, bottom = 8.dp)
            )
            Surface(
                shape = MaterialTheme.shapes.extraLarge,
                border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
                color = MaterialTheme.colorScheme.surfaceContainer,
            ){
                Text(
                    text = "Welcome! i'm your personal AI assistant. How can i help you? \uD83D\uDE03 ",
                    modifier = Modifier.padding(start = 24.dp, bottom = 24.dp, end = 36.dp, top = 16.dp)
                )
            }
        }
    }
}

@Composable
@Preview
fun FontScaleScreenPreview() {
    ChatbotTheme {
        FontScaleScreenContent()
    }
}