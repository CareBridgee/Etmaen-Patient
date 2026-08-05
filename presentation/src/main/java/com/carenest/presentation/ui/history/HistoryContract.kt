package com.carenest.presentation.ui.history

import com.carenest.domain.model.home.Booking

data class HistoryState(
    val historyItems: List<Booking> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed interface HistoryIntent {
    data object LoadHistory : HistoryIntent
    data class HistoryItemClicked(val historyId: String) : HistoryIntent
    data object RetryClicked : HistoryIntent
    data object BackClicked : HistoryIntent
    data object ExploreServicesClicked : HistoryIntent
}

sealed interface HistoryEffect {
    data class NavigateToHistoryDetails(val historyId: String) : HistoryEffect
    data object NavigateBack : HistoryEffect
    data object NavigateToServices : HistoryEffect
}
