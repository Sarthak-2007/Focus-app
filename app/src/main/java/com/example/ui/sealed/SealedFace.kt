package com.example.ui.sealed

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.ui.foundation.DrivenDoor
import com.example.ui.foundation.LocalStrongroomLightState
import com.example.ui.foundation.StrongroomInks
import com.example.ui.foundation.StrongroomLight
import com.example.ui.foundation.StrongroomLightState
import com.example.ui.foundation.StrongroomLitSurface
import com.example.ui.foundation.StrongroomRoles
import com.example.ui.foundation.StrongroomType
import kotlin.math.cos
import kotlin.math.sin

/**
 * C-01 THE DOOR, SEALED · C-12 THE FOUR PRINTINGS · C-03 THE SWING ·
 * C-10 THE KNOCK — the sealed face and its sealing act, authored by
 * BUILD-SR-37 from the rebuild element census (INVENTORY-SR-1),
 * firsthand.
 *
 * THE FACE. While a seal stands, this face IS the launcher and
 * there is nowhere else to go (ledger D-7): no Deck, no signing
 * table, no settings, no records. The face is a STRUCK RECORD,
 * printed ONCE at the closing and never altered while the seal
 * stands — Sarthak killed the Deepening forever: no dark
 * deepening, no subtraction ladder, no crack/damage imagery in
 * any form; the face does not change, age, or react across the
 * sealed hours. What stands on it is exactly the four printings
 * in their decided hierarchy (Stage 5; [SealedPrintings]) and the
 * door's own furniture — the great wheel at rest, the dead dial,
 * the bolts driven home, the grille in the flank. Attempts leave
 * nothing: the knock (below) is answered and absorbed, and the
 * face after it is this same face, byte for byte.
 *
 * THE PAINT BAR (intake §1 — a craft bar, stamped a bar, not a
 * device claim): the sealed face paints detailed-and-instant.
 * Its ground in code: the face is a pure function of ONE immutable
 * snapshot ([SealedFaceState]) derived synchronously from the
 * sealed day's standing record before the face is composed — no
 * flow collection, no loading state, no live re-derivation stands
 * between the seal and its face, so the first frame is the whole
 * face, grain and printings together.
 *
 * LIGHT: the face stands in RELIGHT's sealed state (L-SEALED) —
 * the whole face mass descended 3/8 toward graphite, the engraved
 * hour and the pinstripe glint the two promoted reads, no third.
 *
 * THE BASE (LEAD-SEALED): the face's ground is THE PRINT — the
 * exact seven-ink vault-door photograph Sarthak picked and
 * cleared by his own eye (Nov 1, 2026) — carried full-screen as
 * the leaf itself. The door's furniture stands IN the print: the
 * great wheel, the bolts, the pinstripe are the photograph's own,
 * and its dial is dead the plain way — a photograph does not
 * turn, and never shows a time. The four printings are engraved
 * OVER the print in the palette's own line-work, each cut reading
 * by its light-lip (the foundation's law for the hour: it reads
 * first-glance by its shadow). Only the grille is still drawn —
 * a small lattice in the print's dark floor, in graphite and
 * gold tones. [SealedTexture] remains in this file for the swing
 * act's leaf, which is another surface's business.
 *
 * THE KNOCK (C-10): a tap on the face — a reach's landing inside
 * the room — is answered by the knock and nothing else: the door
 * absorbs one blow through its deadest body ([KnockAnswer]) and
 * is still; no word, no count, no mark, no way onward is shown.
 * (Reaches at the platform's edge are answered by the engine's
 * own knock voice through the bridge — service/StrongroomKnock;
 * the face's [SealedFaceActions.onKnock] is the seam the Stage
 * 13/14 sound/haptic substance rides later.)
 *
 * THE GRILLE (C-08's door, on this face): the antechamber is
 * reached only through the face's own order — the deliberate
 * second touch ([GrilleSecondTouch]): first touch seats the
 * latch, second touch swings the grille, and the host is asked
 * to stand the antechamber. Nothing else on this face moves.
 */

