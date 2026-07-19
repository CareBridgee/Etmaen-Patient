package com.carenest.presentation.ui.profile.steps

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.carenest.designsystem.components.textfield.CustomTextField
import com.carenest.designsystem.theme.SpTheme
import com.carenest.designsystem.theme.Theme
import com.carenest.presentation.navigation.ScreenTopBar
import com.carenest.presentation.R
import com.carenest.presentation.ui.profile.components.ProfileProgressIndicator
import com.carenest.presentation.ui.profile.components.ProfileScreenNavigation

@Composable
fun BasicHealthInfoScreen(
    height: String,
    weight: String,
    bloodType: String,
    onHeightChange: (String) -> Unit,
    onWeightChange: (String) -> Unit,
    onBloodTypeChange: (String) -> Unit,
    onBack: () -> Unit,
    onContinue: () -> Unit
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
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ProfileProgressIndicator(
                step = 1,
                title = stringResource(R.string.basic_health_title),
            )

            HealthIntroCard()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MeasurementCard(
                    label = stringResource(R.string.basic_health_height),
                    value = height,
                    unit = stringResource(R.string.basic_health_cm),
                    onValueChange = onHeightChange,
                    modifier = Modifier.weight(1f)
                )
                MeasurementCard(
                    label = stringResource(R.string.basic_health_weight),
                    value = weight,
                    unit = stringResource(R.string.basic_health_kg),
                    onValueChange = onWeightChange,
                    modifier = Modifier.weight(1f)
                )
            }

            BloodTypeCard(
                selectedBloodType = bloodType,
                onBloodTypeSelected = onBloodTypeChange
            )
        }

        ProfileScreenNavigation(
            onBack = onBack,
            onContinue = onContinue
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
            .padding(20.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
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
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .shadow(4.dp, RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .background(Theme.colors.surface)
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        BasicText(
            text = label,
            style = Theme.typography.body.small.copy(
                color = Theme.colors.hint,
                fontWeight = FontWeight.Medium
            )
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            CustomTextField(
                text = value,
                onTextChange = { onValueChange(it.filter(Char::isDigit).take(3)) },
                singleLine = true,
                fieldHeight = 52.dp,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.size(8.dp))
            BasicText(
                text = unit,
                style = Theme.typography.body.small.copy(color = Theme.colors.secondaryFont)
            )
        }
    }
}

@Composable
private fun BloodTypeCard(
    selectedBloodType: String,
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
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
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
                    .clickable { expanded = true }
                    .padding(horizontal = 16.dp),
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
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                bloodTypes.forEach { bloodType ->
                    DropdownMenuItem(
                        text = { BasicText(bloodType) },
                        onClick = {
                            onBloodTypeSelected(bloodType)
                            expanded = false
                        }
                    )
                }
            }
        }
        BasicText(
            text = stringResource(R.string.basic_health_blood_type_note),
            style = Theme.typography.body.small.copy(
                color = Theme.colors.onDisable,
                fontSize = 10.sp
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
