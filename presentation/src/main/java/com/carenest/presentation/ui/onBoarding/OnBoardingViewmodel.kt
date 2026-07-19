package com.carenest.presentation.ui.onBoarding

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
            is OnBoardingIntent.OnNextClicked -> handleNextClicked()
            is OnBoardingIntent.OnSkipClicked -> handleSkipClicked()
        }
    }


    private fun handleCardSwiped(newIndex: Int) {
        updateState {
            val clamped = newIndex.coerceIn(0, pages.lastIndex)
            copy(
                currentPageIndex = clamped,
                isLastPage = clamped == pages.lastIndex,
            )
        }
    }

    private fun handleNextClicked() {
        val state = currentState
        if (state.isLastPage) {
            completeOnBoarding()
        } else {
            val next = (state.currentPageIndex + 1).coerceAtMost(state.pages.lastIndex)
            updateState {
                copy(
                    currentPageIndex = next,
                    isLastPage = next == pages.lastIndex,
                )
            }
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
