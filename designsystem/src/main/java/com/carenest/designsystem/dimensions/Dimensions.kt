package com.carenest.designsystem.dimensions

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class SPSpacing(
    val extraSmall: Dp = 4.dp,
    val space6: Dp = 6.dp,
    val small: Dp = 8.dp,
    val space9: Dp = 9.dp,
    val space10: Dp = 10.dp,
    val space12: Dp = 12.dp,
    val space14: Dp = 14.dp,
    val medium: Dp = 16.dp,
    val space18: Dp = 18.dp,
    val space20: Dp = 20.dp,
    val space22: Dp = 22.dp,
    val large: Dp = 24.dp,
    val space28: Dp = 28.dp,
    val extraLarge: Dp = 32.dp,
    val space36: Dp = 36.dp
)

data class SPShapes(
    val small: Shape = RoundedCornerShape(4.dp),
    val medium: Shape = RoundedCornerShape(8.dp),
    val large: Shape = RoundedCornerShape(16.dp)
)

data class SPSize(
    val small: Dp = 24.dp,
    val iconSmall: Dp = 16.dp,
    val iconMedium: Dp = 24.dp,
    val componentsNormalHeight: Dp = 48.dp,
    val medium: Dp = 48.dp,
    val large: Dp = 64.dp ,
    val logo: Dp = 220.dp,
    val onboardingImage: Dp = 280.dp,
    val onboardingBottomSpace: Dp = 120.dp
)

val LocalSPSpacing = staticCompositionLocalOf { SPSpacing() }
val LocalSPShapes = staticCompositionLocalOf { SPShapes() }
val LocalSPSize = staticCompositionLocalOf { SPSize() }
