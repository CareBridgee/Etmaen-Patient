package com.carenest.presentation.auth.login.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.carenest.designsystem.components.button.PrimaryButton
import com.carenest.designsystem.components.textfield.PhoneInputField
import com.carenest.designsystem.components.topbar.BaseTopAppBar
import com.carenest.designsystem.theme.SpTheme
import com.carenest.designsystem.theme.Theme
import com.carenest.presentation.auth.login.LoginEvent
import com.carenest.presentation.auth.login.LoginState
import com.carenest.presentation.auth.login.LoginStep
import com.carenest.designsystem.R as DR

@Composable
fun PhoneInputScreen(state: LoginState, onEvent: (LoginEvent) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.colors.backGround)
    ) {
        BaseTopAppBar(
            title = "Join us via phone number",
            leadingIcon = painterResource(id = DR.drawable.ic_arrow_back),
            onLeadingClick = { onEvent(LoginEvent.BackClicked) },
            autoMirrorLeadingIcon = true
        )
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            BasicText(
                text = "We'll text a code to verify your phone.",
                style = Theme.typography.title.copy(
                    color = Theme.colors.primaryFont
                )
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            BasicText(
                text = "Phone Number",
                style = Theme.typography.body.large.copy(
                    color = Theme.colors.primary,
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            PhoneInputField(
                phone = state.phoneNumber,
                onPhoneChange = { onEvent(LoginEvent.PhoneNumberChanged(it)) }
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            BasicText(
                text = "Carrier charges may apply for SMS.",
                style = Theme.typography.body.medium.copy(color = Theme.colors.hint)
            )
            
            if (state.errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                BasicText(
                    text = state.errorMessage,
                    style = Theme.typography.body.medium.copy(color = Theme.colors.error)
                )
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            PrimaryButton(
                caption = "NEXT",
                onClick = { onEvent(LoginEvent.RequestOtpClicked) },
                isLoading = state.isLoading,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PhoneInputScreenPreview() {
    SpTheme {
        PhoneInputScreen(
            state = LoginState(currentStep = LoginStep.PHONE_INPUT, phoneNumber = "5550000000"),
            onEvent = {}
        )
    }
}
