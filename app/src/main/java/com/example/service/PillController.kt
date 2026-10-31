package com.example.service

import android.animation.ValueAnimator
import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.provider.Settings
import android.util.TypedValue
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import com.example.MainActivity
import com.example.data.MonolithDao
import com.example.data.MonolithRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * The pill's driver (PILL-AND-TIMES §2.2, Route A).
 *
 * Owned by MonolithShieldService, which runs exactly while a
 * session is active. A 1 s ticker inside the service derives the
 * face from stored ends (PillDerivation) and touches the window
 * only when the printed string changes. No new wakelock, no new
 * service, no new polling of anything new — the foreground read is
 * a widening of the usage-stats query the shield already runs.
 *
 * Death behaviour: the window is removed in the service's
 * onDestroy (via [stop]) and on the first tick whose derivation
 * reads ABSENT. If the service dies, the pill dies with it — a
 * frozen pill printing a dead session's time would be a warrant
 * lie in a new hat. If the overlay grant is missing or revoked,
 * canDrawOverlays reads false at the next tick and the window
 * never draws — Route B (the shield notification) carries the
 * time instead. Blocking never consults this class; its death is
 * cosmetic by construction.
 */
class PillController(
    private val context: Context,
    private val repository: MonolithRepository,
    private val dao: MonolithDao,
    private val scope: CoroutineScope,
    private val ownPackage: String
) {
    private val windowManager =
        context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private val keyguardManager =
        context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
    private val mainHandler = Handler(Looper.getMainLooper())

    private var tickerJob: Job? = null

    // Window state — main thread only.
    private var rootView: LinearLayout? = null
    private var labelText: TextView? = null
    private var timeText: TextView? = null
    private var closeText: TextView? = null
    private var layoutParams: WindowManager.LayoutParams? = null
    private var lastRenderKey: String? = null
    private var snapAnimator: ValueAnimator? = null

    @Volatile
    private var currentFace: PillFace = PillFace.Absent

    /** The last PROVEN foreground read. Starts at own-package:
     *  until a usage-events read names another app, the pill
     *  assumes he is inside Monolith and stays hidden — §2.4 Q1
     *  is a hard rule, so an unreadable foreground resolves to
     *  the safe side. */
    @Volatile
    private var lastForegroundPkg: String = ownPackage

    fun start() {
        if (tickerJob?.isActive == true) return
        tickerJob = scope.launch {
            while (isActive) {
                // A cosmetic surface may never kill its own ticker
                // on a transient read or a revoked-grant race: the
                // failure of one tick is silence for one second.
                runCatching { tick() }
                delay(1000L)
            }
        }
    }

    fun stop() {
        tickerJob?.cancel()
        tickerJob = null
        // onDestroy runs on the main thread in practice, but the
        // removal must not depend on the caller's thread — and it
        // must not ride the service scope, which is cancelled in
        // the same onDestroy breath.
        if (Looper.myLooper() == Looper.getMainLooper()) {
            removeWindow()
        } else {
            mainHandler.post { removeWindow() }
        }
    }

    private suspend fun tick() {
        val session = dao.getActiveSessionSnapshot()
        val appRows = if (session != null && session.isActive) {
            dao.getAllAppsSnapshot()
        } else {
            emptyList()
        }
        repository.queryForegroundPackage()?.let { lastForegroundPkg = it }
        val face = PillDerivation.derive(
            session = session,
            appRows = appRows,
            nowMono = SystemClock.elapsedRealtime(),
            foregroundPkg = lastForegroundPkg,
            ownPackage = ownPackage,
            keyguardLocked = keyguardManager.isKeyguardLocked
        )
        currentFace = face
        withContext(Dispatchers.Main) { render(face) }
    }

    // ------------------------------------------------------------------
    // Rendering — main thread only.
    // ------------------------------------------------------------------

    private fun render(face: PillFace) {
        if (face is PillFace.Absent || !Settings.canDrawOverlays(context)) {
            removeWindow()
            return
        }
        val root = rootView ?: buildWindow()
        if (face.renderKey == lastRenderKey) return
        lastRenderKey = face.renderKey

        val palette = PillTheme.palette
        labelText?.setTextColor(palette.mutedTextArgb)
        timeText?.setTextColor(palette.textArgb)
        closeText?.setTextColor(palette.accentArgb)
        (root.background as? GradientDrawable)?.apply {
            setColor(palette.backgroundArgb)
            setStroke(dp(1), palette.borderArgb)
        }
        when (face) {
            is PillFace.Session -> {
                labelText?.text = if (face.isStopwatch) {
                    "SESSION · ELAPSED"
                } else {
                    "SESSION · BLOCK ${face.blockIndex}/${face.blockCount}"
                }
                timeText?.text = if (face.isStopwatch) {
                    "+${formatClock(face.elapsedSeconds)}"
                } else {
                    formatClock(face.remainingSeconds)
                }
                closeText?.visibility = View.GONE
            }
            is PillFace.Break -> {
                labelText?.text = "BREAK"
                timeText?.text = formatClock(face.remainingSeconds)
                closeText?.visibility = View.GONE
            }
            is PillFace.Peek -> {
                labelText?.text = face.appName.uppercase()
                timeText?.text = formatClock(face.remainingSeconds)
                closeText?.visibility = View.VISIBLE
            }
            PillFace.Absent -> Unit // handled above
        }
    }

    private fun buildWindow(): LinearLayout {
        val palette = PillTheme.palette
        val typeface = PillTheme.figures(context)

        val time = TextView(context).apply {
            setTextColor(palette.textArgb)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
            this.typeface = typeface
            includeFontPadding = false
        }
        val label = TextView(context).apply {
            setTextColor(palette.mutedTextArgb)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 9f)
            this.typeface = typeface
            includeFontPadding = false
        }
        val column = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            addView(label)
            addView(time)
        }
        val close = TextView(context).apply {
            text = "✕"
            setTextColor(palette.accentArgb)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)
            this.typeface = typeface
            setPadding(dp(10), dp(6), dp(2), dp(6))
            visibility = View.GONE
            setOnClickListener { onHandBack() }
        }
        val root = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(14), dp(8), dp(12), dp(8))
            background = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = dp(999).toFloat()
                setColor(palette.backgroundArgb)
                setStroke(dp(1), palette.borderArgb)
            }
            addView(column)
            addView(close)
            setOnTouchListener(DragListener())
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            overlayWindowType(),
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 0
            y = (context.resources.displayMetrics.heightPixels * 0.22f).toInt()
        }
        rootView = root
        labelText = label
        timeText = time
        closeText = close
        layoutParams = params
        lastRenderKey = null

        val added = runCatching { windowManager.addView(root, params) }.isSuccess
        if (!added) {
            // The grant died between the check and the draw: stay
            // absent; Route B carries the time. The next face
            // change rebuilds from a clean slate.
            removeWindow()
            return root
        }

        // Born at the right edge — the glass he is looking at is
        // the other app's; the pill takes its margin, not its centre.
        root.post {
            val p = layoutParams ?: return@post
            p.x = (context.resources.displayMetrics.widthPixels - root.width - dp(10))
                .coerceAtLeast(0)
            runCatching { windowManager.updateViewLayout(root, p) }
        }
        return root
    }

    private fun removeWindow() {
        snapAnimator?.cancel()
        snapAnimator = null
        val root = rootView
        if (root != null) {
            runCatching { windowManager.removeView(root) }
        }
        rootView = null
        labelText = null
        timeText = null
        closeText = null
        layoutParams = null
        lastRenderKey = null
    }

    // ------------------------------------------------------------------
    // Verbs.
    // ------------------------------------------------------------------

    /** The PEEK face's ✕ — the first surface from which a peek can
     *  be handed back early (§2.1). Calls the airlock's own verb;
     *  the lease zeroes, the cooldown re-anchors to now, and the
     *  next tick re-derives the face from the stored rows. */
    private fun onHandBack() {
        val face = currentFace as? PillFace.Peek ?: return
        scope.launch { repository.recordPeekEnded(face.packageName) }
    }

    /** Body tap — RETURN TO FOCUS (§2.1): back to Monolith. The
     *  displayed overlay window is also this app's background-
     *  launch exemption — the tap is his hand on the glass, and
     *  the machine answers it. */
    private fun onBodyTap() {
        val intent = Intent(context, MainActivity::class.java).apply {
            addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP
            )
        }
        runCatching { context.startActivity(intent) }
    }

    /** Draggable, edge-snapping — the dead contract's own words,
     *  now kept. Position is NOT persisted (residency-scoped;
     *  §2.1 priced and declined the settings column). A press that
     *  never travels is the body tap; one that travels snaps to
     *  the nearest vertical edge on release. */
    private inner class DragListener : View.OnTouchListener {
        private var downRawX = 0f
        private var downRawY = 0f
        private var startX = 0
        private var startY = 0
        private var moved = false

        override fun onTouch(view: View, event: MotionEvent): Boolean {
            val params = layoutParams ?: return false
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    snapAnimator?.cancel()
                    downRawX = event.rawX
                    downRawY = event.rawY
                    startX = params.x
                    startY = params.y
                    moved = false
                    return true
                }
                MotionEvent.ACTION_MOVE -> {
                    val dx = (event.rawX - downRawX).toInt()
                    val dy = (event.rawY - downRawY).toInt()
                    if (kotlin.math.abs(dx) + kotlin.math.abs(dy) > dp(6)) moved = true
                    if (moved) {
                        params.x = startX + dx
                        params.y = startY + dy
                        runCatching { windowManager.updateViewLayout(view, params) }
                    }
                    return true
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    if (!moved) {
                        if (event.action == MotionEvent.ACTION_UP) onBodyTap()
                    } else {
                        snapToEdge(view, params)
                    }
                    return true
                }
            }
            return false
        }

        private fun snapToEdge(view: View, params: WindowManager.LayoutParams) {
            val screenWidth = context.resources.displayMetrics.widthPixels
            val target = if (params.x + view.width / 2 < screenWidth / 2) {
                dp(6)
            } else {
                (screenWidth - view.width - dp(6)).coerceAtLeast(0)
            }
            val from = params.x
            snapAnimator = ValueAnimator.ofInt(from, target).apply {
                duration = 140L
                addUpdateListener { animator ->
                    params.x = animator.animatedValue as Int
                    runCatching { windowManager.updateViewLayout(view, params) }
                }
                start()
            }
        }
    }

    private fun overlayWindowType(): Int =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        }

    private fun formatClock(totalSeconds: Int): String {
        val clamped = totalSeconds.coerceAtLeast(0)
        return String.format("%02d:%02d", clamped / 60, clamped % 60)
    }

    private fun dp(value: Int): Int =
        (value * context.resources.displayMetrics.density).toInt()
}
