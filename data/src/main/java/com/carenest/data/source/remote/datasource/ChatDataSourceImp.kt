package com.carenest.data.source.remote.datasource


import android.os.Build
import androidx.annotation.RequiresApi
import com.carenest.domain.model.chat.ChatMessage
import com.carenest.domain.model.chat.ChatMessageType
import com.carenest.domain.model.chat.ChatParticipant
import com.carenest.domain.model.chat.ChatSession
import com.carenest.domain.model.chat.MessageSender
import com.carenest.domain.model.chat.MessageStatus
import kotlinx.coroutines.delay
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
class ChatDataSourceImp @Inject constructor() : ChatDataSource {

    private fun todayAt(hour: Int, minute: Int): Long =
        java.time.LocalDate.now()
            .atTime(hour, minute)
            .atZone(java.time.ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

    override suspend fun fetchChatSession(requestId: String): ChatSession {
        delay(500)
        return ChatSession(
            participant = ChatParticipant(
                nurseId = "nurse_001",
                name = "Sarah Mitchell",
                photoUrl = null,
                isOnline = true,
                phoneNumber = "+15551234567",
            ),
            messages = listOf(
                ChatMessage(
                    id = "sys_1",
                    type = ChatMessageType.SYSTEM_TIP,
                    text = "Sarah is your assigned caregiver for today. You can share vitals or images securely through this encrypted chat.",
                    senderType = MessageSender.NURSE,
                    sentAtEpochMillis = todayAt(10, 41),
                ),
                ChatMessage(
                    id = "msg_1",
                    type = ChatMessageType.INCOMING,
                    text = "Hello Elena! I'm on my way to your location. I should be there in about 10 minutes.",
                    senderType = MessageSender.NURSE,
                    sentAtEpochMillis = todayAt(10, 42),
                ),
                ChatMessage(
                    id = "msg_2",
                    type = ChatMessageType.OUTGOING,
                    text = "Thank you, Sarah. I have the medical reports ready for you.",
                    senderType = MessageSender.PATIENT,
                    sentAtEpochMillis = todayAt(10, 43),
                    status = MessageStatus.SEEN,
                ),
            ),
        )
    }

    override suspend fun sendMessage(requestId: String, text: String): ChatMessage {
        delay(300)
        return ChatMessage(
            id = "msg_${System.currentTimeMillis()}",
            type = ChatMessageType.OUTGOING,
            text = text,
            senderType = MessageSender.PATIENT,
            sentAtEpochMillis = System.currentTimeMillis(),
            status = MessageStatus.SENT,
        )
    }
}