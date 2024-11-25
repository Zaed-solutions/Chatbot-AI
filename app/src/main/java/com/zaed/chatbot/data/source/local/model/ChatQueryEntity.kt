package com.zaed.chatbot.data.source.local.model

import com.zaed.chatbot.data.model.ChatQuery
import com.zaed.chatbot.data.model.FileType
import com.zaed.chatbot.data.model.MessageAttachment
import com.zaed.chatbot.data.model.toMessageAttachment
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import kotlinx.datetime.Clock

class ChatQueryEntity : RealmObject {
    var chatId: String = ""
    var createdAtEpochSeconds: Long = Clock.System.now().epochSeconds
    var prompt: String = ""
    var response: String = ""
    var isLoading: Boolean = false
    var animateResponse: Boolean = false
    var promptAttachments: RealmList<MessageAttachmentEntity> = realmListOf()
    var responseAttachments: RealmList<MessageAttachmentEntity> = realmListOf()
}

fun ChatQueryEntity.toChatQuery() =
    ChatQuery(
        chatId = chatId,
        createdAtEpochSeconds = createdAtEpochSeconds,
        prompt = prompt,
        response = response,
        animateResponse = animateResponse,
        isLoading = isLoading,
        promptAttachments = promptAttachments.map { it.toMessageAttachment() },
        responseAttachments = responseAttachments.map { it.toMessageAttachment() }
    )


class MessageAttachmentEntity : RealmObject {
    var uri: String = ""
    var type: String = FileType.ALL.name
    var name: String = ""

    companion object {
        fun toMessageAttachmentEntity(messageAttachment: MessageAttachment) =
            MessageAttachmentEntity().apply {
                name = messageAttachment.name
                type = messageAttachment.type.name
                uri = messageAttachment.uri.toString()
            }
    }
}