# METHODS APPLICATION LEDGER — were the Methods proposals actually applied?

**Waage, Methods (measurement) — working directly under Ren, flat studio.**
Filed Oct 29, 2026, ~23:50 IST, on Sarthak's order of 23:16 IST
("Run methods and keepers of record"). Evidence rule, stated once and
held throughout: **a proposal is APPLIED only where a file, setting, or
behaviour on disk tonight proves it. A proposal quoted, praised, or
ruled in a letter but with no on-disk proof of application is NOT
applied.** No Gradle was run for this ledger (compile-once regime);
`tools/queue.sh` and all app code are untouched.

---

## 1 · THE COUNT — there is no 36

**No list, ledger, or count of "36 Methods proposals" exists on disk.**
Sarthak's "36 proposals" (Oct 29, 23:16 IST) does not match any
Methods artefact I can find. Searches run, all over
`design-refs/swarm/**/*.md` plus `tools/` and the trunk build files:

- `grep -rn "36 proposals" / "36 Methods" / "thirty-six"` — **0 hits.**
- Full read of the only Sweep-1 proposal file,
  `plan/PROCESS-PROPOSALS-1.md` (345 lines, every heading and ID
  extracted mechanically).
- Greps for `Methods` / `proposal` citations in the three plan books
  that cite Methods — `plan/WORK-QUEUE.md`, `plan/RULINGS.md`,
  `plan/MASTER-PLAN.md` — plus `plan/PIPELINE.md` and
  `plan/WASTE-LEDGER.md`.
- Every `\b36\b` hit in Methods scope is something else: evidence
  density **36/60** (checker-v2), **36/36** build files identical
  across lanes (dependency-audit), sdk **36**, **360 s** runs, the
  wheel's **36°** dead sector. None is a proposal count.

**The real numbers, for the record:**

| Count | Value | Source |
|---|---|---|
| Sweep-1 proposals, as claimed by their own file | **39** | `plan/PROCESS-PROPOSALS-1.md` L13: "39 proposals from 8 seats"; repeated at `plan/PIPELINE.md` L128 |
| Sweep-1 proposals, distinct IDs actually extractable | **38** | Mechanical extraction: Fernrohr 5, Fundus 5, Gitta 5, Massarbeit 5, Probe 5, Restwert 4, Sicht 5, Waage 4 = **38**. **The claimed 39th proposal is named nowhere — the source file's own coverage note is off by one, and PIPELINE repeats the error.** |
| Sweep-1 ranked entries | 31 | 7 proposals are folded as convergences into 4 ranked entries (rank 3: +1, rank 4: +3, rank 5: +1, rank 25: +2), so 38 distinct proposals present as 31 rows in the ranking |
| Sweep-2 + follow-on studies in `methods/` | **29** files | `methods/*.md`, listed in §4 of this ledger |
| **Total rows in this ledger** | **67** | 38 Sweep-1 + 29 Sweep-2 |

If Sarthak's 36 came from anywhere, the nearest real candidates are
the 38 distinct Sweep-1 IDs (misremembered), the 31 ranked entries,
or the 39 the file claims. None of them is 36, and I will not
manufacture one.

## 2 · THE TOTALS — applied vs not, tonight

| State | Sweep 1 (38) | Sweep 2 (29) | Total (67) |
|---|---|---|---|
| **APPLIED** — on-disk proof tonight | 6 | 16 | **22** |
| **PARTIAL** — part landed, part didn't | 12 | 12 | **24** |
| **QUEUED** — unruled or waiting at a named place | 19 | 1 | **20** |
| **DEAD** — superseded/killed | 1 | 0 | **1** |

**The headline in one sentence:** of everything Methods proposed,
only the top-5 Sweep-1 rulings (as PIPELINE amendments A1–A5) and the
Sweep-2 *tooling* studies were systematically applied; the other 19
Sweep-1 proposals were never ruled on at all and wait, unnamed by any
queue, inside `plan/PROCESS-PROPOSALS-1.md` — and several "applied"
items are applied as *law or paper* while the mechanism that was
actually proposed (a generated REGISTER, a merge log that gets
written, delivery records that get opened) never happened.

---

## 3 · SWEEP 1 — the 38 proposals (plan/PROCESS-PROPOSALS-1.md)

