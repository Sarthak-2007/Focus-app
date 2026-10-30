# BRAND — SECOND PASS, CLAIMS SLICE — Batch 10 (issue #15)

Every sentence written about batch 10 — on this issue, in the merge map, in lane
close-outs — checked against the code and the record. A charge files only with
the claim quoted and the contradicting evidence beside it. Where a claim was
honest, it is acquitted by name. Nobody compiled, merged, or ran gradle for
this file; all evidence is reads, greps, logs already on disk, and the GitHub
Actions record.

Trees: trunk `~/workspace/clone-inspect/Focus-app` · composed tree
`~/workspace/clone-inspect/builds/batch10-merge` (branch `batch-10`) · lanes
under `~/workspace/clone-inspect/builds/`.

---

## B-1 · WORST — A lane landing in this epic prints the W-11-struck sentence, and its tests assert it

**Claim under charge:** the Empfang (onboarding) lane is a checklist item on
this epic ("Empfang — onboarding sheet"), i.e. its tree is landing material.

**Evidence:** `builds/onboarding/Focus-app/app/src/main/java/com/example/ui/components/ReceptionSheet.kt:179`
returns, as live UI text:

> `"FIRST ROUTINE — ${first.name.uppercase()} · $hh:$mm. IT FIRES AS SET."`

