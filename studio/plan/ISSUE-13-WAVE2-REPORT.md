# ISSUE #13 — WAVE-2 REPORT (Session Room merge)
Cluster F lead: **Amt** (flat under Ren). Branch `agent/issue-13`.
Wave-1 state on arrival: plan commit `b2cdc3f` + LARGE UNCOMMITTED
merge (VM +318, overlays +592/−114, Repository, ReceiptState,
MainActivity; new DeskClockSettle.kt, ReceiptFiledStore.kt,
DeskClockFlowTest.kt) + the cell critic's charges C-15…C-24 against
that merge (Salz). Wave-2 reviewed it line by line, FIXED the
convicted charges, and only then committed. DECLARED, uncompiled.

## Rulings check (binding)
- V1 AMENDED — sampling ~50 Hz (SENSOR_DELAY_GAME) only while
  armed-undecided ✓; 7–14 Hz band ✓ (as amended in Wave-2, below);
  HELD suppresses both raise paths ✓ (machine order verified);
  backstop alone for UNKNOWN / sensor-absent ✓.
- E-1 night veil + Landing — RATIFIED form merged ✓; trigger
  narrowed in Wave-2 (C-22 cure, below).
- V6 — STRUCK: zero V6 code in the diff, verified by grep ✓.
- M-4 keep-awake AMENDED — SLEEP span on battery yields after
  settle + 10 min still ✓ (and now actually fed a true disconnect
  stamp — C-16 cure).

## Charges C-15…C-24 — Wave-2 dispositions
- **C-16 CRITICAL — CURED.** The surrender's disconnect stamp was
  only ever written in the clock-HIDDEN branch (and zeroed by any
  plugged hidden beat): an unplug at 03:00 with the face standing
  yielded instantly, no fade, at a deadline set by the evening.
  Cure: the ticker samples plug state EVERY beat through a new
  pure `chargerDisconnectStamp` (edge-stamping, sticky, first-
  observation stamping); the hidden-branch writes are deleted.
  Salz's demanded scenario is now a test: visible at T, unplug at
  T+5 h → deadline = unplug + 10 min, stamp provenance included.
- **C-15 — CURED.** A-4 arrival/exit animations ran in series
  (animateTo suspends), ~40% long, black slab on the WATCHED row.
  Ground + print now launch in parallel child coroutines on every
  arrival row and both exits; dismissal is literally "together".
- **C-17 — CURED (estimator), bench debt stands.** The "tremor
  band" was a first difference (broadband gain rising past the
  band) against a magic 0.02, with a ~2 s spurious HELD at every
  registration (seed 0 vs |g|). Cure: two-one-pole bandpass
  (slow ≈ <7 Hz pole, fast ≈ <14 Hz pole, difference = the band
  signal), poles seeded from the first sample (verdict UNKNOWN
  until real band content exists), clean state on unregister and
  in `onCleared` (the listener leaked — also cured). The floor is
  renamed `TREMOR_FLOOR_UNCALIBRATED` and the code now says, in
  writing, that no bench has tuned it (§7 debt 9 stands).
- **C-18 — CURED.** The break line printed a countdown while its
  comment claimed a wall-time UNTIL. Now it PRINTS the UNTIL:
  `BREAK — PEEK APPS OPEN · UNTIL HH:MM`, the end derived at the
  reading instant (now + the row's remaining break seconds — no
  stored break-end field exists, and the notes no longer claim
  one; MERGE-NOTES A-11 corrected).
- **C-19 — CURED.** Tear notches were cut at equal fractions
  (k/n); a span is n blocks + (n−1) breaks. Notches now use the
  span's own arithmetic: (k·block + (k−1)·600) / spanTarget.
- **C-20 — REMAINDER (cross-issue).** A filed receipt still has no
  record surface that can re-open it read-only. Building one is
  the Drawer's territory (issue #8) — recorded there as a binding
  remainder, not guessed into this merge.
- **C-21 — CURED.** The filed marker was written AFTER the №
  claim across two stores — a kill between them stood a routine
  slip on the Deck. Order reversed (filed-but-unclaimed is the
  safe failure); the single-slot invariant is now named in
  ReceiptFiledStore's header.
- **C-22 — CURED.** E-1's veil fired for every routine-born kind
  at any hour (a witnessed noon finish blacked the Deck in his
  hand). The veil now gates on ROUTINE_SLEEP_STANDDOWN alone —
  the night machine-return, which is the veil's grammar.
- **C-23 — REMAINDER (named).** The finish seam's ground hold is
  still a fixed 1200 ms against a detention that can run to
  1800 ms on the faulted branch. Keying it to the VM's issued
  flag is the cure; not done here, debt recorded at the code site.
- **C-24 — CURED.** The banner slot was measured in a font it
  doesn't hold (claimed 15 sp, text is 14 sp) and a 3-line title
  clipped in silence. Slot re-measured from the real text
  (68 dp), and the text carries a law: maxLines 2 + ellipsis —
  truncation by decision, never silent clipping.

