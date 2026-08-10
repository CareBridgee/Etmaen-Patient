package com.carenest.presentation.ui.components

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.carenest.presentation.R
import java.util.Calendar
import kotlinx.coroutines.delay

private enum class GreetingPeriod(
    @param:StringRes val namedGreetingRes: Int,
    @param:StringRes val genericGreetingRes: Int,
) {
    Morning(
        namedGreetingRes = R.string.profile_greeting_morning,
        genericGreetingRes = R.string.profile_greeting_morning_generic,
    ),
    Afternoon(
        namedGreetingRes = R.string.profile_greeting_day,
        genericGreetingRes = R.string.profile_greeting_day_generic,
    ),
    Evening(
        namedGreetingRes = R.string.profile_greeting_evening,
        genericGreetingRes = R.string.profile_greeting_evening_generic,
    ),
}

@Composable
fun rememberTimeBasedGreeting(userName: String?): String {
    var period by remember { mutableStateOf(currentGreetingPeriod()) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(GREETING_REFRESH_INTERVAL_MS)
            period = currentGreetingPeriod()
        }
    }

    val trimmedName = userName?.trim().orEmpty()
    return if (trimmedName.isBlank()) {
        stringResource(period.genericGreetingRes)
    } else {
        stringResource(period.namedGreetingRes, trimmedName)
    }
}

private fun currentGreetingPeriod(): GreetingPeriod =
    when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
        in 5..11 -> GreetingPeriod.Morning
        in 12..17 -> GreetingPeriod.Afternoon
        else -> GreetingPeriod.Evening
    }

private const val GREETING_REFRESH_INTERVAL_MS = 60_000L
