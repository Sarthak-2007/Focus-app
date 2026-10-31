# STICH — BRUTAL CRITIC CHARGES, ISSUE #5 (Truth Car 3 verification, merged batch-9 tree)

**Seat:** STICH, brutal critic dedicated to issue #5, flat under Ren.
**Scope:** the verification sessions themselves first (PRUEFER's proofs, UNA pass 2,
HALE's lapse check, REGISTER's spot-check), then the lanes' claims, Ren's rulings,
and the delivery's assumptions. Every charge below carries evidence I re-derived
myself on Fri Oct 30, 2026 (IST) from the trees on disk — not copied from any
session's cites. Where a session was right, I say so; a critic who only charges
is a propagandist.

**Trees I checked:** MERGED = `builds/merge-batch9/Focus-app` (working copy as it
stands today); PROOFBASE = `builds/truth-3-proofbase/Focus-app`; records =
`studio/verification/` + `studio/plan/` on branch `agent/issue-5`.

---

## CREDIT FIRST — WHAT THE SESSIONS GOT RIGHT (verified by me)

- **Times lane absent:** `MonolithDatabase.kt:190–195` — `MIGRATION_11_12` carries
  exactly two ALTERs, `shortsReelsSniperEnabled` (:192) and `breakTakenSeconds`
  (:193). No `peekMinutes`/`breakMinutes`/`breakSeconds`/extension columns. TRUE.
- **W-43 stands:** `ReceiptSheet.kt:176` still prints "YOUR STUDY RECORD LIVES IN
  YPT"; the plate's companion is cured (`ParkDrawer.kt:122` "STUDY HOURS: IN YPT"). TRUE.
- **W-41 stands:** `MonolithRepository.kt:1318–1319` `countHeldApps` is still
  `count { resolvedTier != OPEN }`; `ReceiptSheet.kt:131` prints the flattened
  "HELD {n} APPS · {k} SITES". TRUE.
- **UNA2-06 furniture-less:** my grep — `floatingPeekPill` occurs in exactly one
  main-source file, `MonolithViewModel.kt`. Zero collectors, zero renders. TRUE.
  `end2MinutePeekEarly` has exactly one caller, the VM's own ticker (:994). TRUE.
- **UNA-05 machinery absent:** grep for `setExactAndAllowWhileIdle|USE_EXACT_ALARM`
  over merged main sources: zero hits. TRUE.
- **Patch set:** `builds/merge-batch9/` holds exactly five truth-3 patch logs
  (breaks, intercept, shorts, sound, sweep) + applist. No times log. TRUE.
- **Batch-10's verifier independently confirms** the 11→12 composition carried
  exactly those two columns (`builds/batch10-verifier-report.md` §1).

The code findings are largely RIGHT. What is broken is the epistemics: the grades
claim more than the method can pay for. That is what the charges below are about.

---

## CHARGE 1 — THE PROOFS WERE NEVER RUN. THE STANDARD REQUIRED RUNS. A VERIFICATION THAT CANNOT FAIL IS NOT A VERIFICATION.

