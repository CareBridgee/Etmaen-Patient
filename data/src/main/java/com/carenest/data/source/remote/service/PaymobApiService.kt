package com.carenest.data.source.remote.service

import com.carenest.data.source.remote.dto.paymob.PaymobIntentionRequestDto
import com.carenest.data.source.remote.dto.paymob.PaymobIntentionResponseDto

interface PaymobApiService {
    suspend fun createIntention(request: PaymobIntentionRequestDto): Result<PaymobIntentionResponseDto>
}
