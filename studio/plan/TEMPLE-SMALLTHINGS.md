# TEMPLE — SMALL THINGS: the build answers (Kleinod, Small Things seat)

*Kleinod (small treasure), Small Things seat in Stein's Temple crew.
Filed Oct 29, 2026, ~22:22 IST. For the builders — Maurer, Setzer —
working the Temple lane: `builds/look-temple/Focus-app/`. Stein
coordinates; Ren coordinates Stein. This file is the register answered
for Temple, item by item, in the form a builder can build from. It
does not compile anything and it claims no machinery that is not
Temple's to claim.*

*Sources, read whole: plan/SMALL-THINGS.md (the register — ST-01…ST-08
+ the yardstick: the pill, editable peek, editable break) ·
plan/DEEPEN-temple.md §§7/8/9/10 (Temple's filed answers; where this
file and the deepening disagree, this file names the disagreement —
§6) · plan/PILL-AND-TIMES.md (Freya's derivation — the pill's and the
times' machinery; Temple designs faces, Freya's derivation binds) ·
plan/PRESENTATION-temple.md (phase 2 — §7 the Held Door, §9 feel law) ·
plan/SMALL-THINGS-SPECS-PASS.md (Klar's pass — Temple's starting
position: pill ABSENT, break unaddressed, cooldown unprinted, no
stance on either duration, no printed hour) ·
plan/TEMPLE-DAYFIT.md (Pilger's day-fit pass, via Stein's watch
letter of Oct 29, 22:56 IST — ruled on at ST-06 below).*

## 0. How to read the verdicts

Every register item gets exactly one headline verdict:

- **BUILT-IN-THIS-LANE** — view, composition, words, and call-site
  voice parameters on a tick that already runs. The surface and the
  file it lands in are named. Maurer/Setzer build it in
  `builds/look-temple/` without waiting for anyone.
- **PRESENTATION-READY-AWAITING-MACHINERY** — Temple's face is fully
  designed here (and in DEEPEN §§7–10); it cannot function until the
  named machinery lands. The machinery and its owning lane are named.
  Three pieces of machinery are **Ren-ruled and are not claimed
  anywhere in this file**: the **END BREAK verb**, the **duration
  settings storage**, and the **add-time writer**. A Temple surface
  that needs one of them hosts it, prints from it, and stops there.
- **REFUSED-BY-NAME** — Temple declines, with the argument on the
  record. There is exactly one: **no sound at the 03:30 stand-down**
  (ST-05 — argued in one paragraph, as the register deserves).

Lane file key (all paths under
`builds/look-temple/Focus-app/app/src/main/java/com/example/`):

| Short name in this file | File |
|---|---|
| Deck | `ui/screens/TactileDeckScreen.kt` |
| Shield | `ui/screens/ShieldMatrixScreen.kt` |
| Door + Clock | `ui/components/AirlockAndDisarmOverlays.kt` (Held Door intercept + AOD desk clock) |
| Receipt | `ui/components/ReceiptSheet.kt` |
| Courses / strip | `ui/components/MonolithCustomComponents.kt` + Deck |
| Ring / bolt | `ui/views/BlockitCoreViews.kt` |
| **Tablet (NEW)** | `ui/components/PillTablet.kt` — builders create this file; the face only |
| VM | `ui/MonolithViewModel.kt` |
| Sound engine | `service/TactileSoundEngine.kt` |
| Shield service | `service/MonolithShieldService.kt` — the pill's driver lives here, and it is **Pastille's machinery**, not this lane's |
| Heartbeat | `service/RoutineHeartbeat.kt` — **Wache / Ren** family, not this lane's |
| Data | `data/MonolithModels.kt` · `data/MonolithRepository.kt` · `data/MonolithDatabase.kt` — machinery lanes only; **DB v11 compatibility binds this lane: no schema change in the look lane** |

## 1. The verdict table — nothing dies by silence

| Item | Verdict | Temple surface | Lands in |
|---|---|---|---|
| **Y-1 · The pill** | **AWAITING-MACHINERY** (face built-in, §4) | The hanging bronze tablet, 3 faces | Face: Tablet (NEW). Window + derivation: Shield service — **Pastille / Ren** |
| **Y-2 · Editable peek** | **AWAITING-MACHINERY** (inscription built-in) | `PEEK — {n} MIN` cut in the Shield gate course head; typed re-cut | Inscription: Shield. Storage: GuardSettings `peekMinutes`, migration 11→12 — **Pastille / Ren** |
| **Y-3 · Editable break** | **AWAITING-MACHINERY** (inscription built-in) | `BREAK {n} MIN` tablet on the Deck base course; typed re-cut | Inscription: Deck. Storage: GuardSettings `breakMinutes` + session `breakSeconds` stamp — **Pastille / Ren** |
| **ST-01 · No clock tells the time** | **BUILT-IN-THIS-LANE** | Hour on the lintel; `NOW HH:mm` on every frieze; hour on the clock's stand; idle clock prints the device's time | Deck · Shield · Door + Clock |
| **ST-02 · Break cannot end early** | **AWAITING-MACHINERY** | `END BREAK` bronze tablet on the threshold course; pill BREAK face body-tap returns to it | Tablet: Deck (+ Tablet face). Verb: **END BREAK — Ren-ruled** (Truth Car 3 / Wagen neighbourhood) |
| **ST-03 · Break boundaries silent** | **BUILT-IN-THIS-LANE** | One low knock at each flip (enter repose / leave repose), existing voice family | VM ticker call site + Sound engine (call-site params only) |
| **ST-04 · No add-time to a running block** | **AWAITING-MACHINERY** | `+ MIN` tablet on the dedication line; `EXTENDED +{n} MIN` row on the receipt | Tablets: Deck + Receipt. Writer: **add-time writer — Ren-ruled** |
| **ST-05 · Endings silent unless app open** | **AWAITING-MACHINERY**, with one **REFUSED-BY-NAME** instance (03:30 — see ST-05) | Tablet's dying knock in other apps; shield completion post in the shade (acquitted, kept) | Reach: heartbeat / shield family — **Wache / Pastille / Ren**. Face: Tablet |
| **ST-06 · No warning before a routine arms** | **AWAITING-MACHINERY** | One low knock 60 s before the boundary — "the lamp is lit before the rite" — **fenced: no knock before the FASTED arm (Pilger ruling, ST-06)** | Knock spec here; reach: Heartbeat — **Wache / Ren** |
| **ST-07 · Idle Deck says nothing next** | **BUILT-IN-THIS-LANE** | Frieze course at the Deck's base: idle, the next notch prints `NEXT — SLEEP 20:00` | Deck (seat is the Deck — Klar K-9; the Shield frieze is the second home, not the cure) |
| **ST-08 · Peek strands him; clock already running** | **AWAITING-MACHINERY — AND FLAGGED: half of it is silently missed by DEEPEN (F-1, §6)** | Countdown + hand-back: the pill PEEK face (§4). Grant-time launch to the peeked app: **no Temple surface exists — none is needed; it is one launch intent, machinery only** | Face: Tablet. Launch intent: VM grant path — **Pastille / Ren** |

