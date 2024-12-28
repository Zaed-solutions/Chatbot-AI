package com.zaed.chatbot.ui.mainchat

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aallam.openai.api.model.ModelId
import com.zaed.chatbot.data.model.ChatQuery
import com.zaed.chatbot.data.model.MessageAttachment
import com.zaed.chatbot.data.repository.ChatRepository
import com.zaed.chatbot.data.repository.SettingsRepository
import com.zaed.chatbot.ui.mainchat.components.ChatModel
import com.zaed.chatbot.ui.util.ConnectivityObserver
import com.zaed.chatbot.ui.util.detectLanguage
import com.zaed.chatbot.ui.util.toMessageAttachments
import com.zaed.chatbot.ui.util.translateToEnglish
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readBytes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okio.Source
import okio.buffer
import okio.source
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.UUID

class MainChatViewModel(
    private val chatRepository: ChatRepository,
    private val connectivityObserver: ConnectivityObserver,
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(MainChatUiState())
    val uiState = _uiState.asStateFlow()

    fun init(chatId: String, androidId: String) {
        if (chatId.isNotBlank()) {
            fetchChat(chatId)
        } else {
            _uiState.update {
                it.copy(
                    threadId = UUID.randomUUID().toString(),
                    androidId = androidId
                )
            }
        }
    }


    private suspend fun checkInternetConnection() {
        connectivityObserver.isConnected
            .collect { result ->
                _uiState.update {
                    it.copy(internetConnected = result)
                }
            }
    }

    }
    fun decrementImageSubscriptionLimitCount() {
        viewModelScope.launch {
            settingsRepository.decrementUserImageFreeTrialCount(uiState.value.androidId)
        }
    }

    private fun fetchChat(chatId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            checkInternetConnection()
            if (uiState.value.internetConnected){
                chatRepository.getChatById(chatId).collect { result ->
                    result.onSuccess {
                        Log.d("MainChatViewModel", "fetchChat: $it")
                        _uiState.update { oldState ->
                            oldState.copy(
                                queries = it.reversed().toMutableList()
                            )
                        }
                    }.onFailure {
                        Log.e("MainChatViewModel", "fetchChat: ${it.message}")
                    }
                }
            }
        }
    }

    fun handleAction(action: MainChatUiAction) {
        when (action) {
            is MainChatUiAction.OnAddAttachment -> addAttachment(action.attachment)
            is MainChatUiAction.OnChangeModel -> clearChat(action.model)
            is MainChatUiAction.OnDeleteAttachment -> deleteAttachment(action.attachmentUri)
            MainChatUiAction.OnNewChatClicked -> clearChat()
            MainChatUiAction.OnSendPrompt -> sendPrompt()
            is MainChatUiAction.OnSendSuggestion -> sendSuggestion(action.suggestionPrompt)
            is MainChatUiAction.OnUpdatePrompt -> updatePrompt(action.text)
            is MainChatUiAction.OnStopAnimation -> stopAnimation()
            else -> Unit
        }
    }


    private fun listModels() {
        viewModelScope.launch(Dispatchers.IO) {
            val models = chatRepository.listModels()
            Log.d("MainChatViewModel", "listModels: $models")
        }
    }

    private fun stopAnimation() {
        viewModelScope.launch {
            Log.d("MainChatViewModel1", "${uiState.value.queries}")
            val updatedQueries = _uiState.value.queries
            if (updatedQueries.isNotEmpty()) {
                updatedQueries[0] = updatedQueries[0].copy(animateResponse = false)
            }
            _uiState.update { currentState ->
                currentState.copy(queries = updatedQueries, isAnimating = false)
            }
            Log.d("MainChatViewModel2", "${uiState.value.queries}")
        }
    }


    private fun updatePrompt(text: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(currentPrompt = text)
            }
        }
    }

    private fun sendSuggestion(prompt: String) {
        viewModelScope.launch(Dispatchers.IO) {
            checkInternetConnection()
            if (uiState.value.internetConnected){
                _uiState.update {
                    it.copy(currentPrompt = prompt)
                }
                sendPrompt()
            }
        }
    }

    private fun createImages(translatedPrompt: String) {
        val isFirstMessage = uiState.value.queries.isEmpty()
        viewModelScope.launch(Dispatchers.IO) {
            viewModelScope.launch(Dispatchers.IO) {
                val query = ChatQuery(
                    chatId = uiState.value.threadId,
                    prompt = translatedPrompt,
                    response = "",
                    promptAttachments = uiState.value.attachments,
                    isLoading = true,
                    animateResponse = true
                )
                _uiState.update { oldState ->
                    oldState.queries.add(0, query)
                    oldState.copy(
                        currentPrompt = "",
                        attachments = mutableListOf(),
                        isLoading = true,
                        imageHitTimes = oldState.imageHitTimes.plus(1),
                        totalHitTimes = oldState.totalHitTimes.plus(1)
                    )
                }
                chatRepository.createImage(
                    chatQuery = query,
                    n = 1,
                    size = com.aallam.openai.api.image.ImageSize.is256x256,
                    isFirstMessage = isFirstMessage
                ).collect { imageCreation ->
                    imageCreation.onSuccess { result ->
                        _uiState.update { oldState ->
                            oldState.copy(
                                queries = oldState.queries.map {
                                    if (it.isLoading) it.copy(
                                        isLoading = false,
                                        response = "",
                                        animateResponse = false,
                                        responseAttachments = result.toMessageAttachments()
                                    ) else it.copy(isLoading = false, animateResponse = false)
                                }.toMutableList(), isLoading = false, isAnimating = true
                            )
                        }
                        decrementImageSubscriptionLimitCount()
                    }.onFailure { error ->
                        Log.d("tetooo", "createImages: ${error.message}")
                        _uiState.update { oldState ->
                            oldState.copy(
                                queries = oldState.queries.map {
                                    if (it.isLoading) it.copy(
                                        isLoading = false,
                                        response = error.message ?: "",
                                        animateResponse = false,
                                    ) else it.copy(isLoading = false, animateResponse = false)
                                }.toMutableList(), isLoading = false, isAnimating = true
                            )
                        }
                    }
                }
            }
        }
    }

    private fun sendPrompt() {
        viewModelScope.launch {
            checkInternetConnection()
            if (uiState.value.internetConnected){

                when (uiState.value.selectedModel) {
                    ChatModel.GPT_4O_MINI -> createText(ChatModel.GPT_4O_MINI.modelId)
                    ChatModel.GPT_4O -> createText(ChatModel.GPT_4O.modelId)
                    ChatModel.AI_ART_GENERATOR -> {
                        detectLanguage(uiState.value.currentPrompt) { languageCode ->
                            if (languageCode == "ar") {
                                translateToEnglish(uiState.value.currentPrompt) { translatedPrompt ->
                                    createImages(translatedPrompt)
                                }
                            } else {
                                createImages(uiState.value.currentPrompt)
                            }
                        }

                    }
                }
            }
        }
    }

    private fun createText(modelId: ModelId) {
        val isFirstMessage = uiState.value.queries.isEmpty()
        viewModelScope.launch(Dispatchers.IO) {
            val query = ChatQuery(
                chatId = uiState.value.threadId,
                prompt = uiState.value.currentPrompt,
                response = "",
                promptAttachments = uiState.value.attachments,
                isLoading = true,
                animateResponse = true
            )
            _uiState.update { oldState ->
                oldState.queries.add(0, query)
                oldState.copy(
                    currentPrompt = "",
                    attachments = mutableListOf(),
                    isLoading = true,
                    textHitTimes = oldState.textHitTimes.plus(1),
                    totalHitTimes = oldState.totalHitTimes.plus(1)
                )
            }
            Log.d("MainChatViewModel25", "createText: $query")
            if (query.promptAttachments.isNotEmpty()) {
                query.promptAttachments.filter {
                    it.type == com.zaed.chatbot.data.model.FileType.IMAGE
                }.forEach { attachment ->
                    chatRepository.uploadNewImage(attachment.uri).collect { result ->
                        result.onSuccess { uri ->
                            Log.d("MainChatViewModel36", "createText: $uri")
                            chatRepository.sendPrompt(
                                query.copy(
                                    promptAttachments = listOf(
                                        attachment.copy(
                                            uri = uri.toUri()
                                        )
                                    )
                                ),
                                modelId,
                                isFirstMessage
                            ).collect { response ->
                                response.onSuccess { result ->
                                    _uiState.update { oldState ->
                                        oldState.copy(
                                            queries = oldState.queries.map {
                                                if (it.isLoading) it.copy(
                                                    isLoading = false,
                                                    response = result.choices.first().message.content.orEmpty(),
                                                    animateResponse = true,
//                                    responseAttachments = data.responseAttachments
                                                ) else it.copy(
                                                    isLoading = false,
                                                    animateResponse = false
                                                )
                                            }.toMutableList(), isLoading = false, isAnimating = true
                                        )

                                    }
                                }.onFailure { error ->
                                    _uiState.update { oldState ->
                                        oldState.copy(
                                            queries = oldState.queries.map {
                                                if (it.isLoading) it.copy(
                                                    isLoading = false,
                                                    response = error.message ?: "",
                                                    animateResponse = false,
                                                ) else it.copy(
                                                    isLoading = false,
                                                    animateResponse = false
                                                )
                                            }.toMutableList(), isLoading = false, isAnimating = true
                                        )
                                    }
                                }
                            }
                        }.onFailure { error ->
                            _uiState.update { oldState ->
                                oldState.copy(
                                    queries = oldState.queries.map {
                                        if (it.isLoading) it.copy(
                                            isLoading = false,
                                            response = error.message ?: "",
                                            animateResponse = false,
                                        ) else it.copy(isLoading = false, animateResponse = false)
                                    }.toMutableList(), isLoading = false, isAnimating = true
                                )
                            }
                        }
                    }
                }
                query.promptAttachments.filter {
                    it.type != com.zaed.chatbot.data.model.FileType.IMAGE
                }.forEach { attachment ->
                    chatRepository.sendPrompt(query, modelId, isFirstMessage).collect { response ->
                        response.onSuccess { result ->
                            _uiState.update { oldState ->
                                oldState.copy(
                                    queries = oldState.queries.map {
                                        if (it.isLoading) it.copy(
                                            isLoading = false,
                                            response = result.choices.first().message.content.orEmpty(),
                                            animateResponse = true,
                                        ) else it.copy(isLoading = false, animateResponse = false)
                                    }.toMutableList(), isLoading = false, isAnimating = true
                                )
                            }
                        }.onFailure { error ->
                            _uiState.update { oldState ->
                                oldState.copy(
                                    queries = oldState.queries.map {
                                        if (it.isLoading) it.copy(
                                            isLoading = false,
                                            response = error.message ?: "",
                                            animateResponse = false,
                                        ) else it.copy(isLoading = false, animateResponse = false)
                                    }.toMutableList(), isLoading = false, isAnimating = true
                                )
                            }
                        }
                    }
                }
            } else {
                chatRepository.sendPrompt(query, modelId, isFirstMessage).collect { response ->
                    response.onSuccess { result ->
                        _uiState.update { oldState ->
                            oldState.copy(
                                queries = oldState.queries.map {
                                    if (it.isLoading) it.copy(
                                        isLoading = false,
                                        response = result.choices.first().message.content.orEmpty(),
                                        animateResponse = true,
//                                    responseAttachments = data.responseAttachments
                                    ) else it.copy(isLoading = false, animateResponse = false)
                                }.toMutableList(), isLoading = false, isAnimating = true
                            )
                        }
                    }.onFailure { error ->
                        _uiState.update { oldState ->
                            oldState.copy(
                                queries = oldState.queries.map {
                                    if (it.isLoading) it.copy(
                                        isLoading = false,
                                        response = error.message ?: "",
                                        animateResponse = false,
                                    ) else it.copy(isLoading = false, animateResponse = false)
                                }.toMutableList(), isLoading = false, isAnimating = true
                            )
                        }
                    }
                }
            }
        }
    }


    private fun clearChat(selectedModel: ChatModel = ChatModel.GPT_4O_MINI) {
        viewModelScope.launch {
            _uiState.update {

                MainChatUiState(
                    selectedModel = selectedModel,
                    threadId = UUID.randomUUID().toString(),
                )
            }
        }
    }

    private fun addAttachment(attachment: MessageAttachment) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(attachments = it.attachments + attachment)
            }
        }
    }

    private fun deleteAttachment(uri: Uri) {
        viewModelScope.launch {
            _uiState.update { oldState ->
                oldState.copy(attachments = oldState.attachments.filter { it.uri != uri })
            }
        }
    }

    private fun editImage() {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("mogo", "editImage: in vm")
            val imageSource = downloadFileFromUrl()
            val maskSource = downloadFileFromUrl()

            if (imageSource == null) {
                throw Exception("Failed to download image from Firebase")
            }
            chatRepository.editImage(
                modelId = "dall-e-2",
                imageSource = imageSource,
                maskSource = maskSource ?: imageSource,
                prompt = "convert the car to bmw"
            )
        }

    }

    suspend fun downloadFileFromUrl(): Source? {
        return try {
            // Create Ktor client
            val client = HttpClient()

            // Download the file
            val response: HttpResponse =
                client.get("https://cdn.pixabay.com/photo/2015/10/01/17/17/car-967387_1280.png")

            // Read the response as byte array
            val bytes = response.readBytes()

            // Check the size of the file (optional)
            if (bytes.size > 4 * 1024 * 1024) { // 4MB size limit
                throw IOException("File size exceeds 4 MB")
            }
            val rgba = convertToRGBA(bytes)

            // Convert to Source (buffered input stream)
            val byteArrayOutputStream = ByteArrayOutputStream()
            byteArrayOutputStream.write(rgba)

            // Convert the byte array to a source
            val bufferedSource = byteArrayOutputStream.toByteArray().inputStream().source().buffer()

            // Close the client
            client.close()

            // Return the Source
            bufferedSource
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun convertToRGBA(imageBytes: ByteArray): ByteArray? {
        // Decode the byte array into a Bitmap
        val originalBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

        if (originalBitmap == null) {
            return null
        }

        // Create a new Bitmap with RGBA format (ARGB_8888 includes the Alpha channel)
        val rgbaBitmap = Bitmap.createBitmap(
            originalBitmap.width,
            originalBitmap.height,
            Bitmap.Config.ARGB_8888
        )

        // Create a Canvas to draw the original image onto the new Bitmap
        val canvas = Canvas(rgbaBitmap)
        val paint = Paint()
        paint.isFilterBitmap = true

        // Draw the original image on the new Bitmap (this converts it to RGBA)
        canvas.drawBitmap(originalBitmap, 0f, 0f, paint)

        // Convert the RGBA Bitmap to a byte array
        val byteArrayOutputStream = ByteArrayOutputStream()
        try {
            rgbaBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }

        return byteArrayOutputStream.toByteArray()
    }


}