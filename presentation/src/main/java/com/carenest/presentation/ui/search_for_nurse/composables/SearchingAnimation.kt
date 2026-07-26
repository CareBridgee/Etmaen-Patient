package com.carenest.presentation.ui.search_for_nurse.composables

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MedicalServices
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.carenest.designsystem.theme.SpTheme
import com.carenest.designsystem.theme.Theme
import com.carenest.designsystem.R as RD


@Composable
fun SearchingAnimation() {

    val colors = Theme.colors

    val infinite = rememberInfiniteTransition(label = "")

    val scale by infinite.animateFloat(
        initialValue = 1f, targetValue = 1.18f, animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1200, easing = FastOutSlowInEasing
            ), repeatMode = RepeatMode.Reverse
        ), label = ""
    )

    val alpha by infinite.animateFloat(
        initialValue = .35f, targetValue = 0f, animationSpec = infiniteRepeatable(
            animation = tween(1200), repeatMode = RepeatMode.Reverse
        ), label = ""
    )

    Box(
        modifier = Modifier.size(150.dp), contentAlignment = Alignment.Center
    ) {

        Ripple(delay = 0)

        Ripple(delay = 600)

        Ripple(delay = 1200)

        Box(
            modifier = Modifier
                .size(120.dp * scale)
                .alpha(alpha)
                .background(
                    colors.primary, CircleShape
                )
        )

        Box(
            modifier = Modifier
                .size(96.dp)
                .shadow(
                    8.dp, CircleShape
                )
                .background(
                    colors.primary, CircleShape
                )
        )

        Icon(
            painter = painterResource(RD.drawable.ic_search),
            contentDescription = null,
            modifier = Modifier
                .size(35.dp)
                .clip(CircleShape),
            tint = colors.onPrimary

        )

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(42.dp)
                .background(color = colors.tint, shape = RoundedCornerShape(50.dp))
        ) {
            Icon(
                Icons.Outlined.MedicalServices,
                null,
                tint = Theme.colors.onPrimary,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

@Composable
private fun Ripple(
    delay: Int
) {

    val infinite = rememberInfiniteTransition(label = "")

    val scale by infinite.animateFloat(
        initialValue = 1f,
        targetValue = 2.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1800,
                delayMillis = delay,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = ""
    )

    val alpha by infinite.animateFloat(
        initialValue = .35f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1800,
                delayMillis = delay
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = ""
    )

    Box(
        Modifier
            .size((90 * scale).dp)
            .alpha(alpha)
            .background(
                Theme.colors.primary,
                CircleShape
            )
    )
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    SpTheme {
        SearchingAnimation()
    }
}