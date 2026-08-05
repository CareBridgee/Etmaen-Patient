package com.carenest.presentation.ui.aichat.emergency

data class EmergencyAssistanceState(
    val patientId: String = "",
    val isEmergencyActive: Boolean = true,
    val inputText: String = "",
    val isLoading: Boolean = false
)

sealed class EmergencyAssistanceEvent {
    object OnBackClicked : EmergencyAssistanceEvent()
    object OnCallAmbulanceClicked : EmergencyAssistanceEvent()
    object OnCallFamilyMemberClicked : EmergencyAssistanceEvent()
    object OnDismissClicked : EmergencyAssistanceEvent()
    data class OnInputTextChanged(val text: String) : EmergencyAssistanceEvent()
    object OnSendMessage : EmergencyAssistanceEvent()
}

sealed class EmergencyAssistanceEffect {
    object NavigateBack : EmergencyAssistanceEffect()
    object CallAmbulance : EmergencyAssistanceEffect()
    object CallFamilyMember : EmergencyAssistanceEffect()
    object DismissEmergency : EmergencyAssistanceEffect()
}
