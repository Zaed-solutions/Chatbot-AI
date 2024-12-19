package com.zaed.chatbot.data.source.remote

import android.net.Uri
import android.util.Log
import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.audio.SpeechRequest
import com.aallam.openai.api.audio.Transcription
import com.aallam.openai.api.audio.TranscriptionRequest
import com.aallam.openai.api.audio.Translation
import com.aallam.openai.api.audio.TranslationRequest
import com.aallam.openai.api.audio.Voice
import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.chat.chatMessage
import com.aallam.openai.api.core.Role
import com.aallam.openai.api.exception.InvalidRequestException
import com.aallam.openai.api.file.FileId
import com.aallam.openai.api.file.FileSource
import com.aallam.openai.api.file.FileUpload
import com.aallam.openai.api.file.Purpose
import com.aallam.openai.api.image.ImageCreation
import com.aallam.openai.api.image.ImageEdit
import com.aallam.openai.api.image.ImageSize
import com.aallam.openai.api.image.ImageURL
import com.aallam.openai.api.image.ImageVariation
import com.aallam.openai.api.message.MessageRequest
import com.aallam.openai.api.model.Model
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.api.moderation.ModerationRequest
import com.aallam.openai.api.thread.Thread
import com.aallam.openai.api.thread.ThreadId
import com.aallam.openai.client.OpenAI
import com.google.firebase.storage.FirebaseStorage
import com.zaed.chatbot.data.model.ChatQuery
import com.zaed.chatbot.data.model.FileType
import com.zaed.chatbot.data.model.MessageAttachment
import com.zaed.chatbot.ui.mainchat.components.ChatModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import okio.Source

