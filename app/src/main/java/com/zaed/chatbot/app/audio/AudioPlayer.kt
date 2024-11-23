package com.zaed.chatbot.app.audio

import java.io.File

interface AudioPlayer {
    fun playFile(file: File)
    fun stop()
}