package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = FabWarmOrange,
    secondary = WarmCreamSurface,
    tertiary = PremiumBrownGold,
    background = DeepDarkBrown,
    surface = Color(0xFF382313),
    onPrimary = DeepDarkBrown,
    onSecondary = DeepDarkBrown,
    onTertiary = FabWarmOrange,
    onBackground = WarmCreamSurface,
    onSurface = WarmCreamSurface,
    surfaceVariant = Color(0xFF2E1C0F),
    onSurfaceVariant = WarmCreamSurface,
    outline = Color(0xFF4B3423)
)

private val LightColorScheme = lightColorScheme(
    primary = PremiumBrownGold,
    secondary = DeepDarkBrown,
    tertiary = MediumSpiceBrown,
    background = CleanMinimalBackground,
    surface = WarmCreamSurface,
    onPrimary = CleanMinimalBackground,
    onSecondary = CleanMinimalBackground,
    onTertiary = CleanMinimalBackground,
    onBackground = DarkNeutralText,
    onSurface = DarkNeutralText,
    surfaceVariant = LightCardBackground,
    onSurfaceVariant = MediumSpiceBrown,
    outline = SubtleBorder
)


@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Set to false to prioritize brand colors
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