Ruled proposals are A1–A5 (`plan/PIPELINE.md` L128–158, Ren's rulings
~09:45 IST on Takt's top 5). Everything ranked 6–31 was **never ruled**
— that is itself the largest finding in this section.

| # | Proposal | State | Evidence / what didn't land |
|---|---|---|---|
| 1 | P-PROBE-4 — tiered train proof (rank 1) | **APPLIED** | Ruled as amendment A1 (PIPELINE L131). Working form on disk: `methods/test-cost.md` §2 tier table T0/T1/T2 with copy-paste filter commands, priced saving 7–10 min/train; `tools/merge-prep.sh` groups packets by A1 scope; `merge-review/merge-log.md` records the token car as "A1: FULL". A1's precondition (time one isolated suite run) was met by the manual solo run, 230 s wall (`methods/memory-budget.md` §10). Limit: the harness cannot be filtered per screen — Probe's per-destination split (§2.4 of test-cost) is unbuilt, so per-car proof is tier-shaped, not screen-shaped. |
| 2 | P-FERNROHR-3 — one interrupt queue for Sarthak (rank 2) | **APPLIED** | Ruled as A2. `plan/INTERRUPTS.md` exists and is *used*: 1 entry in Closed (U1/U2, answered Oct 29 20:09 by his tap, written back on the same lines) and 1 live in Queued (Q6, the slide's two-note sound, with options + default + `plan/Q6-BRIEF.md`). Tooling: `tools/question-gate.sh` (heuristic gate for draft batches). |
| 3 | P-MASSARBEIT-3 — REGISTER as validated token file (rank 3) | **PARTIAL** | Ruled as A3, staged. Landed: `tools/constants.json` v1 — the charged set only, header citing this proposal + P-FUNDUS-4, and `studio_check.py` checks charged slide values against REGISTER's slide section. Did NOT land: the proposed `plan/register.tokens.json` (no such file), REGISTER.md as a *generated* view (REGISTER is still hand-edited; constants.json governs by declaration, not generation), generated Kotlin constants (builders still transcribe), the stage-3 literal validator as specified. |
| 4 | P-FUNDUS-4 — constants.yaml, converged into rank 3 | **PARTIAL** | Same landing as row 3: credited in `tools/constants.json`'s `ruled_by` line; its YAML form was not adopted (Takt ruled Massarbeit's DTCG form the target) and the DTCG form itself exists only as the small hand-kept constants.json. |
| 5 | P-FUNDUS-2 — docs-as-tests settlement lint (rank 4) | **APPLIED** | Ruled as A4, built as ONE checker: `tools/studio_check.py`, now v2 (changelog in its header cites A3+A4), full run on record at `methods/studio-check-v2-run.txt`, exit-1-on-FAIL semantics, dispositions in `methods/checker-v2.md`. |
| 6 | P-FERNROHR-5 — guardrail checks at handoffs, converged into rank 4 | **APPLIED** | Its four named checks (REGISTER-CORRECTION, SHIPPED, BUILD-READY, FINDING) *are* A4's checker gates in `tools/studio_check.py` — shipped-gate entry-scoped in v2, forks gate, code-site gate, dead-literal gate. |
| 7 | P-SICHT-3 — Danger-style one script, three severities, converged into rank 4 | **APPLIED** | As converged: one script (`studio_check.py`), severity classes FAIL / WARN / INFO in v2's dead-literal classification (checker-v2 §: 40 v1 WARNs resolve to 3 FAIL + 2 WARN + 36 INFO). No separate Danger tooling exists — by Takt's ruling, correctly. |
| 8 | P-RESTWERT-4 — poka-yoke one-value checker, converged into rank 4 | **APPLIED** | As converged: the dead-literal / one-value checks are studio_check v2's core (context-window classification; governing-position hits FAIL). |
| 9 | P-SICHT-1 — Required (S1-only) vs Advisory review (rank 5) | **PARTIAL** | Ruled as A5 (PIPELINE L152) — the law landed. The mechanism did not demonstrably fire: `methods/review-flow.md` §2 audited the day and found advisory findings adopted WHOLE as binding in 7 minutes with no severity ever assigned (Finishers pass 1), i.e. A5's central distinction collapsed in its first live loop. |
| 10 | P-SICHT-4 — lazy-consensus deadlines, with rank 5 | **PARTIAL** | In A5's text. In practice: review-flow §2 — **every** lazy-consensus deadline of the day passed unmarked; 0 verifiable firings against ~44 advisory charges in LIE-REGISTER-3 alone. Repair adopted later: the CLOSES: stamp (Sicht F-1) in `tools/templates/README.md` amendments. |
| 11 | P-SICHT-2 — Nit: / approve-if-it-improves (rank 17, rider to rank 5) | **PARTIAL** | Takt's Ruling 5 adopts its prefixes (BLOCKER / Nit: / Question:) as house style, and severity-at-birth landed in the templates amendments (Sicht F-5). No census of findings letters actually using the prefixes exists; adoption is template-level, not proven practice. |
| 12 | P-WAAGE-1 — Talaiot per-build timing ledger (rank 6) | **QUEUED** | Never ruled. No Talaiot anywhere in trunk build files (grep: 0 hits), no `build-timings.jsonl`, no `swarm/metrics/`. Timing knowledge tonight is Waage's log archaeology + Takt's one-off merge timing — by hand, not by this proposal's instrument. Waits in: `plan/PROCESS-PROPOSALS-1.md` rank 6, unruled. |
| 13 | P-PROBE-2 — Robolectric diet (rank 7) | **PARTIAL** | Ratified *on paper*: `methods/memory-budget.md` §6 ratifies forkEvery=0 stated explicitly, maxParallelForks=1 standing / 2 solo-only, maxHeapSize="1g" at forks=2. In code: **nothing** — trunk `app/build.gradle.kts` sets none of these (grep-verified tonight), there is no shared `robolectric.properties`, and logic tests (ledger/week/migration classes) still run on `RobolectricTestRunner`. Two of the three caps coincide with Gradle defaults, which is not application. |
| 14 | P-FERNROHR-1 — a Refinery seat owns the merge queue (rank 8) | **QUEUED** | Never ruled, never seated. Ren is still the sole integrator tonight: `methods/merge-pipeline-timing.md` (23:20 IST) prices *Ren's* merge of Truth Car 3. The rehearsal merge mechanises labour *around* him — it is not this proposal. Waits in: PROCESS-PROPOSALS-1 rank 8. |
| 15 | P-GITTA-1 — git-spice tracked stack (rank 9) | **QUEUED** | Never ruled, not installed/used on this tree. Gitta's applied Sweep-2 route was the opposite shape — file manifests (`tools/lane-base.sh`, sha256) with folder-copy lanes. Waits in: rank 9. |
| 16 | P-SICHT-5 — Bors/GitHub merge queue (rank 10) | **QUEUED** | Never ruled. No queue script drives merges; Law 5 trains are hand-merged (merge-pipeline-timing is tonight's receipt). Re-proposed in adapted form as swarm-research T2 — also unapplied (see §4 row 25). Waits in: rank 10. |
| 17 | P-RESTWERT-2 — andon cord for unlanded settlements (rank 11) | **QUEUED** | Never ruled. No cord pull is on record anywhere; unlanded settlements were instead caught by audits and the kit false-closure sweep (a cousin, §4 row 15) a day-part later — exactly the delay this proposal existed to kill. Waits in: rank 11. |
| 18 | P-FUNDUS-1 — immutable ADR ruling records + generated CURRENT.md (rank 12) | **QUEUED** | Never ruled. No `CURRENT.md` exists; `plan/RULINGS.md` is still one hand-edited file where superseded values sit beside replacements (its own Contradictions section, C4, is the standing example). Waits in: rank 12. |
| 19 | P-MASSARBEIT-1 — the Clarify Gate (rank 13) | **PARTIAL** | The stage-3 zero-open-forks law pre-exists (PIPELINE stage 3) and the shape landed: `tools/templates/build-ready.md` §6 "FORKS — ZERO OPEN" + studio_check's forks gate. The proposal's actual machinery did not: no mandatory FORKS table with priced options + decider + citation per file, no CLARIFY LOG, no `NEEDS CLARIFICATION\|TBD\|OPEN` grep gate. Proof it does not block: SLICE-E-BUILD-READY filed complete with **19 open forks** in its §12 (review-flow §1; plan/debates/2026-10-29-slice-e-unruled.md). |
| 20 | P-MASSARBEIT-5 — delta-only build-ready files (rank 14) | **QUEUED** | Never ruled. Build-ready files on disk tonight (e.g. ROUTINES-REBUILD-READY, SLICE-E-BUILD-READY) are full restatements, not ADDED/MODIFIED/REMOVED/CITED deltas. Waits in: rank 14. |
| 21 | P-WAAGE-3 — delivered-vs-built counter, DORA shrunk (rank 15) | **QUEUED** | Never ruled. No `swarm/metrics/METRICS.md`, no metrics dir, no standing counter. The SHIPPED-word discipline improved by other means (A4's shipped gate, Hale's W-11 language), but the four numbers were never computed once. Waits in: rank 15. **Not** Stellwerk's item 15 — a different 15; do not confuse them. |
| 22 | P-FERNROHR-2 — effort-scaling caps + four-field delegation contract (rank 16) | **PARTIAL** | Half landed by shape: `tools/templates/spawn-brief.md` carries a Boundaries section and the studio's briefs carry objectives by habit (Law 10's NEXT is the other half of the culture). Not landed: complexity classes (A/B/C), seat caps, the cap-stops-the-wave rule — no trace in PIPELINE or the template. |
| 23 | P-RESTWERT-1 — WIP cap on unbuilt paper (rank 18) | **DEAD** | Never ruled, never applied — and superseded in practice by Sarthak's own later orders the same day (parallel execution by default, "everything gets worked parallel", Oct 29 22:10–22:12 IST, MEMORY.md): the studio deliberately runs the opposite policy. No ruling names this proposal; the kill is by contrary standing order, recorded here so nobody revives it as pending. |
| 24 | P-PROBE-3 — Roborazzi golden-verify (rank 19) | **QUEUED** | Never ruled. The harness still captures with hand-rolled `decorView.draw(Canvas)` and no golden comparison (test-cost §1/§4, source-read). Waits in: rank 19. |
| 25 | P-MASSARBEIT-2 — EARS-only behaviour lines (rank 20) | **QUEUED** | Never ruled. No build-ready template section, no R-line numbering on disk. Waits in: rank 20. |
| 26 | P-MASSARBEIT-4 — exhaustive state×event table (rank 21) | **QUEUED** | Never ruled. No statechart table in the build-ready template or in the slide/seal build-ready file it was priced for. Waits in: rank 21. |
| 27 | P-FUNDUS-5 — SETTLED.md front door + local search (rank 22) | **PARTIAL** | SETTLED.md was never built and no local search (qmd or other) is installed. Its function was partially delivered by a different artefact: `methods/read-first-map.md` — the governing line for the 15 most-asked question classes, every entry verified by opening the file. A map for 15 classes is not the proposed ~100-line pointer file over all settled law. |
| 28 | P-FUNDUS-3 — books as an LLM wiki, Ingest/Query/Lint (rank 23) | **QUEUED** | Never ruled. The Keepers' books (WORK-REGISTRY, OPEN-LOOPS) are run as records, not as a wiki with named Ingest/Query/Lint operations. Waits in: rank 23. |
| 29 | P-FERNROHR-4 — MetaGPT schema'd artefacts, seats pull their stage only (rank 24) | **QUEUED** | Never ruled. No schema pass exists; seats still receive whole files by brief. Waits in: rank 24. |
| 30 | P-WAAGE-2 — stage-duration spans for the 8 pipeline stages (rank 25) | **PARTIAL** | No harvester script, no spans. One manual instance exists: `methods/merge-pipeline-timing.md` timed tonight's car end-to-end by hand (spawn records + mtimes + diffs). A one-off study is the proposal done once by a person, not the standing instrument proposed. |
| 31 | P-RESTWERT-3 — value-stream board, converged into rank 25 | **PARTIAL** | Same landing as row 30: WASTE-LEDGER carries value-stream-flavoured entries (it cites this ID), but no board is generated or standing. |
| 32 | P-WAAGE-4 — merge-queue analytics, converged into rank 25 | **PARTIAL** | Same landing as row 30: merge waits were measured once by hand (`methods/queue-waits.md`, 96 logs) and merge time once (`merge-pipeline-timing.md`); no analytics recur. |
| 33 | P-PROBE-1 — Paparazzi pilot (rank 26) | **QUEUED** | Never ruled. No Paparazzi dependency or pilot on disk; static renders still ride Robolectric (test-cost §4). Waits in: rank 26. |
| 34 | P-PROBE-5 — one managed-device smoke per ship batch (rank 27) | **QUEUED** | Never ruled. No managed-device configuration in trunk build files, no smoke run on record. Waits in: rank 27. |
| 35 | P-GITTA-3 — git-branchless pre-flight (rank 28) | **QUEUED** | Never ruled, not used. Pre-flight tonight is the rehearsal merge (a Methods invention of Oct 29 23:31, §4 row 29) — a different mechanism reaching for the same pain. Waits in: rank 28. |
| 36 | P-GITTA-4 — GitHub native stacked PRs, `gh stack` (rank 29) | **QUEUED** | Never ruled. The repo's trains never ran as PR stacks; lanes are folder copies merged by hand. Waits in: rank 29. *(Ledger row 36 — the only "36" this file can honestly offer.)* |
| 37 | P-GITTA-2 — Jujutsu (jj) lanes over Git trunk (rank 30) | **QUEUED** | Never ruled, no jj on this tree. Waits in: rank 30. |
| 38 | P-GITTA-5 — Sapling `sl absorb` for stage-6 fixups (rank 31) | **QUEUED** | Never ruled, no Sapling on this tree. Waits in: rank 31. |

**Sweep-1 subtotal: 6 APPLIED · 12 PARTIAL · 19 QUEUED · 1 DEAD.**
Pattern worth naming: everything Ren *ruled* (the top 5) got built or
half-built within the day; everything he didn't rule got nothing —
no seat, no queue entry, no second mention. The 19 QUEUED proposals
do not wait anywhere a finisher would ever pull them from; they wait
only inside the proposals file itself.

---

## 4 · SWEEP 2 + FOLLOW-ONS — the 29 studies in methods/

These were mostly commissioned as *tools and measurements*, not
proposals awaiting ruling — which is why their application rate is
higher. Where a study's own recommendations outran its deliverable,
the row says so.

| # | Study (methods/) | State | Evidence / what didn't land |
|---|---|---|---|
| 1 | waage-baseline.md (+ run-baseline.sh, baseline-runs/) | **PARTIAL** | BEFORE table filed (113 logs, 98 runs, 27% failure rate, assemble median 101 s — the numbers BUILD-CHANGES 11:50 cites). The instrument exists and was upgraded (PID-keyed sampler). But the official solo run has **never fired**: `baseline-runs/baseline-runs.csv`'s only row is STRUCK (instrument failure — `/usr/bin/time` missing, Gradle never ran), and §10 of memory-budget.md still lists it fire-ready with preconditions P1–P5. Ren approved the solo seating at the wave's landing (mail/waage 2112) — it waits for a quiet box, not for permission. |
| 2 | test-cost.md (Probe) | **APPLIED** | The A1 tier table (T0 0.03 s / T1 25.9 s / T2 40.8 s / FULL 66.8 s) is the studio's standing test protocol — cited as LANDED in BUILD-CHANGES 11:50, with copy-paste filter commands in §2.3. Inside it, two inventions remain unbuilt and are counted here, not hidden: the per-destination harness split (§2.4, ~1 h, app code) and the `fastTest` Gradle alias. |
| 3 | queue-waits.md (Stellwerk, WORK-QUEUE item 7) | **APPLIED** | Filed ~10:30 IST (96 logs, 92 exact intervals; the 10:13–10:19 triple-overlap breach reconstructed to the second) and **closed DONE in WORK-QUEUE's Pulled list**. Its findings are the evidence base queue.sh v2 was built on. |
| 4 | queue-claim-fix.md (Stellwerk, WORK-QUEUE item 15) | **QUEUED** | Built as a candidate only — by the item's own constraint. `methods/queue.sh.v2.1-candidate` is proven against copies in `methods/queue-sim/` (sims A–D2, v2.0→v2.1 diff, 160 lines): write-then-rename claim, vanish guard, reaper notes + litter, truthful status, the `[dry-run]` false print fixed. **The live `tools/queue.sh` is untouched** (verified tonight — still v2.0 header). Waits at: WORK-QUEUE item 15 + §4 swap procedure in the study — needs a no-live-lanes window and Ren's swap ruling. This ledger did not touch it. |
| 5 | memory-budget.md (Waage, WORK-QUEUE item 14) | **APPLIED** | Page delivered and closed DONE as queue item 14 (per-process budgets B1–B6, lawful-state totals, Probe's spec answered §6). It is a *working* page tonight: Vorrat's checks (row 6) run against its B-rows live. One internal debt closed tonight by Waage — see §5 of this ledger (the B1 reprice). B3/B4/B6 still stand DERIVED/PROVISIONAL pending row 1's solo run. |
| 6 | memory-budget-page.md (Vorrat, WORK-QUEUE item 8) | **APPLIED** | Filed tonight 23:18 IST — the checkable half item 14 never claimed: a command + reading + threshold per process class, a verdict ladder, and a **worked audit run against tonight's live box** (§4: caught a lawful two-flag state sitting at 557–639 MB available — BREACH on headroom with zero processes at fault). Bookkeeping lag, named in its §6.2: WORK-QUEUE item 8 still stands open on disk while OPEN-LOOPS L25 reads CLOSED. The strike is Ren's/Protokoll's pen, not Methods'. |
| 7 | dependency-audit.md (Auslese) | **PARTIAL** | The priced cull **was applied**: lane `builds/depcull` (Oct 29 20:59, kit v1.3) cut C1–C5 + C7 after re-grepping current trunk; C6 (kotlinx-coroutines-test) was **acquitted** on re-verified evidence (Batch 6's ArmProvingControllerTest uses it) — the audit's own addendum records both. Trunk tonight confirms it: `Focus-app/app/build.gradle.kts` carries none of the six cut declarations and keeps coroutines-test. Not landed: the **Dormant Annex** (the 24 commented-out catalog entries are still commented lines in the build file), and depcull's own proof compile never ran (stood down in the 21:09 queue breach; proof.sh waits for the approved build). |
| 8 | cache-warming.md (Heizung) | **APPLIED** | `tools/warm-lane.sh` — built, measured (hardlink prime: 5 s, 44 B new content, verify pass), contract v1, and wired into the lane kit (make-lane.sh calls it). The daemon pre-warm verdict (NO, with one leashed exception) stands as the kit's practice. |
| 9 | lane-anatomy.md (Bausatz) | **APPLIED** | The six-part lane anatomy is the spec `tools/make-lane.sh` implements; `tools/lane-base.sh` freezes bases at creation (confidence exact) and is called by the kit at creation (kit-false-closure sweep row 4/9: VERIFIED 7/7 v1.3 lanes). |
| 10 | kitproof.md (Bausatz) | **APPLIED** | Proof run on record: one command births a lane with a warm GUH (kit v1.1 → v1.2 → v1.3 re-proofs in the file). The kit it proves is the kit in use tonight (birth-census row 14: lanes born 20:59 IST under v1.3). |
| 11 | lane-kit-metadata-fix.md (Bausatz, kit v1.3) | **APPLIED** | The fix is in the live tools: `tools/make-lane.sh` carries `KIT_VERSION="1.3"`; `tools/warm-lane.sh` v1.3 seeds metadata-2.107 as a PRIVATE REAL COPY (its header changelog names the defect and the fix). Before/after on record in the study: 0/436 descriptor.bin → 436/436, 439/439 files. |
| 12 | guh-census.md (Bausatz) | **APPLIED** | Census executed name-by-name (24 homes) and the instrument survives the event: `tools/guh-census.sh` in tools/ tonight. Spot-check by this ledger tonight: every `gradle.properties` under trunk-guh, tools/guh and `builds/*/guh` reads `-Xmx2g` — 33/33 homes (the tree grew since the census). |
| 13 | guh-alignment-sweep.md (Bausatz) | **APPLIED** | The alignment it documents landed: before 5/24 homes at -Xmx2g → after 24/24 at 2g in live **and** standby files, zero 3g/4g anywhere (BUILD-CHANGES 11:02 entry + the study). This is also the correction of §9 of memory-budget.md (Waage's sighting verdict 2 — the earlier "GUH layer closed" claim was trunk-only; corrected on record, and Ren's ruling to Waage, mail 2112, confirms the correction). |
| 14 | birth-census.md (Bausatz) | **APPLIED** | Instrument `tools/birth-census.sh` written this seating and run: the first v1.3 births (caps, depcull, truth-2, all born 20:59 IST) are censused in the file, caps as subject of record. |
| 15 | kit-false-closure-sweep.md (Bausatz) | **APPLIED** | Ran tonight ~23:33 IST: 11 "closed/done/proven" claims sampled and re-verified against disk — 2 FALSE/STALE **fixed in the same seating** (tools/guh-gradle.properties.note; warm-lane.sh WHY paragraph), 1 false-when-written already corrected on record, the rest VERIFIED with evidence. The sweep's method is the one this ledger borrows. |
| 16 | collision-radar.md (Gitta) | **PARTIAL** | Its tooling landed — `tools/lane-diff.sh` exists and the merge-review diffs it produced are on disk (token/routines diffs + summary.json, hash-verified). Its merge-order advice (routines → drawer registration → slide .tmp promotion) was followed in shape by the day's train. But the radar itself is a dated snapshot (~10:20 IST), never re-run as a standing per-merge instrument; tonight's Truth Car 3 merges priced collisions through Konto's map instead. |
| 17 | integrator-leverage.md (Kraftarm) | **PARTIAL** | Built and proven on the token lane: `tools/merge-prep.sh` (one packet per lane — manifest, diff stat, A1 grouping, dead-literal scan vs constants.json, claim-verification table) and `tools/merge-log.sh`. Proof artefact: `merge-review/token-packet.md`. What didn't become practice: only ONE packet exists in merge-review/, and merges after token were prepped by hand / by Konto's map, not by this tool. |
| 18 | merge-log-format.md (Waage spec, Kraftarm build) | **PARTIAL** | The format exists twice — this spec (CSV, one line per merge, weekly roll-up into WASTE-LEDGER) and the built minimal variant (`tools/merge-log-format-minimal.md` + merge-log.sh writing `merge-review/merge-log.md`) — and the reconciliation the tool's own header says Waage owes was never filed. Usage proof: `merge-review/merge-log.md` contains exactly **one** entry (token, retrospective). Every later merge is logged in BUILD-CHANGES instead. A format nobody writes to is a proposal, not a record. |
| 19 | delivery-record-format.md (Durchlauf) | **PARTIAL** | The format landed as templates: `tools/templates/merge-record.md` + `delivery-record.md` implement it (templates README says so explicitly, "never two formats"). The records themselves never started: **no `records/` directory exists** in the swarm tree tonight and no artifact record was ever opened — deliveries are still proven (or not) in BUILD-CHANGES prose. |
| 20 | output-path-audit.md (Durchlauf, WORK-QUEUE item 9) | **PARTIAL** | The audit itself delivered and closed DONE (queue Pulled list); its stranded-list and Hale's shipped-definition fed row 19's format and the A4 shipped gate. As a standing registry it was explicitly a snapshot, and no successor registry run exists — the Keepers' WORK-REGISTRY is the nearest living relative, kept by other hands. |
| 21 | template-proof.md (Schablone) | **APPLIED** | The proof specimen did its job: the six templates it validates are live in `tools/templates/` (letter, build-ready, spawn-brief, merge-record, delivery-record, lane-readme), are shaped so studio_check passes by construction, and carry later amendments (CLOSES stamp, distillate cap, severity at birth). Letters across the studio tonight use the shape — compliance line first, NEXT last. |
| 22 | checker-v2.md (Massarbeit) | **APPLIED** | `tools/studio_check.py` v2 is the live checker (header changelog = this study's four rebuilds: evidence density per claim block, dead-literal context windows, code-site reachability, forks-gate names, entry-scoped shipped gate, GOVERNS citations, constants section check). Full run filed: `methods/studio-check-v2-run.txt`. Open tail, stated plainly: v2's run still ends with **3 FAIL + 2 WARN** against other files (REGISTER governing-position dead literals among them, dispositions named in the study §4) — the checker is applied; what it caught is not all fixed. |
| 23 | read-first-map.md (Fundus) | **APPLIED** | Filed and verified entry-by-entry (15 question classes → one governing file + line each). Its findings have partially closed since: F-4 ("no governing home for memory budget") is closed by rows 5–6 landing; F-5 ("no standalone test-tier home") is answered in substance by test-cost §2's tier table (row 2). The map itself has not been updated to say so — a small staleness, noted for its owner. |
| 24 | review-flow.md (Sicht) | **PARTIAL** | Of its six priced fixes: F-1 (CLOSES stamp) and F-5 (severity at birth) **adopted** in templates README amendments; F-3 (pre-merge S1 sweep) **applied** — first sweep ran on the routines car (BUILD-CHANGES 11:02) and Ren ruled it a standing gate (mail/takt/1150, BUILD-CHANGES L368); F-2 (S1-WAIVED state) **named by Ren's ruling** but no waived-S1 entry exists on record yet — a state with zero instances; F-4 (INTERRUPTS delivery receipt) partially — INTERRUPTS.md carries a Closed section with the answered U1/U2 entry, not the proposed Delivered section; F-6 (review-latency line per merge entry) **not applied** — no `review:` lines in BUILD-CHANGES. |
| 25 | swarm-research.md (Fernrohr — transfers T1–T7) | **PARTIAL** | One of seven adopted: T5 (return-by-reference, ~1,500-token distillate cap) in templates README amendments. Not applied: T1 (claim-lease queue — no `plan/queue/` dir; WORK-QUEUE is still a list), T2 (speculative staging tree — the rehearsal merge, row 29, is a partial cousin built for a different commission), T3 (JSONL registers — the Keepers' books are still markdown), T4 (checker as completion hook — studio_check is run at gates, not wired into letter/done transitions), T6 (build-state.json — none exists), T7 (stacked sub-cars — no gate line). The file's own "if only three survive, take T1, T2, T5" — one of its three did. |
| 26 | debate-mechanics.md (Takt) | **PARTIAL** | Mechanics filed (DEBATE-ASK letter, Streit owns the floor, Ren closes). Practice on disk: exactly one debate record, `plan/debates/2026-10-29-slice-e-unruled.md` — but it was opened by Streit under `plan/DEBATE-PROTOCOL.md`, the older protocol, not under these mechanics, and no DEBATE-ASK letter is on record. The mechanics have never run a debate. |
| 27 | chunked-write-protocol.md (Satz) | **PARTIAL** | The practice is studio-wide tonight (this ledger is itself written in chunks, per R1) and the page is cited in KEEPERS-SWEEP and OPEN-LOOPS-MAHNER-ESCALATIONS (as O-7's formalisation). But it was never entered into `plan/PIPELINE.md` — it is "law here" (its own words, in methods/) and nowhere in the pipeline book that briefs actually cite. |
| 28 | merge-pipeline-timing.md (Takt, tonight 23:20) | **PARTIAL** | The timing study itself delivered in full (Truth Car 3 timeline, Ren's merge priced from Konto's map, compile-once trade measured). Its §3.3 recommendations await ruling: (1) declare-time grep census as a lane-close requirement — happened ad hoc tonight (Imke, Pruefer), not yet a requirement anywhere; (2) cartographer's final edition before the last builder declares — happened tonight, not yet law; (3) compile-once scoping line (≥3-lane cars / A-class collisions; solo zero-collision fixes may compile in-lane) — **not ruled**; PIPELINE Law 11 still reads unscoped. Its §4 invention was built the same night — row 29. |
| 29 | rehearsal-merge-first-run.md (Takt, tonight 23:31) | **APPLIED** | `tools/rehearsal-merge.sh` built and **run**: the three declared Car-3 lanes applied to a scratch trunk in Konto's map order in ~10 s; census live at `builds/rehearsal-merge/census.txt`, scratch tree at `builds/rehearsal-merge/Focus-app`. Exit 1 with six failed hunks — the instrument working as designed (it found, in seconds and with no Gradle, collisions the merge would otherwise have met by hand). |

**Sweep-2 subtotal: 16 APPLIED · 12 PARTIAL · 1 QUEUED · 0 DEAD.**
Pattern: Methods' *tools* got built and used; Methods' *record
formats* (merge log, delivery records) got built and then not
written to — the studio reaches for BUILD-CHANGES prose instead,
every time. That is the single most repeatable failure in this
ledger, and it is a habit failure, not a tooling failure.

---

## 5 · WHAT WAS APPLIED TONIGHT, BY THIS COMMISSION

Screen applied before writing: pure writing/settings work, safe
under the compile-once regime, inside Methods' own files, no live
queue touched, no app code touched, no `tools/queue.sh` touched.

### Applied 1 — the B1 reprice, closed in Waage's own page

Vorrat's page (§4 row 6) named the debt at
`methods/memory-budget-page.md` §6.1: item 14's gate still spent
B1 = 1,800 after Ren had ruled B1 provisionally repriced to 2,000
(Ren's ruling to Waage, mail/waage 2112, Oct 29 ~21:12 IST — "Edit
§9/the B-row accordingly; the provisional status prints wherever
the number is used"). A lane running the §7 gate as printed
under-charged every daemon by 200 MB. The page is Waage's; the edit
was mine to make. **Done tonight, ~23:45 IST.**

Before (all in `methods/memory-budget.md`, pre-edit):
- §3 B-row: budget **1,800 MB**, DERIVED.
- §4 lawful two-flag peak: 2×1,800 + 1,000 + 300 + 1,000 = 5,900 ✓;
  two flags + one idle resident: 7,700 ⚠ "inside the number".
- §6 Probe answers spent 1,800 (solo 4,600 ✓; repriced solo 5,600 ✓).
- §7 pre-flag gate: "every resident daemon charges B1 (1,800 MB)",
  `N×1800 + peak + 300 + 1000 ≤ 7935`; worked limits "≤3 residents …
  7,700".
- §10 precondition P3 spent B1 1,800 (= 5,124, "5,324 if repriced").

After (same file, verified by re-grep at filing):
- §3 B-row: **2,000 MB — PROVISIONAL (sighting-based; the solo run
  re-sights it)**, with the ruling, the sighting (temple 1,899 MB
  under a true 2g cap), and the application note in the provenance.
- §4 recomputed: assembling 3,300 ✓ · full suite incl. harness
  4,300 ✓ · two-flag peak **6,300 ✓** · +one idle resident
  **8,300 ✗ over the 7,935 box** · +two idle 10,300 ✗.
- §6: solo 4,800 ✓; repriced-B3′ solo 5,800 ✓.
- §7 gate: B1 **2,000 (provisional)**, `N×2000 … ≤ 7935`; worked
  limits rewritten — two flags with a harness are lawful only at
  exactly 2 residents; at 3 that state is OVER.
- §10 P3: 5,324, no conditional.
- §9 verdict 1 now carries an APPLIED note closing the loop.
- Remaining `1,800` strings in the file (4) are all provenance /
  history ("Originally DERIVED at 1,800", "pre-reprice this row read
  7,700", §9's sighting narrative) — no operative charge still
  spends 1,800. This also makes Waage's page and Vorrat's §1 agree:
  both now check against 2,000.

## 6 · WHAT NEEDS REN'S RULING OR A LANE WINDOW (not applied here)

1. **queue.sh v2.1 swap** (§4 row 4) — Stellwerk's candidate is
   built and sim-proven; the swap (§4 of queue-claim-fix.md) needs a
   no-live-lanes window and Ren's ruling. Never edit the gate
   mid-wave — the item's own constraint, honoured here.
2. **The solo baseline run** (§4 row 1) — already approved by Ren
   (mail/waage 2112: Waage fires it at the wave's landing and flips
   B3/B4/B6 to MEASURED in the same seating). Needs a quiet box
   (§10 preconditions P1–P5), not a ruling.
3. **WORK-QUEUE item 8 strike + the L25 reconciliation**
   (§4 row 6) — Vorrat's page says item 8 can be struck on its
   delivery; the queue and the registry currently disagree.
   Protokoll's/Ren's pen.
4. **Merge-pipeline §3.3 amendments** (§4 row 28) — grep census as a
   lane-close requirement, cartographer-before-last-declaration,
   and the compile-once scoping line. PIPELINE Law 11 amendment =
   Ren's ruling.
5. **Depcull's proof compile** (§4 row 7) — stood down in the 21:09
   breach; Auslese's proof.sh is scripted and rides the approved
   build. Lane window, not a ruling.
6. **The per-destination harness split + `fastTest` alias**
   (§4 row 2) — app/test code, ~1 h, one before/after timing run.
   Lane work; Probe's NEXT want from Oct 29 morning, still unseated.
7. **P-PROBE-2's code half** (§3 row 13) — writing the ratified
   fork/heap caps into `app/build.gradle.kts` is a 3-line app-code
   edit, but app code is a lane's, not this commission's. Lane
   window.
8. **The 19 QUEUED Sweep-1 proposals** (§3) — they wait in no queue
   a finisher pulls from. If any of them is ever to move, Ren rules
   it in by name (PIPELINE amendment or WORK-QUEUE entry, the two
   routes that demonstrably worked today); the rest should be
   struck in the open rather than left as ambient "pending". Bauzeit's
   territory (Gradle build speed) additionally stands **unresearched**
   since Sweep 1 — its seat's run never completed.
9. **Merge-log reconciliation** (§4 row 18) — Waage owes the format
   reconciliation merge-log.sh's header names (spec CSV vs built
   minimal format). Pure writing, Waage's — but it only matters if
   Ren rules that merges are logged in the log, not in BUILD-CHANGES
   prose; without that ruling the reconciliation is decoration.
   Ruling first, writing second.
10. **Checker v2's open FAILs** (§4 row 22) — 3 FAIL + 2 WARN stand
    against other owners' files (dispositions in checker-v2.md §4).
    Their files, their fixes; the ledger names them so they are not
    mistaken for closed.

## 7 · METHOD + LIMITS

- Sources read in full or in governing part: `methods/` (all 29
  studies + the v2.1 candidate + sim logs), `plan/PROCESS-PROPOSALS-1.md`,
  `plan/PIPELINE.md` (Laws + A1–A5), `plan/WORK-QUEUE.md`,
  `plan/INTERRUPTS.md`, `plan/RULINGS.md` (cited sections),
  `BUILD-CHANGES.md` (11:02 + 11:50 entries), `tools/` (every script's
  header + the templates README), trunk `Focus-app/app/build.gradle.kts`,
  `Focus-app/gradle.properties`, and every GUH `gradle.properties` on
  the tree.
- Every APPLIED above names the proving file/setting/behaviour.
  Where I could not find proof, the row says so and lands in
  PARTIAL/QUEUED — including for my own seat's work (rows §4.1,
  §4.18 are Waage's own and are not graded kindly).
- Not checked: phone-side behaviour (nothing here is proven on
  Sarthak's device), mail archives beyond the letters cited, and
  lanes born after ~23:40 IST tonight.
- Inbox: 1 unread at start (Ren 2112, the sightings rulings — read
  and acted on, §5 above), 0 at close.

— Waage, Methods · Oct 29, 2026, ~23:50 IST
