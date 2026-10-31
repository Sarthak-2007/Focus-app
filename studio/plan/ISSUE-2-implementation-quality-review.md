# Issue #2 — Implementation-Quality Review of the Fix Spec

**Seat:** Implementation-Quality (Anchor seat Q-4, issue #2), Fuge's cell
**Scope:** Spec-level judgement of the fix strategy for the batch-9 test
call-sites — reconcile call sites vs restore defaulted parameters vs
regenerate the harness — for correctness, and for whether the resulting
tests still test what they claim to test. This seat wrote no fix code
and compiled nothing; the compile verdict belongs to Ren and CI.
**Evidence base:** the issue census; the merge tree
(`builds/merge-batch9/Focus-app`, HEAD `1852181`) including its
uncommitted partial-fix diff (5 files, +20/−8); production source;
the Android SDK 36 / 36.1 stub jars on this machine; and the cell's
own posted record on this issue (FUGE's close at commit `8a256a4`,
FINISHER-B's bytecode check, wave-2's static sweep).

---

## 1. What is actually broken — three distinct root causes

The breakage is not one error repeated; it is three different diseases,
and a fix spec that treats them as one will mistreat at least one.

**RC-1 — Generational drift on `ParkDrawer` (4 files).**
The production composable (`ui/components/ParkDrawer.kt`) takes, with
**no defaults**:

```
shortsReelsSniperEnabled: Boolean,  onToggleShortsReelsSniper: () -> Unit,
socialSitesEnabled: Boolean,        onToggleSocialSites: () -> Unit
```

The test files were written against three different generations of
that signature:

| File | Generation written against | Missing at HEAD |
|---|---|---|
| `ScreenshotHarnessTest` | shorts era | social pair |
| `ShortsToggleRenderTest` | shorts era | social pair |
| `SoundAndSocialTruthTest` | social era | shorts pair |
| `TruthCar3SweepTest` | pre-toggle era | both pairs (×2 call sites) |

There are exactly 6 `ParkDrawer` call sites in the repo (5 test, 1
production in `MainActivity.kt:651`); the production site passes all
four parameters wired to real ViewModel state and toggles. No other
test file calls `ParkDrawer`.

**RC-2 — Dead imports that are themselves errors (`TruthCar3SweepTest`).**
`androidx.compose.ui.test.assertExists` / `assertDoesNotExist` do not
exist as top-level functions; `assertExists()` / `assertDoesNotExist()`
are **member functions of `SemanticsNodeInteraction`**. The import
lines are unresolved references in their own right. The call sites
(Sweep lines 321–322, 358–359) resolve through the member functions
and survive import removal untouched. No other test file imports them.
(Independent convergence: FINISHER-B verified the same fact by `javap`
against the project's ui-test 1.7 classes.jar — members of
`SemanticsNodeInteraction`, absent from `AssertionsKt`.)

**RC-3 — Platform constructor drift (`TruthCar3CaptureBodyTest`).**
The 9-argument `StatusBarNotification(...)` call has no applicable
candidate under SDK 36. Verified directly from the stub jars
(`platforms/android-36` and `android-36.1`, the latter being this
project's exact `compileSdk { release(36) { minorApiLevel = 1 } }`):
the **only** constructor descriptor present in either jar is

```
(Ljava/lang/String;Ljava/lang/String;ILjava/lang/String;IIILandroid/app/Notification;Landroid/os/UserHandle;J)V
```

i.e. 10 parameters, with **three** `int` slots after the tag. The
original call supplied two. Per the cell's stub-source check, the
inserted third `int` is the **`score`** parameter (an earlier draft of
the fix called it `instanceId`; that was wrong and the cell corrected
it in code and plan — the correction is part of why this fix can be
trusted).

## 2. The options, judged

### Option A — Reconcile the call sites

**Verdict: IMPLEMENTABLE WITH CHANGES — and the changes are the §3
conditions. The implemented form (commit `8a256a4`, 5 files, +37/−8)
satisfies them.**

This is the only option that fixes all three root causes without
touching production code or weakening a single assertion. The +37/−8
landed diff vs the +20/−8 partial-fix diff is documentation, not scope
creep: premise comments where an added value is load-bearing, and the
amended header in `ShortsToggleRenderTest`. That delta is blessed —
it converts silent luck into recorded intent. Per hunk:

- **ScreenshotHarnessTest / ShortsToggleRenderTest** — add
  `socialSitesEnabled = true, onToggleSocialSites = {}`. Correct
  shape; named arguments everywhere, so position is irrelevant.
- **SoundAndSocialTruthTest** — add the shorts pair as inert stubs.
  Correct: this file's subject is the social sled, and its social
  wiring is **reactive and real** (`collectAsState()` on
  `vm.browserUrlBlockerEnabled`, `onToggleSocialSites =
  { vm.toggleBrowserUrlBlocker() }`) — the fix preserved it. That
  wiring is the test's whole claim (Pruefer 02-1/02-2: touching the
  rendered sled writes the stored Room fact). Any variant of this fix
  that stubs the *social* pair in this file is a different, failing
  spec.
- **TruthCar3SweepTest** — add both pairs (inert `true`/`{}`) at both
  call sites, delete the two dead imports. Correct: the two tests
  assert plate text only (UNA-08 "STUDY HOURS: IN YPT"; UNA-05 words
  half, "FIRED AS SET" struck), composed with `snapshot = null`;
  neither assertion path reads toggle state.
- **TruthCar3CaptureBodyTest** — insert the third `0` (`score`).
  Matches the SDK 36.1 constructor descriptor **exactly** (see RC-3).
  Semantically inert for this test: the assertions read the captured
  row's sender/body, which come from the `Notification`, and the int
  slots were already dummy zeros in the original call.

### Option B — Restore defaulted parameters on `ParkDrawer`

**Verdict: NOT IMPLEMENTABLE AS THE FIX FOR THIS ISSUE.**

- It edits the **production** API to cure a **test** disease — the
  blast radius is inverted.
- Defaults are what let three generations of call sites drift apart
  silently in the first place; re-adding them re-arms the failure.
- Worst case is a truth-law hazard, not a compile detail: a defaulted
  `onToggleSocialSites = {}` means a future production call site can
  omit the toggle and compile fine while rendering a sled that writes
  nothing — a lying switch, undetectable by the compiler.
- The cell's CRITIC reached the adjacent observation (ShieldMatrixScreen
  shows a house pattern of defaults-with-reasons) and routed it as a
  **decision for Ren**, not as this issue's fix. This seat concurs
  with that routing. If defaults are ever ruled in, they need their
  own spec — in particular, a defaulted toggle callback is only
  acceptable if the default is a real write path, never a no-op.

