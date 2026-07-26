package com.carenest.data.source.remote.service

internal class ProfileApiException(
    val statusCode: Int,
    val backendCode: String?,
    message: String
) : Exception(message)
