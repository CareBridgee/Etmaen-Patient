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

import com.carenest.domain.repository.ProfileRepository
import com.carenest.domain.usecase.user.ObserveCurrentUserUseCase
import kotlinx.coroutines.flow.firstOrNull

@HiltViewModel
class AIChatViewModel @Inject constructor(
    private val sendAiChatMessageUseCase: SendAiChatMessageUseCase,
    private val observeCurrentUserUseCase: ObserveCurrentUserUseCase,
    private val profileRepository: ProfileRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel(),
    StateHolder<AIChatState> by DefaultStateHolder(AIChatState()),
    EffectPublisher<AIChatEffect> by DefaultEffectPublisher() {

    init {
        val patientIdParam: String? = savedStateHandle["patientId"]
        if (!patientIdParam.isNullOrBlank()) {
            updateState { copy(patientId = patientIdParam) }
        }
        viewModelScope.launch {
            observeCurrentUserUseCase().collect { user ->
                user ?: return@collect
                if (currentState.patientId.isBlank()) {
                    val defaultId = user.defaultProfileId
                    if (!defaultId.isNullOrBlank()) {
                        updateState { copy(patientId = defaultId) }
                    }
                }
            }
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

        viewModelScope.launch {
            var activeProfileId = currentState.patientId
            if (activeProfileId.isBlank()) {
                val user = observeCurrentUserUseCase().firstOrNull()
                activeProfileId = user?.defaultProfileId.orEmpty()
            }
            if (activeProfileId.isBlank()) {
                activeProfileId = profileRepository.getDefaultProfile().getOrNull()?.id.orEmpty()
            }

            if (activeProfileId.isBlank()) {
                val errorMsg = "Please select or set up a patient profile first."
                updateState { copy(isLoading = false, errorMessage = errorMsg) }
                sendEffect(AIChatEffect.ShowError(errorMsg))
                return@launch
            }

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

            val result = sendAiChatMessageUseCase(activeProfileId, textToSend)
            result.fold(
                onSuccess = { chatResult ->
                    val aiTextMessage = ChatMessage(
                        id = UUID.randomUUID().toString(),
                        text = chatResult.reply,
                        isUser = false
                    )

                    val newMessages = mutableListOf<ChatMessage>()
                    newMessages.add(aiTextMessage)

                    if (!chatResult.serviceTypeId.isNullOrBlank() || !chatResult.serviceTypeName.isNullOrBlank()) {
                        val recData = ServiceRecommendationData(
                            categoryId = chatResult.serviceTypeId.orEmpty(),
                            title = chatResult.serviceTypeName.takeIf { !it.isNullOrBlank() } ?: "Recommended Healthcare Service",
                            subtitle = chatResult.serviceDescription.takeIf { !it.isNullOrBlank() }
                                ?: chatResult.careDescription.orEmpty(),
                            price = "",
                            duration = ""
                        )
                        newMessages.add(
                            ChatMessage(
                                id = UUID.randomUUID().toString(),
                                text = "",
                                isUser = false,
                                type = ChatMessageType.SERVICE_RECOMMENDATION,
                                serviceData = recData
                            )
                        )
                    }

                    updateState {
                        copy(
                            messages = messages + newMessages,
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