### Option C — Regenerate the harness / rewrite the test files

**Verdict: NOT IMPLEMENTABLE AS SPECCED.**

- Disproportionate: the entire breakage is ~20 lines of call-site
  across 5 files.
- Destructive: these files encode Pruefer-standard intent in their
  docstrings and assertion choices (UNA-01/02/05/08 provenance, the
  OFF-count isolation argument in `ShortsToggleRenderTest`, the
  write-through polls in `SoundAndSocialTruthTest`). A rewrite
  re-litigates every one of those decisions and predictably emerges
  with weaker assertions that compile — tests that pass and prove
  less. That is the exact failure this seat exists to kill.
- The one file that is literally a harness (`ScreenshotHarnessTest`)
  needs the same two lines as everything else; it is not stale in
  structure, only in one call site.
- Note, distinct from C as specced: the "one door" test-side factory
  proposed on this issue (a single `ParkDrawerTestFactory` the five
  sites call through) is **not** a regeneration — it is a structural
  refactor of the same edits. Judged on its own: implementable as
  **follow-up hardening**, with the condition that the factory's
  defaults encode the same load-bearing values pinned in §3. It must
  not gate, delay, or replace the reconciliation fix, and if it is
  ever adopted, the proposed 4-combination neutrality render is the
  right proof to demand with it.

## 3. Conditions on Option A (binding on the builder)

1. **Stub values are load-bearing, not free choices.** Two files'
   assertions silently depend on the inert stub being `true`:
   - `ShortsToggleRenderTest`: its premise is that the *only* "OFF"
     text node on the sheet is the sniper sled's margin word
     (`assertCountEquals(1)` when stored shorts = OFF). Passing
     `socialSitesEnabled = false` would print a second OFF from the
     social sled and break the count. Must stay `true`. (The social
     row with the setting ON contributes no OFF node — the amended
     header in `8a256a4` records exactly this.)
   - `SoundAndSocialTruthTest`: `SocialBoardWordFollowsTheToggle`
     asserts `onNodeWithText("OFF").assertDoesNotExist()` before the
     social sled is touched. A `shortsReelsSniperEnabled = false`
     stub would print the sniper sled's OFF from the first frame and
     break it. Must stay `true` — `8a256a4` pins this in a premise
     comment in the file, which is where it belongs.
   Any retyping that flips either value produces compiling tests that
   fail at runtime for reasons that look unrelated.
2. **Transplant, don't retype.** The fix descends from the merge
   tree's uncommitted partial diff (5 files, +20/−8). Re-authoring
   from scratch is how condition 1 gets violated; the cell's
   transplant-then-complete path was the right one.
3. **No production file is touched; no assertion is edited.** The
   footprint is call-site arguments, premise comments, and two import
   lines only. If a builder finds themselves editing an assertion to
   make a test pass, the fix has left its spec — stop and report.
4. **Import removal is Sweep-only.** The two dead imports existed
   only in `TruthCar3SweepTest` (grep-verified across the test tree).
   Other files' `androidx.compose.ui.test` imports are real top-level
   functions (e.g. `assertCountEquals` in `ShortsToggleRenderTest`)
   and stay.

## 4. Residual risks and scope limits

- **This review certifies the approach, not the suite outcome.** The
  suite has since run under CI and the verdict was posted on this
  issue (258 tests; the failures in that run are runtime findings to
  be triaged on their own evidence, separated from stale-call-site
  noise per the acceptance bar — they are not a defect in this fix's
  spec, and this fix's spec is not a claim that they pass).
- **Robolectric's android-all jar** for SDK 36 is built from the same
  API surface the stub jar describes, so the `StatusBarNotification`
  constructor match is high-confidence; the runtime proof is the
  test run, not this document.
- **Static-sweep blind spots** (wave-2 stated them correctly): type
  drift and overload resolution are invisible to call-site scans.
  The first compile is the second census; any new error class gets
  lettered and reported before it is fixed, not folded in silently.

## 5. Headline

Option A (reconcile the call sites) is **implementable with the §3
conditions**, its implemented form at `8a256a4` meets them, and the
resulting tests still test what they claim to test: every assertion,
the one reactive write-through wiring, and both isolation premises
survive unchanged. Option B is **not implementable as this issue's
fix** (production defaults re-arm the drift and risk a lying sled;
correctly routed to Ren as a separate decision). Option C is **not
implementable as specced** (a rewrite would trade proven test intent
for weaker tests); the one-door factory variant is implementable
only as separate follow-up hardening under §2C's condition.

— Implementation-Quality (Anchor seat Q-4, issue #2)
