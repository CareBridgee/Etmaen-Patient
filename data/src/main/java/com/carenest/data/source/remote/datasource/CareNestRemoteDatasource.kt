package com.carenest.data.source.remote.datasource

import com.carenest.data.source.remote.dto.ServiceDto
import com.carenest.data.source.remote.dto.UserDto
import com.carenest.domain.model.home.Booking
import com.carenest.domain.model.home.User


interface CareNestRemoteDatasource {
    suspend fun getServices(): Result<List<ServiceDto>>
    suspend fun getUser() : Result<UserDto>
    suspend fun getUserRequestsHistory() : Result<List<Booking>>
}