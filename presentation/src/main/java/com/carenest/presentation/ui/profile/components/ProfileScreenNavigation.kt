package com.carenest.presentation.ui.profile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
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
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Theme.colors.backGround)
            .padding(
                horizontal = Theme.spacing.space20,
                vertical = Theme.spacing.space12
            ),
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing.small)
    ) {
        SecondaryButton(
            caption = stringResource(R.string.profile_back),
            onClick = onBack,
            modifier = Modifier.weight(0.34f)
        )
        PrimaryButton(
            caption = stringResource(R.string.profile_continue),
            onClick = onContinue,
            iconPosition = ButtonIconPosition.End,
            modifier = Modifier.weight(0.66f)
        )
    }
}
