package com.strathmore.groupworkmanager.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.Font
import androidx.core.view.ViewCompat

/**
 * Custom App theme for GroupWork Manager. Applies either the light or dark color scheme
 * depending on the system preference. Uses Material3 for typography and shapes.
 */
@Composable
fun GroupWorkTheme(
    useDarkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colors = if (useDarkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colors,
        typography = Typography(),
        content = content
    )
}