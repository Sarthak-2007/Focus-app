# Issue #16 — Comment pack (ready to post)

*Finisher-5 (issue-16 cell), Oct 30 2026. Built from every draft in
the cell dir as it stood at writing: Pastille's `verification-plan.md`
+ `merge-flags.md` (repo, `studio/issue-16/`), Designer, Innovator,
Critic (C1–C28), Builder, Finisher-1 (verification plan, final),
Finisher-2 (`test-v2.sh`), Finisher-3 (test inventory),
Finisher-4 (merge-flags package, final), Feel-1–Feel-5.
Comments 1–3 are post-ready. §4 is a separate comment for #16 only —
post it, or resolve it, before anything in Comments 1–2 is treated
as settled where §4 says the drafts disagree. Nothing in this pack
was compiled or run; nobody in this cell compiles.*

---

## Comment 1 — for issue #16: roster + prep status

**Cell roster — issue #16 (pill lane), Pastille's cell**

- **Lead:** Pastille — verification plan + merge flags
  (`repo/studio/issue-16/`), Flag A composition pre-applied on
  branch `agent/issue-16`.
- **Designer** — verification + merge presentation design.
- **Innovator** — clean-shot run design (shard the 18, harvest XML).
- **Critic** — charges C1–C28, paper audit, evidence on every charge.
- **Builder** — merge-flag verification in code; grant gate (§A4 of
  his report) applied on the clone.
- **Finisher-1** — verification plan, final (one-read sign-off).
- **Finisher-2** — `test-v2.sh`, the corrected re-run script
  (written, syntax-checked only, never run).
- **Finisher-3** — test inventory: all 18 catalogued, flags F1–F6.
- **Finisher-4** — merge-flags package, final (batch-10 execution
  copy; supersedes the lead's `merge-flags.md` in polish).
- **Finisher-5** — this comment pack.
- **Feel-1 · Weight** (drag / snap / settle) · **Feel-2 · Light**
  (colour / contrast / state light) · **Feel-3 · Timing** (countdown
  cadence / expiry) · **Feel-4 · Touch & Sound** (targets / verbs /
  voices) · **Feel-5 · Instant Read** (0.5 s glance test; current
  pill grades FAIL, 12-criteria checklist + fix list).

**Prep status — stated exactly**

- **Verified (inspection, file:line):** the 18 tests exist and are
  counted — 15 in `PillDerivationTest` + 3 in
  `PillLeaseRepositoryTest`, each catalogued by Finisher-3. Flag A's
  two halves and Flag B's seam were traced member by member in all
  three trees (pill / batch-9 / truth-3-times) by Builder and
  re-verified by Finisher-4; the composed `start2MinutePeek` is
  quoted from the clone, not reconstructed. Critic's 28 charges
  stand filed with evidence.
- **Historical, scoped:** the pill lane's `assembleDebug` went
  GREEN (17m 43s, `builds/pill/assemble.log`) and its test sources
  compiled inside the last suite attempt before the Gradle daemon
  disappeared (daemon pid 53253, `builds/pill/test.log`). That is
  the pill lane's past, **not** a compile of this branch.
- **Written but uncompiled (DECLARED):** branch `agent/issue-16`
  at `352e5db` carries Pastille's Flag A composition (Suki guard +
  ST-08 launch + `Intent` import) — committed, never compiled.
  Builder's grant gate sits **on top of it, uncommitted**: 2 files
  modified in the clone working tree (`MonolithViewModel.kt`,
  `MonolithRepository.kt`, +28/−2). `test-v2.sh` is written and
  syntax-checked only.
- **No verdict exists:** the suite has died 3× to the environment,
  never to a test failure. Today the honest line is: *code
  compiles (lane, historically). 18 tests written. 0 tests have
  ever reported. 0 tests have ever failed.* Status:
  **NO_VERDICT 18/18** — A Absence ?/5 · B Time maths ?/6 ·
  C Peek face ?/4 · D Lease writes ?/3. Merge-ready: **NO**.
- **Still waits:** the suite re-run waits for Ren's healthy-window
  call — compile/test authority is Ren's alone, on Sarthak's word.
  This cell does not run it, and no comment from this cell will
  claim a verdict before harvested JUnit XML exists. Also open:
  the re-run route itself (§4, R1), the A-3 legacy-pill-state
  decision, the B-break mode decision, and the on-device checks
  (PillController, Route B) that only a merged build can close.

