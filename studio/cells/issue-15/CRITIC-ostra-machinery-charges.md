# ISSUE #15 — SECOND PASS · MACHINERY SLICE — OSTRA'S CHARGES

**Seat:** Ostra, brutal critic, #15 second pass — machinery.
**Slice:** MIGRATION_12_13's composition and the merge mechanics
themselves. Not the wide pass (Verity's), not the map (Konto's).
**Evidence base:** batch-9 base tree `~/workspace/clone-inspect/
Focus-app` (DB v12) and the lane trees under `builds/` —
`truth-3-times` (Frist), `routine-edit` (Voss), `nofire` (Wache),
read at source, line numbers as cited. Plan documents: Ed2
(`studio/plan/BATCH10-MERGE-PLAN.md`), Ed3 + errata, Meister's
judgment (branch commit 3900356). No compile, no merge, no
gradle was run or is proposed by this file. Every charge carries
its evidence or it would not be here. Acquittals at the end —
defaults that are actually truthful are acquitted by name.

---

## CHARGES

### M-1 · `RoutineHeartbeat.kt` is the isTombstoned tripwire, and no plan surface owns it
Trunk's Heartbeat calls the very flag Wache's F2 deletes:
`Focus-app/.../service/RoutineHeartbeat.kt:147` —
`if (transition.isTombstoned(desired.routineId, desired.windowStartMillis))`.
Nofire's Heartbeat replaces that call with the re-prove block
(`builds/nofire/.../service/RoutineHeartbeat.kt:203-205` —
`transition.proveAndSeal(reproveId, null)`), and its
SessionSealTransition deletes the method, its tombstone store
(nofire SST :261, "The tombstone store itself is gone"), and
adds `SealVerdict.ADOPTED` (nofire SST :87-101; trunk's enum at
SST :80 is `{SEALED, JAMMED, ANNULLED}` — no ADOPTED).
Yet Ed2 mentions `RoutineHeartbeat` exactly **once** — a
parenthetical at BATCH10-MERGE-PLAN.md:268. The file is absent
from §2's collision surface, absent from the five line-by-line
files, and no step-3 exit condition names it. Mechanics of the
failure: land Wache's SST against trunk's Heartbeat and the
build breaks at Heartbeat:147 — and the cheapest repair a
merger will reach for, re-adding `isTombstoned` as a stub
returning `false`, compiles clean and silently resurrects
what F2 abolished: a never-tombstoned world in which annulled
routine windows are re-materialisable. Konto was warned about
preserving the flag; the symmetric trap — preserving its
CALLER, or stubbing the callee — is written nowhere. The
deletion, the Heartbeat re-prove, and the fake-override death
in `ArmProvingControllerTest.kt:137` (trunk) are ONE atomic
act and must be gated as one, by name, at step 3.

### M-2 · E-14(c) dies silently in `reopenStoredTask` — WORST CHARGE IN THIS SET
Base text, trunk `MonolithRepository.kt:2629ff`: the receipt
repair inside `reopenStoredTask` is gated on the ACTIVE
session alone — `val session = dao.getActiveSessionSnapshot()`,
repair only `if (target.completedInSessionId == session.id)`.
Voss's repair, routine-edit `MonolithRepository.kt:2376-2390`:
on a gate miss the completing session is fetched BY ID —
`else -> dao.getSessionById(target.completedInSessionId)` —
and repaired in place (his doc comment at :2351 names it
E-14(c): "fetched by id on an active-gate miss").
The two function bodies are ~90% identical: same name, same
preamble, same split/join repair (`" • "`, trunk :2650-2656 =
Voss's text). A hunk pass that keeps base text here, or reads
Voss's version as "same function, already present," loses
exactly one `when` block — and NOTHING signals it:
- it compiles (every symbol exists in base; `getSessionById`
  is trunk Dao :93);
- no base test witnesses it: trunk `DrawerPermanenceTest.kt`
  contains zero `E-14` hits; the only witness is Voss's own
  test (`routine-edit/.../DrawerPermanenceTest.kt:228`);
- Ed2 never admits it: §4A2's confirmed delta inventory for
  Voss is the withinLabel freeze ONLY. E-14(c) is not in the
  inventory, so under Ed2's own admission rule ("files outside
  it are named never-port" logic applied to hunks) the repair
  is not merely at risk — it is currently scheduled to be
  left behind while the epic reports Voss's lane merged.
Until §4A2 names the `reopenStoredTask` by-id block AND its
DrawerPermanenceTest case as admitted deltas, the merge ships
the Drawer-permanence lie E-14(c) was built to kill: reopen a
task from the Drawer after its session finished, and the false
entry stays on that session's stored summary forever.

### M-3 · The migration's row count is now disputed ON THE SAME BRANCH, and the executable text is the stale side
Ed2 §1 prints drop-in SQL with EIGHT rows, rows 7–8
(`heldShutAtSeal`/`heldPeekAtSeal`, DEFAULT -1) included as
executable text, while its own warning calls the -1 INFERRED
and sourced from no tree. Ed3 Step 2 carried R1 as an open
sub-decision. Meister's judgment (this branch, 3900356,
`studio/plan/MEISTER-JUDGMENT-BATCH10.md`) now answers R1:
**HOLD rows 7–8 — ship 6, hold 2** ("a schema column nobody
writes is a lie the database tells every future reader").
So the branch holds two authorities: a judgment saying six,
and the merge plan's step-2 drop-in text saying eight. Step 2
is executed from Ed2 §1, not from a judgment file. Until Ren
ratifies the hold AND §1's SQL is struck to six rows (or
annotated with the ratification), the path of least resistance
ships the inferred columns under a provisional default — the
exact laundering Ed2's warning was written to prevent.