Acquittals the register already filed stand untouched and are not
re-sold as answers: the shade notification, the Deck's `BREAK · N MIN`
eyebrow (housed on the base course by DEEPEN §7), the strip's
`ENDS HH:mm`, held messages readable mid-session, the `N/1 WEEK`
emergency count, the witnessed finish's E-clock voice, free PEEK-tier
unlock during a break, the routine arm's seal voice, the clock's
true-black ground + burn-in shift.


## 2. The yardstick — his orders, answered first

### Y-1 · The pill over other apps (session / break / peek time)

**Verdict: PRESENTATION-READY-AWAITING-MACHINERY.** The face is
Temple's and is specified in full at §4 of this file (the hanging
bronze tablet — exact prints, exact sizes, the derivation binding,
both knocks, the 03:30 behaviour). The face lands as a new file,
`ui/components/PillTablet.kt`, built in this lane.

What it waits for, named: the **overlay window + the
service-driven derivation** — Freya PILL-AND-TIMES §2, Route A
(`WindowManager`, `TYPE_APPLICATION_OVERLAY`, driven by a 1 s ticker
inside `MonolithShieldService`, window removed in `onDestroy` and on
the first ABSENT read). Owner: **Pastille (the pill lane) / Ren**.
Temple claims neither the window, nor the driver, nor the derivation
code. The permission is not priced as new: `SYSTEM_ALERT_WINDOW` is
already declared, already watched (`WatchStore` OVERLAY), already
grantable from the Guard's FIX key (Freya §1.5). It must **not**
become a counted fifth Guard item.

Fallback honesty (see also F-2, §6): where the grant is missing or
revoked, Freya's **Route B** — the shield notification upgraded
(chronometer, peek face, END PEEK action) — is the floor. Temple has
no grammar for Route B and does not invent one here; the floor ships
in Freya's stock wording or it does not ship in Temple's name. The
tablet must never be faked over the shade.

### Y-2 · Editable peek duration

**Verdict: PRESENTATION-READY-AWAITING-MACHINERY.**

- **Built in this lane (the inscription):** Shield, ALL APPS course
  head — the inscription **`PEEK — {n} MIN`**, printed from the
  setting, never a hardcoded `2`. Tap the inscription → the cutting:
  **typed direct entry of minutes** (the exact-value law the routine
  plaques already obey — known values are entered, never stepped to).
  Every downstream print reads the same setting: the Shield gate
  chips (`PEEK {n} MIN`), the Held Door side door (checklist §7.3, Door + Clock),
  the pill's offer arithmetic. File: Shield (`ShieldMatrixScreen.kt`).
- **Awaited (not claimed):** the **storage** — `peekMinutes` on
  `GuardSettingsEntity` (`data/MonolithModels.kt`), default **2**,
  bounds **0–30** clamped by the setter (0 = peeks off: no grant, no
  offer — Freya §3.5), read at exactly one place, `startPeekLease` at
  grant time, which already stamps an absolute lease end — so the
  setting needs no second stamp. Joint migration **11→12**. Owner:
  **Pastille / Ren** (PILL-AND-TIMES §§3.1–3.6, 4.1). No schema change
  in this lane.
- **Mid-flight law, binding on the inscription's wiring (Freya
  §3.4):** an edit prices the future, never the present. A live lease
  keeps its written end; the inscription's tap writes the *setting
  row only* — there is no code path from the inscription to any live
  lease, break pair, or span. If a builder wires it otherwise, this
  item becomes a lie; see F-4.

### Y-3 · Editable break duration

**Verdict: PRESENTATION-READY-AWAITING-MACHINERY.**

- **Built in this lane (the inscription):** Deck, base course, beside
  the course of set stones — a small inscribed tablet **`BREAK {n}
  MIN`**, printed from the setting; tap to re-cut, typed minutes.
  The break shapes the span, so its measure is cut where the span is
  composed (DEEPEN §9.1) — not buried in a settings room. The repose's
  own line (`BREAK · N MIN`, DEEPEN §7) and the pill's BREAK face
  (§4) print from the session's stamp, not from the setting. File:
  Deck (`TactileDeckScreen.kt`).
