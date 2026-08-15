package com.carenest.domain.usecase.location

import com.carenest.domain.model.LocationDetails
import com.carenest.domain.repository.GeocodingRepository
import javax.inject.Inject

class ReverseGeocodeLocationUseCase @Inject constructor(
    private val geocodingRepository: GeocodingRepository
) {
    suspend operator fun invoke(latitude: Double, longitude: Double): Result<LocationDetails> {
        return geocodingRepository.reverseGeocode(latitude, longitude)
    }
}
