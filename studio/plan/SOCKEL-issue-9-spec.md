# SOCKEL SPEC — #9 PARK: WatchStore sweep mechanics, CHECK verb, Guard prints
## Sockel, technical execution. Evidence: cells/issue-9 tree —
## WatchStore.kt (543 lines, read whole), ParkDrawer.kt, GuardLine.kt.
## Nothing compiled. Prices: XS <1h · S ~½ day · M 1–2 days.

## 0 · THE STORE'S SHAPE (verified, and honest about itself)

WatchStore is an in-memory singleton StateFlow. Persistence: NONE
— its own header says so; a process death returns the board to
UNMEASURED and the Guard line to NOT CHECKED, which is the truth
of a cold start. Writers, only these: lifecycle hooks (BIND items
+ device admin, sweepGen 0), the shield loop's
`onShieldIteration` (~2 s, gen 0, re-stamps ACCESSIBILITY +
NOTIFICATION_LISTENER from `instance != null`), and `runSweep`.
All publications pass through one `@Synchronized publish()` that
re-derives the interceptor family and re-tests the complete-set
rule for LAST PROVED. Serialisation is real; the Oct-29 lost-stamp
race is closed by construction.

Derivations (all pure, all render-time):
- `permPrintAt(snapshot, item, nowMono)`: UNMEASURED if no stamp;
  STALE iff GRANTED and `nowMono − measuredMono > 45_000`
  (STALE_AFTER_MS); MISSING never stales.
- `countedWarranted`: COUNTED four (ACCESSIBILITY, USAGE_ACCESS,
  DEVICE_ADMIN, NOTIFICATION_LISTENER) with print == GRANTED —
  a FRESH-PROOF count, never a grant count. Every surface that
  prints n/4 must be read with that meaning (see §4).
- `deriveWarrant`: LIVE iff a11y bound ∧ stamped-this-residency ∧
  usage ALLOWED; OFF on unbound/DENIED; else UNPROVEN.
  `printState` degrades a LIVE older than 45 s to UNPROVEN.

## 1 · THE SWEEP — how a proof is actually made

`runSweep(query)`: `gen = ++sweepGen`, records
`pendingSweepStartWall` (wall) + `pendingSweepGen`, launches SIX
parallel queries on Dispatchers.IO; each landing stamps with its
gen. A query that THROWS stamps nothing (C-9 — a crashed question
is not a measured "no"; correct, and see §3 for its cost).
Complete-set rule, inside publish(): all six PERMISSIONS carry
`measuredWall >= pendingSweepStartWall` AND
`completedAt − sweepStart <= 45_000` → LAST PROVED advances to the
set's max wall, `lastCompletedSweepGen = pendingSweepGen`, pending
clears. Past 45 s wall the pending sweep is abandoned; its late
stamps remain ordinary measurements.

## 2 · LIE #1 — THE SET IS AN ERA, NOT A GENERATION (Sockel, stands)

