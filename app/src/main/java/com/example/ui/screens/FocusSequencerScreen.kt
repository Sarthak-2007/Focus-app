package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.FocusTaskEntity
import com.example.data.ClockText
import androidx.compose.ui.platform.LocalContext
import com.example.service.TactileSoundEngine
import com.example.ui.components.BlockitActionChip
import com.example.ui.components.inkControlFill
import com.example.ui.components.onInkControlFill
import com.example.ui.components.FullBleedSectionHeader
import com.example.ui.components.rememberMixedTypographyText
import com.example.ui.theme.AnonymousProFamily
import com.example.ui.theme.AnonymousProFamily
import com.example.ui.theme.AtkinsonHyperlegibleFamily
import com.example.ui.theme.MonolithCurves
import com.example.ui.theme.SequencerEdgeDay1
import com.example.ui.theme.SequencerEdgeDay2
import com.example.ui.theme.SequencerEdgeDay3
import com.example.ui.theme.SequencerEdgeNight1
import com.example.ui.theme.SequencerEdgeNight2
import com.example.ui.theme.SequencerEdgeNight3
import com.example.ui.theme.SequencerHairlineNight
import com.example.ui.theme.TokenColorSecondaryContainer
import com.example.ui.views.TaskHorizonBarView
import kotlinx.coroutines.delay

/**
 * TAB 2: HORIZON TASK QUEUE (DEVELOPER-GUIDE.pdf §1.1, §2, §3)
 * - Full-bleed stacked sections with 12dp universal spacing.
 * - Proportional `TaskHorizonBarView` at the top where only active task #1 uses THE Accent (#F55314).
 * - Single-line task entry + micro-step auto-splitter (`+ SPLIT 3`) + Undo banner.
 */
