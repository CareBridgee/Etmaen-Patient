package com.carenest.presentation.ui.socketexample

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carenest.domain.repository.ReservationSocketRepository
import com.carenest.domain.socket.model.ReservationEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SocketExampleState(
    val isLoading: Boolean = false,
    val recentEvent: ReservationEvent? = null,
    val errorMessage: String? = null
)

sealed interface SocketExampleIntent {
    data class ObserveReservation(val reservationId: String) : SocketExampleIntent
    data class AcceptOffer(val offerId: String) : SocketExampleIntent
}

sealed interface SocketExampleEffect {
    data class ShowToast(val message: String) : SocketExampleEffect
    data object NavigateBack : SocketExampleEffect
}

@HiltViewModel
class SocketExampleViewModel @Inject constructor(
    private val reservationSocketRepository: ReservationSocketRepository
) : ViewModel() {

    // Simulating StateHolder<S> and EffectPublisher<E> from core.mvi
    private val _state = MutableStateFlow(SocketExampleState())
    val state: StateFlow<SocketExampleState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<SocketExampleEffect>()
    val effect = _effect.asSharedFlow()

    fun onIntent(intent: SocketExampleIntent) {
        when (intent) {
            is SocketExampleIntent.ObserveReservation -> observeReservation(intent.reservationId)
            is SocketExampleIntent.AcceptOffer -> acceptOffer(intent.offerId)
        }
    }

    private fun observeReservation(reservationId: String) {
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            reservationSocketRepository.observeReservationEvents(reservationId)
                .catch { e ->
                    _state.update { it.copy(isLoading = false, errorMessage = e.message) }
                }
                .collect { event ->
                    _state.update { it.copy(isLoading = false, recentEvent = event) }
                    _effect.emit(SocketExampleEffect.ShowToast("New event: ${event::class.simpleName}"))
                }
        }
    }

    private fun acceptOffer(offerId: String) {
        viewModelScope.launch {
            try {
                reservationSocketRepository.acceptOffer(offerId)
            } catch (e: Exception) {
                _effect.emit(SocketExampleEffect.ShowToast("Failed to accept offer: ${e.message}"))
            }
        }
    }
}
