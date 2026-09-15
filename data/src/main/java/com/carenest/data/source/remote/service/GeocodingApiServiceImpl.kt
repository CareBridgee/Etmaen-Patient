package com.carenest.data.source.remote.service

import com.carenest.data.BuildConfig
import com.carenest.data.di.qualifier.LocationIQHttpClient
import com.carenest.data.source.remote.dto.ReverseGeocodeResponse
import com.carenest.domain.exception.NoInternetException
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.url
import io.ktor.http.isSuccess
import io.ktor.util.network.UnresolvedAddressException
import java.io.IOException
import javax.inject.Inject
import kotlinx.coroutines.CancellationException

class GeocodingApiServiceImpl @Inject constructor(
    @param:LocationIQHttpClient val httpClient: HttpClient
) : GeocodingApiService {

    override suspend fun reverseGeocode(
        latitude: Double,
        longitude: Double
    ): Result<ReverseGeocodeResponse> = try {
        val response = httpClient.get {
            url("https://us1.locationiq.com/v1/reverse")
            parameter("key", BuildConfig.location_iq_token)
            parameter("lat", latitude)
            parameter("lon", longitude)
            parameter("format", "json")
        }

        if (response.status.isSuccess()) {
            val body = response.body<ReverseGeocodeResponse>()
            Result.success(body)
        } else {
            Result.failure(Exception("LocationIQ API error: ${response.status}"))
        }
    } catch (c: CancellationException) {
        throw c
    } catch (e: IOException) {
        Result.failure(NoInternetException())
    } catch (e: UnresolvedAddressException) {
        Result.failure(NoInternetException())
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun forwardGeocode(query: String): Result<List<ReverseGeocodeResponse>> = try {
        val response = httpClient.get {
            url("https://us1.locationiq.com/v1/search")
            parameter("key", BuildConfig.location_iq_token)
            parameter("q", query)
            parameter("format", "json")
        }

        if (response.status.isSuccess()) {
            val body = response.body<List<ReverseGeocodeResponse>>()
            Result.success(body)
        } else {
            Result.failure(Exception("LocationIQ search API error: ${response.status}"))
        }
    } catch (c: CancellationException) {
        throw c
    } catch (e: IOException) {
        Result.failure(NoInternetException())
    } catch (e: UnresolvedAddressException) {
        Result.failure(NoInternetException())
    } catch (e: Exception) {
        Result.failure(e)
    }
}
