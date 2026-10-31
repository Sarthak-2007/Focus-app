# ISSUE #13 — SESSION ROOM MERGE NOTES
Bauer (builder, Wacht's cell) — Oct 30, 2026. Lane closes
DECLARED: nothing compiled, nothing committed, nothing pushed,
nothing posted. Base = main (batch-9 tree, DB v12 — unchanged).
Lane source = builds/flow-clock/Focus-app (trunk-era).

## FILE DELTA MAP — as actually used

| File | Disposition |
|---|---|
| `ui/DeskClockSettle.kt` | LANE-ONLY → ported as a new file, then amended in place for V1-as-amended (three-state verdict, HELD suppression + duty cycle, wake re-arm, A-10(v) surrender deadline as pure functions). |
| `ui/components/AirlockAndDisarmOverlays.kt` | Hand-merged: ONLY the desk-clock composable + its print derivations were taken from the lane (819 main / 838 lane). Main's Truth-Car-3 hunks elsewhere in the file (peek cooldown prints, airlock session line, capture body rule, primer text) are main's newer work and were NOT overwritten. A-1…A-7 then applied on top of the merged face. The overlay's keep-awake `DisposableEffect` is DELETED (A-10(v) move). |
| `ui/MonolithViewModel.kt` | Hand-merged: lane's settle integration (machine field, process birth, interaction stamp, ticker beat, hand-decision on toggle) inserted into main's newer VM (Truth-Car-3 ticker/finish work untouched). Plus A-8's gate site, A-9, A-10(i)/(ii)/(iv)/(v) machinery, A-4 cause stamping, E-1 veil/Landing state. |
| `MainActivity.kt` | Main's file + wiring only: `onUserInteraction` override → VM stamp; keep-awake flag applied from the VM flow; overlay gains entryCause / exitIsFinish / surrenderFade params; E-1 veil + Landing dim composed over the Deck; receipt dismissal (scrim AND nav/PARK effect) now writes through via `viewModel.dismissReceipt` (A-10(iii)). |
| `data/ReceiptState.kt` | Identical lane↔main (verified). Amended: `finishPulses` stand-down → false (A-10(i)); new pure helpers `isRoutineBornKind`, `receiptCliffExempt` (A-10(ii)/(iv)). |
| `data/ReceiptFiledStore.kt` | NEW. The one persisted filed-marker (A-10(ii)/(iii)): SharedPreferences + `commit()`, UndoDeleteStore's pattern. A guard_settings column would have been a DB change; this merge carries none (DB stays v12). |
| `data/MonolithRepository.kt` | Main's file + A-10: `publishReceipt` writes the filed marker for routine-born kinds in the publish act; `rehydrateReceipt` refuses a filed session; `markReceiptFiled` / `isReceiptFiled` doors for the VM. |
| `service/SessionSealTransition.kt`, `service/WatchStore.kt` | DIFFER lane↔main and MAIN IS NEWER (batch-9 / Truth-Car-3 content: sound-fact resync, test seams, warrant staleness). Verified the diffs carry NO session-room hunks — left entirely untouched. |
| `ui/ArmProvingController.kt`, `data/ReceiptState.kt` (pre-amendment) | Verified identical lane↔main — untouched / amended only as above. |
| `app/src/test/java/com/example/DeskClockFlowTest.kt` | NEW in main (lane-only test): ported + adapted (see below). |

## A-1 … A-12 — DISPOSITIONS

| Item | Disposition | Where |
|---|---|---|
| A-1 block line | MERGED | `deskClockBlockLine` — state-only (suppressed on break, stopwatch, single-block), `BLOCK k OF n · UNTIL HH:MM` for k<n from the per-block remaining, final block figureless. |
| A-2 descent completed | MERGED | `deskClockPrintAlphas` (pure): hero+rule 0.55→0.36 by the NAMED `spanElapsedFraction`; number lines floor 0.50; exit floor 0.40; restore-on-raise = 60 s suspension stamped at the raise (VM interaction stamp + composition raise stamp); DOOR INVARIANT pinned as a comment + `DOOR_INVARIANT_RATIO = 1.10f`, kept by construction (`exit = max(0.40, hero × 1.10)`, capped at the standing 0.58). |
| A-3 dawn ramp | MERGED | Same pure function: final 60 s lerps descended alphas back to standing, full at T-0. |
| A-4 arrival/exit cause table | MERGED | `DeskClockEntryCause` carried from the VM (settle stamp discriminates watched <10 s / unwatched; cold-open → SEEDED; chip → HAND). Overlay animates ground and print separately per the table (260A/+140·520C; 180A/+180·420B; 180A/+180·260A; SEEDED no fade). Exit dismissal 180 EasingThrow together; exit finish: print 260A out, ground holds ≈ detention then 260A as the receipt fades in. Approximation named: the ground's hold is a fixed 1200 ms (Detention's held-frame scale), not a live read of the detention state. |
| A-5 banner slot height | MERGED | While running, the banner slot is a fixed 62 dp box (≈15 sp × 2 + 12 dp vertical padding), top-aligned, transparent when the banner is absent; idle wears no slot. |
| A-6 seats | MERGED | Top-line ENDS seat DELETED; top line is the constant `TAP ANYWHERE TO EXIT`. `deskClockSubLine`: timed `NOW HH:MM · ENDS HH:MM`, last minute ENDS alone, stopwatch `NOW HH:MM` alone, idle none. |
| A-7 tear rule | MERGED | Tear fraction `coerceIn(TEAR_HAIR_FRACTION, 1f)`; rule rebuilt as a Canvas (solid spent / dashed attached); notches 3 dp, full-height, ground-black at block-boundary fractions. Verified: no `NightColorSurfaceVariant` bed remains under the rule — the old capsule + its bed are deleted with it; the gaps draw true black over the face's true-black ground. |
| A-8 idle banner gate | MERGED | The banner derives only inside the `running` composition branch (the derivation's call site) — the VM's queued-task fallback can no longer name a non-running session on the idle face. |
| A-9 silent clock path | MERGED | `toggleAlwaysOnDeskClock`: `playMechanicalLatch` deleted on this path only, both ways; hand-decision + interaction stamp added. All other latch calls untouched. |
| A-10 stand-down | MERGED (i)–(v) | (i) `finishPulses(ROUTINE_SLEEP_STANDDOWN) = false`; the VM gates the WHOLE ceremony launch (E0 knock, E700 pulse, E880 pair) on the kind — gate on the calls, so audio + haptic die together. (ii) routine-born receipts born-filed: `publishReceipt` writes the marker, `emitReceipt` and `rehydrateReceipt` refuse filed/routine-born slips; the record (№, row) survives. (iii) `dismissReceipt` writes the same persisted fact; MainActivity's scrim dismiss and nav/PARK dismissal both route through it — the `remember{}` dismissal is dead. (iv) branch U: `receiptCliffExempt` kinds (stored-end / routine) bypass the 10-min cliff; manual kinds keep it. (v) keep-awake ownership moved to the VM (`deskClockKeepAwake` flow, applied by MainActivity); surrender deadline = max(settle, last interaction, last charger disconnect) + 10 min still on battery, evaluated on the ticker; plug-in before the deadline cancels (charging branch clears the fade and holds), after it does not resurrect; at deadline − 520 ms `deskClockSurrenderFade` drives the overlay's 520 Curve C print fade to 0, then the flag clears into OLED black and the session is marked yielded; a wake interaction re-raises SEEDED and RE-ARMS the sampler (`noteWakeReRaise`). Named approximation: "true-off" is keyed to the designed surrender deadline (the earlier horizon), not to a live read of `Settings.System.SCREEN_OFF_TIMEOUT` — no such read existed in the tree to reuse. |
| A-11 pill emissaries | CROSS-LANE | Pastille's pill merge owns it (one derivation, span number, one break sentence). NOT implemented here. Recorded binding on that merge; the room side speaks the cured §C-3 sentence — as corrected by Wave-2 (Salz C-18): `BREAK — PEEK APPS OPEN · UNTIL HH:MM`, the end DERIVED at the reading instant (now + the row's remaining break seconds; no stored break-end field exists). |
| A-12 conditionals | MERGED (answered in code) | C-1 and C-2 answered at `deskClockEndsAtMillis` / `deskClockBannerText` and below. |

