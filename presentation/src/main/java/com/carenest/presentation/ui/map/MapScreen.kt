package com.carenest.presentation.ui.map

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.carenest.designsystem.components.button.PrimaryButton
import com.carenest.designsystem.theme.Theme
import com.carenest.domain.model.LocationDetails
import com.carenest.presentation.core.mvi.ObserveEffect
import com.carenest.presentation.core.util.UiText
import com.carenest.presentation.navigation.HideTopBar
import com.carenest.presentation.navigation.ScreenTopBar
import com.google.android.gms.location.LocationServices
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import com.mapbox.maps.plugin.gestures.gestures
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    onLocationConfirmed: (LocationDetails) -> Unit,
    onBack: () -> Unit,
    viewModel: MapViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val defaultLat = 30.0444
    val defaultLng = 31.2357

    val mapViewportState = rememberMapViewportState {
        setCameraOptions {
            center(Point.fromLngLat(defaultLng, defaultLat))
            zoom(12.0)
            pitch(0.0)
            bearing(0.0)
        }
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            coroutineScope.launch {
                try {
                    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                        location?.let {
                            val currentPoint = Point.fromLngLat(it.longitude, it.latitude)
                            mapViewportState.flyTo(
                                CameraOptions.Builder()
                                    .center(currentPoint)
                                    .zoom(15.0)
                                    .build(),
                                MapAnimationOptions.mapAnimationOptions { duration(1500) }
                            )
                            viewModel.onIntent(MapIntent.OnCurrentLocationReceived(currentPoint))
                        }
                    }
                } catch (e: SecurityException) {
                    // Handle exception
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        val initialPoint = Point.fromLngLat(defaultLng, defaultLat)
        viewModel.onIntent(MapIntent.OnMapTapped(initialPoint))
    }

    ObserveEffect(viewModel.effect) { effect ->
        when (effect) {
            is MapEffect.NavigateBackWithResult -> onLocationConfirmed(effect.locationDetails)
            MapEffect.NavigateBack -> onBack()
            is MapEffect.ShowError -> { /* Could show a toast here */ }
            MapEffect.RequestLocationPermission -> {
                locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    HideTopBar()

    Scaffold(
        topBar = {
            ScreenTopBar(
                title = "Select Location",
                onLeadingClick = onBack
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.onIntent(MapIntent.OnMyLocationClicked) },
                containerColor = Theme.colors.surface,
                contentColor = Theme.colors.primary,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.padding(bottom = 140.dp)
            ) {
                Icon(
                    painter = painterResource(id = com.carenest.designsystem.R.drawable.ic_location),
                    contentDescription = "My Location",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            MapboxMap(
                Modifier.fillMaxSize(),
                mapViewportState = mapViewportState,
                onMapClickListener = { point ->
                    viewModel.onIntent(MapIntent.OnMapTapped(point))
                    true
                }
            ) {
                // Here we could add a marker for the selected point
            }

            // Center Pin Overlay
            Icon(
                painter = painterResource(id = com.carenest.designsystem.R.drawable.ic_location),
                contentDescription = null,
                tint = Theme.colors.primary,
                modifier = Modifier
                    .size(40.dp)
                    .align(Alignment.Center)
                    .offset(y = (-20).dp)
            )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
            ) {
                AnimatedVisibility(
                    visible = state.selectedPoint != null,
                    enter = slideInVertically(
                        initialOffsetY = { it },
                        animationSpec = tween(durationMillis = 400),
                    ) + fadeIn(animationSpec = tween(durationMillis = 300)),
                    exit = slideOutVertically(
                        targetOffsetY = { it },
                        animationSpec = tween(durationMillis = 300)),
                ) {
                    LocationInfoCard(
                        locationDetails = state.locationDetails,
                        isLoading = state.isGeocodingLoading,
                        error = state.geocodingError,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Theme.spacing.medium),
                    )
                }

                PrimaryButton(
                    caption = "Confirm Location",
                    onClick = { viewModel.onIntent(MapIntent.OnConfirmLocation) },
                    isDisabled = state.locationDetails == null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Theme.spacing.medium),
                )
            }
        }
    }
}

@Composable
private fun LocationInfoCard(
    locationDetails: LocationDetails?,
    isLoading: Boolean,
    error: UiText?,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .shadow(12.dp, RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .background(Theme.colors.backGround)
            .padding(Theme.spacing.medium),
    ) {
        when {
            isLoading -> {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = Theme.spacing.small),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Theme.colors.primary,
                        strokeWidth = 2.dp,
                    )
                    Spacer(modifier = Modifier.width(Theme.spacing.small))
                    BasicText(
                        text = "Finding address...",
                        style = Theme.typography.body.medium.copy(
                            color = Theme.colors.secondaryFont,
                        ),
                    )
                }
            }

            error != null -> {
                BasicText(
                    text = error.asString(),
                    style = Theme.typography.body.medium.copy(
                        color = Theme.colors.error,
                        fontWeight = FontWeight.Medium,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = Theme.spacing.small),
                )
            }

            locationDetails != null -> {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    BasicText(
                        text = locationDetails.address,
                        style = Theme.typography.body.large.copy(
                            color = Theme.colors.primaryFont,
                            fontWeight = FontWeight.Bold,
                        ),
                        maxLines = 2,
                    )
                    if (locationDetails.district.isNotBlank()) {
                        BasicText(
                            text = locationDetails.district,
                            style = Theme.typography.body.small.copy(
                                color = Theme.colors.secondaryFont,
                            ),
                        )
                    }
                }
            }
        }
    }
}
