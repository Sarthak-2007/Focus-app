# Suki — Wild, space

> Sarthak's direct order, just now: the citation requirement is LIFTED. No seat needs to cite design references or anything else. Full freedom and creativity for you and all 20 seats — the reference library, wave-1 files, and renders are there if a seat wants them, never as a quota. Pass this down to every seat verbatim. What still binds is only what is HIS: the settled decisions (Ledger dead, banked hold, routines his, Guard open) and the three standards (ARM ≤10s, HOLD absolute, TRUTH once). Form, method, and ideas are yours.

Lens: mission control, stripped of story. What survives from flight operations is one discipline — **never render a state you have not measured, never ask the operator to re-decide what was already decided on the ground, and say the failure in plain words the first time.** No T-minus theatre, no countdown voice, no GO as decoration. GO, when it appears at all, is a measured fact or it does not appear. This file reverses my own wave-1 GO Poll where the new law requires it, and says so.

Assignment scope: (1) real readiness states on Deck / chrome, (2) the airlock interception overlay, (3) countdown / abort language, (4) the Guard fork from a systems-readiness lens.

**Amendment after first writing — Ledger REOPENED (pass-down from Marlowe, Sarthak's words: "run ledger through agents, im not sure if we should keep or not").** The verbatim order quoted above says "Ledger dead"; that one item is superseded. The Ledger is **OPEN**: (a) keep as rebuilt tab, (b) demote to a sheet / Shield section / chrome corner with only what earns its place, (c) kill entirely. Reopening fact, which this lens confirms from its own side: YPT tracks his *study*; it does not track *Monolith itself* — sessions sealed, deflections absorbed, holds spent, routine attendance, and (this seat's addition) coverage gaps where blocking silently failed. That record exists only in this app. Every place below where my recommendation depends on the answer is flagged **⚑ LEDGER-DEPENDENT** and mapped in the Ledger dependency section before "Too good to lose." Nothing in this file silently assumes the Ledger dead or alive. The old screen's forms — study-minute duplication, streak tower, HMAC theatre, dead curios — stay rejected under every outcome; the open question is only the Monolith-native record.

---

## Deck / Chrome — Real readiness states

### Chosen form — THE TRUTH LINE: measured before he arrives, rendered once in chrome, silent on Deck when true

**The idea in one line:** Readiness is not a ceremony performed during the slide. It is a set of four system checks the app runs continuously in the background; the result lives in exactly one place — a single line in the chrome nameplate zone — and the Deck adds nothing when the answer is "ready." Silence is the ready state.

**Mechanism and state / data source (the only readiness data that exists):**

Four checks, each a real query against the OS, none inferred, none cached beyond 5 seconds, re-run on: app foreground, permission-change broadcast, Shield service bind/unbind, and Deck entry.

1. **Blocking service alive** — AccessibilityService connection state (`AccessibilityManager` service-connected callback + the Shield foreground service running flag). True only if both report connected *now*.
2. **App-usage permission held** — `AppOpsManager` `GET_USAGE_STATS` == `MODE_ALLOWED`.
3. **Overlay permission held** — `Settings.canDrawOverlays()` == true. Required because the airlock (Element 2) is an overlay; without it the app cannot honestly promise interception.
4. **Blocklist non-empty and loaded** — Shield's enforced list count > 0 **and** the in-memory set the service will actually match against equals the persisted set (count + hash compared at service bind). This kills the classic lie: settings say 47 apps blocked, the running service holds an empty list.

Readiness = all four true. There is no fifth decorative check (battery, "focus score," streak) — anything that does not change whether a blocked app will actually be stopped is not readiness and is never rendered as readiness.

**Rendering — chrome (the single home):**

- Location: second line of the chrome nameplate, replacing any HELD / GUARD pill fills. One line, Anonymous Pro 10sp, letterspaced 0.08em, ink tiers only — **zero orange, zero colour coding of any kind** (locked law: zero orange in chrome; never green; no traffic-light metaphor smuggled back in as amber).
- Ready: `READY · 47 APPS BLOCKED` — the count is the enforced-list count from check 4, the same number the service matches against, not the settings count.
- Not ready: `NOT READY · 2 OF 4 CHECKS MISSING` — a count of failed checks, nothing more. It does not name them here; chrome is a readout, not a repair shop.
- The line is a tap target (full line height 32dp hit area) that goes to the repair surface — destination depends on the Guard fork (Element 4). The line itself never changes its wording based on where it points.
- Day / night: day = ink at tier ≈5.6:1 on cream ground; night = cream text on `#171412` ground at the same tier. NOT READY is rendered one ink tier *stronger* than READY (more ink, not more colour) — the failure is heavier, not redder.

