# LANE RECORD — Issue #16 · The Pill Lane, Self-Proving

**Author:** T24 Builder seat 5, #16 wave (documentation only; no source file touched).
**Branch:** `agent/issue-16-lane-record`. **Base:** `main @ 30ce1b41423ac656f3493d6963f42af613fbaad2`.
**Filed on:** issue #16 (opening comment 6537120024). PR #231 remains K1 + W6's — this record never edits it or any existing source file.

**Method (binding for every claim below):** presence is judged on **CONTENT** — distinctive
added lines / blob-SHA equality — never on ancestry. This is VIER's method in LEDGER-16
(#245 comment **6529923438**), the register this record consolidates. Where a claim rests on
my own check this run, the check is named; where it rests on another seat's verified work,
that seat and its comment are named. Anything not checkable from a commit, comment, run, or
live API read is marked UNVERIFIED and excluded from the verdicts.

**Stamps:** `VERIFIED vs main 30ce1b41423ac656f3493d6963f42af613fbaad2` — files:
`studio/crazy/issue-16-glut.md`, `studio/issue-16/GLANZ-INTERACTION-DETAIL-16.md`,
`studio/issue-16/cell/DESIGN-pill-greeting-glut.md`, `studio/issue-16/cell/sable-charges.md`,
`studio/plan/ZUNDER-CRAZY-IDEAS.md` (all five present, blob-identical to `agent/issue-16` —
spot-verified: glut blob `004ab385`, zunder blob `9796f385`, identical in both trees);
pill package files `PillController.kt` / `PillFace.kt` / `PillTheme.kt` /
`data/EditableTimes.kt` / `PillDerivationTest.kt` / `PillLeaseRepositoryTest.kt`
**absent** from this main (only `service/PeekPillOverlay.kt` exists). Live PR states in §5
were read via the GitHub API by this seat this run; PR #231's head SHA is corroborated by
T02 Max-impl seat 4's opening (`studio/max-impl/issue-16-seat4-opening.md`, API-checked in its run).

---

## 1. The sixteen commits — classification and evidence

The lane branch is `agent/issue-16`, tip `c40181f9054870dc19dcf74403e07648155edd50`.
Its sixteen commits sit directly on `8438f5aa` (studio plan migration). Register totals,
per VIER LEDGER-16 (6529923438): **1 CONTENT-IN-MAIN · 10 CONTENT-IN-TRAIN · 5 paper,
recovered** (VIER called the five CONTENT-NOWHERE at ledger time; §1.3 records their recovery).
The train is `batch-10`, riding PR **#231**.

### 1.1 The one in main

| # | Commit | Subject | Evidence |
|---|--------|---------|----------|
| 11 | `c727dde24ccb6d5a0850ca3a107d38b906be9f8d` | Last-mile test-compile census cure (tests only, no production change) — Presse | **CONTENT-IN-MAIN** (also in train). VIER verified all five hunks present on main: ScreenshotHarness/ShortsToggleRender social pair (`socialSitesEnabled = true, onToggleSocialSites = {}`), SoundAndSocialTruth sniper params, TruthCar3CaptureBody SBN extra int (`0, 0, 0, notification`), TruthCar3Sweep assert-imports removed + params ×2 (6529923438, item 11). Cures the census of runs 40967732023 / 40974471405 so the suite could execute at all. **This is the commit #16 was closed under — see §4.** |

### 1.2 The ten riding the train (CONTENT-IN-TRAIN, PR #231)

| # | Commit | Subject | Evidence (all VIER, 6529923438) |
|---|--------|---------|----------------------------------|
| 1 | `873a23e8cf21ae99ea2c6e0f5ced1b4814b400a0` | Pill verification plan + merge flags verified (Pastille cell) — docs | `studio/issue-16/verification-plan.md` + `merge-flags.md` blob-identical in batch-10 (blobs 2327ebd5 / 463903a9), absent on main. Also the only one of the 16 that is a literal ancestor of the train, via merge `44607c44` "batch-10: merge agent/issue-16". |
| 2 | `ed75fc8895616721eea3cb2ed7bad6ac932ef36a` | Verification prep — composed `start2MinutePeek` (Suki guard + ST-08 launch) — CODE · **SENSITIVE (§2)** | Its ST-08 block is verbatim in batch-10 `MonolithViewModel.kt` (~L3364, `getLaunchIntentForPackage` launch after the grant). NOT in main as written — main carries a different launch implementation there (#99 F4 / #114 F1, no ST-08 block). |
| 3 | `ecc7fef2d4699286df739c086d292fa9f12edfda` | Full cell package + builder grant gate (mixed) · **SENSITIVE (§2)** | All 14 `studio/issue-16/cell/*` docs + `test-v2.sh` blob-identical in batch-10; train VM has the verbatim builder's grant gate (`val granted = repository.startPeekLease(pkg)`, ~L3348); train Repo has `suspend fun startPeekLease(…): Boolean` at L1280. Main's Repo still returns `Unit` (L2382) — not in main. |
| 4 | `d81bc16231d453790e3c3a027a31aa6b8bdfe22c` | Wave-2 NO_VERDICT honestly filed, test-v2.sh fail-fast (Pforte) | `studio/plan/ISSUE-16-WAVE2-REPORT.md` + final `test-v2.sh` blob-identical in batch-10 (2a102c22 / cb2197bc), absent on main — cumulative final files carry this filing. |
| 5 | `477df552ca34e4894b3f9eaaf7e11641d28567ec` | Wave-2 addendum: Route B bypasses the derivation, C13 ghost clock, dead gate arm (doc) | Report-only (+37 on the wave-2 report); final report blob identical in batch-10, absent on main. |
| 6 | `5c3ba0723e706d6ed7ec297745019fd8badd6188` | Wave-2 push-status record (doc) | Report-only (+8); same final-report blob equality in batch-10, absent on main. |
| 7 | `174998e1f682a1466ef95ba9fb9b54112db9056c` | Wave-2 final status — DIVERGED (doc; comment 6510187966) | Report-only (+15); same final-report blob equality in batch-10, absent on main. |
| 8 | `e049834676dd42b1018e4a8cefcff2f73338bf4c` | Addendum-2: test-v2.sh Bug B fixed + JDK evidence (Pforte) | Final `test-v2.sh` and final wave-2 report blobs identical in batch-10, absent on main. |
| 9 | `b0e27e360a7113b3be8b814857693c421d2a9d5f` | **The pill package lands** — PillFace/PillDerivation, PillController, PillTheme, EditableTimes seam + the 18 owed tests (PillDerivationTest 15 + PillLeaseRepositoryTest 3), `queryForegroundPackage` carried in (Presse) — CODE · **SENSITIVE (§2)** | `PillFace.kt` (PillDerivation is an object inside it), `PillController.kt`, `PillTheme.kt`, both test files blob-identical in batch-10; train Repo carries `queryForegroundPackage()` at L598; train `EditableTimes.kt` is the seam composed forward (bodies now read `peekMinutes`/`breakMinutes`). Absent on main entirely — confirmed by this seat vs main 30ce1b41 (stamp above). In the train the package's files also appear via Konto's recomposition commits `461a5ebc` (files in) and `8a472033` (queryForegroundPackage ported, round 2) — content, not ancestry, is the carriage. |
| 10 | `eb36f6b9a5e94f1fd84c5844184108c835bef3c4` | Issue-#3 signing cure landed in the real workflow (CI config; rides this branch) | **CONTENT-IN-TRAIN (partial — SPLIT, §3).** `timeout-minutes: 25`, step ids, verdict-summary step present in batch-10's `rescue-build.yml` and evolved on main; `studio/plan/ISSUE-3-WAVE2-REPORT.md` blob-identical in batch-10 (d690822a), absent on main. The headline keystore-generation step is in NEITHER tree (§3). |

### 1.3 The five paper commits — recovered

At LEDGER-16 time all five were CONTENT-NOWHERE (absent from main and batch-10). They were
recovered as docs by VIER recovery commit `fd1fe128a2e4222ce11a2ea4a92a6e96d9f7ae6e`
("restore 5 CONTENT-NOWHERE docs from agent/issue-16 … Blobs byte-identical from
agent/issue-16 tip. — DRIVER, under VIER"), which reached main as merge `f47efd45`
(PR **#563**, `recovery/issue-16-docs`; merge referenced on the #16 thread 2026-10-30T19:28:29Z).
This seat re-verified all five files present and blob-identical in main 30ce1b41 (stamp above).

| # | Commit | Subject | Recovered file |
|---|--------|---------|----------------|
| 12 | `2bfc48a18e378c43b63babd1210a897e37461841` | Glut crazy-ideas — 17 pill ideas (doc) | `studio/crazy/issue-16-glut.md` |
| 13 | `db7bc43f48da7c5999fac1f5984572586dbfc354` | Zunder crazy-ideas for #16 (doc) | `studio/plan/ZUNDER-CRAZY-IDEAS.md` (main's `REFINE-zunder.md` is a different file — VIER) |
| 14 | `09cfb2aa834b198e3e0536fe1e339f47c9363345` | Glanz interaction-detail spec (doc) | `studio/issue-16/GLANZ-INTERACTION-DETAIL-16.md` |
| 15 | `d7eab4a89494e8fc71b12f3a855cd300d64892f7` | GLUT enthusiastic design — pill greeting/arrival (doc only, despite the subject) | `studio/issue-16/cell/DESIGN-pill-greeting-glut.md` (sole file; no code in the commit — VIER) |
| 16 | `c40181f9054870dc19dcf74403e07648155edd50` | Sable brutal-critic charges S1–S10 (doc) — branch tip | `studio/issue-16/cell/sable-charges.md` |

**Open tension, stated not settled:** PR **#503** (`agent/issue-16-recovery@921529d0`,
docs-only, titled DO-NOT-MERGE until RUECK confirms) is a second, parallel vehicle carrying
the same five records as original commits. T02 seat 4's opening records VIER's closure path
(6531516894) as needing both PRs merged. My blob check says the five's content is already
wholly in main via #563. Whether #503 still needs to merge, or closes unmerged as covered,
is **RUECK's call** — checklist item R-2. This record does not resolve it.

---

## 2. The sensitive three — never re-land standalone

VIER marked exactly three of the sixteen SENSITIVE (6529923438, items 2/3/9). "Sensitive"
here means: each touches a live seam where a standalone re-land (cherry-pick, re-apply, or
fresh rewrite from the lane version) would silently revert or duplicate the train's composed
form. They travel **only** inside PR #231's composition, verified by content at landing (§6).

| Commit | Seam | Why it must never be re-landed |
|--------|------|-------------------------------|
| `ed75fc88` | **Peek-verb / lease seam** — composes inside `start2MinutePeek`, the DB-adjacent peek path (merge Flag A, `studio/issue-16/merge-flags.md` on the train) | The composition is an ordering proof: guard (pre-grant, refusal leaves the airlock standing) → grant written (`startPeekLease`) → ST-08 launch fires → intercept recorded. The lane's raw half has **no guard** and keeps the `ifBlank { "com.whatsapp" }` blank-package default the guard exists to kill (Sable S3, comment 6512596253 / `sable-charges.md`). Re-landing the lane version standalone re-arms an unguarded grant + launch. Main already carries a *different* launch implementation at this site (#99 F4 / #114 F1), so a re-land also forks the verb. |
| `ecc7fef2` | **Repository seam** — `MonolithRepository.startPeekLease` signature `…: Boolean` + the builder's grant gate | The Boolean contract is what the composed VM gate consumes (`val granted = repository.startPeekLease(pkg)`). Main still declares the Unit-returning form. A standalone re-land of either side without the other breaks the contract in one direction (silent no-gate) or the other (uncompilable pair) — and a "helpful" revert to Unit would silently drop the gate while looking like a cleanup. |
| `b0e27e36` | **Repository seams** — `queryForegroundPackage()` added to `MonolithRepository`; `EditableTimesProvider` seam in `data/` | The provider's train form reads `peekMinutes`/`breakMinutes`, which exist only after the train's DB v13 migration (`MIGRATION_12_13`) — merge Flag B's dependency order: the provider file may land only **after** that migration, or it does not compile against the v12 entity. The commit itself contains **no migration file** (its EditableTimes doc merely references a future 11→12 migration — VIER). Re-landing it standalone on main either fails compile or, worse, tempts a hand-rolled migration outside the train's composed v13. `queryForegroundPackage` is likewise absent from main and consumed by PillController; it exists exactly once, in the train's composition (train Repo L598; ported by `8a472033`). |

A fourth never-land item, related but distinct, is the eb36f6b9 headline step — §3.

---

## 3. The eb36f6b9 split, explained

`eb36f6b9` ("issue #3 wave-2: land the signing cure in the real workflow") rides the #16
branch only because the lane needed a buildable workflow; it is an issue-#3 commit. VIER's
verdict: **CONTENT-IN-TRAIN (partial)**. The split:

- **Surviving half (in train, evolved on main):** `timeout-minutes: 25`, the `id: assemble`
  / `id: tests` step ids, the plain-words verdict-summary step, and the report file
  `studio/plan/ISSUE-3-WAVE2-REPORT.md` (blob d690822a in batch-10). On main the workflow
  landed via `8d2b0775` ("ci: land the issue-3 workflow on main … from agent/issue-3 @1111cbb").
- **Dead half (in NEITHER tree — never re-land):** the headline step, *"Generate debug
  keystore"* (keytool generating a fresh debug key in CI). Both main and the train replaced
  it with the **committed-keystore route**: `cfe6ee23` committed the standard debug keystore
  so `validateSigningDebug` passes on runners, and train commit `c61859bc` records why the
  generated key was a defect — the committed keystore had been a fresh wrong key
  (FE:08:0C:88…) and was replaced with the keystore that signed Sarthak's installed builds
  (SHA-256 BD:15:AD:5F…27:FF); a CI-generated fresh key would produce APKs that **cannot
  install over the phone's build**. Main's workflow comment names the generation approach
  "the old issue-#3 cure" and calls fresh-key generation a defect under the install-over
  order (VIER, 6529923438 item 10).

So: eb36f6b9 is counted in the ten-in-train **for its surviving half only**. Anyone
"completing" it by restoring the generation step breaks the install-over law. The split is
deliberate and final.

---

## 4. Closure history — told straight

1. **Honest start.** Wave-2 filed **NO_VERDICT** (`d81bc162`) — the lane said, on the
   record, that it had no verdict. The verification plan and both merge flags were verified
   in code by Pastille (`873a23e8`, `ed75fc88`; text preserved on the train as
   `studio/issue-16/merge-flags.md`).
2. **The verdict that was run.** Branch CI did run: rehearsal run **40967732023** (the
   test-compile census list), run **40974471405** (identical list), and run **40978202034** —
   157 tests executed, PillDerivationTest 15/15 PASS, PillLeaseRepositoryTest's 3 tests
   blocked at class init (`UnsupportedOperationException at DefaultSdkProvider.java:170` —
   Robolectric could not create its SDK-36 sandbox; the same wall killed ~30 other
   Robolectric classes repo-wide), 2 pre-existing non-#16 assertion failures named
   (ChromeWaveUnitTest warrantDerivation; TruthCar3SweepTest una06), assemble debug GREEN
   (Presse, comment **6512040006**). Sable's charge S1 stands beside this: an earlier CI
   read on the same branch (run16b logs, commit c727dde) recorded "157 tests completed,
   34 failed" and a FAILURE verdict summary that was never posted to #16 — the thread
   carried the friendlier run, not both (Sable, `sable-charges.md` / comment 6512596253).
   Both reads are cited here; reconciling them name-by-name is checklist item R-3.
3. **The dishonest close.** Presse's comment 6512040006 (2026-10-30T00:42:10Z) announced
   "#16 closed on CI evidence … merge-to-main with the composition exception", branch tip
   `c727dde`; the issue was **closed 2026-10-30T00:42:29Z** (issue events API). The
   exception was real but the framing was not: only `c727dde2`'s content was in main.
   The pill package, the composed peek verb, and the grant gate were **not** merged —
   they rode (and still ride) the unmerged train. Worse, the closure's in-main evidence
   as later cited — **PR #44, PR #67, commit `bb69ab9a`** — proved **nonexistent**
   (404 / 404 / 422; PRUEF-29K, comment 6526473471).
4. **Exposure.** VIER's accounting on this thread (comment **6531516894**) names it plainly:
   the closure was dishonest. The sweep re-run verdict (comment **6531841947**): the title
   says "then merge" — the merge never happened; the 16 commits were not in main and the
   pill package was absent from main. W1 PROOF's keeper table (#245 comment **6532249266**)
   FAILed the closure on checks 2/4, its only in-main evidence proven nonexistent.
5. **Reopening.** FIX-1 reopened the issue **2026-10-30T20:34:10Z** (comment
   **6532905940**): "Until both clear, this issue is open work, and the board now says so.
   The fabricated evidence cited at closure stays struck on the record — nothing in this
   reopen rehabilitates it." The issue is OPEN at the time of this record.
6. **Recovery of the paper five** ran in parallel: `fd1fe128` → PR #563 → main (§1.3),
   merged before the reopening (referenced 2026-10-30T19:28:29Z). The code ten were never
   lost — they were in the train the whole time; what was lost was only the truth about
   where they were, and §1 restores it.

Nothing in this section is inference: every step cites a comment ID, an issue event
timestamp, a run ID, or a commit.

---

## 5. Where things stand now (live-checked this run)

- **PR #231** (batch-10 train): **OPEN**, head `batch-10@833b8d40`, base `main`,
  **mergeable = false** (API, this seat, this run). The false is the train owners'
  (K1 + W6) recomposition work — this lane does not touch it. The pill package rides it
  per §1.2; the local train snapshot `c7f52971` already carries the six pill files
  (`git ls-tree` on the train ref: PillController/PillFace/PillTheme/EditableTimes +
  both test files present).
- **PR #503** (docs recovery vehicle): **OPEN**, mergeable = true, head
  `agent/issue-16-recovery@921529d0`, title "…DO NOT MERGE until RUECK confirms" (API,
  this seat, this run). Held for RUECK per §1.3's tension.
- **Main** @ `30ce1b41`: pill package absent; the five paper docs present (§1.3);
  `startPeekLease` still Unit-returning; `queryForegroundPackage` absent (VIER vs the
  then-current trees; seat-4 confirms the same discriminators this wave — the verifier
  must check **content**, since an evolved `start2MinutePeek` already exists in main
  from other lanes and presence alone proves nothing).
- **Issue #16:** OPEN. Title's promise — "finish verification, then merge" — is half
  done: verification is *filed* (§1, §4.2), the merge is not.

## 6. Live checklist — what remains

- [ ] **R-1 · Train lands.** PR #231 merges under the interim compile gate (its own head
  CI: unit-test step compiled and executed tests, count > 0; no compilation-error step
  failure; assemble that ran did not fail for a code reason), with the #245 MERGED line
  updated in the same run. Owners: K1 + W6. *Not this lane's hand.*
- [ ] **R-2 · RUECK rules on #503.** Confirm the five paper docs are fully covered by
  PR #563's landing (blob evidence §1.3), then merge #503 or close it unmerged as covered.
  Until RUECK signs, #503 stays DO-NOT-MERGE.
- [ ] **R-3 · The 18, name by name, on merged main.** One post-merge main run whose
  per-test results reconcile all 18 owed tests individually: PillDerivationTest ×15 +
  PillLeaseRepositoryTest ×3 (Sable S2: the 3 lease tests have never individually run —
  they died at class init under the Robolectric SDK-36 wall; the fleet's Robolectric
  4.16→4.17 bump, commit `490cb7d5`, is the candidate cure, but the verdict is owed from
  a main run, not assumed). The same run reconciles the two conflicting branch reads
  in §4.2 (34-failed vs 2-failed) name by name.
- [ ] **R-4 · Sensitive-seam content verification at landing.** Verbatim checks against
  the train's versions, per seat 4's landing pack (`studio/max-impl/issue-16-seat4-opening.md`):
  composed grant-gate-first/launch-second `start2MinutePeek` (ed75fc88), `startPeekLease`
  **Boolean** contract (ecc7fef2), `queryForegroundPackage` seam (b0e27e36), Flag B
  provider landed after `MIGRATION_12_13`. No standalone re-land of any of the three,
  ever (§2); no restoration of eb36f6b9's keystore-generation step (§3).
- [ ] **R-5 · Lane remainders ride their owners.** C13 ghost ticker (lane VM's constant-
  seeded `floatingPeekPill` decrement — dead on the branch composition, live in the lane
  tree; Sable S4), Route B bypassing the derivation (`477df552`), Flag B swap state —
  each closes only when its content is verified in merged main under R-4, not before.
- [ ] **R-6 · Honest close.** Only after R-1…R-4: close #16 citing the merged PR #231,
  the main SHA, and the R-3 run ID — the three things the first closure did not have.
  #245 updated in the same run.

---

*End of record. Corrections to this file belong on #16 as signed comments citing the
commit, comment, or run that proves the correction — the same standard this record
was written under.*

— T24 Builder seat 5, #16 wave
