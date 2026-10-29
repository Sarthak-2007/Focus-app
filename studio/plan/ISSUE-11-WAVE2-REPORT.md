# ISSUE #11 — WAVE-2 REPORT (Held Door audit fixes)

**Lead:** Tor, cluster E, flat under Ren. **Cell:** builds/cells/issue-11, branch `agent/issue-11`.
**Base:** wave-1 commit c956260 (pushed). **Wave-2 state:** DECLARED, uncompiled — nobody compiled (standing law).
**Method:** 30 seats in 3 bundles + lead's own greps/fixes. Paper evidence only: file + line.

## HEADLINE

Wave-1's FIX-3…FIX-8 + FIX-5d survive re-verification. The worst defect is dead twice over:
RETURN is stationary across counting / offer / held / never-owed frames (fixed 110dp peek zone,
bottom-anchored card, RETURN centre 136dp above the card's bottom edge on every branch), and
"EMERGENCY PEEK" has zero hits in `app/src/main` (it survives only as a negative test assertion).
Wave-2 found and fixed the remainders wave-1 left: critic N-4, a test-tripwire blind spot set,
a stepping gate drain, and one orphaned enum.

## FIXES MADE IN WAVE-2 (this branch, uncommitted→committed with this report)

1. **N-4 CURED — fabricated hero name dies.** `MainActivity.kt:142` fell back to
   `?: "Short-Form Feed"` when the name extra was missing — a genre invented for an
   unidentified app, in the face's fact slot. Now `?: pkg` (package verbatim, the
   finisher-5 rule for unresolvable names). Sibling cured in the same pass:
   `MonolithShieldService.kt:194` `blockedApp?.appName ?: "Blocked App"` →
   `?: foregroundBlockedPkg`. (Brutal critic flagged N-4 was live at HEAD c956260;
   this commit is what closes it.)
2. **Gate drain stepping cured (Feel-3 FIX-FIRST).** The overlay redraw loop ran
   `delay(250L)`; the draining rule emptied in visible quarter-second steps.
   Now `delay(50L)` — printed seconds still derive from the anchored ends; only
   the drain's smoothness changed. AirlockAndDisarmOverlays.kt, redraw effect.
3. **Test tripwires repaired (Deep-thinker / Designer-29y / Implementation-quality FIX-FIRST):**
   - "EMERGENCY PEEK" now asserted absent in the OFFER frame — the frame where the
     chip actually stands (wave-1 asserted it only in the counting frame, where the
     chip cannot exist; a rename-back would have stayed green).
   - Chip badge asserted at the surface in the offer frame ("2m" for the 120s lease,
     honest m+s wiring, not just the pure function).
   - Honest exit asserted displayed on a peek-owing door and asserted as a ROUTE:
     new `honestExitRoutesOutOfTheDoor` clicks it and requires the caller's
     end-session handler to fire.
   - New `returnBoundsHoldWithMemoryErrandAndHomecomingContent`: stationarity frames
     now compose the three variable-content risks wave-1 never rendered — memory line
     ("THIRD TIME THIS SESSION", prior=2), errand sub-line (nextBreak set), homecoming
     greeting ("THE PEEK ENDED", justExpiredLease) — RETURN bounds must equal the plain frame.
4. **Orphaned enum KILLED (Purpose-questioning).** `DistractionTriggerReason`
   (MonolithModels.kt) deleted whole: FIX-3 killed its picker, state, coaching tips
   and every caller; the taxonomy remained as dead vocabulary of exactly the shape
   that re-arms a fabricated default. Verified zero references in app/src after removal
   (historical comments excepted).

## RE-VERIFIED (paper, per charge)

- **FIX-6 skeleton / stationarity:** Z = 48 (Z1) + 6 + 34 (Z2) + 4 + 18 (Z3) = 110dp exactly,
  offer/withheld/counting alike; tenants crossfade inside Z1's fixed 48dp; no `spacedBy`.
  Both wave-1 jumps (~55dp offer, ~17dp withheld) dead. Test asserts pixel-equal bounds.
