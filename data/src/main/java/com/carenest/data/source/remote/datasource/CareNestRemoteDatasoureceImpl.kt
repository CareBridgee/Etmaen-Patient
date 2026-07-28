package com.carenest.data.source.remote.datasource

import com.carenest.data.source.remote.dto.ServiceDto
import com.carenest.data.source.remote.dto.UserDto
import com.carenest.data.source.remote.service.CareNestApiService
import com.carenest.domain.model.home.Booking
import kotlinx.coroutines.delay
import javax.inject.Inject


class CareNestRemoteDataSourceImpl @Inject constructor(
    private val careNestApiService: CareNestApiService
): CareNestRemoteDatasource {
    override suspend fun getServices(): Result<List<ServiceDto>> {
        return careNestApiService.getServices()
    }

    override suspend fun getUser(): Result<UserDto> {
        return careNestApiService.getUser()
    }

    override suspend fun getUserRequestsHistory(): Result<List<Booking>> {
        delay(500)
        val booking =listOf<Booking> (
            Booking(
            id = "bk_001",
            providerName = "Nurse Sarah Jenkins",
            serviceName = "General Nursing Care",
            timeText = "Today, 02:30 PM",
            statusText = "Confirmed",
            avatarUrl = null
        ),
            Booking(
            id = "bk_001",
            providerName = "Nurse Sarah Jenkins",
            serviceName = "General Nursing Care",
            timeText = "Today, 02:30 PM",
            statusText = "Confirmed",
            avatarUrl = null
        ),
            )
        return Result.success(booking)
    }
}