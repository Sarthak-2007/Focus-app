# CRITIC CHARGES — Held Door (issue #11, Tor's cell)

Critic seat, Tor's cell. Scope unrestricted; every charge below carries
file + line proof read in the current code. Nobody compiled (Ren's law) —
all verdicts are paper verdicts, and say so where that matters.

Trees read:
- FIX-1/FIX-2 verification tree: `builds/merge-batch9/Focus-app`
- FIX-3…FIX-8 + new-hunt tree: `builds/cells/issue-11/` (repo root is the
  app; audit at `studio/plan/AUDIT-held-door.md`, byte-identical to the
  copy in `builds/wt-ci`)
- Identity check that binds the two halves of this report:
  `diff -rq cells/issue-11/app/src/main merge-batch9/Focus-app/app/src/main`
  → identical except an empty `assets/` dir present only in merge-batch9.
  Every main-source line cited below is the same line in both trees.

---

## §1 — FIX-1 (S1, door does not compile): CURED ON PAPER

The audit's three defects (AUDIT-held-door.md, FIX-1) are all absent in
the merged tree, and the cure follows the audit's spec in substance:

1. `leaseLive` is now declared, not dangling.
   `MonolithAccessibilityService.kt:225-228` (merged tree):
   ```kotlin
   val leaseRow = dao.getAllAppsSnapshot().find { it.packageName == pkgName }
   val leaseLive = leaseRow != null &&
       leaseRow.resolvedTier == AppBlockTier.PEEK_2M &&
       SystemClock.elapsedRealtime() < leaseRow.activePeekUntilElapsedRealtime
   ```
   Every symbol resolves: `getAllAppsSnapshot` MonolithDao.kt:17,
   `activePeekUntilElapsedRealtime` MonolithModels.kt:241,
   `resolvedTier` MonolithModels.kt:245, `AppBlockTier` imported at
   MonolithAccessibilityService.kt:11.
2. `launchInterceptScreen` (def. :416-431) still requires `path` with no
   default, and **all four** callers now pass one: guard :208-213 passes
   `InterceptPath.UNINSTALL_GUARD`, sniper :234-239 `SNIPER`, site
   :262-270 `SITE`, app-tier :288-294 `APP_TIER`. The fifth door is not a
   caller of this function at all: MonolithShieldService.kt:188 writes
   `EXTRA_INTERCEPT_PATH = InterceptPath.SHIELD_APP.name` into its own
   intent. No callerless required parameter remains in the tree
   (`grep launchInterceptScreen(` → 4 calls + 1 definition).
3. The lease now silences the two doors that fired blind: sniper gated
   at :231 (`if (!leaseLive && shortsSniperArmed(...))`), site blocker
   at :254 (`if (!leaseLive && ...)`).

**One deviation from the audit's letter, filed as an acquittal, not a
charge:** the audit specced `leaseLive` as "a live peek lease
(`activePeekUntilElapsedRealtime > now`); no row → false". The shipped
derivation adds `resolvedTier == PEEK_2M`. That is stricter and more
correct: under the audit's literal spec, a row re-tiered to LOCKED with
a stale lease timestamp still in it would silence the sniper and the
site blocker for a lease nobody is owed. Tor should ratify the
deviation so the next audit doesn't "fix" it back.

**Rider (H-11 still binds):** this is a static cure. I traced every
identifier in the file to a declaration, but nobody has compiled this
tree in my hearing, and I am forbidden to. "The door does X" still
carries *as written, on paper* until Ren's compile lands.

## §2 — FIX-2 (S1, SESSION ENDS reads the wrong clock): CURED

Both halves of the cure the audit demanded are present, in the same
pass, so the two faces cannot disagree:

1. The door: `MonolithViewModel.kt:2368-2370` —
   `sessionEndsAtMillis = active?.takeIf { !it.isStopwatchMode }?.let {
   System.currentTimeMillis() + it.spanRemainingSeconds * 1000L }`,
   with the comment at :2362-2367 naming Tor FIX-2 explicitly. The
   per-block `remainingSeconds` the audit charged (old VM :2364-2366)
   is gone from this derivation.
2. The Deck's TIME cell — the second site the audit ordered cured in
   the same pass: `TactileDeckScreen.kt:525` —
   `endsAtText(nowFrame + activeSession.spanRemainingSeconds * 1000L)`.
   Same derivation object (`spanRemainingSeconds`,
   MonolithModels.kt:430-431 = `spanTargetSeconds - spanElapsedSeconds`,
   floored at 0), so door and Deck print the same instant by
   construction.
3. The Safe Break symptom is cured by the same derivation:
   `spanElapsedSeconds` (MonolithModels.kt:416-428) counts an in-break
   session as `currentBlockIndex * effectiveTargetSeconds +
   breakTakenSeconds + (SAFE_BREAK - safeBreakSecondsRemaining)`, so
   span remaining is never 0 merely because the per-block remainder is.
   The sniper/site doors firing inside a break can no longer print
   "SESSION ENDS {now}".

**Examined and acquitted (not a residual charge):** TactileDeckScreen
:312 and :339 still read per-block `remainingSeconds`. Those are the
hero dial's block readout and its progress fraction — a surface *with*
block context, pricing the current block, which is exactly the
distinction the model's own span law draws (MonolithModels.kt:386-395).
The audit charged only end-instant prints; both of those now read the
span. No charge.

---

## §3 — FIX-3 … FIX-8 against current code: ALL STAND

None of FIX-3…FIX-8 has been implemented in the cell clone (or, per the
identity check, in merge-batch9). Verdicts per fix, with the current
evidence and the places where the audit's own text needs correction:

### FIX-3 (S1, ledger pack) — STANDS EXACTLY, all five sub-charges

- (a) MUSCLE_MEMORY default alive twice over: state default
  `MonolithViewModel.kt:159` (`selectedReason = MUSCLE_MEMORY`) and
  stamped into every fresh door at :2355. It is booked on both exits:
  peek grant :2413 and RETURN :2438 both pass
  `triggerReason = state.selectedReason.shortTag` — and MUSCLE_MEMORY's
  shortTag is literally `"Habit"` (MonolithModels.kt:201). The
  repository's own default also lives: MonolithRepository.kt:2109
  `triggerReason: String = "Habit"`, and a third copy the audit didn't
  cite, the entity default MonolithModels.kt:459
  `val triggerReason: String = "Habit"`. Three defaults, one lie.
- (b) Dead wires alive: overlay parameters `onSelectReason` /
  `onTransmuteParticleTap` (AirlockAndDisarmOverlays.kt:551-552) are
  accepted and never invoked anywhere in the overlay body; they are
  wired at MainActivity.kt:795-796 to `selectDistractionReason`
  (MonolithViewModel.kt:2376-2379) and `incrementAirlockParticlePulse`
  (:2443-2447), whose state fields (`selectedReason`,
  `transmutedParticles`, VM :159-160) no composable reads.
  `coachingTip` strings (MonolithModels.kt:203, 208, 213, 218) are read
  by no code anywhere (`grep coachingTip` outside the enum → 0 hits):
  counsel written for a picker that was never composed, exactly as
  charged.
- (c) RETURN books the sequel, not the tap: MonolithViewModel.kt:2437
  `outcome = "Stayed Focused"`. Stands verbatim.
- (d) Blank-package fallback books TikTok: MonolithViewModel.kt:2434
  `state.interceptedPackageName.ifBlank { "com.zhiliaoapp.musically" }`.
  Stands verbatim — and see N-1: there is a second, uncited instance.
- (e) Intention fallback to the composer's draft:
  MonolithViewModel.kt:2354
  `sessionIntention = active?.intention ?: _intentionText.value`, and
  re-applied at booking time at :2411 and :2436
  (`state.sessionIntention.ifBlank { _intentionText.value }`). Stands.

### FIX-4 (S2, repeat intercepts can miss the door) — STANDS EXACTLY

MainActivity.kt:121-124: `onNewIntent` does `setIntent(intent)` and
nothing else. The only consumption of the intercept extras is
`handleIncomingInterceptIntent`, called once from
`LaunchedEffect(Unit)` inside `onCreate`'s `setContent`
(MainActivity.kt:99-101). A second intercept against the live
singleTop activity (both services set `FLAG_ACTIVITY_SINGLE_TOP`:
MonolithAccessibilityService.kt:425, MonolithShieldService.kt:~184)
is never routed to the door. Not one line has moved.

### FIX-5 (S2, cooldown truth pack) — STANDS EXACTLY, (a)(b)(c) all live

- (a) The floor: MonolithRepository.kt:1180-1181 —
  `remainingMs = (anchorEndMono + PEEK_COOLDOWN_MILLIS - nowMono)...;
  return if (remainingMs > 0L) (remainingMs / 1000L).toInt() else 0`.
  Floor in the repository, ceiling in the print
  (`peekCooldownPrint`, AirlockAndDisarmOverlays.kt:233-236): a true
  61 s remaining floors to 60, then prints "AGAIN IN 1 MIN" — understated
  by a minute, the one direction this face swears it never goes. The
  unit test even enshrines the print half in isolation
  (TruthCar3InterceptTest.kt:213-218 asserts `peekCooldownPrint(61)`
  = "AGAIN IN 2 MIN") while no test feeds it the repository's floored
  output. The composition of the two correct-looking halves is the lie.
- (b) The overlay re-ticks instead of deriving:
  AirlockAndDisarmOverlays.kt:577-586 — `cooldownSecs` is `remember`ed
  from the state's snapshot and decremented by a `delay(1000L)` loop.
  The VM's own derived-from-anchor doctrine exists in the same file
  (`peekSecondsRemaining`, MonolithViewModel.kt:180-188) and the door
  doesn't use it. Note the overlay's own comment at :574-576 claims
  "the print never quotes a number minutes stale" — see N-3.
- (c) "AGAIN IN 0 MIN" is representable *today*, proof by construction:
  the local ticker stops at 0 (:582 `while (cooldownSecs > 0)`),
  `state.canUse2MinutePeek` and `state.peekWithheld` are trigger-time
  snapshots that nothing re-derives while the door stands, so at
  ticker-0 the withheld branch (:690-698) renders
  `peekCooldownPrint(0)` = `"PEEK COOLDOWN — AGAIN IN 0 MIN"`
  ((0+59)/60 = 0) and renders it forever. No expiry re-derivation
  exists anywhere in the overlay. Stands, and it is not an edge case:
  it is the *normal* end state of every cooldown door that stands
  longer than its own cooldown.
- The F-1 twin (5-second wall as a free-running loop) also stands:
  AirlockAndDisarmOverlays.kt:562-571, literal `5`, `delay(1000L)`,
  decrement. Baseline law 5's bare literal is still bare.

### FIX-6 (S2, composition pack) — STANDS; two corrections to the audit

Every element verified unimplemented:
- No fixed skeleton: the peek/withheld zone is composed only when
  `countdownSecs == 0` (AirlockAndDisarmOverlays.kt:663, :690) inside a
  centre-aligned Column (`contentAlignment = Alignment.Center`, :594;
  `Arrangement.spacedBy(16.dp)`, :604) — nothing is reserved from frame one.
- The numeral is still the hero: 88sp Anonymous Pro,
  AirlockAndDisarmOverlays.kt:635-641, uncaptioned — it gates the peek
  only, and nothing on the face says so (B-8 stands).
- Eyebrow: `"AIRLOCK INTERCEPTION • ${badge}"` still printed,
  AirlockAndDisarmOverlays.kt:607.
- Intention prefix: `"ACTIVE INTENTION: ${...}"`, :648.
- Touch sizes: RETURN and PEEK are both `BlockitActionChip`, whose
  height is fixed at **42dp** (MonolithCustomComponents.kt:201) —
  neither the 56dp RETURN slab nor the 48dp PEEK chip exists.
- Ink: RETURN's label colour is the chip's accent-armed foreground
  `Color(0xFFFBF1EC)` (MonolithCustomComponents.kt:193) on
  `NightColorPrimaryContainer = Color(0xFFF47442)` (Color.kt:39) —
  the audit's 2.55:1 cream-on-vermilion stands as the lowest contrast
  on the face, on the one word he must hit. The FIX-6 cure colour
  (#471602 on-container) appears nowhere.
- Border alpha 0.2 (AirlockAndDisarmOverlays.kt:601), body-line alpha
  0.8 (:630, :651, :681, :693-708), no centre text alignment on the
  wrapped lines, name Text at :615-621 has **no `maxLines` and no
  ellipsis** (F-9 stands).
- Name slot: guard door still prints the fiction "System Settings
  Guard" as the app name (MonolithAccessibilityService.kt:210);
  site door still prints the matched *domain* in the name slot
  (:266 `appName = matchedDomain`) with no browser name anywhere.

**Correction 1 to the audit — the "Locked" fallback is FOUR sites, not
three.** FIX-6 says "at all three sites (C-10)". The literal occurs at
MainActivity.kt:133, MonolithShieldService.kt:185,
MonolithViewModel.kt:157 (state default) **and** MonolithViewModel
.kt:2318 (trigger default parameter). A cure that sweeps three leaves
the fourth printing. (The tier's own badge for LOCKED is already
"Blocked", MonolithModels.kt:21 — the fallbacks are also off-vocabulary,
not just off-word.)

**Correction 2 to the audit — F-2's jump figure is branch-dependent.**
The audit prices the countdown-0 growth at "≈110dp, RETURN jumped
55dp". That is the *offer* branch (chip 42dp + 16dp spacing + price
line ≈36-54dp wrapped). The *withheld* branch grows the card by one
12.5sp line + spacing ≈34dp, moving RETURN ≈17dp. Both are the same
defect — a control moving under a travelling thumb — but a builder
verifying "the 55dp jump" on a cooldown door will measure 17dp and
may close the charge as unreproducible. Price both branches in the fix.

### FIX-7 (S2, arrival seam) — STANDS

- (a) BACK parity still false on both main doors. The accessibility
  service performs `GLOBAL_ACTION_BACK` before the guard (:207),
  sniper (:233) and site (:261) launches — and **not** before the
  app-tier launch (:288-294, no call). MonolithShieldService.kt:177-192
  launches its intercept intent with no equivalent at all; the blocked
  app stands visible behind/through the launch on both doors. W-32's
  acquittal premise remains false, as charged.
- (b) Entry is still `fadeIn(MonolithCurves.specB())`
  (AirlockAndDisarmOverlays.kt:559) and specB is 420ms
  (MonolithCurves.kt:69-70). Exit is specA 260ms (:560). The 180ms
  cure is absent.
- (c) Arrival voice still the switch tick: trigger plays
  `playMechanicalLatch(true)` (MonolithViewModel.kt:2321), whose
  transient is 760Hz / **16ms** / gain 0.54 / chordRatio 1.2
  (TactileSoundEngine.kt:697-702). The seal recipe it was charged
  against (360/480Hz, 48ms, 0.62, 1.5 — :721-726) still carries the
  visit's heaviest sound on the *goodbye* (`transmuteImpulseAndReturn`
  and `start2MinutePeek` both play it). The door-pitch cure
  (300Hz/44ms/0.60/1.5) appears nowhere.

