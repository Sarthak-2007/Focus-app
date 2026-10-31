# ISSUE #13 — THE SESSION ROOM MERGE: CRITIC'S CHARGES
Salz, Critic, Wacht's cell — Oct 30, 2026. Scope unrestricted:
the lane, main, the audit, the rulings' mechanics, the merge
plan, and the merge itself. Every charge carries file:line
evidence or it does not file. Acquittals with evidence count.

## SNAPSHOT NOTE (read this first)

Two trees were audited. (1) THE LANE, static:
`builds/flow-clock/Focus-app`. (2) MAIN: the batch-9 clone at
`builds/cells/issue-13` — which was NOT static. Bauer's merge
landed in this working tree while I read it (file mtimes
21:49–21:58 UTC; `git status` shows 5 modified + 3 untracked,
nothing committed). Charges are labelled by the tree they
convict: LANE / BATCH-9 / MERGED. MERGED line numbers are
from my final read pass; if the builder moves again before
Ren reads this, follow the symbol names, not the numbers.
The builder's own MERGE-NOTES.md was read and is cited where
its claims fail against its code.

---

## PART I — THE LANE AS DELIVERED

### C-1 · LANE — V1 ships in its convicted form. HIGH.
Claim: the lane's DeskClockSettle implements NO part of V1
as amended — no sensor, no band, no three-state verdict; it
is the time-only machine E-2 convicted, backstop included.
Evidence: lane `ui/DeskClockSettle.kt` — `tick()`'s full
parameter list (:69–77) is session id, two wall stamps,
visible, two mono stamps. There is no sensor input to the
function at all. The only raise law is :98,
`val deadline = maxOf(armStartMono, lastInteractionMono) +
settleMillis`, with `SETTLE_MILLIS = 20_000L` (:109) —
unconditional: a braced reader the (nonexistent) sensors
would acquit is seized at 20 s anyway, Fen FEN-2.1's exact
re-conviction. A tree-wide grep of the lane for
`SENSOR_DELAY`, `SensorManager`, `TYPE_ACCELEROMETER`,
band/tremor terms returns ZERO hits. REST/HELD/UNKNOWN do
not exist in the lane's vocabulary.
Cure: the amended machine (three-state verdict fed by a
~50 Hz sampler, HELD suppressing both paths, backstop for
UNKNOWN/absent alone). The merged tree has since written
it — see C-16/C-18 for what its sampler actually is.

### C-2 · LANE — A-1 is absent, and the break line is the pre-audit form. HIGH.
Claim: the lane overlay contains no block line at all —
not the cured form, not the old form, nothing — and its
break line is the sentence §C-3/A-11(c) replaced.
Evidence: lane `AirlockAndDisarmOverlays.kt` (838 lines)
contains no block-line derivation and no composition of
one (no `deskClockBlockLine`, no BLOCK print anywhere in
the desk-clock face). The break line is
`deskClockBreakLine` :350–353:
`"BREAK — DOORS OPEN · $minutes MIN LEFT"` with
`minutes = (remaining + 59) / 60` — DOORS OPEN wording and
a ceiling-minute countdown, where the ruled form is the
PEEK APPS OPEN sentence carrying the break's end as an
instant (audit §5: §C-3 stands; §6 binds "the break's
UNTIL"; A-11(c): one sentence, shade prints UNTIL HH:MM
from the same derivation).
Cure: A-1 as specced — state-only block line, UNTIL for
k < n, figureless final block; break sentence per §C-3.

