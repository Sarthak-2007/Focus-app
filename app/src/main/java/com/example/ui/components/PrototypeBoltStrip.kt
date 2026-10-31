package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AnonymousProFamily
import com.example.ui.theme.AtkinsonHyperlegibleFamily
import com.example.ui.theme.TokenAlert
import kotlin.math.PI

/**
 * PROTOTYPE — issue #4, BUILDER-3 (T24 Builder, DURCH-A run).
 *
 * A throwaway-but-real implementation of the thread's leading
 * object-form, THE BOLT (design of record c5de8d2,
 * `studio/plan/DESIGN-issue-4-the-bolt.md`), built so the panel
 * judges a RENDER, not a description. It is NOT wired into the
 * Deck host and merges only if the panel ratifies the form and
 * DURCH-A coordinates it; otherwise it dies as evidence.
 *
 * What this prototype carries, and where each piece comes from:
 * - One Canvas node for the whole instrument (MAX-2 / MIP-02's
 *   render direction: Pose x Lamp x Window from one fold).
 * - WOW-1's keeper jaw: a 16dp jaw block whose face plate the
 *   thrown bolt's tip passes BEHIND — arrival is silhouette,
 *   not a 6dp collar sliver (the #500 port's known gap).
 * - KRALL K-3 cure: the locking pin is drawn LAST, into a real
 *   notch recessed in the bolt's top edge; geometry forbids the
 *   drop in any pose but THROWN.
 * - HOLLIS H-3 cure: ENDING_SOON's threshold is floored by the
 *   span (`min(300s, span/2)`) — the shortest legal 5-minute
 *   session is no longer born "ending soon".
 * - The glance arithmetic (THE BOLT §6) computed from the
 *   elements that actually render, pinned by
 *   `PrototypeBoltStripTest` — no constant asserted equal to
 *   itself (KRALL K-5).
 *
 * The face's whole vocabulary: `END` / `PLANNED END` engraved
 * on the bezel, `HH:mm` in the glass. No state labels, no
 * BIND / PROOF / counts — drawn geometry only.
 */

/** The prototype's faces — pose x lamp x window, one fold. */
enum class PrototypeBoltFace {
    OFF, UNPROVEN, BLOCKING, RUNNING, RUNNING_UNPROVEN,
    STALE, ENDING_SOON, DETENTION
}

enum class PrototypeLamp { DARK, BRASS, MAGENTA, MAGENTA_HALO }

enum class PrototypeWindow { LIVE_END, PLANNED_END, BLANK, SHUTTERED, INVERTED_END }

data class PrototypePose(
    val fraction: Float,
    val lamp: PrototypeLamp,
    val window: PrototypeWindow
)

/** Pose/lamp/window per face — the design of record §3 table. */
fun prototypePoseFor(face: PrototypeBoltFace): PrototypePose = when (face) {
    PrototypeBoltFace.OFF ->
        PrototypePose(0f, PrototypeLamp.DARK, PrototypeWindow.PLANNED_END)
    PrototypeBoltFace.UNPROVEN ->
        PrototypePose(1f / 3f, PrototypeLamp.DARK, PrototypeWindow.PLANNED_END)
    PrototypeBoltFace.BLOCKING ->
        PrototypePose(1f, PrototypeLamp.BRASS, PrototypeWindow.PLANNED_END)
    PrototypeBoltFace.RUNNING ->
        PrototypePose(1f, PrototypeLamp.MAGENTA_HALO, PrototypeWindow.LIVE_END)
    PrototypeBoltFace.RUNNING_UNPROVEN ->
        PrototypePose(1f, PrototypeLamp.DARK, PrototypeWindow.LIVE_END)
    PrototypeBoltFace.STALE ->
        PrototypePose(1f, PrototypeLamp.DARK, PrototypeWindow.SHUTTERED)
    PrototypeBoltFace.ENDING_SOON ->
        PrototypePose(1f, PrototypeLamp.BRASS, PrototypeWindow.INVERTED_END)
    PrototypeBoltFace.DETENTION ->
        PrototypePose(1f, PrototypeLamp.MAGENTA, PrototypeWindow.BLANK)
}

