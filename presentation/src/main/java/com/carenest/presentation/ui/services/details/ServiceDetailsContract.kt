package com.carenest.presentation.ui.services.details

import com.carenest.presentation.model.HealthcareServiceUiModel

data class ServiceDetailsState(
    val service: ServiceDetails = ServiceDetails.IV_HYDRATION,
    val healthcareService: HealthcareServiceUiModel? = null
)

enum class ServiceDetails {
    IV_HYDRATION,
}

sealed interface ServiceDetailsIntent {
    data class ServiceReceived(val service: HealthcareServiceUiModel) : ServiceDetailsIntent
    data object BackClicked : ServiceDetailsIntent
    data object ShareClicked : ServiceDetailsIntent
    data object RequestServiceClicked : ServiceDetailsIntent
}

sealed interface ServiceDetailsEffect {
    data object NavigateBack : ServiceDetailsEffect
    data object ShareService : ServiceDetailsEffect
    data class RequestService(val service: HealthcareServiceUiModel) : ServiceDetailsEffect
}