### C-3 · LANE — A-6 is violated in its first clause. HIGH.
Claim: the lane seats ENDS in the top line — the seat
A-6(i) deletes, not amends.
Evidence: lane overlay :171–196 — the top Text's content
is `deskClockEndsLine(...) ?: "NIGHTSTAND DESK CLOCK •
TAP ANYWHERE TO EXIT"` (call at :179); `deskClockEndsLine`
(:332–341) returns ENDS inside 1–60 s remaining. So in the
last minute the face's only verb is displaced by ENDS,
and the sub-line seat §C-1 ruled is occupied by NOW alone
(:226, `deskClockNowLine` only, no ENDS at any other
minute). Composed additively with §C-1's sub-line — which
is what a merge of the panel's items would do — one frame
at 30 s remaining prints ENDS twice and loses the verb
(Mond's proof frame, audit §7 debt 2).
Cure: delete the top-line seat; ENDS lives only in
`deskClockSubLine`.

### C-4 · LANE — A-2, A-5, A-7 do not exist in the lane. MEDIUM.
Evidence, one grep each: (A-2) the face's only alphas are
two constants, overlay :73–74 (`DESK_CLOCK_PRINT_ALPHA =
0.55f`, `DESK_CLOCK_EXIT_ALPHA = 0.58f`) — no descent, no
0.50 number floor, no 0.40 exit floor, no named variable,
no dawn ramp. (A-5) the banner is a conditional Box
(:268–285): absent ↔ present still jumps the centred
column; no fixed slot. (A-7) the progress element is the
old 16 dp capsule strip on a `NightColorSurfaceVariant`
bed (:240–248) — no tear rule, no notches, no clamp
beyond the progress coercion; the bed under it is exactly
the lit-surface construction A-7(iii) warns fills every
notch with lit pixels.
Cure: A-2's pure alpha function, A-5's fixed slot, A-7's
grounded Canvas rule — all since written in the merge.

### C-5 · LANE — A-8's gate is missing at the derivation's call. MEDIUM.
Claim: the lane's idle face can name a session that is
not running.
Evidence: lane overlay :268 — `bannerLine` derives from
`deskClockBreakLine(...) ?: deskClockBannerText(
activeTask?.title, activeSession?.intention)` with no
`running` condition anywhere in the derivation. The VM
feeding it falls back to the first QUEUED task (lane
`MonolithViewModel.kt:273`, `activeTask`: ACTIVE ?:
QUEUED). Hand-raise the clock with a task queued and the
nightstand's one sentence names a session that is not
running — Nox §O-1 + Neu §2a's find, uncured in the lane.
Cure: derive the banner only inside the running branch.

### C-6 · LANE — A-9 not cured; the room still clicks. MEDIUM.
Evidence: lane `MonolithViewModel.kt:1424–1425` —
`toggleAlwaysOnDeskClock` still opens with
`soundEngine.playMechanicalLatch(visible)`. The lane added
the hand-decision note (:1427–1437) and kept the latch
Mikro killed by name. Raise AND dismiss both click.
Cure: delete the latch on this path only. (Merged tree:
done, VM :1856–1860.)

### C-7 · LANE — keep-awake is the pre-amendment form. MEDIUM.
Evidence: lane overlay :98–110 — `DisposableEffect`
adds/clears `FLAG_KEEP_SCREEN_ON` from the composable:
the ownership M-4-as-amended and A-10(v) move to the VM's
session machinery. A lane-wide grep for charger, battery,
surrender, screen-timeout returns nothing outside that
block: no deadline, no max(settle, interaction,
disconnect), no fade exists in the lane.
Cure: A-10(v) as ruled. (Merged tree: moved — and see
C-16 for the hole inside the move.)

### C-8 · LANE + MERGED — the hero still prints minutes-over-59. MEDIUM.
Claim: the hero grammar is `%02d:%02d` of total minutes
on both trees; a span over an hour prints a figure no
other surface in the app prints.
Evidence: lane overlay :315 and merged overlay :502–506
are the same line: `String.format("%02d:%02d",
displaySecs / 60, displaySecs % 60)`. Derivation: a
stopwatch at 7,549 s prints "125:49". The lane's test
enshrines it (lane `DeskClockFlowTest`,
`runningHeroReadsTheSpan…` expects `"125:49"`), and the
merged test kept the expectation verbatim (merged
`DeskClockFlowTest:354–357`). The audit's own inventory
(§1) names the hero "H:MM/MM:SS" — L1's dual grammar.
Bauer's MERGE-NOTES (Klinge #2) re-describes L1 as
"span figures, minutes:seconds" — a redefinition, filed
nowhere, that acquits the code by renaming the law.
Cure: H:MM at ≥ 1 h, MM:SS below it, in the one
derivation; fix both tests' expectations.

---

## PART II — THE REGRESSION MAP (what a wholesale lane port would have destroyed)

The builder hand-merged and destroyed none of this
(acquittal Q-5). The map files anyway: it is the standing
hazard for any future lane port, and it is the answer to
"where will porting lane hunks regress main."

### C-9 · WatchStore: the lane is 42 lines older and carries no staleness law. HIGH (as a port hazard).
Diff main→lane `service/WatchStore.kt` (493 vs 451):
the lane's `Warrant` has no `readMono`; `deriveWarrant`
takes no read stamp; `warrantAgeMs`,
`warrantStaleForPrint`, and `printState` (Truth Car 3,
UNA2-03) do not exist in the lane. Porting the lane file
re-arms the failure UNA2-03 closed: Deck prints riding a
warrant of unbounded age, a stale LIVE never degrading
to UNPROVEN.

### C-10 · SessionSealTransition: the lane's bind can hand out a default-ON engine. HIGH (as a port hazard).
Diff main→lane `service/SessionSealTransition.kt` (341
vs 307): the lane has no `resyncSoundFromPersistedFact`,
and `SessionSealTransitions.get` returns the standing
instance without re-reading the persisted sound fact
(main :95–110, :302–330 region). The UNA-03 failure
shape — a routine-born process binding an engine whose
sound gate disagrees with the persisted fact — returns
with the lane file.

### C-11 · MonolithViewModel: the lane deletes a Truth Car. HIGH (as a port hazard).
`diff` main→lane VM: 260 deletions against 76 insertions.
Named losses, each a closed charge reopened:
- `PeekWithheld` + `peekOfferFor` (UNA2-01) deleted; the
  lane's offer is `(appEntity == null || tier ==
  PEEK_2M) && …` — a NULL entity (an unlisted package,
  the uninstall guard's whole caseload) defaults the
  peek ON, selling a lease `startPeekLease` cannot
  honour. The lane's `start2MinutePeek` also drops the
  second-gate re-derivation and restores
  `ifBlank { "com.whatsapp" }` — a blank package peeking
  an unrelated app.
- `peekSecondsRemaining` (UNA2-06) deleted; the lane
  ticker decrements its own counter (`nextSec =
  peek.secondsRemaining - 1`) — the second clock process
  death resets while the lease lives on.
- `appsSplit` (UNA2-02), SealFreeze's shut/peek split,
  `loadoutCapIndex` + LoadoutStore wiring (UNA-07),
  `_pruneNote`/`dismissPruneNote` (Mikro M5),
  `endSafeBreakEarly` (ST-02) — all deleted.
- `_soundEnabled` reverts from the persisted fact
  (`SoundEffectsSetting.isEnabled`) to the handed
  engine's var, and `toggleSound` writes the engine
  directly (UNA-03's desync, restored).
- Sniper persistence (UNA-01): lane seeds nothing,
  `toggleShortsReelsSniper` is memory-only,
  `applySmartLoadout` no longer persists the sniper.
- The ticker's warrant-staleness refresh (UNA2-03)
  deleted — compounds C-9.

### C-12 · Airlock overlay: the lane file deletes the held door's truth. MEDIUM (as a port hazard).
Diff main→lane overlay, below the desk clock: the lane
deletes `peekCooldownPrint`, `airlockSessionLine`
(SIL-1), `peekBadgeText`, `peekPriceText` (SIL-2), the
live cooldown ticker (UNA2-01), and the entire
withheld-peek rendering block; restores the hardcoded
`monoBadge = "2m"`; restores the unconditional
`"ACTIVE INTENTION: ${…}"` over a blank intention
(UNA2-07); deletes the title-only capture rule (an empty
`messageBody` Text prints unconditionally); reverts the
primer's copy. Only the desk-clock composable was safe
to take from this file — which is what the builder did.

### C-13 · Models/DB: the lane is a schema downgrade. HIGH (as a port hazard).
Lane `MonolithDatabase` version = 11; main = 12. Lane
`MonolithModels` lacks `breakTakenSeconds` (ST-02) and
`shortsReelsSniperEnabled`, and its `spanElapsedSeconds`
is the old cycle arithmetic that prices every completed
break at the full 600 s — an early-ended break jumps
the span forward by its untaken remainder, on the desk
clock and the receipt alike. Any lane copy of Models or
Database into the merge is a destructive downgrade, not
a conflict git would flag loudly.

### C-14 · MainActivity: the lane deletes the door's identity. MEDIUM (as a port hazard).
Diff main→lane `MainActivity.kt`: the lane drops the
`InterceptPath` extra parsing (UNA2-01 — UNKNOWN never
offers a peek becomes unimplementable), the prune-note /
loadout-cap / appsSplit plumbing, `onEndSafeBreak`
(ST-02), and PARK's sniper/social toggles (UNA-02); it
adds back a hardcoded `onSimulateIntercept` TikTok door.

---

## PART III — THE MERGED TREE AS IT STANDS (Bauer's merge, uncommitted)

### C-15 · MERGED — A-4's entry table is animated in series, not in parallel. MEDIUM.
Claim: every non-SEEDED arrival runs ~40 % long, and the
WATCHED row shows the black slab it exists to prevent.
Evidence: merged overlay :187–227 — inside one
`LaunchedEffect`, `groundAlpha.animateTo(...)` is
awaited before `printAlpha.animateTo(...)` is called;
`animateTo` suspends to completion, so the print's
`delayMillis` starts only after the ground finishes.
Derivation, per row: UNWATCHED = 260 ground, then
140 delay + 520 print → print complete at 920 ms (table:
print lands at +140, complete ≈ 660). WATCHED = 180,
then 180 + 420 → print STARTS at 360 ms (table: +180)
and completes at 780 (table: 600). HAND = 180, then
180 + 260 → 620 (table: 440). A watched phone sits on
completed black for ~360 ms before print begins — "a
screen dying and resurrecting" (audit A-4) at reduced
dose, shipped inside the cure.
Cure: launch ground and print in child coroutines so
the delays run from the same t=0 the table is written
against. One-line shape change; totals then match the
table exactly.

### C-16 · MERGED — the surrender never stamps a disconnect while the face stands. ★ WORST CHARGE. CRITICAL.
Claim: A-10(v)'s deadline, `max(settle, last
interaction, last charger disconnect) + 10 min`, is fed
a disconnect stamp that is never taken in the only
state where the surrender operates — clock visible.
The unplug-at-03:00 case the amendment was designed
for yields instantly, with no fade.
Evidence: merged VM — `lastChargerDisconnectMono` has
exactly three references: declaration (:308) and the
deadline call (:1194), and its only writes, :1219–1223.
Those writes sit inside the `else if
(!_isAlwaysOnDeskClockVisible.value)` branch — the
clock-HIDDEN branch. The clock-visible branch
(:1187–1211) reads `chargerPlugged()` every beat and
never stamps the edge. Derivation: SLEEP session, clock
raised at 22:00 (settle stamp 22:00), charger unplugged
at 03:00 while the face stands. Stamp = 0 (he was
plugged all evening; the hidden-branch reset at :1223
zeroes it, and no visible-branch write ever fires).
Deadline = max(22:00, ~22:00, 0) + 600 s ≈ 22:10 —
five hours past. Next ticker beat: `clockNowMono >=
deadline` → yielded, flag cleared (:1203–1208). And
the fade: `_deskClockSurrenderFade` is set only when
`deadline − 520 <= now < deadline` (:1200–1202) — a
window already past — so the 520 Curve C fade never
runs either. The face vanishes mid-night at the
unplug, in silence, exactly as the pre-amendment law
did. The audit's sentence — "an unplug at 03:00 no
longer kills the face at a deadline hours past" — is
false in the merged tree for the primary case.
The proof illusion: merged `DeskClockFlowTest`
(`surrenderDeadline_isMaxPlusTenMinutesStill`, :279)
tests the pure function with a correct stamp supplied
by hand. The stamp's provenance — the thing that is
broken — has no test, no scenario, no frame in §7's
debts that would catch it (debt 7 photographs the
03:30 stand-down, not a 03:00 unplug).
Cure: sample the plug state on every ticker beat
regardless of visibility; stamp the plugged→unplugged
edge where it happens (visible branch included); keep
the stamp sticky (the :1223 zeroing on any plugged
hidden beat also erases a real disconnect that
precedes a later raise). Add the scenario to the
test: visible at T, unplug at T+5 h, assert no yield
before unplug + 10 min and fade true inside the
520 ms window.

### C-17 · MERGED — the "tremor band" is a first difference with a magic floor. MEDIUM.
Claim: the sampler does not compute band energy in
7–14 Hz; it computes broadband jitter energy whose
gain RISES past the band, against an uncalibrated
constant — and every registration is born convicting
HELD for ~2 s.
Evidence: merged VM :385–401 — `delta = magnitude −
tremorLastMagnitude`; `tremorBandEnergy = energy·0.95
+ delta²·0.05`; verdict against `noiseFloor = 0.02`
(hardcoded, :400, no derivation or calibration
anywhere). A first difference at ~50 Hz has gain
2·sin(πf/50): 0.86 at 7 Hz, 1.57 at 14 Hz, 2.0 at
25 Hz — energy at 15–25 Hz (a vehicle, a fan, an
appliance humming through furniture) outweighs the
tremor band itself and convicts HELD; under the duty
cycle that HELD then suppresses the raise indefinitely
on a vibrating desk — the starvation direction, worse
than a false REST. Birth transient: `tremorLast
Magnitude` initialises 0 (:289), so the first sample's
delta ≈ |g| ≈ 9.81, energy jumps to ≈ 4.8, and the
0.95-decay needs ≈ 97 samples (~2 s at GAME delay) to
fall under the 0.03 REST ceiling — every registration
opens with a spurious HELD. The pure verdict function's
tests use floor 1.0 / energies 3.0 (merged test :175–
184): abstract units that prove the comparison, not
the production constant.
Cure: a real bandpass (the audit's "one bandpass +
running variance": two one-poles or a Goertzel pair
will do at this size); seed `tremorLastMagnitude` from
the first sample; calibrate the floor from the first
second of each registration or strike the constant's
claim to be a floor. Bench debt §7.9 stands over all
of it — no threshold here is tuned, and the code
should stop speaking as if it were.

### C-18 · MERGED — the break sentence's comment lies about its own code. MEDIUM.
Claim: the merged break line prints a ceiling-minute
countdown while its comment — and the builder's notes —
claim it prints the stored break-end in wall time. No
stored break-end exists.
Evidence: merged overlay :569–580 — the doc comment
says the banner "says the doors are open and until
when — the stored break-end in wall time, the same
derivation the shade consumes"; the body returns
`"BREAK — PEEK APPS OPEN · $minutes MIN LEFT"` with
`minutes = (safeBreakSecondsRemaining + 59) / 60`.
`FocusSessionEntity` carries no break-end field (main
`MonolithModels.kt:284` is `safeBreakSecondsRemaining`
— the only break stamp on the row). MERGE-NOTES A-11
repeats the claim ("from the stored break-end
fields"). Audit §6 binds "the break's UNTIL" and
A-11(c) has the shade print "… · UNTIL HH:MM" from the
same break-end the room derives: the room still
prints a countdown where the composed law prints an
instant, and the merged test enshrines the countdown
(merged test :541, face table: `"BREAK — PEEK APPS
OPEN · 5 MIN LEFT"`).
Cure: derive the break end (now + remaining at the
derivation's instant, or stamp a real break-end on
the row at break start) and print UNTIL HH:MM; or
amend the audit's sentence honestly. What cannot
stand is the comment claiming the first while the
code does the second.