**Rendering — Deck (deliberately almost nothing):**

- When ready: **no readiness widget, word, rail, or animation on Deck at all.** The slide reads `SLIDE TO ARM SESSION` as today, the duration readout is the only big number, and arming proceeds. Readiness was already told, once, in chrome; repeating it on Deck would be a second rendering of one fact.
- When not ready: the slide does not arm, and the reason is printed *in the slide track's own label slot* — the one place his thumb already is. Priority order = blocking order (service → usage → overlay → list), only the **first** failure shown, in human language, Atkinson Hyperlegible 13sp:
  - `TURN ON BLOCKING TO ARM` (service dead)
  - `ALLOW APP-USAGE ACCESS TO ARM`
  - `ALLOW OVERLAY TO ARM`
  - `ADD AT LEAST ONE APP TO BLOCK`
  The slide cap still drags (the hand must never hit a dead control — a control that ignores touch is a lie of a different kind), travels to 40% with normal 1:1 gain, then meets firm resistance (gain 0.15×) and settles back on release. Tapping the label jumps to the same repair surface as the chrome line. One failure at a time: fix it, the next one — if any — replaces the text. No list, no modal, no checklist dumped on the arming surface.
- No state is ever rendered from a stale check: if the last check is older than 5s at Deck entry, the slide label holds `CHECKING…` (Anonymous Pro 10sp, in the badge slot, never in the track) for the re-query only, measured at <300ms on device class; the slide is inert during that window and says so by not moving. `CHECKING…` may never persist — if a query throws, the honest render is `CAN'T CHECK BLOCKING — NOT READY`, treated as failure #1.

**Motion:**
- Chrome line change (READY ↔ NOT READY, count change): 90ms cross-fade, no slide, no scale. Instruments change their legend; they do not animate it.
- Deck label swap (failure text ↔ `SLIDE TO ARM SESSION`): 90ms cross-fade, same slot, same baseline.
- Slide resistance engage at 40% on a failed check: no visual snap; gain change is felt, not shown. Release settle: spring `dampingRatio 0.80, stiffness 420`, ~200ms to rest.
- Everything else still. Readiness has no idle animation, no pulse, no breathing dot.

**Haptics:**
- Resistance engage (failed-check drag past 40%): single `12ms @ 0.5` — a stop felt once, not a buzz per millimetre.
- On the check that *becomes* true while he is on Deck (he returns from system settings): `tick 5ms @ 0.4` once, simultaneous with the label swap. Repair confirmed in the hand.
- Ready state: explicit silence. No haptic for opening Deck, none for a passing check he did not just fix.

**Sound:** Explicit silence throughout. Readiness is read, never announced. (A sound on every Deck entry would train him to ignore the one sound that matters — the seal.)

**Depth / material:** The chrome line is letterpress — printed one tonal step into the nameplate surface, no fill, no pill, no border. The Deck failure text sits in the existing recessed slide track at the track's own depth; nothing is added above the surface. Night: line on `#171412`, no fill behind it (fills cap `#4A423C` is not spent here — there is no control to fill).