- **FIX-3 ledger:** UNSTATED defaults (Models entity default now "UNSTATED";
  Repository recordIntercept/bookInterceptArrival defaults); RETURNED AT DOOR
  (VM outcome; "Stayed Focused" 0 hits); blank books blank (intention `active?.intention ?: ""`,
  TikTok/YouTube booking fallbacks gone). Third "Habit" default: KILLED.
- **N-1:** `splitActiveTaskAndReturn` deleted (0 hits); its "Stuck" booking unbookable
  (enum had zero callers — now the enum itself is gone, fix 4 above).
- **N-2 / FIX-5d:** session liveness is an input to `peekOfferFor` (first conjunct),
  end-instant suppressed on dead sessions, grant re-derives and refuses before
  sound/lease/booking. **Bounded residual (Brutal critic, accepted):** liveness and
  dismissal are session-emission-bound and read stored row counters, not wall clock;
  the shield heartbeat is the normal emission source. Recorded, not hidden.
- **N-3:** self-ticking counter and its lying comment both dead; `derivedSecondsUntil`
  ceiling form from anchored ends; "AGAIN IN 0 MIN" unrepresentable (line composes
  only at cooldownSecs > 0; derived-0 triggers re-derivation).
- **FIX-5 cooldown truth, FIX-4 onNewIntent, FIX-7 arrival seam (BACK parity, 180ms
  specA entry), FIX-8 (taper 5+session count cap 10 — pure fn fully pinned by tests;
  errand kernel `nextBreakInstantMillis` → "BREAK AT HH:mm OPENS THIS APP"; honest
  exit 48dp row → P-2-guarded `requestDisarmChallengeFromDoor`):** all present in code.
- **Sound score complete:** playDoorArrival/Arm/Return/Peek/Exit (TactileSoundEngine),
  five distinct recipes, arm once-per-nonce, refused grant silent, no gain > 0.60.

## REMAINDERS (not fixed in wave-2, with reasons)

- **FIX-1 compile proof** — Ren's build on Sarthak's word only. Still owed; every
  verdict here is paper until the door has run once.
- **Cooldown-at-0 seam untested** (Technical execution): overlay→VM refresh contract
  is correctly built but no test renders derived-0 / the RESOLVING tenant. Test
  extraction priced ~1 frame + a pure-derivation extraction; left for batch-10
  proof obligations (it joins #5's §8 list of proofs owed for cures in tree).
- **RETURN press craft** (Interaction-detail): bare `.clickable`, no pressed
  treatment; PEEK chip's 600ms fill has no haptic ramp. Proposals P-A/P-B below.
- **Gate track legibility in daylight** (Finisher-1 nit): undrained 2dp track at
  0.20 alpha may vanish in daylight; lawful at night, flagged for the phone proof.
- **`peekCooldownPrint(0)`** returns "AGAIN IN 0 MIN" as a pure function; the law
  holds only via the composable's guard (Thoughtful). Fragile, true today.
- **Raw package as hero** (Crazy-ideas/Designer): after fix 1, the nameless edge
  case prints e.g. a package string as the hero — truthful, ugly; a resolved-label
  lookup is polish for a later pass, not a truth fix.

## PROPOSALS (spec + price, NOT implemented — creativity mandate)

- **P-A "The latch lifts in your hand"** (Wow + Interaction-detail): haptic ramp
  synced to the chip's 600ms fill (amplitude envelope at press / 50% / completion,
  night-law compliant, haptic-only when sound off). ~50–70 LOC, 0 DB/VM changes,
  1 on-device feel check. Paint/layout untouched; stationarity unaffected.
- **P-B "Notches on the door"** (Intentionality): prior arrivals as tally notches
  drawn inside RETURN's existing 56dp bounds (paint-only, count = the same
  priorInterceptsThisSession the memory line reads). ~35–45 LOC, 0 new state,
  0 new prints.

## DECISION-FOR-SARTHAK

- **W-29 ratification (Ren's call, flagged for Sarthak's record):** Hale's wording
  book KEPT "EMERGENCY PEEK"; Tor's #11 audit overturned it and this branch
  implements the overturn (chip = "PEEK" + badge). The overturn still awaits
  formal ratification — see ISSUE-5-WAVE2-REPORT, where W-29 is filed as the
  register's one unchecked collision. No new decision is asked of Sarthak here;
  the item is recorded so it cannot be silently lost.
