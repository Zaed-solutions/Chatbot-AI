package com.zaed.chatbot.ui.settings.language

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zaed.chatbot.ui.theme.ChatbotTheme
import com.zaed.projecttemplate.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangeLanguageScreen(
    onNavigateBack: () -> Unit,
    onLanguageSelected: (String) -> Unit,
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.language),
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBackIos, contentDescription = "Back")
                    }
                }
            )
        }
    ) {
        Column(modifier = Modifier.padding(it)) {
            var selectedIndex by remember { mutableStateOf(0) }
            LazyColumn {
                items(Languages.entries) { item ->
                    OutlinedCard(
                        onClick = {
                            selectedIndex = item.ordinal
                            onLanguageSelected(item.code)
                        },
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth(),
                        border = CardDefaults.outlinedCardBorder(enabled = selectedIndex == item.ordinal),
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = item.drawableResId),
                                modifier = Modifier.size(48.dp),
                                contentDescription = "Back",
                                tint = Color.Unspecified
                            )
                            Spacer(modifier = Modifier.padding(8.dp))
                            Text(
                                text = item.nativeName,
                            )
                            Spacer(modifier = Modifier.weight(1f))
//                            Text(
//                                text = item.price.name,
//                                modifier = Modifier.background(
//                                    MaterialTheme.colorScheme.primary,
//                                    RoundedCornerShape(24.dp)
//                                ).padding(8.dp),
//                                color = MaterialTheme.colorScheme.onPrimary
//                            )
                        }
                    }
                }
            }
        }
    }

}

enum class Languages(
    val code: String,
    val nativeName: String,
    @DrawableRes val drawableResId: Int
) {
    ARABIC("ar", "العربية", R.drawable.arabic_foreground),
    ENGLISH("en", "English", R.drawable.english_foreground),
    SPANISH("es", "Español", R.drawable.spanish_foreground),
    FRENCH("fr", "Français", R.drawable.frensh_foreground);
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
        ChangeLanguageScreen(onNavigateBack = {}, {})
    }
}
