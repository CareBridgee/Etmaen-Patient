package com.carenest.presentation.ui.history

import com.carenest.domain.model.ServiceHistory
import com.carenest.presentation.core.util.UiText

data class HistoryState(
    val historyItems: List<ServiceHistory> = emptyList(),
    val isLoading: Boolean = false,
    val error: UiText? = null
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
