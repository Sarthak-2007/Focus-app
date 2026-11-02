package com.example.ui.strongroom

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.domain.LiftingPlate
import com.example.ui.theme.PlateBodyStyle
import com.example.ui.theme.PlateBrass
import com.example.ui.theme.PlateBrassDark
import com.example.ui.theme.PlateDarkRoom
import com.example.ui.theme.PlateInkGold
import com.example.ui.theme.PlateLineWork
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

        // THE TRAY'S KEPT PLACE (A-8; R3 CC-5): the snapped crank
        // seals lie here, in snap order (the pile's order stands
        // in until the week state carries snap order itself —
        // the interim source named in LiftingPlate.PlatePile),
        // visible whenever the tray is open — never on a plate.
        val snappedDays = pile.snappedSealDays()
        if (snappedDays.isNotEmpty()) {
            Row(
                Modifier.testTag("tray_snapped_seals"),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                for (day in snappedDays) {
                    SnappedSealChip(day)
                }
            }
            Spacer(Modifier.height(16.dp))
        }

        LazyColumn(Modifier.testTag("plate_pile_list")) {
            items(pile.platesNewestFirst, key = { it.truth.filingDateIso }) { plate ->
                DayPlateView(plate = plate)
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

/** A snapped crank seal lying in the tray: a small disc split
 *  in two, its halves parted — the spend's permanent testimony,
 *  drawn from the plate's own inks. [day] is carried for the
 *  tray's own record of which day's spend this was. */
@Composable
private fun SnappedSealChip(day: String) {
    Canvas(
        Modifier
            .size(22.dp)
            .testTag("tray_snapped_seal_$day")
    ) {
        drawArc(
            color = PlateInkGold,
            startAngle = 100f, sweepAngle = 160f,
            useCenter = false,
            style = Stroke(width = 3f)
        )
        drawArc(
            color = PlateInkGold,
            startAngle = 285f, sweepAngle = 140f,
            useCenter = false,
            style = Stroke(width = 3f)
        )
        drawCircle(color = PlateLineWork, radius = 1.6f, center = center)
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
                    // The slip's finished-date in the telemetry
                    // hand (Anonymous Pro 400 — DESIGNER-SR-5 §9);
                    // the task's own words below stay his, verbatim.
                    Text(
                        text = day,
                        style = PlateBodyStyle,
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
