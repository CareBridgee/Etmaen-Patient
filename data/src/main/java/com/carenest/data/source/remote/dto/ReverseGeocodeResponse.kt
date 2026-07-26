package com.carenest.data.source.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReverseGeocodeResponse(
    @SerialName("display_name")
    val displayName: String?,
    @SerialName("lat")
    val lat: String?,
    @SerialName("lon")
    val lon: String?,
    @SerialName("address")
    val address: AddressDto?
)

@Serializable
data class AddressDto(
    @SerialName("house_number")
    val houseNumber: String? = null,
    @SerialName("road")
    val road: String? = null,
    @SerialName("suburb")
    val suburb: String? = null,
    @SerialName("city_district")
    val cityDistrict: String? = null,
    @SerialName("city")
    val city: String? = null,
    @SerialName("state")
    val state: String? = null,
    @SerialName("country")
    val country: String? = null,
    @SerialName("postcode")
    val postcode: String? = null
)
