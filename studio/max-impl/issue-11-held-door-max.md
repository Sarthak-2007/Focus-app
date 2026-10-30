# MAX-IMPL — THE HELD DOOR AT PEAK INTERACTION PERFECTION (issue #11)

**Seat:** MIP-05, Maximum-Implementation-Potential · **Repo:** Sarthak-2007/Focus-app
**Branch:** `agent/issue-11` · **Status:** DECLARED, uncompiled — nobody compiles (standing law).
**Ground:** `studio/plan/AUDIT-held-door.md` (Tor's audit, FIX-1…FIX-8, Conflicts 1–6);
this branch's landed wave (Tor: FIX-3…FIX-8 + FIX-5d — fixed skeleton, taper, honest
exit, DECLARED); Schwelle's skeleton design (`studio/plan/DESIGN-held-door-skeleton-schwelle.md`);
the cell's feel bench (Atem — hold/countdown; Daumen — thumb/press; Ton — sound score;
Ruhe — exit moment; Druck — the man at the door); `TactileSoundEngine.kt`,
`AirlockAndDisarmOverlays.kt`, `MonolithViewModel.kt`, `MonolithRepository.kt` read whole.
FIX-1/FIX-2 were already cured in the batch-9 merge and are not re-opened.

**The Held Door Truth Baseline binds, verbatim, throughout:** (1) no remaining duration —
end instant only; (2) a withheld peek prints state AND reason; (3) a peek is offered only
where a lease can be honoured — `peekOfferFor` + grant-time re-derivation are acquitted by
six seats and this spec does not touch them; (4) doors that never owed a peek print no peek
line; (5) derived values, never literals. **The maximum below is a maximum of interaction
perfection standing on that honesty — it adds no offer, no discount, no negotiation.**

---

## 0. THE MAXIMUM IN ONE PARAGRAPH

The fullest buildable Held Door is a **fixed-geometry instrument with a complete feel
score**: a skeleton whose every slot height, gap, and screen-space address lives in one
typed contract object and is *proven stationary by test across every frame a visit can
produce* — wall-max, wall−1, wall−0, offer-armed, withheld, resolving, homecoming,
process-death restore, font-scale variants — so RETURN's centre sits 136dp above the
card's bottom edge in all of them and the audit's worst defect (RETURN jumping 55dp at
countdown 0, onto the peek chip, under a travelling thumb) becomes *unrepresentable, not
merely fixed*; a hold/lease core where every clock is an anchored monotonic end and every
print is a pure derivation from a stored anchor; an honest exit that routes — never
spends — to the Disarm face's banking 60-second valve; and, per Sarthak's standing order
that any proposed button must name its technique, **a named press technique for every
button on the face and its sibling valve, each with its exact vibration waveform
(timings + amplitudes), its exact sound recipe (pitch, duration, gain, chord) on the
engine's existing legacy lane, and its timing budget** — so "peak perfection" is a table
a builder can type and a test can check, not an adjective.

---

## 1. THE SKELETON GEOMETRY LAW (the maximum form)

The cell's skeleton (Schwelle, ratified by Tor, finished by Glanz) is adopted whole and
then hardened into law. The maximisation is not new geometry — it is turning the geometry
into a **contract the code cannot quietly break**.

### 1.1 One typed contract, no scattered literals

A single object is the only place the door's geometry exists:

```kotlin
object HeldDoorSkeleton {
    // Card
    val ScreenPadding = 20.dp;  val CardPadding = 24.dp; val CardRadius = 24.dp
    val BorderAlpha = 0.38f                       // was 0.20 (F-5)
    // Slots (top → bottom)
    val NameH = 32.dp;          // H1 — the face's one hero, Sora Bold 24sp, 1 line, ellipsis
    val GapNameIntention = 4.dp
    val IntentionH = 24.dp      // H2 — his words bare, 16sp, alpha 0.92; blank = empty slot, no print
    val GapIntentionSession = 4.dp
    val SessionBlockH = 40.dp   // H3 — 2 × 20dp lines (session end line; break appointment / domain)
    val GapSessionZone = 12.dp
    val PeekZoneH = 110.dp      // Z — present on EVERY peek-owing door; ABSENT (not blanked) otherwise
    val GapZoneReturn = 16.dp   // peek-owing visits only
    val ReturnH = 56.dp         // R — the slab
    val GapReturnExit = 12.dp
    val ExitRowH = 48.dp        // X — honest exit touch row
    val GapExitFooter = 8.dp
    val FooterH = 16.dp         // F — badge word alone; "AIRLOCK INTERCEPTION •" stays dead
    // Derived (asserted, never hand-written twice)
    val CardPeekOwing = 430.dp  // 24+32+4+24+4+40+12+110+16+56+12+48+8+16+24
    val CardNeverOwed = 304.dp  // 430 − 110 − 16
    val ReturnCentreFromCardBottom = 136.dp  // 24 pad + 16 F + 8 + 48 X + 12 + 28 half-slab
    val PeekCentreAboveReturnCentre = 130.dp // 28 + 16 + 18 Z3 + 4 + 34 Z2 + 6 + 24 half-Z1
}
```

