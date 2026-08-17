package com.carenest.presentation.paymob

import android.app.Activity
import android.app.Application
import android.os.Bundle
import java.lang.ref.WeakReference
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrentActivityProvider @Inject constructor(
    application: Application,
) : Application.ActivityLifecycleCallbacks {
    private var currentActivityReference = WeakReference<Activity>(null)

    init {
        application.registerActivityLifecycleCallbacks(this)
    }

    fun currentActivity(): Activity? = currentActivityReference.get()

    override fun onActivityStarted(activity: Activity) {
        currentActivityReference = WeakReference(activity)
    }

    override fun onActivityResumed(activity: Activity) {
        currentActivityReference = WeakReference(activity)
    }

    override fun onActivityDestroyed(activity: Activity) {
        if (currentActivityReference.get() === activity) {
            currentActivityReference.clear()
        }
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) = Unit
    override fun onActivityPaused(activity: Activity) = Unit
    override fun onActivityStopped(activity: Activity) = Unit
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) = Unit
}
