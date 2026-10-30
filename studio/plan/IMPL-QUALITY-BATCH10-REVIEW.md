# BATCH 10 — IMPLEMENTATION-QUALITY REVIEW (spec level)

**Seat:** Implementation-Quality (Anchor seat Q-3, issue #15).
**Job:** say, at SPEC level and before the merge is treated as done,
whether Konto's batch-10 merge plan can be implemented top-notch —
and kill what cannot. Not the merger (Ren merges), not a lane
builder. Nobody compiled for this review; no merge performed.
**Spec under review:** `BATCH10-MERGE-PLAN.md` Ed2 (commit 3875a78)
+ Ed3 overlay + errata (`BATCH10-MERGE-PLAN-EDITION-3.md`, e216481),
issue #15 body + comments (incl. #16 flags package 6509653913 and
its correction 6509684841).
**Evidence base:** the lane pins cited in Ed2 §1/§2, re-checked
against the as-built composition on `origin/batch-10` (which now
exists and partially executes the plan), `origin/main` (DB v12),
and `origin/agent/issue-16`. Where the as-built tree already
implements a spec item, that is the strongest implementability
evidence there is; where it diverges, the divergence is charged
by file and line.
**Verdict scale:** IMPLEMENTABLE · IMPLEMENTABLE-WITH-CHANGES ·
NOT-IMPLEMENTABLE-AS-SPECCED. Every flag carries its cheapest
safe alternative.

---

## 1 · THE ONE COMPOSED MIGRATION_12_13

### 1.1 Rows 1–5 — Frist's five — **IMPLEMENTABLE**

`peekMinutes` (guard_settings, INTEGER NOT NULL DEFAULT 2),
`breakMinutes` (guard_settings, … DEFAULT 10), `breakSeconds`
(focus_sessions, … DEFAULT 600), `extensionSecondsAdded`
(focus_sessions, … DEFAULT 0), `extensionLog` (focus_sessions,
TEXT NOT NULL DEFAULT ''). Each pins verbatim to lane source
(times MonolithDatabase.kt:197–201, triple-seat confirmed in
Ed2 §1), entity defaults = migration defaults, and both clamps
agree (peek 0–30, break 0–60; bounds in the setters, not the
schema). As-built proof: `origin/batch-10`
MonolithDatabase.kt:211–215 carries the five ALTERs exactly;
MonolithModels.kt mirrors them (GuardSettings:780–781 appended
after `shortsReelsSniperEnabled`:774; FocusSession:345–351).
No change needed to the migration text. (Whether all five
columns are ever *written* is a different charge — §3.2.)

### 1.2 Row 6 — Voss's `withinLabel`, from a v10 base — **IMPLEMENTABLE-WITH-CHANGES**

The column itself is sound and the plan handles the v10-base
problem correctly: Voss's tree is a v10-era CODE base wearing a
v12 Database (Dao paging cannot express DRW-04's page+1 law,
ReceiptState `plannedSeconds` reverted non-null against BASE's
RC-02 nullable, dead voiced surface at VM:2233–2245), so the
plan admits ONLY the column into 12→13 — nullable TEXT, NO
default clause (entity `String? = null`) — and re-bases the
rest hunk-by-hunk at step 6. That is the right call; a
transplant of Voss's Models/Dao/Database would silently
regress v12. As-built: the ALTER is exact (Database:216,
Models:355).

**The change required:** the column must not travel alone.
RC-03's cure is the FREEZE — Voss's Dao stamp
(routine-edit MonolithDao.kt:106–108) + Repository freeze
(:1597–1602) + the assembly read. In the as-built tree none of
those landed: `withinLabel` has exactly three references
(migration, entity, migration comment), and the receipt still
derives its WITHIN line live (`routineWindowLabelAt` →
`routineLabelWithin` in `assemblePendingReceipt`). A restored
slip can therefore still disagree with its first printing —
the exact defect RC-03 exists to kill — while the checklist
shape suggests Voss landed.
**Cheapest safe alternative:** port the three freeze hunks
(Dao stamp, Repository freeze writer, assembly read switch)
onto batch-10's current text — an afternoon-scale port, no
Voss file taken whole — or strike the Voss epic item to
"column only; RC-03 NOT cured" and keep the box unchecked.
Shipping the column alone and calling it Voss is the one
option that is not safe.

