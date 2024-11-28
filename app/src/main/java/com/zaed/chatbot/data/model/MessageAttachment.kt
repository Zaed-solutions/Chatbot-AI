package com.zaed.chatbot.data.model

import android.graphics.Bitmap
import android.net.Uri
import androidx.core.net.toUri
import com.zaed.chatbot.data.source.local.model.MessageAttachmentEntity
import java.io.File

data class MessageAttachment(
    val name: String = "",
    val type: FileType = FileType.ALL,
    val uri: Uri = Uri.EMPTY,

)
fun MessageAttachmentEntity.toMessageAttachment() =
    MessageAttachment(
        name = name,
        type = type.toFileType(),
        uri = uri.toUri()
    )