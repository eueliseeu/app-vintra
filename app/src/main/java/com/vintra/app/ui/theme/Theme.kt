package com.vintra.app.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = ButtonSave,
    secondary = ButtonSave,
    tertiary = ButtonSave,
    background = DarkBackground,
    surface = DarkBackground,
    onBackground = WhiteText,
    onSurface = WhiteText,
    error = ButtonDanger,
    onError = WhiteText
)

private val LightColorScheme = lightColorScheme(
    primary = ButtonSave,
    secondary = ButtonSave,
    tertiary = ButtonSave,
    background = DarkBackground,
    surface = DarkBackground,
    onBackground = WhiteText,
    onSurface = WhiteText,
    error = ButtonDanger,
    onError = WhiteText
)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
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