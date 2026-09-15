package com.carenest.presentation.core.util

import com.carenest.domain.exception.*
import com.carenest.presentation.R

fun Throwable.toUiText(): UiText {
    return when (this) {
        is NoInternetException -> UiText.StringResource(R.string.auth_error_network)
        is UnauthorizedException -> UiText.StringResource(R.string.auth_error_verification)
        is NotFoundException -> UiText.StringResource(R.string.profile_load_failed)
        is BadRequestException -> UiText.StringResource(R.string.error_fill_required_fields)
        is ServerException -> UiText.StringResource(R.string.auth_error_service_unavailable)
        is ApiException -> UiText.StringResource(R.string.home_error_default_desc)
        else -> {
            val msg = this.message
            if (msg.isNullOrBlank()) {
                UiText.StringResource(R.string.home_error_default_desc)
            } else {
                UiText.DynamicString(msg)
            }
        }
    }
}
