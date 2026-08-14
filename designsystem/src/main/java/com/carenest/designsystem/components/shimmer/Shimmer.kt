package com.carenest.designsystem.components.shimmer

import com.carenest.designsystem.theme.Theme


import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp

/**
 * Animated shimmer/skeleton background. Apply to a sized, clipped placeholder
 * Box to indicate that real content is loading.
 *
 * Usage:
 * ```
 * Box(Modifier.size(120.dp).clip(Theme.shapes.medium).shimmerEffect())
 * ```
 */
@Composable
fun Modifier.shimmerEffect(): Modifier = composed {
    val baseColor = Theme.colors.surfaceVariant
    val highlightColor = Theme.colors.hint.copy(alpha = 0.25f)

    var size by remember { mutableStateOf(IntSize.Zero) }
    val transition = rememberInfiniteTransition(label = "shimmer")
    val startOffsetX by transition.animateFloat(
        initialValue = -2 * size.width.toFloat(),
        targetValue = 2 * size.width.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200),
            repeatMode = RepeatMode.Restart,
        ),
        label = "shimmerOffset",
    )

    this
        .background(
            brush = Brush.linearGradient(
                colors = listOf(baseColor, highlightColor, baseColor),
                start = Offset(startOffsetX, 0f),
                end = Offset(startOffsetX + size.width.toFloat(), size.height.toFloat()),
            ),
        )
        .onGloballyPositioned { size = it.size }
}

/**
 * Shared skeleton primitive used by loading layouts across the app.
 * Keeping the clipping and shimmer behavior here prevents small visual
 * differences between screens while still letting each screen mirror its
 * real content dimensions.
 */
@Composable
fun ShimmerPlaceholder(
    modifier: Modifier,
    shape: Shape = RoundedCornerShape(12.dp),
) {
    Box(
        modifier = modifier
            .clip(shape)
            .shimmerEffect(),
    )
}