Rules that make it a law, not a style:

- **L-G1 · Bottom anchor.** The card is bottom-anchored in screen space. Where zone Z is
  lawfully absent (never-owed doors), the card is 126dp shorter and grows *upward* into
  empty night. RETURN's address is defined in screen space because the thumb lives in
  screen space.
- **L-G2 · No collapsing spacing.** The card column carries **no `spacedBy`** anywhere.
  Collapsing gaps around absent children were the original defect's mechanism; every
  space on this face is an explicit fixed slot from the contract.
- **L-G3 · Contents may change; bounds may not.** Countdown state, cooldown resolution,
  a homecoming greeting, a third price line — all are *content* events inside fixed
  bounds (crossfade in place, §4). No state of the visit may add, remove, or resize a
  slot on a peek-owing door.
- **L-G4 · Stationarity is a test, not an intention.** The stationarity suite (§8, T-1)
  asserts RETURN's pixel bounds identical across the full frame list. A 1px move fails
  the build. This is the maximum form of FIX-6: the defect class is closed by proof.
- **L-G5 · Font-scale armour (the maximisation beyond the cell spec).** Stationarity is
  per-visit; configuration is chosen once, at frame one, before the fade. At
  `fontScale ≥ 1.3` the skeleton selects its **tall variant**: Z2 (price) 34dp → 51dp
  (3 lines), SessionBlockH 40dp → 60dp, card totals re-derived (481dp / 355dp) — RETURN's
  136dp centre-from-bottom is *unchanged*, because the growth is entirely above it.
  Within a visit the variant never switches. A price that wraps pushes nothing; it was
  already given its room before the man arrived.
- **L-G6 · The numeral stays dead as hero.** The wall prints only as the captioned gate
  line "PEEK OPENS IN {M:SS}" (Anonymous Pro Bold 15sp, tabular figures, zero width
  jitter) over a draining 2dp rule, inside Z1. It names what it gates. It never gated
  RETURN and must never look like it does.

### 1.2 Zone Z — the peek zone (110dp, fixed, five lawful tenants)

Internal stack: **Z1 48 + gap 6 + Z2 34 + gap 4 + Z3 18 = 110dp exactly.**

| Slot | Content | Timing law |
|---|---|---|
| **Z1 · ACTION (48dp)** | One slot, five tenants, crossfading *inside unchanged bounds* (180ms, specA/EasingA): **GATE** (gate line + draining rule, counting) · **CHIP** ("PEEK" + derived badge, armed) · **WITHHELD** (state + reason) · **RESOLVING** (empty frame at derived-0 while the offer re-derives) · **EMPTY** (nothing owed in this slot; the slot still stands) | GATE from frame one; CHIP only at wall 0; WITHHELD from **second 0** |
| **Z2 · PRICE (34dp)** | "WHEN THE PEEK ENDS, A {n} MINUTES COOLDOWN STARTS ON THIS APP" — re-timed per FIX-8/C-11: the cooldown anchors at the lease's *end*, so from the tap the exclusion is lease + cooldown, and the sentence says so. Derived `{n}` from `PEEK_COOLDOWN_MILLIS`, never a literal. Withheld branch: slot stands, prints nothing. | Standing from **frame one** on the offer branch — read *before* the temptation exists |
| **Z3 · MEMORY (18dp)** | The taper's count in the gate line's family — "THIRD TIME TONIGHT" (§2.3). Empty-held when there is no count; a homecoming prints none. | From frame one |

**Second-0 law (FIX-6/C-6):** withheld lines and the price print from the moment the door
opens. Only the offer chip waits for the wall. A known refusal is never withheld behind
the wall — withholding a refusal would be the door lying by delay.

**Badge honesty (FIX-8/F-8):** `peekBadgeText` renders m+s — 90s prints "1m30s", never
"1m". The current trunk function's `"${durationSeconds / 60}m"` truncation is cured as
part of this spec wherever it survives.

### 1.3 Ink law (FIX-6, carried at full strength)