— Finisher-5 (issue-16 cell)

---

## Comment 2 — for issue #15 (batch-10 epic): Pastille mirrors this, verbatim-ready

**From issue #16 (pill lane) — merge flags carried into batch-10**

Both flags + one bonus delta, verified in code against the pill,
batch-9, and truth-3-times trees. Full execution copy:
Finisher-4's merge-flags package on #16 (§6 is the ordered
sequence). Everything below is DECLARED, uncompiled.

- **Flag A — `start2MinutePeek` COMPOSES.** Batch-9 function kept
  whole (Suki guard first, synchronous, refusal leaves the airlock
  standing, no blank-package default — the pill lane's
  `ifBlank { "com.whatsapp" }` must not survive); ST-08's
  `runCatching` launch inserted inside the coroutine **between**
  `startPeekLease` and `recordIntercept`; one import added
  (`android.content.Intent`). Ordering is load-bearing:
  guard → grant written → launch → record. Already composed on
  branch `agent/issue-16`, **plus** the builder's grant gate:
  `startPeekLease` returns `Boolean` and launch + record fire only
  on a proven grant — without it, Flag B's `peekMinutes = 0` would
  launch an app no lease protects and log a peek that never
  happened. (Grant gate is currently uncommitted on the branch —
  2 files; it must land with the composition.)
- **Flag B — Frist's `EditableTimesProvider` COMPOSES, ordered.**
  Same class, constructor `(MonolithDao)`, same two suspend
  signatures; at defaults (2 / 10) it returns bit-identical values
  (120 000 ms / 600 s). Dependency order is the flag's sharp edge:
  **(1)** new `MIGRATION_12_13` + entity fields
  `peekMinutes: Int = 2`, `breakMinutes: Int = 10` appended to
  `GuardSettingsEntity` **after** `shortsReelsSniperEnabled`,
  DB version 12 → 13 — Frist's own `MIGRATION_11_12` cannot be
  reused, the batch-9 lineage already spent that name/pair on
  different SQL; **(2)** provider file lands whole. In the pill
  lineage this is a swap; in the batch-9 lineage it is an **add** —
  no `EditableTimes.kt` and no repository instance exist there
  yet; the instance line arrives with the bonus delta.
  "Frist's five columns" = the 2 guard columns above (this flag)
  + 3 `focus_sessions` columns (his wider slice).
- **Bonus delta — pill `startPeekLease`, TAKE WHOLESALE, with two
  companions.** Brings provider-read duration, refusal at 0
  (no row touched), and one-live-lease enforcement via
  `dao.clearOtherPeekLeases`. Companions, both mandatory:
  **(i)** add `clearOtherPeekLeases` to the DAO — it exists only
  in the pill DAO today; **(ii)** the `Boolean` signature patch
  (return `false` on the ≤ 0 refusal, `true` after the write)
  that Flag A's gate gates on.
- **Scope warnings, so nobody over-claims at the merge:**
  editable *breaks* are **not** wired by Flag B alone —
  `breakDurationSeconds()` has no caller in the pill/batch-9
  lineages; the creation-time `breakSeconds` stamp is Frist-slice
  work, a separate landing. The VM pill countdown seed
  (`PEEK_DURATION_SECONDS`, constant 120) likewise stays constant
  under both flags; moving it to the provider's value is a named
  follow-up, not part of either flag.
- **Test verdict from #16:** none yet — NO_VERDICT 18/18, re-run
  waiting on Ren's healthy-window call. A green suite is not
  required to sign these flags, and signing them does not claim
  one: none of the 18 tests calls `start2MinutePeek` or constructs
  the Frist provider.

— Finisher-5 (issue-16 cell), for Pastille to mirror

---

## Comment 3 — for issue #16: acceptance checklist (closing)

**Issue #16 acceptance — honest state at cell close**

- [ ] **Test verdict posted on #16** — NOT DONE. NO_VERDICT 18/18;
  the re-run waits for Ren's healthy-window call. (Pastille's plan
  carries this same box unticked: "waiting on the window".)
- [x] **Merge flags verified in code** — Flag A, Flag B, and the
  bonus delta, traced in all three trees (Builder; re-verified
  line by line by Finisher-4, execution copy final).
