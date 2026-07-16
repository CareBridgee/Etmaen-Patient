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
     data object Login : AppRoute

     @Serializable
     data object Register : AppRoute

}