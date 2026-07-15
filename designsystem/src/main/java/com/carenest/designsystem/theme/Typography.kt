package com.carenest.designsystem.theme


import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.carenest.designsystem.R

internal val defaultFontFamily: FontFamily
    get() = FontFamily(
        Font(R.font.cairo_extralight, FontWeight.ExtraLight),
        Font(R.font.cairo_light, FontWeight.Light),
        Font(R.font.cairo_regular, FontWeight.Normal),
        Font(R.font.cairo_medium, FontWeight.Medium),
        Font(R.font.cairo_semibold, FontWeight.SemiBold),
        Font(R.font.cairo_bold, FontWeight.Bold),
        Font(R.font.cairo_extrabold, FontWeight.ExtraBold),
        Font(R.font.cairo_black, FontWeight.Black)
    )

internal val englishFontFamily: FontFamily
    get() = FontFamily(
        Font(R.font.poppins_bold, FontWeight.ExtraLight),
        Font(R.font.poppins_regular, FontWeight.Light),
        Font(R.font.poppins_regular, FontWeight.Normal),
        Font(R.font.poppins_medium, FontWeight.Medium),
        Font(R.font.poppins_bold, FontWeight.SemiBold),
        Font(R.font.poppins_bold, FontWeight.Bold),
        Font(R.font.poppins_bold, FontWeight.ExtraBold),
        Font(R.font.poppins_bold, FontWeight.Black)
    )

data class SPTextStyle(
    val display: TextStyle,
    val title: TextStyle,
    val body: SizedTextStyle,
    val hint: SizedTextStyle,
    val displayMedium: TextStyle
)

internal val LocalSPTypography = staticCompositionLocalOf<SPTextStyle> {
    error("No typography provided")
}

internal val LocalSPFontFamily = staticCompositionLocalOf<FontFamily> {
    error("No font family provided")
}

data class SizedTextStyle(
    val large: TextStyle,
    val medium: TextStyle,
    val small: TextStyle
)

@Composable
internal fun spTypographyOf(fontFamily: FontFamily): SPTextStyle = SPTextStyle(
    display = TextStyle(
        fontFamily = fontFamily,
        fontSize = 40.sp,
        fontWeight = FontWeight.Bold,
        lineHeight = 42.sp
    ),
    displayMedium = TextStyle(
        fontFamily = fontFamily,
        fontSize = 26.sp,
        fontWeight = FontWeight.Bold,
        lineHeight = 34.sp
    ),
    title = TextStyle(
        fontFamily = fontFamily,
        fontSize = 24.sp,
        fontWeight = FontWeight.SemiBold,
        lineHeight = 32.sp
    ),
    body = SizedTextStyle(
        large = TextStyle(
            fontFamily = fontFamily,
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            lineHeight = 24.sp
        ),
        medium = TextStyle(
            fontFamily = fontFamily,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            lineHeight = 22.sp
        ),
        small = TextStyle(
            fontFamily = fontFamily,
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal,
            lineHeight = 18.sp
        )
    ),
    hint = SizedTextStyle(
        large = TextStyle(
            fontFamily = fontFamily,
            fontSize = 17.sp,
            fontWeight = FontWeight.Light,
            lineHeight = 26.sp
        ),
        medium = TextStyle(
            fontFamily = fontFamily,
            fontSize = 14.sp,
            fontWeight = FontWeight.Light,
            lineHeight = 20.sp
        ),
        small = TextStyle(
            fontFamily = fontFamily,
            fontSize = 12.sp,
            fontWeight = FontWeight.Light,
            lineHeight = 16.sp
        )
    )
)

@Composable
internal fun defaultSPTypographyForLanguage(languageCode: String): SPTextStyle =
    spTypographyOf(fontFamily = defaultFontFamily)
