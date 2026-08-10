package com.carenest.data.utils

/**
 * Parses a time string (expected format HH:mm:ss or HH:mm) into hour and minute.
 * Returns a Pair of (hour, minute). Defaults to (0, 0) if parsing fails.
 */
fun parseTimeString(timeStr: String?): Pair<Int, Int> {
    if (timeStr == null) return Pair(0, 0)
    return try {
        val parts = timeStr.split(":")
        val hour = parts.getOrNull(0)?.toIntOrNull() ?: 0
        val minute = parts.getOrNull(1)?.toIntOrNull() ?: 0
        Pair(hour, minute)
    } catch (e: Exception) {
        Pair(0, 0)
    }
}
