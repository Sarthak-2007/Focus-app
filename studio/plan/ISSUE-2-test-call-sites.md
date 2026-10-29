# ISSUE #2 — FIX THE BATCH-9 TEST CALL-SITES

**Cell:** FUGE's cell for GitHub issue #2, `Sarthak-2007/Focus-app`
(fix the batch-9 test call-sites). Branch: `agent/issue-2`.
Clone: `builds/cells/issue-2/`.
**Filed:** Oct 30, 2026, by FINISHER 5 (plan-file finisher).
**State at filing: DECLARED, not proven.** The partial fixes sit
uncommitted in this clone's working tree (5 files modified, see §3).
Nobody in this cell has compiled anything, and nobody will — §7 is
the close rule and it binds the whole cell.

**Ground, checked in this clone at filing time, not from summaries:**

- Production signature: `ParkDrawer` in
  `app/src/main/java/com/example/ui/components/ParkDrawer.kt`
  (declaration at ~:128–147).
- App build config: `app/build.gradle.kts` — `compileSdk 36.1`,
  `targetSdk 36`, `minSdk 24`.
- CI: `.github/workflows/rescue-build.yml` — on every push it runs
  `assembleDebug` and then `testDebugUnitTest` on GitHub's machines.
- Working-tree evidence: `git diff HEAD` / `git diff --numstat HEAD`
  on branch `agent/issue-2` (see §3 for the reconciliation).

---

## 1 · THE PROBLEM — LANE-GENERATION DRIFT IN TEST CALL-SITES

The app itself is not the problem. Batch 9 built and was delivered
(Oct 30); what does not compile is the **unit-test source set**.

The cause is lane-generation drift. The tests were written in
different lanes, at different times, against different generations
of the same production composable:

- Truth Car 3 made the Shorts/Reels sniper a persisted Guard
  setting (UNA-01). `ParkDrawer` gained two **required, no-default**
  parameters: `shortsReelsSniperEnabled: Boolean` and
  `onToggleShortsReelsSniper: () -> Unit`.
- The social-sites / browser-blocker work gave `ParkDrawer` two
  more required, no-default parameters: `socialSitesEnabled:
  Boolean` and `onToggleSocialSites: () -> Unit`.

Production moved; `MainActivity.kt` (its `ParkDrawer` call at ~:651)
moved with it. The test call-sites did not all move. Each test file
still speaks the generation of the lane that wrote it:

- Files from the shorts generation already pass the shorts pair but
  predate the social pair.
- The sound/social file already passes the social pair but predates
  the shorts pair.
- The Truth Car 3 sweep file predates **both** pairs.

Because neither pair has a default value, every stale call-site is
a hard compile error — `No value passed for parameter …` — not a
warning. Kotlin then reports the test source set as failed, and the
suite verdict the studio needs simply does not exist.

A second, independent drift of the same kind sits in the same
source set: a platform-constructor drift. The test that builds a
`StatusBarNotification` by hand was written against the 9-argument
constructor; under SDK 36 the only public constructor takes 10
arguments (the only public form left is the @Deprecated one
carrying a `score` parameter). Same disease, different organ:
the caller froze, the callee moved.

**Scope discipline:** this issue fixes **callers in tests only**.
No production file changes. In particular, nobody "fixes" this by
adding default values to `ParkDrawer` — that would silently change
the production contract to accommodate stale tests, and it would
hide exactly the drift this issue exists to expose.

---

## 2 · ERROR CENSUS

Census method: every `ParkDrawer(` call in
`app/src/test/java/com/example/*.kt` read against the current
production signature, plus the two further error sites named on the
issue. Five `ParkDrawer` call-sites exist in tests — all five are
stale. No sixth site was found by grep.

### Class A — missing the social pair (2 call-sites)

| # | File | Site | Already passes | Missing |
|---|---|---|---|---|
| A-1 | `ScreenshotHarnessTest.kt` | `ParkDrawer(` at ~:333 | shorts pair | `socialSitesEnabled`, `onToggleSocialSites` |
| A-2 | `ShortsToggleRenderTest.kt` | `ParkDrawer(` at ~:53 | shorts pair (`shortsReelsSniperEnabled = storedShorts`) | `socialSitesEnabled`, `onToggleSocialSites` |

### Class B — missing the shorts pair (1 call-site)

| # | File | Site | Already passes | Missing |
|---|---|---|---|---|
| B-1 | `SoundAndSocialTruthTest.kt` | `ParkDrawer(` at ~:369 | social pair (`socialSitesEnabled = socialOn`, wired to `vm.toggleBrowserUrlBlocker()`) | `shortsReelsSniperEnabled`, `onToggleShortsReelsSniper` |

