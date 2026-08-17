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
    val tint: Color,
    val successContainer: Color,
    val onSuccessContainer: Color,
    val warningContainer: Color,
    val onWarningContainer: Color,
    val primaryContainer: Color,
    val onPrimaryContainer: Color,
    val infoContainer: Color,
    val onInfoContainer: Color,
    val bankContainer: Color,
    val onBankContainer: Color,
    val vodafoneContainer: Color,
    val onVodafoneContainer: Color,
    val processingContainer: Color,
    val onProcessingContainer: Color,
    val errorContainer: Color,
    val onErrorContainer: Color,
    val track: Color,
    val divider: Color,
    val cardBackground: Color
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
    tint = Color(0xFF006970),
    successContainer = Color(0xFFE8F5E9),
    onSuccessContainer = Color(0xFF2E7D32),
    warningContainer = Color(0xFFFFF3E0),
    onWarningContainer = Color(0xFFEF6C00),
    primaryContainer = Color(0xFFD3E8E6),
    onPrimaryContainer = Color(0xFF004D40),
    infoContainer = Color(0xFFF5F5F5),
    onInfoContainer = Color(0xFF636369),
    bankContainer = Color(0xFFE0F2F1),
    onBankContainer = Color(0xFF004D40),
    vodafoneContainer = Color(0xFFFFF1F0),
    onVodafoneContainer = Color(0xFFD32F2F),
    processingContainer = Color(0xFFE3F2FD),
    onProcessingContainer = Color(0xFF1976D2),
    errorContainer = Color(0xFFFFEBEE),
    onErrorContainer = Color(0xFFD32F2F),
    track = Color(0xFFEBEBEB),
    divider = Color(0xFFF0F0F0),
    cardBackground = Color(0xFFF4F6F6)
)

val darkColors = ColorScheme(
    primary = Color(0xFF00796B),           // Richer, darker primary teal
    primaryVariant = Color(0xFF004D40),    // Deep primary variant
    onPrimary = Color(0xFFFFFFFF),         // White text on primary buttons
    onPrimaryVariant = Color(0xFFB2DFDB),  // Light teal text on variant
    secondary = Color(0xFF8B9BB4),         // Cool navy-slate secondary
    onSecondary = Color(0xFF0F172A),       // Deep navy contrast
    backGround = Color(0xFF0D1627),        // Deep rich navy background
    primaryFont = Color(0xFFF1F5F9),       // Crisp off-white text
    secondaryFont = Color(0xFF94A3B8),     // Readable slate-navy body font
    surface = Color(0xFF172033),           // Elevated dark navy surface card
    surfaceVariant = Color(0xFF223047),    // Secondary navy surface elevation
    onSurface = Color(0xFFF1F5F9),         // On surface text
    hint = Color(0xFF64748B),              // Outline hint color
    warning = Color(0xFFFFB74D),           // Warm amber warning
    onWarning = Color(0xFF0F172A),
    error = Color(0xFFEF5350),             // Soft red error
    onError = Color(0xFFFFFFFF),
    success = Color(0xFF66BB6A),           // Green success
    onSuccess = Color(0xFF0F172A),
    amber = Color(0xFFF59E0B),
    onAmber = Color(0xFFFFFFFF),
    disable = Color(0xFF1E293B),           // Disabled container
    onDisable = Color(0xFF475569),         // Disabled text
    tint = Color(0xFF00796B),
    successContainer = Color(0xFF142E21),     // Navy-green container
    onSuccessContainer = Color(0xFFA5D6A7),
    warningContainer = Color(0xFF382710),     // Navy-amber container
    onWarningContainer = Color(0xFFFFE082),
    primaryContainer = Color(0xFF102E33),     // Deep navy-teal container
    onPrimaryContainer = Color(0xFF80CBC4),
    infoContainer = Color(0xFF192438),        // Navy info container
    onInfoContainer = Color(0xFF94A3B8),
    bankContainer = Color(0xFF0F3330),
    onBankContainer = Color(0xFF80CBC4),
    vodafoneContainer = Color(0xFF331212),
    onVodafoneContainer = Color(0xFFEF9A9A),
    processingContainer = Color(0xFF0F2642),
    onProcessingContainer = Color(0xFF90CAF9),
    errorContainer = Color(0xFF3B1515),
    onErrorContainer = Color(0xFFEF9A9A),
    track = Color(0xFF1E293B),                // Switch track in navy
    divider = Color(0xFF223047),              // Divider line in navy
    cardBackground = Color(0xFF172033)        // Elevated card background
)

internal val localSPColorScheme = staticCompositionLocalOf { darkColors }