package com.carenest.data.source.remote.service

import com.carenest.data.source.remote.dto.aichat.AiChatRequestDto
import com.carenest.data.source.remote.dto.aichat.AiChatResponseDto
import com.carenest.data.source.remote.dto.aichat.ResetAiChatRequestDto

interface AiChatApiService {
    suspend fun sendChatMessage(request: AiChatRequestDto): Result<AiChatResponseDto>
    suspend fun resetChat(request: ResetAiChatRequestDto): Result<Unit>
}
