package com.zaed.chatbot.ui.settings

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Loop
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Support
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aallam.openai.api.image.internal.ImageResponseFormat.Companion.url
import com.android.billingclient.api.ProductDetails
import com.zaed.chatbot.R
import com.zaed.chatbot.ui.activity.SubscriptionAction
import com.zaed.chatbot.ui.mainchat.components.ProSubscriptionBottomSheet
import com.zaed.chatbot.ui.theme.ChatbotTheme
import com.zaed.chatbot.ui.util.Constants.PRIVACY_POLICY_URL
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = koinViewModel(),
    onNavigateBack: () -> Unit = {},
    onNavigateToFontScale: () -> Unit = {},
    onNavigateToChatMode: () -> Unit = {},
    onNavigateToPromoCode: () -> Unit = {},
    onNavigateToFaqSupport: () -> Unit = {},
    onNavigateToPrivacyPolicy: () -> Unit = {},
    onNavigateToCommunityGuidelines: () -> Unit = {},
    onSubscriptionAction: (SubscriptionAction) -> Unit = {},
    isPro: Boolean = false,
    products: List<ProductDetails> = emptyList(),
    changeLanguage: () -> Unit = {},
) {
    val context = LocalContext.current
    SettingsScreenContent(
        isPro = isPro,
        products = products,
        onAction = { action ->
            when (action) {
                SettingsUiAction.OnBackPressed -> onNavigateBack()
                SettingsUiAction.OnDefaultChatModeClicked -> onNavigateToChatMode()
                SettingsUiAction.OnFontSizeClicked -> onNavigateToFontScale()
                SettingsUiAction.OnPromoCodeClicked -> onNavigateToPromoCode()
                SettingsUiAction.OnRateUsClicked -> {
                    openPlayStoreListing(context)
                }
                SettingsUiAction.OnLanguageClicked -> {
                    changeLanguage()
                    onNavigateBack()
                }
                SettingsUiAction.OnRestorePurchaseClicked -> {
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse("https://play.google.com/store/account/subscriptions")
                    }
                    context.startActivity(intent)
                }
                SettingsUiAction.OnFaqSupportClicked -> onNavigateToFaqSupport()
                SettingsUiAction.OnPrivacyPolicyClicked -> {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(PRIVACY_POLICY_URL))
                    context.startActivity(intent)
                }
                SettingsUiAction.OnPrivacyTermsClicked -> onNavigateToPrivacyPolicy()
                SettingsUiAction.OnCommunityGuidelinesClicked -> onNavigateToCommunityGuidelines()
                is SettingsUiAction.OnUpgradeSubscription -> onSubscriptionAction(
                    SubscriptionAction.UpgradeSubscription(
                        action.productDetails
                    )
                )
                is SettingsUiAction.OnSubmitPromoCode -> onSubscriptionAction(
                    SubscriptionAction.OnApplyPromoCode(action.promoCode)
                )
                SettingsUiAction.OnRestorePurchaseClicked -> onSubscriptionAction(SubscriptionAction.RestoreSubscription)
                SettingsUiAction.OnCancelSubscription -> onSubscriptionAction(SubscriptionAction.ManageSubscription)
                else -> viewModel.handleAction(action)
            }
        },
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreenContent(
    modifier: Modifier = Modifier,
    onAction: (SettingsUiAction) -> Unit = {},
    isPro: Boolean = false,
    products: List<ProductDetails> = emptyList()
) {
    var isBottomSheetVisible by remember { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    Scaffold(modifier = modifier, topBar = {
        TopAppBar(title = {
            Text(
                text = stringResource(id = R.string.settings), fontWeight = FontWeight.Bold
            )
        }, actions = {
            IconButton(onClick = { onAction(SettingsUiAction.OnBackPressed) }) {
                Icon(Icons.Default.Close, contentDescription = "Back")
            }
        })
    }) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
            SettingItems(items = SettingItems.entries,
                isPro = isPro,
                onPromoClicked = { isBottomSheetVisible = true },
                action = { action ->
                    onAction(action)
                })
            AnimatedVisibility(visible = isBottomSheetVisible) {
                ModalBottomSheet(
                    onDismissRequest = {
                        isBottomSheetVisible = false
                    }, sheetState = bottomSheetState, shape = RectangleShape
                ) {
                    ProSubscriptionBottomSheet(
                        modifier = Modifier.fillMaxSize(),
                        onDismiss = { isBottomSheetVisible = false },
                        onRestore = { onAction(SettingsUiAction.OnRestorePurchaseClicked) },
                        products = products,
                        onContinue = { product ->
                            onAction(
                                SettingsUiAction.OnUpgradeSubscription(
                                    product
                                )
                            )
                        },
                        onPrivacyTermsClicked = { onAction(SettingsUiAction.OnPrivacyTermsClicked) },
                    )
                }

            }
        }
    }
}