### FIX-8 (S3, names, prices, memory) — STANDS in every clause

- "EMERGENCY PEEK" still the chip's word: AirlockAndDisarmOverlays
  .kt:665 — one string in the tree, exactly where the audit left it.
- Price sentence still mis-timed: `peekPriceText`
  (AirlockAndDisarmOverlays.kt:265-269) prints "TAKING THE PEEK STARTS
  A $minutes MINUTES COOLDOWN ON THIS APP". The machinery anchors the
  cooldown at the lease's *end*: `startPeekLease` pre-schedules
  `lastPeekEndElapsedRealtime = leaseEndMono` (MonolithRepository
  .kt:1194-1204) and `recordPeekEnded` re-anchors at the actual end
  (:1208-1216); the remaining-time derivation (:1176-1181) counts from
  that end anchor. From the tap, the exclusion is lease + cooldown;
  the sentence still says the cooldown starts at the tap.
- Badge truncation: `peekBadgeText` (AirlockAndDisarmOverlays.kt:257)
  is `"${durationSeconds / 60}m"` — 90s → "1m", as charged. The only
  tests assert whole minutes (TruthCar3InterceptTest.kt:226-227).
- `canUse2MinutePeek` state default still `true`:
  MonolithViewModel.kt:161.
- The taper: absent. The wall is the bare literal 5
  (AirlockAndDisarmOverlays.kt:563-566); `AirlockInterceptState`
  carries no intercept count; no "THIRD TIME TONIGHT"-family print
  exists anywhere (`grep "THIRD TIME"` → 0 hits).