@Composable
fun FocusSequencerScreen(
    activeTask: FocusTaskEntity?,
    queuedTasks: List<FocusTaskEntity>,
    completedTasks: List<FocusTaskEntity>,
    deferredTasks: List<FocusTaskEntity>,
    deletedTaskForUndo: FocusTaskEntity?,
    totalQueueMinutes: Int,
    isSessionActive: Boolean,
    soundEngine: TactileSoundEngine? = null,
    isDarkTheme: Boolean = false,
    onAddTask: (String, Int, String, String, Boolean) -> Unit,
    onEditTask: (Int, String, Int, String, String) -> Unit,
    onUpdateTaskDuration: (Int, Int) -> Unit,
    onCueTaskToFocus: (Int) -> Unit,
    onCompleteTask: (Int) -> Unit,
    onPromoteTask: (Int) -> Unit,
    onMoveTaskOrder: (Int, Boolean) -> Unit,
    onDeferTask: (Int) -> Unit,
    onDeleteTask: (Int) -> Unit,
    onUndoDeleteTask: () -> Unit,
    onDismissUndoBanner: () -> Unit,
    onReopenTask: (Int) -> Unit,
    onToggleSubStep: (Int, Int) -> Unit,
    onAddSubStep: (Int, String) -> Unit,
    onAutoSplitTask: (Int) -> Unit,
    onNavigateToDeck: () -> Unit
) {
    val scheme = MaterialTheme.colorScheme
    val effectiveNight = isDarkTheme || isSessionActive
    var newTaskTitle by remember { mutableStateOf("") }

    // Truth Car 2 (G-01): the tear's ghost. The booking now lands
    // AT THE TAP (see onTearLeaf); what remains on the pad for
    // the tear's 260ms is this snapshot of the already-booked
    // leaf, rendered non-interactive at its old slot while the
    // motion runs over the booked fact — the settle is pure
    // motion again, and neither process death in the window nor
    // navigation away can un-book the completion.
    var tornTask by remember { mutableStateOf<FocusTaskEntity?>(null) }
    var tornIndex by remember { mutableIntStateOf(0) }
    LaunchedEffect(tornTask) {
        if (tornTask != null) {
            delay(MonolithCurves.DURATION_260.toLong())
            tornTask = null
        }
    }

    val orderedQueue = remember(activeTask, queuedTasks) {
        buildList {
            if (activeTask != null) add(activeTask)
            addAll(queuedTasks.filter { it.id != activeTask?.id })
        }
    }
    val displayQueue = remember(orderedQueue, tornTask, tornIndex) {
        val base = orderedQueue.map { LeafSlot(it, ghost = false) }
        val ghost = tornTask ?: return@remember base
        base.toMutableList().apply {
            add(tornIndex.coerceIn(0, base.size), LeafSlot(ghost, ghost = true))
        }
    }
    val computedTotalMins = remember(orderedQueue) {
        orderedQueue.sumOf { it.estimatedMinutes }
    }

    fun submitTask() {
        val clean = newTaskTitle.trim()
        if (clean.isNotEmpty()) {
            soundEngine?.playMechanicalLatch(true)
            // MINUTES DIE (Sarthak's order): the composer asks
            // NOTHING about duration — 0, no estimate given.
            onAddTask(clean, 0, "Study", "", orderedQueue.isEmpty())
            newTaskTitle = ""
        }
    }

    fun onTearLeaf(task: FocusTaskEntity, index: Int) {
        if (isSessionActive || tornTask?.id == task.id) return
        // G-01 — book AT THE TAP, in the house order (the seal's
        // own: write, commit, publish — then sound/motion). The
        // completion is dispatched FIRST; the tear's sound and
        // its 260ms of motion follow over an already-booked fact.
        onCompleteTask(task.id)
        soundEngine?.playTearPulse()
        tornIndex = index
        tornTask = task
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(scheme.background)
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .testTag("focus_sequencer_screen"),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        // F-04 (chrome C9): the floating band's clearance.
        contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 100.dp)
    ) {
        // =====================================================================
        // SECTION 1: PROPORTIONAL HORIZON WORKLOAD BAR (TaskHorizonBarView)
        // =====================================================================
        item {
            FullBleedSectionHeader(
                titleWord = "HORIZON",
                // MINUTES DIE: the minute total prints only while
                // stored estimates exist to sum (pre-order rows).
                // A queue of name-only tasks totals nothing, and
                // printing "0m" would state a false sum.
                rightWordPrefix = if (computedTotalMins > 0) {
                    "${orderedQueue.size} TASKS • "
                } else {
                    "${orderedQueue.size} TASKS"
                },
                rightMonoNumber = if (computedTotalMins > 0) "${computedTotalMins}m" else "",
                useAccentOnNumber = false
            )
            Spacer(modifier = Modifier.height(8.dp))
            AndroidView(
                factory = { ctx ->
                    TaskHorizonBarView(ctx).apply {
                        this.soundEngine = soundEngine
                    }
                },
                update = { view ->
                    view.isNightMode = effectiveNight
                    view.soundEngine = soundEngine
                    view.tasks = orderedQueue
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp)
                    .testTag("total_horizon_bar")
            )
        }

        // =====================================================================
        // SECTION 2: SINGLE-LINE TASK COMPOSER (Decision #18)
        // =====================================================================
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Recessed single-line field (T4) — the composer has no card;
                // the field IS the object, keys sit under it (Ren).
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(scheme.surfaceVariant)
                        .border(1.dp, scheme.outlineVariant, RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (newTaskTitle.isEmpty()) {
                        Text(
                            text = "Add single-line focus task...",
                            fontFamily = AtkinsonHyperlegibleFamily,
                            fontSize = 13.sp,
                            color = scheme.onSurfaceVariant
                        )
                    }
                    BasicTextField(
                        value = newTaskTitle,
                        onValueChange = { newTaskTitle = it },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { submitTask() }),
                        textStyle = TextStyle(
                            fontFamily = AtkinsonHyperlegibleFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = scheme.onBackground
                        ),
                        // The accent law (chrome C7): ink caret.
                        cursorBrush = SolidColor(scheme.onBackground),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("new_task_input")
                    )
                }

                // MINUTES DIE (Sarthak's direct order, Oct 29):
                // the 15/25/45/90 bank is removed whole — the
                // composer asks for a name and nothing else. ADD
                // stands alone at full width.
                KeyCap(
                    label = "ADD",
                    selected = true,
                    onClick = { submitTask() },
                    modifier = Modifier.fillMaxWidth().testTag("add_task_button")
                )
            }
        }

        // Undo deleted task banner
        if (deletedTaskForUndo != null) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        // The accent law (chrome C7): the banner
                        // behind UNDO was a full-width accent surface
                        // — the loudest object on the screen, spent
                        // on furniture. It takes the ink control
                        // fill; UNDO's text alone wears the accent.
                        .background(inkControlFill())
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "REMOVED: ${deletedTaskForUndo.title}",
                        fontFamily = AtkinsonHyperlegibleFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = onInkControlFill(),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    BlockitActionChip(
                        wordLabel = "UNDO",
                        isUndoStyle = true,
                        onClick = onUndoDeleteTask,
                        testTag = "undo_delete_task_button"
                    )
                }
            }
        }

        // =====================================================================
        // SECTION 3: PROPORTIONAL TASK QUEUE CARDS (Decision #19)
        // =====================================================================
        item {
            // Title only — the DONE n tally that rode here counted
            // stored tasks with no lawful action attached; the
            // Drawer is storage, and counts of it are tracker talk.
            FullBleedSectionHeader(
                titleWord = "QUEUE",
                rightWordPrefix = "",
                rightMonoNumber = ""
            )
        }

        // THE LAST-FINISHED LINE (Wieder; ruled in by Sarthak —
        // "Add it — let me undo a mistaken finish"): one slot,
        // latest completion only, replaced by the next finish,
        // never accumulated. A mistaken DONE is one tired tap; this
        // line is the way back. While sealed it reads as pure
        // record — REOPEN is composed only while unsealed, and it
        // calls the dedicated un-complete verb (reopenStoredTask),
        // never the completion path: completing is one-way.
        val lastFinished = completedTasks.maxByOrNull { it.completedAtMillis }
        if (lastFinished != null) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("last_finished_line"),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = lastFinished.title,
                        fontFamily = AtkinsonHyperlegibleFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.5.sp,
                        color = scheme.onSurfaceVariant,
                        textDecoration = TextDecoration.LineThrough,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "FINISHED ${
                            ClockText.timeOfDay(LocalContext.current, lastFinished.completedAtMillis)
                        }",
                        fontFamily = AnonymousProFamily,
                        fontSize = 10.5.sp,
                        color = scheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 10.dp)
                    )
                    if (!isSessionActive) {
                        Text(
                            text = "REOPEN",
                            fontFamily = AtkinsonHyperlegibleFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = scheme.onBackground,
                            modifier = Modifier
                                .clickable { onReopenTask(lastFinished.id) }
                                .testTag("last_finished_reopen")
                        )
                    }
                }
            }
        }

        // The held blank leaf (Counterfoil §2.2): an empty queue does
        // not collapse the pad — perforation + ruled blank at full
        // height, so the address of "what's next" is learned once and
        // kept. The composer above is this screen's door; the blank
        // leaf itself carries no action.
        if (orderedQueue.isEmpty() && tornTask == null) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .testTag("task_blank_leaf"),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PerforationRow(sealed = isSessionActive)
                    // The ruled blank (chrome C9, §10.3): the held
                    // blank doubles to the Abriss standard, 112dp,
                    // with one rule line under the word — a page
                    // ruled for the hand, not a shrunken stub.
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(112.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "NOTHING QUEUED",
                            fontFamily = AtkinsonHyperlegibleFamily,
                            fontSize = 15.sp,
                            color = scheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(
                                    if (effectiveNight) {
                                        SequencerHairlineNight.copy(alpha = 0.18f)
                                    } else {
                                        TokenColorSecondaryContainer.copy(alpha = 0.18f)
                                    }
                                )
                        )
                    }
                }
            }
        }

        itemsIndexed(displayQueue, key = { _, slot -> slot.key }) { index, slot ->
            TaskLeafItem(
                task = slot.task,
                index = index,
                leavesBehind = (displayQueue.size - 1 - index).coerceIn(0, 3),
                isCued = index == 0,
                isSessionActive = isSessionActive,
                ghost = slot.ghost,
                onTear = { onTearLeaf(slot.task, index) },
                onToggleSubStep = onToggleSubStep,
                onAutoSplitTask = onAutoSplitTask,
                onCueTaskToFocus = onCueTaskToFocus,
                onDeleteTask = onDeleteTask,
                onNavigateToDeck = onNavigateToDeck
            )
        }
    }
}

