package com.carenest.presentation.ui.searchfornurse

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carenest.domain.repository.NotificationSocketRepository
import com.carenest.domain.repository.ReservationSocketRepository
import com.carenest.domain.socket.ConnectionState
import com.carenest.domain.socket.SocketConnectionManager
import com.carenest.domain.socket.SocketServiceController
import com.carenest.domain.socket.model.ReservationEvent
import com.carenest.domain.usecase.tracking.CancelVisitUseCase
import com.carenest.domain.usecase.tracking.GetNurseOffersUseCase
import com.carenest.presentation.core.mvi.DefaultEffectPublisher
import com.carenest.presentation.core.mvi.DefaultStateHolder
import com.carenest.presentation.core.mvi.EffectPublisher
import com.carenest.presentation.core.mvi.StateHolder
import com.carenest.presentation.ui.searchfornurse.NurseSearchEffect.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject

@HiltViewModel
class NurseSearchViewModel @Inject constructor(
    private val reservationSocketRepository: ReservationSocketRepository,
    private val notificationSocketRepository: NotificationSocketRepository,
    private val getNurseOffersUseCase: GetNurseOffersUseCase,
    private val socketConnectionManager: SocketConnectionManager,
    private val cancelVisitUseCase: CancelVisitUseCase,
    private val socketServiceController: SocketServiceController,
) : ViewModel(),
    StateHolder<NurseSearchState> by DefaultStateHolder(NurseSearchState()),
    EffectPublisher<NurseSearchEffect> by DefaultEffectPublisher() {

    private var observationJob: Job? = null
    private var connectionJob: Job? = null
    private var notificationJob: Job? = null
    private var offersRefreshJob: Job? = null
    private var reservationId: String = ""
    private var serviceRequestId: String = ""
    private var socketOffersRevision: Long = 0

    fun onIntent(intent: NurseSearchIntent) {
        when (intent) {
            is NurseSearchIntent.StartSearching -> startSearching(intent.reservationId, intent.serviceRequestId)
            is NurseSearchIntent.AcceptOffer -> acceptOffer(intent.offerId)
            is NurseSearchIntent.DeclineOffer -> declineOffer(intent.offerId)
            NurseSearchIntent.CancelSearch -> updateState { copy(showCancelConfirmation = true) }
            NurseSearchIntent.ConfirmCancelSearch -> confirmCancelSearch()
            NurseSearchIntent.DismissCancelConfirmation -> updateState { copy(showCancelConfirmation = false) }
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
        if (reservationId == resId && serviceRequestId == srId) return // already started
        reservationId = resId
        serviceRequestId = srId
        socketServiceController.startService(srId)
        observeReservationEvents()
        observeSocketConnection()
        observeNotifications()
        requestInitialOffers()
    }

    private fun observeReservationEvents() {
        observationJob?.cancel()
        observationJob = viewModelScope.launch {
            reservationSocketRepository.observeReservationEvents(reservationId)
                .catch { e ->
                    sendEffect(ShowError(e.message ?: "Connection error"))
                }
                .collect { event -> handleEvent(event) }
        }
    }

    private fun observeSocketConnection() {
        connectionJob?.cancel()
        connectionJob = viewModelScope.launch {
            socketConnectionManager.connectionState.collect { state ->
                if (state is ConnectionState.Connected) {
                    // Refresh offers via STOMP and REST whenever socket becomes Connected
                    requestInitialOffers()
                }
            }
        }
    }

    private fun observeNotifications() {
        notificationJob?.cancel()
        notificationJob = viewModelScope.launch {
            notificationSocketRepository.observeNotifications()
                .catch { e ->
                    sendEffect(ShowError(e.message ?: "Notification stream error"))
                }
                .collect { notification ->
                    // Re-query offers whenever a new notification arrives
                    if (notification.type == "BOOKING" && notification.relatedEntityId == serviceRequestId) {
                        requestInitialOffers()
                    }
                }
        }
    }

    private fun requestInitialOffers() {
        offersRefreshJob?.cancel()
        offersRefreshJob = viewModelScope.launch {
            withTimeoutOrNull(SOCKET_SUBSCRIBE_GRACE_MS) {
                socketConnectionManager.connectionState.first { it is ConnectionState.Connected }
            }
            kotlinx.coroutines.delay(200)
            runCatching { reservationSocketRepository.requestOffersList(serviceRequestId) }

            val revisionAtRequest = socketOffersRevision
            getNurseOffersUseCase(serviceRequestId)
                .onSuccess { restOffers ->
                    if (revisionAtRequest == socketOffersRevision) {
                        updateState {
                            copy(
                                offers = restOffers,
                                isSearching = restOffers.isEmpty(),
                                activeNursesCount = restOffers.size
                            )
                        }
                    }
                }
                .onFailure {
                    if (state.value.offers.isEmpty()) {
                        sendEffect(ShowError(it.message ?: "Failed to load offers"))
                    }
                }
        }
    }

    private fun handleEvent(event: ReservationEvent) {
        when (event) {
            is ReservationEvent.OffersList -> {
                socketOffersRevision++
                updateState {
                    copy(
                        offers = event.offers,
                        isSearching = event.offers.isEmpty(),
                        activeNursesCount = event.offers.size
                    )
                }
            }
            is ReservationEvent.OfferCreated -> {
                socketOffersRevision++
                updateState {
                    val existingIndex = offers.indexOfFirst { it.id == event.offer.id }
                    val newOffers = if (existingIndex >= 0) {
                        offers.toMutableList().apply { set(existingIndex, event.offer) }
                    } else {
                        offers + event.offer
                    }
                    copy(
                        offers = newOffers,
                        isSearching = false,
                        activeNursesCount = newOffers.size
                    )
                }
            }
            is ReservationEvent.OfferUpdated -> {
                socketOffersRevision++
                updateState {
                    copy(offers = offers.map { if (it.id == event.offer.id) event.offer else it })
                }
            }
            is ReservationEvent.OfferCountered -> {
                socketOffersRevision++
                updateState {
                    copy(offers = offers.map { if (it.id == event.offer.id) event.offer else it })
                }
            }
            is ReservationEvent.OfferAccepted -> {
                socketServiceController.startService(event.offer.serviceRequestId)
                sendEffect(NavigateToEnRoute(event.offer.serviceRequestId))
            }
            is ReservationEvent.OfferWithdrawn -> updateState {
                val updatedOffers = offers.filter { it.id != event.offerId }
                copy(offers = updatedOffers, activeNursesCount = updatedOffers.size)
            }
            is ReservationEvent.OfferRejected -> updateState {
                val updatedOffers = offers.filter { it.id != event.offerId }
                copy(offers = updatedOffers, activeNursesCount = updatedOffers.size)
            }
            is ReservationEvent.RequestCancelled -> {
                updateState { copy(showCancelConfirmation = false) } // Ensure dialog is closed if open
                viewModelScope.launch { runCatching { cancelVisitUseCase(serviceRequestId) } }
                sendEffect(NavigateBack)
            }
            is ReservationEvent.Unknown -> Unit
            else -> Unit
        }
    }

    private fun acceptOffer(offerId: String) {
        val offer = state.value.offers.find { it.id == offerId } ?: return
        viewModelScope.launch {
            runCatching { reservationSocketRepository.acceptOffer(offer.id) }
                .onSuccess {
                    updateState { copy(showPaymentSheet = false, selectedOfferForPayment = null) }
                    socketServiceController.startService(offer.serviceRequestId)
                    sendEffect(NavigateToEnRoute(offer.serviceRequestId))
                }
                .onFailure {
                    sendEffect(ShowError(it.message ?: "Failed to accept offer"))
                }
        }
    }

    private fun declineOffer(offerId: String) {
        viewModelScope.launch {
            runCatching { reservationSocketRepository.rejectOffer(offerId) }
                .onFailure { sendEffect(ShowError(it.message ?: "Failed to decline offer")) }
        }
    }

    private fun confirmCancelSearch() {
        updateState { copy(showCancelConfirmation = false) }
        viewModelScope.launch {
            runCatching { cancelVisitUseCase(serviceRequestId) }
            runCatching { reservationSocketRepository.cancelRequest(serviceRequestId) }
            sendEffect(NavigateBack)
        }
    }

    private fun confirmPayment() {
        val offer = state.value.selectedOfferForPayment ?: return
        viewModelScope.launch {
            runCatching { reservationSocketRepository.acceptOffer(offer.id) }
                .onSuccess {
                    updateState { copy(showPaymentSheet = false, selectedOfferForPayment = null) }
                    socketServiceController.startService(offer.serviceRequestId)
                    sendEffect(NavigateToEnRoute(offer.serviceRequestId))
                }
                .onFailure {
                    sendEffect(ShowError(it.message ?: "Failed to accept offer"))
                }
        }
    }

    override fun onCleared() {
        super.onCleared()
        observationJob?.cancel()
        connectionJob?.cancel()
        notificationJob?.cancel()
        offersRefreshJob?.cancel()
    }

    companion object {
        private const val SOCKET_SUBSCRIBE_GRACE_MS = 2_000L
    }
}