## Wave-2 seats' further convictions — dispositions
- **Banner profile floor unwired (Overlay-Review, NEW)** — CURED.
  `deskClockBannerText` gained the profileName floor (A-12/C-2)
  but the production call site never passed it; only the test's
  fake call did. The call site now passes
  `activeSession?.profileName`.
- **C-8 hero grammar (Salz, pre-merge charge)** — CURED. The hero
  printed total minutes ("125:49", "65:00"). L1 dual grammar now:
  ≥ 60 min → H:MM (no seconds), < 60 min → MM:SS. Tests updated.
- **Door invariant vs exit ceiling (Overlay-Review)** — HONESTLY
  RESTATED, ruling remainder. At standing alphas the 0.58 exit
  ceiling gives 1.055× hero, not the claimed 1.10× — the comment
  claimed "kept by construction" and it was false at standing,
  in the suspension, and at dawn's return. The comment now says
  exactly where the cap binds; neither ruled number was changed
  by this merge. Ren's ruling needed (see below).
- **REST was sampled, not sustained (Samt F-1)** — CURED. One
  REST beat (an EMA trough in a braced reader's breath) seized
  the phone. The machine now requires 2 consecutive REST beats;
  HELD/UNKNOWN reset the dwell. Tests rewritten + a dwell-restart
  case added.
- **Descent scope (Samt D-1 HIGH / D-2)** — CURED. The descent
  fired on every timed span (a 25-minute afternoon session sank
  to vigil alphas in daylight) and the stopwatch descended
  against an invented 1200 s span. Now: the VM derives
  `deskClockDescentApplies` from the stand-down's own
  isSleepLoadout path; only the SLEEP span descends; the
  stopwatch never does.
- **Descent resume step (Samt D-3)** — REMAINDER, with reason:
  the overlay recomposes at the session ticker's ~1 s grain, so
  a 520 ms resume ease would render in 0–1 frames — theatre, not
  a cure. Recorded; a finer recomposition driver is the real fix.
- **Surrender fade grain (VM-Review)** — CURED. The 520 ms fade
  window was evaluated on a 1 s ticker — a coin-flip whether the
  farewell ever rendered. The flag now leads by one ticker grain
  (1 s); the 520 Curve C fade runs inside the final second.
- **Wake re-raise doc lie (VM-Review)** — CURED (in words, the
  code was lawful): the wake tap raises BY HAND at once (a hand
  decision under law 2) and re-arms the machine for the next
  auto-raise; the doc claimed the wake raise itself was
  sensor-built. It now says what the code does.
- **Keep-awake stuck ON (VM-Review)** — CURED: a visible-with-no-
  session beat fell through both branches; now clears keep-awake,
  fade and descent state.
- **Sensor leak (VM-Review)** — CURED in `onCleared` (above).
- **Veil z-order (VM-Review)** — ACQUITTED BY LEAD: the receipt
  composes over the veil by design — the veil guards the Deck,
  and the finish's own receipt is the one surface that may stand
  above it. Recorded so the next critic doesn't re-file it blind.
- **Cold-open first-tick HELD (VM-Review)** — REMAINDER: §2.3
  prices the cold-open raise as immediate; with verdict still
  UNKNOWN (no sample yet) it raises. HELD-vs-cold-open priority
  on the first beat is a ruling question, not guessed here.

## Roster — signed sections (30 seats)
### Finishers ×5
- Finisher-1 (surface) — Seat Overlay-Review (ran): full-file
  verdict FIX-FIRST on arrival; every blocker it named is cured
  or honestly restated above.
- Finisher-2 (motion) — Seat Overlay-Review + Amt: C-15 parallel
  motion is this seat's delivery.
- Finisher-3 (light) — LEAD-COVERED: alpha floors verified by hand
  (hero 0.36, numbers 0.50, exit floor 0.40, dawn continuity).
- Finisher-4 (sound/haptics) — LEAD-COVERED: A-9 silent clock path
  + A-10(i) ceremony gate on the CALLS (audio+haptic die together)
  re-verified in the diff.
- Finisher-5 (typography/print) — Amt: C-8 H:MM grammar + C-18
  UNTIL print are this seat's cures.
### Feel specialists ×5
- Feel-1 (weight) — LEAD-COVERED (Samt filing stands as record).
- Feel-2 (light) — Seat Panel-Feel-Critic (ran) + Samt: descent
  scope (D-1) was the light defect of the merge; cured.
- Feel-3 (timing) — Samt (cell filing, ran in wave-1; Wave-2
  applied): F-1 dwell, D-3 remainder with grain reasoning.
- Feel-4 (touch+sound) — LEAD-COVERED: A-9's silent toggle path
  re-verified; no latch on the clock path either way.
- Feel-5 (instant-read) — LEAD-COVERED: hero now reads in the
  app's own grammar at a glance (H:MM), banner truncates by law.
### The 20 named types
- Crazy-ideas — Seat Panel-Feel-Critic (ran): see Next wants.
- Maximum-implementation-potential — Amt: C-16's cure is a pure
  function + scenario test, the widest honest form of the fix.
- Brutal critic — Salz (cell, ran) + Overlay-Review + VM-Review
  (ran): 10 charges + 8 further convictions; dispositions above.
- Improver — Amt (lead): all cures.
- Designer — Vela (cell filing stands); Wave-2 LEAD-COVERED.
- Consumer — Seat Panel-Feel-Critic (ran): for the sleeping
  student the merge's promise is the 03:00 unplug case — now true.
- Student — Seat Panel-Feel-Critic (ran): the 05:00 FASTED face
  no longer descends like a night vigil (D-1 cure).
- User — LEAD-COVERED via Consumer/Student.
- User-experience — Seat Overlay-Review (ran).
- Purpose-questioning — LEAD-COVERED: "should a study clock dim
  at all by day?" — D-1's cure IS the answer: it shouldn't.
- Thoughtful — LEAD-COVERED.
- Deep-thinker — Seat VM-Review (ran): stamp provenance, fade
  grain, leak, z-order — the invisible machinery audited.
- Enthusiastic designer — LEAD-COVERED.
- Wow-level designer — LEAD-COVERED: no wow claimed; the face's
  honesty is the delivery.
- Designer-29-years — LEAD-COVERED: two-one-pole bandpass is the
  textbook minimum honest filter; chosen over theatre.
- Intentionality-focused designer — LEAD-COVERED: every remainder
  above is a named decision, none an omission.
- Synchronous collaboration — Amt: three review seats + cell
  critic converged on the same defects independently (C-16 was
  found by all three) — the merge is stronger for it.
- Implementation-quality — Seat VM-Review (ran): no unresolved
  symbols found by eye in scoped files (uncompiled by law).
- Technical execution specialist — Amt: sampler lifecycle
  (register/unregister/onCleared) closed as one system.
- Interaction-detail specialist — LEAD-COVERED: banner ellipsis
  law + parallel arrival motion are the detail cures.

## DECISION-FOR-SARTHAK
- None. One RULING-FOR-REN (not Sarthak): the door invariant's
  two constants conflict at standing (exit ceiling 0.58 vs ratio
  1.10 × hero 0.55 = 0.605). Wave-2 changed neither and made the
  code say so. Ren rules which yields before batch-10.

## Exact issue comment (posted via gh; if posting failed it
## stands here as COMMENT-PENDING)
---
Wave-2 (Amt, cluster F) — the Session Room merge is committed,
after the charges against it were cured. DECLARED (uncompiled).

The wave-1 merge arrived uncommitted with the cell critic's
charges C-15…C-24 standing against it. Wave-2 reviewed every
file line by line (three independent seats) and fixed before
committing:
C-16 CRITICAL — the keep-awake surrender never stamped an
unplug while the clock stood, so the face died mid-night at a
deadline set by the evening. Now: plug state sampled every
beat through a pure stamp function; Salz's unplug-at-03:00
scenario is a test.
C-15 parallel arrival/exit motion; C-17 a real two-one-pole
7–14 Hz bandpass, seeded (no birth HELD), floor honestly
labelled uncalibrated; C-18 the break line prints UNTIL
HH:MM; C-19 notches at true block boundaries; C-21 filed
marker written before the № claim; C-22 the veil gates on
the sleep stand-down alone; C-24 banner slot measured from
its real text, truncation by law.
Also cured: C-8 hero H:MM grammar; the banner's profile floor
was unwired at its call site — now wired; REST raises only
when sustained (2-beat dwell); the descent belongs to the
SLEEP span alone and the stopwatch never descends; the
surrender fade can actually render at the ticker's grain;
sensor listener released in onCleared.
Remainders, named in the report: C-20 (filed receipt's record
surface — Drawer's territory, issue #8), C-23 (ground hold vs
1800 ms detention), D-3 (resume ease vs 1 s recomposition
grain), cold-open first-beat priority, §7 bench debts 1–9.
One ruling for Ren: door invariant 1.10× vs exit ceiling 0.58
conflict at standing — code now says so; neither number moved.
Full record: studio/plan/ISSUE-13-WAVE2-REPORT.md on
agent/issue-13.

— Amt, cluster F lead (Wave-2)
---

## Next wants
- (a) What I want: Ren's door-invariant ruling, then one line
  changes whichever constant yields.
- (b) Unassigned, I could: re-derive the tremor floor from
  Vega's bench traces (§7 debt 9) as a table, when they exist,
  and discharge the C-20 record surface jointly with Lade's
  Drawer cell.

— Amt
