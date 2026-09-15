package com.carenest.presentation.ui.profilecompletion.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.carenest.designsystem.components.button.ButtonIconPosition
import com.carenest.designsystem.components.button.PrimaryButton
import com.carenest.designsystem.components.button.SecondaryButton
import com.carenest.designsystem.theme.Theme
import com.carenest.presentation.R

@Composable
fun ProfileScreenNavigation(
    onBack: () -> Unit,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier,
    continueCaption: String = stringResource(R.string.profile_continue),
    stackButtons: Boolean = false,
    showBackButton: Boolean = true,
    continueEnabled: Boolean = true,
    isLoading: Boolean = false
) {
    val navigationModifier = modifier
        .fillMaxWidth()
        .background(Theme.colors.backGround)
        .padding(
            horizontal = Theme.spacing.space20,
            vertical = Theme.spacing.space12
        )

    when {
        !showBackButton -> {
            Column(modifier = navigationModifier) {
                PrimaryButton(
                    caption = continueCaption,
                    onClick = onContinue,
                    iconPosition = ButtonIconPosition.End,
                    isDisabled = !continueEnabled,
                    isLoading = isLoading,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        stackButtons -> {
            Column(
                modifier = navigationModifier,
                verticalArrangement = Arrangement.spacedBy(Theme.spacing.space12)
            ) {
                PrimaryButton(
                    caption = continueCaption,
                    onClick = onContinue,
                    isDisabled = !continueEnabled,
                    isLoading = isLoading,
                    modifier = Modifier.fillMaxWidth()
                )
                SecondaryButton(
                    caption = stringResource(R.string.profile_back),
                    onClick = onBack,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        else -> {
            Row(
                modifier = navigationModifier,
                horizontalArrangement = Arrangement.spacedBy(Theme.spacing.small)
            ) {
                SecondaryButton(
                    caption = stringResource(R.string.profile_back),
                    onClick = onBack,
                    modifier = Modifier.weight(0.34f)
                )
                PrimaryButton(
                    caption = continueCaption,
                    onClick = onContinue,
                    iconPosition = ButtonIconPosition.End,
                    isDisabled = !continueEnabled,
                    isLoading = isLoading,
                    modifier = Modifier.weight(0.66f)
                )
            }
        }
    }
}