@Composable
private fun SettingItems(
    items: List<SettingItems> = emptyList(),
    isPro: Boolean = false,
    action: (SettingsUiAction) -> Unit = {},
    onPromoClicked: () -> Unit,
) {
    Log.d("s0s0", "SettingItems: $items")
    Log.d("s0s0", "SettingItems: $isPro")
    Log.d("s0s0", "SettingItems: $action")
    val groupedItems = items.groupBy { it.category }
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        if (!isPro) {
            item {
                PromoCard(
                    onPromoClicked = onPromoClicked
                )
            }
        }
        items(groupedItems.entries.toList()) { items ->
            OutlinedCard(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                border = CardDefaults.outlinedCardBorder()
            ) {
                items.value.forEachIndexed { innerIndex, item ->
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                            .clickable { action(item.action) },
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = stringResource( item.title),
                            modifier = Modifier.padding(8.dp)
                        )
                        Spacer(modifier = Modifier.padding(12.dp))
                        Text(
                            text = stringResource(id = item.title),
                        )
                    }
                    if (innerIndex < groupedItems.entries.size - 1) {
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    }

                }
            }


        }
    }
}

@Composable
private fun PromoCard(
    modifier: Modifier = Modifier,
    onPromoClicked: () -> Unit,
) {
    Surface(
        onClick = {
            onPromoClicked()
        },
        shape = MaterialTheme.shapes.large,
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        color = MaterialTheme.colorScheme.primary
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = stringResource(R.string.become_unlimited),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_double_star),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = stringResource(id = R.string.answers_from_gpt_4o),
                    style = MaterialTheme.typography.titleSmall
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_image_1),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = stringResource(R.string.infinite_art_generations),
                    style = MaterialTheme.typography.titleSmall
                )
            }
        }
    }
}


enum class SettingsCategory {
    CATEGORY_1, CATEGORY_2, CATEGORY_3, CATEGORY_4;
}

enum class SettingItems(
    @StringRes val title: Int,
    val icon: ImageVector,
    val action: SettingsUiAction,
    val category: SettingsCategory
) {
    DEFAULT_CHAT_MODE(
        R.string.default_chat_mode,
        Icons.AutoMirrored.Filled.Chat,
        SettingsUiAction.OnDefaultChatModeClicked,
        SettingsCategory.CATEGORY_1
    ),
    FONT_SIZE(
        R.string.font_size,
        Icons.Default.Title,
        SettingsUiAction.OnFontSizeClicked,
        SettingsCategory.CATEGORY_1
    ),
    Language(
        R.string.change_language,
        Icons.Default.Language,
        SettingsUiAction.OnLanguageClicked,
        SettingsCategory.CATEGORY_1
    ),
    PROMO_CODE(
        R.string.promo_code,
        Icons.Default.QrCode,
        SettingsUiAction.OnPromoCodeClicked,
        SettingsCategory.CATEGORY_2
    ),
    RATE_US(
        R.string.rate_us,
        Icons.Default.Star,
        SettingsUiAction.OnRateUsClicked,
        SettingsCategory.CATEGORY_3
    ),
    RESTORE_PURCHASE(
        R.string.restore_purchase,
        Icons.Default.Loop,
        SettingsUiAction.OnRestorePurchaseClicked,
        SettingsCategory.CATEGORY_3
    ),
    FAQ_SUPPORT(
        R.string.faq_support,
        Icons.Default.Support,
        SettingsUiAction.OnFaqSupportClicked,
        SettingsCategory.CATEGORY_3
    ),
    PRIVACY_POLICY(
        R.string.privacy_policy,
        Icons.Default.PrivacyTip,
        SettingsUiAction.OnPrivacyPolicyClicked,
        SettingsCategory.CATEGORY_4
    ),
    COMMUNITY_GUIDELINES(
        R.string.community_guidelines,
        Icons.Default.People,
        SettingsUiAction.OnCommunityGuidelinesClicked,
        SettingsCategory.CATEGORY_4
    ),
}


fun openPlayStoreListing(context: Context) {
    try {
        context.startActivity(Intent(Intent.ACTION_VIEW,
            Uri.parse("market://details?id=${context.packageName}")))
    } catch (e: ActivityNotFoundException) {
        context.startActivity(Intent(Intent.ACTION_VIEW,
            Uri.parse("https://play.google.com/store/apps/details?id=${context.packageName}")))
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL)
@Composable
fun SettingsScreenPreview() {
    ChatbotTheme {
        SettingsScreenContent()
    }
}