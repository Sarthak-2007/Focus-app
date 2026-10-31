# Issue #16 — Pill Lane: Test Inventory (18 tests, gallery standard)

**Seat:** Finisher-3 (issue-16 cell), Pastille's cell · **Repo:** `Sarthak-2007/Focus-app`
**Status:** DECLARED inventory only — nothing compiled, nothing run, per the build
authority. Every line below is a read of the source (`INSPECTION`), not a verdict.
**Purpose:** catalogue the 18 pill tests so completely that the eventual run's
verdict is a formality — each test's name, location, claim, and merge relevance
is fixed here *before* any run, and so are its weaknesses.

**Sources (counts verified by grep: 15 + 3 = 18 `@Test`):**

- `builds/pill/Focus-app/app/src/test/java/com/example/PillDerivationTest.kt` — 15 tests, plain JUnit, no runner, no Android.
- `builds/pill/Focus-app/app/src/test/java/com/example/PillLeaseRepositoryTest.kt` — 3 tests, Robolectric + Room.

Line numbers are the `fun` line (the `@Test` is the line above it in every case).
Guard labels use the merge vocabulary fixed for this cell: **pill derivation** /
**lease+cooldown** / **EditableTimes seam** / **ST-08 launch** / **provider duration**.

---

## Group 1 — Derivation (15, `PillDerivationTest.kt`)

Code under test: `PillDerivation.derive` (`service/PillFace.kt:82–165`).
All 15 are deterministic: the clock is an injected parameter (`now`, default
`t0 = 1_000_000L`), no `SystemClock`, no Android runtime, no randomness.
None of the 15 is environment-sensitive.

### 1A — Absence gates (5)

| # | Test | File:line | What it proves (one sentence) | Guards |
|---|---|---|---|---|
| 1 | `noSessionIsAbsent` | `PillDerivationTest.kt:71` | With no session row at all, the pill face is `Absent` — nothing is drawn. | pill derivation |
| 2 | `inactiveSessionIsAbsent` | `PillDerivationTest.kt:76` | A session row that exists but is not active still yields `Absent`. | pill derivation |
| 3 | `ownPackageIsAbsent` | `PillDerivationTest.kt:81` | When the foreground app is Monolith itself, the face is `Absent` (§2.4 Q1 — inside Monolith, Monolith speaks). | pill derivation |
| 4 | `telecomForegroundIsAbsent` | `PillDerivationTest.kt:87` | When an in-call/dialer app is foreground, the face is `Absent` (§2.4 Q4 — a human voice outranks a fuse). **Partial: 2 of 5 packages — see Flag F4.** | pill derivation |
| 5 | `keyguardIsAbsent` | `PillDerivationTest.kt:94` | When the lock screen (keyguard) is up, the face is `Absent` (§2.4 Q2). | pill derivation |

### 1B — Time maths from stored ends (6)

| # | Test | File:line | What it proves (one sentence) | Guards |
|---|---|---|---|---|
| 6 | `breakOutranksPeek` | `PillDerivationTest.kt:100` | During a safe break the face is `Break(570)` (600 s stored − 30 s elapsed) even when a live peek lease exists for the foreground app — break outranks peek. | pill derivation (break face over lease) |
| 7 | `breakRemainingEqualsTheHouseDerivationAtEverySample` | `PillDerivationTest.kt:114` | At 7 sampled offsets (0 ms … 900 000 ms, incl. past the end) the break face equals `breakRemainingAfter(600, t0, t0+offset)`. **Circular oracle — see Flag F1.** | pill derivation (wiring only, not arithmetic) |
| 8 | `stopwatchShowsElapsedFromTheStoredPair` | `PillDerivationTest.kt:128` | Stopwatch elapsed is the stored elapsed (100 s) plus whole seconds since the stored tick — 105 s at +5 s and at +5.999 s, 106 s at +6 s — so sub-second deltas credit nothing. | pill derivation |
| 9 | `timedShowsNowFrameRemainingAndBlock` | `PillDerivationTest.kt:145` | The timed face shows remaining = target − elapsed-at-instant (1500 − 310 = 1190 s) and carries the block position (2 of 4) through unchanged. | pill derivation |
| 10 | `unstampedTickCreditsNothingBeyondTheStoredFact` | `PillDerivationTest.kt:160` | With no tick ever stamped (`lastTick = 0`), remaining is exactly target − stored elapsed (1200 s) — an unstamped row credits no phantom time. | pill derivation |
| 11 | `remainingIncludesThePenalty` | `PillDerivationTest.kt:167` | Remaining is computed against the effective target, target + penalty (1500 + 900 − 300 = 2100 s) — the Now Frame's penalty law. | pill derivation |

