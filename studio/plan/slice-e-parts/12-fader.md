# 12 — FADER — THE SEALED COUNT'S ONE CAPTURE (Slice E build-ready)

**Seat:** Fader (Improvers, the loadout band) · **Coordinator:** Ilya ·
**Joint:** the single capture behind fork **F18** (ruled **A** by Ren:
APPS `n = LOCKED + PEEK_2M` in every tense) and fork **F15**
(`heldAppsAtSeal`, Migration 9→10). Consumers in scope:
D6's APPS strip cell, D9's imprint SEALED tenant, E4/E6's receipt
provenance (MASTER-PLAN §5.7: *"loadout counts as actually sealed"*).
**Inbox at seating:** no unread letters (7 in `read/`, all Ledger-wave).
**Coordination sent:** column contract letters to Zettel (receipt)
and Saum (seam), this seating. **No code edited, no gradle run.**

---

## 0. VERDICT IN ONE BREATH

There is **one capture**, owned by the **repository**, at the
**insert** inside `startSession` — strengthened from Emil's filing
in one respect: the snapshot read, `deactivateAllSessions()`, and
the insert run inside **one `withTransaction`**, so the frozen
number is a fact of the same atomic act that births the row.
Every consumer, in every mode (live session in memory, session
restored after process death), reads **the entity field and
nothing else**. Nobody recomputes a sealed number, ever.

```kotlin
// data/MonolithRepository.kt — the ONLY writer, the ONLY derivation
val heldAppsAtSeal = countHeldApps(dao.getAllAppsSnapshot())
// …written as a constructor argument of the FocusSessionEntity
// inserted at (today) MonolithRepository.kt:997, inside the
// transaction specified in §3.
```

---

## 1. TODAY'S COUNT — WHERE IT IS COMPUTED NOW (trunk, file:line)

All paths `Focus-app/app/src/main/java/com/example/`, verified
against today's trunk (line numbers have drifted +~80 from the
slice-de parts' citations; today's numbers govern).

**There is no sealed count today.** `grep heldAppsAtSeal` over
`app/src/main` + `app/src/test`: **zero hits.** What exists is one
wrong derivation, copied three times, plus a third wrong number:

| # | Surface | Site | Derivation today |
|---|---|---|---|
| 1 | Deck header (the proto-imprint / strip ancestor) | `ui/screens/TactileDeckScreen.kt:120`, printed `:219` as `SHUT $n APPS` / `WILL SHUT $n APPS` | `apps.count { it.resolvedTier == AppBlockTier.LOCKED }` — **LOCKED-only**, computed in the composable via `remember(apps)` |
| 2 | Bottom-nav badge `blockedCountBadge` | `MainActivity.kt:226–228`, consumed `:494` | the identical LOCKED-only count, duplicated in the host |
| 3 | `SystemGuardStatus.installedLauncherAppsCount` | `data/MonolithRepository.kt:700` (`dao.getAllAppsSnapshot().size`), assigned `:711`, inside the heavy sweep `getSystemGuardStatus()` `:684` | **total registry size, OPEN included** — wrong number at the highest price in the codebase; forbidden as a count source (Uma §2) |

**The live path that feeds #1/#2:** `MonolithDao.getAllApps()`
Flow (`data/MonolithDao.kt:13–14`) → `MonolithRepository.allAppsFlow`
(`data/MonolithRepository.kt:79`) → `MonolithViewModel.apps`
(`ui/MonolithViewModel.kt:261–262`, `stateIn(WhileSubscribed(5000),
emptyList())`) → `MainActivity.kt:171` (`collectAsStateWithLifecycle`)
→ Deck parameter `apps = allApps` (`MainActivity.kt:308`).

**The Live Wire snapshot is NOT a count source and cannot become
one cheaply:** `grep apps` over `service/LiveWire.kt` returns
**zero hits** — the registry carries service facts (bind, stamp,
protecting) and no registry-list fact at all. APPS stays a DAO
fact, deliberately on a different source from BLOCK (Uma §1.2,
upheld).

