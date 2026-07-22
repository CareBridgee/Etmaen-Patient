package com.carenest.presentation.ui.services.details

import com.carenest.domain.model.home.ServiceCategory

data class ServiceDetailsState(
    val category: ServiceCategory = ServiceCategory.IV_DRIP,
    val service: ServiceDetails = ServiceDetails.IV_HYDRATION,
)

enum class ServiceDetails {
    IV_HYDRATION,
}

sealed interface ServiceDetailsIntent {
    data class CategoryReceived(val category: ServiceCategory) : ServiceDetailsIntent
    data object BackClicked : ServiceDetailsIntent
    data object ShareClicked : ServiceDetailsIntent
    data object RequestServiceClicked : ServiceDetailsIntent
}

sealed interface ServiceDetailsEffect {
    data object NavigateBack : ServiceDetailsEffect
    data object ShareService : ServiceDetailsEffect
    data object RequestService : ServiceDetailsEffect
}
