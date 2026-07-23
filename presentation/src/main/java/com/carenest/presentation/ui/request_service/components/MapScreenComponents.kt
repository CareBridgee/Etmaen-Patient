package com.carenest.presentation.ui.request_service.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.carenest.designsystem.theme.Theme
import com.carenest.designsystem.components.button.PrimaryButton
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.rememberMapState

@Composable
fun MapScreen(
    onLocationSelected: (Double, Double) -> Unit,
    onBack: () -> Unit
) {
    val viewportState = rememberMapViewportState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            PrimaryButton(
                caption = "Confirm Location",
                onClick = {
                    onLocationSelected(30.0444, 31.2357)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Theme.spacing.medium)
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            MapboxMap(
                modifier = Modifier.fillMaxSize(),
                mapState = rememberMapState {  }
            )
        }
    }
}
