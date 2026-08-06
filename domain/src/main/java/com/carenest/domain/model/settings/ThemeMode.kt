package com.carenest.domain.model.settings

enum class ThemeMode {
    LIGHT,
    DARK,
    SYSTEM;

    companion object {
        fun fromStoredValue(value: String?): ThemeMode = entries.firstOrNull {
            it.name.equals(value, ignoreCase = true)
        } ?: SYSTEM
    }
}
