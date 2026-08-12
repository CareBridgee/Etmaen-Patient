package com.carenest.presentation.ui.servicelist.components

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.carenest.designsystem.components.shimmer.shimmerEffect
import com.carenest.designsystem.theme.Theme

@Composable
fun ServicesShimmerLoading(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing.medium)
    ) {
        // Row 1: High (left) / Small (right)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Theme.spacing.space12)
        ) {
            ServiceCardShimmer(height = 198.dp, modifier = Modifier.weight(1f))
            ServiceCardShimmer(height = 116.dp, modifier = Modifier.weight(1f))
        }
        // Row 2: Small (left) / High (right)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Theme.spacing.space12)
        ) {
            ServiceCardShimmer(height = 116.dp, modifier = Modifier.weight(1f))
            ServiceCardShimmer(height = 198.dp, modifier = Modifier.weight(1f))
        }
        // Row 3: High (left) / Small (right)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Theme.spacing.space12)
        ) {
            ServiceCardShimmer(height = 198.dp, modifier = Modifier.weight(1f))
            ServiceCardShimmer(height = 116.dp, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun ServiceCardShimmer(
    height: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(height)
            .clip(Theme.shapes.large)
            .background(Theme.colors.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Theme.spacing.medium),
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(Theme.shapes.medium)
                    .shimmerEffect()
            )

            Spacer(Modifier.height(Theme.spacing.space12))

            Box(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(16.dp)
                    .clip(CircleShape)
                    .shimmerEffect()
            )

            Spacer(Modifier.height(4.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth(0.4f)
                    .height(12.dp)
                    .clip(CircleShape)
                    .shimmerEffect()
            )
        }
    }
}
