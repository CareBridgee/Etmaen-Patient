package com.carenest.presentation.ui.onBoarding

import androidx.annotation.DrawableRes
import com.carenest.designsystem.R

data class OnBoardingPage(
    val id: Int,
    @DrawableRes val illustrationRes: Int,
    val title: String,
    val description: String,
)

data class OnBoardingState(
    val pages: List<OnBoardingPage> = defaultPages(),
    val currentPageIndex: Int = 0,
)

private fun defaultPages() = listOf(
    OnBoardingPage(
        id = 1,
        illustrationRes = R.drawable.onboarding1,
        title = "Professional Home Nursing",
        description = "Get medical assistance in the comfort of your home with our certified nurses.",
    ),
    OnBoardingPage(
        id = 2,
        illustrationRes = R.drawable.onboarding3,
        title = "Modern Healthcare",
        description = "Manage your health journey with ease and professional guidance.",
    ),
    OnBoardingPage(
        id = 3,
        illustrationRes = R.drawable.onboarding2,
        title = "Specialized Support",
        description = "Expert care for children and specialized medical needs tailored for you.",
    ),
    OnBoardingPage(
        id = 4,
        illustrationRes = R.drawable.onboarding0,
        title = "Compassionate Care",
        description = "We are here to support you and your loved ones with kindness and empathy.",
    ),
)

sealed interface OnBoardingIntent {
    data class OnCardSwiped(val newIndex: Int) : OnBoardingIntent
    data object OnSkipClicked : OnBoardingIntent
}

sealed interface OnBoardingEffect {
    data object NavigateToHome : OnBoardingEffect
}
