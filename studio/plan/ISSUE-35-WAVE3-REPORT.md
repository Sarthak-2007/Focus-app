# ISSUE #35 (app-rules) — Wave-3 Report · HERZ cluster C3

Branch: `agent/issue-35` · Base: origin/main @ e29acdf · Audit tree read: 8438f5a
Verified against CURRENT code on main, not the audit tree alone.

## Per-finding dispositions

### 1. [MEDIUM] Ordering guarantee false — FIXED-IN-CODE
Evidence (verified): AppRules.kt:22–25 (main) sorted only by
`appName.lowercase()` — no tie-break; MonolithDao.kt:13 feed is
`ORDER BY isBlocked DESC, interceptCount DESC, appName ASC`, so the
header's "identical to the catalogue's" claim (AppRules.kt:5–8) was
false and name ties inherited intercept order. No consumer depends
on catalogue-order equality: ShieldMatrixScreen slipLines() even
re-sorted by lowercase name alone.
Cure: real total order in AppRules — `compareBy { appName.lowercase() }
.thenBy { packageName }` (package unique after dedupe ⇒ total,
input-order independent); header rewritten to state the real order
and explicitly NOT claim catalogue identity; slipLines() in
ShieldMatrixScreen switched to the same comparator. Proven by
AppRulesTest.totalOrder_* (same-name clones in both input orders).

### 2. [MEDIUM] Silent-drop construction — FIXED-IN-CODE
Evidence (verified): AppRules.kt:21–26 (main) was two equality
filters; OPEN dropped by design, any non-matching tier dropped by
accident, no remainder/invariant.
Cure: classification is now an exhaustive `when` over the parsed
stored tier into four named buckets — peek / shut / open / corrupt.
New `accountedCount` = sum of bucket sizes = distinct input
packages, asserted in tests. Nothing can vanish.

### 3. [MEDIUM] Corrupt tiers laundered via resolvedTier — FIXED-IN-CODE (AppRules scope) + CROSS-ISSUE evidence for #32
Evidence (verified): MonolithModels.kt:245–248 resolvedTier falls
back to LOCKED/OPEN from isBlocked on an unparseable blockTier;
AppRules grouped purely by resolvedTier, so a corrupt blocked row
printed as a deliberate SHUT decision.
Cure within AppRules: grouping parses the stored `blockTier`
string directly (`storedTierOrNull()`); unparseable ⇒ `corrupt`
bucket, never peek/shut/open. Tests prove both fallback directions
(blocked→would-be LOCKED, unblocked→would-be OPEN) land in corrupt.
CROSS-ISSUE FOR #32 (do NOT fix here — MonolithModels.kt is #32's
turf): resolvedTier at MonolithModels.kt:245–248 still launders
corrupt strings for every other consumer — MonolithRepository.kt:266,
1106, 1319; MonolithViewModel.kt:149, 542, 558–559, 2049, 2068, 2338;
MonolithShieldService.kt:167, 185; MonolithAccessibilityService.kt:227,
274; MonolithNotificationListener.kt:76; ShieldMatrixScreen slipTap/
slipLines tier reads. Recommendation to #32: add a validity surface
(e.g. `storedTierOrNull` / `hasCorruptTier`) on the entity and let
enforcement fail closed loudly instead of silently falling back.

### 4. [MEDIUM] Headline count guarantee dead code — FIXED-IN-CODE (consumer now uses it)
Evidence (verified): ShieldMatrixScreen.kt:139–140 recounted
LOCKED/PEEK_2M itself, :149 derived openCount by subtraction, :406
printed those locals; AppRules.peekCount/shutCount had no production
caller (grep: only tests).
Cure: ShieldMatrixScreen now derives shutCount/peekCount/openCount
from `rules.shutCount / peekCount / openCount` (the bucket list
sizes). The guarantee is live, not deleted. Side effect, intended:
corrupt rows and duplicate packages no longer inflate the tally —
the printed count is the lines under it, literally.