/** One slot of the pad: a live leaf, or a torn leaf's ghost
 *  (G-01 — the booked leaf's snapshot, held for the tear's 260ms
 *  of pure motion). Keys keep the two apart across the booking:
 *  the live leaf leaves the list the instant it is torn; the
 *  ghost enters under its own key and is never the same item. */
private data class LeafSlot(val task: FocusTaskEntity, val ghost: Boolean) {
    val key: String get() = if (ghost) "ghost_${task.id}" else "task_${task.id}"
}

/**
 * One leaf of the pad (Linea — THE COUNTERFOIL, Improvers 05, applied
 * to the Tasks queue): the task line as a tear-off stub. A perforation
 * tops the leaf — anchor and affordance in one mark — the leaves
 * behind it show as a stack edge at the right, and completing the
 * task IS the tear: the leaf detaches at the perforation (drops 10dp,
 * fades, 180ms curve A; the torn edge's dashes halve over 260ms curve
 * B) and only then does the existing completion path run, so the next
 * leaf is uncovered, never swapped in. While a session is sealed the
 * perforation is overprinted solid and the tear does not exist —
 * no DONE word, no gesture (HOLD).
 *
 * Truth Car 2 (G-01) reversed the booking order the paragraph above
 * was written under: the completion now books AT THE TAP (the
 * parent dispatches the write, then the tear's sound), and this
 * leaf's tear motion runs as a GHOST ([ghost] = true) over the
 * already-booked fact. The old order — sound at the tap, booking
 * 260ms later inside a plain LaunchedEffect — let process death
 * resurrect the task and let navigation cancel the booking; it
 * was ESR-11's last live site. The motion is unchanged (drops
 * 10dp, fades 180ms curve A, dashes halve over 260ms curve B);
 * only its position in the order moved: last, where motion
 * belongs.
 *
 * Adaptations from the filed Deck spec, forced by this screen and
 * recorded for the merge: the drag form of the tear is NOT built
 * here — a vertical drag on a LazyColumn item is the list's own
 * scroll gesture, so tap parity (the spec's accessibility floor)
 * carries the tear alone; the drag belongs to the Deck application,
 * where the leaf does not live in a scroller.
 */
