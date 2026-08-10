package com.carenest.data.source.remote.service

import com.carenest.data.socket.models.ChatMessageResponseDto
import com.carenest.data.socket.models.SendMessageRequestDto
import com.carenest.data.source.remote.dto.tracking.NurseDetailsDto
import com.carenest.data.source.remote.dto.tracking.ServiceRequestTrackingDto
import com.carenest.data.source.remote.dto.tracking.VisitCodeResponseDto

interface NurseTrackingService {
    suspend fun fetchServiceRequest(requestId: String): Result<ServiceRequestTrackingDto>
    suspend fun cancelVisit(requestId: String): Boolean
    suspend fun fetchVisitCode(requestId: String): Result<VisitCodeResponseDto>
    suspend fun fetchNurseDetails(nurseId: String): Result<NurseDetailsDto>
    suspend fun getChatMessages(reservationId: String): Result<List<ChatMessageResponseDto>>
    suspend fun sendChatMessage(reservationId: String, body: SendMessageRequestDto): Result<ChatMessageResponseDto>
}