package com.carenest.data.source.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.carenest.data.source.local.database.entity.ServiceHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ServiceHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<ServiceHistoryEntity>)

    @Query("SELECT * FROM service_history")
    fun getAllHistory(): Flow<List<ServiceHistoryEntity>>

    @Query("SELECT * FROM service_history WHERE serviceRequestId = :requestId")
    suspend fun getHistoryById(requestId: String): ServiceHistoryEntity?

    @Query("DELETE FROM service_history")
    suspend fun clearAll()
}
