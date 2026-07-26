package com.carenest.data.mapper.profile

import com.carenest.domain.model.profile.ProfileException
import java.util.UUID

internal fun String?.requiredUuid(label: String): String {
    val value = requiredText(label)
    if (runCatching { UUID.fromString(value) }.isFailure) {
        throw ProfileException("Backend returned an invalid $label")
    }
    return value
}

internal fun String?.requiredText(label: String): String =
    this?.takeIf(String::isNotBlank)
        ?: throw ProfileException("Backend returned a missing $label")

internal fun String?.optionalUuid(label: String): String? {
    val value = this?.trim()?.takeIf(String::isNotEmpty) ?: return null
    if (runCatching { UUID.fromString(value) }.isFailure) {
        throw ProfileException("Backend returned an invalid $label")
    }
    return value
}
