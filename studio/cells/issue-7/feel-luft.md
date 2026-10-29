# Issue #7 — Shield: the room-tone of a standing wall

**Seat:** LUFT, FEEL SPECIALIST 5 (ATMOSPHERE), in VOGT's cell for issue #7,
"Main-app pass — Shield (audit fixes)."
**Domain, only this:** atmosphere — how Shield *feels* when the wall is
standing vs. when it is not; how session-night should differ from ordinary
dark; what the new status line's presence does to the whole screen's mood;
calm authority vs. surveillance; and the 3:30 AM test — a tired man glances
once and trusts it.
**Status:** DECLARED — feel specification only. No app code edited, nothing
compiled, no tests run. MONTEUR is sole code editor; §6 names what he would
build if VOGT adopts.
**Hard constraints honoured throughout:** Sora / Atkinson Hyperlegible /
Anonymous Pro only; no green or near-green anywhere in any proposal (the
palette's zero-green law, `Color.kt` header, is not worked around — §2.4
argues it is an atmospheric asset). No new hue is proposed at all.
**Sources read:** `studio/plan/AUDIT-shield.md` in full (R-1…R-13);
`ui/screens/ShieldMatrixScreen.kt` as the working tree stands (MONTEUR in
flight: `effectiveNight` :145, status sentence item 0, sled hosting);
`ui/ShieldBlockingStatus.kt` (the proof derivation and its four states:
LIVE / NO_SESSION / UNPROVEN / STALE); `MainActivity.kt` (`effectiveNight`
immediate vs. `groundNight` stepped, and the theme pin at the
`MyApplicationTheme` call); `ui/MonolithViewModel.kt` (`ShotGround` driver,
:764–768, :1095–1128); `ui/screens/TactileDeckScreen.kt` (the night split,
:266–269); `ui/theme/Color.kt`, `Theme.kt`, `Type.kt`; cell filings
`designer-form.md`, `critic-salz.md`, `finisher-glanz.md`,
`innovator-funke.md` — concurred with where noted; my charges are against
the *room*, not against my cellmates' surfaces.

---

## 0. The headline feel finding: session-night does not exist on Shield

There are, in the code, two colour schemes — warm stone day
(`WarmTactileStudioColorScheme`) and sealed-vault night
(`SealedVaultNightColorScheme`) — and one predicate spends the night one:

- `MyApplicationTheme` selects night when `darkTheme || isFocusSessionActive`
  (`Theme.kt`). A session and a user's own dark preference draw **the exact
  same scheme, token for token.** `NightColorBackground #171412`,
  `NightColorSurfaceBright #362C27` (the raised card, 1.35:1 over ground),
  cream primary `#FFE4CF` — identical whether he chose the dark at 9 PM to
  browse, or the SLEEP routine sealed the phone at 22:30.
- Shield's own `effectiveNight = isDarkTheme || isSessionActive`
  (`ShieldMatrixScreen.kt:145`) therefore answers a question the room never
  asks. Its only Shield consumer is the sled hardware
  (`CatalogueAppRow → view.isNightMode`). Nothing else on the screen —
  ground, density, accent spend, type voice — changes when the wall stands.

