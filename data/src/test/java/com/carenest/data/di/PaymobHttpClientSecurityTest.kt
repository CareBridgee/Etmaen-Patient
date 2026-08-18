package com.carenest.data.di

import com.carenest.data.source.local.preferences.AuthTokens
import com.carenest.data.source.local.preferences.CarenestDatastore
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respondOk
import io.ktor.client.request.get
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class PaymobHttpClientSecurityTest {
    @Test
    fun `Paymob authorization attaches only to exact official host`() = runTest {
        var authorization: String? = null
        val client = paymobClient(
            requestedAuthorization = { authorization = it },
            allowedHost = "accept.paymob.com",
        )

        client.get("https://accept.paymob.com/v1/intention/")

        assertEquals("Token test-secret", authorization)
        client.close()
    }

    @Test
    fun `Paymob authorization is not attached to lookalike hosts`() = runTest {
        val hosts = listOf(
            "accept.paymob.com.evil.test",
            "evilaccept.paymob.com",
            "checkout.accept.paymob.com",
        )

        hosts.forEach { host ->
            var authorization: String? = null
            val client = paymobClient(
                requestedAuthorization = { authorization = it },
                allowedHost = "accept.paymob.com",
            )

            client.get("https://$host/v1/intention/")

            assertNull("$host must not receive Paymob auth", authorization)
            client.close()
        }
    }

    @Test
    fun `CareNest bearer token never reaches Paymob`() = runTest {
        var authorization: String? = null
        val client = HttpClient(MockEngine) {
            installBearerAuthentication(FakeCarenestDatastore())
            engine {
                addHandler { request ->
                    authorization = request.headers[HttpHeaders.Authorization]
                    respondOk("{}")
                }
            }
        }

        client.get("https://accept.paymob.com/v1/intention/")

        assertNull(authorization)
        client.close()
    }

    @Test
    fun `Paymob credential never reaches CareNest`() = runTest {
        var authorization: String? = null
        val client = paymobClient(
            requestedAuthorization = { authorization = it },
            allowedHost = "accept.paymob.com",
        )

        client.get("https://api.carenest.test/api/v1/users/user-id/credit")

        assertNull(authorization)
        client.close()
    }

    private fun paymobClient(
        requestedAuthorization: (String?) -> Unit,
        allowedHost: String,
    ): HttpClient =
        HttpClient(MockEngine) {
            installPaymobSecretAuthorization(
                secretKey = "test-secret",
                allowedHost = allowedHost,
            )
            engine {
                addHandler { request ->
                    requestedAuthorization(request.headers[HttpHeaders.Authorization])
                    respondOk("{}")
                }
            }
        }
}

private class FakeCarenestDatastore : CarenestDatastore {
    override val isOnBoardingDone: Flow<Boolean> = flowOf(true)
    override val isLoggedIn: Flow<Boolean> = flowOf(true)
    override val authTokens: Flow<AuthTokens?> = flowOf(AuthTokens("care-token", "refresh-token"))
    override val languageCode: Flow<String> = flowOf("en")
    override val themeMode: Flow<String> = flowOf("SYSTEM")
    override val userId: Flow<String> = flowOf("user-id")

    override suspend fun setOnboardingDone(done: Boolean) = Unit
    override suspend fun setLoggedIn(done: Boolean) = Unit
    override suspend fun saveAuthTokens(accessToken: String, refreshToken: String) = Unit
    override suspend fun clearAuthTokens() = Unit
    override suspend fun setLanguageCode(languageCode: String) = Unit
    override suspend fun setThemeMode(themeMode: String) = Unit
    override suspend fun setUserId(id: String) = Unit
    override suspend fun clearAll() = Unit
    override suspend fun clearUserId() = Unit
}
