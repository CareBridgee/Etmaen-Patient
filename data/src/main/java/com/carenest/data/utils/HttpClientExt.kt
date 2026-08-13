package com.carenest.data.utils

import com.carenest.data.source.remote.ApiException
import com.carenest.data.source.remote.dto.ErrorResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.request
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.json.Json

internal suspend inline fun <reified T> HttpClient.executeRequest(
    json: Json,
    noinline block: HttpRequestBuilder.() -> Unit
): Result<T> = try {
    val response = request(block)
    response.ensureSuccessful(json)
    Result.success(response.body<T>())
} catch (cancellation: CancellationException) {
    throw cancellation
} catch (error: Throwable) {
    Result.failure(error)
}

internal suspend fun HttpClient.executeUnitRequest(
    json: Json,
    block: HttpRequestBuilder.() -> Unit
): Result<Unit> = try {
    val response = request(block)
    response.ensureSuccessful(json)
    Result.success(Unit)
} catch (cancellation: CancellationException) {
    throw cancellation
} catch (error: Throwable) {
    Result.failure(error)
}

@PublishedApi
internal suspend fun HttpResponse.ensureSuccessful(json: Json) {
    if (status.value in 200..299) return

    val rawBody = bodyAsText()
    val errorResponse = runCatching {
        json.decodeFromString<ErrorResponse>(rawBody)
    }.getOrNull()

    val message = errorResponse?.details?.values?.firstOrNull()
        ?: errorResponse?.message
        ?: rawBody.takeIf(String::isNotBlank)
        ?: "Request failed with HTTP ${status.value}"

    throw ApiException(
        statusCode = status.value,
        backendCode = errorResponse?.code,
        details = errorResponse?.details,
        message = message
    )
}
