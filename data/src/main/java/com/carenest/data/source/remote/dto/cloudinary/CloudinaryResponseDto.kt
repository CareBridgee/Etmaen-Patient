package com.carenest.data.source.remote.dto.cloudinary

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CloudinaryResponseDto(
    @SerialName("secure_url") val secureUrl: String? = null,
    @SerialName("url") val url: String? = null,
    @SerialName("public_id") val publicId: String? = null
)
