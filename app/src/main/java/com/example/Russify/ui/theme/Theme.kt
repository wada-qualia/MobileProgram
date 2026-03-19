package com.example.Russify.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Composable
fun JopaPisunTheme(
    useOceanTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val DarkColorScheme = darkColorScheme(
        primary = if (useOceanTheme) OceanActiveIconColor else PinkActiveIconColor,
        background = if (useOceanTheme) OceanDarkBackground else PinkDarkBackground,
        surface = if (useOceanTheme) OceanBottomNavColor else PinkBottomNavColor,
        onPrimary = PinkTextWhite,
        onBackground = PinkTextWhite,
        onSurface = PinkTextWhite,
    )

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = (if (useOceanTheme) OceanHeaderGradientEnd else PinkHeaderGradientEnd).toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}