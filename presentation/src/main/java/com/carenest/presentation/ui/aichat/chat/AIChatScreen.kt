package com.carenest.presentation.ui.aichat.chat

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.carenest.designsystem.theme.SpTheme
import com.carenest.designsystem.theme.Theme
import com.carenest.designsystem.R as RD
import com.carenest.presentation.R
import com.carenest.presentation.core.mvi.ObserveEffect
import com.carenest.presentation.navigation.ScreenTopBar

@Composable
fun AIChatScreen(
    patientId: String,
    onNavigateBack: () -> Unit,
    onNavigateToBookings: () -> Unit,
    onNavigateToServiceDetails: (String) -> Unit,
    viewModel: AIChatViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    ScreenTopBar(
        title = "AI Health Assistant",
        onLeadingClick = onNavigateBack
    )

    ObserveEffect(viewModel.effect) { effect ->
        when (effect) {
            AIChatEffect.NavigateBack -> onNavigateBack()
            AIChatEffect.NavigateToBookings -> onNavigateToBookings()
            is AIChatEffect.NavigateToServiceDetails -> onNavigateToServiceDetails(effect.categoryId)
        }
    }

    AIChatContent(
        state = state,
        onEvent = viewModel::onEvent
    )
}

@Composable
fun AIChatContent(
    state: AIChatState,
    onEvent: (AIChatEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.colors.backGround)
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            items(state.messages) { message ->
                when (message.type) {
                    ChatMessageType.TEXT -> {
                        TextMessageBubble(message = message)
                    }
                    ChatMessageType.SERVICE_RECOMMENDATION -> {
                        message.serviceData?.let { service ->
                            ServiceRecommendationCard(
                                serviceData = service,
                                onBookNowClick = { onEvent(AIChatEvent.OnBookNowClicked) },
                                onViewServiceClick = { onEvent(AIChatEvent.OnViewServiceClicked(service.categoryId)) }
                            )
                        }
                    }
                }
            }
        }

        ChatInputBar(
            inputText = state.inputText,
            onInputChange = { onEvent(AIChatEvent.OnInputTextChanged(it)) },
            onSendClick = { onEvent(AIChatEvent.OnSendMessage) }
        )
    }
}

@Composable
fun TextMessageBubble(message: ChatMessage) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (message.isUser) Alignment.End else Alignment.Start
    ) {
        Surface(
            shape = RoundedCornerShape(
                topStart = 24.dp,
                topEnd = 24.dp,
                bottomStart = if (message.isUser) 24.dp else 4.dp,
                bottomEnd = if (message.isUser) 4.dp else 24.dp
            ),
            color = if (message.isUser) Theme.colors.primary else Theme.colors.surface,
            shadowElevation = if (message.isUser) 0.dp else 2.dp,
            modifier = Modifier.fillMaxWidth(0.85f)
        ) {
            Box(modifier = Modifier.padding(20.dp)) {
                if (message.text.contains("Hypertension")) {
                    val annotatedString = buildAnnotatedString {
                        val parts = message.text.split("Hypertension")
                        append(parts[0])
                        withStyle(style = SpanStyle(color = if (message.isUser) Theme.colors.surface else Theme.colors.primary, fontWeight = FontWeight.Bold)) {
                            append("Hypertension")
                        }
                        if (parts.size > 1) {
                            append(parts[1])
                        }
                    }
                    Text(
                        text = annotatedString,
                        style = Theme.typography.body.large.copy(
                            fontSize = 15.sp,
                            lineHeight = 22.sp
                        ),
                        color = if (message.isUser) Theme.colors.surface else Theme.colors.primaryFont
                    )
                } else {
                    Text(
                        text = message.text,
                        style = Theme.typography.body.large.copy(
                            fontSize = 15.sp,
                            lineHeight = 22.sp
                        ),
                        color = if (message.isUser) Theme.colors.surface else Theme.colors.primaryFont
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Just now",
            style = Theme.typography.body.small.copy(fontSize = 12.sp),
            color = Theme.colors.secondaryFont.copy(alpha = 0.7f),
            modifier = Modifier.padding(horizontal = 4.dp)
        )
    }
}

@Composable
fun ServiceRecommendationCard(
    serviceData: ServiceRecommendationData,
    onBookNowClick: () -> Unit,
    onViewServiceClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = Theme.colors.surface,
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .background(Theme.colors.primary.copy(alpha = 0.08f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = RD.drawable.ic_heart_beat),
                    contentDescription = null,
                    tint = Theme.colors.primary,
                    modifier = Modifier.size(56.dp)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Theme.colors.primary.copy(alpha = 0.15f))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "RECOMMENDED",
                        style = Theme.typography.body.small.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            letterSpacing = 0.5.sp
                        ),
                        color = Theme.colors.primary
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = serviceData.title,
                    style = Theme.typography.display.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    ),
                    color = Theme.colors.primaryFont
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = serviceData.subtitle,
                    style = Theme.typography.body.medium.copy(
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    ),
                    color = Theme.colors.secondaryFont
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onBookNowClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Theme.colors.primary)
                ) {
                    Text(
                        text = stringResource(R.string.book_now),
                        style = Theme.typography.body.large.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        ),
                        color = Theme.colors.surface
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = onViewServiceClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Theme.colors.primary.copy(alpha = 0.12f)
                    )
                ) {
                    Text(
                        text = stringResource(R.string.view_service),
                        style = Theme.typography.body.large.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        ),
                        color = Theme.colors.primary
                    )
                }
            }
        }
    }
}

