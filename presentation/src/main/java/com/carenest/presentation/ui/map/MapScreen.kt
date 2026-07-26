package com.carenest.presentation.ui.map

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.carenest.designsystem.components.button.PrimaryButton
import com.carenest.designsystem.theme.Theme
import com.carenest.domain.model.LocationDetails
import com.carenest.presentation.core.mvi.ObserveEffect
import com.carenest.presentation.util.LocationPermissionHandler
import com.google.android.gms.location.LocationServices
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapboxExperimental
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.annotation.ViewAnnotation
import com.mapbox.maps.extension.compose.rememberMapState
import com.mapbox.maps.plugin.animation.flyTo
import com.mapbox.maps.viewannotation.geometry
import com.mapbox.maps.viewannotation.viewAnnotationOptions

@OptIn(MapboxExperimental::class)
@SuppressLint("MissingPermission")
@Composable
fun MapScreen(
    onLocationConfirmed: (LocationDetails) -> Unit,
    onBack: () -> Unit,
    viewModel: MapViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    // Default location: Cairo
    val defaultPoint = Point.fromLngLat(31.2357, 30.0444)
    var cameraOptions by remember {
        mutableStateOf(
            CameraOptions.Builder()
                .center(defaultPoint)
                .zoom(12.0)
                .build()
        )
    }
    var flyToTrigger by remember { mutableLongStateOf(0L) }

    var showLocationPermissionHandler by remember { mutableStateOf(false) }
    var showLocationRationale by remember { mutableStateOf(false) }

    ObserveEffect(viewModel.effect) { effect ->
        when (effect) {
            is MapEffect.NavigateBackWithResult -> onLocationConfirmed(effect.locationDetails)
            MapEffect.NavigateBack -> onBack()
            is MapEffect.ShowError -> { /* Could show a toast here */ }
            MapEffect.RequestLocationPermission -> {
                showLocationPermissionHandler = true
            }
        }
    }

    if (showLocationPermissionHandler) {
        LocationPermissionHandler(
            onPermissionGranted = {
                showLocationPermissionHandler = false
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    location?.let {
                        val currentPoint = Point.fromLngLat(it.longitude, it.latitude)
                        cameraOptions = CameraOptions.Builder()
                            .center(currentPoint)
                            .zoom(15.0)
                            .build()
                        flyToTrigger = System.currentTimeMillis()
                        viewModel.onIntent(MapIntent.OnCurrentLocationReceived(currentPoint))
                    }
                }
            },
            onPermissionDenied = {
                showLocationPermissionHandler = false
                showLocationRationale = true
            },
            showRationale = showLocationRationale,
            onRationaleDismissed = {
                showLocationRationale = false
            },
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        MapboxMap(
            modifier = Modifier.fillMaxSize(),
            mapState = rememberMapState(),
            onMapClickListener = { point ->
                cameraOptions = CameraOptions.Builder()
                    .center(point)
                    .zoom(15.0)
                    .build()
                flyToTrigger = System.currentTimeMillis()
                viewModel.onIntent(MapIntent.OnMapTapped(point))
                true
            },
        ) {
            MapEffect(cameraOptions, flyToTrigger) { map ->
                map.mapboxMap.flyTo(cameraOptions)
            }

            state.selectedPoint?.let { point ->
                ViewAnnotation(
                    options = viewAnnotationOptions {
                        geometry(point)
                        allowOverlap(true)
                    },
                ) {
                    MarkerPin()
                }
            }
        }

        IconButton(
            onClick = { viewModel.onIntent(MapIntent.OnBackClicked) },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(Theme.spacing.medium)
                .padding(top = 32.dp)
                .shadow(8.dp, CircleShape)
                .clip(CircleShape)
                .background(Theme.colors.backGround)
                .size(44.dp),
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Theme.colors.primaryFont,
            )
        }

        FloatingActionButton(
            onClick = { viewModel.onIntent(MapIntent.OnMyLocationClicked) },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(Theme.spacing.medium)
                .padding(bottom = if (state.selectedPoint != null) 280.dp else 80.dp),
            containerColor = Theme.colors.backGround,
            contentColor = Theme.colors.primary,
            shape = CircleShape,
        ) {
            Icon(Icons.Default.MyLocation, contentDescription = "My Location")
        }

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
                    animationSpec = tween(durationMillis = 300),
                ) + fadeOut(animationSpec = tween(durationMillis = 200)),
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

@Composable
private fun MarkerPin() {
    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 300),
        label = "marker_scale",
    )

    Icon(
        imageVector = Icons.Default.LocationOn,
        contentDescription = null,
        tint = Theme.colors.onErrorContainer,
        modifier = Modifier
            .size(Theme.size.medium)
            .scale(scale),
    )
}

@Composable
private fun LocationInfoCard(
    locationDetails: LocationDetails?,
    isLoading: Boolean,
    error: String?,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .shadow(12.dp, RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
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
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = Theme.spacing.small),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Color(0xFFE53935),
                        modifier = Modifier.size(28.dp),
                    )
                    Spacer(modifier = Modifier.width(Theme.spacing.small))
                    BasicText(
                        text = error,
                        style = Theme.typography.body.medium.copy(
                            color = Color(0xFFE53935),
                        ),
                    )
                }
            }

            locationDetails != null -> {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top,
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Theme.colors.primaryVariant.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = Theme.colors.primaryVariant,
                            modifier = Modifier.size(24.dp),
                        )
                    }

                    Spacer(modifier = Modifier.width(Theme.spacing.small))

                    Column(modifier = Modifier.weight(1f)) {
                        BasicText(
                            text = locationDetails.address,
                            style = Theme.typography.body.large.copy(
                                color = Theme.colors.primaryFont,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp,
                            ),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )

                        val subtitle = listOfNotNull(
                            locationDetails.apartment.takeIf { it.isNotBlank() },
                            locationDetails.district.takeIf { it.isNotBlank() },
                        ).joinToString(" • ")

                        if (subtitle.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            BasicText(
                                text = subtitle,
                                style = Theme.typography.body.medium.copy(
                                    color = Theme.colors.secondaryFont,
                                    fontSize = 14.sp,
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        BasicText(
                            text = "%.5f, %.5f".format(
                                locationDetails.latitude,
                                locationDetails.longitude,
                            ),
                            style = Theme.typography.body.small.copy(
                                color = Theme.colors.secondaryFont.copy(alpha = 0.6f),
                                fontSize = 12.sp,
                            ),
                        )
                    }
                }
            }
        }
    }
}
