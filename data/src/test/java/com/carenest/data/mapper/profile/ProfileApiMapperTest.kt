package com.carenest.data.mapper.profile

import com.carenest.data.source.remote.dto.profile.ProfileMedicationResponseDto
import org.junit.Assert.assertEquals
import org.junit.Test

class ProfileApiMapperTest {
    @Test fun medicationDto_mapsBackendRelationship() {
        val mapped = ProfileMedicationResponseDto(
            id = "33333333-3333-3333-3333-333333333333",
            profileId = "22222222-2222-2222-2222-222222222222",
            medicationId = "44444444-4444-4444-4444-444444444444", medicationName = "Aspirin"
        ).toDomain()
        assertEquals("44444444-4444-4444-4444-444444444444", mapped.medicationId)
        assertEquals("Aspirin", mapped.name)
    }
}
