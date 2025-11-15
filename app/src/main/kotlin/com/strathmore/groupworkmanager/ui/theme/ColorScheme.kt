package com.strathmore.groupworkmanager.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Define light and dark color schemes using our custom colors defined in resources
val LightColors = lightColorScheme(
    primary = Color(0xFF003366),
    onPrimary = Color.White,
    secondary = Color(0xFFFFCC33),
    onSecondary = Color.Black,
    error = Color(0xFFCC0000),
    onError = Color.White,
    background = Color.White,
    onBackground = Color.Black,
    surface = Color.White,
    onSurface = Color.Black
)

val DarkColors = darkColorScheme(
    primary = Color(0xFFFFCC33),
    onPrimary = Color.Black,
    secondary = Color(0xFFCC0000),
    onSecondary = Color.Black,
    error = Color(0xFFCC0000),
    onError = Color.White,
    background = Color(0xFF121212),
    onBackground = Color.White,
    surface = Color(0xFF121212),
    onSurface = Color.White
)