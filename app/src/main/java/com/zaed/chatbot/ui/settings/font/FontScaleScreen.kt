package com.zaed.chatbot.ui.settings.font

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
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
import androidx.compose.ui.unit.dp
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
            TopAppBar(
                title = {
                    Text(
                        text = "Font Scale",
                        fontWeight = FontWeight.Bold
                    )
                    },
                actions = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.Cancel, contentDescription = "Back")
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
        }
    }

}