package com.carenest.data.source.remote.service

import com.carenest.data.source.remote.dto.wallet.CreditResponseDto
import com.carenest.data.source.remote.dto.wallet.CreditUpdateRequestDto
import com.carenest.data.source.remote.dto.wallet.CreditUpdateResponseDto
import com.carenest.data.utils.executeRequest
import io.ktor.client.HttpClient
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import io.ktor.http.path
import javax.inject.Inject
import kotlinx.serialization.json.Json

class WalletApiServiceImpl @Inject constructor(
    private val httpClient: HttpClient,
    private val json: Json,
) : WalletApiService {
    override suspend fun getCredit(userId: String): Result<CreditResponseDto> =
        httpClient.executeRequest(json) {
            method = HttpMethod.Get
            url { path("api/v1/users/$userId/credit") }
        }

    override suspend fun updateCredit(
        userId: String,
        request: CreditUpdateRequestDto,
    ): Result<CreditUpdateResponseDto> =
        httpClient.executeRequest(json) {
            method = HttpMethod.Patch
            url { path("api/v1/users/$userId/credit") }
            contentType(ContentType.Application.Json)
            setBody(request)
        }
}
