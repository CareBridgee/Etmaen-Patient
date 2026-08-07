package com.carenest.data.mapper.history

import com.carenest.data.source.local.database.entity.ServiceHistoryEntity
import com.carenest.data.source.remote.dto.history.ServiceHistoryDto
import com.carenest.data.source.remote.dto.history.PreferredTimeDto
import com.carenest.domain.model.history.ServiceHistory
import com.carenest.domain.model.history.PreferredTime

fun ServiceHistoryDto.toDomain(): ServiceHistory {
    return ServiceHistory(
        serviceRequestId = serviceRequestId.orEmpty(),
        serviceTypeId = serviceTypeId.orEmpty(),
        serviceName = serviceName.orEmpty(),
        serviceDescription = serviceDescription.orEmpty(),
        preferredDate = preferredDate.orEmpty(),
        preferredTime = preferredTime?.toDomain() ?: PreferredTime(0, 0),
        status = status.orEmpty(),
        nurseId = nurseId,
        nurseName = nurseName,
        createdAt = createdAt.orEmpty(),
        updatedAt = updatedAt.orEmpty()
    )
}

fun PreferredTimeDto.toDomain(): PreferredTime {
    return PreferredTime(
        hour = hour ?: 0,
        minute = minute ?: 0,
        second = second ?: 0,
        nano = nano ?: 0
    )
}

fun ServiceHistory.toEntity(): ServiceHistoryEntity {
    return ServiceHistoryEntity(
        serviceRequestId = serviceRequestId,
        serviceTypeId = serviceTypeId,
        serviceName = serviceName,
        serviceDescription = serviceDescription,
        preferredDate = preferredDate,
        hour = preferredTime.hour,
        minute = preferredTime.minute,
        second = preferredTime.second,
        nano = preferredTime.nano,
        status = status,
        nurseId = nurseId,
        nurseName = nurseName,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun ServiceHistoryEntity.toDomain(): ServiceHistory {
    return ServiceHistory(
        serviceRequestId = serviceRequestId,
        serviceTypeId = serviceTypeId,
        serviceName = serviceName,
        serviceDescription = serviceDescription,
        preferredDate = preferredDate,
        preferredTime = PreferredTime(
            hour = hour,
            minute = minute,
            second = second,
            nano = nano
        ),
        status = status,
        nurseId = nurseId,
        nurseName = nurseName,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
