package com.eitanadler.arkhamcalcredux2.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

@Immutable
internal data class DiceChanceColoursPalette(
    val isHighChance: Color = Color.Unspecified,
    val isLowChance: Color = Color.Unspecified,
    val isMidChance: Color = Color.Unspecified,
)
internal val LocalDiceChanceColoursPalette = staticCompositionLocalOf { DiceChanceColoursPalette() }

private val DarkDiceChanceColoursPalette = DiceChanceColoursPalette(
    isHighChance = Color.Red,
    isLowChance = Color.Green,
    isMidChance = Color.Blue,
)
private val LightDiceChanceColoursPalette = DiceChanceColoursPalette(
    isHighChance = Color.Red,
    isLowChance = Color.Green,
    isMidChance = Color.Blue,
)

private val DarkColorScheme =
    darkColorScheme(
        primary = Purple80,
        secondary = PurpleGrey80,
        tertiary = Pink80,
    )

private val LightColorScheme =
    lightColorScheme(
        primary = Purple40,
        secondary = PurpleGrey40,
        tertiary = Pink40,
    )

internal val MaterialTheme.chanceColorPalette: DiceChanceColoursPalette
    @Composable
    @ReadOnlyComposable
    get() = LocalDiceChanceColoursPalette.current

@Composable
internal fun ArkhamCalcReduxTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
    val colorScheme =
        when {
            dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                val context = LocalContext.current
                if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            }

            darkTheme -> DarkColorScheme
            else -> LightColorScheme
        }

    val chanceColoursPalette =
        if (darkTheme) DarkDiceChanceColoursPalette else LightDiceChanceColoursPalette
    CompositionLocalProvider(LocalDiceChanceColoursPalette provides chanceColoursPalette) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content,
        )
    }
}
