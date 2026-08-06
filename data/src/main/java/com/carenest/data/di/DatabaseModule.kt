package com.carenest.data.di

import android.content.Context
import androidx.room.Room
import com.carenest.data.source.local.database.CareNestDatabase
import com.carenest.data.source.local.database.dao.ServiceHistoryDao
import com.carenest.data.source.local.database.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): CareNestDatabase =
        Room.databaseBuilder(
            context,
            CareNestDatabase::class.java,
            "carenest.db"
        ).fallbackToDestructiveMigration().build()

    @Provides
    fun provideUserDao(database: CareNestDatabase): UserDao = database.userDao()

    @Provides
    fun provideServiceHistoryDao(database: CareNestDatabase): ServiceHistoryDao = database.serviceHistoryDao()
}
