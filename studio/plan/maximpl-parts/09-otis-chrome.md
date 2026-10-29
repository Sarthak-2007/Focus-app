# 09 — CHROME / NAV / HEADER (PLAN WAVE) — Otis, Maximum-Implementable (coord. Voss)

Scope: `MonolithBottomNavView` (band switch), `MonolithPullDownHeader` (nameplate →
war line), the host mapping in `MainActivity.kt`, and screen transitions. Plan only —
no code. Guard's fate and Ledger's fate are **Sarthak's open picks**; this spec is
written in two fully-headed branches and decides neither.

**Drift note (read first):** the code has moved since the wave-1 diagnosis
(`maximpl/09-bottom-nav.md`, N1–N9). Round 0/1c already landed inside
`MonolithBottomNavView` (now `BlockitCoreViews.kt:827–1009`): the sliding pill is
`tokControlFill()` (not the `#FFB38F` peach), counts already render in legend ink
(not orange), and the armed dot inside the FOCUS icon is ink, not accent. What
still stands from wave-1: floating capsule (N1), highlight-block selection (N2),
icons duplicating words (N5), Guard = −1 vanishing state (N6), glide motion (N7),
dead pitch ladder (N8 — now documented in `fireFeel`'s own contract comment,
L199–211), approximate touch partitions (N9). This plan finishes the job wave-1
specced, corrects the two places the interim fixes diverged from the locked law
(the armed lamp returns to accent — it is the band's *only* orange, §Mechanism.4),
and adds what wave-1 never covered: drag, the Cloister, the war line's data
honesty, and both branches.

---

## Mechanism

### 1. The band — flush recessed rail, not a floating pill

The capsule dies. The bottom of the phone becomes a full-bleed panel strip:
square corners, edge to edge, one tonal step off the ground, separated from the
panel above by a top seam, never by shadow or elevation. The slide-to-arm bar on
Deck is then the only floating-weight object at the bottom of the screen, which
is correct — it is the hero control; the nav is architecture.

| Property | Value |
|---|---|
| Host height | `Modifier.height(80.dp)` → **72.dp** (`MainActivity.kt`, nav `AndroidView`) |
| Strip rect | `(0, 0, w, h)`, radius 0 |
| Strip fill | `tokSurfaceVariant()` — the first step above ground; at night it resolves through the rebuilt staircase (ground `#171412`, steps ≥1.35:1 — do not hardcode the old `#272320`) |
| Top seam | 1dp `tokHairline()` at `y=0.5dp`, full width; under it the recess shade `0x2E000000`, 1.4dp, at `y=1.4dp` (sled-track constants, already shipped) |
| Safe area | unchanged — host `Scaffold` applies `safeDrawing` outside the view |

### 2. The slit, the stops, the cap

One axis runs the band: the slit at `slitY = h × 0.46f` (≈33dp at 72dp). Everything
is printed relative to it. The mechanism is `RombiKnobView`'s, scaled to machine
width — recessed slit, printed stops, one travelling cap in `tokControlFill()`,
one marker line. (Marko mailed, thread `band-travel-math`: mechanism shared,
travel pricing deliberately divergent — §Motion.)

| Element | Spec |
|---|---|
| Slit span | `center[first] − 14dp` → `center[last] + 14dp` |
| Slit draw | dark line `0x2E000000`, 2dp, round cap; light line `0x2EFFFFFF`, 1dp, at `slitY + 1.6dp` — verbatim knob track |
| Stop ticks | 1.4dp `tokTextSecondary()`, `slitY − 5dp … slitY + 5dp`, at each stop centre |
| Numerals | `01…`, Anonymous Pro **Bold** (real file, `paintMono`), 9sp, centred on stop, baseline `slitY − 19dp` |
| Legends | stop word, Atkinson **Bold** (`paintWord`), 11sp, centred on stop, baseline `slitY + 26dp`; count suffix on the same baseline via `drawMixedWordAndMono` (§5) |
| Cap | height **30dp**, centred on slit, radius **8dp**; width per stop (§Branches) |
| Cap fill | `tokControlFill()` — day ink; night `#4A423C` (the locked fills cap) |
| Cap marker | one vertical line, 3dp wide, round cap, `slitY − 7dp … slitY + 7dp`, cap centre — the cap carries **nothing else**: no word, no icon, no count |

Words, numerals, counts are **printed on the band and stay put**; the cap only
points. That is the entire difference between a switch and a highlight.

### 3. Icons — deleted

All four hand-drawn glyphs go. Each duplicates the printed word beneath it, the
icon-above-label stack is the Material bottom-nav schema (the thing that makes
the current bar read as phone chrome), and at arm's length mid-walk the words
pass the glance test the glyphs fail. The freed vertical budget is what buys the
numeral-above / name-below printed scale.

### 4. The armed lamp — the band's only orange

The session-armed state becomes a confirmation lamp: a **3dp-diameter dot**
(radius 1.5dp) in `tokAccent()`, printed on the band at the FOCUS stop, right of
its numeral: centre at `(center[FOCUS] + 14dp, slitY − 22dp)`. Geometry is fixed
whether lit or not — the lamp's *position* never moves; unlit, nothing is drawn
(the plate is blank, not greyed).

- **When lit:** exactly while `isSessionArmed == true` (host: `activeSession?.isActive`).
  It lights on the frame the session arms and dies on the frame it ends — it is
  slaved to the session record, never to an animation or a timer.
- **Why it exists:** position already says which panel you're on; the lamp
  confirms the one fact position can't — *the machine is holding a session* —
  and it says it on every panel, in peripheral vision, while you're in SYSTEM
  or HORIZON. That is work the old in-icon dot (Round 1c: ink) could not do:
  ink reads as part of the glyph; the lamp must be findable without reading.
- **Budget:** this is the chrome accent budget entire — one 3dp dot, conditional,
  at FOCUS. Everything else in chrome is ink. (The header's 8dp brand pip
  currently spends `primaryContainer` orange while a session is active — that
  spend dies with the war-line rework, §War line.3: armed is rendered once,
  here.)

### 5. Counts — letterpressed readings, zero suppressed

Counts are inventories printed next to their legends, in the legend's own ink —
never a badge, never orange, in both selection states:

| Stop | Count | Source (host, already computed in `MainActivity.kt`) |
|---|---|---|
| SYSTEM | blocked apps | `blockedAppsCount` = `allApps.count { resolvedTier == LOCKED }` → view's `blockedCountBadge` |
| HORIZON | queued tasks | `activeTasksCount` = active (0/1) + `queuedTasks.size` → `tasksCountBadge` |
| GUARD *(BRANCH–KEEP only)* | readiness `n/4` | `guardReadinessCount` = the 4-boolean list `.count { it }` (L208–213) → **new** view var `guardReadinessBadge: Int = -1`; `>= 0` prints `" $it/4"` |

- Rendering: same `drawMixedWordAndMono` call as today, mono suffix 9sp,
  `monoColor` = legend colour (`tokTextSecondary()` unselected /
  `tokTextPrimary()` selected).
- **Zero suppresses.** Count 0 prints nothing — a printed `0` on a switch legend
  is noise; the panel itself carries the explicit empty state.
- `guardReadinessBadge` follows the existing badge-var setter pattern
  (invalidate on change); host wiring is one line in the existing `update {}`.

### 6. Touch model — tap, drag, seat

Replaces the current DOWN/UP partition arithmetic (N9: hit zones offset from
drawn slots by up to ~6dp, vertical extent ignored). Geometry now derives from
one source: `stopCenters: FloatArray` (recomputed in `onSizeChanged`),
`stopEdges` = midpoints between adjacent centres. The **whole 72dp strip** is
the hit area — a switch strip is allowed vertical forgiveness; a floating pill
was not.

- **DOWN:** `pressedStop` = partition containing x. That stop's numeral + legend
  draw at `tokTextPrimary()` while pressed (the plate under the finger).
  DOWN tick (§Haptics). `requestDisallowInterceptTouchEvent(true)`.
- **MOVE:** drag mode. `capPos` follows x through the inverse piecewise-linear
  map over `stopCenters`, hard-clamped to `[0, lastStop]` — end detents are
  walls, no rubber-band past the scale (a scale that over-travels lies about
  its range). Passing ticks fire as `round(capPos)` changes (§Sound).
- **UP:** target = nearest stop to release x (after a drag) or the DOWN
  partition's stop (a tap). If target ≠ selected: set `selectedTabIndex`
  (starts travel, or the ≤0.5-stop settle after a drag) and invoke
  `onTabSelected(target)` **immediately** — navigation never waits for the cap;
  ARM's ≤10s budget is spent on the session, never on chrome. Seat feel lands
  with the animator's end (§Motion). If target == selected after a drag:
  settle back silently — a non-event doesn't clunk.
- **CANCEL:** settle back to `selectedTabIndex`, silent, release intercept.
- Selection still commits on UP, not DOWN — the existing contract, and correct
  for a band a thumb can sweep across without switching panels mid-gesture.
- Accessibility: one line in the `selectedTabIndex` setter —
  `contentDescription = "Panel ${value + 1} of $stopCount, ${labels[value]}"`
  (guard the −1 case) — the switch gets a spoken state to match its physical one.

---

## BRANCH–KEEP — Guard kept as a destination

*Sarthak's pick, unmade. Everything in this section is conditional on it.*

### K.1 Stops — five positions, Guard the reduced end detent

`FOCUS · SYSTEM · HORIZON · LEDGER · GUARD` — four equal stops + Guard at
**0.66× width** behind a divider:

- Stops 0–3: unit `u = w / 4.66f` each. Stop 4 (GUARD): width `0.66u`, centre at
  `4u + 0.33u`. At 360dp wide: main pitch ≈77dp (still wider than today's pill
  slots), Guard slot ≈51dp — `GUARD` at 11sp Bold ≈38dp, fits with margin.
- **Divider:** 1dp `tokHairline()`, vertical, at the LEDGER/GUARD edge,
  `slitY − 16dp … slitY + 16dp`. The band itself announces the fifth position is
  a different species of place — a hatch, not a panel (the multimeter's end
  detent: visited deliberately, never lived on, unreachable by an overshooting
  sweep the way an equal middle stop is).
- Cap width lerps with `capPos`: stops 0–3 → `u − 18dp`; Guard → `0.66u − 14dp`;
  fractional positions interpolate neighbouring widths in `onDraw` (no new
  animator).
- Guard's seat feel is the latch, not the click (§Haptics) — the hatch sounds
  like a hatch.
- Ledger stop: present in this branch while Ledger lives (Ledger's own fate is
  the second open pick — if Ledger dies under BRANCH–KEEP, the band runs four
  positions: `u = w / 3.66f`, Guard still the 0.66 end detent, indices remap
  per §K.2's table minus VAULT).

### K.2 Host mapping — nav and host land in ONE commit

Current traps, named exactly (`MainActivity.kt`):

- L223–229: `MonolithDestination.GUARD -> -1` → **`-> 4`**. The −1 vanish dies;
  entering Guard is a travel to 4, leaving (tap or BackHandler → DECK) is a
  travel back. The cap's position on the Guard screen is the fix for N6: you can
  finally see where you are from the band while standing in the permissions panel.
- Callback L491–499: the `when(idx)` ends `else -> MonolithDestination.VAULT`.
  **Index 4 would silently navigate to VAULT.** Add explicit
  `4 -> MonolithDestination.GUARD` *before* the else — or better, kill the else
  in the same commit: exhaustive arms `0..4`, `else -> Unit` (an unknown index
  navigates nowhere; it must never guess a destination).
- View: `selectedTabIndex` clamp `coerceIn(-1, 3)` → `coerceIn(-1, 4)`; `tabLabels`
  gains `"GUARD"` (five entries; the draw loops run over the array).
- `update {}` block: add `view.guardReadinessBadge = guardReadinessCount`.
- Existing Guard routes stay (header GUARD ink, §War line; the status drawer;
  Shield's permission primer) — the detent is Guard's *state display* first and
  its third route second.
- Theme override, if Sten's rule keeps one: lives on the Guard screen
  (inventory note only — Guard's interior is not this spec's to redesign).

### K.3 What this branch costs

Main-stop pitch drops ~7% vs a four-stop band; the band carries a destination
he opens rarely at permanent full price. The divider + reduced slot keep that
price honest — Guard reads as furniture, not as a fifth home.

---

## BRANCH–DISSOLVE — Guard dissolved

*Sarthak's pick, unmade. Everything in this section is conditional on it.*

Guard stops being a place. Its three contents go to their three homes:
**status → chrome** (the readiness reading lives in the war line's ink,
§War line.3 — visible everywhere, which is where he already looks), **websites
→ Shield** (the blocked-sites list joins the blocking targets it describes;
Shield's spec, not this one), **the rest → a drawer off the band's PARK
end-detent** (inventory below).

### D.1 Stops — destinations + PARK, Ledger sub-branched

PARK is an end detent that is **not a destination**: same 0.66× reduced slot,
same hairline divider, legend printed `PARK`, no numeral (numerals count
destinations; PARK gets none — printing `05` would claim it is one). The cap
seats at PARK while the drawer is open and travels back to the destination
underneath when it closes. Position still tells the truth: cap at PARK =
*you are in the service space, above no panel.*

- **DISSOLVE + LEDGER (Ledger kept):** `FOCUS · SYSTEM · HORIZON · LEDGER` +
  PARK — indices 0–3 destinations, 4 = PARK. `u = w / 4.66f`, PARK `0.66u`.
- **DISSOLVE − LEDGER (Ledger killed):** `FOCUS · SYSTEM · HORIZON` + PARK —
  indices 0–2 destinations, 3 = PARK. `u = w / 3.66f`, PARK `0.66u`; main pitch
  grows to ≈98dp at 360dp — the roomiest band of any branch.

### D.2 The PARK drawer — contents inventory only

The drawer occupies the existing drawer slot (below the header, above the
viewport — the `UnifiedStatusGuardDrawer` position) so the band and the cap at
PARK stay visible while it is open. Open/close motion: the drawer's own —
expand `CurveC` 420ms + fadeIn `specA`; collapse `specA` 260ms + fadeOut. The
drawer's interior design is not this spec's; its **inventory** is fixed:

1. **Permissions** — the four readiness items behind `guardReadinessCount`
   (accessibility service, usage stats, device admin, notification listener),
   each with its state word and its open-settings intent (the five
   `Settings.ACTION_*` intents currently wired to `SystemGuardScreen`).
2. **Sound** — the `soundEnabled` toggle (currently Guard's `onToggleSound`).
3. **Theme-forced override** — *only if Sten's rule keeps one* (mailed, thread
   `theme-flip-chrome`): one row stating the override and its release
   (`THEME — FORCED NIGHT UNTIL 03:30`). If no override survives, this row does
   not exist.
4. **Exam-date row** — the war line's correction home (§War line.2):
   `NEET DATE — 2 MAY 2027 · NOT YET ANNOUNCED` + set action. (In BRANCH–KEEP
   this row lives on the Guard screen instead.)

Flagged duplication, disposition for the merge: the existing status drawer's
SHORTS BLOCKER / LINK BLOCKER toggle chips duplicate Shield sleds (one fact,
two renderings). This spec's recommendation: the PARK drawer carries those as
**status words only**, toggles stay in Shield. The drawer's HELD DIGEST and
STREAK entries are not Guard contents and are untouched by this branch.

### D.3 Host mapping — nav and host land in ONE commit

- `selectedTabIndex` mapping (L223–229): destinations map as today
  (DECK 0, SHIELD 1, TASKS 2, VAULT 3 if Ledger lives). **There is no GUARD arm.**
  PARK is driven by a separate view var, `parkEngaged: Boolean`, set from the
  drawer's open state — never from `currentDestination`.
- Callback: indices 0–3 navigate as today; **the PARK index (4, or 3 in
  DISSOLVE − LEDGER) must NOT call `navigateTo`** — it calls
  `onParkSelected` → host opens the drawer. The `else -> VAULT` trap is lethal
  here in a new way: an unguarded PARK index would open the Vault. Same fix as
  K.2: exhaustive arms, PARK arm explicit, `else -> Unit`.
- Every existing `navigateTo(MonolithDestination.GUARD)` caller (header GUARD
  ink, the old drawer's PERMISSIONS chip, `ShieldPermissionPrimerModal`'s
  routes) is repointed to *open the PARK drawer*. `MonolithDestination.GUARD`
  and `SystemGuardScreen` become unreachable in this branch — their removal is
  a separate cleanup commit; the nav spec requires only that no live route
  lands there. Defensive rule: if `currentDestination` ever *is* GUARD, the
  host opens the drawer and the cap goes to PARK — the band never shows nothing.
- Back behaviour: drawer open → Back closes the drawer (cap returns) before
  Back ever means DECK. The existing BackHandler chain already prioritises the
  drawer; PARK inherits that slot.

---

## War line — the nameplate becomes the count

`MonolithPullDownHeader` (`MonolithCustomComponents.kt`) today spends its row on
a brand word, three filled pills (HELD / GUARD / DAY–NIGHT), and a hairline.
It becomes a printed plate carrying the war in one line, with the counts demoted
to quiet ink.

### W.1 The line

```
186D TO NEET · TODAY 2H30 / 12H
```

(Words in Atkinson, numerals in Anonymous Pro — the header's existing
mixed-typography helper, recoloured to ink. Example only: the day count is
computed, §W.2 — with the default date below and today 2026-10-29 the true
figure is **185D**; the synthesis's "186D" was illustrative, and the plate
prints the computed number or nothing.)

- Set letterpressed: word spans in text-secondary ink, numeral spans in
  text-primary ink, one baseline, 12.5sp, no fill, no border, no chip behind
  any part of it. It sits as the header's primary row, left-aligned where
  `MONOLITH` sits today.
- Format rules: days = integer + `D`; TODAY = `2H30` (hours, then two-digit
  minutes), `45M` under one hour, `0H00` at zero — zero prints; the truth at
  03:30 is zero and the plate says so. Target formats by the same formatter
  (`12H`). Separator is ` · `, spaces included, exactly as shown.
- The word `MONOLITH` leaves the header. The machine's name survives on the
  app label and the Deck; the header's job is the count. The 8dp brand pip
  stays as pure geometry (drawer affordance anchor) but its colour is fixed
  `outlineVariant` in both states — it no longer flips to `primaryContainer`
  when a session is active (armed is the lamp's rendering, §Mechanism.4; the
  `• ARMED` suffix on the wordmark dies for the same reason).

### W.2 Data honesty — every number names its source

**NEET days.** Computed as `ChronoUnit.DAYS.between(LocalDate.now(), examDate)`
from a **stored** value, never a hardcoded constant in the composable:

- *Where stored:* the app's only settings row — `GuardSettingsEntity`
  (`guard_settings`, id = 1) — gains `neetExamEpochDay: Long`,
  `neetExamConfirmed: Boolean`, `dailyTargetMinutes: Int`. One Room migration,
  defaults below. (No SharedPreferences exists anywhere in this codebase; Room
  is the store.)
- *Default:* **2027-05-02**, `neetExamConfirmed = false` — early May 2027 is
  the expectation; NTA has **not** officially announced the date.
- *Honesty mechanism:* while `neetExamConfirmed == false`, the days numeral
  prints in **text-secondary ink** (the plate's provisional register) instead of
  primary — the number is visibly a placeholder, not a fact — and the
  correction row (Guard screen in BRANCH–KEEP, PARK drawer in BRANCH–DISSOLVE,
  §D.2 item 4) states the source in words: `NEET DATE — 2 MAY 2027 · NOT YET
  ANNOUNCED`. The day he confirms the announced date, the numeral goes primary
  and the row reads `NEET DATE — <date> · ANNOUNCED`.
- *How he corrects it:* one row, one action — tap the date row, pick the date;
  that single write sets both fields. That is the whole correction path; there
  is no settings maze behind it.
- *Recompute:* on `ON_RESUME` and on date change — the plate is never stale by
  more than a sleep. If the stored date passes uncorrected, the segment prints
  `NEET — DATE TO SET` in place of a negative or clamped lie.

**TODAY.** Minutes from **this app's own session records**, today — the pattern
`MonolithRepository.compute7DayHistory()` already runs, narrowed to one day:

- Query: sessions from `focus_sessions` (DAO `getSessionHistorySnapshot()` /
  `sessionHistoryFlow`) with `startWallTimeMillis` in
  `[startOfLocalDay, startOfLocalDay + 86_400_000)`, summed over
  `elapsedSeconds`, **plus** the live active session's current `elapsedSeconds`.
  New derived flow in `MonolithViewModel`: `todayFocusedMinutes: StateFlow<Int>`.
- A session counts to the day it **started** (named rule; sessions don't cross
  midnight in his day — sleep is 20:00–03:30 — and the rule keeps the number
  stable while he looks at it).
- **Never YPT's number.** YPT tracks his study across everything; Monolith can
  only know what Monolith sealed. Printing YPT's total here would claim a
  measurement this app never made. The war line's TODAY is Monolith's own
  record, labelled by being exactly what its records hold — smaller on a day
  he studied outside the app, and honest.
- **Never `totalFocusedMinutes`.** The existing flow is all-time *and* floored:
  `.coerceAtLeast(35)` with initial value 75 (ViewModel L280–286) — it prints
  35 minutes on a fresh install that has focused zero. The war line must not
  touch it. (The status drawer's `BANKED ${totalFocusedMinutes}m` line spends
  the same floored number — flagged in §Code: fights; its fix belongs to the
  drawer's owner, but it is the same lie and the merge should kill both.)

**12H.** His daily target, `dailyTargetMinutes`, default **720**, stored in the
same settings row, correctable at the same date-row home (one `TARGET — 12H`
row beside it). Not a constant buried in the header.

### W.3 HELD / GUARD — fills die, ink remains

- Both pills lose fill, border, and clip. What remains: the word in
  text-secondary, the count in text-primary mono, baseline-aligned, on the
  header's right, separated by 16dp of air: `HELD 03` · `GUARD 3/4`.
- They stay tappable — invisible hit boxes keep the 32dp height and ≥48dp
  width; HELD opens the status drawer (digest), GUARD navigates
  (BRANCH–KEEP) or opens the PARK drawer (BRANCH–DISSOLVE). Affordance is
  carried by position and the press state (ink deepens to primary while
  pressed), not by a box.
- In BRANCH–DISSOLVE this GUARD ink *is* "status → chrome": the readiness
  count sits on every screen, in the header, with no destination behind it.

### W.4 DAY/NIGHT pill — dies; replaced by nothing

The pill and `onToggleTheme` leave the header. **Nothing replaces its
information in chrome, and that is justified, not an omission:** the scheme is
the surface itself — a screen visibly in night ink announces itself; a pill
announcing it is a second rendering of a fact the whole display already states
(one fact, one rendering). Theme is now the schedule's output (Sten owns the
rule; mailed, thread `theme-flip-chrome`), not a state Sarthak manages from
chrome. The single invisible fact a schedule can hide — *an override is being
held* — gets exactly one printed home, and it is not the header: the Guard
screen (BRANCH–KEEP) or the PARK drawer (BRANCH–DISSOLVE), and only if Sten's
rule keeps an override at all.

**Flip behaviour (chrome's side of Sten's rule):** when `effectiveNight`
flips, band and war line swap tokens on the same frame as the rest of the app
— no colour animation, no fade, no beat. Colour is print, not light. A cap
mid-travel keeps travelling; positions, partitions, and the animator are
untouched by a token swap. The view already re-invalidates on `isNightMode`;
the spec adds only: the flip never restarts, delays, or re-prices a travel in
flight.

---

## Motion (curve + ms)

All travel is `ValueAnimator` on `capPos: Float` (stop units), the knob's
pattern generalised to a non-uniform scale: `onDraw` maps `capPos` → x by
piecewise-linear interpolation over `stopCenters` (zero allocation in
`onDraw` — preallocated arrays, the file's standing discipline).

| Beat | Curve | Ms | Notes |
|---|---|---|---|
| Cap travel, \|Δ\| ≤ 1.5 stops | **CurveB** (0.05, 0.7, 0.1, 1) | **280** | the knob's own travel (flat 280 there) |
| Cap travel, \|Δ\| > 1.5 stops | **CurveB** | **420** | distance-priced: at 280 a 4-stop throw compresses passing ticks to ~70ms apart — mush; 420 spaces them ~105–140ms, legible as events. (Marko mailed — his band has no tick obligation, so his flat 280 stands; divergence is the tick's, not taste's.) |
| Settle after drag release (≤0.5 stop) | CurveB | **180** | the remainder of a drag is a seat, not a journey |
| Cloister spring-back | **CurveA** (0.2, 0, 0, 1) | **180** (twitch: 120 out / 180 back) | a refusal returns fast and plainly; CurveB's long glide would read as travel |
| PARK engage / return | CurveB | 280 / 420 by distance, same table | PARK is a position like any other |
| Cap fade (legacy −1 only) | CurveA | 180 | `capAlpha` animator; only if a host ever sends −1 again — retained API safety, used by neither branch |
| **No overshoot, anywhere** | — | — | detents do not bounce past their stop; `MechanicalOvershoot` belongs to spring-back sleds, a different physical claim |

- **CurveB, not CurveA, for travel:** CurveA is indicator easing (fast-out,
  abrupt tail). CurveB is what this codebase already uses for a cap travelling
  to a stop (`RombiKnobView.animateCapTo`) and the wheel's settle — fast
  launch, long mechanical glide, soft seat. Same control family, same motion
  grammar.
- **First layout snaps.** When the view is created or the host first sets
  `selectedTabIndex`, `capPos` starts *seated* at that stop — no travel from 0
  on cold start, no travel on recomposition. Travel is for changes, not arrivals.
- **Superseded animators:** each `animateCapTo` captures its target; the end
  listener fires the seat feel only if the captured target still equals
  `selectedTabIndex` (and, in BRANCH–DISSOLVE, the PARK state). A cancelled
  animator seats nothing and sounds nothing.
- **Passing ticks** fire from the animator's update listener when
  `round(capPos)` changes mid-travel (and during drags, §Mechanism.6) —
  the hand hears the cap pass each stop on a long throw.

### Screen transitions (the cap is the cue)

- **Content crossfades; it does not slide.** The panels are switch positions,
  not pages in a row — sliding content would claim a spatial adjacency that
  doesn't exist (SYSTEM is not "to the right of" FOCUS in any sense but the
  scale). The cap's travel already carries direction and distance; the content
  owes only a clean exchange. Direction logic: **none**, deliberately.
- Timing retunes from the current `AnimatedContent` spec
  (`fadeIn(specB 420)` together with `fadeOut(specA 260)`): **fadeOut 180ms
  CurveA, fadeIn 280ms CurveB**, both starting on the UP that commits the
  navigation. On short hops the new panel settles as the cap seats (280 ≈ 280);
  on long throws the panel is already home while the cap is still travelling —
  correct: the switch drives a relay, the room lights before the switch
  finishes its throw. Navigation fires immediately (§Mechanism.6); chrome
  never gates content, so the ARM ≤10s standard pays nothing here.
- Back-to-DECK uses the identical crossfade; the cap travelling home is the
  only directional signal, and it's enough.
- The war line does not transition. Its numbers re-strike in place when their
  sources change (computed numbers never roll through intermediate values).

---

## Haptics (per beat)

All feel routes through `fireFeel(hapticMs, freqHz)` → `TactileSoundEngine`.
`fireFeel`'s contract (code L199–211) is the law of this table: **only the 1L
branch consumes pitch** (`playRotaryDetent(freqHz)`); 4L and 8L play canned
effects and ignore any frequency passed. No new engine methods.

| Beat | Call | Waveform (engine) | Why |
|---|---|---|---|
| Finger DOWN on band | `fireFeel(1L, 460f)` | detent one-shot | pre-click — "finger is on the switch"; quiet enough to fire on every press, including presses that end as drags, refusals, or same-stop taps |
| Cap passes a stop (animator or drag) | `fireFeel(1L, 500f + stop×40f)` → 500/540/580/620/660Hz | detent tick at pitch | the ladder wave-1 intended, routed through the only branch where pitch survives; ascending pitch = ascending position — position is audible |
| Seat on a destination stop | `fireFeel(8L)` | `playFirmClick()` | the detent thunk; identical to `RombiKnobView`'s seat — same family, learned once. Pass no frequency (8L ignores it; a comment in code must say so, so nobody re-adds the dead ladder) |
| Seat on GUARD (BRANCH–KEEP) / PARK (BRANCH–DISSOLVE) | `fireFeel(4L)` | `playMechanicalLatch(true)` | the hatch unlatches — a different species from a panel seat, matching the reduced slot on the other side of the divider |
| Cloister refusal (§State machine) | `fireFeel(1L, 300f)` | single low detent tick | one tick, below the passing ladder's floor — reads as *denied*, not as travel; no seat sound, because no seat happened |
| Same-stop tap / drag settles home / CANCEL | nothing past the DOWN tick | — | non-events don't clunk |

## Sound (primitive + pitch)

Sound is the same events as haptics — one call site, one owner (the view).
Engine primitives only; no new methods, no new files:

| Beat | Primitive | Pitch / character |
|---|---|---|
| DOWN | `playRotaryDetent` | 460Hz, 6ms transient, gain 0.38, waveform `[0,7,5] @ [0,180,70]` |
| Passing tick | `playRotaryDetent` | `500 + stop×40` Hz, same transient; engine clamps 280–1480Hz, the ladder sits well inside |
| Seat, destination | `playFirmClick` | 12ms @ 620Hz, gain 0.5, waveform `[0,13,20,8] @ [0,225,0,135]` — hard strike, breath, softer seat |
| Seat, GUARD/PARK | `playMechanicalLatch(true)` | 16ms @ 760Hz two-stage, waveform `[0,11,26,9] @ [0,215,0,150]` |
| Refusal | `playRotaryDetent` | 300Hz, single tick |
| Theme flip / drawer open-close / count changes / lamp | **silent** | chrome does not narrate bookkeeping |

**Double-fire to kill (host side):** `MonolithViewModel.navigateTo()` currently
plays `playRotaryDetent(540.0)` on *every* navigation (ViewModel L357–367) — so
a nav tap today sounds the view's feel *and* the ViewModel's detent. Synthesis
item 4 already ruled: one gesture, one owner. Nav-originated navigation must
not fire the ViewModel detent (flag or parameter on `navigateTo`); the band's
DOWN tick is the gesture's sound. Non-nav navigations (Deck → TASKS buttons,
Back) keep the ViewModel's detent — they have no view of their own to speak.

---

## Depth & light

- **Recess, never shadow, for the band.** The strip is found by a finish
  change on the same plane: one tonal step + the seam (§Mechanism.1). It
  casts nothing and carries no elevation; at night it must never become the
  brightest mass on screen — the failure the old capsule's peach pill
  committed and the interim `tokControlFill` pill only half-fixed (a 52dp
  filled block still out-weighs printed glyphs; the band's largest fill is
  now the 30dp cap).
- **The cap is the only raised object.** Pooled contact shadow `0x38000000`,
  offset +2dp Y, radius 8dp; lit top edge `0x59FFFFFF`, 1.1dp, inset 14dp from
  both ends — both verbatim from the knob cap. The shadow hugs the cap; it is
  a contact shadow, not a drop shadow, and it does not grow with travel.
- **Light is fixed; it carries no data.** The lit edge and the slit's light
  line are constants of the drawing, not functions of state — they do not
  brighten on selection, armed, or proximity. Selection is position; armed is
  the lamp; nothing else glows.
- **Night staircase compliance:** ground `#171412`; strip = first step above
  ground (≥1.35:1); cap fill capped at `#4A423C`; text tiers 11 / 5.8 / 3.5 —
  selected legend/numeral at the 11 tier (`tokTextPrimary()`), unselected at
  5.8 (`tokTextSecondary()`), locked legends under Cloister at the 3.5
  decorative tier (§State machine). No cream fills at night; cream is a text
  tone. Day mode mirrors the same tier *roles* through the day tokens.
- **Cap marker colour — wave-1's flagged deviation: KEPT, with reason.**
  Marker = `tokTextPrimary()` at night, `tokOnPrimary()` by day. The knob's
  precedent is `tokOnControlFill()` (cream in both modes, after its own Round 0
  fix found `tokOnPrimary()` at night to be near-black `#1B110D`, ~2:1 on the
  `#4A423C` cap). `tokTextPrimary()` at night (`#F8F5F5`) sits in the same
  cream family but is the staircase's 11:1 text tier — the marker is the
  band's single state mark and should pair with the printed scale's ink, not
  with a label token. If the knob cohort wants the same line, it's the same
  one-line resolver there; divergence until then is recorded, not drift.

---

## State machine

Nav states (view-local; the host owns destination and session truth):

| State | Entered by | Cap | Feel/sound | Exits to |
|---|---|---|---|---|
| **SEATED(i)** | arrival, travel end, settle end | at stop i, full width for i | — | DOWN → PRESSED; host set → TRAVELLING |
| **PRESSED(i)** | DOWN in partition i | still seated (finger hasn't moved) | DOWN tick 460Hz; stop i's print deepens | MOVE → DRAGGING; UP same stop → SEATED (silent); UP other partition → commit |
| **DRAGGING** | MOVE past touch slop | follows finger (inverse map, clamped to scale ends) | passing ticks at each `round()` change | UP → TRAVELLING (settle ≤0.5 stop, 180ms) + navigation if target ≠ selected; CANCEL → TRAVELLING home, silent |
| **TRAVELLING(t)** | host set, tap commit, Back | animator running to t | passing ticks; seat feel on arrival (target-guard checked) | end → SEATED(t) / PARKED; new set → TRAVELLING(t′) (old animator cancelled, no seat) |
| **PARKED** *(BRANCH–DISSOLVE only)* | PARK tap; drawer opens | seated at PARK detent | latch seat (4L) on arrival | drawer closes → TRAVELLING back to destination stop |
| **CLOISTERED** | host: session becomes active | locked at FOCUS (see below) | refusal tick only | session ends → SEATED(FOCUS), lock lifts silently |
| **UNSEATED (−1)** | host sends −1 (neither branch does) | fading out via `capAlpha` | silent | any valid set → TRAVELLING |

**Under Session Cloister** (Freya owns the rules; the nav owns behaviour —
mailed, thread `cloister-nav-lock`; the lock is implemented as host-supplied
data, `lockedStops: Set<Int>`, so her rule can change without a nav redesign):

- Default lock set: **every stop except FOCUS**, for exactly as long as
  `activeSession.isActive`. PARK locks too (BRANCH–DISSOLVE) — the drawer is
  a mid-session decision, and HOLD allows none.
- Visual state: locked stops' numerals + legends drop to the 3.5:1 decorative
  tier; ticks and slit stay printed at full strength (the scale doesn't lie
  about what exists). The cap sits at FOCUS; the lamp is lit (same fact, its
  own rendering). Nothing is greyed with a Material "disabled" wash — ink
  steps down, that's all.
- If a session arms while he's seated elsewhere (routine fire, or arm from a
  non-Deck surface): host force-navigates to DECK, the cap travels home at
  normal distance pricing, and the lock engages on arrival. A session never
  begins with the cap parked on a locked stop.
- Refusal feel: tap on a locked stop — DOWN tick, then the cap **twitches**
  0.12 stop-units toward the stop (120ms CurveA out) and springs back (180ms
  CurveA), one refusal tick at 300Hz at the twitch's peak, no navigation, no
  seat sound. Drag against the lock: cap follows at **0.18× gain**, hard wall
  at 0.22 units; release springs back at 180ms CurveA with the same single
  refusal tick (once per gesture — a held, straining finger does not machine-
  gun ticks). HOLD is absolute, but the switch still answers the hand: a dead
  switch reads as broken; a switch that moves a little and refuses reads as
  locked. (Freya may strike the twitch for zero-motion — the state machine
  survives either way; the tick + no-seat is non-negotiable, because a seat
  sound would lie.)
- The emergency valve is untouched by all of this: it lives on Deck (Ines's
  seat), the cap is already home when it's needed, and the lamp stays lit
  until the session record actually ends — the lamp is slaved to the record,
  not to the valve's progress, so it cannot contradict the banked-hold state.

---

## Edge cases

- **Theme flip mid-travel / mid-drag:** token swap on the flip frame;
  `capPos`, partitions, and animator timing are geometry, untouched. A drag
  in progress keeps its finger mapping.
- **Resize / rotation mid-travel:** `onSizeChanged` recomputes `stopCenters`;
  `capPos` is in stop units and survives; the animator continues to its stop.
- **Process death / recomposition:** host re-sets `selectedTabIndex`; first
  set snaps (§Motion) — the band never replays a journey he didn't take.
- **Rapid tapping across stops:** each UP commits immediately; animators
  supersede cleanly (captured-target guard); at most one seat sound, for the
  stop he actually lands on.
- **Drag past either end:** hard clamp at the end centres — no overshoot, no
  tick beyond the last stop, no wrap.
- **Font scale 1.3×:** numerals/legends scale (sp), cap and slit don't;
  shortest legends (`GUARD` in its `0.66u` slot ≈51dp) still fit at 11sp×1.3.
  If a future locale lengthens a word past its slot, the word truncates —
  the cap never resizes for text.
- **Counts change while seated:** reprint in place, silent, no animation —
  computed numbers re-strike, never roll.
- **War line at boundaries:** midnight rollover recomputes days + TODAY on
  resume; TODAY during a live session includes the active session's elapsed
  seconds and ticks up only when the header recomposes (no per-second
  reprint — the plate is a reading, not a stopwatch; the session's own
  screens own live time).
- **Exam date absent/corrupt:** `neetExamEpochDay <= 0` → the segment prints
  `NEET — DATE TO SET`; the plate never invents a date.
- **`guardReadinessBadge = -1`** (host hasn't wired it): GUARD legend prints
  bare — absence, not `−1/4`.
- **Sound off:** `fireFeel` still routes through the engine, which owns the
  `soundEnabled` gate; haptics ride the same call — if the engine is muted,
  the band is silent *and* still, by design (one owner, no side channels).

---

## Code: helps / fights

### Helps (build on these)

- `BlockitCoreViews.kt:199–211` — `fireFeel` with its honest contract comment:
  pitch lives only in the 1L branch. The whole feel table (§Haptics) is
  routable today, zero engine work.
- `BlockitCoreViews.kt:128–133` — `tokControlFill()` (night `#4A423C`) and
  `tokOnControlFill()`: the X1 discipline already exists as helpers.
- `BlockitSuitcaseAndSwitchesViews.kt:172–302` — `RombiKnobView`: the proven
  slit/cap/marker module, `animateCapTo` at 280ms CurveB, seat via
  `fireFeel(8L)`. The band is this, scaled.
- `MonolithCurves.kt:225–253` — CurveA/B/C + durations {180, 260, 280, 420,
  520} + `specA/specB/specC`: every motion in this spec is already in the file.
- `MainActivity.kt:208–221` — `guardReadinessCount`, `blockedAppsCount`,
  `activeTasksCount` already computed in the host; the band needs one new
  view var and one `update {}` line, not new plumbing.
- `MonolithRepository.kt:1119+` — `compute7DayHistory()`'s day-filter pattern
  (`startWallTimeMillis in [startOfDay, +86_400_000)`, sum `elapsedSeconds`)
  is exactly the TODAY query, narrowed.
- `MonolithModels.kt:449–453` — `guard_settings` singleton row: the natural
  home for the exam date + target (§W.2).
- `MonolithCustomComponents.kt` header — `rememberMixedTypographyText`
  already does word/mono spans; the war line is a recolour + recontent of
  the existing header, not a new component family.
- Real font files (Round 0b): Atkinson Bold + Anonymous Pro Bold exist as
  files and `paintWord`/`paintMono` already bind them — the letterpress
  hierarchy in this spec is real weights, not smear.

### Fights (name them before they bite)

- `BlockitCoreViews.kt:827–1009` — the current `MonolithBottomNavView`:
  capsule geometry, icon `when` block, `animatePillTo` (260ms CurveA glide),
  `onTouchEvent` partition math `(width − 2×gapUnit)/4` that ignores drawn
  slots (N9). Rewritten wholesale per §Mechanism/§Motion; `tabLabels`,
  badge vars, and `onTabSelected` API are kept.
- `MainActivity.kt:223–229` — `GUARD -> -1`: the vanishing state (N6).
  BRANCH–KEEP: `-> 4`. BRANCH–DISSOLVE: no arm; PARK is drawer state.
- `MainActivity.kt:491–499` — callback `else -> MonolithDestination.VAULT`:
  **the trap.** Any new index (4 in KEEP, PARK in DISSOLVE) is silently
  swallowed by VAULT. Nav + host mapping land in ONE commit, exhaustive
  arms, `else -> Unit`.
- `MainActivity.kt` nav host — `Modifier.height(80.dp)` → 72.dp, same commit.
- `MonolithViewModel.kt:357–367` — `navigateTo()` plays
  `playRotaryDetent(540.0)` unconditionally: double sound on every nav tap
  (§Sound). Needs a nav-originated path that stays silent.
- `MonolithViewModel.kt:280–286` — `totalFocusedMinutes` is all-time and
  floored at 35 (initial 75): a printed lie the war line must never reuse
  (§W.2). The status drawer's `BANKED …m` line spends it too.
- `MainActivity.kt:226` area — `effectiveNight = darkTheme || sessionActive`:
  `darkTheme` is an in-memory `MutableStateFlow` flipped only by the dying
  pill. Sten's schedule rule replaces its source; chrome consumes the
  boolean and doesn't care — but the pill's removal and the rule's arrival
  must land together or the app loses its only theme control.
- `MonolithCustomComponents.kt` header — the brand pip's
  `primaryContainer`-when-active is a second orange spend in chrome; dies
  with §W.1. `BlockitActionChip`'s `isAccentArmed` path exists in the same
  file — out of this spec's surface, flagged for the accent-budget audit.
- Legacy −1 handling (view setter): kept as a fade (§Motion), but neither
  branch's host may send it — if a future host does, the band shows *no cap*,
  which is a lie by absence; treat any −1 in logs as a host bug.

---

## Law check

- **ARM ≤10s:** chrome adds zero gates. Navigation commits on UP, content
  crossfades immediately, cap travel never blocks input or the arm path; the
  band is tappable throughout its own animations. Switching panels costs one
  tap, as today.
- **HOLD absolute:** §State machine's Cloister — every non-FOCUS stop locked
  for the session's duration, refusal that answers the hand without offering
  a decision, PARK/drawer locked too, lamp slaved to the session record so
  chrome can't contradict the banked emergency valve.
- **TELL THE TRUTH once, in human language:** the war line prints three
  numbers, each with a named, stored, correctable source (exam date +
  confirmed flag; the app's own session records; the stored target) — and
  visibly demotes the one number whose source is unconfirmed. No number is
  borrowed from YPT, floored, or rolled through values it never held.
- **One fact, one rendering:** selection = cap position only (no highlight
  fill anywhere); armed = the lamp only (header pip + `ARMED` word killed);
  counts = printed ink only (no badges); theme = the surface itself (pill
  killed, nothing replaces it); Guard readiness = one home per branch
  (band legend in KEEP, header ink in DISSOLVE — in DISSOLVE the band has no
  Guard stop to duplicate it).
- **Beauty is retention:** the band is a machined switch he operates ten
  times a day — slit, cap, detent ticks, distance-priced travel — not a
  Material dock with a theme on it. The war line is letterpress, not a
  dashboard widget.
- **Rested decides, tired executes:** everything decidable (exam date,
  target, theme rule, Guard's contents) lives in rested-hours surfaces; at
  03:30 the chrome asks nothing — it states the count and seats the cap.
- **Accent budget:** chrome orange = one 3dp lamp, conditional, at FOCUS.
  Counts and badges in ink. Never green, anywhere, in either mode.
- **Fonts:** real Bold files only (Atkinson Bold, Anonymous Pro Bold);
  hierarchy by weight + ink tier, never by fake-bold on the legends
  (numerals' emphasis comes from ink tier + position).
- **Night staircase:** ground `#171412`, steps ≥1.35:1, text 11 / 5.8 / 3.5,
  fills capped at `#4A423C` — §Depth & light, row by row.
- **Open picks untouched:** BRANCH–KEEP and BRANCH–DISSOLVE are both fully
  specced, neither recommended over the other in this document; Ledger's
  stop is branched inside both. Those two calls are Sarthak's.

— Otis, CHROME / NAV / HEADER, plan wave.
