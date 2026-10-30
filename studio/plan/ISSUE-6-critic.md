# ISSUE #6 — CRITIC · Deck audit verification
## Cell: Rost (lead) · Critic seat · Fri Oct 30, 2026
## Tree: `agent/issue-6` — verified at HEAD `2a1d192` (wave 1) and,
## for the pre-fix state, at its parent `8438f5a` (817e8c7-lineage
## main, DB v12 — MonolithDatabase.kt:25 `version = 12`).
## Method: every audit claim re-derived from the code itself —
## signature/body word-boundary counts run by script, call sites
## traced, derivation functions read whole. Paper only; nothing
## compiled. Boundary obeyed: no strip redesign filed; strip- and
## header-adjacent facts are noted only where Deck truth depends
## on them (they are issue #4's cell's to rebuild).

---

## THE DEAD-PARAMETER COUNT — VERIFIED BY ME, TWICE

Parent `8438f5a`, `TactileDeckScreen.kt` signature (then lines
96–185): **61 parameters. Dead in the body: 20** — studyPreset,
checklist, completedTasksCount, totalQueueMinutes,
selectedProfileName, intentionText, totalFocusedMinutes,
totalInterceptions, profiles, apps, shortsReelsSniperEnabled,
browserUrlBlockerEnabled, onToggleChecklistItem,
onAddChecklistItem, onDeferActiveTask, onExtendActiveTaskOvertime,
onAddQuickTaskToQueue, onOpenCurioVault, onIntentionChanged,
onProfileSelected. Each: 0 word-boundary occurrences in the body
under the signature. Every other parameter occurs ≥1 time.

HEAD `2a1d192` (wave 1): signature lines 93–162, **41 parameters,
0 dead.** The cut removed exactly the 20 above; both call sites
(MainActivity.kt:470, CascadeDeckRenderTest.kt:87) were cut with
it and pass no removed name. **F1 is closed, correctly, on paper.**

The audit's 21st name never existed — see C1.

---

## CHARGES

