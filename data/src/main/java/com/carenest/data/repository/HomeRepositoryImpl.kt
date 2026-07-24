package com.carenest.data.repository

import com.carenest.data.di.IoDispatcher
import com.carenest.data.source.local.datasource.home.HomeDatasource
import com.carenest.domain.model.home.Booking
import com.carenest.domain.model.home.HealthcareService
import com.carenest.domain.model.home.User
import com.carenest.domain.repository.HomeRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeRepositoryImpl @Inject constructor(
    private val homeDatasource: HomeDatasource,
    @IoDispatcher private val dispatcher: CoroutineDispatcher = Dispatchers.IO
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
