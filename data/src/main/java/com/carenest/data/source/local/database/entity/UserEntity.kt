package com.carenest.data.source.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "current_user")
data class UserEntity(
    @PrimaryKey val id: String,
    val phoneNumber: String,
    val email: String?,
    val firstName: String?,
    val lastName: String?,
    val dateOfBirth: String?,
    val gender: String?,
    val profileImageUrl: String?,
    val isDeleted: Boolean,
    val createdAt: String?,
    val updatedAt: String?,
    val lastLoginAt: String?,
    val defaultProfileId: String?
)
