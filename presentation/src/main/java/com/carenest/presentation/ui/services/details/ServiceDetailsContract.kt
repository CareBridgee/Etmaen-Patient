package com.carenest.presentation.ui.services.details

import com.carenest.domain.model.ServiceDetailsModel

data class ServiceDetailsState(
    val healthcareService: ServiceDetailsModel? = null,
    val errorMessage : String?=null
)


sealed interface ServiceDetailsIntent {
    data class ServiceReceived(val service: ServiceDetailsModel) : ServiceDetailsIntent
    data class GetServiceDetails(val serviceId: String) : ServiceDetailsIntent
    data object BackClicked : ServiceDetailsIntent
    data object ShareClicked : ServiceDetailsIntent
    data object RequestServiceClicked : ServiceDetailsIntent
}

sealed interface ServiceDetailsEffect {
    data object NavigateBack : ServiceDetailsEffect
    data class ShareService(val serviceId: String, val serviceName: String, val description: String) : ServiceDetailsEffect
    data class RequestService(val serviceId: String) : ServiceDetailsEffect
}
