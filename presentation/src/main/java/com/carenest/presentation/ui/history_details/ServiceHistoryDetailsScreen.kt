package com.carenest.presentation.ui.history_details

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.carenest.designsystem.theme.SpTheme
import com.carenest.designsystem.theme.Theme
import com.carenest.domain.model.history.ServiceHistory
import com.carenest.presentation.R
import com.carenest.presentation.core.mvi.ObserveEffect
import com.carenest.presentation.navigation.ScreenTopBar
import com.carenest.designsystem.R as RD

@Composable
fun ServiceHistoryDetailsScreen(
    requestId: String,
    onNavigateBack: () -> Unit,
    viewModel: ServiceHistoryDetailsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(requestId) {
        viewModel.onEvent(ServiceHistoryDetailsIntent.LoadDetails(requestId))
    }

    ObserveEffect(viewModel.effect) { effect ->
        when (effect) {
            ServiceHistoryDetailsEffect.NavigateBack -> onNavigateBack()
        }
    }

    ScreenTopBar(
        title = stringResource(R.string.history_details_title),
        onLeadingClick = { viewModel.onEvent(ServiceHistoryDetailsIntent.BackClicked) }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.colors.backGround)
            .statusBarsPadding()
            .padding(top = Theme.size.large)
    ) {
        if (state.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = Theme.colors.primary
            )
        } else if (state.error != null) {
            Text(
                text = state.error!!,
                style = Theme.typography.body.medium,
                modifier = Modifier.align(Alignment.Center),
                color = Theme.colors.error
            )
        } else {
            state.serviceHistory?.let { history ->
                ServiceHistoryDetailsContent(history)
            }
        }
    }
}

@Composable
private fun ServiceHistoryDetailsContent(history: ServiceHistory) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Theme.spacing.space20, vertical = Theme.spacing.medium),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing.large)
    ) {
        HeaderSection(history)

        InfoCard(title = stringResource(R.string.history_section_visit)) {
            DetailRow(
                label = stringResource(R.string.history_label_service),
                value = history.serviceName,
                icon = RD.drawable.ic_bottom_nav_services
            )
            DetailRow(
                label = stringResource(R.string.history_label_date),
                value = history.preferredDate,
                icon = RD.drawable.ic_time
            )
            DetailRow(
                label = stringResource(R.string.history_label_time),
                value = "${history.preferredTime.hour}:${history.preferredTime.minute.toString().padStart(2, '0')}",
                icon = RD.drawable.ic_time
            )
        }

        InfoCard(title = stringResource(R.string.history_section_provider)) {
            NurseSection(history)
        }

        if (history.serviceDescription.isNotBlank()) {
            InfoCard(title = stringResource(R.string.history_label_description)) {
                Text(
                    text = history.serviceDescription,
                    style = Theme.typography.body.medium.copy(
                        color = Theme.colors.secondaryFont,
                        lineHeight = 22.sp
                    ),
                    modifier = Modifier.padding(vertical = Theme.spacing.extraSmall)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(Theme.spacing.large))
    }
}

@Composable
private fun HeaderSection(history: ServiceHistory) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(Theme.size.large + Theme.spacing.medium)
                .clip(CircleShape)
                .background(Theme.colors.primary.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = RD.drawable.ic_bottom_nav_services),
                contentDescription = null,
                tint = Theme.colors.primary,
                modifier = Modifier.size(Theme.size.medium - Theme.spacing.small)
            )
        }

        Spacer(modifier = Modifier.height(Theme.spacing.medium))

        Text(
            text = history.serviceName,
            style = Theme.typography.displayMedium.copy(
                textAlign = TextAlign.Center
            ),
            color = Theme.colors.primaryFont
        )

        Spacer(modifier = Modifier.height(Theme.spacing.small))

        StatusBadge(status = history.status)
    }
}

@Composable
private fun StatusBadge(status: String) {
    val (backgroundColor, contentColor) = when (status.uppercase()) {
        "COMPLETED" -> Theme.colors.successContainer to Theme.colors.onSuccessContainer
        "CONFIRMED" -> Theme.colors.processingContainer to Theme.colors.onProcessingContainer
        "PENDING" -> Theme.colors.warningContainer to Theme.colors.onWarningContainer
        else -> Theme.colors.primary.copy(alpha = 0.1f) to Theme.colors.primary
    }

    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(backgroundColor)
            .padding(horizontal = Theme.spacing.medium, vertical = Theme.spacing.space6)
    ) {
        Text(
            text = status,
            style = Theme.typography.body.small.copy(
                fontWeight = FontWeight.Bold,
            ),
            color = contentColor
        )
    }
}

@Composable
private fun InfoCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = Theme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = Theme.colors.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(Theme.spacing.large),
            verticalArrangement = Arrangement.spacedBy(Theme.spacing.medium)
        ) {
            Text(
                text = title.uppercase(),
                style = Theme.typography.hint.small.copy(
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.2.sp,
                ),
                color = Theme.colors.hint
            )
            content()
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
    icon: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing.medium)
    ) {
        Box(
            modifier = Modifier
                .size(Theme.size.medium - Theme.spacing.small)
                .clip(Theme.shapes.medium)
                .background(Theme.colors.backGround),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                tint = Theme.colors.primary,
                modifier = Modifier.size(Theme.size.iconMedium - Theme.spacing.extraSmall)
            )
        }
        Column {
            Text(
                text = label,
                style = Theme.typography.body.small.copy(
                    color = Theme.colors.hint,
                )
            )
            Text(
                text = value,
                style = Theme.typography.body.medium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Theme.colors.primaryFont
                )
            )
        }
    }
}

@Composable
private fun NurseSection(history: ServiceHistory) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing.medium)
    ) {
        Image(
            painter = painterResource(id = RD.drawable.nurse_image),
            contentDescription = null,
            modifier = Modifier
                .size(Theme.size.large - Theme.spacing.small)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        Column {
            Text(
                text = history.nurseName ?: stringResource(R.string.history_value_unassigned),
                style = Theme.typography.body.medium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Theme.colors.primaryFont
                )
            )
            if (history.nurseId != null) {
                Text(
                    text = stringResource(R.string.history_provider_role),
                    style = Theme.typography.body.small.copy(color = Theme.colors.hint)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ServiceHistoryDetailsPreview() {
    val fakeHistory = ServiceHistory(
        serviceRequestId = "123",
        serviceTypeId = "type_001",
        serviceName = "General Nursing Care",
        serviceDescription = "Comprehensive home nursing care including vital signs monitoring and medication administration.",
        preferredDate = "2026-08-06",
        preferredTime = com.carenest.domain.model.history.PreferredTime(
            hour = 14,
            minute = 30
        ),
        status = "COMPLETED",
        nurseId = "nurse_789",
        nurseName = "Sarah Jenkins",
        createdAt = "2026-08-05T10:00:00Z",
        updatedAt = "2026-08-06T16:00:00Z"
    )

    SpTheme {
        Box(modifier = Modifier.fillMaxSize().background(Theme.colors.backGround)) {
            ServiceHistoryDetailsContent(history = fakeHistory)
        }
    }
}