- [ ] **Merge flags carried onto the batch-10 epic (#15)** —
  prepared as Comment 2 of the cell's comment pack; the box ticks
  when Pastille mirrors it onto #15. (Pastille's plan ticks the
  combined "verified and carried" box; this pack splits it so the
  thread shows which half is done.)
- [x] Verification plan finished for one-read sign-off (Finisher-1).
- [x] Test inventory complete — 18/18 catalogued with weaknesses
  flagged F1–F6 (Finisher-3).
- [x] Critic charges filed — C1–C28, each with evidence and what
  settles it.
- [x] Feel specs delivered — Weight, Light, Timing, Touch & Sound,
  Instant Read (all paper-only; proposals, none ruled yet).
- [ ] **Runnable re-run script agreed** — `test-v2.sh` exists
  (Finisher-2); the shard design (Innovator / Finisher-1) is a
  different, unbuilt script. Route unresolved — see §4, R1.
- [ ] Branch composition compiled — never attempted; Ren's call only.
- [ ] Builder's grant gate committed — written, uncommitted (2 files).
- [ ] A-3 decision (legacy `FloatingPeekPillState` second clock) —
  open; gating positions differ — see §4, X5.
- [ ] B-break mode decision (provider-only / with Frist's stamp
  wiring / defer) — open.
- [ ] Feel conflicts ruled (slop, urgency thresholds, sound,
  time format, ✕ face) — open — see §4, F1–F8.

— Finisher-5 (issue-16 cell)

---

## §4 — Contradictions between drafts — SEPARATE SECTION (comment for #16; do not mirror to #15)

*The thread must not contain two truths. Each item names the two
claims and where they live. Where a later draft re-verified the
fact, that is stated — the older text still needs correcting or
striking wherever it is posted.*

**Merge-critical**

- **X1 — Migration vehicle, DB version, field placement.**
  Pastille / Builder / Finisher-4: **new `MIGRATION_12_13`, DB
  12 → 13**, `peekMinutes`/`breakMinutes` appended **after**
  `shortsReelsSniperEnabled`. Designer §6.4/§7, Finisher-1
  §7.4/§8, Critic C11: **`MIGRATION_11_12` reconciliation, DB
  stays 12**, fields append **before** `shortsReelsSniperEnabled`.
  Both cannot be true. Finisher-4 (§B2/§7, re-read from source
  Oct 30) supports 12_13 / v13 / after — the Designer and
  Finisher-1 manifests are wrong on this point and must not be
  posted uncorrected.
- **X2 — Flag B "swaps in unchanged, no consumer rewiring"**
  (Pastille headline) vs Builder §B2 + Finisher-4 §B3: true only
  for the pill lineage. Batch-9/clone have **no** `EditableTimes.kt`
  and **no** repository instance — there it is an add (file +
  instance line), and the file alone does not compile before X1's
  prerequisites land.
- **X3 — Flag A "composes" + "`peekMinutes = 0` composes with the
  guard (refusal at the source)"** (Pastille) vs Builder §A4,
  Critic C9, Finisher-4 §A5: the naive composition launches an
  unleased app and records a false peek. It composes only **with
  the grant gate**, which is uncommitted. Second layer: even with
  the gate, a provider refusal is **not** side-effect-free — in
  the clone the latch has already played and the airlock already
  dismissed, and the pill is seeded then cleared (VM :2399–2406,
  gate inside the coroutine). The Designer/Finisher-1 invariant
  "refusal = no sound, airlock standing, no state change" holds
  only for `peekOfferFor` refusals, not provider refusals.
- **X4 — Flag A's port surface.** Critic C4 + Finisher-1 §6.2
  price a 4-piece port (`InterceptPath` enum, state field,
  trigger parameter, intent plumbing) as merge work. Builder +
  Finisher-4 show the batch-9 base already contains all four —
  the port exists only if merging into the pill lineage.
  Finisher-1 carries the port list **and** a batch-9-base manifest
  (§8): the merger must not do this work twice.