- The errand's kernel: absent. No next-break instant is printed on any
  door (`grep nextBreak` in overlay/VM → 0 hits).
- The honest exit: absent. No "END THE SESSION INSTEAD" string exists
  in the tree; the overlay's only exits are RETURN and the peek.

---

## §4 — NEW CHARGES (what the audit missed)

### N-1 (S3) — A second blank-package fabrication + unstated reason, in a dead VM function the audit never opened

`MonolithViewModel.kt:1966-1983`, `splitActiveTaskAndReturn`: no caller
exists anywhere in `app/src/main` (`grep splitActiveTaskAndReturn` →
the definition only), yet it is a fully-armed booking function: it
closes the airlock, books `recordIntercept` with
`packageName = state.interceptedPackageName.ifBlank {
"com.google.android.youtube" }` (:1975) — FIX-3(d)'s disease with
YouTube substituted for TikTok — and `triggerReason =
DistractionTriggerReason.STUCK_ON_PROBLEM.shortTag` (:1979), a reason
("Stuck") booked from the function's name, never stated by the man.
The audit swept the face's dead wires (FIX-3b) and the RETURN path's
fallback (FIX-3d) and missed the sibling one screen up. If anyone
wires this function tomorrow, FIX-3 re-opens through the side door.
Cure: it dies with the FIX-3 sweep, or its fallbacks and reason die
before it is ever wired.

