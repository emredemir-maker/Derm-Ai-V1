with open("app/src/main/java/com/example/ui/theme/Color.kt", "w") as f:
    f.write("""package com.example.ui.theme

import androidx.compose.ui.graphics.Color

val md_theme_light_primary = Color(0xFF5B3CDD)
val md_theme_light_onPrimary = Color(0xFFFFFFFF)
val md_theme_light_primaryContainer = Color(0xFF7459F7)
val md_theme_light_onPrimaryContainer = Color(0xFFFFFBFF)
val md_theme_light_secondary = Color(0xFF00658D)
val md_theme_light_onSecondary = Color(0xFFFFFFFF)
val md_theme_light_secondaryContainer = Color(0xFF61C6FE)
val md_theme_light_onSecondaryContainer = Color(0xFF005171)
val md_theme_light_tertiary = Color(0xFF933882)
val md_theme_light_onTertiary = Color(0xFFFFFFFF)
val md_theme_light_tertiaryContainer = Color(0xFFB0529C)
val md_theme_light_onTertiaryContainer = Color(0xFFFFFBFF)
val md_theme_light_error = Color(0xFFBA1A1A)
val md_theme_light_errorContainer = Color(0xFFFFDAD6)
val md_theme_light_onError = Color(0xFFFFFFFF)
val md_theme_light_onErrorContainer = Color(0xFF93000A)
val md_theme_light_background = Color(0xFFFBF8FF)
val md_theme_light_onBackground = Color(0xFF131A36)
val md_theme_light_surface = Color(0xFFFBF8FF)
val md_theme_light_onSurface = Color(0xFF131A36)
val md_theme_light_surfaceVariant = Color(0xFFDDE1FF)
val md_theme_light_onSurfaceVariant = Color(0xFF484555)
val md_theme_light_outline = Color(0xFF797587)
val md_theme_light_inverseOnSurface = Color(0xFFF0EFFF)
val md_theme_light_inverseSurface = Color(0xFF292F4C)
val md_theme_light_inversePrimary = Color(0xFFC9BFFF)
val md_theme_light_surfaceTint = Color(0xFF5D3FE0)
val md_theme_light_outlineVariant = Color(0xFFC9C4D8)
val md_theme_light_scrim = Color(0xFF000000)

val md_theme_dark_primary = Color(0xFFC9BFFF)
val md_theme_dark_onPrimary = Color(0xFF2B009D)
val md_theme_dark_primaryContainer = Color(0xFF441CC8)
val md_theme_dark_onPrimaryContainer = Color(0xFFE5DEFF)
val md_theme_dark_secondary = Color(0xFF81CFFF)
val md_theme_dark_onSecondary = Color(0xFF00344B)
val md_theme_dark_secondaryContainer = Color(0xFF004C6B)
val md_theme_dark_onSecondaryContainer = Color(0xFFC6E7FF)
val md_theme_dark_tertiary = Color(0xFFFFACE8)
val md_theme_dark_onTertiary = Color(0xFF5C0054)
val md_theme_dark_tertiaryContainer = Color(0xFF79216B)
val md_theme_dark_onTertiaryContainer = Color(0xFFFFD7F0)
val md_theme_dark_error = Color(0xFFFFB4AB)
val md_theme_dark_errorContainer = Color(0xFF93000A)
val md_theme_dark_onError = Color(0xFF690005)
val md_theme_dark_onErrorContainer = Color(0xFFFFDAD6)
val md_theme_dark_background = Color(0xFF131A36)
val md_theme_dark_onBackground = Color(0xFFE5E7FF)
val md_theme_dark_surface = Color(0xFF131A36)
val md_theme_dark_onSurface = Color(0xFFE5E7FF)
val md_theme_dark_surfaceVariant = Color(0xFF484555)
val md_theme_dark_onSurfaceVariant = Color(0xFFC9C4D8)
val md_theme_dark_outline = Color(0xFF938F9C)
val md_theme_dark_inverseOnSurface = Color(0xFF131A36)
val md_theme_dark_inverseSurface = Color(0xFFE5E7FF)
val md_theme_dark_inversePrimary = Color(0xFF5B3CDD)
val md_theme_dark_surfaceTint = Color(0xFFC9BFFF)
val md_theme_dark_outlineVariant = Color(0xFF484555)
val md_theme_dark_scrim = Color(0xFF000000)
""")