- **X5 — What gates Flag B.** Designer: three B-break sign modes,
  recommends "only with Frist's wiring". Finisher-1 §6.4: an A-3
  decision is **mandatory before Flag B is signed**, and
  accept-and-document is explicitly **not** offered (Designer
  offered it, as his recommendation (i)). Finisher-4 §6: lands
  Flag B with **no** break wiring and treats the countdown-seed
  fix as a separate follow-up (warning only). Three different
  sign-off gates for the same flag — Pastille/Ren must pick one.
- **X6 — Bonus delta's DAO companion.** Designer/Finisher-1
  manifests list `MonolithDao.kt` as already carrying the pair
  (L32, L41). Finisher-4 §C2, verified: `clearOtherPeekLeases`
  exists **only** in the pill DAO; batch-9, clone, and
  truth-3-times DAOs lack it. Wholesale-taking `startPeekLease`
  without adding it does not compile.
- **X7 — Provider setters in scope?** Designer §6.4 + Finisher-1
  §7.4: Frist's setters are part of the Flag B package. Builder
  §B2 + Finisher-4 §B2: not needed by the provider, do not pull
  in for this flag (settings surface, separate landing).

**The re-run route**

- **R1 — There are two different "one commands".** (a) Pastille's
  plan + Finisher-2's artifact: `test-v2.sh` **exists** (cell dir),
  runs the **full unfiltered suite** in the cell clone, keeps
  `gradle --stop`, queue optional, evidence in the cell dir
  (`test-v2.log` / `test-v2.verdict` / `test-v2.completed.txt`).
  (b) Innovator + Finisher-1: `test2.sh` — different name, pill
  lane, `builds/pill/runs/` — **does not exist** (~150 lines still
  to write), runs **only the 18** in 4 shards + a compile shard,
  warm daemon reused, harvest + ledger per shard. Finisher-1's
  sheet says "runnable script: NO"; Pastille's plan says the
  corrected script is prepared. Both are in the thread; only one
  can be the route. Related misdescription: Pastille's plan says
  her script has the queue "removed", logs to the lane's
  `test.log`, and uses the lane's `guh` — the actual script keeps
  the queue as optional, logs to the cell's `test-v2.log`, and
  uses the cell's `guh` and clone.
- **R2 — Daemon law vs daemon practice.** Pastille's pre-flight
  cites the cure-hunters' law "never `--stop` a window-born
  daemon" while her own script discipline (kept verbatim in
  `test-v2.sh`) runs `gradle --stop; sleep 4`. Innovator +
  Finisher-1 pre-flight check 8 forbids the stop and reuses a
  healthy warm daemon. The plan contradicts itself and them.
- **R3 — Verdict shape.** Pastille: verdict = `TEST_EXIT` + suite
  totals (total / passed / failed / skipped). Innovator +
  Finisher-1 + Finisher-3: verdict = per-test PASS/FAIL/ERROR/
  NO_VERDICT for **exactly the 18**, reconciled against an
  expected-18 manifest (COMPLETE / PARTIAL k/18). A full-suite
  run cannot produce the 18-only verdict as defined, and the
  filtered run cannot produce suite totals.

**Feel specs — all unruled proposals; none may be posted as settled**

- **F1 — Drag slop.** Feel-1: Manhattan `|dx|+|dy| > dp(6)` is
  "fine, keep it — the one number that needs no change". Feel-4:
  replace with Euclidean `scaledTouchSlop` (~8 dp); a 6 dp
  Manhattan trip swallows tap verbs. (Feel-4 itself flags this
  divergence; Pastille to rule.)
- **F2 — Urgency thresholds, colours, type floors.** Feel-2:
  gold at ≤15 s, violet flood `#493EA4` at ≤5 s, ✕ accent
  `#F47442` → `#E47E35`, label 9→10 sp, figures stay 16 sp.
  Feel-3: figures to accent `#F47442` at T−10 only. Feel-4:
  warning = one raised detent at 10 s. Feel-5: accent at ≤30 s,
  urgent colour at ≤10 s, figures **24 sp**, label **11 sp**
  (floors: time ≥20 sp, label ≥11 sp). Thresholds (30/15/10/5),
  colours, and floors are mutually incompatible as end states.
- **F3 — Sound and haptics.** Feel-3: "**Sound: none, at any
  point in the 120 s**" (haptics at T−10/T−5, none at zero).
  Feel-4: pickup detent, snap-settle knock, hand-back latch,
  10 s warning detent, expiry knock, 15 s heartbeat detents.
  Feel-1: no haptics in v1 (deferred option only) — vs Feel-4's
  settle knock, which bundles a haptic by engine law.
