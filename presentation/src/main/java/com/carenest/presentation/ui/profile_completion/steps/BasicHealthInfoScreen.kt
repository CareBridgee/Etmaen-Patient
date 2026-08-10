package com.carenest.presentation.ui.profile_completion.steps

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.MedicalInformation
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.carenest.designsystem.components.textfield.CustomTextField
import com.carenest.designsystem.theme.SpTheme
import com.carenest.designsystem.theme.Theme
import com.carenest.presentation.R
import com.carenest.presentation.navigation.ScreenTopBar
import com.carenest.presentation.ui.profile_completion.components.ProfileProgressIndicator
import com.carenest.presentation.ui.profile_completion.components.ProfileScreenNavigation

@Composable
fun BasicHealthInfoScreen(
    height: String,
    weight: String,
    bloodType: String,
    heightError: String? = null,
    weightError: String? = null,
    bloodTypeError: String? = null,
    onHeightChange: (String) -> Unit,
    onWeightChange: (String) -> Unit,
    onBloodTypeChange: (String) -> Unit,
    onBack: () -> Unit,
    onContinue: () -> Unit,
    isSubmitting: Boolean = false
) {
    ScreenTopBar(
        title = stringResource(R.string.welcome_topbar_title),
        showLeadingIcon = true,
        onLeadingClick = onBack
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
                .padding(
                    horizontal = Theme.spacing.space20,
                    vertical = Theme.spacing.medium
                ),
            verticalArrangement = Arrangement.spacedBy(Theme.spacing.medium)
        ) {
            ProfileProgressIndicator(
                step = 1,
                title = stringResource(R.string.basic_health_title),
            )

            HealthIntroCard()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Theme.spacing.space12)
            ) {
                MeasurementCard(
                    label = stringResource(R.string.basic_health_height),
                    value = height,
                    unit = stringResource(R.string.basic_health_cm),
                    hint = stringResource(R.string.basic_health_height_hint),
                    onValueChange = onHeightChange,
                    errorMessage = heightError,
                    modifier = Modifier.weight(1f)
                )
                MeasurementCard(
                    label = stringResource(R.string.basic_health_weight),
                    value = weight,
                    unit = stringResource(R.string.basic_health_kg),
                    hint = stringResource(R.string.basic_health_weight_hint),
                    onValueChange = onWeightChange,
                    errorMessage = weightError,
                    modifier = Modifier.weight(1f)
                )
            }

            BloodTypeCard(
                selectedBloodType = bloodType,
                errorMessage = bloodTypeError,
                onBloodTypeSelected = onBloodTypeChange
            )
        }

        ProfileScreenNavigation(
            onBack = onBack,
            onContinue = onContinue,
            showBackButton = true,
            continueEnabled = !isSubmitting,
            isLoading = isSubmitting
        )
    }
}

@Composable
private fun HealthIntroCard() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(5.dp, RoundedCornerShape(22.dp))
            .clip(RoundedCornerShape(22.dp))
            .background(Theme.colors.surface)
            .padding(Theme.spacing.space20),
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing.space14),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Theme.colors.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.MedicalInformation,
                contentDescription = null,
                tint = Theme.colors.primary,
                modifier = Modifier.size(24.dp)
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(Theme.spacing.small)) {
            BasicText(
                text = stringResource(R.string.basic_health_title),
                style = Theme.typography.title.copy(
                    color = Theme.colors.primaryFont,
                    fontSize = 18.sp
                )
            )
            BasicText(
                text = stringResource(R.string.basic_health_description),
                style = Theme.typography.body.medium.copy(
                    color = Theme.colors.secondaryFont,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            )
        }
    }
}

@Composable
private fun MeasurementCard(
    label: String,
    value: String,
    unit: String,
    hint: String,
    onValueChange: (String) -> Unit,
    errorMessage: String? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .shadow(4.dp, RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .background(Theme.colors.surface)
            .padding(Theme.spacing.space18),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing.small)
    ) {
        BasicText(
            text = label,
            style = Theme.typography.body.small.copy(
                color = Theme.colors.hint,
                fontWeight = FontWeight.Medium
            )
        )
        Row(verticalAlignment = Alignment.Top) {
            CustomTextField(
                text = value,
                onTextChange = { onValueChange(it.filter(Char::isDigit).take(3)) },
                hint = hint,
                singleLine = true,
                fieldHeight = 52.dp,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = errorMessage != null,
                errorMessage = errorMessage,
                reserveErrorSpace = true,
                errorSpaceHeight = 36.dp,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.size(Theme.spacing.small))
            BasicText(
                text = unit,
                modifier = Modifier.padding(top = 16.dp),
                style = Theme.typography.body.small.copy(color = Theme.colors.secondaryFont)
            )
        }
    }
}

