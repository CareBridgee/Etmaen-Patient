package com.carenest.data.source.local.preferences

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

internal object PreferenceKeys {
    internal const val DATA_STORE_FILE_NAME = "app_preferences.preferences_pb"

    val IS_ONBOARDING_DONE = booleanPreferencesKey("IS_ONBOARDING_DONE")
    val IS_LOGGED_IN = booleanPreferencesKey("IS_LOGGED_IN")
    val LANGUAGE_CODE = stringPreferencesKey("LANGUAGE_CODE")
    val IS_DARK_MODE = booleanPreferencesKey("IS_DARK_MODE")
    val EMAIL_UPDATES = booleanPreferencesKey("EMAIL_UPDATES")
    val SMS_ALERTS = booleanPreferencesKey("SMS_ALERTS")
}