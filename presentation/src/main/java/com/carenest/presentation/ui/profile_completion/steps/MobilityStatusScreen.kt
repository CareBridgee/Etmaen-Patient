package com.carenest.presentation.ui.profile_completion.steps

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Accessible
import androidx.compose.material.icons.automirrored.outlined.DirectionsWalk
import androidx.compose.material.icons.outlined.AssistWalker
import androidx.compose.material.icons.outlined.Bed
import androidx.compose.material.icons.outlined.Blind
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.carenest.designsystem.components.textfield.CustomTextField
import com.carenest.designsystem.theme.SpTheme
import com.carenest.designsystem.theme.Theme
import com.carenest.domain.model.profile.MobilityStatus
import com.carenest.presentation.R
import com.carenest.presentation.navigation.ScreenTopBar
import com.carenest.presentation.ui.profile_completion.components.ProfileProgressIndicator
import com.carenest.presentation.ui.profile_completion.components.ProfileScreenNavigation

private data class MobilityOption(
    val status: MobilityStatus,
    val title: String,
    val description: String,
    val icon: ImageVector
)

@Composable
fun MobilityStatusScreen(
    selectedStatus: MobilityStatus?,
    additionalNotes: String,
    statusError: String? = null,
    notesError: String? = null,
    onStatusSelected: (MobilityStatus) -> Unit,
    onAdditionalNotesChange: (String) -> Unit,
    onBack: () -> Unit,
    onContinue: () -> Unit,
    isSubmitting: Boolean = false
) {
    ScreenTopBar(
        title = stringResource(R.string.welcome_topbar_title),
        showLeadingIcon = true,
        onLeadingClick = onBack
    )

    val options = listOf(
        MobilityOption(
            MobilityStatus.Independent,
            stringResource(R.string.mobility_independent),
            stringResource(R.string.mobility_independent_description),
            Icons.AutoMirrored.Outlined.DirectionsWalk
        ),
        MobilityOption(
            MobilityStatus.NeedsAssistance,
            stringResource(R.string.mobility_needs_assistance),
            stringResource(R.string.mobility_needs_assistance_description),
            Icons.Outlined.AssistWalker
        ),
        MobilityOption(
            MobilityStatus.UsesWalkingAid,
            stringResource(R.string.mobility_walking_aid),
            stringResource(R.string.mobility_walking_aid_description),
            Icons.Outlined.Blind
        ),
        MobilityOption(
            MobilityStatus.WheelchairUser,
            stringResource(R.string.mobility_wheelchair),
            stringResource(R.string.mobility_wheelchair_description),
            Icons.AutoMirrored.Outlined.Accessible
        ),
        MobilityOption(
            MobilityStatus.Bedridden,
            stringResource(R.string.mobility_bedridden),
            stringResource(R.string.mobility_bedridden_description),
            Icons.Outlined.Bed
        )
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
                step = 6,
                title = stringResource(R.string.mobility_progress_title)
            )

            Column(verticalArrangement = Arrangement.spacedBy(Theme.spacing.small)) {
                BasicText(
                    text = stringResource(R.string.mobility_title),
                    style = Theme.typography.displayMedium.copy(
                        color = Theme.colors.primaryFont
                    )
                )
                BasicText(
                    text = stringResource(R.string.mobility_description),
                    style = Theme.typography.body.medium.copy(
                        color = Theme.colors.secondaryFont,
                        fontSize = 14.sp,
                        lineHeight = 22.sp
                    )
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(Theme.spacing.space12)) {
                options.forEach { option ->
                    MobilityOptionCard(
                        option = option,
                        selected = selectedStatus == option.status,
                        onClick = { onStatusSelected(option.status) }
                    )
                }
                statusError?.let {
                    BasicText(
                        text = it,
                        style = Theme.typography.body.small.copy(color = Theme.colors.error)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(Theme.colors.primaryContainer.copy(alpha = 0.18f))
                    .border(
                        width = 1.dp,
                        color = Theme.colors.primary.copy(alpha = 0.28f),
                        shape = RoundedCornerShape(24.dp)
                    )
                    .padding(Theme.spacing.large),
                verticalArrangement = Arrangement.spacedBy(Theme.spacing.small)
            ) {
                BasicText(
                    text = stringResource(R.string.mobility_notes_title),
                    style = Theme.typography.body.medium.copy(
                        color = Theme.colors.primary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                )
                CustomTextField(
                    text = additionalNotes,
                    onTextChange = onAdditionalNotesChange,
                    hint = stringResource(R.string.mobility_notes_hint),
                    fieldHeight = 96.dp,
                    fieldVerticalAlignment = Alignment.Top,
                    borderColor = Theme.colors.cardBackground,
                    containerColor = Theme.colors.cardBackground,
                    isError = notesError != null,
                    errorMessage = notesError,
                    modifier = Modifier.fillMaxWidth()
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
private fun MobilityOptionCard(
    option: MobilityOption,
    selected: Boolean,
    onClick: () -> Unit
) {
    val cardShape = RoundedCornerShape(24.dp)
    val backgroundColor = if (selected) {
        Theme.colors.primaryContainer
    } else {
        Theme.colors.surface
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, cardShape)
            .clip(cardShape)
            .background(backgroundColor)
            .then(
                if (selected) {
                    Modifier.border(2.dp, Theme.colors.primary, cardShape)
                } else {
                    Modifier
                }
            )
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 96.dp)
                .padding(Theme.spacing.large),
            horizontalArrangement = Arrangement.spacedBy(Theme.spacing.medium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Theme.colors.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = option.icon,
                    contentDescription = null,
                    tint = Theme.colors.primary,
                    modifier = Modifier.size(32.dp)
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(Theme.spacing.extraSmall)
            ) {
                BasicText(
                    text = option.title,
                    style = Theme.typography.title.copy(
                        color = Theme.colors.primaryFont,
                        fontSize = 18.sp
                    )
                )
                BasicText(
                    text = option.description,
                    style = Theme.typography.body.small.copy(
                        color = Theme.colors.secondaryFont
                    )
                )
            }
            if (selected) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(Theme.colors.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Check,
                        contentDescription = null,
                        tint = Theme.colors.onPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MobilityStatusScreenPreview() {
    SpTheme {
        MobilityStatusScreen(
            selectedStatus = MobilityStatus.UsesWalkingAid,
            additionalNotes = "Occasional falls",
            onStatusSelected = {},
            onAdditionalNotesChange = {},
            onBack = {},
            onContinue = {}
        )
    }
}
