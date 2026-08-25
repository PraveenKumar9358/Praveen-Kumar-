package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = ShopnovaBlueLight,
    onPrimary = Color.White,
    primaryContainer = ShopnovaBlueDark,
    onPrimaryContainer = Color.White,
    secondary = ShopnovaGold,
    onSecondary = Color.Black,
    tertiary = ShopnovaGreen,
    background = DarkBackground,
    surface = DarkSurface,
    onBackground = DarkTextPrimary,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurfaceElevated,
    onSurfaceVariant = DarkTextSecondary,
    outline = DarkBorder
)

private val LightColorScheme = lightColorScheme(
    primary = ShopnovaBlue,
    onPrimary = Color.White,
    primaryContainer = ShopnovaBlueSoft,
    onPrimaryContainer = ShopnovaBlueDark,
    secondary = ShopnovaGold,
    onSecondary = Color.White,
    tertiary = ShopnovaGreen,
    background = MarketplaceBackground,
    surface = SurfaceWhite,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceElevated,
    onSurfaceVariant = TextSecondary,
    outline = BorderLight
)

@Composable
fun ShopnovaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
