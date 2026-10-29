# ISSUE #9 — PARK (main-app pass): cell plan + dispositions

*Cell lead: Hof (author of AUDIT-park.md). Branch: agent/issue-9.
Everything here is DECLARED — nobody in this cell compiles; the
verdict belongs to Ren's build on Sarthak's word.*

## The three items, as found and as left

### 1 · PARK never re-proves → the CHECK verb (audit P-1, merge §4.1) — FIXED
Found: sweeps fired on Deck entry, Deck resume, FIX return only.
Opening PARK fired `refreshSystemGuard()` — a diagnostic whose
result, in MainActivity's own words, "renders NOWHERE". The board
read whatever an earlier surface happened to measure; grants
decayed GRANTED → STALE in front of the reader with no verb.

Left:
- `MainActivity`: the parkOpen effect now ALSO calls
  `viewModel.runWatchSweep()` — the sweep the board actually
  reads. Every open path (rail cap, PARK re-tap, first-run
  auto-open) passes through `parkOpen`, so none bypasses it.
- `ParkDrawer`: a CHECK key — the board's second object, FIX's
  grammar (48dp, ink fill, radius 8, seat knock on press, 1dp
  dip). Present whenever the drawer is operable, including
  never-measured (where it is the only honest move on the
  surface); absent under the seal, where the drawer is print.
  Its CHECKING state is a derivation, never a timer: CHECKING
  until every permission item carries a measurement stamped at
  or after the press, whoever stamped it.
- A whole landing plays the firm click (the FIX return's voice
  for a repaired line). An unwhole landing prints a persistent
  verdict line under the key — `NOT PROVED` + the first unproved
  counted door, in the board's own row grammar — clearing on the
  next press, on close, or whenever the board reads whole by any
  road. (Designer's failed state, adopted with one correction:
  her draft wording blamed a door that "did not answer". Every
  door answers; MISSING is an answer. The line names the
  measured lack and claims nothing more.)

### 2 · Cold-open Guard line blank → `GUARD — NOT CHECKED` (P-6, §4.5) — FIXED
`GuardLine.kt`: both unmeasured paths (null snapshot; any
counted item UNMEASURED) now return the NOT CHECKED print
instead of null. The doc comment records why the silence died:
the old blank existed to prevent a fabricated count, but blank
read as FINE to a tired man — a blank is a claim too. The line
now names its own ignorance and still assumes nothing.
DECLARED tests: `GuardLinePrintTest` pins all four prints
(NOT CHECKED ×2, 4/4, named lack, named stale lack).

### 3 · `FIRED AS SET` on the plate (P-7 / W-11) — ALREADY STRUCK on main; verified
No code change in this cell, because the merged tree already
carries the cure: PLATE_BODY prints "ROUTINES: SET BY HAND ON
SHIELD" and the UNA-05 comment in ParkDrawer.kt documents the
strike and its return conditions (Wache's F1–F5 in the same
build AND a live fire re-verified). A repo-wide grep finds the
sentence only in that comment and in paper. Finisher 3 owns the
full grep census as a checklist. Nothing was "earned" back:
no F1–F5, no re-verified live fire — the sentence stays off.

## W-08 (dynamic PARK plate, batch 10) — inputs prepared, plate NOT built
Ren's plate lands in batch 10; this cell prepares only its inputs:
- `peekMinutes` (Designer's find, the plate's PEEK line): today
  the plate prints a hard-coded 2 minutes while the lease reads
  the `PEEK_DURATION_SECONDS = 120` constant — one fact, two
  sources, already a lie waiting for Frist's editable times
  (batch 10) to make it print a stale number. Input contract:
  ParkDrawer takes the same value the lease reads (post-Frist:
  the `guard_settings` flow PARK already consumes), null → the
  plate omits the number rather than guessing, 0 → OFF form,
  1 → singular.
- Live-state facts the plate may honestly draw on, all already
  in the WatchSnapshot the drawer receives: the warrant word
  (LIVE/OFF/UNPROVEN via `WatchStore.printState`), the counted
  warranted n/4, per-item prints + stamp ages (measuredMono /
  measuredWall), sweepGen, the engine heartbeat + protecting
  flag, LAST PROVED wall. Freshness rule for all of them: a
  fact older than the board's own 45s stale threshold prints
  as history (a time), never as a present-tense claim.

## Scope discipline (what this cell did NOT do)
- The Live-Fire Strip: parked (issue #21) — not designed, not
  built; CHECK's copy register bars its language.
- The wider §4 merge list (warrant line §4.2, headline count
  §4.3, age column §4.4, FIX staircase §4.6, tiers §4.7,
  colophon §4.8, plate cut §4.9, moves §4.10, padding §4.11,
  doorLocked gate §4.12) belongs to the pass that owns it —
  Finisher 5's checklist keeps the boundary explicit. This
  branch carries §4.1 + §4.5 + the P-7 verification only.
- `ParkDrawer`'s new `onCheckClick` parameter has exactly one
  host (MainActivity) and no default — a second host cannot
  silently seat the drawer without wiring the verb.

## Cell roster
Lead: Hof. Wave 1: Designer (spec delivered:
issue-9-designer.md), Innovator, Critic, Builder (dossier) —
running. Expansion (Sarthak's order via Ren): Finishers 1–5
(board print, keys, plate, guard line, sheet), Feel 1–5
(touch, sound, motion, atmosphere, the 1 AM test) — seating
proceeded one-by-one through an infrastructure pool limit;
all seats are recorded on the issue.

— Hof, cell lead, issue #9
