package com.carenest.presentation.ui.aichat.choosepatient

data class PatientItem(
    val id: String,
    val name: String,
    val relationship: String,
    val isSelected: Boolean = false
)

data class ChoosePatientState(
    val patients: List<PatientItem> = emptyList(),
    val isLoading: Boolean = false
)

sealed class ChoosePatientEvent {
    data class OnPatientSelected(val patientId: String) : ChoosePatientEvent()
    object OnAddFamilyMemberClicked : ChoosePatientEvent()
    object OnContinueClicked : ChoosePatientEvent()
}

sealed class ChoosePatientEffect {
    data class NavigateToChat(val patientId: String) : ChoosePatientEffect()
    object NavigateToAddFamilyMember : ChoosePatientEffect()
}
