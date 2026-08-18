package com.carenest.presentation.ui.aichat.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import com.carenest.designsystem.components.button.PrimaryButton
import com.carenest.designsystem.components.button.SecondaryButton
import com.carenest.designsystem.util.bounceClick
import com.carenest.designsystem.theme.SpTheme
import com.carenest.designsystem.theme.Theme
import com.carenest.designsystem.R as RD
import com.carenest.presentation.R
import com.carenest.presentation.core.mvi.ObserveEffect
import com.carenest.presentation.navigation.ScreenTopBar
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatMessageTime(epochMillis: Long): String {
    val formatter = SimpleDateFormat("h:mm a", Locale.getDefault())
    return formatter.format(Date(epochMillis))
}

@Composable
fun AIChatScreen(
    patientId: String,
    onNavigateBack: () -> Unit,
    onNavigateToBookings: () -> Unit,
    onNavigateToServiceDetails: (String) -> Unit,
    onNavigateToRequestService: (String) -> Unit = {},
    onShowMessage: (String) -> Unit = {},
    viewModel: AIChatViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    ScreenTopBar(
        title = stringResource(R.string.ai_health_assistant),
        onLeadingClick = onNavigateBack
    )

    ObserveEffect(viewModel.effect) { effect ->
        when (effect) {
            AIChatEffect.NavigateBack -> onNavigateBack()
            AIChatEffect.NavigateToBookings -> onNavigateToBookings()
            is AIChatEffect.NavigateToServiceDetails -> onNavigateToServiceDetails(effect.categoryId)
            is AIChatEffect.NavigateToRequestService -> onNavigateToRequestService(effect.serviceId)
            is AIChatEffect.ShowError -> {
                onShowMessage(effect.message)
            }
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
    val listState = rememberLazyListState()

    LaunchedEffect(state.messages.size, state.isLoading) {
        if (state.messages.isNotEmpty()) {
            listState.animateScrollToItem(0)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.colors.backGround)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Theme.colors.primary)
                )
                Text(
                    text = stringResource(R.string.online_ready),
                    style = Theme.typography.body.small.copy(fontSize = 12.sp),
                    color = Theme.colors.secondaryFont
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Theme.colors.primary.copy(alpha = 0.1f))
                    .then(
                        if (!state.isResetting && !state.isLoading) {
                            Modifier.bounceClick(
                                shape = RoundedCornerShape(20.dp),
                                onClick = { onEvent(AIChatEvent.OnStartOverClicked) }
                            )
                        } else {
                            Modifier
                        }
                    )
                    .padding(horizontal = 14.dp, vertical = 7.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (state.isResetting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(14.dp),
                            color = Theme.colors.primary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = stringResource(R.string.start_over),
                            tint = Theme.colors.primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Text(
                        text = stringResource(R.string.start_over),
                        style = Theme.typography.body.medium.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        ),
                        color = Theme.colors.primary
                    )
                }
            }
        }

        LazyColumn(
            state = listState,
            reverseLayout = true,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            if (state.isLoading) {
                item(key = "loading_indicator") {
                    AiLoadingBubble()
                }
            }

            items(state.messages.asReversed(), key = { it.id }) { message ->
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

            if (state.messages.isEmpty()) {
                item(key = "initial_welcome_message") {
                    TextMessageBubble(
                        message = ChatMessage(
                            id = "welcome",
                            text = stringResource(R.string.ai_chat_welcome_message),
                            isUser = false
                        )
                    )
                }
            }
        }

        ChatInputBar(
            inputText = state.inputText,
            isLoading = state.isLoading,
            onInputChange = { onEvent(AIChatEvent.OnInputTextChanged(it)) },
            onSendClick = { onEvent(AIChatEvent.OnSendMessage) }
        )
    }
}

@Composable
fun AiLoadingBubble() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Surface(
            shape = RoundedCornerShape(
                topStart = 24.dp,
                topEnd = 24.dp,
                bottomStart = 4.dp,
                bottomEnd = 24.dp
            ),
            color = Theme.colors.surface,
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    color = Theme.colors.primary,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = stringResource(R.string.ai_is_thinking),
                    style = Theme.typography.body.large.copy(fontSize = 14.sp),
                    color = Theme.colors.secondaryFont
                )
            }
        }
    }
}

@Composable
fun TextMessageBubble(message: ChatMessage) {
    val formattedTime = remember(message.sentAtEpochMillis) {
        formatMessageTime(message.sentAtEpochMillis)
    }

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

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = formattedTime,
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
        shape = RoundedCornerShape(Theme.spacing.large),
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
                    .padding(Theme.spacing.medium)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Theme.colors.primary.copy(alpha = 0.15f))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = stringResource(R.string.recommended_service),
                        style = Theme.typography.body.small.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            letterSpacing = 0.5.sp
                        ),
                        color = Theme.colors.primary
                    )
                }

                Spacer(modifier = Modifier.height(Theme.spacing.small))

                Text(
                    text = serviceData.title,
                    style = Theme.typography.title.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    ),
                    color = Theme.colors.primaryFont
                )

                Spacer(modifier = Modifier.height(Theme.spacing.extraSmall))

                Text(
                    text = serviceData.subtitle,
                    style = Theme.typography.body.medium.copy(
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    ),
                    color = Theme.colors.secondaryFont
                )

                Spacer(modifier = Modifier.height(Theme.spacing.medium))

                PrimaryButton(
                    caption = stringResource(R.string.book_now),
                    onClick = onBookNowClick,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(Theme.spacing.small))

                SecondaryButton(
                    caption = stringResource(R.string.view_service),
                    onClick = onViewServiceClick,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun ChatInputBar(
    inputText: String,
    isLoading: Boolean = false,
    onInputChange: (String) -> Unit,
    onSendClick: () -> Unit
) {
    val canSend = inputText.isNotBlank() && !isLoading

    Surface(
        color = Theme.colors.surface,
        shadowElevation = 8.dp,
        modifier = Modifier
            .fillMaxWidth()
            .imePadding()
            .navigationBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Theme.spacing.medium, vertical = Theme.spacing.small),
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
                        enabled = !isLoading,
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

                    Spacer(modifier = Modifier.width(Theme.spacing.small))

                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = stringResource(R.string.voice_input),
                        tint = Theme.colors.secondaryFont,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(Theme.spacing.small))

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(if (canSend) Theme.colors.primary else Theme.colors.primary.copy(alpha = 0.4f))
                    .then(
                        if (canSend) {
                            Modifier.bounceClick(shape = CircleShape, onClick = onSendClick)
                        } else {
                            Modifier
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = RD.drawable.ic_send),
                    contentDescription = stringResource(R.string.send),
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
                        text = "Hello, I'm your AI health assistant. How can I help you today?",
                        isUser = false,
                        type = ChatMessageType.TEXT
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
                        text = "Hello, I'm your AI health assistant. How can I help you today?",
                        isUser = false,
                        type = ChatMessageType.TEXT
                    )
                ),
                inputText = ""
            ),
            onEvent = {}
        )
    }
}
