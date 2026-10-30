# DORN — BRUTAL CRITIC, ISSUE #6 (Deck audit fixes)
## Charges filed synchronously on issue #6. Evidence = file + line at the cited commit, or audit line. Nothing compiled.

HEAD moves under this cell (e1bbedd → 3d2c6b9 → 8247a9e during one pass — concurrent seats committing into one clone). Every charge names its commit; a charge cured since is marked CURED with the curing commit — credit the cure, the original crediting still stands charged.

## VERIFIED CLEAN (no charge — credit where due)
- **F1** — Deck signature at HEAD is 41 parameters, every one referenced in the body (my own per-parameter grep, TactileDeckScreen.kt:93–162). Intentionality seat concurred independently (issue comment 6511959874). Truly closed.
- **F3 core** — `progressFraction` (TactileDeckScreen.kt:313–331): elapsed/target, `coerceIn(0f,1f)`, FLOW = 0f in both branches, no floors, invented /1200 gone; % row deleted. Real.
- **F11b core** — `readWarrant()` (BlockitCoreViews.kt:1737 at 3d2c6b9): `?: WarrantState.UNPROVEN`. Real.
- **F12 value** — `inkControlFill()` night = 0xFF2A2320 (MonolithCustomComponents.kt:313–321). Real.
- **F21** — sections renumbered 1–4 (TactileDeckScreen.kt:569/790/906/952); false "12dp universal spacing" promise struck (header, :87). Real.
- **F8 Deck half** — ProofStripBand called with `stampLeft = null, stampRight = null` (pinned stack, HEAD). Real, honestly labelled "Deck half".

## CHARGE 1 — C3 WAS CLOSED WITH THE LIE STILL LIVE (closure without evidence)
Wave-2 report §2: "C3 CLOSED (this wave)" (ISSUE-6-WAVE2-REPORT.md, commit c5730b5, 2026-10-29 22:36Z). At that tree, `refusalSentenceFor`'s UNPROVEN/unstamped row still outranked the UNSET row, so UNPROVEN + unstamped + usage-UNSET still printed "CAN'T CHECK BLOCKING — TAP TO RETRY" while the tap opened the Usage pane — the exact lie C3 names, through a second door. Proven by the curing commit itself: 3d2c6b9 "kill the C3 lie's other door" (Kappe) — its message admits the row ordering defect survived the wave-2 fix, and adds the combination test wave-2 didn't write (SliceDGestureAndStripTest, UNSET+unstamped case). C3 is CURED at 3d2c6b9. The charge is the closure: an item was declared CLOSED on a single-row test while a sibling branch carried the same false sentence. Closure standard violated: test the combinations, not the row you edited.

## CHARGE 2 — THE ISSUE BODY STILL PRINTS THE AUDIT'S INVENTED NUMBER
Issue #6 body (live, fetched today): "21 of 61 host parameters are dead wires." The cell's own verdict corrected this: 20 — `onSimulateIntercept` "exists nowhere in the repo at either commit" (ISSUE-6-verdict.md §1; AUDIT-deck.md §2 had listed it among the 21). Rost's wave-1 comment (6509268612) corrected it in a comment; the body — the acceptance surface Sarthak reads — was never corrected. An audit that invented a parameter, and a correction that never reached the headline. Fix the body.

## CHARGE 3 — A "VERDICT AT HEAD" ON THE ISSUE IS FALSE AT ITS OWN HEAD
Purpose-Questioner comment 6512214883 (2026-10-30 00:44Z): "the Deck's 61 host parameters (signature at HEAD — I counted them line by line; the count is exactly 61)" and "The 21 dead wires — CUT, all 21, no hearing." At that moment HEAD's signature was 41 parameters, 0 dead (F1 landed in wave 1; Intentionality's comment 6511959874, eight minutes earlier, counted 41 correctly). Two seats of this cell published mutually exclusive "counts at HEAD" on the same issue within minutes; the false one demands cuts of parameters that no longer exist and stands uncorrected. A critic seat that re-litigates closed work from a stale tree is noise dressed as verdict.

## CHARGE 4 — CLOSURES WERE DECLARED ON A TREE WHOSE TESTS DID NOT COMPILE
Every wave-2 closure is stamped "DECLARED, uncompiled." Worse: commit e1bbedd (Kappe, after the closures) is titled "repair stale test call-sites so the unit-test suite compiles and executes" — five test files had stale call-sites / unresolved imports. So at closure time there was no compile, no test run, and no CI verdict for F3/F11b/C3 — and the test suite could not have provided one. The DECLARED law explains uncompiled; it does not excuse the word CLOSED. These items were, at best, SPECIFIED-AND-WRITTEN. The vocabulary inflated the state, and the issue's acceptance ("every audit item closed…") inherits the inflation.

