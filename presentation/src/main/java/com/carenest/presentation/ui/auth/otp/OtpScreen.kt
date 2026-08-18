package com.carenest.presentation.ui.auth.otp

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PhoneAndroid
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.carenest.designsystem.components.button.PrimaryButton
import com.carenest.designsystem.components.dialog.CareNestDialog
import com.carenest.designsystem.theme.SpTheme
import com.carenest.designsystem.theme.Theme
import com.carenest.domain.validation.PhoneValidator
import com.carenest.presentation.R
import com.carenest.presentation.core.mvi.ObserveEffect
import com.carenest.presentation.navigation.AppRoute
import com.carenest.presentation.navigation.ScreenTopBar
import com.carenest.presentation.ui.auth.login.components.OtpTextField
import com.carenest.presentation.ui.auth.localizedMessage

@Composable
fun OtpScreen(
    entry: AppRoute.Otp,
    viewModel: OtpViewModel = hiltViewModel(),
    onNavigateToRegister: () -> Unit = {},
    onNavigateToCompleteProfile: () -> Unit = {},
    onNavigateToHome: () -> Unit = {},
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    androidx.compose.runtime.LaunchedEffect(entry.phone, entry.otp, entry.pendingToken) {
        viewModel.onEvent(OtpIntent.PhoneNumberChanged(entry.phone, entry.pendingToken))
        entry.otp?.let {
            viewModel.onEvent(OtpIntent.OtpCodeChanged(it))
        }
    }

    ObserveEffect(viewModel.effect) { effect ->
        when (effect) {
            is OtpEffect.NavigateToRegister -> onNavigateToRegister()
            is OtpEffect.NavigateToCompleteProfile -> onNavigateToCompleteProfile()
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
    val formattedPhoneNumber = PhoneValidator.formatInternationalNumber(state.phoneNumber)
    val leftToRightPhoneNumber = "\u2066$formattedPhoneNumber\u2069"

    ScreenTopBar(
        title = stringResource(R.string.otp_app_name),
        showLeadingIcon = true,
        onLeadingClick = { onEvent(OtpIntent.BackClicked) }
    )

    if (state.showExistingAccountDialog) {
        val maskedName = state.existingAccountName ?: ""
        CareNestDialog(
            title = stringResource(R.string.auth_error_phone_already_taken),
            message = stringResource(R.string.auth_existing_account_message, maskedName),
            confirmText = stringResource(R.string.auth_existing_account_action_signin),
            dismissText = stringResource(R.string.auth_existing_account_action_cancel),
            onConfirm = { onEvent(OtpIntent.ConfirmSignInToExistingAccount) },
            onDismiss = { onEvent(OtpIntent.DismissExistingAccountDialog) },
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.colors.backGround)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(24.dp))

            // Hero Icon
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(
                        color = Theme.colors.primary.copy(alpha = 0.1f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.PhoneAndroid,
                    contentDescription = null,
                    tint = Theme.colors.primary,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            BasicText(
                text = stringResource(R.string.otp_title),
                style = Theme.typography.display.copy(
                    fontWeight = FontWeight.Bold,
                    color = Theme.colors.primaryFont,
                    textAlign = TextAlign.Center
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            BasicText(
                text = stringResource(
                    R.string.otp_subtitle,
                    leftToRightPhoneNumber
                ),
                modifier = Modifier.padding(horizontal = 16.dp),
                style = Theme.typography.body.large.copy(
                    color = Theme.colors.secondaryFont,
                    textAlign = TextAlign.Center
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Theme.colors.surface
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 6.dp
                )
            ) {

                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    OtpTextField(
                        otpValue = state.otpCode,
                        onOtpValueChange = {
                            onEvent(OtpIntent.OtpCodeChanged(it))
                        }
                    )

                    state.errorMessage.localizedMessage()?.let { errorMessage ->

                        Spacer(modifier = Modifier.height(16.dp))

                        BasicText(
                            text = errorMessage,
                            style = Theme.typography.body.medium.copy(
                                color = Theme.colors.error,
                                textAlign = TextAlign.Center
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    PrimaryButton(
                        caption = stringResource(R.string.otp_verify_btn),
                        onClick = {
                            onEvent(OtpIntent.VerifyOtpClicked)
                        },
                        isLoading = state.isLoading,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            BasicText(
                text = if (state.remainingSeconds > 0) {
                    val minutes = state.remainingSeconds / 60
                    val seconds = state.remainingSeconds % 60
                    stringResource(
                        R.string.otp_resend_timer,
                        "%02d:%02d".format(minutes, seconds)
                    )
                } else {
                    stringResource(R.string.otp_resend_code)
                },
                modifier = Modifier.clickable(enabled = state.canResend) {
                    onEvent(OtpIntent.ResendClicked)
                },
                style = Theme.typography.body.large.copy(
                    color = if (state.canResend) Theme.colors.primary else Theme.colors.secondaryFont,
                    textAlign = TextAlign.Center
                )
            )

            Spacer(modifier = Modifier.height(24.dp))
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
                otpCode = ""
            ),
            onEvent = {}
        )
    }
}
