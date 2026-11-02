package com.example.ui.foundation

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

/**
 * THE THEME — the foundation composed for consumption.
 *
 * Authored from zero by BUILD-SR-33 (T24). This is the one door
 * rebuilt surfaces enter through: it provides the seven-ink scheme
 * ([StrongroomInks]/[StrongroomRoles]), the Stage 5 scales as the
 * Material typography ([StrongroomType]), and the session's light
 * state ([LocalStrongroomLightState], consumed via [StrongroomLight]).
 *
 * THE NAME: the app is ULYSSES on every user-facing surface
 * (ledger E-12, his pick) — [APP_NAME] is the foundation's single
 * home for that word in code. "Monolith" survives only as an
 * internal/working name (packages, project documents); no user
 * ever sees it, and no surface in the rebuild may print it.
 *
 * The scheme is built ONLY from the seven inks — every slot is an
 * ink or a role over an ink. There is no light scheme: the room is
 * dark in every state, and the states differ by LIGHT
 * ([StrongroomLight]), never by swapping the palette.
 */
const val APP_NAME: String = "ULYSSES"

/** The Material colour scheme over the seven inks. Slot law:
 *  the violet ground is the surface; the graphite field is the
 *  room behind it; gold carries primary action and reading voice
 *  on the dark grounds; graphite line-work reads on gold. The mark
 *  inks sit in the secondary/tertiary/error slots at mark scale —
 *  a slot assignment is not a field licence (Grade Book §1.3:
 *  magenta and rose are withheld from every field). */
val StrongroomColorScheme = darkColorScheme(
    primary = StrongroomInks.Gold,
    onPrimary = StrongroomInks.Graphite,
    secondary = StrongroomInks.Blue,
    onSecondary = StrongroomInks.Graphite,
    tertiary = StrongroomInks.Rose,
    onTertiary = StrongroomInks.Graphite,
    background = StrongroomRoles.Field,
    onBackground = StrongroomRoles.OnGround,
    surface = StrongroomRoles.Ground,
    onSurface = StrongroomRoles.OnGround,
    surfaceVariant = StrongroomRoles.Field,
    onSurfaceVariant = StrongroomRoles.OnGround,
    outline = StrongroomRoles.LineWork,
    error = StrongroomInks.Magenta,
    onError = StrongroomInks.Graphite
)

/** The Material typography, mapped slot-for-slot onto the Stage 5
 *  scales — a Material slot is a doorway into a scale step, never
 *  a second scale. Surfaces should prefer naming the step itself
 *  ([StrongroomType]) wherever the system's own name exists. */
val StrongroomTypography = Typography(
    displayLarge = StrongroomType.Scr6,
    headlineLarge = StrongroomType.Scr5,
    headlineMedium = StrongroomType.Scr4,
    titleLarge = StrongroomType.Scr3,
    bodyLarge = StrongroomType.Scr2,
    bodyMedium = StrongroomType.Scr1,
    labelSmall = StrongroomType.Scr0
)

/**
 * The foundation's theme. [lightState] is the room's current light
 * — the surface's state (signing at the day's bookends by default);
 * the sealed/dark/day states are supplied by the surfaces' own
 * decided state, never guessed here.
 */
@Composable
fun StrongroomTheme(
    lightState: StrongroomLightState = StrongroomLightState.SIGNING,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalStrongroomLightState provides lightState) {
        MaterialTheme(
            colorScheme = StrongroomColorScheme,
            typography = StrongroomTypography,
            content = content
        )
    }
}
