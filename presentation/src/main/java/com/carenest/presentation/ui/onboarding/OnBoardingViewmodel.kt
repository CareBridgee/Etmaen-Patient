package com.carenest.presentation.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carenest.domain.usecase.settings.UpdateOnboardingStatusUseCase
import com.carenest.presentation.core.mvi.DefaultEffectPublisher
import com.carenest.presentation.core.mvi.DefaultStateHolder
import com.carenest.presentation.core.mvi.EffectPublisher
import com.carenest.presentation.core.mvi.StateHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class OnBoardingViewModel @Inject constructor(
    private val updateOnboardingStatusUseCase: UpdateOnboardingStatusUseCase
) :
    ViewModel(),
    StateHolder<OnBoardingState> by DefaultStateHolder(OnBoardingState()),
    EffectPublisher<OnBoardingEffect> by DefaultEffectPublisher() {

    fun onIntent(intent: OnBoardingIntent) {
        when (intent) {
            is OnBoardingIntent.OnCardSwiped -> handleCardSwiped(intent.newIndex)
            is OnBoardingIntent.OnSkipClicked -> handleSkipClicked()
        }
    }


    private fun handleCardSwiped(newIndex: Int) {
        updateState {
            val clamped = newIndex.coerceIn(0, pages.lastIndex)
            copy(
                currentPageIndex = clamped,
            )
        }
    }

    private fun handleSkipClicked() {
        completeOnBoarding()
    }

    private fun completeOnBoarding() {
        viewModelScope.launch {
            updateOnboardingStatusUseCase(true)
        }
        sendEffect(OnBoardingEffect.NavigateToHome)
    }
}