### N-2 (S2) — The door's offer gate is session-blind: it can sell a peek for a session that no longer exists, and book it

The audit verified the offer matrix against path, tier, cooldown and
pill (acquitted, and I re-verified `peekOfferFor`,
MonolithViewModel.kt:141-152 — those four inputs are honestly
checked). It never asked about the fifth input: **whether a session
is alive at all.**
- `triggerAirlockIntercept` snapshots `sessionEndsAtMillis` once
  (VM :2368-2370); nothing in the overlay re-derives or expires it.
  A door that stands past its session's end (the shield service ends
  sessions on its own heartbeat while Monolith is foreground) keeps
  printing "SESSION ENDS {a past instant}" — an end time in the past,
  stated as the future, on the face whose first law is the end instant.
- Worse, the *grant* is session-blind: `start2MinutePeek`'s only gate
  is `peekOfferFor(...)` re-derivation (VM :2393), which has no
  session parameter. Tapped after the session died, it still calls
  `repository.startPeekLease(pkg)` — which pre-schedules
  `lastPeekEndElapsedRealtime = leaseEnd` (Repository :1194-1204) —
  and books `recordIntercept(..., outcome = "Used 2m Peek")`
  (VM :2408-2414) into the ledger. Consequences that outlive the dead
  session: the booked cooldown anchor means the *next* session, if
  sealed within ~22 minutes, inherits a cooldown for a peek taken in
  no session at all; and the ledger gains a peek nobody was sealed
  against. The lease itself is inert without a session (every door
  early-returns on no active session), which bounds the charge at S2,
  but the booking and the cooldown theft are real, and the printed
  past end-instant is a Baseline-law-1 violation the audit's scorecard
  marked "OBEYED in form".

