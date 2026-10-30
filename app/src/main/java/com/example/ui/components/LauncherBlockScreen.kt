package com.example.ui.components

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppBlockTier
import com.example.data.ClockText
import com.example.data.BlockedAppEntity
import com.example.data.FocusSessionEntity
import com.example.ui.theme.AnonymousProFamily
import com.example.ui.theme.AtkinsonHyperlegibleFamily
import com.example.ui.theme.NightColorBackground
import com.example.ui.theme.NightColorOnSurfaceVariant
import com.example.ui.theme.NightColorPrimary
import com.example.ui.theme.NightColorPrimaryContainer
import com.example.ui.theme.NightColorSurfaceBright
import com.example.ui.theme.NightColorSurfaceSheet
import com.example.ui.theme.NightTextColorSecondary
import com.example.ui.theme.SoraFamily
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.delay

// =====================================================================
// THE LAUNCHER BLOCK SURFACE (Sarthak's ruling, Oct 30 04:29 IST, on
// issue #22): "the block screen should be like a launcher or something
// which u cant exit and had a seperate screen for apps allowed and
// peek ones as usual beautiful ui... The whole design needs to be
// revamped. Make the app crazy good."
//
// While a session is sealed this surface IS the phone's home inside
// Monolith: one dial object carries the session's state, and two
// doors lead out of it — ALLOWED APPS (OPEN tier, tap launches) and
// PEEK APPS (PEEK_2M tier, tap raises that app's Held Door, where the
// existing peek offer machinery decides). The surface creates NO new
// exit: the only ways out remain the Emergency Break valve, the end
// of the session, and the doors the tiers already lawfully have.
// =====================================================================

/** The launcher's three rooms. */
enum class LauncherPage { HOME, ALLOWED, PEEK }

/** The tier partition the launcher prints and navigates by — derived
 *  from the rows' own resolved tiers, so a count here can never
 *  disagree with the Shield's truth. */
data class LauncherPartition(
    val allowed: List<BlockedAppEntity>,
    val peek: List<BlockedAppEntity>,
    val shut: List<BlockedAppEntity>
)

fun launcherPartition(apps: List<BlockedAppEntity>): LauncherPartition {
    val byName = compareBy<BlockedAppEntity, String>(String.CASE_INSENSITIVE_ORDER) { it.appName }
    return LauncherPartition(
        allowed = apps.filter { it.resolvedTier == AppBlockTier.OPEN }.sortedWith(byName),
        peek = apps.filter { it.resolvedTier == AppBlockTier.PEEK_2M }.sortedWith(byName),
        shut = apps.filter { it.resolvedTier == AppBlockTier.LOCKED }.sortedWith(byName)
    )
}

/** Session progress as a fraction, clamped — a dial may never sweep
 *  past its own rim, whatever the rows say. */
fun launcherProgress(elapsedSeconds: Int, targetSeconds: Int): Float {
    if (targetSeconds <= 0) return 0f
    return (elapsedSeconds.toFloat() / targetSeconds.toFloat()).coerceIn(0f, 1f)
}

/** The dial's numeral: MM:SS under an hour, H:MM:SS above it.
 *  A negative remainder is a finished session, printed as zero —
 *  never as a debt. */
fun launcherRemainingPrint(remainingSeconds: Int): String {
    val s = remainingSeconds.coerceAtLeast(0)
    return if (s < 3600) {
        String.format(Locale.US, "%02d:%02d", s / 60, s % 60)
    } else {
        String.format(Locale.US, "%d:%02d:%02d", s / 3600, (s % 3600) / 60, s % 60)
    }
}

/**
 * The launcher's haptic vocabulary (Scout-1 finds #1/#2): platform
 * composition primitives where the hardware speaks them, predefined
 * effects below that, silence where nothing honest exists. Budget:
 * at most one event per press — clear, never buzzy.
 */
object SealHaptics {
    private fun vibrator(context: Context): Vibrator? = runCatching {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            (context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager)
                ?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }.getOrNull()

