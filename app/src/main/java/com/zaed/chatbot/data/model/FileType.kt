package com.zaed.chatbot.data.model

enum class FileType {
    IMAGE,
    AUDIO,
    ALL
}
fun String.toFileType(): FileType {
    return when (this.lowercase()) {
        "image" -> FileType.IMAGE
        "audio" -> FileType.AUDIO
        else -> FileType.ALL
    }
}