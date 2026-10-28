package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
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
import com.example.service.TactileSoundEngine
import com.example.ui.components.BlockitActionChip
import com.example.ui.components.FullBleedSectionHeader
import com.example.ui.components.rememberMixedTypographyText
import com.example.ui.theme.JetBrainsMonoFamily
import com.example.ui.theme.PlusJakartaSansFamily
import com.example.ui.views.TaskHorizonBarView

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
    onClearCompleted: () -> Unit,
    onToggleSubStep: (Int, Int) -> Unit,
    onAddSubStep: (Int, String) -> Unit,
    onAutoSplitTask: (Int) -> Unit,
    onNavigateToDeck: () -> Unit
) {
    val scheme = MaterialTheme.colorScheme
    val effectiveNight = isDarkTheme || isSessionActive
    var newTaskTitle by remember { mutableStateOf("") }
    var newTaskMinutes by remember { mutableIntStateOf(25) }

    val orderedQueue = remember(activeTask, queuedTasks) {
        buildList {
            if (activeTask != null) add(activeTask)
            addAll(queuedTasks.filter { it.id != activeTask?.id })
        }
    }
    val computedTotalMins = remember(orderedQueue) {
        orderedQueue.sumOf { it.estimatedMinutes }
    }

    fun submitTask() {
        val clean = newTaskTitle.trim()
        if (clean.isNotEmpty()) {
            soundEngine?.playMechanicalLatch(true)
            onAddTask(clean, newTaskMinutes, "Study", "", orderedQueue.isEmpty())
            newTaskTitle = ""
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(scheme.background)
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .testTag("focus_sequencer_screen"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // =====================================================================
        // SECTION 1: PROPORTIONAL HORIZON WORKLOAD BAR (TaskHorizonBarView)
        // =====================================================================
        item {
            FullBleedSectionHeader(
                titleWord = "WORKLOAD HORIZON",
                rightWordPrefix = "${orderedQueue.size} TASKS • ",
                rightMonoNumber = "${computedTotalMins}m",
                useAccentOnNumber = true
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
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(scheme.surfaceBright)
                    .border(1.dp, scheme.outlineVariant, RoundedCornerShape(24.dp))
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(scheme.surfaceVariant)
                        .border(1.dp, scheme.outlineVariant, RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (newTaskTitle.isEmpty()) {
                        Text(
                            text = "Add single-line focus task...",
                            fontFamily = PlusJakartaSansFamily,
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
                            fontFamily = PlusJakartaSansFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = scheme.onBackground
                        ),
                        cursorBrush = SolidColor(scheme.primaryContainer),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("new_task_input")
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(15, 25, 45, 90).forEach { mins ->
                        BlockitActionChip(
                            wordLabel = "",
                            monoBadge = "${mins}m",
                            isPrimaryInk = newTaskMinutes == mins,
                            onClick = { newTaskMinutes = mins },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    BlockitActionChip(
                        wordLabel = "ADD",
                        isAccentArmed = true,
                        onClick = { submitTask() },
                        modifier = Modifier.weight(1.2f),
                        testTag = "add_task_button"
                    )
                }
            }
        }

        // Undo deleted task banner
        if (deletedTaskForUndo != null) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(scheme.primary)
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "REMOVED: ${deletedTaskForUndo.title}",
                        fontFamily = PlusJakartaSansFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = scheme.onPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    BlockitActionChip(
                        wordLabel = "UNDO",
                        isAccentArmed = true,
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
            FullBleedSectionHeader(
                titleWord = "PRIORITY TASK QUEUE",
                rightWordPrefix = "DONE ",
                rightMonoNumber = completedTasks.size.toString()
            )
        }

        itemsIndexed(orderedQueue, key = { _, t -> t.id }) { index, task ->
            val isCued = index == 0
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(if (isCued) 24.dp else 12.dp))
                    .background(if (isCued) scheme.surfaceBright else scheme.surfaceVariant)
                    .border(
                        width = 1.dp,
                        color = if (isCued) scheme.primaryContainer else scheme.outlineVariant,
                        shape = RoundedCornerShape(if (isCued) 24.dp else 12.dp)
                    )
                    .padding(14.dp)
                    .testTag("task_item_${task.id}"),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = rememberMixedTypographyText(
                            wordPrefix = if (isCued) "ACTIVE • " else "QUEUE ",
                            monoNumber = String.format("#%02d", index + 1),
                            wordSuffix = " • ",
                            wordColor = if (isCued) scheme.primaryContainer else scheme.onSurfaceVariant,
                            monoColor = if (isCued) scheme.primaryContainer else scheme.onBackground
                        ),
                        fontSize = 11.sp
                    )
                    Text(
                        text = "${task.estimatedMinutes}m",
                        fontFamily = JetBrainsMonoFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = if (isCued) scheme.primaryContainer else scheme.onBackground
                    )
                }

                Text(
                    text = task.title,
                    fontFamily = PlusJakartaSansFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = scheme.onBackground
                )

                // Micro-steps list if split
                if (task.subSteps.isNotEmpty()) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        task.subSteps.forEachIndexed { stepIdx, step ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (step.isDone) scheme.primary else scheme.background)
                                    .border(1.dp, scheme.outlineVariant, RoundedCornerShape(12.dp))
                                    .clickable { onToggleSubStep(task.id, stepIdx) }
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = step.title,
                                    fontFamily = PlusJakartaSansFamily,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 12.sp,
                                    color = if (step.isDone) scheme.onPrimary else scheme.onBackground,
                                    textDecoration = if (step.isDone) TextDecoration.LineThrough else TextDecoration.None
                                )
                                Text(
                                    text = if (step.isDone) "DONE" else "0${stepIdx + 1}",
                                    fontFamily = JetBrainsMonoFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.5.sp,
                                    color = if (step.isDone) scheme.primaryContainer else scheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                HorizontalDivider(thickness = 1.dp, color = scheme.outlineVariant)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    BlockitActionChip(
                        wordLabel = "DONE",
                        isPrimaryInk = true,
                        onClick = { onCompleteTask(task.id) },
                        modifier = Modifier.weight(1f)
                    )
                    if (task.subSteps.isEmpty()) {
                        BlockitActionChip(
                            wordLabel = "SPLIT",
                            monoBadge = "3",
                            onClick = { onAutoSplitTask(task.id) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    BlockitActionChip(
                        wordLabel = if (isCued) "START" else "CUE",
                        monoBadge = "${task.estimatedMinutes}m",
                        isAccentArmed = isCued,
                        onClick = {
                            onCueTaskToFocus(task.id)
                            onNavigateToDeck()
                        },
                        modifier = Modifier.weight(1.1f)
                    )
                    BlockitActionChip(
                        wordLabel = "DEL",
                        onClick = { onDeleteTask(task.id) },
                        modifier = Modifier.weight(0.75f)
                    )
                }
            }
        }
    }
}
