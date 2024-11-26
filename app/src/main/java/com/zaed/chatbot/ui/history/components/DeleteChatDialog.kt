package com.zaed.chatbot.ui.history.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.zaed.chatbot.R

@Composable
fun DeleteChatDialog(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    onDelete: () -> Unit
) {
    Dialog(
        properties = DialogProperties(),
        onDismissRequest = { onDismiss() }
    ) {
        Card(
            modifier = modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.extraLarge
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 36.dp)
            ) {
                Text(
                    text = stringResource(R.string.delete_chat),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                Text(
                    text = stringResource(R.string.this_chat_will_be_permanently_deleted_are_you_sure_you_want_to_proceed_with_the_deletion),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(top = 16.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedButton(onClick = { onDismiss() }) {
                        Text(text = stringResource(R.string.cancel))
                    }
                    Button(
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.onError
                        ),
                        onClick = {
                            onDelete()
                        }
                    ) {
                        Text(text = stringResource(R.string.ok))
                    }

                }
            }
        }

    }
}