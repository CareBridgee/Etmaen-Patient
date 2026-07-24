package com.carenest.presentation.ui.tracking

import com.carenest.domain.model.tracking.NurseTrackingInfo


data class NurseOnTheWayState(
    val isLoading: Boolean = true,
    val nurseInfo: NurseTrackingInfo? = null,
    val isCancelling: Boolean = false,
    val showCancelConfirmationDialog: Boolean = false,
    val errorMessage: String? = null,
)

sealed interface NurseOnTheWayIntent {
    data class LoadNurseTrackingInfo(val requestId: String) : NurseOnTheWayIntent
    data object OnCallNurseClicked : NurseOnTheWayIntent
    data object OnMessageNurseClicked : NurseOnTheWayIntent
    data object OnShowQrCodeClicked : NurseOnTheWayIntent
    data class OnCancelVisitClicked(val requestId: String) : NurseOnTheWayIntent
    data object OnConfirmCancelVisitClicked : NurseOnTheWayIntent
    data object OnDismissCancelDialogClicked : NurseOnTheWayIntent
    data object OnErrorDismissed : NurseOnTheWayIntent
}

sealed interface NurseOnTheWayEffect {
    data class InitiateCall(val phoneNumber: String) : NurseOnTheWayEffect
    data class OpenChat(val nurseId: String) : NurseOnTheWayEffect
    data object NavigateToQrCode : NurseOnTheWayEffect
    data object NavigateBackAfterCancel : NurseOnTheWayEffect
    data class ShowCancellationFeeWarning(val message: String) : NurseOnTheWayEffect
    data class ShowError(val message: String) : NurseOnTheWayEffect
}
