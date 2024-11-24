package com.zaed.chatbot.ui.settings

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Loop
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Support
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zaed.chatbot.ui.theme.ChatbotTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = koinViewModel(),
    onNavigateBack: () -> Unit = {},
    onNavigateToFontScale: () -> Unit = {},
    onNavigateToChatMode: () -> Unit = {},
    onNavigateToLanguage: () -> Unit = {},
    onNavigateToPromoCode: () -> Unit = {},
    onNavigateToRateUs: () -> Unit = {},
    onNavigateToRestorePurchase: () -> Unit = {},
    onNavigateToFaqSupport: () -> Unit = {},
    onNavigateToTermsOfUse: () -> Unit = {},
    onNavigateToPrivacyPolicy: () -> Unit = {},
    onNavigateToCommunityGuidelines: () -> Unit = {},
) {
    SettingsScreenContent(
        onNavigateBack = onNavigateBack,
        action = { action ->
            when (action) {
                SettingsUiAction.OnDefaultChatModeClicked -> onNavigateToChatMode()
                SettingsUiAction.OnLanguageClicked -> onNavigateToLanguage()
                SettingsUiAction.OnFontSizeClicked -> onNavigateToFontScale()
                SettingsUiAction.OnPromoCodeClicked -> onNavigateToPromoCode()
                SettingsUiAction.OnRateUsClicked -> onNavigateToRateUs()
                SettingsUiAction.OnRestorePurchaseClicked -> onNavigateToRestorePurchase()
                SettingsUiAction.OnFaqSupportClicked -> onNavigateToFaqSupport()
                SettingsUiAction.OnTermsOfUseClicked -> onNavigateToTermsOfUse()
                SettingsUiAction.OnPrivacyPolicyClicked -> onNavigateToPrivacyPolicy()
                SettingsUiAction.OnCommunityGuidelinesClicked -> onNavigateToCommunityGuidelines()
                else -> viewModel.handleAction(action)
            }
        },
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreenContent(
    action: (SettingsUiAction) -> Unit = {},
    onNavigateBack: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.Close, contentDescription = "Back")
                    }
                }
            )
        }
    ) { it ->

        Column(
            modifier = Modifier
                .padding(it)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = "Become Unlimited",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(vertical = 16.dp, horizontal = 8.dp)
                )
                Text(
                    text = "Answers from GPT-4o",
                    modifier = Modifier.padding(bottom = 8.dp, start = 8.dp, end = 8.dp)
                )
                Text(
                    text = "Infinite Art Generations",
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 8.dp)
                )
            }
            SettingItem(
                items = SettingItems.entries,
                action = { a ->
                    action(a)
                }
            )
        }
    }
}

@Composable
private fun SettingItem(
    items: List<SettingItems> = emptyList(),
    action: (SettingsUiAction) -> Unit = {},
) {
    val groupedItems = items.groupBy { it.category }

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        // Iterate over each group
        // Create a card for each item in the group
        itemsIndexed(groupedItems.entries.toList()) { index, items ->
            OutlinedCard(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                border = CardDefaults.outlinedCardBorder()
            ) {
                items.value.forEach { item ->
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                            .clickable { action(item.action) },
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.title,
                            modifier = Modifier.padding(8.dp)
                        )
                        Spacer(modifier = Modifier.padding(12.dp))
                        Text(
                            text = item.title,
                        )
                    }
                    if (index < groupedItems.entries.size - 1) {
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    }

                }
            }


        }
    }
}


enum class SettingsCategory {
    CATEGORY_1,
    CATEGORY_2,
    CATEGORY_3,
    CATEGORY_4,
    CATEGORY_5,
}

enum class SettingItems(
    val title: String,
    val icon: ImageVector,
    val action: SettingsUiAction,
    val category: SettingsCategory
) {
    DEFAULT_CHAT_MODE(
        "Default Chat Mode",
        Icons.Default.Chat,
        SettingsUiAction.OnDefaultChatModeClicked,
        SettingsCategory.CATEGORY_1
    ),
    LANGUAGE(
        "Language",
        Icons.Default.Language,
        SettingsUiAction.OnLanguageClicked,
        SettingsCategory.CATEGORY_2
    ),
    FONT_SIZE(
        "Font Size",
        Icons.Default.Title,
        SettingsUiAction.OnFontSizeClicked,
        SettingsCategory.CATEGORY_2
    ),
    PROMO_CODE(
        "Promo Code",
        Icons.Default.QrCode,
        SettingsUiAction.OnPromoCodeClicked,
        SettingsCategory.CATEGORY_3
    ),
    RATE_US(
        "Rate Us",
        Icons.Default.Star,
        SettingsUiAction.OnRateUsClicked,
        SettingsCategory.CATEGORY_4
    ),
    RESTORE_PURCHASE(
        "Restore Purchase",
        Icons.Default.Loop,
        SettingsUiAction.OnRestorePurchaseClicked,
        SettingsCategory.CATEGORY_4
    ),
    FAQ_SUPPORT(
        "FAQ & Support",
        Icons.Default.Support,
        SettingsUiAction.OnFaqSupportClicked,
        SettingsCategory.CATEGORY_4
    ),
    TERMS_OF_USE(
        "Terms of Use",
        Icons.Default.Book,
        SettingsUiAction.OnTermsOfUseClicked,
        SettingsCategory.CATEGORY_5
    ),
    PRIVACY_POLICY(
        "Privacy Policy",
        Icons.Default.PrivacyTip,
        SettingsUiAction.OnPrivacyPolicyClicked,
        SettingsCategory.CATEGORY_5
    ),
    COMMUNITY_GUIDELINES(
        "Community Guidelines",
        Icons.Default.People,
        SettingsUiAction.OnCommunityGuidelinesClicked,
        SettingsCategory.CATEGORY_5
    ),
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL)
@Composable
fun SettingsScreenPreview() {
    ChatbotTheme {
        SettingsScreen()
    }
}