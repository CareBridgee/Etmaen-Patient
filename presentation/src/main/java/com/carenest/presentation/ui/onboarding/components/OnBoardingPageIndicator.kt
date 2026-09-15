package com.carenest.presentation.ui.onboarding.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.carenest.designsystem.theme.Theme


@Composable
fun OnBoardingPageIndicator(
    pageCount: Int,
    currentPage: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(pageCount) { index ->
            val isActive = index == currentPage
            IndicatorDot(isActive = isActive)
        }
    }
}

@Composable
private fun IndicatorDot(isActive: Boolean) {
    val width by animateDpAsState(
        targetValue = if (isActive) 24.dp else 8.dp,
        animationSpec = spring(dampingRatio = 0.7f, stiffness = 300f),
        label = "dotWidth",
    )
    val color by animateColorAsState(
        targetValue = if (isActive) Theme.colors.primary else Theme.colors.onDisable,
        label = "dotColor",
    )

    Box(
        modifier = Modifier
            .width(width)
            .height(8.dp)
            .clip(CircleShape)
            .background(color),
    )
}
