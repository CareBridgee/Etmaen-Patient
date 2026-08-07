package com.carenest.data.mapper.settings

import com.carenest.domain.model.settings.ThemeMode

internal fun String?.toThemeMode(): ThemeMode = ThemeMode.entries.firstOrNull {
    it.name.equals(this, ignoreCase = true)
} ?: ThemeMode.SYSTEM
