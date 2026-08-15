package com.carenest.presentation.ui.address

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carenest.domain.model.LocationDetails
import com.carenest.domain.usecase.location.GeocodeAddressUseCase
import com.carenest.domain.usecase.location.ReverseGeocodeLocationUseCase
import com.carenest.presentation.core.mvi.DefaultEffectPublisher
import com.carenest.presentation.core.mvi.DefaultStateHolder
import com.carenest.presentation.core.mvi.EffectPublisher
import com.carenest.presentation.core.mvi.StateHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ManualAddressViewModel @Inject constructor(
    private val geocodeAddressUseCase: GeocodeAddressUseCase,
    private val reverseGeocodeLocationUseCase: ReverseGeocodeLocationUseCase
) : ViewModel(),
    StateHolder<ManualAddressUiState> by DefaultStateHolder(ManualAddressUiState()),
    EffectPublisher<ManualAddressEffect> by DefaultEffectPublisher() {

    private var isInitialized = false

    fun init(
        initialAddress: String?,
        initialApartment: String?,
        initialDistrict: String?,
        latitude: Double?,
        longitude: Double?,
        defaultCountry: String
    ) {
        if (isInitialized) return
        isInitialized = true
        updateState {
            copy(
                country = defaultCountry,
                street = initialAddress.orEmpty(),
                apartment = initialApartment.orEmpty(),
                area = initialDistrict.orEmpty(),
                initialStreet = initialAddress.orEmpty(),
                initialApartment = initialApartment.orEmpty(),
                initialArea = initialDistrict.orEmpty(),
                latitude = latitude,
                longitude = longitude,
                coordinatesStale = false
            )
        }
    }

    fun onIntent(intent: ManualAddressIntent) {
        when (intent) {
            is ManualAddressIntent.OnCountryChanged -> onGeographicFieldChanged { copy(country = intent.country) }
            is ManualAddressIntent.OnCityChanged -> onGeographicFieldChanged { copy(city = intent.city, cityHasError = false, geocodingError = false) }
            is ManualAddressIntent.OnAreaChanged -> onGeographicFieldChanged { copy(area = intent.area, areaHasError = false, geocodingError = false) }
            is ManualAddressIntent.OnStreetChanged -> onGeographicFieldChanged { copy(street = intent.street, streetHasError = false, geocodingError = false) }
            is ManualAddressIntent.OnBuildingChanged -> updateState { copy(building = intent.building) }
            is ManualAddressIntent.OnApartmentChanged -> updateState { copy(apartment = intent.apartment) }
            is ManualAddressIntent.OnLandmarkChanged -> updateState { copy(landmark = intent.landmark) }

            ManualAddressIntent.OnUseCurrentLocationClicked -> sendEffect(ManualAddressEffect.RequestLocationPermission)

            ManualAddressIntent.OnCurrentLocationRequested -> {
                updateState { copy(isLoadingCurrentLocation = true) }
            }

            ManualAddressIntent.OnCurrentLocationFailed -> {
                updateState { copy(isLoadingCurrentLocation = false) }
                sendEffect(ManualAddressEffect.ShowError(com.carenest.presentation.R.string.error_location_unavailable))
            }

            is ManualAddressIntent.OnCurrentCoordinatesReceived -> {
                fetchCurrentLocationDetails(intent.latitude, intent.longitude)
            }

            is ManualAddressIntent.OnMapLocationReceived -> {
                applyMapResult(intent.locationDetails)
            }

            ManualAddressIntent.OnMapPreviewClicked -> sendEffect(ManualAddressEffect.NavigateToMap)
            ManualAddressIntent.OnSaveClicked -> validateAndSaveAddress()
            ManualAddressIntent.OnBackClicked -> sendEffect(ManualAddressEffect.NavigateBack)
        }
    }

    /**
     * When the user changes a geographic field (country, city, area, street),
     * mark coordinates as stale so we re-geocode on save.
     */
    private fun onGeographicFieldChanged(update: ManualAddressUiState.() -> ManualAddressUiState) {
        updateState {
            update().copy(coordinatesStale = true)
        }
    }

    /**
     * Flow A: Use Current Location.
     * GPS provides lat/lng → reverse geocode → populate ALL address fields.
     */
    private fun fetchCurrentLocationDetails(latitude: Double, longitude: Double) {
        updateState {
            copy(
                latitude = latitude,
                longitude = longitude,
                coordinatesStale = false,
                isLoadingCurrentLocation = true,
                geocodingError = false
            )
        }

        viewModelScope.launch {
            reverseGeocodeLocationUseCase(latitude, longitude)
                .onSuccess { details ->
                    updateState {
                        copy(
                            isLoadingCurrentLocation = false,
                            // Current location overwrites all geographic fields
                            street = details.address,
                            area = details.district,
                            city = details.city,
                            apartment = details.apartment,
                            coordinatesStale = false
                        )
                    }
                }
                .onFailure {
                    // Coordinates are still valid even if reverse geocoding fails.
                    // User can fill in address fields manually.
                    updateState { copy(isLoadingCurrentLocation = false) }
                    sendEffect(ManualAddressEffect.ShowError(com.carenest.presentation.R.string.error_location_unavailable))
                }
        }
    }

    /**
     * Flow B: Map selection returned a LocationDetails (already reverse-geocoded by MapViewModel).
     * Update coordinates and populate empty geographic fields OR default fields. 
     * Do NOT destroy user's explicit manual input.
     */
    private fun applyMapResult(mapDetails: LocationDetails) {
        updateState {
            copy(
                latitude = mapDetails.latitude,
                longitude = mapDetails.longitude,
                coordinatesStale = false,
                geocodingError = false,
                // Only populate fields that the user hasn't explicitly changed from the initial value
                street = if (street.isBlank() || street == initialStreet) mapDetails.address else street,
                area = if (area.isBlank() || area == initialArea) mapDetails.district else area,
                city = if (city.isBlank()) mapDetails.city else city,
                apartment = if (apartment.isBlank() || apartment == initialApartment) mapDetails.apartment else apartment
            )
        }
    }

    private fun validateAndSaveAddress() {
        val currentState = state.value

        val isStreetBlank = currentState.street.isBlank()
        val isCityAndAreaBlank = currentState.city.isBlank() && currentState.area.isBlank()

        if (isStreetBlank || isCityAndAreaBlank) {
            updateState {
                copy(
                    streetHasError = isStreetBlank,
                    cityHasError = currentState.city.isBlank() && currentState.area.isBlank(),
                    areaHasError = currentState.city.isBlank() && currentState.area.isBlank()
                )
            }
            return
        }

        val currentLat = currentState.latitude
        val currentLon = currentState.longitude
        val hasValidCoordinates = currentLat != null && currentLon != null && !currentState.coordinatesStale

        if (hasValidCoordinates) {
            confirmLocationWithCoordinates(currentLat!!, currentLon!!)
        } else {
            geocodeAndConfirm()
        }
    }

    private fun geocodeAndConfirm() {
        val currentState = state.value
        val query = listOf(
            currentState.street.trim(),
            currentState.area.trim(),
            currentState.city.trim(),
            currentState.country.trim()
        ).filter { it.isNotBlank() }.joinToString(", ")

        updateState { copy(isGeocoding = true, geocodingError = false) }

        viewModelScope.launch {
            geocodeAddressUseCase(query)
                .onSuccess { geocoded ->
                    updateState {
                        copy(
                            isGeocoding = false,
                            latitude = geocoded.latitude,
                            longitude = geocoded.longitude,
                            coordinatesStale = false
                        )
                    }
                    confirmLocationWithCoordinates(geocoded.latitude, geocoded.longitude)
                }
                .onFailure {
                    updateState { copy(isGeocoding = false, geocodingError = true) }
                }
        }
    }

    /**
     * Build the final LocationDetails from the user's actual input.
     * The user's text is always the source of truth for the address string.
     * Coordinates come from geocoding or map selection — never from the user's text.
     */
    private fun confirmLocationWithCoordinates(lat: Double, lon: Double) {
        val s = state.value

        // Compose the full user-entered address into LocationDetails.address
        val addressParts = listOf(
            s.street.trim(),
            s.building.trim().takeIf { it.isNotBlank() },
            s.apartment.trim().takeIf { it.isNotBlank() },
            s.area.trim().takeIf { it.isNotBlank() },
            s.city.trim().takeIf { it.isNotBlank() },
            s.landmark.trim().takeIf { it.isNotBlank() },
            s.country.trim().takeIf { it.isNotBlank() }
        ).filterNotNull().filter { it.isNotBlank() }.joinToString(", ")

        // apartment field: building + apt number
        val apartmentField = listOfNotNull(
            s.building.trim().takeIf { it.isNotBlank() },
            s.apartment.trim().takeIf { it.isNotBlank() }
        ).joinToString(", ")

        // district field: area + city
        val districtField = listOfNotNull(
            s.area.trim().takeIf { it.isNotBlank() },
            s.city.trim().takeIf { it.isNotBlank() }
        ).joinToString(", ")

        val locationDetails = LocationDetails(
            address = addressParts.ifBlank { s.street.trim() },
            apartment = apartmentField,
            district = districtField.ifBlank { s.area.trim() },
            city = s.city.trim(),
            latitude = lat,
            longitude = lon
        )

        sendEffect(ManualAddressEffect.ConfirmLocation(locationDetails))
    }
}
