package com.carenest.data.repository

import com.carenest.data.source.remote.dto.aichat.AiChatRequestDto
import com.carenest.data.source.remote.service.AiChatApiService
import com.carenest.domain.repository.AiChatRepository
import javax.inject.Inject
import javax.inject.Singleton

import com.carenest.domain.model.AiChatMessageResult

@Singleton
class AiChatRepositoryImpl @Inject constructor(
    private val apiService: AiChatApiService
) : AiChatRepository {

    private var lastAiReport: String? = null

    override suspend fun sendChatMessage(profileId: String, message: String): Result<AiChatMessageResult> {
        val rawResult = apiService.sendChatMessage(AiChatRequestDto(profileId = profileId, message = message))
        rawResult.onSuccess { response ->
            val descriptionToCache = response.draft?.serviceDescription
                ?.takeIf { it.isNotBlank() }
                ?: response.draft?.careDescription
                    ?.takeIf { it.isNotBlank() }
                ?: response.reply
            lastAiReport = descriptionToCache
        }
        return rawResult.map { response ->
            AiChatMessageResult(
                reply = response.reply,
                serviceTypeId = response.draft?.serviceTypeId,
                serviceTypeName = response.draft?.serviceTypeName,
                serviceDescription = response.draft?.serviceDescription,
                careDescription = response.draft?.careDescription,
                isComplete = response.draft?.complete ?: false
            )
        }
    }

    override fun getLastAiReport(): String? = lastAiReport

    override fun setLastAiReport(report: String?) {
        lastAiReport = report
    }
}
