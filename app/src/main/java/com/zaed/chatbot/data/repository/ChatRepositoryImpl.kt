package com.zaed.chatbot.data.repository

import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.image.ImageSize
import com.aallam.openai.api.image.ImageURL
import com.aallam.openai.api.model.Model
import com.aallam.openai.api.model.ModelId
import com.zaed.chatbot.data.model.ChatHistory
import com.zaed.chatbot.data.model.ChatQuery
import com.zaed.chatbot.data.source.local.ChatLocalDataSource
import com.zaed.chatbot.data.source.remote.OpenAIRemoteDataSource
import kotlinx.coroutines.flow.Flow

class ChatRepositoryImpl(
    private val chatRemoteDataSource: OpenAIRemoteDataSource,
    private val chatLocalDataSource: ChatLocalDataSource
) : ChatRepository {
    override suspend fun sendPrompt(
        chatQuery: ChatQuery,
        modelId: ModelId,
        isFirstMessage: Boolean
    ): Flow<ChatCompletion> {
        val result = chatRemoteDataSource.sendPrompt(chatQuery, modelId)
        result.collect { data ->
            chatLocalDataSource.saveChat(
                chatQuery.copy(
                    isLoading = false,
                    animateResponse = false,
                    response = data.choices.first().message.content.toString()
                )
            ).collect { result ->
                result.onSuccess {
                    if (isFirstMessage) {
                        //todo chat title
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
                        )
                    }
                }
            }
        }
        return result
    }

    override suspend fun createImage(prompt: String, n: Int, size: ImageSize): List<ImageURL> =
        chatRemoteDataSource.createImage(prompt, n, size)

    override suspend fun listModels(): List<Model> = chatRemoteDataSource.listModels()


    override suspend fun getChatById(chatId: String): Flow<Result<List<ChatQuery>>> {
        return chatLocalDataSource.getChatById(chatId)
    }

    override suspend fun getChatHistories(): Flow<Result<List<ChatHistory>>> {
        return chatLocalDataSource.getChatHistories()
    }

    override suspend fun deleteChatHistory(chatId: String) {
        return chatLocalDataSource.deleteChatHistory(chatId)
    }

    override suspend fun updateChatHistory(chatHistory: ChatHistory) {
        return chatLocalDataSource.updateChatHistory(chatHistory)
    }

    override suspend fun createChatHistory(chatHistory: ChatHistory) {
        return chatLocalDataSource.createChatHistory(chatHistory)
    }

}