### 1.3 Rows 7–8 — Suki's `heldShutAtSeal` / `heldPeekAtSeal` — **IMPLEMENTABLE-WITH-CHANGES (package or nothing)**

These names exist in NO lane tree; they are Suki's Car-3 offer
(AUDIT-receipt.md:87,259), and Ed2 is honest about it (Critic
C1, four seats). The −1 default was Konto's provisional
adoption by analogy to `heldAppsAtSeal`/`heldSitesAtSeal`
(Models:332–333 in main). Ruling recommendation: **confirm −1
(R1)** — the as-built tree adopted it (Database:217–218,
Models:362–363) and it is the only default under which
pre-split rows cannot print a false HELD line; 0 would
fabricate "HELD 0 SHUT · 0 PEEK" history for every pre-v13
row, and holding the columns (Planner's 6-column fallback)
strands W-41 for another batch.

**The change required:** the columns cure nothing without
Ed2 step 9's full package, and the package is 90% landed —
birth writer stores the split beside the merged count with an
enforcer-derived sites figure (Repository:1494–1511 ✓) and
the fill point exists (Repository:1807ff ✓) — **except the
all-or-nothing gate is weaker than specced**: it tests
`heldShutAtSeal >= 0 && heldPeekAtSeal >= 0` and then passes
`heldSites = row.heldSitesAtSeal` unchecked, while Ed2 step 9
requires shut, peek **and sites** all ≥ 0 or the line is
OMITTED. In practice the birth writer sets all three together,
so the hole is latent, not live — but spec-level is exactly
where latent holes get closed.
**Cheapest safe alternative:** one condition —
`&& row.heldSitesAtSeal >= 0` — in the existing gate. No new
machinery. Until that lands, W-41 is "implemented with a known
gate defect", not signed.

### 1.4 Composed order + the anchor collision — **IMPLEMENTABLE**

Frist and Voss append focus_sessions fields at the SAME anchor
(after `receiptNumber`); Ed2 §1 fixes the composed order
(breakSeconds, extensionSecondsAdded, extensionLog,
withinLabel, heldShutAtSeal, heldPeekAtSeal) and requires the
entity fields to mirror it exactly, per table. Verified in
the as-built: Models:339–363 follows the table precisely,
GuardSettings appends after batch-9's last column, version
12→13 (Database:25), MIGRATION_12_13 appended to addMigrations
(:267), exportSchema=false so no schema files are owed.
Negative grep passes: exactly one MIGRATION_11_12 symbol
(definition + registration) — neither lane's private 11→12
text survives. `heldAppsAtSeal` stays (additive). This is the
plan's best-built section.

### 1.5 The 12→13 chain test — **NOT-IMPLEMENTABLE-AS-SPECCED at step 11 (owed, still absent)**

Ed2 §1/§6 assign a fresh ≡ upgraded chain test to step 11's
proof set; Ed3 keeps it open. It still does not exist: the
only migration tests on batch-10 are `BreakMigrationTest`
(11→12 only) and `TokenMigrationTest`. Step 11's exit line (b)
— "migration chain verdict per column (12→13, fresh ≡
upgraded)" — cannot be produced from anything in the tree,
and three mutually exclusive MIGRATION_11_12 symbols across
the lane trees mean no in-lane test discharges it (C12).
**Cheapest safe alternative:** extend `BreakMigrationTest`'s
own pattern — a v12-shaped file → MIGRATION_12_13 alone →
assert all 8 columns + defaults (−1/−1, withinLabel NULL) →
in-memory v13 Room round-trip. One test class, existing
infrastructure, no new harness. Until it exists, step 11
stays blocked by name (Ed2 gate M4), not by silence.

---

## 2 · THE HAND-MERGE POINTS

### 2.1 `start2MinutePeek` (ViewModel) — **IMPLEMENTABLE** (composition proven; two defects, §2.2–2.3)

