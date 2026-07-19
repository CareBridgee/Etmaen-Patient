package com.carenest.data.utils

/**
 * Author: Wahid Ali Wahid Hussien
 * Created: 16/07/2026
 */

import com.carenest.data.source.remote.dto.ErrorResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.request
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import kotlinx.serialization.json.Json

suspend inline fun <reified T> HttpClient.getResults(
    block: HttpRequestBuilder.() -> Unit
): Result<T> = try {
    val response = request(block)
    if (response.status == HttpStatusCode.OK || response.status == HttpStatusCode.Created) {
        Result.success(response.body())
    } else {
        val errorText = response.bodyAsText()
        val errorMessage = try {
            val errorBody = Json { ignoreUnknownKeys = true }.decodeFromString<ErrorResponse>(errorText)
            errorBody.details?.values?.firstOrNull() ?: errorBody.message ?: "Unknown error"
        } catch (e: Exception) {
            "Error ${response.status.value}: $errorText"
        }
        Result.failure(Throwable(errorMessage))
    }
} catch (e: Exception) {
    Result.failure(e)
}