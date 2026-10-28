package com.example.ui.views

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.os.SystemClock
import android.view.MotionEvent
import com.example.data.AppBlockTier
import com.example.data.BlockedAppEntity
import com.example.ui.theme.MonolithCurves
import java.util.Locale
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

/**
 * ============================================================================
 * 6. BAG TILES SUITCASE VIEW (DEVELOPER-GUIDE.pdf §3 & §4.2 #4 BagTiles)
 * ============================================================================
 * - Grid of 48dp cells (28dp drawn vector icons, 12dp radius, 12dp gap) representing
 *   the suitcase of apps in the focus session.
 * - Armed (`LOCKED`) cells render as solid `colorPrimary` ink with `colorOnPrimary` icon
 *   and a 4dp `colorPrimaryContainer` (#F55314) armed dot.
 * - `PEEK_2M` cells render in `colorSecondaryContainer` with a Space Mono `2m` corner tag.
 * - Empty / `OPEN` cells render as recessed outline slots (`colorSurfaceVariant` + hairline).
 * - All cells are drawn directly in `onDraw()` — zero inflated child views.
 */
class BagTilesSuitcaseView(context: Context) : BlockitCanvasView(context) {

    var appsList: List<BlockedAppEntity> = emptyList()
        set(value) {
            field = value
            invalidate()
        }

    var columnsCount: Int = 6
        set(value) {
            if (field != value) {
                field = value.coerceIn(4, 8)
                invalidate()
            }
        }

    var onAppCellTapped: ((BlockedAppEntity) -> Unit)? = null

    private var pressedCellIndex: Int = -1
    private var cellPressScale: Float = 1f
    private var pressAnimator: ValueAnimator? = null

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val w = width.toFloat()
        val h = height.toFloat()
        if (w <= 0f || h <= 0f) return

        val cols = columnsCount
        val gap = gapUnit // 12dp universal gap
        val cellSize = ((w - gap * (cols - 1)) / cols.toFloat()).coerceAtLeast(dp(40f))
        val items = appsList.take(cols * 2) // 2 rows of suitcase cells (12 slots)
        val totalSlots = cols * 2

