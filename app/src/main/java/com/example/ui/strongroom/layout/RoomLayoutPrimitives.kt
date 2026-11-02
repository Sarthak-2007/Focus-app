package com.example.ui.strongroom.layout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * SLICE 2 — LAYOUT (BUILD-SR-7). Compose slot primitives carrying
 * [RoomLayout]'s spatial law. Each primitive is slots-only: it decides
 * where things stand and in what order, and renders NO content, colour,
 * type or wording of its own — surfaces (BUILD-SR-2's lane) fill the slots
 * with P0 tokens and Stage 5 steps. A surface composed through these
 * primitives cannot mis-order the antechamber, cannot seat the tray among
 * the signing's materials, and cannot give the drawer a score — the slots
 * for those errors do not exist.
 */

/**
 * The Doorway (B-17): exactly four elements, in the ruled order — the
 * composition at the open door · one plain line · the held waiting · the
 * striking. The entry/glance form every arrival meets.
 */
@Composable
fun DoorwayLayout(
    modifier: Modifier = Modifier,
    compositionAtOpenDoor: @Composable () -> Unit,
    onePlainLine: @Composable () -> Unit,
    heldWaiting: @Composable () -> Unit,
    striking: @Composable () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(RoomGrid.dp(RoomGrid.FIELD_INSET_STEPS).dp),
        verticalArrangement = Arrangement.Top,
    ) {
        compositionAtOpenDoor()
        Spacer(Modifier.height(RoomGrid.dp(RoomGrid.LIST_BAND_STEPS).dp))
        onePlainLine()
        Spacer(Modifier.height(RoomGrid.dp(RoomGrid.LIST_BAND_STEPS).dp))
        heldWaiting()
        Spacer(Modifier.height(RoomGrid.dp(RoomGrid.SECOND_STEP_BAND_STEPS).dp))
        striking()
    }
}

/**
 * The signing place at the open door. Holds the composition and the
 * signing's controls — and nothing from the tray side: there is no slot
 * here a plate, a pile or a record could be passed into (B-16). The
 * signing's order is the composition first (it stands composed before he
 * arrives; the signing amends, never creates — B-2), then his acts.
 */
@Composable
fun SigningPlaceLayout(
    modifier: Modifier = Modifier,
    composition: @Composable () -> Unit,
    signingControls: @Composable () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(RoomGrid.dp(RoomGrid.FIELD_INSET_STEPS).dp),
        verticalArrangement = Arrangement.Top,
    ) {
        composition()
        Spacer(Modifier.height(RoomGrid.dp(RoomGrid.SECOND_STEP_BAND_STEPS).dp))
        signingControls()
    }
}

/**
 * The face's field: one field, composed once, carrying the four printings
 * in Stage 5 §3.3's hierarchy order down the field — hour first (the first
 * fact), door-word slot, sentence or its kept place, held-mark slot last
 * and quietest. The face never alters while the seal stands; this
 * primitive is a standing field, not a feed: it has no slot for anything
 * live, counted, or scored.
 */
@Composable
fun FaceFieldLayout(
    modifier: Modifier = Modifier,
    liftingHour: @Composable () -> Unit,
    doorWordSlot: @Composable () -> Unit,
    sentenceOrKeptPlace: @Composable () -> Unit,
    heldMarkSlot: @Composable () -> Unit,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        liftingHour()
        Spacer(Modifier.height(RoomGrid.dp(RoomGrid.FACE_BAND_STEPS).dp))
        doorWordSlot()
        Spacer(Modifier.height(RoomGrid.dp(RoomGrid.FACE_BAND_STEPS).dp))
        sentenceOrKeptPlace()
        Spacer(Modifier.height(RoomGrid.dp(RoomGrid.FACE_BAND_STEPS).dp))
        heldMarkSlot()
    }
}

/**
 * The antechamber's list (B-9), its order enforced by the slot structure:
 * calling out first (the head band — the surface renders it biggest, per
 * Stage 5 §3.4.2, people outrank apps) · the allowed things exactly as
 * signed · the held pile behind a second step of its own — set apart by a
 * full extra band and an inset, a deliberate second region, not the next
 * row. There is NO slot for spent things: gone is gone.
 */
@Composable
fun AntechamberLayout(
    modifier: Modifier = Modifier,
    callingOut: @Composable () -> Unit,
    allowedAsSigned: @Composable () -> Unit,
    heldPileSecondStep: @Composable () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(RoomGrid.dp(RoomGrid.FIELD_INSET_STEPS).dp),
        verticalArrangement = Arrangement.Top,
    ) {
        callingOut()
        Spacer(Modifier.height(RoomGrid.dp(RoomGrid.LIST_BAND_STEPS).dp))
        allowedAsSigned()
        Spacer(Modifier.height(RoomGrid.dp(RoomGrid.SECOND_STEP_BAND_STEPS).dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = RoomGrid.dp(RoomGrid.LIST_BAND_STEPS).dp),
        ) {
            heldPileSecondStep()
        }
    }
}

/**
 * The tray side (B-16 / E-2): the room's side rail, where yesterday's and
 * the season's plates rest and the snapped governor's seal is kept —
 * browsed here, away from the signing place, which this primitive can
 * never be composed inside (it is a sibling zone, a fixed-width rail,
 * not a section of any signing surface).
 */
@Composable
fun TraySideLayout(
    modifier: Modifier = Modifier,
    dayPlates: @Composable () -> Unit,
    snappedGovernorSeal: @Composable () -> Unit,
) {
    Column(
        modifier = modifier
            .width(RoomGrid.dp(RoomGrid.TRAY_RAIL_STEPS).dp)
            .padding(RoomGrid.dp(RoomGrid.LIST_BAND_STEPS).dp),
        verticalArrangement = Arrangement.Top,
    ) {
        dayPlates()
        Spacer(Modifier.height(RoomGrid.dp(RoomGrid.LIST_BAND_STEPS).dp))
        snappedGovernorSeal()
    }
}

/**
 * A room with its tray: the main field and the tray side standing as the
 * two zones they are — side by side, never merged. Signing surfaces use
 * the main field; the tray rides beside the room, not inside the signing.
 */
@Composable
fun RoomWithTrayLayout(
    modifier: Modifier = Modifier,
    mainField: @Composable () -> Unit,
    traySide: @Composable () -> Unit,
) {
    Row(modifier = modifier.fillMaxSize()) {
        Box(modifier = Modifier.weight(1f).fillMaxSize()) { mainField() }
        traySide()
    }
}

/**
 * The drawer's quiet grid (D-8): finished tasks stored, plates browsed —
 * a uniform, quiet grid on the one grid's spacing. No header slot, no
 * summary slot, no score slot: the drawer is looked back through rarely,
 * and it grades nothing.
 */
@Composable
fun DrawerGrid(
    modifier: Modifier = Modifier,
    itemCount: Int,
    item: @Composable (index: Int) -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = RoomGrid.dp(RoomGrid.DRAWER_CELL_MIN_STEPS).dp),
        modifier = modifier
            .fillMaxSize()
            .padding(RoomGrid.dp(RoomGrid.FIELD_INSET_STEPS).dp),
        horizontalArrangement = Arrangement.spacedBy(RoomGrid.dp(RoomGrid.LIST_BAND_STEPS).dp),
        verticalArrangement = Arrangement.spacedBy(RoomGrid.dp(RoomGrid.LIST_BAND_STEPS).dp),
    ) {
        items(itemCount) { index -> item(index) }
    }
}
