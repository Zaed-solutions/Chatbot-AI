package com.zaed.chatbot.ui.settings.mode

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zaed.projecttemplate.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatModeScreen(
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Default Chat Mode",
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
                items(ChatMode.entries) { item ->
                    OutlinedCard(
                        onClick = { selectedIndex = item.ordinal },
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth()
                            .padding(8.dp),
                        border = CardDefaults.outlinedCardBorder(enabled = selectedIndex == item.ordinal),
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.openai_foreground),
                                modifier = Modifier.size(48.dp),
                                contentDescription = "Back"
                            )
                            Spacer(modifier = Modifier.padding(8.dp))
                            Text(
                                text = item.title,
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            if (item.price == PricePlan.Paid)
                                Text(
                                    text = item.price.name,
                                    modifier = Modifier
                                        .background(
                                            Color.Black,
                                            RoundedCornerShape(50.dp)
                                        )
                                        .padding(horizontal = 6.dp, vertical = 2.dp),
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                        }
                    }
                }
            }
        }
    }

}

enum class PricePlan {
    Free,
    Paid
}

enum class ChatMode(
    val title: String,
    val icon: ImageVector,
    val price: PricePlan
) {
    GPT_4O_MINI("GPT-4o Mini", Icons.Default.Chat, PricePlan.Free),
    GPT_4O_BASIC("GPT-4o", Icons.Default.Chat, PricePlan.Paid),
    GPT_4O_PRO("AI Art Generator", Icons.Default.Chat, PricePlan.Paid)
}

@Composable
@Preview
fun ChatModeScreenPreview() {
    ChatModeScreen(onNavigateBack = {})
}