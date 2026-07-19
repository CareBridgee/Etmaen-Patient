package com.carenest.presentation.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface AppRoute : NavKey {

    // screen with no parameters → use data object
    // screen with parameters → use data class

     @Serializable
     data object Splash : AppRoute

     @Serializable
     data object OnBoarding : AppRoute

     @Serializable
     data object Login : AppRoute

     @Serializable
     data class Otp(val phone: String, val method: com.carenest.presentation.ui.auth.login.OtpDeliveryMethod = com.carenest.presentation.ui.auth.login.OtpDeliveryMethod.SMS) : AppRoute

     @Serializable
     data object VerificationSuccess : AppRoute

     @Serializable
     data object ProfileWelcome : AppRoute

     @Serializable
     data object ProfilePersonalInfo : AppRoute

     @Serializable
     data object ProfileBasicHealth : AppRoute

     @Serializable
     data object ProfileMedicalConditions : AppRoute

     @Serializable
     data object ProfileAllergies : AppRoute

}
