package com.zaed.chatbot.ui.mainchat.components

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zaed.chatbot.ui.theme.ChatbotTheme
import com.zaed.chatbot.R

@Composable
fun ChatModelItem(
    modifier: Modifier = Modifier,
    isSelected: Boolean,
    @StringRes
    titleRes: Int,
    @StringRes
    subtitleRes: Int,
    @DrawableRes
    trailingIconRes: Int,
) {
    Row(
        modifier = modifier.width(180.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if(isSelected){
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(height = 24.dp, width = 16.dp)
            )
        } else {
            Spacer(modifier = Modifier.size(24.dp))
        }
        Column(
            modifier = Modifier.weight(1f).padding(start = 8.dp)
        ) {
            Text(
                text = stringResource(id = titleRes),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = stringResource(id = subtitleRes),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Icon(
            painter = painterResource(id = trailingIconRes),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(16.dp)
        )
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
private fun ChatModelItemPreview() {
    ChatbotTheme {
        ChatModelItem(
            isSelected = true,
            titleRes = R.string.gpt_4o_mini,
            subtitleRes = R.string.smart_and_fast,
            trailingIconRes = R.drawable.ic_star
        )
    }
}