# IMPLEMENTATION-QUALITY — Held Door FIX-3…FIX-8, spec-level review

**Seat:** Implementation-Quality (Anchor seat Q-5, issue #11)
**Scope:** Tor's cell specs FIX-3…FIX-8 from `studio/plan/AUDIT-held-door.md`
(+ `docs/tor-plan.md` work order). FIX-1/FIX-2 are out of scope — cured
in the batch-9 merge, verified by the cell's critic.
**Ground read:** audit (whole), tor-plan, wave-2 report
(`studio/plan/ISSUE-11-WAVE2-REPORT.md`), and the branch's current code
at HEAD `2a27313` — `AirlockAndDisarmOverlays.kt`,
`MonolithViewModel.kt`, `MonolithRepository.kt`, `MainActivity.kt`,
`HeldDoorFixTest.kt`, `HeldDoorStationarityTest.kt`. Code is used here
as evidence of implementability, not as a substitute for the spec
verdicts: every verdict below is about the SPEC, and holds even where
the branch already implements it.
**Compile state:** nobody compiled, including this seat. Every verdict
is paper until Ren's build on Sarthak's word.

## HEADLINE

**No fix is not-implementable. Nothing in FIX-3…FIX-8 needs new
machinery, a schema change, or a platform capability Android does not
have.** FIX-3 and FIX-4 are implementable exactly as specced.
FIX-5, FIX-6, FIX-7, FIX-8 are implementable-with-changes — the
changes are small, priced below, and three of them are already the
branch's (Tor-ratified) practice; this review pins them into the spec
so they stop living only in code comments.

| Fix | Verdict |
|---|---|
| FIX-3 ledger pack | **Implementable** |
| FIX-4 repeat intercepts | **Implementable** |
| FIX-5 cooldown truth pack | **Implementable-with-changes** (C-5a, C-5b) |
| FIX-6 composition pack / fixed skeleton | **Implementable-with-changes** (C-6a, C-6b, C-6c) |
| FIX-7 arrival seam | **Implementable-with-changes** (C-7a, wording only) |
| FIX-8 names, prices, memory, honest exit, taper | **Implementable-with-changes** (C-8a, C-8b) |
| Lease/grant re-derivation (cross-cutting) | **Implementable-with-changes** (C-L1, C-L2) |

---

## FIX-3 — The ledger pack · IMPLEMENTABLE

Pure data and deletion work; the cheapest fix in the set and the one
with the least platform risk.

- **UNSTATED default:** `triggerReason` is an existing String column.
  Changing the default at the entity/repository layer is a value
  change, **not a migration — DB stays v11**, no destructive-upgrade
  risk, no backfill question (old rows keep their historical lie;
  the spec correctly does not propose rewriting history).
- **Dead wires:** `onSelectReason` / `onTransmuteParticleTap`, their
  state fields and the `coachingTip` strings have no live caller once
  the picker is gone — deletion is grep-provable, and the wave-2
  deletion of the orphaned `DistractionTriggerReason` enum itself is
  the right completion: a dead taxonomy is how a fabricated default
  re-arms.
- **"RETURNED AT DOOR":** verified no code path switches on the old
  `"Stayed Focused"` string (its only remaining occurrence is a
  comment stating the ban) — the outcome string is printed, not
  branched on, so the rename cannot break a downstream consumer in
  this tree. Downstream *prints* outside this tree (Ledger surface)
  are Lade's surface, already ridered in the audit.
- **Blank books blank / no draft fallback:** removal of two fallback
  expressions. Zero cost, zero risk.
- **One spec note, not a change:** FIX-3c says "stayed focused" is
  bookable at session end. That booking does not exist yet and is
  correctly referred downstream — the spec must not be read as
  closing it inside this fix.

## FIX-4 — Repeat intercepts · IMPLEMENTABLE

Standard Android lifecycle work, no exotic surface.

- `singleTop` + `onNewIntent` → `setIntent` + consume extras through
  the same `handleIncomingInterceptIntent` used at first composition
  is the canonical cure. Cost: ~10 LOC plus the routing call.
- **Idempotence is the spec's hidden requirement, and it is
  satisfiable:** the handler must consume-and-clear the trigger
  extra (`EXTRA_INTERCEPTED_PACKAGE` removed after handling, as the
  branch does) so a configuration change or process recreation
  cannot re-fire a stale door from the retained intent. The arrival
  nonce (`interceptNonce`, FIX-8/P-4c machinery) is the second line:
  a re-fired intent for the same visit re-derives state under a new
  nonce rather than inheriting an expired wall.
- Residual: name/badge extras are not cleared, only the trigger,
  path and sub-line extras are. Harmless today (the package extra is
  the sole trigger), but the spec should say "clear every extra the
  handler reads" so a future extra does not become a stale channel.

## FIX-5 — The cooldown truth pack · IMPLEMENTABLE-WITH-CHANGES

The derivation doctrine (print = f(anchored end, now), ticker =
redraw trigger only) is exactly right and is the pill's own proven
pattern in this tree. Two changes pin its edges:

- **C-5a — The refresh contract needs a stated termination proof.**
  At derived-0 the overlay calls `onDoorStateRefresh()`; if the VM's
  re-derivation returns the same withheld state, a naive
  implementation loops (effect → refresh → recomposition → effect).
  Satisfiable, and satisfied on the branch: `refreshAirlockDoorState`
  returns without a state write when neither the offer nor the
  withheld value changed, and the effect is keyed on the derived
  seconds, which do not change again. The spec must carry this
  guard as a requirement, not leave it as an implementation habit:
  *a FIX-5c refresh that changes nothing writes nothing.*
  Also pin the branch's resolution rule into the spec: **a resolved
  offer stands immediately and does not queue behind a second taper
  wall** — the cooldown was the visit's wall. Without that sentence
  the spec is ambiguous and a second implementer will gate it.
- **C-5b — Name the anchor's platform limit in the spec.**
  All anchors are `SystemClock.elapsedRealtime()` values. They are
  correct within a boot and meaningless across one: the repository
  persists monotonic instants in Room, and after a reboot a stored
  anchor is compared against a restarted clock. The failure
  direction is safe — `getPeekCooldownRemainingSeconds` caps the
  remainder at `PEEK_COOLDOWN_MILLIS`, so the worst post-reboot
  outcome is a full 20-minute cooldown, never a free peek and never
  a negative print — but the spec currently claims "derived, never
  literal" without disclosing that the derivation's clock dies at
  reboot. One sentence in the spec; no code change owed.
- The ceiling cure (FIX-5a of the audit) is one arithmetic change at
  the repository source. Its other caller surface is internal to the
  same derivation family; no external consumer depends on the floor.

## FIX-6 — The composition pack / fixed skeleton · IMPLEMENTABLE-WITH-CHANGES

**This is the fix I was seated to judge hardest, because the named
defect — RETURN jumping 55dp under a travelling thumb at countdown
0 — is a layout-stability defect, and layout stability is decided by
composition structure, not by drawing.** The audit's diagnosis is
correct: the old centred column grew ≈110dp when the offer armed,
RETURN (56dp slab in the audit's accounting; the jump measured at
55dp, more than the slab's effective travel tolerance) moved under
a thumb already in motion toward where EMERGENCY PEEK would appear.
Any cure that only reserves space "most of the time" does not cure
it. The spec's cure direction is right; three changes make it
airtight:

- **C-6a — The audit's own zone heights do NOT, by themselves, cure
  stationarity across a mid-visit branch change — adopt the unified
  zone + bottom anchor into the spec.** As written, the audit prices
  the peek zone at 110dp (offer) / 34dp (withheld) / 0dp (never
  owed). That cures the named defect — countdown-0 is a GATE→CHIP
  flip *inside* the offer branch, both at 110dp — but FIX-5c creates
  a second mid-visit flip the audit's numbers re-break: a withheld
  door whose cooldown expires re-derives into an offer on the same
  visit, and 34dp → 110dp is +76dp of growth, i.e. the same jump,
  smaller, through a different door. The branch's Tor-ratified
  deviation is the correct cure and must become spec text:
  **every peek-owing door (offer or withheld) composes the zone at
  the full 110dp from frame one (Z1 48 + 6 + Z2 34 + 4 + Z3 18),
  tenants crossfade inside Z1's fixed 48dp, and the whole card is
  bottom-anchored so the never-owed↔owing height difference moves
  only the card's top edge, never RETURN's screen address**
  (RETURN's centre: 136dp above the card's bottom edge —
  24 padding + 16 footer + 8 + 48 honest-exit + 12 + 28 half-slab —
  on every branch, every visit). Stationarity tests must compose
  the branch-flip frames, not only the countdown frames; the wave-2
  tripwire repair (memory line + errand sub-line + homecoming
  frames) is the right shape and should be named in the spec as the
  acceptance frame set.
- **C-6b — The skeleton is only guaranteed at the font scale it was
  measured at; the spec must state its envelope.** The zone heights
  are fixed `dp`; the text inside them is `sp`. `dp` does not scale
  with the user's font-size setting, `sp` does. At fontScale 1.3:
  Z2's price (12.5sp, maxLines 2, in a 34dp box) needs ≈44dp of line
  height and will clip or ellipsize — and an ellipsised *price* is a
  truth defect, not a cosmetic one, on this face. Z3's 18dp memory
  line and Z1's gate line (15sp in 48dp — survives) are similarly
  exposed. And the audit's own caveat — "verify the 110 on device
  against the price's real line count" — is width-dependent too: on
  a narrow device the price sentence can want a third line the
  2-line cap will cut. Spec change (choose one, say which):
  (i) acceptance includes a device pass at fontScale 1.0 **and**
  1.3 and at the narrowest supported width, with clipping = fail; or
  (ii) the price sentence is shortened until it is one line at
  1.3 on the narrow width. Silence on this point is how the skeleton
  ships stationary and unreadable.
- **C-6c — Split the redraw rate by tenant.** The wave-2 cure for
  the stepping drain (250ms → 50ms) is correct for the ≤10s wall:
  printed seconds derive from the anchor at whatever frame prints
  them, so the rate is purely cosmetic there. But the same 50ms loop
  is the overlay's only clock for its whole visit, including a
  withheld door standing on a 20-minute cooldown whose print changes
  once a minute — that is ~20 recompositions/second of the full
  overlay for zero information. Cost is small per frame and the
  door is transient, so this is not a blocker; the top-notch form
  is a two-rate spec: 50ms while the GATE tenant stands, 250–1000ms
  otherwise. Priced at ~5 LOC in the redraw effect.
- The rest of FIX-6 is implementable as written and cheap: gate line
  as captioned print over a 2dp draining rule (the drain fraction is
  one division against `wallTotalSeconds`), intention bare at 16sp,
  eyebrow death, badge-word fallback ×3 sites (four with
  MainActivity), name-slot truths (all data already reaches the
  trigger — guard/site/sniper differ only in which existing field
  fills the slot), RETURN 56dp slab with `#471602` on-container ink
  (5.37:1 vs today's 2.55:1 — arithmetic, platform-independent),
  PEEK chip 48dp, border alpha 0.38, body alpha 0.92, centred
  wrapped lines, one-line ellipsised name. No machinery, no state
  change beyond what FIX-5/FIX-8 already carry.

## FIX-7 — The arrival seam · IMPLEMENTABLE-WITH-CHANGES

- **C-7a — One wording correction with teeth:** `GLOBAL_ACTION_BACK`
  is `AccessibilityService.performGlobalAction`, which *requests*
  the action and returns a Boolean; it is asynchronous and can
  no-op (service mid-rebind, action refused). The spec's intent —
  BACK performed **before** launch on the app-tier and shield paths,
  like the three sibling paths — is fully implementable and is the
  right parity cure. But no spec or wording-book entry may claim
  the blocked app "cannot stand visible": the honest claim is
  *BACK is dispatched before launch; the launch no longer waits
  behind a visible reward by construction of the ordering, not by
  guarantee of the action.* The audit's correction of W-32/A-08
  stands and should use exactly that weaker, true form.
- Entry fadeIn specA 180ms / exit 260ms: trivial, already the
  curve family in the tree; opacity-only over a fully composed
  skeleton (per FIX-6) means the fade can never ghost geometry.
- Arrival voice (300Hz, 44ms, gain 0.60, chordRatio 1.5): pure
  synthesis parameters in the existing `TactileSoundEngine` recipe
  shape. No asset, no permission, no cost beyond the recipe entry.
  The once-per-visit guard belongs in the VM (nonce-keyed), as the
  branch has it — the spec should keep the sound's *ownership*
  sentence ("the overlay reports, the VM sounds") because the
  double-sound seam is structural, not accidental.

## FIX-8 — Names, prices, and the door's memory · IMPLEMENTABLE-WITH-CHANGES

Four of its six parts are trivially implementable; two carry the
changes.

- **Trivial, as specced:** "EMERGENCY PEEK" → "PEEK" + badge is one
  string at one site (face :665 in the audit's numbering) — the
  cheapest S-grade honesty fix in the set. The re-timed price
  sentence is true *because of the lease mechanics* (see C-L2):
  `startPeekLease` pre-anchors the cooldown at the lease's
  scheduled end and `recordPeekEnded` re-anchors it at an early
  end, so "WHEN THE PEEK ENDS, A {n} MINUTES COOLDOWN STARTS ON
  THIS APP" is derivable, not aspirational. `peekBadgeText` m+s is
  a pure function (90s → "1m30s"). `canUse2MinutePeek` default
  true → false is one literal and removes a real foot-gun.
- **C-8a — The taper is implementable; its count's freshness is a
  bounded race and the spec must bound it in words.**
  `taperWallSeconds` itself is a pure function (5 + booked, cap 10)
  — free. Its input, this session's booked intercepts of this
  package, is a log query over the session's wall window
  (`sessionInterceptCount` over `getInterceptLogsBetween` — the
  receipt assembly's own precedent; explicitly *not* the lifetime
  `interceptCount` on the app row, which would print a lifetime
  total in session clothes). The race: the arrival row is booked in
  a coroutine at trigger (C-11 machinery); a second intercept fired
  inside that write's flight can undercount by one — wall 1s short,
  memory line off by one ordinal, both wrong in the forgiving
  direction and both self-correcting on the next visit. Acceptable
  **if** the spec states the count is "booked intercepts visible to
  the trigger-time query" and the trigger path serialises arrival
  booking before the count query in the same scope order. Do not
  spec the count as exact without that sentence.
  The taper's gating scope is implementable exactly as ruled —
  offer appearance only, never RETURN, never the withheld lines —
  because the wall lives entirely inside the offer branch's Z1
  tenant selection.
- **C-8b — The errand's kernel is implementable only while blocks
  are uniform; say so.** `nextBreakInstantMillis` derives the break
  instant from the session's own fields (current block's stored
  remainder; mid-break: break remainder + `effectiveTargetSeconds`).
  That is exact for the session model this tree has — one target
  length per session. The mid-break branch silently assumes the
  *next* block runs the full effective target; the day per-block
  custom durations land, this derivation prints a confident wrong
  instant. Spec sentence owed: "derived from the uniform-block
  session model; a per-block-duration model must re-derive this
  instant from the schedule, not from the target." Null cases
  (single-block, last block, stopwatch) print nothing — correct and
  free.
- **THE HONEST EXIT — implementable as specced, and it is the
  best-shaped item in FIX-8.** It needs no new machinery: the
  Disarm face, its 60-second hold and its state all exist; the door
  adds one footer route. The implementation conditions that make
  it honest rather than decorative, all satisfiable in the existing
  state shapes, and all to be carried as spec requirements:
  1. **Skeleton membership:** the exit row lives *below* RETURN in
     the fixed below-RETURN stack at a full 48dp touch height —
     quiet by typography, never by target size — so its presence
     can never move RETURN (it is inside the 136dp invariant in
     C-6a). A spec that adds the line without fixing its height
     re-opens FIX-6's defect from below.
  2. **Route, not spend:** the tap books nothing, sounds only the
     exit voice on a *confirmed* handoff, and the airlock is **not**
     dismissed — the Disarm face stands in front of the
     still-standing door; KEEP FOCUSING returns to the untouched
     door; the door clears only when a disarm actually completes.
     (Branch: `requestDisarmChallengeFromDoor` — guard first, sound
     second, no airlock write. Correct order.)
  3. **Dead-session guard:** with no live session the tap is a
     silent no-op — no face, no sound, no stranded state. A footer
     that opens nothing must never dismiss the door underneath it.
  4. **No silent completion:** a stopwatch session past its floor
     may complete, but only as a deliberate slab *on the Disarm
     face* (`cleanCompletionAvailable`, derived at open), never as
     a silent shortcut taken by the footer tap itself. The footer
     is a door to the valve, never the valve.
  Cost: one 48dp row, one VM entry function (~20 LOC), one state
  flag. The gravity stays where the audit put it — on the valve's
  own face.

## CROSS-CUTTING — the lease/grant re-derivation the door depends on

The audit acquitted `peekOfferFor` + grant-time re-derivation
unanimously; this seat confirms the acquittal at implementation
level and adds two changes:

- **The shape is right.** The offer is a pure function of (path,
  entity, cooldown, pill, session liveness); the grant
  (`start2MinutePeek`) re-runs the *same* function at tap time
  against freshly read state and refuses silently — no lease, no
  cooldown anchor, no booking, no sound — when it fails. Pure
  function + re-derivation at the spend is the strongest honesty
  pattern available in this architecture, and it is unit-testable
  door by door, as the branch's tests do.
- **C-L1 — Enforcement lives in exactly one caller; spec the
  invariant or move the guard.** `MonolithRepository.startPeekLease`
  itself performs no tier/cooldown/liveness check — any future
  caller grants unconditionally. Today the VM is the only caller
  and it re-derives. Either is defensible; silence is not. Price of
  the strong form: a repository-level guard re-checking tier +
  cooldown at the write (~15 LOC + tests), making the lease write
  itself refuse. Recommended; not a blocker for this pass.
  Related bounded residual (wave-2's brutal critic, concurred):
  the check reads Flow snapshots (`apps.value`,
  `activeSession.value`) plus one fresh suspend cooldown read —
  mixed freshness, and liveness is emission-bound (the shield
  heartbeat is the normal emission source). The failure direction
  is refusal or a lease granted against a session that died within
  the same emission gap; the lease is 120s and self-expiring, so
  the blast radius is one peek. Recorded, accepted, not hidden.
- **C-L2 — The cooldown anchor semantics must be spec text, because
  FIX-8's price sentence depends on them.** `startPeekLease`
  writes `activePeekUntil = now + 120s` and pre-schedules
  `lastPeekEnd = leaseEnd`, so during a live lease the cooldown
  derivation (`max(lastPeekEnd, activePeekUntil) + 20min − now`)
  reads lease-remainder + cooldown. Two consequences the spec
  must pin: (i) the withheld precedence — a live lease on *this*
  package outranks its own cooldown (`ALREADY_ACTIVE` before
  `COOLDOWN`), or the door would print a cooldown that is really
  the lease; (ii) early end re-anchors via `recordPeekEnded`, and
  if that call were ever dropped the cooldown would run from the
  *scheduled* end — the conservative direction, but then FIX-8's
  re-timed sentence would overstate the wait. The sentence and
  the anchor are one fact; spec them together.

## WHAT THIS SEAT KILLS

- Any re-spec of FIX-6 that keeps per-branch zone heights (110/34/0)
  without the unified peek-owing zone + bottom anchor — it re-opens
  the 55dp class of defect through the FIX-5c flip. (C-6a)
- Any claim, in spec or wording book, that the arrival BACK action
  *guarantees* the blocked app is hidden. (C-7a)
- Any taper/memory spec that calls the session count exact without
  the trigger-time-query qualification. (C-8a)
- `peekCooldownPrint(0)` returning "AGAIN IN 0 MIN" as a pure
  function (wave-2 remainder, concurred): the law currently holds
  only via the composable's `cooldownSecs > 0` guard. Harden the
  function itself (empty string / require > 0) at the next touch —
  a truth law that lives in one caller's guard is one refactor
  away from printing.

## ACCEPTANCE CONDITIONS (for the builder's DECLARED close)

1. Stationarity frame set includes the FIX-5c branch-flip frames
   (withheld → offer, withheld → resolving) and the wave-2 variable
   content frames (memory line, errand sub-line, homecoming), with
   RETURN bounds pixel-equal across all of them.
2. Skeleton envelope pass owed on device: fontScale 1.0 and 1.3,
   narrowest supported width — price line unclipped. (C-6b)
3. Taper tests pin the pure function (present) *and* state the
   count-freshness qualification in the spec text. (C-8a)
4. Honest-exit test asserts route-not-spend: footer tap fires the
   end-session handler, books nothing, and the door state survives
   a dismissed Disarm face. (Present in wave-2's repaired tripwires;
   keep.)
5. Cooldown-at-0 / RESOLVING frame test — already carried as a
   wave-2 remainder to batch-10 proof obligations; this seat
   seconds carrying it by name.

**Verdict, one line:** build FIX-3…FIX-8 — with C-5a/C-5b, C-6a/C-6b/
C-6c, C-7a, C-8a/C-8b and C-L1/C-L2 folded into the spec text first;
none of them changes a ruling, a price, or a face — they close the
gaps between what the spec says and what the platform actually does.

— Implementation-Quality (Anchor seat Q-5, issue #11)
