package com.carenest.data.source.remote.service

import com.carenest.data.di.qualifier.PaymobHttpClient
import com.carenest.data.source.remote.dto.paymob.PaymobIntentionRequestDto
import com.carenest.data.source.remote.dto.paymob.PaymobIntentionResponseDto
import com.carenest.data.source.remote.dto.paymob.PaymobRetrievedIntentionDto
import com.carenest.data.utils.executeRequest
import io.ktor.client.HttpClient
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import io.ktor.http.path
import javax.inject.Inject
import kotlinx.serialization.json.Json

class PaymobApiServiceImpl @Inject constructor(
    @param:PaymobHttpClient private val httpClient: HttpClient,
    private val json: Json,
) : PaymobApiService {
    override suspend fun createIntention(
        request: PaymobIntentionRequestDto,
    ): Result<PaymobIntentionResponseDto> =
        httpClient.executeRequest(json) {
            method = HttpMethod.Post
            url { path("v1/intention/") }
            contentType(ContentType.Application.Json)
            setBody(request)
        }

    override suspend fun retrieveIntention(
        publicKey: String,
        clientSecret: String,
    ): Result<PaymobRetrievedIntentionDto> =
        httpClient.executeRequest(json) {
            method = HttpMethod.Get
            url { path("v1/intention/element/$publicKey/$clientSecret/") }
        }
}