### M-4 · withinLabel's freeze is a THREE-hunk chain; Ed2's inventory names two
The RC-03 freeze only works as a chain:
1. Dao stamp — routine-edit `MonolithDao.kt:106-108`
   (`stampWithinLabel`, one targeted UPDATE);
2. Repository freeze call — routine-edit
   `MonolithRepository.kt:1597-1602`, inside publishReceipt's
   issuance act: `dao.stampWithinLabel(pending.sessionId,
   pending.routineLabelWithin ?: "")` — note the `?: ""`: an
   issued ABSENCE is frozen as `''`, deliberately distinct
   from NULL (never issued);
3. Assembly read — routine-edit `MonolithRepository.kt:1465`:
   `row.withinLabel != null -> row.withinLabel.ifEmpty { null }`
   — the frozen label wins over live re-derivation, `''`
   prints as no line.
Ed2 §4A2 names legs 1 and 2. Leg 3 is in NO inventory. Drop it
and the composed tree writes a column nothing reads — silently,
because the column is nullable and the fallback (live
re-derivation) is the pre-freeze behaviour: every test that
doesn't rename a routine after issuance stays green. Drop leg
2's `""` instead (a merger "tidying" an empty stamp) and
restored slips re-derive a WITHIN line the issued slip never
had. The chain, all three legs, or RC-03 is recorded cured
while the receipt keeps re-writing its own past.

### M-5 · Frist's entity carries a stale composition-order comment that contradicts Ed2 §1
truth-3-times `MonolithModels.kt`, GuardSettingsEntity doc
(~:830-834): "(Composed-migration order: these two append
first among the car's guard adds; Falk's
shortsReelsSniperEnabled follows — Konto's map.)" That was
the DEAD 11→12 order. Batch 9 actually landed the sniper
column FIRST — it is trunk GuardSettingsEntity's last field
today (trunk `MonolithModels.kt:778`, after
`lastReceiptSessionId` :771) — and Ed2 §1's composed order
appends `peekMinutes`, `breakMinutes` AFTER it. Frist's
fields sit after `lastReceiptSessionId` in his tree
(times Models :835-836). Transplant his block at its authored
position and the fresh-install entity order
(…, lastReceiptSessionId, peekMinutes, breakMinutes,
shortsReelsSniperEnabled) contradicts both the migration's
ADD COLUMN order and Ed2's own law ("entity append order =
ADD COLUMN order per table") — and his comment remains in the
composed tree instructing the next reader that the wrong
order is the ruled one. The comment must be struck or
corrected at transplant; it cannot ride in as documentation.

### M-6 · There is no downgrade path, and neither edition prices the way back
Grep `downgrade|rollback|reinstall` across Ed2 and Ed3: zero
hits. The composed `getInstance` registers forward migrations
only, and `fallbackToDestructiveMigration` is gone BY DESIGN
(trunk `MonolithDatabase.kt`, MIGRATION_7_8 doc: "any future
un-migrated bump would silently wipe sessions" — the loud
IllegalStateException is the chosen behaviour). Consequence
nobody has written down: Batch 10's v13 lands on a phone
running v12; if the single build fails Sarthak's bench, re-
installing the batch-9 (v12) APK over a v13 database throws
on every cold start — the app is dead with its record intact
and unreachable, and the only exits are clear-data (the record
dies) or an emergency v14. Eleven steps are priced to the
minute; the retreat is not priced at all. Name the rollback
(a forward-only fix release? a pre-upgrade backup of the DB
file? acceptance of no-retreat, stated?) before step 2 opens,
not after the bench verdict.

