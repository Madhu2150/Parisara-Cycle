package com.parisara.cycle.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary          = GreenPrimary,
    onPrimary        = androidx.compose.ui.graphics.Color.White,
    primaryContainer = GreenContainer,
    onPrimaryContainer = GreenDark,
    secondary        = EcoAmber,
    tertiary         = SkyBlue,
    error            = HazardRed,
    surface          = SurfaceWhite,
    onSurface        = OnSurfaceDark,
    background       = SurfaceWhite,
)

@Composable
fun ParisaraCycleTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context)
        else dynamicLightColorScheme(context)
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = Typography,
        content     = content
    )
}