package com.carenest.domain.model.profile

class ProfileException(
    message: String,
    val statusCode: Int? = null,
    val backendCode: String? = null
) : Exception(message)
