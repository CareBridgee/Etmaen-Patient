package com.carenest.presentation.ui.chat.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.carenest.designsystem.theme.SpTheme
import com.carenest.designsystem.theme.Theme
import com.carenest.domain.model.chat.ChatMessage
import com.carenest.domain.model.chat.ChatMessageType
import com.carenest.domain.model.chat.MessageSender
import com.carenest.domain.model.chat.MessageStatus
import com.carenest.presentation.R
import com.carenest.presentation.core.util.formatMessageTime
import com.carenest.designsystem.R as RD

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MessageBubble(message: ChatMessage, modifier: Modifier = Modifier) {
    when (message.type) {
        ChatMessageType.SYSTEM_TIP -> QuickTipCard(text = message.text, modifier = modifier)
        ChatMessageType.INCOMING -> ChatBubble(
            text = message.text,
            timestamp = formatMessageTime(message.sentAtEpochMillis),
            isOutgoing = false,
            status = null,
            modifier = modifier,
        )

        ChatMessageType.OUTGOING -> ChatBubble(
            text = message.text,
            timestamp = formatMessageTime(message.sentAtEpochMillis),
            isOutgoing = true,
            status = message.status,
            modifier = modifier,
        )
    }
}

@Composable
private fun ChatBubble(
    text: String,
    timestamp: String,
    isOutgoing: Boolean,
    status: MessageStatus?,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = if (isOutgoing) Arrangement.End else Arrangement.Start,
    ) {
        Column(
            modifier = Modifier.widthIn(max = 260.dp),
            horizontalAlignment = if (isOutgoing) Alignment.End else Alignment.Start,
        ) {
            Text(
                text = text,
                style = Theme.typography.body.medium.copy(
                    fontWeight = FontWeight.Normal
                ),
                color = if (isOutgoing) Theme.colors.onPrimary else Theme.colors.primaryFont,
                modifier = Modifier
                    .background(
                        color = if (isOutgoing) Theme.colors.primary else Theme.colors.disable,
                        shape = RoundedCornerShape(16.dp),
                    )
                    .padding(horizontal = 14.dp, vertical = 10.dp),
            )
            Spacer(modifier = Modifier.padding(top = 2.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = timestamp,
                    style = Theme.typography.hint.small,
                    color = Theme.colors.hint,
                )
                if (isOutgoing && status == MessageStatus.SEEN) {
                    Spacer(modifier = Modifier.padding(start = 4.dp))
                    Icon(
                        imageVector = Icons.Filled.DoneAll,
                        contentDescription = stringResource(R.string.chat_message_seen),
                        tint = Theme.colors.primary,
                        modifier = Modifier.padding(start = 2.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickTipCard(text: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Theme.colors.primaryContainer, RoundedCornerShape(12.dp))
            .padding(Theme.spacing.medium),
    ) {
        Icon(
            painter = painterResource(RD.drawable.ic_light),
            contentDescription = null,
            tint = Theme.colors.onPrimaryContainer,
            modifier = Modifier.padding(top = Theme.spacing.extraSmall)
        )
        Spacer(modifier = Modifier.padding(start = Theme.spacing.small))
        Column {
            Text(
                text = stringResource(R.string.chat_quick_tip_title),
                style = Theme.typography.body.medium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = Theme.colors.onPrimaryContainer,
            )
            Text(
                text = text,
                style = Theme.typography.body.small,
                color = Theme.colors.onPrimaryContainer,
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
private fun Preview() {
    SpTheme {
        MessageBubble(
            message = ChatMessage(
                id = "",
                type = ChatMessageType.INCOMING,
                text = "Hello Elena! I'm on my way to your\n" + "location. I should be there in about\n" + "10 minutes.",
                senderType = MessageSender.NURSE,
                status = MessageStatus.SEEN,
                sentAtEpochMillis = 0
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewQuickTipCard() {
    SpTheme {
        QuickTipCard(
            text = "Sarah is your assigned caregiver for today. You\n" + "can share vitals or images securely through this\n" + "encrypted chat.",
        )
    }
}