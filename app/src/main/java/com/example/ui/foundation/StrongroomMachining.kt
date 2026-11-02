package com.example.ui.foundation

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.cos
import kotlin.math.sin

/**
 * THE MACHINING KIT — solid metal, drawn (LEAD-MOVES richness
 * pass, 2026-11-03, under Ren). The rebuild's first mechanisms
 * were drawn as diagrams: teeth as lines, spokes as lines, rims
 * as stroke circles, bolts as flat bars. A mechanism has MASS:
 * a rim is a band with two edges and a grain, a tooth is a club
 * with a root and a tip, a bolt is a bar that travels in a
 * housing. This file is the shared drawing kit for that mass —
 * annuli, discs and bars filled cell by cell with the surface's
 * own grain (the slice textures' `cellTone`, passed in as
 * [toneAt]), club teeth and tapered spokes as filled paths with
 * RELIGHT edges, and the print's rim bolt heads.
 *
 * THE GRAIN LAW BINDS THE KIT: grain is a deterministic function
 * of position IN THE PART'S OWN FRAME — a turning part carries
 * its grain with it ([rotationDeg] inverse-rotates the sample
 * point before the tone is asked), because a wheel whose grain
 * stays behind while it turns is a screen, not a clock. Every
 * tone the kit spends comes from the caller's own texture law —
 * the surface ink moved toward its own RELIGHT partners in whole
 * eighths. The kit names no colour of its own.
 *
 * Light direction is read from [StrongroomLight.paramsFor] at
 * the call site and passed as [lightDir]: the edge of a tooth or
 * spoke whose outward normal faces the light takes the lit edge
 * colour; the other takes the shadow edge. Relief light derived
 * from the surface's own luminance, per RELIGHT — no drawn-in
 * shading, no eighth ink, no image.
 */

/** A filled disc of grain cells, clipped to the circle. */
fun DrawScope.grainDisc(
    center: Offset,
    radius: Float,
    cellPx: Float,
    rotationDeg: Float = 0f,
    toneAt: (cellX: Int, cellY: Int) -> Color
) {
    grainClipped(center, radius, -1f, cellPx, rotationDeg, toneAt)
}

/** A filled annulus (rim band) of grain cells, clipped to the
 *  band between [innerR] and [outerR]. */
fun DrawScope.grainAnnulus(
    center: Offset,
    outerR: Float,
    innerR: Float,
    cellPx: Float,
    rotationDeg: Float = 0f,
    toneAt: (cellX: Int, cellY: Int) -> Color
) {
    grainClipped(center, outerR, innerR, cellPx, rotationDeg, toneAt)
}

private fun DrawScope.grainClipped(
    center: Offset,
    outerR: Float,
    innerR: Float,
    cellPx: Float,
    rotationDeg: Float,
    toneAt: (cellX: Int, cellY: Int) -> Color
) {
    val rot = Math.toRadians(-rotationDeg.toDouble())
    val cosR = cos(rot).toFloat()
    val sinR = sin(rot).toFloat()
    val x0 = center.x - outerR
    val y0 = center.y - outerR
    var yy = y0
    while (yy < center.y + outerR) {
        var xx = x0
        while (xx < center.x + outerR) {
            val px = xx + cellPx / 2f - center.x
            val py = yy + cellPx / 2f - center.y
            val d2 = px * px + py * py
            if (d2 <= outerR * outerR && (innerR < 0f || d2 >= innerR * innerR)) {
                // The cell's indices in the PART's frame: the
                // sample point inverse-rotated about the centre,
                // so the grain turns with the part.
                val lx = px * cosR - py * sinR
                val ly = px * sinR + py * cosR
                val cellX = Math.floor((lx / cellPx).toDouble()).toInt()
                val cellY = Math.floor((ly / cellPx).toDouble()).toInt()
                drawRect(
                    color = toneAt(cellX, cellY),
                    topLeft = Offset(xx, yy),
                    size = Size(cellPx + 1f, cellPx + 1f)
                )
            }
            xx += cellPx
        }
        yy += cellPx
    }
}

/** A filled rectangle of grain cells — a bolt bar, a housing,
 *  a jamb: metal with a skin, never a flat fill. */
fun DrawScope.grainRect(
    topLeft: Offset,
    size: Size,
    cellPx: Float,
    toneAt: (cellX: Int, cellY: Int) -> Color
) {
    if (size.width <= 0f || size.height <= 0f) return
    var cy = 0
    var yy = topLeft.y
    while (yy < topLeft.y + size.height) {
        var cx = 0
        var xx = topLeft.x
        while (xx < topLeft.x + size.width) {
            drawRect(
                color = toneAt(cx, cy),
                topLeft = Offset(xx, yy),
                size = Size(cellPx + 1f, cellPx + 1f)
            )
            xx += cellPx; cx++
        }
        yy += cellPx; cy++
    }
}