        for (idx in 0 until totalSlots) {
            val row = idx / cols
            val col = idx % cols
            val left = col * (cellSize + gap)
            val top = row * (cellSize + gap)
            if (top + cellSize > h + dp(4f)) break

            canvas.save()
            if (idx == pressedCellIndex) {
                canvas.scale(cellPressScale, cellPressScale, left + cellSize * 0.5f, top + cellSize * 0.5f)
            }

            cellRect.set(left, top, left + cellSize, top + cellSize)
            val app = items.getOrNull(idx)
            val tier = app?.resolvedTier ?: AppBlockTier.OPEN

            when (tier) {
                AppBlockTier.LOCKED -> {
                    paintFill.color = tokPrimary()
                    canvas.drawRoundRect(cellRect, radiusSmall, radiusSmall, paintFill)
                    // Armed state pip in THE Accent (#F55314)
                    paintAccent.color = tokAccent()
                    canvas.drawCircle(cellRect.right - dp(7f), cellRect.top + dp(7f), dp(3f), paintAccent)
                }
                AppBlockTier.PEEK_2M -> {
                    paintFill.color = tokSecondaryContainer()
                    canvas.drawRoundRect(cellRect, radiusSmall, radiusSmall, paintFill)
                    paintMono.color = tokOnSecondaryContainer()
                    paintMono.textSize = sp(8f)
                    paintMono.textAlign = Paint.Align.RIGHT
                    canvas.drawText("2m", cellRect.right - dp(4f), cellRect.top + dp(10f), paintMono)
                }
                AppBlockTier.OPEN -> {
                    // Empty / Open slot renders as outline slot in colorSurfaceVariant + hairline
                    paintFill.color = tokSurfaceVariant()
                    canvas.drawRoundRect(cellRect, radiusSmall, radiusSmall, paintFill)
                    paintStroke.color = tokHairline()
                    paintStroke.strokeWidth = dp(1f)
                    canvas.drawRoundRect(cellRect, radiusSmall, radiusSmall, paintStroke)
                }
            }

            // Draw 28dp geometric app glyph + short label inside cell
            if (app != null) {
                val glyphColor = when (tier) {
                    AppBlockTier.LOCKED -> tokOnPrimary()
                    AppBlockTier.PEEK_2M -> tokOnSecondaryContainer()
                    AppBlockTier.OPEN -> tokOnSurface()
                }
                drawAppVectorGlyph(
                    canvas = canvas,
                    appName = app.appName,
                    cx = cellRect.centerX(),
                    cy = cellRect.centerY() - dp(5f),
                    iconRadius = dp(10f),
                    color = glyphColor
                )

                paintWord.color = glyphColor
                paintWord.textSize = sp(8.5f)
                paintWord.textAlign = Paint.Align.CENTER
                val shortName = if (app.appName.length > 7) app.appName.take(6) else app.appName
                canvas.drawText(shortName, cellRect.centerX(), cellRect.bottom - dp(6f), paintWord)
            } else {
                // Plus crosshair in empty slot
                paintStroke.color = tokHairline()
                paintStroke.strokeWidth = dp(1.2f)
                val cx = cellRect.centerX()
                val cy = cellRect.centerY()
                canvas.drawLine(cx - dp(5f), cy, cx + dp(5f), cy, paintStroke)
                canvas.drawLine(cx, cy - dp(5f), cx, cy + dp(5f), paintStroke)
            }

            canvas.restore()
        }
    }

    private fun drawAppVectorGlyph(
        canvas: Canvas,
        appName: String,
        cx: Float,
        cy: Float,
        iconRadius: Float,
        color: Int
    ) {
        paintStroke.color = color
        paintStroke.strokeWidth = dp(1.7f)
        paintStroke.strokeCap = Paint.Cap.ROUND
        paintFill.color = color

        val key = appName.lowercase(Locale.US)
        when {
            key.contains("tiktok") -> {
                // Musical note / pulse wave
                canvas.drawLine(cx, cy - iconRadius * 0.7f, cx, cy + iconRadius * 0.5f, paintStroke)
                canvas.drawCircle(cx - dp(3f), cy + iconRadius * 0.5f, dp(3f), paintFill)
                canvas.drawLine(cx, cy - iconRadius * 0.7f, cx + dp(5f), cy - iconRadius * 0.4f, paintStroke)
            }
            key.contains("insta") -> {
                // Camera lens square + circle
                thumbRect.set(cx - iconRadius * 0.75f, cy - iconRadius * 0.75f, cx + iconRadius * 0.75f, cy + iconRadius * 0.75f)
                canvas.drawRoundRect(thumbRect, dp(4f), dp(4f), paintStroke)
                canvas.drawCircle(cx, cy, dp(3.2f), paintStroke)
            }
            key.contains("youtube") || key.contains("netflix") || key.contains("twitch") -> {
                // Play triangle
                vectorPath.reset()
                vectorPath.moveTo(cx - dp(4f), cy - dp(5f))
                vectorPath.lineTo(cx + dp(5f), cy)
                vectorPath.lineTo(cx - dp(4f), cy + dp(5f))
                vectorPath.close()
                canvas.drawPath(vectorPath, paintFill)
            }
            key.contains("whatsapp") || key.contains("discord") || key.contains("telegram") || key.contains("snap") -> {
                // Chat capsule
                thumbRect.set(cx - iconRadius * 0.8f, cy - iconRadius * 0.6f, cx + iconRadius * 0.8f, cy + iconRadius * 0.6f)
                canvas.drawRoundRect(thumbRect, dp(5f), dp(5f), paintStroke)
                canvas.drawCircle(cx - dp(3f), cy, dp(1.3f), paintFill)
                canvas.drawCircle(cx + dp(3f), cy, dp(1.3f), paintFill)
            }
            else -> {
                // Monogram geometric badge
                canvas.drawCircle(cx, cy, iconRadius * 0.75f, paintStroke)
                paintMono.color = color
                paintMono.textSize = sp(9.5f)
                paintMono.textAlign = Paint.Align.CENTER
                val initial = appName.firstOrNull()?.uppercaseChar()?.toString() ?: "A"
                canvas.drawText(initial, cx, cy + dp(3.5f), paintMono)
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val cols = columnsCount
        val gap = gapUnit
        val cellSize = ((width - gap * (cols - 1)) / cols.toFloat()).coerceAtLeast(dp(40f))
        val col = (event.x / (cellSize + gap)).toInt().coerceIn(0, cols - 1)
        val row = (event.y / (cellSize + gap)).toInt().coerceIn(0, 1)
        val idx = row * cols + col

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                pressedCellIndex = idx
                animateCellScale(0.92f)
                return true
            }
            MotionEvent.ACTION_UP -> {
                animateCellScale(1f)
                pressedCellIndex = -1
                val app = appsList.getOrNull(idx)
                if (app != null) {
                    fireFeel(4L, 520f + idx * 18f)
                    onAppCellTapped?.invoke(app)
                }
                return true
            }
            MotionEvent.ACTION_CANCEL -> {
                animateCellScale(1f)
                pressedCellIndex = -1
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    private fun animateCellScale(target: Float) {
        pressAnimator?.cancel()
        pressAnimator = ValueAnimator.ofFloat(cellPressScale, target).apply {
            duration = MonolithCurves.DURATION_180.toLong()
            interpolator = MonolithCurves.CurveA
            addUpdateListener {
                cellPressScale = it.animatedValue as Float
                invalidate()
            }
            start()
        }
    }
}

/**
 * ============================================================================
 * 7. SLED SWITCH / ADVANCE LEVER VIEW (DEVELOPER-GUIDE.pdf §4.2 #6 SledSwitch)
 * ============================================================================
 * - Mechanical toggle: a physical sled that slides in a recessed track with a lever throw,
 *   not a Material Switch.
 * - State changes are animated throws (260ms, Curve A), with an 8ms firm click + synth clunk.
 */
class SledSwitchView(context: Context) : BlockitCanvasView(context) {

    var titleText: String = "SHORTS & REELS SNIPER"
        set(value) {
            if (field != value) {
                field = value
                invalidate()
            }
        }

    var subtitleText: String = "Auto-back out of vertical short feeds"
        set(value) {
            if (field != value) {
                field = value
                invalidate()
            }
        }

    var isChecked: Boolean = true
        set(value) {
            if (field != value) {
                field = value
                animateThrow(if (value) 1f else 0f)
            }
        }

    var onToggleRequested: (() -> Unit)? = null

    private var sledProgress: Float = 1f
    private var throwAnimator: ValueAnimator? = null

    private fun animateThrow(target: Float) {
        throwAnimator?.cancel()
        throwAnimator = ValueAnimator.ofFloat(sledProgress, target).apply {
            duration = MonolithCurves.DURATION_260.toLong()
            interpolator = MonolithCurves.CurveA
            addUpdateListener {
                sledProgress = it.animatedValue as Float
                invalidate()
            }
            start()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val w = width.toFloat()
        val h = height.toFloat()
        if (w <= 0f || h <= 0f) return

        // Full-bleed row background (flat by default: tokSurface) + bottom hairline
        paintFill.color = tokSurface()
        canvas.drawRect(0f, 0f, w, h, paintFill)

        paintStroke.color = tokHairline()
        paintStroke.strokeWidth = dp(1f)
        canvas.drawLine(0f, h - dp(0.5f), w, h - dp(0.5f), paintStroke)

        // Left title & subtitle
        paintWord.color = tokTextPrimary()
        paintWord.textSize = sp(13f)
        paintWord.textAlign = Paint.Align.LEFT
        canvas.drawText(titleText, 0f, h * 0.42f, paintWord)

        paintWordSub.color = tokTextSecondary()
        paintWordSub.textSize = sp(11f)
        paintWordSub.textAlign = Paint.Align.LEFT
        canvas.drawText(subtitleText, 0f, h * 0.74f, paintWordSub)

        // Right Mechanical Sled Track (76dp wide x 34dp tall, 12dp corner radius)
        val trackW = dp(76f)
        val trackH = dp(34f)
        val trackLeft = w - trackW
        val trackTop = (h - trackH) * 0.5f
        trackRect.set(trackLeft, trackTop, w, trackTop + trackH)

        paintFill.color = tokSurfaceVariant()
        canvas.drawRoundRect(trackRect, radiusSmall, radiusSmall, paintFill)
        paintStroke.color = tokHairline()
        canvas.drawRoundRect(trackRect, radiusSmall, radiusSmall, paintStroke)

        // Sled thumb inside track
        val pad = dp(3f)
        val sledW = (trackW - pad * 2f) * 0.54f
        val maxTravel = (trackW - pad * 2f - sledW)
        val sledLeft = trackLeft + pad + sledProgress * maxTravel
        thumbRect.set(sledLeft, trackTop + pad, sledLeft + sledW, trackTop + trackH - pad)

        paintFill.color = if (sledProgress > 0.5f) tokPrimary() else tokSecondaryContainer()
        canvas.drawRoundRect(thumbRect, radiusSmall - dp(2f), radiusSmall - dp(2f), paintFill)

        // Sled state label ("ON" / "OFF") + #F55314 hot-orange armed pip when ON
        paintMono.color = if (sledProgress > 0.5f) tokOnPrimary() else tokOnSecondaryContainer()
        paintMono.textSize = sp(9.5f)
        paintMono.textAlign = Paint.Align.CENTER
        val stateWord = if (sledProgress > 0.5f) "ARM" else "OFF"
        canvas.drawText(stateWord, thumbRect.centerX(), thumbRect.centerY() + dp(3.5f), paintMono)

        if (sledProgress > 0.5f) {
            paintAccent.color = tokAccent()
            canvas.drawCircle(trackLeft + dp(10f), trackRect.centerY(), dp(3f), paintAccent)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.actionMasked == MotionEvent.ACTION_UP) {
            // §6: 8ms Firm click on SledSwitch throw!
            fireFeel(8L, if (!isChecked) 680f else 420f)
            onToggleRequested?.invoke()
            return true
        }
        return true
    }
}

/**
 * ============================================================================
 * 8. ROMBI KNOB VIEW (DEVELOPER-GUIDE.pdf §4.2 #7 RombiKnob)
 * ============================================================================
 * - A diamond/rhombus rotary knob for discrete levels (Loadouts: STUDY / DEEP / SLEEP);
 *   rotation snaps at detents with Curve B (280ms) and an 8ms firm click.
 */
class RombiKnobView(context: Context) : BlockitCanvasView(context) {

    private val levelLabels = arrayOf("STUDY", "DEEP", "SLEEP")
    var selectedLevel: Int = 0
        set(value) {
            val clamped = value.coerceIn(0, 2)
            if (field != clamped) {
                field = clamped
                animateDetent(clamped)
            }
        }

    var onLevelChanged: ((Int) -> Unit)? = null

    private var animatedAngleDeg: Float = -45f
    private var knobAnimator: ValueAnimator? = null

    private fun levelToAngle(level: Int): Float = when (level) {
        0 -> -45f
        1 -> 0f
        else -> 45f
    }

    private fun animateDetent(level: Int) {
        val target = levelToAngle(level)
        knobAnimator?.cancel()
        knobAnimator = ValueAnimator.ofFloat(animatedAngleDeg, target).apply {
            duration = MonolithCurves.DURATION_280.toLong()
            interpolator = MonolithCurves.CurveB
            addUpdateListener {
                animatedAngleDeg = it.animatedValue as Float
                invalidate()
            }
            start()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val w = width.toFloat()
        val h = height.toFloat()
        if (w <= 0f || h <= 0f) return

        // Full-bleed loadout selector band
        cardRect.set(0f, 0f, w, h)
        paintFill.color = tokSurfaceVariant()
        canvas.drawRoundRect(cardRect, radiusSmall, radiusSmall, paintFill)
        paintStroke.color = tokHairline()
        paintStroke.strokeWidth = dp(1f)
        canvas.drawRoundRect(cardRect, radiusSmall, radiusSmall, paintStroke)

        // Left: Rotating Diamond Rhombus Knob
        val knobCx = dp(42f)
        val knobCy = h * 0.5f
        val r = min(dp(22f), h * 0.36f)

        // Detent pips around knob
        val detentAngles = floatArrayOf(-135f, -90f, -45f)
        for (i in 0..2) {
            val rad = detentAngles[i] * (PI.toFloat() / 180f)
            val px = knobCx + cos(rad) * (r + dp(7f))
            val py = knobCy + sin(rad) * (r + dp(7f))
            paintFill.color = if (i == selectedLevel) tokAccent() else tokOutline()
            canvas.drawCircle(px, py, if (i == selectedLevel) dp(3f) else dp(2f), paintFill)
        }

        // Draw rotated rhombus (RombiKnob)
        canvas.save()
        canvas.rotate(animatedAngleDeg, knobCx, knobCy)
        vectorPath.reset()
        vectorPath.moveTo(knobCx, knobCy - r)
        vectorPath.lineTo(knobCx + r * 0.78f, knobCy)
        vectorPath.lineTo(knobCx, knobCy + r)
        vectorPath.lineTo(knobCx - r * 0.78f, knobCy)
        vectorPath.close()

        paintFill.color = tokPrimary()
        canvas.drawPath(vectorPath, paintFill)

        // Indicator notch at top vertex of rhombus
        paintAccent.color = tokAccent()
        canvas.drawCircle(knobCx, knobCy - r + dp(6f), dp(2.8f), paintAccent)
        canvas.restore()

        // Right: 3 Discrete Loadout Detent Segments
        val segAreaLeft = dp(84f)
        val segAreaW = w - segAreaLeft - dp(8f)
        val segW = segAreaW / 3f
        val padV = dp(8f)

        for (i in 0..2) {
            val sLeft = segAreaLeft + i * segW + dp(3f)
            val sRight = segAreaLeft + (i + 1) * segW - dp(3f)
            cellRect.set(sLeft, padV, sRight, h - padV)

            val isSel = i == selectedLevel
            if (isSel) {
                paintFill.color = tokPrimary()
                canvas.drawRoundRect(cellRect, radiusSmall, radiusSmall, paintFill)
            }

            paintMono.color = if (isSel) tokAccent() else tokTextSecondary()
            paintMono.textSize = sp(9.5f)
            paintMono.textAlign = Paint.Align.CENTER
            canvas.drawText("0${i + 1}", cellRect.centerX(), cellRect.centerY() - dp(4f), paintMono)

            paintWord.color = if (isSel) tokOnPrimary() else tokTextPrimary()
            paintWord.textSize = sp(11.5f)
            paintWord.textAlign = Paint.Align.CENTER
            canvas.drawText(levelLabels[i], cellRect.centerX(), cellRect.centerY() + dp(11f), paintWord)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.actionMasked == MotionEvent.ACTION_UP) {
            val nextLevel = if (event.x < dp(84f)) {
                (selectedLevel + 1) % 3
            } else {
                val segAreaLeft = dp(84f)
                val segW = (width - segAreaLeft - dp(8f)) / 3f
                ((event.x - segAreaLeft) / segW).toInt().coerceIn(0, 2)
            }
            selectedLevel = nextLevel
            fireFeel(8L, 500f + nextLevel * 90f)
            onLevelChanged?.invoke(nextLevel)
            return true
        }
        return true
    }
}

/**
 * ============================================================================
 * 9. APP ROW 3-STOP SLED ROCKER VIEW (Ultra-Minimal Installed App Row)
 * ============================================================================
 * - Full-bleed installed app row with bottom hairline (`colorOutlineVariant`).
 * - Left: 28dp vector glyph + Google Sans app name.
 * - Right: 3-stop physical sled switch (`SHUT` / `2m` / `OPEN`) with Curve A (260ms) throw.
 */
class AppRowSledToggleView(context: Context) : BlockitCanvasView(context) {

    var appName: String = "Instagram"
        set(value) {
            if (field != value) {
                field = value
                invalidate()
            }
        }

    var currentTier: AppBlockTier = AppBlockTier.LOCKED
        set(value) {
            if (field != value) {
                field = value
                animateRockerTo(tierToIndex(value).toFloat())
            }
        }

    var onTierSelected: ((AppBlockTier) -> Unit)? = null

    private var rockerIndex: Float = 0f
    private var rockerAnimator: ValueAnimator? = null

    private fun tierToIndex(tier: AppBlockTier): Int = when (tier) {
        AppBlockTier.LOCKED -> 0
        AppBlockTier.PEEK_2M -> 1
        AppBlockTier.OPEN -> 2
    }

    private fun animateRockerTo(target: Float) {
        rockerAnimator?.cancel()
        rockerAnimator = ValueAnimator.ofFloat(rockerIndex, target).apply {
            duration = MonolithCurves.DURATION_260.toLong()
            interpolator = MonolithCurves.CurveA
            addUpdateListener {
                rockerIndex = it.animatedValue as Float
                invalidate()
            }
            start()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val w = width.toFloat()
        val h = height.toFloat()
        if (w <= 0f || h <= 0f) return

        // Bottom hairline
        paintStroke.color = tokHairline()
        paintStroke.strokeWidth = dp(1f)
        canvas.drawLine(0f, h - dp(0.5f), w, h - dp(0.5f), paintStroke)

        // Left status pip + App Name
        val isShut = currentTier == AppBlockTier.LOCKED
        paintFill.color = if (isShut) tokAccent() else tokOutline()
        canvas.drawCircle(dp(6f), h * 0.5f, if (isShut) dp(4f) else dp(2.5f), paintFill)

        paintWord.color = tokTextPrimary()
        paintWord.textSize = sp(14f)
        paintWord.textAlign = Paint.Align.LEFT
        canvas.drawText(appName, dp(18f), h * 0.5f + dp(5f), paintWord)

        // Right 3-Stop Sled Rocker (168dp wide x 34dp tall, 12dp radius)
        val rockerW = dp(168f)
        val rockerH = dp(34f)
        val rLeft = w - rockerW
        val rTop = (h - rockerH) * 0.5f
        trackRect.set(rLeft, rTop, w, rTop + rockerH)

        paintFill.color = tokSurfaceVariant()
        canvas.drawRoundRect(trackRect, radiusSmall, radiusSmall, paintFill)
        paintStroke.color = tokHairline()
        canvas.drawRoundRect(trackRect, radiusSmall, radiusSmall, paintStroke)

        val pad = dp(3f)
        val slotW = (rockerW - pad * 2f) / 3f
        val thumbL = rLeft + pad + rockerIndex * slotW
        thumbRect.set(thumbL, rTop + pad, thumbL + slotW, rTop + rockerH - pad)

        paintFill.color = when (currentTier) {
            AppBlockTier.LOCKED -> tokPrimary()
            AppBlockTier.PEEK_2M -> tokSecondaryContainer()
            AppBlockTier.OPEN -> tokSurfaceBright()
        }
        canvas.drawRoundRect(thumbRect, radiusSmall - dp(2f), radiusSmall - dp(2f), paintFill)

        val stops = arrayOf("SHUT", "2m", "OPEN")
        for (i in 0..2) {
            val cx = rLeft + pad + (i + 0.5f) * slotW
            val cy = trackRect.centerY() + dp(4f)
            val selected = i == tierToIndex(currentTier)
            val txtColor = if (selected) {
                when (currentTier) {
                    AppBlockTier.LOCKED -> tokOnPrimary()
                    AppBlockTier.PEEK_2M -> tokOnSecondaryContainer()
                    AppBlockTier.OPEN -> tokTextPrimary()
                }
            } else {
                tokTextSecondary()
            }

            if (i == 1) {
                // "2m" has numeral in Space Mono
                drawMixedWordAndMono(
                    canvas = canvas,
                    wordPrefix = "",
                    monoNumber = "2",
                    wordSuffix = "m",
                    x = cx,
                    baselineY = cy,
                    wordSizePx = sp(10f),
                    monoSizePx = sp(10.5f),
                    wordColor = txtColor,
                    monoColor = txtColor,
                    centered = true
                )
            } else {
                paintWord.color = txtColor
                paintWord.textSize = sp(10f)
                paintWord.textAlign = Paint.Align.CENTER
                canvas.drawText(stops[i], cx, cy, paintWord)
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.actionMasked == MotionEvent.ACTION_UP) {
            val rockerW = dp(168f)
            val rLeft = width - rockerW
            val nextTier = if (event.x >= rLeft) {
                val idx = ((event.x - rLeft) / (rockerW / 3f)).toInt().coerceIn(0, 2)
                when (idx) {
                    0 -> AppBlockTier.LOCKED
                    1 -> AppBlockTier.PEEK_2M
                    else -> AppBlockTier.OPEN
                }
            } else {
                when (currentTier) {
                    AppBlockTier.LOCKED -> AppBlockTier.PEEK_2M
                    AppBlockTier.PEEK_2M -> AppBlockTier.OPEN
                    AppBlockTier.OPEN -> AppBlockTier.LOCKED
                }
            }
            fireFeel(8L, 520f)
            onTierSelected?.invoke(nextTier)
            return true
        }
        return true
    }
}

/**
 * ============================================================================
 * 10. EMERGENCY 60S PRESSURE VALVE VIEW (Unbreakable Lock + 1/Week 60s Hold)
 * ============================================================================
 * - Requires continuous 60-second finger hold to trigger the single weekly emergency break.
 * - Releasing early immediately decays back to 0.
 */
class Emergency60sValveView(context: Context) : BlockitCanvasView(context) {

    var requiredSeconds: Int = 60
    var tokensRemaining: Int = 1
        set(value) {
            field = value
            invalidate()
        }

    var onEmergencyBreakCompleted: (() -> Unit)? = null

    private var isHolding: Boolean = false
    private var holdStartRealtimeMs: Long = 0L
    private var holdProgress: Float = 0f
    private var lastSecondTick: Int = -1

    private val holdTickRunnable = object : Runnable {
        override fun run() {
            if (!isHolding) return
            val elapsedMs = (SystemClock.elapsedRealtime() - holdStartRealtimeMs).coerceAtLeast(0L)
            val totalMs = (requiredSeconds * 1000L).coerceAtLeast(1000L)
            holdProgress = (elapsedMs.toFloat() / totalMs.toFloat()).coerceIn(0f, 1f)
            val sec = (elapsedMs / 1000L).toInt()
            if (sec != lastSecondTick) {
                lastSecondTick = sec
                fireFeel(if (sec % 5 == 0) 8L else 4L, 380f + holdProgress * 460f)
            }
            invalidate()
            if (holdProgress >= 1f) {
                isHolding = false
                fireFeel(10L, 840f)
                onEmergencyBreakCompleted?.invoke()
            } else {
                postDelayed(this, 32L)
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val w = width.toFloat()
        val h = height.toFloat()
        if (w <= 0f || h <= 0f) return

        trackRect.set(0f, 0f, w, h)
        paintFill.color = tokBlockBar()
        canvas.drawRoundRect(trackRect, radiusLarge, radiusLarge, paintFill)

        paintStroke.color = tokHairline()
        paintStroke.strokeWidth = dp(1f)
        canvas.drawRoundRect(trackRect, radiusLarge, radiusLarge, paintStroke)

        if (holdProgress > 0f) {
            cellRect.set(0f, 0f, w * holdProgress, h)
            paintAccent.color = tokAccent()
            paintAccent.alpha = 180
            canvas.drawRoundRect(cellRect, radiusLarge, radiusLarge, paintAccent)
            paintAccent.alpha = 255
        }

        val remSeconds = ((1f - holdProgress) * requiredSeconds).toInt().coerceAtLeast(0)
        val label = if (tokensRemaining <= 0) {
            "NO WEEKLY EMERGENCY TOKENS REMAINING"
        } else if (isHolding) {
            "KEEP HOLDING VALVE • "
        } else {
            "HOLD 60S FOR WEEKLY EMERGENCY BREAK • "
        }
        val monoBadge = if (tokensRemaining <= 0) "0/1" else if (isHolding) "${remSeconds}s" else "${tokensRemaining}/1 LEFT"

        drawMixedWordAndMono(
            canvas = canvas,
            wordPrefix = label,
            monoNumber = monoBadge,
            wordSuffix = "",
            x = w * 0.5f,
            baselineY = h * 0.5f + dp(5f),
            wordSizePx = sp(11.5f),
            monoSizePx = sp(13f),
            wordColor = LIGHT_ON_PRIMARY,
            monoColor = tokAccent(),
            centered = true
        )
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (tokensRemaining <= 0) return false
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                parent?.requestDisallowInterceptTouchEvent(true)
                isHolding = true
                holdStartRealtimeMs = SystemClock.elapsedRealtime()
                lastSecondTick = -1
                removeCallbacks(holdTickRunnable)
                post(holdTickRunnable)
                return true
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                isHolding = false
                removeCallbacks(holdTickRunnable)
                holdProgress = 0f
                invalidate()
                parent?.requestDisallowInterceptTouchEvent(false)
                return true
            }
        }
        return super.onTouchEvent(event)
    }
}
