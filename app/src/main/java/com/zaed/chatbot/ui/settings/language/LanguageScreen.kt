package com.zaed.chatbot.ui.settings.language

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zaed.chatbot.R
import com.zaed.chatbot.ui.settings.SettingsUiAction
import com.zaed.chatbot.ui.settings.SettingsViewModel
import com.zaed.chatbot.ui.theme.ChatbotTheme
import org.koin.androidx.compose.koinViewModel
import java.util.Locale

@Composable
fun LanguageScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = koinViewModel(),
    onNavigateBack: () -> Unit,
    onLanguageSelected: (String) -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    LanguageScreenContent(
        modifier = modifier,
        currentLocale = state.locale,
        onNavigateBack = onNavigateBack,
        onLanguageSelected = { languageCode ->
            onLanguageSelected(languageCode)
            viewModel.handleAction(SettingsUiAction.OnSetLanguage(languageCode))
        }
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun LanguageScreenContent(
    modifier: Modifier = Modifier,
    currentLocale: Locale = Locale.getDefault(),
    onNavigateBack: () -> Unit = {},
    onLanguageSelected: (String) -> Unit = {}
) {
    Scaffold(modifier = modifier, topBar = {
        CenterAlignedTopAppBar(title = {
            Text(
                text = stringResource(R.string.language), fontWeight = FontWeight.Bold
            )
        }, navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBackIos, contentDescription = "Back")
            }
        })
    }) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            var selectedLanguage by remember {
                mutableStateOf(Languages.entries.firstOrNull { it.code == currentLocale.language }
                    ?: Languages.ENGLISH)
            }
            LazyColumn {
                items(Languages.entries) { item ->
                    val isSelected = selectedLanguage == item
                    Surface(
                        onClick = {
                            if (!isSelected) {
                                selectedLanguage = item
                                onLanguageSelected(item.code)
                            }
                        },
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .fillMaxWidth(),
                        shape = MaterialTheme.shapes.large,
                        tonalElevation = if (isSelected) 2.dp else 0.dp,
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(
                                alpha = 0.5f
                            )
                        ),
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = item.drawableResId),
                                modifier = Modifier.size(48.dp),
                                contentDescription = "Icon Flag",
                                tint = Color.Unspecified
                            )
                            Text(
                                text = item.nativeName, modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

enum class Languages(
    val code: String, val nativeName: String, @DrawableRes val drawableResId: Int
) {
    ARABIC(
        "ar", "العربية", R.drawable.arabic_foreground
    ),
    ENGLISH(
        "en", "English", R.drawable.english_foreground
    );
//    SPANISH(
//        "es",
//        "Español",
//        R.drawable.spanish_foreground
//    ),
//    FRENCH(
//        "fr",
//        "Français",
//        R.drawable.frensh_foreground
//    );
//    GERMAN("de", "Deutsch", R.drawable.german_foreground),
//    CHINESE("zh", "中文", R.drawable.chinese_foreground),
//    JAPANESE("ja", "日本語", R.drawable.japanese_foreground),
//    RUSSIAN("ru", "Русский", R.drawable.russian_foreground),
//    PORTUGUESE("pt", "Português", R.drawable.portuguese_foreground),
//    HINDI("hi", "हिन्दी", R.drawable.hindi_foreground);

    companion object {
        fun fromCode(code: String): Languages? {
            return values().find { it.code.equals(code, ignoreCase = true) }
        }
    }
}


@Composable
@Preview
fun ChangeLanguageScreenPreview() {
    ChatbotTheme {
        LanguageScreenContent()
    }
}
