# Issue #16 — Pill Lane: Verification + Merge Presentation Design

**Author:** Designer, Pastille's cell (issue #16) — design only. No compiling, no test runs, no Gradle executed for this note.
**For:** Pastille (cell lead) → the verification-plan document that will be posted on issue #16 of `Sarthak-2007/Focus-app`.
**Companion in this cell:** `innovator-clean-shot.md` (execution design: shard the run, harvest XML per shard, pre-flight the environment). This note is the layer on top of it — **how the results and the two merge flags are presented so a reviewer signs in one read.** Where the two overlap (verdict taxonomy), this note adopts the Innovator's terms verbatim; do not invent a second vocabulary.
**Date:** 2026-10-30

**Inspected (read-only), pill lane `~/workspace/clone-inspect/builds/pill/Focus-app/`:**

| File | What was taken from it |
|---|---|
| `app/src/main/java/com/example/service/PillFace.kt` | `PillFace` / `PillDerivation.derive` (L60–155), the "one derivation, two mouths" contract |
| `app/src/main/java/com/example/service/PillController.kt` | Route A window, 1 s ticker (L96–131), `✕` → `recordPeekEnded` (L288–292), body-tap / drag / snap |
| `app/src/main/java/com/example/service/PillTheme.kt` | `PillPalette` / `PillPalettes.Default`, `PillTheme.palette` as data |
| `app/src/main/java/com/example/data/EditableTimes.kt` | Pastille's seam: `EditableTimesProvider(dao)` (L31), two suspend fns returning shipped constants (L38, L44) |
| `app/src/main/java/com/example/data/MonolithRepository.kt` | provider instance L93–95; `startPeekLease` L1180–1192; `recordPeekEnded` L1198–1205; constants L98–103 |
| `app/src/main/java/com/example/data/MonolithDao.kt` | `updatePeekLeaseAndCooldown` L32; `clearOtherPeekLeases` L38–42 |
| `app/src/main/java/com/example/service/MonolithShieldService.kt` | Route B: `ACTION_END_PEEK` handler L38–58; peek face L160–191; `END PEEK` action L325–343; pill ownership L34, L111–119; pill death in `onDestroy` |
| `app/src/main/java/com/example/ui/MonolithViewModel.kt` | pill-lane `start2MinutePeek` L2155–2193 (incl. ST-08 block); pill-lane guard in `triggerAirlockIntercept` L2128–2131 |
| `app/src/test/java/com/example/PillDerivationTest.kt` | 15 tests, L71–233 |
| `app/src/test/java/com/example/PillLeaseRepositoryTest.kt` | 3 tests, L55–143 |
| `assemble.log` / `test.log` / `test-run.log` (lane root) | the evidence history in §2.1 |

**Cross-checked against the merge targets:** batch-9 merge tree `builds/merge-batch9/` (DB **v12**, `MonolithDatabase.kt:25`; contains **no** `Pill*.kt` and **no** `EditableTimes.kt` — the pill is entirely additive there) and Frist's slice `builds/truth-3-times/` (`EditableTimes.kt`, `MonolithModels.kt` GuardSettings L819–833, `MonolithRepository.kt:1377`).

---

## 0. The one-read sign-off sheet (what page 1 of the plan must look like)

The whole design principle: **the reviewer never reads a log, a diff, or a second document to reach a decision.** Page 1 of the verification plan on #16 is a single sheet with exactly four blocks, in this order — verdict first, flags second, honesty third, signature last:

```markdown
## #16 PILL — SIGN-OFF SHEET
Status today:  NO_VERDICT 18/18  ·  assemble: GREEN (17m 43s, assemble.log)  ·  merge-ready: NO

[1] TEST VERDICT            A Absence  ?/5   B Time maths  ?/6   C Peek face  ?/4   D Lease writes  ?/3
[2] MERGE FLAG A  start2MinutePeek composition   ☐ SIGN  ☐ SIGN WITH A-3 DECIDED  ☐ REFUSE
[3] MERGE FLAG B  EditableTimesProvider swap     ☐ SIGN B-peek  ☐ SIGN B-break  ☐ DEFER B-break  ☐ REFUSE
[4] NOT PROVED BY ANY TEST  Controller window · Route B notification · ST-08 launch · PillTheme — inspection only (§4)

Signed: __________   Date: ______   Next action if unsigned: <one line>
```