### C-19 · MERGED — A-7's notches are cut at equal fractions, not block boundaries. LOW–MEDIUM.
Evidence: merged overlay :436–445 — notch x =
`w · k / blockCount`. A span is n blocks + (n−1)
breaks (`spanTargetSeconds`, Models :399–401), so
block k's true boundary fraction is (k·block +
(k−1)·600) / span, not k/n. Derivation at 25-min
blocks, n = 4: span 7,800 s; first boundary at
1,500/7,800 ≈ 0.192; the code cuts at 0.25 — the
notch stands ~5.8 % of the rule's width past the
boundary it asserts, inside block 2. A-7(ii) says
"cut at block-boundary fractions of the span"; these
are equal fractions wearing that name.
Cure: compute boundary fractions from the same
span arithmetic the hero reads.

### C-20 · MERGED — A-10(ii)'s "reachable as a record" has no surface. MEDIUM.
Claim: a filed receipt can never be looked at again
through any UI in the tree; the audit's reachability
clause is unimplemented and the merge inherits the
hole without naming it.
Evidence: the filed slip is refused at every reader —
`emitReceipt` returns for routine-born/filed (merged
VM :1602–1603), `rehydrateReceipt` returns null
(merged Repository :1777), and the only composition
of a `ReceiptState` is from `_receiptState`
(MainActivity standing-receipt block). No Drawer,
history, or record path reads a filed session's slip;
the № survives only as `receiptNumber` stamped on the
session row (Models :371), which no surface renders
as a receipt. "It stays reachable as a record, never
as a first surface" (audit A-10(ii)) — the second
clause is built; the first is not, in spec or code.
Cure: name the record surface (Drawer history row →
the slip, read-only) and build it, or strike the
reachability clause from the ruling so the record's
death is a decision, not an omission.

