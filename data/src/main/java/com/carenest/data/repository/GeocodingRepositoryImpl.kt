package com.carenest.data.repository

import com.carenest.data.di.IoDispatcher
import com.carenest.data.source.remote.service.GeocodingApiService
import com.carenest.domain.model.LocationDetails
import com.carenest.domain.repository.GeocodingRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GeocodingRepositoryImpl @Inject constructor(
    private val geocodingApiService: GeocodingApiService,
    @IoDispatcher private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : GeocodingRepository {

    override suspend fun reverseGeocode(
        latitude: Double,
        longitude: Double
    ): Result<LocationDetails> {
        return withContext(dispatcher) {
            geocodingApiService.reverseGeocode(latitude, longitude).map { response ->
                val address = response.displayName ?: ""

                val houseNumber = response.address?.houseNumber
                val road = response.address?.road
                val apartment =
                    listOfNotNull(houseNumber, road).filter { it.isNotBlank() }.joinToString(" ")

                val district = response.address?.suburb ?: response.address?.cityDistrict
                ?: response.address?.city ?: ""

                val lat = response.lat?.toDoubleOrNull() ?: 0.0
                val lon = response.lon?.toDoubleOrNull() ?: 0.0

                LocationDetails(
                    address = address,
                    apartment = apartment,
                    district = district,
                    latitude = lat,
                    longitude = lon
                )
            }
        }
    }
}
