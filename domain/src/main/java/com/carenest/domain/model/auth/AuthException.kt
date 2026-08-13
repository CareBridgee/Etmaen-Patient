package com.carenest.domain.model.auth

enum class AuthFailure {
    Network,
    InvalidPhone,
    InvalidOtp,
    ExpiredOtp,
    TooManyRequests,
    Server,
    PhoneAlreadyTaken,
    Unknown
}

class AuthException(
    val failure: AuthFailure,
    message: String,
    val statusCode: Int? = null,
    val backendCode: String? = null,
    val details: Map<String, String>? = null,
    cause: Throwable? = null
) : Exception(message, cause)