@Composable
private fun BloodTypeCard(
    selectedBloodType: String,
    errorMessage: String? = null,
    onBloodTypeSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val bloodTypes = stringResource(R.string.basic_health_blood_types).split(",")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(22.dp))
            .clip(RoundedCornerShape(22.dp))
            .background(Theme.colors.surface)
            .padding(Theme.spacing.space20),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing.small)
    ) {
        BasicText(
            text = stringResource(R.string.basic_health_blood_type),
            style = Theme.typography.body.small.copy(
                color = Theme.colors.hint,
                fontWeight = FontWeight.Medium
            )
        )
        Box {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Theme.colors.disable)
                    .then(
                        if (errorMessage != null) Modifier.border(1.dp, Theme.colors.error, RoundedCornerShape(14.dp))
                        else Modifier
                    )
                    .clickable { expanded = true }
                    .padding(horizontal = Theme.spacing.medium),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                BasicText(
                    text = if (selectedBloodType.isBlank()) {
                        stringResource(R.string.basic_health_blood_type_hint)
                    } else {
                        selectedBloodType
                    },
                    style = Theme.typography.body.medium.copy(
                        color = if (selectedBloodType.isBlank()) Theme.colors.hint else Theme.colors.primaryFont,
                        fontSize = 14.sp
                    )
                )
                Icon(
                    imageVector = Icons.Outlined.KeyboardArrowDown,
                    contentDescription = null,
                    tint = Theme.colors.hint
                )
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(
                    color = Theme.colors.surface,
                    shape = RoundedCornerShape(16.dp)
                )
                    .widthIn(min = 180.dp, max = 240.dp)
                    .heightIn(max = 260.dp)
            ) {
                bloodTypes.forEach { bloodType ->
                    DropdownMenuItem(
                        text = {
                            BasicText(
                                text = bloodType,
                                style = Theme.typography.body.medium.copy(
                                    color = if (bloodType == selectedBloodType) {
                                        Theme.colors.primary
                                    } else {
                                        Theme.colors.primaryFont
                                    },
                                    fontWeight = if (bloodType == selectedBloodType) {
                                        FontWeight.SemiBold
                                    } else {
                                        FontWeight.Normal
                                    },
                                    fontSize = 15.sp
                                )
                            )
                        },
                        trailingIcon = if (bloodType == selectedBloodType) {
                            {
                                Icon(
                                    imageVector = Icons.Outlined.Check,
                                    contentDescription = null,
                                    tint = Theme.colors.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        } else {
                            null
                        },
                        modifier = Modifier
                            .height(48.dp)
                            .background(
                                if (bloodType == selectedBloodType) {
                                    Theme.colors.primaryContainer
                                } else {
                                    Theme.colors.surface
                                }
                            ),
                        onClick = {
                            onBloodTypeSelected(bloodType)
                            expanded = false
                        }
                    )
                }
            }
        }
        Box(modifier = Modifier.height(20.dp)) {
            errorMessage?.let {
                BasicText(
                    text = it,
                    style = Theme.typography.body.small.copy(color = Theme.colors.error)
                )
            }
        }
        BasicText(
            text = stringResource(R.string.basic_health_blood_type_note),
            style = Theme.typography.body.small.copy(
                color = Theme.colors.hint,
                fontSize = 12.sp,
                lineHeight = 18.sp
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BasicHealthInfoScreenPreview() {
    SpTheme {
        BasicHealthInfoScreen(
            height = "170",
            weight = "65",
            bloodType = "",
            onHeightChange = {},
            onWeightChange = {},
            onBloodTypeChange = {},
            onBack = {},
            onContinue = {}
        )
    }
}
