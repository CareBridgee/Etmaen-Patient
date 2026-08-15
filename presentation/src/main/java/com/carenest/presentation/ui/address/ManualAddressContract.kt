package com.carenest.presentation.ui.address

import com.carenest.domain.model.LocationDetails

data class ManualAddressUiState(
    val country: String = "",
    val city: String = "",
    val area: String = "",
    val street: String = "",
    val building: String = "",
    val apartment: String = "",
    val landmark: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val initialStreet: String = "",
    val initialArea: String = "",
    val initialApartment: String = "",
    /** True when geographic fields (street/city/area/country) have changed since coordinates were resolved. */
    val coordinatesStale: Boolean = false,
    val cityHasError: Boolean = false,
    val areaHasError: Boolean = false,
    val streetHasError: Boolean = false,
    val isGeocoding: Boolean = false,
    val isLoadingCurrentLocation: Boolean = false,
    val isReverseGeocoding: Boolean = false,
    val geocodingError: Boolean = false,
)

sealed class ManualAddressIntent {
    data class OnCountryChanged(val country: String) : ManualAddressIntent()
    data class OnCityChanged(val city: String) : ManualAddressIntent()
    data class OnAreaChanged(val area: String) : ManualAddressIntent()
    data class OnStreetChanged(val street: String) : ManualAddressIntent()
    data class OnBuildingChanged(val building: String) : ManualAddressIntent()
    data class OnApartmentChanged(val apartment: String) : ManualAddressIntent()
    data class OnLandmarkChanged(val landmark: String) : ManualAddressIntent()
    data object OnUseCurrentLocationClicked : ManualAddressIntent()
    data object OnCurrentLocationRequested : ManualAddressIntent()
    data object OnCurrentLocationFailed : ManualAddressIntent()
    data class OnCurrentCoordinatesReceived(val latitude: Double, val longitude: Double) : ManualAddressIntent()
    /** Coordinates selected from MapScreen. Triggers reverse geocoding to populate address fields. */
    data class OnMapLocationReceived(val locationDetails: LocationDetails) : ManualAddressIntent()
    data object OnMapPreviewClicked : ManualAddressIntent()
    data object OnSaveClicked : ManualAddressIntent()
    data object OnBackClicked : ManualAddressIntent()
}

sealed class ManualAddressEffect {
    data class ConfirmLocation(val locationDetails: LocationDetails) : ManualAddressEffect()
    data object NavigateToMap : ManualAddressEffect()
    data object NavigateBack : ManualAddressEffect()
    data object RequestLocationPermission : ManualAddressEffect()
    data class ShowError(val messageRes: Int) : ManualAddressEffect()
}
