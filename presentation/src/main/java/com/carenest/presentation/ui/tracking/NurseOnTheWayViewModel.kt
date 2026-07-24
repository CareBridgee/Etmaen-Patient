package com.carenest.presentation.ui.tracking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carenest.domain.usecase.tracking.CancelVisitUseCase
import com.carenest.domain.usecase.tracking.GetNurseTrackingInfoUseCase
import com.carenest.presentation.core.mvi.DefaultEffectPublisher
import com.carenest.presentation.core.mvi.DefaultStateHolder
import com.carenest.presentation.core.mvi.EffectPublisher
import com.carenest.presentation.core.mvi.StateHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class NurseOnTheWayViewModel @Inject constructor(
    private val getNurseTrackingInfoUseCase: GetNurseTrackingInfoUseCase,
    private val cancelVisitUseCase: CancelVisitUseCase,
) : ViewModel(), EffectPublisher<NurseOnTheWayEffect> by DefaultEffectPublisher() ,
    StateHolder<NurseOnTheWayState> by DefaultStateHolder(NurseOnTheWayState()) {


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

            NurseOnTheWayIntent.OnErrorDismissed -> updateState { copy(errorMessage = null) }
        }
    }

    private fun confirmCancelVisit() {
        val requestId = currentState.nurseInfo?.requestId ?: return

        viewModelScope.launch {
            updateState { copy(isCancelling = true) }

            cancelVisitUseCase(requestId)
                .onSuccess { wasFreeOfCharge ->
                    updateState { copy(isCancelling = false) }
                    if (wasFreeOfCharge) {
                        sendEffect(NurseOnTheWayEffect.NavigateBackAfterCancel)
                    } else {
                        sendEffect(
                            NurseOnTheWayEffect.ShowCancellationFeeWarning(
                                "A cancellation fee applies after the free window has passed."
                            )
                        )
                    }
                }
                .onFailure { throwable ->
                    updateState { copy(isCancelling = false) }
                    sendEffect(NurseOnTheWayEffect.ShowError(throwable.message.orEmpty()))
                }
        }
    }
    private fun loadNurseTrackingInfo(requestId: String) {
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

}