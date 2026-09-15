package com.carenest.presentation.ui.historydetails

import com.carenest.domain.model.ServiceHistory
import com.carenest.presentation.core.util.UiText

data class ServiceHistoryDetailsState(
    val serviceHistory: ServiceHistory? = null,
    val isLoading: Boolean = true,
    val error: UiText? = null
)

sealed interface ServiceHistoryDetailsIntent {
    data class LoadDetails(val requestId: String) : ServiceHistoryDetailsIntent
    data object BackClicked : ServiceHistoryDetailsIntent
}

sealed interface ServiceHistoryDetailsEffect {
    data object NavigateBack : ServiceHistoryDetailsEffect
}