    private fun compose(context: Context, build: (VibrationEffect.Composition) -> Unit) {
        val vib = vibrator(context) ?: return
        runCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val supported = runCatching {
                    vib.arePrimitivesSupported(
                        VibrationEffect.Composition.PRIMITIVE_CLICK,
                        VibrationEffect.Composition.PRIMITIVE_THUD,
                        VibrationEffect.Composition.PRIMITIVE_TICK
                    )
                }.getOrNull()
                if (supported != null && supported.all { it }) {
                    val composition = VibrationEffect.startComposition()
                    build(composition)
                    vib.vibrate(composition.compose())
                    return
                }
            }
            vib.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
        }
    }

    /** A machined plate going down under the finger. */
    fun doorPress(context: Context) = compose(context) {
        it.addPrimitive(VibrationEffect.Composition.PRIMITIVE_CLICK, 0.9f)
    }

    /** The seal's own weight, for the dial at the moment of opening. */
    fun sealWeight(context: Context) = compose(context) {
        it.addPrimitive(VibrationEffect.Composition.PRIMITIVE_THUD, 0.8f)
    }

    /** One detent tick — an app tile seating. */
    fun tileSeat(context: Context) = compose(context) {
        it.addPrimitive(VibrationEffect.Composition.PRIMITIVE_TICK, 0.6f)
    }
}

@Composable
fun LauncherBlockScreen(
    visible: Boolean
,
    session: FocusSessionEntity?,
    apps: List<BlockedAppEntity>,
    initialPage: LauncherPage = LauncherPage.HOME,
    onClose: () -> Unit,
    onLaunchApp: (BlockedAppEntity) -> Unit,
    onPeekApp: (BlockedAppEntity) -> Unit,
    onRequestEmergencyBreak: () -> Unit
) {
    if (!visible) return
    val context = LocalContext.current
    var page by remember(visible, session?.id) { mutableStateOf(initialPage) }
    val partition = remember(apps) { launcherPartition(apps) }

    // The dial's clock: derived from the session's own numbers at
    // open, ticked locally each second — the same derivation the
    // Airlock's session line uses (now + the span's remaining), so
    // the two surfaces can never print two different ends.
    var nowMs by remember(session?.id) { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(visible, session?.id) {
        SealHaptics.sealWeight(context)
        while (true) {
            nowMs = System.currentTimeMillis()
            delay(1000L)
        }
    }
    val endsAtMs = remember(session?.id, session?.spanRemainingSeconds) {
        if (session == null || session.isStopwatchMode) null
        else System.currentTimeMillis() + session.spanRemainingSeconds * 1000L
    }
    val remainingSeconds = endsAtMs?.let { ((it - nowMs) / 1000L).toInt().coerceAtLeast(0) }
    val progress = if (session == null) 0f else launcherProgress(
        elapsedSeconds = session.spanTargetSeconds - (remainingSeconds ?: session.spanRemainingSeconds),
        targetSeconds = session.spanTargetSeconds
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NightColorBackground)
            .testTag("launcher_block_screen")
    ) {
        when (page) {
            LauncherPage.HOME -> LauncherHome(
                session = session,
                remainingSeconds = remainingSeconds,
                endsAtMs = endsAtMs,
                progress = progress,
                allowedCount = partition.allowed.size,
                peekCount = partition.peek.size,
                shutCount = partition.shut.size,
                onOpenAllowed = { SealHaptics.doorPress(context); page = LauncherPage.ALLOWED },
                onOpenPeek = { SealHaptics.doorPress(context); page = LauncherPage.PEEK },
                onClose = onClose,
                onRequestEmergencyBreak = onRequestEmergencyBreak
            )
            LauncherPage.ALLOWED -> LauncherAppsPage(
                title = "ALLOWED APPS",
                sub = "OPEN — TAP TO USE",
                apps = partition.allowed,
                emptyText = "NOTHING IS OPEN IN THIS SESSION",
                onBack = { page = LauncherPage.HOME },
                onAppTap = { app -> SealHaptics.tileSeat(context); onLaunchApp(app) }
            )
            LauncherPage.PEEK -> LauncherAppsPage(
                title = "PEEK APPS",
                sub = "2M PEEK — THE DOOR DECIDES",
                apps = partition.peek,
                emptyText = "NO PEEK APPS ARE SET",
                onBack = { page = LauncherPage.HOME },
                onAppTap = { app -> SealHaptics.tileSeat(context); onPeekApp(app) }
            )
        }
    }
}

