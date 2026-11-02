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
 */
@Composable
fun StrongroomShell(
    viewModel: MonolithViewModel,
    doorwayContent: @Composable () -> Unit = { DoorwayBirthWord() },
    sealedFaceContent: @Composable () -> Unit = {},
    antechamberContent: @Composable () -> Unit = {},
    breakContent: @Composable () -> Unit = {},
    liftingContent: @Composable () -> Unit = {}
) {
    val session by viewModel.activeSession.collectAsStateWithLifecycle()
    val finish by viewModel.finishState.collectAsStateWithLifecycle()
    val standingPlace by viewModel.currentPlace.collectAsStateWithLifecycle()

    val place = deriveShellPlace(
        sealed = session?.isActive == true,
        onBreak = session?.isInSafeBreak == true,
        lifting = finish != null && !finish.issued,
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
