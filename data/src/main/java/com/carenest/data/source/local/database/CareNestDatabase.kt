package com.carenest.data.source.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.carenest.data.source.local.database.dao.ServiceHistoryDao
import com.carenest.data.source.local.database.dao.UserDao
import com.carenest.data.source.local.database.entity.ServiceHistoryEntity
import com.carenest.data.source.local.database.entity.UserEntity

@Database(
    entities = [UserEntity::class, ServiceHistoryEntity::class],
    version = 5,
    exportSchema = true
)
abstract class CareNestDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun serviceHistoryDao(): ServiceHistoryDao
}