@Composable
private fun TaskLeafItem(
    task: FocusTaskEntity,
    index: Int,
    leavesBehind: Int,
    isCued: Boolean,
    isSessionActive: Boolean,
    ghost: Boolean,
    onTear: () -> Unit,
    onToggleSubStep: (Int, Int) -> Unit,
    onAutoSplitTask: (Int) -> Unit,
    onCueTaskToFocus: (Int) -> Unit,
    onDeleteTask: (Int) -> Unit,
    onNavigateToDeck: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scheme = MaterialTheme.colorScheme
    // A ghost tears from its first frame; a live leaf never tears
    // in place (its tap books, and the ghost replaces it).
    var tearing by remember(task.id, ghost) { mutableStateOf(false) }
    LaunchedEffect(ghost) {
        if (ghost) tearing = true
    }
    val tearProgress by animateFloatAsState(
        targetValue = if (tearing) 1f else 0f,
        animationSpec = MonolithCurves.specA<Float>(MonolithCurves.DURATION_180),
        label = "counterfoilTear"
    )
    val dashSettle by animateFloatAsState(
        targetValue = if (tearing) 0.5f else 1f,
        animationSpec = MonolithCurves.specB<Float>(MonolithCurves.DURATION_260),
        label = "counterfoilDashSettle"
    )

    fun tear() {
        if (!ghost && !isSessionActive) {
            // The parent books at this tap, then sounds the tear;
            // this leaf's own motion is the ghost's, not its own.
            onTear()
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .testTag("task_item_${task.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                // The pad edge owns the leaf's right rim; print never
                // reaches under it. (Chrome C9: the edge pitch steps
                // 2→3 and the inset 6→8 — the stack reads thicker.)
                .padding(end = (leavesBehind * 3 + 8).dp)
                // The leaf's paper (chrome C6, ESR-13 — the false
                // close, rewritten): a QUEUED leaf lies on the sheet
                // (surface); the HEAD leaf is RAISED (surfaceBright)
                // — the pad's top sheet reads as the top sheet. The
                // old comment here claimed this background gave the
                // leaf paper; Quaere's census measured surface at
                // 1.00:1 against the ground — it IS the ground token,
                // and a claim in a comment is not a fix. The raised
                // variant now exists exactly where the raise is
                // true: the head leaf, and nowhere else.
                .background(if (isCued) scheme.surfaceBright else scheme.surface)
                .graphicsLayer {
                    alpha = 1f - tearProgress
                    translationY = tearProgress * 10.dp.toPx()
                }
                .padding(vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PerforationRow(sealed = isSessionActive, dashScale = dashSettle, raised = isCued)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Running mark (Counterfoil §2.5): a 5dp filled
                    // square by the plate — shape and position carry
                    // the state, no colour anywhere.
                    if (isCued && isSessionActive) {
                        Box(
                            modifier = Modifier
                                .size(5.dp)
                                .background(scheme.onBackground)
                        )
                    }
                    Text(
                        text = if (isCued) "ACTIVE" else "QUEUE",
                        fontFamily = AnonymousProFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.5.sp,
                        letterSpacing = 1.sp,
                        color = if (isCued) scheme.onBackground else scheme.onSurfaceVariant
                    )
                }
                Text(
                    // MINUTES DIE: the estimate prints only where
                    // one was given (a pre-order row's history).
                    text = if (task.estimatedMinutes > 0) {
                        "#${String.format("%02d", index + 1)} · ${task.estimatedMinutes}m"
                    } else {
                        "#${String.format("%02d", index + 1)}"
                    },
                    fontFamily = AnonymousProFamily,
                    fontSize = 11.sp,
                    color = scheme.onSurfaceVariant
                )
            }

            Text(
                text = task.title,
                fontFamily = AtkinsonHyperlegibleFamily,
                fontWeight = if (isCued) FontWeight.Bold else FontWeight.Medium,
                fontSize = 15.sp,
                color = scheme.onBackground
            )

            // Sub-steps as quiet numbered text (T8) — tap toggles; the
            // strikethrough is the state, no pill, no DONE block. They
            // are printed on the leaf, so they tear away with it.
            if (task.subSteps.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    task.subSteps.forEachIndexed { stepIdx, step ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(enabled = !ghost) { onToggleSubStep(task.id, stepIdx) }
                                .padding(start = 14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = step.title,
                                fontFamily = AtkinsonHyperlegibleFamily,
                                fontWeight = FontWeight.Medium,
                                fontSize = 12.5.sp,
                                color = if (step.isDone) scheme.onSurfaceVariant else scheme.onBackground,
                                textDecoration = if (step.isDone) TextDecoration.LineThrough else TextDecoration.None,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                // DRW-08: the shared two-digit
                                // voice — never "010".
                                text = com.example.data.twoDigitCount(stepIdx + 1),
                                fontFamily = AnonymousProFamily,
                                fontSize = 10.5.sp,
                                color = scheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Actions as words, not a chip farm (T8). The screen's one
            // orange lives on the cued task's START — as text.
            Row(
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // The tear label (Counterfoil §2.3): a state word at
                // the lawful 15sp, plate family. Absent — not greyed —
                // while a session is sealed: its absence is the lock.
                if (!isSessionActive) {
                    Text(
                        text = "DONE — TEAR",
                        fontFamily = AnonymousProFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        letterSpacing = 1.sp,
                        color = scheme.onBackground,
                        modifier = Modifier.clickable(enabled = !ghost) { tear() }
                    )
                }
                if (task.subSteps.isEmpty()) {
                    Text(
                        text = "SPLIT",
                        fontFamily = AtkinsonHyperlegibleFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = scheme.onSurfaceVariant,
                        modifier = Modifier.clickable(enabled = !ghost) { onAutoSplitTask(task.id) }
                    )
                }
                if (isCued) {
                    // START is the screen's one commit verb, and it
                    // takes the commit's type (chrome C9 §10.4):
                    // Anonymous Pro 700 on the 15sp step, tracked
                    // +0.5, accent TEXT — the budget buys the verb,
                    // never a fill.
                    Text(
                        // MINUTES DIE: START carries the estimate
                        // only where one was given; the verb stands
                        // alone on a name-only task.
                        text = if (task.estimatedMinutes > 0) {
                            "START ${task.estimatedMinutes}m"
                        } else {
                            "START"
                        },
                        fontFamily = AnonymousProFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        letterSpacing = 0.5.sp,
                        color = scheme.primaryContainer,
                        modifier = Modifier.clickable(enabled = !ghost) {
                            onCueTaskToFocus(task.id)
                            onNavigateToDeck()
                        }
                    )
                } else {
                    Text(
                        text = "CUE",
                        fontFamily = AtkinsonHyperlegibleFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = scheme.onSurfaceVariant,
                        modifier = Modifier.clickable(enabled = !ghost) {
                            onCueTaskToFocus(task.id)
                            onNavigateToDeck()
                        }
                    )
                }
                Text(
                    text = "DEL",
                    fontFamily = AtkinsonHyperlegibleFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = scheme.onSurfaceVariant,
                    modifier = Modifier.clickable(enabled = !ghost) { onDeleteTask(task.id) }
                )
            }
        }

        // The pad edge (Counterfoil §2.1): one 1dp line per leaf
        // behind this one, capped at 3 — queue depth as the object's
        // thickness, felt before it is read. Absence is the state:
        // the last leaf has no edge lines. Drawn, not laid out: a
        // fillMaxHeight chain inside a wrap-height list item
        // collapses to zero and the edge vanishes (first render
        // proved it), so the lines are painted on a matchParentSize
        // canvas that takes the leaf's measured height.
        if (leavesBehind > 0) {
            // The pad edge, depth-stepped (chrome C9 §10.3): each
            // leaf behind steps AWAY in tone — day steps down the
            // warm greys, night steps down the warm darks — so the
            // stack's depth is read in the tones, not just counted
            // in lines. One flat outlineVariant for every depth was
            // a wall, not a stack.
            val night = scheme.background.luminance() < 0.5f
            val edgeTones = if (night) {
                listOf(
                    SequencerEdgeNight1,
                    SequencerEdgeNight2,
                    SequencerEdgeNight3
                )
            } else {
                listOf(
                    SequencerEdgeDay1,
                    SequencerEdgeDay2,
                    SequencerEdgeDay3
                )
            }
            Canvas(modifier = Modifier.matchParentSize()) {
                val inset = 4.dp.toPx()
                val lineW = 1.dp.toPx()
                for (k in 0 until leavesBehind) {
                    val x = size.width - (1f + 3f * k).dp.toPx() - lineW / 2f
                    drawLine(
                        color = edgeTones[k.coerceIn(0, 2)],
                        start = Offset(x, inset),
                        end = Offset(x, size.height - inset),
                        strokeWidth = lineW
                    )
                }
            }
        }
    }
}

/**
 * The perforation (Counterfoil §2.1): a row of cuts in the hairline's
 * own token — 3dp dash / 3dp gap, 1dp tall — replacing the solid
 * divider. It says "belongs to the pad above" and "this leaf detaches
 * here" in one mark. While a session is sealed the dashes are
 * overprinted by a solid rule: a perforation you cannot tear is a
 * lie, so running seals it shut in the mark itself. [dashScale] is
 * the torn edge settling (3dp → 1.5dp) during a tear.
 */
@Composable
private fun PerforationRow(
    sealed: Boolean,
    dashScale: Float = 1f,
    raised: Boolean = false,
    modifier: Modifier = Modifier
) {
    val scheme = MaterialTheme.colorScheme
    // Chrome C6/C9: the perforation's tone follows its leaf. On the
    // RAISED head leaf the dashes are cut in the ground's own tone
    // (scheme.background — a cut shows what's beneath the sheet).
    // On a queued leaf they print at 30% of the hairline token —
    // the queued perforation recedes; only the head leaf's cut is
    // at full strength.
    val color = if (raised) {
        scheme.background
    } else {
        scheme.outlineVariant.copy(alpha = 0.30f)
    }
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(1.dp)
    ) {
        val y = size.height / 2f
        val dash = 3.dp.toPx() * dashScale
        val gap = 3.dp.toPx()
        var x = 0f
        while (x < size.width) {
            drawLine(
                color = color,
                start = Offset(x, y),
                end = Offset(minOf(x + dash, size.width), y),
                strokeWidth = size.height
            )
            x += dash + gap
        }
        if (sealed) {
            drawLine(
                color = color,
                start = Offset(0f, y),
                end = Offset(size.width, y),
                strokeWidth = size.height
            )
        }
    }
}

/**
 * A small physical key (Ren, ET66 lesson): hairline-edged cap one step up
 * from the page; selected = ink fill. The Tasks composer's duration + ADD.
 */
@Composable
private fun KeyCap(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scheme = MaterialTheme.colorScheme
    Box(
        modifier = modifier
            .height(36.dp)
            .clip(RoundedCornerShape(9.dp))
            .background(if (selected) inkControlFill() else scheme.surface)
            .border(1.dp, if (selected) inkControlFill() else scheme.outlineVariant, RoundedCornerShape(9.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontFamily = AnonymousProFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = if (selected) onInkControlFill() else scheme.onSurfaceVariant
        )
    }
}
