package com.carenest.presentation.ui.auth.register

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.carenest.designsystem.components.shimmer.shimmerEffect
import com.carenest.designsystem.theme.Theme

@Composable
internal fun RegisterLoadingShimmer() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.colors.backGround)
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ShimmerBox(Modifier.fillMaxWidth(0.32f).height(14.dp))
            ShimmerBox(Modifier.fillMaxWidth(0.14f).height(14.dp))
        }
        Spacer(Modifier.height(10.dp))
        ShimmerBox(Modifier.fillMaxWidth().height(8.dp), 8.dp)
        Spacer(Modifier.height(24.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .background(Theme.colors.surface)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ShimmerBox(Modifier.fillMaxWidth(0.48f).height(24.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ShimmerField(Modifier.weight(1f))
                ShimmerField(Modifier.weight(1f))
            }
            ShimmerField(Modifier.fillMaxWidth())
            ShimmerBox(Modifier.fillMaxWidth(0.28f).height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ShimmerBox(Modifier.weight(1f).height(48.dp), 14.dp)
                ShimmerBox(Modifier.weight(1f).height(48.dp), 14.dp)
            }
            ShimmerBox(Modifier.fillMaxWidth().height(52.dp), 14.dp)
        }
    }
}

@Composable
private fun ShimmerField(modifier: Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ShimmerBox(Modifier.fillMaxWidth(0.55f).height(14.dp))
        ShimmerBox(Modifier.fillMaxWidth().height(48.dp), 14.dp)
    }
}

@Composable
private fun ShimmerBox(
    modifier: Modifier,
    cornerRadius: Dp = 10.dp
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .shimmerEffect()
    )
}
