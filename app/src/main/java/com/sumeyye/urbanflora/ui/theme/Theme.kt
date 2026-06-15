package com.sumeyye.urbanflora.ui.theme

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
    primary = GreenSoft,
    secondary = PeachWarm,
    tertiary = PinkLight,
    background = DeepText,
    surface = DeepText,
    onPrimary = DeepText,
    onBackground = SoftBackground,
    onSurface = SoftBackground
)

private val LightColorScheme = lightColorScheme(
    primary = GreenDeep,
    onPrimary = Color.White,
    primaryContainer = GreenSoft.copy(alpha = 0.3f),
    onPrimaryContainer = GreenDeep,
    secondary = CoralMain,
    onSecondary = Color.White,
    secondaryContainer = PeachWarm,
    onSecondaryContainer = DeepText,
    tertiary = PinkLight,
    background = SoftBackground,
    surface = Color.White,
    onBackground = DeepText,
    onSurface = DeepText,
    surfaceVariant = GreenSoft.copy(alpha = 0.2f)
)

@Composable
fun UrbanFloraTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
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