So today "session-night" is ordinary dark with a session somewhere behind
it. The room does not know a wall is standing in it. A man who set dark
theme himself gets the sealed room for free, all evening, wall or no wall;
a man in day theme gets the sealed room the instant a session is born.
Darkness on this screen currently means *a preference or a row exists* —
never *you are protected*. That is the atmospheric form of the audit's
headline charge (Fremd C1): not only is the sentence missing (now supplied
by MONTEUR's pass), the **mood is unqualified too** — and mood is read
before sentences are (§5).

Everything below is the cure, in one principle:

> **Room-tone is a claim, and it must be proved by the same witnesses as
> the printed claim.** A cosy sealed-feeling room over a dead wall is a
> *felt* lie — worse than a printed one, because it is absorbed
> pre-verbally and never audited. Shield may only *feel* sealed when
> `ShieldBlockingStatus` proves LIVE.

---

## 1. Three rooms, not two themes (Call A-1)

Shield needs three atmospheric states, mapped to the proof derivation that
already exists (`ShieldBlockingStatus.kt`) — not to `isSessionActive`:

| Room | When | Ground feel |
|---|---|---|
| **Ordinary room** | No session (NO_SESSION), in whichever scheme the user's own theme picks | The workshop: full working contrast, raised surfaces raised, editing invited. Day or ordinary dark — both are this room; the lights are his. |
| **Session-night** | Session **and proof LIVE** (both witnesses attest this session id, beat fresh) | The sealed room (§2): flattened, quieted, accent-fast. This — and only this — is what "the wall is standing" feels like. |
| **Claimed, unproven** | Session row active but proof UNPROVEN or STALE | **Ordinary room, deliberately.** No dimming into false calm, no alarm wash either. The room withholds its verdict; the status sentence (§3) carries the uncertainty in words. An unresolved room *feels* unresolved — that discomfort is the truth, at exactly the volume it deserves. |

Why the third room matters: the current predicate collapses rooms 2 and 3
into one instant night flip, so the screen's mood asserts precisely what
SALZ C-2/C-3 prove its words must not — enforcement — from precisely the
witness they disqualify — the row's flag. Splitting the rooms costs no new
machinery: the status derivation MONTEUR already built returns the proof
state; the room reads the same object the sentence reads. One derivation,
two renderings (print and tone), zero chance of them parting.

**What does NOT change the room:** a Safe Break, and a live peek lease.
During a break SHUT still enforces and PEEK is lawfully open
(`ShieldBlockingStatus` appends `· SAFE BREAK — PEEK APPS OPEN` to the
sentence) — the room stays sealed; the sentence names the aperture. A door
ajar by rule is not a fallen wall, and brightening the room for a
ten-minute break would teach him that breaks are escapes. One app out on a
2-minute lease likewise: the wall's room does not flicker for one door
opening and re-shutting.

---

## 2. What session-night should feel like (Calls A-2, A-3)

Session-night is not "darker." The night staircase is already well judged
(`Color.kt`: body text deliberately stepped down from ~16.8:1 "everything
shouted" to 11:1; a real muted tier at 3.5:1 exists). Session-night differs
from ordinary dark in **posture**, on three axes, all achievable inside the
existing tokens — no new colours:

### 2.1 Flattening — the room becomes a wall surface

Ordinary dark keeps the staircase's invitation: ground `#171412`, recesses
below it (`#100E0C`), the raised card a full step up (`#362C27`, 1.35:1) —
furniture you can touch. In session-night the raised step **steps down
toward the ground**: raised surfaces sit at or near ground value, recesses
unchanged below. The screen stops being a room with objects in it and
becomes the wall itself — flat, continuous, nothing protruding to fiddle
with. He can still edit (the ratchet permits strengthening; refusals print
at the control, R-7), but nothing on the surface *invites* editing. A
workshop invites; a wall just stands.

Concretely for MONTEUR (if adopted): while proof is LIVE, Shield's raised
fills (`surfaceBright` consumers, cap/sheet fills) resolve one staircase
step lower than the night scheme's default; ground and recess tokens do
not move. Priced as a token-resolution change at Shield's call sites, not
a third colour scheme.

### 2.2 Quieting — the edges recede, the facts don't

In session-night, secondary print (section furniture, meta lines, filter
words unselected) sits at the muted tier it already has; the standing
facts — status sentence, tally numerals, group words, sled positions —
stay at full ink / body tier. The *ratio* between fact and furniture
widens; in the ordinary room they compete more evenly, because there the
furniture (search, filters, editors) is the work. Sealed, the work is
done; the room's remaining job is testimony. This is a density ruling, not
a brightness one: nothing vital is ever *moved into* the muted tier (§5.3
— at 3:30 AM the 3.5:1 tier effectively vanishes), the furniture is simply
not promoted out of it while sealed.

### 2.3 Accent fast — orange leaves the sealed room

The night accent `#F47442` is the loudest object available on a dark
ground. In session-night it should be **absent from Shield entirely**:
no status colour (§3.2), no tally flash, no cap accent (the cap already
practises "accent abstinence" — audit acquittals). The one permitted
orange in the sealed room is a ratchet refusal at the control touched
(R-7), for exactly as long as the refusal stands — orange as an *event*,
never as wallpaper. The ViewModel's own comment names the entry
choreography: in the 400 ms pre-ground window "the scheme holds its day
values while the orange exits" — the accent leaves first, then the ground
steps. That ordering is atmospherically right and §2.5 keeps it: first
the noise goes, then the room settles.

### 2.4 The zero-green law is the calm solution, not a constraint on it

The conventional dashboard move — green tick, green "PROTECTED" — is
forbidden here, and the room is better for it. Green status light is
surveillance-console grammar: it says *a system is monitoring and reports
nominal*. Ink-and-solidity grammar (§3.3's glyph) says something else: *a
fact, in the house's own materials*. Every proposal in this file stays
inside the warm stone / cream / single-orange staircase. Nothing here
needs a "go" colour, because nothing here is a traffic light.

### 2.5 The ground step — one step, together, or the room trips (A-3)

The Deck already knows how a room changes state: the hero reads night at
t=0, the **ground steps at t=400** with the scheme pin
(`TactileDeckScreen.kt:266–269`; `ShotGround.SEALED_PRE_GROUND → GROUNDED`
at t0+400, `MonolithViewModel.kt:1095–1110`), and a session this process
did not watch seal — routine-born, restored — "stands GROUNDED from its
first sighting — no deferral theatre for a shot nobody was present for."

Shield currently breaks that choreography into three unsynchronised
flips:

1. Sled hardware: **instant** — Shield's `effectiveNight` reads the host's
   immediate `effectiveNight = darkTheme || sealed`
   (`MainActivity.kt:256`), bypassing the ground step entirely.
2. Compose scheme (ground, text): **stepped** — the theme pin waits for
   `ShotGround.GROUNDED`.
3. Status sentence: **instant and unanimated** — it appears in whatever
   state the first composition derives.

For ~400 ms after every hand-arm, Shield is a split-brain room: hardware
already night, ground still day, verdict already printed over both. Four
hundred milliseconds is not subliminal — it is exactly the duration the
seal shot spends making the Deck's transition *felt as one event*. On
Shield the same interval is spent as a glitch.

**Call:** Shield's sleds take the **stepped** night (`groundNight` /
`GROUNDED`), not the immediate predicate, so hardware, ground, and scheme
step once, together, at t+400 — a door closing is one sound, not three.
The sentence may print at t=0 (facts don't wait for furniture), but the
*room* steps as one body. And the born-settled law is kept whole: opening
Shield mid-session, or on a routine-born session, shows the sealed room
already settled — walking into a room that sealed hours ago must not
replay the sealing. Atmosphere that re-performs its transition on every
visit is theatre; this room's authority is that it was already like this
before he arrived.

**Priced follow-up, not in this merge (for VOGT/Ren): the exit step.**
Entry steps down over 400 ms; exit currently snaps — session ends,
`NOT_SEALED`, and if his own theme is light the ground jumps straight to
warm stone day `#E4DFDA`. A SLEEP session ending at 03:30 therefore ends
in a floodlight: the brightest event of the night, delivered to the eyes
least able to take it, at the exact hour §5 tests. A symmetric step *up*
(same ground-step machinery, reversed) is the named cure. It touches the
host theme pin, so it is not Shield-merge scope — but it should be filed
now, while the asymmetry is visible.

---

## 3. The status line: what its presence does to the room (Calls A-4…A-7)

The sentence (MONTEUR's build, from `ShieldBlockingStatus`):
`BLOCKING NOW — SLEEP SESSION · ENDS 03:30` /
`NOT BLOCKING — NO SESSION RUNNING` /
`NOT BLOCKING — … ENFORCEMENT NOT PROVEN` /
`NOT BLOCKING — SHIELD PROOF STALE`. Its arrival changes the screen's
genre. Before it, Shield was an inventory that *implied* protection;
with it, every SHUT below is a claim made under a standing verdict.
That is the audit's intent and the feel is right: the screen goes from
*filing cabinet* to *posted watch*. Four rulings keep that watch calm
instead of carceral:

### 3.1 Presence, once — the ceiling of the room

The sentence is the room's ceiling: item 0, first, qualifying everything
under it. It should be encountered **exactly once per visit**. Do not pin
it, sticky it, or repeat it in the header band. SALZ C-7 is right that it
scrolls away from the deep catalogue — and the atmospheric answer is
*not* a second, hovering copy. A verdict that follows him down the list
stops reading as a posted notice and starts reading as a watcher: the
difference between a guard at the gate and a camera over every aisle.
§3.4 gives the travelling proof instead.

### 3.2 Colour: the current dress inverts the room (A-5) — change it

As built in the working tree, the sentence prints in
`AnonymousProFamily` Bold 13sp, coloured:

- LIVE → `scheme.onBackground` (quiet ink), 
- everything else → `scheme.error`.

`scheme.error` is theme-dependent: day scheme `TokenAlert #D63E06` (deep
red-orange), night scheme re-voiced to the accent `#F47442` itself
(`Theme.kt`, Chrome C7 comment). So the *failure* states are the warmest,
brightest, most saturated object on the screen — in a dark room, the only
orange — while success whispers in ink. Three atmospheric failures follow:

1. **The alarm is the room's focal point.** At any hour, his eye is pulled
   to the orange line first. A screen whose loudest element is its bad
   news feels like an incident board, however true the news is.
2. **A scheduled ending reads as a failure.** SLEEP ends 03:30 *by his
   own order*. Glancing at 03:31 to `NOT BLOCKING` in alert orange frames
   a completed plan as a breach. NOT BLOCKING is often the *correct*
   state of the world — morning, finished, stood down. A colour that
   cannot distinguish "wall fell" from "watch ended" moralises both.
3. **The same sentence changes temperature with the theme** (deep alert
   by day, warm accent by night), so the room's emotional register for
   one fact is unreliable — and register is precisely what a tired
   reader trusts when he can't yet parse words.

**Call:** the sentence prints in **full ink in every state** —
`onBackground`, day and night, LIVE, NO_SESSION, UNPROVEN, STALE alike.
States are distinguished by verb and solidity glyph (§3.3), never by
colour. Orange's only Shield appearances remain the refusal event (§2.3)
and nothing else. This also retires a token trap GLANZ/FORM flagged in
passing: an error slot whose night value *is* the accent makes "alert"
and "decoration" the same paint.

### 3.3 Voice: a whole sentence in telemetry face is a log, not a notice (A-4)

Anonymous Pro is the house's telemetry face — `Type.kt` is explicit:
"timers, serial numbers, peek tags, quota badges." A one-line *verdict
about his own phone*, set entirely in that face, reads as system output
*about* him: a log line, a monitor's report. That is the surveillance
register entering through typography. The house already owns the calmer
grammar — the tally's mixed construction (Anonymous numerals, Atkinson
words) and FORM §2.2's dress — and the feel reason to adopt it is this:
**Atkinson words are the house speaking to him; Anonymous numerals are
the machine keeping count.** A sentence that is mostly words should be
mostly the speaking face, with `ENDS 03:30` in mono as the counted part.
Keep the fixed slot, single line, and whole-line 260 ms Curve-A
re-strike on state change (the tally's manners — GLANZ §3.4, FORM §2.4);
a verdict does not roll, pulse, or tick. In particular the `ENDS` time is
a *horizon*, printed once and held — the moment it becomes a countdown
the room acquires a clock-watching anxiety it did not have. A wall with
a known end is a night watch; a wall counting down at him is a timer he
is failing.

Pair the line with the solidity glyph (solid ink square / 55% / hollow
frame, Panzer's scale via FORM §2.2): shape is read before words (§5),
and a tired eye that gets *solid = standing* from geometry alone has
already passed the test before it reads anything.

### 3.4 The travelling proof is the ground, not a banner (A-6)

What qualifies the deep catalogue while he scrolls is the **session-night
ground itself** (§1–§2). It never scrolls away, it needs no duplicate
print, and it testifies peripherally the whole way down: *this list is
being read inside the sealed room*. That is why §1's truth rule matters
so much — if the ground could claim sealed on the row's flag alone, the
travelling proof would be a travelling lie, felt at every scroll
position. Sentence at the door, room-tone on the road: each fact one
home, in two registers.

### 3.5 No catch-memory in this room (A-7)

Resist, on feel grounds, any corroboration furniture on Shield of the
`LAST TURNED AWAY — INSTAGRAM · 14:02 · n THIS SESSION` kind (FUNKE §4
proposes it as a second line; the mechanism verdict is his and VOGT's —
the *placement* is atmosphere, and it is mine to flag). A tally of the
wall's catches turns the room into a security office: it keeps score of
his temptations, in the room he opens to check his *rules*. A door does
not brag about who it turned away, and a shield that reports his reaches
back to him at 1 AM is surveillance wearing proof's clothes — however
honest the count. If that receipt lives anywhere, it lives with the
session's own record (Deck receipt / PARK / the drawer's quiet history),
never as standing furniture on Shield. Shield's mood is *the wall as he
left it* (the audit's one job, clause two) — a state, not a rap sheet.

The same ruling covers proof-age prints, heartbeat indicators, and
"checked 4s ago" tickers, should any be proposed: freshness is the
derivation's internal discipline (the 10 s beat law in
`ShieldBlockingStatus`), never a displayed pulse. A room that shows its
own checking is a room asking to be watched checking.

---

## 4. Calm authority vs. surveillance — the standing test

Every future Shield proposal can be graded on one axis. This room is a
**posted watch**, and the two registers differ mechanically:

| Calm authority (this room) | Surveillance (not this room) |
|---|---|
| States the fact once, at the door, then goes quiet | Repeats, pins, follows, badges |
| Ink and solidity; colour only for an event (refusal) | Colour as a standing state (orange/green lights) |
| Human face for words, mono for counts | Telemetry face throughout — the system reporting |
| A horizon (`ENDS 03:30`), held still | Countdowns, ages, heartbeats, tickers |
| No memory displayed; the past lives in the drawer | Catch logs, reach counts, last-attempt receipts |
| Controls rest calmly; refusals are firm *at the touch* | Controls glow, nudge, or warn pre-emptively |
| Uncertainty printed plainly (`ENFORCEMENT NOT PROVEN`) and the room stays ordinary | Uncertainty dressed as either calm or alarm |

The ratchet is the model citizen of the left column and worth naming as
the room's temperament: it does not watch him browse the catalogue, does
not pre-warn, does not colour anything in anticipation — it simply
refuses, at the control, in words, when weakening is attempted (R-7's
in-place form). Authority that acts only at the moment of the act, and
is silent the rest of the time. The status sentence and the room-tone
should have exactly that temperament, and nothing on Shield should have
any other.

One sentence for the wall of the studio: **the wall does not watch him;
it watches the door.** Every design that keeps that sentence true lands
in the left column on its own.

---

## 5. The 3:30 AM test — walked, in order of perception

The scene, concretely: the SLEEP routine's session is running (or has
just ended at its 03:30 boundary — the audit's own example end-time). He
wakes, picks up the phone at minimum brightness, eyes half-adjusted,
working memory near zero. He opens Shield to answer one question: *is it
still shut?* He will give it roughly one second and one fixation. What
reaches him, in the order it reaches any human:

### 5.1 First: the ground (0 words, ~200 ms, peripheral)

Before any reading, the room's overall value registers. **Pass condition:**
session-night ground (§2) is distinguishable from ordinary dark *without
reading anything* — flattened, accent-free, deep warm `#171412` family —
so the answer begins forming pre-verbally: *sealed room*. **Today: FAIL**
— the ground is identical to the ordinary dark he may have set himself
(§0), so the first, fastest channel carries no information about the wall
at all. After a scheduled end, the inverse must also hold: ordinary room
again, calmly — not an orange incident (§3.2) and not, per §2.5's
follow-up, a snap to daylight.

### 5.2 Second: shape (1 fixation, no decoding)

The status line's solidity glyph (§3.3): solid square = standing. Shape
and density survive blur, low brightness, and half-open eyes in a way
13sp letterforms do not. **Today: absent** — the line is text only, and
its state channel is colour, the channel §3.2 shows is inverted. With
glyph + ink-only colour, a man who reads *nothing* still leaves with the
right answer if the glyph is solid — and with no false comfort if it is
hollow, because hollow reads as *not solid* even unnamed.

### 5.3 Third: the words (only if he reads)

`BLOCKING NOW — SLEEP SESSION · ENDS 03:30`. Atkinson words decode at a
glance (the family exists for legibility); the mono `03:30` matches the
clock in his head — *it ends when it was supposed to end; the night is
nearly done; go back to sleep.* That last inference is the trust the test
is named for, and it is carried by the horizon segment more than by the
verb. Two failure notes at this layer, both live today: nothing vital may
sit in the muted tier at night (3.5:1 over `#171412` at minimum brightness
is, for tired eyes, *absent* — the muted tier is for furniture only, §2.2);
and the routine row's Sora display name (16sp Bold, `RoutineRow`) is the
largest, most poster-like type in the room — in the sealed room it reads
as signage shouting over the verdict. Panzer's R-12 eviction of the Sora
guest (Atkinson Bold 15sp) is supported here on feel grounds: Sora is the
across-the-room display face (`Type.kt`: dial numerals, AOD); Shield at
arm's length, at night, has no across-the-room job.

**Verdict on the test as specified in this file:** ground says it,
shape confirms it, words name it — three channels, one fact, any one of
them sufficient, none of them alarming. As the tree stands today: one
channel (words), inverted colour, identical ground — a man must *read,
in the dark, a mono log line* to learn whether he is protected. That is
the distance this filing exists to close.

---

## 6. Calls summary — for VOGT to rule

- **A-1 · Three rooms.** Ordinary / session-night (proof LIVE only) /
  claimed-unproven (ordinary room, no false calm). Room-tone derives from
  `ShieldBlockingStatus` proof, never from `isSessionActive` /
  `effectiveNight` alone. Safe Break and peek leases never change the room.
- **A-2 · Session-night posture.** Flatten raised steps one staircase
  step toward ground; widen fact-to-furniture density ratio; total accent
  fast (orange = refusal event only). No new colours; zero-green kept.
- **A-3 · One ground step.** Sled `isNightMode` reads the stepped
  `groundNight`/`GROUNDED`, stepping with scheme and ground at t+400;
  born-settled law for pre-existing/routine-born sessions kept.
  **Follow-up filed:** symmetric exit step (no 03:30 floodlight).
- **A-4 · Sentence voice.** Atkinson Bold words + Anonymous Pro numerals
  (`ENDS` time only); fixed slot; whole-line 260 ms Curve-A re-strike;
  `ENDS` held as horizon, never a countdown; solidity glyph paired.
- **A-5 · Sentence colour.** Full ink (`onBackground`) in **all** states;
  `scheme.error` removed from the status line. Verdict by verb + glyph.
- **A-6 · No sticky duplicate.** The sentence prints once, in flow; the
  travelling qualifier down the catalogue is the session-night ground.
- **A-7 · No catch-memory on Shield.** No last-turned-away line, proof-age,
  or heartbeat furniture on this screen; receipts live with the session's
  record elsewhere.
- **Verification (device, Ren's — paper claims end here):** at minimum
  brightness, in a dark room — (i) ordinary dark vs. session-night told
  apart with the status line covered; (ii) glyph state read correctly
  with the words covered; (iii) arm a session and watch Shield through
  t+400 — one step, not three flips; (iv) open Shield mid-session —
  room already settled, no replay; (v) let a session end on schedule —
  `NOT BLOCKING` reads as stood-down, not breached.

---

## Draft GitHub comment (for VOGT to post)

— LUFT (FEEL · ATMOSPHERE), for VOGT's issue #7 cell.

Feel filing delivered: `studio/cells/issue-7/feel-luft.md` (DECLARED — feel spec only, no code edited, nothing compiled).

Headline: **session-night does not currently exist on Shield.** The night scheme is spent identically by the user's own dark preference and by a live session (`Theme.kt`: `darkTheme || isFocusSessionActive` → one scheme), and Shield's `effectiveNight = isDarkTheme || isSessionActive` drives nothing but the sleds' `isNightMode`. Darkness on this screen means "a preference or a row exists" — never "you are protected." The mood is as unqualified as the audit found the print to be, and mood is read first.

My calls, for VOGT's ruling (A-1…A-7 in the filing): **Three rooms, not two themes** — ordinary room; session-night only when `ShieldBlockingStatus` proves LIVE; and a claimed-but-unproven session keeps the ordinary room, because room-tone is a claim and a cosy sealed feel over a dead wall is a felt lie, absorbed pre-verbally and never audited. **Session-night's posture** is flattening (raised steps step down toward the `#171412` ground until the screen reads as wall, not furniture), quieting (facts stay full ink, furniture stays muted), and a total accent fast — orange survives only as the ratchet-refusal event; no new colours are proposed and the zero-green law is kept, because ink-and-solidity is calmer grammar than a green "protected" light in any case. **One ground step:** Shield's sleds currently flip instantly on the host's immediate `effectiveNight` while the scheme steps at t+400 (`ShotGround.GROUNDED`) — a ~400 ms split-brain room after every arm; sleds should read the stepped night so the room closes as one sound, with the born-settled law (routine-born/restored sessions show the settled room, no replayed sealing) kept whole. Filed as a priced follow-up: the exit step — a SLEEP session ending at 03:30 currently snaps to light ground if his own theme is light; the brightest event of the night, at the worst hour.

On the status line itself: its presence rightly changes the screen's genre from filing cabinet to posted watch, and four dress rulings keep that watch calm rather than carceral. **Colour (A-5):** as built, LIVE prints in quiet ink and every other state in `scheme.error` — deep alert `#D63E06` by day, the accent `#F47442` by night — so failure is the warmest, loudest object in the room, and a session that ended *on schedule* at 03:30 reads as a breach. Print the sentence in full ink in all states; state by verb + solidity glyph, never colour. **Voice (A-4):** a whole verdict in Anonymous Pro is a log line — the telemetry face reporting on him; Atkinson Bold words with mono reserved for the `ENDS` numerals is the house speaking, and `ENDS` must stay a held horizon, never a countdown. **Presence (A-6/A-7):** print it once, in flow — do not pin a following copy (a verdict that follows him down the catalogue is a watcher, not a notice); the travelling proof while scrolling is the session-night ground itself. And no catch-memory on this screen — no last-turned-away line, proof-age, or heartbeat furniture; a door does not brag about who it turned away.

The 3:30 AM test, as the standard: ground says it (peripherally, 0 words), glyph confirms it (one fixation, survives blur), words name it — three channels, one fact, any one sufficient, none alarming. Today there is one channel — mono words in inverted colour over an identical ground. The filing walks the test channel by channel and lists the five device checks that would prove the room, at minimum brightness, in a dark room. The one-line test for everything proposed on this screen afterwards: **the wall does not watch him; it watches the door.**

— LUFT

---

*LUFT, feel specialist (atmosphere), VOGT's issue #7 cell — Oct 30, 2026.
DECLARED: feel specification only; no app code edited, nothing compiled,
no tests run, nothing committed or pushed. GitHub coordination is VOGT's.*
