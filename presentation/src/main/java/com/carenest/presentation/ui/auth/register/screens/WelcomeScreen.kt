package com.carenest.presentation.ui.auth.register.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.carenest.designsystem.components.button.PrimaryButton
import com.carenest.designsystem.components.button.SecondaryButton
import com.carenest.designsystem.components.topbar.BaseTopAppBar
import com.carenest.designsystem.theme.SpTheme
import com.carenest.designsystem.theme.Theme
import com.carenest.presentation.ui.auth.register.RegisterIntent
import com.carenest.presentation.R
import com.carenest.designsystem.R as DR

@Composable
fun WelcomeScreen(onEvent: (RegisterIntent) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.colors.backGround)
    ) {
        BaseTopAppBar(
            title = stringResource(R.string.welcome_topbar_title),
            leadingIcon = painterResource(id = DR.drawable.ic_arrow_back),
            onLeadingClick = { onEvent(RegisterIntent.BackClicked) },
            autoMirrorLeadingIcon = true
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))
            
            BasicText(
                text = stringResource(R.string.welcome_title),
                style = Theme.typography.displayMedium.copy(
                    color = Theme.colors.primaryFont,
                    fontWeight = FontWeight.Bold
                )
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            BasicText(
                text = stringResource(R.string.welcome_subtitle),
                style = Theme.typography.body.large.copy(
                    color = Theme.colors.primaryFont,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.padding(horizontal = 24.dp)
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            PrimaryButton(
                caption = stringResource(R.string.welcome_complete_profile_btn),
                onClick = { onEvent(RegisterIntent.CompleteProfileClicked) },
                modifier = Modifier.fillMaxWidth().height(80.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            SecondaryButton(
                caption = stringResource(R.string.welcome_skip_btn),
                onClick = { onEvent(RegisterIntent.SkipClicked) },
                modifier = Modifier.fillMaxWidth().height(80.dp)
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            BasicText(
                text = stringResource(R.string.welcome_skip_warning),
                style = Theme.typography.body.small.copy(
                    color = Theme.colors.hint,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun WelcomeScreenPreview() {
    SpTheme {
        WelcomeScreen(onEvent = {})
    }
}