// ────────────────────────────────────────────────────────────────
// Texture — the sealed face's grain (intake §1)
// ────────────────────────────────────────────────────────────────

object SealedTexture {

    enum class Species(val cellPx: Int, val reliefEighthsCap: Int) {
        ENAMEL(cellPx = 7, reliefEighthsCap = 2),
        STEEL(cellPx = 11, reliefEighthsCap = 3),
        BRASS(cellPx = 6, reliefEighthsCap = 2)
    }

    fun grainAt(cellX: Int, cellY: Int, seed: Int): Float {
        var h = (cellX * 0x1E35A7BD) xor (cellY * 0x27D4EB2F) xor (seed * 0x165667B1)
        h = h xor (h ushr 15)
        h *= 0x85EBCA6B.toInt()
        h = h xor (h ushr 13)
        h *= 0xC2B2AE35.toInt()
        h = h xor (h ushr 16)
        return (h and 0xFFFFFF) / 0xFFFFFF.toFloat()
    }

    fun gradientAt(cellX: Int, cellY: Int, seed: Int): Float =
        grainAt(cellX, cellY, seed) * 2f - 1f

    fun cellTone(
        ink: Color,
        field: Color,
        species: Species,
        cellX: Int,
        cellY: Int,
        seed: Int,
        state: StrongroomLightState
    ): Color {
        val g = gradientAt(cellX, cellY, seed)
        val steps = (kotlin.math.abs(g) * species.reliefEighthsCap)
            .toInt().coerceIn(0, species.reliefEighthsCap)
        if (steps == 0) return ink
        return if (g > 0f) {
            StrongroomLight.blendEighth(
                ink, StrongroomLight.litPartner(ink, field, state), steps
            )
        } else {
            StrongroomLight.blendEighth(
                ink, StrongroomLight.shadowPartner(ink), steps
            )
        }
    }
}

// ────────────────────────────────────────────────────────────────
// THE SEALED FACE
// ────────────────────────────────────────────────────────────────

data class SealedFaceActions(
    /** A reach landed on the face: the knock is answered. The
     *  seam for the Stage 13/14 sound/haptic substance; the face
     *  itself changes nothing in answer. */
    val onKnock: () -> Unit,
    /** The grille's second touch swung it: the host stands the
     *  antechamber (the VM's movement law judges the step). */
    val onGrilleOpen: () -> Unit
)

/** The engraving's light-lip for a DARK cut (the hour, graphite):
 *  an engraving reads by the light its lower edge catches — a
 *  warmed gold lip standing just under the glyph, so the cut
 *  reads on the print's bright ring and its dark steel alike. */
private val DarkCutLip = Shadow(
    color = StrongroomLight.sealedPinstripeLitLip().copy(alpha = 0.65f),
    offset = Offset(0f, 3f),
    blurRadius = 3f
)

/** The engraving's depth for a GOLD cut (the door-word, his
 *  sentence): a graphite shadow standing just under the glyph,
 *  so the gold reads on the print's brass and its dark glass
 *  alike. */
private val GoldCutShadow = Shadow(
    color = StrongroomInks.Graphite.copy(alpha = 0.85f),
    offset = Offset(0f, 3f),
    blurRadius = 3f
)

