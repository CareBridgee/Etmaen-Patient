package com.carenest.presentation.ui.servicedetails

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.carenest.designsystem.components.button.ButtonIconPosition
import com.carenest.designsystem.components.button.PrimaryButton
import com.carenest.designsystem.theme.SpTheme
import com.carenest.designsystem.theme.Theme
import com.carenest.designsystem.util.noRippleClickable
import com.carenest.domain.model.ServiceDetailsModel
import com.carenest.presentation.R
import com.carenest.presentation.ui.home.components.HomeShimmerLoading
import com.carenest.presentation.core.mvi.ObserveEffect
import com.carenest.presentation.navigation.HideTopBar
import com.carenest.presentation.ui.servicedetails.components.ServiceChecklistItem
import com.carenest.presentation.ui.servicedetails.components.ServiceInformationNote
import com.carenest.presentation.ui.servicedetails.components.ServiceMetricCard
import com.carenest.presentation.ui.servicedetails.components.ServiceSurfaceCard
import com.carenest.designsystem.R as RD

@Composable
fun ServiceDetailsScreen(
    serviceId : String,
    onNavigateBack: () -> Unit,
    onRequestService: (serviceId: String) -> Unit = {},
    viewModel: ServiceDetailsViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(serviceId) {
        viewModel.onEvent(ServiceDetailsIntent.GetServiceDetails(serviceId))
    }

    ObserveEffect(viewModel.effect) { effect ->
        when (effect) {
            ServiceDetailsEffect.NavigateBack -> onNavigateBack()
            is ServiceDetailsEffect.ShareService -> {
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_SUBJECT, effect.serviceName)
                    
                    val shareMessage = """
                        CareNest Service: ${effect.serviceName}
                        
                        ${effect.description}
                        
                        Download CareNest to book now!
                    """.trimIndent()
                    
                    putExtra(Intent.EXTRA_TEXT, shareMessage)
                }
                context.startActivity(Intent.createChooser(shareIntent, "Share via"))
            }
            is ServiceDetailsEffect.RequestService ->{
                onRequestService(effect.serviceId)
            }
        }
    }

    ServiceDetailsScreenContent(state = state, onEvent = viewModel::onEvent)
}

@Composable
internal fun ServiceDetailsScreenContent(
    state: ServiceDetailsState,
    onEvent: (ServiceDetailsIntent) -> Unit,
) {
    HideTopBar()

    if (state.healthcareService != null) {
        ServiceDetailsLayout(service = state.healthcareService, onEvent = onEvent)
    } else if (state.errorMessage != null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            BasicText(text = state.errorMessage, style = Theme.typography.body.large)
        }
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(Theme.spacing.medium)
        ) {
            HomeShimmerLoading()
        }
    }
}

@Composable
private fun ServiceDetailsLayout(
    service: ServiceDetailsModel,
    onEvent: (ServiceDetailsIntent) -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Theme.colors.backGround,
        contentWindowInsets = WindowInsets(0),
        topBar = {
            ServiceDetailsTopBar(
                onBack = { onEvent(ServiceDetailsIntent.BackClicked) },
                onShare = { onEvent(ServiceDetailsIntent.ShareClicked) },
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Theme.colors.surface)
                    .padding(
                        start = Theme.spacing.space20,
                        top = Theme.spacing.space14,
                        end = Theme.spacing.space20,
                        bottom = Theme.spacing.medium,
                    ),
            ) {
                PrimaryButton(
                    caption = stringResource(R.string.service_details_request_button),
                    onClick = { onEvent(ServiceDetailsIntent.RequestServiceClicked) },
                    iconPainter = rememberVectorPainter(Icons.AutoMirrored.Outlined.ArrowForward),
                    iconPosition = ButtonIconPosition.End,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(
                    start = Theme.spacing.space20,
                    top = Theme.spacing.space12,
                    end = Theme.spacing.space20,
                    bottom = Theme.spacing.large,
                ),
            verticalArrangement = Arrangement.spacedBy(Theme.spacing.space20),
        ) {
            ServiceImageHeader(imageUrl = service.imageUrl)

            Column(verticalArrangement = Arrangement.spacedBy(Theme.spacing.extraSmall)) {
                BasicText(
                    text = service.name,
                    style = Theme.typography.title.copy(
                        color = Theme.colors.primary,
                        fontWeight = FontWeight.SemiBold,
                    ),
                )
                BasicText(
                    text = service.description,
                    style = Theme.typography.body.medium.copy(color = Theme.colors.secondaryFont),
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Theme.spacing.space12),
            ) {
                ServiceMetricCard(
                    label = stringResource(R.string.service_details_average_price),
                    value = "$${service.basePrice}",
                    icon = rememberVectorPainter(Icons.Outlined.Payments),
                    modifier = Modifier
                        .weight(1f)
                        .height(108.dp),
                )
                ServiceMetricCard(
                    label = stringResource(R.string.service_details_duration),
                    value = "${service.estimatedDurationMinutes} min",
                    icon = rememberVectorPainter(Icons.Outlined.Schedule),
                    modifier = Modifier
                        .weight(1f)
                        .height(108.dp),
                )
            }

            ServiceSurfaceCard {
                DetailsSectionTitle(stringResource(R.string.service_details_about_title))
                Spacer(Modifier.height(Theme.spacing.space12))
                BasicText(
                    text = service.description,
                    style = Theme.typography.body.medium.copy(color = Theme.colors.secondaryFont),
                )
            }

            if (service.includedItems.isNotEmpty()) {
                ServiceSurfaceCard {
                    DetailsSectionTitle(stringResource(R.string.service_details_included_title))
                    Spacer(Modifier.height(Theme.spacing.medium))
                    service.includedItems.forEachIndexed { index, item ->
                        ServiceChecklistItem(
                            text = item,
                            checkIcon = painterResource(RD.drawable.ic_check),
                        )
                        if (index != service.includedItems.lastIndex) {
                            Spacer(Modifier.height(Theme.spacing.space12))
                        }
                    }
                }
            }

            if (service.preparationNote.isNotBlank()) {
                ServiceInformationNote(
                    text = service.preparationNote,
                    infoIcon = painterResource(RD.drawable.ic_info),
                )
            }
        }
    }
}