- **F4 — Time format.** Feel-5 Fix 1: Peek prints total seconds
  (`"118s"`, no colon clock). Feel-3 §4 (must-not-change):
  `formatClock` MM:SS is the only numeric surface, no
  bare-seconds switch. Directly opposed.
- **F5 — The ✕ and the body tap.** Feel-4: 48×48 dp via
  `TouchDelegate`, glyph unchanged, the pill's face does not
  grow; plus a recommendation that a Peek body tap do **both**
  verbs (hand back + return). Feel-5: `"END ✕"` chip, pill
  widens ~30 dp on Peek; body tap = return only, taught by a
  return glyph. Current code agrees with Feel-5 on the verb and
  with neither on the target.
- **F6 — Analog channel + death.** Feel-3: fuse fraction =
  `remainingMs / 120 000`, animator re-anchored per tick — the
  hardcoded 120 s denominator contradicts Flag B's editable
  durations (up to 30 min). Feel-5: draining bar stepped per
  second from a lease total carried on the face (needs a new
  `totalSeconds` field). Death: Feel-1 = instant cut, no exit
  motion ("truth outranks feel"); Feel-3 = exit animation
  ≤140 ms, digits dying in frame 0, pill gone ≤150 ms after end.
- **F7 — Route B "same derivation" claim.** Feel-5 grades IR-11
  PASS partly because "Route B prints the same derivation so the
  shade can't contradict the glass". Critic C15 + Designer §4 +
  Finisher-1 §5: Route B **never calls** `PillDerivation.derive`
  — it re-implements the priority and ceiling inline and adds a
  wall-clock chronometer, so the mouths can diverge. Feel-5's
  rationale overclaims; the PASS rests on the one-cluster half.
- **F8 — Drag bounds.** Feel-1: legal field inset 6 dp (X) /
  24 dp (Y) with 0.35× rubber-band overdrag capped at 18 dp.
  Feel-4: hard clamp at 0 margins, whole pill (✕ included) never
  leaves the glass. Different fields, different walls.

**Minor / internal**

- **M1 — Feel-5's own tally.** §0 claims "2 of 12 criteria pass";
  its §2 table shows exactly one full PASS (IR-11), with IR-3 and
  IR-10 passing only in sub-parts (stare / face+contrast).
- **M2 — Feel-2's price.** "~30 lines" (§0 verdict) vs "~35
  lines" (§6 table) for the same fix set.
- **M3 — Break-hardcode count.** Critic C10 says the 600 s is
  baked at the block-finish write "and twice more" in the spans,
  then lists three span sites. Finisher-1 §7.3 counts it cleanly:
  four sites total (`MonolithRepository.kt:1897` +
  `MonolithModels.kt:391/395/398`).
- **M4 — Line-anchor drift.** Same code, different anchors across
  drafts: batch-9 `start2MinutePeek` ends :2413 (Designer /
  Finisher-1) vs :2416 (Critic / Finisher-4); render-key gate at
  `PillController` :144 (Feel-3) / :146 (Feel-5) / :147
  (Finisher-1); `recordPeekEnded` starts :1197 (Critic /
  Finisher-3) vs :1198 (Finisher-1); derivation range
  `PillFace.kt` 60–155 (Designer) / 60–165 (Finisher-1) / 82–165
  (Finisher-3). Comments 1–2 above avoid the drifted anchors;
  anyone quoting lines should use Finisher-4's / Finisher-3's
  Oct 30 re-reads.
- **M5 — "assemble: GREEN" scope trap.** Designer + Finisher-1
  sign-off sheets print it unscoped. The artefact is the pill
  lane's historical `assemble.log`; the `agent/issue-16`
  composition and the grant gate have never compiled. Scoped in
  Comment 1; the sheets should be read with that scope.
- **M6 — Finisher-4's "none contradicted".** Its §7 header says
  the refinements update facts with "none contradicted" — true
  against Pastille's briefed facts. Against Designer/Finisher-1
  (X1, X5 above) the package **does** contradict and supersede
  them; trust its §B2/§6 over the older manifest where they differ.

— Finisher-5 (issue-16 cell)