@Composable
fun SealedFace(
    state: SealedFaceState,
    actions: SealedFaceActions,
    modifier: Modifier = Modifier
) {
    // NAV-ALL (the NORMAL-APP redirect, Sarthak's word 2026-11-03):
    // the sealed face is a plain status screen — a title, the
    // lifting hour, his sentence, and two labelled buttons. The
    // acts are unchanged: KNOCK is the knock (the whole face
    // answers a tap the same way — a reach is a knock), and
    // ALLOWED APPS is the grille's second touch into the
    // antechamber, where the allowed apps are taken. The rich
    // print face below (SealedFaceInSealedLight) stands retired
    // in this file for the later art pass; it is no longer the
    // surface. The shell's sealed screen (SealedScreen) adds the
    // allowed-apps grid under this status.
    val printings = state.printings
    androidx.compose.foundation.layout.Column(
        modifier = modifier
            .fillMaxSize()
            .background(com.example.ui.foundation.StrongroomRoles.Field)
            .clickable { actions.onKnock() }
            .padding(horizontal = 24.dp, vertical = 20.dp),
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
    ) {
        androidx.compose.material3.Text(
            text = "Sealed",
            style = com.example.ui.foundation.StrongroomType.Scr3,
            color = com.example.ui.foundation.StrongroomRoles.OnGround
        )
        androidx.compose.material3.Text(
            text = "Locked until ${printings.liftingHour.text}",
            style = com.example.ui.foundation.StrongroomType.Anon2,
            color = com.example.ui.foundation.StrongroomInks.Gold,
            modifier = androidx.compose.ui.Modifier.padding(top = 6.dp)
        )
        androidx.compose.material3.Text(
            text = "“${printings.sentence.text ?: com.example.domain.StrongroomDay.PURPOSE_SENTENCE}”",
            style = com.example.ui.foundation.StrongroomType.Scr1,
            color = com.example.ui.foundation.StrongroomRoles.OnGround
                .copy(alpha = 0.85f),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = androidx.compose.ui.Modifier.padding(top = 16.dp)
        )
        androidx.compose.foundation.layout.Spacer(
            androidx.compose.ui.Modifier.weight(1f)
        )
        androidx.compose.foundation.layout.Box(
            modifier = androidx.compose.ui.Modifier
                .fillMaxWidth()
                .background(com.example.ui.foundation.StrongroomInks.Rose)
                .clickable { actions.onKnock() }
                .padding(vertical = 18.dp),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            androidx.compose.material3.Text(
                text = "KNOCK",
                style = com.example.ui.foundation.StrongroomType.Scr2,
                color = com.example.ui.foundation.StrongroomRoles.Field
            )
        }
        androidx.compose.foundation.layout.Spacer(
            androidx.compose.ui.Modifier.height(12.dp)
        )
        androidx.compose.foundation.layout.Box(
            modifier = androidx.compose.ui.Modifier
                .fillMaxWidth()
                .border(
                    1.dp,
                    com.example.ui.foundation.StrongroomInks.Gold
                )
                .clickable { actions.onGrilleOpen() }
                .padding(vertical = 16.dp),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            androidx.compose.material3.Text(
                text = "ALLOWED APPS",
                style = com.example.ui.foundation.StrongroomType.Scr2,
                color = com.example.ui.foundation.StrongroomInks.Gold
            )
        }
    }
}