**On the seed registry the three numbers disagree today:**
12 rows seeded at `MonolithRepository.kt:148–161` —
LOCKED 7 (TikTok, Instagram, X, Reddit, Snapchat, Clash Royale,
Roblox), PEEK_2M 3 (YouTube, WhatsApp, Discord), OPEN 2 (Chrome,
Docs). Surfaces #1/#2 print **7**; #3 reports **12**; the seal
actually holds **10**. That spread is the bug this joint closes.

## 2. WHAT LOCKED AND PEEK_2M COUNT FROM

- **The tier resolver:** `BlockedAppEntity.resolvedTier`
  (`data/MonolithModels.kt:226–231`) — parses the persisted
  `blockTier` string (`:218`), falling back to
  `isBlocked → LOCKED else OPEN` on a corrupt string. Entity at
  `:213–231`; the enum `AppBlockTier` at `:13–37` (LOCKED `:19`,
  PEEK_2M `:25`, OPEN `:31`). **The count predicate is
  `resolvedTier != OPEN`** — never the `isBlocked` flag, never
  the raw string: enforcement reads `resolvedTier`, so wall
  number and intercept set are one derivation by construction.
- **The enforcers the predicate matches:** Shield loop builds its
  blocked set from `dao.getAllAppsSnapshot().filter { … }` at
  `service/MonolithShieldService.kt:123–134` (OPEN → false at
  `:127`, else true); Accessibility intercepts identically at
  `service/MonolithAccessibilityService.kt:144–157` (OPEN returns
  at `:146`).
- **The snapshot source:** `MonolithDao.getAllAppsSnapshot()`
  (`data/MonolithDao.kt:16–17`, `SELECT * FROM blocked_apps`,
  no ORDER BY — a count is order-insensitive, so this is safe).
  This is the same snapshot discipline D8's cascade proves
  against (Emil §3.2): the sealed line prints the proved set,
  not the displayed set. The UI Flow is **not** the capture
  source — a Flow emission and the DAO snapshot can differ by
  one emission, and the VM's `apps` flow additionally starts as
  `emptyList()` (VM:262), indistinguishable from a true 0.

### 2.1 The PEEK gap — named, and its fix

The brief asks whether today's count source can see PEEK_2M
state. It cannot, in **two different senses**, with opposite fixes:

1. **PEEK the tier — today's count is blind to it (THE GAP).**
   Both live derivations filter `== LOCKED` (Deck:120,
   MainActivity:226–228), so every PEEK_2M app is invisible to
   the printed number while both enforcers hold it. **Fix:**
   the single predicate of §3 (`resolvedTier != OPEN`). This is
   F18-A implemented, not re-argued.
2. **PEEK the lease — the count must NEVER see it (by law).**
   The live lease state exists and is persisted per app:
   `activePeekUntilElapsedRealtime` / `lastPeekEndElapsedRealtime`
   (`MonolithModels.kt:222–223`), written via
   `MonolithDao.updatePeekLeaseAndCooldown` (`MonolithDao.kt:31–32`)
   from `MonolithRepository.startPeekLease` (`:830`). The
   enforcers consult it to *momentarily exempt* a PEEK app
   (Shield `:129`: `tier == PEEK_2M && nowMono <
   activePeekUntilElapsedRealtime -> false`; A11y `:155–157`;
   Safe-Break exemption Shield `:128`, A11y `:149–151`). The
   snapshot source *carries* these columns — capability is not
   the issue. If `n` followed lease state it would flicker
   mid-session and the freeze would be a lie: a peek is an
   exception **inside** the hold, not an absence of one (Uma
   §3a). **Fix: none — the capture reads `resolvedTier` only,
   and the lease columns are named here as a forbidden input**
   so no builder "improves" the count with them.

### 2.2 The priming gap — capture counts APPLIED tiers, never projected ones

There is **no priming in today's trunk** (`grep primed` over
`app/src/main`: zero hits). Both existing `startSession` callers
apply before they seal, in the same coroutine, in this order:

- Manual: `MonolithViewModel.armMonolithSession` (`:909–920`)
  calls `repository.startSession(...)` (`:913`) on the registry
  as it stands, then `startShield`.
- Routine: `RoutineAlarmReceiver.kt:45–56` —
  `repository.applySmartLoadout(loadout)` **then**
  `repository.startSession(...)`, then `startShield`.

