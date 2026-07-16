package com.carenest.presentation.auth.login.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.carenest.designsystem.components.button.PrimaryButton
import com.carenest.designsystem.components.textfield.OtpTextField
import com.carenest.designsystem.components.topbar.BaseTopAppBar
import com.carenest.designsystem.theme.SpTheme
import com.carenest.designsystem.theme.Theme
import com.carenest.presentation.auth.login.LoginEvent
import com.carenest.presentation.auth.login.LoginState
import com.carenest.presentation.auth.login.LoginStep
import com.carenest.designsystem.R as DR

@Composable
fun VerifyPhoneScreen(state: LoginState, onEvent: (LoginEvent) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.colors.backGround)
    ) {
        BaseTopAppBar(
            title = "",
            leadingIcon = painterResource(id = DR.drawable.ic_arrow_back),
            onLeadingClick = { onEvent(LoginEvent.BackClicked) },
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
                text = "Verify Phone",
                style = Theme.typography.display.copy(
                    color = Theme.colors.primaryFont,
                    fontWeight = FontWeight.Bold
                )
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            BasicText(
                text = "We've sent a 6-digit code to your phone number ${state.phoneNumber}",
                style = Theme.typography.body.large.copy(
                    color = Theme.colors.primaryFont,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.padding(horizontal = 24.dp)
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            OtpTextField(
                otpValue = state.otpCode,
                onOtpValueChange = { onEvent(LoginEvent.OtpCodeChanged(it)) }
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
                caption = "VERIFY",
                onClick = { onEvent(LoginEvent.VerifyOtpClicked) },
                isLoading = state.isLoading,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            BasicText(
                text = "Resend code in 00:30",
                style = Theme.typography.body.large.copy(color = Theme.colors.primary)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun VerifyPhoneScreenPreview() {
    SpTheme {
        VerifyPhoneScreen(
            state = LoginState(
                currentStep = LoginStep.VERIFY_OTP,
                phoneNumber = "+1 555 000 0000",
                otpCode = "123"
            ),
            onEvent = {}
        )
    }
}
