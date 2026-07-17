package com.carenest.presentation.ui.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.carenest.designsystem.theme.SpTheme
import com.carenest.designsystem.theme.Theme
import com.carenest.presentation.core.mvi.ObserveEffect
import com.carenest.designsystem.R as RD

@Composable
fun SplashScreen(
    onNavigateToOnBoarding: () -> Unit,
    viewModel: SplashViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveEffect(viewModel.effect) { effect ->
        when (effect) {
            SplashEffect.NavigateToOnBoarding -> onNavigateToOnBoarding()
        }
    }

    SplashContent(
        state = state,
    )
}

@Composable
private fun SplashContent(
    state: SplashState,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.colors.primary)
            .systemBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(Theme.shapes.large)
                    .background(Theme.colors.surface),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = RD.drawable.ic_work),
                    contentDescription = "App Icon",
                    tint = Theme.colors.primary,
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            BasicText(
                text = "CareNest",
                style = Theme.typography.displayMedium.copy(
                    color = Theme.colors.onPrimary,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            BasicText(
                text = "Personalized home care with a\nhuman touch.",
                style = Theme.typography.body.medium.copy(
                    color = Theme.colors.onPrimary.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center,
                )
            )

            Spacer(modifier = Modifier.height(48.dp))

            CircularProgressIndicator(
                color = Theme.colors.onPrimary,
                trackColor = Theme.colors.onPrimary.copy(alpha = 0.2f),
                modifier = Modifier.size(40.dp),
                strokeWidth = 3.dp
            )

            Spacer(modifier = Modifier.height(16.dp))

            BasicText(
                text = "INITIALIZING CARE",
                style = Theme.typography.hint.medium.copy(
                    color = Theme.colors.onPrimary.copy(alpha = 0.8f),
                    fontWeight = FontWeight.Medium,
                    letterSpacing = androidx.compose.ui.unit.TextUnit(0.1f, TextUnitType.Em)
                )
            )
        }

        BasicText(
            text = "Trusted by thousands of families worldwide",
            style = Theme.typography.hint.small.copy(
                color = Theme.colors.onPrimary.copy(alpha = 0.7f),
            ),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
        )
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun SplashContentPreview() {
    SpTheme(isDarkTheme = false, languageCode = "en") {
        SplashContent(
            state = SplashState(),
        )
    }
}
