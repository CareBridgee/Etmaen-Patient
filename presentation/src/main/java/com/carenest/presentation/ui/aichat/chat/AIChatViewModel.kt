package com.carenest.presentation.ui.aichat.chat

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.carenest.presentation.core.mvi.DefaultEffectPublisher
import com.carenest.presentation.core.mvi.DefaultStateHolder
import com.carenest.presentation.core.mvi.EffectPublisher
import com.carenest.presentation.core.mvi.StateHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AIChatViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel(),
    StateHolder<AIChatState> by DefaultStateHolder(AIChatState()),
    EffectPublisher<AIChatEffect> by DefaultEffectPublisher() {

    init {
        // Patient ID would typically be passed in and retrieved here, 
        // but since we are mocking, we just set initial messages.
        val mockMessages = listOf(
            ChatMessage(
                id = UUID.randomUUID().toString(),
                text = "Hello Elena, I'm your AI assistant. Would you like to schedule a blood pressure check or receive guidance on your recent symptoms?",
                isUser = false,
                type = ChatMessageType.TEXT
            ),
            ChatMessage(
                id = UUID.randomUUID().toString(),
                text = "",
                isUser = false,
                type = ChatMessageType.SERVICE_RECOMMENDATION,
                serviceData = ServiceRecommendationData(
                    categoryId = "IV_DRIP", // using IV_DRIP to match ServiceCategory enum
                    title = "IV Hydration Therapy",
                    subtitle = "Personalized hydration and vitamin infusion.",
                    price = "$189.00",
                    duration = "45–60 min"
                )
            )
        )
        updateState { copy(messages = mockMessages) }
    }

    fun onEvent(event: AIChatEvent) {
        when (event) {
            is AIChatEvent.OnInputTextChanged -> {
                updateState { copy(inputText = event.text) }
            }
            is AIChatEvent.OnSendMessage -> {
                val currentText = currentState.inputText
                if (currentText.isNotBlank()) {
                    val newMessage = ChatMessage(
                        id = UUID.randomUUID().toString(),
                        text = currentText,
                        isUser = true
                    )
                    updateState { 
                        copy(
                            messages = messages + newMessage,
                            inputText = ""
                        )
                    }
                }
            }
            is AIChatEvent.OnBackClicked -> {
                sendEffect(AIChatEffect.NavigateBack)
            }
            is AIChatEvent.OnBookNowClicked -> {
                sendEffect(AIChatEffect.NavigateToBookings)
            }
            is AIChatEvent.OnViewServiceClicked -> {
                sendEffect(AIChatEffect.NavigateToServiceDetails(event.categoryId))
            }
        }
    }
}
