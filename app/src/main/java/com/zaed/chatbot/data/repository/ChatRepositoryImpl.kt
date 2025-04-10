package com.zaed.chatbot.data.repository

import android.net.Uri
import android.util.Log
import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.file.FileId
import com.aallam.openai.api.image.ImageSize
import com.aallam.openai.api.image.ImageURL
import com.aallam.openai.api.model.Model
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.api.thread.Thread
import com.zaed.chatbot.data.model.ChatHistory
import com.zaed.chatbot.data.model.ChatQuery
import com.zaed.chatbot.data.model.MessageAttachment
import com.zaed.chatbot.data.source.local.ChatLocalDataSource
import com.zaed.chatbot.data.source.remote.OpenAIRemoteDataSource
import com.zaed.chatbot.ui.util.toMessageAttachments
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import okio.Source

class ChatRepositoryImpl(
    private val chatRemoteDataSource: OpenAIRemoteDataSource,
    private val chatLocalDataSource: ChatLocalDataSource
) : ChatRepository {
    override fun uploadNewImage(uri: Uri): Flow<Result<String>> = chatRemoteDataSource.uploadNewImage(uri)

    override suspend fun sendPrompt(
        chatQuery: ChatQuery,
        modelId: ModelId,
        isFirstMessage: Boolean
    ): Flow<Result<ChatCompletion>> = callbackFlow {
        chatRemoteDataSource.sendPrompt(chatQuery, isFirst = isFirstMessage, modelId).collect { result ->
            result.onSuccess { data ->
                Log.d("ChatRepositoryImpl", "sendPrompt received data: $data")
                chatLocalDataSource.saveChat(
                    chatQuery.copy(
                        isLoading = false,
                        animateResponse = false,
                        response = data.choices.first().message.content.toString()
                    )
                ).collect { result ->
                    result.onSuccess {
                        if (isFirstMessage) {
                            chatLocalDataSource.createChatHistory(
                                ChatHistory(
                                    chatId = chatQuery.chatId,
                                    lastResponse = data.choices.first().message.content.toString(),
                                    lastResponseTime = chatQuery.createdAtEpochSeconds,
                                    title = chatQuery.prompt
                                )
                            )
                        } else {
                            chatLocalDataSource.updateChatHistory(
                                ChatHistory(
                                    chatId = chatQuery.chatId,
                                    lastResponse = data.choices.first().message.content.orEmpty(),
                                    lastResponseTime = chatQuery.createdAtEpochSeconds,
                                )
                            ).collect {}
                        }
                    }
                }
                trySend(Result.success(data))
            }.onFailure {
                Log.e("ChatRepositoryImpl", "sendPrompt: $it")
                trySend(Result.failure(it))
            }
        }
        awaitClose()
    }



    override suspend fun createImage(
        chatQuery: ChatQuery, n: Int, size: ImageSize, isFirstMessage: Boolean
    ): Flow<Result<List<ImageURL>>> = flow {
        chatRemoteDataSource.createImage(chatQuery, n, size).collect { remoteResult ->
            remoteResult.onSuccess { remoteResult ->
                chatLocalDataSource.saveChat(
                    chatQuery.copy(
                        isLoading = false,
                        animateResponse = false,
                        response = remoteResult.first().revisedPrompt ?: "",
                        responseAttachments = remoteResult.toMessageAttachments()
                    )
                ).collect { result ->
                    result.onSuccess {
                        if (isFirstMessage) {
                            //todo chat title
                            chatLocalDataSource.createChatHistory(
                                ChatHistory(
                                    chatId = chatQuery.chatId,
                                    lastResponse = remoteResult.first().revisedPrompt ?: "",
                                    lastResponseTime = chatQuery.createdAtEpochSeconds,
                                    title = chatQuery.prompt,

                                    )
                            )
                        } else {
                            chatLocalDataSource.updateChatHistory(
                                ChatHistory(
                                    chatId = chatQuery.chatId,
                                    lastResponse = remoteResult.first().revisedPrompt ?: "",
                                    lastResponseTime = chatQuery.createdAtEpochSeconds,
                                )
                            ).collect {}
                        }
                    }
                }
                emit(Result.success(remoteResult))
            }.onFailure {
                Log.e("ChatRepositoryImpl", "createImage: $it")
                emit(Result.failure(it))
            }
        }
}



override suspend fun getChatById(chatId: String): Flow<Result<List<ChatQuery>>> {
    return chatLocalDataSource.getChatById(chatId)
}

override suspend fun getChatHistories(): Result<List<ChatHistory>> {
    return chatLocalDataSource.getChatHistories()
}

override suspend fun deleteChatHistory(chatId: String): Flow<Result<Unit>> {
    return chatLocalDataSource.deleteChatHistory(chatId)
}

override suspend fun updateChatHistory(chatHistory: ChatHistory): Flow<Result<Unit>> {
    return chatLocalDataSource.updateChatHistory(chatHistory)
}

override suspend fun createChatHistory(chatHistory: ChatHistory) {
    return chatLocalDataSource.createChatHistory(chatHistory)
}


}