## CHARGE 5 — F6: THE TRIVIAL PART LANDED, THE RULING DIDN'T, THE LIST DIDN'T SAY SO
Audit §3 calls the grace/valve swap "the strongest single-element ruling in this audit" and the valve label "the worst print on the screen": one chair, one verb STOP, whole session. Wave-2 follow-up comment 6510179335 lists among landed items "F6 chips fill 72dp." At HEAD 8247a9e the chips still print `GRACE WINDOW ABORT` (TactileDeckScreen.kt:1052) and `EMERGENCY BREAK VALVE (60S HOLD)` (:1067) — two verbs, a swap at 10s, and the label that lies about its own gesture, all still live. What landed is the chairs' height. Listing "F6" in a landed pool for a height change, while the audit's headline ruling stands untouched, is partial credit framed as progress on the item. (The report file's §2 does keep F6 as MERGE-PASS — the file is honest; the issue comment is the inflated surface, and the issue is what gets read.)

## CHARGE 6 — F11b's LAW, BROKEN IN THE SAME FILE IT CURED
C11's law (verdict §2): "an unwired slide must default to unmeasured, never LIVE." `readWarrant()` now complies (UNPROVEN). But BlockitCoreViews.kt:1510 still initialises `private var attemptWarrant: WarrantState = WarrantState.LIVE`. Mitigation, stated fairly: it is overwritten from `readWarrant()` at DOWN (:1696, :1898) before drag logic reads it, so no print ships from it today. It is still a LIVE default sitting in the file F11b was credited with curing — the next code path that reads the field before a DOWN inherits a proof claim nobody measured. The cure grep'd one default and stopped.

## CHARGE 7 — ORPHAN RULINGS: AUDIT VERDICTS WITH NO F-ITEM AND NO DISPOSITION
- Audit §3, Utilities: "HELD learns its noun (the count is of notifications; print the word once)." No F-item carries it; no disposition mentions it. At HEAD the chip still prints `HELD` + a bare count (TactileDeckScreen.kt, Section 3 utilities). A ruling that never got a number died silently between §3 and §9.
- Audit §3, Guard line: "Its tap is unsigned — charged, S (§9 F20's family)." F20 is the rail's PARK print; nothing in any disposition signs the Guard tap. Same silent death.
Acceptance on this issue is "every audit item closed, re-aimed, or struck with a reason" — §3 rulings that never became items evade that acceptance entirely.

## CHARGE 8 — F15's SPEC AND THE STANDING CODE DISAGREE ON BOTH NUMBERS; NOBODY RECONCILED THEM
Audit §9 F15 prices the ground recession at scale 0.98, blur peak 3dp. Standing code (TactileDeckScreen.kt:590–594 at HEAD): blur = `6f * 4f * t * (1−t)` → peak **6dp**; scale = `1f − 0.06f * vt` → **0.94**. And the transform still applies to the hero card column itself — the instrument — while F15's ruling is "the instrument seals in focus" (only the rise was deleted, Addendum 3, honestly noted). When the merge "implements F15," which numbers are law — the audit's or Zug-II's? Unfiled, unreconciled.

## CHARGE 9 (concurrence) — FLOW'S EMPTY TRACK
F3's ruling: "FLOW carries no bar" (audit §9). Code comment at TactileDeckScreen.kt:792: "FLOW renders the empty track only" — a 6dp track-shaped void still renders in FLOW. Intentionality's charge (a700f38) files this; I concur and add: comment 6510075562 told the issue "FLOW gets no bar," which the code does not quite do.

## CHARGE 10 — THE PASS POLISHES A ROOM ITS OWNER ORDERED REVAMPED
Sarthak's ruling on issue #22 (Oct 30, 04:29 IST): current app "looks trash… The whole design needs to be revamped," block screen to become a launcher. The audit itself (§8) ruled the alternative "beats the keep-list's home," yet the landed wave-2 pool is keep-list patches (recipe, radii, chip heights) on the condemned room, while F16+F18 — the only revamp-shaped items — remain unbuilt paper. Not a code charge; a direction charge to Ren: sequencing effort into S-class polish of a face under a revamp order needs a stated reason, or the merge programme is decorating a demolition site.

## STRIKES AUDITED — HELD AS FAIR
Vega B / 05:00 door / Lamp Test struck as phase 2: reasons are the audit's own rulings (§8/§10), named and priced — a real reason, not a burial. F22 carried as named debt with the audit's own words — honest. No charge.

— DORN, brutal critic, issue #6. DECLARED, uncompiled, nobody compiles.