### 1C — Peek face (4)

| # | Test | File:line | What it proves (one sentence) | Guards |
|---|---|---|---|---|
| 12 | `peekFaceNamesTheRowAndDiesExactlyAtItsEnd` | `PillDerivationTest.kt:179` | The peek face names the leased row (`Peek("com.peeked", "Peeked", 120)`), still reads 1 in its last partial second (ceiling), and at the exact end instant reverts to a `Session` face — never a zero fuse. **Last assertion is type-only — see Flag F5.** | pill derivation · lease+cooldown (read side of the lease end) |
| 13 | `peekFaceFollowsHimWhereverHeWanders` | `PillDerivationTest.kt:198` | The peek face appears even when the foreground is a different app, and still names the peeked app, not the foreground one (§2.4 Q5). | pill derivation · lease+cooldown (read side) |
| 14 | `twoLiveLeasesResolveToTheLaterEnd` | `PillDerivationTest.kt:207` | If storage ever held two live leases, the face resolves to the later-ending one (`Peek("com.b", "B", 90)`). **Unreachable state by design — see Flag F3.** | pill derivation · lease+cooldown (read-side arbitration only) |
| 15 | `faceEqualsStoredEndsAtEverySampledInstant` | `PillDerivationTest.kt:219` | One fixed stored lease end sampled at 6 instants yields remaining = end − instant every time, with no state carried between samples — the face is recomputed, never counted. **Formula-mirroring oracle — see Flag F2.** | pill derivation · lease+cooldown (read side) |

---

## Group 2 — Repository / lease writes (3, `PillLeaseRepositoryTest.kt`)

Code under test: `MonolithRepository.startPeekLease` (pill `MonolithRepository.kt:1180`),
`recordPeekEnded` (pill `:1197`), via the real Room DAO.
Runner: `@RunWith(RobolectricTestRunner::class)`, `@Config(sdk = [36])` (class, `:34–35`).
All 3 are environment-sensitive — see Flag F6, which applies to the whole group.

