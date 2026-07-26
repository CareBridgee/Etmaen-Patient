package com.carenest.data.source.local.preferences

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey


internal object PreferenceKeys {
    internal const val DATA_STORE_FILE_NAME = "app_preferences.preferences_pb"

    val IS_ONBOARDING_DONE = booleanPreferencesKey("IS_ONBOARDING_DONE")
    val IS_LOGGED_IN = booleanPreferencesKey("IS_LOGGED_IN")
    val ACCESS_TOKEN = stringPreferencesKey("ACCESS_TOKEN")
    val REFRESH_TOKEN = stringPreferencesKey("REFRESH_TOKEN")

}
