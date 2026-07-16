package com.carenest.presentation.navigation

import androidx.navigation3.runtime.NavKey
import androidx.savedstate.serialization.SavedStateConfiguration
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

/**
 * Navigation 3 stores destination objects in the back stack instead of
 * String routes. To restore the navigation stack after process death or
 * configuration changes, every AppRoute must be registered here.
 */

object NavigationConfig {

    val serializer = SerializersModule {
        polymorphic(NavKey::class) {
            subclass(AppRoute.Splash::class ,AppRoute.Splash.serializer())
        }
    }

    val savedStateConfiguration = SavedStateConfiguration {
        serializersModule = serializer
    }
}