### Class C — missing both pairs (2 call-sites, 1 file)

| # | File | Site | Missing |
|---|---|---|---|
| C-1 | `TruthCar3SweepTest.kt` | `ParkDrawer(` at ~:301 | both pairs |
| C-2 | `TruthCar3SweepTest.kt` | `ParkDrawer(` at ~:337 | both pairs |

### Class D — platform constructor arity (1 site)

| # | File | Site | Error |
|---|---|---|---|
| D-1 | `TruthCar3CaptureBodyTest.kt` | `StatusBarNotification(` at ~:74 | 9 positional arguments supplied; SDK 36's only public constructor takes 10 — `(pkg, opPkg, id, tag, uid, initialPid, score, notification, user, postTime)` (the @Deprecated form) |

### Class E — dead imports (2 lines, 1 file) — SEE THE OPEN RISK, §6

| # | File | Lines at HEAD | Status |
|---|---|---|---|
| E-1 | `TruthCar3SweepTest.kt` | `import androidx.compose.ui.test.assertDoesNotExist` | Removed by the partial fix — correctness **unproven** |
| E-2 | `TruthCar3SweepTest.kt` | `import androidx.compose.ui.test.assertExists` | Removed by the partial fix — correctness **unproven** |

### Census totals

- **5** stale `ParkDrawer` call-sites, across **4** files (A×2, B×1, C×2).
- **1** stale platform-constructor call (D×1), in a **5th** file.
- **2** import lines in dispute (E×1 file, already counted under C).
- **5 test files** touched in total; **0 production files**.

Census limit, stated plainly: this census covers the errors named
on the issue plus the full `ParkDrawer` grep. It is not a compile.
A compiler stops and starts in its own order, and errors behind
these six sites — if any exist — are invisible until the first
compile (§6, Risk 2).

---

## 3 · STARTING POINT — THE MERGE TREE'S PARTIAL FIXES

This cell does not start from zero. The batch-9 merge tree's
working copy already carried partial fixes (recorded on the issue
as **5 files, +20/−8**), and those exact five files sit modified
and uncommitted in this clone's working tree on `agent/issue-2`:

| File | `git diff --numstat HEAD` | What the partial fix does |
|---|---|---|
| `ScreenshotHarnessTest.kt` | +3 / −1 | A-1: appends the social pair |
| `ShortsToggleRenderTest.kt` | +3 / −1 | A-2: appends the social pair |
| `SoundAndSocialTruthTest.kt` | +3 / −1 | B-1: appends the shorts pair |
| `TruthCar3CaptureBodyTest.kt` | +5 / −1 | D-1: inserts the 10th constructor argument, plus a 4-line comment explaining the SDK 36 arity |
| `TruthCar3SweepTest.kt` | +10 / −4 | C-1 + C-2: appends both pairs at both sites; E-1/E-2: deletes the two imports |

**Reconciliation of the two counts:** `git diff --numstat` in this
clone reads **+24 / −8**. The issue's **+20 / −8** is the same diff
counted without the 4 comment lines in `TruthCar3CaptureBodyTest.kt`.
Both numbers describe the same starting point; no line is missing
and no extra line has crept in.

**What the starting point is not:** it is not verified. It has
never been compiled in the merge tree or in this clone. It is a
head start, not a result — the state noted on the issue when the
cell was seated, carried into this branch, and now this cell's to
finish **on paper** and hand to the compile gate (§5, §7).

---

## 4 · FIX APPROACH, PER FILE

General rule for every `ParkDrawer` fix: append the missing
named arguments at the end of the existing argument list (before
the trailing `modifier` default, which no test passes), change
nothing else in the call, and touch no assertion in the file.
Values are chosen to be **neutral**: they must let the call compile
without changing what the test was already asserting.

### 4.1 `ScreenshotHarnessTest.kt` — A-1

Append:

```kotlin
socialSitesEnabled = true,
onToggleSocialSites = {}
```

Rationale: this harness renders PARK to capture it; it exercises
neither the social toggle nor its callback. `true` matches the
persisted setting's default, and the no-op lambda keeps the
harness honest — a toggle press in this test would be a harness
artefact, not behaviour under test. Already in the working tree.

### 4.2 `ShortsToggleRenderTest.kt` — A-2

Append the same social pair (`true` / `{}`), and nothing else.
The file's actual subject is the shorts pair, which it already
passes from a real stored value (`storedShorts`) — that wiring is
not to be touched. The social pair is scaffolding here, so it gets
the neutral values. Already in the working tree.

