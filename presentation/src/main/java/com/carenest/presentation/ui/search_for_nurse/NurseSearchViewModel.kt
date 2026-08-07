package com.carenest.presentation.ui.search_for_nurse

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carenest.domain.repository.ReservationSocketRepository
import com.carenest.domain.socket.model.ReservationEvent
import com.carenest.presentation.core.mvi.DefaultEffectPublisher
import com.carenest.presentation.core.mvi.DefaultStateHolder
import com.carenest.presentation.core.mvi.EffectPublisher
import com.carenest.presentation.core.mvi.StateHolder
import com.carenest.presentation.ui.search_for_nurse.NurseSearchEffect.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NurseSearchViewModel @Inject constructor(
    private val reservationSocketRepository: ReservationSocketRepository,
) : ViewModel(),
    StateHolder<NurseSearchState> by DefaultStateHolder(NurseSearchState()),
    EffectPublisher<NurseSearchEffect> by DefaultEffectPublisher() {

    private var observationJob: Job? = null
    private var reservationId: String = ""
    private var serviceRequestId: String = ""

    fun onIntent(intent: NurseSearchIntent) {
        when (intent) {
            is NurseSearchIntent.StartSearching -> startSearching(intent.reservationId, intent.serviceRequestId)
            is NurseSearchIntent.AcceptOffer -> acceptOffer(intent.offerId)
            is NurseSearchIntent.DeclineOffer -> declineOffer(intent.offerId)
            NurseSearchIntent.CancelSearch -> cancelSearch()
            is NurseSearchIntent.PaymentMethodSelected -> updateState {
                copy(
                    selectedPaymentMethod = intent.paymentMethod,
                    paymentMethods = paymentMethods.map {
                        it.copy(isSelected = it.id == intent.paymentMethod.id)
                    }
                )
            }
            NurseSearchIntent.ConfirmPayment -> confirmPayment()
            NurseSearchIntent.DismissPaymentSheet -> updateState {
                copy(showPaymentSheet = false, selectedOfferForPayment = null)
            }
        }
    }

    private fun startSearching(resId: String, srId: String) {
        if (reservationId == resId) return // already started
        reservationId = resId
        serviceRequestId = srId
        observeReservationEvents()
        requestInitialOffers()
    }

    private fun observeReservationEvents() {
        observationJob?.cancel()
        observationJob = viewModelScope.launch {
            reservationSocketRepository.observeReservationEvents(reservationId)
                .catch { e ->
                    sendEffect(NurseSearchEffect.ShowError(e.message ?: "Connection error"))
                }
                .collect { event -> handleEvent(event) }
        }
    }

    private fun requestInitialOffers() {
        viewModelScope.launch {
            runCatching { reservationSocketRepository.requestOffersList(serviceRequestId) }
        }
    }

    private fun handleEvent(event: ReservationEvent) {
        when (event) {
            is ReservationEvent.OffersList -> {
                updateState { copy(offers = event.offers, isSearching = event.offers.isEmpty()) }
            }
            is ReservationEvent.OfferCreated -> {
                updateState { copy(offers = offers + event.offer, isSearching = false) }
            }
            is ReservationEvent.OfferUpdated -> updateState {
                copy(offers = offers.map { if (it.id == event.offer.id) event.offer else it })
            }
            is ReservationEvent.OfferCountered -> updateState {
                copy(offers = offers.map { if (it.id == event.offer.id) event.offer else it })
            }
            is ReservationEvent.OfferAccepted -> {
                sendEffect(NavigateToEnRoute(event.offer.nurseId))
            }
            is ReservationEvent.OfferWithdrawn -> updateState {
                copy(offers = offers.filter { it.id != event.offerId })
            }
            is ReservationEvent.OfferRejected -> updateState {
                copy(offers = offers.filter { it.id != event.offerId })
            }
            is ReservationEvent.RequestCancelled -> {
                sendEffect(NurseSearchEffect.NavigateBack)
            }
            is ReservationEvent.Unknown -> Unit
            ReservationEvent.Completed -> TODO()
        }
    }

    private fun acceptOffer(offerId: String) {
        val offer = state.value.offers.find { it.id == offerId } ?: return
        updateState { copy(showPaymentSheet = true, selectedOfferForPayment = offer) }
    }

    private fun declineOffer(offerId: String) {
        viewModelScope.launch {
            runCatching { reservationSocketRepository.rejectOffer(offerId) }
                .onFailure { sendEffect(NurseSearchEffect.ShowError(it.message ?: "Failed to decline offer")) }
        }
    }

    private fun cancelSearch() {
        viewModelScope.launch {
            runCatching { reservationSocketRepository.cancelRequest(serviceRequestId) }
            sendEffect(NurseSearchEffect.NavigateBack)
        }
    }

    private fun confirmPayment() {
        val offer = state.value.selectedOfferForPayment ?: return
        viewModelScope.launch {
            runCatching { reservationSocketRepository.acceptOffer(offer.id) }
                .onSuccess {
                    updateState { copy(showPaymentSheet = false, selectedOfferForPayment = null) }
                    sendEffect(NurseSearchEffect.NavigateToEnRoute(offer.nurseId))
                }
                .onFailure {
                    sendEffect(NurseSearchEffect.ShowError(it.message ?: "Failed to accept offer"))
                }
        }
    }

    override fun onCleared() {
        super.onCleared()
        observationJob?.cancel()
    }
}