/** HOLLIS H-3: the effective ending-soon threshold — the lesser
 *  of the 300s cap and half the session's own span. */
fun prototypeEndingSoonThresholdFor(spanSeconds: Int?, capSeconds: Int = 300): Int =
    if (spanSeconds != null && spanSeconds > 0) minOf(capSeconds, spanSeconds / 2)
    else capSeconds

/**
 * The prototype fold — deliberately small, precedence explicit,
 * running rows first (the design's order). Inputs are plain
 * facts the host already owns; the fold reads no store, no
 * warrant object, no clock.
 */
fun prototypeFaceFor(
    sessionRunning: Boolean,
    detentionHeld: Boolean,
    liveOff: Boolean,
    hasRead: Boolean,
    proofStale: Boolean,
    provenNow: Boolean,
    isStopwatch: Boolean,
    remainingSeconds: Int?,
    spanSeconds: Int?
): PrototypeBoltFace {
    if (detentionHeld) return PrototypeBoltFace.DETENTION
    if (sessionRunning) {
        // The live wire's OFF outranks any frozen verdict.
        if (liveOff) return PrototypeBoltFace.OFF
        // Proof that existed and expired is not "never proven".
        if (proofStale) return PrototypeBoltFace.STALE
        if (!hasRead || !provenNow) return PrototypeBoltFace.RUNNING_UNPROVEN
        if (isStopwatch) return PrototypeBoltFace.RUNNING
        if (remainingSeconds != null &&
            remainingSeconds <= prototypeEndingSoonThresholdFor(spanSeconds)
        ) return PrototypeBoltFace.ENDING_SOON
        return PrototypeBoltFace.RUNNING
    }
    if (liveOff || !hasRead) {
        return if (liveOff) PrototypeBoltFace.OFF else PrototypeBoltFace.UNPROVEN
    }
    return if (provenNow) PrototypeBoltFace.BLOCKING else PrototypeBoltFace.UNPROVEN
}

/** Geometry + glance arithmetic for the prototype strip.
 *  All units dp at the 360dp reference width unless noted;
 *  the channel is the one flexible element — the bolt never grows. */
object PrototypeBoltGeometry {
    const val housingHeightDp = 60f
    const val insetDp = 12f
    const val trackHeightDp = 36f
    const val boltWidthDp = 90f
    const val boltHeightDp = 34f
    const val jawWidthDp = 16f
    const val jawFacePlateDp = 10f
    const val jawSlotWidthDp = 8f
    const val jawSlotHeightDp = 20f
    const val jewelDiameterDp = 9f
    const val bezelDiameterDp = 17f
    const val haloDiameterDp = 21f
    const val windowWidthDp = 102f
    const val windowHeightDp = 36f
    const val pinWidthDp = 4f
    const val notchBoltLocalStartDp = 66f
    const val notchWidthDp = 12f
    const val seatedSinkDp = 2f
    const val throwMs = 50

    /** Inner width after the housing's horizontal insets. */
    fun innerWidthDp(housingWidthDp: Float): Float = housingWidthDp - 2 * insetDp

    /** Channel width: everything else in the row is fixed, so
     *  all width growth lands here (THE BOLT §1). */
    fun channelWidthDp(housingWidthDp: Float): Float =
        innerWidthDp(housingWidthDp) - windowWidthDp - 12f - haloDiameterDp - 12f

    /** Bolt travel: open stop to thrown stop. */
    fun travelDp(housingWidthDp: Float): Float =
        channelWidthDp(housingWidthDp) - boltWidthDp

    /** Bolt left edge for a travel fraction (0 / 1/3 / 1). */
    fun boltLeftDp(housingWidthDp: Float, fraction: Float): Float =
        insetDp + travelDp(housingWidthDp) * fraction

    /** How much of the thrown bolt's tip hides behind the jaw
     *  face plate — the swallow. Must stay >= 8dp (WOW-1). */
    fun swallowDp(): Float = jawFacePlateDp

