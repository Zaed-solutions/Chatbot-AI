package com.zaed.chatbot.ui.mainchat

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.android.billingclient.api.ProductDetails
import com.zaed.chatbot.R
import com.zaed.chatbot.data.model.ChatQuery
import com.zaed.chatbot.data.model.FileType
import com.zaed.chatbot.data.model.MessageAttachment
import com.zaed.chatbot.ui.activity.SubscriptionAction
import com.zaed.chatbot.ui.mainchat.components.ChatModel
import com.zaed.chatbot.ui.mainchat.components.EmptyChat
import com.zaed.chatbot.ui.mainchat.components.MainChatBottomBar
import com.zaed.chatbot.ui.mainchat.components.MainChatTopBar
import com.zaed.chatbot.ui.mainchat.components.ProSubscriptionBottomSheet
import com.zaed.chatbot.ui.mainchat.components.QueryList
import com.zaed.chatbot.ui.mainchat.components.SpeechRecognitionBottomSheet
import com.zaed.chatbot.ui.theme.ChatbotTheme
import com.zaed.chatbot.ui.util.createImageFile
import com.zaed.chatbot.ui.util.downloadImage
import com.zaed.chatbot.ui.util.downloadImageWithMediaStore
import com.zaed.chatbot.ui.util.getFileNameFromUri
import com.zaed.chatbot.ui.util.readPdfContent
import com.zaed.chatbot.ui.util.readWordContent
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.text.NumberFormat
import java.util.Locale

private val TAG = "MainChatScreen"

