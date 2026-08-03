import re

color_kt = """package com.example.ui.theme

import androidx.compose.ui.graphics.Color

val Purple500 = Color(0xFF7B61FF)
val Pink400 = Color(0xFFF38BDA)
val Blue400 = Color(0xFF63C7FF)
val Navy900 = Color(0xFF1D2340)
val Lilac50 = Color(0xFFF7F4FB)
val Lilac200 = Color(0xFFE7E1F2)

val Purple700 = Color(0xFF5B3FE0)
val Purple600 = Color(0xFF6A4EF0)
val Purple300 = Color(0xFFB9A6FB)
val Purple100 = Color(0xFFEEEAFE)
val Purple50 = Color(0xFFF7F4FE)

val Lilac100 = Color(0xFFF3F1FB)
val Lilac300 = Color(0xFFDCD4EE)
val Pink600 = Color(0xFFDC4C93)
val Pink300 = Color(0xFFF9B4E4)
val Pink100 = Color(0xFFFDECF5)

val Blue600 = Color(0xFF2E7FC4)
val Blue300 = Color(0xFFA6DDFF)
val Blue100 = Color(0xFFE4F1FE)

val Navy700 = Color(0xFF3A4160)
val Navy500 = Color(0xFF6B7192)
val Navy300 = Color(0xFF9AA0BC)
val Navy250 = Color(0xFFBCB9C8)
val Navy200 = Color(0xFFC3C7DA)
val White = Color(0xFFFFFFFF)

val Rose600 = Color(0xFFD0605F)
val Rose100 = Color(0xFFFFF2F4)
val Green600 = Color(0xFF4E7A5B)
val Green100 = Color(0xFFEDF4ED)

val Mint500 = Color(0xFF3DBFA0)
val Mint300 = Color(0xFFA8E6D4)
val Mint100 = Color(0xFFE4F7F1)
val Amber600 = Color(0xFFF59A2E)
val Amber300 = Color(0xFFFDD9A0)
val Amber100 = Color(0xFFFDF0DC)

// Semantic colors
val TextPrimary = Navy900
val TextSecondary = Navy500
val TextMuted = Navy300
val TextDisabled = Navy250
val SurfacePage = Lilac50
val SurfaceCard = White
val SurfaceTint = Color(0xFFF5F1FC)
val SurfaceBrandSoft = Purple100
val BorderDefault = Lilac200
val BorderStrong = Lilac300
val BorderInput = Color(0xFFE9E9F1)
"""

with open("app/src/main/java/com/example/ui/theme/Color.kt", "w") as f:
    f.write(color_kt)

theme_kt = """package com.example.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = Purple600,
    onPrimary = White,
    primaryContainer = Purple100,
    onPrimaryContainer = Purple700,
    
    secondary = Pink400,
    onSecondary = White,
    secondaryContainer = Pink100,
    onSecondaryContainer = Pink600,
    
    tertiary = Blue400,
    onTertiary = White,
    tertiaryContainer = Blue100,
    onTertiaryContainer = Blue600,
    
    background = SurfacePage,
    onBackground = TextPrimary,
    
    surface = SurfaceCard,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceTint,
    onSurfaceVariant = TextSecondary,
    
    error = Rose600,
    onError = White,
    errorContainer = Rose100,
    onErrorContainer = Rose600,
    
    outline = BorderDefault,
    outlineVariant = BorderInput
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false, // Force light theme to match design system
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // Disable dynamic color to enforce our branding
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()
            WindowCompat.setDecorFitsSystemWindows(window, false)
            val wic = WindowCompat.getInsetsController(window, view)
            wic.isAppearanceLightStatusBars = true
            wic.isAppearanceLightNavigationBars = true
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
"""

with open("app/src/main/java/com/example/ui/theme/Theme.kt", "w") as f:
    f.write(theme_kt)

