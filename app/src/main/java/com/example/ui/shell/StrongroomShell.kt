package com.example.ui.shell

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.session.MonolithViewModel
import com.example.session.StrongroomPlace
import com.example.ui.foundation.APP_NAME
import com.example.ui.foundation.StrongroomRoles
import com.example.ui.foundation.StrongroomTheme

/**
 * THE SHELL — the Strongroom's own order as the app's structure
 * (BUILD-SR-34; route-book Law R-0: the Strongroom is the
 * structure, not a destination inside one).
 *
 * The host composes exactly one place at a time, derived from the
 * kept engine's session state — never from a navigation action:
 *
 * | engine state                                  | host        |
 * |-----------------------------------------------|-------------|
 * | a finish stands unissued (the lifting)        | LIFTING     |
 * | sealed, the break's span running              | BREAK       |
 * | sealed, his second touch stands               | ANTECHAMBER |
 * | sealed                                        | SEALED_FACE |
 * | no seal                                       | DOORWAY     |
 *
 * There is no navigation furniture: no rail, no tabs, no back
 * stack (census GAP-R01 — navigation glue DOES-NOT-EXIST; the
 * day's acts are the transitions). While any sealed place stands,
 * Back is consumed and does nothing: the seal is left by the
 * time-lock's firing, never by a route (ledger D-7 — while
 * sealed, this launcher is everything).
 *
 * The hosts are SKELETONS — an empty room in foundation dress per
 * place, each with a content slot. The surfaces themselves
 * (Doorway/Signing, the sealed face, the antechamber, the break,
 * the lifting/plate) are their own slices seating behind this
 * one; nothing finished is authored here.
 *
 * THE BREAK'S STANDING, FROM THE STRONGROOM DAY (PLACES cure):
 * the BREAK place stands on THREE standings, any one sufficient —
 * the kept session's standing break (the shell's original fact),
 * [strongroomBreakStanding] (the Strongroom span's own firing
 * running — the time-lock's priced break, derived by the host
 * from the sealed day's span, never from the legacy session), and
 * [crankMinuteStanding] (the governor's crank minute running —
 * the week's 60 seconds open a break inside the block). Before
 * this cure the derivation read the legacy session alone, so for
 * a Strongroom-sealed day no break room ever stood and nothing
 * unblocked at the firing.
 *
 * THE SEALED STANDING, UNIFIED (BUILD-SR-38 merge composition,
 * consuming BUILD-SR-37's named remainder): the seal's standing
 * is derived ONCE — from the sealed day's standing record, as the
 * sealed face's immutable snapshot (the host's loadSealFacts) —
 * and this shell consumes THAT fact through [sealedStanding]
 * whenever the host supplies it, instead of deriving a parallel
 * standing from the kept session. Both face hosts (this shell's
 * SEALED_FACE host and the doorway place's glue) render the one
 * snapshot, and the place that stands them is derived from the
 * same fact: there is no second derivation of the seal anywhere
 * in the composition. [sealedStanding] = null keeps the shell's
 * standalone default (the kept session's standing) for callers
 * that compose the shell without the Strongroom seal's facts.
 * The lifting's inputs remain the finish facts. The break's
 * inputs are the kept session's standing break PLUS the
 * Strongroom day's own standings (the firing + the crank
 * minute), supplied by the host — the PLACES cure in the head.
 */
@Composable
fun StrongroomShell(
    viewModel: MonolithViewModel,
    sealedStanding: Boolean? = null,
    strongroomBreakStanding: Boolean = false,
    crankMinuteStanding: Boolean = false,
    doorwayContent: @Composable () -> Unit = { DoorwayBirthWord() },
    sealedFaceContent: @Composable () -> Unit = {},
    antechamberContent: @Composable () -> Unit = {},
    breakContent: @Composable () -> Unit = {},
    liftingContent: @Composable () -> Unit = {}
) {
    val session by viewModel.activeSession.collectAsStateWithLifecycle()
    val finish by viewModel.finishState.collectAsStateWithLifecycle()
    val standingPlace by viewModel.currentPlace.collectAsStateWithLifecycle()

    val finishNow = finish
    val place = deriveShellPlace(
        sealed = sealedStanding ?: (session?.isActive == true),
        onBreak = session?.isInSafeBreak == true ||
            strongroomBreakStanding || crankMinuteStanding,
        lifting = finishNow != null && !finishNow.issued,
        atAntechamber = standingPlace == StrongroomPlace.ANTECHAMBER
    )

    // The day moves by its acts, not by Back: inside the seal
    // there is nowhere Back could lawfully go.
    BackHandler(enabled = place != StrongroomPlace.DOORWAY) {
        // The door holds; Back is not an exit.
    }

    StrongroomTheme {
        when (place) {
            StrongroomPlace.DOORWAY -> PlaceRoom { doorwayContent() }
            StrongroomPlace.SEALED_FACE -> PlaceRoom { sealedFaceContent() }
            StrongroomPlace.ANTECHAMBER -> PlaceRoom { antechamberContent() }
            StrongroomPlace.BREAK -> PlaceRoom { breakContent() }
            StrongroomPlace.LIFTING -> PlaceRoom { liftingContent() }
        }
    }
}

/**
 * The derivation, pure so the end phase can pin it whole: the
 * lifting outranks everything (a finish standing unissued is the
 * day's end being lived), the break's span outranks the room's
 * steps, the antechamber stands only inside a seal, and the
 * doorway is the ground state of an unsealed day.
 */
internal fun deriveShellPlace(
    sealed: Boolean,
    onBreak: Boolean,
    lifting: Boolean,
    atAntechamber: Boolean
): StrongroomPlace = when {
    lifting -> StrongroomPlace.LIFTING
    onBreak && sealed -> StrongroomPlace.BREAK
    atAntechamber && sealed -> StrongroomPlace.ANTECHAMBER
    sealed -> StrongroomPlace.SEALED_FACE
    else -> StrongroomPlace.DOORWAY
}

/** One empty room: the field, and whatever its slice has put in it. */
@Composable
private fun PlaceRoom(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(StrongroomRoles.Field)
    ) {
        content()
    }
}

/** The doorway's birth word — the one word the minimum host
 *  carried, kept until the Doorway slice authors the room. */
@Composable
private fun DoorwayBirthWord() {
    Text(
        text = APP_NAME,
        color = StrongroomRoles.OnGround,
        style = MaterialTheme.typography.titleLarge
    )
}