## A-12 — THE ANSWERS, IN WRITING

**C-1 (face-ENDS vs routine windowEndMillis):** They are NOT
the same derivation in the pre-merge code. Face-ENDS (lane)
derived from `nowWallMillis + spanRemainingSeconds` — span
arithmetic (block targets + breaks, MonolithModels'
`spanTargetSeconds`). A routine row's stand-down, however,
keeps its FROZEN `windowEndMillis` (the schedule snapshot
stamped at materialisation; `finishKindOf` and `derivedEndWall`
both read it). A routine span's target CAN differ from its
window (window = schedule, span = blocks + 600 s breaks), so
the lane's ENDS could promise an end the stand-down would not
keep. Ruled form implemented: for routine-born rows
(`routineId != null && windowEndMillis > 0`) ENDS derives from
`windowEndMillis`; for hand rows, from now + span remaining.

**C-2 (L7 fallback floor):** The chain was task → intention →
absent (lane `deskClockBannerText`). The row DOES carry a
truer floor: `profileName`, stamped on every session row at
birth from the seal's loadout snapshot (hand rows wear the
loadout's name; routine rows wear `Routine: <name>`). The
chain is now task → intention → routine/loadout name (prefix
stripped) → absent. Anonymity stands only when even
profileName is blank, which no arming path in this tree
produces — priced and named here rather than silent, per the
audit's terms.

