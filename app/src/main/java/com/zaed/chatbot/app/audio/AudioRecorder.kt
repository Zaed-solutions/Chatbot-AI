package com.zaed.chatbot.app.audio

import java.io.File

interface AudioRecorder {
    fun start(outputFile: File)
    fun stop()
}