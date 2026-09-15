package com.carenest.presentation.ui.chat


import com.carenest.domain.model.ChatMessage
import com.carenest.domain.model.ChatParticipant
import com.carenest.presentation.core.util.UiText

data class ChatState(
    val isLoading: Boolean = true,
    val participant: ChatParticipant? = null,
    val messages: List<ChatMessage> = emptyList(),
    val inputText: String = "",
    val isSending: Boolean = false,
    val showNurseCancelledDialog: Boolean = false,
    val errorMessage: UiText? = null,
)

sealed interface ChatIntent {
    data class LoadChat(val requestId: String) : ChatIntent
    data class OnMessageInputChanged(val text: String) : ChatIntent
    data object OnSendMessageClicked : ChatIntent
    data object OnCallClicked : ChatIntent
    data object OnBackClicked : ChatIntent
    data object OnNurseCancelledDismissed : ChatIntent
    data object OnErrorDismissed : ChatIntent
}

sealed interface ChatEffect {
    data class InitiateCall(val phoneNumber: String) : ChatEffect
    data object NavigateBack : ChatEffect
    data object ScrollToBottom : ChatEffect
    data class ShowError(val message: UiText) : ChatEffect
}