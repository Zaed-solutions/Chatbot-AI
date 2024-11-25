package com.zaed.chatbot.ui.mainchat.components

import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpeechRecognitionBottomSheet(
    onDismiss: () -> Unit = {},
    onResult: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val speechRecognizer = remember { SpeechRecognizer.createSpeechRecognizer(context) }
    val recognizerIntent = remember {
        Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        }
    }

    val sheetState = rememberModalBottomSheetState()
    var isListening by remember { mutableStateOf(false) }
    var speechText by remember { mutableStateOf("Say something...") }
    val amplitudes = remember { mutableStateListOf<Float>() } // List for amplitude visualization
    val scope = rememberCoroutineScope()
    var hasPermission by remember { mutableStateOf(false) }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
    }
    val listener = remember {
        object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                speechText = "Listening..."
            }

            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {
                val amplitude = rmsdB.coerceIn(0f, 10f) / 10f // Normalize RMS values to 0-1
                amplitudes.add(amplitude)
                if (amplitudes.size > 20) amplitudes.removeAt(0) // Limit to 20 bars
            }

            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {
                isListening = false
//                onResult(speechText)
                scope.launch { sheetState.hide() }
            }

            override fun onError(error: Int) {
                speechText = "Sorry, something went wrong."
                onResult("")
                scope.launch { sheetState.hide() }
            }

            override fun onResults(results: Bundle?) {
                val result =
                    results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull()
                Log.d("AudioRec1", result ?: "No speech detected.")
                speechText = result ?: "No speech detected."
                onResult(speechText)
                scope.launch { sheetState.hide() }

            }

            override fun onPartialResults(partialResults: Bundle?) {
                onResult(speechText)
                scope.launch { sheetState.hide() }
            }

            override fun onEvent(eventType: Int, params: Bundle?) {
                onResult(speechText)
                scope.launch { sheetState.hide() }
            }
        }
    }

    LaunchedEffect(Unit) {
        speechRecognizer.setRecognitionListener(listener)
    }

    // Bottom sheet with a microphone and live visualization
    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = {
            isListening = false
            speechRecognizer.stopListening()
            scope.launch { sheetState.hide() }
            onDismiss()
        }
    ) {
        if (!hasPermission) {
            permissionLauncher.launch(android.Manifest.permission.RECORD_AUDIO)
            return@ModalBottomSheet
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Microphone icon
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(if (isListening) Color.Green else Color.Red, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Mic",
                    tint = Color.White,
                    modifier = Modifier
                        .size(50.dp)
                        .background(Color.Transparent)
                        .clickable {
                            if (isListening) {
                                speechRecognizer.stopListening()
                                isListening = false
                                scope.launch { sheetState.hide() }
                            } else {
                                isListening = true
                                speechRecognizer.startListening(recognizerIntent)
                                scope.launch { sheetState.show() }
                            }
                        }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            // Frequency visualization
            RealTimeFrequencyVisualizer(amplitudes = amplitudes)
            Spacer(modifier = Modifier.height(16.dp))
            // Text for live feedback
            Text(
                text = speechText,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            speechRecognizer.destroy()
        }
    }
}

@Composable
fun RealTimeFrequencyVisualizer(
    amplitudes: List<Float>
) {

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .padding(horizontal = 16.dp)
    ) {
        val barCount = amplitudes.size
        val barWidth = size.width / (barCount * 2) // Space between bars
        val centerY = size.height / 2

        // Draw bars for the amplitudes
        amplitudes.forEachIndexed { index, amplitude ->
            val barHeight =
                (amplitude * size.height).coerceAtLeast(4.dp.toPx()) // Set a minimum height for visibility
            val barX = index * barWidth * 2 // Position bar
            drawRoundRect(
                color = Color.Blue,
                topLeft = Offset(barX, centerY - barHeight / 2),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(4.dp.toPx())
            )
        }
    }
}