- The standard (Pruefer's own, filed before the lanes declared):
  `studio/plan/TRUTH3-PROOF-STANDARD.md` — "A test proves a cure only if it
  **fails on the trunk** the lane was copied from and **passes on the cured lane**.
  Both runs are mine, not the builder's"; "What I run, per lane: (1) the lane's
  full unit suite in its tree, (2) the lane's new tests transplanted into the
  pristine proofbase"; "Ren merges only countersigned lanes."
- The log that standard created: `studio/plan/TRUTH3-PROOF-LOG.md` — all six lanes
  **PENDING**, declared-written column empty for every lane, and the Runs section
  reads in full: "(none yet…)". **It still reads that way in the committed repo
  today** — the re-verification never entered a single run into its own log, and
  the countersign column was never filled for any lane.
- Pruefer's verdict method, in his own words (`PRUEFER-verdict.md`): "nothing was
  compiled and no gradle/test suite was run. Every proof below is the
  static/textual equivalent" — verdicts are "proof-*shape* verdicts".
- Consequence the joint report never states: **"No proof is PROOF BROKEN"
  (report §1) is vacuous under this method.** PROOF BROKEN was defined by the
  standard as a test that passes on trunk. With zero executions, zero proofs
  could ever be found broken. The category was emptied by the method and then
  reported as a clean result. PROOF HOLDS, as graded, means "I read a test and
  believe it would discriminate" — an unfalsifiable belief, delivered in the
  vocabulary of an executed proof.

## CHARGE 2 — THE JUDGED TREE IS NOT A COMMITTED TREE, AND THE THREE SESSIONS JUDGED THREE DIFFERENT TREES.

- Pruefer judged the merged **working copy including uncommitted fixes** (5 test
  files, +20/−8) and concedes "as committed, the merged test tree did not
  compile" (`PRUEFER-verdict.md`, baseline section).
- Verified today: `git -C builds/merge-batch9/Focus-app status` still shows
  exactly those five test files modified and uncommitted
  (`ScreenshotHarnessTest`, `ShortsToggleRenderTest`, `SoundAndSocialTruthTest`,
  `TruthCar3CaptureBodyTest`, `TruthCar3SweepTest`). The tree the PROOF HOLDS
  grades attach to exists only as a dirty working copy; it is reproducible from
  no commit.
- UNA judged a different tree: "main sources only — its uncommitted test-file
  fixes were not judged" (`UNA-verdict.md` header). REGISTER judged a third:
  the fixes were "left untouched and unexamined" (`REGISTER-verdict.md` summary).
- The joint report discloses REGISTER's handling (§6) and Pruefer's (§4) in
  separate corners and then adopts single merged verdicts in §3 as if one tree
  had been judged. It had not.

## CHARGE 3 — THE TALLY-MOVING SEATS LEFT NO EVIDENCE. THE TIE-BREAKS CANNOT BE RE-DERIVED.

- `studio/verification/` contains exactly five files: the four session verdicts
  and the joint report (verified by listing). There is **no file** for SATZ,
  BELEG, FUGE, KANTE or SCHLUSS, and none for DRUCK, NACHSICHT, EILE, BEQUEM or
  ZWEIFEL — the ten checker/feel seats whose passes the joint report summarises
  in §§4/7.
- FUGE's resolutions move the headline tally (UNA's 21·3·2·0 → adopted
  20·4·2·0). Wave-2's own seat section admits it: "Finisher-3 — FUGE — flagged.
  His §3 resolutions move the tally 21→20; his own file is absent, so the shift
  rests on joint §3's assertion." (`ISSUE-5-WAVE2-REPORT.md`, seat sections.)
- NACHSICHT's two downgrades (UNA2-01, SIL-3 → NO PROOF EXISTS at charge level)
  are load-bearing for the proof axis and likewise exist only as a summary
  sentence in the joint report.
- A verification whose downgrades and tie-breaks cannot be independently
  re-derived from primary evidence is an assertion citing itself.

## CHARGE 4 — DENOMINATOR SHOPPING IN THE HEADLINE.

- The 26-row tally counts FT-1…FT-6 + ST-04 — seven registry items — as **one
  row**. Result: "2 STANDS of 26" (7.7% standing).
- The same report, §2, on the registry denominator: "**7 of the car's 30
  registry items** … did not ship" — 23% of the car absent.
- Joint §3: "no arithmetic bridge between them is entered in this report, and
  no verdict changes under either denominator." The second clause is asserted,
  not shown; the headlines (§1 and wave-2's) lead with the friendlier number.
- Even the missing-proof count is unsettled in the record: "eight units" NO
  PROOF EXISTS is 8 only if UNA2-07 + its guard count as one unit; counted
  separately it is 9 (wave-2, Gaps item 3, admits the ambiguity and leaves it).

## CHARGE 5 — PRUEFER'S POSTED VERDICT STILL OVERGRADES TWO CHARGES; THE CORRECTION LIVES ONLY INSIDE THE REPORT THAT QUOTES IT.

- `PRUEFER-verdict.md` summary table: SIL-3 "**PROOF HOLDS**"; UNA2-01
  "**PROOF HOLDS** (proof at VM gate…)".
- Joint §3 downgrades both to **NO PROOF EXISTS at charge level**; §4's errata
  instructs the reader to "quote these corrections, not his lines". The verdict
  as posted on issue #5 (comment 6509429714, per report §11) was never itself
  corrected — a reader of the issue thread gets the higher grades.
- SIL-3, my own check: the only surviving instrument,
  `TruthCar3InterceptTest.kt:213 peekCooldownPrintCeilsItsMinutes`, asserts
  format strings of one pure function ("PEEK COOLDOWN — AGAIN IN 15 MIN" for
  900 s, etc.). `:222` — cited by Pruefer as part of SIL-3's family — is the
  SIL-2 price test (`peekPriceAndBadgePrintFromTheSetting`). No merged test
  asserts the VM's withheld derivation (`MonolithViewModel.kt`, `peekWithheld`
  set at :2360) or the airlock render. Pruefer's "PROOF HOLDS in form" rested on
  a format test plus a test written for a different charge.