So a capture at insert counts the loadout that was actually
applied — including the routine's. Uma's *projected* count
(the extracted pure tier `when` of `applySmartLoadout`,
`MonolithRepository.kt:762–805` in today's numbering) is a
**display-only** input for the strip's primed tense. **Rule,
binding on the builder: the capture never reads a projection.**
A projected capture would freeze a number for a loadout that
was never written to the registry — provenance for an act that
did not happen. Reihe's coordination rule (his §4) is the same
law from the routine side and is adopted verbatim: *both
snapshots are captured in the repository at insert, never in a
view or ViewModel*; his test T-R11 guards the funnel order
(apply act 1 → startSession act 2) and this joint depends on it.

## 3. THE CAPTURE — EXACT SPEC

### 3.1 Owner, function, inputs

**Owner: `MonolithRepository`** — the seal transition's only
author. Not the ViewModel (routine births have no VM; a VM
capture silently breaks routine receipts while manual ones keep
working — Reihe §4), not the Deck (a composable cannot own a
fact), not a service.

**The one derivation** (new, in `MonolithRepository`'s companion
or as a file-level function in `data/` — builder's choice of
home, one home only):

```kotlin
fun countHeldApps(apps: List<BlockedAppEntity>): Int =
    apps.count { it.resolvedTier != AppBlockTier.OPEN }
```

Uma's live `appsCellCount` map (her §3b) and the strip's live
tense must call **this** function, not re-spell the predicate —
the live number and the sealed number then share one derivation
text, and a future tier added to the enum changes both at once.

**Inputs:** `dao.getAllAppsSnapshot()` read **inside the
transaction** (§3.2). No other input: no clock, no session
state, no lease columns (§2.1), no projection (§2.2), no
`getSystemGuardStatus` (§1 #3).

### 3.2 The seal instant — INSERT, inside one transaction

The three candidate instants, ruled:

| Candidate | Verdict | Why |
|---|---|---|
| **DOWN snapshot** (D9's imprint latch, Emil §3.1d) | Display freeze only | At DOWN no session exists; a refused arm would have "captured" a count for a seal that never happened |
| **onLatch** (sled seats, D9 §3.3) | Rejected | Orders, proves nothing; capture there still precedes the row |
| **t=0** (PROVING → SEALED-RUNNING) | Rejected | The row is born at insert and re-anchored at t=0 (D8); capturing at t=0 means a post-insert **UPDATE** — a second writer shape, a PROVING window where the row exists uncaptured, and a race with D8's annulment-by-deletion at L+1500 (pre-t0) |
| **INSERT in `startSession`** | **THE CAPTURE** | The count is a fact of the seal act, written by the act (Emil §3.2); annulled rows take their count with them; no update path ever exists |

**Exact change** to `startSession` (`MonolithRepository.kt:950–999`
today; signature unchanged):

1. Everything clock/crypto stays where it is (`:963–972`:
   wall/mono/boot/HMAC computed before, outside the transaction).
2. Replace the bare tail — `dao.deactivateAllSessions()` (`:956`,
   hoisted out of its current first-line position) and
   `val id = dao.insertSession(session)` (`:997`) — with:

```kotlin
val database = MonolithDatabase.getInstance(context)
val sessionWithId = database.withTransaction {
    dao.deactivateAllSessions()
    val held = countHeldApps(dao.getAllAppsSnapshot())
    val id = dao.insertSession(session.copy(heldAppsAtSeal = held)).toInt()
    session.copy(id = id, heldAppsAtSeal = held)
}
return sessionWithId
```

(The entity is constructed at `:974–996` with the default
`heldAppsAtSeal = -1`; the `copy()` above overwrites it before
insert — the default never reaches a real row. Builder may
equally pass it as a constructor argument; the transaction and
the single read are what's normative, not the spelling.)

**Precedent in-file:** `completeEmergencyBreak` already does
exactly this shape — `MonolithDatabase.getInstance(context)` +
`withTransaction` at `MonolithRepository.kt:1225–1226`, with its
check inside the transaction. The import
`androidx.room.withTransaction` is already present (`:21`).
Today's `startSession` is **not** transactional — deactivate,
HMAC, and insert are three separate acts, and a tier write
landing between a pre-read snapshot and the insert would freeze
a count that was never the registry's at any single instant.
The wrap closes that; it also makes deactivate+insert atomic
(a failed insert no longer orphans the previous session's
deactivation — strictly safer than today, named in §6).

### 3.3 The write — column contract (coordinated with Zettel + Saum)

| Contract item | Value |
|---|---|
| Entity field | `heldAppsAtSeal: Int = -1` on `FocusSessionEntity`, `data/MonolithModels.kt`, beside `totalStudyBlocks` (entity today `:248–275`; field lands in the constructor) |
| DDL | ``ALTER TABLE `focus_sessions` ADD COLUMN `heldAppsAtSeal` INTEGER NOT NULL DEFAULT -1`` |
| Migration | **9→10**, in the already-booked batch: this column + Reihe's `lastReceiptSessionId` on `guard_settings` (his §3 rule 3) + Draht's `releaseFaultShield` / `releaseRetriesDone` / `releaseFaultedAtWallMillis` on `focus_sessions` (his §5.2) — one `MIGRATION_9_10` object, four DDL statements, registered beside `MIGRATION_7_8` at `MonolithDatabase.kt:79`, version bumped 9→10 at `:24` |
| Sequencing | Today's trunk is **DB v8** (`MonolithDatabase.kt:24`, only `MIGRATION_7_8` registered). The routines rebuild's 8→9 must merge first (Voss's lane has it built); this column **may not** steal 8→9 nor ride 7→8 (Emil §3.2, upheld) |
| Writer | Exactly one: §3.2. **No DAO `@Query` update for this column is to be created** — its absence is grep-enforceable (T-CAP-7) |
| Sentinel | `-1` = uncaptured (pre-9→10 rows; the three inactive seed constructions at `MonolithRepository.kt:279/:298/:317`, which lawfully keep the default). `>= 0` = a sealed fact. **`0` is lawful** (an all-OPEN registry seals holding nothing; APPS never faults on 0 — Uma §1.3) |
| Survival | Every later session write goes through `copy()` — progress (`:1037–1102`), disarm (`:1265/:1285`), task-receipt (`:1471`) — so the column survives the session's whole life by construction. The only fresh `FocusSessionEntity(` constructions in the tree are the three seeds + `startSession` (grep-verified, §3.2) |

### 3.4 The read path — all three consumers, both modes

**One field, two modes, zero holders.** In memory, the live
session *is* the entity: `startSession` returns it (with `held`
set, §3.2) and `repository.activeSessionFlow`
(`MonolithRepository.kt:81`, from `MonolithDao.getActiveSessionFlow`,
`MonolithDao.kt:57–58`) re-emits the full row on every session
write → `MonolithViewModel.activeSession` (`:280–281`) →
`MainActivity.kt:169` → the Deck already receives `activeSession`
as a parameter (`MainActivity.kt:294`). After process death,
Room re-emits the same row from disk and the same field arrives
by the same path. **No in-memory holder is built**: F15's
rejected fallback (b) (a VM `StateFlow` snapshot) is forbidden
as a consumer source — a holder that dies with the process is
the disease, not a cache of the cure. Consumers read
`session.heldAppsAtSeal` in both modes; there is no second
representation to drift.

| Consumer | Tense / trigger | Reads | Falls back to |
|---|---|---|---|
| **Strip APPS cell** (D6) | unsealed (idle / primed / mid-press) | live `appsCellCount` (Uma §3b, via `countHeldApps` §3.1); mid-press value latched at DOWN per D6/D9 | — (live tense) |
| **Strip APPS cell** (D6) | **sealed** — from the seal frame until the receipt dismisses | `activeSession.heldAppsAtSeal` **only** | nothing; if `-1`, the cell keeps its DOWN-latched live value for this residency (Emil's rule, §3.5) |
| **Imprint SEALED tenant** (D9) | sealed, `{n} APPS HELD` | `activeSession.heldAppsAtSeal` and nothing else (Emil §3.2, upheld verbatim) | `-1`: live count once at first observation, latched for the residency, never persisted as a backfill (Emil) |
| **Receipt provenance** (E4/E6, Zettel) | at issuance, incl. cold-start issuance days later (Reihe §3 rule 2) | the **terminal row's** `heldAppsAtSeal` (via the session-history / by-id read, never the live registry) | `-1`: **omit the count** from the provenance line (§3.5) |

**Why the strip's sealed tense must switch source (a correction
to D6's filed reasoning):** D6 froze the strip on the press
snapshot claiming it "structurally guaranteed — loadout edits
live only on the Shield destination." In today's trunk that
guarantee does not hold for a *returning* Deck: the one-way
ratchet **tightens tiers mid-session** (`cycleAppBlockTier` /
`setAppBlockTier`, `MonolithViewModel.kt:768–815` — OPEN→PEEK
and PEEK→LOCKED are lawful while sealed), the Deck unmounts
while Shield is composed, and on return a live-derived cell
recomputes — one app higher after a single tightening — while
the imprint beside it prints the frozen column. Two numbers,
one screen, one tense: the exact configuration F18-A exists to
kill. The source switch (sealed → column) is priced in §5.
(Enforcement following the tightened live tiers is *correct* —
the ratchet is a real tightening; only the *printed sealed
number* is frozen. The gap between held-as-sealed and
held-after-tightening is told at unlock / on the receipt per
Suki, never by reprinting.)

### 3.5 The −1 divergence, ruled inside this joint

Emil's fallback (live-once-and-latch) and a receipt recompute
look alike and are not: Emil's is same-session, same-residency,
minutes after the seal, for a row that was genuinely sealed
before the column existed. A receipt can issue on a cold start
**days** later; recomputing then would print today's registry
as a finished session's provenance — a fabricated fact of
exactly the kind §5.7's "as actually sealed" forbids. **Ruling:
imprint keeps Emil's latch; the receipt omits on −1.** Flagged
to Zettel by letter as his surface to contest; the sentinel
semantics (§3.3) are unaffected either way. Not a fork for Ren.

## 4. WHAT THIS JOINT DOES NOT DO

- It does not fix Prima's V-7 hole (`applySmartLoadout` has **no
  session guard** in the VM, `MonolithViewModel.kt:818–829` — a
  mid-session loadout apply rewrites every tier and enforcement
  follows). The capture makes the *printed* number immune; the
  enforcement divergence is that joint's, named here so nobody
  mistakes this spec for its cure.
- It does not give the receipt a deflection/site count, does not
  touch the strip's rendering grammar (Rosa), the Strike (Emil),
  or the cascade (Opal). It supplies one integer and its law.

## 5. PRICE (net lines, production; tests separate)

Already priced elsewhere and **excluded** here: the entity field
+ startSession snapshot inside Emil's +9 (his §5); Uma's live
derivation ≈63 (D6); Reihe's `getSessionByIdFlow` +4 and his
9→10 ceremony share; Draht's three columns.

| # | Item | File | Net |
|---|---|---|---|
| 1 | `countHeldApps` — the one derivation (§3.1) | `data/MonolithRepository.kt` | +3 |
| 2 | `withTransaction` wrap: `getInstance` + wrap, deactivate hoisted inside, snapshot read inside, return carries `held` (§3.2) | `data/MonolithRepository.kt` (`startSession`) | +6 |
| 3 | Strip sealed-tense source switch: `if (sealed) session.heldAppsAtSeal else liveCount` at the cell's input | Deck / strip host (inside D6 build) | +4 |
| 4 | This column's share of `MIGRATION_9_10`: 1 DDL line + registration share | `data/MonolithDatabase.kt` | +3 |
| | **Total, this joint** | | **≈ +16 net** |
| | Tests (§6) | `app/src/test/` | ≈ +95 |

## 6. RISKS

1. **Migration sequencing.** Trunk is v8; if a builder lands
   this column before the routines 8→9 merge, the temptation is
   to renumber it 8→9 — forbidden (§3.3). The capture *code*
   (§3.1–3.2) compiles and runs correctly against v8's entity
   the day the field is added; only the DDL waits for its batch.
2. **Transaction semantics change.** `startSession` becomes
   atomic where today it is not (§3.2). Behaviour change is
   one-directional (a failed insert no longer deactivates the
   previous session), but it is a change: T-CAP-10 covers it,
   and the slide/cascade builders should know the function's
   failure mode moved.
3. **The snapshot-and-display can differ by one.** The last idle
   print counts the Flow; the capture counts the in-transaction
   snapshot. A tier edit between Deck entry and the slide makes
   both prints true at their moments and different from each
   other (Emil risk 2, inherited). Named so it is never "fixed"
   by making the sealed count live.
4. **Package sync writes the registry.** `ensureSeededAndSyncPackages`
   inserts newly discovered apps (Repository `:165–210`); run at
   VM init it can move the *live* count mid-session. The sealed
   column is immune by construction; the live tense moving is
   lawful (it is a live tense).
5. **A fourth consumer appears** (AOD, chrome, Ledger) reading
   the live count for a sealed session. The law travels with the
   field's KDoc — builder instruction: the entity field carries
   a comment naming §3.4's rule (*sealed surfaces read this;
   live surfaces never do*), so the next surface inherits it
   from the code, not from this file.

## 7. ACCEPTANCE TESTS

Harness precedents: `TokenMigrationTest.kt` (migration),
`EmergencyBreakRepositoryTest.kt` (repository behaviour).

- **T-CAP-1 · Capture counts PEEK (F18-A).** Seed registry
  (7 LOCKED / 3 PEEK_2M / 2 OPEN) → `startSession` → returned
  entity **and** re-read persisted row both carry
  `heldAppsAtSeal == 10`.
- **T-CAP-2 · Frozen against tier edits.** Seal at 10 →
  `setAppTier(WhatsApp, OPEN)`, then `applySmartLoadout(SLEEP)`
  → `getActiveSessionSnapshot().heldAppsAtSeal` still **10**;
  live `countHeldApps(snapshot)` asserted different (both facts
  in one test, so the freeze is proven against a moving registry,
  not a static one).
- **T-CAP-3 · Survives process death.** Seal → close the
  database → reopen the same file with a fresh repository →
  active session's `heldAppsAtSeal == 10`.
- **T-CAP-4 · Routine order (guards Reihe T-R11).**
  `applySmartLoadout(STUDY)` then `startSession` (the
  `RoutineAlarmReceiver.kt:45` order) → captured `n` equals the
  count of the **applied** STUDY registry, not the pre-apply one.
- **T-CAP-5 · Zero is lawful, and is not −1.** All tiers OPEN →
  stored value is **0**; a consumer fixture renders `0` plainly.
- **T-CAP-6 · Migration + sentinel.** A **real-DDL** v9 fixture
  (the full `focus_sessions` table as 8→9 leaves it — not a
  two-column fiction; Falk's F-1 charge is the standing warning)
  → run `MIGRATION_9_10` → pre-existing rows read
  `heldAppsAtSeal == -1`, new inserts via `startSession` read
  their capture.
- **T-CAP-7 · Single writer (grep gate).** No `@Query` in
  `MonolithDao.kt` mentions `heldAppsAtSeal`; the only
  construction passing a non-default value is `startSession`'s.
  Construction-verified if the grep gate can't run in CI.
- **T-CAP-8 · Three consumers, one numeral.** One entity fixture
  (`heldAppsAtSeal = 10`, live registry fixture at 7) fed to the
  strip's sealed formatter, the imprint tenant, and the receipt
  provenance builder → all three emit **10**; the strip's
  unsealed formatter on the same registry emits **7** (tense
  separation proven in the same test).
- **T-CAP-9 · Tightening does not reprint.** Seal at 10 →
  ratchet-tighten an OPEN app to PEEK_2M (VM path) → sealed
  consumers still read **10** while the enforcer's blocked set
  (Shield filter, `:123–134`) now includes the tightened app —
  print frozen, enforcement live, both asserted.
- **T-CAP-10 · Atomicity.** Forced insert failure inside
  `startSession` → the previously active session remains active
  and no uncaptured active row exists. If the harness cannot
  force the failure, this downgrades to construction-verified
  (the `withTransaction` at §3.2) and is reported as such —
  never silently as a pass.

## 8. FORKS

**None new.** F18 is ruled A and F15 adopted; this file is their
implementation contract. Two micro-rulings made inside the joint
and recorded, not forked: the in-transaction capture (§3.2,
strengthens Emil without changing his act or instant) and the
receipt's −1 omission (§3.5, flagged to Zettel as his surface).

*— Fader, Improvers · the loadout band*
