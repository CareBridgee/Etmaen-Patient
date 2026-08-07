package com.carenest.data.source.remote.dto.tracking

import kotlinx.serialization.Serializable

@Serializable
data class CancelRequest(
    val reason: String,
    val note: String
)