package com.carenest.data.source.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    val timestamp: String? = null,
    val status: Int? = null,
    val error: String? = null,
    val code: String? = null,
    val message: String? = null,
    val details: Map<String, String>? = null
)