**Standards check:**
- **ARM ≤10s:** Ready path adds **0 gestures, 0ms**. Checks run before and outside the arm path. Not-ready path cannot arm at all — which is the truth, not a tax: a session armed over a dead service would be a ghost session.
- **HOLD:** No mid-session component. Readiness is a pre-arm fact only; once sealed, the line freezes at its arm-time value and is not re-rendered during the session (a permission revoked mid-session surfaces at the airlock's failure handling, Element 2 — never as a chrome prompt competing with the session).
- **TRUTH:** One fact (will blocking actually work, and over how many apps), one rendering (chrome line), one home. Deck's failure text is not a second rendering — it is the *repair instruction* for the same fact, shown only when the fact is negative and only on the surface where the consequence lands. Highest truth risk in this element: the enforced-list hash check (check 4) is load-bearing — ship the line without it and `READY` becomes theatre within one service restart.

### Runner-up — THE POLL RAIL (wave-1 GO Poll, cut down)

Three station words — `BLOCKING · PERMISSIONS · LIST` — printed inside the slide track, lighting in sequence *as the sled passes them* during the drag; all lit at 100% = arm proceeds; a failed station holds the sled at that point with the reason under the track.

- Compact spec: station words Anonymous Pro 9sp in the track's top hairline; report-in 120ms stagger, stamp curve `CubicBezier(0.2, 0.9, 0.25, 1.0)`; per-station haptic `tick 6ms @ 0.45`; failure resistance gain 0.35× past the failed station; same four data checks as the chosen form, polled at drag start.
- **Why it lost:** It spends the scarcest resource in this app — the tired user's attention during the one gesture that must stay under 10 seconds and thoughtless — on watching the machine certify itself. Worse, it renders readiness a *second* time (once in chrome, once in the track) and renders it *later* than the chosen form knows it: a check that failed an hour ago should be known at Deck entry, not discovered mid-drag at 03:30 with the thumb already committed. It is beautiful. Beauty is retention — but ceremony on the daily arm path is the one place the settled law forbids beauty to charge rent. Kept here in full because if Sarthak ever wants theatre, this is the honest version of it: every station is a real check, and NO-GO is a real answer.

**Guard fork (this element):** The chrome line's tap target is the fork. Under **dissolved** Guard: tap → Shield, scrolled to the repair module at its foot (Element 4A). Under **Guard as destination**: tap → Guard, readiness checklist at its head (Element 4B). The line, its wording, and its data are identical under both. **His call** — see Element 4.

---

## Airlock — The interception overlay

### Chosen form — THE WALL CARD: full-screen, zero choices, one fact per line

**The idea in one line:** When a blocked app is opened mid-session, the overlay is not a dialog and offers nothing to decide. It is a wall that states three true things — what you opened, how long is left, when it ends — and the only way past it is the way that already existed: go back, or spend the banked break through its own separate hold (Element 3). The overlay itself contains no break button.

**Mechanism and state / data source:**
- Trigger: AccessibilityService foreground-package change to a package in the enforced set while a session is sealed, **or** a blocked website's domain matched in the browser address bar (same overlay, website name rendered instead of app name).
- Latency budget: overlay drawn within **120ms** of the foreground change (WindowManager overlay, pre-inflated view kept warm by the Shield service for the session's duration — inflating on intercept is how overlays arrive late and flickery, and a late wall teaches the hand to race it).
- Data rendered, all live from the session record at intercept instant: blocked target name + its real 28dp app icon (recognition must be instant and his — a generic shield icon would force reading); session remaining time (the single session clock, Element 3); clock finish time; session's cued task name if one is set, else that line is omitted entirely — never filled with a motivational substitute.
- Dismissal: system Back, Home, or tapping `BACK` (the single text action, bottom, full-width 56dp hit area) all return to the previous safe surface (launcher / Deck running state). All three do the same thing; none is styled as primary. Opening Recents and re-selecting the blocked app re-intercepts, indefinitely, with no escalation, no counter shown to him, no "attempt 3" — **interceptions are counted in the session record and rendered only after the session ends**, never live. ⚑ LEDGER-DEPENDENT (rendering home only): the *counting* is fork-independent — the intercept log is written during the session under every outcome, or the app cannot be honest even transiently. Where the total is *rendered and kept* depends on the Ledger answer: (a) tab → receipt line + the tab's session history; (b) sheet/section → the end-of-session sheet, optionally the Shield section; (c) kill → the transient end-of-session receipt only, shown once, with no browsable history behind it. A live counter is a score, and a score is a game played against the wall at the exact moment HOLD must be absolute.
- Failure honesty: if the overlay permission was revoked mid-session, interception degrades to the AccessibilityService's fastest fallback — return-to-home action + a Deck running-state line on next unlock: `OVERLAY WAS OFF — [APP] NOT COVERED FROM 14:02`. The gap is recorded with its start time, not papered over. The session receipt carries it. ⚑ LEDGER-DEPENDENT (persistence only): the gap *fact* and its unlock-time Deck line are fork-independent; whether the gap survives past the end-of-session receipt into a kept record is exactly the Ledger question — under (c) the app admits the failure once and then forgets it ever failed, which this lens flags as the strongest truth-cost of killing (see dependency map). This is the only mid-session truth the system is allowed to volunteer, and it waits for unlock — it never interrupts.

**Layout / rendering (top to bottom, nothing else on screen):**
1. Target icon 28dp + name, Sora SemiBold 22sp: `Instagram` — his app, named, not "this app."
2. Remaining time, Anonymous Pro Bold, 64sp, the screen's hero and its only large number: `32 MIN LEFT` — minutes only (see Element 3 for the final-minute exception).
3. One line, Atkinson Hyperlegible 14sp: `Blocked until 14:32 · Physics — Rotational Mechanics` (task segment omitted when no task is cued).
4. Bottom: `BACK` as text action; beneath it, in Anonymous Pro 10sp at the quietest text tier, the break's existence stated as a fact, not offered as a button: `Emergency break: hold the session seal on Deck`. A direction, not a control — the hold itself lives on Deck where it always lives, so the overlay introduces no second break surface to keep in sync.

**Motion:**
- Appearance: **cut, not transition.** Overlay at full opacity in one frame, followed by a 60ms tonal settle (ground steps from one step above `#171412` night / cream day to its rest tone) so the eye registers arrival without watching a page travel. A sliding or fading wall reads as a screen you navigated to; a cut reads as a fact.
- No exit animation: on dismiss, one-frame removal. The blocked app must never be glimpsed during a fade — a 200ms fade-out is a 200ms peek, and this app does not sell peeks through its own animations.
- Repeated intercepts: identical every time. No variation, no escalation of tone or copy. Sameness is the mechanism.

**Haptics:**
- On intercept: `thud 22ms @ 0.85`, followed at +90ms by `tick 5ms @ 0.3` — mass arriving, then the latch. Once per intercept, never repeated while the overlay sits.
- On `BACK` press: `tick 5ms @ 0.4` on DOWN. Nothing on dismiss itself.

**Sound:** One low relay *thunk* on intercept (synthesised, ~70ms, low gain), the same family as the seal sound so the ear learns one vocabulary: that sound means the boundary is real. No sound on dismiss. If Sound Effects are off in settings, silence — the haptic carries it alone. Never a voice, never an alarm.

**Depth / material:** Flat wall, not a card on a scrim — there is no visible "behind" to imply the blocked app is still present underneath. Ground is the night staircase ground `#171412` (day: cream ground), content on a single recessed panel one tonal step down with a 1dp hairline top catch-line; remaining-time numeral sits *in* the recess. No shadows, no blur backdrop (blur is both a shadow-substitute and a peek). Zero orange anywhere on this surface — orange is COMMIT, and nothing here is committable.

**Standards check:**
- **ARM:** Not on the arm path. Cost 0.
- **HOLD:** This element *is* HOLD, enforced. Decisions introduced mid-session: **zero.** No open-anyway, no 5-minute pass, no snooze, no settings link, no break button. The two exits (back / the pre-existing banked hold on Deck) are both decisions made by the rested user when the system was designed, executed — not made — by the tired user.
- **TRUTH:** Every rendered value is live session data; the one number (minutes left) is the same clock as Deck and AOD, rendered once per surface it owns. Risk: icon/name mismatch if the service's package→label cache stales — mitigated by resolving the label at intercept from PackageManager, not from the persisted list.

### Runner-up — THE FIVE-SECOND VEIL

A half-height sheet naming the app and counting `5 · 4 · 3…` before auto-returning home; tapping anywhere returns immediately.

- Compact spec: sheet rises 180ms `CubicBezier(0.3, 0.8, 0.3, 1.0)`; countdown Anonymous Pro 28sp stepping per second with `tick 4ms @ 0.3` per step; auto-dismiss at 0 with `thud 18ms @ 0.7`.
- **Why it lost:** It converts a wall into a wait, and a wait is a decision ("sit through it or tap out?") placed mid-session, five seconds at a time, dozens of times a day. It also trains a rhythm — open, endure 5s, retry — that the Wall Card's instant, identical, choiceless return deliberately refuses to provide. And any auto-dismiss timer is a small countdown rendered *in addition to* the session clock: a second clock for one fact's neighbourhood, against TRUTH-once discipline.

**Guard fork (this element):** None for behaviour — interception is identical under both outcomes. Only the degraded-mode repair direction changes: the receipt's overlay-gap note points to Shield's repair module (dissolved) or to Guard (destination). Wording of the note does not change. **His call** rides on Element 4 only.

---

## Session clock — Countdown and abort language

### Chosen form — PLAIN COUNT, BANKED ABORT: one clock, human sentences, seconds only when seconds decide something

**The idea in one line:** A mission clock is honest because it counts one thing and calls an abort an abort. Monolith gets one session clock, rendered as a sentence a tired person can act on, and an abort path — the banked emergency break — whose language states its cost and its banked progress as plainly as its button states its duration.

**The one clock (mechanism / data source):**
- Single source: the session record's `endsAtMillis` (wall-clock) minus now, computed at render; elapsed is never stored as a second counter — it is `now − startedAtMillis` wherever a receipt needs it, derived from the same two timestamps. No surface keeps its own timer state.
- **Deck running state** (the clock's home): two facts, always together, never one without the other — `32 MIN LEFT` (Anonymous Pro Bold 56sp) and `ENDS 14:32` (Atkinson Hyperlegible 14sp directly beneath). Remaining without finish invites arithmetic; finish without remaining invites clock-watching. Together they are the complete fact, stated once.
- **AOD desk clock:** same two facts, same words, larger type, true-black ground. It is the same rendering at a second size, not a second rendering — the AOD shows no elapsed, no mission time, no next-event line.
- **Minutes discipline:** remaining renders in whole minutes, rounding **up** (32 min left at 31:12 remaining — the session never claims less time than it will take; rounding down would promise an end that arrives late, a small lie repeated every minute). **Exception:** inside the final 60 seconds, Deck and AOD switch to seconds (`48 SEC LEFT`) — because in the final minute the second is the unit a decision is made in ("one more question or stop cleanly"), and withholding it would be its own distortion. The switch happens once, at 60s, with no animation.
- **No other countdown exists in the app.** Routine start times are clock times (`STARTS 05:00`), never "in 3h 12m" — a duration-to-routine would be a second, drifting rendering of a fixed fact. Receipts state durations as facts after the fact (`SAT 50 MIN`), never as counts.

**Abort language — the banked emergency break, word for word:**

The break is initiated only from Deck running state, by press-and-hold on the session seal (88dp target). Its language system, all Atkinson Hyperlegible except the bank readout (Anonymous Pro), all stating only verifiable facts:

- Idle (before touch), printed under the seal at the quietest tier: `EMERGENCY BREAK · 1 LEFT THIS WEEK` — or, when spent, the line is **absent** and the seal is inert with no label at all. A spent break does not advertise its own absence; the rested user set the cap, the tired user is not lectured by it.
- During hold: a bank readout replaces the line, updating only at bank points: `BANKED 15 SEC` → `BANKED 30 SEC` → `BANKED 45 SEC` → at 60s total the session ends. Between bank points, a hairline progress arc around the seal fills continuously (the fill is a position, not a number — position is not a second rendering of the banked seconds; the number changes only when the bank does).
- On slip (finger lifts before 60s): banked progress holds; unbanked progress drains at **120ms of progress lost per 100ms of real time** until it reaches the last bank point, then stops draining — the drain is shown by the same arc unwinding, and the line reads `BANKED 30 SEC · HOLD AGAIN TO CONTINUE`. No zeroing, no failure word, no buzz. The sentence is an instruction, not a verdict.
- On completion (60s reached): session ends; the end screen's first line is the abort stated plainly — `ENDED EARLY · 18 MIN UNUSED` — followed by the receipt. ⚑ LEDGER-DEPENDENT (the receipt after this line, not the line itself): `ENDED EARLY · 18 MIN UNUSED` is transient end-screen copy and stands under all three outcomes; the receipt that follows it is the session-end rendering whose persistence — filed under (a), sheet-only under (b), shown-once under (c) — is Sarthak's open Ledger call. The words "break," "abort," "quit," "fail" are never mixed: it is called **emergency break** before and during, and **ended early** after. One event, one name per tense.
- Words never used anywhere in this system: *streak, shame/guilt vocabulary, "are you sure", "you'll lose", T-minus, mission, abort* (as a label — it reads as drama, not information), and any percentage of the session "wasted." Unused minutes are a fact; their moral weight is not the app's to assign.

**Motion:**
- Minute rollover on Deck/AOD: digits change by cut, no roll, no flip. A clock that performs its counting is a clock asking to be watched.
- Bank arc fill: strictly finger-locked linear, no easing — the arc is the finger's pressure over time, and easing it would falsify the one-to-one mapping the hold depends on.
- Drain on slip: linear unwind at the drain rate above, `no curve` — same honesty.
- End-early transition: session surface steps one tonal step darker over 200ms as the receipt rises — a room going quiet, not a celebration or an alarm.

**Haptics:**
- Hold start (finger down on seal): `tick 6ms @ 0.5`.
- Each bank point (15/30/45): `knock 14ms @ 0.75` — a notch seating. Distinctly heavier than a tick; the hand must be able to count banks with eyes closed, because at the moment of abort the eyes are the compromised instrument.
- Slip: explicit silence. The drain is seen, not punished.
- 60s completion: `thud 26ms @ 0.9` + `tick 5ms @ 0.3` at +110ms — the seal releasing.
- Minute rollover, final-minute switch: silence, always.

**Sound:** Silence for all counting and banking. At 60s completion only: the seal sound played in reverse family — a single low *un*-latch (filtered mechanical release, ~90ms, low gain) — the auditory pair of the arming seal. Nothing at session *completion* changes: the existing completion language stands; this element governs the early-end only.

**Depth / material:** The seal is the existing raised Deck element; the bank arc is drawn as a recessed groove *in* the seal's rim (tone step, 2dp), filling by ink density, not colour — no orange (the hold is not a COMMIT; it is the opposite of one), no other hue. The bank line is letterpress beneath it.

**Standards check:**
- **ARM:** Not on the arm path. The seal-hold target exists only in running state.
- **HOLD:** The break is the *settled exception* to HOLD, specced to introduce the minimum possible decision surface: one gesture, already known, with cost and progress stated before and during — never negotiated. No confirmation dialog ("are you sure" would be a second decision stacked on the first, at the worst moment). Language risk policed above: no copy on this path may frame, warn, or persuade — it may only state.
- **TRUTH:** One clock, two timestamps, every surface derives. Banked seconds are real accumulated hold milliseconds rounded *down* to the bank point (the app never claims bank it hasn't measured). `1 LEFT THIS WEEK` reads the actual weekly-use record; if that record is unavailable the line omits the count rather than guessing it: `EMERGENCY BREAK`.

### Runner-up — DUAL CLOCK (mission elapsed + session remaining)

Deck running state shows both `IN 00:41:07` (elapsed, ticking seconds) and `LEFT 32 MIN`, AOD adds a third line with the next routine as a countdown.

- Compact spec: elapsed in Anonymous Pro 14sp above the hero remaining; per-second cut updates; next-event countdown in Anonymous Pro 10sp on AOD only.
- **Why it lost:** It renders one fact — where the session stands in time — as two numbers that must be added together to mean anything, and adds a third (time-to-routine) that duplicates a clock time already printed on Shield. Elapsed time during a session has exactly one honest home: the receipt, after it can no longer compete with the work. Live elapsed is a progress bar wearing a clock's clothes, and per-second ticking on Deck manufactures clock-watching — the precise behaviour the minutes discipline exists to prevent. (My wave-1 dual-clock hold banner dies with it, for the same reason, and good riddance.)

---

## Guard — The fork, from a systems-readiness lens

**Flag, stated before any argument: the final call is Sarthak's.** Guard's fate is his open pick under settled law. What follows is this lens's evidence and two fully specced outcomes that share one data model, so his pick costs no rework either way. This seat's lens-preference is stated at the end and labelled as a preference, not a verdict.

**What the readiness lens sees:** Guard's contents are not one kind of thing. They are three:
1. **Status** — the four checks of Element 1 (service, usage, overlay, list). Status is ambient truth. Its home is chrome (already chosen, fork-independent).
2. **Repair** — the actions that fix a failed check: deep-links to system settings, re-bind the service, manage the enforced list. Repair is *episodic* — needed rarely, needed urgently, needed at the moment a check fails (which is why Elements 1–2 point at it).
3. **Preferences** — sound effects on/off, theme behaviour. Preferences are neither status nor repair; they are rested-user decisions with no readiness content at all.

A destination screen bundles all three because they arrived together historically. The readiness lens asks only: does bundling help a failed check get fixed faster, by a tired user, at 03:30? Both outcomes below answer with a complete spec.

### Chosen form (lens-preferred, NOT decided) — OUTCOME A: Guard dissolved; repair becomes a Shield module, preferences move to the band's PARK drawer

- **Status:** lives in chrome only (Element 1). Nothing about status is rendered inside Shield's module except the same failed check's name when arrived at from the chrome line (arrival context, not a second status rendering — the module opens pre-scrolled with the failed check's row expanded; it does not re-print `NOT READY · 2 OF 4`).
- **Repair module — `BLOCKING` section at the foot of Shield** (below routines and the apps list, because repair is rarer than either): four rows, one per check, each row = check name in Atkinson Hyperlegible 14sp (`Blocking service`, `App-usage access`, `Overlay`, `Blocked list`), its real state as a word at the right margin in Anonymous Pro 10sp (`ON` / `OFF` / `47 APPS`), and — only when the state is failing — a text action (`OPEN SETTINGS`, `RETRY`, `EDIT LIST`) that deep-links or retries. Passing rows have no action: a working system offers nothing to press. Row height 52dp, hairline separators, no icons, no fills; a failing row's state word sits one ink tier stronger (weight, not colour — the Element 1 rule, repeated nowhere else).
  - List repair (`EDIT LIST`) edits the same enforced list Shield already owns — one list, one home, finally true in both directions: the count in chrome, the list in Shield, the service's hash over both.
  - Motion: module rows change state word by 90ms cross-fade; `RETRY` spins nothing — it re-queries (<300ms) and the word changes or doesn't. Haptics: `tick 5ms @ 0.4` on a row flipping to passing while he watches; silence otherwise. Sound: explicit silence. Depth: rows flat on Shield ground, failing row's action printed in a recessed well one step down — the one place a thumb is invited.
- **Preferences:** sound + theme move to the PARK-detent drawer off the bottom band (the destination the synthesis already sketched): two rows, words not switches-as-decoration — `SOUND EFFECTS · ON` as a tappable word-box that flips the word. No readiness content in the drawer, ever.
- **What the nav loses / gains:** bottom band returns to three stops (Deck · Shield · Tasks). One less destination whose only daily use was checking a fact chrome now tells for free.
- **Standards check:** ARM — repair is off the arm path; the chrome line's jump lands pre-scrolled on the failing row, so worst-case repair is: tap line → tap `OPEN SETTINGS` → system toggle → back = within his control, two in-app gestures. HOLD — nothing here is reachable mid-session (band locked in session, per Session Cloister discipline); a mid-session permission failure surfaces only via Element 2's unlock note, which points here. TRUTH — status rendered once (chrome); the module renders *actions and per-check states*, which are different facts from the aggregate verdict, and renders them only on the repair surface.

### Runner-up — OUTCOME B: Guard kept as a destination; rebuilt as the readiness checklist and nothing else

- **Form:** Guard tab stays (fourth band stop). Its screen is exactly: the four checks as full rows (same names, states, and conditional actions as Outcome A's module — identical components, identical data), headed by the aggregate sentence in human language, Sora Medium 18sp: `Blocking is on and covering 47 apps.` / `Blocking is not fully on.` — a sentence, not a score. Preferences (sound, theme) are **evicted** from Guard under this outcome too — they go to the PARK drawer as in Outcome A — because a readiness screen that also holds a theme toggle teaches the eye that readiness is a preference. Websites management stays inside Guard under B (it is list content, and the list is readiness content) as a section below the checks: `BLOCKED WEBSITES · 12` with its edit action.
  - Motion / haptics / sound / depth: identical component specs to Outcome A (90ms word cross-fades, single tick on repair-while-watching, silence, hairlines, recessed action wells, zero orange, no fills on passing rows).
- **Why it lost (on this lens only):** It gives an episodic repair job a permanent nav stop, and a permanent stop invites habitual visiting — each visit re-rendering, in a second home, a verdict chrome already states. The checklist itself is flawless; its *address* is the cost. It wins outright on one real axis, discoverability: a new user who has never seen the chrome line fail can still find "the blocking screen" by name. If Sarthak weights findability over nav economy, B is the correct pick and A is over-clever. **That weighting is his, not this seat's — his call.**

**The lens-preference, labelled:** Readiness is a property of the system, not a place in the app. Systems are monitored from where you already stand (chrome) and repaired where the related machinery already lives (Shield's list, the service's settings). Outcome A. — *Preference only. Guard's fate remains Sarthak's open pick, and both outcomes above are specced to ship without rework to Elements 1–3.*

---

## Ledger — dependency map (question OPEN; the call is Sarthak's)

Everything in this file that touches the Monolith-native record, in one place, with its dependency stated. Readiness/truth state is Monolith-native too, so this section also answers the chrome question put to this seat directly.

**Fork-INDEPENDENT (stand under (a), (b), and (c) alike):**
- The intercept log, the coverage-gap record, the banked-hold accumulation, and the session timestamps are **written** during the session regardless of outcome. A record that is never *kept* still has to *exist* transiently, or the end-of-session receipt and the unlock-time gap line have nothing true to print. "Kill the Ledger" can only honestly mean "kill its kept, browsable form" — it cannot mean "stop measuring," or the app's remaining truth claims (READY, blocked counts) lose their audit trail.
- **The chrome Truth Line (Element 1) is not the Ledger and does not depend on it.** It is present tense: *will blocking work right now, over how many apps.* It earns its chrome corner on exactly one ground — it is the only fact in the app that is both (i) needed before every arm and (ii) false-able by the OS outside the app's control (a permission revoked in Settings at noon must be visible at 17:00 without opening anything). That is what earns the corner: **liveness under external change**, nothing else.

**What does NOT earn a chrome corner — stated exactly, since outcome (b) names "chrome corner" as a possible Ledger home:**
- No historical total belongs in chrome under any outcome: no deflection count, no holds-spent, no sessions-this-week, no attendance figure. Reason, from this lens: chrome already holds one present-tense verdict. A past-tense number printed beside it blurs tense — at a tired glance `READY · 47 APPS BLOCKED` next to `DEFLECTIONS 212` reads as one status, and a cumulative deflection total is a score, and a score in chrome is the streak tower rebuilt at 10sp. If the record lives under (b), its home is the **session-end sheet** (first rendering) and optionally a **Shield-foot section** (kept rendering) — never chrome. Chrome's corner stays spent, entirely, on the live verdict.

**Fork-DEPENDENT (flagged inline at each site):**
- Element 2, dismissal: deflection totals — counted always; rendered/kept per outcome.
- Element 2, failure honesty: coverage gaps — admitted live at unlock always; persisted per outcome.
- Element 2, Guard fork note: the receipt's overlay-gap note inherits both flags above.
- Element 3, abort completion: the `ENDED EARLY` line is transient under all outcomes; the receipt following it persists per outcome. (Element 3's runner-up rejection — "elapsed time's one honest home is the receipt" — likewise means: the receipt *moment* under (c), the receipt *plus kept record* under (a)/(b). The rejection of live elapsed stands under every outcome.)

**This lens's position, labelled as preference, not verdict:** Against (c), for a truthfulness reason only: (c) makes every coverage failure unprovable after the fact. The app would still print READY every morning — but with no kept record of the mornings it was wrong, READY becomes an assertion nobody can audit, including Sarthak. A truth system that deletes its own failures is theatre with better typography. Between (a) and (b) this lens prefers **(b), session-end sheet + Shield-foot section**: the Monolith-native record (sessions sealed, deflections, holds spent, gaps, attendance) is consulted rarely and always after the fact; a tab invites daily visiting, and daily visiting is how a record becomes a score. But the (a)/(b) weighting — permanence and pride of place vs. quietness — is **Sarthak's call**, and every spec in this file ships under either without rework.

---

## Too good to lose

### 1. THE RECEIPT GAP LINE (un chosen surface, chosen honesty)

⚑ LEDGER-DEPENDENT — placement given under all three outcomes; the line's content never changes. The session's interception and coverage facts, stated once, after the fact — `BLOCKED OPENS TURNED BACK · 6` and, when applicable, `NOT COVERED 14:02–14:09 · OVERLAY OFF`. Spec: Anonymous Pro 10sp, quietest tier, printed last on the receipt; data from the session record's intercept log (timestamps captured at intercept / at the unlock that revealed the gap); no haptic, no sound, no motion — it is print. **Placement:** (a) rebuilt tab → last line of the session's receipt, and the same line in the tab's session history (one fact, one wording; the history entry is the receipt, filed — not a re-rendering); (b) demoted → last line of the end-of-session sheet; if a Shield-foot record section exists under (b), the *gap* line alone also files there, because a coverage failure is a repair-relevant fact, not a statistic — the deflection count does **not** file there (it is a statistic, and statistics in Shield become scores); (c) killed → the line prints on the transient end-of-session receipt and is then gone; this seat records, without re-arguing it, that under (c) the most truthful sentence the app can print has a lifetime of one viewing. Why it matters under any outcome: it is the only place HOLD's performance is ever totalled, it totals it where it cannot be gamed (after the session), and the gap line is the moment the app admits it failed. Standards: ARM 0, HOLD 0 decisions (post-session), TRUTH — within whichever home Sarthak picks, this is its *only* rendering; the live overlay deliberately shows no counter precisely so this line can be the one home.

### 2. THE SETTLE SECOND (airlock micro-behaviour)

When the Wall Card dismisses via `BACK`, the returning surface (Deck running state) holds its remaining-time numeral *unchanged for one extra second* before resuming its normal minute updates — even if a minute boundary falls inside that second. Spec: a 1000ms render-freeze flag on dismiss, purely presentational, clock math untouched underneath (the next render is fully correct; nothing is owed or rounded). No haptic, no sound, no visible marker. Why: without it, a user who opens a blocked app at 31:59 and returns at 32:01 sees the number drop the instant he returns, and the wall *feels* like it taxed him two minutes for one glance — a felt falsehood generated by true numbers. The settle second keeps the felt truth aligned with the real one. Flagged honestly: this is a presentation delay of a true value, bounded at 1s, never cumulative, and it must be first on the chopping block if TRUTH review reads it as a lie — the argument for it is perceptual accuracy, and reasonable reviewers can land either way.

## Mail / wall note

Inbox: two letters read, both thread `citation-requirement-lifted`, both relaying Sarthak's citation-lift order verbatim — Orson (Wild) at start, Mara (Wild) arriving before wrap-up; both moved to `mail/suki/read/` after reading. The same order arrived in my assignment directly and is reproduced verbatim at the head of this file, as instructed. Wall: read in full before writing; one signed entry appended (05:22 IST) recording this seat's reversal of its own wave-1 GO Poll under the settled law. No other mail sent. **Ledger-reopen revision (05:26 IST):** inbox re-checked — empty, no new mail. File revised per Marlowe's pass-down: amendment note added at the head, ⚑ LEDGER-DEPENDENT flags placed at Element 2 (deflection totals, coverage-gap persistence, Guard-fork receipt note), Element 3 (abort receipt persistence), and Too-good-to-lose #1 (placements specced under (a)/(b)/(c)); new "Ledger — dependency map" section states what is fork-independent, answers the chrome-corner question exactly (live verdict only; no historical total earns chrome), and labels this seat's (b)-leaning position as preference with the call Sarthak's. Second wall entry appended (05:26 IST) carrying that position to the Commons.
