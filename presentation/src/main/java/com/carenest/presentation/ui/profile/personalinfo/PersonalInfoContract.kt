package com.carenest.presentation.ui.profile.personalinfo

data class PersonalInfoState(
    val firstName: String = "",
    val lastName: String = "",
    val dateOfBirth: String = "",
    val nationalId: String = "",
    val gender: String = "",
    val accountType: String = ""
)

sealed interface PersonalInfoIntent {
    data class FirstNameChanged(val firstName: String) : PersonalInfoIntent
    data class LastNameChanged(val lastName: String) : PersonalInfoIntent
    data class DateOfBirthChanged(val dateOfBirth: String) : PersonalInfoIntent
    data class NationalIdChanged(val nationalId: String) : PersonalInfoIntent
    data class GenderChanged(val gender: String) : PersonalInfoIntent
    data class AccountTypeChanged(val accountType: String) : PersonalInfoIntent
    data object BackClicked : PersonalInfoIntent
    data object ContinueClicked : PersonalInfoIntent
}

sealed interface PersonalInfoEffect {
    data object NavigateBack : PersonalInfoEffect
    data object NavigateToBasicHealth : PersonalInfoEffect
}
