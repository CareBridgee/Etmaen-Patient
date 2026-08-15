package com.carenest.presentation.ui.servicelist

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.SupportAgent
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.carenest.designsystem.components.emptystate.EmptyState
import com.carenest.designsystem.components.textfield.CustomTextField
import com.carenest.designsystem.theme.SpTheme
import com.carenest.designsystem.theme.Theme
import com.carenest.presentation.R
import com.carenest.presentation.core.mvi.ObserveEffect
import com.carenest.presentation.navigation.HideTopBar
import com.carenest.presentation.ui.components.rememberTimeBasedGreeting
import com.carenest.designsystem.R as RD
import com.carenest.presentation.ui.servicelist.components.ServicesShimmerLoading
import com.carenest.domain.model.home.HealthcareService
import com.carenest.presentation.ui.servicelist.components.ServiceCategoryCard

@Composable
fun ServicesScreen(
    onNavigateToDetails: (String) -> Unit,
    onNavigateToAIChat: () -> Unit = {},
    onOpenFilters: () -> Unit = {},
    viewModel: ServicesViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveEffect(viewModel.effect) { effect ->
        when (effect) {
            is ServicesEffect.NavigateToDetails -> onNavigateToDetails(effect.serviceId)
            ServicesEffect.OpenFilters -> onOpenFilters()
            ServicesEffect.OpenCareCoordinator -> onNavigateToAIChat()
        }
    }

    ServicesScreenContent(state = state, onEvent = viewModel::onEvent)
}

@Composable
internal fun ServicesScreenContent(
    state: ServicesState,
    onEvent: (ServicesIntent) -> Unit,
) {
    HideTopBar()

    val listState = rememberLazyListState()

    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.colors.backGround)
            .statusBarsPadding(),
        contentPadding = PaddingValues(
            start = Theme.spacing.space20,
            top = Theme.spacing.small,
            end = Theme.spacing.space20,
            bottom = Theme.spacing.medium,
        ),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing.medium),
    ) {
        item { GreetingHeader(userName = state.userName, userImage = state.userImageUrl) }
        item {
            CustomTextField(
                text = state.searchQuery,
                onTextChange = { onEvent(ServicesIntent.SearchQueryChanged(it)) },
                hint = stringResource(R.string.services_search_hint),
                leadingIcon = rememberVectorPainter(Icons.Outlined.Search),
                fieldHeight = 54.dp,
                shape = Theme.shapes.large,
                borderColor = Color.Transparent,
                onFocusBorderColor = Theme.colors.primaryVariant,
                containerColor = Theme.colors.cardBackground,
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        item {
            Column {
                BasicText(
                    text = stringResource(R.string.services_categories_title),
                    style = Theme.typography.body.large.copy(
                        color = Theme.colors.primaryFont,
                        fontWeight = FontWeight.Medium,
                    ),
                )
                BasicText(
                    text = stringResource(R.string.services_categories_subtitle),
                    style = Theme.typography.body.small.copy(color = Theme.colors.secondaryFont),
                )
                Spacer(Modifier.height(Theme.spacing.space12))

                if (state.isLoading) {
                    ServicesShimmerLoading()
                } else if (state.filteredServices.isEmpty() && state.searchQuery.isNotBlank()) {
                    EmptyState(
                        title = stringResource(R.string.home_services_search_empty_title),
                        description = stringResource(R.string.home_services_search_empty_desc),
                        accentColor = Theme.colors.primary,
                        modifier = Modifier.padding(vertical = 32.dp)
                    )
                } else if (state.filteredServices.isEmpty()) {
                    EmptyState(
                        title = stringResource(R.string.home_services_empty_title),
                        description = stringResource(R.string.home_services_empty_desc),
                        accentColor = Theme.colors.primary,
                        modifier = Modifier.padding(vertical = 32.dp)
                    )
                } else {
                    CategoryGrid(
                        services = state.filteredServices,
                        onCategoryClick = { onEvent(ServicesIntent.CategoryClicked(it.id)) },
                    )
                }
            }
        }
        item { Spacer(Modifier.height(Theme.spacing.extraLarge)) }
        item {
            CareCoordinatorCard(onClick = { onEvent(ServicesIntent.ConsultationClicked) })
        }
    }
}

@Composable
private fun GreetingHeader(userName: String, userImage: String?) {
    val greeting = rememberTimeBasedGreeting(userName)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing.space12),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .border(1.dp, Theme.colors.primaryVariant, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            AsyncImage(
                model = userImage,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                placeholder = painterResource(RD.drawable.ic_profile)
            )
        }
        Column {
            BasicText(
                text = greeting,
                style = Theme.typography.body.medium.copy(
                    color = Theme.colors.primary,
                    fontWeight = FontWeight.Medium,
                ),
            )
            BasicText(
                text = stringResource(R.string.services_greeting_subtitle),
                style = Theme.typography.body.small.copy(color = Theme.colors.secondaryFont),
            )
        }
    }
}

