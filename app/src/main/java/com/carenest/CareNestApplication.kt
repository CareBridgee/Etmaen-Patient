package com.carenest

import android.app.Application
import com.carenest.presentation.paymob.CurrentActivityProvider
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class CareNestApplication : Application() {
    @Inject
    lateinit var currentActivityProvider: CurrentActivityProvider

    override fun onCreate() {
        super.onCreate()
        currentActivityProvider.currentActivity()
    }
}
