package com.zaed.chatbot.ui.mainchat.components

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.billingclient.api.ProductDetails
import com.zaed.chatbot.R
import com.zaed.chatbot.ui.theme.ChatbotTheme
import com.zaed.chatbot.ui.util.Constants

@Composable
fun ProSubscriptionBottomSheet(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit = {},
    onRestore: () -> Unit = {},
    products: List<ProductDetails> = emptyList(),
    onContinue: (ProductDetails) -> Unit = {},
    onPrivacyTermsClicked: () -> Unit = {},
) {
    var selectedProduct by remember { mutableStateOf<ProductDetails?>(null) }
    var isLifeTimeSelected by remember { mutableStateOf(false) }
    var selectedIndex by remember { mutableStateOf(0) }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp).verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                modifier = Modifier.size(24.dp),
                onClick = { onDismiss() }
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.size(24.dp)
                )
            }
            FilledTonalButton(
                modifier = Modifier.heightIn(min = 24.dp),
                onClick = {
                    onRestore()
                    onDismiss()
                },
                contentPadding = PaddingValues(vertical = 4.dp, horizontal = 12.dp)
            ) {
                Text(
                    text = stringResource(R.string.restore),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Light

                )
            }
        }
        Text(
            modifier = Modifier.padding(vertical = 8.dp),
            text = stringResource(R.string.unlock_unlimited_access),
            style = MaterialTheme.typography.headlineMedium,
            fontSize = 29.sp,
            fontWeight = FontWeight.Bold
        )
        ProBenefit.entries.forEach { benefit ->
            ListItem(
                modifier = Modifier.padding(horizontal = 8.dp),
                colors = ListItemDefaults.colors(
                    containerColor = Color.Transparent,
                    headlineColor = MaterialTheme.colorScheme.onBackground,
                    supportingColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    leadingIconColor = MaterialTheme.colorScheme.primary
                ),
                headlineContent = {
                    Text(
                        text = stringResource(id = benefit.titleRes),
                        style = MaterialTheme.typography.titleLarge,
                        fontSize = 20.sp,
                        maxLines = 1,
                        fontWeight = FontWeight.Bold
                    )
                },
                supportingContent = {
                    Text(
                        text = stringResource(id = benefit.subtitleRes),
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                    )
                },
                leadingContent = {
                    Icon(
                        painter = painterResource(id = benefit.iconRes),
                        contentDescription = "Benefit Icon",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(30.dp)
                    )
                }
            )
        }
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(top = 16.dp)
//                .border(
//                    width = 1.dp,
//                    color = MaterialTheme.colorScheme.outlineVariant,
//                    shape = MaterialTheme.shapes.large
//                ),
//            horizontalArrangement = Arrangement.SpaceBetween,
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Text(
//                text = stringResource(R.string.enable_free_trial),
//                style = MaterialTheme.typography.titleMedium,
//                fontWeight = FontWeight.SemiBold,
//                modifier = Modifier.padding(start = 16.dp)
//            )
//            Switch(
//                checked = isFreeTrialEnabled,
//                onCheckedChange = {
//                    isFreeTrialEnabled = it
//                },
//                modifier = Modifier.padding(end = 16.dp, top = 2.dp, bottom = 2.dp)
//            )
//        }
        products.forEachIndexed { _, product ->
            SubscriptionItem(
                isSelected = selectedProduct == product,
                onClick = {
                    selectedProduct = product
                    Log.d("momo", "ProSubscriptionBottomSheet: ${selectedProduct?.name}")
                },
                name = product.name,
                formattedPrice = product.subscriptionOfferDetails?.first()?.pricingPhases?.pricingPhaseList?.first()?.formattedPrice
                    ?: "Unknown",
                isLifeTime = product.subscriptionOfferDetails?.first()?.pricingPhases?.pricingPhaseList?.first()?.billingPeriod == "P1W"
            )
        }
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
            onClick = {
                selectedProduct?.let {onContinue(it) }
                onDismiss()
            },
            shape = MaterialTheme.shapes.large,
        ) {
            Text(
                modifier = Modifier.padding(vertical = 8.dp),
                text = stringResource(R.string.continue_),
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.Center,
        ) {
            Text(
                text = stringResource(R.string.privacy_terms),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.clickable { onPrivacyTermsClicked() }
            )
            if (!isLifeTimeSelected) {
                Text(
                    text = stringResource(R.string.cancel_anytime),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.End,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onRestore() }
                )
            }
        }
    }
}

@Composable
fun SubscriptionItem(
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    onClick: () -> Unit = {},
    name: String = "Weekly Subscription",
    formattedPrice: String = "EGP 279.99",
    isLifeTime: Boolean = false,
) {
    Log.d("momo", " $name + SubscriptionItem: $isSelected")
    val selectedOutlineColor = MaterialTheme.colorScheme.onBackground
    val unSelectedOutlineColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
    OutlinedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        onClick = onClick,
        border = BorderStroke(
            width = 1.dp,
            color = if (isSelected) selectedOutlineColor else unSelectedOutlineColor
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = formattedPrice,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )

        }
        Text(
            modifier = Modifier.padding(start = 16.dp, bottom = 12.dp),
            text = if (isLifeTime) stringResource(id = R.string.billed_once) else stringResource(R.string.auto_renewal),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.outlineVariant,
        )
    }
}

enum class ProBenefit(val titleRes: Int, val subtitleRes: Int, val iconRes: Int) {
    ANSWERS_FROM_GPT_4O(
        R.string.answers_from_gpt_4o,
        R.string.faster_more_accurate_answers,
        R.drawable.ic_double_star
    ),
    INFINITE_IMAGE_GENERATION(
        R.string.infinite_image_generations,
        R.string.create_stunning_visuals,
        R.drawable.ic_image_1
    ),
    NO_LIMITS(R.string.no_limits, R.string.have_unlimited_dialogues, R.drawable.ic_chat),
    ADVANCED_AI_CAPABILITIES(
        R.string.advanced_ai_capabilities,
        R.string.upload_file_images_to_ask,
        R.drawable.ic_note
    ),
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showSystemUi = true, showBackground = true)
@Composable
private fun ProPreview() {
    ChatbotTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp)
        ) {
            ProSubscriptionBottomSheet(
            )
        }
    }
}