- **Awaited (not claimed):** the **storage + the stamp** —
  `breakMinutes` on `GuardSettingsEntity`, default **10**, bounds
  **0–60** clamped (0 = no gaps; the span stays honest at
  (n−1) × 0), **plus `breakSeconds` stamped onto
  `FocusSessionEntity` at creation** (creation reads the setting
  once; the three literal `600`s in the span formula —
  `MonolithModels.kt` span / cycle / in-break elapsed — are replaced
  by the stamp, collapsing four sources of truth to one). The dead
  `StudyModePreset.breakMinutes` field (no reader in the tree) is
  **not** revived; it is not the home. Joint migration **11→12**.
  Owner: **Pastille / Ren** (PILL-AND-TIMES §§3.1–3.2, 4.1).

## 3. The register, ST-01 … ST-08

### ST-01 · No clock anywhere tells the time — BUILT-IN-THIS-LANE

Register: every face formats HH:mm for other facts; no face prints
the current time; idle, the desk clock renders the composer's dial
setting dressed as a time (`50:00`). Price in the register: **none**.

Temple's prints (DEEPEN §9.3), all view-only reads of the device's
own clock — no new state, no machinery, no one's lane but this one:

1. **The lintel** (Deck): the current hour inscribed at the beam's
   right end, Anonymous Pro, glyph tier — format `HH:mm` (24 h, the
   frieze's own convention). Updates on the minute cadence the Deck
   already ticks on; it is a print, not a second hand.
2. **Every frieze** (Deck base course · Shield entablature · the
   Held Door miniature): the gold NOW marker carries its print —
   **`NOW HH:mm`** set at the marker in Anonymous Pro. A drawn
   position without its number was a map without a scale.
3. **The desk clock's stand** (Door + Clock): the hour inscribed at
   the right end of the brass stand, glyph-sized, under the same
   55% composite law as the numerals (measured 0.201 vs Nacht's 0.25
   ceiling — PRESENTATION §6/§10).
4. **The idle lie dies:** idle, the desk clock prints **the device's
   time** — never the dial setting. DEEPEN §9.3 flagged this
   `[MERGE]`; in the all-looks-in-app world that flag is a trap
   (F-3): builders treat the idle branch of
   `AirlockAndDisarmOverlays.kt` as **lane work, now**, not as a
   merge errand. Running, the clock keeps its shipped truth — the
   hero counts the *span* (elapsed + remaining); the stand's hour
   is the addition, the span is not displaced.

Files: Deck · Shield · Door + Clock. Nothing awaited.

### ST-02 · A break cannot be ended early — AWAITING-MACHINERY

Register: the 10-minute safe break ends only on expiry
(`MonolithRepository.kt` progress updater); no end/skip/resume verb
exists. Price: small — the flip into the next block already exists.

- **Built in this lane (the host):** during the repose, the Deck's
  **threshold course** carries a bronze tablet — **`END BREAK`** —
  exactly where the gate bolt is drawn at an arm (DEEPEN §7). Bronze,
  not gold: ending a repose is a choice, not a commit (*bronze
  chooses, gold commits*). No vermilion. File: Deck. Second host:
  the pill's BREAK face — its body tap returns him to the Deck **at
  this tablet** (§4); the tablet is the repose's door from outside.
- **Awaited (not claimed):** the **END BREAK verb** — one VM verb
  that zeroes the remaining break and takes the existing flip into
  the next block. **Ren-ruled machinery**; DEEPEN §7 names Truth
  Car 3 / Ren, and Una's deepen audit verified the shape (the flip
  exists; only the verb is missing). Until Ren seats it, the tablet
  is **not rendered** — a tablet that promises a verb the tree
  cannot keep is a painted door, and Temple does not paint doors
  (ST-08 below; DEEPEN §6's law for the side door applies here
  verbatim). Builders: build the tablet behind the verb's existence,
  ship it the hour the verb lands.

### ST-03 · Neither break boundary makes a sound — BUILT-IN-THIS-LANE

Register: block → break and break → next block are silent state
flips; eyes on paper miss the break starting; peek apps re-lock
under his thumbs at its death. Price: small — the VM ticker already
watches the session every second.

Temple's answer (DEEPEN §§7, 9.2), built here:

- **Entering the repose: one low knock** — the unbarring knock.
  **Leaving it: one low knock**, at the flip. One knock per flip,
  the register's price adopted exactly. No knock *before* either
  flip, and no second knock at the repose's last minute — the
  design warns at boundaries he cannot see coming only where
  DEEPEN §9.2 says so (the peek, §4); the break's own countdown is
  printed on the Deck and on the tablet, in front of him.
- **Voice:** the existing voice family only — the seat knock at its
  floor (PRESENTATION §9: pitch toward 160–220 Hz, 24–32 ms, full
  amplitude — mass, never duration; **no temple bell**, the fraud
  line stands). Call-site parameters only; the seal's Throw is
  untouched and bit-identical.
- **Files:** the call sites land on the VM ticker that already
  observes the flip — `ui/MonolithViewModel.kt`, observing the
  Repository progress updater's two transitions (into
  `isInSafeBreak`, out of it) — with parameters against
  `service/TactileSoundEngine.kt`. No new engine, no new state, no
  schema. If the pill tablet is up when the flip fires, the knock
  is the same single knock — the tablet does not add its own
  (double-knock guard, §4.6).

### ST-04 · No way to add time to a running block — AWAITING-MACHINERY

