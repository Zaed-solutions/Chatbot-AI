package com.zaed.chatbot.data.model

import android.net.Uri

data class MessageAttachment(
    val name: String = "",
    val type: FileType = FileType.ALL,
    val uri: Uri = Uri.EMPTY
)
