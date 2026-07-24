package com.carenest.presentation.ui.map

import com.carenest.domain.model.LocationDetails
import com.mapbox.geojson.Point

data class MapUiState(
    val selectedPoint: Point? = null,
    val locationDetails: LocationDetails? = null,
    val isGeocodingLoading: Boolean = false,
    val geocodingError: String? = null,
)

sealed interface MapIntent {
    data class OnMapTapped(val point: Point) : MapIntent
    data object OnMyLocationClicked : MapIntent
    data class OnCurrentLocationReceived(val point: Point) : MapIntent
    data object OnConfirmLocation : MapIntent
    data object OnBackClicked : MapIntent
}

sealed interface MapEffect {
    data class NavigateBackWithResult(val locationDetails: LocationDetails) : MapEffect
    data object NavigateBack : MapEffect
    data class ShowError(val message: String) : MapEffect
    data object RequestLocationPermission : MapEffect
}
