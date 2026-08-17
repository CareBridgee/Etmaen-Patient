package com.carenest.data.source.remote.service

import com.carenest.data.source.remote.dto.paymob.PaymobIntentionRequestDto
import com.carenest.data.source.remote.dto.paymob.PaymobIntentionResponseDto
import com.carenest.data.source.remote.dto.paymob.PaymobRetrievedIntentionDto

interface PaymobApiService {
    suspend fun createIntention(request: PaymobIntentionRequestDto): Result<PaymobIntentionResponseDto>
    suspend fun retrieveIntention(
        publicKey: String,
        clientSecret: String,
    ): Result<PaymobRetrievedIntentionDto>
}
