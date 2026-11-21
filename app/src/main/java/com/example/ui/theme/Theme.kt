package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

private val SemainierColorScheme = lightColorScheme(
    primary = Terracotta500,
    onPrimary = PureWhite,
    primaryContainer = Terracotta100,
    onPrimaryContainer = Terracotta700,
    secondary = Sage600,
    onSecondary = PureWhite,
    secondaryContainer = Sage100,
    onSecondaryContainer = Sage700,
    tertiary = Amber400,
    onTertiary = Stone900,
    tertiaryContainer = Amber50,
    onTertiaryContainer = Amber900,
    background = Cream50,
    onBackground = Stone800,
    surface = PureWhite,
    onSurface = Stone900,
    surfaceVariant = Cream100,
    onSurfaceVariant = Stone600,
    outline = Cream200,
    outlineVariant = Stone200,
    error = Red600,
    onError = PureWhite,
    errorContainer = Red50,
    onErrorContainer = Red600
)

val SemainierShapes = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(14.dp),
    large = RoundedCornerShape(18.dp),
    extraLarge = RoundedCornerShape(24.dp)
)

@Composable
fun SemainierTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = SemainierColorScheme,
        typography = SemainierTypography,
        shapes = SemainierShapes,
        content = content
    )
}
