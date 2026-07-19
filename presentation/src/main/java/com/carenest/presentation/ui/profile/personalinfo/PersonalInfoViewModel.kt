package com.carenest.presentation.ui.profile.personalinfo

import androidx.lifecycle.ViewModel
import com.carenest.presentation.core.mvi.DefaultEffectPublisher
import com.carenest.presentation.core.mvi.DefaultStateHolder
import com.carenest.presentation.core.mvi.EffectPublisher
import com.carenest.presentation.core.mvi.StateHolder

class PersonalInfoViewModel : ViewModel(),
    StateHolder<PersonalInfoState> by DefaultStateHolder(PersonalInfoState()),
    EffectPublisher<PersonalInfoEffect> by DefaultEffectPublisher() {

    fun onEvent(event: PersonalInfoIntent) {
        when (event) {
            is PersonalInfoIntent.FirstNameChanged -> updateState { copy(firstName = event.firstName) }
            is PersonalInfoIntent.LastNameChanged -> updateState { copy(lastName = event.lastName) }
            is PersonalInfoIntent.DateOfBirthChanged -> updateState { copy(dateOfBirth = event.dateOfBirth) }
            is PersonalInfoIntent.NationalIdChanged -> updateState { copy(nationalId = event.nationalId) }
            is PersonalInfoIntent.GenderChanged -> updateState { copy(gender = event.gender) }
            is PersonalInfoIntent.AccountTypeChanged -> updateState { copy(accountType = event.accountType) }
            PersonalInfoIntent.BackClicked -> sendEffect(PersonalInfoEffect.NavigateBack)
            PersonalInfoIntent.ContinueClicked -> sendEffect(PersonalInfoEffect.NavigateToBasicHealth)
        }
    }
}
