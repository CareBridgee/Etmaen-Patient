package com.carenest.data.socket.models

import com.carenest.domain.socket.model.NurseOfferResponse
import com.carenest.domain.socket.model.ReservationEvent
import com.carenest.data.socket.serialization.FlexibleDateStringSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.Json

@Serializable
data class ReservationEventDto(
    val type: ReservationEventType,
    val reservationId: String = "",
    val data: JsonElement? = null
) {
    fun toDomain(json: Json): ReservationEvent {
        return try {
            when (type) {
                ReservationEventType.OFFER_CREATED -> {
                    val offerDto = data?.let { json.decodeFromJsonElement<NurseOfferResponseDto>(it) }
                    if (offerDto != null) ReservationEvent.OfferCreated(reservationId, offerDto.toDomain())
                    else ReservationEvent.Unknown(reservationId, "MISSING_DATA")
                }

                ReservationEventType.OFFER_UPDATED -> {
                    val offerDto = data?.let { json.decodeFromJsonElement<NurseOfferResponseDto>(it) }
                    if (offerDto != null) ReservationEvent.OfferUpdated(reservationId, offerDto.toDomain())
                    else ReservationEvent.Unknown(reservationId, "MISSING_DATA")
                }

                ReservationEventType.OFFER_COUNTERED -> {
                    val offerDto = data?.let { json.decodeFromJsonElement<NurseOfferResponseDto>(it) }
                    if (offerDto != null) ReservationEvent.OfferCountered(reservationId, offerDto.toDomain())
                    else ReservationEvent.Unknown(reservationId, "MISSING_DATA")
                }

                ReservationEventType.OFFER_ACCEPTED -> {
                    val offerDto = data?.let { json.decodeFromJsonElement<NurseOfferResponseDto>(it) }
                    if (offerDto != null) ReservationEvent.OfferAccepted(reservationId, offerDto.toDomain())
                    else ReservationEvent.Unknown(reservationId, "MISSING_DATA")
                }

                ReservationEventType.OFFER_WITHDRAWN -> {
                    val payload = data?.let { json.decodeFromJsonElement<OfferIdPayloadDto>(it) }
                    if (payload != null) ReservationEvent.OfferWithdrawn(reservationId, payload.offerId)
                    else ReservationEvent.Unknown(reservationId, "MISSING_DATA")
                }

                ReservationEventType.OFFER_REJECTED -> {
                    val payload = data?.let { json.decodeFromJsonElement<OfferIdPayloadDto>(it) }
                    if (payload != null) ReservationEvent.OfferRejected(reservationId, payload.offerId)
                    else ReservationEvent.Unknown(reservationId, "MISSING_DATA")
                }

                ReservationEventType.REQUEST_CANCELLED -> ReservationEvent.RequestCancelled(reservationId)
                ReservationEventType.PRESENCE_UPDATE -> {
                    val presence = data?.let { json.decodeFromJsonElement<PresenceUpdateDto>(it) }
                    if (presence != null) ReservationEvent.PresenceUpdate(reservationId, presence.isOnline)
                    else ReservationEvent.Unknown(reservationId, "MISSING_DATA")
                }

                ReservationEventType.COMPLETED -> ReservationEvent.Completed
                ReservationEventType.OFFERS_LIST -> {
                    val offers = data?.let { json.decodeFromJsonElement<List<NurseOfferResponseDto>>(it) }
                    if (offers != null) ReservationEvent.OffersList(reservationId, offers.map { it.toDomain() })
                    else ReservationEvent.Unknown(reservationId, "MISSING_DATA")
                }

                ReservationEventType.UNKNOWN -> ReservationEvent.Unknown(reservationId, "UNKNOWN")
            }
        } catch (e: Exception) {
            ReservationEvent.Unknown(reservationId, "DECODE_ERROR: ${e.message}")
        }
    }
}

@Serializable
data class PresenceUpdateDto(
    val isOnline: Boolean
)

@Serializable
data class NurseInfoDto(
    @SerialName("id") val id: String? = null,
    @SerialName("firstName") val firstName: String? = null,
    @SerialName("lastName") val lastName: String? = null,
    @SerialName("ratingAvg") val ratingAvg: Double? = null,
    @SerialName("totalReviews") val totalReviews: Int? = null,
    @SerialName("profileImageUrl") val profileImageUrl: String? = null,
    @SerialName("photoUrl") val photoUrl: String? = null,
    @SerialName("avatarUrl") val avatarUrl: String? = null,
    @SerialName("profileImage") val profileImage: String? = null
) {
    fun toDomain() = com.carenest.domain.socket.model.NurseInfo(
        id = id ?: "",
        firstName = firstName ?: "",
        lastName = lastName ?: "",
        ratingAvg = ratingAvg ?: 0.0,
        totalReviews = totalReviews ?: 0,
        photoUrl = profileImageUrl ?: photoUrl ?: avatarUrl ?: profileImage
    )
}

@Serializable
data class NurseOfferResponseDto(
    @SerialName("id") val id: String? = null,
    @SerialName("offerId") val offerId: String? = null,
    @SerialName("serviceRequestId") val serviceRequestId: String? = null,
    @SerialName("nurse") val nurse: NurseInfoDto? = null,
    @SerialName("nurseId") val nurseId: String? = null,
    @SerialName("proposedPrice") val proposedPrice: Double? = null,
    @SerialName("proposedDate") @Serializable(with = FlexibleDateStringSerializer::class) val proposedDate: String? = null,
    @SerialName("proposedTime") @Serializable(with = FlexibleDateStringSerializer::class) val proposedTime: String? = null,
    @SerialName("message") val message: String? = null,
    @SerialName("status") val status: String? = null,
    @SerialName("createdAt") @Serializable(with = FlexibleDateStringSerializer::class) val createdAt: String? = null,
    @SerialName("updatedAt") @Serializable(with = FlexibleDateStringSerializer::class) val updatedAt: String? = null
) {
    fun toDomain() = NurseOfferResponse(
        id = id ?: offerId ?: "",
        serviceRequestId = serviceRequestId ?: "",
        nurse = nurse?.toDomain(),
        proposedPrice = proposedPrice ?: 0.0,
        proposedDate = proposedDate ?: "",
        proposedTime = proposedTime ?: "",
        message = message,
        status = status ?: "PENDING",
        createdAt = createdAt ?: "",
        updatedAt = updatedAt ?: "",
        nurseId = nurse?.id ?: nurseId ?: ""
    )
}

@Serializable
data class OfferIdPayloadDto(
    @SerialName("offerId") val offerId: String
)