class OpenAIRemoteDataSourceImpl(
    val openAI: OpenAI,
    val storage: FirebaseStorage
) : OpenAIRemoteDataSource {
    private val chatHistoryMap: MutableMap<String, MutableList<ChatMessage>> = mutableMapOf()
    @OptIn(BetaOpenAI::class)
    override fun createNewThread(): Flow<Result<Thread>> = flow {
        val thread = openAI.thread()
        emit(Result.success(thread))
    }

    override suspend fun listModels(): List<Model> = openAI.models()
    suspend fun retrieveModel(modelId: String): Model = openAI.model(ModelId(modelId))
    @OptIn(BetaOpenAI::class)
    override suspend fun sendPrompt(
        chatQuery: ChatQuery,
        isFirst: Boolean,
        modelId: ModelId
    ): Flow<Result<ChatCompletion>> = flow {
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
            chatCompletion.choices.forEach { choice->
                val assistantMessage = chatMessage {
                    role = ChatRole.Assistant
                    content {
                        choice.message.content?.let { text(it) }
                    }
                }
                messageHistory.add(assistantMessage)
            }
//            val message2 = openAI.message(
//                threadId = chatQuery.chatId,
//                request =  MessageRequest(
//                    role = Role.User,
//                    content = chatQuery.prompt
//                )
//            )
            emit(Result.success(chatCompletion))
        } catch (e: InvalidRequestException) {
            emit(Result.failure(e))
            e.printStackTrace()
        }
    }

    override fun uploadNewImage(uri: Uri): Flow<Result<String>> = callbackFlow {
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
        awaitClose { }
    }

    override fun uploadNewFile(attachment: MessageAttachment): Flow<Result<FileId>> = flow {

    }

    override suspend fun createImage(
        chatQuery: ChatQuery,
        n: Int,
        size: ImageSize
    ): Flow<Result<List<ImageURL>>> = flow {
        try {
            val result = openAI.imageURL( // or openAI.imageJSON
                creation = ImageCreation(
                    prompt = chatQuery.prompt,
                    model = ChatModel.AI_ART_GENERATOR.modelId,
                    n = 1,
                    size = ImageSize.is1024x1024
                )
            )
            emit(Result.success(result))
        } catch (e: InvalidRequestException) {
            e.printStackTrace()
            emit(Result.failure(e))
            Log.d("RemoteDataSourceImpl12", "createImage: ${e.message}")
        }
    }


    suspend fun editImage(
        modelId: String,
        fileName: String,
        imageSource: Source,
        maskSource: Source,
        prompt: String
    ) = openAI.imageURL(
        //Todo replace model and chat message with the params
        edit = ImageEdit(
            image = FileSource(name = fileName, source = imageSource),
            model = ModelId("dall-e-3"),
            mask = FileSource(name = "<filename>", source = maskSource),
            prompt = "a sunlit indoor lounge area with a pool containing a flamingo",
            n = 1,
            size = ImageSize.is1024x1024
        )
    )

    suspend fun createImageVariation(
        modelId: String,
        fileName: String,
        imageSource: Source,
        prompt: String
    ) = openAI.imageURL(
        variation = ImageVariation(
            image = FileSource(name = "<filename>", source = imageSource),
            model = ModelId("dall-e-3"),
            n = 1,
            size = ImageSize.is1024x1024
        )
    )

    suspend fun createSpeech(): ByteArray = openAI.speech(
        request = SpeechRequest(
            model = ModelId("tts-1"),
            input = "The quick brown fox jumped over the lazy dog.",
            voice = Voice.Alloy,
        )
    )

    suspend fun createTranscription(fileName: String, audioSource: Source): Transcription {
        val request = TranscriptionRequest(
            audio = FileSource(name = fileName, source = audioSource),
            model = ModelId("whisper-1"),
        )
        val transcription = openAI.transcription(request)
        return transcription
    }

    suspend fun createTranslation(audioSource: Source, fileName: String): Translation {
        val request = TranslationRequest(
            audio = FileSource(name = fileName, source = audioSource),
            model = ModelId("whisper-1"),
        )
        val translation = openAI.translation(request)
        return translation
    }

    suspend fun listFiles() = openAI.files()
    suspend fun uploadFile(source: FileSource): Boolean {
        val file = openAI.file(
            request = FileUpload(
                file = source,
                purpose = Purpose("fine-tune")
            )
        )
        return true
    }

    suspend fun deleteFile(fileId: FileId) = openAI.delete(fileId)
    suspend fun retrieveFile(fileId: FileId) = openAI.file(fileId)
    suspend fun retrieveFileContent(fileId: FileId) = openAI.download(fileId)
    suspend fun createModeration() = openAI.moderations(
        request = ModerationRequest(
            input = listOf("I want to kill them.")
        )
    )
//    private suspend fun uploadAttachment(
//         attachment: ByteArray
//    ): String {
//        return try {
//            val path = "images/${UUID.randomUUID()}"
//            val bucket = supabase.storage.from("IMAGES")
//            val key = bucket.upload(path = path, data =  attachment)
//            print("attachment uploaded: $key")
//            val url = supabase.storage.from("IMAGES").publicUrl(path)
//            println("attachment url: $url")
//                url
//
//        } catch (e: Exception) {
//            e.printStackTrace()
//            return ""
//        }
//
//    }
override suspend fun editImage(
    modelId: String,
    imageSource: Source,
    maskSource: Source,
    prompt: String
): Result<List<ImageURL>> {
    return try {
        Log.d("mogo", "editImage:in remote$ ")
        // Creating an image edit request
        val imageEditRequest = ImageEdit(
            image = FileSource(name = "image.png", source = imageSource),
            mask = FileSource(name = "mask.png", source = maskSource),
            prompt = prompt, // Description for editing
            n = 1

        )

        // Sending the request to OpenAI's API
        val result = openAI.imageURL(edit = imageEditRequest)
        Log.d("mogo", "editImage: $result")
        // Process the result as needed
        result.forEach {
            Log.d("initimage", "editImage: $it")
        }

        // Return the result with image URLs
        Result.success(result)
    } catch (e: Exception) {
        Log.d("mogo", "editImage: ${e.message}")
        e.printStackTrace()
        Result.failure(e)
    }
}

}