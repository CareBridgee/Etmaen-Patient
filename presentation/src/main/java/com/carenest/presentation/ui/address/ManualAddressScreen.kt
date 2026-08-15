package com.carenest.presentation.ui.address

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.carenest.designsystem.R
import com.carenest.designsystem.components.button.PrimaryButton
import com.carenest.designsystem.components.textfield.CustomTextField
import com.carenest.designsystem.theme.SpTheme
import com.carenest.designsystem.theme.Theme
import com.carenest.designsystem.util.noRippleClickable
import com.carenest.domain.model.LocationDetails
import com.carenest.presentation.BuildConfig
import com.carenest.presentation.core.mvi.ObserveEffect
import com.carenest.presentation.navigation.ScreenTopBar
import com.carenest.presentation.util.LocationPermissionHandler
import com.google.android.gms.location.LocationServices

private const val MAP_SNAPSHOT_ZOOM = 14

private fun buildMapSnapshotUrl(latitude: Double, longitude: Double): String {
    val lon = "%.6f".format(longitude)
    val lat = "%.6f".format(latitude)
    val pin = "pin-s+e53935($lon,$lat)"
    return "https://api.mapbox.com/styles/v1/mapbox/streets-v12/static/" +
            "$pin/$lon,$lat,$MAP_SNAPSHOT_ZOOM,0/700x300@2x" +
            "?access_token=${BuildConfig.MAPBOX_ACCESS_TOKEN}"
}

