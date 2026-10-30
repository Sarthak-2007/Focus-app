# ISSUE #6 — BUILDER MAP (Deck screen, audit fixes F1–F22)

Builder shift 1 · Rost's cell · Paper-verified against the tree on
branch `agent/issue-6` (no compile — code closes DECLARED, per the
build law). Source audit: `studio/plan/AUDIT-deck.md` (§9 fix list).
Primary file: `app/src/main/java/com/example/ui/screens/TactileDeckScreen.kt`
(1,148 lines at map time). Host: `app/src/main/java/com/example/MainActivity.kt`
(Deck call at line 470). Only other call site in the repo:
`app/src/test/java/com/example/CascadeDeckRenderTest.kt` (`setDeck()`,
line 88) — verified by repo-wide grep for `TactileDeckScreen(`:
exactly two call sites, both named-argument style.

Effort key (audit §9): S = a sitting's edit · M = a lane · L = a
restructure. Efforts below are the audit's own prices, kept.

---

## F1 — Cut the dead parameters + host wiring · S · **DONE THIS SHIFT (20 of 21; see ledger)**

- Files/functions: `TactileDeckScreen.kt` → `TactileDeckScreen()`
  signature (lines 100–130); `MainActivity.kt` → Deck destination
  call (lines 474–511); `CascadeDeckRenderTest.kt` → `setDeck()`
  call (lines 92–122).
- Change: delete every parameter the body never reads, its
  call-site argument at both call sites, and the three imports
  orphaned by the deletions (`StudyChecklistItem`,
  `BlockedAppEntity`, `FocusProfileEntity` in the Deck file).
- Full per-parameter evidence ledger at the end of this file.

## F2 — Kill the FLOW cap as a control; mode is printed, never a second door · S

- Files/functions: `TactileDeckScreen.kt` → Section 1 idle row,
  the FLOW key-cap `Box` (clickable calling
  `onSelectStudyPreset(StudyModePreset.STOPWATCH_FLOW)` +
  `onDialMinutesChanged(5)`); knock-on: `onSelectStudyPreset`
  parameter + `MainActivity.kt` line 497 wiring
  (`viewModel::selectStudyPreset`).
- Change: delete the cap; `isStopwatchMode` stays keyed on
  `dialMinutes < 10` alone (ESR-08 derivation already in body),
  and the surviving `onSelectStudyPreset` wire becomes dead and
  is cut in the same pass (cascade, not part of F1).

## F3 — Kill the % text; progress made true · S

- Files/functions: `TactileDeckScreen.kt` → `progressFraction`
  derivation (body, after `totalSpanMinutes`) and the running
  branch of Section 1 (progress track `Box` + the `%` `Text` row).
- Change: timed progress = elapsed/target filling (invert the
  current remaining/target, drop the 0.03/0.05 floors); FLOW
  renders no track at all; delete the `%` text row.

## F4 — Eyebrow prints the recipe; standing TOTAL / SESSION RUNNING die · S

- Files/functions: `TactileDeckScreen.kt` → eyebrow `Text` in the
  hero card's top `Row` (its `text = when { … }`).
- Change: idle branch prints the recipe from
  `studyBlocksCount × dialMinutes` (form `3 × 50 + BREAKS`);
  `showRunningForm -> "SESSION RUNNING"` and `else -> "TOTAL"`
  branches die; routine-born sessions print their name (source
  of the name — session/profile field — to be confirmed against
  `FocusSessionEntity` at build time; the break branch already
  exists and stays).

## F5 — Break hero: during a Safe Break the hero IS the break · S

- Files/functions: `TactileDeckScreen.kt` → `displaySeconds`
  derivation, `heroAnnotated` when-block, eyebrow `Text`.
- Change: when `activeSession.isInSafeBreak`, hero readout counts
  down `safeBreakSecondsRemaining` (not elapsed/remaining) and
  the eyebrow prints `BREAK — BACK AT HH:MM` (via `endsAtText`
  in `MonolithCustomComponents.kt`).

## F6 — The chair: one verb STOP, whole session · M

- Files/functions: `TactileDeckScreen.kt` → Section 5 pinned
  stack, the 72dp `Box` (grace-chip / valve-chip branches,
  `withinGraceWindow`); hero readout for the grace countdown;
  callbacks already present (`onCancelWithinGraceWindow`,
  `onRequestDisarm`).
