package com.carenest.presentation.ui.aichat.chat

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carenest.domain.usecase.aichat.SendAiChatMessageUseCase
import com.carenest.presentation.core.mvi.DefaultEffectPublisher
import com.carenest.presentation.core.mvi.DefaultStateHolder
import com.carenest.presentation.core.mvi.EffectPublisher
import com.carenest.presentation.core.mvi.StateHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AIChatViewModel @Inject constructor(
    private val sendAiChatMessageUseCase: SendAiChatMessageUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel(),
    StateHolder<AIChatState> by DefaultStateHolder(AIChatState()),
    EffectPublisher<AIChatEffect> by DefaultEffectPublisher() {

    init {
        val patientIdParam: String? = savedStateHandle["patientId"]
        updateState {
            copy(
                patientId = patientIdParam ?: ""
            )
        }
    }

    fun onEvent(event: AIChatEvent) {
        when (event) {
            is AIChatEvent.OnInputTextChanged -> {
                updateState { copy(inputText = event.text, errorMessage = null) }
            }
            is AIChatEvent.OnSendMessage -> {
                sendMessage()
            }
            is AIChatEvent.OnBackClicked -> {
                sendEffect(AIChatEffect.NavigateBack)
            }
            is AIChatEvent.OnBookNowClicked -> {
                val serviceId = currentState.messages.lastOrNull {
                    it.type == ChatMessageType.SERVICE_RECOMMENDATION
                }?.serviceData?.categoryId ?: ""
                sendEffect(AIChatEffect.NavigateToRequestService(serviceId))
            }
            is AIChatEvent.OnViewServiceClicked -> {
                sendEffect(AIChatEffect.NavigateToServiceDetails(event.categoryId))
            }
            is AIChatEvent.OnDismissError -> {
                updateState { copy(errorMessage = null) }
            }
        }
    }

    private fun sendMessage() {
        val textToSend = currentState.inputText.trim()
        if (textToSend.isBlank() || currentState.isLoading) return

        val userMessage = ChatMessage(
            id = UUID.randomUUID().toString(),
            text = textToSend,
            isUser = true
        )

        updateState {
            copy(
                messages = messages + userMessage,
                inputText = "",
                isLoading = true,
                errorMessage = null
            )
        }

        viewModelScope.launch {
            val result = sendAiChatMessageUseCase(textToSend)
            result.fold(
                onSuccess = { reply ->
                    val aiMessage = ChatMessage(
                        id = UUID.randomUUID().toString(),
                        text = reply,
                        isUser = false
                    )
                    updateState {
                        copy(
                            messages = messages + aiMessage,
                            isLoading = false
                        )
                    }
                },
                onFailure = { error ->
                    val errorMsg = error.message ?: "Failed to get AI response. Please try again."
                    updateState {
                        copy(
                            isLoading = false,
                            errorMessage = errorMsg
                        )
                    }
                    sendEffect(AIChatEffect.ShowError(errorMsg))
                }
            )
        }
    }
}
