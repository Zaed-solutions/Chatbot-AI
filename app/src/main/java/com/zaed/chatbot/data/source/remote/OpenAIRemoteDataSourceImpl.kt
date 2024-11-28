package com.zaed.chatbot.data.source.remote

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
import com.aallam.openai.api.file.FileId
import com.aallam.openai.api.file.FileSource
import com.aallam.openai.api.file.FileUpload
import com.aallam.openai.api.file.Purpose
import com.aallam.openai.api.image.ImageCreation
import com.aallam.openai.api.image.ImageEdit
import com.aallam.openai.api.image.ImageSize
import com.aallam.openai.api.image.ImageURL
import com.aallam.openai.api.image.ImageVariation
import com.aallam.openai.api.model.Model
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.api.moderation.ModerationRequest
import com.aallam.openai.client.OpenAI
import com.zaed.chatbot.data.model.ChatQuery
import com.zaed.chatbot.ui.mainchat.components.ChatModel

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okio.Source
import java.io.File
import java.util.UUID

class OpenAIRemoteDataSourceImpl(
    val openAI: OpenAI,
//    val supabase: SupabaseClient
) : OpenAIRemoteDataSource {

    override suspend fun listModels(): List<Model> = openAI.models()
    suspend fun retrieveModel(modelId: String): Model = openAI.model(ModelId(modelId))
    override suspend fun sendPrompt(
        chatQuery: ChatQuery,
        isFirst: Boolean,
        modelId: ModelId
    ): Flow<Result<ChatCompletion>> = flow {
        try {
            val message = if (chatQuery.promptAttachments.isEmpty()) {
                chatMessage {
                    role = ChatRole.User
                    content {
                        text(chatQuery.prompt)
                    }
                }
            } else {
//                val url = chatQuery.promptAttachments.first().byteArray?.let { uploadAttachment(it) }
//                if(url.isNullOrEmpty()) throw Exception("Attachment url is null or empty")
                chatMessage {
                    role = ChatRole.User
                    content {
                        text(chatQuery.prompt)
//                        image(url)
                    }
                }
            }
            val messages = mutableListOf(message)
            if (isFirst) {
                val titleMessage = ChatMessage(
                    role = ChatRole.System,
                    content = "Give a title for the other chat message no words before or after just the title"
                )
                messages.add(titleMessage)
            }
            val chatCompletionRequest = ChatCompletionRequest(
                model = modelId,
                messages = messages
            )
            emit(Result.success(openAI.chatCompletion(chatCompletionRequest)))
        } catch (e: Exception) {
            emit(Result.failure(e))
            e.printStackTrace()
        }
    }

    override suspend fun createImage(
        chatQuery: ChatQuery,
        n: Int,
        size: ImageSize
    ): Result<List<ImageURL>> {
        return try {
            Result.success(
                openAI.imageURL( // or openAI.imageJSON
                    creation = ImageCreation(
                        prompt = chatQuery.prompt,
                        model = ChatModel.AI_ART_GENERATOR.modelId,
                        n = 1,
                        size = ImageSize.is1024x1024
                    )
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
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
            model = ModelId("dall-e-2"),
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


}