- Change: replace both chips with one STOP chip filling the full
  72dp zone — tap inside 10s routes to
  `onCancelWithinGraceWindow`, after 10s to `onRequestDisarm`,
  label carries the price (`STOP — USES YOUR 1 BREAK THIS
  WEEK`); grace countdown moves into the hero; the
  `(60S HOLD)` wording dies.

## F7 — Task row → ghost title; sub-steps + QUEUE move to Tasks · M

- Files/functions: `TactileDeckScreen.kt` → Section 2 task
  `Column` (`stepsExpanded` state, title `clickable`, DONE
  key-cap, `QUEUE n` `Text`, sub-step rows). Tasks side already
  owns both: `FocusSequencerScreen.kt` (no change expected).
- Change: title becomes read-only (idle) / title + DONE
  (running — gate the DONE cap on `showRunningForm`); delete the
  sub-step expansion and QUEUE print+door; knock-on dead wires
  `onToggleTaskSubStep`, `onOpenTaskSequencer` are cut in the
  same pass (cascade, not F1).

## F8 — Seam stamps → PARK · S

- Files/functions: `TactileDeckScreen.kt` → `seamMarks` val +
  `ProofStripBand(… stampLeft/stampRight …)` call in Section 5;
  `MonolithCustomComponents.kt` → `ProofStripBand()` (stamp
  params) and `seamMarksFor()` (line 413); `ParkDrawer.kt` →
  `ParkDrawer()` / `ParkBoardRow()` gains the stamp row.
- Change: Deck stops passing/printing both stamps; PARK renders
  them on its board instead.

## F9 — Sentence words plain; misrouted FIX corrected; fault promotes the sentence · S

- Files/functions: `MonolithCustomComponents.kt` →
  `refusalSentenceFor()` (lines 501–525: `BLOCKING IS OFF — TAP
  TO FIX` at lines 505/513 is wired to
  `RefusalAction.FIX_ACCESSIBILITY` for a non-accessibility lack)
  and `jamSentenceFor()` (line 541) wording;
  `TactileDeckScreen.kt` → Section 5 pinned `Column` order +
  `RefusalSentenceSlot` `onAction` routing.
- Change: rewrite the sentence texts plain (form to beat:
  `NOT SURE BLOCKING IS ON — TAP TO CHECK AGAIN`), point the
  misrouted branch at the pane that owns the lack, and move
  `RefusalSentenceSlot` above the slide zone in the pinned
  column when a fault sentence stands.

## F10 — Hero single-press (one tokenised shadow ghost) · S

- Files/functions: `TactileDeckScreen.kt` → hero glyph `Box`
  (three stacked `Text`s: `heroShadowGhost`, `heroCatchGhost`,
  glyph).
- Change: delete the catch-ghost `Text`; the surviving shadow
  ghost takes a scheme token (see F17), one direction; ghosts
  render only for the static span — off when
  `isStopwatchMode` (FLOW) and when `showRunningForm` (ticking
  MM:SS).

## F11 — The card gets its shadow, or stops claiming lift · S

- Files/functions: `TactileDeckScreen.kt` → hero card `Column`
  modifier chain (Section 1); note the currently dead
  `import androidx.compose.ui.draw.shadow` (line 36 — imported,
  never applied; left untouched this shift).
- Change: apply a real 2dp shadow in day mode / tone-only at
  night on the hero card, or delete the lift claim (import +
  comment) if the shadow reads wrong on device.

## F12 — Unlight the utilities (night ink steps down) · S

- Files/functions: `TactileDeckScreen.kt` → Section 3 utilities
  `Row` (`BlockitActionChip` × 2 + END BREAK chip);
  `MonolithCustomComponents.kt` → `inkControlFill()` /
  `onInkControlFill()` and `BlockitActionChip`.
- Change: night fill for the utility chips steps down to
  `#2A2320` (tokenised under F17) so brightness order glyph >
  card > controls > ground holds at sealed night.

## F13 — Key-cap radii registered: 8dp → 12dp · S