@Composable
private fun LauncherHome(
    session: FocusSessionEntity?,
    remainingSeconds: Int?,
    endsAtMs: Long?,
    progress: Float,
    allowedCount: Int,
    peekCount: Int,
    shutCount: Int,
    onOpenAllowed: () -> Unit,
    onOpenPeek: () -> Unit,
    onClose: () -> Unit,
    onRequestEmergencyBreak: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        androidx.compose.material3.Text(
            text = "M O N O L I T H",
            fontFamily = SoraFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = NightTextColorSecondary
        )
        androidx.compose.material3.Text(
            text = if (session != null) "SEALED" else "NO SESSION SEALED",
            fontFamily = AtkinsonHyperlegibleFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            color = NightColorPrimaryContainer
        )

        Spacer(Modifier.height(30.dp))

        // THE DIAL — one object carries the state: ring (form),
        // numeral (number), intention (word). Any one of the three
        // still tells the truth alone (the TP-7 test).
        Box(contentAlignment = Alignment.Center) {
            Canvas(modifier = Modifier.size(248.dp)) {
                val stroke = 10.dp.toPx()
                val inset = stroke / 2 + 2.dp.toPx()
                val arcSize = Size(size.width - inset * 2, size.height - inset * 2)
                drawArc(
                    color = NightColorSurfaceBright,
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = false,
                    topLeft = Offset(inset, inset),
                    size = arcSize,
                    style = Stroke(width = stroke)
                )
                if (progress > 0f) {
                    drawArc(
                        color = NightColorPrimaryContainer,
                        startAngle = -90f,
                        sweepAngle = 360f * progress,
                        useCenter = false,
                        topLeft = Offset(inset, inset),
                        size = arcSize,
                        style = Stroke(width = stroke)
                    )
                }
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                androidx.compose.material3.Text(
                    text = when {
                        session == null -> "—"
                        session.isStopwatchMode ->
                            launcherRemainingPrint(session.spanElapsedSeconds)
                        else -> launcherRemainingPrint(remainingSeconds ?: session.spanRemainingSeconds)
                    },
                    fontFamily = AnonymousProFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 46.sp,
                    color = NightColorPrimary
                )
                androidx.compose.material3.Text(
                    text = when {
                        session == null -> ""
                        session.isStopwatchMode -> "ELAPSED · NO SET END"
                        else -> "REMAINING"
                    },
                    fontFamily = AtkinsonHyperlegibleFamily,
                    fontSize = 10.5.sp,
                    color = NightTextColorSecondary
                )
            }
        }

        Spacer(Modifier.height(14.dp))

        if (session != null && session.isInSafeBreak) {
            // Safe-break eyebrow (design-revamp salvage, D-04): the
            // Deck's own eyebrow for this state, verbatim in form —
            // the branch's first wording claimed the session clock
            // was paused while the dial under it kept counting; the
            // truth is the break minutes remaining.
            androidx.compose.material3.Text(
                text = "SAFE BREAK · ${(session.safeBreakSecondsRemaining + 59) / 60} MIN",
                fontFamily = AnonymousProFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                letterSpacing = 1.5.sp,
                color = NightColorPrimaryContainer,
                textAlign = TextAlign.Center,
                modifier = Modifier.testTag("launcher_safe_break_eyebrow")
            )
            Spacer(Modifier.height(8.dp))
        }
        if (session != null && session.intention.isNotBlank()) {
            androidx.compose.material3.Text(
                text = session.intention,
                fontFamily = SoraFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 17.sp,
                color = NightColorPrimary,
                textAlign = TextAlign.Center
            )
        }
        if (endsAtMs != null) {
            androidx.compose.material3.Text(
                text = "ENDS " + ClockText.timeOfDay(LocalContext.current, endsAtMs),
                fontFamily = AnonymousProFamily,
                fontSize = 13.sp,
                color = NightTextColorSecondary
            )
        }
        if (session != null && shutCount > 0) {
            androidx.compose.material3.Text(
                text = "$shutCount SHUT",
                fontFamily = AtkinsonHyperlegibleFamily,
                fontSize = 11.sp,
                color = NightColorOnSurfaceVariant
            )
        }

        Spacer(Modifier.height(30.dp))

        LauncherDoorPlate(
            title = "ALLOWED APPS",
            count = allowedCount,
            sub = "OPEN — TAP TO USE",
            testTag = "launcher_door_allowed",
            onClick = onOpenAllowed
        )
        Spacer(Modifier.height(14.dp))
        LauncherDoorPlate(
            title = "PEEK APPS",
            count = peekCount,
            sub = "2M PEEK — THE DOOR DECIDES",
            testTag = "launcher_door_peek",
            onClick = onOpenPeek
        )

        Spacer(Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            androidx.compose.material3.Text(
                text = "RETURN TO THE DECK",
                fontFamily = AtkinsonHyperlegibleFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = NightColorPrimary,
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .clickable(onClick = onClose)
                    .padding(horizontal = 10.dp, vertical = 10.dp)
                    .testTag("launcher_close")
            )
            androidx.compose.material3.Text(
                text = "EMERGENCY BREAK",
                fontFamily = AtkinsonHyperlegibleFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = NightColorPrimaryContainer,
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .clickable(onClick = onRequestEmergencyBreak)
                    .padding(horizontal = 10.dp, vertical = 10.dp)
                    .testTag("launcher_emergency_break")
            )
        }
    }
}

