package com.carenest.presentation.ui.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carenest.domain.model.LocationDetails
import com.carenest.domain.repository.GeocodingRepository
import com.carenest.presentation.core.mvi.DefaultEffectPublisher
import com.carenest.presentation.core.mvi.DefaultStateHolder
import com.carenest.presentation.core.mvi.EffectPublisher
import com.carenest.presentation.core.mvi.StateHolder
import com.mapbox.geojson.Point
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val geocodingRepository: GeocodingRepository,
) : ViewModel(),
    StateHolder<MapUiState> by DefaultStateHolder(MapUiState()),
    EffectPublisher<MapEffect> by DefaultEffectPublisher() {

    private var geocodingJob: Job? = null

    fun onIntent(intent: MapIntent) {
        when (intent) {
            is MapIntent.OnMapTapped -> {
                updateState {
                    copy(
                        selectedPoint = intent.point,
                        locationDetails = null,
                        geocodingError = null,
                    )
                }
                performReverseGeocoding(intent.point)
            }

            MapIntent.OnMyLocationClicked -> {
                sendEffect(MapEffect.RequestLocationPermission)
            }

            is MapIntent.OnCurrentLocationReceived -> {
                updateState {
                    copy(
                        selectedPoint = intent.point,
                        locationDetails = null,
                        geocodingError = null,
                    )
                }
                performReverseGeocoding(intent.point)
            }

            MapIntent.OnConfirmLocation -> {
                val details = currentState.locationDetails
                if (details != null) {
                    sendEffect(MapEffect.NavigateBackWithResult(details))
                }
            }

            MapIntent.OnBackClicked -> {
                sendEffect(MapEffect.NavigateBack)
            }
        }
    }

    private fun performReverseGeocoding(point: Point) {
        geocodingJob?.cancel()
        geocodingJob = viewModelScope.launch {
            updateState { copy(isGeocodingLoading = true, geocodingError = null) }

            geocodingRepository.reverseGeocode(
                latitude = point.latitude(),
                longitude = point.longitude(),
            ).onSuccess { locationDetails ->
                updateState {
                    copy(
                        locationDetails = locationDetails,
                        isGeocodingLoading = false,
                    )
                }
            }.onFailure { error ->
                updateState {
                    copy(
                        isGeocodingLoading = false,
                        geocodingError = error.message ?: "Failed to get address",
                    )
                }
            }
        }
    }
}
