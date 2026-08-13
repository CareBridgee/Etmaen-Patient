package com.carenest.data.source.remote.dto.profile

import kotlinx.serialization.Serializable

@Serializable
data class ProfileRequestDto(
    val relationship: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val phoneNumber: String? = null,
    val dateOfBirth: String? = null,
    val gender: String? = null,
    val bloodType: String? = null,
    val height: Double? = null,
    val weight: Double? = null,
    val mobilityStatus: String? = null,
    val mobilityNotes: String? = null,
    val previousSurgeries: String? = null,
    val previousHospitalizations: String? = null,
    val profileImageUrl: String? = null
)

internal fun ProfileRequestDto.toMultipartFormData(): io.ktor.client.request.forms.MultiPartFormDataContent =
    io.ktor.client.request.forms.MultiPartFormDataContent(
        io.ktor.client.request.forms.formData {
            relationship?.uppercase()?.takeIf(String::isNotBlank)?.let { append("relationship", it) }
            firstName?.takeIf(String::isNotBlank)?.let { append("firstName", it) }
            lastName?.takeIf(String::isNotBlank)?.let { append("lastName", it) }
            dateOfBirth?.takeIf(String::isNotBlank)?.let { append("dateOfBirth", it.toBackendDateFormat()) }
            gender?.uppercase()?.takeIf(String::isNotBlank)?.let { append("gender", it) }
            bloodType?.takeIf(String::isNotBlank)?.let { append("bloodType", it) }
            height?.takeIf { it > 0 }?.let { append("height", it.toString()) }
            weight?.takeIf { it > 0 }?.let { append("weight", it.toString()) }
            mobilityStatus?.takeIf(String::isNotBlank)?.let { append("mobilityStatus", it) }
            mobilityNotes?.takeIf(String::isNotBlank)?.let { append("mobilityNotes", it) }
            previousSurgeries?.takeIf(String::isNotBlank)?.let { append("previousSurgeries", it) }
            previousHospitalizations?.takeIf(String::isNotBlank)?.let { append("previousHospitalizations", it) }
            profileImageUrl?.takeIf(String::isNotBlank)?.let { append("profileImageUrl", it) }
        }
    )

private fun String.toBackendDateFormat(): String {
    val trimmed = trim()
    if (trimmed.isBlank()) return ""
    if (trimmed.matches(Regex("^\\d{4}-\\d{2}-\\d{2}$"))) return trimmed
    val utc = java.util.TimeZone.getTimeZone("UTC")
    val parsers = listOf(
        "MM/dd/yyyy",
        "dd/MM/yyyy",
        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
        "yyyy-MM-dd'T'HH:mm:ss'Z'"
    )
    for (pattern in parsers) {
        val parser = java.text.SimpleDateFormat(pattern, java.util.Locale.US).apply {
            isLenient = false
            timeZone = utc
        }
        val parsed = runCatching { parser.parse(trimmed) }.getOrNull()
        if (parsed != null) {
            val target = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).apply { timeZone = utc }
            return target.format(parsed)
        }
    }
    return trimmed
}

@Serializable
data class ProfileResponseDto(
    val id: String? = null,
    val userId: String? = null,
    val relationship: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val phoneNumber: String? = null,
    val dateOfBirth: String? = null,
    val gender: String? = null,
    val bloodType: String? = null,
    val height: Double? = null,
    val weight: Double? = null,
    val mobilityStatus: String? = null,
    val mobilityNotes: String? = null,
    val previousSurgeries: String? = null,
    val previousHospitalizations: String? = null,
    val profileImageUrl: String? = null,
    val isPrimary: Boolean? = null,
    val isDeleted: Boolean? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)
