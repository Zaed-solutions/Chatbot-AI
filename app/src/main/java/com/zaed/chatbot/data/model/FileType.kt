package com.zaed.chatbot.data.model

enum class FileType {
    IMAGE,
    ALL
}
fun String.toFileType(): FileType {
    return when (this.lowercase()) {
        "image" -> FileType.IMAGE
        else -> FileType.ALL
    }
}