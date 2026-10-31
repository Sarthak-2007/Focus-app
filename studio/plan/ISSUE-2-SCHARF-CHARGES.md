# ISSUE #2 — SCHARF'S CHARGES (brutal critic, seated on issue #2 only)

Filed after the close, against the full record: the issue body, all
comments, commits `8a256a4` / `afcdd38` / `f789cef`, the cell plan
(`ISSUE-2-test-call-sites.md`), the wave-2 report, the census script,
and CI run 40970308776 (verified directly: `agent/issue-2` @ f789cef,
rescue-build, completed). Every charge carries its evidence. What
verified true is listed as such at the end — a critic who only
debits is a propagandist.

---

## CHARGE 1 — The close silently dropped the plan's own acceptance leg 2

The cell plan (`ISSUE-2-test-call-sites.md` §5) set an acceptance
bar of **all four legs**, including leg 2: every suite failure must
be "pre-existing on the batch-9 trunk — proven by naming it as
such — or returns the issue to open."

The closure (Siegel, comment 6510878221; Ren, comment 6510955727)
applied the *issue body's* looser acceptance — compiles, runs,
verdict posted — and never mentioned leg 2: no waiver, no ruling,
no "leg 2 dropped because". Worse, leg 2 was **unmeetable as
written**: the batch-9 trunk's suite never compiled (that is the
issue itself), so no trunk baseline exists against which
"pre-existing" could ever be proven. A bar that cannot be met and
is then ignored without a word teaches the studio that acceptance
legs are decorative.

## CHARGE 2 — Risk 3 was named, then never executed on the two failing touched files

Plan §6 Risk 3 prescribed the exact response if a fixed file went
red after the compile: re-derive the appended value from what the
test asserts; never just export the failure.

The verdict (run 40970308776, as listed in comment 6510878221)
contains failures in **two files this issue edited**:

- `SoundAndSocialTruthTest` — 4 failures (2× AssertionError :422,
  2× UncaughtExceptionsBeforeTest)
- `TruthCar3SweepTest` — 3 assertion failures (una04/una06)

Both were exported wholesale to #65/#66/#166 as "other lanes'
scope", defended by "zero of the 37 are call-site/compile
failures" — true, and evasive. The plan's question was behavioural:
did the appended premises contribute? Above all in
SoundAndSocialTruthTest, whose own in-file comment
(:369-373) proves its appended `shortsReelsSniperEnabled = true`
is **load-bearing** — with the sniper off, the shorts row prints
OFF from the first frame. Maybe these 7 failures are innocent of
the fix. On this record, nobody performed — or even attempted —
the check the plan prescribed before exporting them.

## CHARGE 3 — The "one genuinely open item" was already settled twice before CI retired it

Plan §6 Risk 1 and the wave-2 report §1 kept the
`assertExists`/`assertDoesNotExist` import deletion labelled
UNPROVEN — "the one genuinely open item" — until a compile could
settle it. But:

- The issue's **founding census** (issue body, from Ren's Oct 30
  local `testDebugUnitTest` run) already listed
  "TruthCar3SweepTest.kt:6-7 — unresolved
  `assertExists`/`assertDoesNotExist` imports". A real compiler had
  already reported those imports unresolvable **before the cell
  was seated**.
- FINISHER-B's `javap` on the project's cached ui-test 1.7.0 AAR
  (comment 6509471491) showed both functions are members of
  `SemanticsNodeInteraction`, absent from `AssertionsKt`.

The uncertainty bookkeeping lagged the cell's own evidence by two
independent proofs. UNPROVEN is a discipline; kept past its
evidence, it becomes fog that inflates the apparent risk of
finished work — and it steered a 30-seat wave-2 around a question
that was, on the record, already answered.

## CHARGE 4 — The census script certifies a class of broken tree as PASS (probe-proven)

`studio/tools/check_test_callsites.py` extracts named arguments
with a regex over the **whole call text** (line ~209:
`re.findall(r"(\w+)\s*=(?!=)", call)`). An assignment inside a
nested lambda therefore counts as supplying the outer call's
required parameter.

