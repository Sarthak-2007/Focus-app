# ZETTEL — Improvers (the slip) · SLICE E BUILD-READY · THE RECEIPT CONTRACT

*Seat: Zettel, Improvers — the slip (notes/receipts). Coordinator:
Ilya. Joint: the ONE contract object that E4 (Wren, Release Proof +
the finish) and E6 (Fen, the Sled Goes Home) share, plus fork F15's
consumer map. Sources pinned this run: SLICE-D-E-BUILD-READY §2-E4,
§2-E6, §5 item 5, §6-F15 (ADOPT); MASTER-PLAN §5.6, §5.7 (the
Numbered Single Strike); WILD-PLAN §2.3; wild-seats/14-uma.md
(provenance + permanence); slice-de-parts/emil.md (D9 imprint +
§3.2 the column), fen.md (E6), wren.md (E4); slice-e-parts/
05-draht.md (E4 fault persistence), 19-linea.md (FinishSnapshot,
the slip's exit). All trunk lines verified this run against
`~/workspace/clone-inspect/Focus-app` (read only, no gradle).*

---

## 0 — VERDICT

**BUILDS, as one contract, at ≈+150 net production + ≈80 tests
(my increment only — Fen's E6 job and Wren's detention state
machine are priced in their files and are NOT re-counted here).**

Three rulings this file exists to make, stated up front:

1. **ReceiptState is born at ISSUANCE, never at E0.** What E4
   holds from E0 to issuance is detained *facts* (Wren §4.3:
   "held in the VM as a pending-receipt state — data, not
   pixels"). If ReceiptState existed at E0, E6's deadline
   (`published + 1000ms`) could be computed from the wrong
   anchor and the sled would go home up to 1800ms early. The
   contract therefore has two phases with two names (§1.1) and
   only the second is ReceiptState.
2. **The № counter and the lastReceipt slot live in Room**, in a
   new singleton-row table `receipt_store` — NOT DataStore.
   §5.7 calls the counter "the only permanent artefact"; the
   token ledger's precedent (derived, transactional, Room-only)
   and Draht's §5.2 rejection of prefs ("a one-record prefs file
   contradicts the Room-only pattern") both point the same way,
   and only a Room transaction can make *increment + slot
   overwrite + session-id latch* one atomic act (§2).
3. **A valve-ended session publishes NO ReceiptState.** Fen §3a
   says "both end paths converge" including early end; MASTER
   §5.7 + §5.3.8 overrule him: a valve-ended session "receives
   no finish receipt at all" — its `ENDED BY EMERGENCY BREAK`
   label belongs to the session *record's* rendering. E6
   inherits the gate: no receipt → no +1000ms anchor → the sled
   is rendered home by the rest contract's IDLE branch (Fen
   §3c), never animated home off a receipt that does not exist.
   Fen's convergence sentence is corrected here, in the open.

---

## 1 — (1) ReceiptState: THE ONE CONTRACT OBJECT

### 1.1 Lifecycle — two phases, two names

| Phase | Name | Born | Dies |
|---|---|---|---|
| Detained facts | `PendingReceipt` (E4-internal, VM finish state machine — Wren's ~150-line E4 state, his price) | **E0** — the StateFlow-edge flip `!isActive && completedSuccessfully` first observed (repository flip at `MonolithRepository.kt:1110–1111` in today's merged trunk; `updateSessionProgress` ≈:1026–1035 in the D/E tree — builder re-pins) | At issuance (becomes ReceiptState) or at annulment/supersede (a new arm before issuance — impossible in practice: issuance ≤E1800 always precedes any lawful re-arm; if it ever happens, the pending facts are dropped, no № is consumed) |
| Published | **`ReceiptState`** | **Issuance** — the receipt's t=0 under Wren's formula `min(max(E1480, R0+280), E1800)`: E1480 happy, P+280 slow, E1800 faulted | §1.4 |

ReceiptState is **immutable** (`data class`, all `val`s, the one
list deeply copied at assembly). It is never edited after
publication — a healed release fault "simply stops being true,
and files to the record" (Wren §4.3); it never amends the issued
receipt. Any builder who adds a `copy()` call on a published
ReceiptState outside the publish function has broken the
contract.

### 1.2 Owner and writers

- **Sole publisher: `MonolithRepository.publishReceipt(sessionId)`**
  — one Room `withTransaction` (the `completeEmergencyBreak`
  precedent, `MonolithRepository.kt:1224–1252`): latch check →
  № increment → slot overwrite → return the ReceiptState. Returns
  `null` when the row does not qualify (§1.5 gate) or the
  ReceiptState when it does — including the *already-published*
  ReceiptState for that sessionId (idempotent re-entry, §2.3).
- **Sole StateFlow owner: `MonolithViewModel`** —
  `val receiptState: StateFlow<ReceiptState?>`. It calls
  `publishReceipt` at the issuance frame E4's formula dictates,
  mirrors the result into the flow, and hands it to E6's
  scheduler (Fen's job, his price). The VM never constructs a
  ReceiptState with its own hands; it only mirrors what the
  transaction returned. (Fen's file has the VM *owning* a
  2-field ReceiptState — this contract supersedes the shape,
  not the ownership of the flow.)
- **Assembly: `MonolithRepository.assemblePendingReceipt(sessionId)`**
  at E0 — pure reads, no writes, returns `PendingReceipt`'s facts.
  E4 holds them invisible through detention.
- **Consumers are read-only**: Deck composition (D9/Emil),
  E6's return job (Fen), Linea's end-state frame. Nobody else
  reads ReceiptState; §3's non-reader list is normative.

### 1.3 Fields — every field with its named consumer

```kotlin
// data/ReceiptState.kt (new file)
data class ReceiptDeflection(
    val packageName: String,
    val appName: String,
    val timesWallMs: List<Long>,   // reduced: deduped, break-window-free
)

data class ReceiptState(
    // — identity & trigger (E6 + Deck) —
    val sessionId: Int,
    val receiptNo: Int,
    val receiptPublishedMonoMs: Long,   // SystemClock.elapsedRealtime()
    val receiptPublishedWallMs: Long,   // System.currentTimeMillis()
    val restored: Boolean,
    // — issuance kind (composition + E4) —
    val endedFault: Boolean,
    val stillBlockedShield: Boolean,
    // — provenance, frozen (composition only) —
    val title: String,
    val armWallMs: Long,
    val endedAtWallMs: Long,
    val plannedSeconds: Int,
    val sealedElapsedSeconds: Int,
    val heldAppsAtSeal: Int,
    val heldSitesAtSeal: Int,
    val routineLabelWithin: String?,
    val bankedBreakSeconds: Int,
    val shieldWasOff: Boolean,
    val deflections: List<ReceiptDeflection>,
)
```

Field-by-field justification (a field with no consumer below
does not ship):

| Field | Consumer(s) — exactly who reads it, to do what |
|---|---|
| `sessionId` | **E6** guard (i): fire only if still the latest completed session (Fen §3a). **Deck** once-key: strike keyed by sessionId (Emil's `struckSessionId` pattern; Linea T10). **E6** guard (iii) via the VM's `returnRunForSessionId`. **Composition** identity for the settled re-render after nav-away/return. |
| `receiptNo` | **Composition** only: prints `№ %03d` (Uma: Anonymous Pro Bold, the strongest ink on the slip). No logic anywhere branches on it except the publish latch (§2.3), which reads the *store's* copy, not this field. |
| `receiptPublishedMonoMs` | **E6** only: `returnStartMono = receiptPublishedMonoMs + 1000` (Fen §3a). Monotonic clock only — wall time never anchors motion. Sampled **in the publish transaction's return path**, i.e. at issuance, never at E0. |
| `receiptPublishedWallMs` | **Rehydration** (§2.4): persisted in the slot; on relaunch the VM rebuilds ReceiptState from it and can state the slip's age/date honestly. **Composition** does NOT print it (the slip prints arm/seal times, not its own publication wall time — Uma's layout has no such line). It exists for the store, and is carried in the object so the rehydrated and the live object are the same type with the same values. |
| `restored` | **Deck** strike gate: `false` = this residency witnessed issuance → the Strike may play (once, keyed by sessionId). `true` = rebuilt from the slot after process death/relaunch → render **settled**, no Strike, ever (Linea §5.4, T10). **E6**: a restored state never schedules the return job (Fen's unwitnessed law). |
| `endedFault` | **Composition**: the slip's issuance form — ENDED-FAULT sheet over the still-defocused Deck (Wren §4.3, faulted branch) vs the clean strike. **E4** reads its own formula result back from the published object so the Deck's rack completion (≤E2080 behind the sheet) keys off the same fact, not a parallel flag. E6 does NOT consume it — the sled goes home on faulted receipts identically (Fen: the road home does not ask why). |
| `stillBlockedShield` | **Composition** only: prints the one lawful fault line, once — Draht §7-T3: fault lines ⊆ {SHIELD}; the grammar (`… — STILL BLOCKED (…)` wording) is Opal/Suki's, owned by composition (§4). Mirrors the persisted `releaseFaultShield` column on the session row (Draht §5.2, riding the same 9→10 batch). A `List` here would pretend a multi-link fault is possible; Draht proved only one link can fault — Boolean is the truthful shape. |
| `title` | **Composition**: the slip's title line, *as it stood at sealing* (Uma; §5.7). Sourced from the session row's `intention` (`MonolithModels.kt:251`) — already a string copy frozen at `startSession` (VM passes `_intentionText.value`, `MonolithViewModel.kt:914` today), so no new capture is needed and Linea's micro-line must read the same row value, never `activeTask` (her §2.2 collector leak, VM:318–325 today). Renders `UNTITLED SESSION` when blank-ish per Uma — that wording choice is composition's; plumbing passes the raw string. |
| `armWallMs` | **Composition**: provenance line `Armed by hand at HH:MM · sealed until HH:MM`. Source: session row `startWallTimeMillis` (:255). |
| `endedAtWallMs` | **Composition**: the `sealed until` half of the same line + the deflection window's end (§deflections). Sampled by E4 at **E0** (wall clock at the flip observation), carried through PendingReceipt, persisted in the slot at publish (§2.2) — because the session row has **no completion timestamp** (Fen §2, verified: fields end at `completedSuccessfully`, `MonolithModels.kt:249–276`) and post-death re-derivation has no other honest source. Never derived as arm+elapsed: reboots, penalties and breaks make that arithmetic a lie. |
| `plannedSeconds` | **Composition**: medium line `… MIN PLANNED`. Source: row `spanTargetSeconds` derivation (`MonolithModels.kt:297–299`) sampled at E0 assembly — the whole-span figure (n blocks + (n−1)×600 breaks), not per-block `targetDurationSeconds`. |
| `sealedElapsedSeconds` | **Composition**: medium line `… MIN SEALED`. Source: row `spanElapsedSeconds` (:301–311) at the E0 flip — final at E0 by definition (Wren: "the receipt's facts are final at E0"). **Linea's** held-frame micro-line reads the same E0 sample via PendingReceipt (her FinishSnapshot and this assembly are one pipeline or the two surfaces disagree — her §2.2 law; the builder merges the two snapshots into the single E0 assembly, it is not two samples). |
| `heldAppsAtSeal` | **Composition**: provenance `Loadout as sealed — {n} apps · {s} sites` (Uma). Source: the F15 column on the session row (§3) — never the live apps flow, never a recompute. E6/E4 do NOT read it (§3 non-readers). |
| `heldSitesAtSeal` | **Composition**: the `{s} sites` half of the same line. Source: a **second F15 column** (§3.4 — my extension of F15 as ruled, separable, flagged to Saum/Ilya). Without it the receipt would count sites live at E0 — the exact rewrite F15 exists to kill, on the receipt's own provenance line. |
| `routineLabelWithin` | **Composition**: the routine provenance line, present only when non-null — `Within his routine — {name}, {window}` (Uma: *within*, never *by*). Evaluated at **E0 assembly** from the routines snapshot + `RoutineSchedule.isActiveNow(routine, armWallMs)` (`MonolithModels.kt:487`) over the arm timestamp (a historical check, so a routine edited *after* the session still evaluates against its window as it stands at E0 — named imperfection, Risk R4). The evaluated label is persisted in the slot at publish so rehydration never re-evaluates it. |
| `bankedBreakSeconds` | **Composition**: printed separately, never folded into sealed time (§5.7; Uma). Source at assembly: the spend ledger row for this sessionId (`EmergencyBreakSpendEntity.sessionId`, DAO `getAllSpendsSnapshot` filtered, or a by-session query, §5). **Named truth:** in today's trunk the only spend row belongs to a valve-ended session, which publishes no receipt — so on every publishable receipt this field is **0 today**, and composition omits the clause at 0. The field ships because the *contract* must not fold break seconds into `sealedElapsedSeconds` the day a mid-session banked break exists; it is not evidence a nonzero case exists now. |
| `shieldWasOff` | **Composition** only: the death line `THE SHIELD WAS OFF FOR PART OF THAT SESSION` (§5.6/§5.7 — the receipt is its only mid-flow rendering). Producer: E4/D8's death witness at assembly (the session's BROKEN booking source — Live Wire heartbeat gap over the sealed interval). Plumbing carries the Boolean; *detecting* death is D8's joint, not priced here. Interplay: a fully-elapsed death-booked session still publishes (its flip is `completedSuccessfully = true` in the repository's terms) and still takes a № — but E4's pulse gate withholds the completion pulse (§5.7 merge ruling). The № gate and the pulse gate are different gates; this file owns only the № gate. |
| `deflections` | **Composition** only: the itemised block headed `AS LOGGED BY THE SHIELD` (§5.7). **Reduction happens at assembly (plumbing), not in the composable**: rows from `intercept_logs` in the window `[armWallMs, endedAtWallMs]` with `sessionIntention == session.intention` (the log's only session key — `InterceptLogEntity`, `MonolithModels.kt:335–343`), excluding `packageName == "com.monolith.system"` (disarm/partial-stamp rows, `MonolithRepository.kt:1296–1305`), excluding break-window openings, 30s-per-package dedupe keeping the first timestamp (Uma). Grouped by package at assembly into `ReceiptDeflection`s. There is deliberately **no total field, no count field** — §5.7 forbids a deflection total anywhere; a contract that carried one would let a future surface print it. |

**Explicitly NOT fields** (considered, rejected, with reason):
`returnRunForSessionId` (E6's private VM guard — Fen §3a; putting
it on the shared object would let composition see trigger
machinery), `pawlLiftMonoMs P` / rack state (E4's finish state
machine, consumed before issuance), `verdict` for the Shield
Verdict Line (Uma's separate struct `{verdict, endedAt}`, a
different surface with a different overwrite — sharing the object
would give the deflection *events* a second home, which her
design forbids structurally), `strikePlayed` (derivable:
`!restored` + Deck-local sessionId key).

### 1.4 Death of a published ReceiptState

The StateFlow value is cleared / superseded at exactly these
moments, and no others:

1. **Next arm**: the VM nulls `_receiptState` in the arm path
   immediately after the new session's insert returns
   (`armMonolithSession`, after `repository.startSession(...)` —
   today `MonolithViewModel.kt:913–918` region; the Deck's visual
   supersede happens earlier, at PROVING, per Linea §5.4 — the
   slip leaves the *screen* on the next arm's DOWN, the *state*
   leaves when the new session exists). The persisted **slot is
   NOT cleared here** — §5.7: overwritten by the next *completed*
   session's publish, not by an arm.
2. **Overwrite**: the next genuine completion's publish replaces
   the flow value and the slot in the same transaction.
3. **Never on a timer.** No dismissal, no expiry, no nav-away
   clearing (Fen: navigation cancels E6's *job*; the state and
   the slip persist, and a Deck return re-renders the slip
   settled from this same object).

Process death does **not** kill it — §2.4 rehydrates it.

### 1.5 The publication gate (who may publish)

`publishReceipt` returns `null` — no № consumed, no slot touched,
no state emitted — unless the session row by id satisfies:
`isActive == false && completedSuccessfully == true`. That is:
natural, fully-elapsed completion only (Freya's charge, MASTER
§9/§5.7: "the receipt's trigger is `completedSuccessfully`
only"). Valve (`completeEmergencyBreak` → `completed = false`),
grace cancel, early disarm, annulled/deleted rows: **no
receipt, no №, no gap in the sequence** (Uma: abandoned/killed
sessions consume nothing, so the sequence needs no excuses).
Release-faulted and shield-death sessions DO publish (their
completion is genuine; the fault/death are lines on the slip,
not bars to it) — issuance at E1800 / with `shieldWasOff`
respectively.

### 1.6 The boundary against Emil's composition (D9) — the line, precisely

**Slice E (this contract) owns the plumbing:**
- the `ReceiptState` type, its assembly, its publication
  transaction, the №/slot store and its migration (§2);
- the issuance *call* (E4's formula decides the frame; the
  publish function is the mechanism it calls);
- the deflection **reduction** (window, exclusions, dedupe —
  data facts, final at E0);
- rehydration and the `restored` flag;
- E6's trigger interface: the published object + the +1000ms
  deadline fact. (E6's job, animator and rest contract are
  Fen's build, Fen's price.)

**Emil / D9 owns the composition — excluded from E's price,
named so no builder bills it twice:**
- the slip composable itself: layout, room/host (Linea's fork
  F-L1 is the host's open question — composition's problem,
  not plumbing's), all wording of every line including the
  fault-line grammar and the footer (`THE MACHINE KEEPS ITS
  OWN LOG · YOUR STUDY RECORD LIVES IN YPT`, §5.7 as merged);
- **the Strike as rendering**: 220ms empty beat → sheet rises
  24dp over 380ms `CubicBezier(0.2, 0.9, 0.25, 1)` → all type
  in one 110ms blow — timings, easing constants, thump/tick
  voicing (WILD §2.3). Plumbing's only duty to the Strike is
  delivering the object at issuance + `restored = false`;
- the `№ %03d` formatting, the provenance block's typography,
  the deflection itemisation's *rendering* (grouping is
  plumbing's reduction; lines, rules, tiers are composition's);
- `Save slip as image` (Uma's footer action) — a rendering of
  the composed slip; plumbing stores nothing for it.

**The rule that polices the boundary:** composition receives
`ReceiptState` and nothing else — no DAO, no Flow, no
repository call inside the slip's composable or its view-model
path. If composition needs a fact the object does not carry,
that is a contract amendment to *this* file, priced here —
never a live query wearing a receipt's clothes (a live query
is how a finished session's provenance gets rewritten).

---

## 2 — (2) THE № COUNTER + lastReceipt SLOT: WHERE THEY LIVE

### 2.1 Today: nowhere

Grep-verified this run: no receipt, no counter, no slot exists
in the trunk. `grep -rn "ReceiptState|lastReceipt"` over
`app/src/main` = **0 hits**. The VM's only completion event is
the 1s ticker branch (`MonolithViewModel.kt:330–349`,
`playHarmonicSeal(arming = false)` at :344 — E4 replaces the
discovery; this file replaces what it discovers *into*).
`FocusSessionEntity` has no completion timestamp
(`MonolithModels.kt:249–276`).

### 2.2 The store: a Room singleton row — `receipt_store`

```kotlin
// data/MonolithModels.kt — beside GuardSettingsEntity (:505–508),
// the trunk's existing singleton-row precedent (@PrimaryKey id = 1,
// MonolithDao.kt:157–161).
@Entity(tableName = "receipt_store")
data class ReceiptStoreEntity(
    @PrimaryKey val id: Int = 1,
    val lastReceiptNo: Int = 0,               // the № counter itself
    val lastReceiptSessionId: Int = -1,       // the slot's pointer
    val lastReceiptPublishedWallMs: Long = 0L,
    val lastReceiptPublishedMonoMs: Long = 0L,
    val lastReceiptEndedAtWallMs: Long = 0L,
    val lastReceiptRoutineLabel: String? = null,
)
```

The slot's payload is **re-derived at rehydration** from the
session row by id (title, arm, planned, elapsed, held counts,
fault columns) + the intercept-log window — the slot stores
only what cannot be re-derived: the № itself, *which* session,
and the three publish-time samples (published wall/mono,
ended-at wall, the evaluated routine label). This is §5.7
honoured literally: the slot holds a number and a pointer —
"nothing of 047 is stored" *in the slot*; the session record
lives in the record store, where §5.7 already puts it.

**Why Room, not DataStore** (the decision the brief demands):
- DataStore-Preferences is a *dependency* in the trunk
  (`app/build.gradle.kts:101`, `libs.versions.toml:36`) with
  **zero usages** in code — grep finds the word only in a
  comment (`CryptoIntegrityManager.kt:16`). Adopting it for the
  app's most truth-critical integer would found a second
  persistence regime for one record (Draht §5.2, same
  rejection, same reasoning).
- Atomicity: № increment + slot overwrite + already-published
  latch must be one act or a death between them mints a № with
  no slip, or a slip with a reused №. Room gives
  `withTransaction`; two stores (Room row + DS file) cannot be
  transacted together, full stop.
- Survival: the Room DB is device-protected-storage backed
  (`MonolithDatabase.kt:69–84`) and survives process death,
  reboot and update identically to the session rows the slot
  points at. A store that can outlive / underlive its payload
  is a desync waiting for a morning.
- **Migration 9→10 untouched by this table's *mechanism*:**
  `receipt_store` is a **new table** — it fits REGISTER's
  9→10 ruling ("append/child tables only") cleanly, unlike the
  ADD COLUMNs (§3.4's named tension). CREATE TABLE only; no
  existing table is altered for the №/slot.

### 2.3 The publish transaction (exact semantics)

```
publishReceipt(sessionId): ReceiptState?
  db.withTransaction {
    row = dao.getSessionById(sessionId) ?: return null
    if (row.isActive || !row.completedSuccessfully) return null   // §1.5 gate
    store = dao.getReceiptStore() ?: ReceiptStoreEntity().also { dao.insertReceiptStore(it) }
    if (store.lastReceiptSessionId == sessionId)
        return rebuildReceiptState(store, row)                    // already published:
                                                                  // same №, restored latch —
                                                                  // NO second increment
    no = store.lastReceiptNo + 1
    mono = SystemClock.elapsedRealtime(); wall = System.currentTimeMillis()
    dao.upsertReceiptStore(store.copy(
        lastReceiptNo = no, lastReceiptSessionId = sessionId,
        lastReceiptPublishedWallMs = wall, lastReceiptPublishedMonoMs = mono,
        lastReceiptEndedAtWallMs = pending.endedAtWallMs,
        lastReceiptRoutineLabel = pending.routineLabelWithin))
    return ReceiptState(..., receiptNo = no, restored = false)
  }
```

`getSessionById` is Draht's query (§5 of his file, +5, his
price — shared, not double-counted here). Exactly-once is the
`lastReceiptSessionId == sessionId` latch *inside* the
transaction: death after publish but before the Deck witnessed
it, a double invocation from E4's formula, a relaunch racing a
slow issuance — all return the same №.

### 2.4 Rehydration (process death / reboot)

On VM init: read the slot; if `lastReceiptSessionId != -1` and
the session row still exists, rebuild ReceiptState with
**`restored = true`** and publish it to the flow (so a Deck
return shows the slip settled — Linea §5.4). Rules:
- **E6 never re-fires** off a restored state, at any age (Fen's
  unwitnessed law — the return is theatre for a witnessed
  ending; the sled renders at its ReceiptState-derived
  position: fraction 0 if `publishedMono + 1650 < nowMono` on
  the same boot, else 1.00 — position is *derived*, never
  replayed).
- **Reboot staleness**: `receiptPublishedMonoMs` is
  boot-relative. If the current boot count (`querySystemBootCount`,
  the repository's existing mechanism) differs from the session
  row's era, the mono sample is void: treat the slip as
  settled regardless of arithmetic, `restored = true`. The
  wall sample stays valid for age.
- If the pointed-at session row is **gone** (Drawer/record
  deletion paths): the slot is dangling — clear the flow,
  keep the № (the counter never goes backwards; §5.7's proof
  that 047 existed must survive 047's record being deleted).

### 2.5 If the store is cleared — the №'s honest obituary

- **App data cleared / DB file deleted / fresh install**: the
  row is gone; the next `publishReceipt` recreates it at 0 and
  the next genuine completion is **№ 001**. Stated plainly:
  the counter is permanent *for the life of the install's
  database* — §5.7's "only permanent artefact" means permanent
  against the slip's overwrite, not against uninstall. Any
  design claiming more would be lying about Android storage.
- **Never backfilled**: a missing row over a *surviving*
  session history must NOT seed the counter from history size —
  history contains uncompleted, valve-ended and (today) the
  fabricated seed sessions (`MonolithRepository.kt` seeding,
  charged in LIE-REGISTER-3); counting it would mint a № for
  sessions that never took one. Missing row ⇒ 0. 
- **No silent reset via upgrade**: `fallbackToDestructiveMigration`
  is GONE (`MonolithDatabase.kt:59–67`, comment-verified) — a
  missing future migration crashes loudly in testing rather
  than wiping the counter on his phone. The № cannot die
  quietly by that road again.

---

## 3 — (3) F15 CONSUMER MAP: `heldAppsAtSeal`

### 3.1 The capture point (coordinated with Fader — letter sent)

- **Where**: inside `MonolithRepository.startSession`
  (`MonolithRepository.kt:950–1003` today), in the same call
  that builds and inserts the `FocusSessionEntity`
  (insert/return ≈:1000–1002) — the count is written into the
  inserted row's constructor, or it is a ghost (Emil §3.2,
  adopted verbatim).
- **From what**: `dao.getAllAppsSnapshot()` (`MonolithDao.kt:19`),
  `count { it.resolvedTier == LOCKED || it.resolvedTier == PEEK_2M }`
  — F18 ruled A: PEEK in (SLICE-D-E §6-F18; Emil A3). The DAO
  snapshot, never the UI `apps` Flow: the Flow and the snapshot
  can differ by an emission, and the sealed line prints the
  *proved* set.
- **Both birth sites covered for free**: the VM arm
  (`armMonolithSession` → `startSession`) and the routine arm
  (`service/RoutineAlarmReceiver.kt:45` → `startSession`,
  Siegel §7) funnel through this one repository function.
  Capture anywhere else (VM, receiver, view) covers one birth
  and lies about the other.
- **Fader coordination** (his loadout band, improvers/06):
  the band *edits tiers*; this capture *reads* them, once, at
  the seal instant, from the DAO. Two named non-sources:
  (i) Uma's **projected** primed count (`projectedShutCount` —
  a what-if for the Deck cell) is never the sealed count;
  (ii) a Smart Loadout applied mid-session rewrites DAO tiers
  live — the row's column does not move, which is the entire
  point of F15 (the in-memory fallback "would silently absorb
  tier edits made during the gap on restore-recompute",
  SLICE-D-E §6-F15). Letter in `mail/fader/`, thread
  `heldapps-capture-point`.

### 3.2 Every reader, with its read path

| # | Reader | Read path | What it does with the number |
|---|---|---|---|
| 1 | **D9 imprint, SEALED tenant** (Emil §3.1/§3.2) | Deck ← `viewModel.activeSession: StateFlow<FocusSessionEntity?>` (`MonolithViewModel.kt:280–283` today) ← `repository.activeSessionFlow` ← the row's column. `activeSession.heldAppsAtSeal`, **nothing else**, for the session's whole life | Prints `{n} APPS HELD` (singular `1 APP HELD`); the pressure lip's tenant. A mid-session tier edit, peek lease or revocation never reprints it |
| 2 | **Receipt composition — the provenance line** (§5.7; Uma) | E0 assembly ← `dao.getSessionById(id).heldAppsAtSeal` (Draht's by-id query) → `PendingReceipt` → `ReceiptState.heldAppsAtSeal` → composition | Prints `Loadout as sealed — {n} apps · {s} sites`. Assembly reads the *row*, not the flow — the session is inactive by E0 and `activeSession` is already null |
| 3 | **Receipt provenance as a fact** (the §5.7 snapshot itself) | Same read as #2 — listed separately because the brief names it: provenance is the *reason* the column exists; #2 is its rendering. One read, two names, no second fetch | The frozen snapshot the slip, the imprint and (via `heldSitesAtSeal`) the sites count all agree on |
| — | **Legacy fallback reader** (§4) | Imprint, for a `-1` row only | Once-sample freeze, §4.2 |

### 3.3 Named NON-readers (normative — wiring any of these is a failed build)

- **E4's STILL BLOCKED context**: **no read path.** The release
  fault is the *shield link* (Draht: `releaseFaultShield`,
  one Boolean, fault lines ⊆ {SHIELD}); the apps count is not
  an input to the detention formula, the fault grammar, the
  retry schedule or the pawl. A builder tempted to print
  "n apps still blocked" has invented a per-app fault Draht's
  T3 explicitly forbids this code path to produce.
- **E6 / ReceiptState trigger machinery**: reads `sessionId` +
  `receiptPublishedMonoMs` only. The count never gates,
  delays or shapes the sled's return.
- **Linea's micro-line / FinishSnapshot**: title + true length
  + queue count only (her §2.2). The held-frame does not print
  an apps count; the imprint beside it already does.
- **Shield Verdict Line** (Uma): receives a verdict enum +
  end date, never the count — her structural refusal, kept.
- **The live APPS cell (D6/Uma)**: reads the live/projected
  count in every *unsealed* tense. The column is sealed-tense
  only; the cell and the column are different facts that happen
  to share a numeral on quiet days (Emil Risk 2).

### 3.4 The DDL (coordinated with Saum — letter sent)

Riding **Migration 9→10** as ruled (F15 ADOPT, SLICE-D-E §5
item 5), inside the single `MIGRATION_9_10` object the batch
owner (Saum, the seam) registers in `MonolithDatabase.kt`
(today: only `MIGRATION_7_8` at :41, `.addMigrations(...)`
at :79). **Saum's filed batch (15-saum.md §1.1) is four
ADD COLUMNs on `focus_sessions` and creates NO table** —
F15's column + Draht's three fault columns. My DDL in that
migration is exactly one of those four lines:

```sql
ALTER TABLE focus_sessions ADD COLUMN heldAppsAtSeal INTEGER NOT NULL DEFAULT -1;
```

The №/slot DDL is NOT in this migration at all: it rides on
`guard_settings` (§2.2 as amended by §10-Z1 — Nocke's
`receiptSequence` + Reihe's `lastReceiptSessionId`, in the
8→9/9→10 guard_settings additions those seats already filed).
`heldSitesAtSeal` is **requested, not assumed**: it joins
9→10 only under Saum's standing pre-freeze offer (+2 lines:
1 DDL + 1 entity field, 15-saum.md §1.5); if he declines,
composition omits the sites figure (§10-Z3). My first letter
to Saum carried a `receipt_store` CREATE TABLE — **withdrawn**
by follow-up letter; the table does not exist in this spec.

(Draht's three release-fault columns ride the same object —
his SQL, his file §5.2; the migration is one ceremony, the
columns are itemised per owner so the merge can strike one
line without striking the batch.)

**Named tension, not hidden**: REGISTER's data arbitration
calls 9→10 a "plumbing batch — append/child tables only."
`receipt_store` complies; the two ADD COLUMNs do not, literally.
F15 as ADOPTED overrules the append-only wording for exactly
these columns (Emil already flagged this registration as the
fork if refused; SLICE-D-E §5 settled it as a ride, not a
request). If Saum refuses the columns a home in 9→10, the
fallback is the **same SQL in a 10→11 of its own** — priced
identically, scheduled later, and the in-memory fallback
remains **rejected in every branch** (F15's ruling, Emil §7).
Letter in `mail/saum/`, thread `migration-9-10-receipt-ddl`,
carrying the SQL above verbatim.

`heldSitesAtSeal` capture, for completeness: same instant,
same function — `dao.getAllSitesSnapshot().size`
(`MonolithDao.kt`, blocked_sites snapshot query) written into
the same inserted row (+2 lines inside §5's capture price).
Sites have no tiers; the count is the sealed set's size, which
is the whole provenance claim Uma's line makes.

---

## 4 — (4) THE LEGACY QUESTION: ROWS SEALED BEFORE 9→10

### 4.1 The default

**`-1`, for both columns** — Emil's specced default (§3.2),
kept. `-1` is not a count; it is the *absence* of a sealed
count, in a column whose lawful counts are ≥ 0 (a seal of zero
apps is grammatical and prints `0` — Uma §3c). Any default
≥ 0 would fabricate a provenance fact for sessions whose seal
nobody measured. `NOT NULL DEFAULT -1` in the DDL (§3.4);
entity default `heldAppsAtSeal: Int = -1` so pre-migration
rows materialise identically in Kotlin.

Which rows carry it: every `focus_sessions` row written before
the 9→10 migration runs — completed history (never read by
this contract's live paths; §4.3) and, the case that matters,
**a session ACTIVE at the moment of the update**: sealed under
v9, still running when the v10 build first opens the DB, its
row migrated in place with `-1`.

### 4.2 The rendering law for a restored-active legacy session

Scenario, exact: he seals a session; the app updates (9→10
migrates the live row); the app relaunches; the session
restores active (the repository's existing restore path —
`getActiveSessionSnapshot`, reboot/HMAC re-anchoring in
`updateSessionProgress`). Its imprint tenant reads **what?**

**The law (Emil §3.2's fallback, made precise):**

1. The SEALED tenant **never renders `-1`**, never renders a
   blank, never renders `0` by default. `-1 APPS HELD` on
   screen is a failed build (test T5).
2. On the **first composition** of the SEALED tenant for that
   sessionId, the Deck/VM samples the live count **once** —
   `dao`-snapshot semantics via the apps flow's current value:
   `count(LOCKED) + count(PEEK_2M)` — and **freezes it** in
   Deck-residency state keyed by sessionId
   (`legacyFrozenCount: Map<Int, Int>`, or a single
   `(sessionId, count)` pair — only one session is ever
   active). Every later composition of that session reads the
   freeze, exactly as a post-slice session reads its column.
3. The freeze is **residency-scoped, not persisted** —
   deliberately. Persisting a guessed count into the column
   would promote a re-measure into a sealed fact in the
   record itself; keeping it in memory labels it, honestly,
   as the tenant's best rendering rather than the seal's
   measurement. Consequence, named: if the process dies *again*
   mid-session, the next residency re-samples once — a tier
   edit between the two deaths can shift the printed number
   by the edit. There is no truthful source to do better
   with; the alternatives (print nothing for a live seal, or
   persist the guess as fact) are both lies of a worse kind.
4. **At issuance, the receipt does NOT resolve `-1`**
   (**Fader's ruling**, letter 0955, adopted §10-Z2 — it
   overrules the resolution rule this section originally
   filed). The Deck latch (rules 1–3) is scoped to the Deck
   tenant: same residency, minutes after the seal. A receipt
   can issue at a cold start *days* after the seal (Reihe §3
   rule 2), when the live registry has moved — printing a
   sampled number there fabricates a provenance fact, the
   exact lie §5.7 forbids ("loadout counts as actually
   sealed"). So `ReceiptState.heldAppsAtSeal` **carries `-1`
   through**, and composition's law for `-1` is **OMIT** —
   the apps figure does not print on that slip. The sentinel
   contract stands either way (Fader): `-1` = uncaptured,
   `≥ 0` = sealed fact, `0` lawful.
5. The session then completes and publishes **normally** —
   №, slot, sled anchor all as §1–§2. A legacy seal is not a
   second-class completion; only its provenance line is
   shorter (rule 4's omission), and the slip does not print
   provenance about its own provenance.

### 4.3 Rows completed before the update

A session that *completed* under v9 (or any pre-slice build)
**never publishes a receipt after the update.** Publication is
an issuance-time act keyed to a witnessed E0 edge; there is no
slot, no PendingReceipt, and rehydration (§2.4) only restores
an already-published slot. Its row sits in history with `-1`;
nothing renders its held count anywhere (history/Drawer
rendering of session rows is the record surface's business and
this contract gives it no tenant). The first receipt the app
ever issues is for the first genuine completion *under the
slice*, and it is **№ 001** (§2.5 — no backfill, no exceptions).

---

## 5 — CODE SITES + PRICE (my increment only)

Net production lines against today's trunk. **Excluded and
not double-counted**: Fen's E6 VM job + rest contract (his
≈+45 VM / ≈+102 total), Wren's E4 finish state machine +
detention timing (his ≈+150 VM share), Draht's `getSessionById`
(+5, his) and his 3 fault columns, Emil's composition (D9,
§1.6), Linea's FinishSnapshot fields (merged into the E0
assembly — one sample, §1.3 `sealedElapsedSeconds`).

| Site | Change | Net |
|---|---|---|
| `data/MonolithModels.kt` (after :276; :505) | `heldAppsAtSeal` + `heldSitesAtSeal` on `FocusSessionEntity` (+2); `ReceiptStoreEntity` (+13) | **+15** |
| `data/ReceiptState.kt` (new) | `ReceiptState` + `ReceiptDeflection` + `PendingReceipt` types | **+38** |
| `data/MonolithDao.kt` | Receipt store get/upsert (+8); intercept logs by window+intention query (+6); spends by sessionId (+4) | **+18** |
| `data/MonolithDatabase.kt` | My SQL inside `MIGRATION_9_10` (2 ADD COLUMN + CREATE TABLE; the migration object + registration are the batch owner's) | **+9** |
| `data/MonolithRepository.kt` (`startSession` :950) | Capture: apps count from `getAllAppsSnapshot()` + sites count, written into the inserted row | **+6** |
| `data/MonolithRepository.kt` (new fns) | `assemblePendingReceipt` (row read, span samples, deflection reduction ≈30, routine-window evaluation ≈8, legacy resolution ≈6) + `publishReceipt` transaction (§2.3, ≈30) + `rebuildReceiptState` for latch/rehydration (≈14) | **+88** |
| `ui/MonolithViewModel.kt` | `receiptState` StateFlow + mirror (≈8); rehydration on init (≈10); null-on-arm (≈3); publish call at E4's issuance frame (≈3) | **+24** |
| **Production total** | | **≈ +198 gross / +150 net** *(net of Fen's already-priced 2-field ReceiptState stub (~12) and Wren's pending-receipt holder (~36) which this contract's types replace)* |
| Tests (§6) | JVM/Robolectric, trunk's existing suites | **≈ +80** |

No new dependency (Room, StateFlow, `SystemClock` — all in
trunk). No settings surface. No change to `startSession`'s
signature, the ticker, or any service.

---

## 6 — ACCEPTANCE TESTS

- **T1 · Exactly-once publish.** Complete a session; call
  `publishReceipt(id)` twice (and once more after a simulated
  process restart over the same DB). All calls return the
  same `receiptNo`; the store holds one slot; № advanced by
  exactly 1.
- **T2 · Gate.** Valve end, grace cancel, early disarm,
  annulled row: `publishReceipt` returns null in all four;
  store untouched; the next genuine completion takes the
  *next* № with no gap.
- **T3 · Death mid-detention.** E0 observed; process killed
  before issuance; relaunch; E4's formula runs. PendingReceipt
  is VM memory and dies with the process — so the relaunch
  re-derives E0 from the flipped row itself (the flip is
  persisted; only its *observation* died), re-assembles, and
  publishes: the test asserts №-once + publication, and
  *documents* the one drift — `endedAtWallMs` is sampled at
  re-assembly, so the slip's `sealed until` is off by the
  death interval. Named in Risk R3, not asserted away.
- **T4 · Death after publish.** Kill after issuance, before
  E6's deadline: relaunch → flow holds ReceiptState with
  `restored = true`, same №; no Strike key fires; no return
  job is scheduled; sled position derives (§2.4). Reboot
  variant (boot count changed): same, with mono treated void.
- **T5 · Legacy.** A v9-shaped row (column `-1`) restored
  active: imprint prints a non-negative count, identical
  across 50 recompositions and a tier edit mid-session;
  `-1` appears in no rendered string; its completion's
  ReceiptState carries `-1` and the rendered slip contains
  **no apps figure** (Fader omission, §10-Z2) while every
  other provenance line prints normally.
- **T6 · Freeze under edit (F15's core).** Seal with n=10;
  mid-session apply a loadout rewriting every tier; imprint
  still prints 10 at every sample; receipt prints 10 apps.
  Routine-arm variant: seal via `RoutineAlarmReceiver` path;
  column written identically (capture is in `startSession`).
- **T7 · Reduction.** Logs seeded: two same-package events
  10s apart (→ one, first time), one inside a break window
  (→ excluded), one `com.monolith.system` row (→ excluded),
  one outside the sealed window (→ excluded). ReceiptState's
  list matches exactly; reflection asserts `ReceiptState`
  declares no total/count-of-deflections field.
- **T8 · Store cleared.** Fresh DB: first genuine completion
  publishes № 001. Store row deleted over surviving history:
  next completion is № 001 — **not** history-size + 1.
- **T9 · Faulted + death lines.** Release-faulted completion:
  publishes at the E1800 branch with `endedFault = true`,
  `stillBlockedShield = true`, № incremented. Shield-death
  completion: publishes with `shieldWasOff = true`, №
  incremented. Both: E6 deadline computed from the published
  mono, unchanged.
- **T10 · Supersede.** After publish, arm a new session: flow
  nulls at insert-return; **slot still holds the old №/id**
  until the new session completes and overwrites it. Nav-away
  and Deck-return between publish and arm: state and settled
  slip unchanged.

---

## 7 — RISKS

- **R1 · REGISTER append-only vs ADD COLUMN** (§3.4). The
  batch's wording and F15's ruling conflict on paper; the
  build must not discover it at merge. Mitigation is the Saum
  letter + the 10→11 fallback, priced identically. The
  `receipt_store` half is tension-free either way.
- **R2 · The intercept log's session key is a string.**
  `InterceptLogEntity.sessionIntention` is the only join to a
  session; two sessions with the same intention text in
  adjacent windows are separated by the timestamp window
  alone (sessions never overlap — `deactivateAllSessions` at
  `startSession` — so windows are disjoint; sound, but the
  builder must apply BOTH filters, window ∧ intention ∧
  package exclusion, or T7 fails).
- **R3 · `endedAtWallMs` drifts across a mid-detention death**
  (T3). The row carries no completion timestamp and this
  contract does not add one — adding `completedAtWallMillis`
  to the session row (+1 ADD COLUMN, +2 write lines inside
  `updateSessionProgress`'s flip) would close it and is
  **recommended to Ilya as a separable amendment**, not
  smuggled in here: it also gives Draht's fault row a sibling
  timestamp discipline. Until it lands, the drift is bounded
  by the death interval and stated, not hidden.
- **R4 · Routine label evaluated at E0**, against routines as
  they stand then (§1.3). A routine renamed mid-session
  renames the slip's line. Freezing it at seal would cost a
  column + a `startSession` evaluation against a routines
  snapshot the seal does not currently take; the fact at
  stake (window membership of the arm instant) is historical
  and only the *label text* can drift. Accepted, named;
  revisit if the routines rebuild (8→9) makes seal-time
  evaluation free.
- **R5 · № is install-scoped** (§2.5). If Sarthak ever
  expects the counter to survive a phone migration, that is
  a backup/restore feature (the AES-GCM backup's scope),
  not a property this store can claim. Not built, not
  implied.
- **R6 · Fen's convergence sentence** (§0 ruling 3). If the
  studio instead rules valve sessions DO get a receipt, the
  change is one gate line in `publishReceipt` + a
  `bankedBreakSeconds` that suddenly has a nonzero source —
  the contract absorbs it without a shape change. The
  ruling stands until overruled because MASTER §5.7's text
  is explicit.

## 8 — FORKS

**None for Ren.** One recommendation *to Ilya* (R3's
`completedAtWallMillis` column — separable, not a blocker)
and two registrations *to Saum/Fader* by letter (§3.1, §3.4).
F15 is adopted as ruled; F-L1 (the slip's host) is Linea's
fork and does not block this contract — plumbing delivers
the object to whatever room composition is given.

## 9 — MAIL / WALL NOTE

Inbox at seating: **empty** (no unread letters; `read/`
empty) — nothing to move. Letters sent this run: **Fader**
(thread `heldapps-capture-point` — the capture instant, the
DAO source, the two non-sources) and **Saum** (thread
`migration-9-10-receipt-ddl` — the §3.4 SQL verbatim, the
REGISTER tension, the 10→11 fallback). Wall: appended under
my name with the three rulings of §0 and the legacy law.
No code edited; no gradle run — per brief.

## NEXT

(a) **What I want to do next:** own the receipt side of the
build's proof pass — when E4+E6 land, run T1–T10 against the
built tree and file the grep receipt (publish call sites =
1, `ReceiptState` constructors outside the repository = 0)
the way Siegel's T3 pins the seal's singularity. A contract
nobody audits becomes a suggestion.
(b) **What I could do unassigned:** the session record's
rendering of `ENDED BY EMERGENCY BREAK` (§5.7's orphan label
— it belongs to the record, and no seat currently owns where
the record renders it, §9 C-6's vacancy). The slip's
contract already carries every fact that label's surface
would need; I can spec that rendering against the Drawer/
record joint without touching this contract.

— Zettel, Improvers · the slip

---

## 10 — RECONCILIATION (filed same run; SUPERSEDES where it names a section)

Three letters landed while this file was being written —
Fader (0955), Schlitten (1000), and Saum's own part
(15-saum.md) was filed in the same wave. Where they overrule
this file's first form, the amendment below governs; the
original text stands in place so the disagreement stays
auditable.

- **Z1 · The store's home is `guard_settings`, not a new
  table** (supersedes §2.2's `receipt_store` entity and §0
  ruling 2's table claim; the Room-over-DataStore decision
  itself stands). The studio converged while I wrote:
  Nocke (06-nocke.md §3) files the № as
  `guard_settings.receiptSequence INTEGER NOT NULL DEFAULT 0`,
  minted inside his `claimComposite` transaction on the
  singleton row (`MonolithModels.kt:504–508`); Reihe
  (04-reihe.md §3 rule 3) files the slot pointer as
  `guard_settings.lastReceiptSessionId INTEGER NOT NULL
  DEFAULT 0`, written at issuance in the same transaction as
  the counter increment; Saum's 9→10 creates **no table**.
  Adopted whole: **№ = `receiptSequence`; slot pointer =
  `lastReceiptSessionId`; the claim transaction is the
  publish latch** (§2.3's semantics transfer onto Nocke's
  claim: `lastReceiptSessionId == sessionId` ⇒ already
  published, return the existing №, never re-increment).
  My slot's extra sample columns (published wall/mono,
  ended-at wall, routine label) **die**: the mono must never
  persist (Z4), published-wall is printed nowhere (§1.3
  already said so — a field whose only consumer was its own
  storage), ended-at for routine sessions is the row's 8→9
  `windowEndMillis` (Reihe §3 rule 2: "the receipt's printed
  finish is the row's stored windowEndMillis — never the
  observation time"), and the routine label is Z5. What
  persists is exactly: a number, a pointer, an outcome word
  (Nocke's `lastReceiptOutcome`). §2.5's cleared-store law is
  unchanged in substance: row gone / fresh DB ⇒
  `receiptSequence` restarts at 0, next completion № 001,
  never backfilled.
- **Z2 · Fader's −1 law for the receipt: OMIT** (supersedes
  §4.2 rule 4's first form; §4.2 and T5 amended in place).
  The Deck's once-sample latch (Emil/Saum's standing form)
  is Deck-scoped only. `ReceiptState.heldAppsAtSeal` may
  carry `-1`; composition omits the figure. Fader's writer
  law is also adopted verbatim: **exactly one writer**
  (`startSession`, inside a transaction per his filing),
  and **no DAO update for this column will exist** — a
  builder who adds one has broken the contract.
- **Z3 · `heldSitesAtSeal` is a request, not a ride**
  (supersedes §3.4's first form; amended in place). It joins
  9→10 only under Saum's pre-freeze +2-line offer. If it
  does not join: `ReceiptState` carries no sites field and
  the provenance line prints the apps figure alone — the
  sites count is never recomputed live at issuance, in any
  branch.
- **Z4 · Schlitten's trigger clauses C1–C3, adopted**
  (06: his file 13-schlitten.md §3; letter 1000). C1: E4
  calls exactly one entry point — **`MonolithViewModel.
  publishReceipt(sessionId)`** — the VM facade over the
  repository claim transaction; no second path, no direct
  StateFlow writes. C2: the VM samples
  `receiptPublishedMonoMs = SystemClock.elapsedRealtime()`
  **inside that call**, in the same emission that first
  makes the receipt visible; a duplicate call for the same
  sessionId is ignored — the first emission stands (the
  claim latch returning an existing № is a *rehydration*,
  Z6, never a second emission). C3: **the trigger is never
  persisted and never restored** — ReceiptState's clock
  lives in VM memory only. What §2 persists (№, pointer,
  outcome) re-renders the *slip*; it cannot re-arm the
  *sled*. Schlitten's provability law and §5.7's slot law
  are about different objects and both stand.
- **Z5 · The routine line is frozen at insert** (supersedes
  §1.3's E0-evaluation source for `routineLabelWithin` and
  Risk R4, which dies). Reihe §4: the line prints iff the
  row's 8→9 `routineId != null`; its window is the row's
  frozen `windowStartMillis`/`windowEndMillis`; its name is
  stripped from the row's frozen `profileName = "Routine:
  <name>"`. Zero new E columns; a mid-session routine rename
  cannot touch it. **One divergence flagged for Ilya, not
  silently resolved**: Uma/§5.7 also promise the line to a
  *hand-armed* session whose arm falls inside a routine
  window — Reihe's source cannot see that session (its
  `routineId` is null). Until the studio closes it, the
  contract prints the line for routine-materialised
  sessions only, and the hand-armed case carries no line
  rather than a re-evaluated one.
- **Z6 · Schlitten's C4 — the relaunch reading, CONFIRMED
  as his first reading** (answers his §3.4 gap G2; amends
  §2.4/T3). Two disjoint cases: **(a) death before any
  publication** — nothing was ever published, so the
  relaunch's issuance (Reihe's cold-start edge: immediately
  on Deck open, held frame NOT replayed) goes through
  `publishReceipt` as a **fresh publication**: new mono
  sampled in-call, `restored = false`, **E6 arms and
  fires** — lawful, because the receipt genuinely first
  becomes visible in front of a present user. **(b) death
  after publication** — the slot re-renders the slip
  settled (`restored = true`); `publishReceipt` is NOT
  called; E6 cannot arm, because no call exists for it to
  arm from. E6 never has to distinguish the cases: the
  presence or absence of the call *is* the distinction.

Price delta of the reconciliation: **−≈20 net** against §5
(the `receipt_store` entity/DAO lines die; the guard_settings
columns are Nocke's/Reihe's prices, not mine) → **my
increment ≈+130 net production + ≈80 tests**. Letters:
replies to Fader (adoption) and Schlitten (C4 confirmed) on
their threads; a correction letter to Saum withdrawing the
`receipt_store` DDL from my first letter and requesting
`heldSitesAtSeal` under his +2 offer.
