package com.example.dogizzy.ui.theme

import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorPalette = darkColorScheme(
    primary = Orange,
    onPrimary = Peach,
    secondary = Pink,
    onSecondary = PinkButton,
    tertiary = ChatColor,
    surface = White,
    onSurface = Black
)

private val LightColorPalette = lightColorScheme(
    primary = Orange,
    onPrimary = Peach,
    secondary = Pink,
    onSecondary = PinkButton,
    tertiary = ChatColor,
    surface = Black,
    onSurface = White
)



@Composable
fun DogizzyTheme(darkTheme: Boolean = isNightMode(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }


    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        content = content
    )
}

@Composable
fun isNightMode() = when (AppCompatDelegate.getDefaultNightMode()) {
    AppCompatDelegate.MODE_NIGHT_NO -> false
    AppCompatDelegate.MODE_NIGHT_YES -> true
    else -> isSystemInDarkTheme()
}