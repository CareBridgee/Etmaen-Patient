package com.carenest.data.socket.models

import kotlinx.serialization.Serializable

@Serializable
enum class ReservationEventType {
    OFFER_CREATED,
    OFFER_UPDATED,
    OFFER_COUNTERED,
    OFFER_ACCEPTED,
    OFFER_WITHDRAWN,
    OFFER_REJECTED,
    REQUEST_CANCELLED,
    COMPLETED,
    OFFERS_LIST,
    UNKNOWN
}
