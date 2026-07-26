package com.carenest.data.source.remote.service

import com.carenest.data.source.remote.dto.ReverseGeocodeResponse

interface GeocodingApiService {
    suspend fun reverseGeocode(latitude: Double, longitude: Double): Result<ReverseGeocodeResponse>
}
