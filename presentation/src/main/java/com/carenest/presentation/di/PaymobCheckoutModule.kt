package com.carenest.presentation.di

import com.carenest.domain.repository.PaymobNativeCheckoutLauncher
import com.carenest.presentation.paymob.PaymobNativeCheckoutLauncherImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PaymobCheckoutModule {
    @Binds
    @Singleton
    abstract fun bindPaymobNativeCheckoutLauncher(
        impl: PaymobNativeCheckoutLauncherImpl,
    ): PaymobNativeCheckoutLauncher
}
