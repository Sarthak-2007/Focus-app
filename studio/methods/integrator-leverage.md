# INTEGRATOR LEVERAGE — Kraftarm, Methods (integrator-leverage tooling), Sweep 2 commission 2

*The lever arm for the one pair of merging hands. Built, proven on the
token lane, priced. Disk evidence linked per claim; estimates are
labelled estimates, measurements are measured.*

## What was built (all in `design-refs/swarm/tools/`)

1. **`merge-prep.sh <lane> [--letter p] [--mode pre|post] [--stdout]`** —
   one merge packet per lane at `merge-review/<lane>-packet.md`:
   manifest, diff stat, A1 tier grouping, dead-literal scan vs
   `tools/constants.json`, register/migration collision check, and a
   claim-verification table (closing-letter identifiers × disk grep).
   Diff engine: Gitta's `lane-diff.sh` summary when valid; light
   shell-diff fallback otherwise.
   **The invention: the closing letter IS the manifest.** A raw
   lane↔trunk diff lies once trunk moves (token proof: 12 differing
   src files, 7 of them trunk-ahead work the lane never claimed —
   LiveWire, B2 wheel files, A11y/Shield services). The letter's
   Files-changed section defines scope; everything else differing is
   listed as NOT-THIS-LANE. First proof run caught the naive version
   of this script claiming LiveWire.kt for token — because Voss's
   letter mentions it precisely to disown it. Section-scoped
   extraction fixed that; the trap is documented in the script.
2. **`merge-log.sh <lane> <verdict> <tests> [train]`** — one line in
   `merge-review/merge-log.md` (minimal format,
   `tools/merge-log-format-minimal.md`) + a paste-ready BUILD-CHANGES
   stub + a pre-filled Schablone merge-record
   (`merge-review/<lane>-merge-record.md`). Waage's
   `methods/merge-log-format.md` had **not landed** at build time;
   the minimal format stands until hers replaces it, under one
   contract: one command, sub-30-seconds. Reconciliation mailed to Waage.
3. **`question-gate.sh <draft>`** — heuristic checklist for INTERRUPTS
   batches, explicitly not a classifier. Fingerprints design words
   (should-we / which-variant / dp / layout…) vs life-words (phone,
   meditate, Saturday, did-you…). Exit 1 on any FLAG-DESIGN.

## Proof on token (already merged — packet vs the record)

Packet: `merge-review/token-packet.md` (§9 carries the findings).
- Manifest extracted = **13 files — exactly Voss's claimed 13.**
- Claim table: 15 YES / 4 NO, and **all 4 NOs are absence claims the
  letter itself made** (deleted VM flow; routines machinery flagged
  nonexistent) — i.e. confirmations. Two YESes are comment-only hits;
  flagged in the packet as evidence-quality caveats.
- Constants: 0 dead literals. Migration: DB v8 both sides, 7→8
  arbitration respected. A1 verdict FULL suite ↔ BUILD-CHANGES'
  recorded 24/24 full-suite run at 09:23. **Consistent.**
- **Discrepancy 1 (filing):** BUILD-CHANGES' 09:12–09:52 entry lumps
  five cars into one prose block and names none of token's 13 files.
  Token cannot be audited from the record alone. Merge-log fixes this.
- **Discrepancy 2 (review coverage):** Ren's hand-made diff set
  (`~/workspace/clone-inspect/merge-review/*.diff`) covers 8 of 13
  manifest files — MonolithWeek.kt (new) and all 4 test files had no
  diff. The merge was right; the review surface was 62% of the car.

## Prices — Ren-minutes before vs after

BEFORE = estimates from today's record, labelled as such (no stopwatch
existed; that absence is Sweep 1's point). AFTER = measured this run.

| Job | Before (est.) | After (measured) |
|---|---|---|
| Merge packet per car (read letter, make diffs, grep claims) | ~15 min/car (letter ~5, diffs ~5, ~19 claim greps ~6) | **3.7 s** compute (`--stdout` run) + ~2 min Ren skim of one page |
| Filing one merge (log line + BUILD-CHANGES entry) | ~5–10 min/train, composed from memory into prose | **0.05 s** script + ~5 s paste of the stub; merge-record arrives pre-filled |
| Question-gate check per INTERRUPTS batch | ~2 min/item, Ren reading every item cold (PLAN-AUDIT: 21 touches, 11 unnecessary) | **0.2–0.4 s** per batch; Ren reads only FLAG/REVIEW items |
| Claim verification per car | folded into packet time above; in practice partial (62% diff coverage) | full identifier table every car, ~0 extra Ren-minutes |

Proof-run timings on record: question-gate on the live INTERRUPTS.md
(0.23 s, queued item correctly PASS-candidate — it is a
facts-only-he-knows provenance question) and on a synthetic bad batch
(0.4 s: knob-vs-cap design question FLAG-DESIGN rc=1; phone-during-
meditation PASS-candidate). Merge-log token line written in one run:
`13 files | A1: FULL | tests: 24/24 | MERGED`.

## The environment finding (priced honestly — it ate my morning)

This VM **poisons an exec session's writes (EPERM) after heavy reads
of the app tree**: proven by isolation tests — small writes succeed in
a fresh exec, fail immediately after `diff -rq` over `app/src`, and
are still failing after a 30 s in-exec sleep; a fresh exec writes
fine. It zeroed Gitta's `token.diff`/`token.summary.json` (her writer
uses python `open().write()`), zeroed my first two script drafts on a
python overwrite, and killed several packet installs. Mitigations
shipped in merge-prep: build in memory, two-step protocol (heavy diff
as its own step, install in a fresh step), retry-with-verify, and
FAIL LOUD instead of a 0-byte success. One background run did install
(107 s with retries) — the block is probabilistic, not absolute.
**This belongs in the WASTE-LEDGER:** every lane tool that diffs a
tree and writes a report in one breath is exposed. Filed to Gitta +
Takt by letter.

## What remains manual, and why (deliberately)

- **Judging a NO in the claim table.** Absence can be a deletion claim
  holding, a rename, or a lie — token had all three shapes in one
  table's neighbourhood. Automating the verdict would need the tool
  to understand claim polarity; a wrong automated "lie" stamp is
  worse than a 2-minute glance. The tool narrows; Ren decides.
- **The transplant + conflicts inside manifest files** where trunk
  moved (token: Airlock/Repository/Models all moved post-fork). No
  script merges Kotlin semantics honestly.
- **Prose caveats in letters** ("unbuildable as written" items).
  §7 greps identifiers; honesty lives in prose. Ren reads that part —
  it is usually one paragraph, and it is where lanes confess.
- **The gate's REVIEW items.** The checklist flags fingerprints;
  a question can carry both (design words + life words). Ren reads
  those in full. Anything stronger would be a fake classifier
  pretending to know Sarthak's intent — refused on principle.

— Kraftarm, Methods · integrator leverage · 29 Oct 2026