    /** The pin's fixed centre x: under it sits the notch only
     *  when the bolt is THROWN (bolt-local notch centre carried
     *  to the thrown position). */
    fun pinCentreXDp(housingWidthDp: Float): Float =
        boltLeftDp(housingWidthDp, 1f) + notchBoltLocalStartDp + notchWidthDp / 2f

    /** Window left edge. */
    fun windowLeftDp(housingWidthDp: Float): Float =
        insetDp + innerWidthDp(housingWidthDp) - windowWidthDp

    /** Jewel centre x: 12dp clear of the window, halo included. */
    fun jewelCentreXDp(housingWidthDp: Float): Float =
        windowLeftDp(housingWidthDp) - 12f - haloDiameterDp / 2f

    /** OFF's orange luminous total: the fully exposed bed (the
     *  bolt's thrown footprint) + the jaw slot's painted back
     *  wall, times the one orange's relative luminance. */
    fun offLuminousTotal(): Float =
        (boltWidthDp * boltHeightDp + jawSlotWidthDp * jawSlotHeightDp) *
            TokenAlert.luminance()

    /** A lit face's jewel luminous total at night (the brighter
     *  mode): disc for every lit lamp, plus the halo annulus
     *  only where the face wears one (RUNNING). */
    fun jewelLuminousTotal(face: PrototypeBoltFace, night: Boolean): Float {
        val pose = prototypePoseFor(face)
        val disc = (PI * 4.5 * 4.5).toFloat()
        val halo = (PI * (10.5 * 10.5 - 8.5 * 8.5)).toFloat()
        return when (pose.lamp) {
            PrototypeLamp.DARK -> 0f
            PrototypeLamp.BRASS -> disc * PrototypeBoltPalette.brass.luminance()
            PrototypeLamp.MAGENTA ->
                disc * PrototypeBoltPalette.magenta(night).luminance()
            PrototypeLamp.MAGENTA_HALO ->
                (disc + halo) * PrototypeBoltPalette.magenta(night).luminance()
        }
    }

    /** The glance gate (THE BOLT §6): OFF's orange total must be
     *  at least 3x the brightest lit jewel's total. */
    fun offToRunningRatio(): Float =
        offLuminousTotal() / jewelLuminousTotal(PrototypeBoltFace.RUNNING, night = true)
}

/** The prototype's pinned colours — the design of record §5
 *  table, constants not scheme roles (WOW-1/GLANZ: a screenshot
 *  has no scheme context; recognisability is constants). */
object PrototypeBoltPalette {
    val warningOrange: Color = TokenAlert // #D63E06, mode-invariant
    val brass: Color = Color(0xFFE2BC39) // one value both modes
    fun magenta(night: Boolean): Color =
        if (night) Color(0xFFF04FCB) else Color(0xFFC81FA8)
    fun glass(night: Boolean): Color =
        if (night) Color(0xFF161311) else Color(0xFF2E2A26)
    fun windowInk(night: Boolean): Color =
        if (night) Color(0xFFFFE4CF) else Color(0xFFFAF6F0)
    val endingSoonPlate: Color = Color(0xFFE2BC39)
    val endingSoonInk: Color = Color(0xFF252323)
    fun jawCap(night: Boolean): Color =
        if (night) Color(0xFF4A423C) else Color(0xFFD8D1CB)
    fun boltCap(night: Boolean): Color =
        if (night) Color(0xFF4A423C) else Color(0xFF615B5A)
    val gripCream: Color = Color(0xFFFFE4CF)
    fun recess(night: Boolean): Color =
        if (night) Color(0xFF211D1A) else Color(0xFFD8D1CB)
}

private fun PrototypeBoltFace.spokenSentence(): String = when (this) {
    PrototypeBoltFace.OFF -> "Blocking is off. The bolt is open."
    PrototypeBoltFace.UNPROVEN -> "Blocking is not proven. The bolt is caught, not locked."
    PrototypeBoltFace.BLOCKING -> "Blocking is on and proven. The bolt is thrown."
    PrototypeBoltFace.RUNNING -> "A session is running and proven. The bolt is thrown."
    PrototypeBoltFace.RUNNING_UNPROVEN ->
        "A session is running. Its blocking is not proven now."
    PrototypeBoltFace.STALE -> "A session is running. Its proof has gone stale."
    PrototypeBoltFace.ENDING_SOON -> "A session is running and ending soon."
    PrototypeBoltFace.DETENTION -> "The session end was refused. The bolt stays thrown."
}