Rules for this sheet:

1. **`NO_VERDICT` is the headline until XML says otherwise.** Today all 18 tests are `NO_VERDICT` — not failed, not passed. The sheet must never render the current state as "0/18 failed" or "tests pending = probably fine." The three deaths were environmental (§2.1); a fourth outcome — a real test failure — has simply never been observed.
2. **Four category counters, never one "18/18".** A single number hides *which* law is unproved. The counters map 1:1 to §3's categories and to the Innovator's shards (`D1-absent` = A, `D2-time` = B, `D3-peek` = C, `L-lease` = D), so a `PARTIAL 11/18` result lands on the sheet as e.g. `A 5/5 · B 6/6 · C ?/4 · D ?/3` — the reviewer sees instantly that time maths is proved and the lease writes are not.
3. **Flags are signed separately from tests.** A green suite does not auto-sign either flag: neither flag's composition is exercised by any of the 18 (§5.3, §6.3). Conversely a refused flag does not block banking a test verdict. Separate checkboxes, separate decisions.
4. **Block [4] is mandatory, not a footnote.** If a surface has no test, the sheet says so on page 1 — otherwise "18/18 PASS" will be over-read as "the pill works."

---

## 1. What the verification-plan document must contain (section map)

Nine sections, in this order. Each entry: what goes in it, and what decision it lets the reviewer make.

| # | Section | Contents | Reviewer can decide |
|---|---|---|---|
| 1 | **Sign-off sheet** | §0 above, filled with the run's actual numbers | Sign / refuse / defer, in one read |
| 2 | **Attempt ledger** | One row per attempt ever made (§2.1): date, scope, how it died, artefact link. Includes the 3 dead attempts — deleting them would make attempt 4 look like attempt 1 | "Is this lane flaky, or was the box?" |
| 3 | **The 18, grouped** | §3's four category tables: test name, file:line, what PASS proves, merge consequence of FAIL | Which failures block merge vs. which block only a claim |
| 4 | **Coverage honesty map** | §4's table: every pill surface × (tested by / inspection only / unproved) | What "verified" does **not** mean here |
| 5 | **Run protocol (pointer)** | One paragraph + link to the Innovator's shard/harvest/pre-flight design in this cell. The plan does not re-specify execution; it specifies how execution results are *reported back into §1 and §3* (per-shard XML → category counters; infra deaths named, e.g. `INFRA_DAEMON_GONE`, stamped on the shard, never on the tests) | That the next run's output will be legible |
| 6 | **Flag A — composition** | §5: side-by-side anatomy, composed sequence, invariant checklist, sign box | Sign the composition without reading two ViewModels |
| 7 | **Flag B — seam swap** | §6: swap-diff table, consumer inventory, B-peek / B-break split, dependency list, sign boxes | Sign the peek half and the break half independently |
| 8 | **Merge manifest** | §7: exact file list in / modified / untouched, DB-version note, merge order | That nothing outside the manifest changes |
| 9 | **Open items & residual risks** | §8: A-3 legacy-pill-state question, Route B derivation-drift risk, Controller-on-device check. Each item: owner + what closes it | What is knowingly being carried past merge |

Presentation rules for the whole document: every claim carries a `file:line`; every number carries its artefact (log/XML) name; no claim is phrased stronger than its evidence class — `PASS` (XML), `INSPECTION` (read the code, §4), `UNPROVED` are the only three evidence words used, everywhere.

---

## 2. Ground facts the presentation must not blur

### 2.1 Attempt ledger (as it stands today — the plan reproduces this table and appends to it)

| Attempt | Scope | Outcome | Artefact |
|---|---|---|---|
| Assemble | `:app:assembleDebug` (JDK17) | **GREEN** — `BUILD SUCCESSFUL in 17m 43s`, main + test sources compiled (`:app:compileDebugUnitTestKotlin` also completed inside the test run) | `builds/pill/assemble.log` |
| Test 1–2 | `:app:testDebugUnitTest` full suite | Died to the environment; no test XML harvested | (superseded logs — itself a defect the new protocol fixes by never overwriting: runs go to `runs/<run-id>/`) |
| Test 3 | `:app:testDebugUnitTest` full suite | Reached `:app:testDebugUnitTest`, then **"Gradle build daemon disappeared unexpectedly"** (daemon pid 53253) — death *after* compilation, *before* any verdict | `builds/pill/test.log` |
| Test re-run script | `test.sh` | `test-run.log` is **0 bytes** — the attempt left no evidence at all | `builds/pill/test-run.log` |