with open("app/src/main/java/com/example/ui/theme/Theme.kt", "w") as f:
    f.write("""package com.example.ui.theme

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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColors = lightColorScheme(
    primary = md_theme_light_primary,
    onPrimary = md_theme_light_onPrimary,
    primaryContainer = md_theme_light_primaryContainer,
    onPrimaryContainer = md_theme_light_onPrimaryContainer,
    secondary = md_theme_light_secondary,
    onSecondary = md_theme_light_onSecondary,
    secondaryContainer = md_theme_light_secondaryContainer,
    onSecondaryContainer = md_theme_light_onSecondaryContainer,
    tertiary = md_theme_light_tertiary,
    onTertiary = md_theme_light_onTertiary,
    tertiaryContainer = md_theme_light_tertiaryContainer,
    onTertiaryContainer = md_theme_light_onTertiaryContainer,
    error = md_theme_light_error,
    errorContainer = md_theme_light_errorContainer,
    onError = md_theme_light_onError,
    onErrorContainer = md_theme_light_onErrorContainer,
    background = md_theme_light_background,
    onBackground = md_theme_light_onBackground,
    surface = md_theme_light_surface,
    onSurface = md_theme_light_onSurface,
    surfaceVariant = md_theme_light_surfaceVariant,
    onSurfaceVariant = md_theme_light_onSurfaceVariant,
    outline = md_theme_light_outline,
    inverseOnSurface = md_theme_light_inverseOnSurface,
    inverseSurface = md_theme_light_inverseSurface,
    inversePrimary = md_theme_light_inversePrimary,
    surfaceTint = md_theme_light_surfaceTint,
    outlineVariant = md_theme_light_outlineVariant,
    scrim = md_theme_light_scrim,
)

private val DarkColors = darkColorScheme(
    primary = md_theme_dark_primary,
    onPrimary = md_theme_dark_onPrimary,
    primaryContainer = md_theme_dark_primaryContainer,
    onPrimaryContainer = md_theme_dark_onPrimaryContainer,
    secondary = md_theme_dark_secondary,
    onSecondary = md_theme_dark_onSecondary,
    secondaryContainer = md_theme_dark_secondaryContainer,
    onSecondaryContainer = md_theme_dark_onSecondaryContainer,
    tertiary = md_theme_dark_tertiary,
    onTertiary = md_theme_dark_onTertiary,
    tertiaryContainer = md_theme_dark_tertiaryContainer,
    onTertiaryContainer = md_theme_dark_onTertiaryContainer,
    error = md_theme_dark_error,
    errorContainer = md_theme_dark_errorContainer,
    onError = md_theme_dark_onError,
    onErrorContainer = md_theme_dark_onErrorContainer,
    background = md_theme_dark_background,
    onBackground = md_theme_dark_onBackground,
    surface = md_theme_dark_surface,
    onSurface = md_theme_dark_onSurface,
    surfaceVariant = md_theme_dark_surfaceVariant,
    onSurfaceVariant = md_theme_dark_onSurfaceVariant,
    outline = md_theme_dark_outline,
    inverseOnSurface = md_theme_dark_inverseOnSurface,
    inverseSurface = md_theme_dark_inverseSurface,
    inversePrimary = md_theme_dark_inversePrimary,
    surfaceTint = md_theme_dark_surfaceTint,
    outlineVariant = md_theme_dark_outlineVariant,
    scrim = md_theme_dark_scrim,
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = false, // Forced false for light, spacious skincare branding
  // Disable dynamicColor by default to guarantee our premium aesthetic skincare theme is applied
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit
) {
  val colorScheme = when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
          val context = LocalContext.current
          if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }
      darkTheme -> DarkColors
      else -> LightColors
  }
  
  val view = LocalView.current
  if (!view.isInEditMode) {
      SideEffect {
          val window = (view.context as Activity).window
          window.statusBarColor = colorScheme.background.toArgb()
          window.navigationBarColor = colorScheme.background.toArgb()
          WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
          WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
      }
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
""")
