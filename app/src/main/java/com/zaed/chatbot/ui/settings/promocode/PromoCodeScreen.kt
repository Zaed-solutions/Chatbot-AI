package com.zaed.chatbot.ui.settings.promocode

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zaed.chatbot.ui.theme.ChatbotTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PromoCodeScreen(
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Promo Code",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Back")
                    }
                }
            )
        }
    ) {
        Column(modifier = Modifier
            .padding(it)
            .padding(16.dp)) {

            Text(
                text = "Enter Your Promo Code",
                style = MaterialTheme.typography.headlineSmall,
            )
            Spacer(modifier = Modifier.padding(8.dp))
            Text(
                text = "If you received a referral code, you can enter the code here!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.padding(8.dp))
            OutlinedTextField(
                value = "",
                onValueChange = {},
                label = { Text("Promo Code") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
            )
            Spacer(modifier = Modifier.padding(8.dp))
            Button(
                onClick = {},
                modifier = Modifier
                    .height(54.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(
                    text = "Submit",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }

}

@Composable
@Preview
fun PromoCodeScreenPreview() {
    ChatbotTheme {
        PromoCodeScreen(onNavigateBack = {})
    }
}