### N-3 (S3) — The overlay's own comment asserts the truth property its code doesn't have

AirlockAndDisarmOverlays.kt:574-576, over the cooldown ticker:
"seeded from the state's snapshot at open, counted down here — **the
print never quotes a number minutes stale**." The implementation
(:577-586) is a local `delay(1000L)` decrement loop: it drifts with
every recomposition it skips, it never re-reads the stored anchor, and
per FIX-5(c) it terminates in a permanently stale "AGAIN IN 0 MIN".
In this codebase comments are load-bearing — the audit itself, and
half the derivations in these files, are specified in comment prose
that later agents treat as the spec. A comment claiming the derived-
value property for a self-ticking counter is how FIX-5(b) survives
its own fix: the next reader sees the claim, not the loop. Charge the
sentence alongside the code; the FIX-5 cure must rewrite both.

### N-4 (S3) — MainActivity fabricates the hero name: "Short-Form Feed"

MainActivity.kt:130-131: a missing name extra defaults to
`?: "Short-Form Feed"`, which the overlay then prints at 24sp as the
intercepted app's name (AirlockAndDisarmOverlays.kt:615-621). Every
current caller passes a name, so this is a loaded fallback, not a live
lie — the same classification FIX-3(d)/(e) received. The audit caught
the badge fallback two lines below it (MainActivity.kt:133,
"Locked") and missed this one. A guard or site intercept arriving
nameless would announce itself as a short-form feed. Cure with the
FIX-3 family: blank books blank — no name extra → no name line, or
the package's own resolved label, never a genre.