/**
 * One club tooth of an escape wheel: a filled trapezoid from
 * [rootR] to [tipR] at [angleDeg] (canvas convention: degrees,
 * y down, turning clockwise), root wider than tip — the profile
 * that locks and releases. Its two flanks take the lit and
 * shadow edge colours by the light's direction: the flank whose
 * outward normal faces [lightDir] is lit.
 */
fun DrawScope.clubTooth(
    center: Offset,
    angleDeg: Double,
    rootR: Float,
    tipR: Float,
    rootHalfPx: Float,
    tipHalfPx: Float,
    fill: Color,
    litEdge: Color,
    shadowEdge: Color,
    lightDir: Offset
) {
    val a = Math.toRadians(angleDeg)
    val dir = Offset(cos(a).toFloat(), sin(a).toFloat())
    val perp = Offset(-dir.y, dir.x)
    val rootC = center + dir * rootR
    val tipC = center + dir * tipR
    val r1 = rootC + perp * rootHalfPx
    val r2 = rootC - perp * rootHalfPx
    val t1 = tipC + perp * tipHalfPx
    val t2 = tipC - perp * tipHalfPx
    val path = Path().apply {
        moveTo(r1.x, r1.y)
        lineTo(t1.x, t1.y)
        lineTo(t2.x, t2.y)
        lineTo(r2.x, r2.y)
        close()
    }
    drawPath(path, fill)
    // The flanks: flank 1 runs r1→t1 with outward normal +perp,
    // flank 2 runs r2→t2 with normal −perp. The one facing the
    // light is lit; the other stands in shadow.
    val facing = perp.x * lightDir.x + perp.y * lightDir.y
    val flank1 = if (facing >= 0f) litEdge else shadowEdge
    val flank2 = if (facing >= 0f) shadowEdge else litEdge
    drawLine(color = flank1, start = r1, end = t1, strokeWidth = 1.4f)
    drawLine(color = flank2, start = r2, end = t2, strokeWidth = 1.4f)
    // The tip land: the face the pallet meets — lit from above.
    drawLine(color = litEdge, start = t1, end = t2, strokeWidth = 1.2f)
}

/**
 * One tapered spoke: a filled quad from [fromR] to [toR] at
 * [angleDeg], wider at the hub than at the rim — a casting, not
 * a line. Its light-facing flank takes [litEdge].
 */
fun DrawScope.taperedSpoke(
    center: Offset,
    angleDeg: Double,
    fromR: Float,
    toR: Float,
    fromHalfPx: Float,
    toHalfPx: Float,
    fill: Color,
    litEdge: Color,
    shadowEdge: Color,
    lightDir: Offset
) {
    val a = Math.toRadians(angleDeg)
    val dir = Offset(cos(a).toFloat(), sin(a).toFloat())
    val perp = Offset(-dir.y, dir.x)
    val c1 = center + dir * fromR
    val c2 = center + dir * toR
    val p1 = c1 + perp * fromHalfPx
    val p2 = c1 - perp * fromHalfPx
    val p3 = c2 - perp * toHalfPx
    val p4 = c2 + perp * toHalfPx
    val path = Path().apply {
        moveTo(p1.x, p1.y)
        lineTo(p4.x, p4.y)
        lineTo(p3.x, p3.y)
        lineTo(p2.x, p2.y)
        close()
    }
    drawPath(path, fill)
    val facing = perp.x * lightDir.x + perp.y * lightDir.y
    drawLine(
        color = if (facing >= 0f) litEdge else shadowEdge,
        start = p1, end = p4, strokeWidth = 1.3f
    )
    drawLine(
        color = if (facing >= 0f) shadowEdge else litEdge,
        start = p2, end = p3, strokeWidth = 1.3f
    )
}

/**
 * A bolt head, as the picked print wears them around its rim:
 * a round steel head with a domed lit crown — the crown's lit
 * arc on the light's side, its shadow arc opposite, and the
 * maker's punch at its heart.
 */
fun DrawScope.boltHead(
    center: Offset,
    radius: Float,
    face: Color,
    lit: Color,
    shadow: Color
) {
    drawCircle(color = shadow, radius = radius, center = center)
    drawCircle(color = face, radius = radius * 0.86f, center = center)
    drawArc(
        color = lit, startAngle = 130f, sweepAngle = 150f, useCenter = false,
        topLeft = Offset(center.x - radius * 0.62f, center.y - radius * 0.62f),
        size = Size(radius * 1.24f, radius * 1.24f),
        style = Stroke(width = radius * 0.30f)
    )
    drawCircle(color = shadow, radius = radius * 0.20f, center = center)
}

/** The state's light direction as a canvas vector (y down),
 *  for the kit's edge lighting. */
fun lightVectorFor(state: StrongroomLightState): Offset {
    val p = StrongroomLight.paramsFor(state)
    return Offset(p.lightDirectionX, p.lightDirectionY)
}
