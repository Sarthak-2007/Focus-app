# AUDIT — TASKS (the task sequencer) · FULL-APP BRUTAL AUDIT

*Coordinator: Amt. Ordered by Sarthak, Oct 30 01:15 IST — every screen to the
critics and everybody else, coordinators too. Ruled Oct 30, ~01:30 IST.
Paper only: nobody compiled (Ren compiles, when Sarthak says).*

**Crew (one of each kind, all filed to mail/amt/):** Dens (critic, density) ·
Plan (designer, information architecture) · Vega (inventor, mechanisms) ·
Muede (human, the 1 AM walk) · Weiss (finisher, Tasks bench) · Fremd
(fresh-eyes, cold protocol — code first, intentions second).
Filings: mail/amt/ 0119 Plan · 0121 Dens · 0122 Vega · 0123 Weiss ·
0124 Muede · 1953 Fremd. Every load-bearing fact below was spot-verified
against trunk by Amt (bar floor, split template, routine intention, undo
window, the nine dead parameters).

**The screen's job, one sentence:** line up tomorrow's work in order — a pad
of leaves, the head leaf is next; it stores nothing and scores nothing.

## The headline

The screen's machinery is honest in its bones (the tear books at the tap;
the composer asks nothing) and false in its furniture (a proportional bar
measuring a dead quantity, a SPLIT that fabricates, a START that starts
nothing). But the audit's largest find is not on this screen at all:

**The 1 AM errand fails at 05:00 even when the screen is used perfectly.**
Muede walked it: queue tomorrow's first task at 1 AM, and the FASTED BLOCK
routine session still seals titled "FASTED BLOCK" — RoutineDerive.kt:169
sets `intention = routine.name` and the routine path never reads the task
queue. A hand-armed session honours the head leaf (MonolithViewModel);
the routine arm ignores it. The pad's whole promise — queue it tonight,
it starts tomorrow — is kept by one arm path and broken by the other.
This is fix F-1, escalated to Ren: it is machinery, not furniture.

## Convergences (independent seats, same facts)

