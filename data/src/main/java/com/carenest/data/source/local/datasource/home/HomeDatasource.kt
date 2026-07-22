package com.carenest.data.source.local.datasource.home

import com.carenest.domain.model.home.Booking
import com.carenest.domain.model.home.HealthcareService
import com.carenest.domain.model.home.User

interface HomeDatasource {
    suspend fun getUser(): Result<User>
    suspend fun getServices(): Result<List<HealthcareService>>
    suspend fun getUpcomingBooking(): Result<Booking?>
}
