package com.carenest.data.source.remote.dto.user

import kotlinx.serialization.Serializable

@Serializable
data class FileUploadRequestDto(
    val file: String
)
