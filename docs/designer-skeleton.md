# DESIGN — THE HELD DOOR, FIXED SKELETON (Issue #11)

Seat: Designer, Tor's cell · For: Tor (lead) → Builder
Ground read, whole: `studio/plan/AUDIT-held-door.md` (Tor, Oct 30 2026);
`app/src/main/java/com/example/ui/components/AirlockAndDisarmOverlays.kt`
(`AirlockInterceptOverlay`, :548–720 in the issue-11 clone — byte-identical
to merge-batch9 at read time); `MonolithViewModel.kt` (`AirlockInterceptState`
:153–182, `peekOfferFor` :141, `triggerAirlockIntercept` :2315,
`transmuteImpulseAndReturn` :2428, `requestDisarmChallenge` :2236);
`MonolithCustomComponents.kt` (`BlockitActionChip` :170, `endsAtText` :427);
`theme/Color.kt`, `theme/Type.kt`, `theme/MonolithCurves.kt`;
`service/MonolithAccessibilityService.kt` (`InterceptPath` :46, door call
sites :208/:234/:262/:288); `MainActivity.kt` (overlay wiring :793,
intent fallbacks :133).
Scope: **design only. Nobody compiles.** Every size below is a buildable
number; every printed string is either quoted from the audit or flagged
as mine for Tor's ratification.

House rules applied: Sora = display, Atkinson Hyperlegible = body,
Anonymous Pro = numbers/badges. Night palette only (the door is always
Night — audit Conflicts 3, NIGHT KEEPS). No green anywhere; no colour
below is green or near it. No images, generated or otherwise — this face
is type, one rule, and two slabs. Held Door Truth Baseline binds every
print: nothing on this face may state what the machinery cannot prove
at the frame it prints.

---

## 1. The defect this skeleton kills (F-2, the audit's worst)

Today the card is a centre-anchored `Column` (`Arrangement.spacedBy(16.dp)`)
whose children appear when `countdownSecs == 0`
(`AirlockAndDisarmOverlays.kt:663` and `:690`). At countdown 0:

