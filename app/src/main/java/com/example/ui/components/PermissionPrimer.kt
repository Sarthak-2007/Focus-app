package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PermissionStepLogic
import com.example.data.permissionExplainer
import com.example.service.WatchItemId
import com.example.ui.theme.AnonymousProFamily
import com.example.ui.theme.AtkinsonHyperlegibleFamily
import com.example.ui.theme.MonolithCurves

/**
 * Issue #324, finding 4 (build half, relabelled on the record):
 * the Shield Permission Primer, RE-CREATED.
 *
 * The original modal no longer exists — Slice D deleted it
 * because its "COMMIT & ARM ANYWAY" path (register LR2-S1-18)
 * let a session arm over missing grants. This re-creation keeps
 * the primer's honest half and none of the bypass:
 *
 *  - ONE step at a time, in the board's cheapest-first order
 *    ([WatchItemId.PERMISSIONS]), each with its own explainer
 *    ([permissionExplainer]) printed BEFORE the ask — the #58
 *    design half's shape: explained per-permission, resumable.
 *  - The only action on a step is FIX, routed by the host to
 *    that permission's own Settings surface (MainActivity
 *    .openFixPane, which also arms the PermissionReturnService
 *    return trip and records the step in PermissionStepStore).
 *  - There is NO arm, commit, continue-anyway or skip control
 *    anywhere in this composable — arming lives where it lives
 *    today: the warrant reads the grants itself and refuses in
 *    the open. LATER dismisses; the persisted step state is what
 *    a later reopen resumes from (PermissionStepLogic).
 *  - The step shown is DERIVED, never stored in the UI: the host
 *    computes it from the live grants + the persisted in-flight
 *    step, so a granted step never re-shows, a revoked grant
 *    reopens exactly its own step, and a completed flow never
 *    renders at all (the host passes currentStep = null).
 */
@Composable
fun ShieldPermissionPrimerModal(
    visible: Boolean,
    currentStep: WatchItemId?,
    grants: Map<WatchItemId, Boolean>,
    onFixClick: (WatchItemId) -> Unit,
    onDismiss: () -> Unit
) {
    val step = currentStep
    AnimatedVisibility(
        visible = visible && step != null,
        enter = fadeIn(MonolithCurves.specC()),
        exit = fadeOut(MonolithCurves.specA())
    ) {
        if (step == null) return@AnimatedVisibility
        val scheme = MaterialTheme.colorScheme
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(scheme.scrim.copy(alpha = 0.65f))
                .clickable(onClick = onDismiss)
                .padding(12.dp)
                .testTag("permission_primer"),
            contentAlignment = Alignment.BottomCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(scheme.surfaceBright)
                    .border(1.dp, scheme.outlineVariant, RoundedCornerShape(24.dp))
                    .clickable(enabled = false) {}
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "THE SHIELD'S GRANTS",
                    fontFamily = AtkinsonHyperlegibleFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = scheme.onBackground
                )
                Text(
                    text = "STEP ${PermissionStepLogic.stepNumber(step)} OF ${PermissionStepLogic.stepCount} — ${step.boardName}",
                    fontFamily = AnonymousProFamily,
                    fontSize = 12.sp,
                    color = scheme.primaryContainer
                )
                Text(
                    text = permissionExplainer(step),
                    fontFamily = AtkinsonHyperlegibleFamily,
                    fontSize = 14.sp,
                    color = scheme.onSurface
                )
                // The board so far: every step, granted or standing.
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    WatchItemId.PERMISSIONS.forEach { item ->
                        val granted = grants[item] == true
                        Text(
                            text = "${if (granted) "✓" else "·"} ${item.boardName}${if (item == step) " — THIS STEP" else ""}",
                            fontFamily = AnonymousProFamily,
                            fontSize = 12.sp,
                            color = if (granted) scheme.onSurfaceVariant else scheme.onSurface
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .border(1.dp, scheme.primary, RoundedCornerShape(8.dp))
                            .clickable { onFixClick(step) }
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                            .testTag("permission_primer_fix")
                    ) {
                        Text(
                            text = "FIX — OPEN SETTINGS",
                            fontFamily = AnonymousProFamily,
                            fontSize = 12.sp,
                            color = scheme.primary
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clickable(onClick = onDismiss)
                            .padding(horizontal = 8.dp, vertical = 10.dp)
                            .testTag("permission_primer_later")
                    ) {
                        Text(
                            text = "LATER",
                            fontFamily = AnonymousProFamily,
                            fontSize = 12.sp,
                            color = scheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