| # | Test | File:line | What it proves (one sentence) | Guards |
|---|---|---|---|---|
| 16 | `grantStampsEndAndPreArmsCooldown` | `PillLeaseRepositoryTest.kt:55` | A grant stamps the lease end ≈120 s out (asserted window 100–120 s) and pre-arms that row's cooldown anchor to exactly the lease's own end. **Duration window is loose — see Flag F6b.** | lease+cooldown · provider duration (at today's default only) · EditableTimes seam (indirect — the grant reads `editableTimes.peekDurationMillis()`, pill repo `:1181`) |
| 17 | `grantZeroesEveryOtherRowsLease` | `PillLeaseRepositoryTest.kt:77` | A second grant zeroes the first row's lease while leaving its earned cooldown anchor standing, leaves a bystander row at 0, and leaves exactly 1 live lease in storage. | lease+cooldown (one-live-lease invariant, sequential grants only) |
| 18 | `handBackZeroesTheLeaseAndAnchorsCooldownToNow` | `PillLeaseRepositoryTest.kt:120` | Hand-back (`recordPeekEnded` — the pill ✕'s verb and Route B `END PEEK`'s verb) zeroes the lease and re-anchors the cooldown to the hand-back instant, not the granted end. **Shadow-clock bracket — see Flag F6c.** | lease+cooldown |

---

## Flags — vacuous, weak-oracle, and environment-sensitive tests

**Headline: no test is fully vacuous.** All 18 contain at least one exact
equality assertion on a face or a stored row. Six flags follow, in severity
order; F1, F2 and F6 are the ones a verdict-reader must hold in mind, because
a PASS on those tests proves less than their names claim. (Charges cross-ref:
`critic-charges.md` in this cell.)

### F1 — CIRCULAR ORACLE — #7 `breakRemainingEqualsTheHouseDerivationAtEverySample` (C24, medium)

- The expected value is computed by calling `breakRemainingAfter(600, t0, …)`
  in the test (`PillDerivationTest.kt:122–123`) — the identical function
  `PillDerivation.derive` delegates to for the Break face
  (`service/PillFace.kt:100–108`; function at `data/WallAccrual.kt:40–51`).
- Consequence: any bug inside `breakRemainingAfter` (clamp, divisor, sign)
  passes on both sides at once. The test proves *wiring* — that `derive`
  passes its stored pair through and wraps the result as `Break` — not the
  arithmetic.
- Settling fix (not done here): literal expected values for the sampled
  offsets — 600, 599, 541, 540, 1, 0, 0 — computed by hand from the spec.
- Not affected: #6 `breakOutranksPeek` asserts the literal `Break(570)`,
  so the break arithmetic is not wholly unguarded — it is guarded at
  exactly one point.

### F2 — FORMULA-MIRRORING ORACLE — #15 `faceEqualsStoredEndsAtEverySampledInstant` (C25, medium)

- Expected remaining is computed in the test with the same ceiling
  expression the implementation uses: `((expectedMs + 999L) / 1000L)`
  (`PillDerivationTest.kt:231–236`) vs `((remainingMs + 999L) / 1000L)`
  (`service/PillFace.kt:120–125`).
- Consequence: a shared ceiling-vs-floor misunderstanding of the spec
  passes both sides. What the test *does* prove independently is the
  statelessness claim — repeated sampling of one fixed end, no drift —
  which is its load-bearing half.
- Partial mitigation already in the suite: #12 asserts the ceiling
  behaviour with literals (120 at start, 1 at +119 999 ms), so the
  ceiling choice itself is pinned once, independently of F2.
- Settling fix: literal expected seconds per sample in #15.

### F3 — UNREACHABLE STATE — #14 `twoLiveLeasesResolveToTheLaterEnd` (C25, medium)

- The test's own comment concedes the premise: "Storage can never hold
  two after a grant (the grant zeroes the rest) — the max stays as the
  belt" (`PillDerivationTest.kt:208–210`). It tests the derivation's
  defensive `maxByOrNull` (`PillFace.kt:113–115`), not a state the
  system is supposed to reach.
- It is not vacuous — the arbitration rule is real code and a wrong
  pick would ship — but its merge value is the lowest of the 15, and it
  must not be read as proving the one-live-lease invariant. That
  invariant is proved (sequentially) by #17, and is *not* proved under
  concurrency by anything in the 18: the grant is two non-atomic DAO
  calls with no `@Transaction`/mutex (C26 — outside this inventory's
  test-quality scope, recorded here so #14+#17 are never cited as
  closing it).

### F4 — PARTIAL COVERAGE — #4 `telecomForegroundIsAbsent` (C28, low)

- `TELECOM_FOREGROUND_PACKAGES` holds 5 packages (`PillFace.kt:66–72`);
  the test exercises 2 (`com.android.incallui`,
  `com.google.android.dialer`, `PillDerivationTest.kt:89–90`).
- Untested: `com.samsung.android.incallui`, `com.samsung.android.dialer`,
  `com.android.dialer` — a typo in any of the three ships undetected.
- Settling fix: iterate the set itself in the test so test and list
  cannot drift apart.

### F5 — TYPE-ONLY FINAL ASSERTION — #12 `peekFaceNamesTheRowAndDiesExactlyAtItsEnd` (minor, found in this inventory)

- The end-instant assertion is `assertTrue(atEnd is PillFace.Session)`
  (`PillDerivationTest.kt:194`) — it proves the face is no longer a
  `Peek`, but pins none of the reverted Session's values (remaining,
  elapsed, block). A revert to a Session with wrong numbers passes.