Three authors, one body, and Ed2 §2's prescription is exactly
what the as-built does (VM:3303ff on batch-10): Suki's
pre-grant `peekOfferFor` re-derivation as base (strengthened
by Tor's N-2 session-liveness term), `val pkg =
state.interceptedPackageName` with no `ifBlank {
"com.whatsapp" }` anywhere, Frist's seed inside
(`secondsRemaining = (peekMinutes ?: 2).coerceIn(0,30) * 60`,
:3334–3345), the #16 Boolean grant gate gating launch +
record with pill-clear on refusal, and Pastille's ST-08 launch
transplanted AFTER `repository.startPeekLease(pkg)` inside
the grant block (:3358ff) — lease written before launch fires,
as ordered (guard → latch → grant → gate → launch → record).
The #16 correction's seed note is correctly superseded by
Ed2's PULS/SCHLIFF call. Verdict: the hand-merge is
implementable as specced; it has, in substance, been
implemented.

### 2.2 M3(b) composed `canPeek` — **NOT-IMPLEMENTABLE-AS-SPECCED as composed (one-line cure)**

Ed2 M3(b) demands the composed expression `canPeek =
peekOfferFor(...) && peekMinutes.value > 0` — an expression
that exists in NO tree, assigned to the merge. It was never
composed: `peekOfferFor` (VM, batch-10) takes no peekMinutes
parameter, and `peekWithheldFor` evaluates `canPeek -> null`
as its FIRST branch, so the S-7(a) rewire that DID land
(`peekMinutes <= 0 -> PeekWithheld.PEEK_OFF`, :209, correctly
fed at both call sites :3100/:3250) is **shadowed whenever
the offer is true**. Net behaviour at peekMinutes = 0: the
airlock offers the peek, `canUse2MinutePeek` = true, the
withheld reason is null — and refusal happens only later,
inside the Repository. That is Ed2's own warned failure
("a stale offer launches an app no lease protects" /
the offer outliving the grant) in milder dress: no unleased
launch occurs (the gate holds), but the door lies about
what it can sell.
**Cheapest safe alternative:** add the single term to
`peekOfferFor` — `&& peekMinutes > 0` with the value both
call sites already read from `guardSettingsState` — or move
the `PEEK_OFF` branch above `canPeek` in `peekWithheldFor`
AND force `canUse2MinutePeek = false` when it fires. The
first is one term + two call-site arguments. Do not sign
this seam under Ed2 gate M2 until one of them lands.

### 2.3 Grant-order deviation — **IMPLEMENTABLE-WITH-CHANGES**

The specced order puts the latch/seed after the grant is
known. The as-built plays `soundEngine.playDoorPeek()` and
seeds `_floatingPeekPill` synchronously BEFORE the async
grant; on refusal the pill is cleared but the door chord has
already sounded — a voiced false start on precisely the
peek = 0 path. **Cheapest fix:** gate sound + seed on the
already-in-hand `peekMinutes > 0`, or move both into the
granted branch of the launch block. Minutes of work; record
whichever is chosen, per the plan's own "silences are chosen,
not inherited" law (inverted: sounds too).

### 2.4 `startPeekLease` + Repository-head guard (B6) — **IMPLEMENTABLE** ✓

As-built matches Ed2 §2/§4B6 verbatim in structure: provider
duration first, `durationMillis <= 0` → false, unlisted-
package refuse (no lease, no cooldown), `clearOtherPeekLeases`
Dao companion present, lease write, Boolean return. The owed
merge-time write exists and is correct. Sign it.

### 2.5 The provider file selection (S-3) — **IMPLEMENTABLE** ✓ (one doc cure)

Times' `EditableTimes.kt` landed whole; its two functions
read the settings row with the specced arithmetic
(peekMinutes × 60_000 clamp 0–30; breakMinutes × 60 clamp
0–60); pill's constants version never entered; exactly one
provider survives. The file's header doc, however, still
says the shipped constants are the source "until the merge"
and describes the column reads in future tense — the code is
true and its own doc is now false. Cure the doc in the same
pass as §3.2 or it becomes the next stale record someone
merges against.

### 2.6 SessionSealTransition / isTombstoned — **NOT YET COMPOSED (see §3, step 3)**

Ed2 §2's corrected bullet (F2 deletes `isTombstoned`, ADOPT
lands, batch-9 sound tail composed on) is implementable as
written — the three-seat evidence is solid — but nothing of
it is in the as-built tree: `isTombstoned` is still live
(SessionSealTransition + RoutineHeartbeat caller). No verdict
against the spec; a verdict against calling step 3 done.

### 2.7 Unverified in this pass (named, not charged)

The C13 ghost ticker's final form (the seed comment claims
the ticker now derives from the lease end, UNA2-06 — the
ticker body was not re-derived here), M2's ReceptionSheet
duration cure, #14's S-1 test flip, and W-13's half of the
W-43 twin cure (W-43 itself verified landed verbatim,
ReceiptSheet.kt:337–353). Each is a step-10 reconciliation
item, listed so nobody mistakes this review for their proof.

---

## 3 · LANE-BY-LANE LANDING ORDER (Ed2 §3 steps, audited)

| Step | Item | Verdict | Basis / cheapest safe alternative |
|---|---|---|---|
| 0 | Baseline freeze + FAIL capture | **IMPLEMENTABLE-WITH-CHANGES** | Sound and non-negotiable, but Ed3's census is stale: the cells have since been merged into `batch-10`. Re-freeze against `origin/batch-10` HEAD + `origin/main` HEAD, not the cell branches; capture the FAIL baselines for the items still open (Voss freeze, nofire, warrant, chain test) before touching them. |
| 1 | Admission gate (PARTIAL on delta inventory, R3) | **IMPLEMENTABLE** | R3 is the right call — CONFIRMED-only would bar the deltas the epic exists to land. #5 Pass-2 rulings remain owed per lane; they block the lanes still open (Voss/nofire/warrant), not the ones already composed. |
| 2 | MIGRATION_12_13 first | **IMPLEMENTABLE** ✓ done | §1. As-built matches spec exactly. |
| 3 | warrant → nofire | **NOT-IMPLEMENTABLE-AS-SPECCED from the current branch set** | Stempel's and Wache's lanes were never issue branches #2–#17 and were never merged into `batch-10`: no `provePipelineNow`/`notePipelineProof`, no F1 `USE_EXACT_ALARM`, no F2 ADOPT, no F5 header, `isTombstoned` still live. **Cheapest safe alternative:** a step-3b port of the two lanes' named deltas directly from their lane trees onto batch-10 (warrant = 2 files + test, lands as written, FIRST; nofire's F1–F5 hunks + the §2 SessionSealTransition composition in the same sitting, incl. the ArmProvingControllerTest fake-override death). Until then both epic boxes stay unchecked — "CAN'T CHECK BLOCKING" is uncured in the as-built tree. |
| 4 | truth-3-times + provider, same sitting | **IMPLEMENTABLE-WITH-CHANGES — currently HALF-landed** | Provider ✓ (§2.5). Missing: (a) `breakSeconds` has NO writer — session creation never stamps it from `breakDurationSeconds()`, so the column sits at DEFAULT 600 forever; (b) the composed span hybrid (C6) was never written — `spanElapsedSeconds` still prices the in-break term off `SAFE_BREAK_DURATION_SECONDS` and `spanTargetSeconds` inter-block breaks at a hard 600, under a Models comment (:396–403) that still calls the stamp future; (c) ST-04 absent entirely (see below). **Cheapest:** one stamp writer at session creation + re-pointing the in-break term at the row's `breakSeconds` — Frist's own Models comment prescribes exactly this hybrid. |
| 4/R6 | ST-04 extend / extension columns | **NOT-IMPLEMENTABLE-AS-SPECCED until R6 is ruled; absent as-built** | No `extendActiveSession`, no ADD TIME chip; `extensionSecondsAdded`/`extensionLog` are dead columns, and `effectiveTargetSeconds` = target + penalty with NO extension term — while its entity comment (Models:347) claims the getters price the extension term. **Spec ruling recommended: current-block-only.** Never add the term to `effectiveTargetSeconds` (per-block multiplication: one +10 tap on a 3-block session = +30 span, receipt prints one event — PULS S2). Price the extension once into the current block's remaining and once into the span. **Cheapest safe alternative if Ren declines that work:** hold ST-04 entirely — columns stay at default 0, no chip — and record Frist's item as NOT landed. Half-landing it (writer without span law, or chip without writer) is the only unsafe option. |
| 5 | pill — #16 flags package | **IMPLEMENTABLE** ✓ in substance | Order respected (guard → latch → grant → gate → launch → record), Dao companion in, provider-as-add landed after the migration as Flag B requires. Residue: §2.2 + §2.3. |
| 6 | routine-edit (Voss), hunk ledger | **NOT-IMPLEMENTABLE-AS-SPECCED by transplant — and not yet attempted** | The plan's own re-base method is correct; the as-built simply skipped the editor: Shield still exposes batch-9's `onAdjustRoutineTime`, no `setRoutineTime`, no dual snap wheels, no EDIT chip, no `persistRoutine` create/rename/delete. This is the largest single remaining item in the epic. It lands only as: signature re-based onto BASE call sites (MainActivity's included), hunk ledger landed/excluded per hunk, dead voiced surface never ported, DRW-04 page+1 limitation stated in the ledger rather than papered over. Plus the §1.2 freeze hunks, which belong to this step. |
| 7 | session room from #13 cell tree | **IMPLEMENTABLE** ✓ | `DeskClockSettle.kt` + `ReceiptFiledStore.kt` (as a STORE, per Ed3 flag 8 — no new §1 item) are in the as-built; GANG S1's warning was heeded (room from the cell tree, not the settle-only lane). ROOM-sentence edge-cure (cold-open / hand-dismissed, C7 residue) remains owed at this step's exit — cured text or strike, by name. |
| 8 | onboarding LAST | **IMPLEMENTABLE** ✓ in substance | ReceptionSheet is in the as-built, after flow-clock, per the truth dependency. S-1 disposition (cured-with-test-flip per #14's commits) to be re-verified at step 10, not taken on the commit message. |
| 9 | Ren's hand-work | **IMPLEMENTABLE-WITH-CHANGES** | W-08 ✓ (dynamic PARK plate via `guardSettingsState`). W-43 ✓ verbatim ("THE MACHINE KEEPS ITS OWN LOG · YOUR STUDY HOURS LIVE IN YPT", ReceiptSheet:353). W-41: package landed except the §1.3 gate condition. Receipt Group A arrived via #10's merges. The M1 dropped-block gate must run against a RE-frozen inventory (step 0 note) — against Ed3's census it would audit the wrong tree. |
| 10 | Test call-site reconciliation | **IMPLEMENTABLE** | Method is right (table over EVERY test file, count stated, before the suite runs). Must now also cover: the step-3b fake-override death, the §1.5 chain test's addition, and §2.7's unverified list. |
| 11 | The single build | **BLOCKED — by named preconditions, not by the plan** | Entry needs Sarthak's quoted, dated word (G1) — Ren's to obtain, not this seat's. Proof lines (b) is unproducible until §1.5 lands. See also §4: the landing TARGET has moved. |

---

## 4 · THE BASE MOVED — the one structural change to the plan

Ed2's base-wins law was written against the batch-9 tree.
Since then `main` took the design-revamp (Sarthak's Oct 30
ruling: launcher-style block surface, separate ALLOWED/PEEK
screens — merge #167, `agent/design-revamp`), which is NOT an
ancestor of `batch-10`; and `batch-10` is NOT in `main`.
The epic therefore no longer lands on the base it was specced
against: closing #15 now requires a second merge —
revamp × batch-10 — over exactly the hot faces both sides
rewrote (MainActivity, Deck/TactileDeck, Airlock overlays).
**Verdict: the plan is IMPLEMENTABLE against its stated base;
against current main it is IMPLEMENTABLE-WITH-CHANGES.**
Cheapest safe alternative: treat the revamp's launcher
partition as base-wins for the Deck faces (it is Sarthak's
newest direct ruling — it outranks lane text under the plan's
own law), re-run Ed2 §2's seam ledger over the union tree,
and price that pass explicitly in the merge record instead of
discovering it during the landing. Do not let batch-10's
existence argue the epic is "basically merged": three of the
epic's seven lanes (Voss editor, Wache, Stempel) are absent
from it, one (Frist) is half-landed, and its target branch
has moved.

---

## 5 · SUMMARY — what Ren can sign, what he cannot

**Signable now:** the MIGRATION_12_13 text and composed order
(§1.1, §1.4); R1 = −1; the provider selection (§2.5);
`startPeekLease` + B6 guard (§2.4); the `start2MinutePeek`
composition's structure (§2.1); steps 2, 5, 7, 8 in substance.

**Signable after the named cheap fixes:** W-41 (one gate
condition, §1.3); the peek offer (one term, §2.2); the grant
order (gate the sound/seed, §2.3); step 4 (stamp writer +
span re-point, §3); the chain test (one class, §1.5).

**Not signable as landed, however the branch looks:** Voss
(editor absent, freeze absent — §1.2, step 6); warrant +
nofire (absent — step 3b owed); ST-04/extensions (absent,
R6 unruled — current-block-only recommended); the final
landing onto current main (unpriced revamp × batch-10 pass,
§4).

Nothing in this review compiles, merges, or messages
Sarthak. All rulings named (R1, R6, step-3b scope, §4
landing) are Ren's.

— Implementation-Quality (Anchor seat Q-3, issue #15)
