package com.example

import android.content.Context
import android.graphics.Bitmap
import androidx.activity.ComponentActivity
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import com.example.data.MonolithDatabase
import com.example.data.StrongroomDayBirth
import com.example.data.StrongroomHeldArrivalEntity
import com.example.data.StrongroomStore
import kotlinx.coroutines.runBlocking
import com.example.domain.LiftingPlate
import com.example.domain.StrongroomComposition
import com.example.domain.StrongroomDay
import com.example.domain.StrongroomGuard
import com.example.ui.doorway.DoorFace
import com.example.ui.doorway.Doorway
import com.example.ui.doorway.DoorwayState
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
import androidx.compose.ui.graphics.toArgb
import com.example.ui.lifting.Lifting
import com.example.ui.lifting.LiftingCeremony
import com.example.ui.lifting.LiftingState
import com.example.ui.lifting.PlatePileView
import com.example.ui.lifting.StruckPlateView
import com.example.ui.sealed.SealedFace
import com.example.ui.sealed.SealedFaceActions
import com.example.ui.sealed.SealedFaceState
import com.example.ui.signing.Signing
import com.example.ui.signing.SigningActions
import com.example.ui.signing.SigningState
import com.example.ui.signing.SigningToken
import com.example.ui.foundation.StrongroomInks
import com.example.ui.foundation.StrongroomLightState
import com.example.ui.foundation.StrongroomTheme
import com.example.ui.signing.PrintBenchPose
import com.example.ui.signing.SigningPrintBench
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeUp
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.io.File
import java.io.FileOutputStream
import java.time.DayOfWeek
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
        val density = activity.resources.displayMetrics.density
        val w = (412 * density).toInt()
        val h = (915 * density).toInt()
        // LAY OUT AT THE SHOT'S OWN SIZE BEFORE ANY INTERACTION
        // (cure 2c, found on this slice's pile shots): under the
        // Robolectric window's small default layout, a scrollable
        // room's lower nodes report clamped bounds, and the
        // "lowest clickable" pick landed on the wrong node — the
        // pile's second step never opened and its shots repeated
        // the room. At the true size the geometry is honest, the
        // corner is the corner, and the click is his hand's.
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
        }
        interact()
        composeRule.waitForIdle()
        val holder = java.util.concurrent.atomic.AtomicReference<Bitmap>()
        composeRule.runOnUiThread {
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

    /** Write the shot and hold it to the harness's gate — the
     *  gate measures what the visual bar actually cares about
     *  (BUILD-SR-38, on landed evidence): the shot must exist and
     *  be uncorrupted (a 5,000-byte floor — a truly blank 412x915
     *  field compresses under it); it must carry real colour
     *  VARIANCE (> 40 distinct sampled colours — the flat-banded
     *  first shutter failed here in spirit: a handful of tones is
     *  a placeholder, however large its file); and the surface
     *  must be POPULATED (> 10% of sampled pixels off the field
     *  colour — a lawful but field-heavy step, like the held
     *  pile's second step at ~30% coverage, passes on its
     *  furniture; an empty room at ~0% fails). The earlier
     *  byte-size-only form (15,000) is retired: it passed stale
     *  captures and failed a lawful sparse step — it measured
     *  the file, not the surface. */
    private fun writeShot(name: String, bitmap: Bitmap) {
        val dir = File(System.getenv("SCREENSHOTS_DIR") ?: "build/outputs/screenshots")
        dir.mkdirs()
        val file = File(dir, name)
        FileOutputStream(file).use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
        assertTrue("shot must exist: ${file.absolutePath}", file.exists() && file.length() > 0)
        assertTrue(
            "shot must be an uncorrupted render (${file.length()} bytes)",
            file.length() > 5_000
        )
        val fieldArgb = StrongroomInks.Graphite.toArgb()
        val colors = HashSet<Int>()
        var samples = 0
        var offField = 0
        var x = 0
        while (x < bitmap.width) {
            var y = 0
            while (y < bitmap.height) {
                val px = bitmap.getPixel(x, y)
                colors.add(px)
                samples++
                if (px != fieldArgb) offField++
                y += 7
            }
            x += 7
        }
        assertTrue(
            "shot must carry real colour variance (distinct sampled colours: ${colors.size})",
            colors.size > 40
        )
        assertTrue(
            "shot must be a populated surface (off-field coverage: " +
                "${100 * offField / samples.coerceAtLeast(1)}%)",
            samples > 0 && offField.toDouble() / samples > 0.10
        )
    }

    /** The lawful-sparse gate (LEAD-SEALED live leg, R-10's
     *  direction): a face whose design IS field and furniture —
     *  the shut drawer front under the table, the open drawer's
     *  room, the handing-back threshold — compresses under the
     *  standard byte floor and populates under the standard
     *  coverage tenth, and failed both while rendering truly
     *  (venue evidence: drawer_frontShut and
     *  drawer_thresholdHandingBack at the 5,000-byte assert,
     *  drawer_openRich at the coverage assert, on every main
     *  run — which starved the venue of EVERY face's screens).
     *  The sparse gate keeps the honesty checks that still bite
     *  on a flat design — the shot exists, and it carries real
     *  colour variance (a blank field yields a handful of
     *  tones) and some furniture off the field — and prints its
     *  measured numbers to the run log, so the record shows
     *  what the render actually holds. */
    private fun writeShotSparse(name: String, bitmap: Bitmap) {
        val dir = File(System.getenv("SCREENSHOTS_DIR") ?: "build/outputs/screenshots")
        dir.mkdirs()
        val file = File(dir, name)
        FileOutputStream(file).use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
        assertTrue("shot must exist: ${file.absolutePath}", file.exists() && file.length() > 0)
        val fieldArgb = StrongroomInks.Graphite.toArgb()
        val colors = HashSet<Int>()
        var samples = 0
        var offField = 0
        var x = 0
        while (x < bitmap.width) {
            var y = 0
            while (y < bitmap.height) {
                val px = bitmap.getPixel(x, y)
                colors.add(px)
                samples++
                if (px != fieldArgb) offField++
                y += 7
            }
            x += 7
        }
        val coverage = if (samples > 0) offField.toDouble() / samples else 0.0
        println(
            "SPARSE-SHOT $name bytes=${file.length()} colours=${colors.size} " +
                "coverage=${(1000 * coverage).toInt() / 10.0}%"
        )
        assertTrue(
            "sparse shot must carry real colour variance " +
                "(distinct sampled colours: ${colors.size})",
            colors.size > 25
        )
        assertTrue(
            "sparse shot must carry its furniture (off-field coverage: " +
                "${(1000 * coverage).toInt() / 10.0}%)",
            coverage > 0.02
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

    // ── THE DOORWAY (LEAD-DOORFIX) — the opening, as the phone
    // hosts it ─────────────────────────────────────────────────
    // The opening surface composed the way MainActivity's doorway
    // place composes it: the Doorway in its weight(1f) reach, the
    // signing region below at weight(1.35f) — standing EMPTY here,
    // as the host's SigningHost renders nothing on a day with no
    // composed day standing (the state Sarthak's phone opened in,
    // 2026-11-03 03:35 IST) — and the drawer's shut front at the
    // foot. One place, shot whole: this is the app's first screen.

    private fun shootDoorway(name: String) {
        val bitmap = drawShot {
            StrongroomTheme(lightState = StrongroomLightState.DAY) {
                Box(Modifier.size(412.dp, 915.dp)) {
                    // The face behind the whole place, as the host
                    // lays it (LEAD-DOORFIX): the seven-ink print,
                    // edge to edge — the door IS the surface.
                    DoorFace()
                    Column(Modifier.fillMaxSize()) {
                        Box(Modifier.weight(1f)) {
                            Doorway(state = DoorwayState.AtRest)
                        }
                        Box(Modifier.weight(1.35f)) {}
                        DrawerFront(onOpen = {})
                    }
                }
            }
        }
        writeShot(name, bitmap)
    }

    @Test
    fun doorway_opening_atRest() {
        shootDoorway("doorway-opening.png")
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

    private fun shootDrawer(
        name: String,
        sparse: Boolean = false,
        content: @androidx.compose.runtime.Composable () -> Unit
    ) {
        val bitmap = drawShot {
            StrongroomTheme(lightState = StrongroomLightState.DAY) {
                Box(Modifier.size(412.dp, 915.dp)) { content() }
            }
        }
        if (sparse) writeShotSparse(name, bitmap) else writeShot(name, bitmap)
    }

    @Test
    fun drawer_frontShut() {
        shootDrawer("drawer-front.png", sparse = true) {
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
        shootDrawer("drawer-open-rich.png", sparse = true) {
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

    // ── DEVICE-TRUTH: THE EMPTY DRAWER AT HIS PHONE'S SPEC ────
    // The state Sarthak's phone actually carries (build 4 photo,
    // 2026-11-03 04:13 IST): a migrated install whose Strongroom
    // drawer is EMPTY — no struck plates, no stored tasks — opened
    // full-screen. The harness never shot it: every drawer fixture
    // is seeded rich, every shot is composed in DAY light (where
    // the wood grain's lit partner vanishes and the body renders
    // flat by construction), and the frame is 412x915dp. On the
    // phone the doorway place lives in SIGNING light at
    // 1440x3168 / density 3.1875 (OnePlus 13, 510dpi class), and
    // there the old WOOD grain (13dp cells, 6:1 stretch) stood as
    // hard-edged violet bars wall-to-wall. This shot renders that
    // exact state at that exact spec in the production light, so
    // the bars can never hide behind the harness again. The gate
    // is the sparse gate (non-blank, real variance); the render
    // itself is the witness — it ships in the screens artifact.
    @Test
    fun drawer_openEmpty_deviceSpec() {
        val bitmap = drawShotDevice {
            StrongroomTheme(lightState = StrongroomLightState.SIGNING) {
                Drawer(
                    state = DrawerState(
                        contents = DrawerContents.Empty,
                        handingBack = LiftingPlate.HandingBack(emptyList()),
                        handingBackDelivered = true
                    ),
                    actions = DrawerActions()
                )
            }
        }
        writeShotSparse("drawer-open-empty-device.png", bitmap)
    }

    /** The capture at a real device's spec: the bitmap IS the
     *  device's pixel grid (1440x3168) and the composition runs
     *  under the device's own density (510 / 160 = 3.1875), so
     *  every dp lands where his phone puts it. Same hand-drawn
     *  discipline as [drawShot] — measured, laid out, drawn. */
    private fun drawShotDevice(
        content: @androidx.compose.runtime.Composable () -> Unit
    ): Bitmap {
        val activity = composeRule.activity
        val view = androidx.compose.ui.platform.ComposeView(activity)
        val w = 1440
        val h = 3168
        composeRule.runOnUiThread {
            view.setContent {
                CompositionLocalProvider(
                    LocalDensity provides Density(3.1875f)
                ) { content() }
            }
            activity.setContentView(view)
        }
        composeRule.waitForIdle()
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
        }
        composeRule.waitForIdle()
        val holder = java.util.concurrent.atomic.AtomicReference<Bitmap>()
        composeRule.runOnUiThread {
            view.layout(0, 0, w, h)
            val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
            view.draw(android.graphics.Canvas(bmp))
            holder.set(bmp)
        }
        return holder.get()
    }

    // ── THE SIGNING PRINT BENCH (DESIGN-MOTION) ────────────────
    // The moving-parts-on-a-photograph prototype: the picked
    // print as the still world, the crafted wheel and dial
    // seated on it in shadow and grain. Two stills — the bench
    // AT REST (wheel at 0°, the dial hand seated on its detent,
    // the record's 20:00 read as the face's 8) and MID-TURN
    // (the wheel at 41° with its ghost trail standing, the dial
    // hand at 10:43 — between the 10:30 and 10:45 detents, the
    // spring not yet caught — one seat standing taken).

    private fun benchState(): SigningState = SigningState(
        day = StrongroomComposition.ComposedDay(
            filingDateIso = "2026-08-31",
            shape = StrongroomComposition.DayShape.ORDINARY,
            record = signing(),
            amendedBySigning = false
        ),
        tokens = labels.map { SigningToken(it.key, it.value) },
        guard = StrongroomGuard.GuardState(allowanceMinutes = 30)
    )

    private fun benchActions() = SigningActions(
        onAmend = {},
        onAllowanceMove = {},
        onSignature = {}
    )

    private fun shootBench(name: String, pose: PrintBenchPose) {
        val bitmap = drawShot {
            StrongroomTheme(lightState = StrongroomLightState.SIGNING) {
                Box(Modifier.size(412.dp, 915.dp)) {
                    SigningPrintBench(
                        state = benchState(),
                        actions = benchActions(),
                        pose = pose
                    )
                }
            }
        }
        writeShot(name, bitmap)
    }

    @Test
    fun signing_printBench_atRest() {
        shootBench("signing-print-bench-rest.png", PrintBenchPose())
    }

    @Test
    fun signing_printBench_midTurn() {
        shootBench(
            "signing-print-bench-midturn.png",
            PrintBenchPose(
                wheelDeg = 41f,
                wheelGhostDeg = 9f,
                dialHandMinute = 643,
                seatsTaken = 1
            )
        )
    }

    @Test
    fun signing_printBench_midGesture() {
        // UNDER DRAG, as far as a still can swear to it: the
        // wheel deep in its second pass at 118°, the ghost
        // trail widened to 14° (a faster hand leaves a longer
        // trail), the dial hand at 10:17 — two minutes off the
        // 10:15 detent, the spring in flight — two seats
        // standing. The harness draws settled compositions, so
        // no still can show the drag itself; this is the drag's
        // own frame, named as such.
        shootBench(
            "signing-print-bench-midgesture.png",
            PrintBenchPose(
                wheelDeg = 118f,
                wheelGhostDeg = 14f,
                dialHandMinute = 617,
                seatsTaken = 2
            )
        )
    }

    @Test
    fun drawer_thresholdHandingBack() {
        shootDrawer("drawer-threshold.png", sparse = true) {
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

    // ── THE LIFTING + THE PLATE STRIKE (BUILD-SR-40) and THE
    // SEALED FACE'S LAWFUL VARIANTS (BUILD-SR-37), joined by
    // TEST-SEAT — the blind spots LEAD-LIFT named: until here
    // the harness carried no Lifting face at all and no sealed
    // face. The three seal-truth states stand each on the
    // surface the law gives them, honestly labelled:
    //   · SEAL WHOLE — the sealed face standing as struck (the
    //     face is printed ONCE at the closing and never alters
    //     while the seal stands, so no variant of truth can
    //     stand ON it), and the ceremony's end: the door open,
    //     today's plate struck WHOLE on the pile.
    //   · SEAL BROKEN — never a defaced face (damage imagery is
    //     dead law): a broken day stands as the host derives it
    //     — the failure face (LiftingState.isFailure follows the
    //     plate's own truth, as loadLiftingState derives it),
    //     its plate's seal line reading "seal broken".
    //   · DOOR AJAR — the fallen day's failure face: the door
    //     ajar in a dark room, the plate struck WITH THE GAP
    //     (the bracket pair in its settled form).
    // Ceremony shots freeze the compose test clock
    // (mainClock.autoAdvance = false): the pose drawn is the
    // pose at the state's own ceremony offset, never a drifted
    // frame. Fixture plates are fabricated standings, named as
    // such, built through the very mappings the host uses
    // (StruckPlateView.fromStruckPlate · PlatePileView.fromPlates).

    private fun liftingPlate(
        iso: String,
        heldMillis: Long,
        seal: StrongroomDay.SealState,
        kinds: List<StrongroomDay.PunchKind>,
        crank: Boolean = false,
        gapMillis: Long = 0L,
        task: String? = null
    ) = StrongroomDay.DayPlate(
        filingDateIso = iso,
        hoursHeldMillis = heldMillis,
        punches = kinds.mapIndexed { i, kind ->
            StrongroomDay.Punch(kind, (i + 1) * (heldMillis / (kinds.size + 1)))
        },
        sealState = seal,
        crankSealBroken = crank,
        bracketGapMillis = gapMillis,
        taskLine = task
    )

    private fun plateView(plate: StrongroomDay.DayPlate) =
        StruckPlateView.fromStruckPlate(plate, sealedAtWallMillis = 1_788_000_000_000L)

    private fun wholePlateView() = plateView(
        liftingPlate(
            "2026-11-02", 26_400_000L, StrongroomDay.SealState.LIFTED,
            listOf(
                StrongroomDay.PunchKind.REACH, StrongroomDay.PunchKind.HELD,
                StrongroomDay.PunchKind.SHOWING, StrongroomDay.PunchKind.FLOOR,
                StrongroomDay.PunchKind.REACH
            ),
            task = "finish the optics one-shot"
        )
    )

    private fun restingViews(): List<StruckPlateView> = listOf(
        plateView(
            liftingPlate(
                "2026-11-01", 21_600_000L, StrongroomDay.SealState.LIFTED,
                listOf(
                    StrongroomDay.PunchKind.REACH, StrongroomDay.PunchKind.HELD,
                    StrongroomDay.PunchKind.FLOOR
                ),
                crank = true
            )
        ),
        plateView(
            liftingPlate(
                "2026-10-31", 19_800_000L, StrongroomDay.SealState.LIFTED,
                listOf(
                    StrongroomDay.PunchKind.HELD, StrongroomDay.PunchKind.REACH,
                    StrongroomDay.PunchKind.FLOOR, StrongroomDay.PunchKind.FLOOR
                )
            )
        ),
        plateView(
            liftingPlate(
                "2026-10-30", 2_520_000L, StrongroomDay.SealState.BROKEN,
                listOf(
                    StrongroomDay.PunchKind.FLOOR, StrongroomDay.PunchKind.HELD
                )
            )
        )
    )

    private fun shootLifting(name: String, state: LiftingState) {
        composeRule.mainClock.autoAdvance = false
        try {
            val bitmap = drawShot {
                StrongroomTheme(lightState = StrongroomLightState.DAY) {
                    Box(Modifier.size(412.dp, 915.dp)) {
                        Lifting(state = state)
                    }
                }
            }
            writeShot(name, bitmap)
        } finally {
            composeRule.mainClock.autoAdvance = true
        }
    }

    private fun ceremonyState(offsetMs: Int): LiftingState {
        val whole = wholePlateView()
        return LiftingState(
            plate = whole,
            pile = PlatePileView.fromPlates(listOf(whole) + restingViews()),
            isFailure = false,
            ceremonyStartOffsetMs = offsetMs
        )
    }

    @Test
    fun lifting_doorMidOpen_byWeight() {
        // Half the swing-open's travel on the ceremony's own
        // clock: bolts withdrawn, the door mid-travel BY WEIGHT,
        // the plate not yet struck.
        val midOpen = LiftingCeremony.SwingOpenMs +
            (LiftingCeremony.StrikeMs - LiftingCeremony.SwingOpenMs) / 2
        shootLifting("lifting-door-mid-open.png", ceremonyState(midOpen))
    }

    @Test
    fun lifting_plateStrike_theLanding() {
        // 250 ms past the strike: the door's travel has met its
        // stop, the day is struck, the brass is mid-landing into
        // the tray (PlateLanding's own fraction at this offset).
        shootLifting(
            "lifting-plate-strike.png",
            ceremonyState(LiftingCeremony.StrikeMs + 250)
        )
    }

    @Test
    fun lifting_platePile_theDayStruckWhole() {
        // The score's end: the door rests open, today's plate
        // stands landed on the pile — seal line "seal whole".
        shootLifting(
            "lifting-plate-pile.png",
            ceremonyState(LiftingCeremony.ScoreEndMs)
        )
    }

    @Test
    fun lifting_handingBack_midReturn() {
        // LEAD-MOVES: THE HANDING BACK, mid-return. 1,500 ms into
        // the ceremony — the lock tripped at 600 and four people
        // who waited stand at the threshold as wordless brass
        // slips, each mid-travel into the threshold's return slot
        // on its own staggered body (HandingBackMotion): handed
        // back BEFORE the swing opens (2,680) and long before
        // the plate is struck. No names, no printed count — the
        // slips are the presence; the names live at the Drawer.
        val whole = wholePlateView()
        shootLifting(
            "lifting-handing-back.png",
            LiftingState(
                plate = whole,
                pile = PlatePileView.fromPlates(listOf(whole) + restingViews()),
                isFailure = false,
                waitingSlips = 4,
                ceremonyStartOffsetMs = 1_500
            )
        )
    }

    @Test
    fun lifting_failureFace_sealBroken() {
        val broken = plateView(
            liftingPlate(
                "2026-11-02", 11_700_000L, StrongroomDay.SealState.BROKEN,
                listOf(
                    StrongroomDay.PunchKind.REACH, StrongroomDay.PunchKind.FLOOR,
                    StrongroomDay.PunchKind.FLOOR
                )
            )
        )
        shootLifting(
            "lifting-failure-seal-broken.png",
            LiftingState(
                plate = broken,
                pile = PlatePileView.fromPlates(listOf(broken) + restingViews().take(2)),
                isFailure = broken.isFailure,
                ceremonyStartOffsetMs = LiftingCeremony.ScoreEndMs
            )
        )
    }

    @Test
    fun lifting_failureFace_ajarFallenDay() {
        val ajar = plateView(
            liftingPlate(
                "2026-11-02", 18_120_000L, StrongroomDay.SealState.AJAR,
                listOf(
                    StrongroomDay.PunchKind.REACH, StrongroomDay.PunchKind.HELD,
                    StrongroomDay.PunchKind.FLOOR
                ),
                gapMillis = 5_400_000L
            )
        )
        shootLifting(
            "lifting-failure-ajar.png",
            LiftingState(
                plate = ajar,
                pile = PlatePileView.fromPlates(listOf(ajar) + restingViews().take(1)),
                isFailure = ajar.isFailure,
                ceremonyStartOffsetMs = LiftingCeremony.ScoreEndMs
            )
        )
    }

    // ── THE SEALED FACE (BUILD-SR-37) — the face standing ──────
    // The face stands ONLY from a standing seal
    // (SealedFaceState.standing on a sealed day's record), in
    // its own sealed light, which the composable itself
    // provides. Two lawful standings: the printings cut whole
    // (his sentence engraved, his door-word set), and the kept
    // places standing (sentence and door-word both unset — the
    // blanks are the form, never placeholders).

    private fun sealedSigning(sentence: String?): StrongroomDay.SigningRecord =
        StrongroomDay.SigningRecord(
            filingDateIso = "2026-08-31",
            lockedPackages = emptySet(),
            allowedPackages = labels.keys.toList(),
            liftingTime = LocalTime.of(20, 0),
            sentence = sentence,
            breakLengthMinutes = 30,
            breakFiresAtOffsetMillis = -1L,
            allowanceMinutes = 30,
            gaps = emptyList()
        )

    private fun shootSealedFace(name: String, state: SealedFaceState) {
        composeRule.mainClock.autoAdvance = false
        try {
            val bitmap = drawShot {
                StrongroomTheme(lightState = StrongroomLightState.DAY) {
                    Box(Modifier.size(412.dp, 915.dp)) {
                        SealedFace(
                            state = state,
                            actions = SealedFaceActions(onKnock = {}, onGrilleOpen = {})
                        )
                    }
                }
            }
            writeShot(name, bitmap)
        } finally {
            composeRule.mainClock.autoAdvance = true
        }
    }

    @Test
    fun sealedFace_standing_sealWhole() {
        val state = SealedFaceState.standing(
            sealedSigning("what I settle while I am strong, this holds while I am not"),
            doorWord = "holdfast"
        )!!
        shootSealedFace("sealed-face-standing.png", state)
    }

    @Test
    fun sealedFace_keptPlaces() {
        val state = SealedFaceState.standing(sealedSigning(null), doorWord = null)!!
        shootSealedFace("sealed-face-kept-places.png", state)
    }

    /** THE KNOCK MOMENT (LEAD-SEALED live leg): the standing
     *  face STRUCK — a tap dispatched at its centre under a
     *  frozen test clock, the clock then advanced into the
     *  blow, so the leaf is caught mid-press (the knock
     *  displacement near its full travel, the shadow gap open
     *  at the leaf's top and left edges), before the settle
     *  takes it home. The face's one living moment, held in a
     *  still: proof the face answers a strike as a mechanism,
     *  not a poster. */
    @Test
    fun sealedFace_knockMoment() {
        val state = SealedFaceState.standing(
            sealedSigning("what I settle while I am strong, this holds while I am not"),
            doorWord = "holdfast"
        )!!
        composeRule.mainClock.autoAdvance = false
        try {
            val bitmap = drawShot(
                interact = {
                    // The strike as real motion events on the
                    // decor view, at the laid-out face's own
                    // centre (drawShot has already laid the view
                    // out at the shot's size): down and up 30 ms
                    // apart — a tap, not a press — then the test
                    // clock advanced into the blow.
                    val activity = composeRule.activity
                    val decor = activity.window.decorView
                    val density = activity.resources.displayMetrics.density
                    val cx = 206 * density
                    val cy = 457 * density
                    val now = android.os.SystemClock.uptimeMillis()
                    composeRule.runOnUiThread {
                        val down = android.view.MotionEvent.obtain(
                            now, now, android.view.MotionEvent.ACTION_DOWN, cx, cy, 0
                        )
                        val up = android.view.MotionEvent.obtain(
                            now, now + 30, android.view.MotionEvent.ACTION_UP, cx, cy, 0
                        )
                        decor.dispatchTouchEvent(down)
                        decor.dispatchTouchEvent(up)
                        down.recycle()
                        up.recycle()
                    }
                    composeRule.mainClock.advanceTimeBy(48)
                }
            ) {
                StrongroomTheme(lightState = StrongroomLightState.DAY) {
                    Box(Modifier.size(412.dp, 915.dp)) {
                        SealedFace(
                            state = state,
                            actions = SealedFaceActions(onKnock = {}, onGrilleOpen = {})
                        )
                    }
                }
            }
            writeShot("sealed-face-knock.png", bitmap)
        } finally {
            composeRule.mainClock.autoAdvance = true
        }
    }

    // ── THE DOORWAY, POPULATED · THE SIGNING (METHODS-SEAT,
    // 2026-11-03) — the two standings the harness still lacked:
    // the doorway with its day standing in it (TEST-SEAT's shot
    // carries the opening at rest; this one carries the sheet on
    // the press, the waiting slip, and yesterday's struck plate
    // at the press), and the signing table itself — the last
    // face of the day with no shot at all. Same contract as
    // every face above: fabricated standings, the harness's own
    // gate, one PNG per face.

    /** Tomorrow, standing composed from a fabricated week's
     *  standing order (the P1 fixture's shape): every weekday
     *  ordinary, the template carrying the harness labels into
     *  the vault, a gym gap, and a task in his own words. */
    private fun composedDay(): StrongroomComposition.ComposedDay {
        val template = StrongroomComposition.DayTemplate(
            lockedPackages = labels.keys.toSet(),
            liftingTime = LocalTime.of(6, 0),
            breakLengthMinutes = 30,
            breakFiresAtOffsetMillis = 7_200_000L,
            allowanceMinutes = 30,
            gaps = listOf(
                StrongroomDay.ShapedGap(
                    StrongroomDay.GapKind.GYM, 7_200_000L, 3_600_000L
                )
            ),
            taskLine = "finish the optics one-shot"
        )
        val order = StrongroomComposition.WeekStandingOrder(
            shapeByWeekday = DayOfWeek.values().associateWith {
                StrongroomComposition.DayShape.ORDINARY
            },
            templateByShape = mapOf(
                StrongroomComposition.DayShape.ORDINARY to template
            )
        )
        // 2026-11-03 is a Tuesday (the P1 fixture's own date).
        return StrongroomComposition.compose(order, "2026-11-03")
    }

    /** A whole struck day, as the plate tells it: three graded
     *  punches, the seal whole, the crank seal unbroken. */
    private fun plateTruth(): LiftingPlate.PlateTruth = LiftingPlate.PlateTruth(
        filingDateIso = "2026-11-02",
        hoursHeldMillis = 6L * 3_600_000L,
        punches = listOf(
            LiftingPlate.Punch(
                kind = LiftingPlate.PunchKind.REACH,
                atOffsetMillis = 900_000L, packageName = "app.whatsapp"
            ),
            LiftingPlate.Punch(
                kind = LiftingPlate.PunchKind.HELD,
                atOffsetMillis = 1_800_000L, packageName = "app.youtube"
            ),
            LiftingPlate.Punch(
                kind = LiftingPlate.PunchKind.FLOOR,
                atOffsetMillis = 2_700_000L, packageName = "app.chrome"
            )
        ),
        sealTruth = LiftingPlate.SealTruth.WHOLE,
        crankSealBroken = false
    )

    @Test
    fun doorway_theDayStanding() {
        val bitmap = drawShot {
            StrongroomTheme(lightState = StrongroomLightState.DAY) {
                Box(Modifier.size(412.dp, 915.dp)) {
                    Doorway(
                        state = DoorwayState.fromFacts(
                            composedDay = composedDay(),
                            waitingSlipPresent = true,
                            plate = plateTruth()
                        )
                    )
                }
            }
        }
        writeShot("doorway-day-standing.png", bitmap)
    }

    /** THE BORN DAY ON THE TABLE (DAYBIRTH, 2026-11-03): unlike
     *  every shot above, this standing is NOT fabricated — it is
     *  produced by the production producer itself
     *  ([StrongroomDayBirth]) over the P9 production fixtures in
     *  an in-memory store, exactly as the composed app's entry
     *  paths run it: an evening open births tomorrow COMPOSED
     *  from his loadout, and the table stands alive on it. This
     *  is the shot of FLOW-SEAT's step-1 cure: the signing table
     *  a real user can now reach. */
    @Test
    fun signing_bornDay_theTableAlive() {
        val fx = P9ProductionFixtures
        val context = ApplicationProvider.getApplicationContext<Context>()
        val born = runBlocking {
            val db = Room.inMemoryDatabaseBuilder(
                context, MonolithDatabase::class.java
            ).allowMainThreadQueries().build()
            try {
                val dao = db.monolithDao()
                dao.insertApps(fx.blockedApps)
                dao.insertSite(fx.blockedSite)
                dao.insertProfile(fx.profile)
                dao.insertGuardSettings(fx.guardSettings)
                fx.sessions().forEach { dao.insertSession(it) }
                val outcome = StrongroomDayBirth.ensureStandingDays(
                    StrongroomStore(dao), dao,
                    fx.NOW_MILLIS + 7 * 3_600_000L, fx.UTC
                )
                (outcome as StrongroomDayBirth.Outcome.Standing).tomorrow
            } finally {
                db.close()
            }
        }
        val names = fx.blockedApps.associate { it.packageName to it.appName }
        val bitmap = drawShot {
            StrongroomTheme(lightState = StrongroomLightState.DAY) {
                Box(Modifier.size(412.dp, 915.dp)) {
                    Signing(
                        state = SigningState(
                            day = born,
                            tokens = names.map { (pkg, label) ->
                                SigningToken(pkg, label)
                            },
                            guard = StrongroomGuard.GuardState(
                                allowanceMinutes = born.record.allowanceMinutes
                            ),
                            firstSigning = true
                        ),
                        actions = SigningActions(
                            onAmend = {},
                            onAllowanceMove = {},
                            onSignature = {}
                        )
                    )
                }
            }
        }
        writeShot("signing-born-day.png", bitmap)
    }

    @Test
    fun signing_theTableStanding() {
        val bitmap = drawShot {
            StrongroomTheme(lightState = StrongroomLightState.DAY) {
                Box(Modifier.size(412.dp, 915.dp)) {
                    Signing(
                        state = SigningState(
                            day = composedDay(),
                            tokens = labels.map { (pkg, label) ->
                                SigningToken(pkg, label)
                            },
                            guard = StrongroomGuard.GuardState(
                                allowanceMinutes = 30
                            )
                        ),
                        actions = SigningActions(
                            onAmend = {},
                            onAllowanceMove = {},
                            onSignature = {}
                        )
                    )
                }
            }
        }
        writeShot("signing.png", bitmap)
    }

    // ── The signing table in its own evening light (LEAD-SIGNING) ──
    // The standing shots above read the table under DAY light; the
    // signing is an EVENING act, and the table's dressed metal —
    // minted token edges, struck-brass knobs and discs, the
    // sentence sheet's paper grain — is tuned to the SIGNING
    // light. Two faces: the table as he meets it, and its foot
    // reached by his own scroll (the sheet and the slip).

    private fun eveningSigningState(): SigningState =
        SigningState(
            day = composedDay(),
            tokens = labels.map { (pkg, label) -> SigningToken(pkg, label) },
            guard = StrongroomGuard.GuardState(allowanceMinutes = 30)
        )

    private fun eveningSigningActions(): SigningActions =
        SigningActions(onAmend = {}, onAllowanceMove = {}, onSignature = {})

    @Test
    fun signing_tableTop_evening() {
        val bitmap = drawShot {
            StrongroomTheme(lightState = StrongroomLightState.SIGNING) {
                Box(Modifier.size(412.dp, 915.dp)) {
                    Signing(
                        state = eveningSigningState(),
                        actions = eveningSigningActions()
                    )
                }
            }
        }
        writeShot("signing-table-evening.png", bitmap)
    }

    @Test
    fun signing_tableFoot_evening() {
        val bitmap = drawShot(
            interact = {
                repeat(3) { composeRule.onRoot().performTouchInput { swipeUp() } }
            }
        ) {
            StrongroomTheme(lightState = StrongroomLightState.SIGNING) {
                Box(Modifier.size(412.dp, 915.dp)) {
                    Signing(
                        state = eveningSigningState(),
                        actions = eveningSigningActions()
                    )
                }
            }
        }
        writeShot("signing-table-lower.png", bitmap)
    }
}