- offer branch composes a 42dp chip + 16dp gap + a price line
  (≈110dp total growth, Schliff's measure);
- the card is vertically **centred** in its `Box`, so half the growth
  lands above RETURN: RETURN's centre jumps ≈**55dp — more than its
  own 42dp height — under a travelling thumb, onto the peek chip**.

A control that moves at the exact second a thumb is already travelling
to it is not a layout bug, it is a trap. The cure is not animation,
not a faster fade, not a warning. The cure is geometry that cannot move:

> **THE SKELETON LAW — Nothing on this face changes position, size, or
> existence based on countdown state. Every slot is composed at frame
> one, at its final bounds, for the whole visit. Countdown state may
> change a slot's *contents* (crossfade in place) — never its bounds,
> never a sibling's bounds. RETURN's bounds at wall = max, wall = 1,
> wall = 0, and offer-armed are pixel-identical, and are asserted so
> in test (§11).**

Corollaries the builder must not re-break:

- **No `Arrangement.spacedBy` on the card column.** SpacedBy collapses
  around conditionally-absent children — it is the mechanism of the
  original defect. All spacing below is explicit fixed-height slots
  and fixed gaps (§3). A slot with nothing to print is composed
  **empty at full height** (the imprint's empty-box discipline:
  absence of print, never absence of space), except the two
  per-visit branches in §3.3, which are decided once at frame one
  from state that cannot change its branch mid-visit (with the one
  handled exception, §5.4).
- **No hero numeral.** The 88sp countdown (`:636`) dies (Conflicts 1).
  Nothing at display size exists on this face except the app name.
- **RETURN is live from frame one** in every state (acquitted by six
  seats; never gated by the wall, the taper, or anything else).

---

## 2. Shell (unchanged acquittals, tightened values)

| Property | Today | Fixed skeleton |
|---|---|---|
| Scrim/ground | `NightColorBackground` #171412, full size | **Keep** |
| Card placement | `Box(contentAlignment = Center)`, outer padding 20dp | **Top-anchored**: `contentAlignment = TopCenter`, outer padding 20dp on all sides. Card top edge sits at 20dp from the top of the safe area in every visit. (Schwelle's top-anchored skeleton, adopted in FIX-6/Conflicts 2.) |
| Card shell | clip 24dp, `NightColorSurfaceBright` #362C27, border 1dp `NightColorPrimary` @ α0.20, padding 24dp | **Keep shell** (card SURVIVES, Conflicts 2); border α **0.20 → 0.38** (F-5); padding 24dp all sides, keep |
| Card width | fillMaxWidth | Keep |
| Card height | content-driven (the defect) | **Fixed per branch**: 418dp (peek-owing) / 292dp (never-owed) — arithmetic in §3.4. Height is set by the slot stack, not by an explicit `height()` call: if a device/font-scale makes text exceed its slot, the slot clips/ellipsises (§9), the card does not grow. |
| Entry motion | `fadeIn(specB)` = 420ms ghost | **`fadeIn(specA(180))`** (FIX-7b) — opacity only, no scale, no translation; the full skeleton is composed at final geometry underneath the fade |
| Exit motion | `fadeOut(specA)` 260ms | **Keep** (FIX-7b) |
| Arrival sound | 16ms switch tick via `playMechanicalLatch` at trigger | Out of my seat's hands, recorded for the builder: FIX-7c — seal recipe at door pitch (300Hz, 44ms, gain 0.60, chordRatio 1.5, latch haptics kept). No layout consequence. |

Fonts on this face, exhaustively: **Sora** (name, RETURN label, PEEK
label), **Atkinson Hyperlegible** (intention, session lines, price,
withheld lines, honest exit), **Anonymous Pro** (gate line, memory
line, all badges, footer badge word). No fourth family, no exceptions.

Ink on this face, exhaustively:

| Token | Hex | Used for |
|---|---|---|
| `NightColorPrimary` (cream) | #FFE4CF | name, gate line, PEEK label/badge, footer badge (see §3), body text at α0.92 |
| `NightColorPrimaryContainer` (accent) | #F47442 | RETURN slab fill (the face's **only** accent fill), draining-rule fill |
| `NightColorOnPrimaryContainer` | #471602 | RETURN label ink — 5.37:1 on the accent (F-3; today cream-on-vermilion is 2.55:1, the face's worst contrast on its most important word) |
| `NightColorSecondaryContainer` | #4A423C | PEEK chip fill (deliberately **not** accent: the accent budget buys RETURN alone) |
| `NightColorSurfaceBright` | #362C27 | card |
| `NightColorBackground` | #171412 | ground |
| `NightTextColorSecondary` | #9C8E84 | footer badge word only (5.8:1 tier — quiet by colour token, not by an α trick) |

Body-line opacity: α **0.80 → 0.92** everywhere it appears today
(F-10). Wrapped lines centre-aligned (F-11) — every multi-line text
slot below is `textAlign = Center`.

---

## 3. The slot stack (top → bottom, exhaustive)

All heights are the slot's total vertical extent. Text slots are
single-composition `Box`es of exactly this height, content centred
vertically, so an empty slot and a full slot are the same geometry.

### 3.1 Fixed slots (every door, every visit)

| # | Slot | Height | Content (see §4 for per-door values) |
|---|---|---|---|
| — | card top padding | 24dp | — |
| H1 | **NAME** | 32dp | Sora Bold 24sp, cream, `maxLines = 1`, ellipsis (F-9), centred |
| — | gap | 4dp | |
| H2 | **INTENTION** | 24dp | His words **bare** — the `ACTIVE INTENTION:` prefix dies (FIX-6). Atkinson Regular 16sp (Schwelle's size; the name is the face's one hero), α0.92, centred, `maxLines = 1`, ellipsis. Blank intention → slot composed empty, prints nothing (acquitted silence). |
| — | gap | 4dp | |
| H3 | **SESSION BLOCK** | 40dp | Two stacked 20dp lines, Atkinson Regular 12.5sp, α0.92, centred. Line 1 = session line (§4.4). Line 2 = break-appointment line (§4.5). Either may be empty-held independently. |
| — | gap | 12dp | |
| Z | **PEEK ZONE** | **110dp or 0dp** | §5. The only branch in the stack. |
| — | gap after Z | 16dp if Z = 110, else 0dp | travels with the branch, decided at frame one |
| R | **RETURN slab** | 56dp | §6 |
| — | gap | 12dp | |
| X | **HONEST EXIT** | 36dp | §7 |
| — | gap | 8dp | |
| F | **FOOTER BADGE** | 16dp | The badge word **alone** — `AIRLOCK INTERCEPTION •` dies (FIX-6). Anonymous Pro Bold 10sp, letterSpacing +2.0 (the `PillBadgeTextStyle` register), uppercase verbatim from `interceptReasonBadge`, `NightTextColorSecondary`, centred. |
| — | card bottom padding | 24dp | — |

### 3.2 What is NOT in the stack (negative spec — do not re-add)

- **No reason-chip row, no reason picker, no particles.** FIX-3b kills
  `onSelectReason` / `onTransmuteParticleTap` and their state; FIX-3's
  chip row is STRUCK — a survey at the craving second is the wrong
  instrument. No slot is reserved for them, not even an empty one:
  reserving space for a struck element invites its return.
- **No countdown numeral** at any size (Conflicts 1). The only
  countdown print is the gate line inside Z (§5.2).
- **No eyebrow.** Nothing prints above the name.
- **No second accent fill.** PEEK is a neutral slab (§5.3).
- **No remaining-duration print anywhere** (Baseline law 1) — no
  "x MIN LEFT", no session countdown, no cooldown duration except
  the withheld line's ceiling print, which is the Baseline's own
  required form (§5.5).

### 3.3 The two per-visit branches (decided once, at frame one)

Z's height is a function of the **door's derivation at open**, never
of time:

- **Z = 110dp — peek-owing doors**: `state.canUse2MinutePeek == true`
  (offer will arm or is armed) **or** `state.peekWithheld != null`
  (an offer was owed and withheld). This includes offer-pending
  (wall running): the offer branch is composed at full height from
  frame one, its chip slot occupied by the gate stack until the
  wall expires (§5.2).
- **Z = 0dp — never-owed doors**: `canUse2MinutePeek == false &&
  peekWithheld == null` — LOCKED-tier apps and the guard / sniper /
  site / unknown doors (`peekOfferFor` false, withheld null by
  construction). Baseline law 4: these doors print **no peek line of
  any kind** — no chip, no price, no refusal, no empty labelled slot.
  The zone is absent, not blanked.

**Deviation from Schliff's filed numbers, flagged for Tor:** the audit
files withheld at 34dp (one line + gaps). I spec withheld at the full
110dp, for a reason inside the audit's own logic: FIX-5c requires a
cooldown withheld line to *resolve* mid-visit — at derived 0 the face
re-runs the offer derivation and the peek may stand up in its place.
Under a 34dp withheld zone, that resolution grows the card 76dp and
moves RETURN ≈38dp — F-2's defect returning through the side door,
on a rarer but real path. At 110dp the resolution is a crossfade
inside Z1 (§5.4) and RETURN does not move by a pixel. The withheld
content sits top-aligned in Z1; the rest of the zone is honest empty
card. If Tor holds Schliff's 34dp, the builder must instead *suppress*
the FIX-5c in-visit resolution (withheld stays printed until the next
visit) — I recommend against: it keeps a stale refusal standing after
its reason has expired, which is its own Baseline problem. **Tor to
ratify: uniform 110dp (recommended) vs 34dp + suppressed resolution.**

### 3.4 Card heights and RETURN's address (testable arithmetic)

- Peek-owing card: 24+32+4+24+4+40+12+110+16+56+12+36+8+16+24 =
  **418dp**. RETURN slab top at **266dp** from card top; RETURN centre
  at **294dp** from card top, **314dp** from the top of the safe area
  (20dp outer padding).
- Never-owed card: 418 − 110 − 16 = **292dp**. RETURN slab top at
  **140dp** from card top; RETURN centre at **168dp** from card top.
- Within either branch, RETURN's bounds are **constant for the entire
  visit, in every state in §8** — that constancy, not the absolute
  numbers, is the deliverable. Cross-branch difference is a per-visit
  fact known at frame one; no thumb is ever travelling between visits.

---

## 4. Per-door content of the fixed slots (FIX-6 name slot, C-8/B-7)

H1/H3 values by `state.interceptPath`. The badge already names the
door (footer F); the name slot holds a **fact about what he opened**,
never the door's own label restated.

| Door | H1 NAME prints | H3 line 2 / sub-line behaviour |
|---|---|---|
| `APP_TIER` | The app's real name from its row (`appRule.appName`, as today) | — |
| `SHIELD_APP` | The app's real name from its row (as today) | — |
| `SNIPER` | **Keep**: `YouTube Shorts` / `Instagram Reels` — the feed is the fact of what was stopped (audit: sniper names KEEP) | — |
| `SITE` | **The browser's name** (the intercepted package's app name, e.g. Chrome) — today the slot wrongly holds the matched domain | The **matched domain** prints as its own line: I place it in H3 line 2's slot **only when no break-appointment line is owed** (site doors never owe one — they cannot honour a peek, §4.5's condition fails — so the slot is always free on this door). Same register as H3 line 1. |
| `UNINSTALL_GUARD` | **What he actually opened** — the Settings app or the package installer, by the package that fired (`pkgName` at the :208 call site). `System Settings Guard` **dies**: it is a fiction in the slot that everywhere else holds a fact, and the footer badge (`Uninstall Guard`) already names the guard. **State gap for the builder** (§10): `triggerAirlockIntercept` receives only the composed `appName` string; the opened-target fact must be composed at the service call site (it knows the package) and passed through — never re-inferred in the composable from the badge or package string. If the target's human name cannot be resolved, print the package name verbatim rather than inventing a label. | — |
| `UNKNOWN` | The name carried in the intent, verbatim (today's behaviour); if blank, the existing `Short-Form Feed` fallback in `MainActivity` stands — no new invention | — |

Fallback badge word: **`Locked` → `Blocked` at every site** (FIX-6,
C-10). Sites found in the tree: `MonolithViewModel.kt:157` (state
default), `MonolithViewModel.kt:2318` (trigger default),
`MainActivity.kt:133` (intent fallback),
`MonolithShieldService.kt:185` (extra fallback). The audit says three
sites; I found four occurrences — builder sweeps all four, Tor's count
notwithstanding.

**Footer-badge inconsistency I am flagging, not silently fixing:**
for a `PEEK_2M` app-tier door, `interceptReasonBadge` is
`AppBlockTier.badge` = the literal `"2m Peek"` (`MonolithModels.kt:27`),
so footer F would print `2M PEEK` — a duration literal that lies the
day the peek length is edited (the exact disease FIX-8 cures on the
chip). Recommendation to Tor: on peek-tier doors the footer prints
`PEEK` alone (duration lives only on the chip badge, derived, §5.3);
other doors print their badge verbatim. Builder does not improvise
this without Tor's word.

### 4.4 H3 line 1 — session line (form acquitted; value is FIX-2's)

Verbatim from `airlockSessionLine` — **no wording change in my spec**:

- timed session: `SESSION ENDS {HH:mm}` (via `endsAtText`);
- stopwatch: `SESSION RUNNING · NO SET END`;
- no session: line empty-held, slot stands.

The *value* behind it is the builder's FIX-2 cure (span end, not
per-block remaining — I note the issue-11 tree's `triggerAirlockIntercept`
already carries the span derivation in comment and code; the Deck TIME
cell takes the same cure in the same pass, per the audit, or the two
faces disagree). My skeleton's only demand: whichever instant it is,
it prints in this slot, in this register, from frame one.

