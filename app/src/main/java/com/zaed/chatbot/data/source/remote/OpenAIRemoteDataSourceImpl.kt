package com.zaed.chatbot.data.source.remote

import android.net.Uri
import android.util.Log
import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.chat.chatMessage
import com.aallam.openai.api.exception.InvalidRequestException
import com.aallam.openai.api.image.ImageCreation
import com.aallam.openai.api.image.ImageSize
import com.aallam.openai.api.image.ImageURL
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.google.firebase.storage.FirebaseStorage
import com.zaed.chatbot.data.model.ChatQuery
import com.zaed.chatbot.data.model.FileType
import com.zaed.chatbot.ui.mainchat.components.ChatModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class OpenAIRemoteDataSourceImpl(
    private val openAI: OpenAI,
    private val storage: FirebaseStorage
) : OpenAIRemoteDataSource {
    private val chatHistoryMap: MutableMap<String, MutableList<ChatMessage>> = mutableMapOf()


    override suspend fun sendPrompt(
        chatQuery: ChatQuery,
        isFirst: Boolean,
        modelId: ModelId
    ): Flow<Result<ChatCompletion>> = callbackFlow {
        val messageHistory = chatHistoryMap.getOrPut(chatQuery.chatId) { mutableListOf() }
        try {
            Log.d("RemoteDataSourceImpl", "sendPrompt: $chatQuery")
            val userMessage = if (chatQuery.promptAttachments.isEmpty()) {
                chatMessage {
                    role = ChatRole.User
                    content {
                        text(chatQuery.prompt)
                    }
                }
            } else {
                when (chatQuery.promptAttachments[0].type) {
                    FileType.IMAGE -> {
                        chatMessage {
                            role = ChatRole.User
                            content {
                                text(chatQuery.prompt)
                                image(chatQuery.promptAttachments[0].uri.toString())
                            }
                        }
                    }

                    FileType.ALL -> {
                        chatMessage {
                            role = ChatRole.User
                            content {
                                text("the file content is " + chatQuery.promptAttachments[0].text + "\n" + "the prompt is " + chatQuery.prompt)
                            }
                        }
                    }
                }

            }
            messageHistory.add(userMessage)
            messageHistory.forEach {
                Log.d("RemoteDataSourceImpl2", "sendPrompt: ${it.role}")
            }
            val chatCompletionRequest = ChatCompletionRequest(
                model = modelId,
                messages = messageHistory
            )
            val chatCompletion = openAI.chatCompletion(chatCompletionRequest)
            chatCompletion.choices.forEach { choice ->
                val assistantMessage = chatMessage {
                    role = ChatRole.Assistant
                    content {
                        choice.message.content?.let { text(it) }
                    }
                }
                messageHistory.add(assistantMessage)
            }
            trySend(Result.success(chatCompletion))
        } catch (e: InvalidRequestException) {
            trySend(Result.failure(e))
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
            trySend(Result.failure(e))
        }
        awaitClose {}
    }

    override fun uploadNewImage(uri: Uri): Flow<Result<String>> = callbackFlow {
        try {
            Log.d("RemoteDataSourceImpl5", "uploadNewFile: $uri")
            val timeStamp = System.currentTimeMillis().toString()
            val filePathAndName = "ChatGPT/$timeStamp"
            val ref = storage.reference.child(filePathAndName)
            val uploadTask = ref.putFile(uri)
            uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                ref.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result.toString()
                    Log.d("RemoteDataSourceImpl6", "uploadNewImage: $downloadUri")
                    trySend(Result.success(downloadUri))
                } else {
                    trySend(Result.failure(task.exception ?: Exception("Unknown error")))
                    task.exception?.printStackTrace()
                    Log.d("RemoteDataSourceImpl7", "uploadNewImage: ${task.exception?.message}")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            trySend(Result.failure(e))
            Log.d("RemoteDataSourceImpl13", "createImage: ${e.message}")
        }
        awaitClose { }
    }


    override suspend fun createImage(
        chatQuery: ChatQuery,
        n: Int,
        size: ImageSize
    ): Flow<Result<List<ImageURL>>> = callbackFlow {
        try {
            val result = openAI.imageURL(
                creation = ImageCreation(
                    prompt = chatQuery.prompt,
                    model = ChatModel.AI_ART_GENERATOR.modelId,
                    n = 1,
                    size = ImageSize.is1024x1024
                )
            )
            trySend(Result.success(result))
        } catch (e: InvalidRequestException) {
            e.printStackTrace()
            trySend(Result.failure(e))
            Log.d("RemoteDataSourceImpl12", "createImage: ${e.message}")
        } catch (e: Exception) {
            e.printStackTrace()
            trySend(Result.failure(e))
            Log.d("RemoteDataSourceImpl13", "createImage: ${e.message}")
        }
        awaitClose { }
    }
}