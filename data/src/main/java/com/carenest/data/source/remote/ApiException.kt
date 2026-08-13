package com.carenest.data.source.remote

internal class ApiException(
    val statusCode: Int,
    val backendCode: String?,
    val details: Map<String, String>? = null,
    message: String
) : Exception(message)
