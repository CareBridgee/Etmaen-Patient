package com.carenest.presentation.ui.auth.register

import androidx.lifecycle.ViewModel
import com.carenest.presentation.core.mvi.DefaultEffectPublisher
import com.carenest.presentation.core.mvi.DefaultStateHolder
import com.carenest.presentation.core.mvi.EffectPublisher
import com.carenest.presentation.core.mvi.StateHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@HiltViewModel
class RegisterViewModel @Inject constructor() : ViewModel(),
    StateHolder<RegisterState> by DefaultStateHolder(RegisterState()),
    EffectPublisher<RegisterEffect> by DefaultEffectPublisher() {

    fun onEvent(event: RegisterIntent) {
        when (event) {
            is RegisterIntent.FirstNameChanged -> updateState {
                copy(firstName = event.firstName)
            }
            is RegisterIntent.LastNameChanged -> updateState {
                copy(lastName = event.lastName)
            }
            is RegisterIntent.DateOfBirthChanged -> updateState {
                copy(dateOfBirth = event.dateOfBirth)
            }
            is RegisterIntent.NationalIdChanged -> updateState {
                copy(nationalId = event.nationalId)
            }
            is RegisterIntent.GenderChanged -> updateState {
                copy(gender = event.gender)
            }
            is RegisterIntent.AccountTypeChanged -> updateState {
                copy(accountType = event.accountType)
            }
            RegisterIntent.BackClicked -> sendEffect(RegisterEffect.NavigateBack)
            RegisterIntent.ContinueClicked -> {
                sendEffect(RegisterEffect.NavigateToWelcome)
            }
        }
    }
}
