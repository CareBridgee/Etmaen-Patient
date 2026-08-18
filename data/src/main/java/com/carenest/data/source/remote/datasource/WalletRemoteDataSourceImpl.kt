package com.carenest.data.source.remote.datasource

import com.carenest.data.source.remote.dto.wallet.CreditUpdateRequestDto
import com.carenest.data.source.remote.service.WalletApiService
import javax.inject.Inject

class WalletRemoteDataSourceImpl @Inject constructor(
    private val api: WalletApiService,
) : WalletRemoteDataSource {
    override suspend fun getCredit(userId: String) = api.getCredit(userId)

    override suspend fun updateCredit(
        userId: String,
        request: CreditUpdateRequestDto,
    ) = api.updateCredit(userId, request)
}
