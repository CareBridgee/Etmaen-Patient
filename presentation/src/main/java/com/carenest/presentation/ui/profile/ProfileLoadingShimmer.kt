package com.carenest.presentation.ui.profile

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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.carenest.designsystem.components.shimmer.shimmerEffect
import com.carenest.designsystem.theme.SpTheme
import com.carenest.designsystem.theme.Theme

@Composable
fun ProfileLoadingShimmer() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.colors.backGround)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(
                start = Theme.spacing.large,
                top = Theme.spacing.space20,
                end = Theme.spacing.large,
                bottom = Theme.spacing.extraLarge
            )
    ) {
        SkeletonLine(
            modifier = Modifier.size(width = 140.dp, height = 22.dp),
            cornerRadius = Theme.spacing.space6
        )

        Spacer(modifier = Modifier.height(Theme.spacing.space28))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SkeletonCircle(size = 120.dp)
            Spacer(modifier = Modifier.height(Theme.spacing.space12))
            SkeletonLine(
                modifier = Modifier.size(width = 156.dp, height = 22.dp),
                cornerRadius = Theme.spacing.space6
            )
            Spacer(modifier = Modifier.height(Theme.spacing.small))
            SkeletonLine(
                modifier = Modifier.size(width = 92.dp, height = 14.dp),
                cornerRadius = Theme.spacing.extraSmall
            )
        }

        Spacer(modifier = Modifier.height(Theme.spacing.extraLarge))

        Column(verticalArrangement = Arrangement.spacedBy(Theme.spacing.space12)) {
            repeat(6) {
                SkeletonCard()
            }
        }

        Spacer(modifier = Modifier.height(Theme.spacing.medium))

        SkeletonLogoutCard()

        Spacer(modifier = Modifier.height(Theme.spacing.extraLarge))

        SkeletonLine(
            modifier = Modifier
                .size(width = 96.dp, height = 12.dp)
                .align(Alignment.CenterHorizontally),
            cornerRadius = Theme.spacing.extraSmall
        )
    }
}

@Composable
private fun SkeletonCard() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        shape = RoundedCornerShape(20.dp),
        color = Theme.colors.surface,
        shadowElevation = 0.dp
    ) {
        Row(
            modifier = Modifier.padding(Theme.spacing.medium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SkeletonLine(
                modifier = Modifier.size(48.dp),
                cornerRadius = Theme.spacing.space14
            )

            Spacer(modifier = Modifier.width(Theme.spacing.medium))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(Theme.spacing.small)
            ) {
                SkeletonLine(
                    modifier = Modifier
                        .fillMaxWidth(0.46f)
                        .height(16.dp),
                    cornerRadius = Theme.spacing.extraSmall
                )
                SkeletonLine(
                    modifier = Modifier
                        .fillMaxWidth(0.72f)
                        .height(12.dp),
                    cornerRadius = Theme.spacing.extraSmall
                )
            }

            Spacer(modifier = Modifier.width(Theme.spacing.space12))

            SkeletonLine(
                modifier = Modifier.size(20.dp),
                cornerRadius = Theme.spacing.extraSmall
            )
        }
    }
}

@Composable
private fun SkeletonLogoutCard() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        shape = RoundedCornerShape(20.dp),
        color = Theme.colors.surface,
        shadowElevation = 0.dp
    ) {
        Row(
            modifier = Modifier.padding(Theme.spacing.medium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SkeletonLine(
                modifier = Modifier.size(48.dp),
                cornerRadius = Theme.spacing.space14
            )
            Spacer(modifier = Modifier.width(Theme.spacing.medium))
            SkeletonLine(
                modifier = Modifier.size(width = 88.dp, height = 16.dp),
                cornerRadius = Theme.spacing.extraSmall
            )
        }
    }
}

@Composable
private fun SkeletonLine(
    modifier: Modifier,
    cornerRadius: Dp
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .shimmerEffect()
    )
}

@Composable
private fun SkeletonCircle(size: Dp) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .shimmerEffect()
    )
}

@Preview(name = "Profile Loading Light", showBackground = true, widthDp = 390, heightDp = 900)
@Composable
private fun ProfileLoadingLightPreview() {
    SpTheme(isDarkTheme = false) {
        ProfileLoadingShimmer()
    }
}

@Preview(name = "Profile Loading Dark", showBackground = true, widthDp = 390, heightDp = 900)
@Composable
private fun ProfileLoadingDarkPreview() {
    SpTheme(isDarkTheme = true) {
        ProfileLoadingShimmer()
    }
}
