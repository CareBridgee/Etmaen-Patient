package com.carenest.presentation.ui.auth.registration

import androidx.lifecycle.ViewModel
import com.carenest.presentation.core.mvi.DefaultEffectPublisher
import com.carenest.presentation.core.mvi.DefaultStateHolder
import com.carenest.presentation.core.mvi.EffectPublisher
import com.carenest.presentation.core.mvi.StateHolder

class RegistrationViewModel : ViewModel(),
    StateHolder<RegistrationState> by DefaultStateHolder(RegistrationState()),
    EffectPublisher<RegistrationEffect> by DefaultEffectPublisher() {

    fun onEvent(event: RegistrationIntent) {
        when (event) {
            is RegistrationIntent.FirstNameChanged -> updateState {
                copy(firstName = event.firstName)
            }
            is RegistrationIntent.LastNameChanged -> updateState {
                copy(lastName = event.lastName)
            }
            is RegistrationIntent.DateOfBirthChanged -> updateState {
                copy(dateOfBirth = event.dateOfBirth)
            }
            is RegistrationIntent.NationalIdChanged -> updateState {
                copy(nationalId = event.nationalId)
            }
            is RegistrationIntent.GenderChanged -> updateState {
                copy(gender = event.gender)
            }
            is RegistrationIntent.AccountTypeChanged -> updateState {
                copy(accountType = event.accountType)
            }
            RegistrationIntent.BackClicked -> sendEffect(RegistrationEffect.NavigateBack)
            RegistrationIntent.ContinueClicked -> {
                sendEffect(RegistrationEffect.NavigateToWelcome)
            }
        }
    }
}
