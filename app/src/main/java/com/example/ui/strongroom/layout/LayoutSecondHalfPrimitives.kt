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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * SLICE 2 — LAYOUT, SECOND HALF (BUILD-SR-10). Compose slot primitives
 * carrying [LayoutSecondHalf]'s composition law. Slots only, exactly
 * as BUILD-SR-7's primitives: no content, colour, type or wording is
 * rendered here — surfaces fill the slots. A surface composed through
 * these primitives cannot seat the drum inside the B-9 list, cannot
 * give the held chamber a words shelf, and cannot head the drawer
 * with a summary — those slots do not exist.
 */

/**
 * The antechamber as a room (Seam S-B, ruled (b)): BUILD-SR-7's
 * [AntechamberLayout] list column and the drum housing stand as
 * SIBLINGS under one room root. The housing is a zone of the room,
 * not a row of the list — the column's slot structure is untouched,
 * and no parameter of this primitive can pass the housing into it.
 */
@Composable
fun AntechamberRoomLayout(
    modifier: Modifier = Modifier,
    listColumn: @Composable () -> Unit,
    drumHousing: @Composable () -> Unit,
) {
    Column(modifier = modifier.fillMaxSize()) {
        Box(modifier = Modifier.weight(1f).fillMaxWidth()) { listColumn() }
        Spacer(Modifier.height(RoomGrid.dp(RoomGrid.LIST_BAND_STEPS).dp))
        drumHousing()
    }
}

/**
 * The held pile's chamber, behind its own second grille: the shelves
 * in [HeldPileShelf]'s ruled order — waiting · the digits line · the
 * taking — set apart by the second-step band, a further place, not
 * the next screen of the antechamber.
 */
@Composable
fun HeldPileLayout(
    modifier: Modifier = Modifier,
    waiting: @Composable () -> Unit,
    digitsLine: @Composable () -> Unit,
    taking: @Composable () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(RoomGrid.dp(RoomGrid.FIELD_INSET_STEPS).dp),
        verticalArrangement = Arrangement.Top,
    ) {
        waiting()
        Spacer(Modifier.height(RoomGrid.dp(RoomGrid.SECOND_STEP_BAND_STEPS).dp))
        digitsLine()
        Spacer(Modifier.height(RoomGrid.dp(RoomGrid.LIST_BAND_STEPS).dp))
        taking()
    }
}

/**
 * The drawer's composition (L2-c): the plate pile first — its growth
 * is the progress display (E-2) — and the browsing list below it.
 * There is no header slot and no summary slot: the drawer is looked
 * back through rarely, and it grades nothing (D-8).
 */
@Composable
fun TrayDrawerLayout(
    modifier: Modifier = Modifier,
    platePile: @Composable () -> Unit,
    browsingList: @Composable () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(RoomGrid.dp(RoomGrid.FIELD_INSET_STEPS).dp),
        verticalArrangement = Arrangement.Top,
    ) {
        platePile()
        Spacer(Modifier.height(RoomGrid.dp(RoomGrid.SECOND_STEP_BAND_STEPS).dp))
        browsingList()
    }
}

/**
 * The room with its tray at composition depth (B-16): the room field
 * and the tray side as siblings of one root — the tray rides beside
 * the room, never inside the signing place composed within it.
 */
@Composable
fun RoomTraySiblingsLayout(
    modifier: Modifier = Modifier,
    roomField: @Composable () -> Unit,
    traySide: @Composable () -> Unit,
) {
    Row(modifier = modifier.fillMaxSize()) {
        Box(modifier = Modifier.weight(1f).fillMaxSize()) { roomField() }
        traySide()
    }
}
