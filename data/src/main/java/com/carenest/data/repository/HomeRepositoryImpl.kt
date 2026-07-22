package com.carenest.data.repository

import com.carenest.data.source.local.datasource.home.HomeDatasource
import com.carenest.domain.model.home.ServiceCategory
import com.carenest.domain.model.home.Booking
import com.carenest.domain.model.home.HealthcareService
import com.carenest.domain.model.home.User
import com.carenest.domain.repository.HomeRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeRepositoryImpl @Inject constructor(
    private val homeDatasource: HomeDatasource
) : HomeRepository {

    override suspend fun getUser(): Result<User> {
        return homeDatasource.getUser()
    }

    override suspend fun getServices(): Result<List<HealthcareService>> {
        return homeDatasource.getServices()
    }

    override suspend fun getUpcomingBooking(): Result<Booking?> {
        return homeDatasource.getUpcomingBooking()
    }
}
