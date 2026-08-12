package com.carenest.presentation.ui.chat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carenest.domain.model.chat.ChatMessage
import com.carenest.domain.model.chat.ChatMessageType
import com.carenest.domain.model.chat.MessageSender
import com.carenest.domain.model.chat.MessageStatus
import com.carenest.domain.model.chat.ChatParticipant
import com.carenest.domain.repository.ChatSocketRepository
import com.carenest.domain.repository.ReservationSocketRepository
import com.carenest.domain.repository.UserRepository
import com.carenest.domain.socket.SocketServiceController
import com.carenest.domain.socket.model.ChatMessageResponse
import com.carenest.domain.socket.model.ReservationEvent
import com.carenest.domain.usecase.chat.GetChatSessionUseCase
import com.carenest.domain.usecase.chat.SendMessageUseCase
import com.carenest.domain.usecase.tracking.GetNurseTrackingInfoUseCase
import com.carenest.presentation.core.mvi.DefaultEffectPublisher
import com.carenest.presentation.core.mvi.DefaultStateHolder
import com.carenest.presentation.core.mvi.EffectPublisher
import com.carenest.presentation.core.mvi.StateHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val getChatSessionUseCase: GetChatSessionUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val getNurseTrackingInfoUseCase: GetNurseTrackingInfoUseCase,
    private val chatSocketRepository: ChatSocketRepository,
    private val reservationSocketRepository: ReservationSocketRepository,
    private val userRepository: UserRepository,
    private val socketServiceController: SocketServiceController,
) : ViewModel(), EffectPublisher<ChatEffect> by DefaultEffectPublisher(),
    StateHolder<ChatState> by DefaultStateHolder(ChatState()) {

    private var requestId: String? = null
    private var chatObservationJob: Job? = null
    private var reservationEventsJob: Job? = null
    private var currentUserId: String = ""

    init {
        viewModelScope.launch {
            userRepository.observeCurrentUser().collect { user ->
                currentUserId = user?.id.orEmpty()
            }
        }
    }

    fun handleIntent(intent: ChatIntent) {
        when (intent) {
            is ChatIntent.LoadChat -> loadChat(intent.requestId)
            is ChatIntent.OnMessageInputChanged -> updateState { copy(inputText = intent.text) }
            ChatIntent.OnSendMessageClicked -> sendMessage()
            ChatIntent.OnCallClicked -> {
                val phone = currentState.participant?.phoneNumber ?: return
                sendEffect(ChatEffect.InitiateCall(phone))
            }
            ChatIntent.OnBackClicked -> sendEffect(ChatEffect.NavigateBack)
            ChatIntent.OnNurseCancelledDismissed -> {
                socketServiceController.stopService()
                updateState { copy(showNurseCancelledDialog = false) }
                sendEffect(ChatEffect.NavigateBack)
            }
            ChatIntent.OnErrorDismissed -> updateState { copy(errorMessage = null) }
        }
    }

    private fun loadChat(requestId: String) {
        this.requestId = requestId
        socketServiceController.startService(requestId)
        observeChatSocket(requestId)
        observeReservationEvents(requestId)
        
        viewModelScope.launch {
            updateState { copy(isLoading = true, errorMessage = null) }

            // Fetch nurse details first or in parallel to ensure we have real data
            val nurseResult = getNurseTrackingInfoUseCase(requestId)
            
            getChatSessionUseCase(requestId)
                .onSuccess { session ->
                    val realParticipant = nurseResult.getOrNull()?.let { info ->
                        ChatParticipant(
                            nurseId = info.nurseId,
                            name = info.name,
                            photoUrl = info.photoUrl,
                            isOnline = info.isOnline, // Use real online status from tracking info
                            phoneNumber = info.phoneNumber
                        )
                    } ?: session.participant

                    updateState {
                        copy(
                            isLoading = false,
                            participant = realParticipant,
                            messages = session.messages,
                        )
                    }
                    sendEffect(ChatEffect.ScrollToBottom)
                }
                .onFailure { throwable ->
                    updateState { copy(isLoading = false, errorMessage = throwable.message) }
                    sendEffect(ChatEffect.ShowError(throwable.message.orEmpty()))
                }
        }
    }

    private fun observeChatSocket(requestId: String) {
        chatObservationJob?.cancel()
        chatObservationJob = viewModelScope.launch {
            chatSocketRepository.observeChat(requestId)
                .catch { e ->
                    Log.e("ChatViewModel", "Socket error: ${e.message}")
                }
                .collect { response ->
                    handleNewMessage(response)
                }
        }
    }

    private fun observeReservationEvents(requestId: String) {
        reservationEventsJob?.cancel()
        reservationEventsJob = viewModelScope.launch {
            reservationSocketRepository.observeReservationEvents(requestId)
                .catch { e ->
                    Log.e("ChatViewModel", "Reservation socket error: ${e.message}")
                }
                .collect { event ->
                    Log.d("ChatViewModel", "Received reservation event: $event")
                    when (event) {
                        is ReservationEvent.RequestCancelled -> {
                            updateState { copy(showNurseCancelledDialog = true) }
                        }
                        is ReservationEvent.PresenceUpdate -> {
                            updateState {
                                copy(participant = participant?.copy(isOnline = true))
                            }
                        }
                        else -> Unit
                    }
                }
        }
    }

    private fun handleNewMessage(response: ChatMessageResponse) {
        val newMessage = response.toChatMessage(currentUserId)
        
        // Prevent duplicate messages if they were already loaded via REST
        if (currentState.messages.any { it.id == newMessage.id }) return

        updateState {
            copy(messages = messages + newMessage)
        }
        sendEffect(ChatEffect.ScrollToBottom)
    }

    private fun sendMessage() {
        val id = requestId ?: return
        val text = currentState.inputText.trim()
        if (text.isEmpty() || currentState.isSending) return

        viewModelScope.launch {
            updateState { copy(isSending = true, inputText = "") }

            sendMessageUseCase(id, text)
                .onSuccess {
                    updateState { copy(isSending = false) }
                }
                .onFailure { throwable ->
                    updateState { copy(isSending = false) }
                    sendEffect(ChatEffect.ShowError(throwable.message.orEmpty()))
                }
        }
    }

    private fun ChatMessageResponse.toChatMessage(currentUserId: String): ChatMessage {
        val isMine = senderUserId == currentUserId
        return ChatMessage(
            id = id,
            type = if (isMine) ChatMessageType.OUTGOING else ChatMessageType.INCOMING,
            text = content,
            senderType = if (isMine) MessageSender.PATIENT else MessageSender.NURSE,
            sentAtEpochMillis = System.currentTimeMillis(),
            status = if (isMine) MessageStatus.SENT else MessageStatus.DELIVERED
        )
    }

    override fun onCleared() {
        chatObservationJob?.cancel()
        reservationEventsJob?.cancel()
    }
}