- Nothing else on this issue requires Sarthak's ruling. THE LEAF recomposition,
  P-4 THE SLIP, P-6 THE TRADE remain Ren's calls from the wave-1 audit, untouched.

## SEAT SECTIONS (30)

### Finishers ×5
- **Finisher-1 Surface — Anselm Brecht — PASS.** One accent only (RETURN slab);
  chip hold-fill is the same ink at 0.45 alpha; border 0.38 consistent. Nit: gate
  track daylight legibility (see Remainders). — A. Brecht
- **Finisher-2 Motion — Katrin Voss — PASS.** Entry fadeIn specA 180ms opacity-only
  over a fully composed skeleton; exit 260ms; tenant crossfades inside fixed
  bounds; chip fill/drain is the gesture itself. Nothing bounces. — K. Voss
- **Finisher-3 Light — Marek Lindqvist — PASS.** Opaque night ground; body ink
  0.92 alpha; historic footer AA failure fixed face-locally; RETURN label
  #471602 on #F47442 (5.37:1). No pure white. — M. Lindqvist
- **Finisher-4 Sound/Haptics — Ines Duarte — PASS.** Five voices, five recipes;
  arrival loudest 0.60, arm half, exit the only pitch step down; double-sounding
  structurally impossible on both seams. — I. Duarte
- **Finisher-5 Typography/Print — Ruth Adler — PASS.** NAME sole hero 24sp Sora;
  intention 16sp bare; gate line Anonymous Pro "PEEK OPENS IN M:SS"; badge and
  price derived (90s → "1m30s"; price re-timed to cooldown start). — R. Adler

### Feel specialists ×5
- **Feel-1 Weight — Tomas Riedel — PASS.** Tap / 600ms hold / 60s valve:
  resistance proportional to spend; TalkBack semantics bypass so the gesture
  is never the only door. — T. Riedel
- **Feel-2 Light — Sofia Marchetti — PASS.** Full opacity at 180ms, zero geometry
  ghosting; face feels still — the point of a wall. — S. Marchetti
- **Feel-3 Timing — Henrik Sauer — FIX-FIRST → CURED by lead.** Drain stepped at
  the 250ms redraw; lead applied the 50ms cure (fix 2). Printed seconds were
  always anchored-derived. — H. Sauer
- **Feel-4 Touch+Sound — Yuki Amamiya — PASS.** RETURN 56dp, chip 48dp, exit a
  48dp row (quiet by typography, not by target size); every spend has its voice,
  every non-spend is silent. — Y. Amamiya
- **Feel-5 Instant-Read — Pavel Novak — PASS.** One glance: name → intention →
  session end (or NO SET END) → gate/break → price before the act → RETURN;
  homecoming greeted as one first. — P. Novak

### The 20 named types
- **Crazy-ideas — PASS.** N-4's package-verbatim cure is the honest option; filed
  the raw-package-hero polish note. — Crazy-ideas, Tor's cell
- **Maximum-implementation-potential — PASS.** FIX-5d implemented in full, all
  three cures (offer input, end suppression + dismissal, grant refusal). — MIP
- **Brutal critic — FIX-FIRST (both items actioned by lead).** (1) N-4 lived at
  HEAD — closed by this commit, never record it closed before it. (2) N-2
  liveness is emission-bound — recorded as bounded residual above. — Brutal critic
- **Improver — PASS.** N-3 dead in both halves; derivation, not counting. — Improver
- **Designer — PASS (conditional on the N-4 commit — now made).** Hero slot holds
  a fact or the package; legibility loss in the edge case accepted for truth. — Designer
- **Consumer — PASS.** 1 AM test: RETURN live from frame one, price timed
  truthfully, honest exit spends nothing, no offer on a dead session. — Consumer
- **Student — PASS.** FIX-3 ledger verified clause by clause in code. — Student
- **User — PASS.** N-1 dead, not dormant; "Habit" default killed. — User
- **User-experience — PASS.** No corpse-face on a dead session; caveat shared
  with Brutal critic (emission-bound dismissal). — User-experience
- **Purpose-questioning — KILL (executed by lead).** Orphaned
  DistractionTriggerReason enum — deleted (fix 4). The door's purpose itself: PASS. — Purpose-questioning
