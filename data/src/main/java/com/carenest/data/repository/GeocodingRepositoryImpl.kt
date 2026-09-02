package com.carenest.data.repository

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
import com.carenest.data.di.qualifier.IoDispatcher
import com.carenest.domain.model.LocationDetails
import com.carenest.domain.repository.GeocodingRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Locale
import javax.inject.Inject
import kotlin.coroutines.resume

class GeocodingRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    @param:IoDispatcher private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : GeocodingRepository {

    private val geocoder = Geocoder(context, Locale.getDefault())

    override suspend fun reverseGeocode(
        latitude: Double,
        longitude: Double
    ): Result<LocationDetails> {
        return withContext(dispatcher) {
            try {
                if (!Geocoder.isPresent()) {
                    return@withContext Result.failure(Exception("Geocoder is not present on this device"))
                }
                
                val address = getAddressFromLocation(latitude, longitude)
                if (address != null) {
                    Result.success(mapToLocationDetails(address, latitude, longitude))
                } else {
                    Result.failure(Exception("No geocoding results found for lat/lon"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun geocode(addressString: String): Result<LocationDetails> {
        return withContext(dispatcher) {
            try {
                if (!Geocoder.isPresent()) {
                    return@withContext Result.failure(Exception("Geocoder is not present on this device"))
                }
                
                val address = getAddressFromLocationName(addressString)
                if (address != null) {
                    Result.success(mapToLocationDetails(address, address.latitude, address.longitude))
                } else {
                    Result.failure(Exception("No geocoding results found for: $addressString"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    private suspend fun getAddressFromLocation(lat: Double, lon: Double): Address? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            suspendCancellableCoroutine { continuation ->
                geocoder.getFromLocation(lat, lon, 1) { addresses ->
                    continuation.resume(addresses.firstOrNull())
                }
            }
        } else {
            @Suppress("DEPRECATION")
            geocoder.getFromLocation(lat, lon, 1)?.firstOrNull()
        }
    }

    private suspend fun getAddressFromLocationName(locationName: String): Address? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            suspendCancellableCoroutine { continuation ->
                geocoder.getFromLocationName(locationName, 1) { addresses ->
                    continuation.resume(addresses.firstOrNull())
                }
            }
        } else {
            @Suppress("DEPRECATION")
            geocoder.getFromLocationName(locationName, 1)?.firstOrNull()
        }
    }

    private fun mapToLocationDetails(address: Address, lat: Double, lon: Double): LocationDetails {
        val fullAddressLine = if (address.maxAddressLineIndex >= 0) {
            (0..address.maxAddressLineIndex)
                .mapNotNull { address.getAddressLine(it) }
                .filter { it.isNotBlank() }
                .joinToString(", ")
        } else {
            null
        }

        // City fallback priority
        val city = address.locality 
            ?: address.subAdminArea 
            ?: address.adminArea 
            ?: ""

        // Area / District fallback priority
        val district = address.subLocality 
            ?: (if (address.subAdminArea != city) address.subAdminArea else null)
            ?: (if (address.featureName != address.thoroughfare && address.featureName != city) address.featureName else null)
            ?: ""

        // Street fallback priority
        val street = address.thoroughfare
            ?: (if (address.subThoroughfare != null && address.thoroughfare != null) "${address.subThoroughfare} ${address.thoroughfare}" else null)
            ?: (if (address.featureName != district && address.featureName != city) address.featureName else null)
            ?: ""

        // Apartment fallback priority
        val apartment = address.subThoroughfare ?: ""

        val displayAddress = fullAddressLine?.ifBlank { null }
            ?: listOfNotNull(
                address.featureName?.takeIf { it != street && it != district && it != city },
                street.takeIf { it.isNotBlank() },
                district.takeIf { it.isNotBlank() },
                city.takeIf { it.isNotBlank() },
                address.countryName?.takeIf { it.isNotBlank() }
            ).distinct().joinToString(", ")

        return LocationDetails(
            address = displayAddress,
            apartment = apartment,
            district = district,
            city = city,
            latitude = lat,
            longitude = lon,
            street = street,
            building = apartment,
            area = district,
            country = address.countryName.orEmpty()
        )
    }
}
