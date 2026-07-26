package com.carenest.designsystem.theme

import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.content.res.Resources
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.carenest.designsystem.dimensions.LocalSPShapes
import com.carenest.designsystem.dimensions.LocalSPSize
import com.carenest.designsystem.dimensions.LocalSPSpacing
import com.carenest.designsystem.dimensions.SPShapes
import com.carenest.designsystem.dimensions.SPSize
import com.carenest.designsystem.dimensions.SPSpacing
import java.util.Locale

@Composable
fun SpTheme(
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    languageCode: String = "en",
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val locale = remember(languageCode) { Locale.forLanguageTag(languageCode) }

    val localizedContext = remember(context, locale) {
        LocalizedContextWrapper(context, locale)
    }

    val localizedConfiguration = remember(context, locale) {
        Configuration(context.resources.configuration).apply {
            setLocale(locale)
        }
    }

    val colors = remember(isDarkTheme) {
        if (isDarkTheme) darkColors else lightColors
    }

    val typography = defaultSPTypographyForLanguage(languageCode)

    val layoutDirection = remember(languageCode) {
        if (isRtlLanguage(languageCode)) LayoutDirection.Rtl else LayoutDirection.Ltr
    }

    val fontFamily = if (languageCode == "ar") defaultFontFamily else englishFontFamily

    CompositionLocalProvider(
        LocalContext provides localizedContext,
        LocalConfiguration provides localizedConfiguration,
        LocalLayoutDirection provides layoutDirection,
        localSPColorScheme provides colors,
        LocalSPTypography provides typography,
        LocalSPFontFamily provides fontFamily,
        LocalSPSpacing provides remember { SPSpacing() },
        LocalSPShapes provides remember { SPShapes() },
        LocalSPSize provides remember { SPSize() },
        content = content,
    )
}

private class LocalizedContextWrapper(
    base: Context,
    private val locale: Locale
) : ContextWrapper(base) {
    private var localizedResources: Resources? = null

    override fun getResources(): Resources {
        if (localizedResources == null) {
            val config = Configuration(super.getResources().configuration)
            config.setLocale(locale)
            localizedResources = createConfigurationContext(config).resources
        }
        return localizedResources!!
    }
}

private fun isRtlLanguage(languageCode: String): Boolean =
    when (languageCode) {
        "ar", "fa", "he", "iw", "ur" -> true
        else -> false
    }