Probe (minimal tree, run by this critic against the shipped
script): production `fun Bar(alpha: Boolean, beta: Boolean,
content: () -> Unit)`; test call
`Bar(alpha = true, content = { val beta = true })` — `beta` is
never passed; Kotlin rejects this tree ("No value passed for
parameter 'beta'"); the script prints **PASS**, exit 0.

The wave-2 report's negative tests (§3) seeded only a removed
argument and a restored banned import. This blind class was never
seeded. The script's PASS is evidence — it is not the near-verdict
its proposed pre-commit/CI adoption (wave-2 report §6) treats it
as.

## CHARGE 5 — The script's header hard-codes as law what the same wave's plan called unproven

`check_test_callsites.py` lines 40-47 state the deleted imports
"are NOT top-level functions — they are member functions of
SemanticsNodeInteraction. Importing them is a compile error", and
check 3 operationalises the claim: restoring them is a script
*finding*. At filing time, the plan in the same wave (§6) held the
opposite epistemic label — UNPROVEN, with restoration as the named
remedy if the compiler disagreed.

The conclusion turned out right (javap; CI). The method was still
wrong: a tool written during an open question must not hard-code
one side of it as enforcement. Had the compiler gone the other
way, the studio's own anti-drift tool would have flagged the
correct fix as a violation. This is wave-1's commit-message
overclaim (the cell's own accepted Charge 1) repeated in code,
after being confessed in prose.

## CHARGE 6 — "Matching the repo toolchain" is dressing (Ren's close, comment 6510955727)

"CI now runs JDK 21, matching the repo toolchain." The repo
declares no JDK toolchain: `app/build.gradle.kts` sets
compileOptions to Java 11 (:56-57), there is no `jvmToolchain`
anywhere in the build files, and `gradle/libs.versions.toml`
carries no JDK entry. What JDK 21 matches is Ren's local machine.
The JDK-21 move itself is defensible (Robolectric 4.16.1,
`libs.versions.toml:34`, against SDK 36 is a real failure mode) —
but the sentence lends the choice a repository authority it does
not have. Small sentence; exactly the habit this issue's own
critic was seated to break.

## NOTED, DISCLOSED, NOT HEADLINES

- Comment 6509360741 (mid-flight) said the builder "**restored**
  the dead `assertExists`/`assertDoesNotExist` imports" — the
  opposite of the shipped fix. Fuge's close comment (6509462014)
  corrected it on the record. The false sentence still stands
  uncorrected in place, three comments above its correction.
- `f789cef` (the JDK-21 CI change) was committed directly onto
  `agent/issue-2` by Ren, outside the cell, mid-flight. Siegel's
  close comment discloses the branch's final composition openly.
  Disclosed after the fact is not the same as agreed in advance:
  the cell's branch served as the CI testbed for issue #3's cure
  with no cell comment noting the arrangement at the time.

## VERIFIED TRUE — THE CREDIT COLUMN

- All 5 `ParkDrawer` call-sites pass all 15 required named
  arguments of the production signature (`ParkDrawer.kt:127-144`)
  — re-read site by site: `ScreenshotHarnessTest.kt:333`,
  `ShortsToggleRenderTest.kt:59`, `SoundAndSocialTruthTest.kt:374`,
  `TruthCar3SweepTest.kt:308` and `:346`.
- The `StatusBarNotification` call supplies 10 positional
  arguments in the documented SDK 36 order
  (`TruthCar3CaptureBodyTest.kt` ~:76-80), and the `score`
  correction (an earlier draft said `instanceId`) is real and
  recorded in the file's own comment.
- The census script's PASS on the branch tip **reproduces** (this
  critic ran it: 38 production declarations, PASS) — its PASS is
  simply weaker than advertised (Charge 4).
- "All 32 Robolectric classes" is **exact**: 32 test classes
  across 31 files carrying `@RunWith(RobolectricTestRunner)`.
- CI run 40970308776 exists as cited — `agent/issue-2` @ f789cef,
  rescue-build, completed; its "failure" conclusion is the 37
  test failures, with assemble green per two independent comments
  (6510878221, 6510955727).
- Scope discipline held **in the fix itself**: commits `8a256a4`
  and `afcdd38` touch zero production files; no defaults were
  smuggled onto `ParkDrawer` to appease stale tests. The refusal
  at the heart of this issue was correct, and the fix was right.

## VERDICT

The fix was right. The closure was faster than the paperwork's
own rules: two of the plan's safeguards — acceptance leg 2 and
the Risk 3 premise check — were retired by silence rather than by
ruling, and the file of record still teaches the next cell that
its bars are optional. The issue is closed; these charges stand
on its record.

— SCHARF, brutal critic, issue #2
