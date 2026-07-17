package com.carenest.presentation.ui.auth.otp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.carenest.designsystem.components.button.PrimaryButton
import com.carenest.presentation.ui.auth.login.components.OtpTextField
import com.carenest.designsystem.components.topbar.BaseTopAppBar
import com.carenest.designsystem.theme.SpTheme
import com.carenest.designsystem.theme.Theme
import com.carenest.presentation.core.mvi.ObserveEffect
import com.carenest.presentation.ui.auth.otp.OtpEffect
import com.carenest.presentation.ui.auth.otp.OtpIntent
import com.carenest.presentation.ui.auth.otp.OtpState
import com.carenest.presentation.ui.auth.otp.OtpViewModel
import com.carenest.presentation.R
import com.carenest.designsystem.R as DR

@Composable
fun OtpScreen(
    viewModel: OtpViewModel = hiltViewModel(),
    onNavigateToHome: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    ObserveEffect(viewModel.effect) { effect ->
        when (effect) {
            is OtpEffect.NavigateToHome -> onNavigateToHome()
            is OtpEffect.NavigateBack -> onNavigateBack()
        }
    }

    OtpScreenContent(
        state = state,
        onEvent = viewModel::onEvent
    )
}

@Composable
internal fun OtpScreenContent(
    state: OtpState,
    onEvent: (OtpIntent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.colors.backGround)
    ) {
        BaseTopAppBar(
            title = "",
            leadingIcon = painterResource(id = DR.drawable.ic_arrow_back),
            onLeadingClick = { onEvent(OtpIntent.BackClicked) },
            autoMirrorLeadingIcon = true
        )
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            
            BasicText(
                text = stringResource(R.string.otp_title),
                style = Theme.typography.display.copy(
                    color = Theme.colors.primaryFont,
                    fontWeight = FontWeight.Bold
                )
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            BasicText(
                text = stringResource(R.string.otp_subtitle, state.phoneNumber),
                style = Theme.typography.body.large.copy(
                    color = Theme.colors.primaryFont,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.padding(horizontal = 24.dp)
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            OtpTextField(
                otpValue = state.otpCode,
                onOtpValueChange = { onEvent(OtpIntent.OtpCodeChanged(it)) }
            )
            
            if (state.errorMessage != null) {
                Spacer(modifier = Modifier.height(16.dp))
                BasicText(
                    text = state.errorMessage,
                    style = Theme.typography.body.medium.copy(color = Theme.colors.error)
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            PrimaryButton(
                caption = stringResource(R.string.otp_verify_btn),
                onClick = { onEvent(OtpIntent.VerifyOtpClicked) },
                isLoading = state.isLoading,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            BasicText(
                text = stringResource(R.string.otp_resend_timer),
                style = Theme.typography.body.large.copy(color = Theme.colors.primary)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun OtpScreenPreview() {
    SpTheme {
        OtpScreenContent(
            state = OtpState(
                phoneNumber = "+1 555 000 0000",
                otpCode = "123"
            ),
            onEvent = {}
        )
    }
}
