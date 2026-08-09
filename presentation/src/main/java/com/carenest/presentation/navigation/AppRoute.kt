package com.carenest.presentation.navigation

import androidx.navigation3.runtime.NavKey
import com.carenest.presentation.ui.auth.login.OtpDeliveryMethod
import com.carenest.presentation.ui.profile_completion.ProfileCompletionSource
import kotlinx.serialization.Serializable

@Serializable
sealed interface AppRoute : NavKey {

     @Serializable
     data object Splash : AppRoute

     @Serializable
     data object OnBoarding : AppRoute

     @Serializable
     data object Login : AppRoute

     @Serializable
     data class Otp(
         val phone: String,
         val otp: String? = null,
         val method: OtpDeliveryMethod = OtpDeliveryMethod.SMS
     ) : AppRoute

     @Serializable
     data object Register : AppRoute

     @Serializable
     data class ProfileCompletion(
         val profileId: String? = null,
         val isEditMode: Boolean = false,
         val source: ProfileCompletionSource = ProfileCompletionSource.REGISTRATION
     ) : AppRoute

     @Serializable
     data object Home : AppRoute

     @Serializable
     data object Services : AppRoute

     @Serializable
     data class ServiceDetails(val serviceId: String) : AppRoute

     @Serializable
     data object History : AppRoute

     @Serializable
     data object Profile : AppRoute

     @Serializable
     data object PersonalInfo : AppRoute

     @Serializable
     data object HealthProfile : AppRoute

     @Serializable
     data object Wallet : AppRoute

     @Serializable
     data object AddFunds : AppRoute

     @Serializable
     data object AddPaymentMethod : AppRoute
     @Serializable
     data class RequestService(val serviceId : String) : AppRoute

     @Serializable
     data object Map : AppRoute
     @Serializable
     data class SearchForNurse(
         val reservationId: String,
         val serviceRequestId: String
     ) : AppRoute
     @Serializable
     data class Chat(val requestId: String) : AppRoute

     @Serializable
     data object FamilyMembers : AppRoute

     @Serializable
     data class AddFamilyMember(val memberId: String? = null) : AppRoute

     @Serializable
     data object Settings : AppRoute

     @Serializable
     data object ChoosePatient : AppRoute


     @Serializable
     data class AIChat(val patientId: String) : AppRoute

     @Serializable
     data class EmergencyAssistance(val patientId: String = "") : AppRoute

     @Serializable
     data class NurseOnTheWay(val requestId: String) : AppRoute

     @Serializable
     data class ServiceHistoryDetails(val requestId: String) : AppRoute

     @Serializable
     data class VisitCompleted (val requestId: String): AppRoute

     @Serializable
     data class QrCode(val requestId: String) : AppRoute
}
