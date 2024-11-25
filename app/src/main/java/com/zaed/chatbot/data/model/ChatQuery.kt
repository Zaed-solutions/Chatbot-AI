package com.zaed.chatbot.data.model

import com.zaed.chatbot.data.source.local.model.ChatQueryEntity
import com.zaed.chatbot.data.source.local.model.MessageAttachmentEntity.Companion.toMessageAttachmentEntity
import io.realm.kotlin.ext.toRealmList
import kotlinx.datetime.Clock

data class ChatQuery(
    val chatId: String = "",
    val createdAtEpochSeconds: Long = Clock.System.now().epochSeconds,
    val prompt: String = "",
    val response: String = "",
    val isLoading: Boolean = false,
    val animateResponse: Boolean = false,
    val promptAttachments: List<MessageAttachment> = emptyList(),
    val responseAttachments: List<MessageAttachment> = emptyList()
)

fun toChatQueryEntity(chatQuery: ChatQuery) =
    ChatQueryEntity().apply {
        chatId = chatQuery.chatId
        createdAtEpochSeconds = chatQuery.createdAtEpochSeconds
        prompt = chatQuery.prompt
        response = chatQuery.response
        animateResponse = chatQuery.animateResponse
        isLoading = chatQuery.isLoading
        promptAttachments = chatQuery.promptAttachments.map { toMessageAttachmentEntity(it) }.toRealmList()
        responseAttachments = chatQuery.responseAttachments.map { toMessageAttachmentEntity(it) }.toRealmList()
    }