The completion predicate tests WALL TIME (`measuredWall >=
sweepStart`), never `measurement.sweepGen == pendingSweepGen` —
although every measurement CARRIES its gen, and the ParkDrawer
comment (ParkDrawer.kt:208–217) claims "a generation cannot be
laundered: only the completing set of the sweep the press
dispatched releases the key." That claim is false in the code it
sits above. Gen-0 stamps landing after sweepStart count toward
the set: the shield loop re-stamps ACCESSIBILITY +
NOTIFICATION_LISTENER every ~2 s from `instance != null` —
presence, not a grant query — and lifecycle hooks do the same
for DEVICE_ADMIN. So a CHECK press can be "answered" for up to
3 of 6 items by stamps that never asked the question the sweep
exists to ask. For the BIND items presence ≈ grant in the common
case, but the enabled-but-unbound accessibility state (the S-12
rider in #14: Settings string reads enabled, service unbound)
is exactly the case where they disagree — and it is the case a
CHECK at 1 AM is FOR.
CURE (S): the completion predicate requires
`it.sweepGen == pendingSweepGen` for all six. The sweep's own
queries already read the living facts for the bind items, so
their landings carry the gen and the set still completes at
sweep speed. LAST PROVED takes the same predicate — its
definition ("all six from ONE sweep") is currently honoured by
an era window, not by the sweep. The 45 s coexistence span check
stays as belt. Tests: WatchStoreRobolectricTest gains (a) a
gen-0 stamp landing mid-sweep does NOT complete the set,
(b) six gen-matching landings DO, (c) LAST PROVED does not
advance on a mixed-era set. This is the single highest-value
edit in #9: it makes the comment true.

## 3 · LIE #2 — CHECKING HAS NO EXIT WHEN THE SET NEVER LANDS

`checking` in ParkDrawer is derived as
`checkPressGen >= 0 && snapshot.lastCompletedSweepGen <
checkPressGen`. If any query throws (C-9 stamps nothing), or the
pending sweep is abandoned at 45 s, `lastCompletedSweepGen`
NEVER reaches the press's gen — publish()'s abandonment path
clears the pending fields and records nothing. The key then
reads CHECKING until the drawer closes (the only reset is
`LaunchedEffect(visible)`). A board whose one verb of proof can
spin forever on exactly the failure it was pressed to diagnose
is a print that outruns its evidence.
CURE (S): abandonment becomes a fact. Add
`lastAbandonedSweepGen` to WatchSnapshot, set in the same
publish() branch that clears the pending sweep; ParkDrawer's
derivation becomes `checking = pressGen uncompleted AND
unabandoned`; on abandon-release the existing verdict effect
fires with the board as it stands (first COUNTED lack named, or
NOT PROVED with no name when a query died — the item that never
answered is identifiable: it is the one whose stamp predates
the press). Also on record: two sweeps cannot run honestly
through one pending slot — a second dispatch (Deck resume while
a PARK CHECK is in flight) overwrites start/gen, and the first
press is then released by the SECOND sweep's completion. With
§2's gen predicate that becomes benign-but-mislabelled; the
clean form is join-don't-restart: `runSweep` returns the
in-flight gen when a sweep younger than 45 s is pending (XS,
rides §2). Pick one; do not ship neither.

## 4 · THE GUARD PRINTS — what n/4 may and may not claim

`guardLineFor` (GuardLine.kt) is total and pure: null snapshot
or ANY counted item UNMEASURED → `GUARD — NOT CHECKED` (P-6's
cure; blank-as-fine is dead). Otherwise `GUARD {n}/4` /
`GUARD {n}/4 — {FIRST LACK}` with ` STALE` suffixed on a stale
lack; n = fresh-GRANTED only. The Park headline (wave-2) and the
signed foot line print the same derivation — one derivation, no
fork; verified.
Two standing cautions, priced:
(a) STALE ROT IN AN OPEN DRAWER. Between sweeps a GRANTED row
decays to STALE at 45 s with no re-measurement; PARK-open
re-sweep + CHECK cure the arrival, not the standing read. A
drawer held open watches four SET doors become UNPROVED with
nothing having changed. CURE (S): while PARK is visible, the
host re-sweeps on a 30 s cadence (six cheap queries; only while
the drawer is open — battery cost is a rounding error). The
alternative — not staling while the drawer is open — is a lie
and is rejected by name.
(b) THE SEAL FREEZE. The host captures the Guard print at the
seal frame and holds it for the session's life. A frozen
`GUARD 4/4` is a seal-instant fact printed in the present tense
for hours; a door dying at 21:00 leaves the band claiming 4/4
until morning. The compensating control is §4.12's seal-time
warning line (priced in wave-2 as batch-10, VM-side) — until it
lands, the freeze is the board's one unbacked present tense.
Sockel's position: the freeze itself is lawful (the band is a
record of the seal, like the receipt's №), but the seal-time
warning is NOT optional garnish; it is the freeze's evidence.
Keep both items coupled in the batch-10 list.

## 5 · THE FIX VERDICT — Park still judges the raced way (Sockel)

Reception (#14) cured the return verdict with the Return Chit:
judge ONLY on a measurement with `measuredMono >
departureMono`, persisted across process death in SetupStore.
Park's wave-2 printed verdict (ParkDrawer.kt:332,
LaunchedEffect(parkReturnTick)) is the OLD machine: bare
`remember` (process death on the Settings trip erases it — the
trip that most needs the verdict is the one that kills it), a
fixed `delay(800)`, then a live-store read of whatever stamp
exists. Failure shapes: a slow or throwing sweep → the 800 ms
read lands on the pre-departure stamp: for a STALE door that
stamp reads STALE → STILL OPEN even when he fixed it (a false
STILL OPEN sends him on the trip twice — Muede's exact charge);
for the BIND doors the shield loop's 2 s presence re-stamp can
judge REPAIRED without any grant query having run.
CURE (S–M): Park's FIX writes a departure chit at press (the
SetupStore chit is door-generic already — same prefs file, same
states) and judges with `chitVerdictFor` on return: post-
departure measurement or no verdict (the key re-arms CHECK
instead of guessing). The 800 ms delay and the remember-verdict
die. Composition note: Reception and Park must share ONE chit
slot discipline — one excursion at a time, latest wins (the
store's existing law) — or the two surfaces will overwrite each
other's departures when he bounces between them. That
coordination is a host-level decision for the merge, priced
inside the S–M.

## 6 · RESIDUE, PRICED (from the audit, machinery side only)

- USAGE UNSET → the sweep query is Boolean: UNSET collapses to
  granted=false → the board prints MISSING while the warrant
  derivation (same fact) says UNPROVEN. Two words, one fact.
  Real cure (M): usage becomes tri-state in WatchMeasurement
  (the warrant already carries UsageAccess; the board item
  doesn't). Interim: the collapse is recorded here and in the
  audit; do not paper it with a print hack.
- OFF vs NO DOOR (interceptor family): `deriveInterceptor`
  collapses setting-off and parent-missing into OFF. Splitting
  it changes a derivation the Deck's warrant also reads —
  M, with its own verifier, sequenced by Ren (wave-2 remainder,
  Sockel concurs).
- LAST PROVED is a WALL instant: a user clock change can make
  it print a future or stale-looking time. The age column
  (mono-derived) is the truthful recency; LAST PROVED stays a
  wall DATE for the record. No cure priced; named so nobody
  "fixes" the wrong one.

## PRICE TABLE — #9
| Item | Price | Blocking? |
|---|---|---|
| Gen predicate for complete set (§2) | S | YES — the CHECK verb's truth |
| Abandoned-gen release for CHECKING (§3) | S | YES — with §2 |
| Join-don't-restart sweep (§3) | XS | Rides §2 |
| Open-drawer 30 s re-sweep (§4a) | S | No |
| Seal-time warning (§4b) | batch-10 (wave-2) | Coupled to freeze |
| Park FIX onto the chit (§5) | S–M | YES at composition |
| Usage tri-state (§6) | M | No — filed |
| OFF / NO DOOR split (§6) | M | No — filed |

— Sockel, technical execution
