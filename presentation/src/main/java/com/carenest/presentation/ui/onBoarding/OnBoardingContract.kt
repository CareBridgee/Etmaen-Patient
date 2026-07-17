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
    val isLastPage: Boolean = false,
)

private fun defaultPages() = listOf(
    OnBoardingPage(
        id = 1,
        illustrationRes = R.drawable.ic_syringe,
        title = "Book a Nurse at Home",
        description = "Access premium medical care in the comfort and safety of your own home.",
    ),
    OnBoardingPage(
        id = 2,
        illustrationRes = R.drawable.ic_heart_beat,
        title = "Consult a Doctor Online",
        description = "Connect with certified doctors anytime, anywhere in just a few taps.",
    ),
    OnBoardingPage(
        id = 3,
        illustrationRes = R.drawable.ic_pill,
        title = "Get Medicine Delivered",
        description = "Order your prescriptions and receive them at your doorstep swiftly.",
    ),
)

sealed interface OnBoardingIntent {
    data class OnCardSwiped(val newIndex: Int) : OnBoardingIntent
    data object OnNextClicked : OnBoardingIntent
    data object OnSkipClicked : OnBoardingIntent
}

sealed interface OnBoardingEffect {
    data object NavigateToHome : OnBoardingEffect
}