/**
 * The prototype strip. [endText] is the live end `HH:mm`;
 * [plannedEndText] the dial's planned end, shown dimmed under
 * a PLANNED END engraving. Numerals exist only where the fold
 * says an end exists — no placeholders, ever.
 */
@Composable
fun PrototypeBoltStrip(
    face: PrototypeBoltFace,
    endText: String?,
    plannedEndText: String?,
    modifier: Modifier = Modifier
) {
    val night = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val pose = prototypePoseFor(face)
    // First composition SNAPs to the pose (KANTE B-C5): the
    // Animatable is born at the target; only later pose changes
    // run the 50ms throw. Verdicts (lamp/window) never animate.
    val travel = remember { Animatable(pose.fraction) }
    LaunchedEffect(pose.fraction) {
        if (travel.value != pose.fraction) {
            travel.animateTo(pose.fraction, tween(PrototypeBoltGeometry.throwMs))
        }
    }
    val measurer = rememberTextMeasurer()
    Canvas(
        modifier
            .fillMaxWidth()
            .height(PrototypeBoltGeometry.housingHeightDp.dp)
            .semantics { contentDescription = face.spokenSentence() }
    ) {
        drawPrototypeBolt(
            face = face,
            fraction = travel.value,
            night = night,
            endText = endText,
            plannedEndText = plannedEndText,
            measurer = measurer
        )
    }
}

