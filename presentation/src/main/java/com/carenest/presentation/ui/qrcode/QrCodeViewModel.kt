package com.carenest.presentation.ui.qrcode

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carenest.domain.repository.ReservationSocketRepository
import com.carenest.domain.socket.SocketServiceController
import com.carenest.domain.socket.model.ReservationEvent
import com.carenest.presentation.core.mvi.DefaultEffectPublisher
import com.carenest.presentation.core.mvi.DefaultStateHolder
import com.carenest.presentation.core.mvi.EffectPublisher
import com.carenest.presentation.core.mvi.StateHolder
import com.carenest.domain.usecase.tracking.GetVisitVerificationCodeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QrCodeViewModel @Inject constructor(
    private val getVisitVerificationCodeUseCase: GetVisitVerificationCodeUseCase,
    private val reservationSocketRepository: ReservationSocketRepository,
    private val socketServiceController: SocketServiceController
) : ViewModel(),
    StateHolder<QrCodeState> by DefaultStateHolder(QrCodeState()),
    EffectPublisher<QrCodeEffect> by DefaultEffectPublisher() {

    private var reservationEventsJob: Job? = null

    fun onEvent(event: QrCodeIntent) {
        when (event) {
            is QrCodeIntent.LoadQrCode -> loadQrCode(event.requestId)
            QrCodeIntent.BackClicked -> sendEffect(QrCodeEffect.NavigateBack)
            is QrCodeIntent.RetryClicked -> loadQrCode(event.requestId)
        }
    }

    private fun loadQrCode(requestId: String) {
        observeReservationEvents(requestId)
        viewModelScope.launch {
            updateState { copy(isLoading = true, error = null, requestId = requestId) }
            
            getVisitVerificationCodeUseCase(requestId)
                .onSuccess { qrContent ->
                    updateState {
                        copy(
                            qrData = qrContent,
                            isLoading = false
                        )
                    }
                }
                .onFailure { error ->
                    updateState {
                        copy(
                            isLoading = false,
                            error =  "Failed to load QR code"
                        )
                    }
                }
        }
    }

    private fun observeReservationEvents(requestId: String) {
        reservationEventsJob?.cancel()
        reservationEventsJob = viewModelScope.launch {
            reservationSocketRepository.observeReservationEvents(requestId)
                .catch { e ->
                    Log.e("QrCodeVM", "Socket error: ${e.message}")
                }
                .collect { event ->
                    if (event is ReservationEvent.Completed) {
                        socketServiceController.stopService()
                        sendEffect(QrCodeEffect.NavigateToVisitCompleted(requestId))
                    }
                }
        }
    }

    override fun onCleared() {
        super.onCleared()
        reservationEventsJob?.cancel()
    }
}
