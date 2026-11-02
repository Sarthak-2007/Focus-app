package com.example

import android.graphics.Bitmap
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import com.example.data.StrongroomHeldArrivalEntity
import com.example.domain.LiftingPlate
import com.example.domain.StrongroomDay
import com.example.ui.drawer.Drawer
import com.example.ui.drawer.DrawerActions
import com.example.ui.drawer.DrawerContents
import com.example.ui.drawer.DrawerFront
import com.example.ui.drawer.DrawerState
import com.example.ui.antechamber.Antechamber
import com.example.ui.antechamber.AntechamberActions
import com.example.ui.antechamber.AntechamberModel
import com.example.ui.antechamber.AntechamberState
import com.example.ui.antechamber.AntechamberToken
import com.example.ui.antechamber.HeldSlip
import com.example.ui.`break`.Break
import com.example.ui.`break`.BreakActions
import com.example.ui.`break`.BreakClock
import com.example.ui.`break`.BreakState
import com.example.ui.foundation.StrongroomLightState
import com.example.ui.foundation.StrongroomTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.io.File
import java.io.FileOutputStream
import java.time.LocalTime

/**
 * THE SCREENSHOT HARNESS — revived by BUILD-SR-38 (the venue's
 * Screenshots job invokes this class by name; the class itself
 * died with the Oct-31 wipe and its full re-authoring is the
 * end-phase R-10 act). What stands here is the harness contract —
 * Robolectric renders of THIS tree, one PNG per face, written to
 * SCREENSHOTS_DIR (the venue job's env) or build/outputs/
 * screenshots — carrying THE ANTECHAMBER's faces (BUILD-SR-38's
 * slice), populated and finished: the room dressed with its
 * tokens and its pile standing, the pile's second step (reached
 * by the pile corner's own click, as his hand reaches it), the
 * code slip's cover lifted, and the brass shutter closed over a
 * spent purse. Other slices' faces join this harness at R-10; a
 * face that cannot yet look finished is named on its slice's PR,
 * never faked here.
 *
 * The harness facts are FABRICATED STANDINGS, named as such: app
 * labels stand in for the phone's own (the host's token seam),
 * the numbers are invented plausible digits, the arrivals are
 * invented rows. Nothing here is a kept record.
 *
 * Each shot is gated inside the test itself: the PNG must exist,
 * be non-trivial in size, and carry real colour variance — a
 * blank, flat, or placeholder render FAILS the test, by design
 * (the room merges on how it looks — Sarthak's standing condition
 * of 2026-11-03: surface screenshots are user-facing).
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [36])
class ScreenshotHarnessTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private val labels = linkedMapOf(
        "app.whatsapp" to "WhatsApp",
        "app.youtube" to "YouTube",
        "app.instagram" to "Instagram",
        "app.chrome" to "Chrome",
        "app.spotify" to "Spotify"
    )

    private fun signing(): StrongroomDay.SigningRecord = StrongroomDay.SigningRecord(
        filingDateIso = "2026-08-31",
        lockedPackages = emptySet(),
        allowedPackages = labels.keys.toList(),
        liftingTime = LocalTime.of(20, 0),
        sentence = null,
        breakLengthMinutes = 30,
        breakFiresAtOffsetMillis = -1L,
        allowanceMinutes = 30,
        gaps = emptyList()
    )

    private fun arrivals(): List<StrongroomHeldArrivalEntity> = listOf(
        StrongroomHeldArrivalEntity(
            id = 1, filingDateIso = "2026-08-31",
            kind = StrongroomHeldArrivalEntity.KIND_CALL,
            line = "+91 98220 44513", arrivedAtWallMillis = 1_788_000_000_000L
        ),
        StrongroomHeldArrivalEntity(
            id = 2, filingDateIso = "2026-08-31",
            kind = StrongroomHeldArrivalEntity.KIND_CALL,
            line = "+1 415 555 0132", arrivedAtWallMillis = 1_788_000_600_000L
        ),
        StrongroomHeldArrivalEntity(
            id = 3, filingDateIso = "2026-08-31",
            kind = StrongroomHeldArrivalEntity.KIND_CODE,
            line = "482916", arrivedAtWallMillis = 1_788_001_200_000L
        )
    )

    private fun state(
        purse: StrongroomDay.AllowancePurse,
        crank: StrongroomDay.GovernorCrankWeek,
        revealed: Set<Int> = emptySet()
    ): AntechamberState {
        val signing = signing()
        return AntechamberState(
            signing = signing,
            room = AntechamberModel.room(signing, purse, crank, arrivals()),
            tokens = signing.allowedPackages.map {
                AntechamberToken(it, labels[it] ?: it)
            },
            revealedCodeIds = revealed
        )
    }

    private fun freshCrank() =
        StrongroomDay.GovernorCrankWeek(weekAnchorMillis = 1_787_000_000_000L)

    private fun spentCrank() =
        freshCrank().spend(1_788_000_000_000L)!!

    private fun actions() = AntechamberActions(
        onTakeTurned = {},
        onShowCode = { _: HeldSlip -> },
        onResolveHeld = { _: HeldSlip, _: Boolean -> },
        onTakeCall = { _: HeldSlip -> },
        onGovernorCrank = {},
        onCallOut = {},
        onReturnToFace = {}
    )

    private fun shoot(name: String, state: AntechamberState, openPile: Boolean) {
        val bitmap = drawShot(
            interact = {
                if (openPile) {
                    // The pile's second step, by the pile corner's
                    // own click, as his hand reaches it.
                    clickLowestClickable("the room must carry clickables")
                }
            }
        ) {
            StrongroomTheme(lightState = StrongroomLightState.DAY) {
                Box(Modifier.size(412.dp, 915.dp)) {
                    Antechamber(state = state, actions = actions())
                }
            }
        }
        writeShot(name, bitmap)
    }



    /** THE CAPTURE, deterministic (BUILD-SR-38's cure of the
     *  harness it revived): the content is composed in a
     *  ComposeView standing as the activity's content, settled,
     *  optionally interacted with, then MEASURED, LAID OUT, and
     *  DRAWN BY HAND into the bitmap at the shot's own size
     *  (412 x 915 dp). The first capture route — captureToImage on
     *  the semantics node — returned the Robolectric window's
     *  last-drawn surface (320 x 470), STALE across states: this
     *  slice's room, pile, and revealed shots came back
     *  byte-identical, and BUILD-SR-39's running / re-driving
     *  shots with them. A stale shot is a false witness; this draw
     *  cannot be one — the pixels are rendered from the
     *  composition as it stands at the draw. All faces in this
     *  harness shoot through here. */
    private fun drawShot(
        interact: () -> Unit = {},
        content: @androidx.compose.runtime.Composable () -> Unit
    ): Bitmap {
        val activity = composeRule.activity
        val view = androidx.compose.ui.platform.ComposeView(activity)
        composeRule.runOnUiThread {
            view.setContent(content)
            activity.setContentView(view)
        }
        composeRule.waitForIdle()
        interact()
        composeRule.waitForIdle()
        val density = activity.resources.displayMetrics.density
        val w = (412 * density).toInt()
        val h = (915 * density).toInt()
        val holder = java.util.concurrent.atomic.AtomicReference<Bitmap>()
        composeRule.runOnUiThread {
            view.measure(
                android.view.View.MeasureSpec.makeMeasureSpec(
                    w, android.view.View.MeasureSpec.EXACTLY
                ),
                android.view.View.MeasureSpec.makeMeasureSpec(
                    h, android.view.View.MeasureSpec.EXACTLY
                )
            )
            view.layout(0, 0, w, h)
            val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
            view.draw(android.graphics.Canvas(bmp))
            holder.set(bmp)
        }
        return holder.get()
    }

    /** A second step, by its corner's own click — the lowest
     *  clickable in the surface's order. */
    private fun clickLowestClickable(what: String) {
        val clickables = composeRule.onAllNodes(hasClickAction())
        val nodes = clickables.fetchSemanticsNodes()
        assertTrue(what, nodes.isNotEmpty())
        val lowest = nodes.indices.maxByOrNull { nodes[it].boundsInRoot.top }!!
        clickables[lowest].performClick()
    }

    /** Write the shot and hold it to the harness's gate: it must
     *  exist, be non-trivial in size, and carry real colour
     *  variance — a blank, flat, or placeholder render FAILS, by
     *  design (screenshots are user-facing). */
    private fun writeShot(name: String, bitmap: Bitmap) {
        val dir = File(System.getenv("SCREENSHOTS_DIR") ?: "build/outputs/screenshots")
        dir.mkdirs()
        val file = File(dir, name)
        FileOutputStream(file).use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
        assertTrue("shot must exist: ${file.absolutePath}", file.exists() && file.length() > 0)
        assertTrue(
            "shot must be a rich render, not a blank field (${file.length()} bytes)",
            file.length() > 15_000
        )
        val colors = HashSet<Int>()
        var x = 0
        while (x < bitmap.width) {
            var y = 0
            while (y < bitmap.height) {
                colors.add(bitmap.getPixel(x, y))
                y += 7
            }
            x += 7
        }
        assertTrue(
            "shot must carry real colour variance (distinct sampled colours: ${colors.size})",
            colors.size > 40
        )
    }

    @Test
    fun antechamber_room_dressed() {
        shoot(
            "antechamber-room.png",
            state(StrongroomDay.AllowancePurse(30, 12), freshCrank()),
            openPile = false
        )
    }

    @Test
    fun antechamber_heldPile_secondStep() {
        shoot(
            "antechamber-held-pile.png",
            state(StrongroomDay.AllowancePurse(30, 12), freshCrank()),
            openPile = true
        )
    }

    @Test
    fun antechamber_heldPile_codeRevealed() {
        shoot(
            "antechamber-held-pile-code-revealed.png",
            state(StrongroomDay.AllowancePurse(30, 13), freshCrank(), revealed = setOf(3)),
            openPile = true
        )
    }

    @Test
    fun antechamber_shutterClosed_spent() {
        shoot(
            "antechamber-shutter-closed.png",
            state(StrongroomDay.AllowancePurse(30, 30), spentCrank()),
            openPile = false
        )
    }

    // ── THE BREAK (BUILD-SR-39) — the firing, rendered ─────────
    // The break's faces are phases of ONE span (30 minutes,
    // priced at the harness signing above): the firing's first
    // seconds (the door easing, every tooth gold), the span at
    // rest (half the wheel graphite, the rack dressed, the pile
    // standing with its dot), the pile's second step, and the
    // re-drive (the seam nearly shut, the bolts travelling home).
    // The clock is a fabricated standing, named as such — the
    // surface renders it exactly as it renders the engine's.

    private fun breakState(remainingMs: Long): BreakState {
        val signing = signing()
        val waiting = AntechamberModel.waitingPile(arrivals())
        return BreakState(
            signing = signing,
            clock = BreakClock(
                totalMillis = 1_800_000L,
                remainingAtAnchorMillis = remainingMs,
                anchorElapsedMillis = 0L
            ),
            tokens = signing.allowedPackages.map {
                AntechamberToken(it, labels[it] ?: it)
            },
            waiting = waiting,
            quietDot = AntechamberModel.quietDotVisible(waiting)
        )
    }

    private fun breakActions() = BreakActions(
        onLaunchAllowed = {},
        onShowCode = { _: HeldSlip -> },
        onResolveHeld = { _: HeldSlip, _: Boolean -> },
        onTakeCall = { _: HeldSlip -> },
        onKnock = {}
    )

    private fun shootBreak(name: String, remainingMs: Long, openPile: Boolean) {
        val state = breakState(remainingMs)
        val now = state.clock.totalMillis - remainingMs
        val bitmap = drawShot(
            interact = {
                if (openPile) clickLowestClickable("the break must carry clickables")
            }
        ) {
            StrongroomTheme(lightState = StrongroomLightState.SEALED) {
                Box(Modifier.size(412.dp, 915.dp)) {
                    Break(state = state, actions = breakActions(), nowElapsedMillis = now)
                }
            }
        }
        writeShot(name, bitmap)
    }

    @Test
    fun break_easing_theFiringArrives() {
        // 900 ms into the span: the ease mid-rise on its cam, the
        // seam opening, every tooth still gold.
        shootBreak("break-easing.png", remainingMs = 1_799_100L, openPile = false)
    }

    @Test
    fun break_running_theRoomAtRest() {
        // Half the span run: half the wheel graphite, the rack
        // dressed, the pile standing with its dot.
        shootBreak("break-running.png", remainingMs = 900_000L, openPile = false)
    }

    @Test
    fun break_heldPile_secondStep() {
        shootBreak("break-held-pile.png", remainingMs = 900_000L, openPile = true)
    }

    @Test
    fun break_redriving_theSealRestored() {
        // The span's last second and a half: the ease nearly off,
        // the bolts travelling home on the score.
        shootBreak("break-redriving.png", remainingMs = 1_500L, openPile = false)
    }

    // ── THE DRAWER (BUILD-SR-41) — the kept days, rendered ─────
    // The drawer's faces (census C-22 + C-07/C-19 + C-11): the
    // shut wood front as it stands in the doorway room; the
    // drawer open RICH — six struck plates in their pile (the
    // eye-candy condition: a drawer with plates in it, not an
    // empty state), kept crank seals in the tray, stored task
    // slips below; and the threshold standing, where the
    // handing back stands between his hand and the plates.
    // Fixture plates are fabricated standings, named as such.

    private fun drawerPlate(
        iso: String,
        seal: StrongroomDay.SealState,
        kinds: List<StrongroomDay.PunchKind>,
        crank: Boolean = false,
        task: String? = null
    ) = StrongroomDay.DayPlate(
        filingDateIso = iso,
        hoursHeldMillis = 6L * 3_600_000L,
        punches = kinds.mapIndexed { i, kind ->
            StrongroomDay.Punch(kind, (i + 1) * 900_000L)
        },
        sealState = seal,
        crankSealBroken = crank,
        taskLine = task
    )

    private fun drawerContents(): DrawerContents = DrawerContents.derive(
        struckRows = listOf(
            drawerPlate(
                "2026-11-02", StrongroomDay.SealState.LIFTED,
                listOf(
                    StrongroomDay.PunchKind.REACH, StrongroomDay.PunchKind.REACH,
                    StrongroomDay.PunchKind.HELD, StrongroomDay.PunchKind.FLOOR,
                    StrongroomDay.PunchKind.SHOWING, StrongroomDay.PunchKind.REACH,
                    StrongroomDay.PunchKind.FLOOR
                ),
                task = "finish the optics one-shot"
            ),
            drawerPlate(
                "2026-11-01", StrongroomDay.SealState.LIFTED,
                listOf(
                    StrongroomDay.PunchKind.REACH, StrongroomDay.PunchKind.HELD,
                    StrongroomDay.PunchKind.HELD, StrongroomDay.PunchKind.FLOOR
                ),
                crank = true,
                task = "mock test — full syllabus so far"
            ),
            drawerPlate(
                "2026-10-31", StrongroomDay.SealState.BROKEN,
                listOf(
                    StrongroomDay.PunchKind.REACH, StrongroomDay.PunchKind.FLOOR,
                    StrongroomDay.PunchKind.FLOOR
                )
            ),
            drawerPlate(
                "2026-10-30", StrongroomDay.SealState.LIFTED,
                listOf(
                    StrongroomDay.PunchKind.HELD, StrongroomDay.PunchKind.REACH,
                    StrongroomDay.PunchKind.FLOOR, StrongroomDay.PunchKind.FLOOR,
                    StrongroomDay.PunchKind.FLOOR
                )
            ),
            drawerPlate(
                "2026-10-29", StrongroomDay.SealState.LIFTED,
                listOf(StrongroomDay.PunchKind.REACH, StrongroomDay.PunchKind.FLOOR),
                crank = true
            ),
            drawerPlate(
                "2026-10-28", StrongroomDay.SealState.LIFTED,
                listOf(StrongroomDay.PunchKind.FLOOR, StrongroomDay.PunchKind.HELD)
            )
        ),
        storedEntries = listOf(
            StrongroomDay.DrawerEntry(3, "revise thermodynamics notes", 3_000L),
            StrongroomDay.DrawerEntry(2, "plant kingdom one-shot", 2_000L),
            StrongroomDay.DrawerEntry(1, "write the week's errors into the copy", 1_000L)
        )
    )

    private fun shootDrawer(name: String, content: @androidx.compose.runtime.Composable () -> Unit) {
        val bitmap = drawShot {
            StrongroomTheme(lightState = StrongroomLightState.DAY) {
                Box(Modifier.size(412.dp, 915.dp)) { content() }
            }
        }
        writeShot(name, bitmap)
    }

    @Test
    fun drawer_frontShut() {
        shootDrawer("drawer-front.png") {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 24.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {}
                DrawerFront(onOpen = {})
            }
        }
    }

    @Test
    fun drawer_openRich() {
        shootDrawer("drawer-open-rich.png") {
            Drawer(
                state = DrawerState(
                    contents = drawerContents(),
                    handingBack = LiftingPlate.HandingBack(emptyList()),
                    handingBackDelivered = true
                ),
                actions = DrawerActions()
            )
        }
    }

    @Test
    fun drawer_thresholdHandingBack() {
        shootDrawer("drawer-threshold.png") {
            Drawer(
                state = DrawerState(
                    contents = drawerContents(),
                    handingBack = LiftingPlate.HandingBack.prepare(
                        listOf(
                            LiftingPlate.WaitingPerson("+91 98220 44513", "held:1"),
                            LiftingPlate.WaitingPerson("482916", "held:3")
                        )
                    ),
                    handingBackDelivered = false
                ),
                actions = DrawerActions()
            )
        }
    }
}
