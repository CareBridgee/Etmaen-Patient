package com.carenest.presentation.navigation

import androidx.navigation3.runtime.NavKey
import com.carenest.domain.model.home.ServiceCategory
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
     data object Register : AppRoute

     @Serializable
     data object ProfileCompletion : AppRoute

     @Serializable
     data object Home : AppRoute

     @Serializable
     data object Services : AppRoute

     @Serializable
     data class ServiceDetails(val category: ServiceCategory) : AppRoute


     @Serializable
     data object Bookings : AppRoute

     @Serializable
     data object Profile : AppRoute

     @Serializable
     data object Wallet : AppRoute

     @Serializable
     data object AddFunds : AppRoute

     @Serializable
     data object AddPaymentMethod : AppRoute
     
     @Serializable
     data class Chat(val requestId: String) : AppRoute

     @Serializable
     data class NurseOnTheWay(val requestId: String) : AppRoute

     @Serializable
     data class VisitCompleted (val requestId: String): AppRoute
}