- RETURN label: the scheme's own on-container **#471602 on the #F47442 container —
  5.37:1**, the strongest contrast on the face, on the one word he must hit. (The old
  cream-on-vermilion 2.55:1 — the face's lowest contrast — stays dead.)
- Body lines alpha **0.92** (was 0.80). Border alpha **0.38** (was 0.20). Wrapped lines
  centre-aligned. Name capped at one line with ellipsis.
- Night palette at all hours (Conflicts 3, acquitted): the host pins
  `effectiveNight = darkTheme || sealed`; the door matches the building it locks.
- Name slot honesty (FIX-6/C-8): the guard door prints what he opened (Settings / the
  installer), never the fiction "System Settings Guard"; the site door prints the
  browser name with the matched domain as sub-line; sniper names keep (the feed is the
  fact). Fallback badge word is "Blocked" at all three sites, never "Locked".

---

## 2. HOLD / LEASE MECHANICS (the maximum form)

Everything acquitted stays acquitted. The maximisation is: **every clock on this face is
an anchored end, every number a pure derivation, every transition re-derived** — so no
print on the door can ever disagree with the machinery it names.

### 2.1 The offer and the grant (untouched, restated as the floor)

- `peekOfferFor(path, entity, cooldownLeft, pillActive)` — pure; true only where
  `path.canHonourPeekLease` (APP_TIER / SHIELD_APP), the row exists, tier is PEEK_2M,
  cooldown is spent, no pill is active. A null entity never defaults ON.
  `canUse2MinutePeek`'s state default is **false** (FIX-8/Funke C-4) wherever a default
  survives.
- **Grant re-derivation:** `start2MinutePeek` re-runs `peekOfferFor` against the
  *current* row, cooldown, and pill at the tap. A stale door state can never sell a
  lease. A refused grant leaves the door standing and books nothing.

### 2.2 Anchored clocks only (FIX-5 completed to its maximum)

- **The wall:** state carries `wallEndsAtMono` (elapsedRealtime) + `wallTotalSeconds`.
  The printed gate seconds derive at each recomposition:
  `ceil((wallEndsAtMono − nowMono) / 1000)`. The ticker is a **redraw trigger only**
  (50ms cadence for the draining rule's smoothness — Tor wave-2 part 4), never a counter.
  No free-running `delay(1000)` loop survives anywhere on this face — both the shipped
  countdown loop and the shipped cooldown loop in the trunk overlay are the defect.
- **The cooldown:** the repository returns the **ceiling** form
  `((remainingMs + 999) / 1000)`, matching `peekSecondsRemaining` — the trunk's floor
  (`remainingMs / 1000`, REPO :1180) understates, the one direction this face swears it
  never goes. The overlay derives from the stored anchor (`lastPeekEnd` /
  `activePeekUntil` + `PEEK_COOLDOWN_MILLIS`) at each recomposition; it never re-ticks a
  local copy.
- **Expiry resolves (FIX-5c):** at derived 0 the face re-runs the offer derivation. The
  peek stands there, or nothing prints. "AGAIN IN 0 MIN" is unrepresentable: the
  RESOLVING tenant occupies Z1 for the derivation's duration, inside fixed bounds.
- **The lease:** `startPeekLease` writes `activePeekUntilElapsedRealtime =
  nowMono + PEEK_DURATION_MILLIS` and pre-anchors `lastPeekEndElapsedRealtime` to the
  lease end, so the 20-minute cooldown is armed in Room at grant, not at expiry. Early
  end (`recordPeekEnded`) re-anchors the cooldown to the actual end instant. The
  floating pill's every print derives from the stored lease end via
  `peekSecondsRemaining` — the pill already obeys this doctrine; the door now matches it.
- **Session line:** span derivation only (FIX-2, cured in batch-9; restated so the max
  spec is self-contained): `sessionEndsAtMillis = now + spanRemainingSeconds × 1000`,
  the model's own span law — never per-block remaining, which announced the session over
  at the first block boundary and "ends now" through every Safe Break. Stopwatch →
  "SESSION RUNNING · NO SET END". No session → no line. **Never a remaining duration**
  (Baseline law 1).

### 2.3 The taper — the door's memory (FIX-8, Funke P-1 as amended)

- Wall seconds = `5 + this session's booked intercepts of this package`, **capped at
  10**, derived by the pure function `taperWallSeconds(priorBookedThisSession,
  justExpiredLease)` (cell branch). The count source is the session's booked intercept
  rows for the package — **never** `BlockedAppEntity.interceptCount`, which is a lifetime
  counter and would make the wall a life sentence instead of a night's memory.
- The count **prints** (Z3: "THIRD TIME TONIGHT"). The rule is never silent about
  itself. Never random, never purchasable, never a negotiation.
- The taper gates **only the peek offer's appearance** — never RETURN, never the
  withheld lines, never the honest exit.
- A homecoming (arrival on a just-expired lease of this same package) skips the taper's
  increment for its own expiry: a lease ending is not a reach, and the door does not
  charge a man for coming back through it.

### 2.4 The break appointment (FIX-8, the Errand's kernel)

On PEEK-tier doors with a next Safe Break in the session's own schedule, H3 line 2 may
print the appointment the session already guarantees — the break's instant, derived from
the schedule, end-instant form only. No-break sessions print nothing. No new machinery:
for PEEK apps the break already opens them; for LOCKED apps nothing prints (a
pre-honoured break lease would break the tier law — the Errand as machinery stays
struck, exactly as the audit ruled).

### 2.5 Arrival integrity (FIX-4 + FIX-7a, at maximum)

- **Repeat intercepts (FIX-4):** `onNewIntent` consumes the intercept extras through the
  same `handleIncomingInterceptIntent` route as first composition. A second intercept
  against a live `singleTop` activity can never land him on plain Monolith or on the
  previous app's stale door (wrong name, wrong badge, wrong peek state).
- **BACK parity (FIX-7a):** every path — app-tier, shield, sniper, site, guard —
  performs `GLOBAL_ACTION_BACK` *before* launching the door. The blocked app must not
  stand visible, reward delivered, through launch latency. (The wording book's A-08
  premise was false on both main doors; this is the cure, not a note.)
- **The ledger (FIX-3, restated as binding floor):** intercepts book reason **UNSTATED**
  unless a man actually states one — the MUSCLE_MEMORY default and the repository's
  "Habit" default stay dead; the dead reason-picker wires stay deleted; RETURN books
  **"RETURNED AT DOOR"** (the tap is observed; whether he stayed focused is bookable at
  session end, not at the door); blank package books blank (the TikTok fallback stays
  dead); no armed session in state → blank intention → no line.

---

## 3. THE HONEST EXIT (FIX-8 / Halbwach I-4, at maximum)

> **"END THE SESSION INSTEAD"** — one quiet footer line under RETURN (slot X, 48dp touch
> row, bare text, no fill). **A route, not a spend.** The door itself ends nothing,
> spends nothing, books nothing. A door with an honest exit is a door a man stops
> resenting.

- **Routing:** the tap routes to the existing `DisarmChallengeOverlay`. The Disarm
  face's own gravity — its consequence sentence, worded by the live token count — is
  where the weight lives. The count is **derived live from the repository's spend
  ledger** for as long as the overlay is visible (Amendment A: no count is ever copied
  into state at open; a snapshot can lie for the visit's whole life).
- **The valve (the sibling button, specced in §4.5):** the weekly emergency break costs
  one **continuous 60-second hold** on `Emergency60sValveView`, **banking at 15 / 30 /
  45** (Sarthak's call, Oct 29): crossing each chamber locks it; on release the
  un-banked remainder holds through a grace, then drains at 1.0× for 3s and 2.0× after —
  **never below the last bank**; re-gripping halts the drain instantly and accrual
  resumes after a 250ms re-seat. **The token spends only at the full 60** — 59 banked
  seconds spend nothing, and abandoning the valve at any point spends nothing.
- **The way back:** KEEP FOCUSING on the Disarm face returns to the door **with the
  visit preserved** — the wall does not re-run (the cell's cure: on return the wall's
  anchored end collapses to now if the offer can arm, so a man is never charged a second
  wall for having looked at the exit), the taper does not re-count the visit, and no
  second arrival sound plays. Looking at the exit is free. That is what makes it honest.
- **Spent face:** at 0 tokens remaining the valve is **not composed at all**
  (Ledger-Direct) and the Disarm face renders its spent sentence in its place. There is
  no disabled valve to poke at.

---

## 4. THE INTERACTION-DETAIL TREATMENT — EVERY BUTTON, NAMED TECHNIQUE

Sarthak's order, applied without exception: *when any agent proposes a button, the
maximum version names the technique — press depth / 3D treatment, exact vibration
pattern, sound, timing.* Every act on this face — and the sibling valve it routes to —
is specced below to that standard. All sounds ride the engine's **existing legacy lane**
(`enqueueTransient` → 4-partial float-PCM synth, weights {0.6, 0.28, 0.37, 2.2}, dual
decay, clamp ±0.95). **No door sound may use the Seal Throw's lane**, prime it, or purge
for it — the Throw is the seal's alone. All door sounds fire **from the ViewModel at the
state change** (Ton's L-5), never from a composable: a sound fired from recomposition
can double-fire; a sound fired at a transition cannot.

**House laws for the whole score (Ton L-1…L-4, adopted):**
- **One door, one pitch:** every door sound sits on the door pitch **300Hz**, except the
  exit-to-Disarm step-down to **240Hz**. Identity is carried by duration, gain, chord,
  and haptic shape — never a new pitch to learn at 1 AM.
- **The door is never the loudest thing in the app:** ceiling gain **0.60** (the seal's
  legacy recipe is 0.62, completion/tear 0.68, the Throw outweighs all on energy). No
  door haptic spends amplitude **255** — full force stays the seal's alone; the door's
  strongest segment is **215**.
- **Sound never grades the man:** the arrival beat is identical for every path, tier,
  and count of tonight's returns. The taper may lengthen the wall; it may not lengthen,
  deepen, or multiply the knock.
- **Silence is a specced beat:** every silence below names both channels, its span, and
  its reason.

### 4.0 The shared press technique — "KEYCAP DEEPENING" (names the 3D treatment)

Every tappable surface on this face uses one named technique, Daumen's §6, maximised:

> **KEYCAP DEEPENING** — the surface answers on **pointer-down**, inside its own
> unchanged bounds, the way a mechanical key's cap darkens against its bezel:
> (a) the fill steps one value toward its pole (accent slab presses *lighter*,
> neutral chip presses *darker* — so which slab is under the thumb is legible from its
> edge alone, without reading); (b) the border **closes 1dp → 2dp, drawn inside the
> bounds**, at full-strength ink — the bezel closing around the key; (c) the label does
> not move, scale, or change. **No translation, no scale, no ripple, no glow, no shadow
> bloom** — a surface that moves under a press has broken the address law at the one
> moment the address matters most: mid-aim.
> Timing: deepening reached within **90ms** of pointer-down on EasingA (the house
> `MonolithKin.PRESS_MS` vocabulary); release/cancel returns within **150ms**
> (`PRESS_BACK_MS`). Implementation: explicit `MutableInteractionSource` +
> `collectIsPressedAsState`, `indication = null`.
> Multi-touch law: the first pointer-down owns the press; other pointers change
> nothing. Commit/cancel is decided solely by the owning pointer's up-position. Two
> slabs can never chord: whichever owning pointer lifts inside its bounds first fires,
> exactly once, and the face's exit ends the visit before a second commit can land.
> No hover, long-press, or drag-in behaviour exists on this face.

### 4.1 ARRIVAL (not a button — the beat every button inherits)

| | Spec |
|---|---|
| **Visual** | Blocked app removed first (BACK parity, §2.5); full skeleton composed at final geometry *under* a fadeIn **180ms specA** (EasingA) — opacity only, no growth-in, no 420ms ghost. Exit fadeOut **260ms specA**. |
| **Sound** | The seal's recipe at door pitch (FIX-7c): **300Hz, 44ms, gain 0.60, chordRatio 1.5** — the binding chord, because the door is the seal encountered from the other side. (Today a 16ms switch tick announces the door while the goodbye carries the visit's heaviest sound; that inversion stays dead.) |
| **Vibration** | Latch waveform, kept from FIX-7c: timings **[0, 11, 26, 9]ms**, amplitudes **[0, 215, 0, 150]** — strike, gap, seat. Pre-API-26 fallback: one-shot 4ms. |
| **Timing** | Sound + haptic fire at the state transition in `triggerAirlockIntercept`, the same frame the state publishes — never after first composition. |

### 4.2 RETURN TO FOCUS — the slab (56dp, slot R)

| | Spec |
|---|---|
| **Technique** | KEYCAP DEEPENING on the face's only accent fill. Idle: fill #F47442, border 1dp cream α0.38, label #471602. **Pressed:** fill lightens one step to **#F57F51** (accent + 8% white; label contrast *rises* to ≈5.8:1 — the deepening never costs legibility), border closes to **2dp inside bounds**, #471602 full strength. |
| **Gesture** | **Tap.** Live and tappable **from frame one in every state** — never gated, never dimmed, never re-labelled, **no disabled state exists**. The free act is the easy act. |
| **Sound (commit)** | The seal's voice is **returned to the seal** (Ton §5.4): RETURN no longer borrows `playHarmonicSeal(arming = true)` at full weight. Door voice: **300Hz, 36ms, gain 0.54, chordRatio 1.5** via a new named engine function `playDoorReturn()` — binding chord (he is re-binding himself), door pitch, door ceiling. |
| **Vibration (commit)** | Timings **[0, 18, 30, 12]ms**, amplitudes **[0, 200, 90, 140]** — the disarming-seal's shape at door weight: a settling, not a slam. Fallback one-shot 8ms. |
| **Press feedback** | No sound and no haptic on press-down (the visit's sounds belong to arrivals and commits); the deepening *is* the acknowledgement. |
| **Cancel** | Pointer slides out or lifts outside: deepening releases within 150ms, **nothing fires, nothing books, the door stands**. The key visibly coming back up under a departing thumb is the cancellation's whole message. |
| **Commit effect** | Fires `onReturnToFocus` exactly once; input dead after first commit; books "RETURNED AT DOOR" + reason UNSTATED; face exits 260ms. |

### 4.3 PEEK — the chip (48dp, slot Z1 armed tenant)

| | Spec |
|---|---|
| **Technique** | KEYCAP DEEPENING, neutral pole. Idle: fill #4A423C (`NightColorSecondaryContainer`), border 1dp α0.38, label cream ≈8.1:1. **Pressed:** fill deepens one step to **#443D37** (+8% black; contrast rises to ≈8.8:1), border 2dp inside bounds, cream α0.92. Effective bounds = visual bounds exactly (48dp is the platform minimum met *exactly* — no expansion in any direction). |
| **Gesture** | **600ms HOLD-TO-FILL** (Schwelle §7 / Innovator P-3, adopted at maximum): press-and-hold fills the chip left→right inside its bounds over 600ms (fill = pressed-pole colour advancing over idle fill, redrawn on the 50ms ticker); release before full **drains the fill in 120ms and books nothing**. A travelling thumb *cannot* spend a peek: it can only tap, and a tap on this chip is a press that never completes. This is the gesture-pricing law made literal — RETURN costs a tap, PEEK costs a decision. |
| **Hold detents** | At fill 50%: one rotary detent — sound **300Hz, 6ms, gain 0.38, chord 1.0**; vibration timings **[0, 7, 5]ms**, amplitudes **[0, 180, 70]**. At fill 100% the grant fires (below). No other detents: a hold that ticks like a combination lock teaches counting, not deciding. |
| **Sound (grant)** | A binding in the door's voice (Ton §5.5): **300Hz, 44ms, gain 0.60, chordRatio 0.9** — the latch's *releasing* chord at door pitch: something is being let out, under licence. New named function `playDoorPeekGrant()`. (Today the peek sounds like every light switch in the app — `playMechanicalLatch(true)` at 760Hz; that sameness stays dead.) |
| **Vibration (grant)** | Timings **[0, 9, 22, 10]ms**, amplitudes **[0, 165, 0, 195]** — the releasing latch's shape: lighter strike, heavier seat. Fallback one-shot 4ms. |
| **Commit effect** | Grant re-derivation runs at the *completion of the hold* (§2.1) — the same instant the old tap fired it. Refusal at that instant (cooldown armed by another route mid-hold): fill drains in 120ms, the WITHHELD tenant crossfades into Z1 with state + reason, **no sound, no haptic** — a refusal is information, never a punishment buzz. Success: door exits 260ms, lease writes (§2.2), pill takes over. |
| **Not-armed state** | There is **no disabled chip and no ghost target**. During the wall, Z1 holds print (the gate stack); a tap inside Z1's bounds lands on dead print and does nothing — no feedback of any kind, because there is nothing there to press. |

### 4.4 END THE SESSION INSTEAD — the honest exit row (48dp, slot X)

| | Spec |
|---|---|
| **Technique** | **GHOST SLAB** (Daumen §6.3): bare text has no fill to deepen, so while pressed a #4A423C fill fades in behind the text at its effective bounds (90ms, corner 12dp, text unchanged); on release or slide-off it is gone within 150ms. A man must be able to *feel* he has touched the exit before he has taken it — a bare-text target with no pressed state is a trap disguised as a footnote, and this line of all lines must never fire unannounced-feeling. |
| **Gesture** | **Tap → route only.** It ends nothing, spends nothing, books nothing. |
| **Sound** | The step-down (Ton §5.6): **240Hz, 40ms, gain 0.50, chordRatio 1.21** — the tear pulse's chord family at door weight: paper, not machinery; a page turning toward a graver room. The one lawful exception to the 300Hz door pitch, because the destination is not the door's room. New named function `playDoorExitStep()`. |
| **Vibration** | Timings **[0, 8, 14, 8]ms**, amplitudes **[0, 160, 0, 110]** — the tear's ripping shape. Fallback one-shot 1ms. |
| **Timing** | The Disarm face fades in **280ms specC** over the door (its own entry law); the door state is preserved underneath for the way back (§3). No arrival sound on the Disarm face itself — the step-down was the announcement; two knocks for one door reads as alarm. |

### 4.5 The sibling buttons — Disarm face + the 60-second valve

These are not on the Held Door's face, but the honest exit routes to them, so the
maximum specs them to the same standard. (Their full audit is the Disarm face's own
seat; nothing here re-opens it.)

**KEEP FOCUSING (Disarm face, primary-ink chip):**
- Technique: KEYCAP DEEPENING, ink pole — ink fill deepens one step (+8% black), border
  closes 1→2dp inside bounds, 90ms / ≤150ms as §4.0.
- Sound: **240Hz, 36ms, gain 0.50, chordRatio 1.5** — the binding chord at the graver
  room's pitch: coming back from the exit's doorway is a re-binding, spoken one step
  down. Vibration: timings **[0, 18, 30, 12]ms**, amplitudes **[0, 200, 90, 140]**.
- Effect: dismisses to the preserved door visit (§3) — no new arrival beat, no re-run wall.

**Emergency60sValveView — the continuous hold (68dp AndroidView):**
- **Press:** pointer-down seats the valve: seat knock — sound via `playSeatKnock` at
  door-of-the-graver-room pitch **240Hz, duration 16ms, amplitude01 0.85**; the view's
  fill begins accruing from the banked floor, never from zero on a resumed visit.
- **Bank detents at 15 / 30 / 45s:** each chamber lock is a seat knock rising with the
  stake — `playSeatKnock(durationMs = 14 + chamberIndex × 2, amplitude01 = 0.80 +
  chamberIndex × 0.07, pitchHz = 240f)`: 15s → 14ms @ 0.80; 30s → 16ms @ 0.87; 45s →
  18ms @ 0.94. Vibration per bank: timings **[0, strike, strike/2]ms**, amplitudes
  **[0, 255·a, 89·a]** (the engine's seat-knock shape — the valve is machinery, and the
  255 ceiling law is the *door's*; the valve speaks with the seal's family because
  completing it spends what the seal protects. Stated plainly so the exception is a
  ruling, not a leak).
- **Release / drain:** on release, no sound and no haptic — the drain is watched, not
  announced; the print "BANKED {n}S • RESUME HOLD • …" states the floor. Re-grip halts
  the drain instantly (no beat), accrual resumes after the 250ms re-seat.
- **Completion at 60s:** the token spends, the session ends. Sound: this is a *disarm*,
  and it earns the real thing — `playHarmonicSeal(arming = false)` (480Hz, 48ms, gain
  0.62, chord 1.5; vibration [0, 18, 30, 12] / [0, 200, 90, 140]) fired from the VM's
  disarm completion, **not** the Seal Throw (the Throw is for seals, and this is the
  opposite act). Exactly one beat, at completion only — nothing at 59s, nothing
  celebratory: the valve's completion is a grave act and sounds like one.

### 4.6 The countdown's silence — and the two filed fragments, adjudicated at maximum

- **Seconds 5…1: SILENCE, both channels** (Ton §5.2, adopted). The countdown's silence
  is the longest deliberate sound this door makes. A per-second tick would make the wall
  a metronome the man waits *to* — attention rented to the temptation's clock.
- **Schliff's per-second detent: STRUCK at maximum**, for that reason — with one
  carve-out already in §4.3: detents belong to *his* act (the hold-to-fill), never to
  the door's waiting.
- **Schwelle's knock at HOLD 00: ADOPTED** as the arm beat — at wall 0, the instant the
  chip crossfades in: sound **300Hz, 6ms, gain 0.38, chord 1.0** (the rotary detent's
  recipe at door pitch) + vibration **[0, 7, 5] / [0, 180, 70]**. One knock, naming
  "the offer exists now" to a man whose eyes may be closed. It fires from the VM's
  wall-expiry arm function (the overlay's effect calls it at derived 0 — a state
  transition, Ton L-5 compliant), exactly once per visit, and **never on a withheld
  door** (there is nothing arming; a knock there would announce a refusal as an event).
- **Withheld doors:** no arm beat, no per-second anything. The refusal was printed at
  second 0; the door then simply stands.

### 4.7 Degradation matrix — every beat survives its channel's death

| Channel state | Law |
|---|---|
| Sound OFF (`SoundEffectsSetting`) | Haptic only, every beat (the engine's S0 doctrine: muting loses timbre, never the event). No beat is sound-only. |
| Haptics OFF / no vibrator | Sound only, every beat; press deepening still answers visually within 90ms. No beat is haptic-only. |
| Both OFF | The face is fully legible in print and geometry alone — the score is garnish on the skeleton, never load-bearing. Stationarity and second-0 laws are untouched. |
| Reduced motion (system animator scale 0) | Fades collapse to 0ms hard cuts; crossfades in Z1 become instant tenant swaps **inside the same bounds**; the draining rule still derives and prints. Geometry law outranks motion law. |
| TalkBack | One polite live region for the whole face; announcements at **door-open** (name + withheld/offer state) and at **offer-arm** only — never per-second, never assertive. All three acts reachable and labelled by their printed words. |

---

## 5. STATE MATRIX — THE VISIT, FRAME BY FRAME

| Frame | Z1 tenant | Z2 | Z3 | RETURN | Exit row | Sound/haptic event |
|---|---|---|---|---|---|---|
| Arrival (t=0) | GATE (or WITHHELD) | price standing (offer branch) | count standing | live, idle | live | Arrival beat (§4.1) |
| Counting (wall > 0) | GATE, rule draining | price standing | count standing | live, idle | live | **Silence** (§4.6) |
| Wall 0 (offer) | GATE → CHIP crossfade 180ms | price standing | count standing | live, idle | live | Arm knock (§4.6) |
| Wall 0 (withheld, cooldown live) | WITHHELD, deriving per frame | empty-held | count standing | live, idle | live | Silence |
| Cooldown expiry mid-visit | WITHHELD → RESOLVING → re-derived tenant | per new derivation | count standing | live, idle | live | Arm knock **only if** an offer arms |
| Homecoming arrival | WITHHELD tenant: "THE PEEK ENDED" greeting frame, then cooldown print | empty-held | empty-held | live, idle | live | Arrival beat, identical (L: sound never grades) |
| RETURN pressed | unchanged | unchanged | unchanged | deepening ≤90ms | unchanged | none |
| PEEK hold 0–600ms | CHIP filling | price standing | count standing | live | live | detent at 50% only |
| Exit tapped | — (Disarm face over) | — | — | preserved beneath | ghost slab on press | Step-down (§4.4) |
| Back from Disarm | as left | as left | as left | live | live | none |

Every row: **RETURN's bounds identical. Card bottom edge identical.** (§8, T-1 proves it.)

---

## 6. WHAT IT DEMANDS OF THE APP

- **DB: nothing.** No schema change, no migration. The taper count derives from
  intercept rows the ledger already books per session; the cooldown/lease anchors are
  the entity's existing mono fields; the token count is the repository's existing
  derived flow. DB version untouched — this face must be buildable inside the look
  lanes' "no schema change" physics.
- **State:** `AirlockInterceptState` gains the anchored ends it must carry
  (`wallEndsAtMono`, `wallTotalSeconds`, cooldown end-mono, session-booked count,
  just-expired-lease flag, arrival-row id) and loses the dead reason machinery
  (FIX-3b). All already present on the cell branch; the max spec freezes the list.
- **Engine:** five new named voice functions on the legacy lane — `playDoorArrival()`
  (FIX-7c recipe), `playDoorArm()`, `playDoorReturn()`, `playDoorPeekGrant()`,
  `playDoorExitStep()` — each a `triggerWaveform` + `enqueueTransient` pair with the
  §4 constants, plus the valve's bank calls riding the existing `playSeatKnock`. No new
  lane, no new thread, no audio files. The seal lane is untouched.
- **Services:** BACK-parity lines on the app-tier and shield launch paths; the
  `leaseLive` derivation at the hoisted settings read (FIX-1's cure, batch-9) is the
  only service-side fact the door consumes. `onNewIntent` routing (FIX-4) in
  MainActivity.
- **Compose:** the overlay is rewritten against `HeldDoorSkeleton` — one column, no
  `spacedBy`, fixed slots, bottom anchor, Z1 tenant crossfades, hold-to-fill chip with
  a 50ms redraw ticker whose only job is repaint. The Disarm overlay gains nothing but
  the KEEP FOCUSING deepening and its step-down return beat.
- **Accessibility:** the single polite live region (§4.7); skeleton tall variant at
  fontScale ≥ 1.3 (§1.1, L-G5).

## 7. COST HEADLINE

- **As a delta on this branch** (where Tor's wave already landed FIX-3…FIX-8 DECLARED):
  **SMALL–MED.** The remaining maximum work is: the `HeldDoorSkeleton` contract
  extraction + tall variant (~½ day), the five engine voice functions (~120 lines,
  ~½ day), the hold-to-fill chip replacing the tap chip (~½ day), the arm-beat wiring
  at wall expiry (~¼ day), press-deepening on RETURN / exit ghost slab / KEEP FOCUSING
  (~½ day), and the §8 test extensions (~½ day). **≈ 2–3 builder-days, one lane, no
  other face touched except the Disarm chip's press state.**
- **From trunk, whole programme:** **MED** — the overlay rewrite (~860 lines changed on
  this branch already), VM state/derivation work (~590), tests (~600) — i.e. exactly
  the cell's landed wave plus the delta above. No new dependencies, no schema, no
  service rewrite. The expensive part was never code; it was the audit. It is done.
- **Risk priced honestly:** the 110dp zone's price line at real device widths and the
  tall variant's totals are paper numbers until a device frame is rendered (the audit's
  own rider, H-11). The cure if Z2 needs a third line at fontScale 1.0 is a taller Z2
  inside a re-totalled stack — never a wrap that pushes RETURN. Proof is the §8 suite
  plus one panic-frame screenshot pair (wall−1 vs offer-armed, pixel-diffed) on device.

## 8. ACCEPTANCE — HOW THE MAXIMUM IS PROVEN (paper, for the compile)

- **T-1 Stationarity:** RETURN's pixel bounds asserted equal across wall-max, wall−1,
  wall−0, offer-armed, withheld, resolving, homecoming, memory-line-present,
  appointment-line-present, and process-death-restore frames — in both skeleton
  variants. 1px of movement fails.
- **T-2 No-growth audit:** zero `spacedBy` in the card column; every spacer a contract
  literal; Z height literal 110dp on peek-owing doors, absent otherwise.
- **T-3 Second-0:** withheld lines + price composed in the first frame snapshot; only
  the chip waits for the wall.
- **T-4 Anchored clocks:** with the wall's end-mono frozen, recompositions at arbitrary
  fake-now values print the derived seconds; no counter state exists to drift. Same for
  cooldown; repository cooldown returns ceiling form at 61s → 2 (never 1).
- **T-5 Offer matrix:** `peekOfferFor` truth table per path × tier × cooldown × pill —
  unchanged from the acquitted suite; grant re-derivation refuses a state whose row
  changed after the door opened.
- **T-6 Taper:** wall = min(5 + session-booked count, 10); lifetime `interceptCount`
  provably not read; homecoming expiry does not increment.
- **T-7 Hold-to-fill:** release at 599ms books nothing and drains in 120ms; completion
  at 600ms grants exactly once; a mid-hold cooldown change refuses silently (§4.3).
- **T-8 Exit honesty:** tapping END THE SESSION INSTEAD writes no row, spends no token,
  ends no session; KEEP FOCUSING returns to the same visit with no second wall and no
  second arrival beat; the valve spends at 60.0 and only at 60.0, banks at 15/30/45,
  and never drains below the last bank.
- **T-9 Feel constants:** every §4 recipe asserted against the engine call sites
  (pitch / duration / gain / chord / waveform arrays) — the score is testable because
  it is numbers, and it stays testable because the numbers live in named functions.
- **T-10 Dead words:** "EMERGENCY PEEK" zero hits on this face; footer prints the badge
  word alone; RETURN contrast ≥ 5.37:1; body alpha 0.92; border 0.38.

## 9. STRUCK AT MAXIMUM (so the maximum is a decision, not a pile)

- **The Trade** (peek bought with +10 min of session) — struck, audit's reasoning
  stands: the door must not negotiate the seal's length at the craving second.
- **The Slip** (dictation into the digest) — struck on this face: a keyboard is not a
  craving-second instrument.
- **Reason chips** — struck (FIX-3): a survey at the craving second is the wrong
  instrument; UNSTATED is the honest default.
- **Per-second detents on the countdown** — struck (§4.6); detents belong to his acts.
- **The Leaf** (edge-to-edge recomposition) — remains Ren's call; this skeleton is
  compatible with it and does not wait for it.

---

*What MIP-05 wants next: the Disarm face given this same treatment as its own maximum
(it shares the card, the ledger, and now the step-down pitch — its skeleton is the next
face that can move under a thumb), and the stationarity suite (T-1) carried as a
cross-face pattern to every overlay that can appear over a travelling thumb.*

— MIP-05 (Maximum-Implementation-Potential, issue #11)
