# FINISH — POLITUR (Finisher 2, Wacht's cell, issue #13)

Slice: **RUNNING TIMED + LAST MINUTE** — the hero and its label
(L1 grammar), the sub-line (§C-1 / A-6), the block line as cured
by A-1, and the dawn ramp in the final 60 s (A-3, with Daemmer's
DAE-1 keying check). Gallery-standard pass over the MERGED code,
read fresh.

## STATE OF THE GROUND (read this first)

- Subject file: `app/src/main/java/com/example/ui/components/`
  `AirlockAndDisarmOverlays.kt` in the issue-13 clone. At my
  first read it was **1,320 lines, md5
  `466ef016a4d1840a0501c6c941833d94`** — NOT the 1,197 lines of
  the brief: a Wave-2 pass had already landed on top of Bauer's
  merge (Salz C-8, C-15, C-18, C-19, C-24 and Daemmer DAE-1 are
  cured in it, each marked in code). `git status` was clean
  before I touched anything. Everything below judges THAT tree.
- The tree is settled (no builder rewriting), so this seat made
  the two surgical print-layer cures its slice allows — P-1 and
  P-2 below, logged in full under EDITS MADE. After them the
  file is 1,348 lines, md5 `5cd1848ce5a9b60bb5b419781398c86c`.
- **No compile was run** (standing rule). The tests I added are
  written against the existing `DeskClockFlowTest` helper and
  patterns, but they are UNRUN until whoever compiles runs them;
  I do not claim them green.

## VERIFIED CLEAN (checked, not assumed)

- **C-8 is dead — the hero can no longer print minutes-over-59.**
  `deskClockHeroText` (:635–640): `displaySecs >= 3600` prints
  `"%d:%02d"` of hours and `(displaySecs % 3600) / 60`; below
  the hour it prints `"%02d:%02d"` of `displaySecs / 60` (≤ 59
  by the branch guard) and `displaySecs % 60` — the per-second
  tick under one hour stands, seconds vanish at ≥ 1 h, hours
  are unpadded. A 7,549 s stopwatch prints `2:05`; the merged
  test asserts exactly that (DeskClockFlowTest, 
  `runningHeroReadsTheSpan…`). Salz's `125:49` has no path
  back: there is one derivation and both branches bound the
  minute field.
- **A-6, both clauses.** The top seat is the constant
  `TAP ANYWHERE TO EXIT` (:394) with no conditional — ENDS's
  old top-line seat is deleted, not amended. In the last
  minute ENDS prints exactly once: sub-line alone, block line
  figureless (below), banner carrying no instant.
- **ENDS's derivation (A-12 C-1).** `deskClockEndsAtMillis`
  (:660–665): routine rows read the frozen `windowEndMillis`;
  hand rows read now + span remaining. The face promises the
  end the stand-down will keep.
- **A-1, the block line whole** (`deskClockBlockLine`,
  :697–705): suppressed on a break (:699 — the break line owns
  those minutes), absent for single-block and stopwatch spans,
  `BLOCK k OF n · UNTIL HH:MM` for k < n with the figure from
  the per-block remainder in wall time (:704–705), and
  `BLOCK n OF n` alone for the final block (:703) — one
  instant never prints twice, at any hour.
- **Break hero.** Through a break the hero keeps reading the
  span (or the window, routine) — never the per-block
  remainder that sits at 0 for the whole break.
- **A-3's arithmetic.** `deskClockPrintAlphas` (:155–159):
  `dawn = (60 − remaining) / 60`, every descended alpha lerps
  descended → standing, exit derived by the door invariant;
  the test pins the midpoint (r = 30, f = 1 → hero 0.455).
  The stopwatch takes no ramp (`isStopwatch` guard) — it has
  no T-0. A raise inside the final minute composes correctly:
  suspension lifts to standing first, the ramp then lerps
  standing → standing, a no-op. Wave-2 keyed hero and ramp to
  the window for routine rows (DAE-1) — verified in code at
  the hero and at the alphas call site. (One key still read
  the span. That is P-2.)

## DEFECTS — 4

### P-1 · HIGH — the running hero has no label: LEFT and ELAPSED were never composed — CURED IN PLACE by this seat

