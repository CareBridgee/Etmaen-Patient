package com.carenest.presentation.model

import com.carenest.domain.model.LocationDetails
import kotlinx.serialization.Serializable

@Serializable
data class LocationDetailsArg(
    val address: String,
    val apartment: String,
    val district: String,
    val city: String,
    val latitude: Double,
    val longitude: Double,
    val street: String = "",
    val building: String = "",
    val area: String = "",
    val landmark: String = "",
    val country: String = ""
)

fun LocationDetails.toArg() = LocationDetailsArg(
    address = address, apartment = apartment, district = district,
    city = city, latitude = latitude, longitude = longitude,
    street = street, building = building, area = area,
    landmark = landmark, country = country
)

fun LocationDetailsArg.toDomain() = LocationDetails(
    address = address, apartment = apartment, district = district,
    city = city, latitude = latitude, longitude = longitude,
    street = street, building = building, area = area,
    landmark = landmark, country = country
)