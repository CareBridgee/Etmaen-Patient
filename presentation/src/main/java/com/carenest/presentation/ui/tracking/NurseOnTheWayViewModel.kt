package com.carenest.presentation.ui.tracking

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carenest.domain.repository.ReservationSocketRepository
import com.carenest.domain.socket.SocketServiceController
import com.carenest.domain.socket.model.ReservationEvent
import com.carenest.domain.usecase.tracking.CancelVisitUseCase
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
class NurseOnTheWayViewModel @Inject constructor(
    private val getNurseTrackingInfoUseCase: GetNurseTrackingInfoUseCase,
    private val cancelVisitUseCase: CancelVisitUseCase,
    private val reservationSocketRepository: ReservationSocketRepository,
    private val socketServiceController: SocketServiceController,
) : ViewModel(), EffectPublisher<NurseOnTheWayEffect> by DefaultEffectPublisher() ,
    StateHolder<NurseOnTheWayState> by DefaultStateHolder(NurseOnTheWayState()) {

    private var reservationEventsJob: Job? = null


    fun handleIntent(intent: NurseOnTheWayIntent) {
        when (intent) {
            is NurseOnTheWayIntent.LoadNurseTrackingInfo -> loadNurseTrackingInfo(intent.requestId)
            NurseOnTheWayIntent.OnCallNurseClicked -> onCallNurseClicked()
            NurseOnTheWayIntent.OnMessageNurseClicked -> onMessageNurseClicked()
            NurseOnTheWayIntent.OnShowQrCodeClicked -> sendEffect(NurseOnTheWayEffect.NavigateToQrCode)

            is NurseOnTheWayIntent.OnCancelVisitClicked -> updateState {
                copy(showCancelConfirmationDialog = true)
            }
            NurseOnTheWayIntent.OnConfirmCancelVisitClicked -> {
                updateState { copy(showCancelConfirmationDialog = false) }
                confirmCancelVisit()
            }
            NurseOnTheWayIntent.OnDismissCancelDialogClicked -> updateState {
                copy(showCancelConfirmationDialog = false)
            }
            NurseOnTheWayIntent.OnNurseCancelledDismissed -> {
                socketServiceController.stopService()
                updateState { copy(showNurseCancelledDialog = false) }
                sendEffect(NurseOnTheWayEffect.NavigateBackAfterCancel)
            }
            NurseOnTheWayIntent.OnRequestIdNotFound -> sendEffect(NurseOnTheWayEffect.NavigateBackAfterCancel)
            NurseOnTheWayIntent.OnErrorDismissed -> updateState { copy(errorMessage = null) }
        }
    }

    private fun confirmCancelVisit() {
        val requestId = currentState.nurseInfo?.requestId ?: return

        viewModelScope.launch {
            updateState { copy(isCancelling = true) }

            // Notify via Socket first to ensure immediate nurse notification and state sync
            runCatching { reservationSocketRepository.cancelRequest(requestId) }

            cancelVisitUseCase(requestId)
                .onSuccess { wasFreeOfCharge ->
                    updateState { copy(isCancelling = false) }
                    socketServiceController.stopService()
                    if (!wasFreeOfCharge) {
                        sendEffect(
                            NurseOnTheWayEffect.ShowCancellationFeeWarning(
                                "A cancellation fee applies after the free window has passed."
                            )
                        )
                    }
                    sendEffect(NurseOnTheWayEffect.NavigateBackAfterCancel)
                }
                .onFailure { throwable ->
                    updateState { copy(isCancelling = false) }
                    sendEffect(NurseOnTheWayEffect.ShowError(throwable.message.orEmpty()))
                }
        }
    }
    private fun loadNurseTrackingInfo(requestId: String) {
        socketServiceController.startService(requestId)
        observeReservationEvents(requestId)
        viewModelScope.launch {
            updateState {
                copy(
                    isLoading = true,
                    errorMessage = null
                )
            }

            getNurseTrackingInfoUseCase(requestId)
                .onSuccess { info ->
                    updateState {
                        copy(
                            isLoading = false,
                            nurseInfo = info
                        )
                    }
                }
                .onFailure { throwable ->
                    updateState {
                        copy(
                            isLoading = false,
                            errorMessage = throwable.message
                        )
                    }
                    sendEffect(NurseOnTheWayEffect.ShowError(throwable.message.orEmpty()))
                }
        }
    }

    private fun onCallNurseClicked() {
        val phoneNumber = currentState.nurseInfo?.phoneNumber ?: return
        sendEffect(NurseOnTheWayEffect.InitiateCall(phoneNumber))
    }

    private fun onMessageNurseClicked() {
        val nurseId = currentState.nurseInfo?.nurseId ?: return
        sendEffect(NurseOnTheWayEffect.OpenChat(nurseId))
    }

    private fun observeReservationEvents(requestId: String) {
        reservationEventsJob?.cancel()
        reservationEventsJob = viewModelScope.launch {
            reservationSocketRepository.observeReservationEvents(requestId)
                .catch { e ->
                    Log.e("NurseOnTheWayVM", "Socket error: ${e.message}")
                }
                .collect { event ->
                    when (event) {
                        is ReservationEvent.RequestCancelled -> {
                            if (!currentState.isCancelling) {
                                // Even if nurse cancelled via socket, poke the REST cancel to ensure profile is released
                                viewModelScope.launch { runCatching { cancelVisitUseCase(requestId) } }
                                updateState { copy(showCancelConfirmationDialog = false,showNurseCancelledDialog = true) } // Close if user was about to cancel
                            }
                        }
                        ReservationEvent.Completed -> {
                            socketServiceController.stopService()
                            sendEffect(NurseOnTheWayEffect.NavigateToVisitCompleted(requestId))
                        }
                        else -> Unit
                    }
                }
        }
    }

    override fun onCleared() {
        super.onCleared()
        reservationEventsJob?.cancel()
    }
}