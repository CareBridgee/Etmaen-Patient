package com.carenest.presentation.ui.history_details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
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
            .padding(top = 80.dp) // Space for TopBar
    ) {
        if (state.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = Theme.colors.primary
            )
        } else if (state.error != null) {
            Text(
                text = state.error!!,
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
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Status Card
        DetailItem(
            label = "Status",
            value = history.status,
            icon = RD.drawable.ic_check_white
        )

        // Service Info
        DetailItem(
            label = "Service",
            value = history.serviceName,
            icon = RD.drawable.ic_bottom_nav_services
        )

        DetailItem(
            label = "Description",
            value = history.serviceDescription,
            icon = null
        )

        // Date & Time
        DetailItem(
            label = "Date",
            value = history.preferredDate,
            icon = RD.drawable.ic_time
        )

        DetailItem(
            label = "Time",
            value = "${history.preferredTime.hour}:${history.preferredTime.minute.toString().padStart(2, '0')}",
            icon = RD.drawable.ic_time
        )

        // Nurse Info
        DetailItem(
            label = "Nurse",
            value = history.nurseName ?: "Unassigned",
            icon = RD.drawable.ic_profile
        )
    }
}

@Composable
private fun DetailItem(
    label: String,
    value: String,
    icon: Int?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Theme.colors.surface)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (icon != null) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                tint = Theme.colors.primary,
                modifier = Modifier.size(24.dp)
            )
        }
        Column {
            Text(
                text = label,
                style = Theme.typography.body.small.copy(
                    color = Theme.colors.secondaryFont,
                    fontWeight = FontWeight.Medium
                )
            )
            Text(
                text = value,
                style = Theme.typography.body.medium.copy(
                    color = Theme.colors.primaryFont,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}
