package com.zaed.chatbot.ui.util

import android.util.Log
import com.google.mlkit.nl.languageid.LanguageIdentification
import com.google.mlkit.nl.languageid.LanguageIdentifier
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.TranslatorOptions

fun detectLanguage(text: String, onDetected: (String) -> Unit) {
    val languageIdentifier: LanguageIdentifier = LanguageIdentification.getClient()

    languageIdentifier.identifyLanguage(text)
        .addOnSuccessListener { languageCode ->
            Log.d("LanguageDetection", "Detected language: $languageCode")
            onDetected(languageCode)
        }
        .addOnFailureListener { exception ->
            Log.e("LanguageDetection", "Language identification failed", exception)
            onDetected("unknown")
        }
}
fun translateToEnglish(text: String, onTranslation: (String) -> Unit) {
    // Check if the source text is Arabic
    val options = TranslatorOptions.Builder()
        .setSourceLanguage(TranslateLanguage.ARABIC)
        .setTargetLanguage(TranslateLanguage.ENGLISH)
        .build()

    val translator = com.google.mlkit.nl.translate.Translation.getClient(options)

    translator.translate(text)
        .addOnSuccessListener { translatedText ->
            Log.d("Translation", "Translation successful: $translatedText")
            onTranslation(translatedText)
        }
        .addOnFailureListener { exception ->
            Log.e("Translation", "Translation failed", exception)
            onTranslation(text) // Fallback to original text if translation fails
        }
}