@Composable
fun ChatInputBar(
    inputText: String,
    onInputChange: (String) -> Unit,
    onSendClick: () -> Unit
) {
    Surface(
        color = Theme.colors.surface,
        shadowElevation = 8.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(28.dp))
                    .background(Theme.colors.primary.copy(alpha = 0.06f))
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    BasicTextField(
                        value = inputText,
                        onValueChange = onInputChange,
                        modifier = Modifier.weight(1f),
                        textStyle = Theme.typography.body.large.copy(
                            color = Theme.colors.primaryFont,
                            fontSize = 15.sp
                        ),
                        decorationBox = { innerTextField ->
                            if (inputText.isEmpty()) {
                                Text(
                                    text = stringResource(R.string.ask_health_profile),
                                    style = Theme.typography.body.large.copy(fontSize = 15.sp),
                                    color = Theme.colors.secondaryFont.copy(alpha = 0.6f)
                                )
                            }
                            innerTextField()
                        }
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Voice Input",
                        tint = Theme.colors.secondaryFont,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(if (inputText.isNotBlank()) Theme.colors.primary else Theme.colors.primary.copy(alpha = 0.5f))
                    .clickable(
                        enabled = inputText.isNotBlank(),
                        onClick = onSendClick
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = RD.drawable.ic_send),
                    contentDescription = "Send",
                    tint = Theme.colors.surface,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844, name = "AIChat - Light Mode")
@Composable
fun AIChatScreenLightPreview() {
    SpTheme(isDarkTheme = false) {
        AIChatContent(
            state = AIChatState(
                messages = listOf(
                    ChatMessage(
                        id = "1",
                        text = "Hello Elena, based on your health profile and recent reports, I see you are managing Hypertension. Would you like to schedule a blood pressure check or a follow-up consultation?",
                        isUser = false,
                        type = ChatMessageType.TEXT
                    ),
                    ChatMessage(
                        id = "2",
                        text = "",
                        isUser = false,
                        type = ChatMessageType.SERVICE_RECOMMENDATION,
                        serviceData = ServiceRecommendationData(
                            categoryId = "hydration",
                            title = "IV Hydration Therapy",
                            subtitle = "A 15-minute clinical assessment with a registered nurse to ensure your medication is managing your hypertension effectively.",
                            price = "$49",
                            duration = ""
                        )
                    )
                ),
                inputText = ""
            ),
            onEvent = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844, name = "AIChat - Dark Mode")
@Composable
fun AIChatScreenDarkPreview() {
    SpTheme(isDarkTheme = true) {
        AIChatContent(
            state = AIChatState(
                messages = listOf(
                    ChatMessage(
                        id = "1",
                        text = "Hello Elena, based on your health profile and recent reports, I see you are managing Hypertension. Would you like to schedule a blood pressure check or a follow-up consultation?",
                        isUser = false,
                        type = ChatMessageType.TEXT
                    ),
                    ChatMessage(
                        id = "2",
                        text = "",
                        isUser = false,
                        type = ChatMessageType.SERVICE_RECOMMENDATION,
                        serviceData = ServiceRecommendationData(
                            categoryId = "hydration",
                            title = "IV Hydration Therapy",
                            subtitle = "A 15-minute clinical assessment with a registered nurse to ensure your medication is managing your hypertension effectively.",
                            price = "$49",
                            duration = ""
                        )
                    )
                ),
                inputText = ""
            ),
            onEvent = {}
        )
    }
}

@Preview(showBackground = true, name = "Component - Text Bubble")
@Composable
fun TextMessageBubblePreview() {
    SpTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            TextMessageBubble(
                message = ChatMessage(
                    id = "1",
                    text = "Hello Elena, based on your health profile and recent reports, I see you are managing Hypertension. Would you like to schedule a blood pressure check?",
                    isUser = false
                )
            )
        }
    }
}

@Preview(showBackground = true, name = "Component - Service Card")
@Composable
fun ServiceRecommendationCardPreview() {
    SpTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            ServiceRecommendationCard(
                serviceData = ServiceRecommendationData(
                    categoryId = "hydration",
                    title = "IV Hydration Therapy",
                    subtitle = "A 15-minute clinical assessment with a registered nurse to ensure your medication is managing your hypertension effectively.",
                    price = "$49",
                    duration = ""
                ),
                onBookNowClick = {},
                onViewServiceClick = {}
            )
        }
    }
}

