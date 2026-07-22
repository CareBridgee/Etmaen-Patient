package com.carenest.presentation.ui.aichat.chat

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.carenest.designsystem.components.button.PrimaryButton
import com.carenest.designsystem.components.button.SecondaryButton
import com.carenest.designsystem.theme.Theme
import com.carenest.presentation.R
import com.carenest.designsystem.R as RD
import com.carenest.presentation.core.mvi.ObserveEffect
import com.carenest.presentation.navigation.HideTopBar

@Composable
fun AIChatScreen(
    patientId: String,
    onNavigateBack: () -> Unit,
    onNavigateToBookings: () -> Unit,
    onNavigateToServiceDetails: (String) -> Unit,
    viewModel: AIChatViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    ObserveEffect(viewModel.effect) { effect ->
        when (effect) {
            AIChatEffect.NavigateBack -> onNavigateBack()
            AIChatEffect.NavigateToBookings -> onNavigateToBookings()
            is AIChatEffect.NavigateToServiceDetails -> onNavigateToServiceDetails(effect.categoryId)
        }
    }

    HideTopBar()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.colors.backGround)
            .statusBarsPadding()
    ) {
        AIChatTopBar(onBackClick = { viewModel.onEvent(AIChatEvent.OnBackClicked) })

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(state.messages) { message ->
                if (message.type == ChatMessageType.TEXT) {
                    ChatBubble(message = message)
                } else if (message.type == ChatMessageType.SERVICE_RECOMMENDATION && message.serviceData != null) {
                    ServiceRecommendationBubble(
                        serviceData = message.serviceData,
                        onBookNowClick = { viewModel.onEvent(AIChatEvent.OnBookNowClicked) },
                        onViewServiceClick = { viewModel.onEvent(AIChatEvent.OnViewServiceClicked(message.serviceData.categoryId)) }
                    )
                }
            }
        }

        ChatInputBar(
            inputText = state.inputText,
            onInputChange = { viewModel.onEvent(AIChatEvent.OnInputTextChanged(it)) },
            onSendClick = { viewModel.onEvent(AIChatEvent.OnSendMessage) }
        )
    }
}

@Composable
fun AIChatTopBar(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Theme.colors.surface) 
            .padding(vertical = 12.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .clickable { onBackClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = RD.drawable.ic_arrow_back),
                contentDescription = "Back",
                tint = Theme.colors.primaryFont
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.ai_health_assistant),
                style = Theme.typography.body.large.copy(fontWeight = FontWeight.Bold),
                color = Theme.colors.primaryFont
            )
            Text(
                text = stringResource(R.string.online_ready),
                style = Theme.typography.body.medium,
                color = Theme.colors.secondaryFont
            )
        }

        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Theme.colors.primary.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = RD.drawable.ic_shield_check),
                contentDescription = "Verified",
                tint = Theme.colors.primary,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!message.isUser) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Theme.colors.primary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = RD.drawable.ic_ai_sparkles),
                    contentDescription = "AI",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Box(
            modifier = Modifier
                .weight(1f, fill = false)
                .clip(
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (message.isUser) 16.dp else 4.dp,
                        bottomEnd = if (message.isUser) 4.dp else 16.dp
                    )
                )
                .background(if (message.isUser) Theme.colors.primary else Theme.colors.surface)
                .border(
                    width = if (message.isUser) 0.dp else 1.dp,
                    color = if (message.isUser) Color.Transparent else Theme.colors.surfaceVariant,
                    shape = RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (message.isUser) 16.dp else 4.dp,
                        bottomEnd = if (message.isUser) 4.dp else 16.dp
                    )
                )
                .padding(12.dp)
        ) {
            Text(
                text = message.text,
                style = Theme.typography.body.large,
                color = if (message.isUser) Color.White else Theme.colors.primaryFont
            )
        }
    }
}

@Composable
fun ServiceRecommendationBubble(
    serviceData: ServiceRecommendationData,
    onBookNowClick: () -> Unit,
    onViewServiceClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        // AI Icon spacer
        Spacer(modifier = Modifier.width(40.dp)) // 32dp icon + 8dp spacing

        Column(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(16.dp))
                .background(Theme.colors.surface)
                .border(1.dp, Theme.colors.surfaceVariant, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Theme.colors.surfaceVariant)
            ) {
                Image(
                    painter = painterResource(id = RD.drawable.img_placeholder),
                    contentDescription = "Service Image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                // Recommended Badge
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Theme.colors.secondary.copy(alpha = 0.9f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = stringResource(R.string.recommended_service),
                        style = Theme.typography.hint.small.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = serviceData.title,
                style = Theme.typography.body.large.copy(fontWeight = FontWeight.Bold),
                color = Theme.colors.primaryFont
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = serviceData.subtitle,
                style = Theme.typography.body.medium,
                color = Theme.colors.secondaryFont
            )
            
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = RD.drawable.ic_star),
                        contentDescription = "Rating",
                        tint = Color(0xFFFFC107),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "4.9 (120+)",
                        style = Theme.typography.body.small,
                        color = Theme.colors.secondaryFont
                    )
                }
                Text(
                    text = "•",
                    style = Theme.typography.body.small,
                    color = Theme.colors.secondaryFont
                )
                Text(
                    text = serviceData.price,
                    style = Theme.typography.body.small.copy(fontWeight = FontWeight.Bold),
                    color = Theme.colors.primary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PrimaryButton(
                    caption = stringResource(R.string.book_now),
                    onClick = onBookNowClick,
                    modifier = Modifier.weight(1f).height(48.dp)
                )
                SecondaryButton(
                    caption = stringResource(R.string.view_service),
                    onClick = onViewServiceClick,
                    modifier = Modifier.weight(1f).height(48.dp)
                )
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
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Theme.colors.surface)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(24.dp))
                .background(Theme.colors.surfaceVariant)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                BasicTextField(
                    value = inputText,
                    onValueChange = onInputChange,
                    modifier = Modifier.weight(1f),
                    textStyle = Theme.typography.body.large.copy(color = Theme.colors.primaryFont),
                    decorationBox = { innerTextField ->
                        if (inputText.isEmpty()) {
                            Text(
                                text = stringResource(R.string.ask_health_profile),
                                style = Theme.typography.body.large,
                                color = Theme.colors.secondaryFont
                            )
                        }
                        innerTextField()
                    }
                )
                
                Icon(
                    painter = painterResource(id = RD.drawable.ic_microphone),
                    contentDescription = "Mic",
                    tint = Theme.colors.secondaryFont,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(if (inputText.isNotBlank()) Theme.colors.primary else Theme.colors.primary.copy(alpha = 0.5f))
                .clickable(enabled = inputText.isNotBlank()) { onSendClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = RD.drawable.ic_send),
                contentDescription = "Send",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
