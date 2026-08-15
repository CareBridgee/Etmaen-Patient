package com.carenest.data.source.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "service_history")
data class ServiceHistoryEntity(
    @PrimaryKey val serviceRequestId: String,
    val serviceTypeId: String,
    val serviceName: String,
    val serviceDescription: String,
    val preferredDate: String,
    val hour: Int,
    val minute: Int,
    val second: Int,
    val nano: Int,
    val status: String,
    val nurseId: String?,
    val nurseName: String?,
    val nurseImage: String? = null,
    val createdAt: String,
    val updatedAt: String,
    val nurseProfileImageUrl: String? = null
)