The audit's inventory (§1) and the cell spec (DESIGN §0, §1.3,
§1.4) bind the hero cell as label / hero / sub-line, K-8:
"label and figure inseparable" — `LEFT` over a timed
remainder, `ELAPSED` over a stopwatch, `THE TIME NOW` over
the idle clock. The merged composition printed the label
**only when idle**: the label Text sat inside
`if (!running) { … "THE TIME NOW" … }` (pre-edit :407–418).
Every running frame, all night, was an unlabelled 96 sp
number — a man woken at 02:00 meets `47:12` with nothing on
the face saying whether it is left or elapsed, on the one
face whose whole discipline is that a figure wears its label
in the same breath. No pure function carried the label, so
no test could catch it; the §7 face table asserts hero,
sub-line, block line and banner — and no label column.
CURE (landed, see EDITS): new pure `deskClockHeroLabel`
(timed → `LEFT`, stopwatch → `ELAPSED`, no/inactive session
→ `THE TIME NOW`), composed unconditionally above the hero
at the idle label's own seat, size and alpha
(`alphas.numberLine` — the label's ruled 0.55 → 0.50 floor).
Pinned by `heroLabel_isInseparableInEveryState`.

### P-2 · HIGH — the last-minute promotion still keys to the floored span: on a routine row NOW drops up to ~59 s early, or never — CURED IN PLACE by this seat ★ WORST

Wave-2 cured DAE-1 at two of the three seats that read the
end. The third kept the disease. `deskClockSubLine`
(pre-edit :654) promoted on
`session.spanRemainingSeconds in 1..60` — the SPAN's
arithmetic — while the hero beside it, the ENDS in its own
string, and the dawn ramp all keyed to `windowEndMillis`
for a routine row. The span target is built from whole
floored minutes at materialisation (MonolithModels, routine
derive), so the two remainders differ by 0–59 s every night,
by construction. Both directions fail, on the SLEEP span —
the one span the last minute exists for:

- Span 30 s / window 75 s (Daemmer's own fixture): the
  sub-line drops NOW and stands `ENDS 03:30` alone while the
  hero directly above it reads `01:15` — the face declares
  its last minute 45 s before it arrives, and the dawn ramp
  (window-keyed) has barely begun: promotion, hero and light
  tell three different stories in one frame.
- Span 0 s / window 45 s: the promotion never fires at all
  (0 ∉ 1..60) — the face stands in its true last minute
  still printing `NOW HH:MM · ENDS 03:30`, A-6's settled
  seat broken on the exact frame §7 debt 2 exists to
  photograph.

CURE (landed, see EDITS): one derivation —
`deskClockRemainingSeconds(session, now)` (window for
routine rows, span otherwise) — now read by the hero, the
sub-line's promotion, and the alphas call site alike. There
is no second remaining left in this slice to drift.
Pinned by `subLineLastMinute_keysToTheWindowForRoutineRows`
(both directions: at window-75 s NOW stands, at window-45 s
ENDS stands alone).

### P-3 · MEDIUM — the dawn ramp is still assigned, not animated: in the one watched minute, the light moves in per-second steps (Daemmer DAE-2, still open)

Filed by Daemmer against the merge; Wave-2 did not land it,
and it sits squarely in this slice, so it is re-filed here
with fresh evidence rather than assumed inherited. The
alphas are plain values — `Color.copy(alpha = …)` at every
seat (hero, sub-line, block line, exit) — recomputed when
the session row re-emits. Tree-wide grep of the overlay:
no `animateFloatAsState` exists in the file; the only
`Animatable`s are the ground, the print and the surrender
fade. Every other light event in this room is frame-animated
on a named curve; the descent can afford assignment (it is
found, not watched — Nacht A-N5), but the final 60 s of a
timed span is the one minute a finishing man is reliably
watching, counting seconds with the hero. Watched, a stepped
0.19 return reads as a brightness setting being incremented;
animated, the same endpoints read as light arriving.
CURE (exact, Daemmer's, not landed by this seat — see note):
`animateFloatAsState(target, tween(520, EasingC))` on the
three composed alphas, fed by `deskClockPrintAlphas`'
outputs exactly as now. NOT landed here deliberately:
Daemmer's DAE-2 and Samt's D-3 (suspension resume curve)
are priced as alternatives for the same motion — "the seats
should land ONE of them, not both — Wacht merges." Landing
mine unilaterally from the finisher's bench would spend
Wacht's merge call. Proof owed with it: two frames 250 ms
apart inside one ramp second, alphas differing between them
(Daemmer §5(b)).

### P-4 · LOW — residual proof gap in this slice: the face table still cannot see a routine last minute, or any label

The §7 debt-10 table (`faceTable_stateByFigureByExpectedString`)
gained its strings in Wave-2, but its LAST MINUTE case is a
hand row only and its `FaceCase` has no label field — the
two defects above both lived exactly in that blind spot
(P-2's routine promotion, P-1's composition-only label).
This seat's new tests pin the two cures as unit cases; what
remains owed: (a) a routine LAST MINUTE row in the table
itself (span 30 s / window 45 s → hero `00:45`, sub-line
`ENDS 03:30` alone, dawn keyed to the window), (b) a label
column in the table, and (c) §7 debt 2's frame itself —
30 s remaining, exit line on top AND `ENDS HH:MM` alone,
photographed, ideally on a routine row where P-2 lived.

## CROSS-REFERENCES (verified still present, owned elsewhere — not counted)

- **Schliff F-3** (block-line seat not reserved): the block
  line is still composed conditionally at :448
  (`deskClockBlockLine(...)?.let { … }`) inside the hero
  cell, so at every break start/end the tear rule and banner
  slot jump ≈ one line in 0 ms. His cure (reserve the seat
  at fixed height while a timed multi-block span runs)
  stands unlanded. In my slice's territory, his file owns it.
- **Exit-line wording divergence between the cell's own
  documents**: DESIGN-face-spec §1 tables print the top line
  as `NIGHTSTAND DESK CLOCK • TAP ANYWHERE TO EXIT`; audit
  A-6 rules the constant as `TAP ANYWHERE TO EXIT`. The
  merged code follows the audit (:394). Not filed as a code
  defect — but the two documents cannot both be the spec,
  and Wacht should strike one at the merge table.

## EDITS MADE BY THIS SEAT (all in-slice, print-layer only)

In `AirlockAndDisarmOverlays.kt`:
1. **P-2 cure** — added `deskClockRemainingSeconds(session,
   nowWallMillis)` (window-keyed for routine rows); the
   hero's timed branch, `deskClockSubLine`'s promotion, and
   the `deskClockPrintAlphas` call site all read it now
   (the call site keeps the stopwatch guard). The hero's
   Wave-2 inline window branch is deleted — one derivation,
   three readers.
2. **P-1 cure** — added `deskClockHeroLabel(session)`; the
   hero cell composes it unconditionally at the former
   idle-only label seat (Anonymous Pro Bold, 11 sp, +2 sp,
   `alphas.numberLine`).
3. Stale-comment cure riding P-1's edit: the hero comment
   claimed "(112sp)"; the hero is set at 96 sp (DESIGN §0's
   measure). Comment now says 96 sp. (DESIGN D-14's lane
   charge, still live in the merged file; too small to count,
   too wrong to leave.)

In `DeskClockFlowTest.kt` (additions only, UNRUN — no
compile per orders):
4. Imports for the two new derivations; tests
   `heroLabel_isInseparableInEveryState`,
   `subLineLastMinute_keysToTheWindowForRoutineRows`, and
   `heroBoundary_oneFrameChangeAtTheHour` (3601 / 3600 /
   3599 s → `1:00` / `1:00` / `59:59` — §7 debt 1's hero
   share, previously unpinned as a crossing pair).

Nothing else touched. No commit, no push, no GitHub post —
Wacht carries those.

## CLOSE

**Defect count: 4** (2 HIGH — both cured in place, 1 MEDIUM
open, 1 LOW open), plus 2 cross-references owned by other
seats.
**Worst: P-2** — Wave-2 cured DAE-1 at the hero and the ramp
and left the sub-line reading the floored span, so on the
SLEEP span the face's last minute either began up to ~59 s
before the hero's own count said so, or never began at all.
A dawn keyed to one instant and a promotion keyed to another
is DAE-1's self-contradiction surviving inside the cure's
own frame — and every existing test passed over it, because
the only last-minute case in the table was a hand row, where
the two remainders are the same number.

What I want next: P-3 landed as ONE motion (DAE-2 or Samt
D-3, Wacht's call) and §7 debt 2's frame shot on a routine
row, not a hand row.
What I could do unassigned: the same one-derivation sweep
over the pill's remaining-seconds read (A-11(b)'s span
number), which keys its last minute off its own arithmetic
the same way this one did.

— Politur, Finisher 2 (Running Timed + Last Minute),
Wacht's cell, Oct 30, 2026
