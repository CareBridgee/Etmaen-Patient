package com.carenest.presentation.ui.services.list

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
import androidx.compose.material.icons.outlined.AccessibilityNew
import androidx.compose.material.icons.outlined.ChildCare
import androidx.compose.material.icons.outlined.Healing
import androidx.compose.material.icons.outlined.MedicalServices
import androidx.compose.material.icons.outlined.MonitorHeart
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.SupportAgent
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material.icons.outlined.Vaccines
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.carenest.designsystem.components.textfield.CustomTextField
import com.carenest.designsystem.theme.SpTheme
import com.carenest.designsystem.theme.Theme
import com.carenest.presentation.R
import com.carenest.presentation.core.mvi.ObserveEffect
import com.carenest.presentation.navigation.HideTopBar
import com.carenest.presentation.ui.services.components.ServiceCategoryCard
import com.carenest.designsystem.R as RD

private data class CategoryPresentation(
    val category: ServiceCategory,
    val title: Int,
    val subtitle: Int,
    val icon: ImageVector,
)

@Composable
fun ServicesScreen(
    onNavigateToDetails: (ServiceCategory) -> Unit,
    onOpenFilters: () -> Unit = {},
    onOpenCareCoordinator: () -> Unit = {},
    viewModel: ServicesViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveEffect(viewModel.effect) { effect ->
        when (effect) {
            is ServicesEffect.NavigateToDetails -> onNavigateToDetails(effect.category)
            ServicesEffect.OpenFilters -> onOpenFilters()
            ServicesEffect.OpenCareCoordinator -> onOpenCareCoordinator()
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

    val categories = listOf(
        CategoryPresentation(ServiceCategory.GENERAL_NURSING, R.string.services_general_nursing, R.string.services_general_nursing_subtitle, Icons.Outlined.MedicalServices),
        CategoryPresentation(ServiceCategory.INJECTION, R.string.services_injection, R.string.services_injection_subtitle, Icons.Outlined.Vaccines),
        CategoryPresentation(ServiceCategory.PHYSICAL_THERAPY, R.string.services_physical_therapy, R.string.services_physical_therapy_subtitle, Icons.Outlined.Healing),
        CategoryPresentation(ServiceCategory.WOUND_CARE, R.string.services_wound_care, R.string.services_wound_care_subtitle, Icons.Outlined.MonitorHeart),
        CategoryPresentation(ServiceCategory.POST_NATAL, R.string.services_post_natal, R.string.services_post_natal_subtitle, Icons.Outlined.ChildCare),
        CategoryPresentation(ServiceCategory.ELDERLY_CARE, R.string.services_elderly_care, R.string.services_elderly_care_subtitle, Icons.Outlined.AccessibilityNew),
        CategoryPresentation(ServiceCategory.IV_DRIP, R.string.services_iv_drip, R.string.services_iv_drip_subtitle, Icons.Outlined.MedicalServices),
        CategoryPresentation(ServiceCategory.VACCINATIONS, R.string.services_vaccinations, R.string.services_vaccinations_subtitle, Icons.Outlined.Vaccines),
    ).associateBy(CategoryPresentation::category)
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
            bottom = 112.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing.medium),
    ) {
        item { GreetingHeader() }
        item {
            CustomTextField(
                text = state.searchQuery,
                onTextChange = { onEvent(ServicesIntent.SearchQueryChanged(it)) },
                hint = stringResource(R.string.services_search_hint),
                leadingIcon = rememberVectorPainter(Icons.Outlined.Search),
                trailingIcon = rememberVectorPainter(Icons.Outlined.Tune),
                onClickTrailingIcon = { onEvent(ServicesIntent.FilterClicked) },
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
                CategoryGrid(
                    categories = categories,
                    onCategoryClick = { onEvent(ServicesIntent.CategoryClicked(it)) },
                )
            }
        }
        item { Spacer(Modifier.height(Theme.spacing.extraLarge)) }
        item {
            ChronicCareCard(onClick = { onEvent(ServicesIntent.ChronicCareClicked) })
        }
        item {
            CareCoordinatorCard(onClick = { onEvent(ServicesIntent.ConsultationClicked) })
        }
    }
}

@Composable
private fun GreetingHeader() {
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
            Box(
                Modifier
                    .size(4.dp)
                    .clip(CircleShape)
                    .background(Theme.colors.primaryVariant),
            )
        }
        Column {
            BasicText(
                text = stringResource(R.string.services_greeting),
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
    categories: Map<ServiceCategory, CategoryPresentation>,
    onCategoryClick: (ServiceCategory) -> Unit,
) {
    val left = listOf(
        ServiceCategory.GENERAL_NURSING to 198.dp,
        ServiceCategory.WOUND_CARE to 116.dp,
        ServiceCategory.ELDERLY_CARE to 116.dp,
        ServiceCategory.VACCINATIONS to 116.dp,
    )
    val right = listOf(
        ServiceCategory.INJECTION to 116.dp,
        ServiceCategory.PHYSICAL_THERAPY to 116.dp,
        ServiceCategory.POST_NATAL to 116.dp,
        ServiceCategory.IV_DRIP to 116.dp,
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing.space12),
        verticalAlignment = Alignment.Top,
    ) {
        CategoryColumn(left, categories, onCategoryClick, Modifier.weight(1f))
        CategoryColumn(right, categories, onCategoryClick, Modifier.weight(1f))
    }
}

@Composable
private fun CategoryColumn(
    items: List<Pair<ServiceCategory, androidx.compose.ui.unit.Dp>>,
    categories: Map<ServiceCategory, CategoryPresentation>,
    onCategoryClick: (ServiceCategory) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(Theme.spacing.space12)) {
        items.forEach { (category, height) ->
            val presentation = requireNotNull(categories[category])
            ServiceCategoryCard(
                title = stringResource(presentation.title),
                subtitle = stringResource(presentation.subtitle),
                icon = rememberVectorPainter(presentation.icon),
                height = height,
                onClick = { onCategoryClick(category) },
            )
        }
    }
}

@Composable
private fun ChronicCareCard(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(118.dp)
            .clip(Theme.shapes.large)
            .background(Theme.colors.primaryVariant)
            .clickable(onClick = onClick)
            .padding(Theme.spacing.large),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(Theme.shapes.large)
                .background(Theme.colors.onPrimary.copy(alpha = 0.18f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(RD.drawable.ic_heart_beat),
                contentDescription = null,
                tint = Theme.colors.onPrimary,
                modifier = Modifier.size(26.dp),
            )
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = Theme.spacing.medium),
        ) {
            BasicText(
                text = stringResource(R.string.services_chronic_care_title),
                style = Theme.typography.body.large.copy(
                    color = Theme.colors.onPrimary,
                    fontWeight = FontWeight.Medium,
                ),
            )
            BasicText(
                text = stringResource(R.string.services_chronic_care_subtitle),
                style = Theme.typography.body.medium.copy(color = Theme.colors.onPrimary.copy(alpha = 0.8f)),
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy((-6).dp)) {
            listOf("S", "D").forEach { initial ->
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Theme.colors.surface)
                        .border(1.dp, Theme.colors.primaryVariant, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    BasicText(
                        text = initial,
                        style = Theme.typography.body.small.copy(
                            color = Theme.colors.primary,
                            fontWeight = FontWeight.SemiBold,
                        ),
                    )
                }
            }
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
