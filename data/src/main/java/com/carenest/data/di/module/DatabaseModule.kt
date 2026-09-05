package com.carenest.data.di.module

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import com.carenest.data.source.local.database.CareNestDatabase
import com.carenest.data.source.local.database.dao.ServiceHistoryDao
import com.carenest.data.source.local.database.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    private val migration2To3 = Migration(2, 3) { database ->
        database.execSQL(
            "ALTER TABLE service_history ADD COLUMN nurseProfileImageUrl TEXT"
        )
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): CareNestDatabase =
        Room.databaseBuilder(
            context,
            CareNestDatabase::class.java,
            "carenest.db"
        )
            .addMigrations(migration2To3)
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()

    @Provides
    fun provideUserDao(database: CareNestDatabase): UserDao = database.userDao()

    @Provides
    fun provideServiceHistoryDao(database: CareNestDatabase): ServiceHistoryDao = database.serviceHistoryDao()
}