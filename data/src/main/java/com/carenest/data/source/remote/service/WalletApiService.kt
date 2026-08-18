package com.carenest.data.source.remote.service

import com.carenest.data.source.remote.dto.wallet.CreditResponseDto
import com.carenest.data.source.remote.dto.wallet.CreditUpdateRequestDto
import com.carenest.data.source.remote.dto.wallet.CreditUpdateResponseDto

interface WalletApiService {
    suspend fun getCredit(userId: String): Result<CreditResponseDto>
    suspend fun updateCredit(
        userId: String,
        request: CreditUpdateRequestDto,
    ): Result<CreditUpdateResponseDto>
}