### 5. [LOW] No dedupe / disjointness / edge tests — FIXED-IN-CODE
Evidence (verified): no distinctBy anywhere in AppRules; public
constructor enforced nothing; AppCatalogReconcileTest.kt:117–139
covered only 5 distinct names + all-OPEN.
Cure: dedupe by packageName before classification — strictest
valid ruling wins (LOCKED > PEEK_2M > OPEN; corrupt copy loses to
any valid copy; ties keep first copy) so a blocker fails closed;
AppRules.init requires bucket disjointness by package for ANY
construction. New AppRulesTest.kt (12 tests): empty input,
duplicates (same tier + contradictory tiers), corrupt tier
(blocked + unblocked + vs valid copy), all-shut, all-open,
name-tie total order, case-insensitive order, accounting
invariant, constructor disjointness.

### Wave-2 #7 check — NOT FIXED-ELSEWHERE
origin/agent/issue-7 diff for AppRules.kt adds only
`groupedForCatalogue()` (+21 lines) with the same lowercase-only
sort and resolvedTier filters — it fixes none of the 5 findings;
no duplication. If #7 merges first, groupedForCatalogue should be
re-pointed at this cell's total classification/order on rebase.

## Counts
Verified: 5/5 · Fixed-in-code: 5/5 (finding 3 additionally files
cross-issue evidence for #32) · Struck-false: 0 · Blocked: 0

## 20 seat lines (lens passes, signed)
- Crazy-ideas: "Give corrupt rows their own printed bucket — done; next: surface corruptCount on the slip so damage is visible, not just filed."
- Maximum-implementation-potential: "Total classification + total order + dedupe in one 100-line pure function — the whole cure fits in the file that lied."
- Brutal critic: "The old header was marketing copy over two filters; the new header's every sentence is now backed by a test name."
- Improver: "Consumer recount deleted, not duplicated — one predicate, one home, in AppRules."
- Designer: "Slip order is now deterministic across intercept changes; a name will never jump height between visits."
- Consumer: "As the phone owner: a count that can disagree with its lines is worse than no count — now impossible by construction."
- Student: "Learned: a data class with derived counts is only a guarantee if the screen actually reads them."
- User: "My corrupted row no longer pretends I decided to shut an app — it says corrupt, in its own bucket."
- User-experience: "Fail-closed dedupe means a stale duplicate can never quietly un-shut an app I shut."
- Purpose-questioning: "Asked why OPEN was dropped at all: it wasn't a ruling, but vanishing ≠ uncounted — now it's counted, unprinted."
- Thoughtful: "isEmpty kept its honest meaning (no decisions) while corrupt-only input is no longer 'empty' — damage demands attention."
- Deep-thinker: "The root disease was trusting a fallback getter as a fact source; parsing the stored string in AppRules severs that for this surface."
- Enthusiastic designer: "Four buckets, one invariant, zero silent drops — this little ledger finally behaves like a ledger!"
- Wow-level designer: "Deterministic order is invisible polish — nobody notices, everybody trusts the slip more."
- Designer-29-years: "In 29 years: every 'identical to X's order' comment I've audited was false. Strike the claim, implement a total order — done both."
- Intentionality designer: "Strictest-wins dedupe is an intentional stance: when records disagree, the blocker's doubt resolves toward protection."
- Synchronous collaboration: "Cross-issue evidence for #32 filed with exact consumer lines so the models cell can cure resolvedTier at the root."
- Implementation-quality: "Init-block disjointness makes the invariant hold for any future constructor caller, not just groupedByRule."
- Technical execution: "No schema touch (DB v12 frozen), no MonolithModels edit, surgical 3-file diff + tests; CI is the compile court."
- Interaction-detail: "slipLines re-sort aligned to the same comparator — resting-flip lines can't reorder ties differently from committed lines."
