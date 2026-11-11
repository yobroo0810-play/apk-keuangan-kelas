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
    primary = IndigoSecondary,
    onPrimary = Color.White,
    primaryContainer = IndigoDark,
    onPrimaryContainer = IndigoLight,
    secondary = EmeraldIncome,
    onSecondary = Color.White,
    tertiary = AmberWarning,
    background = DarkBackground,
    surface = DarkSurface,
    onBackground = DarkText,
    onSurface = DarkText,
    surfaceVariant = DarkBorder,
    outline = DarkBorder,
    error = RoseExpense
)

private val LightColorScheme = lightColorScheme(
    primary = IndigoPrimary,
    onPrimary = Color.White,
    primaryContainer = IndigoLight,
    onPrimaryContainer = IndigoDark,
    secondary = EmeraldIncome,
    onSecondary = Color.White,
    tertiary = AmberWarning,
    background = SlateBackground,
    surface = SlateSurface,
    onBackground = SlateTextDark,
    onSurface = SlateTextDark,
    surfaceVariant = SlateBorder,
    outline = SlateBorder,
    error = RoseExpense
)

@Composable
fun KasKelasTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep branded colors for high fidelity
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