@Composable
private fun SealedFaceInSealedLight(
    state: SealedFaceState,
    actions: SealedFaceActions,
    modifier: Modifier = Modifier
) {
    val printings = state.printings

    // THE KNOCK, absorbed: a blow's displacement, decaying dead
    // through the knock body. It is the face's only motion, it is
    // not a state of the face, and when it settles nothing remains.
    var knockElapsedMs by remember { mutableIntStateOf(-1) }
    var knockTrigger by remember { mutableIntStateOf(0) }
    LaunchedEffect(knockTrigger) {
        if (knockTrigger == 0) return@LaunchedEffect
        val start = withFrameMillis { it }
        var ms = 0
        knockElapsedMs = 0
        while (ms <= KnockAnswer.SettleMs) {
            val now = withFrameMillis { it }
            ms = (now - start).toInt()
            knockElapsedMs = ms
        }
        knockElapsedMs = -1
    }
    val knockDisplacement =
        if (knockElapsedMs < 0) 0.0 else KnockAnswer.displacementAt(knockElapsedMs)

    val grilleTouch = remember { GrilleSecondTouch() }
    var latchSeated by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(StrongroomRoles.Field)
            .semantics { contentDescription = "ULYSSES" }
    ) {
        // The leaf and everything printed on it — struck as one
        // body, and the knock's blow moves it as one body.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    translationX = (knockDisplacement * 5.0).toFloat()
                }
                .pointerInput(Unit) {
                    detectTapGestures {
                        actions.onKnock()
                        if (knockElapsedMs < 0) knockTrigger++
                    }
                }
        ) {
            SealedDoorPrint(latchSeated)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 28.dp, vertical = 22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // ── Rank 1 · the lifting hour, engraved on the
                // dial ring (ENG-1): the face's first fact.
                Box(
                    modifier = Modifier.height(150.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = printings.liftingHour.text,
                        style = StrongroomType.Eng1.copy(shadow = DarkCutLip),
                        color = StrongroomLight.sealedHourMark(),
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(Modifier.height(10.dp))
                // ── Rank 2 · the door-word slot (PRT-3): his word,
                // or its kept place — a scribe-line slot standing
                // empty, itself the form.
                val word = printings.doorWord.word
                if (word != null) {
                    Text(
                        text = word,
                        style = StrongroomType.Prt3.copy(shadow = GoldCutShadow),
                        color = StrongroomRoles.OnGround,
                        textAlign = TextAlign.Center
                    )
                } else {
                    KeptPlaceSlot(widthDp = 150, heightDp = 34)
                }
                Spacer(Modifier.height(18.dp))
                // ── Rank 3 · his sentence (PRT-4), or its kept
                // place: ruled blank lines standing for the words
                // that are not there.
                val sentence = printings.sentence.text
                if (sentence != null) {
                    Text(
                        text = sentence,
                        style = StrongroomType.Prt4.copy(shadow = GoldCutShadow),
                        color = StrongroomRoles.OnGround,
                        textAlign = TextAlign.Center,
                        maxLines = 3
                    )
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        KeptPlaceSlot(widthDp = 260, heightDp = 30)
                        Spacer(Modifier.height(10.dp))
                        KeptPlaceSlot(widthDp = 200, heightDp = 30)
                    }
                }
                Spacer(Modifier.height(20.dp))
                // ── Rank 4 · the held mark (PRT-2's place): a
                // shallow punch at the printed floor — a mark, not
                // a glyph, never live, never the quiet dot.
                HeldMarkPunch()
                Spacer(Modifier.weight(1f))
                // The grille's ground is drawn over the print;
                // its touch stands in the overlay below.
                Spacer(Modifier.height(96.dp))
            }
        }
        // THE GRILLE — in the door's flank, bottom-start. Its own
        // touch, in the face's own order: the deliberate second
        // touch, and no other way through.
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 28.dp, bottom = 22.dp)
                .size(width = 92.dp, height = 72.dp)
                .pointerInput(Unit) {
                    detectTapGestures {
                        when (grilleTouch.touch(System.nanoTime() / 1_000_000L)) {
                            GrilleSecondTouch.Result.LATCH_SEATED ->
                                latchSeated = true
                            GrilleSecondTouch.Result.SAME_ACT -> Unit
                            GrilleSecondTouch.Result.OPENED -> {
                                latchSeated = false
                                actions.onGrilleOpen()
                            }
                        }
                    }
                }
        )
    }
}

/** A kept place: an engraved slot standing empty — a shallow
 *  recess with a scribe line at its floor. It is a PRESENCE, not
 *  an absence: the slot was cut at the closing whether or not a
 *  word stands in it. */
