package com.example.playlistmaker.root.ui


import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = YpBlack,
    onPrimary = YpWhite,
    tertiary = YpWhite,
    onTertiary = YpBlack,
    onBackground = YpWhite
)

private val LightColorScheme = lightColorScheme(
    primary = YpWhite,
    onPrimary = YpBlack,
    tertiary = YpGray,
    onTertiary = YpGray,
    onBackground = YpLightGray
)

@Composable
fun PlaylistMakerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}