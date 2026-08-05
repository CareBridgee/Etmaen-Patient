package com.carenest.domain.model.user

class UserException(
    message: String,
    val statusCode: Int? = null,
    val backendCode: String? = null
) : Exception(message)
