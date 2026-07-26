package com.carenest.data.source.remote

internal class ApiException(
    val statusCode: Int,
    val backendCode: String?,
    message: String
) : Exception(message)