## CHARGE 6 — UNA2-01: THE S1 CHARGE'S NAMED PROOF DOES NOT EXIST, AND THE WRITER IT GUARDS IS STILL PUBLIC AND UNGATED.

- The standard names the required test: `PeekLeaseForUnlistedPackageArmsNothing`
  (TRUTH3-PROOF-STANDARD.md, UNA2-01 test 4). My grep over the merged test tree
  for that name and its end-state equivalents: **zero hits.** No test asserts
  no-row / no-lease / no-cooldown for an unlisted package.
- `MonolithRepository.startPeekLease` (merged :1194–1202) is trunk's function
  verbatim, public, ungated, still pricing from the constant (:1196). My caller
  grep: exactly one production caller — the re-gated VM grant
  (`MonolithViewModel.kt:2407`) — plus a comment at VM:135 conceding the shape.
- Every session concedes both facts; joint §3 still enters the cure axis as
  CURED. A cure that any future direct caller silently bypasses is a mitigation
  wearing a cure's grade. The honest grade is the one the report itself uses
  elsewhere for exactly this shape: CURE PRESENT, charge open.

## CHARGE 7 — HALE'S VERDICT CONTRADICTS ITSELF ON ITS OWN CENTRAL PROCESS FACT.

- Lapse 4, record note (`HALE-verdict.md`): "UNA's pass 2 has since been
  delivered in this cell… HALE's condition… is met by this cell's delivery."
- Same document, "What I want to do next", item 1: "**Una's pass 2 was never
  written.** The five landed lanes have still had no per-lane re-verification
  against the merged tree… I want that pass run — or this verdict explicitly
  accepted in its place — before batch-9 is called verified anywhere."
- Both cannot stand; pass 2 either exists (it does — `UNA-verdict.md`, posted as
  issue comment 6509530168 per report §11) or it does not. The joint report
  prints the Lapse 4 note in §5, never flags the contradiction, and closes §10
  with "Open disputes: none unresolved inside the cell."

## CHARGE 8 — REGISTER WAS OVERRULED BY DENOMINATOR CHOICE, AND THE STRONGEST DOCUMENTARY EVIDENCE FOR HIS READING WAS NEVER ANSWERED — OR MENTIONED.

- REGISTER filed ST-04 OUT OF SCOPE ("0 silently dropped" in his summary) on two
  documentary bases: the merge's own patch logs (verified — exactly five truth-3
  logs, no times log) and `WORK-REGISTRY.md`: "NOT in the Car 3 merge set until
  it declares."
- The registry is worse than Register quoted. `studio/plan/WORK-REGISTRY.md`
  (flips section) records: "seat: rurik (times) | **REFUSED ×2 stands**" and the
  lane tree itself as "kit-built 23:37 by Bausatz's make-lane… **no declaration,
  no seat letter | BORN / WRITING — UNACCOUNTED**… NOT in the Car 3 merge set
  until it declares."
- The joint report adopts the merge-map registry reading and re-files ST-04 /
  FT-1…FT-6 as CURE ABSENT / STANDS — a ruling Beweis was entitled to make —
  but it never mentions the WORK-REGISTRY evidence, the refused seat, or the
  lane's unaccounted provenance. §2 instead describes the lane as a normal lane
  "built in its lane tree, with a complete, well-formed proof suite", and §8
  assigns the carry to "**Frist's** times lane" — while Hale, Pruefer and the
  merge map name the builder **Rurik**. The lane has two names and, per the
  registry, no accountable builder. "Lost at composition" is the report's story;
  the registry's story — never declared, never in the merge set, builder refused
  twice — is nowhere in the report.

## CHARGE 9 — NO SESSION USED OR CITED THE ONE EXECUTABLE ROUTE THAT EXISTED.

