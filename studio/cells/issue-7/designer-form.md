# Issue #7 — Shield (audit fixes) — Designer Spec: FORM

**Issue:** Sarthak-2007/Focus-app #7 — “Main-app pass — Shield (audit fixes)”
**Role:** FORM, designer, in VOGT's issue #7 cell
**Status:** DECLARED — design only. No app code written or edited, nothing compiled, no tests run.
**Sources read:** `studio/plan/AUDIT-shield.md` in full (Vogt's verdict, R-1…R-13);
`app/src/main/java/com/example/ui/screens/ShieldMatrixScreen.kt` (1,086-line merged batch-9 Shield);
`MainActivity.kt` (`sealed`, :243, :561); `service/WatchStore.kt` (Warrant, `deriveWarrant`,
`STALE_AFTER_MS`, `printState`, `warrantStaleForPrint`); `service/MonolithShieldService.kt`
(`protecting` / `protectingSessionId` / `protectingMono`); `service/MonolithAccessibilityService.kt`
(:194–195); `data/MonolithRepository.kt` (`standingWarrant()`, peek constants);
`data/MonolithModels.kt` (`RoutineSchedule.liveCount` / `isActiveNow`, session span derivations,
`shieldLastBeatWallMillis` / `shieldGapSeen`); `ui/components/MonolithCustomComponents.kt`
(`FullBleedSectionHeader`, `refusalSentenceFor`, `endsAtText`).

The audit's one job for this screen, held throughout: *set — in seconds — what is SHUT, what
PEEKs, and what stays OPEN; prove at a glance the wall is as he left it; and say, in the same
glance, whether the wall is standing right now.* Everything below serves clause three first.

---

## 1. The headline: the status sentence (audit R-5, merge item #1)

### 1.1 The defect, exactly as it stands in this tree

Shield receives one bit — `isSessionActive: Boolean` (`ShieldMatrixScreen.kt:99`), wired from
`sealed = activeSession?.isActive == true` (`MainActivity.kt:243`). The screen spends that bit
on palette (`effectiveNight`, :132) and on ratchet gating (:208). It never prints it.

Worse: that bit alone **cannot** support a truthful sentence. Enforcement is a conjunction the
bit does not capture:

- `MonolithAccessibilityService` returns without intercepting unless an active session row
  exists (:194–195) — a session is *necessary*.
- If the accessibility service is not bound, that code never runs at all — a session is not
  *sufficient*. A live session over a dead wire blocks nothing while every SHUT on this screen
  still prints as a state.

**FORM's first ruling: the sentence is never built from `isSessionActive` alone.** Printing
“BLOCKING NOW” off that boolean would repeat the audit's headline lie in larger type. The
merge widens Shield's contract to carry the evidence in §1.2 — the facts already exist
(audit R-5: “small — the facts are already in hand”); they are simply not yet handed to
this screen. This is a contract widening, not new machinery.

### 1.2 The evidence model — three print states, derived, never stored

The builder derives one print state per render from four fresh facts. FORM names the
derivation `ShieldBlockingPrint` with exactly three values; the builder maps it onto
existing types and owns the code shape.

| Fact | Source (existing) | What counts as fresh |
|---|---|---|
| Session | Active session snapshot (the row behind `sealed`): non-null, `isActive`, its `id`, `routineId`, `isStopwatchMode`, `isInSafeBreak`, span fields | Read in the current emission — see the cold-start rule, §1.4 |
| Wire | `Warrant` via `MonolithRepository.standingWarrant()` / `WatchStore.deriveWarrant` — the *one* derivation, reused, never re-implemented on Shield | `WatchStore.printState(warrant, nowMono)`, i.e. `readMono > 0` and age ≤ `STALE_AFTER_MS` (45,000 ms — the app's one stale law; no second threshold is invented for Shield) |
| Engine | `MonolithShieldService.protecting == true` **and** `protectingSessionId == session.id` (protecting is only ever set after a full monitor iteration under that session — “started is not protecting”) | The session row's shield beat is within the gap law: `shieldLastBeatWallMillis` not stale past the 10 s law (5 missed 2 s beats) that the finish machine already reads |
| Clock | `SystemClock.elapsedRealtime()` at render, as every other print derivation in this app takes it | — |

### 1.3 The truth table — exact wording for every state

**A — `BLOCKING NOW`.** Requires ALL of: session present and active · wire print-state
`LIVE` and fresh (accessibility bound, stamped this residency, usage ALLOWED) · engine
protecting *this* session id with a live beat.

> `BLOCKING NOW — SLEEP SESSION · ENDS 03:30`
> `BLOCKING NOW — SESSION · ENDS 14:00` (manual session, no routine name to print)
> `BLOCKING NOW — SESSION · NO SET END` (stopwatch — the airlock's precedent: the door never invents an end)

- **Session label** derives from the session's own frozen snapshot only: a routine session
  (`routineId != null`) prints the name snapshot written at materialisation
  (`RoutineDerive` writes `intention = routine.name`, `profileName = "Routine: …"`) +
  ` SESSION`, upper-cased. If `routineId` is set but no name can be read from the snapshot,
  print `ROUTINE SESSION` — never guess a name from the clock, from matching routine
  windows, or from the live routine row (the frozen-snapshot law: judgement reads the
  snapshot, never the live row, never a name string).
- **Never** derive the label from the LOADOUT cap. `loadoutCapIndex` is his last *hand
  pick*; a routine's loadout may be the one running (audit R-4c). Labelling a session
  “SLEEP” because the cap rests on SLEEP would be the cap's lie promoted to the headline.
- **`ENDS HH:MM`** derives from the session's own numbers via the existing
  `endsAtText(endMillis)` (`MonolithCustomComponents.kt:427`): the end wall instant is
  the routine window's frozen `windowEndMillis`, else start + whole-span target
  (`spanTargetSeconds` — *never* per-block `remainingSeconds`, which announces the session
  over at the first block boundary). The instant is computed once per session id and held;
  it must not slide per recomposition (the ever-sliding finish line is a named dead lie in
  that same file). If the derived end is in the past while the session still reads active,
  drop the `ENDS` segment rather than print a past end — the session is mid-transition and
  the next emission will settle the line.
- **Safe Break qualifier:** during `isInSafeBreak`, SHUT still enforces and PEEK does not
  (the shield service's own filter drops PEEK-tier apps in a break). The verb does not
  change; a final segment is appended: `… · SAFE BREAK — PEEK OPEN`. Segment-drop order
  under width pressure (§2.3): session label truncates first, then `ENDS`; the Safe Break
  segment is never dropped — it is the segment that keeps the sentence true.
- **A live peek lease changes nothing.** One app out on a bought 2-minute lease is the
  system working as ruled (the lease is honoured by every door). The sentence is about
  the wall, not one door.

**B — `NOT BLOCKING`.** Printed **only on a proven negative** — one of these, freshly read:

> `NOT BLOCKING — NO SESSION RUNNING` (no active session on a completed read — audit R-5 verbatim)
> `NOT BLOCKING — ACCESSIBILITY OFF` (wire print-state `OFF`: accessibility unbound)
> `NOT BLOCKING — USAGE ACCESS OFF` (wire print-state `OFF`: usage DENIED)
> `NOT BLOCKING — SHIELD NOT RUNNING` (session active, wire LIVE, but the beat has gone stale past the gap law / `shieldGapSeen` latched: the shield *was* there and stopped — a witnessed absence, not a missing reading)

- Where the dead leg has a fix (accessibility / usage), the whole line is tappable to that
  settings pane — the Deck's existing `RefusalAction.FIX_ACCESSIBILITY` / `FIX_USAGE`
  destinations, reused. `NO SESSION RUNNING` and `SHIELD NOT RUNNING` lines are **not**
  tappable: they state a fact and offer no verb (a jam offers no door; the retry/fix is
  the machine's or the Deck's, never a fake button).
- A routine being ARMED (§3.2) never softens this line, and this line never borrows a
  future tense. “Will block at 22:30” is the routines block's business, not the headline's.

**C — `CAN'T CHECK BLOCKING`.** Everything that is neither proven live nor proven dead:

> `CAN'T CHECK BLOCKING — TAP TO RETRY`

— the Deck's sentence verbatim (`refusalSentenceFor`, Ren's chair ruling), reused character
for character so the app says its uncertainty in one voice on every screen. Cases:

- No warrant has landed yet in this visit, or `readMono == 0` (age-unknown proves nothing).
- The warrant is stale for print (age > 45 s — a stale LIVE degrades to UNPROVEN by
  `printState`; Shield prints that degradation, it does not ride the old proof).
- Wire UNPROVEN: accessibility bound but not stamped this residency, or usage UNSET.
- Session active and wire LIVE, but protecting not yet witnessed for this session id and
  the gap law has not yet tripped — the birth window (“started is not protecting”). This
  window resolves itself within beats; it is *unknown*, and printing either neighbour
  during it would be a guess wearing the headline's clothes.

Tap = retry: a fresh warrant read (the `refreshDeckWarrant` route's Shield equivalent —
builder wires the same re-read, not a new sweep). While Shield is visible the read also
refreshes on the app's existing per-second beat at the stale threshold (the VM already
re-reads a stale Deck warrant on that beat), so the unknown state is bounded and
self-healing; the tap exists for the man, not because the machine needs it.

### 1.4 The cold-start rule (non-negotiable)

The initial, not-yet-emitted state of every input is **unknown**, and unknown prints as
state C. A default `false` session flag must never reach the line as `NOT BLOCKING`, and
no default may ever reach it as `BLOCKING NOW`. First frame on a cold open is
`CAN'T CHECK BLOCKING — TAP TO RETRY`, settling to its proven state when the first fresh
snapshot lands (in practice, beats). This closes the exact failure class the warrant
already suffers on cold starts (a residency stamp that never lands) at the print layer:
Shield would rather admit it hasn't looked yet than inherit a default's opinion.

### 1.5 What the sentence never carries

No counts (the tally owns counts — one home per fact, audit R-13). No app names. No
accent colour. No second line. If a fact cannot fit the grammar above, it belongs to
another surface, not to a longer sentence.

---

## 2. Placement, hierarchy, typography, motion

### 2.1 Placement — item 0, and the new top-of-screen order

The sentence is the **first item of Shield's `LazyColumn`**, above everything, including
the ratchet banner (the banner is a transient event; the sentence is the standing fact —
the fact outranks the event, and both fit the first screen together). With R-1 and R-3
applied, the top of the screen reads, in order:

1. **Status sentence** (§1) — fixed-height slot, §2.2.
2. Ratchet banner (existing component; per R-7 it now serves Deck-origin refusals only,
   Shield refusals print at the control touched — that placement is R-7's, unchanged here).
3. **Tally headline** — R-1's state-only headline, promoted from the slip to directly
   under the status sentence (“top of screen, above the tally headline” — R-5 fixes the
   sentence above the tally; R-1 fixes the tally at the top; this is where they meet).
4. LOADOUT cap (R-4 form), ROUTINES (R-2: stays), BLOCKED WEBSITES (R-3: moved up,
   capped), then search + the grouped ALL APPS catalogue.

The sentence is **in-flow, not sticky and not floating**: the screen's law is gap + label,
the floating band already owns the bottom clearance, and a pinned duplicate would give
this fact two homes. FORM records one priced follow-up for VOGT/Ren, *not* part of this
merge: because `listState` is saveable, a man returning to Shield mid-catalogue does not
see the sentence until he scrolls up. If that proves to matter on the phone, a sticky
variant of this same single line (same derivation, same slot grammar) is the named next
step — it is not designed as a second printed copy, and it is not smuggled in here.

### 2.2 Typography and state treatment

Built entirely from the screen's existing grammar — no new typeface, size family, or colour:

- **Words:** Atkinson Hyperlegible Bold — the section-header face, at **14 sp**,
  letterSpacing 0.4 sp. One step above the 11.5 sp headers: this is the loudest *print*
  on the screen by design (the cap remains the largest object; the sentence is the
  largest fact).
- **Numbers:** `ENDS 03:30` in Anonymous Pro Bold, via the tally's existing mixed-grammar
  construction (`tallyText`'s AnnotatedString pattern). Numerals are Anonymous Pro
  everywhere on this screen; the headline is not an exception.
- **Colour — tokens only, never hardcoded** (R-7's lesson: today's banner hardcodes
  `0xFFFBF1EC`). `BLOCKING NOW` in full ink (`onBackground`). `NOT BLOCKING` also in
  full ink — a dead wall at 1 AM must not be greyed into the furniture; it is
  distinguished by glyph and verb, never by colour alone. `CAN'T CHECK` at 55% ink
  (Panzer's solidity scale, R-12: SHUT solid / PEEK 55% / OPEN frame — the unknown
  state borrows PEEK's density: present, not solid).
- **Glyph:** one 10 dp square at the line's left (the finish pass's margin-glyph size),
  in Panzer's solidity scale: **solid ink square** = BLOCKING NOW · **hollow 1.4 dp
  frame** = NOT BLOCKING · **55% ink square** = CAN'T CHECK. Square, not circle — this
  app's geometry is caps and squares, and the glyph rhymes with the catalogue's margin
  marks (§3.1 legend). State is carried by glyph + verb together, so the line survives
  colour-blindness and a greyscale glance.
- **Slot:** fixed 28 dp height, `maxLines = 1`. A state change never moves the cap below
  it by a single dp. testTag proposed: `shield_status_line`.
- **Semantics (R-11 rides this line too):** contentDescription is the sentence read
  plainly — “Blocking now. Sleep session, ends 03:30.” / “Not blocking. No session
  running.” / “Can't check blocking.” — with the retry/fix exposed as a TalkBack custom
  action exactly when the line is tappable, and no action when it is not.

### 2.3 Truncation law

One line, always. Under width pressure segments yield in this order: (1) session label
ellipsises (Atkinson, `TextOverflow.Ellipsis` — the catalogue's own row grammar),
(2) `ENDS …` drops, (3) the Safe Break segment drops **never**. `BLOCKING NOW` /
`NOT BLOCKING` / `CAN'T CHECK BLOCKING` themselves never truncate, wrap, or shrink.

### 2.4 Motion

The screen's manners, applied unchanged: the tally never rolls its digits, and neither
does this line.

- **State change** (including BLOCKING NOW → NOT BLOCKING when a session ends under his
  eyes): the whole line re-strikes once — `AnimatedContent` crossfade, 260 ms,
  `MonolithCurves.EasingA`, exactly `StandingTallyLine`'s spec. Never a partial update,
  never a rolling clock segment; the `ENDS` instant is held per session (§1.3), so the
  line does not re-strike on a timer.
- **No pulse, no blink, no heartbeat animation.** A live wall is calm. A pulsing
  “BLOCKING” would be a slot machine's manners on the one line whose whole job is to
  be believed at a tired glance.
- **No entrance animation.** The sentence is present at first frame in whatever state
  §1.4 dictates; facts do not fade in.
- **Retry tap:** no spinner. The line stays `CAN'T CHECK` until the fresh read lands,
  then re-strikes once to its proven state. Opacity-only motion throughout — nothing
  moves, so nothing here fights reduced-motion settings.

---

## 3. Exact copy implications of the companion rulings

### 3.1 Standing Orders de-duplication (R-1)

The slip's *lines* die; its furniture retires with them. Copy consequences, exact:

- **Killed, no replacement:** the `YOUR RULES` slip header, the `PEEK` / `SHUT` slip
  section headers, every slip line (name · dot leaders · ruling word), and the slip's
  folded search line (`7 SHUT · 2 PEEK` at the slip header). The dot leaders die with
  the lines (Dens F7, absorbed by R-1). Nothing on the screen may enumerate ruled apps
  twice again — that is the ruling, and the copy must not reintroduce it as a
  “summary list”, “quick view”, or any other synonym.
- **The tally survives as the only enumeration of counts**, in its existing mixed
  grammar, unchanged in wording: `9 SHUT · 4 PEEK · 61 OPEN` — three numbers that sum
  to the phone. Under search it stays whole (counts of his decisions are never
  filtered); the catalogue header's `SHOWING n` prints only while a filter/search is
  active (merge item 3).
- **The tally's words are jumps, and must say so.** Fremd C5: a receipt that is secretly
  a switch is a truth defect. Each tally segment therefore wears the screen's existing
  door grammar permanently — the `FilterWord` treatment (ink + the short bar beneath
  the word) — so SHUT / PEEK / OPEN read as tappable at rest, not on hover or discovery.
  Tapping scrolls the saveable `listState` to that catalogue group (R-1). Semantics:
  “9 shut. Tap to jump to the shut apps.” — per segment.
- **Catalogue group headers** (the catalogue, grouped by `groupedByRule()` — PEEK before
  SHUT, per the standing ordering): `PEEK — n` · `SHUT — n` · `OPEN — n`, in
  `SlipSectionHeader`'s existing dress (Atkinson Bold 11.5 sp, ink). A group with no
  members prints the slip's acquitted absence grammar inside the catalogue: `NONE`
  (Anonymous Pro, variant grey) — an absence printed as a fact, never a missing section.
- **PEEK's contract prints once on this screen, in the PEEK group header only**
  (R-6): `PEEK — 2 MINUTES, THEN SHUT AGAIN · 20 MIN BETWEEN PEEKS`, derived from
  `MonolithRepository.PEEK_DURATION_SECONDS` (120) and `PEEK_COOLDOWN_MILLIS` (20 min)
  at render — never hardcoded, so the print cannot drift from the constants it names.
  It prints nowhere else on Shield; PARK's plate stays the settings home.
- **Glyph legend, one line at the catalogue head** (R-10's answer to name-is-not-a-door):
  `■ = SHUT · □ = PEEK · NO MARK = OPEN` — Anonymous Pro 11 sp, variant grey, printed
  once, directly above the first group header. (Glyphs are the solidity scale's marks;
  if the finish pass machines them differently, the legend's marks change with them —
  legend and marks are one fact.)

### 3.2 `ACTIVE` → `ARMED` (R-6)

- The ROUTINES header's right side changes by one word, nothing else:
  `rightMonoNumber = liveCount(routines)`, suffix **`" ARMED"`** — e.g. `2 ARMED`.
  The derivation is already honest (`liveCount` = enabled AND daysMask ≠ 0); only the
  word lied. `ACTIVE` claimed firing-now; `ARMED` claims what is true: set, scheduled,
  will fire.
- **No other `ACTIVE` survives on this screen.** FORM swept the screen's copy: the
  routine row's `ON` / `OFF` cap is a switch state and stays (it *is* on or off); the
  word ACTIVE appears in no row, note, or banner on Shield after this change.
- The one true “now” word for routines is the row marker from R-5: **`RUNNING NOW`**,
  appended to the row's meta line (`DAILY · SLEEP` → `DAILY · SLEEP · RUNNING NOW`)
  when `RoutineSchedule.isActiveNow(routine, now)` is true — Anonymous Pro Bold 11 sp,
  full ink, re-derived on the screen's clock beat. FORM is explicit about the division
  of labour, because confusing them recreates the lie one level down: `RUNNING NOW`
  states the *window* is live (schedule fact); `BLOCKING NOW` states a *session exists
  and the wire proves it* (§1). If a window is live and the headline reads
  `NOT BLOCKING` or `CAN'T CHECK`, both lines print as derived — that visible
  contradiction *is* the missed fire surfacing, and neither line may be hidden to
  spare the other.

### 3.3 daysMask = 0 → `WON'T FIRE` (R-6)

- **Precedence, exact:** a routine row's state cap prints three possible words —
  `OFF` (enabled = false) · **`WON'T FIRE`** (enabled = true, daysMask = 0) · `ON`
  (enabled = true, daysMask ≠ 0). `WON'T FIRE` replaces `ON`; it never appears beside it.
- **The cap sizes to its payload** — the row's own standing comment (“sized to its
  payload, one cap grammar”) is the precedent: the cap wraps its word with the existing
  horizontal padding (min width 48 dp), height unchanged at 32 dp, Anonymous Pro Bold
  11 sp. No second control, no extra badge, no icon.
- **Treatment:** `WON'T FIRE` wears the middle density — surfaceVariant fill, outline,
  55%-ink word. It must read as neither `ON` (solid ink fill: his switch, live) nor
  `OFF` (recessed, variant text: his switch, off) — it is a third fact: *switched on,
  and inert*. No strikethrough (the strike is R-7's in-place ratchet refusal grammar;
  borrowing it here would blur both).
- **Cause and consequence both print, once each:** the meta line already prints the
  cause — `routineDaysLabel(0)` = `NO DAYS SET`. The cap prints the consequence —
  `WON'T FIRE`. That is not duplication: one is the schedule fact, the other is the
  firing verdict, and a 1 AM reader should not have to derive the second from the first.
- **Consistency is automatic and must stay so:** the row is excluded from the header's
  ARMED count by the same `isLive` derivation, and `isActiveNow` already returns false
  for it — so it can never wear `RUNNING NOW` while its cap says `WON'T FIRE`.
- **In the expanded editor:** untoggling the last day chip flips the cap to
  `WON'T FIRE` on commit, immediately, with no confirmation and no additional copy —
  the change is reversible by the same chips, and the cap *is* the notice. Retoggling
  any day restores `ON` with no other word changing anywhere on the screen.

---

## 4. DELEGATED — the ±30-minute chips (explicit, not designed here)

**DELEGATED to Voss's batch-10 time wheels. FORM designs nothing for this problem.**

The ±30 chips in `RoutineRow`'s expanded state put routine times on a 30-minute lattice
(audit finding 3: SLEEP 20:00 → 20:40 does not exist in any number of gestures). Per R-8,
the cure that exists is the routine-edit lane's dual snap wheels
(`setRoutineTime`, commit-once-per-settle) merged with Vega's W-1/W-2/W-3 cures, and the
Day Rail is the named successor mechanism. Both are Voss's batch-10 territory.

FORM therefore specifies, deliberately and completely:

- **No interim design.** No ±5 / ±15 chips, no long-press acceleration, no tap-to-type
  box, no “fine adjust” row. Any of these would be a hack wearing this merge's clothes:
  still a lattice or a silent other-end move, and a second editor to unbuild when the
  wheels land.
- **No copy changes in the time area.** The chips, the `START` / `END` labels, and the
  expanded row's layout ship exactly as they are in this tree until the wheels replace
  them wholesale. The `WON'T FIRE` cap and `RUNNING NOW` marker in §3 touch the row's
  state words only — neither depends on, nor pre-empts, the wheel editor.
- **No dependency:** the status sentence (§1), the tally move (§3.1), and the word cures
  (§3.2–3.3) neither wait for nor constrain the wheels. If batch 10 slips, this merge
  still ships whole.

---

## 5. Draft GitHub comment (for VOGT to post)

> — FORM (DESIGNER), for VOGT's issue #7 cell
>
> Design delivered: `studio/cells/issue-7/designer-form.md` (DECLARED — design only, uncompiled).
>
> Headline (R-5): a status sentence as item 0 of Shield, in three proven states only — `BLOCKING NOW — SLEEP SESSION · ENDS 03:30` / `NOT BLOCKING — NO SESSION RUNNING` / `CAN'T CHECK BLOCKING — TAP TO RETRY` (the Deck's sentence, verbatim). Key truth rule: the sentence is **never** derived from `isSessionActive` alone — that bit is necessary, not sufficient (an unbound accessibility service blocks nothing under a live session). BLOCKING NOW requires the conjunction, all fresh: active session + warrant print-state LIVE within the app's one 45 s stale law + shield `protecting` for *this* session id with a live beat. NOT BLOCKING prints only on a proven negative (no session / wire OFF / beat dead past the gap law). Everything else — no warrant yet, stale read, UNPROVEN, the session-birth window — is CAN'T CHECK, and cold start defaults to CAN'T CHECK, never to a default's opinion. Session label comes from the session's frozen snapshot only, never from the LOADOUT cap. Safe Break appends `· SAFE BREAK — PEEK OPEN`; a live peek lease changes nothing.
>
> Dress: Atkinson Bold 14 sp words, Anonymous Pro numerals (existing `endsAtText`), solidity-scale glyph (solid / hollow / 55%), fixed 28 dp slot, whole-line 260 ms Curve-A re-strike on change — the tally's manners, no pulse, no roll.
>
> Copy cures specced exactly: slip lines killed, tally becomes the state-only headline with jump-words that visibly read as doors (FilterWord treatment), PEEK contract printed once in the PEEK group header derived from the peek constants, glyph legend at the catalogue head; ROUTINES header `ACTIVE` → `ARMED` with `RUNNING NOW` (from `isActiveNow`) as the only true “now” on a row; daysMask = 0 + enabled caps print `WON'T FIRE` in the middle density, `NO DAYS SET` staying as the printed cause.
>
> The ±30-minute chip problem is marked **DELEGATED to Voss's batch-10 time wheels** — no interim hack designed, no copy touched in the time area.
>
> One priced follow-up flagged for VOGT/Ren, not in this merge: a sticky variant of the status line, if saveable-scroll restore hiding the sentence proves to matter on the phone.
>
> — FORM

---

*FORM, designer, VOGT's issue #7 cell — Oct 30, 2026. DECLARED: design only; no code
edited, nothing compiled, no tests run, nothing committed or pushed.*
