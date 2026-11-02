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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.domain.LiftingPlate
import com.example.ui.strongroom.grade.PlateGradeRender
import com.example.ui.strongroom.grade.asComposeColor
import com.example.ui.strongroom.layout.RoomGrid
import com.example.ui.strongroom.layout.TrayDrawerCompositionLaw
import com.example.ui.strongroom.layout.TrayDrawerLayout
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

/**
 * The pile screen, composed through Stage 8's tray/drawer
 * composition at Stage 10 composition (BUILD-SR-16):
 * [TrayDrawerLayout] — the plate pile first (its growth is the
 * progress display, E-2), the browsing list below it; no header
 * slot, no summary slot, no count exists in the composition for
 * this surface to render one with (DrawerComposition, L2-c).
 * The stack itself is struck at DESIGNER-SR-4 §5's grade, from
 * BUILD-SR-10's [PlateGradeRender]: each plate contributes its
 * 3 dp thickness band, at a hand-stacked offset that is a PURE
 * FUNCTION of its filing date (same pile, same rendering, every
 * visit), and the drawn edges are capped at 24 — a picture cap,
 * never a count: beyond it the drawn height stays and the list
 * carries the season.
 */
@Composable
fun PlatePileScreen(
    pile: LiftingPlate.PlatePile,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PlateDarkRoom)
            .testTag("plate_pile")
    ) {
        // Deck §7.8 (Stage 9, BUILD-SR-17 — copy governs): the
        // pile carries no heading, total, or count line. The
        // heading's inset survives as the layout's own spacing
        // (BUILD-SR-16's frame; BUILD-SR-23, step 3).
        Spacer(
            Modifier.height(
                RoomGrid.dp(RoomGrid.FIELD_INSET_STEPS).dp +
                    RoomHeadingStyle.lineHeight.value.dp
            )
        )
        TrayDrawerLayout(
            modifier = Modifier.weight(1f),
            platePile = {
                // The stack: plates in struck order, oldest at
                // the bottom, the newest on top — drawn edges
                // capped by the composition law, each edge at its
                // date-struck offset and rotation.
                val drawn = TrayDrawerCompositionLaw.drawnEdges(pile.platesNewestFirst.size)
                val edgePlates = pile.platesNewestFirst.take(drawn).reversed()
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height((drawn * PlateGradeRender.PLATE_THICKNESS_DP + 8).dp)
                        .testTag("plate_pile_stack")
                ) {
                    edgePlates.forEachIndexed { index, plate ->
                        val epochDay = runCatching {
                            java.time.LocalDate.parse(plate.truth.filingDateIso).toEpochDay()
                        }.getOrDefault(0L)
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .height(PlateGradeRender.PLATE_THICKNESS_DP.dp + 1.dp)
                                .offset(
                                    x = PlateGradeRender.stackOffsetX(epochDay).dp,
                                    y = (index * PlateGradeRender.PLATE_THICKNESS_DP).dp +
                                        PlateGradeRender.stackOffsetY(epochDay).dp
                                )
                                .rotate(PlateGradeRender.stackRotationTenths(epochDay) / 10f)
                                .background(
                                    if (plate.truth.isFailure) PlateBrassDark else PlateBrass
                                )
                        )
                    }
                }
                Spacer(Modifier.height(14.dp))

                // THE TRAY'S SEAL CORNER (A-8; R3 CC-5): the
                // snapped crank seals lie here, in snap order
                // (the pile's order stands in until the week state
                // carries snap order itself — the interim source
                // named in LiftingPlate.PlatePile), visible
                // whenever the tray is open — never on a plate.
                // ONE PIECE each (R5-1, upheld by R7-3 against
                // DESIGNER-SR-4 §5's two-parted-halves form —
                // re-argued in BUILD-SR-16's record): the seal is
                // a single lead crimp; it parts and drops as one
                // piece, kept whole. When no seal has snapped,
                // the corner stands empty WITH ITS PLACE KEPT
                // (§5's kept-place grammar, which survives the
                // re-argument): a shallow round recess, the shape
                // of the thing it would hold.
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
                } else {
                    Canvas(
                        Modifier
                            .size(22.dp)
                            .testTag("tray_seal_kept_place")
                    ) {
                        drawCircle(
                            color = PlateGradeRender.BRASS_RECESS.asComposeColor(),
                            radius = size.minDimension / 2f - 1.5f,
                            center = center,
                            style = Stroke(width = 2f)
                        )
                    }
                }
            },
            browsingList = {
                LazyColumn(Modifier.testTag("plate_pile_list")) {
                    items(pile.platesNewestFirst, key = { it.truth.filingDateIso }) { plate ->
                        DayPlateView(plate = plate)
                        Spacer(Modifier.height(16.dp))
                    }
                }
            }
        )
    }
}

/** A snapped crank seal lying in the tray: ONE piece (R5-1 —
 *  the concept's seal is a single lead crimp; when the crank
 *  turns it parts and drops as one piece, kept whole in the
 *  tray). Drawn as one disc with the parting crack across it —
 *  snapped, never two pieces. [day] is carried for the tray's
 *  own record of which day's spend this was. */
@Composable
private fun SnappedSealChip(day: String) {
    Canvas(
        Modifier
            .size(22.dp)
            .testTag("tray_snapped_seal_$day")
    ) {
        val r = size.minDimension / 2f - 1.5f
        drawCircle(
            color = PlateInkGold,
            radius = r,
            center = center,
            style = Stroke(width = 3f)
        )
        drawLine(
            color = PlateLineWork,
            start = androidx.compose.ui.geometry.Offset(center.x - r * 0.7f, center.y - r * 0.7f),
            end = androidx.compose.ui.geometry.Offset(center.x + r * 0.6f, center.y + r * 0.75f),
            strokeWidth = 1.8f
        )
        drawCircle(color = PlateLineWork, radius = 1.6f, center = center)
    }
}

/**
 * The quiet drawer (D-8). Stage 8 composition note (BUILD-SR-16):
 * BUILD-SR-7's DrawerGrid primitive is NOT adopted here, by name —
 * the grid carries no group-header slot, and this drawer's filed
 * browsing form is the day-grouped list (finished tasks under
 * their filing day, his words verbatim); flattening the groups
 * into uniform cells would delete a filed behaviour to consume a
 * primitive, which is the tail wagging the room. What IS consumed:
 * the drawer's spacing stands on RoomGrid's bands and inset, and
 * its contents are exactly DrawerContent's two lawful kinds —
 * finished tasks here, plates on the pile screen — with no score
 * slot anywhere in either.
 */
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
            .padding(
                horizontal = RoomGrid.dp(RoomGrid.FIELD_INSET_STEPS).dp,
                vertical = RoomGrid.dp(RoomGrid.FIELD_INSET_STEPS).dp
            )
            .testTag("strongroom_drawer")
    ) {
        // Deck §11.3 (Stage 9, BUILD-SR-17 — copy governs): the drawer surface carries ∅ room words.
        Spacer(Modifier.height(RoomGrid.dp(RoomGrid.LIST_BAND_STEPS).dp))
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