@Composable
fun MainChatScreen(
    modifier: Modifier = Modifier,
    viewModel: MainChatViewModel = koinViewModel(),
    chatId: String = "",
    isPro: Boolean = false,
    products: List<ProductDetails> = emptyList(),
    onSubscriptionAction: (SubscriptionAction) -> Unit = {},
    onNavigateToHistoryScreen: () -> Unit = {},
    onNavigateToPersonalizationScreen: () -> Unit = {},
    onNavigateToSettingsScreen: () -> Unit = {},
    onNavigateToPrivacyAndTerms: () -> Unit = {},
    onDecrementFreeTrialCount: () -> Unit,
    freeTrialCount: Int,
    imageFreeTrialCount: Int,
    subscriptionName: String?,
    androidId: String,
) {
    val context = LocalContext.current
    Log.d("tenoo", "mainChatScreen: ${isPro}")
    Log.d("tenoo2", "mainChatScreen: $imageFreeTrialCount")
    Log.d("tenoo2", "mainChatScreen: $subscriptionName")
    Log.d("tenoo2", "mainChatScreen: $androidId")

    val state by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(true) {
        viewModel.init(chatId, androidId)
    }
    val imagePicker =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            uri?.let {
                val attachment = MessageAttachment(
                    uri = it,
                    type = FileType.IMAGE,
                )
                viewModel.handleAction(MainChatUiAction.OnAddAttachment(attachment, ""))
            }
        }
    val filePicker =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            result.data?.data?.let { fileUri ->
                val mimeType = context.contentResolver.getType(fileUri)
                Log.d(TAG, "MainChatScreen: file picker $mimeType")
                val fileName = getFileNameFromUri(context, fileUri)
                var fileContent = ""
                // Filter MIME types for PDF, Word, and Excel files
                if (mimeType != null) {
                    when {
                        mimeType == "application/pdf" -> {
                            fileContent = readPdfContent(context, fileUri)
                        }

                        mimeType == "application/msword" || mimeType == "application/vnd.openxmlformats-officedocument.wordprocessingml.document" -> {
                            fileContent = readWordContent(context, fileUri)
                            Log.d(TAG, "MainChatScreen: file picker $fileContent")

                        }

                        mimeType == "application/vnd.ms-excel" || mimeType == "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" -> {
                            fileContent = readWordContent(context, fileUri)
                        }

                        else -> {
                            // Handle invalid file type
                            Toast.makeText(
                                context,
                                "Invalid file type. Please select a PDF, Word, or Excel file.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    // Handle null MIME type (shouldn't happen, but can be a fallback)
                    Toast.makeText(
                        context,
                        "Unable to determine the file type.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                Log.d(TAG, "MainChatScreen: file picker $fileUri")
                val attachment =
                    MessageAttachment(
                        name = fileName ?: "", uri = fileUri, type = FileType.ALL,
                        text = fileContent
                    )
                viewModel.handleAction(MainChatUiAction.OnAddAttachment(attachment, ""))

            }
        }
    val scope = rememberCoroutineScope()
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    val cameraCaptureLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success && photoUri != null) {
                val attachment =
                    MessageAttachment(
                        uri = photoUri ?: Uri.EMPTY,
                        type = FileType.IMAGE,
                    )
                viewModel.handleAction(MainChatUiAction.OnAddAttachment(attachment, ""))
            } else {
                Log.d(TAG, "Image capture failed.")
            }
        }
    var previewImageFullScreen by remember { mutableStateOf(false to "") }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            scope.launch {
                downloadImage(context, previewImageFullScreen.second)
            }
        } else {
            Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    var recordingBottomSheetVisible by remember { mutableStateOf(false) }
    MainChatScreenContent(
        isConnected = state.internetConnected,
        modifier = modifier,
        onAction = { action ->
            when (action) {
                is MainChatUiAction.OnChangeModel -> {
                    Log.d("tenoo2", "mainChatScreen: ${action.model}")
                    if ((action.model == ChatModel.AI_ART_GENERATOR && imageFreeTrialCount > 0) || freeTrialCount > 0) {
                        viewModel.handleAction(action)
                    } else if (action.model != ChatModel.AI_ART_GENERATOR) {
                        viewModel.handleAction(action)
                    } else {
                        Toast.makeText(
                            context,
                            "you exceeded your limit $imageFreeTrialCount",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                MainChatUiAction.OnAddFileClicked -> {
                    val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                        type = "*/*" // Allow all files
                        // Restrict file types to PDF, Word, and Excel
                        putExtra(
                            Intent.EXTRA_MIME_TYPES,
                            arrayOf(
                                "application/pdf",

                            )
                        )
                    }
                    filePicker.launch(intent)
                }

                MainChatUiAction.OnAddImageClicked -> {
                    imagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                }

                MainChatUiAction.OnHistoryClicked -> {
                    onNavigateToHistoryScreen()
                }

                MainChatUiAction.OnOpenCameraClicked -> {
                    photoUri = createImageFile(context)
                    cameraCaptureLauncher.launch(photoUri ?: Uri.EMPTY)
                }

                MainChatUiAction.OnPersonalizationClicked -> {
                    onNavigateToPersonalizationScreen()
                }

                MainChatUiAction.OnPrivacyTermsClicked -> {
                    onNavigateToPrivacyAndTerms()
                }

                MainChatUiAction.OnRecordVoiceClicked -> {
                    recordingBottomSheetVisible = true
                }

                MainChatUiAction.OnSettingsClicked -> {
                    onNavigateToSettingsScreen()
                }

                MainChatUiAction.OnCancelSubscription -> {
                    onSubscriptionAction(SubscriptionAction.ManageSubscription)
                }

                MainChatUiAction.OnRestoreSubscription -> {
                    onSubscriptionAction(SubscriptionAction.RestoreSubscription)
                }

                is MainChatUiAction.OnUpgradeSubscription -> {
                    onSubscriptionAction(
                        SubscriptionAction.UpgradeSubscription(
                            action.product
                        )
                    )
                }

                is MainChatUiAction.OnImageClicked -> {
                    previewImageFullScreen = true to action.imageUri.toString()
                }

                else -> viewModel.handleAction(action)
            }
        },
        prompt = state.currentPrompt,
        isChatEmpty = state.queries.isEmpty(),
        isPro = isPro,
        products = products,
        isLoading = state.isLoading,
        queries = state.queries,
        selectedModel = state.selectedModel,
        isAnimating = state.isAnimating,
        attachments = state.attachments,
        freeTrialCount = freeTrialCount,
        isError = state.error,
        onDecrementFreeTrialCount = onDecrementFreeTrialCount,
        imageFreeTrialCount = imageFreeTrialCount,
    )
    if (recordingBottomSheetVisible) {
        SpeechRecognitionBottomSheet(
            onDismiss = { recordingBottomSheetVisible = false },
            onResult = { result ->
                Log.d("AudioRec2", result)
                viewModel.handleAction(MainChatUiAction.OnUpdatePrompt(result))
                recordingBottomSheetVisible = false
            }
        )
    }
    if (previewImageFullScreen.first) {
        Dialog(
            properties = DialogProperties(
                dismissOnClickOutside = true,
                dismissOnBackPress = true
            ),
            onDismissRequest = { previewImageFullScreen = false to "" }
        ) {
            Box(Modifier.fillMaxWidth()) {
                Image(
                    painter = rememberAsyncImagePainter(
                        model = previewImageFullScreen.second,
                        contentScale = ContentScale.Fit,
                        filterQuality = FilterQuality.High
                    ),
                    contentDescription = "Attachment BackGround",
                )
                IconButton(
                    onClick = {
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                            permissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        } else {
                            scope.launch {
                                downloadImageWithMediaStore(context, previewImageFullScreen.second)
                            }
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .clip(CircleShape)
                        .background(
                            MaterialTheme.colorScheme.surface
                                .copy(alpha = 0.5f)
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = "Close",
                    )
                }
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainChatScreenContent(
    modifier: Modifier = Modifier,
    isConnected: Boolean = false,
    onAction: (MainChatUiAction) -> Unit = {},
    isChatEmpty: Boolean = true,
    isPro: Boolean = false,
    freeTrialCount: Int = 0,
    onDecrementFreeTrialCount: () -> Unit = {},
    products: List<ProductDetails> = emptyList(),
    prompt: String,
    isError: String = "",
    isLoading: Boolean = false,
    queries: List<ChatQuery> = emptyList(),
    isAnimating: Boolean = false,
    selectedModel: ChatModel = ChatModel.GPT_4O_MINI,
    attachments: List<MessageAttachment> = emptyList(),
    imageFreeTrialCount: Int=0,


    ) {
    Log.d("tenoo", "mainChatScreenContent: ${isPro}")
    var isBottomSheetVisible by remember { mutableStateOf(isPro) }
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val infoDialog = remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(isError) {
        if (isError.isNotBlank()) {
            snackbarHostState.showSnackbar(isError)
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            MainChatTopBar(
                modifier = Modifier.fillMaxWidth(),
                selectedModel = selectedModel,
                isPro = isPro,
                onAction = onAction,
                onChangeModel = {
                    onAction(MainChatUiAction.OnChangeModel(it))
                },
                onProClicked = {
                    isBottomSheetVisible = true
                }
            )
        },
        bottomBar = {
            MainChatBottomBar(
                modifier = Modifier.fillMaxWidth(),
                isLoading = isLoading,
                isAttachmentButtonsVisible = selectedModel != ChatModel.AI_ART_GENERATOR && !isAnimating && attachments.isEmpty(),
                isAnimating = isAnimating,
                onSend = {
                    Log.d(TAG, "MainChatScreenContent: $isPro  $freeTrialCount")
                    if (isPro || freeTrialCount > 0) {
                        onAction(MainChatUiAction.OnSendPrompt)
                        if (!isPro) {
                            onDecrementFreeTrialCount()
                        }
                    } else {
                        isBottomSheetVisible = true
                    }
                },
                onStopAnimation = { onAction(MainChatUiAction.OnStopAnimation) },
                onUpdateText = { text -> onAction(MainChatUiAction.OnUpdatePrompt(text)) },
                prompt = prompt,
                attachments = attachments,
                onRecordVoice = { onAction(MainChatUiAction.OnRecordVoiceClicked) },
                onDeleteAttachment = { uri -> onAction(MainChatUiAction.OnDeleteAttachment(uri)) },
                onAddImage = { onAction(MainChatUiAction.OnAddImageClicked) },
                onOpenCamera = { onAction(MainChatUiAction.OnOpenCameraClicked) },
                onAddFile = { onAction(MainChatUiAction.OnAddFileClicked) },
            )
        },
        modifier = modifier.systemBarsPadding(),
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding),
        ) {
            androidx.compose.animation.AnimatedVisibility(isPro && selectedModel == ChatModel.AI_ART_GENERATOR) {
                Row (
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Text(
                        stringResource(R.string.remaining_quota),
                        modifier = Modifier.weight(1f)
                    )
                    Text(NumberFormat.getInstance(Locale.getDefault()).format(imageFreeTrialCount))
                }
            }
            AnimatedContent(
                targetState = isChatEmpty
            ) { state ->
                when {
                    state -> {
                        EmptyChat(
                            modifier = Modifier.fillMaxSize(),
                            selectedModel = selectedModel,
                            onUseAIArtGenerator = {
                                onAction(
                                    MainChatUiAction.OnChangeModel(
                                        ChatModel.AI_ART_GENERATOR
                                    )
                                )
                            },
                            onSuggestingClicked = { suggestionPrompt ->
                                if (isPro || freeTrialCount > 0) {
                                    onAction(MainChatUiAction.OnSendSuggestion(suggestionPrompt))
                                    if (!isPro) {
                                        onDecrementFreeTrialCount()
                                    }
                                } else {
                                    isBottomSheetVisible = true
                                }
                            })
                    }

                    else -> {
                        QueryList(
                            queries = queries,
                            action = onAction
                        )
                    }
                }
            }
            AnimatedVisibility(visible = isBottomSheetVisible) {
                ModalBottomSheet(
                    sheetState = bottomSheetState,
                    onDismissRequest = { isBottomSheetVisible = false }
                ) {
                    ProSubscriptionBottomSheet(
                        modifier = Modifier.fillMaxSize(),
                        onDismiss = { isBottomSheetVisible = false },
                        onRestore = { onAction(MainChatUiAction.OnRestoreSubscription) },
                        products = products,
                        onContinue = { product ->
                            onAction(
                                MainChatUiAction.OnUpgradeSubscription(
                                    product
                                )
                            )
                        },
                        onPrivacyTermsClicked = { onAction(MainChatUiAction.OnPrivacyTermsClicked) },
                    )
                }
            }
        }
    }
//    if (!isConnected) {
//        InfoDialog(
//            title = "Whoops!",
//            desc = "No Internet Connection found.\n" +
//                    "Check your connection or try again.",
//            onDismiss = {
//                infoDialog.value = false
//            }
//        )
//    }

}

@Preview(showSystemUi = false, showBackground = true)
@Composable
private fun MainChatScreenContentPreview() {
    ChatbotTheme {
        val queries = listOf(
            ChatQuery(
                prompt = "Hello there! tell me a joke", response = "Sure! Here's one for you:\n" +
                        "Why don't skeletons fight each other?\n" +
                        "Because they don’t have the guts! \uD83D\uDE04"
            ),
            ChatQuery(
                prompt = "not funny another one",
                response = "Alright, let me try again:\n" +
                        "Why did the scarecrow win an award?\n" +
                        "Because he was outstanding in his field! \uD83C\uDF3E\uD83D\uDE02"
            )
        )
        MainChatScreenContent(
            isChatEmpty = false,
            queries = queries.reversed(),
            prompt = "",
            isLoading = false,
            onAction = {},


            )
    }
}