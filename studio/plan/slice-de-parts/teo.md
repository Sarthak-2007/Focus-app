# TEO — Wild, nature (seat 13) · Slice D/E joint: THE STILLNESS AUDIT + THE PRIMED IDENTITY, as build law

*My fence: the SLIDE + STRIP zone only. Hero and FINISHES are Slice A law and
are touched here only where their wiring crosses my fence. Sources read in
full: BEYOND-deck W1 (04:58 frame, 19:30 frame, my NONE, the stillness law)
and W4 (imprint passage), REGISTER.md, my wild-seats/13-teo.md and
beyond-deck-parts/teo.md. Code audited in the real tree (not builds/):
`ui/screens/TactileDeckScreen.kt` (534 lines, whole),
`ui/MonolithViewModel.kt`, `ui/views/BlockitCoreViews.kt`
SlideToStartView (1046–1231), plus the host call in MainActivity.kt.*

---

## 1. VERDICT

**The law holds as filed. No amendment to W1/W4 wording is needed — but
today's code does not yet hold it, and one clause needs a build-law
sharpening (not a rewording) so the builder cannot break it by accident:**

> **Sharpening S-1 (build law, added under W1's stillness law):** priming
> is a *data* event, never a *rendering* event. No `isPrimed` / routine /
> provenance boolean or object may be passed to the slide view, the strip,
> the stamp, the imprint slot, or the sentence slot. A prime may reach my
> fence only already-converted into the zone's own facts: the loadout
> behind the imprint count (and the strip APPS count). If a future diff
> shows a primed flag entering the zone, it is a stillness violation on
> sight, whatever it was going to draw.

Why the law survives contact with the code: the slide view itself is
honestly built (§3, clearances C1–C4) — it has no clock, no animator at
rest, and guarded setters. Every violation found lives in the *host
wiring* around it or in elements the merged plan adds that do not exist
yet (strip, stamp, imprint slot, sentence slot — all absent from code).
And the deepest finding is structural: **priming does not exist in the
app at all.** No routine, prime, or provenance reaches the Deck
(MainActivity.kt:166 collects `routines`; the Deck call at :281 passes
none of it; TactileDeckScreen.kt:74–123 has no such parameter). The
primed identity therefore holds today *vacuously* — the slide is
identical primed vs unprimed because the Deck cannot tell the
difference. That is the right shape accidentally. Slice D/E must keep
it deliberately, per S-1, while building the prime everywhere *outside*
my fence (hero, hub, wheel, NEXT word-line — W1's contents).

**Violations found: 6 (V1–V6). Total fence price: ≈9 hours (§5).**

---

## 2. THE ELEMENT-BY-ELEMENT INPUT TABLE — what each element may read

Rule for reading the table: an element may read **only** the inputs in
its row. "Fact change" means the underlying fact itself changed (wheel
turned, permission died, poll returned, loadout edited rested) — a
number changing for any other reason (timer, recomposition, a collector
emitting, a prime landing as a flag) is a leak, named in §3 or banned
here. Primed↔unprimed differences allowed in the whole zone: **exactly
two, both numerals, both the same rule** — the loadout behind the count
(imprint numeral; strip APPS numeral). Everything else: byte-identical.

| Element | Idle–unprimed may read | Idle–primed may read | Allowed primed↔unprimed difference | Mid-press may read | Sealed |
|---|---|---|---|---|---|
| **Cap** (thumb/sled) | nightMode (theme fact); `isArmedState=false` constant; own dragFraction=0 | identical list — **no prime input** | **NONE** — tone, geometry, light identical (W1) | dragFraction; verdict travel-limit from readiness store (OFF-stop 0.65 / Park at catch / seat) — event-driven only | zone leaves the idle Deck; cap never re-renders a session fact |
| **Track** (slot, skirt, gate notches) | nightMode only; notches are static geometry at catch 0.84 (REGISTER) | identical | **NONE** | trail extent as pure f(dragFraction); notch coordinate never moves | — |
| **Badge** (`subMonoBadge`) | **DIES (Aldo's joint).** Row kept only to record the host wiring (§3 V1): today it reads `dialMinutes` alone | — | — (absent in both states) | — | — |
| **Cap words** (label) | a compile-time constant string. **No state input at all** — not dial, not loadout, not readiness, not prime | identical constant | **NONE** — same string, same alpha | string and alpha frozen (§4 A4); the cap may *occlude* the words by passing in front of them — occlusion is geometry, fading is print changing | — |
| **Strip cell 1 — BLOCK** | readiness store only: BLOCK state + proof grade. Refresh events: Deck entry, resume, permission broadcast, 20s foreground poll, press-poll. Never a clock, never dragFraction | identical — priming is not a blocking fact | **NONE** | a press-poll verdict landing is a fact change: cell may flip once, whole, at that frame; never graded by drag position | strip is Deck furniture; on seal it hands over and freezes/leaves with the idle Deck |
| **Strip cell 2 — APPS** | the count of the loadout the next arm will use (selected loadout), frozen between loadout edits | the same cell against the **routine's loadout** — the W4 imprint rule applied to the strip's own count | **Numeral only, only when the two loadouts' counts truly differ.** Form, tone, position identical | **frozen** — no recount mid-slide, even if a poll returns | frozen at the seat-frame count for the session's life (W4 freeze rule) |
| **Strip cell 3 — TIME** | the finish, computed once from the chosen total by the one formula `n × block + (n−1) × 10` (REGISTER); recomputes only when the total fact changes, all at once (W3 RE-SET) | the same computation against the primed total | the time itself, only because the total fact differs — never a countdown, never a "starts in …" | **frozen** | — |
| **Stamp** (on the strip's seam) | last poll instant + proof grade (`PROOF: END-TO-END` / `SELF-REPORT`), age **ceiling-rounded to the next 5s** (W4) | identical | **NONE** | **frozen** — a press-poll may re-stamp at its return frame, once | — |
| **Imprint slot** | idle tenant `{n} APPS WILL SHUT`, n = next-arm loadout count (snapshot, §3 V6); slot geometry fixed, silence = HELD, Δy=0 | **the identical string** against the routine's loadout (W4, verbatim rule): no time, no name, no countdown, no provenance word | **Numeral only**, under the same loadout rule as APPS | **NO CHANGE MID-SLIDE, IN ANY CONDITION** (W4, verbatim) — not for a verdict, not for a refusal, not for a count landing | tenant swaps once at the seat frame to `{n} APPS HELD`, count frozen at the seat frame for the session's life |
| **Sentence slot** (failure sentences, 15sp; incl. Rosa's reserved below-track refusal slot — two tenses, one readiness store) | readiness failure class only (cause-worded: OFF / unverified / can't-check), or silence | identical — a failure is a failure in both states | **NONE** | refusal tense (`— NOTHING WILL BE HELD.`) may appear **only at the refusal frame** (interlock stop / deadline), never progressively, never as a warning on the way | SEALED evicts even TRUTH tenants mid-session (W4 tenancy) |

**Proof of the primed identity, element by element:** cap — no prime
input exists in its argument list (S-1 keeps it that way): identical.
Track — same: identical. Cap words — constant: identical. BLOCK cell,
stamp, sentence — their only source (readiness) does not know priming
exists: identical. APPS cell, TIME cell, imprint — these read the
*session-to-be*, and priming legitimately changes which session that
is; what priming may never change is their **form**: same string, same
cell, same formula, with the loadout/total swapped underneath. That is
exactly W4's imprint sentence generalised one row wider, and it is the
whole of the allowed difference. **The one leak this proof cannot yet
run against real primed code** is that there is no primed code — the
proof is against the table; §4 A8 makes it executable the day the prime
lands.

---

## 3. VIOLATIONS IN TODAY'S CODE (file:line) + FIX PRICES

### Violations

**V1 — The badge carries a live, second, conflicting number in the zone.**
Host: `TactileDeckScreen.kt:498` —
`view.subMonoBadge = if (dialMinutes < 10) "FLOW" else "${dialMinutes}M"`;
drawn at `BlockitCoreViews.kt:1124`, field/setter at :1056–1063.
The badge reads *per-block* dial minutes alone: on the primed 3×50
frame it would print `50M` inside the slide while the hero (one formula,
REGISTER) says 170 — two numbers, one gesture, different facts. It also
re-renders on every wheel detent, i.e. the zone's print changes with a
fact that already has three lawful homes outside the fence. Aldo's
joint kills the badge; the host wiring confirms the kill is safe —
nothing else reads it.
**Fix:** delete the assignment (Deck:498), the field, setter and draw
call (BlockitCoreViews:1056–1063, :1121–1125). **Price: 0.5h.**

**V2 — The cap words fade mid-slide: print changes as a function of drag.**
`BlockitCoreViews.kt:1111` —
`textAlpha = ((1f - dragFraction * 1.3f) * 255)`, applied to label
(:1113) and badge (:1121). From the first millimetre of travel, every
printed word in the zone is continuously changing. That is a mid-slide
change to print, banned by §4. (The trail behind the cap, :1102–1108,
is *not* charged: it is cap-travel rendering, a pure function of
position, in the allowed column.)
**Fix:** pin alpha at 255 from DOWN to the seat/refusal frame; let the
cap occlude honestly. If occlusion legibility fails on device, the
remedy is label placement, not alpha. **Price: 0.5h** (3 lines + device
check). *This is my fork for Ren — §7.*

**V3 — Commit coordinate, commit-on-release, and no thumb hit-test.**
REGISTER: CATCH 0.84, commit = displayed progress crossing 0.84 *under
contact*. Code: the gate constant is **0.82** (`BlockitCoreViews.kt:1200,
1205`), commit is evaluated only on **release** (:1177,
`ACTION_UP`/`ACTION_CANCEL` at :1175–1192), and `ACTION_DOWN`
(:1160–1168) starts a drag from *any* x with no thumb hit-test —
`updateDrag` (:1197–1211) centres the fraction on the touch, so a tap
near the track's right end jumps the fraction past the gate and a
release commits with zero travel. Inside my fence this matters as
freeze law: the printed notch (0.84, D4 as re-coordinated) and the felt
commit (0.82) are one fact at two numbers, and "mid-press" currently
includes a state — tapped-to-the-end — that the travel grammar says
does not exist.
**Fix:** one constant 0.84 from REGISTER; commit on crossing under
contact; thumb hit-test on DOWN (tap on cap = press path per W4's tap
mechanism, tap elsewhere = nothing). **Price: 2.5h** in the one view,
incl. gesture tests.

**V4 — On commit, the cap visibly returns to rest.**
`BlockitCoreViews.kt:1186`: after `onSlideCommitted?.invoke()`,
`animateSpringBack(0f)` runs unconditionally (:1214–1225). The gesture
of no return returns — and if the arm is then refused upstream
(pre-flight), the idle Deck is back pixel-identical with no trace that
a commit was spent, a silent un-happening inside the zone.
**Fix:** hold the cap at the seat until the host leaves idle; spring
back only on an explicit host "arm refused" signal. **Price: 0.5h.**

**V5 — No prime input exists, and a live collector can overwrite the
dial behind any future prime.**
Deck signature `TactileDeckScreen.kt:74–123`: no routine/prime
parameter; MainActivity passes none (call at MainActivity.kt:281;
`routines` collected at :166 goes elsewhere). Meanwhile
`MonolithViewModel.kt:304–312`: an init collector on `activeTask`
writes `_dialMinutes.value = task.estimatedMinutes` whenever the task
changes and no session is active. Today that collector is the dial's
second writer; the day Slice D/E seats a prime through the same
dial/blocks flows, a task emission (e.g. the seeding sync at
`MonolithViewModel.kt:294`) can re-price the offer — hero, badge,
finish — *after* the prime, with no hand on the wheel. A number
changing without its fact changing, at exactly the frame W1 protects.
**Fix (my fence only):** single-writer law — the prime function and
the user's hand are the dial's only writers; the activeTask collector
must not write dial while a prime is live, and the builder must say in
the prime's diff which writer owns each field. **Price: 1h** (guard +
comment-as-law). Building the prime itself is Slice D/E's main price,
not charged here.

**V6 — The proto-imprint reads the wrong count, from a live flow, in
the wrong home.**
`TactileDeckScreen.kt:120`: `blockedCount = apps.count { LOCKED }` —
the *global* blocked count over the live apps flow; printed at :219 as
`WILL SHUT $blockedCount APPS` in the instrument eyebrow. Three faults
against W4: (a) the tenant's count must be the *loadout's* count —
global ≠ loadout, and primed it must be the routine's loadout, so this
string would differ from the lawful tenant in both states; (b) it is
live — `ensureSeededAndSyncPackages()` (MonolithViewModel.kt:294,
again :1063) re-emits apps after first paint, so the number can change
on the settling frame with no arm-relevant fact having changed;
(c) it lives in the eyebrow, not the imprint slot under the track —
when the real slot is built, this line must not survive as a second
rendering of the count.
**Fix:** delete the eyebrow rendering; the imprint tenant (when built)
reads a **count snapshot frozen at prime/arm-attempt time** from the
loadout that will seal, never the live apps flow. **Price: 2h** once
the prime snapshot exists; if it doesn't, add a half day for the
snapshot plumbing (not in my total — flagged, §6).

### Clearances (audited, clean — the builder should know what not to touch)

- **C1 — The slide view animates nothing at rest.** SlideToStartView
  (BlockitCoreViews.kt:1046–1231) contains no Handler, Choreographer,
  clock read, or self-scheduling invalidate. Its only animator
  (spring-back, :1214–1225) is created solely from touch handlers.
  `onDraw` is a pure function of `dragFraction`, the three host
  properties, and `isNightMode`. **Idle stillness of the view: PASS.**
- **C2 — Its setters cannot be spammed into motion.** `labelText`
  (:1048–1054), `subMonoBadge` (:1056–1063), `isArmedState`
  (:1064–1072) and the base `isNightMode` (:104–110) all invalidate
  only on actual change. Host re-composition ≠ redraw. (This is also
  the tripwire: any *unguarded* future setter breaks stillness
  silently — §4 A3/A7 exist because of it.)
- **C3 — The 1-second ticker does not reach the idle slide.**
  `MonolithViewModel.kt:316–352` (`startTickerLoop`, `delay(1000L)`)
  writes only: session progress (only when a session is active — the
  slide is then uncomposed), the peek pill, and the burn-in step every
  60 ticks. None of these is a Deck argument. **No per-second
  recomposition of the idle Deck originates in the ticker. PASS** —
  with the standing warning in §6: the strip's freshness must never be
  wired to this loop.
- **C4 — Slide tone cannot differ primed vs unprimed today.**
  Host passes `isNightMode = effectiveNight` (Deck:494), and idle
  `effectiveNight = isDarkTheme` (Deck:119, since `isSessionRunning`
  is false in both idle states). The only other host arguments are
  constants: `labelText` (Deck:497), `isArmedState = false` (:496).
  The badge (V1) was the sole variable print in the zone.
- **C5 — Strip, stamp, imprint slot, sentence slot: absent, therefore
  silent.** No such view or composable exists in the Deck; nothing in
  the zone polls, summons, or counts down. Their stillness is §2's
  build law, not a clearance of shipped code.

---

## 4. THE MID-SLIDE FREEZE — testable assertions

**A press may change:** cap position (dragFraction, incl. tap-mesh and
seat spring travel) · trail extent (pure f of position) · zone *feels* —
gate tick at the 0.68 crossing, bucket feels, catch/latch knock, all
haptic/sound only · the marker's thickening at the catch (cap geometry,
filed) · the verdict-driven travel limit itself (OFF-stop at 0.65,
Park at the catch, seat) · strip BLOCK cell and stamp, **only** at the
frame a press-poll verdict actually lands (fact change, whole flip).
**A press may NOT change:** any printed word, numeral, alpha, tone, or
position anywhere in the zone — label, imprint, strip words/counts/
times, stamp, sentences — from ACTION_DOWN to the seat or refusal
frame. The imprint's ban is absolute, in any condition (W4).

Assertions (each is a test, not a sentiment):

- **A1 — Cruise stillness.** Idle Deck, primed and unprimed, 5s
  screenshot-diff = empty (W1's own acceptance check), and 60s diff =
  empty except a theme change the user made.
- **A2 — Primed identity.** Pixel-diff of the slide+strip zone, primed
  vs unprimed, same device/theme/loadout-counts-equal = **empty**. With
  differing loadout counts, the diff contains **only** the imprint and
  APPS numerals.
- **A3 — Setter silence.** Instrumented build: in 60s idle, no
  SlideToStartView setter is invoked with a new value, except
  `isNightMode` on a user theme change.
- **A4 — Word freeze mid-press.** Label glyph pixels outside the
  cap-occluded region are identical at dragFraction 0 / 0.34 / 0.67 /
  0.83. (V2 fails this today.)
- **A5 — Number freeze mid-press.** Every numeral in the zone is
  string-identical from DOWN to seat/refusal, sampled per frame.
- **A6 — Stamp discipline.** Stamp age changes only at poll events,
  is always ceiling-rounded to 5s, and never advances per second;
  20s-cadence flips are silent (no haptic, no sound — the strip never
  summons, W4).
- **A7 — Ticker isolation.** With the ViewModel ticker running and no
  session, the slide view's invalidate count over 60s idle = 0.
- **A8 — Prime single-writer.** While primed: emit an activeTask
  change, an apps re-sync, and a routines re-emission — dial, blocks,
  imprint, APPS count, TIME cell all unchanged. (V5/V6 fail this
  today by construction.)
- **A9 — Travel truth.** Crossing 0.84 under contact commits; release
  at 0.83 does not; DOWN outside the cap starts nothing; a BLOCK=OFF
  verdict caps travel at 0.65 and the refusal sentence appears at that
  frame and not before. (V3 fails the first three today.)
- **A10 — Sealed freeze.** At the seat frame the imprint swaps
  `WILL SHUT → HELD` once, with the seat-frame count, and does not
  change again for the session's life — including at block boundaries
  and breaks.

---

## 5. TOTAL PRICE

| Item | Price |
|---|---|
| V1 badge deletion (host + view) | 0.5h |
| V2 label alpha pin | 0.5h |
| V3 catch 0.84 + commit-under-contact + hit-test | 2.5h |
| V4 cap holds at seat | 0.5h |
| V5 prime single-writer guard | 1h |
| V6 imprint re-source to frozen loadout snapshot (relocate/delete eyebrow line) | 2h |
| Assertions A1–A10 as tests / instrumented checks | 2h |
| **Total** | **≈9h — one build day, incl. one device pass** |

Not charged here (other slices' builds, named so nobody double-counts
or loses them): building the strip, stamp, imprint slot and sentence
slot themselves; building the prime; the prime count snapshot if it
doesn't exist (+0.5 day, V6 note).

## 6. RISKS

1. **AndroidView recomposition is the silent killer.** The Deck's
   `update` block (Deck:492–500) runs on every recomposition; C2's
   guarded setters are the only reason that is harmless. Any new
   unguarded property added to the slide view in Slice D/E re-opens
   idle motion without a single line that *looks* like animation. A3/A7
   are the tripwires — do not cut them to save the 2h.
2. **The strip will tempt someone onto the 1s ticker.** Freshness
   "age" is the obvious per-second display. W4 already decided it:
   ceiling-rounded, poll-event-driven, STALE past 45s. If the stamp
   ever ticks, A6 fails and the strip has become a countdown — my
   NONE's fence breached by a clock wearing a proof's clothes.
3. **Prime built as a flag.** The cheapest implementation of priming
   is a boolean threaded through the Deck; S-1 exists because that
   boolean will inevitably acquire a glow, a word, a cocked cap in
   some later pass. The loadout-under-the-count route is slightly
   more plumbing and is the only lawful one.
4. **V3 touches other seats' ground** (Fen's zones, Zug-II's slide,
   Ines's arming). The numbers are REGISTER's (0.68 / 0.84 / 0.65), so
   no re-fight should be needed — but the builder should land V3 as
   one change with the register cited, not as three quiet edits.
5. **V6's snapshot timing.** Freeze too early (Deck entry) and a
   rested loadout edit on Shield shows a stale count on Deck; freeze
   at prime/arm-attempt (my spec) is the latest honest instant.
   Freezing *live* is not freezing — that is V6 as shipped.

## 7. FORK FOR REN

**One fork: V2's label fade — kill it outright (my recommendation),
or keep a fade as occlusion handling?**
Recommendation: **kill it.** The fade's only honest defence is that
the cap covers the words anyway — but occlusion by a passing object
is geometry the eye reads for free, while an alpha ramp is the print
itself changing, continuously, for the whole travel. The freeze law
(W4: no change mid-slide, in any condition — written for the imprint,
and the cap words deserve the same grammar) prices print at zero
motion. If the device pass shows the label genuinely illegible under
the seated cap, move the label; do not fade it.
*Sub-note, not a fork:* §2 grants the strip APPS cell the imprint's
loadout rule (numeral may follow the priming routine's loadout). If
Ren rules the strip loadout-blind instead, the cell shows the selected
loadout in both states and the allowed primed difference in my fence
shrinks to the imprint numeral alone — the law holds either way; the
table's APPS row is the only thing that changes.

## 8. MAIL / WALL NOTE

Inbox at seating: empty of unread letters — all four prior letters
(Orson, Mara: citation-lift; Joss ×2: dawn-frame / primed-frame) were
already in `read/`, re-read for this audit; Joss's primed frame and my
NONE agree, and §2's table is that agreement written as inputs.
Sent this pass: a letter to **Aldo** (badge joint — host wiring
confirmed: badge reads `dialMinutes` alone, Deck:498; deletion priced
0.5h, nothing else reads it) and a letter to **Marlowe** (coordinator —
verdict, S-1, V-count, price). One signed note appended to the Commons
wall: the prime-as-data law (S-1) and the V2 fade charge, so Ines /
Zug-II can attack both before Slice D/E builds.

— Teo, Wild (nature), seat 13.