@SuppressLint("MissingPermission")
@Composable
fun ManualAddressScreen(
    initialAddress: String = "",
    initialApartment: String = "",
    initialDistrict: String = "",
    latitude: Double? = null,
    longitude: Double? = null,
    mapResultLocation: LocationDetails? = null,
    onMapResultConsumed: () -> Unit = {},
    onAddressConfirmed: (LocationDetails) -> Unit,
    onNavigateToMap: (Double?, Double?) -> Unit,
    onBack: () -> Unit,
    viewModel: ManualAddressViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    var showLocationPermissionHandler by remember { mutableStateOf(false) }
    val defaultCountry = stringResource(id = R.string.manual_address_country_default)

    // Initialize once with route parameters
    LaunchedEffect(Unit) {
        viewModel.init(
            initialAddress = initialAddress,
            initialApartment = initialApartment,
            initialDistrict = initialDistrict,
            latitude = latitude,
            longitude = longitude,
            defaultCountry = defaultCountry
        )
    }

    // Handle map result: when MapScreen returns a LocationDetails, send it to ViewModel and consume
    LaunchedEffect(mapResultLocation) {
        mapResultLocation?.let {
            viewModel.onIntent(ManualAddressIntent.OnMapLocationReceived(it))
            onMapResultConsumed()
        }
    }

    ObserveEffect(viewModel.effect) { effect ->
        when (effect) {
            is ManualAddressEffect.ConfirmLocation -> onAddressConfirmed(effect.locationDetails)
            ManualAddressEffect.NavigateToMap -> onNavigateToMap(state.latitude, state.longitude)
            ManualAddressEffect.NavigateBack -> onBack()
            ManualAddressEffect.RequestLocationPermission -> {
                showLocationPermissionHandler = true
            }
            is ManualAddressEffect.ShowError -> {
                // We'd use a local ToastHostState or Snackbar but for simplicity we can use standard Toast
                android.widget.Toast.makeText(context, effect.messageRes, android.widget.Toast.LENGTH_SHORT).show()
            }
        }
    }

    if (showLocationPermissionHandler) {
        LocationPermissionHandler(
            onPermissionGranted = {
                showLocationPermissionHandler = false
                viewModel.onIntent(ManualAddressIntent.OnCurrentLocationRequested)
                
                val cancellationTokenSource = CancellationTokenSource()
                fusedLocationClient.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY, 
                    cancellationTokenSource.token
                ).addOnSuccessListener { loc ->
                    loc?.let {
                        viewModel.onIntent(
                            ManualAddressIntent.OnCurrentCoordinatesReceived(it.latitude, it.longitude)
                        )
                    } ?: run {
                        // Location was null even after fresh request (e.g. GPS off or unavailable)
                        viewModel.onIntent(ManualAddressIntent.OnCurrentLocationFailed)
                    }
                }.addOnFailureListener {
                    viewModel.onIntent(ManualAddressIntent.OnCurrentLocationFailed)
                }
            },
            onPermissionDenied = {
                showLocationPermissionHandler = false
                viewModel.onIntent(ManualAddressIntent.OnCurrentLocationFailed)
            },
            showRationale = false,
            onRationaleDismissed = { 
                showLocationPermissionHandler = false 
                viewModel.onIntent(ManualAddressIntent.OnCurrentLocationFailed)
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.colors.backGround)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            ScreenTopBar(
                title = stringResource(id = R.string.manual_address_title),
                onLeadingClick = { viewModel.onIntent(ManualAddressIntent.OnBackClicked) }
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = Theme.spacing.medium, vertical = Theme.spacing.medium),
                verticalArrangement = Arrangement.spacedBy(Theme.spacing.medium)
            ) {
                // ── Use current location card ──
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(2.dp, Theme.shapes.medium)
                        .clip(Theme.shapes.medium)
                        .background(Theme.colors.surfaceVariant)
                        .clickable { viewModel.onIntent(ManualAddressIntent.OnUseCurrentLocationClicked) }
                        .padding(Theme.spacing.medium),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Theme.spacing.small),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Theme.colors.primaryVariant.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_location),
                                contentDescription = null,
                                tint = Theme.colors.primaryVariant,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            BasicText(
                                text = stringResource(id = R.string.manual_address_use_current_location_title),
                                style = Theme.typography.body.large.copy(
                                    color = Theme.colors.primaryFont,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                            BasicText(
                                text = stringResource(id = R.string.manual_address_use_current_location_subtitle),
                                style = Theme.typography.body.small.copy(
                                    color = Theme.colors.secondaryFont
                                )
                            )
                        }
                    }

                    if (state.isLoadingCurrentLocation) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Theme.colors.primaryVariant,
                            strokeWidth = 2.dp
                        )
                    }
                }

                // ── Country ──
                CustomTextField(
                    text = state.country,
                    onTextChange = { viewModel.onIntent(ManualAddressIntent.OnCountryChanged(it)) },
                    title = stringResource(id = R.string.manual_address_country_label),
                    singleLine = true
                )

                // ── City & Area Row ──
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Theme.spacing.small)
                ) {
                    CustomTextField(
                        text = state.city,
                        onTextChange = { viewModel.onIntent(ManualAddressIntent.OnCityChanged(it)) },
                        title = stringResource(id = R.string.manual_address_city_label),
                        hint = stringResource(id = R.string.manual_address_city_hint),
                        isError = state.cityHasError,
                        errorMessage = if (state.cityHasError) stringResource(id = R.string.manual_address_error_city_required) else null,
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    CustomTextField(
                        text = state.area,
                        onTextChange = { viewModel.onIntent(ManualAddressIntent.OnAreaChanged(it)) },
                        title = stringResource(id = R.string.manual_address_area_label),
                        hint = stringResource(id = R.string.manual_address_area_hint),
                        isError = state.areaHasError,
                        errorMessage = if (state.areaHasError) stringResource(id = R.string.manual_address_error_area_required) else null,
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                // ── Street Name ──
                CustomTextField(
                    text = state.street,
                    onTextChange = { viewModel.onIntent(ManualAddressIntent.OnStreetChanged(it)) },
                    title = stringResource(id = R.string.manual_address_street_label),
                    hint = stringResource(id = R.string.manual_address_street_hint),
                    isError = state.streetHasError,
                    errorMessage = if (state.streetHasError) stringResource(id = R.string.manual_address_error_street_required) else null,
                    singleLine = false,
                    maxLines = 2,
                    fieldHeight = 60.dp
                )

                // ── Building & Apartment Row ──
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Theme.spacing.small)
                ) {
                    CustomTextField(
                        text = state.building,
                        onTextChange = { viewModel.onIntent(ManualAddressIntent.OnBuildingChanged(it)) },
                        title = stringResource(id = R.string.manual_address_building_label),
                        hint = stringResource(id = R.string.manual_address_building_hint),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    CustomTextField(
                        text = state.apartment,
                        onTextChange = { viewModel.onIntent(ManualAddressIntent.OnApartmentChanged(it)) },
                        title = stringResource(id = R.string.manual_address_apartment_label),
                        hint = stringResource(id = R.string.manual_address_apartment_hint),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                // ── Landmark / Notes ──
                CustomTextField(
                    text = state.landmark,
                    onTextChange = { viewModel.onIntent(ManualAddressIntent.OnLandmarkChanged(it)) },
                    title = stringResource(id = R.string.manual_address_landmark_label),
                    hint = stringResource(id = R.string.manual_address_landmark_hint),
                    singleLine = true
                )

                // ── Geocoding Error Alert ──
                if (state.geocodingError) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Theme.colors.error.copy(alpha = 0.1f))
                            .padding(Theme.spacing.medium)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(Theme.spacing.small)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_location),
                                contentDescription = null,
                                tint = Theme.colors.error,
                                modifier = Modifier.size(24.dp)
                            )
                            BasicText(
                                text = stringResource(id = R.string.manual_address_error_geocoding_failed),
                                style = Theme.typography.body.medium.copy(
                                    color = Theme.colors.error
                                )
                            )
                        }
                    }
                }

                // ── Map Location Preview Card ──
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(Theme.shapes.medium)
                        .border(1.dp, Theme.colors.surfaceVariant, Theme.shapes.medium)
                        .background(Theme.colors.surfaceVariant)
                        .noRippleClickable { viewModel.onIntent(ManualAddressIntent.OnMapPreviewClicked) }
                ) {
                    val lat = state.latitude
                    val lon = state.longitude

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .background(Theme.colors.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        if (lat != null && lon != null) {
                            val mapSnapshotUrl = remember(lat, lon) { buildMapSnapshotUrl(lat, lon) }
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(mapSnapshotUrl)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = stringResource(id = R.string.manual_address_map_preview_title),
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(Theme.spacing.small)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_location),
                                    contentDescription = null,
                                    tint = Theme.colors.primaryVariant,
                                    modifier = Modifier.size(32.dp)
                                )
                                BasicText(
                                    text = stringResource(id = R.string.manual_address_map_preview_hint),
                                    style = Theme.typography.body.medium.copy(
                                        color = Theme.colors.secondaryFont
                                    )
                                )
                            }
                        }

                        // Overlay badge
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(Theme.spacing.medium)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Theme.colors.primaryVariant)
                                .padding(horizontal = Theme.spacing.small, vertical = 6.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_location),
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                BasicText(
                                    text = stringResource(id = R.string.manual_address_map_preview_title),
                                    style = Theme.typography.body.small.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(Theme.spacing.small))

                // ── Save Address Button ──
                PrimaryButton(
                    caption = stringResource(id = R.string.manual_address_save_btn),
                    onClick = { viewModel.onIntent(ManualAddressIntent.OnSaveClicked) },
                    isLoading = state.isGeocoding,
                    isDisabled = state.isGeocoding,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Preview
@Composable
private fun ManualAddressScreenPreview() {
    SpTheme {
        ManualAddressScreen(
            onAddressConfirmed = {},
            onNavigateToMap = { _, _ -> },
            onBack = {}
        )
    }
}
