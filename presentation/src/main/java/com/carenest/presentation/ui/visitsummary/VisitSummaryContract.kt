package com.carenest.presentation.ui.visitsummary

import com.carenest.domain.model.VisitSummary


data class VisitCompletedState(
    val isLoading: Boolean = true,
    val summary: VisitSummary? = null,
    val showRatingDialog: Boolean = false,
    val selectedRating: Int = 0,
    val reviewText: String = "",
    val isAnonymous: Boolean = false,
    val isSubmittingRating: Boolean = false,
    val errorMessage: String? = null,
)

sealed interface VisitCompletedIntent {
    data class LoadVisitSummary(val requestId: String) : VisitCompletedIntent
    data class OnStarSelected(val rating: Int) : VisitCompletedIntent
    data class OnReviewTextChanged(val text: String) : VisitCompletedIntent
    data class OnAnonymousChanged(val isAnonymous: Boolean) : VisitCompletedIntent
    data object OnSubmitRatingClicked : VisitCompletedIntent
    data object OnDismissRatingDialogClicked : VisitCompletedIntent
    data object OnBackToHomeClicked : VisitCompletedIntent
    data object OnErrorDismissed : VisitCompletedIntent
}

sealed interface VisitCompletedEffect {
    data object NavigateHome : VisitCompletedEffect
    data object RatingSubmitted : VisitCompletedEffect
    data class ShowError(val message: String) : VisitCompletedEffect
}