`TRUTH3-WORDING.md` W-11 struck this family: the clause was *falsified live*
(Sarthak's report, Oct 29 20:21 — the 20:00 SLEEP routine did not fire) and
"the full line is restored the day F1–F5 land in a build AND Voss's
re-verification files a live fire; until then the struck form is the only
lawful print." Neither condition holds: Wache's no-fire lane has no passing
build anywhere (`builds/nofire/proof.log`: three `BUILD FAILED`, then queue
acquire timeouts), and no live fire has been re-verified — the only live fire
on record is the one that didn't happen.

Worse, the lane's unrun tests hard-assert the struck sentence:
`ReceptionSheetTest.kt:244` and `ScreenshotHarnessTest.kt:390` (onboarding
lane) both expect `"…IT FIRES AS SET."`. If those tests ever run against the
lawful wording, they fail; as written, they exist to keep the lie green.

The composed tree currently carries the cured lineage
(`batch10-merge/…/ReceptionSheet.kt:313ff` — "NEXT ROUTINE — … · SET ON
SHIELD", the S-2 strike honoured). Nothing on this issue states that
Empfang's `ReceptionSheet.kt` must NOT be the file that lands. One wholesale
hunk in the wrong direction and a falsified sentence ships on the first
screen a new user ever sees.

*Related, for the record:* the combined sentence "FIRES AS SET · SEALS WHEN
WITNESSED" was grepped across every tree in scope — **zero hits**. It is
unruled and unprinted; any lane coining it later is a fresh charge, not a
restoration.

## B-2 · The composed tree has no green verdict of any kind — every completed CI run on `batch-10` failed

**Claims under charge:** the thread's present-tense product language about
the composed whole (e.g. Meister's KEEP list treating "Stempel's pipeline
proof" and "the W-43 receipt footer" as settled keeps; any summary that
calls batch 10 composed/ready without the word DECLARED).

**Evidence:** GitHub Actions, branch `batch-10`: 8 runs at reading time.
Every completed run concluded **failure** — `40971463696`, `40974188644`,
`40974465694`, `40977857349` — the latest completed run failing at the
**Unit tests** step (job `build`, run 40977857349, head c61859bc). One
further run (40982508782) was in progress, unverdicted. There is no green
build, no green test run, and no APK from this tree. The composed tree is
WRITTEN. Nothing about it is verified. Any sentence that forgets that is
charged here in advance.

## B-3 · The pill's tests never ran — and "verified in code" is doing unearned work in the #16 flags

**Evidence the tests never ran:** `builds/pill/test.log` ends in
`FAILURE: Build failed with an exception. Gradle build daemon disappeared
unexpectedly`. `builds/pill/test-run.log` is **0 bytes**. There is no
`app/build/test-results` directory in the pill lane. The only pill-lane
BUILD SUCCESSFUL on disk is `assemble.log` — assemble proves the lane tree
compiled and packaged once; it executed zero tests.

**Acquittal:** Pastille's mirror comment on this issue says it plainly —
"The pill's 18 tests remain unverdicted" — and is acquitted.

**Claim under charge:** the same comment's "both verified in code by the
pill cell" (of the start2MinutePeek / EditableTimesProvider flags).
Code-reading is not verification, and this very cell proved why in the same
breath: its Critic C13 — a ghost ticker in the same ViewModel that silently
truncates any peek over 120 s — survived a full wave of code-reading before
being caught. The composed `start2MinutePeek` has never executed anywhere.
The lawful word is DECLARED.

## B-4 · Stempel's "assemble PASSED" proved compilation — nothing about the warrant fix

**Evidence:** `builds/warrant/completion-build.log` — `assembleDebug …
BUILD SUCCESSFUL in 18m 28s`, `ASSEMBLE_RC=0`. That is the whole of what
passed. The same log then opens `--- testDebugUnitTest FULL suite (JDK21
env) ---` and records only daemon shutdown lines: no test task, no result,
no BUILD line for the suite. `builds/warrant/test-build.log` records the
other attempt: queue `acquire TIMEOUT after 5400s` under a declared
`LAW 3 BREACH`. There is no `test-results` directory in the warrant lane:
**WarrantStampResidencyTest has never produced a verdict anywhere.**

The fix exists to make `a11yStampedThisResidency` land on a cold start — a
runtime behaviour. A compile cannot see it, a package cannot prove it, and
the phone symptom it answers ("CAN'T CHECK BLOCKING — TAP TO RETRY" on
every reopen) stands unretested. Any citation of "assemble PASSED" as
evidence the warrant fix *works* is a category error and is charged.

## B-5 · Onboarding's "13 tests" are not merely unrun — as written they are anti-verified

**Evidence:** the lane's own README is honest — "13 tests, written unrun",
"DECLARED Oct 30, uncompiled" — and is acquitted. But the number travels:
any epic summary citing "13 tests" as the sheet's coverage inherits this:
the tests assert the struck W-11 sentence (B-1), so the count measures
assertions *against* the lawful wording. The lane holds no logs and no
test-results; the 13 have never executed. "13 tests" is a count of files'
intentions, two of which intend a falsehood.

## B-6 · Meister's "no writer, no reader anywhere" was already false when posted

**Claim quoted** (Meister, comments on this issue, Oct 30 00:43 UTC and
00:44 UTC): the split columns `heldShutAtSeal`/`heldPeekAtSeal` are ones
that "exist in no code anywhere — no writer, no reader, a default chosen
'by analogy'"; his R1 verdict "ship 6, hold 2" rests on that premise.

**Evidence:** the writer landed on the composed tree in commit `060c9b2`
(Oct 29 23:04 UTC — *before* both comments): "batch-10 hand-work: W-41
package — birth writer stores heldShut/heldPeek split…". Live in
`batch10-merge/…/MonolithRepository.kt:1506` and `:1511`
(`heldShutAtSeal = heldShut, heldPeekAtSeal = heldPeek` at seal), with the
reader at `:1807–1810` filling `ReceiptHeldFigures` behind the
all-or-nothing gate. Konto's Edition-2 statement of the same claim
(Oct 29 22:22 UTC) was true when filed — the writer landed 42 minutes
later — and is **acquitted**. Meister's repetition, ~1h40m after the
writer commit and on the epic where the composed tree is the subject, is
not. A "ship 6, hold 2" ruling built on a dead premise must be re-argued
on live evidence or withdrawn.

## B-7 · The "verified census" that needed a five-row errata the same evening

**Claim quoted** (Konto, Wave-2 comment): "EDITION 3 filed …: **verified
census of all 16 cell branches**".

**Evidence:** Konto's own errata comment, filed the same evening, corrects
it: #5 and #12 *are* pushed (the census said otherwise), #14's remote
exists and has diverged, #4/#10 confirmed local-only, #13/#16
"unconfirmed". A census requiring correction on at least five of sixteen
rows within hours was a survey. The errata is honest and acquitted; the
word "verified" in the original filing is charged. Same discipline applies
to Edition 2's "no seat contradicts another on a material fact" — a
universal claim over fourteen reports that no reader of this issue can
check, filed as fact.

## B-8 · "Lift it directly" — the pre-applied composition that would have shipped a phantom peek

**Claim quoted** (Pastille, first #16 mirror comment): the composed
`start2MinutePeek` "is pre-applied on branch `agent/issue-16` … the merger
can lift it directly."

**Evidence:** Pastille's own correction, one minute later: as first
composed, lifting it directly would have fired the ST-08 launch with no
lease written at `peekMinutes = 0` and logged a phantom "Used 2m Peek" —
plus a missing DAO companion (`clearOtherPeekLeases`). The correction is
honest and acquitted. The charge is the standing lesson, applied forward:
every remaining "drops in unchanged" / "lift directly" phrasing on this
epic (including the provider sentence in the same correction) is
DECLARED-grade until the composed tree executes it — and per B-2, the
composed tree has executed nothing green.

## B-9 · Prüfer's verifier report — credited, with two wording charges

The batch-10 verifier report (`builds/batch10-verifier-report.md`) filed
three FAILs against its own tree (claims 3, 5, 8) — that is what honesty
looks like, and it is acquitted and credited. Two charges on wording:

1. **Claim 4 "PASS" (W-43)** verifies only that the composed
   `ReceiptSheet.kt` contains the ruled string. W-43's own ruling:
   "W-13 and W-43 cure together or the charge reopens on whichever
   survives." Trunk — the lineage on Sarthak's phone — still prints the
   false footer at `Focus-app/…/ReceiptSheet.kt:176`:
   `"THE MACHINE KEEPS ITS OWN LOG · YOUR STUDY RECORD LIVES IN YPT"`.
   An unqualified PASS invites "the footer is cured" as a summary of the
   world; it is cured in one unbuilt tree (B-2) and false in the shipped
   one.
2. **The summary "5 PASS, 3 FAIL"** will be quoted without its scope
   (grep/read of one tree, nothing executed). Claim 5's own verdict shows
   why scope matters: the substance held and the claim *as written* was
   false (the derive call lives in `PillController.kt:122`, not
   `MonolithShieldService.kt`). Every PASS in that report is a statement
   about text, not behaviour — the summary must carry that or it becomes
   the next overclaim's source.

*Postscript on claim 8:* the FAIL's residue (hardcoded `"PEEK: 2 MINUTES,
THEN SHUT AGAIN"` at ParkDrawer PLATE_BODY) has since been cured on the
composed tree (commit d8cb528; the slot is now the bare `"PEEK:"` token).
Noted so nobody re-files it — and so nobody calls the cure verified: it
rides the same unverdicted tree as everything in B-2.

---

**Charge count: 9 (B-1 … B-9). Worst: B-1** — a falsified sentence, struck
by name in W-11, still printing in a lane this epic lands, with tests
written to defend it.

— Brand (brutal critic, #15 second pass — claims)
