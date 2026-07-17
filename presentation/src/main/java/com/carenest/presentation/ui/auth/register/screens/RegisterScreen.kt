package com.carenest.presentation.ui.auth.register.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.carenest.presentation.ui.auth.register.RegisterIntent
import com.carenest.presentation.ui.auth.register.RegisterState
import com.carenest.presentation.ui.auth.register.RegisterStep
import com.carenest.presentation.ui.auth.register.RegisterViewModel
import com.carenest.presentation.core.mvi.ObserveEffect

@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel,
    onNavigateHome: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    ObserveEffect(viewModel.effect) {
        // Effect received means successful registration or skipped
        onNavigateHome()
    }

    RegisterScreenContent(
        state = state,
        onEvent = viewModel::onEvent
    )
}

@Composable
internal fun RegisterScreenContent(
    state: RegisterState,
    onEvent: (RegisterIntent) -> Unit
) {
    when (state.currentStep) {
        RegisterStep.WELCOME -> WelcomeScreen(onEvent)
        RegisterStep.PERSONAL_INFO -> PersonalInfoScreen(state, onEvent)
    }
}
