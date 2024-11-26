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
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.*
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

    // Default language is English
    var selectedLanguage by remember { mutableStateOf(Locale.US.toString()) }

    // Recognizer intent setup
    val recognizerIntent = remember(selectedLanguage) {
        Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, selectedLanguage) // Use the selected language
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 10000)
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 7000)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5)
        }
    }

    val sheetState = rememberModalBottomSheetState()
    var isListening by remember { mutableStateOf(false) }
    var speechText by remember { mutableStateOf("") }
    val amplitudes = remember { mutableStateListOf<Float>() }
    val scope = rememberCoroutineScope()
    var hasPermission by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted -> hasPermission = granted }

    val listener = remember {
        object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBeginningOfSpeech() {
                amplitudes.clear()
            }

            override fun onRmsChanged(rmsdB: Float) {
                val amplitude = rmsdB.coerceIn(0f, 10f) / 10f
                amplitudes.add(amplitude)
                if (amplitudes.size > 20) amplitudes.removeAt(0)
            }

            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}

            override fun onError(error: Int) {
                onResult("Error")
                scope.launch { sheetState.hide() }
            }

            override fun onResults(results: Bundle?) {
                val result =
                    results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull()
                Log.d("AudioRec1", result ?: "No speech detected.")
                speechText += result ?: ""
                onResult(speechText)
                scope.launch { sheetState.hide() }
            }

            override fun onPartialResults(partialResults: Bundle?) {
                val partial = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull()
                if (partial != null) {
                    speechText += partial
                }
            }

            override fun onEvent(eventType: Int, params: Bundle?) {}
        }
    }

    LaunchedEffect(Unit) {
        speechRecognizer.setRecognitionListener(listener)
    }

    // Modal bottom sheet for speech recognition
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
            // Language Switcher
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Switch for language selection
                Text("English")
                Switch(
                    checked = selectedLanguage == "ar", // Check if Arabic is selected
                    onCheckedChange = { isChecked ->
                        // Disable switch if speech is ongoing
                        if (!isListening) {
                            selectedLanguage = if (isChecked) "ar" else Locale.US.toString()
                        }
                    },
                    enabled = !isListening // Disable the switch when listening
                )
                Text("Arabic")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Microphone Button
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
                        .clickable {
                            if (isListening) {
                                speechRecognizer.stopListening()
                                isListening = false
                                scope.launch { sheetState.hide() }
                                onResult(speechText)
                            } else {
                                isListening = true
                                speechRecognizer.startListening(recognizerIntent)
                                scope.launch { sheetState.show() }
                            }
                        }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Frequency visualization (optional)
            RealTimeFrequencyVisualizer(amplitudes = amplitudes)

            Spacer(modifier = Modifier.height(16.dp))

            // Display speech text
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
        val barWidth = size.width / (barCount * 2)
        val centerY = size.height / 2

        amplitudes.forEachIndexed { index, amplitude ->
            val barHeight =
                (amplitude * size.height).coerceAtLeast(4.dp.toPx())
            val barX = index * barWidth * 2
            drawRoundRect(
                color = Color.Blue,
                topLeft = Offset(barX, centerY - barHeight / 2),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(4.dp.toPx())
            )
        }
    }
}