Register: the session's clock grows by exactly one writer — the
tamper penalty. Price: small — the same write, by his hand.
Honesty flag (the register's own): the slip's planned-vs-lived
figures must show the extension.

- **Built in this lane (the hosts):** (a) Deck, dedication line —
  while a block runs, the line's right end carries a small bronze
  tablet **`+ MIN`** (DONE's neighbour; DONE keeps its own tablet).
  Tap → typed entry of minutes (exact-value law). File: Deck.
  (b) Receipt — whenever the session row carries an extension, the
  slip prints it as a fact row in primary ink: **`EXTENDED +{n}
  MIN`** (DEEPEN §10). File: Receipt. An extension the slip hides
  would be this world's first quiet lie; it is designed loud.
- **Awaited (not claimed):** the **add-time writer** — the hand
  that turns the penalty path's existing write (mutating the target
  under a fresh signature) to his intent. **Ren-ruled machinery.**
  Two conditions ride with the request, for Ren's ruling:
  (i) the writer must **record the extension on the session row**
  as a fact the receipt can read — a writer that only moves the
  target leaves the slip nothing truthful to print (F-5);
  (ii) until the writer lands, the `+ MIN` tablet is **not
  rendered**, under the same painted-door law as ST-02.


### ST-05 · An ending is silent unless the app is open — AWAITING-MACHINERY, one instance REFUSED-BY-NAME

Register: the one completion voice lives in the UI-layer finish
machine; the machinery that actually runs the night commands no
sound at any ending — the 03:30 sleep stand-down is "a plain
deactivation" (`RoutineHeartbeat.kt`), and a completion with the
app closed earns a shade notification and nothing else. Price:
small.

- **Presentation's part (built / hosted in this lane):** the ending
  now has a surface wherever he is — the **tablet's dying knock**
  in other apps (§4.6: when the derivation first reads the session
  ended, one low knock, then the tablet is gone), and the shield's
  **acquitted completion post** in the shade, kept as-is. In-app,
  the witnessed finish's E-clock voice already exists (acquittal)
  and is untouched.
- **Awaited (not claimed):** the **reach** — sound call sites in
  the heartbeat / shield family at the endings those services
  themselves execute (timer completion with the app closed;
  routine-driven endings). Owner: the **heartbeat/shield family —
  Wache / Pastille, ruled by Ren**. Temple designs the voice (the
  same low knock, ST-03's family); Temple does not reach into the
  heartbeat from a look lane.

**The refusal — REFUSED-BY-NAME: no sound at the 03:30 stand-down.**
The register is right about the general case and Temple answers it
above; at 03:30 it is wrong, and the distinction is the design, not
an exception smuggled past it: ST-05's harm is an ending a man
*needs to act on* going unheard — a finished block in a pocket, a
completion he is waiting to respond to — but the SLEEP stand-down
asks nothing of him, starts nothing, locks nothing, and arrives
while he is meditating with the phone across the room precisely
because the day was arranged so that nothing at 03:30 requires
him; a knock there would not inform him, it would interrupt the
one practice the whole timetable is built to protect, in order to
announce a *release* — the doors opening — as if a mercy needed an
alarm, and a sound whose only possible effect is harm to the
moment it enters is not a missing signal but a correctly absent
one; the signal itself is not withheld either, only its voice is:
when he opens his eyes the desk clock's face has changed from the
span to the hour (ST-01 above, prints 3–4), which is
information at the speed meditation allows, so Temple refuses the
03:30 knock by name, keeps every other ending's knock, and asks
the register to record the refusal as a ruling about *this*
boundary — a stand-down, not a summons — rather than as silence
about endings in general.

Scope of the refusal, so builders cannot widen it by accident: it
covers **only** the SLEEP routine's 03:30 stand-down (and, by the
same reasoning stated here so it is ruled rather than assumed,
any routine stand-down that *releases* rather than *arms* while
no session is live). It does **not** cover: a timed session that
happens to end near 03:30 under its own timer (dying knock stands),
a break flip, a peek end, or any *arming* boundary (ST-06 stands,
as fenced at ST-06 by the Pilger ruling — the fence is ST-06's own,
not an extension of this refusal).
The tablet's behaviour at this boundary is specified at §4.6.

### ST-06 · Nothing warns before a routine arms — AWAITING-MACHINERY

Register: the heartbeat computes the next boundary and arms the
chain; the first signal of the 20:00 sleep lock is the seal at
20:00 itself. Price: small — one knock a minute ahead is a
comparison the same tick can make.

- **Designed (presentation, complete here):** **one low knock, 60
  seconds before the boundary** — in this world's image, the lamp
  is lit before the rite begins (DEEPEN §9.2). Same voice family
  as ST-03 (seat knock at its floor; mass, not duration). One
  knock per boundary, fired once, at boundary − 60 s; no repeat,
  no escalation, no print obligation on any face (the print side
  of "what happens next" is ST-07, on the Deck). The knock fires
  whether the app is open or closed — that is its whole point; a
  man mid-sentence in a peek app at 19:59 is the case it exists
  for.
- **Awaited (not claimed):** the **reach** — the comparison and the
  call site live in `service/RoutineHeartbeat.kt`, which already
  computes the boundary every tick. That file is the heartbeat
  family's — the no-fire fix (Wache) works its stamps — and a look
  lane does not edit it. Owner: **Wache / Ren**. DEEPEN §9.2 names
  it "for Ren"; this file repeats the name so no builder mistakes
  the knock's design for its delivery.
- **The Pilger fence — RULED AND ADOPTED (Stein's watch item,
  Oct 29, 22:56 IST; Pilger, plan/TEMPLE-DAYFIT.md).** Pilger
  flags what DEEPEN §9.2 and this file's first draft both missed:
  the knock for the **05:00 FASTED arm fires at 04:59 — the last
  minute of the morning meditation** (his hour after the bath,
  phone across the room), and Temple already refuses sound at the
  03:30 stand-down on exactly this ground. The fence is adopted,
  on ST-06's own logic rather than by analogy alone: the pre-arm
  knock exists for a man **awake, active, and interruptible** —
  mid-sentence in a peek app at 19:59, with something to finish
  before the lock. A meditating man has nothing to finish and
  cannot act on a warning; the knock would buy him nothing and
  cost the meditation its last minute — and unlike the 19:59
  case, this boundary **announces itself anyway**: a routine's
  arm sounds the seal (register acquittal), at 05:00, the moment
  the meditation ends by his own design. So: **no ST-06 knock
  precedes the FASTED arm** — operationally, no pre-arm knock
  whose firing instant (boundary − 60 s) falls inside the
  meditation window the SLEEP stand-down opens and the FASTED
  arm closes. The seal at 05:00 is untouched and still sounds;
  the desk clock's face carries the hour (ST-01). Every other
  boundary's knock stands as designed above — 19:59 for SLEEP
  included: he is awake at 19:59, and the fence protects a
  practice, not an evening. Builders of the heartbeat reach
  (Wache/Ren): the suppression is a condition on the *routine
  being armed* (the meditation-closing arm), never a time-of-day
  mute — the same named-condition discipline as §4.6's 03:30
  carve-out, so a timetable edit moves the fence with the
  routines instead of stranding it at 04:59.

### ST-07 · The idle Deck never says what happens next — BUILT-IN-THIS-LANE

Register: the next routine boundary is computed every heartbeat
and printed nowhere in the UI; at 19:45 the Deck shows a wheel and
says nothing about SLEEP at 20:00. Price: small.

Temple's print (DEEPEN §§2, 10): the **frieze course at the Deck's
base** — the brass rule 03–21, the routines' spans notched, NOW
marked with its hour (ST-01) — carries, **when the Deck is idle**,
the next notch speaking in words:

> **NEXT — SLEEP 20:00**

Anonymous Pro, glyph tier, at the notch's own position on the
rule; the name is the routine's name and the hour its boundary,
both read from the same routine state the Shield plaques print —
no new fact is computed for the Deck, and none is invented. When
a session runs, the line retires (the courses carry the running
facts; NEXT is an idle face's sentence). File: **Deck**
(`TactileDeckScreen.kt`).

Seat discipline (Klar K-9, adopted): the Shield's entablature
frieze carries the same band (§4 of the deepening) and is the
second home — it is **not** the cure. ST-07 was filed against the
idle Deck; the print lands on the Deck first or it does not land.

### ST-08 · A peek doesn't take him back to the app — and its clock is already running — AWAITING-MACHINERY, half FLAGGED as missed (F-1)

Register, read carefully — it files **two** harms in one item:
(a) the peek tap starts the 120-second lease and hides the airlock;
he must walk back to the peeked app himself **while the paid
seconds burn** — the missing piece is a **launch intent to the
peeked app at grant** (the intercept machinery launches an activity
on the same evidence in reverse every day); (b) the peek's clock
runs invisibly — no countdown where the peek is spent.

- **Harm (b) — answered by design:** the pill's **PEEK face** (§4.5)
  counts the lease down **in seconds, over the peeked app**, and its
  incised **✕** hands the peek back early — the first reachable
  surface for the existing early-end verb. Face built in this lane
  (Tablet file); the window/derivation it rides on is Y-1's awaited
  machinery (Pastille / Ren).
- **Harm (a) — NOT answered by Temple's filed plan; flagged, not
  papered over (F-1):** DEEPEN §8 claims the PEEK face "answers
  ST-08 in the same stroke" because "the way back hangs in front of
  him" — that answers returning *to Monolith* and seeing the clock;
  it does not launch him *into the peeked app* at grant, which is
  the register's sentence. Temple has **no presentation** for harm
  (a) and needs none: it is one launch intent on the grant path
  (`MonolithViewModel.kt`'s peek grant, wiring in `MainActivity.kt`
  — the register's own evidence), priced small by the register.
  Owner: **Pastille / Ren**, with the pill machinery, since the
  grant, the lease end, and the tablet's first appearance must be
  one atomic moment: lease written → peeked app launched → tablet
  already counting when he lands. Builders in this lane: do not
  simulate the launch from the airlock composable; a composable
  that launches on composition will re-launch on recomposition and
  strand him worse.


## 4. THE PILL TABLET IN FULL — the hanging tablet

*This section is the builders' complete spec for Temple's pill.
DEEPEN §8 designed the object; it gave no dimensions — the sizes
below are this seat's fixing of that silence (F-6), set from
Temple's own grammar (chiselled geometry 2–10 dp; Anonymous Pro;
the glyph tier for small print) and binding on Maurer/Setzer.
Where this section and Freya's spec could be read to differ,
Freya's derivation governs the facts and this section governs
the face.*

### 4.1 The object

- **What it is:** a small **bronze tablet hung at the gate** — the
  plaque a temple hangs by its door for the man outside the walls.
  A native overlay view rendered from `ui/components/PillTablet.kt`
  (NEW, this lane), driven — window, cadence, death — by Pastille's
  shield-service controller (Y-1). Temple builds the face; the face
  holds no state of its own.
- **Material, day and night alike:** always the **deep night
  bronze** — a bronze plaque does not change with the room it hangs
  in, and it must read over *any* app beneath it. Body: night raised
  bronze `#392A16`; edge: a **1 dp brass hairline** on all four
  sides; corners **chiselled at 6 dp** (Temple geometry; no soft
  radius anywhere). Numerals and words in **parchment `#E7D5AB`**
  (Anonymous Pro throughout — the tablet speaks in glyphs, never in
  Sora). Filed contrast: parchment on the raised bronze ≈ **9.6:1**,
  computed from tokens, **an estimate to be re-measured on the
  render** (Una's audit left it uncharged precisely because it was
  filed as a claim; Salz's desk settles it at renders). No gold on
  the tablet — gold commits, and the tablet commits nothing. No
  vermilion on any face.
- **Exact size:** height **56 dp**; width **wrap, minimum 132 dp,
  maximum 220 dp** (the PEEK face's app name truncates with an
  ellipsis past the maximum — the tablet never grows a second line
  to fit a name). Internal padding: **14 dp** horizontal, **8 dp**
  vertical. Primary figure: **20 sp** Anonymous Pro. Secondary
  line: **10 sp** Anonymous Pro, glyph spacing. App name (PEEK
  face): **10 sp**, capitals, letterspaced. The **✕ cell: a 32 × 32
  dp incised square** at the tablet's right end — 1 dp chisel pair
  (dark edge on the shadow side, light edge on the lit side, the
  §3-of-the-deepening daylight system in miniature) — the **✕
  glyph itself 14 sp**, parchment, centred in the cell. Touch
  target for the ✕ is the full 32 dp cell plus its padding —
  **44 dp** effective; the tablet's body is the other target, and
  the two targets never overlap.
- **Hanging:** edge-snapping is the tablet's nature — it **docks
  against the nearest vertical screen edge, 0 dp off the edge**
  (a plaque on a post, not a bubble in the air), vertically where
  he left it; dragged, it follows the finger and **re-hangs on the
  nearest edge on release**. Position is **not persisted**
  (residency-scoped; Freya §2.1 priced and declined a stored x/y).
  **No dismiss control, no close affordance, no swipe-away:** it
  leaves when its state ends, never by accident. The only mark it
  carries beyond numbers is the peek's ✕.

### 4.2 The derivation binding — the facts the face may print

The face prints **only** what Freya's derivation (§2.1) hands it,
computed fresh each tick from **stored ends** — and this binding
is written here so no builder rebuilds the dead state by reflex:

1. **Stored ends only.** SESSION remaining comes from the Now
   Frame derivation over the session row; BREAK remaining from
   `breakRemainingAfter` over the stored (remaining, lastTick)
   pair; PEEK remaining from the row's stored
   `activePeekUntilElapsedRealtime` minus now. **Never the VM's
   tick count.** The in-tree `FloatingPeekPillState`
   (`MonolithViewModel.kt:110`, ticked at `:833`) counts ticks in
   the ViewModel's process while the lease it mirrors lives in
   Room — it is the tick-count disease this codebase has already
   burned itself on, and **builders must not render it, revive it,
   or read it**. The tablet has no ViewModel.
2. **Priority (one face at a time):** no active session, or
   Monolith in front → **ABSENT**. Else if `isInSafeBreak` →
   **BREAK**. Else if any row's lease end is in the future →
   **PEEK**. Else → **SESSION**. **Break outranks peek:** during
   a break the PEEK-tier doors stand open with no lease at all, so
   a lease countdown would print a fuse on an open door — a false
   urgency. The break's clock is the true one.
3. **One live lease:** at grant, every other row's lease end is
   zeroed in the same write round (Freya §2.1), so the PEEK face's
   "the live lease" is never ambiguous. (Grant-side machinery —
   Pastille's; stated here because the face assumes it.)
4. **Cadence:** 1 s derivation in the shield service; the window
   is touched only when the printed string changes (every second
   during a countdown; the window itself is created once).
5. **Absences (Freya's never-list, §2.4 — binding):** never over
   Monolith's own surfaces, the Held Door included (the app's own
   faces print their own numbers; a tablet over the door could sit
   on its verbs); never on the lock screen (the desk clock is the
   lawful night surface there); never for a routine window alone —
   the tablet reports *sessions*, not schedules; absent while the
   in-call face is foreground (a human voice outranks a fuse; the
   clocks keep running underneath and the tablet resumes after).
6. **Death:** the window is removed when the derivation first
   reads ABSENT for session-end, and dies with the shield service
   (`onDestroy`) — it can never outlive the session that makes its
   numbers true. A frozen tablet printing a dead session's time is
   the warrant-blindness lie in a new hat; it is unbuildable under
   this binding.

### 4.3 Face one — SESSION

Shown while a timed or stopwatch block runs and he is in another
app. Exact prints (two lines, left-aligned):

> **Line 1 (20 sp):** `{n} MIN` — the block's remaining, **ceiling
> to whole minutes** (`41 MIN`). The final minute prints `1 MIN`
> until the session ends — the tablet never prints `0 MIN` over a
> living block.
> **Line 2 (10 sp):** `BLOCK {i} OF {n}` — the standing it belongs
> to.

- **Stopwatch (FLOW) sessions:** Line 1 prints the **elapsed**,
  `+{mm}:{ss}` (the derivation's elapsed face — a stopwatch has no
  remaining to promise); Line 2 prints `FLOW`. No other change.
- **Body tap:** returns him to Monolith (the intercept's RETURN TO
  FOCUS semantics). The tablet is never a launcher for any other
  app, on any face.
- No ✕ on this face. No knock on appearance.

### 4.4 Face two — BREAK

Shown while a safe break runs (and outranks any live lease, §4.2.2).
Exact prints:

> **Line 1 (10 sp):** `BREAK`
> **Line 2 (20 sp):** `{n} MIN LEFT` — the break's remaining from
> the stored pair, **ceiling to whole minutes**; the last minute
> prints `1 MIN LEFT` until the flip — never `0 MIN LEFT` over a
> living repose.

- **Body tap:** returns him to the Deck **at the END BREAK tablet**
  (ST-02) — the repose's door from outside. (If the END BREAK verb
  has not landed, the tap still returns him to the Deck, where the
  repose's printed line is; the tablet never pretends the verb
  exists.)
- No ✕ on this face. The knocks at this face's boundaries are
  ST-03's flip knocks (§4.6) — one knock, owned by the flip, not
  doubled by the tablet.

### 4.5 Face three — PEEK (in seconds, with the incised ✕)

Shown while a lease is live and he is anywhere but Monolith — in
practice over the peeked app, which is the point: the fuse burns on
the glass of the app he is spending it in. Exact prints:

> **Line 1 (10 sp, capitals):** the peeked app's **name** (the row's
> own label, e.g. `WHATSAPP`) — the face always names its app, so
> the countdown follows him truthfully if he steps to another
> admitted surface.
> **Line 2 (20 sp):** the remaining **in seconds, as `M:SS`**
> (`1:47`, `0:09`) — derived from the stored lease end (§4.2.1),
> ticking every second. Seconds, not minutes: a two-minute fuse
> read in whole minutes is a fuse that reads `1 MIN` for half its
> life.
> **Right end:** the **incised ✕ cell** (§4.1). Its tap calls the
> existing early-end verb (`recordPeekEnded` — zeroes the lease,
> re-anchors the 20-minute cooldown to *now*) — the verb's first
> reachable surface anywhere in the app. On the ✕: the tablet
> falls back to the SESSION face (or ABSENT, if the session has
  also ended); the cooldown it starts is printed **on the Held
  Door**, not on the tablet — the tablet does not linger to explain
  a door he is no longer outside.

- **The 30-second knock:** at **30 seconds remaining** (the first
  tick at which the derived remaining ≤ 30 s), the tablet sounds
  **one low knock** — the PRESENTATION §9 voice family's seat knock
  at its floor, once, never repeated at 20 s or 10 s. This is the only
  pre-end warning on any tablet face (DEEPEN §9.2): at zero, the
  **intercept itself** is the announcement. BREAK and SESSION get
  no 30-second knock — their ends are flips and finishes he is
  returned by, not doors shutting under his thumbs mid-sentence.
- At natural expiry (no ✕): no knock at zero, no dying knock — the
  session has not ended; the tablet steps down to the SESSION face
  in the same tick the lease end passes.

### 4.6 The knocks, collected — and the tablet at 03:30

One table, so no builder composes two sections into a double knock:

| Moment | Sound | Owner of the call site |
|---|---|---|
| Peek reaches 30 s left | One low knock, once | Tablet (service ticker, Pastille's driver; face spec here) |
| Break flip in / flip out | One low knock each (ST-03) | VM ticker — this lane |
| Session ends while tablet up (any face) | **Dying knock:** one low knock on the first ABSENT-for-end read, then the window is removed in the same tick | Service derivation — Pastille's driver; face spec here |
| Peek expires naturally | None — intercept is the announcement; face steps down | — |
| Break flips while tablet on BREAK face | The ST-03 flip knock only — the face change adds none | VM ticker |
| Routine boundary − 60 s (ST-06) | One low knock — **except the FASTED arm: none (Pilger fence, ST-06)** | Heartbeat — Wache / Ren |
| **03:30 SLEEP stand-down** | **None. Ever. (REFUSED-BY-NAME, ST-05)** | — |

**At the 03:30 boundary, exactly:** SLEEP's session ends by plain
deactivation in the heartbeat. The derivation's next tick reads no
active session → ABSENT. If the tablet is up (he is in another app
at 03:30), it is **removed silently — no dying knock, no knock of
any kind**; the dying-knock rule has exactly this one carve-out,
and builders must implement the carve-out as a named condition
(the ending session is the SLEEP routine's stand-down), not as a
time-of-day check — a hand-armed session ending at 03:30 under its
own timer still gets its dying knock (ST-05's scope). If he is in
Monolith, the tablet is already absent (own-package rule) and
nothing changes on the tablet's account. The visible signal is
the desk clock's: its face changes from the span to the hour
(ST-01). If a break or a lease is somehow live across the boundary,
it does not save the tablet: session-end is read first, the tablet
dies with the session, silently, under this same carve-out.


## 5. Verification the builders inherit (Freya §4.4–4.5, Temple's share)

- **Derivation table test** (unit, Pastille's harness): no session
  → ABSENT; own package → ABSENT; break + lease both live → BREAK;
  stopwatch → elapsed face; lease on row B, foreground B → PEEK with
  B's name and B's remaining; after any grant, at most one row holds
  a future lease end. Temple's face adds three print assertions to
  that table: SESSION never prints `0 MIN`; BREAK never prints
  `0 MIN LEFT`; PEEK always prints `M:SS` from the stored end.
- **Mid-flight immovability** (settings): grant at 2, edit to 30
  mid-lease — the stored end does not move; create at break 10, edit
  to 60 mid-break — pair, stamp, span do not move; next session
  stamps 60. (Pastille's tests; the inscriptions' wiring is judged
  by them — §2, Y-2/Y-3.)
- **Phone bench** (named now, rides the gated build's bench list):
  the tablet actually drawing over a third-party app; the PEEK face
  live over the peeked app in a real lease; drag + edge-snap under a
  real thumb; the ✕ reachable and obeyed; the 30-second knock heard
  once; the dying knock heard at a hand-armed end and **not** heard
  at a rehearsed stand-down ending; the window dying with the
  service; the call-face absence during a real call. Robolectric
  has no other apps — none of this is harness-provable and nothing
  here pretends otherwise. The ≈9.6:1 tablet contrast is re-measured
  from the tablet's first render in the same pass.

## 6. SILENTLY-MISSED FLAGS — what Temple's current plan would have lost

*This section is the seat's reason to exist. Each flag names an item
Temple's filed plan (PRESENTATION + DEEPEN) would have missed,
mis-stated, or left unbuildable **without anyone noticing** — and
what this file does about it. Flags are not charges against Temple;
they are the register's insurance, filed while they are still cheap.*

- **F-1 · ST-08, harm (a): the grant-time launch was claimed but
  never designed.** DEEPEN §8's "answers ST-08 in the same stroke"
  covers the countdown and the way back to Monolith — not the
  register's actual sentence, the launch *into the peeked app* at
  grant while the seconds burn. **Disposition:** answered at ST-08 —
  named as Pastille/Ren machinery (one launch intent on the grant
  path), with the atomic-moment condition and the composable warning.
  Until it lands, ST-08 stays half-open and must be reported as such.
- **F-2 · No-grant state has no Temple face (Route B).** Temple
  designs only the Route A tablet. If he never grants overlays, or
  revokes mid-session, every small thing in this file that lives on
  the tablet — peek countdown, break face, dying knock — vanishes
  with no Temple-designed fallback. **Disposition:** named at §2
  (Y-1): Freya's Route B notification upgrades are the floor, in
  stock wording; Stein/Ren to rule whether Temple ever grammars the
  shade. Recorded so the gated build's bench checks the no-grant
  state on purpose instead of discovering it.
- **F-3 · ST-01's idle-clock cure was flagged `[MERGE]` — the flag
  itself was the risk.** In an all-looks-in-app build there is no
  single "winner's merge" left to carry a `[MERGE]` errand; the
  idle desk clock printing the dial setting (`50:00`) would have
  survived every lane while each assumed another owned it.
  **Disposition:** ST-01 re-homes it as lane work in
  `AirlockAndDisarmOverlays.kt`. Any other `[MERGE]`-flagged small
  thing in Temple's filings should be swept for the same disease.
- **F-4 · The mid-flight law was nowhere in Temple's filings.**
  DEEPEN §9.1 designs the two inscriptions and never states Freya
  §3.4 (edits price the future only). A builder wiring an
  inscription straight to the live constants the tree uses today —
  the four agreeing `600`s, the compile-time `120` — would produce
  an edit that re-prices a running break or a live lease, and the
  inscriptions would *look* finished while lying. **Disposition:**
  the law is bound at §2 (Y-2/Y-3) and tested at §5. The constants
  at `MonolithRepository.kt:93–97` survive only as the defaults'
  ancestors; no Temple surface may hardcode the peek's `2` or the
  cooldown's `20` after the settings land.
- **F-5 · ST-04's receipt row needs a fact the writer might not
  store.** The penalty-path write moves a target; it does not file
  "extended by his hand, by *n*" anywhere the slip can read. If the
  add-time writer lands in that shape, `EXTENDED +{n} MIN` is
  unprintable and the honesty condition dies quietly at the receipt
  — the one surface Sarthak has loved on sight. **Disposition:**
  the storage condition is attached to the machinery request at
  ST-04, for Ren's ruling, before the writer is built.
- **F-6 · DEEPEN §8 gave the tablet no size.** No height, no type
  size, no ✕ dimension, no width bound — every builder would have
  invented his own plaque, and two builders (Maurer, Setzer) would
  have invented two. **Disposition:** fixed at §4.1 (56 dp height;
  132–220 dp width; 20 sp figure; 10 sp secondary; 32 dp ✕ cell,
  44 dp effective target; 6 dp chisel; 1 dp brass edge). If Stein
  amends a size, he amends §4.1 — the numbers live in one place.
- **F-7 · The Held Door's cooldown print exists only as prose
  until its render debt is paid.** DEEPEN §6 designs the barred
  side door (`THE LAST PEEK IS STILL COOLING` / `PEEK RETURNS IN
  {n} MIN`) and §13 owes the door in *both* side-door states. Klar's
  K-3 stands until then: only Signal (and Blueprint) print a
  cooldown today. Not missed on paper — flagged here so the door's
  cooldown render is on the **builders'** checklist, not only the
  designer's debt list: Door + Clock, both states, or K-3 stays open
  against Temple at judging. (Related, for the same checklist: the
  door's side-door offer must print the *set* length `PEEK {n} MIN`
  from the Y-2 setting — a hardcoded `PEEK 2 MIN` surviving into the
  lane build would silently un-answer Y-2 at the app's most-seen
  surface.)

## 7. Builders' checklist — the whole file in one pass

1. Deck: lintel hour · frieze `NOW HH:mm` · idle `NEXT — {name}
   {HH:mm}` · `BREAK {n} MIN` inscription (prints from setting;
   inert until Y-3 storage lands — build the tablet, wire the read)
   · repose line on the base course · `END BREAK` tablet **behind
   the verb's existence** · `+ MIN` tablet **behind the writer's
   existence**.
2. Shield: `PEEK — {n} MIN` course-head inscription + typed cutting
   (setting-row write only) · gate chips print `PEEK {n} MIN` from
   the setting · frieze entablature with `NOW HH:mm`.
3. Door + Clock: side door in three states (offer `PEEK {n} MIN` /
   barred + reason + `PEEK RETURNS IN {n} MIN` / not drawn) · frieze
   miniature `NOW HH:mm` · desk-clock stand hour · **idle clock
   prints the device's time** · clock running face unchanged.
4. Receipt: `EXTENDED +{n} MIN` row, primary ink, printed exactly
   when the session row carries an extension — and a standing
   report to Stein if it carries none (F-5).
5. Tablet (NEW file): §4 whole — material, sizes, three faces,
   derivation binding (stored ends; never `FloatingPeekPillState`),
   priority (break > peek), absences, knocks table, 03:30 carve-out
   as a named condition.
6. VM: ST-03's two flip knocks, call-site parameters only; no new
   engine shapes; no bell.
7. **Not in this lane, do not touch:** `RoutineHeartbeat.kt` call
   sites (ST-06 — Wache/Ren) · GuardSettings columns + migration
   11→12 + `breakSeconds` stamp (Y-2/Y-3 — Pastille/Ren) · the END
   BREAK verb (ST-02 — Ren) · the add-time writer (ST-04 — Ren) ·
   the overlay window + driver (Y-1 — Pastille) · the grant-time
   launch (ST-08a — Pastille/Ren).

*— Kleinod, Small Things seat, Temple crew (Stein), Oct 29, 2026.
Sources on the record in the header; every verdict above names its
surface, its file, or its owner. Nothing on the register dies by
silence in this world.*

