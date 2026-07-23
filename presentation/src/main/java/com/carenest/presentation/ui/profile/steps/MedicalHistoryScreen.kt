package com.carenest.presentation.ui.profile.steps

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Business
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.MedicalServices
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.carenest.designsystem.components.textfield.CustomTextField
import com.carenest.designsystem.theme.SpTheme
import com.carenest.designsystem.theme.Theme
import com.carenest.presentation.R
import com.carenest.presentation.navigation.ScreenTopBar
import com.carenest.presentation.ui.profile.components.ProfileProgressIndicator
import com.carenest.presentation.ui.profile.components.ProfileScreenNavigation

@Composable
fun MedicalHistoryScreen(
    previousSurgeries: String,
    previousHospitalizations: String,
    previousSurgeriesError: String? = null,
    previousHospitalizationsError: String? = null,
    onPreviousSurgeriesChange: (String) -> Unit,
    onPreviousHospitalizationsChange: (String) -> Unit,
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
                    vertical = Theme.spacing.large
                ),
            verticalArrangement = Arrangement.spacedBy(Theme.spacing.large)
        ) {
            ProfileProgressIndicator(
                step = 5,
                title = stringResource(R.string.medical_history_title)
            )

            HistoryInputCard(
                title = stringResource(R.string.medical_history_surgeries),
                subtitle = stringResource(R.string.medical_history_surgeries_subtitle),
                hint = stringResource(R.string.medical_history_surgeries_hint),
                icon = Icons.Outlined.MedicalServices,
                text = previousSurgeries,
                errorMessage = previousSurgeriesError,
                onTextChange = onPreviousSurgeriesChange
            )

            HistoryInputCard(
                title = stringResource(R.string.medical_history_hospitalizations),
                subtitle = stringResource(R.string.medical_history_hospitalizations_subtitle),
                hint = stringResource(R.string.medical_history_hospitalizations_hint),
                icon = Icons.Outlined.Business,
                text = previousHospitalizations,
                errorMessage = previousHospitalizationsError,
                onTextChange = onPreviousHospitalizationsChange
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Theme.colors.primaryContainer.copy(alpha = 0.28f))
                    .border(
                        width = 1.dp,
                        color = Theme.colors.primary.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(Theme.spacing.medium),
                horizontalArrangement = Arrangement.spacedBy(Theme.spacing.medium),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = null,
                    tint = Theme.colors.primary,
                    modifier = Modifier.size(24.dp)
                )
                BasicText(
                    text = stringResource(R.string.medical_history_info),
                    modifier = Modifier.weight(1f),
                    style = Theme.typography.body.medium.copy(
                        color = Theme.colors.secondaryFont,
                        fontSize = 14.sp,
                        lineHeight = 22.sp,
                        fontStyle = FontStyle.Italic
                    )
                )
            }
        }

        ProfileScreenNavigation(
            onBack = onBack,
            onContinue = onContinue,
            continueEnabled = !isSubmitting,
            isLoading = isSubmitting
        )
    }
}

@Composable
private fun HistoryInputCard(
    title: String,
    subtitle: String,
    hint: String,
    icon: ImageVector,
    text: String,
    errorMessage: String? = null,
    onTextChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(24.dp))
            .clip(RoundedCornerShape(24.dp))
            .background(Theme.colors.surface)
            .padding(Theme.spacing.large),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing.medium)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(Theme.spacing.space12),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Theme.colors.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Theme.colors.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                BasicText(
                    text = title,
                    style = Theme.typography.title.copy(
                        color = Theme.colors.primaryFont,
                        fontSize = 18.sp
                    )
                )
                BasicText(
                    text = subtitle,
                    style = Theme.typography.body.small.copy(color = Theme.colors.hint)
                )
            }
        }

        CustomTextField(
            text = text,
            onTextChange = onTextChange,
            hint = hint,
            fieldHeight = 140.dp,
            fieldVerticalAlignment = Alignment.Top,
            borderColor = Theme.colors.cardBackground,
            containerColor = Theme.colors.cardBackground,
            isError = errorMessage != null,
            errorMessage = errorMessage,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MedicalHistoryScreenPreview() {
    SpTheme {
        MedicalHistoryScreen(
            previousSurgeries = "Appendectomy (2015)",
            previousHospitalizations = "",
            onPreviousSurgeriesChange = {},
            onPreviousHospitalizationsChange = {},
            onBack = {},
            onContinue = {}
        )
    }
}
