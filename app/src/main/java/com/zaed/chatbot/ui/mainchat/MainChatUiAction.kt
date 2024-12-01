package com.zaed.chatbot.ui.mainchat

import android.net.Uri
import com.zaed.chatbot.data.model.MessageAttachment
import com.zaed.chatbot.ui.mainchat.components.ChatModel

sealed interface MainChatUiAction {
    data class OnChangeModel(val model: ChatModel) : MainChatUiAction
    data object OnNewChatClicked : MainChatUiAction
    data object OnStopAnimation : MainChatUiAction
    data object OnPersonalizationClicked : MainChatUiAction
    data object OnHistoryClicked : MainChatUiAction
    data object OnSettingsClicked : MainChatUiAction
    data object OnSendPrompt : MainChatUiAction
    data class OnUpdatePrompt(val text: String) : MainChatUiAction
    data object OnRecordVoiceClicked : MainChatUiAction
    data class OnDeleteAttachment(val attachmentUri: Uri) : MainChatUiAction
    data object OnAddImageClicked : MainChatUiAction
    data object OnAddFileClicked : MainChatUiAction
    data object OnOpenCameraClicked : MainChatUiAction
    data class OnSendSuggestion(val suggestionPrompt: String) : MainChatUiAction
    data object OnRestoreSubscription : MainChatUiAction
    data class OnUpgradeSubscription(val isFreeTrialEnabled: Boolean, val isLifetime: Boolean) : MainChatUiAction
    data object OnCancelSubscription: MainChatUiAction
    data object OnPrivacyTermsClicked: MainChatUiAction
    data class OnAddAttachment(val attachment: MessageAttachment,val fileContent:String): MainChatUiAction
    data class OnImageClicked(val imageUri: Uri): MainChatUiAction
}