@Composable
private fun KeptPlaceSlot(widthDp: Int, heightDp: Int) {
    Box(
        modifier = Modifier
            .size(width = widthDp.dp, height = heightDp.dp)
            .background(
                // A recess cut into the print: graphite dark
                // enough to read as a slot in the metal on any
                // ground the photograph offers.
                StrongroomInks.Graphite.copy(alpha = 0.52f),
                RoundedCornerShape(2.dp)
            )
            .border(
                1.dp,
                StrongroomLight.blendEighth(StrongroomInks.Gold, StrongroomInks.Graphite, 5),
                RoundedCornerShape(2.dp)
            ),
        contentAlignment = Alignment.BottomCenter
    ) {
        Box(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 7.dp)
                .fillMaxWidth()
                .height(1.5.dp)
                .background(StrongroomLight.sealedHourMark())
        )
    }
}

/** The held mark, borne as decided: a shallow no-glyph punch at
 *  the printed floor — a recessed impression a finger could learn,
 *  struck once, identical for the whole seal. */
@Composable
private fun HeldMarkPunch() {
    Canvas(modifier = Modifier.size(26.dp)) {
        // The punch's lit lip (upper-left) and its recessed floor,
        // cut into the print: graphite depths with a gold lip.
        drawCircle(
            color = StrongroomLight.blendEighth(StrongroomInks.Gold, StrongroomInks.Graphite, 6),
            radius = size.minDimension / 2f,
            center = center
        )
        drawCircle(
            color = StrongroomLight.blendEighth(StrongroomInks.Graphite, StrongroomInks.Gold, 1),
            radius = size.minDimension / 2f - 2.dp.toPx(),
            center = center + Offset(0.5.dp.toPx(), 0.5.dp.toPx())
        )
        drawCircle(
            color = StrongroomLight.blendEighth(
                StrongroomInks.Graphite,
                StrongroomLight.shadowPartner(StrongroomInks.Graphite), 2
            ),
            radius = size.minDimension / 4f,
            center = center
        )
    }
}

/** The leaf itself: THE PRINT — the seven-ink vault-door
 *  photograph Sarthak picked and cleared, carried full-screen as
 *  the door. Everything the drawn leaf used to fake stands in the
 *  print for real: the great wheel, the dead dial, the bolts
 *  driven home, the pinstripe. Only the grille is still drawn —
 *  a small lattice in the print's dark floor (bottom-start), in
 *  graphite and gold, with its latch bar seated across when the
 *  first touch has taken. The print is cropped to the screen,
 *  centred on the wheel: its floor band carries the grille, and
 *  the ring's top arc carries the engraved hour. */
@Composable
private fun SealedDoorPrint(latchSeated: Boolean) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.sealed_door_print),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Canvas(modifier = Modifier.fillMaxSize()) {
            val h = size.height
            val lightState = StrongroomLightState.SEALED
            val rimLit = StrongroomLight.relief(
                StrongroomInks.Graphite, StrongroomInks.Violet, 0.85f, lightState
            )
            val pin = StrongroomLight.sealedPinstripe()
            // The grille in the flank (bottom-start): a small
            // lattice door of steel bars over the room's dark, its
            // latch bar seated across when the first touch has
            // taken.
            val gx = 28.dp.toPx()
            val gy = h - 22.dp.toPx() - 72.dp.toPx()
            val gw = 92.dp.toPx()
            val gh = 72.dp.toPx()
            drawRect(color = StrongroomInks.Graphite, topLeft = Offset(gx, gy), size = Size(gw, gh))
            for (i in 1 until 5) {
                val bx = gx + gw * i / 5f
                drawLine(
                    color = rimLit, start = Offset(bx, gy + 4.dp.toPx()),
                    end = Offset(bx, gy + gh - 4.dp.toPx()), strokeWidth = 3.dp.toPx()
                )
            }
            for (i in 1 until 3) {
                val by2 = gy + gh * i / 3f
                drawLine(
                    color = rimLit, start = Offset(gx + 4.dp.toPx(), by2),
                    end = Offset(gx + gw - 4.dp.toPx(), by2), strokeWidth = 3.dp.toPx()
                )
            }
            drawRect(
                color = pin, topLeft = Offset(gx, gy), size = Size(gw, 2.dp.toPx())
            )
            if (latchSeated) {
                // The latch, seated: one brass bar across the
                // grille's middle — the first touch's visible fact.
                drawRect(
                    color = StrongroomInks.Gold,
                    topLeft = Offset(gx + 6.dp.toPx(), gy + gh / 2f - 3.dp.toPx()),
                    size = Size(gw - 12.dp.toPx(), 6.dp.toPx())
                )
            }
        }
    }
}

