package com.carenest.presentation.ui.onBoarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.carenest.designsystem.components.button.PrimaryButton
import com.carenest.designsystem.components.swipingcards.SwipeDirection
import com.carenest.designsystem.components.swipingcards.SwipingCardStack
import com.carenest.designsystem.components.swipingcards.rememberSwipingCardStackState
import com.carenest.designsystem.theme.SpTheme
import com.carenest.designsystem.theme.Theme
import com.carenest.designsystem.util.noRippleClickable
import com.carenest.presentation.core.mvi.ObserveEffect
import com.carenest.presentation.ui.onBoarding.components.OnBoardingCard
import com.carenest.presentation.ui.onBoarding.components.OnBoardingPageIndicator
import com.carenest.presentation.R
import com.carenest.presentation.navigation.HideTopBar
import com.carenest.presentation.util.NotificationPermissionHandler

@Composable
fun OnBoardingScreen(
    onNavigateToHome: () -> Unit,
    viewModel: OnBoardingViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showPermissionHandler by remember { mutableStateOf(false) }

    ObserveEffect(viewModel.effect) { effect ->
        when (effect) {
            OnBoardingEffect.NavigateToHome -> {
                showPermissionHandler = true
            }
        }
    }

    if (showPermissionHandler) {
        NotificationPermissionHandler(
            onPermissionGranted = {
                showPermissionHandler = false
                onNavigateToHome()
            },
            onPermissionDenied = {
                // Permission is mandatory; do not proceed to home until granted
            }
        )
    }

    OnBoardingContent(
        state = state,
        onIntent = viewModel::onIntent,
    )
}

@Composable
fun OnBoardingContent(
    state: OnBoardingState,
    onIntent: (OnBoardingIntent) -> Unit,
) {
    val cardStackState = rememberSwipingCardStackState()

    HideTopBar()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.colors.backGround)
            .systemBarsPadding(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            OnBoardingTopBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 24.dp),
                onSkip = { if (!cardStackState.isAnimating) onIntent(OnBoardingIntent.OnSkipClicked) },
            )

            SwipingCardStack(
                cards = state.pages,
                key = { it.id },
                state = cardStackState,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.85f),
                maxVisibleCards = 4,
                onSwipe = { result ->
                    val newFrontId = result.resultingOrder.firstOrNull()?.id
                    val newIndex = state.pages.indexOfFirst { it.id == newFrontId }
                    if (newIndex >= 0) {
                        onIntent(OnBoardingIntent.OnCardSwiped(newIndex))
                    }
                },
            ) { page ->
                OnBoardingCard(
                    page = page,
                    modifier = Modifier.fillMaxSize(),
                )
            }

            Spacer(Modifier.height(32.dp))

            val currentPage = state.pages.getOrNull(state.currentPageIndex)
            currentPage?.let { page ->
                AnimatedContent(
                    targetState = page,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "pageText",
                ) { targetPage ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        BasicText(
                            text = targetPage.title,
                            style = Theme.typography.displayMedium.copy(
                                color = Theme.colors.primaryFont,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                            ),
                            modifier = Modifier.fillMaxWidth(),
                        )
                        BasicText(
                            text = targetPage.description,
                            style = Theme.typography.body.medium.copy(
                                color = Theme.colors.secondaryFont,
                                textAlign = TextAlign.Center,
                            ),
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            OnBoardingPageIndicator(
                pageCount = state.pages.size,
                currentPage = state.currentPageIndex,
            )

            Spacer(Modifier.weight(1f))

            PrimaryButton(
                caption = stringResource(R.string.onboarding_get_started),
                onClick = { onIntent(OnBoardingIntent.OnSkipClicked) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
            )
        }
    }
}

@Composable
private fun OnBoardingTopBar(
    onSkip: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        BasicText(
            text = stringResource(R.string.onboarding_title),
            style = Theme.typography.title.copy(
                color = Theme.colors.primary,
                fontWeight = FontWeight.Bold,
            ),
            modifier = Modifier.align(Alignment.CenterStart),
        )
        BasicText(
            text = stringResource(R.string.onboarding_skip),
            style = Theme.typography.body.medium.copy(
                color = Theme.colors.secondaryFont,
                fontWeight = FontWeight.Medium,
            ),
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .noRippleClickable(onClick = onSkip),
        )
    }
}


@Preview(showBackground = true, widthDp = 390, heightDp = 844, name = "OnBoarding - First page")
@Composable
private fun OnBoardingFirstPagePreview() {
    SpTheme(isDarkTheme = false, languageCode = "en") {
        OnBoardingContent(
            state = OnBoardingState(),
            onIntent = {},
        )
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844, name = "OnBoarding - Last page (Get Started)")
@Composable
private fun OnBoardingLastPagePreview() {
    SpTheme(isDarkTheme = false, languageCode = "en") {
        OnBoardingContent(
            state = OnBoardingState(currentPageIndex = 3),
            onIntent = {},
        )
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844, name = "OnBoarding - Dark")
@Composable
private fun OnBoardingDarkPreview() {
    SpTheme(isDarkTheme = true, languageCode = "en") {
        OnBoardingContent(
            state = OnBoardingState(currentPageIndex = 1),
            onIntent = {},
        )
    }
}
