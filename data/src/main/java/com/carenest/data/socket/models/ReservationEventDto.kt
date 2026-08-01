package com.carenest.data.socket.models

import com.carenest.domain.socket.model.NurseOfferResponse
import com.carenest.domain.socket.model.ReservationEvent
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.Json

@Serializable
data class ReservationEventDto(
    val type: String,
    val reservationId: String,
    val data: JsonElement? = null
) {
    fun toDomain(json: Json): ReservationEvent {
        return when (type) {
            "OFFER_CREATED" -> ReservationEvent.OfferCreated(
                reservationId,
                json.decodeFromJsonElement<NurseOfferResponseDto>(data!!).toDomain()
            )
            "OFFER_UPDATED" -> ReservationEvent.OfferUpdated(
                reservationId,
                json.decodeFromJsonElement<NurseOfferResponseDto>(data!!).toDomain()
            )
            "OFFER_COUNTERED" -> ReservationEvent.OfferCountered(
                reservationId,
                json.decodeFromJsonElement<NurseOfferResponseDto>(data!!).toDomain()
            )
            "OFFER_ACCEPTED" -> ReservationEvent.OfferAccepted(
                reservationId,
                json.decodeFromJsonElement<NurseOfferResponseDto>(data!!).toDomain()
            )
            "OFFER_WITHDRAWN" -> ReservationEvent.OfferWithdrawn(
                reservationId,
                json.decodeFromJsonElement<OfferIdPayloadDto>(data!!).offerId
            )
            "OFFER_REJECTED" -> ReservationEvent.OfferRejected(
                reservationId,
                json.decodeFromJsonElement<OfferIdPayloadDto>(data!!).offerId
            )
            "REQUEST_CANCELLED" -> ReservationEvent.RequestCancelled(reservationId)
            "OFFERS_LIST" -> ReservationEvent.OffersList(
                reservationId,
                json.decodeFromJsonElement<List<NurseOfferResponseDto>>(data!!).map { it.toDomain() }
            )
            else -> ReservationEvent.Unknown(reservationId, type)
        }
    }
}

@Serializable
data class NurseOfferResponseDto(
    val id: String,
    val serviceRequestId: String,
    val nurseId: String,
    val proposedPrice: Double,
    val proposedDate: String,
    val proposedTime: String,
    val message: String?,
    val status: String,
    val createdAt: String,
    val updatedAt: String
) {
    fun toDomain() = NurseOfferResponse(
        id,
        serviceRequestId,
        nurseId,
        proposedPrice,
        proposedDate,
        proposedTime,
        message,
        status,
        createdAt,
        updatedAt
    )
}

@Serializable
data class OfferIdPayloadDto(
    val offerId: String
)