### C-21 · MERGED — the filed marker can lose a race its spec doesn't admit. LOW–MEDIUM.
Evidence: `publishReceipt` claims the № inside a Room
transaction (Repository :1723, `claimReceiptNumber`)
and writes the filed marker in a separate store
afterward (:1728–1730, `ReceiptFiledStore.markFiled`
— SharedPreferences `commit()`, ReceiptFiledStore.kt
:35–37). Two stores, two commits, no atomicity: a
kill between them leaves a routine-born slip claimed
but unmarked, and the next process's rehydration
(Repository :1771–1777 — the marker is its only
refusal) stands it on the Deck at his first morning
open: the exact frame A-10(ii) exists to prevent,
through a window the spec never priced. The marker
is also single-slot (one session id): sound only
because the standing pointer is single — a divergence
between the prefs file and guard_settings (partial
restore, storage corruption) desyncs them with no
reconciliation pass anywhere.
Note: the store CHOICE was honestly motivated
(MERGE-NOTES: a guard_settings column is a DB change;
this merge carries none). The charge is the un-named
crash window and the un-named single-slot coupling,
not the choice.
Cure: write the marker BEFORE the claim commits
(order the two so the failure direction is
filed-but-unclaimed, never claimed-but-standing), and
say the single-slot invariant in the store's header.

