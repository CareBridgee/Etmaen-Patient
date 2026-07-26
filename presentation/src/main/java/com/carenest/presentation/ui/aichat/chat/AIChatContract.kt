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
    val serviceData: ServiceRecommendationData? = null
)

data class AIChatState(
    val patientId: String = "",
    val messages: List<ChatMessage> = emptyList(),
    val inputText: String = "",
    val isLoading: Boolean = false
)

sealed class AIChatEvent {
    data class OnInputTextChanged(val text: String) : AIChatEvent()
    object OnSendMessage : AIChatEvent()
    object OnBackClicked : AIChatEvent()
    object OnBookNowClicked : AIChatEvent()
    data class OnViewServiceClicked(val categoryId: String) : AIChatEvent()
}

sealed class AIChatEffect {
    object NavigateBack : AIChatEffect()
    object NavigateToBookings : AIChatEffect()
    data class NavigateToServiceDetails(val categoryId: String) : AIChatEffect()
}
