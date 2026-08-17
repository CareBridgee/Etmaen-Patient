package com.carenest.presentation.ui.aichat.chat

enum class ChatMessageType {
    TEXT,
    SERVICE_RECOMMENDATION
}

data class ServiceRecommendationData(
    val categoryId: String,
    val title: String,
    val subtitle: String,
    val price: String,
    val duration: String
)

data class ChatMessage(
    val id: String,
    val text: String,
    val isUser: Boolean,
    val type: ChatMessageType = ChatMessageType.TEXT,
    val serviceData: ServiceRecommendationData? = null,
    val sentAtEpochMillis: Long = System.currentTimeMillis()
)

data class AIChatState(
    val patientId: String = "",
    val messages: List<ChatMessage> = emptyList(),
    val inputText: String = "",
    val isLoading: Boolean = false,
    val isResetting: Boolean = false,
    val errorMessage: String? = null
)

sealed class AIChatEvent {
    data class OnInputTextChanged(val text: String) : AIChatEvent()
    object OnSendMessage : AIChatEvent()
    object OnStartOverClicked : AIChatEvent()
    object OnBackClicked : AIChatEvent()
    object OnBookNowClicked : AIChatEvent()
    data class OnViewServiceClicked(val categoryId: String) : AIChatEvent()
    object OnDismissError : AIChatEvent()
}

sealed class AIChatEffect {
    object NavigateBack : AIChatEffect()
    object NavigateToBookings : AIChatEffect()
    data class NavigateToServiceDetails(val categoryId: String) : AIChatEffect()
    data class NavigateToRequestService(val serviceId: String) : AIChatEffect()
    data class ShowError(val message: String) : AIChatEffect()
}