### Examined, not charged

- **Lease silences the sniper for the leased package**
  (MonolithAccessibilityService.kt:226-228, :231): during a YouTube
  peek lease, Shorts inside YouTube are not sniped. This is the
  comment's stated law ("a lease is honoured by every door or it is
  honoured by none", :221-224) and the exposure is bounded by the
  2-minute pill. It is a design decision with its reason written on
  it — criticising it would be taste, not proof. No charge.
- **Cooldown remaining during a live lease can read up to 22 min**
  (Repository :1176-1181, anchor = max(lastPeekEnd, activePeekUntil)):
  unreachable on the door — `peekOfferFor` requires the pill inactive
  and the doors are lease-silenced — so the inflated figure is never
  printed. No charge; recorded so a future change to either gate
  re-opens it knowingly.
- **Green:** no green (or near-green) literal was added anywhere in
  the Held Door files read. Nothing to police.

---

## §5 — Summary for Tor

| Fix | Verdict |
|---|---|
| FIX-1 | **Cured on paper** — static proof complete; compile proof still owed by Ren. One spec deviation (tier gate on `leaseLive`) recommended for ratification. |
| FIX-2 | **Cured** — both faces, same derivation. |
| FIX-3 | Stands exactly (all five). Third "Habit" default found at MonolithModels.kt:459. |
| FIX-4 | Stands exactly. |
| FIX-5 | Stands exactly (a)(b)(c); (c) is the normal end-state of a long-standing cooldown door, not an edge case. |
| FIX-6 | Stands; audit corrected on two points — "Locked" is four sites, and F-2's jump is ~55dp (offer) / ~17dp (withheld). |
| FIX-7 | Stands (a)(b)(c). |
| FIX-8 | Stands in every clause. |
| N-1 | New S3 — dead `splitActiveTaskAndReturn` carries FIX-3(d)'s disease + an unstated "Stuck". |
| N-2 | New S2 — offer/grant is session-blind; past end-instant print + out-of-session peek booking with a live cooldown anchor. |
| N-3 | New S3 — overlay comment claims the derived-value property for a self-ticking counter. |
| N-4 | New S3 — "Short-Form Feed" fabricated hero-name fallback, MainActivity.kt:131. |

The audit's headline survives contact with the current code in one
direction only: FIX-1 and FIX-2 — the two S1s the crew marked for the
merge — genuinely landed. Everything below them in the list is
exactly where the audit left it, and the door acquired none of the
audit's cures by accident. The wall still has good manners. It is
still a wall.

— Critic, Tor's cell, issue #11
