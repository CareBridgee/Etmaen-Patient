package com.carenest.presentation.ui.profile.basichealth

data class BasicHealthInfoState(
    val height: String = "170",
    val weight: String = "65",
    val bloodType: String = ""
)

sealed interface BasicHealthInfoIntent {
    data class HeightChanged(val height: String) : BasicHealthInfoIntent
    data class WeightChanged(val weight: String) : BasicHealthInfoIntent
    data class BloodTypeChanged(val bloodType: String) : BasicHealthInfoIntent
    data object BackClicked : BasicHealthInfoIntent
    data object ContinueClicked : BasicHealthInfoIntent
}

sealed interface BasicHealthInfoEffect {
    data object NavigateBack : BasicHealthInfoEffect
    data object NavigateToMedicalConditions : BasicHealthInfoEffect
}