**Optional companion (Ren's call in the audit, NOT ruled in — do not
build without his word):** Halbwach's `THE FEED WILL STILL BE THERE
AT {HH:mm}`, derived from the same end instant. If Ren rules it in,
it takes H3 line 2 on doors where line 2 is otherwise empty — which
is why line 2's slot exists at fixed height. Absent that ruling,
line 2's only tenant is §4.5.

### 4.5 H3 line 2 — the errand's kernel (FIX-8, P-3 kernel SHIPS)

On **PEEK-tier doors only** (door can honour a peek *and* the app's
tier is `PEEK_2M` — i.e. exactly the doors where a break actually
opens this app), **if** the session's own schedule has a next break,
print the appointment the session already guarantees:

> `BREAK AT {HH:mm} OPENS THIS APP` — *wording mine, Tor to ratify;
> the instant is derived from the session's schedule, never a literal.*

Truth check: the service passes a `PEEK_2M` app freely during a Safe
Break (`MonolithAccessibilityService`, Safe Break rule), so the print
is true of the machinery. No next break in the schedule → line
empty-held. Non-PEEK doors → line never prints this (for LOCKED apps
it would be false — locked stays locked in breaks). This is a print,
not a lease, not a promise the door keeps: the break's own machinery
does the opening. **State gap** (§10): the break instant does not
exist in `AirlockInterceptState` today; builder derives it at trigger
time from the active session's schedule and carries it in state.

---

## 5. The peek zone Z (110dp, internal stack)

Z is a fixed-height `Box` (110dp) containing three stacked sub-slots.
Countdown/offer/withheld transitions are **content crossfades inside
Z1 only** (`fadeIn/fadeOut specA(180)` on the entering/leaving
content) — Z, Z2, Z3, and every slot below them never move.

| Sub-slot | Height | Tenant by branch |
|---|---|---|
| — gap (top of Z) | 0 | Z begins immediately after the 12dp gap under H3 |
| **Z1 ACTION** | 48dp | Gate stack (counting) **or** PEEK chip (offer) **or** withheld line (held) — §5.2/5.3/5.5 |
| — gap | 6dp | |
| **Z2 PRICE** | 34dp | Offer branch: the price sentence, from frame one (§5.6). Held branch: empty-held. |
| — gap | 4dp | |
| **Z3 MEMORY** | 18dp | The taper's count line (§5.7), or empty-held |
| **Total** | **110dp** | 48+6+34+4+18 |

### 5.2 Z1 while counting — the gate stack (the numeral's replacement)

Composed only on the offer branch while the wall runs:

- Line: **`PEEK OPENS IN {M:SS}`** — Scharte's words (FIX-6).
  Anonymous Pro Bold 15sp, cream, centred. `M:SS` = minutes unpadded,
  seconds zero-padded 2 (`0:05`, `0:10`, `1:00` if a future wall ever
  exceeds 60s). Figures are tabular by family; no per-second width
  jitter (Schliff A-4). The line **names what it gates** — the peek,
  and only the peek. It never implies RETURN is gated; RETURN sits
  below, visibly live, the whole time.
- Under it, 6dp below the text baseline box: **Schwelle's draining
  rule** — a 2dp-high, full-card-content-width rule. Track:
  `NightColorPrimary` @ α0.20. Fill: `NightColorPrimaryContainer`
  #F47442, width fraction = `wallRemaining / wallTotal`, draining to
  empty at expiry. It is a progress *of the gate*, printed honestly
  from the same anchored derivation as the figures (§5.8) — never a
  free-running animation.
- Gate stack total height ≤ 48dp, vertically centred in Z1, so its
  bounds equal the chip's bounds it will be replaced by.

The wall's length is the **taper** (FIX-8): `5 + this session's booked
intercepts of this package`, **capped at 10** — derived at trigger
time, carried in state as an anchored end (§5.8), never the bare
literal `5` that stands in the composable today (`:562–571`). The
taper gates **only** the chip's appearance in Z1. It never delays
RETURN, the withheld lines, the price, the memory line, or the exit.

### 5.3 Z1 at offer — the PEEK chip (the death of EMERGENCY PEEK, §5.9)

- Height **48dp** (F-4; `BlockitActionChip`'s hardcoded 42dp does not
  satisfy this — builder gives the door's chips their own height or
  parameterises the chip; the door does not inherit 42dp by accident),
  full content width, corner 12dp, fill `NightColorSecondaryContainer`
  #4A423C, border 1dp `NightColorPrimary` @ α0.38.
- Label: **`PEEK`** — Sora Bold 15sp, cream — followed by the derived
  badge in Anonymous Pro Bold 15sp, same ink, separated by one space
  (the chip's existing word+monoBadge mixed-typography pattern).
- Badge derivation (FIX-8, curing `peekBadgeText` at
  `AirlockAndDisarmOverlays.kt:257`, which today truncates):
  from `PEEK_DURATION_SECONDS` (or its editable-store successor):
  `120 → "2m"`, `90 → "1m30s"`, `60 → "1m"`, `45 → "45s"`,
  `125 → "2m5s"`. Rule: whole minutes, then remaining seconds if
  non-zero; sub-minute durations print seconds alone. **Never a
  truncation.** The badge and the lease read the same value from the
  same source — one derivation, two readers.
- Tap → `onTake2MinutePeek` (grant re-derives via `peekOfferFor` at
  grant time — acquitted machinery, untouched). State `canUse2MinutePeek`
  default flips `true → false` (FIX-8, Funke C-4): an unloaded state
  must read as no-offer, so a door composed before its state lands
  shows the gate/held geometry, never a phantom offer.
- testTag `airlock_peek_button` preserved.

### 5.4 The counting → offer transition (the former crime scene)

At the anchored wall end (§5.8), in **one recomposition**:

1. the gate stack fades out and the PEEK chip fades in, both inside
   Z1's unchanged 48dp bounds, crossfade 180ms specA;
2. nothing else on the face changes — Z2's price has been standing
   since frame one, Z3's memory line since frame one, RETURN has
   not moved and does not move;
3. there is no frame in which both gate and chip are composed as
   layout (crossfade is opacity over identical bounds, not a stack).

The same in-place rule governs the FIX-5c resolution (§5.5): any
mid-visit change of Z1's tenant is a Z1-internal crossfade. **If a
proposed implementation ever needs Z's height to change mid-visit,
the implementation is wrong, not the spec.**

### 5.5 Z1 while held — the withheld lines (print from second 0)

Withheld lines are **never gated by the wall** (FIX-6, SECOND 0: a
known refusal is never withheld). From the first frame, at full
strength, Atkinson Regular 12.5sp, α0.92, centred in Z1, verbatim
from the existing derivations:

- `PeekWithheld.COOLDOWN` → `peekCooldownPrint(derivedSeconds)` =
  `PEEK COOLDOWN — AGAIN IN {n} MIN`, ceiling minutes, derived at
  each recomposition from the stored cooldown end (§5.8) — never
  the self-ticking local counter at `:579–592` today.
- `PeekWithheld.ALREADY_ACTIVE` → `A PEEK IS ALREADY RUNNING`
- `PeekWithheld.PEEK_OFF` → `PEEK IS OFF`

**Expiry resolution (FIX-5c):** when the derived cooldown reaches 0,
the same recomposition re-runs the offer derivation. If the offer now
stands, Z1 crossfades to the chip (§5.4, with Z2's price fading in
inside its already-reserved slot). If it does not stand, the line
re-derives to its new true state or empties. **`AGAIN IN 0 MIN` is
unrepresentable**: at derived 0 there is no cooldown line, by
construction, not by a guard clause on the string.

### 5.6 Z2 — the price, re-timed (FIX-8)

Offer branch, printed **from frame one** — during counting as well as
at offer (Halbwach I-2: price before temptation, SHIPS in FIX-6):

> **`WHEN THE PEEK ENDS, A {n} MINUTES COOLDOWN STARTS ON THIS APP`**

replacing today's `TAKING THE PEEK STARTS A … COOLDOWN`
(`peekPriceText`, :267) — the machine anchors the cooldown at the
lease's *end* (`startPeekLease` pre-schedules `lastPeekEndElapsedRealtime`
at lease end; `recordPeekEnded` re-anchors at actual end), so from the
tap the real exclusion is lease + cooldown and the sentence now says
when the clock starts. `{n}` derives from `PEEK_COOLDOWN_MILLIS` —
20 at today's constant — with the existing singular/plural unit logic
(`1 MINUTE` / `{n} MINUTES`) preserved. Atkinson Regular 12.5sp,
α0.92, centred, max 2 lines inside the 34dp slot (at 12.5sp the
sentence wraps to 2 lines at card width — the slot is sized for it;
verify on device per the audit's instruction to verify the 110
against the price's real line count).

Held branch: Z2 empty-held (a withheld offer has no price to print;
printing one would advertise a transaction that cannot happen).

### 5.7 Z3 — the door's memory (the taper's count, FIX-8)

When this session's booked intercepts of this package **before this
visit** number ≥ 1, Z3 prints the count of tonight's — no, this
session's — returns, in the gate line's family (Anonymous Pro Bold
10sp, letterSpacing +2.0, cream @ α0.92, centred):

> **`{ORDINAL} TIME THIS SESSION`** — e.g. `SECOND TIME THIS SESSION`,
> `THIRD TIME THIS SESSION` — where the ordinal is
> (prior booked intercepts this session, this package) + 1.

**Wording deviation flagged for Tor:** the audit's standing example
is Halbwach's `THIRD TIME TONIGHT`. The taper it accompanies counts
*this session's* booked intercepts (FIX-8's own definition), and a
session is not a night (a session can start before midnight and be
counted after; two sessions can share a night). Printing TONIGHT for
a session-scoped count would be a Baseline violation in miniature.
I spec THIS SESSION; Tor ratifies or supplies the true scope.
Count = 0 prior → Z3 empty-held: the door claims no memory it doesn't
have. This line is a **count it can prove or silence** (Funke's §4
never-list, endorsed in the audit) — it is never a streak, never a
score, never a prediction, and it appears on held doors too (the
memory is true regardless of the peek's state), in the same slot.

**State gap** (§10): the count does not exist in
`AirlockInterceptState` today. Builder derives it at trigger time
from the session's booked intercepts for the package (the ledger
FIX-3 makes honest — reason `UNSTATED`, §9) and carries the *count*
in state; the composable never queries storage.

### 5.8 Anchoring law for every number in Z (FIX-5, applied to Z)

- The wall and the cooldown both enter state as **stored monotonic
  end instants** (`SystemClock.elapsedRealtime()` domain), set at
  trigger time. Every print in Z derives `remaining = end − now` at
  recomposition; a 1s ticker may exist **only as a redraw trigger**,
  never as a counted value (the VM's own doctrine at
  `MonolithViewModel.kt:180–188`, obeyed by the pill, broken by the
  door today at `:562–592` — both local counters die).
- The repository's cooldown derivation returns the **ceiling** form
  (FIX-5a; today `MonolithRepository.kt:1180` floors `remainingMs/1000`
  before the print ceilings it back up — floor-then-ceiling
  understates, the one direction this face never goes). One
  derivation, ceiling once, at the source: `peekSecondsRemaining`'s
  arithmetic is the model.

### 5.9 What the death of EMERGENCY PEEK changes on the surface

One string in the tree (`AirlockAndDisarmOverlays.kt:665`), and the
surface changes in four concrete ways:

1. **The chip shrinks to a verb and a fact**: `PEEK 2m` (word +
   derived badge) instead of a siren. The face's loudest remaining
   element is the **name** — which is correct: what he opened should
   outrank what he's offered.
2. **The emergency register leaves this face entirely.** The app's
   one real emergency act is the Disarm face's `EMERGENCY BREAK`
   (60s valve, weekly token). With `EMERGENCY PEEK` dead, no print on
   the Held Door borrows that gravity for a 2-minute lease — and the
   honest exit (§7), which *does* lead to the emergency face, reads
   in its true proportion: quieter than the peek, graver in fact.
   That inversion is deliberate and is the face's honesty made
   visible.
3. **Cross-surface consistency**: `PEEK` is now the word on the chip,
   the tier badge family, the withheld lines (`PEEK COOLDOWN`,
   `PEEK IS OFF`), and the pill — the studio's one-word law the audit
   cites. A man learns one verb for one act.
4. **Nothing else renames.** RETURN keeps `RETURN TO FOCUS`; the
   withheld strings keep their wording; the price keeps its slot and
   changes only its timing clause (§5.6). The death is surgical, not
   a rebrand.

---

## 6. RETURN slab (R, 56dp)

- Height **56dp** (FIX-6, Schwelle; replaces the 42dp chip height on
  this face), full content width, corner 12dp, fill
  `NightColorPrimaryContainer` #F47442 — the face's only accent spend.
- Label: **`RETURN TO FOCUS`** verbatim, Sora Bold 15sp
  (`PrimaryButtonTextStyle` register: 15sp, letterSpacing +0.5),
  ink `NightColorOnPrimaryContainer` **#471602** (F-3, 5.37:1).
  (Implementation note: `BlockitActionChip`'s `isAccentArmed` branch
  currently forces cream #FBF1EC ink — the door's RETURN must not
  inherit it; the 5.37:1 pairing is the spec, the chip's default is
  the bug.)
- Live and tappable from frame one in **every** state (§8), including
  mid-count and mid-crossfade. Never dimmed, never disabled, never
  re-labelled by state. testTag `airlock_return_button` preserved.
- Tap → existing `onReturnToFocus` (`transmuteImpulseAndReturn`).
  Its booking changes under FIX-3c — outcome **`RETURNED AT DOOR`**,
  not `Stayed Focused` (the tap is observed; the sequel is not) —
  a ledger fact, no print consequence on this face.

## 7. The honest exit (X, 36dp) — `END THE SESSION INSTEAD`

FIX-8 (Halbwach I-4, SHIPS). One quiet footer line **under RETURN** —
the audit's placement, kept literally: RETURN, 12dp gap, exit. Not
in the footer-badge row, not in a corner, not behind a long-press.

- **Print**: `END THE SESSION INSTEAD`, Atkinson Bold 12.5sp,
  cream @ α0.92, centred, no fill, no border, no badge, no icon.
  Quiet by *form* (bare text among two slabs), never by illegibility
  — α0.92 stands; a man at 1 AM must be able to read his exit.
- **Touch target**: the full 36dp slot height, full content width,
  is the tap target (the 12.5sp glyphs alone would be a 36dp-tall
  target's label, not the target). Meets a honest minimum for a
  deliberate act without dressing it as a button.
- **Route, not a spend**: tap routes to the **existing**
  `DisarmChallengeOverlay` (`requestDisarmChallenge`) — the face
  that carries the gravity: `EMERGENCY BREAK`, the consequence
  sentence, the 60-second valve, `KEEP FOCUSING`. This line ends
  nothing, spends nothing, books nothing. If the valve's face is
  where he changes his mind, `KEEP FOCUSING` returns him to the
  session and this door is already dismissed beneath it.
- **Wiring the builder must add** (it does not exist today):
  `AirlockInterceptOverlay` gains a fifth callback,
  `onEndSessionInstead`. In `MainActivity` it binds to a VM action
  that (a) dismisses the airlock (books **nothing** — leaving via
  the exit is neither `RETURNED AT DOOR` nor a peek), then
  (b) calls `requestDisarmChallenge()`. Order matters: the disarm
  overlay composes after the airlock in `MainActivity`, so it would
  render on top regardless — dismissing first keeps exactly one
  door standing at any time.
- **Dead-end guard**: `requestDisarmChallenge` silently returns
  when there is no active session, and takes a clean-completion
  path (no overlay) for a stopwatch past its floor. If the disarm
  face does not open, **the airlock must remain standing** — the
  exit tap must never strand him on no face at all. Builder: the VM
  action reports whether the disarm opened / session completed;
  the airlock dismisses only on a true handoff. (In practice the
  door only fires inside a session, but a design that strands on
  an edge case is a wall, not a door.)
- **No confirmation is added here.** Not a dialog, not a "are you
  sure", not a hold. Adding friction to this line would re-wall the
  door the audit just opened; the valve's 60 seconds *is* the
  friction, and it lives on its own face.

## 8. State matrix — what sits where, state by state

Every state below is a full description of the same skeleton. Read
any column top to bottom: that is the whole face in that state.
**Position-locked across all states**: H1, H2, H3, R, X, F bounds;
card top edge; card height within the visit's branch. **The only
things that ever change mid-visit**: Z1's tenant (crossfade), Z2's
price fading in on a §5.5 resolution, the gate figures/rule (derived
prints), and opacity of the whole card during entry/exit fades.

| Slot | COUNTING (offer-pending: wall running, offer will arm) | OFFER (wall expired, chip armed) | HELD (withheld: COOLDOWN / ALREADY_ACTIVE / PEEK_OFF) | NEVER-OWED (LOCKED tier; guard/sniper/site/unknown) | RETURNING (RETURN tapped; exit fade, ≤260ms) |
|---|---|---|---|---|---|
| H1 NAME | §4 fact | same | same | §4 fact (guard/site rules apply) | frozen |
| H2 INTENTION | bare words or empty-held | same | same | same | frozen |
| H3 L1 session | end instant / NO SET END / empty | same | same | same | frozen |
| H3 L2 | break appointment (PEEK doors w/ break) or site domain (site door) or empty | same | break appointment if owed, else empty | site domain or empty | frozen |
| Z1 (48dp) | **Gate stack**: `PEEK OPENS IN {M:SS}` + draining rule | **PEEK chip** 48dp, `PEEK` + derived badge | **Withheld line**, from frame one, derived live | — (Z absent) | frozen (tenant as at tap) |
| Z2 (34dp) | Price sentence (§5.6) | Price sentence | empty-held | — | frozen |
| Z3 (18dp) | Memory line or empty-held | Memory line or empty-held | Memory line or empty-held | — | frozen |
| R RETURN (56dp) | live, tappable | live | live | live | tapped; input dead after first tap (no double-booking) |
| X EXIT (36dp) | live | live | live | live | frozen |
| F badge (16dp) | badge word alone | same | same | same (`Uninstall Guard` / `Site Blocked` / `Blocked` …) | frozen |
| Card height | 418dp | 418dp (identical) | 418dp (identical) | 292dp | as at tap |

Two more transient states, for completeness:

- **PEEK TAKEN** (chip tapped, grant honoured): identical handling to
  RETURNING — skeleton frozen, exit fade specA 260ms, the floating
  peek pill takes over from there (the pill is another face's audit;
  no pill geometry is specced here). A refused grant (re-derivation
  fails) leaves the door standing in OFFER with **no visual change
  and no error print** — the acquitted machinery's silent refusal is
  the behaviour; the door does not apologise for arithmetic.
- **EXIT TAPPED**: airlock exit fade as RETURNING; the Disarm face
  enters on its own motion (its `specC` today — that face's audit
  may change it; not mine here). No Held Door slot animates
  individually during the handoff.

Note what COUNTING is *not*: there is no state in which the face
shows a numeral, a disabled RETURN, a hidden price, or a withheld
line waiting for permission to print. The wall delays exactly one
thing in the world: a chip's appearance inside a 48dp slot.

## 9. Ledger & truth side-effects the layout depends on (FIX-3)

No prints, but the skeleton's numbers are only as honest as the
ledger under them — builder's FIX-3 pack, recorded here so the
design's dependencies are explicit:

- Reason booked at every intercept outcome is **`UNSTATED`** unless
  a man actually states one — the `MUSCLE_MEMORY` default dies at
  `AirlockInterceptState:160`, `triggerAirlockIntercept`, and the
  repository's `Habit` default. (My Z3 count and the taper both count
  *booked intercepts*; booking a fabricated reason would poison the
  count's meaning even where the count's arithmetic is right.)
- RETURN's outcome books **`RETURNED AT DOOR`** (FIX-3c).
- Blank package books blank (no TikTok fallback, FIX-3d — that
  fallback sits in `transmuteImpulseAndReturn` today); blank
  intention falls back to nothing, never to the composer's draft
  (FIX-3e). H2's empty-held slot is the visual half of that law.
- `selectedReason` / `transmutedParticles` state fields and the
  `DistractionTriggerReason.coachingTip` strings die with the wires
  (FIX-3b) — §3.2's negative spec is their layout consequence.

## 10. State the builder must add (skeleton dependencies)

`AirlockInterceptState` today cannot express this skeleton. Required
additions/changes — names mine, types are the builder's:

1. `wallEndsAtElapsedRealtime: Long?` — anchored wall end (taper
   length applied at trigger); null on non-offer branches.
2. `wallTotalSeconds: Int` — the taper-derived wall length, for the
   draining rule's fraction.
3. `peekCooldownEndsAtElapsedRealtime: Long?` — replaces the
   `peekCooldownRemainingSeconds` snapshot as the print source
   (snapshot may remain as a seed for the first frame, never as the
   ticking value).
4. `priorInterceptsThisSession: Int` — booked intercepts of this
   package in this session *before* this visit (drives both the
   taper and Z3; one count, two readers, same number).
5. `nextBreakEndsAt… / nextBreakAtMillis: Long?` — the session
   schedule's next break instant, for H3 line 2 (§4.5); null when
   none. (Wall-clock domain — it prints as HH:mm, unlike the mono
   anchors above. Keep the domains separate and named so.)
6. `nameSubLine: String?` — the site door's matched domain (§4);
   the guard door's opened-target fact is composed into
   `interceptedAppName` at the service call site instead.
7. `canUse2MinutePeek` default `true → false` (FIX-8).
8. New overlay callback `onEndSessionInstead: () -> Unit` (§7).

## 11. Verification the builder can encode (paper, for Ren's compile)

1. **RETURN immobility**: compose the overlay in COUNTING (wall 10,
   wall 1) and OFFER; assert `airlock_return_button`'s bounds are
   equal across all three, and equal again in HELD. This test is the
   deliverable's proof — F-2 cannot regress silently again.
2. **Zone heights**: peek-owing card content height 418dp-equivalent
   slot stack; never-owed 292dp; Z present at 110dp from the first
   frame in COUNTING (not after expiry).
3. **Strings**: no `EMERGENCY PEEK`, no `AIRLOCK INTERCEPTION`, no
   `ACTIVE INTENTION:`, no `System Settings Guard`, no `Locked`
   fallback anywhere in the tree after the sweep; `PEEK OPENS IN`
   present only in Z1; `END THE SESSION INSTEAD` present in every
   state including NEVER-OWED.
4. **Badge derivation**: `peekBadgeText(90) == "1m30s"`,
   `(120) == "2m"`, `(45) == "45s"`.
5. **Price from frame one**: in COUNTING, the §5.6 sentence is
   composed before the chip exists.
6. **Withheld from frame one**: in HELD-COOLDOWN with wall-equivalent
   time still to run on other branches, the cooldown line is composed
   at first frame; simulating derived 0 removes it in the same
   recomposition that offers the chip — `AGAIN IN 0 MIN` never
   composes.
7. **Contrast/geometry spot values**: RETURN ink #471602 on #F47442;
   RETURN height 56dp; chip height 48dp; card border α0.38; body
   α0.92.
8. **Font scale**: at system font scale 1.3, H1 ellipsises, Z2's price
   stays within its 34dp slot (clip, never grow) — card height
   invariant holds. (If 1.3 scale makes the price unreadable clipped,
   that is a finding for Tor, not a licence to grow the card.)

## 12. Open for Tor (decisions I need, not guesses I'll bury)

1. **Withheld at 110dp vs Schliff's 34dp** (§3.3) — my deviation,
   reasoned from FIX-5c; ratify or rule the suppressed-resolution
   alternative.
2. **`THIS SESSION` vs `TONIGHT`** in Z3 (§5.7) — truth-scope call.
3. **H3 line 2 wording** `BREAK AT {HH:mm} OPENS THIS APP` (§4.5) —
   mine; the kernel ships, the words are yours/Hale's to settle.
4. **Footer on peek-tier doors** (§4): `PEEK` alone vs the literal
   `2M PEEK` the tier badge would feed it.
5. **Halbwach's companion line** (§4.4) is Ren's call per the audit —
   my H3 line 2 slot accommodates it if ruled in, at zero geometry
   cost; on site doors it would contend with the domain line (domain
   wins — it is the interception's fact; the companion is rhetoric).

— Designer, Tor's cell, Issue #11. Design only; nothing compiled,
nothing built. Ground: audit + issue-11 clone as cited inline.