// ────────────────────────────────────────────────────────────────
// THE SWING ACT (census C-03) — composed at the doorway place,
// invocable as the morning's first sitting on the standing
// signing. His hand drags the leaf shut against its mass; at
// frame contact the host fires the seal (the bridge's
// activateSeal — this act is its fresh host, the Caller-Orphan
// Register's named duty); the bolts then drive home in their
// order on the ceremony clock, the press seats the door, and the
// face stands printed. A hand that lets go early watches the
// door swing back open on its own weight (DrivenDoor) — nothing
// is sealed by a half swing.
// ────────────────────────────────────────────────────────────────

data class SwingActState(
    /** The host struck the span (activateSeal returned it): the
     *  bolts may drive. Until this stands, contact alone seals
     *  nothing on this surface. */
    val sealStruck: Boolean = false,
    /** The swing failed (no span): the door reopens on its weight. */
    val sealFailed: Boolean = false
)

data class SwingActActions(
    /** Frame contact, at lawful or slam velocity alike (the door
     *  does not punish): the host fires the seal NOW. */
    val onContact: () -> Unit,
    /** The ceremony completed: the sealed face stands. */
    val onCeremonyComplete: () -> Unit
)

@Composable
fun SwingAct(
    state: SwingActState,
    actions: SwingActActions,
    modifier: Modifier = Modifier
) {
    val lightState = LocalStrongroomLightState.current
    // The frame loop below runs for the act's whole life: it must
    // read the LATEST host state and actions, never the values
    // captured at first composition.
    val currentState by androidx.compose.runtime.rememberUpdatedState(state)
    val currentActions by androidx.compose.runtime.rememberUpdatedState(actions)
    val door = remember { HandClosedDoor() }
    val driven = remember { DrivenDoor() }
    var angle by remember { mutableStateOf(door.angleDeg) }
    var mode by remember { mutableStateOf(SwingMode.HAND) }
    var ceremonyMs by remember { mutableIntStateOf(-1) }
    var ceremonyDone by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        var last = withFrameMillis { it }
        var reopenStartMs = -1L
        var reopenFromAngle = 0.0
        var reopenStartOffset = 0
        var ceremonyStart = -1L
        while (true) {
            val now = withFrameMillis { it }
            val dt = ((now - last) / 1000.0).coerceIn(0.0, 0.05)
            last = now
            when (mode) {
                SwingMode.HAND -> {
                    door.step(dt)
                    angle = door.angleDeg
                    if (door.atFrameContact() && door.handTargetDeg <= 2.0) {
                        door.clampAtContact()
                        angle = 0.0
                        mode = SwingMode.CONTACTED
                        currentActions.onContact()
                    }
                }
                SwingMode.REOPENING -> {
                    if (reopenStartMs < 0) {
                        reopenStartMs = now
                        reopenFromAngle = angle
                        reopenStartOffset = (0..DrivenDoorSamples)
                            .minByOrNull {
                                kotlin.math.abs(driven.angleAt(it) - reopenFromAngle)
                            } ?: 0
                    }
                    val elapsed = (now - reopenStartMs).toInt()
                    // The weight-driven opening, read off the driven
                    // body from the pose nearest the released angle.
                    val a = driven.angleAt(reopenStartOffset + elapsed)
                    angle = a
                    door.moveHandTo(a)
                    if (a >= driven.finalDeg - 0.6) {
                        mode = SwingMode.HAND
                        reopenStartMs = -1
                    }
                }
                SwingMode.CONTACTED -> {
                    angle = 0.0
                    if (currentState.sealFailed) {
                        mode = SwingMode.REOPENING
                    } else if (currentState.sealStruck) {
                        if (ceremonyStart < 0) ceremonyStart = now
                        ceremonyMs = (now - ceremonyStart).toInt()
                        if (!ceremonyDone && SwingCeremony.isCeremonyComplete(ceremonyMs)) {
                            ceremonyDone = true
                            currentActions.onCeremonyComplete()
                        }
                    }
                }
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(216.dp)
            .background(StrongroomRoles.Field)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            // The frame and its dark opening, right of the hinge line.
            val frameX = w * 0.30f
            val frameY = h * 0.06f
            val frameW = w * 0.64f
            val frameH = h * 0.88f
            drawRect(
                color = StrongroomRoles.Metal,
                topLeft = Offset(frameX - 8.dp.toPx(), frameY - 8.dp.toPx()),
                size = Size(frameW + 16.dp.toPx(), frameH + 16.dp.toPx())
            )
            drawRect(
                color = StrongroomInks.Graphite,
                topLeft = Offset(frameX, frameY),
                size = Size(frameW, frameH)
            )
            // The leaf, hinged at the frame's right edge: its
            // apparent width is the swing's projection — at rest
            // open (82°) it stands nearly edge-on; shut (0°) it
            // covers the opening whole. The press seats it the
            // last few px into the frame.
            val pressPx = if (ceremonyMs >= 0) {
                (SwingCeremony.pressFraction(ceremonyMs) * 3.0).toFloat().dp.toPx()
            } else 0f
            val apparent = maxOf(cos(Math.toRadians(angle)), 0.12).toFloat()
            val leafW = frameW * apparent
            val leafX = frameX + frameW - leafW + pressPx
            val enamel = StrongroomLight.stateSurface(
                StrongroomInks.Violet, lightState, StrongroomLitSurface.FACE
            )
            val cell = SealedTexture.Species.ENAMEL.cellPx.dp.toPx()
            var cy = 0
            var yy = frameY
            while (yy < frameY + frameH) {
                var cx = 0
                var xx = leafX
                while (xx < leafX + leafW) {
                    drawRect(
                        color = SealedTexture.cellTone(
                            enamel, StrongroomInks.Graphite,
                            SealedTexture.Species.ENAMEL, cx, cy,
                            seed = 41, state = lightState
                        ),
                        topLeft = Offset(xx, yy),
                        size = Size(cell + 1f, cell + 1f)
                    )
                    xx += cell; cx++
                }
                yy += cell; cy++
            }
            val pin = StrongroomLight.sealedPinstripe()
            val inset = 10.dp.toPx()
            drawRect(
                color = pin, topLeft = Offset(leafX + inset, frameY + inset),
                size = Size(leafW - 2 * inset, 2.dp.toPx())
            )
            drawRect(
                color = pin, topLeft = Offset(leafX + inset, frameY + frameH - inset - 2.dp.toPx()),
                size = Size(leafW - 2 * inset, 2.dp.toPx())
            )
            // The great wheel on the leaf — the hand's handle.
            val wheelC = Offset(leafX + leafW * 0.42f, frameY + frameH * 0.52f)
            val wheelR = leafW * 0.30f
            if (wheelR > 8.dp.toPx()) {
                drawCircle(
                    color = StrongroomInks.Graphite, radius = wheelR, center = wheelC,
                    style = Stroke(width = 6.dp.toPx())
                )
                for (deg in listOf(90f, 210f, 330f)) {
                    val a = deg * (Math.PI / 180f).toFloat()
                    drawLine(
                        color = StrongroomInks.Graphite, start = wheelC,
                        end = wheelC + Offset(cos(a) * wheelR * 0.92f, sin(a) * wheelR * 0.92f),
                        strokeWidth = 4.dp.toPx()
                    )
                }
                drawCircle(color = StrongroomInks.Gold, radius = wheelR * 0.20f, center = wheelC)
            }
            // The bolts, driving home in seat order once the seal
            // is struck: each bar's travel is its own body's.
            if (ceremonyMs >= 0) {
                for (i in 0 until 3) {
                    val frac = SwingCeremony.boltTravelFraction(i, ceremonyMs).toFloat()
                    if (frac <= 0f) continue
                    val by = frameY + frameH * (0.24f + i * 0.26f)
                    val len = 30.dp.toPx() * frac
                    drawRect(
                        color = StrongroomInks.Graphite,
                        topLeft = Offset(leafX - len, by - 4.dp.toPx()),
                        size = Size(len, 8.dp.toPx())
                    )
                }
            }
        }
        // The hand's ground: drag the leaf (from anywhere on the
        // band) toward shut — leftward closes, against the mass.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { if (mode == SwingMode.HAND) Unit },
                        onDragEnd = {
                            if (mode == SwingMode.HAND && !door.atFrameContact()) {
                                mode = SwingMode.REOPENING
                            }
                        },
                        onDragCancel = {
                            if (mode == SwingMode.HAND) mode = SwingMode.REOPENING
                        },
                        onDrag = { change, dragAmount ->
                            if (mode == SwingMode.HAND) {
                                change.consume()
                                // ~4 px of hand travel per degree of
                                // door: an 82° swing is a full pull.
                                door.moveHandTo(door.handTargetDeg + dragAmount.x / 4f)
                            }
                        }
                    )
                }
        )
    }
}

