package com.carenest.presentation.ui.auth.login.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.carenest.designsystem.components.button.PrimaryButton
import com.carenest.presentation.ui.auth.login.components.PhoneInputField
import com.carenest.designsystem.components.topbar.BaseTopAppBar
import com.carenest.designsystem.theme.SpTheme
import com.carenest.designsystem.theme.Theme
import com.carenest.presentation.ui.auth.login.LoginIntent
import com.carenest.presentation.ui.auth.login.LoginState
import com.carenest.presentation.ui.auth.login.LoginStep
import com.carenest.presentation.R
import com.carenest.designsystem.R as DR

@Composable
fun PhoneInputScreen(state: LoginState, onEvent: (LoginIntent) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.colors.backGround)
    ) {
        BaseTopAppBar(
            title = stringResource(R.string.phone_input_topbar_title),
            leadingIcon = painterResource(id = DR.drawable.ic_arrow_back),
            onLeadingClick = { onEvent(LoginIntent.BackClicked) },
            autoMirrorLeadingIcon = true
        )
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            BasicText(
                text = stringResource(R.string.phone_input_subtitle),
                style = Theme.typography.title.copy(
                    color = Theme.colors.primaryFont
                )
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            BasicText(
                text = stringResource(R.string.phone_input_label),
                style = Theme.typography.body.large.copy(
                    color = Theme.colors.primary,
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            PhoneInputField(
                phone = state.phoneNumber,
                onPhoneChange = { onEvent(LoginIntent.PhoneNumberChanged(it)) }
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            BasicText(
                text = stringResource(R.string.phone_input_carrier_charges),
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
                caption = stringResource(R.string.phone_input_next_btn),
                onClick = { onEvent(LoginIntent.RequestOtpClicked) },
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
