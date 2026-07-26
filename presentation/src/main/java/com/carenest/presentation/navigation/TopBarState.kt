package com.carenest.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.painter.Painter

import androidx.compose.ui.platform.LocalInspectionMode

data class TopBarConfiguration(
    val title: String? = null,
    val showLeadingIcon: Boolean = false,
    val onLeadingClick: (() -> Unit)? = null,
    val leadingIcon: Painter? = null
)

val LocalTopBarState = compositionLocalOf { mutableStateOf(TopBarConfiguration()) }

@Composable
fun ScreenTopBar(
    title: String,
    showLeadingIcon: Boolean = true,
    leadingIcon: Painter? = null,
    onLeadingClick: (() -> Unit)? = null
) {
    if (LocalInspectionMode.current) return
    val topBarState = LocalTopBarState.current

    DisposableEffect(title, showLeadingIcon, onLeadingClick, leadingIcon) {
        topBarState.value = TopBarConfiguration(
            title = title,
            showLeadingIcon = showLeadingIcon,
            onLeadingClick = onLeadingClick,
            leadingIcon = leadingIcon
        )
        onDispose { /* State persists until the next screen overrides it */ }
    }
}

/**
 * Call this in screens that should NOT show the top bar (e.g. Splash, OnBoarding, AuthLanding).
 * This explicitly resets the top bar state so no stale bar is visible.
 */
@Composable
fun HideTopBar() {
    if (LocalInspectionMode.current) return
    val topBarState = LocalTopBarState.current
    DisposableEffect(Unit) {
        topBarState.value = TopBarConfiguration()
        onDispose { }
    }
}