### M-7 · Voss's Dao is a three-for-three swap at equal count — an eyeball census passes it
Diff of declared function names, routine-edit Dao vs trunk
Dao: BOTH declare 79 unique names. The sets differ by three
each way. Voss LACKS trunk's `deleteAppsByPackage`,
`stampReceiptNumber`, `updateShortsReelsSniperEnabled`; Voss
ADDS `deleteRoutineById`, `insertRoutine`, `stampWithinLabel`.
Overlay his Dao onto base and RC-04's claim loses
`stampReceiptNumber` (the №-on-the-row guarantee), UNA-01
loses its settings write, batch 9 loses its app-delete.
Overlay base onto his and M-4's leg 1 never exists. Ed2
names this exact trap for TIMES' Dao (§2 M6) but §4A2's Voss
inventory lists no Dao negative space at all. Admission must
record his Dao as named adds AND named absences, or the swap
is discovered by whichever cure silently stops working.

### M-8 · The migration can land perfectly while `breakLengthSeconds` keeps lying — the reader re-point is no step's exit proof
Trunk `MonolithRepository.kt:1843-1844`:
`private fun breakLengthSeconds(session: FocusSessionEntity): Int =
SAFE_BREAK_DURATION_SECONDS` — the function takes the session
and ignores it. Its own doc (:1835-1842) states the contract:
"when Truth Car 3's stamp lands… this reader re-points at the
row's own stamp and the constant survives only as the
default's ancestor. Every break-length read… goes through
here, so the stamp drops in at exactly one place."
Ed2 Step 2's exit is schema-only (diff = Database + entities).
The re-point is mentioned in step 4's prose traffic but is no
step's EXIT condition. Compose Frist's creation stamp
(truth-3-times Repository :1381 —
`breakSeconds = editableTimes.breakDurationSeconds()`) and
leave this reader returning the constant, and the composed
tree is a perfect forgery: column migrated, rows stamped,
settings editable — and every break still lasts exactly the
hardcoded 600 while the hybrid span getter (Ed2 C6) prices
breaks off the stamp the flips never consulted. Demand a
named step-4 exit grep: `breakLengthSeconds` body reads
`session.breakSeconds`; `SAFE_BREAK_DURATION_SECONDS`
survives only as MIGRATION_12_13's default ancestor.

### M-9 · F2's ADOPT guarantee is scoped to one file; the seed-sync dedup deletes the same population unaudited
`deleteSessionById` has a second caller no plan names:
trunk `MonolithRepository.kt:408-416` — the seed-sync dedup
deletes every NON-ACTIVE session whose
`intention|targetDurationSeconds|completedTasksSummary` key
repeats an earlier row. An ADOPTED routine-born row is, by
construction, a row another path created that later stands
non-active — precisely this dedup's prey. Wache's own lane
carries the identical delete (nofire Repository :400-402)
and never mentions it; F2's "never deleted" is proven only
inside SessionSealTransition's annul path (trunk Repository
:1456's caller). No Ed2/Ed3 step audits deleteSessionById's
callers against ADOPT. Either the dedup key learns to skip
routine-born/adopted rows, or the epic states plainly that
ADOPT's guarantee ends at the seal door — but it cannot keep
claiming "never" while a second delete stands unexamined.

---

## ACQUITTALS (evidence-checked; not charges)

- **A-1 · `breakSeconds` DEFAULT 600 is TRUTHFUL.** Trunk
  Repository :110 — `SAFE_BREAK_DURATION_SECONDS = 10 * 60`
  is the only Safe Break length; the creation/flip writes use
  it uniformly (:2027). No pre-v13 session could have been
  created under any other break length. The default is the
  fact. (Contrast `breakTakenSeconds`, DEFAULT 0 at 11→12 —
  a different column, a different truth; the two coexist and
  neither default impeaches the other.)
- **A-2 · `peekMinutes` DEFAULT 2 / `breakMinutes` DEFAULT 10
  stand.** They equal the shipped constants and Frist's entity
  defaults (times Models :835-836), and his clamps (0–30 /
  0–60) agree entity-side and provider-side. No lie.
- **A-3 · `withinLabel`'s nullable, default-less form is
  CORRECT.** Entity `String? = null` (routine-edit Models
  :370); migration `ADD COLUMN withinLabel TEXT` (routine-edit
  Database :184); Voss's chain test asserts NULL for pre-freeze
  rows (RoutinesMigrationChainTest :357-360). The one default
  in this set that was thought through end-to-end. (M-4 charges
  the chain's missing third leg, not this value.)
- **A-4 · `extensionLog` DEFAULT '' cannot crash a reader.**
  `parseExtensionLog` is total: blank → emptyList, malformed
  entries skipped (times Models :484-494). The encoding is
  safe at the default. No charge.

---

**Count: 9 charges (M-1…M-9), 4 acquittals. Worst: M-2** —
the E-14(c) repair is outside Ed2's admitted inventory for
Voss's lane, its loss produces no compile error and no test
failure in the base tree, and the false receipt entry it
exists to remove becomes permanent the first time the composed
build ships without it.

— Ostra (brutal critic, #15 second pass — machinery)
