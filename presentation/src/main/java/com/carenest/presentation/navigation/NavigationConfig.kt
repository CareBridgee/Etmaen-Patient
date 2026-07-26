package com.carenest.presentation.navigation

import androidx.navigation3.runtime.NavKey
import androidx.savedstate.serialization.SavedStateConfiguration
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.modules.PolymorphicModuleBuilder
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.serializer
import kotlin.reflect.KClass

object NavigationConfig {

    @OptIn(InternalSerializationApi::class)
    private fun <T : NavKey> PolymorphicModuleBuilder<NavKey>.serializableSubclass(kClass: KClass<T>) {
        subclass(kClass, kClass.serializer())
    }

    val serializer = SerializersModule {
        polymorphic(NavKey::class) {
            serializableSubclass(AppRoute.Splash::class)
            serializableSubclass(AppRoute.OnBoarding::class)
            serializableSubclass(AppRoute.Login::class)
            serializableSubclass(AppRoute.Otp::class)
            serializableSubclass(AppRoute.Register::class)
            serializableSubclass(AppRoute.ProfileCompletion::class)
            serializableSubclass(AppRoute.Home::class)
            serializableSubclass(AppRoute.Services::class)
            serializableSubclass(AppRoute.ServiceDetails::class)
            serializableSubclass(AppRoute.Bookings::class)
            serializableSubclass(AppRoute.Profile::class)
            serializableSubclass(AppRoute.FamilyMembers::class)
            serializableSubclass(AppRoute.Settings::class)
            serializableSubclass(AppRoute.ChoosePatient::class)
            serializableSubclass(AppRoute.NurseOnTheWay::class)
        }
    }

    val savedStateConfiguration = SavedStateConfiguration {
        serializersModule = serializer
    }
}