- Files/functions: `TactileDeckScreen.kt` → the two
  `RoundedCornerShape(8.dp)` key-caps (FLOW cap's successor
  print — see F2 — and the DONE cap in Section 2).
- Change: both caps to `RoundedCornerShape(12.dp)`; no other
  radius on this screen changes (24dp card / 3dp track stand).

## F14 — Group by silence + fade the scroll's bottom edge · S

- Files/functions: `TactileDeckScreen.kt` → scroll `Column`
  (`Arrangement.spacedBy(12.dp)`, padding 12/8) and its inner
  sections' spacing; a fade modifier on the scroll column's
  bottom edge.
- Change: tighten spacing inside the instrument object, set
  20dp between objects, and fade content out at the scroll's
  bottom edge instead of clipping it.

## F15 — Vertigo moves to the ground · M

- Files/functions: `TactileDeckScreen.kt` → seal-shot driver
  `LaunchedEffect(shot)` + hero card's `graphicsLayer`/`blur`
  modifier (currently applied to the instrument card itself).
- Change: move scale/blur to the page ground (root `Column`
  behind the instrument), peak blur 3dp, same easings and same
  1050ms; the instrument seals in focus, untransformed.

## F16 — Pinned stack collapses into the state-gated proof drawer · L

- Files/functions: `TactileDeckScreen.kt` → Section 5 pinned
  `Column` (`ProofStripBand` + `RefusalSentenceSlot` become
  conditional), derivations `blockCell` / `appsCell` /
  `timeCell` stay as the drawer's content source;
  `MonolithCustomComponents.kt` → `ProofStripBand()` reused as
  drawer body.
- Change: strip + sentence compose only when proof is live
  business (proving / jammed / faulted / one settle beat);
  pinned zone = slide + imprint (+ sentence in fault); TIME/ENDS
  stays on the Deck in all states per the audit's amendment.

## F17 — Tokenise the light (raw hex → scheme tokens) · M

- Files/functions: `TactileDeckScreen.kt` (hero glyph/shadow
  hexes `0xFFEDE3D8`, `0x80000000`, `0x99D8D1CB`, `0xB3FAF6F0`,
  eyebrow shadow `0x66FFFFFF`), `MonolithCustomComponents.kt`
  (5 raw-hex sites), `BlockitCoreViews.kt` (wheel/slide paints);
  tokens land in `ui/theme/Color.kt` / `Theme.kt`.
- Change: every raw hex in those three files becomes a named
  scheme token; no colour value changes, only its address.

## F18 — The Fusion: hero into the wheel hub · M

- Files/functions: `BlockitCoreViews.kt` → `RotaryWheelView`
  (line 371, hub drawing in `onDraw`); `TactileDeckScreen.kt` →
  hero panel (eyebrow, glyph `Box`, progress track) and the
  `displaySeconds` / `progressFraction` derivations.
- Change: hub prints the true span (`totalSpanMinutes`, `+10
  BREAK` sub-line when blocks > 1); at the seal the wheel locks
  in place and the hub becomes the running readout; progress
  becomes ink filling the tick ring (FLOW's ring never
  completes); hero panel, standing eyebrow, progress track, %
  and the fraction derivation die.

## F19 — Receipt dismissal: slip-swipe or PARK/rail only · S

- Files/functions: `ReceiptSheet.kt` → `ReceiptSheet()` (scrim /
  dismissal handling); host wiring in `MainActivity.kt` (line
  781 call, dismissal state at line 307).
- Change: scrim tap no longer dismisses — it shakes the slip;
  dismissal only via slip-swipe or PARK/rail.

## F20 — Print PARK on the rail's unnamed zone · S

