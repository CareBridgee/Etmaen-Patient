package com.carenest.data.source.remote.datasource


import android.os.Build
import androidx.annotation.RequiresApi
import com.carenest.data.socket.models.SendMessageRequestDto
import com.carenest.data.source.local.datasource.UserLocalDataSource
import com.carenest.data.source.remote.service.NurseTrackingService
import com.carenest.domain.model.ChatMessage
import com.carenest.domain.model.ChatMessageType
import com.carenest.domain.model.ChatParticipant
import com.carenest.domain.model.ChatSession
import com.carenest.domain.model.MessageSender
import com.carenest.domain.model.MessageStatus
import kotlinx.coroutines.flow.firstOrNull
import java.time.Instant
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
class ChatDataSourceImp @Inject constructor(
    private val apiService: NurseTrackingService,
    private val userLocalDataSource: UserLocalDataSource,
) : ChatDataSource {

    override suspend fun fetchChatSession(requestId: String): ChatSession {
        val currentUserId = userLocalDataSource.observeCurrentUser().firstOrNull()?.id.orEmpty()
        val messagesDto = apiService.getChatMessages(requestId).getOrThrow()

        val trackingInfo = apiService.fetchServiceRequest(requestId).getOrNull()
        val nurseId = trackingInfo?.nurse?.id
        val nurseDetails = nurseId?.let { apiService.fetchNurseDetails(it).getOrNull() }

        val messages = messagesDto.map { dto ->
            val isMine = dto.senderUserId == currentUserId
            ChatMessage(
                id = dto.id ?: "",
                type = if (isMine) ChatMessageType.OUTGOING else ChatMessageType.INCOMING,
                text = dto.content ?: "",
                senderType = if (isMine) MessageSender.PATIENT else MessageSender.NURSE,
                sentAtEpochMillis = try {
                    Instant.parse(dto.createdAt).toEpochMilli()
                } catch (_: Exception) {
                    System.currentTimeMillis()
                },
                status = if (isMine) MessageStatus.SENT else MessageStatus.DELIVERED
            )
        }

        val participant = ChatParticipant(
            nurseId = nurseId.orEmpty(),
            name = trackingInfo?.nurse?.let { "${it.firstName} ${it.lastName}" }.orEmpty(),
            photoUrl = nurseDetails?.profileImageUrl ?: trackingInfo?.nurse?.profileImageUrl,
            isOnline = true, // Force to true as per requirement
            phoneNumber = nurseDetails?.phoneNumber.orEmpty(),
        )

        return ChatSession(
            participant = participant,
            messages = messages,
        )
    }

    override suspend fun sendMessage(requestId: String, text: String): ChatMessage {
        val response = apiService.sendChatMessage(
            requestId,
            SendMessageRequestDto(content = text)
        ).getOrThrow()

        return ChatMessage(
            id = response.id ?: "msg_${System.currentTimeMillis()}",
            type = ChatMessageType.OUTGOING,
            text = response.content ?: text,
            senderType = MessageSender.PATIENT,
            sentAtEpochMillis = try {
                Instant.parse(response.createdAt).toEpochMilli()
            } catch (_: Exception) {
                System.currentTimeMillis()
            },
            status = MessageStatus.SENT,
        )
    }
}