### C1 — The audit's headline count is wrong: 21 dead wires → 20. The 21st is a ghost the audit invented.
AUDIT-deck.md §2 and §9-F1 list `onSimulateIntercept` among the
21. At parent `8438f5a` it appears **nowhere**: not in the
signature (61 names enumerated above), not in the body, not at
either call site, not repo-wide (`grep -rn "onSimulateIntercept"
--include="*.kt"` = 0 hits). A grep campaign by "three independent
seats" (§2) cannot have produced a name the tree does not contain;
the list was not, as claimed, "identical in substance" across four
greps of this code. The cell's own builder-map already caught
this honestly (ISSUE-6-builder-map.md:301 — "20 removed, 1 already
absent"); the innovator's spec still repeats the false number
three times (ISSUE-6-innovator.md:35, :164, :448 — "21 dead
parameters", "F1 cuts 21"), and Rost's wave-1 commit message does
not repeat it. Rost's cut (20) is right; the audit's number and
every document still quoting it are wrong. Fix the number
everywhere it is cited as the F1 receipt.

### C2 — §5 charge 4 / F9: the "misrouted FIX" is aimed at a correct branch and a dead branch. The misroute, as charged, cannot happen in production.
Builder-map F9 (ISSUE-6-builder-map.md:109–114) targets both
`BLOCKING IS OFF — TAP TO FIX` → `FIX_ACCESSIBILITY` sites in
`refusalSentenceFor()` (MonolithCustomComponents.kt:501–522) as
"wired to FIX_ACCESSIBILITY for a non-accessibility lack."
- **Line 505 is correctly routed.** Its branch condition is
  `!warrant.a11yBound` — accessibility *is* the lack, and the pane
  is the one that owns it.
- **Line 513 is unreachable from any warrant production can
  build.** `WatchStore.deriveWarrant` (WatchStore.kt:296–315) —
  the sole production constructor, via `standingWarrant()`
  (MonolithRepository.kt:629) — yields OFF **iff** `!a11yBound`
  or `usage == DENIED`. Both are pre-empted above line 513:
  `!a11yBound` returns at 505, `usage == DENIED` returns the
  USAGE sentence at 509. Line 513 can only be reached by a
  hand-built `Warrant(...)` — which exists only in tests
  (SliceDGestureAndStripTest.kt:124, TruthCar3InterceptTest.kt:97).
Fremd C3 was true of some earlier wiring or of the table read in
isolation; at this commit the branch it convicts is dead code,
and F9 as mapped spends its S on it. Strike the charge as
charged; the live defect in the same table is C3.

### C3 — The live label/gesture lie in the sentence table, which the audit and F9 both miss.
MonolithCustomComponents.kt:511 — `usage == UNSET` returns
`CAN'T CHECK BLOCKING — TAP TO RETRY` with
`RefusalAction.RETRY_CHECK`. The Deck's handler
(TactileDeckScreen.kt:1058–1066) intercepts exactly that case:
`if (deckWarrant?.usage == UsageAccess.UNSET) onFixUsageAccess()`
— the tap opens the Usage Access **settings pane**, it does not
retry anything. The words promise a re-check; the gesture
performs a settings trip. This is the same species of lie the
audit convicts at the valve (§5 charge 3) — reachable in
production, on the Deck's own fault path, unfiled by all six
seats and unmapped in F9. F9's plain-words rewrite must fix the
*action* or the *words* here, not at C2's branches.

### C4 — §5's acquittal of running TIME/ENDS is false as stated. There is no stored end; the running ENDS can slide.
The acquittal (§5, ACQUITTALS): "TIME/ENDS running is a pure
function of a stored end." No stored end exists.
`FocusSessionEntity.spanRemainingSeconds` is a derived getter —
`spanTargetSeconds − spanElapsedSeconds`, arithmetic over the
ticker-written `elapsedSeconds` column (MonolithModels.kt:427–429
and its span getters above it). The Deck's running cell
(TactileDeckScreen.kt:502) prints
`endsAtText(nowFrame + spanRemainingSeconds × 1000)` at PASS,
where `nowFrame` is the Deck's **own** minute clock
(TactileDeckScreen.kt:330–337, re-derived every minute boundary,
independent of the shield service). While the service ticks, the
sum is roughly constant. The instant the ticker stalls — the
exact failure a truth strip exists to announce — ENDS advances
one minute per minute at PASS density and nothing on the Deck
can tell. `endsAtText`'s own comment (MonolithCustomComponents.kt:
424–426) claims "the shipped ever-sliding finish line dies here";
the function is pure, its running call site is not. The audit
struck the idle slide (§5 charge 5 — verified, §A below) and
acquitted the running form on a mechanism that is not in the
code. Acquittal struck; charge stands in its place, scoped:
running ENDS is honest only while the ticker lives, and carries
no freshness check on the row it derives from.

### C5 — THE HEADLINE TRUTH CHARGE (§d): the Deck prints enforcement from a warrant that never proves enforcement.
Two counts, one root — the word BLOCK / WILL SHUT / HELD claims
the machine is *holding apps*; the verdict behind the print
proves *permission plumbing* only.
- **(a) The enforcer is not a conjunct.** `deriveWarrant`
  (WatchStore.kt:290–309): LIVE = a11yBound ∧ stampedThisResidency
  ∧ usage ALLOWED. `shieldProtecting` is carried in the struct
  (WatchStore.kt:62–72) and expressly excluded from the state
  (comment :293–295: "the shield starts with the session, so
  requiring it first would be circular"). The circularity is real
  **at idle, before the arm**. It expires at t=0 — and nothing
  re-checks it after: the Deck reads `shieldProtecting` **nowhere**
  (sole occurrence in the file is the default preview warrant's
  `= false`, TactileDeckScreen.kt:130), `seamMarksFor` ignores it,
  and the seal freeze captures `standingWarrant().state`
  (MonolithViewModel.kt, SealFreeze captures) without it. A dead
  shield service under a live a11y bind moves **no print on this
  screen**: BLOCK stays ON, the imprint stays HELD, the frozen
  Guard line stays whole. §11 of the audit declined to re-audit
  the warrant "acquitted here where the Deck prints it" — that
  acquittal-by-scope is precisely the hole: what the Deck prints
  *is* the warrant, and the warrant does not contain the fact the
  words assert.
- **(b) The running BLOCK cell degrades asymmetrically.**
  TactileDeckScreen.kt:484–487: running, the cell follows the live
  wire **only** to OFF; a mid-session fall to UNPROVEN (stamp lost,
  usage re-read UNSET) leaves the frozen ON standing, unmarked.
  The imprint's tense suffix has the same blind spot —
  `· AS SEALED` fires only when `wireNow == OFF`
  (MonolithCustomComponents.kt:450–478). One tense per frame was
  the car's own law (UNA2-04); UNPROVEN is the tense it forgot.
Filed as a Deck-print charge, not a machinery redesign: the fix
this cell owns is what the Deck *prints* when the wire cannot
prove holding (UNKNOWN density exists and is already the idle
vocabulary). The warrant's conjuncts are Truth-Cars' ground.

### C6 — §4's census headline is impossible on one face: six renderings of the blocking fact → five, maximum.
"Printed six times on one face" (Dens, adopted §4). The six
renderings exist in the repertoire; they never coexist.
The refusal sentence and the idle imprint tenant are mutually
exclusive: the idle tenant prints only on a freshly LIVE wire
(MonolithCustomComponents.kt:474–478, `warrantState != LIVE →
null`), while every standing sentence branch requires a non-LIVE
verdict or a usage fault that `deriveWarrant` maps to OFF —
never LIVE-with-sentence. Running, the Deck forces the sentence
to null outright (TactileDeckScreen.kt, sentence `when`:
`isSessionRunning -> null`). Count per face: idle-healthy 5
(Guard, BLOCK, seam ×2, imprint), idle-fault 5 (Guard, BLOCK,
seam ×2, sentence), running 5 (Guard frozen, BLOCK, seam ×2,
imprint). Dens's dialect point (four vocabularies, §4) survives;
his simultaneity number does not. Correct the census before it
is quoted at merge.

### C7 — §4's "FLOW prints three times" cannot be reproduced on this screen.
On the Deck face, the word FLOW prints at most **twice**, idle
only: the hero (TactileDeckScreen.kt:618) and the key-cap
(:705). Running, it prints **zero** times — the hero is the
MM:SS readout, TIME reads `NO END`. No third FLOW print exists
in the file (grep: remaining hits are comments and the
`STOPWATCH_FLOW` enum at :699). The "two doors" half stands
(cap + wheel-below-10); the three-prints half is Fremd/Dens
counting something that is not on this face at this commit.

### C8 — Vega T-7 (§3, slide): the words are constant; at 90% travel they are also invisible. The charge as worded is false.
The string is state-blind — `WORDS_START` in every mode but
MESH (BlockitCoreViews.kt:1787, constants :2407–2408). But the
draw applies a travel fade, `(1f − displayed × 1.3f)` clamped,
during DRAG and SEAT-from-drag (:1788–1792), written into the
paint alpha at :1797. The words reach alpha 0 at ≈77% travel;
at the charged 90% there is nothing on the glass instructing
anyone to start sliding. What survives of T-7: in MESH and at
REST the text never learns state, and the fade — not wording —
is the only travel feedback. Price the F-item on that, not on
the 90% claim.

### C9 — The audit's stated source basis is stale against both commits this cell works on.
§2: "TactileDeckScreen.kt whole (1,069 lines)". The file is
**1,148 lines at parent `8438f5a`** and **1,125 at HEAD
`2a1d192`** (`wc -l`). The crew read a Deck ~56–79 lines shorter
than either tree — which is the simplest explanation for C1's
ghost parameter and C7's phantom third print: parts of this
audit describe a Deck that is not in the repo. Every §9 F-item
priced from that read should be re-priced against the current
file before its lane starts; Rost's triage spot-check
(ISSUE-6-triage.md — studyPreset at :100, totalInterceptions at
:109) matches the parent tree, not the audit text, and the
triage is the document the cell should trust for line truth.

### C10 — §3/F5 overstates the break gap: the break is already on the eyebrow; F5's real delta is narrower.
The audit's eyebrow ruling and F5 proceed as if a Safe Break is
unprinted until fixed. At both commits the eyebrow already
prints it: `BREAK · ${(safeBreakSecondsRemaining + 59) / 60} MIN`
(TactileDeckScreen.kt:593, first branch of the eyebrow `when`) —
the minutes left, ceiling-rounded, while §6's 1 AM charge ("the
break's one asked question… is the smallest print on the card,
10sp eyebrow") simultaneously relies on its existence. Both
cannot frame the fix: F5's owed work is the hero becoming the
break countdown and the `BACK AT HH:MM` form — not creating a
break print. (The same eyebrow's standing branches are verified
as charged: `SESSION RUNNING` :594, `TOTAL` :595, no routine-born
branch in the `when` — F4's routine-naming half stands.)

### C11 — The slide assumes LIVE when unwired — the one fake-proof default left standing in the Deck's contract.
BlockitCoreViews.kt:1458–1459 (comment) and :1728:
`warrantProvider?.invoke()?.state ?: WarrantState.LIVE` — "Null =
no provider wired (preview): the wire reads LIVE so the surface
still works." Production wires it (MainActivity.kt:516,
`viewModel::armWarrantNow`), so today this is a loaded default,
not a live lie. But it inverts the house law the Guard line
exists to state (GuardLine.kt:14–19: unmeasured prints *nothing*;
a fabricated 0/4 was "exactly the claim this state exists to
prevent"). Any future host, harness, or refactor that seats the
slide without the provider gets a control that arms against an
unmeasured wire and refuses nothing. F1 cleaned the Deck's dead
wires; nobody's F-item owns this live-by-default one. Filed for
the record: default should be UNPROVEN (refuse-in-hand already
exists for OFF; UNPROVEN must not travel further than OFF).

---

## §A — VERIFIED STANDING (audit right; fixes still owed at HEAD)
Re-derived at `2a1d192`; do not re-litigate these in the cell.

- **F2 substance** — FLOW cap is a live control whose *preset* is
  theatre: cap click (TactileDeckScreen.kt:695–700) fires
  `onSelectStudyPreset(STOPWATCH_FLOW)` **and**
  `onDialMinutesChanged(5)`; the VM's `selectStudyPreset`
  (MonolithViewModel.kt:1459–1464) stores the preset and sets
  dial=5, blocks=1; the engine arms on `durationMinutes < 10`
  alone (MonolithRepository.kt:1330) and nothing on the arm path
  reads `_studyPreset`. Audit's rhetoric ("wired to a ghost")
  overstates — the cap *works*, via the dial side-effect — its
  charge (the preset wire is a corpse; mode must be print, not a
  second door) stands. Note `studyPreset` the parameter is cut;
  `onSelectStudyPreset` is live and was correctly kept.
- **F3** — progressFraction (TactileDeckScreen.kt:304–320):
  FLOW/frozen-FLOW over an invented 1200f, floored 0.05; timed =
  remaining/target (born full, draining), floored 0.03; % text
  printed at :748. All three counts stand verbatim.
- **F4 (non-routine half)** — idle `TOTAL` / `SESSION RUNNING`
  stand at :595/:594; the fake day emboss shadow stands (:601,
  `0x66FFFFFF` 1dp offset shadow, transparent at night).
- **F6** — the chair swap stands: GRACE chip (:1009) and VALVE
  chip (:1024) alternate in the same 72dp zone (:947 box), each
  chip only 58dp tall (:1019, :1030); the valve label
  `(60S HOLD)` names the overlay's gesture
  (DisarmChallengeOverlay), while the chip's own tap is
  `onRequestDisarm` — opens the challenge, holds nothing.
- **F7** — task row stands as charged: DONE is a working verb at
  idle (:812, clickable whenever `!shotActive`, no running-state
  gate), QUEUE n is a tap-door at :821, sub-steps expand in-row.
- **F10** — triple print stands: shadow ghost −1dp, catch ghost
  +1dp, glyph (TactileDeckScreen.kt:632–650), in all states
  including FLOW and the ticking readout — no state gate exists.
- **F11** — the hero card claims lift with border + tone only
  (:551–558); `import androidx.compose.ui.draw.shadow`
  (:36) has **zero** call sites in the file. Dead import stands.
- **F12** — night brightness inversion stands in code values:
  `inkControlFill()` night = `0xFF4A423C`
  (MonolithCustomComponents.kt:315) vs the hero card's night
  `surfaceBright` = `0xFF362C27` (Color.kt:36) — controls
  out-light the card they serve.
- **F13** — key-cap radii 8dp stand (FLOW :695, DONE :802).
- **F15** — the vertigo is applied to the instrument itself:
  scale + blur chain on the hero card's Column
  (TactileDeckScreen.kt:560–578; blur peak `6f·4f·t(1−t)`).
- **F19** — receipt scrim tap dismisses (ReceiptSheet.kt:70,
  scrim `clickable(onClick = onDismiss)`); no shake exists.
- **F20** — the rail's PARK zone prints no word, by filed design
  (MonolithRailView.kt:79–80; WORDS :34 lists four stops only).
- **F21** — sections run 1, 2, 3, 5 (TactileDeckScreen.kt:544,
  758, 874, 920 — no Section 4 exists); file header (:85–91)
  promises "12dp universal spacing" over a 24dp-radius card
  (:551) at 16/18 padding (:578). Comment rot stands.
- **F22** — accessibility debt stands, total: zero hits for
  `contentDescription`, `semantics`, or
  `importantForAccessibility` across TactileDeckScreen.kt,
  BlockitCoreViews.kt (wheel :371, slide :1444), and
  MonolithRailView.kt. Canvas views, no names, no values.
- **§4 tax arithmetic — Dens acquitted exactly.** Header 48
  (47dp row, MonolithCustomComponents.kt:266, + 1dp hairline
  :297–302) + rail 52 (RailGeometry HEIGHT_DP, MonolithRailView
  .kt:26) + pinned 216.5 = strip 53 (MonolithCustomComponents
  .kt:659) + 6 + slide zone 72 (TactileDeckScreen.kt:947) + 8 +
  sentence 22 (MonolithCustomComponents.kt:747) + imprint 39.5
  (1+2+0.5+6+20+4+6, ImprintSlot) + pinned column padding 16 =
  **316.5dp**, and reserved emptiness 22 + 39.5 = **61.5dp**.
  Both numbers reproduce to the half-dp at HEAD.
- **§5 charge 5 (idle ENDS)** — stands: idle cell feeds
  `endsAtText(nowFrame + totalSpanMinutes × 60_000)`
  (TactileDeckScreen.kt:362) beside a comment claiming the finish
  line never slides; it advances every minute by construction.
- **HELD's missing noun (§3)** — stands: chip prints `HELD` +
  bare count badge (TactileDeckScreen.kt:891–892); the counted
  thing (captured notifications) is named nowhere on the chip.
- **Burn-in exposure (§6)** — stands as a code fact: the Deck
  contains no burn-in shift for its static 72sp readout (grep
  `burnIn` in TactileDeckScreen.kt = 0), while the AOD surface
  is fed a figure-8 step from the host (MainActivity,
  `burnInFigure8MinuteStep`). Position-fixed triple print for a
  session's whole life is in F10's blast radius; the OLED count
  itself is a device question, not settled here.
- **Strip-adjacent note for #4's cell (no redesign filed):**
  the frozen Guard line (§5 charge 7) is verified — while a
  session is active, `guardLinePrint` returns the seal-frame
  capture (MonolithViewModel.kt:663–669) and the header prints
  `guardLine.text` bare (MonolithCustomComponents.kt:279–294),
  with no tense mark of the kind the imprint already owns
  (`· AS SEALED`). Ownership sits with the header/#4 lane;
  recorded here because the Deck's truth depends on it.

---

## STALE / WRONG — SUMMARY FOR THE MERGE RECORD
- **Wrong:** dead-wire count 21 → **20** (C1); §5.4 misroute as
  charged (C2); six-on-one-face census (C6); FLOW ×3 prints (C7);
  slide-words-at-90% (C8); running-ENDS acquittal (C4).
- **Stale:** the audit's source file (1,069 lines vs 1,125/1,148
  in the repo — C9); F5's premise that the break is unprinted
  (C10).
- **Already fixed at HEAD:** F1 only — wave 1 (`2a1d192`), cut
  exactly right. Every other F-item above was re-verified as
  still standing in the current code; none is stale, none is
  already done.
- **Missed by the audit entirely:** C3 (RETRY words, settings
  gesture), C5 (enforcement never in the warrant the Deck
  prints), C11 (slide's LIVE-by-default).

— CRITIC, Rost's cell, issue #6. Charges C1–C11 filed with
evidence; §A acquittals are binding on my own later passes.
Nothing compiled. Rost commits.
