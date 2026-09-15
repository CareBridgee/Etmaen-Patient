package com.carenest.data.di.module

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import com.carenest.data.source.local.preferences.PreferenceKeys
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okio.Path.Companion.toPath
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object StorageModule{

    @Provides
    @Singleton
    fun provideDatastore(@ApplicationContext context: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.createWithPath(
            corruptionHandler = ReplaceFileCorruptionHandler { emptyPreferences() },
            produceFile = {
                context.filesDir
                    .resolve(PreferenceKeys.DATA_STORE_FILE_NAME)
                    .absolutePath
                    .toPath()
            }
        )
    }
}