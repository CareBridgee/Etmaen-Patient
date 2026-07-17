package com.carenest.presentation.ui.auth.login.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.carenest.presentation.ui.auth.login.LoginIntent
import com.carenest.presentation.ui.auth.login.LoginState
import com.carenest.presentation.ui.auth.login.LoginStep
import com.carenest.presentation.ui.auth.login.LoginViewModel
import com.carenest.presentation.ui.auth.login.LoginEffect
import com.carenest.presentation.core.mvi.ObserveEffect

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    onNavigateToOtp: (String) -> Unit
) {
    val state by viewModel.state.collectAsState()

    ObserveEffect(viewModel.effect) { effect ->
        when (effect) {
            is LoginEffect.NavigateToOtp -> onNavigateToOtp(effect.phone)
        }
    }

    LoginScreenContent(
        state = state,
        onEvent = viewModel::onEvent
    )
}

@Composable
internal fun LoginScreenContent(
    state: LoginState,
    onEvent: (LoginIntent) -> Unit
) {
    when (state.currentStep) {
        LoginStep.LANDING -> AuthLandingScreen(onEvent)
        LoginStep.PHONE_INPUT -> PhoneInputScreen(state, onEvent)
    }
}
