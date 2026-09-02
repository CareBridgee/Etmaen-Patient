package com.carenest.presentation.ui.historydetails

import com.carenest.domain.model.ServiceHistory

data class ServiceHistoryDetailsState(
    val serviceHistory: ServiceHistory? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)

sealed interface ServiceHistoryDetailsIntent {
    data class LoadDetails(val requestId: String) : ServiceHistoryDetailsIntent
    data object BackClicked : ServiceHistoryDetailsIntent
}

sealed interface ServiceHistoryDetailsEffect {
    data object NavigateBack : ServiceHistoryDetailsEffect
}
