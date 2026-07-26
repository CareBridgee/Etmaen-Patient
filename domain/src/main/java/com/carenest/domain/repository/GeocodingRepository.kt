package com.carenest.domain.repository

import com.carenest.domain.model.LocationDetails

interface GeocodingRepository {
    suspend fun reverseGeocode(latitude: Double, longitude: Double): Result<LocationDetails>
}