private enum class SwingMode { HAND, REOPENING, CONTACTED }

/** DrivenDoor's integrated span, in ms (its own bound). */
private const val DrivenDoorSamples: Int = 6000

// ────────────────────────────────────────────────────────────────
// THE DOOR-WORD SETTER — joins the signing table's setters at
// this slice's composition (the coordinator's ruling at PR #873:
// the setting control joins the signing table's setters at the
// sealed-face slice's composition; the Signing as delivered is
// not reopened). A scribe line on the signing table's paper: his
// word written on the line, committed as written; a writing that
// is not one word is refused by the mechanism (the host's store
// law) and the line keeps the word that stands. No word is
// supplied, suggested, or defaulted here — an empty line is the
// kept place, and it is complete.
// ────────────────────────────────────────────────────────────────

@Composable
fun DoorWordSetter(
    word: String?,
    onSetWord: (String) -> Boolean,
    modifier: Modifier = Modifier
) {
    val lightState = LocalStrongroomLightState.current
    val paper = StrongroomLight.blendEighth(
        StrongroomInks.Gold, StrongroomInks.Graphite, 6
    )
    var draft by remember(word) { mutableStateOf(word ?: "") }
    val focusManager = LocalFocusManager.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(paper, RoundedCornerShape(3.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BasicTextField(
            value = draft,
            onValueChange = { draft = it },
            singleLine = true,
            textStyle = StrongroomType.Scr2.copy(color = StrongroomInks.Graphite),
            cursorBrush = SolidColor(StrongroomInks.Graphite),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {
                    if (!onSetWord(draft)) draft = word ?: ""
                    focusManager.clearFocus()
                }
            ),
            modifier = Modifier
                .weight(1f)
                .semantics { contentDescription = "door-word" },
            decorationBox = { inner ->
                Box(
                    modifier = Modifier
                        .border(
                            1.dp,
                            StrongroomLight.blendEighth(
                                StrongroomInks.Graphite, StrongroomInks.Gold, 3
                            ),
                            RoundedCornerShape(2.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 8.dp)
                ) { inner() }
            }
        )
    }
}