- The first two assertions in the same test are exact equalities, so
  the test as a whole is strong; only its last third is weak.

### F6 — ENVIRONMENT-SENSITIVE GROUP — all of Group 2 (#16–#18) (C27, medium)

The derivation group has no equivalent exposure (injected clock, pure
JUnit). The repository group has three distinct exposures:

- **F6a — Robolectric + shared singleton DB (all 3).** Tests run under
  `RobolectricTestRunner` at SDK 36 against the production singleton
  `MonolithDatabase.getInstance(context)` (`PillLeaseRepositoryTest.kt:42`),
  not an isolated/in-memory database. The class doc admits "the sandbox
  database is shared by the whole class" (`:27–29`); the only isolation
  is unique package prefixes per test (`com.pillg.*`, `com.pillz.*`,
  `com.pillh.*`). A verdict from a dirty sandbox is not interpretable —
  the run protocol must state the sandbox was fresh/cleaned.
- **F6b — Loose duration window (#16).** The grant asserts
  `remainingMs in 100_000L..120_000L` (`PillLeaseRepositoryTest.kt:65–68`).
  A provider returning a flat 100 s passes. So #16 guards provider
  duration only coarsely at the default: it catches a grossly wrong
  length (0, minutes-vs-seconds errors) but not a 100 s-vs-120 s error.
  Its second assertion — cooldown anchor *exactly equals* the lease
  end (`:69–73`) — is exact and unaffected.
- **F6c — Shadow `SystemClock` bracket (#18, also touches #16).** Both
  tests read `SystemClock.elapsedRealtime()` for "now". Under
  Robolectric that clock is scheduler-controlled and does not advance
  during a `runBlocking` DAO call, so #18's `beforeEnd..afterEnd`
  bracket (`PillLeaseRepositoryTest.kt:133–141`) can collapse to one
  frozen value: the test then proves the stamp equals whatever the
  frozen clock said — weaker than "anchored to the hand-back instant",
  and unable to catch an implementation stamping any other
  same-clock-derived value. Settling fix: drive the shadow clock
  explicitly (`ShadowSystemClock`) to a chosen, distinct "now"
  between grant and hand-back.

---

## Coverage honesty — what none of the 18 guards

Fixed here so an "18/18" headline can never be misread at merge:

| Merge-relevant behaviour | Guarded by the 18? |
|---|---|
| Pill derivation (absence, break, stopwatch, timed, peek faces) | **Yes** — #1–#15, with F1/F2/F4/F5 caveats above |
| Lease + cooldown (grant, exclusivity, hand-back) | **Yes, sequentially, at default duration** — #16–#18, with F6 caveats; concurrency (C26) unguarded |
| Provider duration | **Only at the default, only coarsely** — #16's 100–120 s window over the 120 s constant; no test exercises any non-default duration |
| EditableTimes seam | **Indirect only** — #16's grant flows through `EditableTimesProvider.peekDurationMillis()` returning today's constant (`data/EditableTimes.kt:38`); no pill test constructs the provider, substitutes a value, or touches `breakDurationSeconds()` (which has zero consumers in the pill tree). Non-default seam behaviour is Frist-lane `EditableTimesTruthTest` territory, not this suite's |
| ST-08 launch (post-grant launch to the peeked app, `MonolithViewModel.kt` pill lane) | **No — guarded by zero of the 18.** No test calls `start2MinutePeek`, constructs a `PackageManager` intent, or asserts a launch. ST-08 stands on inspection + Flag A composition review only |

---

## Verdict table template — 18 rows (fill at the run; verdict from harvested JUnit XML only)

| Test | Guards | Verdict | Evidence line |
|---|---|---|---|
| `noSessionIsAbsent` | pill derivation |  | `PillDerivationTest.kt:71` — `assertEquals(PillFace.Absent, derive(null))` at `:72` |
| `inactiveSessionIsAbsent` | pill derivation |  | `PillDerivationTest.kt:76` — `assertEquals(PillFace.Absent, …)` at `:77` |
| `ownPackageIsAbsent` | pill derivation |  | `PillDerivationTest.kt:81` — `assertEquals(PillFace.Absent, …)` at `:83` |
| `telecomForegroundIsAbsent` | pill derivation |  | `PillDerivationTest.kt:87` — `assertEquals(PillFace.Absent, …)` at `:89–90` (2 of 5 packages, F4) |
| `keyguardIsAbsent` | pill derivation |  | `PillDerivationTest.kt:94` — `assertEquals(PillFace.Absent, …)` at `:96` |
| `breakOutranksPeek` | pill derivation |  | `PillDerivationTest.kt:100` — `assertEquals(PillFace.Break(570), face)` at `:110` |
| `breakRemainingEqualsTheHouseDerivationAtEverySample` | pill derivation (wiring) |  | `PillDerivationTest.kt:114` — `assertEquals(…, PillFace.Break(expected), face)` at `:123` (circular oracle, F1) |
| `stopwatchShowsElapsedFromTheStoredPair` | pill derivation |  | `PillDerivationTest.kt:128` — elapsed 105 / 105 / 106 at `:136 / :139 / :141` |
| `timedShowsNowFrameRemainingAndBlock` | pill derivation |  | `PillDerivationTest.kt:145` — remaining 1190, block 2/4 at `:154–156` |
| `unstampedTickCreditsNothingBeyondTheStoredFact` | pill derivation |  | `PillDerivationTest.kt:160` — `assertEquals(1200, face.remainingSeconds)` at `:163` |
| `remainingIncludesThePenalty` | pill derivation |  | `PillDerivationTest.kt:167` — `assertEquals(2100, face.remainingSeconds)` at `:175` |
| `peekFaceNamesTheRowAndDiesExactlyAtItsEnd` | pill derivation · lease+cooldown |  | `PillDerivationTest.kt:179` — exact `Peek(…, 120)` / `Peek(…, 1)` at `:182 / :187`; type-only revert at `:194` (F5) |
| `peekFaceFollowsHimWhereverHeWanders` | pill derivation · lease+cooldown |  | `PillDerivationTest.kt:198` — `assertEquals(PillFace.Peek("com.peeked", "Peeked", 120), face)` at `:203` |
| `twoLiveLeasesResolveToTheLaterEnd` | pill derivation · lease+cooldown |  | `PillDerivationTest.kt:207` — `assertEquals(PillFace.Peek("com.b", "B", 90), face)` at `:215` (unreachable state, F3) |
| `faceEqualsStoredEndsAtEverySampledInstant` | pill derivation · lease+cooldown |  | `PillDerivationTest.kt:219` — sampled `assertEquals` at `:231` (formula-mirroring oracle, F2) |
| `grantStampsEndAndPreArmsCooldown` | lease+cooldown · provider duration · EditableTimes seam (indirect) |  | `PillLeaseRepositoryTest.kt:55` — window `100_000..120_000` at `:65`; anchor equality at `:69` (F6a/F6b) |
| `grantZeroesEveryOtherRowsLease` | lease+cooldown |  | `PillLeaseRepositoryTest.kt:77` — zeroed lease `:98`, anchor stands `:103`, exactly 1 live at `:116` (F6a) |
| `handBackZeroesTheLeaseAndAnchorsCooldownToNow` | lease+cooldown |  | `PillLeaseRepositoryTest.kt:120` — lease zeroed `:136`, anchor in bracket `:137` (F6a/F6c) |

Verdict values, when filled: `PASS` (testcase, no failure/error) ·
`FAIL` (`<failure>`) · `ERROR` (`<error>`) · `NO_VERDICT` (no XML entry —
incl. any shard killed by infrastructure; name the shard, never blame the test).

— Finisher-3 (issue-16 cell)
