package com.carenest.data.socket.models

import com.carenest.domain.socket.model.AvailabilityRequest
import com.carenest.domain.socket.model.CancelRequest
import com.carenest.domain.socket.model.LocationRequest
import com.carenest.domain.socket.model.OfferActionRequest
import com.carenest.domain.socket.model.OfferCounterRequest
import com.carenest.domain.socket.model.OfferCreateRequest
import com.carenest.domain.socket.model.OfferUpdateRequest
import com.carenest.domain.socket.model.OffersListRequest
import com.carenest.domain.socket.model.SendMessageRequest
import kotlinx.serialization.Serializable

@Serializable
data class AvailabilityRequestDto(
    val available: Boolean,
    val lat: Double? = null,
    val lng: Double? = null
)

@Serializable
data class LocationRequestDto(
    val lat: Double,
    val lng: Double
)

@Serializable
data class OfferCreateRequestDto(
    val serviceRequestId: String,
    val proposedPrice: Double,
    val proposedDate: String,
    val proposedTime: String,
    val message: String? = null
)

@Serializable
data class OfferUpdateRequestDto(
    val offerId: String,
    val proposedPrice: Double,
    val proposedDate: String,
    val proposedTime: String,
    val message: String? = null
)

@Serializable
data class OfferCounterRequestDto(
    val offerId: String,
    val proposedPrice: Double,
    val proposedDate: String,
    val proposedTime: String,
    val message: String? = null
)

@Serializable
data class OfferActionRequestDto(
    val offerId: String
)

@Serializable
data class CancelRequestDto(
    val serviceRequestId: String
)

@Serializable
data class OffersListRequestDto(
    val serviceRequestId: String
)

@Serializable
data class SendMessageRequestDto(
    val content: String
)

// Mapping extensions
fun AvailabilityRequest.toDto() = AvailabilityRequestDto(available, lat, lng)
fun LocationRequest.toDto() = LocationRequestDto(lat, lng)
fun OfferCreateRequest.toDto() = OfferCreateRequestDto(serviceRequestId, proposedPrice, proposedDate, proposedTime, message)
fun OfferUpdateRequest.toDto() = OfferUpdateRequestDto(offerId, proposedPrice, proposedDate, proposedTime, message)
fun OfferCounterRequest.toDto() = OfferCounterRequestDto(offerId, proposedPrice, proposedDate, proposedTime, message)
fun OfferActionRequest.toDto() = OfferActionRequestDto(offerId)
fun CancelRequest.toDto() = CancelRequestDto(serviceRequestId)
fun OffersListRequest.toDto() = OffersListRequestDto(serviceRequestId)
fun SendMessageRequest.toDto() = SendMessageRequestDto(content)
