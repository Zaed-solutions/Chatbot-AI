package com.zaed.chatbot.ui.history.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.zaed.chatbot.R

@Composable
fun RenameHistoryDialog(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    onRename: (String) -> Unit
) {
    var name by remember {
        mutableStateOf("")
    }
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
                    text = stringResource(R.string.rename_chat),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = {
                        Text(text = stringResource(R.string.new_title))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp)
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
                        onClick = {
                            onRename(name)
                        }
                    ) {
                        Text(text = stringResource(R.string.ok))
                    }

                }
            }
        }

    }
}