- Files/functions: `MonolithRailView.kt` → PARK-zone drawing in
  `onDraw` (zone geometry `RailGeometry.PARK_ZONE_DP` /
  `parkStartDp`, lines 30/43; furniture block ~line 226).
  Host chrome — **outside this cell's Deck-only touch list in
  shift 1; flagged for Rost: this is issue-adjacent, not the
  top status strip (issue #4), but it is not
  `TactileDeckScreen.kt` either.**
- Change: draw the word PARK in the 48dp zone, same print voice
  as the four named stops.

## F21 — Comment truth pass · S

- Files/functions: `TactileDeckScreen.kt` → file header KDoc
  ("12dp universal spacing" vs the actual 24dp-radius / 16-18dp
  card), section banners (numbered 1, 2, 3, 5 — no Section 4),
  the idle-ENDS comment beside `idleTimeCell`.
- Change: renumber or unnumber the sections, correct the header
  spacing promise, and label idle ENDS as planned (or freeze
  its derivation) so comment and code agree (audit §5.5/§5.8).

## F22 — Accessibility semantics on wheel, slide, rail · L · bar debt

- Files/functions: `BlockitCoreViews.kt` → `RotaryWheelView`
  (line 371) and `SlideToStartView` (line 1444);
  `MonolithRailView.kt` → the rail view. (Verified: zero
  `contentDescription` / `semantics` occurrences in either view
  file today.)
- Change: give each Canvas view a name, value and action —
  `contentDescription` + custom accessibility actions /
  `importantForAccessibility` wiring on all three — so the home
  screen is operable eyes-free.

Sequencing (audit §9, kept): F1–F5, F8–F14, F19–F21 are
independent of the restructure and can land on the current room;
F16 + F18 land together; F17 precedes any look touching this
screen. Phase 2 (Vega B, Muede's 05:00 door, Lamp Test) is out
of this map — separate lanes, Ren's word.

---

# F1 EXECUTION LEDGER — shift 1 (the only item implemented)

Method, per parameter: (1) word-boundary grep of
`TactileDeckScreen.kt` split at the signature/body boundary —
count in body must be 0; (2) both call sites inspected — the
argument expression must be a plain value read or an uninvoked
method reference / never-invoked lambda (no side effect in
evaluation); (3) both call sites use named arguments, so
removal cannot shift positional binding of surviving
parameters. All 20 removed parameters passed all three checks.

| # | Parameter (Deck signature line) | Body uses | MainActivity Deck arg (removed) | Test arg in `setDeck()` (removed) | Why safe |
|---|---|---|---|---|---|
| 1 | `studyPreset: StudyModePreset` (L100) | 0 | L474 `studyPreset = studyPreset` | L92 `StudyModePreset.POMODORO_25_5` | Plain state read; the preset the FLOW cap writes is never read here (ESR-08). NOTE: `onSelectStudyPreset` is a different, LIVE parameter (1 body use, the FLOW cap) — kept. |
| 2 | `checklist: List<StudyChecklistItem>` (L101) | 0 | L475 `checklist = checklist` | L93 `emptyList()` | Plain state read; no checklist UI exists in the body. Orphans the `StudyChecklistItem` import — removed. |
| 3 | `completedTasksCount: Int` (L104) | 0 | L478 `completedTasksCount = completedTasks.size` | L96 `0` | `.size` read only; the value is never rendered (QUEUE prints `queuedTasks.size`, a kept parameter). |
| 4 | `totalQueueMinutes: Int` (L105) | 0 | L479 `totalQueueMinutes = totalQueueMinutes` | L97 `0` | Plain read. The same MainActivity variable still feeds `FocusSequencerScreen` (LATER call, untouched) — only the Deck argument is removed. |
| 5 | `selectedProfileName: String` (L106) | 0 | L480 `selectedProfileName = selectedProfileName` | L98 `"Default"` | Plain read; still used by Shield's call in MainActivity (untouched). |
| 6 | `intentionText: String` (L107) | 0 | L481 `intentionText = intentionText` | L99 `"Deep Study"` | Plain read; no intention field renders in the body. |
| 7 | `totalFocusedMinutes: Int` (L108) | 0 | L482 `totalFocusedMinutes = totalFocusedMinutes` | L100 `0` | Plain read; totals are Drawer facts, never rendered here. |
| 8 | `totalInterceptions: Int` (L109) | 0 | L483 `totalInterceptions = totalInterceptions` | L101 `0` | Plain read; never rendered here. |
| 9 | `profiles: List<FocusProfileEntity>` (L110) | 0 | L484 `profiles = profiles` | L102 `emptyList()` | Plain read; profiles' home is Shield. Contributes to orphaning the `FocusProfileEntity` import (with #20) — removed. |
| 10 | `apps: List<BlockedAppEntity>` (L111) | 0 (word-boundary; `appsCellCount`/`appsSplit` are separate LIVE parameters) | L485 `apps = allApps` | L103 `emptyList()` | Plain read; the strip's count comes from `appsCellCount`/`appsSplit`, not this list. Orphans the `BlockedAppEntity` import — removed. |
| 11 | `shortsReelsSniperEnabled: Boolean` (L112) | 0 | L486 `shortsReelsSniperEnabled = shortsReelsSniperEnabled` | L104 `true` | Plain read; the toggle lives on Shield (its MainActivity args at L556+ untouched). |
| 12 | `browserUrlBlockerEnabled: Boolean` (L113) | 0 | L487 `browserUrlBlockerEnabled = browserUrlBlockerEnabled` | L105 `true` | Plain read; same Shield reasoning as #11. |
| 13 | `onToggleChecklistItem: (Int) -> Unit` (L120) | 0 | L499 `viewModel::toggleChecklistItem` | L112 `{}` | Uninvoked method reference — constructing it calls nothing. |
| 14 | `onAddChecklistItem: (String) -> Unit` (L121) | 0 | L500 `viewModel::addChecklistItem` | L113 `{}` | Uninvoked method reference, as #13. |
| 15 | `onDeferActiveTask: (Int) -> Unit` (L123) | 0 | L502 `viewModel::deferTask` | L115 `{}` | Uninvoked method reference; defer lives on Tasks (`FocusSequencerScreen` wiring untouched). |
| 16 | `onExtendActiveTaskOvertime: () -> Unit` (L124) | 0 | L503 `{ viewModel.extendActiveTaskOvertime(5) }` | L116 `{}` | Lambda is created, never invoked by the body — its body never ran from this seam. |
| 17 | `onAddQuickTaskToQueue: (String, Int) -> Unit` (L125) | 0 | L504–506 lambda wrapping `viewModel.addTaskToQueue(…)` | L117 `{ _, _ -> }` | Never-invoked lambda, as #16. Exact multi-line block removed at the host call. |
| 18 | `onOpenCurioVault: () -> Unit` (L128) | 0 | L509 `{ viewModel.navigateTo(MonolithDestination.VAULT) }` | L120 `{}` | Never-invoked lambda; no vault door renders in the body. |
| 19 | `onIntentionChanged: (String) -> Unit` (L129) | 0 | L510 `viewModel::setIntentionText` | L121 `{}` | Uninvoked method reference, as #13. |
| 20 | `onProfileSelected: (FocusProfileEntity) -> Unit` (L130) | 0 | L511 `viewModel::selectAndApplyProfile` | L122 `{}` | Uninvoked method reference; profile selection lives on Shield. With #9, orphans the `FocusProfileEntity` import — removed. |
| 21 | `onSimulateIntercept` | — | — | — | **NOT REMOVED — NOT PRESENT.** The audit (§2) lists it among the 21, but at this commit it exists nowhere: 0 hits in the Deck signature, 0 in the body, 0 at either call site, 0 repo-wide (`grep -rn "onSimulateIntercept" --include="*.kt"` returns nothing). Nothing to cut; flagged so the count is honest: **20 removed, 1 already absent.** |

Kept deliberately, though adjacent: `onSelectStudyPreset`
(LIVE — the FLOW cap invokes it; it dies with F2, not F1);
`appsCellCount`, `appsSplit` (LIVE — the strip/imprint read
them); the dead `androidx.compose.ui.draw.shadow` import
(line 36 — not a host parameter; it belongs to F11's pass).

Post-edit verification (paper, no compile): word-boundary grep
shows 0 occurrences of each removed name in
`TactileDeckScreen.kt`; the Deck call in `MainActivity.kt` and
`setDeck()` in `CascadeDeckRenderTest.kt` pass no removed name;
no other file in the repo calls `TactileDeckScreen(`. The test
file's `import com.example.data.StudyModePreset` also fell
orphaned (its only use was the removed argument) and was
removed with it. Total diff: 66 lines deleted, 0 inserted,
across the three files — pure removal, no surviving line
edited.
MainActivity's own state variables (`checklist`, `profiles`,
`allApps`, …) are untouched — they still feed Shield/Tasks.

— BUILDER, Rost's cell, issue #6, shift 1. DECLARED, uncompiled.
