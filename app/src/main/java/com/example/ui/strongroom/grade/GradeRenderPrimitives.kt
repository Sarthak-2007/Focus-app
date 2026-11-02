package com.example.ui.strongroom.grade

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

/**
 * STAGE 10 PRE-BUILD — GRADE RENDER PRIMITIVES (BUILD-SR-10).
 * Compose Canvas renderers consuming the pure law in
 * [GradePipeline] / [PlateGradeRender] — they strike no colour and
 * invent no figure of their own: every tone arrives from the ladder,
 * every diameter from the figures. The AGSL path ([RelightShader])
 * replaces the tile renderer's execution at Stage 10 composition
 * where the device proves it; the law rendered is identical.
 */

private fun GradeColour.toComposeColor(): Color = Color(toArgb())

/**
 * One punched hole at grade (DESIGNER-SR-4 spec §2): the raised lip
 * toward the room's RELIGHT direction (upper left) · the wall · the
 * through-dark · the far rim in day presentation. Diameter from the
 * figures alone; depth identical for every kind.
 */
@Composable
fun PunchedHoleGrade(
    kind: PlateGradeRender.HoleKind,
    inDark: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val d = PlateGradeRender.punchDiameterDp(kind)
    Canvas(modifier.size(d.dp)) {
        val r = size.minDimension / 2f
        drawCircle(
            color = PlateGradeRender.BRASS_RAISED.toComposeColor(),
            radius = r,
            center = Offset(
                center.x - r * PlateGradeRender.LIP_OFFSET_FRACTION,
                center.y - r * PlateGradeRender.LIP_OFFSET_FRACTION,
            ),
        )
        drawCircle(
            color = PlateGradeRender.BRASS_RECESS.toComposeColor(),
            radius = r * PlateGradeRender.WALL_FRACTION,
            center = Offset(center.x + r * 0.05f, center.y + r * 0.05f),
        )
        drawCircle(
            color = GradeColour.of(GradeInk.GRAPHITE).toComposeColor(),
            radius = r * PlateGradeRender.THROUGH_DARK_FRACTION,
            center = Offset(center.x + r * 0.05f, center.y + r * 0.05f),
        )
        if (!inDark) {
            drawArc(
                color = PlateGradeRender.BRASS_RAISED.toComposeColor(),
                startAngle = 35f,
                sweepAngle = 110f,
                useCenter = false,
                style = Stroke(width = r * PlateGradeRender.FAR_RIM_FRACTION),
            )
        }
    }
}

/**
 * A field struck by the reference renderer: [base] tiles of unlit ink
 * are graded through [GradeFieldRenderer] under [state], and drawn
 * tile by tile. This is the pipeline's in-app working form — the
 * surface it renders reads as a surface (relief from its own
 * luminance), never as a flat fill with a picture's lighting.
 */
@Composable
fun RelightFieldCanvas(
    base: Array<Array<GradeColour>>,
    state: GradeState,
    surface: GradeSurface,
    modifier: Modifier = Modifier,
    heightfield: FaceHeightfield? = null,
) {
    val graded = GradeFieldRenderer.render(base, state, surface, heightfield)
    Canvas(modifier) {
        if (graded.isEmpty() || graded[0].isEmpty()) return@Canvas
        val tileW = size.width / graded[0].size
        val tileH = size.height / graded.size
        graded.forEachIndexed { y, row ->
            row.forEachIndexed { x, colour ->
                drawRect(
                    color = colour.toComposeColor(),
                    topLeft = Offset(x * tileW, y * tileH),
                    size = androidx.compose.ui.geometry.Size(tileW + 1f, tileH + 1f),
                )
            }
        }
    }
}
