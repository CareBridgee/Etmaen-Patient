package com.carenest.data.source.local.datasource.home

import com.carenest.domain.model.home.Booking
import com.carenest.domain.model.home.HealthcareService

import com.carenest.domain.model.home.User
import kotlinx.coroutines.delay
import javax.inject.Inject

class HomeFakeDatasourceImpl @Inject constructor() : HomeDatasource {
    override suspend fun getUser(): Result<User> {
        delay(300)
        return Result.success(
            User(
                id = "usr_001",
                name = "Elena",
                avatarUrl = null
            )
        )
    }

    override suspend fun getServices(): Result<List<HealthcareService>> {
        delay(400)
        val services = listOf(
            HealthcareService(
                id = "srv_1",
                name = "General Nursing",
                iconResName = "ic_heart_beat",
                estimatedDurationMinutes = 1L,
                basePrice = 1.0,
                description = "TODO()"
            ),
            HealthcareService(
                id = "srv_2",
                name = "Injection Service",
                iconResName = "ic_syringe",
                estimatedDurationMinutes = 1L,
                basePrice = 1.0,
                description = "TODO()"
            ),
            HealthcareService(
                id = "srv_3",
                name = "Blood Collection",
                iconResName = "ic_pill",
                estimatedDurationMinutes = 1L,
                basePrice = 1.0,
                description = "TODO()"
            ),
            HealthcareService(
                id = "srv_4",
                name = "IV Therapy",
                iconResName = "ic_syringe",
                estimatedDurationMinutes = 1L,
                basePrice = 1.0,
                description = "TODO()"
            ),
            HealthcareService(
                id = "srv_5",
                name = "Wound Dressing",
                iconResName = "ic_physical_therapy",
                estimatedDurationMinutes = 1L,
                basePrice = 1.0,
                description = "TODO()"
            ),
            HealthcareService(
                id = "srv_6",
                name = "Physical Therapy",
                iconResName = "ic_physical_therapy",
                estimatedDurationMinutes = 1L,
                basePrice = 1.0,
                description = "TODO()"
            ),
            HealthcareService(
                id = "srv_7",
                name = "Post Natal",
                iconResName = "ic_heart_beat",
                estimatedDurationMinutes = 1L,
                basePrice = 1.0,
                description = "TODO()"
            ),
            HealthcareService(
                id = "srv_8",
                name = "Elderly Care",
                iconResName = "ic_heart_beat",
                estimatedDurationMinutes = 1L,
                basePrice = 1.0,
                description = "TODO()"
            ),
            HealthcareService(
                id = "srv_9",
                name = "Vaccinations",
                iconResName = "ic_syringe",
                estimatedDurationMinutes = 1L,
                basePrice = 1.0,
                description = "TODO()"
            )
        )
        return Result.success(services)
    }

    override suspend fun getUpcomingBooking(): Result<Booking?> {
        delay(500)
        val booking = Booking(
            id = "bk_001",
            providerName = "Nurse Sarah Jenkins",
            serviceName = "General Nursing Care",
            timeText = "Today, 02:30 PM",
            statusText = "Confirmed",
            avatarUrl = null
        )
        return Result.success(booking)
    }
}
