package com.example.ui.strongroom

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.domain.LiftingPlate
import com.example.ui.theme.PlateBrass
import com.example.ui.theme.PlateBrassDark
import com.example.ui.theme.PlateDarkRoom
import com.example.ui.theme.PlateInkGold
import com.example.ui.theme.RoomHeadingStyle
import com.example.ui.theme.RoomListStyle

/**
 * P6 — THE ROOM SIDE: THE PLATE PILE + THE QUIET DRAWER
 * (BUILD-SR-4, Slice 1).
 *
 * These two surfaces live in the ROOM, on the tray side — and
 * that placement is law, not layout taste (B-16): the plate stack
 * never stands at the signing place among the signing's
 * materials, because the record never argues at the composing
 * hour. Nothing in this file is composed by the signing flow;
 * these surfaces are reached in the room, in the day, away from
 * the signing table.
 *
 * Both surfaces are QUIET by construction:
 *  - The pile is the progress display (A-11 / E-2): plates
 *    stacking, browsable — and it shows no total, no count of
 *    days, no streak, no average, because [LiftingPlate.PlatePile]
 *    carries no operation that could produce one.
 *  - The drawer stores finished tasks (D-8): titles and the days
 *    they were finished, grouped by filing day for browsing.
 *    No score, no total, no grading — the drawer's model has no
 *    field that could hold one, and this surface invents none.
 * The headings say what the thing IS and nothing more; a heading
 * that congratulated or judged would be a tracker wearing the
 * room's coat, and this programme builds no trackers.
 */

/** The room-side surfaces as one named set, with their place
 *  law attached: the pile and the drawer live in the room
 *  (B-16) — this is the value the surfaces stand under, and the
 *  signing flow holds no reference to it. */
object PlateRoomScreens {
    val pilePlace: LiftingPlate.PilePlace = LiftingPlate.PilePlace.ROOM_TRAY
}

@Composable
fun PlatePileScreen(
    pile: LiftingPlate.PlatePile,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PlateDarkRoom)
            .padding(horizontal = 24.dp, vertical = 28.dp)
            .testTag("plate_pile")
    ) {
        Text(text = "The plates", style = RoomHeadingStyle, color = PlateInkGold)
        Spacer(Modifier.height(18.dp))

        // The stack itself: the newest plates' edges, offset as a
        // physical pile stands — the growing stack IS the display.
        Box(Modifier.fillMaxWidth().height(54.dp).testTag("plate_pile_stack")) {
            val shown = pile.platesNewestFirst.take(4)
            shown.forEachIndexed { index, plate ->
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .offset(y = (index * 12).dp, x = (index * 5).dp)
                        .background(if (plate.truth.isFailure) PlateBrassDark else PlateBrass)
                )
            }
        }
        Spacer(Modifier.height(14.dp))

        LazyColumn(Modifier.testTag("plate_pile_list")) {
            items(pile.platesNewestFirst, key = { it.truth.filingDateIso }) { plate ->
                DayPlateView(plate = plate)
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun StrongroomDrawerScreen(
    drawer: LiftingPlate.TaskDrawer,
    zone: java.time.ZoneId = java.time.ZoneId.systemDefault(),
    modifier: Modifier = Modifier
) {
    val byDay = drawer.byFilingDay(zone)
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PlateDarkRoom)
            .padding(horizontal = 24.dp, vertical = 28.dp)
            .testTag("strongroom_drawer")
    ) {
        Text(text = "The drawer", style = RoomHeadingStyle, color = PlateInkGold)
        Spacer(Modifier.height(18.dp))
        LazyColumn(Modifier.testTag("strongroom_drawer_list")) {
            for ((day, tasks) in byDay) {
                item(key = "day-$day") {
                    Text(
                        text = day,
                        style = RoomListStyle,
                        color = PlateInkGold,
                        modifier = Modifier.padding(top = 10.dp, bottom = 4.dp)
                    )
                }
                items(tasks, key = { it.taskId }) { task ->
                    Text(
                        text = task.title,
                        style = RoomListStyle,
                        color = PlateInkGold,
                        modifier = Modifier
                            .padding(start = 14.dp, top = 3.dp, bottom = 3.dp)
                            .testTag("drawer_task")
                    )
                }
            }
        }
    }
}