### 4.3 `SoundAndSocialTruthTest.kt` — B-1

Append:

```kotlin
shortsReelsSniperEnabled = true,
onToggleShortsReelsSniper = {}
```

Mirror image of 4.1/4.2: this file's social pair is real
(`socialOn`, wired to the ViewModel) and stays exactly as written;
the shorts pair is the scaffolding and gets the neutral values.
`true` is the persisted default, so the rendered drawer matches
what production would print for an untouched setting. Already in
the working tree.

### 4.4 `TruthCar3SweepTest.kt` — C-1, C-2, E-1, E-2

At **both** call-sites, append all four missing arguments:

```kotlin
shortsReelsSniperEnabled = true,
onToggleShortsReelsSniper = {},
socialSitesEnabled = true,
onToggleSocialSites = {}
```

Both sites render the drawer to assert its printed words
(the YPT line at the first site, the routines line at the second);
neither toggle is under test, so all four values are neutral.
Partial fix already does this at both sites — the cell builder's
remaining duty is a line-by-line re-read of both hunks against the
production signature (parameter names spelled exactly as in
`ParkDrawer.kt`; a misspelled named argument is the same compile
error wearing a different coat).

The import deletions (E-1/E-2) are **not** settled by this plan —
see §6, Risk 1. The plan's instruction to the builder: keep the
deletions as the partial fix left them, flag them in the cell's
declaration as the one unproven judgement call, and let the
compile verdict (§5) settle them. Do not restore them speculatively
and do not delete any further imports.

### 4.5 `TruthCar3CaptureBodyTest.kt` — D-1

Insert the missing `score` argument as the third `0` in the
positional run, so the call supplies, in order: package, opPackage,
`7` (id), `null` (tag), `0` (uid), `0` (initialPid), **`0`
(score — the inserted argument)**, the built notification,
`Process.myUserHandle()`, and the post time. Keep the 4-line
comment the partial fix added above the call: it records *why* a
bare `0` sits there, and the next SDK bump will need exactly that
note. No named-argument rewrite: the rest of the call is
positional throughout, and mixing styles in one constructor call
buys nothing. Already in the working tree.

### 4.6 Explicit non-fixes

- No defaults added to `ParkDrawer` in production (§1).
- No assertion, expected string, or test name changed anywhere.
- No other test file edited on suspicion. If the compile surfaces
  a further stale site, it enters this census as a new lettered
  entry with its own fix section — it is not fixed silently inside
  another file's hunk.

---

## 5 · VERIFICATION STANDARD

This issue is proven only by machines compiling and running the
suite — never by this plan, never by a careful re-read, never by
the cell's say-so. Two witnesses, in this order:

1. **Ren's compile (the gate).** On Sarthak's explicit word (§7),
   Ren compiles the branch and runs the unit suite —
   `testDebugUnitTest` — on the composed tree. The verdict that
   counts is the full output: compile success or the first error
   verbatim, then the suite's pass/fail counts with every failing
   test named. A green compile with a red suite is **not** a pass;
   a red compile is a return to §2, not a near-miss.
2. **GitHub Actions (the independent witness).** The branch's push
   triggers `rescue-build.yml`, which runs `assembleDebug` and
   `testDebugUnitTest` on GitHub's machines, on a clean checkout,
   with no local caches and no local state. Its run verdict is
   posted on issue #2 alongside Ren's. If the two witnesses
   disagree, the disagreement itself is the finding — it is posted
   as-is and investigated; neither verdict is quietly preferred.

**Posting standard:** the suite verdict — compile result, test
counts, failing tests by name, and the Actions run reference — is
posted on GitHub issue #2 by FUGE. Until that post exists, every
statement about this issue, in any file or comment, carries the
word **DECLARED** or **UNPROVEN**, never "fixed", "passing", or
"done".

**Acceptance bar, all four legs required:**

- [ ] Test source set compiles with zero errors.
- [ ] Full unit suite runs; every failure (if any) is named on the
      issue and is either pre-existing on the batch-9 trunk —
      proven by naming it as such — or returns the issue to open.
- [ ] The E-1/E-2 import question (§6) is settled by the compile
      output, one way or the other, and the settlement is recorded
      on the issue.
- [ ] Actions run on the pushed branch agrees with Ren's verdict.

---

## 6 · OPEN RISKS

### Risk 1 — the `assertExists` import question in `TruthCar3SweepTest.kt` (the named open risk)

