package com.carenest.data.socket.models

import com.carenest.domain.socket.model.ReservationEvent
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ReservationEventDtoSerializationTest {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
    }

    @Test
    fun `offer created with canonical keys decodes nurse fields`() {
        val payload = """
            {
              "type": "OFFER_CREATED",
              "reservationId": "res-1",
              "data": {
                "id": "offer-1",
                "serviceRequestId": "sr-1",
                "nurse": {
                  "id": "nurse-1",
                  "firstName": "Ada",
                  "lastName": "Byron",
                  "ratingAvg": 4.5,
                  "totalReviews": 12,
                  "profileImageUrl": "https://cdn.example.com/a.png"
                },
                "proposedPrice": 250.0,
                "status": "PENDING"
              }
            }
        """.trimIndent()

        val event = json.decodeFromString<ReservationEventDto>(payload).toDomain(json)

        assertTrue(event is ReservationEvent.OfferCreated)
        event as ReservationEvent.OfferCreated
        assertEquals("res-1", event.reservationId)
        assertEquals("offer-1", event.offer.id)
        assertEquals("nurse-1", event.offer.nurseId)
        assertEquals("https://cdn.example.com/a.png", event.offer.nurse?.photoUrl)
    }

    @Test
    fun `offer id falls back to offerId alias`() {
        val payload = """
            {
              "type": "OFFER_CREATED",
              "reservationId": "res-1",
              "data": { "offerId": "offer-alias", "proposedPrice": 100.0 }
            }
        """.trimIndent()

        val event = json.decodeFromString<ReservationEventDto>(payload).toDomain(json)

        assertTrue(event is ReservationEvent.OfferCreated)
        assertEquals("offer-alias", (event as ReservationEvent.OfferCreated).offer.id)
    }

    @Test
    fun `avatar url aliases resolve in precedence order`() {
        val canonical = decodeNursePhoto(""" "profileImageUrl": "canonical.png" """)
        assertEquals("canonical.png", canonical)

        val photoUrl = decodeNursePhoto(""" "photoUrl": "photo.png", "profileImage": "legacy.png" """)
        assertEquals("photo.png", photoUrl)

        val avatarUrl = decodeNursePhoto(""" "avatarUrl": "avatar.png", "profileImage": "legacy.png" """)
        assertEquals("avatar.png", avatarUrl)

        val legacy = decodeNursePhoto(""" "profileImage": "legacy.png" """)
        assertEquals("legacy.png", legacy)
    }

    @Test
    fun `missing data decodes to unknown event`() {
        val payload = """ { "type": "OFFER_ACCEPTED", "reservationId": "res-1" } """

        val event = json.decodeFromString<ReservationEventDto>(payload).toDomain(json)

        assertTrue(event is ReservationEvent.Unknown)
        assertEquals("MISSING_DATA", (event as ReservationEvent.Unknown).type)
    }

    @Test
    fun `offers list decodes each entry through alias chain`() {
        val payload = """
            {
              "type": "OFFERS_LIST",
              "reservationId": "res-1",
              "data": [
                { "offerId": "o-1", "proposedPrice": 10.0, "nurse": { "profileImage": "legacy.png" } },
                { "id": "o-2", "proposedPrice": 20.0 }
              ]
            }
        """.trimIndent()

        val event = json.decodeFromString<ReservationEventDto>(payload).toDomain(json)

        assertTrue(event is ReservationEvent.OffersList)
        event as ReservationEvent.OffersList
        assertEquals(listOf("o-1", "o-2"), event.offers.map { it.id })
        assertEquals("legacy.png", event.offers[0].nurse?.photoUrl)
    }

    @Test
    fun `offer created with timestamp-array dates decodes`() {
        val payload = """
            {
              "type": "OFFER_CREATED",
              "reservationId": "res-1",
              "data": {
                "id": "offer-1",
                "serviceRequestId": "sr-1",
                "nurse": { "id": "nurse-1", "firstName": "Ada", "lastName": "Byron" },
                "proposedPrice": 250.5,
                "proposedDate": [2026, 8, 15],
                "proposedTime": [10, 30],
                "message": null,
                "status": "PENDING",
                "distanceKm": 0.5,
                "createdAt": [2026, 8, 25, 6, 57, 57],
                "updatedAt": [2026, 8, 25, 6, 57, 57]
              }
            }
        """.trimIndent()

        val event = json.decodeFromString<ReservationEventDto>(payload).toDomain(json)

        assertTrue(event is ReservationEvent.OfferCreated)
        event as ReservationEvent.OfferCreated
        assertEquals("2026-8-15", event.offer.proposedDate)
        assertEquals("10:30", event.offer.proposedTime)
        assertEquals("2026-8-25T6:57:57", event.offer.createdAt)
    }

    private fun decodeNursePhoto(nurseJson: String): String? {
        val payload = """
            {
              "type": "OFFER_CREATED",
              "reservationId": "res-1",
              "data": { "id": "offer-1", "nurse": { ${nurseJson.trim()} } }
            }
        """.trimIndent()
        val event = json.decodeFromString<ReservationEventDto>(payload).toDomain(json)
        return (event as ReservationEvent.OfferCreated).offer.nurse?.photoUrl
    }
}