- **Thoughtful — PASS.** Every print derives from an anchored end; flagged the
  peekCooldownPrint(0) fragility (Remainders). — Thoughtful
- **Deep-thinker — FIX-FIRST (executed by lead).** Three tests passed even if
  their named behaviour regressed; tripwires repaired (fix 3). — Deep-thinker
- **Enthusiastic designer — PASS.** Real material grammar: draining gate rule,
  held-not-tapped chip, five door voices; fonts per spec; no green. — Enthusiastic designer
- **Wow-level designer — PASS + proposal P-A/P-B (§ Proposals).** The 600ms
  hold-to-fill is good, not yet unforgettable. — Wow-level designer
- **Designer, 29 years — FIX-FIRST (executed by lead).** Stationarity proof
  never composed the risky content; new frame test added (fix 3). — Designer-29y
- **Intentionality-focused designer — PASS.** Gate names what it gates; price
  re-timed; memory scope honestly THIS SESSION ("TONIGHT" rejected in the
  function's own doc). — Intentionality
- **Synchronous collaboration — PASS.** Wave-1 seams line up across hands (one
  count drives taper + memory; FIX-5c a clean two-sided contract). Loose thread:
  badge truth owned by TruthCar3's suite, not #11's — mitigated by fix 3's
  surface assertion. — Synchronous collaboration
- **Implementation-quality — FIX-FIRST (executed by lead).** Claim-by-claim:
  stationarity genuinely asserted with holes (now fenced); taper fully pinned;
  honest-exit/badge tests were the holes. — Implementation-quality
- **Technical execution specialist — FIX-FIRST (open).** Cooldown re-derivation
  at 0 correctly built, untested — carried to Remainders/batch-10 proofs. — Technical execution
- **Interaction-detail specialist — FIX-FIRST (open, proposals P-A/P-B).**
  RETURN/chip/errand/taper all verified in code; press-craft gaps are the
  remainder, nothing requested is missing. — Interaction-detail

## GITHUB COMMENT (attempted via connector; exact text if blocked)

"Wave-2 — Tor, cluster E. Wave-1's FIX-3…FIX-8 + FIX-5d re-verified on paper
(30 seats): RETURN stationary in all frames, EMERGENCY PEEK at zero hits in
app/src/main, sound score complete. Wave-2 fixes committed on agent/issue-11:
N-4 fabricated hero name ('Short-Form Feed') + shield sibling cured to package
verbatim; gate drain redraw 250→50ms; stationarity/honest-exit/badge test
tripwires repaired; orphaned DistractionTriggerReason enum deleted. DECLARED,
uncompiled. Remainders: FIX-1 compile proof (Ren's build), cooldown-at-0 test,
press-craft proposals P-A/P-B in studio/plan/ISSUE-11-WAVE2-REPORT.md.
W-29 ratification still owed (see issue #5 wave-2 report). — Tor"

## NEXT (Law 10)

(a) Tor wants: the batch-10 proof obligations carried by name (cooldown-at-0
frame, plus #5's eight NO-PROOF units) and a phone proof of the door after
Ren's compile — daylight gate track, drain smoothness, RETURN address.
(b) Tor could do, unassigned: the Disarm-face audit (three wave-1 seats asked
for it; it shares this file and ledger), and the resolved-label lookup polish
for the nameless-package hero edge case.

— Tor, cluster-E lead, issue #11 (wave 2)

## ADDENDUM — delivery state under the gh/git route (Oct 30, 04:10 IST)

Course correction (Ren, Sarthak's order): the GitHub connector is retired —
gh CLI / git only. Reconciliation under that rule: this branch's remote
commits (219b888, 92f1c4f, b781c45, b6e94a2, landed via the connector
*before* the correction) and the local wave-2 commit (985fabc) were compared
tree-to-tree after `git fetch`: **diff empty — PUSHED-EQUIVALENT**. The local
branch was reset to the remote tip (no content lost; trees identical) and
this addendum is the first commit pushed by plain `git push`. The signed
issue comment was posted before the correction (comment 6510082633,
correction 6510143749); it is NOT re-posted via gh, to avoid a duplicate —
its exact text stands in the GITHUB COMMENT section above. — Tor
