# INNOVATOR — Issue #10, Receipt (audit fixes)

Seat: Innovator, Zettel's cell. Paper only — nothing compiled, nothing run.
Ground: every claim below cites code read in this cell's tree
(`app/src/main/java/com/example/…`, DB version 12) and the canonical audit
`studio/plan/AUDIT-receipt.md` (items 5, 9, 11, 20; cures A1, A4, A9, C1).
TEAR / SLIP BOOK (audit B1/B2, issue #21) are parked and untouched.

Headline finding the audit did not state, which sharpens Problem 1:
there is **no generic "blocked" intercept-log writer in the tree**.
`InterceptLogEntity` rows are written at exactly two places in
`MonolithRepository.kt` — `recordIntercept` (:2104) and the
`com.monolith.system` partial-stamp row inside `disarmActiveSession`
(:2347, already excluded at assembly by package). The accessibility
service and shield service launch the airlock but write no log row.
So a deflection row exists only when the airlock was *resolved*, and
`outcome` names the resolution. The whole vocabulary is three strings,
all in `MonolithViewModel.kt`. That makes an exact allowlist possible
today — no guessing, no new writer needed.

---

## Problem 1 — Deflections print peeks as blocks (Audit item 11 / A4)

### The verified vocabulary

`InterceptLogEntity.outcome` is a free `String`, no enum, no constants
(`data/MonolithModels.kt:452–459`). Every non-system writer, enumerated:

| Outcome literal | Writer | Meaning | Deflection? |
|---|---|---|---|
| `"Used 2m Peek"` | `MonolithViewModel.kt:2412`, `start2MinutePeek()` | Lease granted — **he was LET IN** | **NO — the lie** |
| `"Stayed Focused"` | `MonolithViewModel.kt:2437`, `transmuteImpulseAndReturn()` | Airlock turned him away, he returned | YES |
| `"Split Into 10m Micro-Step"` | `MonolithViewModel.kt:1978`, `splitActiveTaskAndReturn()` | Airlock turned him away, he split the task instead | YES |

Assembly today (`MonolithRepository.assemblePendingReceipt`, :1608–1623)
filters on intention, `packageName != "com.monolith.system"`, break
windows, then 30 s-per-package dedupe — never on `log.outcome`. A peek
row therefore prints identically to a block, *and worse*: because the
dedupe filter is the second `.filter{}` in the chain and mutates
`lastByPackage` as it runs, a peek row can consume the package's 30 s
dedupe slot and swallow a genuine block that follows it inside 30 s.

Also relevant: Safe-Break free opens of PEEK apps and live-lease
revisits write **no** log row (both services return early —
`MonolithAccessibilityService.kt` branches 3–5, `MonolithShieldService`
blocked-set filter). They cannot pollute the slip. The peek *grant* is
the only let-in that ever reaches the log.

### Mechanism (top proposal): fail-closed allowlist at assembly, vocabulary owned beside the entity

1. **Own the vocabulary where the entity lives.** Add to
   `MonolithModels.kt`, directly under `InterceptLogEntity`:

   ```kotlin
   /** The closed outcome vocabulary of InterceptLogEntity — every
    *  writer is named here; an unlisted outcome is unclassified and
    *  prints nowhere (the receipt's omission law applied to rows). */
   object InterceptOutcome {
       const val PEEK_USED = "Used 2m Peek"
       const val STAYED_FOCUSED = "Stayed Focused"
       const val SPLIT_INTO_MICRO_STEP = "Split Into 10m Micro-Step"
       /** Outcomes where the machine turned him away — the only rows
        *  a receipt may list as deflections. */
       val BLOCKED: Set<String> = setOf(STAYED_FOCUSED, SPLIT_INTO_MICRO_STEP)
   }
   ```

2. **Repoint the three VM call sites** to the constants
   (`MonolithViewModel.kt:1978, 2412, 2437` — one line each). Stored
   strings do not change, so existing rows on his phone stay readable;
   this is a rename of the *source*, not of the data. Zero migration.

3. **Filter in the FIRST filter chain** of `assemblePendingReceipt`,
   alongside the intention/package/break predicates:

   ```kotlin
   .filter { log ->
       log.outcome in InterceptOutcome.BLOCKED &&
           log.sessionIntention == row.intention && …
   }
   ```

   Placement is the mechanism, not a detail: the outcome predicate
   must run **before** the dedupe `.filter{}`, so a dropped peek row
   never writes `lastByPackage` and can never eat a real block's slot.
   One predicate, one line, in the chain that already exists.

Why allowlist, not denylist (`outcome != "Used 2m Peek"`): the denylist
is one line cheaper and silently wrong forever — the next let-in
outcome anyone adds (a grace door, a lease variant) would print as a
block by default. The allowlist is fail-closed: a new outcome prints
nowhere until a human classifies it, which is exactly the slip's own
omission law ("an absent line over a false one", audit item 9) applied
to rows, and exactly what the audit demanded ("the outcome vocabulary
must be enumerated in the cure, not assumed"). Filtering at assembly —
not at write time — is deliberate: `recordIntercept` also increments
`interceptCount` and `phantomReachesBlocked` (:2110, :2120); deleting
peek rows at the writer would falsify those ledgers and the 7-day
history. The ledger keeps the peek; the slip stops calling it a block.

Rejected alternative: pushing the predicate into SQL
(`MonolithDao.getInterceptLogsBetween`, :324, `WHERE outcome IN (…)`) —
duplicates the vocabulary as SQL string literals, splits the reduction
logic the DAO comment explicitly assigns to assembly, and makes the
truth table untestable as pure Kotlin.

### Price

- Files: `MonolithModels.kt` (+~14 lines), `MonolithRepository.kt`
  (+1 predicate line, ±0 structure), `MonolithViewModel.kt` (3 × 1-line
  constant swaps).
- Migration risk: **none** — no schema, no stored-value change.
- Tests: one table test over `assemblePendingReceipt` in the existing
  `ReceiptClaimRepositoryTest.kt` harness (it already drives assembly
  at :99–180): seed peek + stayed + split rows inside one window,
  assert peek absent, both blocked rows present, and a peek 10 s
  before a stayed row for the same package does **not** dedupe-swallow
  it. ~35 lines.
- **Size: S (~50 lines total).**

---

## Problem 2 — The slip has no total: `RAN {longSpan}` (Audit item 5 / A1)

### Verified: the data already exists, and what it actually is

`sealedElapsedSeconds` is **not a stored column** — do not let anyone
"add" it. It is the session row's derived span, computed at assembly:

- Derivation: `FocusSessionEntity.spanElapsedSeconds`
  (`MonolithModels.kt:414–428`) — `(currentBlockIndex − 1) ×
  effectiveTargetSeconds + breakTakenSeconds + elapsedSeconds`
  (in-break variant at :416–421). This is the codebase's ONE session-time
  derivation — the Deck, desk clock and receipt all read it, per the
  comment at `MonolithModels.kt:380–385`. `breakTakenSeconds` is real
  stored data (Truth Car 3, :289ff) — breaks contribute seconds
  *actually taken*, never the assumed 600.
- Carriage: `assemblePendingReceipt` sets
  `sealedElapsedSeconds = row.spanElapsedSeconds`
  (`MonolithRepository.kt:1640`) → `PendingReceipt.sealedElapsedSeconds`
  (`ReceiptState.kt:147`) → copied verbatim in `publishReceipt`
  (:1725) and `rehydrateReceipt` (:1777) → `ReceiptState.sealedElapsedSeconds`
  (`ReceiptState.kt:176`).
- Read count by the sheet: **zero**. `ReceiptSheet.kt` never touches it.
  The audit's "assembled, carried, printed nowhere" is exact.

FLOW check: a stopwatch row has `totalStudyBlocks = 1`,
`currentBlockIndex = 1`, `breakTakenSeconds = 0`, so `spanElapsedSeconds`
reduces to `elapsedSeconds` — the true FLOW duration. The total is
lawful on FLOW slips, where it is the headline figure (audit item 5).

Explicitly rejected source: `endedAtWallMs − armWallMs`. Wall difference
prices detention-adjacent gaps, reboots and routine materialisation
offsets as study time, and on routine rows `armWallMs` is the row's
birth, not the window's start. The span derivation exists precisely so
no surface ever does this subtraction.

### Mechanism

In `ReceiptSheet.kt`, directly after the ENDED mono line (:~121),
one line — the formatter is already in the same file
(`longSpan(seconds: Int)`, :202–211, ceiling dialect `(seconds + 59) / 60`,
so `RAN` can never understate the span the Deck printed):

```kotlin
if (receipt.sealedElapsedSeconds > 0) {
    ReceiptMonoLine(text = "RAN ${longSpan(receipt.sealedElapsedSeconds)}")
}
```

The `> 0` guard is the one judgement call, flagged for Zettel: a
clean SLEEP stand-down row can carry `spanElapsedSeconds == 0` (no
booked elapsed), and `RAN 0 MIN` on a slept-through window would be a
figure the machine cannot back — omission law again. If Zettel prefers
the audit's literal "~4 lines, no guard", dropping the guard is a
1-line simplification, not a redesign. Placement under ENDED over a
section rule rides with audit A6's rhythm cure; the line itself does
not depend on it.

### Price

- Files: `ReceiptSheet.kt` only, +4 lines (+5 with the guard as written).
- Migration risk: **none** — no new field, no new formatter, no state change.
- Tests: extend the existing `ScreenshotHarnessTest.kt:624` receipt
  fixture assert / one compose-text assertion that a state with
  `sealedElapsedSeconds = 3000` renders `RAN 50 MIN`. ~10 lines.
- **Size: XS (~5 lines).**

---

## Problem 3 — Dismissal is unpersisted (Audit item 20 interim law / A9)

### Verified current state

- The only dismissal record is host memory: `var dismissedReceiptSessionId
  by remember { mutableStateOf(-1) }` (`MainActivity.kt:~268`). The sheet
  renders iff `dismissedReceiptSessionId != standingReceipt.sessionId`
  (:777–778); `onDismiss` (:783) and the nav/PARK `LaunchedEffect`
  (:~307) write only that `remember`.
- The VM's `_receiptState` is cleared only at the next arm
  (`MonolithViewModel.armMonolithSession`, :2200).
- Cold start resurrects unconditionally: VM init →
  `runSliceERecovery()` (:1099–1102) → `repository.rehydrateReceipt()`
  (`MonolithRepository.kt:1753`) → sets `_receiptState` if null. No
  dismissal check exists on this path — the audit's "re-ambushes at
  cold start" is literal.

### Mechanism (top proposal): a №-keyed SharedPreferences fact, gated at rehydration only

Room is the wrong store for the smallest cure — `guard_settings` is a
Room entity, so a column there costs a DB 12→13 migration. The codebase
already has a settled pattern for exactly this shape of fact — one
small durable value that must survive process death and be readable
synchronously: `SoundEffectsSetting` (`service/SoundEffectsSetting.kt`,
`commit()` never `apply()`, per the G-02 law it documents),
`UndoDeleteStore` (`data/UndoDeleteStore.kt`), the seal marker and the
park-intro bit (`MonolithRepository.kt:189, 454`). Follow it:

1. **New tiny store** `data/ReceiptDismissalStore.kt` (~40 lines),
   an `object` in the `SoundEffectsSetting` shape:
   prefs file `monolith_receipt_dismissal`, keys
   `dismissed_number: Int`, `dismissed_session_id: Int`,
   `dismissed_arm_wall_ms: Long`.
   `markDismissed(context, receipt)` writes all three with `commit()`;
   `isDismissed(context, number, sessionId, armWallMs)` reads them.
   The triple is the slip's exact identity (№ from the claim, session
   id, `armWallMs = row.startWallTimeMillis`): a stale store can never
   suppress a *different* future slip, even across a DB wipe/restore
   skew where № and session id both restart — the arm instant cannot
   collide. One value would do 99% of the job; the triple makes the
   failure mode impossible for ~6 extra lines.
2. **Gate rehydration only** — in `rehydrateReceipt()`, after
   `standingReceipt()` and the row read (the row supplies `armWallMs`):

   ```kotlin
   if (ReceiptDismissalStore.isDismissed(context, number, sessionId, row.startWallTimeMillis)) return null
   ```

   Deliberately **not** gated: `standingReceipt()` itself and
   `publishReceipt`'s once-gate (:1711), which reads the standing
   pointer to stop a second mint — dismissal must not reopen minting.
   A fresh publish in the same process is a *new* №/session/arm
   triple, so it can never match the stored dismissal; no clearing
   step is needed anywhere.
3. **Write at the existing dismissal acts.** Add
   `MonolithViewModel.dismissReceipt(receipt)` (~6 lines; the VM
   already holds `appContext`, :204, and already owns an
   `UndoDeleteStore` instance the same way, :282) that calls
   `markDismissed`. `MainActivity` calls it at the two places that
   already set the host `remember`: the sheet's `onDismiss` (:783)
   and the nav/PARK `LaunchedEffect` (:~307) — +1 line each. The host
   `remember` stays: it is the in-process hide; the store is the
   cross-process hide. Neither replaces the other.
4. **When it clears:** never, explicitly. № (`receiptSequence`) is
   monotonic for the life of the install, so a stored dismissal is
   superseded by the next slip's higher № automatically; overwriting
   happens at the next dismissal. No clear-on-arm hook, no TTL, no
   migration-time cleanup. The faulted slip (audit item 19: dismisses
   only on an explicit act) flows through the same `onDismiss`, so its
   persistence is identical with zero special-casing.

Priced alternative, rejected: `guard_settings.dismissedReceiptNumber`
column — entity +1 field, `MIGRATION_12_13`, DAO targeted UPDATE,
repository gate, VM, host ≈ 6 files and a new link in the migration
chain tests, to store the same integer in a slower store. Its only
win (travels with DB backups) is already covered by the identity
triple. If Ren ever wants dismissal inside Room for its own sake,
the gate and call sites built here do not move — only the store's
backing changes.

### Price

- Files: **new** `ReceiptDismissalStore.kt` (~40 lines),
  `MonolithRepository.kt` (+~4 lines, one gate),
  `MonolithViewModel.kt` (+~6 lines), `MainActivity.kt` (+2 lines).
- Migration risk: **zero** — no DB version bump, no entity change.
  Write is `commit()`-synchronous on a dismissal tap (3 primitives;
  the G-02 precedent accepts exactly this cost for facts that must
  survive death).
- Tests: store round-trip + gate test in `ReceiptClaimRepositoryTest`
  style — dismiss № N, rehydrate → null; publish № N+1, rehydrate →
  slip. ~30 lines.
- **Size: S (~52 lines + ~30 test).**

---

## Problem 4 — HELD line: build the print side NOW for W-41's split columns (Audit item 9 / C1)

### Verified current state

- The lump is end-to-end: `FocusSessionEntity.heldAppsAtSeal: Int = -1`
  and `heldSitesAtSeal: Int = -1` (`MonolithModels.kt:332–333`), written
  once in `startSession`'s birth transaction from
  `countHeldApps(apps) = apps.count { resolvedTier != OPEN }`
  (`MonolithRepository.kt:1318, 1384–1385`) — LOCKED + PEEK_2M summed —
  and `heldSites = dao.getAllSitesSnapshot().size` (list size, blind to
  the `browserBlockerEnabled` toggle).
- The lump flows through `PendingReceipt` (`ReceiptState.kt:148–149`)
  → `ReceiptState` (:177–178) via verbatim copies in
  `publishReceipt` (:1726–1727) and `rehydrateReceipt` (:1778–1779),
  and prints at `ReceiptSheet.kt:129–133` iff both figures `>= 0`.
  **These are the only consumers of either column in the tree** (grep-
  verified) — the blast radius of a contract change is fully known.
- The truthful split already exists as a *live* derivation, twice:
  `List<BlockedAppEntity>.groupedByRule()` (`data/AppRules.kt:21–26`,
  `LOCKED → shut`, `PEEK_2M → peek`) and the VM's `AppsSplit`
  (`MonolithViewModel.kt:553–565`), and its print grammar already
  exists: `splitTenantText` (`ui/components/MonolithCustomComponents.kt:477–486`)
  — sealed tense `"{n} HELD · {m} PEEK"`, zero segment never prints,
  both zero → nothing. W-41's job is only to *persist* that split at
  seal (`heldShutAtSeal` / `heldPeekAtSeal`); the print side should not
  have to be touched when it lands.

### Mechanism: one value object at the contract layer, one adapter at assembly

Replace the two raw `Int`s in the receipt contract with a single
nullable-parts value, defined in `ReceiptState.kt` beside
`ReceiptDeflection`:

```kotlin
/** What the shield held at seal, split by ruling. A null part =
 *  uncaptured / unprovable — the line omits rather than guess.
 *  [line] is the ONLY print derivation: it exists and is tested
 *  now, while every part is still null in production. */
data class HeldAtSeal(
    val shut: Int?,
    val peek: Int?,
    val sites: Int?
) {
    /** "HELD 7 SHUT · 2 PEEK · 8 SITES" — zero parts drop out;
     *  shut unknown → no line (the HELD claim is the shut claim);
     *  all-zero → no line. Mirrors splitTenantText's omission law. */
    fun line(): String? { … }
}
```

Then, mechanically:

1. `PendingReceipt.heldAppsAtSeal/heldSitesAtSeal` →
   `held: HeldAtSeal`; same swap in `ReceiptState`. The two copy
   blocks in `publishReceipt` / `rehydrateReceipt` shrink from two
   lines to one and never change again.
2. **The adapter — the single seam W-41 will touch.** In
   `assemblePendingReceipt`, replace the two field reads with one
   construction:

   ```kotlin
   // NOW (pre-W-41): the split is unprovable from the row, so shut
   // is null and line() omits — the audit's interim law, enforced
   // by construction rather than by a sheet-side if.
   held = HeldAtSeal(shut = null, peek = null, sites = null)
   ```

   When the columns land, this one expression becomes
   `HeldAtSeal(row.heldShutAtSeal.takeIf { it >= 0 }, row.heldPeekAtSeal.takeIf { it >= 0 }, <enforcer-derived sites>)`
   — a ~3-line change in one function. The legacy lumped
   `heldAppsAtSeal` is **never** split retroactively: a sum cannot be
   un-summed, and pre-W-41 rows keep omitting the line forever. That
   is the truthful price, stated now.
   (Sites rides the same seam: today's `heldSitesAtSeal` is a blind
   list size — toggle OFF still books every site — so the adapter
   passes `sites = null` until W-41's enforcer-derived figure exists.
   Post–Truth-Car-3 the stored list *is* the enforced list
   (`MonolithAccessibilityService.kt`, branch 4 comment), so the
   future sites figure is `if (browserBlockerEnabled) list.size else 0`
   captured at seal — but that capture is W-41's writer work, not
   smuggled in here.)
3. **Sheet:** the `if (heldAppsAtSeal >= 0 && …)` block becomes

   ```kotlin
   receipt.held.line()?.let { ReceiptMonoLine(text = it) }
   ```

   From the day this lands, the HELD line is omitted *by construction*
   (audit item 9's interim ruling, currently unimplemented — the lump
   still prints today). When W-41 flips the adapter, the line appears
   in its final grammar with **zero sheet, state, or copy-site changes**
   — no second rewrite, which is the brief.

Why a value object and not "just add two nullable fields now": two more
raw fields re-scatter the omission logic into the sheet (another
`>= 0` guard pair, another grammar string) and W-41 would still have
to touch `ReceiptState`, both copy sites, and the sheet. The object
concentrates everything W-41 must *not* touch into one tested `line()`,
and everything it *must* touch into one adapter expression.

### Price

- Files now: `ReceiptState.kt` (+~25 lines for `HeldAtSeal`, 2 ×
  2-field→1-field swaps), `MonolithRepository.kt` (adapter ~4 lines;
  2 copy blocks 2→1 lines each; `startSession`'s birth capture and
  the entity columns untouched), `ReceiptSheet.kt` (5-line block →
  2 lines).
- Migration risk: **none now** — no schema change in this lane.
  W-41 later pays its own 12→13 (or later) migration for the two
  columns + the `startSession` writer; this lane makes that the
  *entirety* of W-41's receipt-side cost (~3 adapter lines + its
  writer), instead of a second print rewrite.