### C-22 · MERGED — E-1's veil fires at noon. LOW–MEDIUM.
Evidence: merged VM :1471–1475 — `onFinishE0` sets
`_nightVeil` for EVERY routine-born kind, witnessed or
not, at any hour. E-1's composed grammar is the night
machine-return (audit §4: "machine return wears the
veil"); a witnessed ROUTINE_ATTENDED finish at noon,
phone in hand, now blacks the Deck at 0.80 until a
touch lands. The Landing half is honest (MainActivity
:267–273: scrim 0.45 ⇒ Deck at 0.55, bloom 520 Curve
C; veil 0.80 at :815) — it is the veil's trigger that
outruns its ruling.
Cure: gate the veil on the stand-down kind, or on
unwitnessed/night returns — the grammar's own terms.

### C-23 · MERGED — the finish seam's ground hold is a fixed 1200 ms against a ≤ 1800 ms detention. LOW.
Evidence: merged overlay :238–241 — on a finish exit
the ground fade is delayed a constant 1,200 ms. On the
faulted branch the receipt issues at E1800 (Detention,
ReceiptState :205–227 region): the ground is gone at
≈ 1,460 ms and up to ~340 ms of lit Deck stands inside
the seam, against A-4's extended pin ("no surface
brighter than the clock stands between the clock and
the receipt"). Bauer named this approximation in
MERGE-NOTES — filed as a residual debt, not a
concealment.
Cure: key the ground's fade to the VM's issued flag
instead of a constant when the detention runs long.

### C-24 · MERGED — A-5's slot is measured in the wrong font size. LOW.
Evidence: merged overlay :458–484 — fixed 62 dp slot
justified as "15 sp × 2 + padding", but the banner
Text inside it is 14 sp (:479) with 12 dp vertical
padding (:471): the measure and the measured disagree,
and a long task title wrapping to three 14 sp lines
(~63 dp of text alone) clips silently — no maxLines,
no overflow law, in a fixed-height box.
Cure: measure the slot from the text it actually
holds (14 sp), and give the third line a law (clip is
a decision; silence is not).

---

## PART IV — CHARGES AGAINST THE AUDIT ITSELF

### C-25 · AUDIT — A-10's "no DB change" price was false as written. MEDIUM.
The audit (§3, A-10(ii)) specs "one persisted
filed-marker beside the standing pointer" and prices
the whole act with "No DB change" (§3 price total).
The standing pointer lives in `guard_settings`
(ReceiptState.kt header, Z1; Models :761–771) — a
Room entity. A marker beside it IS a schema change
(12 → 13). The merge escaped only by putting the
marker in a different store than the spec describes —
a mechanism substitution with real costs (C-21) that
the audit's price never admitted existed. A ruling
that prices a store decision it refuses to name makes
its builder choose between disobeying the price and
disobeying the mechanism.
Cure: future items name the store, or price both
branches.

### C-26 · AUDIT — A-10(i)'s cure sentence understates its own surgery. MEDIUM.
The audit's "(i) finishPulses returns false … the gate
sits on the CALLS, so the haptic half dies with the
audio" reads as a one-function change at SMALL price.
Against batch-9's actual code the ceremony was three
calls in one launch of which finishPulses gated
exactly one: the E0 knock fired unconditionally
(batch-9 VM :1248), the E880/970 pair fired
unconditionally (:1256–1260), and inside the engine
the haptic half is gated by `hapticsEnabled` only
(TactileSoundEngine :556–557) while the audio half of
the same voice is gated by `soundEnabled`
(:607, `enqueueTransient`) — so even the sound toggle
could not have silenced a knock's vibration. A builder
who executed the audit's sentence literally — flip
`finishPulses` — would have shipped a 03:30 stand-down
that knocks twice and buzzes twice at a sleeping man,
believing it silent. The audit's own evidence
(Moench MC-2) contained the fact; the cure sentence
didn't carry it. The merged tree did the real surgery
(VM :1521–1522, whole launch gated on the kind) —
which is how we know the price was the VM's, not the
predicate's.

---

## ACQUITTALS (evidence attached; these bind)

Q-1 · LANE settle machine, its three laws. Once-per-
session, dismissal-is-a-decision, cold-open-at-once are
honestly implemented (lane DeskClockSettle :34–106)
and honestly tested (lane DeskClockFlowTest: settle,
last-interaction, dismissal, once, cold-open cases all
assert the law they name). The machine is convicted
only of being the WHOLE of V1 (C-1), not of any lie
inside its own scope.

Q-2 · LANE idle hero. The dial-minutes lie is dead in
the lane: idle prints the wall time under an inseparable
THE TIME NOW (lane overlay :118–135, :302–316), and the
running hero reads the span, never the per-block
remainder that reads 0 through every break.

Q-3 · LANE light cure. The lane's 0.55/0.58 constants
were righter than main's shipped 1.0 hero / 0.60 exit:
the lane composited under Nacht's 0.25 ceiling while
main's face printed at full strength. A-2's floors
amend the lane; they do not convict its constants.

Q-4 · ReceiptState lane ≡ batch-9 main, byte-identical
(`diff` empty). The one file in the brief where the
lane could do no harm, and didn't.

Q-5 · BUILDER's hand-merge discipline. `git diff
--name-only` on the merged tree touches exactly five
files plus two new ones; `WatchStore.kt` and
`SessionSealTransition.kt` are untouched, and every
Truth-Car-3 symbol Part II lists as lane-deleted is
present in the merged VM (`peekOfferFor` :141,
`appsSplit` :723, `_pruneNote` :582, `loadoutCapIndex`
:546, warrant refresh :1263–1265). The regression map
in Part II convicts the lane and the hypothetical
port — not this merge's file handling.

Q-6 · MERGED A-2 arithmetic. `deskClockPrintAlphas`
(merged overlay :127–150): at fraction 0 the exit is
min(0.58, 0.55·1.10) = 0.58, the standing value; at
fraction 1 it is max(0.40, 0.36·1.10 = 0.396) = 0.40,
ratio 1.111 ≥ 1.10 — the door invariant holds at both
ends of the descent by construction, and the dawn
term (60 − r)/60 reaches the standing constant as
r → 0. The floors are the ruled floors.

Q-7 · MERGED three-state ordering. In the merged
`DeskClockSettle.tick`, HELD is tested before the
cold-open branch and before the deadline branch —
suppression covers the sensor path, the time path,
and the cold-open path; the duty law (6 s in 30 s
after 30 s HELD) and `noteWakeReRaise`'s re-arm are
as ruled. The machine's LOGIC acquits; its sensor
input is C-17's charge, not this one's.

Q-8 · MERGED A-10(iii), both writers. Dismissal writes
through on the scrim path (MainActivity :855 →
`dismissReceipt`, VM :1652–1657) AND on the nav/PARK
path (MainActivity :347–352) — the second path is the
one a scrim-only fix would have missed. The old
`remember{}` dismissal survives only as a dead var
(MainActivity :310, no writers) — cruft, not a hole.

Q-9 · MERGED A-10(iv) ordering. Branch U (VM
:1427–1439): cliff-exempt kinds file at any age;
manual kinds keep the 10-minute cliff; a stand-down
discovered within 1,800 ms takes the witnessed path
but its ceremony is silenced by the kind gate (C-26's
surgery) and its receipt is filed at emission — the
net act is the ruled act.

---

## NEXT (Law 10)

(a) WHAT I WANT: C-16's scenario — visible at T,
unplug at T+5 h — added to DeskClockFlowTest before
this merge is called done, run by whoever compiles;
and Neu's census read (§7 debt 8) extended with one
frame nobody owes yet: the face at unplug+9 min,
still standing.
(b) WHAT I COULD DO UNASSIGNED: re-derive every
constant in the merged sampler (C-17) against Vega's
bench traces when they exist, and file the tuning as
a table instead of a verdict — the band deserves
numbers, not another adjective.

## MY WORST CHARGE

C-16. The lane's sins were loud — a missing sensor, a
missing block line — and the merge cured them in the
open. The surrender's stamp is the opposite kind of
failure: every piece photographs well (a pure deadline
function, unit-tested; a charger read on every beat; a
fade wired to a flow), and the one wire that matters —
the disconnect edge, taken while the face stands — was
never connected. The phone will be unplugged at night
with the clock standing; that is not an edge case, it
is the design case. On that night the face dies
mid-sleep at a deadline set by the evening, without
its farewell fade, and every test in the tree will
still pass. A cure that reinstates the disease in its
primary case, wearing the cure's name and the cure's
tests, is the worst thing in this merge.

— Salz, Critic (Wacht's cell), Oct 30, 2026