@Composable
private fun ServiceDetailsTopBar(
    onBack: () -> Unit,
    onShare: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Theme.colors.surface)
            .statusBarsPadding()
            .height(58.dp)
            .padding(horizontal = Theme.spacing.space20),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TopBarAction(
            icon = rememberVectorPainter(Icons.AutoMirrored.Outlined.ArrowBack),
            contentDescription = stringResource(R.string.service_details_back),
            onClick = onBack,
        )
        BasicText(
            text = stringResource(R.string.service_details_title),
            style = Theme.typography.body.large.copy(
                color = Theme.colors.primary,
                fontWeight = FontWeight.SemiBold,
            ),
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = Theme.spacing.medium),
        )
        TopBarAction(
            icon = rememberVectorPainter(Icons.Outlined.Share),
            contentDescription = stringResource(R.string.service_details_share),
            onClick = onShare,
        )
    }
}

@Composable
private fun TopBarAction(
    icon: Painter,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(Theme.colors.cardBackground)
            .noRippleClickable(onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = icon,
            contentDescription = contentDescription,
            tint = Theme.colors.secondaryFont,
            modifier = Modifier.size(22.dp),
        )
    }
}

@Composable
private fun ServiceImageHeader(imageUrl: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .clip(Theme.shapes.large)
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFFE9E8FF),
                        Theme.colors.surfaceVariant,
                        Theme.colors.hint.copy(alpha = 0.7f),
                    ),
                ),
            ),
    ) {
        if (imageUrl.isNotEmpty()) {
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Icon(
                imageVector = Icons.Outlined.Image,
                contentDescription = null,
                tint = Theme.colors.primaryVariant.copy(alpha = 0.75f),
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(38.dp),
            )
        }
        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(Theme.spacing.large)
                .clip(CircleShape)
                .background(Theme.colors.primary)
                .padding(horizontal = Theme.spacing.space14, vertical = Theme.spacing.small),
            horizontalArrangement = Arrangement.spacedBy(Theme.spacing.space6),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = painterResource(RD.drawable.ic_work),
                contentDescription = null,
                tint = Theme.colors.onPrimary,
                modifier = Modifier.size(16.dp),
            )
            BasicText(
                text = stringResource(R.string.service_details_clinical_grade),
                style = Theme.typography.body.small.copy(
                    color = Theme.colors.onPrimary,
                    fontWeight = FontWeight.Medium,
                ),
            )
        }
    }
}

@Composable
private fun DetailsSectionTitle(text: String) {
    BasicText(
        text = text,
        style = Theme.typography.body.large.copy(
            color = Theme.colors.primary,
            fontWeight = FontWeight.SemiBold,
        ),
    )
}

@Preview(showBackground = true, widthDp = 390, heightDp = 900)
@Composable
private fun ServiceDetailsScreenPreview() {
    SpTheme(isDarkTheme = false, languageCode = "en") {
        ServiceDetailsScreenContent(state = ServiceDetailsState(), onEvent = {})
    }
}