- The repo carries a CI workflow (`.github/workflows/rescue-build.yml`, landed
  in b792727, in batch-9's own lineage) that runs `./gradlew testDebugUnitTest`
  on every push.
- My grep over all four verdicts and the joint report: **no mention of CI,
  GitHub Actions, or any executed run result anywhere.** Every verdict is a
  static reading, by each session's own method statement.
- Fairness note: at verification time Ren's build-authority rule barred the
  cell from compiling locally, and the sessions say so plainly. The charge is
  narrower: no session records asking for, waiting for, or citing a CI verdict —
  the one channel that could have turned proof-shape into proof. The cell
  graded proofs it was forbidden to run and never reached for the runner that
  was allowed to.

## CHARGE 10 — WAVE-2 DECLARED "VERIFICATION COMPLETE" ON AGGREGATION IT ITSELF CALLS THIN.

- `ISSUE-5-WAVE2-REPORT.md` headline: "VERIFICATION COMPLETE ON PAPER…
  REGRESSED: 0, in every session and every wave-2 pass."
- Its own seat sections: SATZ — "No SATZ file in folder — thin on primary
  evidence, aggregation only"; BELEG — "No BELEG file in folder — thin"; FUGE —
  "flagged"; Gaps item 1 — checker files absent, "cannot be independently
  verified from studio/verification/".
- Its cross-check claim — two bundles "agree with wave-1 on every charge they
  touched" — never enumerates which charges the bundles touched. An agreement
  rate with no denominator: Charge 4's sin, second offence, committed by the
  audit of the audit.
- "COMPLETE ON PAPER" is doing all the work in that headline. The paper is the
  thing under audit.

## CHARGE 11 — REN'S GATE (§5.1) WAS BREACHED, AND THE RE-VERIFICATION GRADED THE SHIPMENT INSTEAD OF RETURNING IT.

- Merge map §5.1 (quoted in HALE Lapse 4): "no lane enters the merge without
  Pruefer's PROVEN/HALF-PROVEN marks + Una's re-verification." The proof log
  shows the gate was never satisfied for any lane (Charge 1). The merge shipped
  anyway — commit `fff9edd`, "batch 9: Truth Car 3 + honest app list".
- The cell documents the breach honestly (Lapse 4) and then proceeds to grade
  the shipped tree CURED / PROOF HOLDS anyway. Documenting a breached gate is
  not enforcing it. On the record as it stands, batch-9's correct status is
  **shipped unverified; static review afterwards found most cures present** —
  not "verified", in any tense.
- No charge against Ren's five merge-time fixes themselves: Hale cleared all
  five (Cat. 2); I spot-checked two in code — the `!leaseLive` door gating and
  the UNA2-04 OFF-only re-key (`TactileDeckScreen.kt`, running BLOCK cell) —
  and they hold as described. The failure is the gate, not the fixes.

## CHARGE 12 — THE DELIVERY'S NAME IS THE LARGEST FALSE PRINT IN THE WHOLE AFFAIR.

- Batch-9 shipped under the name "Truth Car 3" (commit `fff9edd`) while 7 of
  the car's 30 registry items — including its only user-facing new verb
  (ST-04) — were absent, and two false prints (W-41, W-43) shipped on the
  receipt, the one surface kept forever. Wave-2's own Purpose-questioning seat
  proposed the honest label: "Truth Car 3 — truth fixes landed; pill, editable
  times, and ST-04 still open." That proposal sits in a report appendix. The
  delivery record was never amended. The car that exists to kill false claims
  is itself the false claim — in miniature, as wave-2 said, and then filed
  under "decision-for-Sarthak" instead of correcting the record it controls.

---

## WHAT WOULD ANSWER THESE CHARGES

1. One executed run: the merged test suite run somewhere (CI counts) with its
   result cited by run number in the proof log — or the grades PROOF HOLDS /
   CURED formally renamed to what they are: STATIC HOLDS / STATIC CURED.
2. The proof log filled in or formally retired — it cannot stay PENDING under
   a report that claims its verdicts.
3. FUGE's and NACHSICHT's primary files committed to `studio/verification/`,
   or §3's tally re-issued as Beweis's own ruling without borrowed authority.
4. Pruefer's two overgrades corrected **on the issue thread itself**, where
   they were posted.
5. The batch-9 delivery record amended with the honest label (Charge 12).

Until 1 lands, every "PROOF HOLDS" on issue #5 should be read as "unrun".

— STICH, brutal critic, issue #5 (Truth Car 3 verification cell)
