package com.carenest.presentation.ui.profile.allergies

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.HealthAndSafety
import androidx.compose.material.icons.outlined.Medication
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.carenest.designsystem.components.textfield.CustomTextField
import com.carenest.designsystem.theme.SpTheme
import com.carenest.designsystem.theme.Theme
import com.carenest.presentation.navigation.ScreenTopBar
import com.carenest.presentation.R
import com.carenest.presentation.core.mvi.ObserveEffect
import com.carenest.presentation.ui.profile.components.ProfileProgressIndicator
import com.carenest.presentation.ui.profile.components.ProfileScreenNavigation

@Composable
fun AllergiesScreen(
    onNavigateBack: () -> Unit,
    onContinueToRemainingProfile: () -> Unit,
    viewModel: AllergiesViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    ObserveEffect(viewModel.effect) { effect ->
        when (effect) {
            AllergiesEffect.NavigateBack -> onNavigateBack()
            AllergiesEffect.ContinueToRemainingProfile -> onContinueToRemainingProfile()
        }
    }
    AllergiesScreenContent(state = state, onEvent = viewModel::onEvent)
}

@Composable
internal fun AllergiesScreenContent(
    state: AllergiesState,
    onEvent: (AllergiesIntent) -> Unit
) {
    ScreenTopBar(
        title = stringResource(R.string.welcome_topbar_title),
        showLeadingIcon = true,
        onLeadingClick = { onEvent(AllergiesIntent.BackClicked) }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.colors.backGround)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(22.dp)
        ) {
            ProfileProgressIndicator(step = 4, title = stringResource(R.string.allergies_progress_title))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                BasicText(
                    text = stringResource(R.string.allergies_title),
                    style = Theme.typography.displayMedium.copy(
                        color = Theme.colors.primaryFont,
                        fontSize = 23.sp
                    )
                )
                BasicText(
                    text = stringResource(R.string.allergies_description),
                    style = Theme.typography.body.medium.copy(
                        color = Theme.colors.secondaryFont,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                )
            }

            NoKnownAllergiesCard(
                checked = state.hasNoKnownAllergies,
                onCheckedChange = { onEvent(AllergiesIntent.NoKnownAllergiesToggled) }
            )

            Column(
                modifier = Modifier.alpha(if (state.hasNoKnownAllergies) 0.45f else 1f),
                verticalArrangement = Arrangement.spacedBy(22.dp)
            ) {
                AllergyCategory(
                    title = stringResource(R.string.allergies_drug_title),
                    icon = Icons.Outlined.Medication,
                    options = stringResource(R.string.allergies_drug_options).split(","),
                    selectedOptions = state.selectedDrugAllergies,
                    enabled = !state.hasNoKnownAllergies,
                    onOptionClick = { onEvent(AllergiesIntent.DrugAllergyToggled(it)) }
                )
                AllergyCategory(
                    title = stringResource(R.string.allergies_food_title),
                    icon = Icons.Outlined.Restaurant,
                    options = stringResource(R.string.allergies_food_options).split(","),
                    selectedOptions = state.selectedFoodAllergies,
                    enabled = !state.hasNoKnownAllergies,
                    onOptionClick = { onEvent(AllergiesIntent.FoodAllergyToggled(it)) }
                )
                CustomTextField(
                    text = state.otherAllergies,
                    onTextChange = { onEvent(AllergiesIntent.OtherAllergiesChanged(it)) },
                    title = stringResource(R.string.allergies_other_title),
                    hint = stringResource(R.string.allergies_other_hint),
                    enabled = !state.hasNoKnownAllergies,
                    fieldHeight = 96.dp,
                    fieldVerticalAlignment = Alignment.Top,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        ProfileScreenNavigation(
            onBack = { onEvent(AllergiesIntent.BackClicked) },
            onContinue = { onEvent(AllergiesIntent.ContinueClicked) }
        )
    }
}

@Composable
private fun NoKnownAllergiesCard(checked: Boolean, onCheckedChange: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Theme.colors.cardBackground)
            .border(1.dp, Theme.colors.onDisable.copy(alpha = 0.45f), RoundedCornerShape(16.dp))
            .clickable(onClick = onCheckedChange)
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Outlined.HealthAndSafety,
            contentDescription = null,
            tint = Theme.colors.primary,
            modifier = Modifier.size(28.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            BasicText(
                text = stringResource(R.string.allergies_none_title),
                style = Theme.typography.title.copy(
                    color = Theme.colors.primaryFont,
                    fontSize = 17.sp
                )
            )
            BasicText(
                text = stringResource(R.string.allergies_none_description),
                style = Theme.typography.body.small.copy(color = Theme.colors.secondaryFont)
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = { onCheckedChange() },
            colors = SwitchDefaults.colors(
                checkedThumbColor = Theme.colors.onPrimary,
                checkedTrackColor = Theme.colors.primary,
                uncheckedThumbColor = Theme.colors.surface,
                uncheckedTrackColor = Theme.colors.disable,
                uncheckedBorderColor = Theme.colors.onDisable
            )
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AllergyCategory(
    title: String,
    icon: ImageVector,
    options: List<String>,
    selectedOptions: Set<String>,
    enabled: Boolean,
    onOptionClick: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Theme.colors.primary,
                modifier = Modifier.size(22.dp)
            )
            BasicText(
                text = title,
                style = Theme.typography.title.copy(
                    color = Theme.colors.primaryFont,
                    fontSize = 18.sp
                )
            )
        }
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            options.forEach { option ->
                AllergyChip(
                    text = option,
                    selected = option in selectedOptions,
                    enabled = enabled,
                    onClick = { onOptionClick(option) }
                )
            }
        }
    }
}

@Composable
private fun AllergyChip(
    text: String,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (selected) Theme.colors.primary else Theme.colors.backGround
    val contentColor = if (selected) Theme.colors.onPrimary else Theme.colors.secondaryFont
    val borderColor = if (selected) Theme.colors.primary else Theme.colors.onDisable

    BasicText(
        text = text,
        modifier = Modifier
            .clip(RoundedCornerShape(100.dp))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(100.dp))
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 9.dp),
        style = Theme.typography.body.small.copy(
            color = contentColor,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp
        )
    )
}

@Preview(showBackground = true)
@Composable
private fun AllergiesScreenPreview() {
    SpTheme {
        AllergiesScreenContent(
            state = AllergiesState(
                selectedDrugAllergies = setOf("Penicillin"),
                selectedFoodAllergies = setOf("Peanuts", "Dairy")
            ),
            onEvent = {}
        )
    }
}