The partial fix **deleted** both
`androidx.compose.ui.test.assertExists` and
`androidx.compose.ui.test.assertDoesNotExist` imports — yet the
file still **calls** both, four times: `.assertExists()` and
`.assertDoesNotExist()` on the nodes at the two sweep tests
(the YPT-word assertions after the first `ParkDrawer` site, the
routines-word assertions after the second).

Evidence for the deletion being right:

- Sibling tests use the identical calls with **no such import**:
  `CascadeDeckRenderTest.kt` calls `.assertExists()` /
  `.assertDoesNotExist()` repeatedly while importing only
  `assertIsDisplayed` from that package. That is consistent with
  both functions being members of `SemanticsNodeInteraction`
  (needing no import) in the Compose UI-test version this project
  pins (Compose BOM `2024.09.00`, `gradle/libs.versions.toml`).
- An **unresolvable** import is itself a compile error in Kotlin,
  while a merely unused one is a warning. If no top-level
  `assertExists` / `assertDoesNotExist` exists in that package,
  the two import lines at HEAD were errors in their own right and
  deleting them was mandatory, not cosmetic.

Evidence against certainty:

- Nobody has compiled either version. If this UI-test version
  **does** expose them as top-level extensions, the deletion moves
  the error from the import lines to the four call-sites, and the
  fix is to restore both imports exactly as at HEAD.
- The file's own history cannot arbitrate: it never compiled in
  its HEAD state (Classes C and E coexist in it), so "it worked
  before" is not available as an argument.

**Disposition:** keep the deletion, declare it unproven, and let
§5's compile settle it. The settlement costs one compile either
way; guessing costs the same compile plus a wrong plan. If the
compiler rejects the call-sites, restore the two imports and
record in the census that Class E was a misdiagnosis — the census
keeps the scar.

### Risk 2 — errors hidden behind the census

Kotlin compilation of a source set with six known error sites may
be masking further stale call-sites (other composables that gained
required parameters in other lanes, other SDK-36 arity changes).
The `ParkDrawer` grep bounds only `ParkDrawer`. The first compile
is therefore also a **second census**, and this plan expects it to
be read that way: any new error gets lettered into §2 before it
gets fixed (§4.6).

### Risk 3 — neutral values that are not neutral

`socialSitesEnabled = true` / `shortsReelsSniperEnabled = true`
were chosen as the persisted defaults. If any of the five sites
renders a surface whose printed state depends on either flag, a
`true` could change what a screenshot or a word-assertion sees and
surface as a *test failure* after the compile is fixed. That
failure would be a §5 suite matter, not a compile matter — and
the correct response is to re-derive the value from what the test
asserts, never to flip it until the red goes away.

### Risk 4 — SDK 36 constructor, runtime half

D-1 fixes the constructor's **compile-time** arity against
compileSdk 36.1. The Robolectric runtime the sweep/capture tests
execute under may resolve the class differently. Compile-green
does not prove this call constructs at runtime; only the suite
run in §5 does. One more reason the acceptance bar requires the
suite, not just the compile.

---

## 7 · THE DECLARED CLOSE RULE

**Nobody in this cell compiles. Nobody in this cell runs Gradle.**
Not the builder, not a finisher, not FUGE. There is no "quick
check", no proof compile, no exception for the import question —
§6 Risk 1 stays open on paper precisely because this rule holds.

**Ren compiles, and only on Sarthak's explicit word for that
build.** That is the standing build authority for the whole
studio, and this cell's work is written to be judged under it:

- The cell closes its work **DECLARED**: the five test files as
  fixed in §4 (code written, re-read against the production
  signatures, uncompiled), plus this plan, on branch
  `agent/issue-2`.
- FUGE commits and pushes the branch and posts the cell's
  declaration on issue #2 — including the one unproven judgement
  call (E-1/E-2) named as such.
- The push gives GitHub Actions its chance to witness (§5, leg 2);
  its verdict is evidence for Ren, not a substitute for the gate.
- **The issue closes only when** Ren's compile + suite verdict
  (§5, leg 1) is posted on issue #2 and the four acceptance legs
  in §5 are all met. If the compile fails, the issue stays open,
  the new errors enter §2's census, and the cell is re-seated
  flat under Ren for the next pass.
- Until that post exists, this issue's honest one-line state is:
  **fixes written and declared; compilation and suite verdict
  owed; owed by the gate, not by this cell.**

---

*Filed by FINISHER 5 (plan-file finisher), FUGE's cell, issue #2 —
Oct 30, 2026. This plan edits no code and no test; it is the map
the cell's builder works from and the standard its declaration is
judged against.*
