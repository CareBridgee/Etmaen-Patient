package com.carenest.presentation.ui.history_details

import com.carenest.domain.model.history.ServiceHistory

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