- Tests: table-test `HeldAtSeal.line()` exhaustively now — null shut →
  null; shut 0/peek 2 → peek-only line; all zero → null; full triple →
  full line (~30 lines, pure, no Room). Assembly test asserts today's
  adapter yields `line() == null` on a real sealed row (~10 lines).
  When W-41 lands, only the adapter test flips.
- **Size: S–M (~55 lines + ~40 test).**

---

## Totals

| # | Mechanism | Files | Prod lines | Test lines | Migration |
|---|---|---|---|---|---|
| 1 | `InterceptOutcome.BLOCKED` allowlist in assembly's first filter chain | 3 | ~18 | ~35 | none |
| 2 | `RAN {longSpan(sealedElapsedSeconds)}` in the sheet | 1 | ~5 | ~10 | none |
| 3 | №/session/arm-triple prefs store, gate at `rehydrateReceipt` only | 3 + 1 new | ~52 | ~30 | **zero** |
| 4 | `HeldAtSeal` value object + single assembly adapter (W-41 seam) | 3 | ~55 | ~40 | none now |

**Total estimated size: ~130 production lines across 8 files (1 new),
~115 test lines, zero migrations, zero new surfaces.** All four land
independently in any order; #4's adapter and #1's predicate both live
in `assemblePendingReceipt` but touch disjoint lines. Nothing here
compiles or runs under this seat — builder verifies.

— Innovator, Zettel's cell, issue #10.
