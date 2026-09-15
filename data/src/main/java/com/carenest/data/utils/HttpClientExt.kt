package com.carenest.data.utils

import android.util.Log
import com.carenest.data.source.remote.dto.ErrorResponse
import com.carenest.domain.exception.ApiException
import com.carenest.domain.exception.BadRequestException
import com.carenest.domain.exception.NoInternetException
import com.carenest.domain.exception.NotFoundException
import com.carenest.domain.exception.ServerException
import com.carenest.domain.exception.UnauthorizedException
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.request
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.json.Json
import java.io.IOException

internal suspend inline fun <reified T> HttpClient.executeRequest(
    json: Json,
    noinline block: HttpRequestBuilder.() -> Unit
): Result<T> = try {
    val response = try {
        request(block)
    } catch (exception: IOException) {
        throw NoInternetException()
    } catch (exception: UnresolvedAddressException) {
        throw NoInternetException()
    }
    
    Result.success(handleResponseStatusCode<T>(json, response))
} catch (cancellation: CancellationException) {
    throw cancellation
} catch (error: Throwable) {
    Result.failure(error)
}

internal suspend fun HttpClient.executeUnitRequest(
    json: Json,
    block: HttpRequestBuilder.() -> Unit
): Result<Unit> = try {
    val response = try {
        request(block)
    } catch (exception: IOException) {
        throw NoInternetException()
    } catch (exception: UnresolvedAddressException) {
        throw NoInternetException()
    }
    
    handleResponseStatusCode<Unit>(json, response)
    Result.success(Unit)
} catch (cancellation: CancellationException) {
    throw cancellation
} catch (error: Throwable) {
    Result.failure(error)
}

@PublishedApi
internal suspend inline fun <reified T> handleResponseStatusCode(
    json: Json,
    response: HttpResponse
): T {
    return when (response.status.value) {
        in 200..299 -> {
            if (T::class == Unit::class) Unit as T else response.body<T>()
        }

        in 400..499 -> {
            val rawBody = response.bodyAsText()
            val errorResponse = runCatching {
                json.decodeFromString<ErrorResponse>(rawBody)
            }.getOrNull()
            
            val message = errorResponse?.details?.values?.firstOrNull()
                ?: errorResponse?.message
                ?: rawBody.takeIf(String::isNotBlank)
                ?: "Client error ${response.status.value}"

            when (response.status) {
                HttpStatusCode.Unauthorized -> {
                    Log.e("HttpClientExt", "Unauthorized: $message")
                    throw UnauthorizedException()
                }

                HttpStatusCode.NotFound -> {
                    Log.e("HttpClientExt", "Not found: $message")
                    throw NotFoundException()
                }

                HttpStatusCode.BadRequest -> {
                    Log.e("HttpClientExt", "Bad request: $message")
                    throw BadRequestException()
                }

                else -> {
                    Log.e("HttpClientExt", "Client error ${response.status.value}: $message")
                    throw ApiException(response.status.value)
                }
            }
        }

        in 500..599 -> {
            Log.e("HttpClientExt", "Server error: An error occurred on the server side")
            throw ServerException()
        }

        else -> {
            Log.e("HttpClientExt", "Unknown status ${response.status.value}: An unexpected status code was received")
            throw ApiException(response.status.value)
        }
    }
}