Consequence for presentation: the only honest summary line today is
**"Code compiles. 18 tests written. 0 tests have ever reported. 0 tests have ever failed."**
All three clauses go on the sign-off sheet. Dropping the third clause is how `NO_VERDICT` gets misread as failure; dropping the second is how it gets misread as success.

### 2.2 Version / tree facts

- Pill lane is built on **DB v11** (`MonolithDatabase.kt:25` in the pill tree: `version = 11`). The batch-9 merge tree is already **DB v12**. The pill lane therefore brings **no migration of its own** — but Flag B's provider reads columns that only Frist's 11→12 work creates (§6.4). The plan must state this explicitly, or a reviewer will hunt for a pill migration that does not exist.
- Package is `com.example` in both trees; every pill file lands at the identical path. No renames in the manifest (§7).

---

## 3. The 18 tests — four verdict categories

Categories are by **the law being proved**, matching the Innovator's shards so results paste straight in. For each category: the merge consequence is stated *before* the run, so a failure is interpreted by a rule written in advance, not negotiated after.

### Category A — ABSENCE gates (5 tests, pure JUnit) — *the pill must not appear*

`PillDerivationTest.kt`, `PillDerivation.derive` in `PillFace.kt:82–95`.

| Test | Line | Proves |
|---|---|---|
| `noSessionIsAbsent` | L71 | No session row → `Absent` |
| `inactiveSessionIsAbsent` | L76 | Inactive session → `Absent` |
| `ownPackageIsAbsent` | L81 | §2.4 Q1 hard rule: foreground = Monolith itself → `Absent` |
| `telecomForegroundIsAbsent` | L87 | §2.4 Q4: `com.android.incallui` / `com.google.android.dialer` foreground → `Absent` |
| `keyguardIsAbsent` | L94 | §2.4 Q2: lockscreen up → `Absent` |

- **Character:** plain JUnit, no runner, no Android, no DB — cheapest, most deterministic verdict in the lane.
- **PASS proves:** the derivation's three suppression gates in priority order. **PASS does not prove:** that `PillController` actually removes the window (`PillController.kt:139`) or that the foreground read feeding it is right — both are §4 inspection items.
- **FAIL consequence: BLOCKS MERGE.** A pill that draws over Monolith, a call, or the lockscreen is a warrant lie on the glass; there is no partial credit for this category.

### Category B — TIME maths from stored ends (6 tests, pure JUnit) — *the face is recomputed, never counted*

`PillDerivationTest.kt`; derivation in `PillFace.kt:100–154` + `elapsedAt` L160–165, house fn `breakRemainingAfter`.

| Test | Line | Proves |
|---|---|---|
| `breakOutranksPeek` | L100 | Break face wins over a live lease (no fuse on an open door); 600 s − 30 s = `Break(570)` |
| `breakRemainingEqualsTheHouseDerivationAtEverySample` | L114 | Break remaining ≡ `breakRemainingAfter(600, t0, t0+offset)` at 7 sampled offsets incl. past-end |
| `stopwatchShowsElapsedFromTheStoredPair` | L128 | Stopwatch elapsed = stored elapsed + whole seconds since stored tick; sub-second credits nothing |
| `timedShowsNowFrameRemainingAndBlock` | L145 | Timed remaining = target − elapsed-at-instant (1190 s), block index/count carried (2/4) |
| `unstampedTickCreditsNothingBeyondTheStoredFact` | L160 | `lastTick = 0` (pre-first-tick row) credits 0 beyond stored elapsed |
| `remainingIncludesThePenalty` | L167 | Effective target = target + penalty (1500 + 900 − 300 = 2100) |

