package com.carenest.domain.socket.model

data class AvailabilityRequest(
    val available: Boolean,
    val lat: Double? = null,
    val lng: Double? = null
)

data class LocationRequest(
    val lat: Double,
    val lng: Double
)

data class OfferCreateRequest(
    val serviceRequestId: String,
    val proposedPrice: Double,
    val proposedDate: String,
    val proposedTime: String,
    val message: String? = null
)

data class OfferUpdateRequest(
    val offerId: String,
    val proposedPrice: Double,
    val proposedDate: String,
    val proposedTime: String,
    val message: String? = null
)

data class OfferCounterRequest(
    val offerId: String,
    val proposedPrice: Double,
    val proposedDate: String,
    val proposedTime: String,
    val message: String? = null
)

data class OfferActionRequest(
    val offerId: String
)

data class CancelRequest(
    val serviceRequestId: String
)

data class OffersListRequest(
    val serviceRequestId: String
)

data class SendMessageRequest(
    val content: String
)
