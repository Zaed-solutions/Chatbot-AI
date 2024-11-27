package com.zaed.chatbot.ui.settings.promocode

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zaed.chatbot.R
import com.zaed.chatbot.ui.activity.SubscriptionAction
import com.zaed.chatbot.ui.settings.SettingsUiAction
import com.zaed.chatbot.ui.settings.SettingsViewModel
import com.zaed.chatbot.ui.theme.ChatbotTheme
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PromoCodeScreen(
    modifier: Modifier = Modifier,
    onSubscriptionAction: (SubscriptionAction) -> Unit = {},
    onNavigateBack: () -> Unit
) {
    PromoCodeScreenContent(
        modifier = modifier,
        onNavigateBack = onNavigateBack,
        onSubmitCode = { code ->
            onSubscriptionAction(SubscriptionAction.OnApplyPromoCode(code))
        })

}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun PromoCodeScreenContent(
    modifier: Modifier = Modifier,
    onSubmitCode: (String) -> Unit = {},
    onNavigateBack: () -> Unit = {}
) {
    Scaffold(modifier = modifier, topBar = {
        CenterAlignedTopAppBar(title = {
            Text(
                text = stringResource(id = R.string.promo_code),
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.titleLarge,
                fontSize = 20.sp,
            )
        }, navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Back")
            }
        })
    }) { innerPadding ->
        var promoCode by remember { mutableStateOf("") }
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {

            Text(
                text = stringResource(R.string.enter_promo_code),
                style = MaterialTheme.typography.headlineMedium,
            )
            Text(
                modifier = Modifier.padding(top = 16.dp),
                text = stringResource(R.string.if_you_received_a_referral_code_you_can_enter_the_code_here),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
            OutlinedTextField(
                value = promoCode,
                onValueChange = { promoCode = it },
                modifier = Modifier
                    .padding(top = 24.dp)
                    .fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
            )
            Spacer(modifier = Modifier.padding(8.dp))
            Button(
                onClick = {
                    onSubmitCode(promoCode)
                },
                enabled = promoCode.isNotBlank(),
                modifier = Modifier
                    .height(54.dp)
                    .fillMaxWidth(),
                shape = MaterialTheme.shapes.extraLarge
            ) {
                Text(
                    text = stringResource(R.string.submit),
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