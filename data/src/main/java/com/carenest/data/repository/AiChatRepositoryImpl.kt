package com.carenest.data.repository

import com.carenest.data.source.remote.dto.aichat.AiChatRequestDto
import com.carenest.data.source.remote.service.AiChatApiService
import com.carenest.domain.repository.AiChatRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AiChatRepositoryImpl @Inject constructor(
    private val apiService: AiChatApiService
) : AiChatRepository {

    private var lastAiReport: String? = null

    override suspend fun sendChatMessage(message: String): Result<String> {
        val result = apiService.sendChatMessage(AiChatRequestDto(message = message))
            .map { it.reply }
        result.onSuccess { reply ->
            lastAiReport = reply
        }
        return result
    }

    override fun getLastAiReport(): String? = lastAiReport

    override fun setLastAiReport(report: String?) {
        lastAiReport = report
    }
}
