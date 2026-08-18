package com.carenest.domain.model.payment

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class WalletTopUpAmountTest {
    @Test
    fun `valid EGP amount converts exactly to minor units`() {
        val amount = WalletTopUpAmount.parse("500.00").getOrThrow()

        assertEquals("500.00", amount.egp)
        assertEquals(50_000L, amount.minorUnits)
    }

    @Test
    fun `one decimal place is normalized to two places`() {
        val amount = WalletTopUpAmount.parse("12.5").getOrThrow()

        assertEquals("12.50", amount.egp)
        assertEquals(1_250L, amount.minorUnits)
    }

    @Test
    fun `zero negative malformed excessive scale and overflow are rejected`() {
        val invalidAmounts = listOf(
            "0",
            "0.00",
            "-1.00",
            "1.234",
            "abc",
            "999999999999999999999999.00",
        )

        invalidAmounts.forEach { raw ->
            assertTrue(
                "$raw should be invalid",
                WalletTopUpAmount.parse(raw).exceptionOrNull() is WalletException.InvalidAmount,
            )
        }
    }
}
