package com.carenest.domain.exception

open class AppNetworkException(message: String? = null) : Exception(message)

class NoInternetException : AppNetworkException("No internet connection")

class UnauthorizedException : AppNetworkException("Unauthorized access")

class NotFoundException : AppNetworkException("Resource not found")

class BadRequestException : AppNetworkException("Bad request")

class ServerException : AppNetworkException("Internal server error")

class ApiException(val code: Int) : AppNetworkException("API error with status code $code")
