package com.carenest.domain.usecase.location

import com.carenest.domain.model.LocationDetails
import com.carenest.domain.repository.GeocodingRepository
import javax.inject.Inject

class GeocodeAddressUseCase @Inject constructor(
    private val geocodingRepository: GeocodingRepository
) {
    suspend operator fun invoke(address: String): Result<LocationDetails> {
        return geocodingRepository.geocode(address)
    }
}
