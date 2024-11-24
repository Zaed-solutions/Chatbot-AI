package com.zaed.chatbot.ui.settings.font

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zaed.chatbot.ui.theme.ChatbotTheme
import java.lang.reflect.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FontScaleScreen(
    onFontScaleChanged: (Float) -> Unit,
    fontScale: Float,
    onNavigateBack: () -> Unit
){
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Font Scale",
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
        Column (modifier = androidx.compose.ui.Modifier.padding(it)){
            Slider(
                value = fontScale,
                onValueChange = onFontScaleChanged,
                valueRange = 0.8f..1.2f, // Font scale range: 0.5x to 2.0x
                steps = 3, // Optional: for discrete steps
                modifier = androidx.compose.ui.Modifier.padding(horizontal = 16.dp)
            )
            Spacer(androidx.compose.ui.Modifier.padding(8.dp))
            Text(
                text = "TextPreview",
                modifier = androidx.compose.ui.Modifier.padding(horizontal = 16.dp)
            )
            Spacer(androidx.compose.ui.Modifier.padding(8.dp))
            ElevatedCard (
                modifier = androidx.compose.ui.Modifier.padding(horizontal = 16.dp),
            ){
                Text(
                    text = "\uD83D\uDE03 Welcome! i'm your personal AI assistant. How can i help you?",

                    modifier = androidx.compose.ui.Modifier.padding(16.dp)
                )
            }
        }
    }
}
@Composable
@Preview
fun FontScaleScreenPreview(){
    ChatbotTheme {
        FontScaleScreen(onFontScaleChanged = {}, fontScale = 1f, onNavigateBack = {})
    }
}