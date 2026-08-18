package com.carenest.domain.repository

interface WalletOperationGuardRepository {
    suspend fun isTopUpProcessed(transactionId: String): Boolean
    suspend fun markTopUpProcessed(transactionId: String)
    suspend fun isDeductionProcessed(serviceRequestId: String): Boolean
    suspend fun markDeductionProcessed(serviceRequestId: String)
}
