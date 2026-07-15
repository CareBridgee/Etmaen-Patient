package com.carenest.designsystem.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class ColorScheme(
    val primary: Color,
    val primaryVariant: Color,
    val onPrimary: Color,
    val onPrimaryVariant: Color,
    val secondary: Color,
    val onSecondary: Color,
    val backGround: Color,
    val primaryFont: Color,
    val secondaryFont: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val onSurface: Color,
    val hint: Color,
    val warning: Color,
    val onWarning: Color,
    val error: Color,
    val onError: Color,
    val success: Color,
    val onSuccess: Color,
    val amber: Color,
    val onAmber: Color,
    val disable: Color,
    val onDisable: Color,
    val tint: Color
)

val lightColors = ColorScheme(
    primary = Color(0xFF006168),           // primary
    primaryVariant = Color(0xFF0D7C84),    // primary-container
    onPrimary = Color(0xFFFFFFFF),         // on-primary
    onPrimaryVariant = Color(0xFFD9FCFF),  // on-primary-container
    secondary = Color(0xFF516161),         // secondary
    onSecondary = Color(0xFFFFFFFF),       // on-secondary
    backGround = Color(0xFFF8F9FA),        // background
    primaryFont = Color(0xFF191C1D),       // on-surface
    secondaryFont = Color(0xFF3E494A),     // on-surface-variant
    surface = Color(0xFFFFFFFF),           // surface-container-lowest
    surfaceVariant = Color(0xFFE1E3E4),    // surface-variant
    onSurface = Color(0xFF191C1D),         // on-surface
    hint = Color(0xFF6E797A),              // outline
    warning = Color(0xFFFFC107),
    onWarning = Color(0xFF000000),
    error = Color(0xFFBA1A1A),             // error
    onError = Color(0xFFFFFFFF),           // on-error
    success = Color(0xFF43A047),           // progress-indicator success
    onSuccess = Color(0xFFFFFFFF),
    amber = Color(0xFFD97706),
    onAmber = Color(0xFFFFFFFF),
    disable = Color(0xFFEDEEEF),           // surface-container
    onDisable = Color(0xFFBDC9CA),         // outline-variant
    tint = Color(0xFF006970),              // surface-tint
)

val darkColors = ColorScheme(
    primary = Color(0xFF7CD4DD),           // inverse-primary
    primaryVariant = Color(0xFF71D7CD),    // tertiary-fixed-dim
    onPrimary = Color(0xFF002022),         // on-primary-fixed
    onPrimaryVariant = Color(0xFF004F54),  // on-primary-fixed-variant
    secondary = Color(0xFFB8CAC9),         // secondary-fixed-dim
    onSecondary = Color(0xFF0E1E1E),       // on-secondary-fixed
    backGround = Color(0xFF191C1D),
    primaryFont = Color(0xFFE1E3E4),
    secondaryFont = Color(0xFFBDC9CA),
    surface = Color(0xFF2E3132),           // inverse-surface
    surfaceVariant = Color(0xFF3E494A),
    onSurface = Color(0xFFF0F1F2),         // inverse-on-surface
    hint = Color(0xFF6E797A),
    warning = Color(0xFFFFD54F),
    onWarning = Color(0xFF121212),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    success = Color(0xFF81C784),
    onSuccess = Color(0xFF121212),
    amber = Color(0xFFD97706),
    onAmber = Color(0xFFFFFFFF),
    disable = Color(0xFF3E494A),
    onDisable = Color(0xFF6E797A),
    tint = Color(0xFF7CD4DD),
)

internal val localSPColorScheme = staticCompositionLocalOf { darkColors }