/**
 * A door plate built like a machine key: raised at rest, it travels
 * when pressed — shadow collapses, face scales a breath, the spring
 * is heavy (mass, not bounce). REST / PRESS / SEATED are three
 * drawings of one physical law.
 */
@Composable
private fun LauncherDoorPlate(
    title: String,
    count: Int,
    sub: String,
    testTag: String,
    onClick: () -> Unit
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.975f else 1f,
        animationSpec = spring(dampingRatio = 0.55f, stiffness = Spring.StiffnessMedium),
        label = "launcher_plate_press"
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .shadow(if (pressed) 1.dp else 6.dp, RoundedCornerShape(18.dp))
            .clip(RoundedCornerShape(18.dp))
            .background(NightColorSurfaceBright)
            .border(1.dp, NightColorSurfaceSheet, RoundedCornerShape(18.dp))
            .clickable(interactionSource = interaction, indication = null, onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 18.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            androidx.compose.material3.Text(
                text = title,
                fontFamily = SoraFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = NightColorPrimary
            )
            androidx.compose.material3.Text(
                text = sub,
                fontFamily = AtkinsonHyperlegibleFamily,
                fontSize = 11.5.sp,
                color = NightTextColorSecondary
            )
        }
        androidx.compose.material3.Text(
            text = count.toString(),
            fontFamily = AnonymousProFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp,
            color = NightColorPrimaryContainer
        )
    }
}

@Composable
private fun LauncherAppsPage(
    title: String,
    sub: String,
    apps: List<BlockedAppEntity>,
    emptyText: String,
    onBack: () -> Unit,
    onAppTap: (BlockedAppEntity) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 24.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            androidx.compose.material3.Text(
                text = "‹ LAUNCHER",
                fontFamily = AtkinsonHyperlegibleFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = NightColorPrimary,
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .clickable(onClick = onBack)
                    .padding(horizontal = 8.dp, vertical = 8.dp)
                    .testTag("launcher_page_back")
            )
            Spacer(Modifier.size(10.dp))
            Column {
                androidx.compose.material3.Text(
                    text = title,
                    fontFamily = SoraFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 19.sp,
                    color = NightColorPrimary
                )
                androidx.compose.material3.Text(
                    text = sub,
                    fontFamily = AtkinsonHyperlegibleFamily,
                    fontSize = 11.sp,
                    color = NightTextColorSecondary
                )
            }
        }

        Spacer(Modifier.height(18.dp))

        if (apps.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                androidx.compose.material3.Text(
                    text = emptyText,
                    fontFamily = AtkinsonHyperlegibleFamily,
                    fontSize = 13.sp,
                    color = NightTextColorSecondary,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                contentPadding = PaddingValues(4.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.testTag("launcher_app_grid")
            ) {
                items(apps, key = { it.packageName }) { app ->
                    LauncherAppTile(app = app, onTap = { onAppTap(app) })
                }
            }
        }
    }
}

@Composable
private fun LauncherAppTile(app: BlockedAppEntity, onTap: () -> Unit) {
    val context = LocalContext.current
    val iconBitmap: Bitmap? = remember(app.packageName) {
        runCatching {
            val drawable = context.packageManager.getApplicationIcon(app.packageName)
            val bmp = Bitmap.createBitmap(96, 96, Bitmap.Config.ARGB_8888)
            val canvas = android.graphics.Canvas(bmp)
            drawable.setBounds(0, 0, 96, 96)
            drawable.draw(canvas)
            bmp
        }.getOrNull()
    }
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.93f else 1f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessMedium),
        label = "launcher_tile_press"
    )
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .scale(scale)
            .clip(RoundedCornerShape(16.dp))
            .clickable(interactionSource = interaction, indication = null, onClick = onTap)
            .padding(vertical = 6.dp)
            .testTag("launcher_app_${app.packageName}")
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .shadow(if (pressed) 1.dp else 4.dp, RoundedCornerShape(14.dp))
                .clip(RoundedCornerShape(14.dp))
                .background(NightColorSurfaceBright),
            contentAlignment = Alignment.Center
        ) {
            if (iconBitmap != null) {
                Image(
                    bitmap = iconBitmap.asImageBitmap(),
                    contentDescription = app.appName,
                    modifier = Modifier.size(40.dp)
                )
            } else {
                androidx.compose.material3.Text(
                    text = app.appName.take(1).uppercase(Locale.US),
                    fontFamily = SoraFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = NightColorPrimary
                )
            }
        }
        Spacer(Modifier.height(5.dp))
        androidx.compose.material3.Text(
            text = app.appName,
            fontFamily = AtkinsonHyperlegibleFamily,
            fontSize = 10.5.sp,
            color = NightColorPrimary,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}
