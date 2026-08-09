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
class ChatDataSourceImp @Inject constructor(

) : ChatDataSource {

    override suspend fun fetchChatSession(requestId: String): ChatSession {
        delay(500)
        return ChatSession(
            participant = ChatParticipant(
                nurseId = "",
                name = "",
                photoUrl = null,
                isOnline = false,
                phoneNumber = "",
            ),
            messages = emptyList(),
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