@Composable
private fun CategoryGrid(
    services: List<HealthcareService>,
    onCategoryClick: (HealthcareService) -> Unit,
) {
    val left = mutableListOf<Pair<HealthcareService, Dp>>()
    val right = mutableListOf<Pair<HealthcareService, Dp>>()

    services.forEachIndexed { index, service ->
        // Create masonry effect by varying heights
        val height = if ((index % 4 == 0) || (index % 4 == 3)) 198.dp else 116.dp
        if (index % 2 == 0) {
            left.add(service to height)
        } else {
            right.add(service to height)
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing.space12),
        verticalAlignment = Alignment.Top,
    ) {
        CategoryColumn(left, onCategoryClick, Modifier.weight(1f))
        CategoryColumn(right, onCategoryClick, Modifier.weight(1f))
    }
}

@Composable
private fun CategoryColumn(
    items: List<Pair<HealthcareService, Dp>>,
    onCategoryClick: (HealthcareService) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(Theme.spacing.space12)) {
        items.forEach { (service, height) ->
            val iconRes = when (service.iconResName) {
                "ic_syringe" -> RD.drawable.ic_syringe
                "ic_pill" -> RD.drawable.ic_pill
                "ic_physical_therapy" -> RD.drawable.ic_physical_therapy
                "ic_services" -> RD.drawable.ic_services
                else -> RD.drawable.ic_heart_beat
            }
            ServiceCategoryCard(
                title = service.name,
                subtitle = "Professional care", // Fallback generic subtitle
                icon = painterResource(id = iconRes),
                height = height,
                onClick = { onCategoryClick(service) },
            )
        }
    }
}

@Composable
private fun CareCoordinatorCard(onClick: () -> Unit) {
    val isDarkTheme = Theme.colors.backGround.luminance() < 0.5f
    val containerColor = if (isDarkTheme) {
        Theme.colors.primaryContainer
    } else {
        Color(0xFF8FE8F0)
    }
    val illustrationColor = if (isDarkTheme) {
        Theme.colors.onPrimaryContainer.copy(alpha = 0.3f)
    } else {
        Theme.colors.primaryVariant.copy(alpha = 0.14f)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(208.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(containerColor)
            .clickable(onClick = onClick)
    ) {
        Icon(
            imageVector = Icons.Outlined.SupportAgent,
            contentDescription = null,
            tint = illustrationColor,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 24.dp, y = 18.dp)
                .size(128.dp),
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = Theme.spacing.large,
                    top = Theme.spacing.extraLarge,
                    end = Theme.spacing.large,
                    bottom = Theme.spacing.large,
                ),
        ) {
            BasicText(
                text = stringResource(R.string.services_consultation_title),
                style = Theme.typography.title.copy(
                    color = Theme.colors.primaryFont,
                    fontWeight = FontWeight.SemiBold,
                ),
            )
            Spacer(Modifier.height(Theme.spacing.space12))
            BasicText(
                text = stringResource(R.string.services_consultation_subtitle),
                style = Theme.typography.body.medium.copy(color = Theme.colors.secondaryFont),
                modifier = Modifier.fillMaxWidth(0.9f),
            )
            Spacer(Modifier.height(Theme.spacing.space14))
            Box(
                modifier = Modifier
                    .height(48.dp)
                    .clip(Theme.shapes.large)
                    .background(Theme.colors.primary)
                    .padding(horizontal = Theme.spacing.space20),
                contentAlignment = Alignment.Center,
            ) {
                BasicText(
                    text = stringResource(R.string.services_consultation_button),
                    style = Theme.typography.body.medium.copy(color = Theme.colors.onPrimary),
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 1100)
@Composable
private fun ServicesScreenPreview() {
    SpTheme(isDarkTheme = false, languageCode = "en") {
        ServicesScreenContent(state = ServicesState(), onEvent = {})
    }
}
