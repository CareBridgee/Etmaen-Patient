package com.carenest.data.source.remote.service

import com.carenest.data.source.remote.dto.aichat.AiChatRequestDto
import com.carenest.data.source.remote.dto.aichat.AiChatResponseDto

interface AiChatApiService {
    suspend fun sendChatMessage(request: AiChatRequestDto): Result<AiChatResponseDto>
}