Four seats convicted the horizon bar without seeing each other's work
(Dens, Plan, Vega as a bonus charge, Fremd as lie #2). Three convicted
SPLIT (Vega, Fremd as lie #1, Dens in part). Three convicted the sealed
state's live verbs (Muede, Fremd, Weiss). Convergence is treated as proof.

## Inventory & verdicts (screen order, trunk FocusSequencerScreen.kt)

1. **HORIZON header + proportional horizon bar (42dp) — KILL, both.**
   The bar's proportions derive from `estimatedMinutes.coerceAtLeast(10)`
   per task (BlockitTowerFadersAndCatalogViews.kt:194,198): since MINUTES
   DIE, every task the composer can create carries 0, is floored to a
   phantom 10, and draws an EQUAL segment — a proportional chart of a
   quantity the app no longer asks for, printed ~200dp above the queue it
   summarises. The header's "N TASKS • NNNm" is the same regime's total.
   The task count MOVES to the QUEUE header (item 4) as its right word;
   the minute total dies with the bar. (Dens, Plan, Vega, Fremd.)
2. **Composer (field + full-width ADD) — KEEP the field, MOVE the block,
   SHRINK the key.** The composer asks a name and nothing else — MINUTES
   DIE verified in submitTask (duration hard-coded 0). But the door sits
   above the queue it feeds while a new leaf lands at the bottom, and the
   full-width ink ADD out-shouts START, the screen's real commit. The
   composer becomes the pad's next blank leaf at its foot; ADD becomes an
   end-key on that leaf; a second key, FIRST, lands the leaf at the head
   (Muede's errand, F-2). (Plan, Muede, Dens.)
3. **Undo banner (REMOVED: title + UNDO) — KEEP (state-only, as built),**
   with its dismiss wired (item 10). The 5,000 ms undo window
   (UndoDeleteStore.kt:99) is short for a 1 AM thumb; extending it is a
   machinery price, noted not ruled here. (Dens acquits; Muede charges
   the window.)
4. **QUEUE header — KEEP, becomes the screen's one header.** Right word:
   "N TASKS", and while a session runs, "N TASKS · SEALED" — the seal
   gets one printed word instead of a silent darkening (Muede F-3).
5. **LAST-FINISHED line — KEEP, content untouchable** (Sarthak ruled it
   in: mistaken-finish undo). Two execution cures: the FINISHED print
   gains its day when it isn't today (a yesterday finish currently reads
   as today's time — Fremd), and REOPEN stays composed only while
   unsealed, as built. Placement stays (Plan's move-under-head option
   declined: a finished record does not belong between the man and his
   next task).
6. **Blank leaf (NOTHING QUEUED, 112dp) — KEEP.** The pad holds its
   address when empty; acquitted by every seat that named it. Its second
   printing elsewhere on the screen (Fremd, Plan) is KILLed — one
   printing of any fact.
7. **Leaf plate words ACTIVE / QUEUE — KILL.** The same fact as position,
   printed as a state word — and ACTIVE is false as printed: it means
   "first in line" while no session runs (Muede), and it is derived from
   index, never from task.status (Fremd). Head-ness keeps its honest
   signals: raised paper, bold title, #01, the accent verb.
8. **Plate number #NN — KEEP.** Plan charged it as machinery on paper;
   Muede's 5-second test passed it and Dens keeps it. Ruled for the
   human: #NN is the man's own handle for a leaf. During the tear's
   ghost window, #NN and the header count derive from the displayed
   list (ghost included) so print never disagrees with the paper in
   hand (Fremd's ghost-window charge).
9. **Title (15sp Atkinson) — KEEP.** The leaf's reason to exist; in the
   recomposition it becomes the 3rd object the eye crosses, not the
   10th (Plan's count: ~285dp down today, ~365dp worst case).
10. **Sub-steps (numbered quiet print, tap toggles strike) — KEEP the
    print, GATE the tap.** Acquitted as print (no fraction, no score —
    Dens). While sealed, the toggle dies with the other verbs (item 12).
    A full-width tap target that strikes silently is a 1 AM trap
    (Muede): the strike gains the tear's own sound tick, or the toggle
    narrows to the step's number — builder's choice, priced in F-6.
11. **SPLIT — KILL the verb as built; replace with HAND-SPLIT.**
    `defaultSubStepsForCategory(title, category)` never reads the title
    and branches on category alone; the composer hard-codes category
    "Study", which matches no branch, so EVERY split task receives the
    same physics triple ("Check units & boundary conditions" on a
    biology leaf). A verb that fabricates is worse than no verb. The
    replacement wires the dead onAddSubStep: SPLIT opens blank ruled
    lines on the leaf and the man writes his own steps. Vega's Stencil
    (his own saved splits offered back) is recorded as a future
    fragment, never generated content. (Vega, Fremd, Dens.)
12. **The leaf's verbs under a seal — GATE ALL.** Today only the tear
    withdraws when a session seals; DEL, CUE/START, SPLIT and sub-step
    toggles stay live (Muede, Fremd, Weiss — three seats). The sealed
    leaf is a record: every verb absent, not greyed, exactly as the
    tear already behaves. The perforation's solid overprint is the
    mark; the QUEUE header's SEALED word is the sentence.
13. **DONE — TEAR — KEEP the act, RENAME to TEAR, ENLARGE the target,
    RESPIN the motion.** The booking is acquitted everywhere (G-01:
    books at the tap, motion follows the fact). The word shortens —
    the perforation already teaches the gesture (Plan). The target
    grows from a glyph-height text to a 48dp zone (Muede). The motion
    as built is a fade in a tear's name: Weiss measured curve A at
    0.878 of fade by 90ms, drop 10dp against a 12dp inter-leaf gap,
    and the dashes' 260ms settle painted inside a layer whose alpha
    reaches 0 at 180ms — the tear's second half is invisible by
    construction. Respin (Weiss, ~15 lines): 30dp travel, one 300ms
    clock, alpha held to 40%, accelerating cubic, perforation lifted
    out of the fading layer, ghost lifetime 260→300ms. Beyond the
    respin, Vega's ZIP-TEAR is ruled the house tear standard for the
    next tear build (see Escalations).
14. **START / CUE — SPLIT THE VERB.** As built they are one handler in
    two costumes (promote + navigate to Deck), so START starts nothing
    and CUE secretly preempts the current head (Fremd, Muede, Dens).
    Ruled: CUE = make this leaf the head, STAY on Tasks. START =
    appears on the head leaf only, arms via the Deck as today. One
    act per word; CUE's teleport dies.
15. **DEL — KEEP, gated by the seal (item 12).** Bare-word row with no
    confirmation is lawful here because the undo banner exists; its
    5-second window is the weak half (item 3).
16. **Pad edge (right stack lines) — KEEP the device, MOVE the lines.**
    Tones step truly (Weiss: day 1.89/1.50/1.27, night 2.04/1.56/1.27)
    but the lines cluster 1–7dp from the edge against a leaf rim at
    11/14/17dp — 9–10dp of bare ground reads as a margin stripe, not
    a stack. Re-anchor to rim + 1 + 3k; leaf gap 12dp → 4dp so the
    pad touches. Saturation at 3 leaves behind is honest (deeper
    stacks don't claim more paper than drawn) — KEEP the cap.
17. **Perforation — KEEP, UNIFY the tone.** Inversion found by Weiss:
    the head leaf's cut (ground tone over bright paper, 1.23/1.35:1)
    is WEAKER than the queued leaves' (30% outlineVariant, 1.50/1.56:1),
    against the code comment's claim that the head's cut is "at full
    strength" — the one tearable leaf has the invisible cut. One tone
    for all leaves: outlineVariant at alpha 0.55 (2.18–2.46:1).
18. **Paper — KEEP the head raise, FINISH the queued leaves.** Head
    raise is real but thin (1.23:1 day / 1.35:1 night) — Weiss's
    foot-shadow spec stands priced below. Queued leaves sit at 1.00:1
    — the surface IS the ground; they get a foot rule (state-only
    separation) per Weiss, not a fake raise.
19. **Night plate print — MOVE (token, leaf-local).** Night plate/index
    at 3.51:1 fails a tired eye; leaf-local #9C8E84 (5.78:1) per Weiss.

## The fix list (priced, in build order)

- **F-1 · Routine sessions take their intention from the head task**
  (RoutineDerive.kt:169). The audit's headline. Machinery, escalated
  to Ren for the machinery seat's exact price — Muede's sketch reads
  SMALL–MEDIUM. Nothing else in this list matters more.
- **F-2 · Composer to the foot + FIRST key** (items 2). Inside Plan's
  recomposition. The recomposition as a whole (bar/header kill, count
  to QUEUE, composer leaf, time-to-answer 10th object → 3rd) is priced
  by Plan at ≈ 2½ builder-days, paper only.
- **F-3 · Kill bar + HORIZON header** (~113 lines deleted, zero added —
  Dens). Count rewire to QUEUE header incl. the SEALED state word.
- **F-4 · Verb truth set**: ACTIVE/QUEUE words killed; CUE/START split
  (CUE stops navigating); TEAR renamed + 48dp target; legacy "NNm"
  struck from START (it is a promise; the plate keeps it as record —
  Dens) — all SMALL.
- **F-5 · Seal gating**: DEL/CUE/SPLIT/sub-step toggles absent while
  sealed (item 12). SMALL.
- **F-6 · Hand-SPLIT**: kill `defaultSubStepsForCategory` as this
  screen's splitter; SPLIT opens blank ruled lines via onAddSubStep;
  sub-step strike feedback (tick or narrowed target). SMALL–MEDIUM.
- **F-7 · Tear respin** (Weiss, ~15 lines) + ghost-window print freeze
  (item 8). SMALL.
- **F-8 · Finish set** (Weiss): unified cut tone @0.55 alpha; pad edge
  re-anchored + 4dp leaf gap; night plate #9C8E84; queued-leaf foot
  rule; head foot-shadow. SMALL, all token/geometry.
- **F-9 · The nine dead parameters**: seven DELETED from the signature
  (onEditTask, onUpdateTaskDuration, onPromoteTask, onMoveTaskOrder —
  superseded by F-4's split and Slip-Sort below — onDeferTask,
  totalQueueMinutes, deferredTasks), two WIRED (onAddSubStep by F-6,
  onDismissUndoBanner on the banner). A signature must not promise a
  manager the screen isn't. SMALL.
- **Total: ≈ 2½ builder-days (recomposition incl. F-2/F-3/F-4/F-9) +
  F-5/F-6/F-7/F-8 inside or beside it + F-1 machinery (Ren's seat).**

## Escalations to Ren (above a coordinator's ruling)

1. **F-1** — routine intention ← head task. Machinery ruling + price.
2. **ZIP-TEAR as the house tear standard** (Vega, ~180 lines: finger
   traces the perforation in a 32dp band, dashes consume behind the
   finger, booking at 85% crossing in G-01 order, tap retained as the
   accessibility floor). Ruled here as the standard for the NEXT tear
   build; whether it displaces F-7's respin in this pass is Ren's.
3. **Slip-Sort** (Vega): reorder by dragging the pad-edge handle,
   wiring the move verb honestly (+ a one-transaction setQueueOrder
   in the repository, ~40 lines, replacing the ±1 swap). This audit
   rules the ABSENCE of reorder a defect on a screen named Sequencer;
   the mechanism choice is Ren's.
4. **Back of the Leaf** (Vega, ~160 lines): flip face carrying edit,
   add-step, FOLD (defer). A new surface — Ren's call, not this pass.
5. **Deferred tasks are invisible here** (Fremd): deferredTasks arrives
   and is never rendered. Where a deferred task can be SEEN is a
   cross-screen question this audit cannot answer from one screen.

## Acquittals (binding — these stand as the screen's proven goods)

- The composer asks NOTHING about duration; MINUTES DIE holds whole
  (all six seats). · The tear BOOKS AT THE TAP — G-01's order is
  honest machinery (Dens, Vega, Fremd, Muede). · The sealed
  perforation's solid overprint and the tear's absence-not-greying
  (Plan, Vega, Fremd). · The held blank leaf (all). · The head leaf's
  raised paper and START as the screen's one accent (Weiss, Plan). ·
  Sub-steps as quiet print with no fraction or score printed (Dens). ·
  The undo banner's accent demotion (Plan). · twoDigitCount's shared
  voice (Fremd). · Blocker-not-tracker holds on this screen: nothing
  here grades, streaks, or totals finished work (Muede, Dens).

— Amt, coordinator, TASKS audit. Filings answered in full by this
verdict; dissents recorded where ruled against (Plan on #NN and the
last-finished placement; Fremd on START in part).