- **PASS proves:** one clock (caller's `nowMono`), all numbers derived from Room-row ends at the sampled instant — the "two clocks disagree" disease is disproved by construction, not by timing luck.
- **FAIL consequence: BLOCKS MERGE** for the failing row's face only — the plan reports B per-test, so e.g. a penalty-only failure blocks the timed face's claim, not the (separately proved) break face. The category counter on the sheet is therefore always accompanied by the per-test table in §3 of the plan; the counter alone never carries a B verdict.

### Category C — PEEK face (4 tests, pure JUnit) — *the fuse names its app and dies on time*

`PillDerivationTest.kt`; lease scan in `PillFace.kt:113–127`.

| Test | Line | Proves |
|---|---|---|
| `peekFaceNamesTheRowAndDiesExactlyAtItsEnd` | L179 | `Peek(pkg, name, 120)`; ceiling rounding — last partial second reads `1`; at the end instant the face reverts to `Session`, never `0` |
| `peekFaceFollowsHimWhereverHeWanders` | L198 | §2.4 Q5: peek face is shown whatever the foreground app; face names the *peeked* app, not the foreground one |
| `twoLiveLeasesResolveToTheLaterEnd` | L207 | Belt-over-braces: if storage ever held two live leases, `maxBy` end wins |
| `faceEqualsStoredEndsAtEverySampledInstant` | L219 | The load-bearing property: fixed stored ends × 6 sampled instants → remaining = `(end − now + 999)/1000` every time, no state carried between samples |

- **FAIL consequence: BLOCKS MERGE.** C is the pill's reason to exist; a fuse that dies early, reads 0, or names the wrong app is worse than no pill.

### Category D — LEASE writes (3 tests, Robolectric + Room) — *the write side keeps the laws C assumes*

`PillLeaseRepositoryTest.kt` (`@RunWith(RobolectricTestRunner)`, `@Config(sdk=[36])`, real `MonolithDatabase.getInstance`, packages unique per test against the shared sandbox DB — class doc L27–29). Code under test: `MonolithRepository.startPeekLease` L1180–1192, `recordPeekEnded` L1198–1205, `MonolithDao` L32/L41.

| Test | Line | Proves |
|---|---|---|
| `grantStampsEndAndPreArmsCooldown` | L55 | Grant end ≈ now + 120 s (asserted window 100–120 s); `lastPeekEndElapsedRealtime` pre-armed to the lease's own end |
| `grantZeroesEveryOtherRowsLease` | L77 | Second grant zeroes the first row's lease **but leaves its earned cooldown anchor standing**; bystander untouched; exactly 1 live lease in storage |
| `handBackZeroesTheLeaseAndAnchorsCooldownToNow` | L120 | `recordPeekEnded` (the pill `✕` verb and Route B's `END PEEK` verb) zeroes the lease and re-anchors cooldown to the hand-back instant, *not* the granted end |

- **Character:** the heavy verdict — Robolectric sandbox + Room on the 2-core/7,935 MB box. This is the category most exposed to another environmental death, and the one whose `NO_VERDICT` must be most visibly distinguished from `FAIL` on the sheet.
- **PASS proves:** grant / exclusivity / hand-back semantics **at today's defaults** (120 s via `EditableTimesProvider` returning `PEEK_DURATION_MILLIS`, `EditableTimes.kt:38`). **PASS does not prove:** behaviour at any other `peekMinutes` — that is `EditableTimesTruthTest` FT-3/FT-4 territory in Frist's lane, and the plan must cross-reference it under Flag B rather than implying D covers it.
- **FAIL consequence: BLOCKS MERGE.** D is the storage law both faces read from; C-without-D is a proved reader of an unproved writer.

**Verdict taxonomy (adopted from the Innovator's design, printed in the plan so nobody re-litigates it mid-run):** per test — `PASS` / `FAIL` / `ERROR` from harvested JUnit XML only, `NO_VERDICT` when no XML entry exists; per run — `COMPLETE` iff the harvested names reconcile exactly against the expected-18 manifest, else `PARTIAL k/18` with missing names listed; infrastructure deaths (`INFRA_DAEMON_GONE`, `INFRA_RAM`, `INFRA_FILTER`) stamp the *shard*, never the tests inside it.

---

## 4. Coverage honesty map (plan §4 — what the 18 do not touch)

| Surface | File | Evidence class | What the plan must say |
|---|---|---|---|
| `PillDerivation.derive` | `PillFace.kt:60–165` | **TEST** (A+B+C, 15 tests) | Proved by §3, once run |
| Lease grant / hand-back writes | `MonolithRepository.kt:1180–1205`, `MonolithDao.kt:32–42` | **TEST** (D, 3 tests, defaults only) | Proved at 120 s only |
| `PillController` — window create/remove, `canDrawOverlays` gate (L139), render-key short-circuit (L147), drag + edge snap, body tap → `MainActivity`, `✕` → `onHandBack` (L288–292) | `PillController.kt` (whole file) | **INSPECTION** | No test constructs it. Plan carries a read-through checklist (window dies on `Absent`, on grant loss, and in `stop()` L106–119) + one on-device check post-merge: arm, peek, drag, hand back, revoke overlay grant mid-peek |
| Route B — shield notification peek face, chronometer, `END PEEK` action → `ACTION_END_PEEK` → `recordPeekEnded` | `MonolithShieldService.kt:38–58, 160–191, 298–343` | **INSPECTION** | No test builds a notification. Checklist: peek subtitle format `Peeking at %s • %02d:%02d left`, action present **only** when `peekPackage != null`, handler touches nothing else (no loop restart — L38–57) |
| ST-08 — post-grant launch to the peeked app | `MonolithViewModel.kt:2179–2189` (pill lane) | **INSPECTION** | No test can launch an activity meaningfully here. Its safety is structural (grant-before-launch ordering, `runCatching`) — which is exactly Flag A's checklist (§5) |
| `PillTheme` / `PillPalettes.Default` (night tokens, accent `#F47442`, never green) | `PillTheme.kt` | **INSPECTION** | Palette is data; the check is a read: 5 ARGB values vs. trunk night tokens, Anonymous Pro via `R.font.anonymous_pro` with monospace fallback |
| `EditableTimesProvider.breakDurationSeconds()` | `EditableTimes.kt:44` | **UNPROVED in this lane** | In the pill tree this function has **no consumer** (§6.3). The plan must not list break editability as pill-proved under any wording |
| "One derivation, two mouths" — Route A and Route B printing the same time | `PillFace.kt:1–14` vs `MonolithShieldService.kt:162–191` | **INSPECTION — with a drift warning** | Route B does **not** call `PillDerivation.derive`; the shield re-implements the priority (break → lease → session) and the ceiling formula `(+999)/1000` inline at L172–181. Today they agree — by mirroring, not by shared code. See §8, risk R-1 |

---

## 5. Merge Flag A — `start2MinutePeek` composition

### 5.1 How the plan presents it: anatomy table, then ONE composed sequence

The flag exists because the two trees each hold half the verb, and neither half is optional:

| Element | Pill lane (`MonolithViewModel.kt:2155–2193`) | Batch-9 tree (`MonolithViewModel.kt:2384–2413`) |
|---|---|---|
| Pre-grant guard at grant time | **Absent.** `start2MinutePeek` trusts the airlock state; the only guard is at *intercept* time (`triggerAirlockIntercept` L2128–2131: tier == `PEEK_2M` && cooldown == 0 && no pill active → `canUse2MinutePeek`) | **Suki's second gate (UNA2-01):** re-derives the offer from the *current* row/cooldown/pill via `peekOfferFor(state.interceptPath, entity, cooldownNow, …)`; refusal `return`s and **leaves the airlock standing** |
| Blank-package behaviour | `interceptedPackageName.ifBlank { "com.whatsapp" }` — a blank silently grants a lease on an unrelated app | `val pkg = state.interceptedPackageName` — no default; the guard's `entity == null` path refuses first |
| Grant write | `repository.startPeekLease(pkg)` — first statement in the coroutine (L2168) | Same call, same position (after the guard) |
| **ST-08 post-grant launch** | **Present** (L2172–2189): after the grant, `runCatching { getLaunchIntentForPackage(pkg)?.addFlags(NEW_TASK) → startActivity }`, then `recordIntercept(…, "Used 2m Peek", …)` | **Absent.** Coroutine is grant → `recordIntercept` only |
| Legacy in-memory pill state | Sets `_floatingPeekPill` with `secondsRemaining = PEEK_DURATION_SECONDS` (L2162–2167) | Same set, same constant |

### 5.2 The composed verb the reviewer is signing (numbered — the plan prints exactly this)

1. Read airlock state. **(Suki)** Re-derive the offer from current facts: `entity = apps.find { it.packageName == state.interceptedPackageName }`, `cooldownNow` from the repository, pill-active flag → `peekOfferFor(...)`.
2. **If refused: stop.** No sound, no state change, no lease, no launch, no intercept log. The airlock stays on screen. *(This line is the guard's whole value — it must be checkable in the merged code as a bare `return` before any mutation.)*
3. If granted: latch sound, dismiss airlock, set `_floatingPeekPill` (see open item A-3, §8).
4. In `viewModelScope.launch`, **in this order**: ① `repository.startPeekLease(pkg)` — the lease is *written* before anything visible happens, so the shield admits the app he lands in; ② **(Pastille, ST-08)** `runCatching` launch to `pkg` — no launcher entry or a refused launch leaves him standing, lease running, exactly as pre-ST-08 behaviour; ③ `repository.recordIntercept(..., outcome = "Used 2m Peek")`.
5. `pkg` is `state.interceptedPackageName` **with no `ifBlank` default** — Suki's semantics win over Pastille's on this line, and the plan says so in words, because it is the one place the composition *deletes* pill-lane behaviour rather than adding to it.

### 5.3 Invariant checklist + sign box (plan §6 ends with this, verbatim)

- ☐ Guard executes **before** every mutation (sound, airlock dismiss, pill state, coroutine) — not merely before the lease.
- ☐ Lease-before-launch ordering preserved; launch is inside the same coroutine, after the grant, wrapped in `runCatching`, `FLAG_ACTIVITY_NEW_TASK` set.
- ☐ Refusal path is side-effect-free and leaves the airlock visible.
- ☐ No `com.whatsapp` (or any) blank-package fallback survives anywhere in the merged function.
- ☐ `recordIntercept` fires exactly once, after the launch attempt, with outcome `"Used 2m Peek"`.
- ☐ Intercept-time first gate (`peekOfferFor` at `triggerAirlockIntercept`) and grant-time second gate are the *same* pure function — composition does not fork it.

**Sign:** ☐ SIGN composition as §5.2 ☐ SIGN with A-3 decided the other way (§8) ☐ REFUSE — reason: ______

*Why tests don't sign this:* none of the 18 calls `start2MinutePeek`. D proves the repository verb it calls; the composition itself is an **INSPECTION** verdict, and the sheet (§0) keeps it in the flag block, not the test block, for exactly that reason.

---

## 6. Merge Flag B — Frist's `EditableTimesProvider` seam swap

### 6.1 How the plan presents it: the swap is one file, zero consumer edits — prove it with the diff shape

Pastille's seam (`builds/pill/…/data/EditableTimes.kt`, 45 lines) was written *for* this swap: its header (L1–29) is the contract, and Frist's fulfilment (`builds/truth-3-times/…/data/EditableTimes.kt`) states the same in its header — "same class name, same constructor, same two suspend functions, so every consumer … composes unchanged." The plan presents the swap as a 6-row table; if any row ever stops being true, the flag is no longer a swap and must be re-presented as a change:

| Contract point | Pastille (pill) | Frist (truth-3-times) | Changed? |
|---|---|---|---|
| Class + package | `com.example.data.EditableTimesProvider` | identical | No |
| Constructor | `(dao: MonolithDao)` — dao `@Suppress("unused")` (L31–33) | `(dao: MonolithDao)` — dao now actually read (L34–36) | Signature: no. Body use: yes, that's the point |
| `peekDurationMillis(): Long` (suspend) | `= MonolithRepository.PEEK_DURATION_MILLIS` (120 000) — L38 | `dao.getGuardSettingsSnapshot() ?: GuardSettingsEntity()` → `peekMinutes.coerceIn(0, 30) * 60_000` — L41–45 | Body only |
| `breakDurationSeconds(): Int` (suspend) | `= MonolithRepository.SAFE_BREAK_DURATION_SECONDS` (600) — L44 | `breakMinutes.coerceIn(0, 60) * 60` — L51–55 | Body only |
| Bounds constants | In prose only (§3.5: peek 0–30, break 0–60) | `companion object` `PEEK_MINUTES_MIN/MAX`, `BREAK_MINUTES_MIN/MAX` — L57–66 | Additive |
| Mid-flight law (§3.4) | Provider read at grant / creation only; no enforcer or derivation reads it | Identical law, restated L29–33 | No |

### 6.2 Consumer inventory (the "unchanged" claim, made checkable)

| Consumer | Pill tree | After swap |
|---|---|---|
| Provider instantiation | `MonolithRepository.kt:95` — `private val editableTimes = EditableTimesProvider(dao)` | Byte-identical line (Frist's tree has the same at its L96) |
| Peek grant | `startPeekLease` L1180–1192: `durationMillis = editableTimes.peekDurationMillis()`; `if (durationMillis <= 0L) return` — the 0 = peeks-off semantics (§3.5) **already honoured by Pastille's consumer** | Unchanged; a 0 setting now actually returns 0 instead of never occurring |
| Break at session creation | **No consumer exists in the pill tree** — see §6.3 | ← the split |
| Everything else (`PillDerivation`, shield, enforcers) | Never reads the provider (structural §3.4) | Unchanged — a live lease/break already holds an absolute end/stamp, so an edit prices the future only |

**Default-equivalence proof (one line, printed in the plan):** at Frist's defaults (2 / 10) the provider returns `2 × 60_000 = 120_000 ms` and `10 × 60 = 600 s` — bit-identical to the constants it replaces (§3.6), and an absent settings row reads as the entity defaults, so a never-seeded install behaves exactly as the pill lane does today. Category D's 120 s assertions therefore remain valid after the swap at defaults — D does not need re-authoring, only re-running.

### 6.3 The split the reviewer must see: B-peek is a swap, B-break is a swap plus a missing wire

This is the single most important presentation decision in Flag B. Signing "the provider swap" as one item would let a reviewer believe both times become editable. In the pill tree they do not:

- **B-peek — true drop-in.** Provider body changes; the one consumer (`startPeekLease`) already handles the full range including 0. Sign and done.
- **B-break — provider swaps, pill-side effect does not exist yet.** Pill's break length is still written from the raw constant in the progress machine: `MonolithRepository.kt:1897` sets `safeBreakSecondsRemaining = SAFE_BREAK_DURATION_SECONDS` when a block finishes, `breakDurationSeconds()` has **zero callers** in the pill tree, and pill's `FocusSessionEntity` has **no `breakSeconds` column** at all (grep: no hits in pill `MonolithModels.kt`). Frist's tree wires it differently: session row carries `breakSeconds: Int = 600` (`MonolithModels.kt:372`) stamped at creation from `editableTimes.breakDurationSeconds()` (`MonolithRepository.kt:1377`), and the span maths reads the stamp.
  → The plan therefore offers three distinct boxes: ☐ SIGN B-break **as provider-only** (break editability explicitly deferred to the session-stamp wiring), ☐ SIGN B-break **only together with** Frist's `breakSeconds` stamp + `startSession` wiring, ☐ DEFER B-break entirely. Default recommendation printed beside the boxes: the second — otherwise the swapped `breakDurationSeconds()` is dead code wearing a proved costume.

### 6.4 Dependencies the swap silently requires (listed on the flag, not discovered at compile time)

1. **GuardSettings columns:** `peekMinutes: Int = 2`, `breakMinutes: Int = 10` on `GuardSettingsEntity` — Frist's `MonolithModels.kt:819–833`. **Not present in the batch-9 tree**, whose GuardSettings ends at `shortsReelsSniperEnabled` (batch-9 `MonolithModels.kt:772–779`). Without these columns Frist's provider does not compile — the dependency is a *prerequisite merge item*, listed before the swap in merge order (§7).
2. **Migration 11→12** for those columns (Frist's tree) landing on a tree **already at v12** (batch-9, for other Truth Car 3 adds). The composed-migration column order is already ruled in Frist's own comment (`MonolithModels.kt` truth-3-times, GuardSettings doc): *peek/break append first among the car's guard adds; `shortsReelsSniperEnabled` follows.* The plan quotes that line — schema identity at v12 depends on it, and it is the easiest thing in the whole merge to get silently wrong.
3. **Setters' write clamp** (repository setters coerce to the same §3.5 bounds; provider's read clamp is the second wall) — Frist's tree carries them; the plan names them as part of the B package so the provider is never merged without its writers.

**Sign:** ☐ SIGN B-peek ☐ SIGN B-break (mode per §6.3) ☐ REFUSE — reason: ______

---

## 7. Merge manifest (plan §8 — the exact shape of the merge)

**New files into batch-9 tree** (none exist there today): `service/PillFace.kt`, `service/PillController.kt`, `service/PillTheme.kt`, `data/EditableTimes.kt` (Pastille's version first; Frist's body replaces it under Flag B), `test/PillDerivationTest.kt`, `test/PillLeaseRepositoryTest.kt`.

**Modified files:** `data/MonolithRepository.kt` (provider field L95, `startPeekLease`/`recordPeekEnded`, `queryForegroundPackage`), `data/MonolithDao.kt` (L32, L41), `service/MonolithShieldService.kt` (pill ownership, Route B, `ACTION_END_PEEK`), `ui/MonolithViewModel.kt` (`start2MinutePeek` — **Flag A composition applied here, neither tree's version copied verbatim**).

**Merge order printed in the plan:** (1) Frist GuardSettings columns + 11→12 migration reconciliation → (2) pill files + modifications → (3) Flag A composition in the ViewModel → (4) Flag B provider body swap → (5) verification run (§3 categories) on the *merged* tree. Steps 1 and 4 are one flag (B) split around the pill landing — the plan draws it that way so nobody swaps the provider into a tree that lacks its columns.

**Untouched, stated by name:** DB version stays 12 (no pill migration); `PillTheme.palette` ships as `PillPalettes.Default` — look lanes redress it later by writing the palette, per `PillTheme.kt`'s own contract, and no look work is smuggled into #16.

---

## 8. Open items & residual risks (plan §9 — each with what closes it)

- **A-3 — Legacy pill state vs. editable peek length.** Both trees set `_floatingPeekPill.secondsRemaining = MonolithRepository.PEEK_DURATION_SECONDS` (pill L2166; batch-9 same) — a constant 120. `PillFace.kt`'s header already calls `FloatingPeekPillState` the dead contract's state (a 1 Hz counter in the wrong process). Once Flag B lands, a non-default `peekMinutes` makes that legacy number actively wrong wherever it still renders. Options for the reviewer: (i) accept + document that `PillDerivation` is the only truth and the legacy state is display-dead, (ii) wire the legacy state's seconds from the provider at grant. **Designer recommendation: (i) for #16, with a named follow-up** — but it must be a *decision on the sheet*, not a discovery later.
- **R-1 — Route B drift risk.** Route B mirrors `PillDerivation` inline in the shield loop (`MonolithShieldService.kt:162–191`) instead of calling it, including its own copy of the ceiling formula. Any future derivation change must be made twice or the two mouths diverge — the exact failure `PillFace.kt`'s header exists to prevent. Not a merge blocker for #16 (they agree today, checklist-verified in §4); the plan logs it as a refactor follow-up: Route B should consume `PillDerivation.derive`'s result directly.
- **R-2 — Controller is inspection-only.** The overlay window (drag, snap, grant-loss, death-with-service) has no automated verdict in this lane and cannot get one from these 18. Closed only by the on-device check listed in §4 after the merged build exists. The sheet's block [4] keeps this visible until then.
- **R-3 — Category D's sandbox hygiene on re-run.** D's tests share one sandbox DB by design (unique packages per test, class doc L27–29). On a *re*-run in the same sandbox, leftover rows from a previous partial run are a plausible false-failure source; the plan's run section must state the sandbox is fresh or cleaned before D is interpreted.

---

## 9. Designer's closing note to Pastille

The presentation bet in one line: **separate the three things reviewers habitually fuse — "tests passed", "flags compose", "everything is proved" — into three separately signed blocks on one page, with `NO_VERDICT` and `UNPROVED` printed as loudly as `PASS` ever will be.** The pill's code is unusually honest (every header states its own law); the verification plan should match that register. If only one part of this design survives editing, keep §6.3: B-peek and B-break are different-sized promises, and the sheet must not let one signature sell both.

— Designer, Pastille's cell, issue #16
