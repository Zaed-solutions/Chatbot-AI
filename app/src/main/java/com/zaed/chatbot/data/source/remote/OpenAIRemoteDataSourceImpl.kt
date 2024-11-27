package com.zaed.chatbot.data.source.remote

import android.util.Log
import com.aallam.openai.api.audio.SpeechRequest
import com.aallam.openai.api.audio.Transcription
import com.aallam.openai.api.audio.TranscriptionRequest
import com.aallam.openai.api.audio.Translation
import com.aallam.openai.api.audio.TranslationRequest
import com.aallam.openai.api.audio.Voice
import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionChunk
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.file.FileId
import com.aallam.openai.api.file.FileSource
import com.aallam.openai.api.file.FileUpload
import com.aallam.openai.api.file.Purpose
import com.aallam.openai.api.image.ImageCreation
import com.aallam.openai.api.image.ImageEdit
import com.aallam.openai.api.image.ImageSize
import com.aallam.openai.api.image.ImageVariation
import com.aallam.openai.api.model.Model
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.api.moderation.ModerationRequest
import com.aallam.openai.client.OpenAI
import com.zaed.chatbot.data.model.ChatQuery
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okio.Source

class OpenAIRemoteDataSourceImpl(
    val openAI: OpenAI
) : OpenAIRemoteDataSource {

    suspend fun listModels(): List<Model> = openAI.models()
    suspend fun retrieveModel(modelId: String): Model = openAI.model(ModelId(modelId))
    override suspend fun sendPrompt(chatQuery: ChatQuery): Flow<ChatCompletion> = flow {
        val chatCompletionRequest = ChatCompletionRequest(
            model = ModelId("gpt-4o-mini"),
            messages = listOf(
                ChatMessage(
                    role = ChatRole.User,
                    content = chatQuery.prompt
                )
            )
        )

        emit(openAI.chatCompletion(chatCompletionRequest))


    }

    suspend fun createImage(
        prompt: String,
        n: Int = 1,
        size: ImageSize = ImageSize.is1024x1024
    ) = openAI.imageURL( // or openAI.imageJSON
        //Todo replace model and chat message with the params
        creation = ImageCreation(
            prompt = "A cute baby sea otter",
            model = ModelId("dall-e-3"),
            n = 2,
            size = ImageSize.is1024x1024
        )
    )

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
        //Todo replace model and chat message with the params
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


}