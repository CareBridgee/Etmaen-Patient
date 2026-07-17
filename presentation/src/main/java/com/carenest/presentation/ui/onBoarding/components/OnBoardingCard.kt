package com.carenest.presentation.ui.onBoarding.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.carenest.designsystem.theme.Theme
import com.carenest.presentation.ui.onBoarding.OnBoardingPage

@Composable
fun OnBoardingCard(
    page: OnBoardingPage,
    modifier: Modifier = Modifier,
) {
    val cardShape = Theme.shapes.large

    Box(
        modifier = modifier
            .shadow(elevation = 2.dp, shape = cardShape, clip = false)
            .clip(cardShape)
            .background(Theme.colors.primaryContainer),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(id = page.illustrationRes),
            contentDescription = page.title,
            tint = Theme.colors.primary,
            modifier = Modifier
                .size(96.dp)
                .padding(8.dp),
        )
    }
}
