package com.carenest.data.mapper

import com.carenest.domain.model.ThemeMode

internal fun String?.toThemeMode(): ThemeMode = ThemeMode.entries.firstOrNull {
    it.name.equals(this, ignoreCase = true)
} ?: ThemeMode.SYSTEM