private fun DrawScope.drawPrototypeBolt(
    face: PrototypeBoltFace,
    fraction: Float,
    night: Boolean,
    endText: String?,
    plannedEndText: String?,
    measurer: TextMeasurer
) {
    val g = PrototypeBoltGeometry
    val wDp = size.width / density
    val trackY = (g.housingHeightDp - g.trackHeightDp) / 2f
    val channelW = g.channelWidthDp(wDp)
    val channelX = g.insetDp
    val boltLeft = g.boltLeftDp(wDp, fraction)
    val seated = fraction >= 0.99f
    val sink = if (seated) g.seatedSinkDp else 0f
    val boltTop = trackY + (g.trackHeightDp - g.boltHeightDp) / 2f + sink
    val pose = prototypePoseFor(face)
    fun x(dp: Float) = dp * density
    fun y(dp: Float) = dp * density

    // Housing: a band, not a card — radius 0, hairlines only.
    drawRect(PrototypeBoltPalette.recess(night), Offset(0f, 0f), size)
    val hairline = if (night) Color(0xFF4A423C) else Color(0xFF615B5A)
    drawRect(hairline, Offset(0f, 0f), Size(size.width, 1.dp.toPx()))
    drawRect(hairline, Offset(0f, size.height - 1.dp.toPx()), Size(size.width, 1.dp.toPx()))

    // Channel recess.
    drawRoundRect(
        color = if (night) Color(0xFF161311) else Color(0xFFCFC7C0),
        topLeft = Offset(x(channelX), y(trackY)),
        size = Size(x(channelW), y(g.trackHeightDp)),
        cornerRadius = CornerRadius(x(3f), y(3f))
    )

    // Warning bed at the shut end — the bolt's thrown footprint.
    // Exposed exactly when the bolt is not home (THE BOLT §1).
    val bedLeft = channelX + channelW - g.boltWidthDp
    drawRect(
        color = PrototypeBoltPalette.warningOrange,
        topLeft = Offset(x(bedLeft), y(boltTop)),
        size = Size(x(g.boltWidthDp), y(g.boltHeightDp))
    )

    // Keeper jaw body (behind the bolt; WOW-1 render order).
    val jawX = channelX + channelW - g.jawWidthDp
    drawRect(
        color = PrototypeBoltPalette.jawCap(night),
        topLeft = Offset(x(jawX), y(trackY)),
        size = Size(x(g.jawWidthDp), y(g.trackHeightDp))
    )

    // The bolt itself, with cream grips and its machined notch
    // (a recess on the top edge, at the pin's bolt-local x).
    val boltColor = if (seated) {
        PrototypeBoltPalette.boltCap(night)
    } else {
        if (night) Color(0xFF3A342F) else Color(0xFF7A736E)
    }
    drawRoundRect(
        color = boltColor,
        topLeft = Offset(x(boltLeft), y(boltTop)),
        size = Size(x(g.boltWidthDp), y(g.boltHeightDp)),
        cornerRadius = CornerRadius(x(4f), y(4f))
    )
    for (i in 0..2) {
        val gripX = boltLeft + 10f + i * 7f
        drawRect(
            color = PrototypeBoltPalette.gripCream,
            topLeft = Offset(x(gripX), y(boltTop + 7f)),
            size = Size(x(3f), y(g.boltHeightDp - 14f))
        )
    }
    // The notch: housing-dark recess cut into the bolt's top edge.
    drawRect(
        color = if (night) Color(0xFF161311) else Color(0xFFCFC7C0),
        topLeft = Offset(x(boltLeft + g.notchBoltLocalStartDp), y(boltTop)),
        size = Size(x(g.notchWidthDp), y(3f))
    )
    // Contact shadow rib under the bolt (drawn, never elevation).
    if (!night) {
        drawRect(
            color = Color(0xFF2E2A26).copy(alpha = 0.35f),
            topLeft = Offset(x(boltLeft), y(boltTop + g.boltHeightDp - 1f)),
            size = Size(x(g.boltWidthDp), y(1f))
        )
    }

    // Jaw face plate OVER the bolt — the thrown tip is swallowed.
    val plateX = channelX + channelW - g.jawFacePlateDp
    drawRect(
        color = PrototypeBoltPalette.jawCap(night),
        topLeft = Offset(x(plateX), y(trackY)),
        size = Size(x(g.jawFacePlateDp), y(g.trackHeightDp))
    )
    // Mouth slot in the plate; its back wall is hazard orange
    // in OFF only — an open mouth with a hazard throat (WOW-1).
    val slotX = plateX + (g.jawFacePlateDp - g.jawSlotWidthDp) / 2f
    val slotY = trackY + (g.trackHeightDp - g.jawSlotHeightDp) / 2f
    drawRoundRect(
        color = if (face == PrototypeBoltFace.OFF) {
            PrototypeBoltPalette.warningOrange
        } else {
            PrototypeBoltPalette.glass(night)
        },
        topLeft = Offset(x(slotX), y(slotY)),
        size = Size(x(g.jawSlotWidthDp), y(g.jawSlotHeightDp)),
        cornerRadius = CornerRadius(x(2f), y(2f))
    )

    // The locking pin, drawn LAST (KRALL K-3): it can only sit
    // in the notch when the bolt is THROWN — geometry, not a flag.
    val pinCx = g.pinCentreXDp(wDp)
    val pinDrop = if (seated) 5f else 0f
    drawRect(
        color = PrototypeBoltPalette.gripCream,
        topLeft = Offset(x(pinCx - g.pinWidthDp / 2f), y(trackY - 2f + pinDrop)),
        size = Size(x(g.pinWidthDp), y(7f))
    )

    // Jewel in its bezel: dark / brass / magenta, <=9dp, steady.
    val jewelCx = g.jewelCentreXDp(wDp)
    val jewelCy = g.housingHeightDp / 2f
    if (pose.lamp == PrototypeLamp.MAGENTA_HALO) {
        drawCircle(
            color = PrototypeBoltPalette.magenta(night),
            radius = x(g.haloDiameterDp / 2f),
            center = Offset(x(jewelCx), y(jewelCy)),
            alpha = 0.28f
        )
    }
    drawCircle(
        color = if (night) Color(0xFF4A423C) else Color(0xFF252323),
        radius = x(g.bezelDiameterDp / 2f),
        center = Offset(x(jewelCx), y(jewelCy))
    )
    val jewelColor = when (pose.lamp) {
        PrototypeLamp.DARK -> PrototypeBoltPalette.glass(night)
        PrototypeLamp.BRASS -> PrototypeBoltPalette.brass
        PrototypeLamp.MAGENTA, PrototypeLamp.MAGENTA_HALO ->
            PrototypeBoltPalette.magenta(night)
    }
    drawCircle(
        color = jewelColor,
        radius = x(g.jewelDiameterDp / 2f),
        center = Offset(x(jewelCx), y(jewelCy))
    )

    // The glass window: live end / planned end / blank louvre /
    // shuttered solid / brass-inverted ending-soon block.
    val winX = g.windowLeftDp(wDp)
    val inverted = pose.window == PrototypeWindow.INVERTED_END
    drawRect(
        color = if (inverted) PrototypeBoltPalette.endingSoonPlate
        else PrototypeBoltPalette.glass(night),
        topLeft = Offset(x(winX), y(trackY)),
        size = Size(x(g.windowWidthDp), y(g.windowHeightDp))
    )
    // Cinema hairline (WOW-1): a lit picture, not a readout.
    if (pose.window == PrototypeWindow.LIVE_END && !inverted) {
        drawRect(
            color = PrototypeBoltPalette.windowInk(night).copy(alpha = 0.55f),
            topLeft = Offset(x(winX + 2f), y(trackY + 2f)),
            size = Size(x(g.windowWidthDp - 4f), y(1f))
        )
        drawRect(
            color = PrototypeBoltPalette.windowInk(night).copy(alpha = 0.55f),
            topLeft = Offset(x(winX + 2f), y(trackY + g.windowHeightDp - 3f)),
            size = Size(x(g.windowWidthDp - 4f), y(1f))
        )
    }
    val numerals: String? = when (pose.window) {
        PrototypeWindow.LIVE_END, PrototypeWindow.INVERTED_END -> endText
        PrototypeWindow.PLANNED_END -> plannedEndText
        else -> null
    }
    if (numerals != null) {
        val style = TextStyle(
            fontFamily = AnonymousProFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )
        val layout = measurer.measure(numerals, style)
        val ink = when {
            inverted -> PrototypeBoltPalette.endingSoonInk
            pose.window == PrototypeWindow.PLANNED_END ->
                PrototypeBoltPalette.windowInk(night).copy(alpha = 0.55f)
            else -> PrototypeBoltPalette.windowInk(night)
        }
        drawText(
            textLayoutResult = layout,
            color = ink,
            topLeft = Offset(
                x(winX) + (x(g.windowWidthDp) - layout.size.width) / 2f,
                y(trackY) + (y(g.windowHeightDp) - layout.size.height) / 2f
            )
        )
    } else if (pose.window == PrototypeWindow.BLANK ||
        pose.window == PrototypeWindow.SHUTTERED
    ) {
        // Louvre slats: resting face at 0.35, STALE solid (WOW-1).
        val slatAlpha = if (pose.window == PrototypeWindow.SHUTTERED) 1f else 0.35f
        for (sy in listOf(9f, 18f, 27f)) {
            drawRect(
                color = PrototypeBoltPalette.windowInk(night).copy(alpha = slatAlpha * 0.6f),
                topLeft = Offset(x(winX + 8f), y(trackY + sy)),
                size = Size(x(g.windowWidthDp - 16f), y(1f))
            )
        }
    }
    // Bezel engraving under the glass: END, or PLANNED END.
    val engraving = if (pose.window == PrototypeWindow.PLANNED_END) "PLANNED END" else "END"
    val engStyle = TextStyle(
        fontFamily = AtkinsonHyperlegibleFamily,
        fontSize = 9.sp
    )
    val engLayout = measurer.measure(engraving, engStyle)
    drawText(
        textLayoutResult = engLayout,
        color = (if (night) Color(0xFF807A7A) else Color(0xFF615B5A)),
        topLeft = Offset(x(winX), size.height - engLayout.size.height - 1.dp.toPx())
    )
}