## KLINGE 1–19 — RECONSTRUCTED DISPOSITIONS

The audit cross-references Klinge's list by item number and by
its V/M/L/§C/K codes; the numbering below is reconstructed
from those cross-references (the audit itself pins: item
5 ≈ sub-line silence, 8 = tear fraction, 10 = V5, 14 = V4,
17 = exit throw, 19 = finish-seam pin). Where the mapping is
an inference it is marked (inf.).

| # | Code | Disposition |
|---|---|---|
| 1 | M-1 arrival (ground 260A / print 520C at +140) (inf.) | MERGED — as A-4's UNWATCHED row. |
| 2 | L1 hero grammar (span figures, minutes:seconds) (inf.) | ALREADY-IN-MAIN via the lane port — `deskClockHeroText` reads the span; table-tested. |
| 3 | L2 hero label binding (K-8 family) (inf.) | MERGED via lane port — THE TIME NOW inseparable above the idle hero; NOW wears its label in-string. |
| 4 | §C-1 ENDS in the sub-line (inf.) | MERGED — A-6(ii) seats it; last-minute promotion included. |
| 5 | sub-line silence for stopwatch/idle ENDS | MERGED — A-6(ii): stopwatch `NOW HH:MM` alone; idle has no sub-line. |
| 6 | §C-3 break sentence (UNTIL / PEEK APPS OPEN) (inf.) | MERGED — `deskClockBreakLine` cured form; the lane's `DOORS OPEN · n MIN LEFT` superseded by the audit's §C-3. |
| 7 | L4 invented numbers' deaths (dial hero, 0.65 progress) (inf.) | ALREADY-IN-MAIN via lane port — idle hero is wall time; fabricated progress constant gone. |
| 8 | tear fraction (item 8) | MERGED — A-7(i) adds the upper clamp the audit required. |
| 9 | V3 notches (inf.) | MERGED — A-7(ii): 3 dp, full-height, ground-black gaps. |
| 10 | V5 block line | MERGED AS AMENDED by A-1 (state-only, UNTIL, final figureless) — the lane's `n MIN LEFT` form is STRUCK by Nox's derivation proof, recorded in the audit. |
| 11 | L5 fake buttons' deaths (inf.) | ALREADY-IN-MAIN — no verb was added to the read face by this merge; the face's only verb remains the tap. |
| 12 | V2 persisted dismissal (clock) (inf.) | ALREADY-IN-MAIN via lane port — the settle machine's dismissal bit (in-memory by §2.2's stated design); A-10(iii) extends the same law to receipts with a persisted fact. |
| 13 | M-4 keep-awake + surrender (inf.) | MERGED AS AMENDED by A-10(v) — ownership moved to the VM; designed surrender form implemented. |
| 14 | V4 descent | MERGED AS COMPLETED by A-2 (floors, named variable, restore-on-raise, door invariant). |
| 15 | L6 banner derivation (task → intention → absent) (inf.) | MERGED via lane port, EXTENDED by A-12/C-2's loadout floor. |
| 16 | L7 fallback chain (inf.) | MERGED AS ANSWERED by A-12/C-2 (see above). |
| 17 | exit dismissal 180 throw | MERGED — A-4's exit-dismissal row. |
| 18 | M-6 stand-down question (does a routine receipt stand + pulse?) (inf.) | ANSWERED YES from the code by the audit; cured by A-10 — MERGED. |
| 19 | finish-seam pin (no brighter surface between clock and receipt) | MERGED AS EXTENDED by A-4's exit-finish row (motion pin → light pin). |
| — | V1 settle discriminator | MERGED AS AMENDED (E-2, Ren-ratified): ~50 Hz SENSOR_DELAY_GAME only while armed-undecided; 7–14 Hz band energy (`deskClockTremorVerdict`, pure); REST raises at once; HELD suppresses both paths + duty-cycles 6 s/30 s; UNKNOWN/sensor-absent take the 20 s backstop alone; wake re-raise re-arms. The VM's estimator is a detrended-magnitude running variance — honest bench tuning of its noise floor (0.02) is §7 debt 9, still owed on hardware. |
| — | V6 (a face for the night return) | STRUCK — Ren struck it; E-1 implements choreography only (veil + Landing), no face. lockNow also NOT implemented (Ren's lean: veil + Landing without it for this merge). |
| — | §C-2 exit line | UNAMENDED per E-4 — the top line is the constant exit verb, no clause added. |
| — | K-3 (no phase animation on the rule) | HELD — the tear rule is a static Canvas draw per frame; no phase/dash animation exists (the dash is a path effect on the attached segment, not an animated phase). |
| — | K-6 (instant swaps) | HELD — A-5 charges the banner slot's height only; swaps remain instant. |
| — | M-9 / remaining M-codes not cross-referenced by the audit | NOT RECONSTRUCTIBLE from the audit alone — no disposition claimed; flagged for Wecker/Wacht to map from mail/wecker/0058 before this table is called complete. |

## E-1 / E-2 — AS RATIFIED

E-1 MERGED (veil + Landing, no lockNow): VM sets `nightVeil`
on any routine-born finish; MainActivity composes the 0.80
black scrim until first touch (`unveilNightReturn`), under it
the Landing dim (Deck at the room's light law) which the first
verb blooms to full over 520 Curve C via the interaction
stamp. E-2 MERGED as V1-as-amended above.

**CORRECTION (wave-3, Wecker — the paragraph above
OVERCLAIMS, and the record is amended, not silently
rewritten):** E-1 merged IN FORM, defective IN ACT. What the
wave-1 text above describes as landed was, on the merged
code: a veil gated (after wave-2's C-22) on the stand-down
kind alone but set for witnessed AND stale cold discoveries
alike; TWO uncomposed scrims whose sum flashed full Deck
light for ~300 ms at the unveil (Daemmer DAE-6 / Hauch H-1);
a Landing that existed ONLY behind the veil — a night hand
dismissal landed on the full-light Deck (DAE-5 / Ruhe R-1);
a bloom that fired on ANY interaction via
`noteUserInteraction`, not on the first true verb (DAE-7 /
Ruhe R-3); a veil composed OVER the clock, cutting the
finish print's ruled fade at E0 (Hauch H-2 / Glanz G-4); and
a Landing weight applied over whatever scheme the Deck
happened to wear — day, on the stand-down path (Ruhe R-4).
The wave-3 disposition below replaces this paragraph's
claim: E-1 is cured in wave 3 as ONE scrim (see item 1
there). E-2's sentence stands as written.

## PROOF DEBTS (§7) — STATUS

Discharged in code: debt 10 (state × figure × expected-string
table inside DeskClockFlowTest). Still owed on the merged
build (unchanged by this lane): debts 1–9 (frames, captures,
Vega's bench incl. the braced/lap traces before any V1
threshold is called tuned) — none can be discharged without
compiling and a phone; this lane is DECLARED, uncompiled.

## COULD NOT HONESTLY MERGE / APPROXIMATIONS ON THE RECORD

1. A-11 — cross-lane by the brief; recorded, not implemented.
2. Klinge items whose codes the audit never cross-references
   (M-9 et al.) — not reconstructible; no claim made.
3. A-4 exit-finish ground hold: fixed 1200 ms, not a live
   detention read (named at the code site).
4. A-10(v) "true-off": keyed to the designed surrender
   deadline, not a live system screen-timeout read (named at
   the code site).
5. V1's noise floor is an estimator constant, untuned —
   §7 debt 9 stands.
6. ReceiptFiledStore is a new store, not a guard_settings
   column: the audit's "beside the standing pointer" is met
   in fact (one persisted marker, two writers) without a DB
   change — if Wacht wants the column instead, that is a
   v12→v13 migration decision for Ren, not this lane.

## WAVE-3 CURE PASS — DISPOSITIONS (Wecker, cure-builder)

Base for this pass: the wave-1 merge + wave-2 cures committed
at e155802 (CI-green), plus the in-place finisher cures found
UNCOMMITTED in the working tree and built on, never reverted:
Politur P-1/P-2 (hero label in every state; the one
window-keyed remaining-seconds derivation), Kante K-4/K-5/K-6
(SLEEP banner sovereign over the queued-task fallback; exit
ceiling 0.58 → 0.61 so the door invariant holds at standing;
banner slab descends with the hero), and Glanz's themes.xml
cure (the #000000 window background — item 3's already-landed
half, verified and kept). Nothing compiled, nothing
committed, nothing pushed, nothing posted in this pass —
DECLARED, CI judges on the lead's push. Wave-2 cures
(C-15/C-16/C-17/C-19/C-22, Samt D-1/D-2, DAE-1, Schliff F-1)
checked at every touch site: no conflicts found, none
regressed knowingly.

| # | Item | Disposition |
|---|---|---|
| 1 | E-1 AS ONE SCRIM (DAE-5/6/7, H-1…H-7, R-1…R-5) | LANDED. One Animatable scrim in MainActivity over a VM `NightReturnPhase` (VEILED 0.80 / LANDING 0.45 / OPEN 0), one 520 Curve C per leg; the pure phase machine lives in DeskClockSettle.kt (`NightReturnState`, `nightReturnOn*`) and is JVM-tested. Bloom ONLY from verb handlers (Ruhe §5/R-3 taxonomy: navigateTo, armMonolithSession, requestDisarmChallenge, dismissReceipt, completeTaskAndAdvance — toggleChecklistItem delegates there — promoteTaskToActive, deferTask, selectStudyPreset, selectAndApplyProfile, applySmartLoadout, setStudyBlocksCount, endSafeBreakEarly, and inside the changed-guards of setDialMinutes / setIntentionText); the dim clear is REMOVED from noteUserInteraction. Night hand dismissal enters at LANDING, snap entry (toggleAlwaysOnDeskClock(false) under themeEvaluation.isNight OR an open episode). Night scheme held while an episode is armed (the OR into MainActivity's effectiveNight + the Deck's isDarkTheme site; Ruhe R-4). Arm timestamp + 30-min expiry + ON_RESUME re-check (R-5, expireNightReturnIfStale). Veil only if witnessed, or cold discovery ≤ 30 min (H-3; seeded from the finish controller's first-emission handling — if a future frame capture shows uncovered frames on the fresh-cold path, Hauch's priced fallback is no-veil there, not a late veil). Scrim composed BELOW AlwaysOnDeskClockOverlay (H-2). Unveil on pointer DOWN via detectTapGestures(onPress), no indication (H-4/H-5). JVM tests: six phase-machine tests in DeskClockFlowTest (weights, gate, unveil/bloom, hand dismissal, expiry, snap rules) — the grammar's first coverage (Ruhe §7). |
| 2 | THE YIELD GETS ITS OWN EXIT (Samt S-1/S-3, Glanz G-1, Kante K-1/K-2/K-3) | LANDED. Yielded state exposed as `deskClockYielded` StateFlow; MainActivity composes a non-clickable true-black Box above the Deck while it stands (testTag desk_clock_yielded_black). The yield beat NEVER resets the surrender-fade flag (performDeskClockYield), the overlay's surrender effect holds 0 on a yield (no snap back to 1f), and the overlay's yield exit animates NOTHING — no dismissal throw (exitIsYield seam fact, one of the overlay's four exit branches). Scheduled two-phase clear (K-3): the ticker discovers deadline − 520 ms, sets the fade, and schedules performDeskClockYield for the deadline exactly (a plug-in before the deadline cancels the job and the yield). This REPLACES wave-2's one-ticker-grain fade lead. |
| 3 | SEEDED FRAME-ONE (Schimmer FE-2) | LANDED. themes.xml #000000 window background: ALREADY-LANDED-BY-SIBLING (Glanz G-6, in the tree uncommitted; verified, kept). Raise-generation-keyed remember: the VM stamps every raise through stampDeskClockRaise (generation + entryStartsAtFull); the overlay's animatables are `remember(raiseGeneration)` seeded at 1f for SEEDED raises — frame one by construction, no LaunchedEffect snap trusted. Plus the pre-decision black gate, generalised from item 2's black surface: `deskClockColdGate` (live pre-birth session ∧ clock not visible ∧ no settle decision) composes black over the Deck from the process's first frames. |
| 4 | DAWN AS MOTION (DAE-2 + Politur P-3; lead's ruling: DAE-2's form) | LANDED. The three composed alphas are animateFloatAsState targets (tween 520 Curve C) fed by deskClockPrintAlphas' outputs; the pure function's signature is UNCHANGED and no test call site moved. The same chase absorbs Samt D-3 / Kante K-7's resume curtain: at suspension expiry the target falls to the descended value and the animation walks it down over 520 Curve C. ONE form landed, per the merge ruling — D-3's resumeFraction input was NOT also built. |
| 5 | FINISH SEAM ON THE EVENT (DAE-8 + Glanz G-3) | LANDED. `deskClockGroundRelease` flips at the publish act in the detention loop (both the formula branch and the ceiling branch — filed-issuance for routine kinds included, since publish IS their issuance); the overlay releases the ground 260 Curve A on that event, retaining a 2,200 ms failsafe delay in place of the fixed 1,200 ms hold. Branch-U unwitnessed finishes now publish FIRST and hide after, in the same order, with a snap exit (exitSnap) — the hide moved inside the branches (the witnessed hide is unchanged at E0). |
| 6 | GLIDE + IDLE (Glaette G-1/G-2/G-3/G-4/G-6) | LANDED, all four. Glide: offsets are dp counts (3 dp step, ±6 dp grid) converted at the density and carried by animateIntOffsetAsState over 520 Curve C (G-1+G-2). Idle hero repaint aligned to the wall minute: an overlay-local minute-boundary clock drives idle derivations (G-3). Idle hand-raised keep-awake restored inside the VM machinery: the ticker's no-session beat keeps the flag for a visible HAND-raised face, no surrender deadline (G-4). dialMinutes threading REMOVED: the parameter is deleted from AlwaysOnDeskClockOverlay and from MainActivity's call site; the ripple check was clean (the overlay was the only consumer; the VM's own _dialMinutes flow is the composer's and untouched) (G-6). NOT landed: Glaette G-5 (serpentine glide path) — not in this pass's brief; the row-end leap stands as filed. |
| 7 | SLOT + SEATS (Schliff F-3/F-4/F-5) | LANDED, all three. Block-line seat reserved: a fixed 17 dp box stands whenever a timed multi-block span runs, text (lineHeight 17.sp) prints inside only when the derivation returns non-null — break start/end no longer jumps the column (F-3). Banner slot height DERIVED from BANNER_LINE_HEIGHT (19.sp × 2 + 24.dp padding) — the 68.dp magic (62.dp in the original merge) is dead; the banner Text carries lineHeight + maxLines 2 + ellipsis (F-4). Dash pattern in dp: floatArrayOf(8.dp.toPx(), 6.dp.toPx()), phase still 0f (F-5). |
| 8 | PROOF TABLE (Politur P-4) | LANDED. DeskClockFlowTest's face table gains the routine LAST MINUTE row (span 30 s / window 45 s → hero 00:45, sub-line ENDS 03:30 alone, banner SLEEP) and a label column asserted in every state (IDLE THE TIME NOW / timed LEFT / stopwatch ELAPSED) — the two blind spots P-1 and P-2 lived in. |
| 9 | V1 RESIDUALS (Samt F-2/F-3) | PARTLY LANDED, rest RECORDED-AS-DEBT. Checked DeskClockSettle against current code: HELD did NOT latch across sampler off-windows — any non-HELD beat (UNKNOWN included) cleared the held state and re-armed, restarting the 20 s backstop on every flap. Funke P-3's REDUCED LATCH ONLY is landed: HELD clears only on a positive REST verdict; UNKNOWN while held holds the latch and returns false (pinned by heldLatchesAcrossUnknownBeats_untilPositiveRest). REST dwell VERIFIED as Glaette observed: REST_DWELL_BEATS = 2 consecutive ticker beats (~2 s of sustained furniture signature at the 1 s grain) — meets Samt F-1's 1.5 s ask; recorded here, unchanged. NOT landed (tuning debt under §7 debt 9, unchanged): F-2's dual-threshold hysteresis + sway channel, F-3's sample-count gate, F-4's floor calibration — all threshold work that must wait for Vega's bench traces; landing them untested would be inventing numbers. |
| 10 | THIS RECORD | LANDED (this section + the E-1 correction above). |

Named remainders this pass did NOT land, for the lead's
table: Daemmer DAE-3 (the stand-down's trigger is an
emission, not the instant — the ticker re-check cure was not
in this pass's brief; DAE-1's keying landed in wave-2, so the
pair Daemmer named is half-landed and the debt stands
open); Glaette G-5 and G-7 (glide path; the dismissal-
persistence document contradiction — Wacht's call, either
way is one line); Schimmer FE-1 (HAND print delay 180 → 120 —
a table amendment for Ren, not a builder); Samt S-4 (the
wake tap spent twice — priced LOW, deferrable by name);
Glanz G-8's full seam-state consolidation (mitigated, not
landed whole: all raise sites now write through
stampDeskClockRaise — one assignment per fact in one
function — but visible/entryCause/exit-kind remain separate
flows).

— Wecker, wave-3 cure-builder (Wacht's cell, issue #13)

— Bauer, builder (Wacht's cell, issue #13)
