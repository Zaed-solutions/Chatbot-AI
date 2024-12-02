package com.zaed.chatbot.ui.settings.mode

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.android.billingclient.api.ProductDetails
import com.zaed.chatbot.R
import com.zaed.chatbot.ui.activity.SubscriptionAction
import com.zaed.chatbot.ui.mainchat.components.ChatModel
import com.zaed.chatbot.ui.mainchat.components.ProSubscriptionBottomSheet
import com.zaed.chatbot.ui.settings.SettingsUiAction
import com.zaed.chatbot.ui.settings.SettingsViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatModeScreen(
    modifier: Modifier = Modifier,
    defaultChatMode: ChatModel,
    onSubscriptionAction: (SubscriptionAction) -> Unit = {},
    onSetDefaultChatMode: (ChatModel) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = koinViewModel(),
    isPro: Boolean = false,
    products: List<ProductDetails> = emptyList()
) {
    ChatModeScreenContent(
        modifier = modifier,
        defaultChatMode = defaultChatMode,
        onAction = { action ->
            when (action) {
                SettingsUiAction.OnBackPressed -> onNavigateBack()
                is SettingsUiAction.OnSetDefaultChatMode -> onSetDefaultChatMode(action.chatModel)
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
        isPro = isPro,
        products = products
    )

}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun ChatModeScreenContent(
    modifier: Modifier = Modifier,
    onAction: (SettingsUiAction) -> Unit = {},
    defaultChatMode: ChatModel = ChatModel.GPT_4O_MINI,
    isPro: Boolean = false,
    products: List<ProductDetails> = emptyList(),
) {
    var isBottomSheetVisible by remember { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.default_chat_mode),
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.titleLarge,
                        fontSize = 20.sp,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onAction(SettingsUiAction.OnBackPressed) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBackIos, contentDescription = "Back")
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier.padding(it),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            var selectedIndex by remember { mutableIntStateOf(defaultChatMode.ordinal) }
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                items(ChatModel.entries) { item ->
                    val isSelected = selectedIndex == item.ordinal
                    Surface(
                        onClick = {
                            if (item.isPaid && !isPro) {
                                isBottomSheetVisible = true
                            } else {
                                selectedIndex = item.ordinal
                                onAction(SettingsUiAction.OnSetDefaultChatMode(item))
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth(),
                        shape = MaterialTheme.shapes.large,
                        tonalElevation = if (isSelected) 2.dp else 0.dp,
                        border = BorderStroke(
                            width = 1.dp,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(
                                alpha = 0.5f
                            )
                        ),
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = item.iconRes),
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp),
                                contentDescription = "Back"
                            )
                            Spacer(modifier = Modifier.padding(8.dp))
                            Text(
                                text = stringResource(id = item.nameRes),
                                style = MaterialTheme.typography.bodyLarge,
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            if (item.isPaid)
                                Text(
                                    text = stringResource(R.string.paid),
                                    modifier = Modifier
                                        .background(
                                            MaterialTheme.colorScheme.primary,
                                            RoundedCornerShape(50.dp)
                                        )
                                        .padding(horizontal = 6.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                        }
                    }
                }
            }
            Text(
                text = stringResource(R.string.you_can_select_your_ai_model_as_your_default),
                style = MaterialTheme.typography.bodyMedium,
            )
            androidx.compose.animation.AnimatedVisibility(visible = isBottomSheetVisible) {
                ModalBottomSheet(
                    onDismissRequest = {
                        isBottomSheetVisible = false
                    },
                    sheetState = bottomSheetState,
                    shape = RectangleShape
                ) {
                    ProSubscriptionBottomSheet(
                        modifier = Modifier.fillMaxSize(),
                        onDismiss = { isBottomSheetVisible = false },
                        onRestore = {
                            onAction(SettingsUiAction.OnRestorePurchaseClicked)
                            isBottomSheetVisible = false
                        },
                        products = products,
                        onContinue = { product ->
                            onAction(
                                SettingsUiAction.OnUpgradeSubscription(
                                    product
                                )
                            )
                            isBottomSheetVisible = false
                        },
                        onPrivacyTermsClicked = {
                            onAction(SettingsUiAction.OnPrivacyTermsClicked)
                            isBottomSheetVisible = false
                        },
                    )
                }

            }
        }
    }
}


@Composable
@Preview
fun ChatModeScreenPreview() {
    ChatModeScreenContent()
}