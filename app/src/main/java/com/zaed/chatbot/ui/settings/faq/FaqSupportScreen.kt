package com.zaed.chatbot.ui.settings.faq

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zaed.projecttemplate.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FaqSupportScreen(
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.faq_support),
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBackIos, contentDescription = stringResource(R.string.back))
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = stringResource(R.string.faq_support),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.padding(8.dp))
            var expanded1 by remember { mutableStateOf(false) }
            var expanded2 by remember { mutableStateOf(false) }
            var expanded3 by remember { mutableStateOf(false) }
            var expanded4 by remember { mutableStateOf(false) }
            var expanded5 by remember { mutableStateOf(false) }
            OutlinedCard({

            }) {
                Column(modifier = Modifier.padding(16.dp)) {
                    ExpandedFaqItem(
                        title= stringResource(R.string.faq_1_title),
                        body= stringResource(R.string.faq_1_body).trimIndent(),
                        onClick = {
                            expanded1 = !expanded1
                            expanded2 = false
                            expanded3 = false
                            expanded4 = false
                            expanded5 = false
                        },
                        expanded = expanded1
                    )
                    HorizontalDivider(modifier = Modifier.padding(8.dp))
                    ExpandedFaqItem(
                        title= stringResource(R.string.faq_2_title),
                        body= stringResource(R.string.faq_2_body).trimIndent(),
                        onClick = {
                            expanded2 = !expanded2
                            expanded1 = false
                            expanded3 = false
                            expanded4 = false
                            expanded5 = false
                        },
                        expanded = expanded2
                    )
                    HorizontalDivider(modifier = Modifier.padding(8.dp))
                    ExpandedFaqItem(
                        title= stringResource(R.string.faq_3_title),
                        body= stringResource(R.string.faq_3_body).trimIndent(),
                        onClick = {
                            expanded3 = !expanded3
                            expanded1 = false
                            expanded2 = false
                            expanded4 = false
                            expanded5 = false
                        },
                        expanded = expanded3
                    )
                    HorizontalDivider(modifier = Modifier.padding(8.dp))
                    ExpandedFaqItem(
                        title= stringResource(R.string.faq_4_title),
                        body= stringResource(R.string.faq_4_body).trimIndent(),
                        onClick = {
                            expanded4 = !expanded4
                            expanded1 = false
                            expanded2 = false
                            expanded3 = false
                            expanded5 = false
                        },
                        expanded = expanded4
                    )
                    HorizontalDivider(modifier = Modifier.padding(8.dp))
                    ExpandedFaqItem(
                        title= stringResource(R.string.faq_5_title),
                        body= stringResource(R.string.faq_5_body).trimIndent(),
                        onClick = {
                            expanded5 = !expanded5
                            expanded1 = false
                            expanded2 = false
                            expanded3 = false
                            expanded4 = false
                        },
                        expanded = expanded5
                    )
                }
            }
        }
    }

}

@Composable
private fun ColumnScope.ExpandedFaqItem(
    title: String,
    body: String,
    onClick: () -> Unit,
    expanded: Boolean,
) {
    Row(
        Modifier
            .clickable {
                onClick()
            }
            .padding(8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            imageVector = if(expanded)Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
            contentDescription = "show more"
        )
    }
    AnimatedVisibility(expanded) {
        Spacer(modifier = Modifier.padding(8.dp))
        Text(
            body,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
@Preview
fun FaqSupportScreenPreview() {
    FaqSupportScreen(onNavigateBack = {})
}