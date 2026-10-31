# Issue #7 — Shield (audit fixes) — Feel Spec: TAST (TOUCH)

**Seat:** TAST, FEEL SPECIALIST 3 (TOUCH), in VOGT's cell for issue #7,
"Main-app pass — Shield audit fixes."
**Domain, only this:** touch and haptics — what the finger does, what the
hand is told back, and what the hand must never be told. Words are FORM's,
integrated choreography is HAUT's, motion is SCHWUNG's, surface finish is
GLANZ/Panzer's. Where their filings already rule inside my domain I adopt
them and say so; I do not re-choreograph their moments.
**Status:** DECLARED — specification only. No app code written or edited,
nothing compiled, no tests run. MONTEUR is sole code editor; §6 is his
haptic patch list. GitHub coordination is VOGT's. Nobody messages Sarthak.

**Sources read:** `studio/plan/AUDIT-shield.md` in full (R-1…R-13; the
R-10 sled acquittals are binding and are treated as settled law, not
redesign material); the working tree on `agent/issue-7`:
`ui/screens/ShieldMatrixScreen.kt`, `ui/components/ShieldCapSelector.kt`,
`ui/ShieldBlockingStatus.kt`, `ui/views/BlockitSuitcaseAndSwitchesViews.kt`
(`AppRowSledToggleView`, `SledSwitchView`), `ui/views/BlockitCoreViews.kt`
(`fireFeel` / `fireWeightedKnock`), `service/TactileSoundEngine.kt` (the
whole voice inventory), `ui/MonolithViewModel.kt` (every Shield-path sound
call, the ratchet warning, the routine verbs); sibling filings in this
cell: `designer-form.md`, `innovator-funke.md`, `feel-haut.md` (the
SETTLE / RELEASE / SLIP choreography), `feel-schwung.md` (motion table
M-1…M-21), `finisher-glanz.md` (§3, the tally as built jumps).

---

## 0. The touch law of this screen, in six sentences

1. **Haptics answer the finger, not the animator.** A voice fires because
   his hand did something, at the moment the thing he did lands. Machine-
   caused change — a routine firing, a proof resolving, a re-seat, a cap
   re-deriving — is silent, however important it is. Print is its channel.
2. **One event, one voice, one owner.** Every committed act on Shield
   speaks exactly once, from exactly one layer. The shipped code already
   knows this law (the `fireFeel` contract comment: the old double-buzz
   "felt like mush"; CRITICS A-5: view owns travel, VM owns value
   changes). §6 lists the two places the Shield path still breaks it.
3. **The voice grades the act.** Lightest to heaviest, the hand learns the
   scale once and reads it forever: *detent tick* (a value stepped) <
   *latch* (a switch engaged or released) < *firm detent* (a heavy
   commit) < *seal voices* (a session sealing — not Shield's to spend).
   Nothing on Shield may borrow a heavier voice than its act earns.
4. **A refusal is print, only print, and print first.** R-7 puts the word
   at the control touched. The hand gets *nothing* — no commit voice, no
   error buzz, no sympathy tick — because any vibration at a refusal
   either counterfeits a commit or reads as an alarm, and both are lies.
   The VM already states this law for the banner
   (`triggerRatchetWarning`: "The warning is a print; it sounds
   nothing"). This spec extends the same law from the banner to the hand.
5. **A no-op is silent.** A tap that changes nothing — same sled stop,
   same cap stop, a clamped time step — must not sound like a change.
6. **No fact is carried by vibration alone.** Every feel below is paired
   with print or position (the cap colour follows the rocker, the word
   prints under the cap, the tally re-strikes). A user with haptics off
   loses texture, never information — the engine's own S0 law, held.

**The voice inventory (all existing, `TactileSoundEngine` — nothing new
is invented in this spec):**

| Voice | Pattern (timings @ amplitudes) | House meaning |
|---|---|---|
| Rotary detent — `playRotaryDetent` | [0, 7, 5] @ [0, 180, 70], pitched | a value stepped; travel under the finger |
| Mechanical latch — `playMechanicalLatch(true/false)` | engage [0, 11, 26, 9] @ [0, 215, 0, 150]; release [0, 9, 22, 10] @ [0, 165, 0, 195] | a switch engaged / released — direction is in the shape |
| Firm click — `playFirmClick` | [0, 13, 20, 8] @ [0, 225, 0, 135] | one firm detent: a heavy control committed |
| Seat knock — `playSeatKnock` | parameterised strike + tail | a weighted physical seat (wheel, gates) — **not spent on Shield** |
| Harmonic seal — `playHarmonicSeal` | [0, 24, 38, 16] @ [0, 255, 110, 175] | arming/disarming a session — **struck from Shield, §3.2** |
| The Throw — `playSealThrow` | [0, 24, 38, 16] @ [0, 255, 0, 175], bit-identical, hash-pinned | the seal itself, fired once by `SessionSealTransition` — **never Shield's** |
| Completion pulse — `playCompletionPulse` | three softening ticks | the finish's reserved voice (E4 census) — **never Shield's** |

Legacy path note: with no engine attached (previews), `fireFeel` falls
back to plain 1/4/8/10 ms one-shots. The grading in the map below is the
engine grading; the fallback preserves the *order* (tick < latch < firm),
which is all a fallback owes.

---

## 1. THE MAP — surface → feel → reason

### 1.1 The sled (the catalogue row's 3-stop rocker) — R-10 acquitted, held

The throw is right and this section does not redesign it: 1:1 drag,
800 dp/s flick to the far stop, Curve A settle (260 ms one stop /
340 ms two), cancel re-seats, cap colour follows the rocker not the
commit. What follows is its feel, voice by voice, as shipped — plus the
two leaks §6 closes.

| Surface / moment | Feel (as shipped) | Reason |
|---|---|---|
| Finger takes the rocker (DOWN on the track) | **Nothing** | A grip is not an event. Buzzing on grab would tax every scroll that starts on a row. |
| Drag begins (past the 6 dp threshold) | **Nothing new** — the rocker is already following 1:1 | 1:1 *is* the feedback. The hand is holding the object; it does not need to be told it is holding it. |
| Drag crosses the middle stop (PEEK), either direction | **Rotary detent, 1 ms class @ 440 Hz** — once per crossing (`noteTravel`) | The middle is a place you pass *through*; a tick says "passed", not "arrived". One crossing rule for drag, tap-throw and settle, so the sled never grows two dialects (the code's own M1 doctrine — acquitted). |
| Drag hits either end of travel and is held against it | **Nothing** | The drawn hard stop is the message. No clamp bump is invented: a buzz at the rail would claim a fourth stop exists. |
| Release, slow → nearest stop seats | **Landing voice, by destination** (below) | The seat is the commit; the settle animation is machine motion and stays silent until it lands. |
| Flick (> 800 dp/s) → far stop | Crossing tick in flight (if it crosses PEEK), then **landing voice** at the far stop | The flick-to-far physics stands (Panzer's acquittal). The hand threw; the hand hears it land where it was thrown. |
| **Landing on SHUT** | **Latch, engaging** — `fireFeel(4L)` → `playMechanicalLatch(true)` | Tightening ends in an engagement: strike, gap, seat. The heaviest sled voice for the strictest stop — the grade matches the act. |
| **Landing on PEEK** | **Rotary detent @ 520 Hz** — `fireFeel(1L, 520f)` | Deliberately the weakest landing. PEEK is a notch you settle *into*, not a destination with its own voice (M1's doctrine). If PEEK landed like SHUT, the hand would stop distinguishing a 2-minute lease from a wall. |
| **Landing on OPEN** | **Latch, releasing** — `playMechanicalLatch(false)` (fallback `fireFeel(8L)` firm click when no engine) | Loosening is the same mechanism run backwards, and the release shape seats *heavier* (195 tail) — opening a door should feel like the latch coming off, not like nothing happened. |
| Tap directly on a stop (no drag) | Identical path: throw, crossing tick if PEEK is crossed, landing voice | One dialect for tap and thumb (acquitted). A tap is a short throw, not a different control. |
| Tap on the stop the rocker already occupies | **Nothing** — `commitTo` sees `targetTier == currentTier` and fires neither feel nor callback | Law 5. A restamp that sounds like a commit trains the hand to distrust every real commit. |
| Tap on the app's *name* | **Nothing, and nothing happens** | Name-is-not-a-door stands (R-10, Dens A5 + Muede over Fremd C11). A dead surface must be feel-dead too: any response — even a tick — would teach the finger the name is a weak door. |
| Gesture stolen by the list (ACTION_CANCEL) → rocker re-seats to the committed tier | **Nothing** (one leak excepted, §6 item T-3) | A cancelled gesture is an event that did not occur. The re-seat is machine motion (Law 1). |
| An external re-tier lands mid-drag (a loadout commits under his finger) | **Nothing**; the rocker seats directly on the new tier, no animation | S-3's machinery question (the next MOVE must not silently erase it) is MONTEUR's; the feel side is fixed: an outside change arriving under the finger never buzzes. If his own release then commits, that landing voice is the only one. |
| Provisional cap (V-2): rocker draws hollow between detents until the seat | **Nothing at the hollow state** | The hollow cap is a *prediction* — where he will land if he lets go. A haptic there would claim a landing that has not happened. Feel begins at the seat, never at the forecast. |
| **Ratchet refusal: he throws toward a weakening during a live session and the VM refuses** | **NOTHING. No landing voice, no error buzz, no re-seat voice.** The rocker tries and stays; the refusal prints at the row, in place (R-7) | The central never-buzz of this screen — §4, item N-1, including the shipped leak that must close (§6 item T-1): today the view fires the landing voice *before* the VM refuses, so the hand hears a commit the machine then un-happens. |
| TalkBack custom action sets a tier (R-11 semantics) | The same landing voice as a tap on that stop; refusal → silence, as above | Feel marks the *state change*, not the input path. A blind user's commit is the same commit; a blind user's refusal is the same print-only refusal (spoken by TalkBack, never buzzed). |

**Touch mechanics, sled (observed, no redesign — R-10 acquits the row
entire):** the rocker track is 168 × 34 dp inside a 54 dp row; stops are
~54 dp wide as *drawn* but the drag divides travel by 56 dp/stop — the
S-1 mismatch (~4 dp of lag at the far stop) is a **feel defect**, not a
cosmetic one: at the far stop the thumb arrives before the cap does, and
the hand reads that as softness in the mechanism. S-1's one-constant fix
rides with my full support, for touch reasons. Taps that land on the row
but miss the 34 dp rocker vertically hit a dead row: silent, correctly —
but it is why the landing voices matter; they are the only confirmation
a hit ever gives.

### 1.2 The loadout cap (STUDY / WORK / SLEEP) — one double voice to kill, one act to split

As shipped, a cap tap fires `playFirmClick()` in the selector **and**
`playHarmonicSeal(true)` in the VM's `applySmartLoadout` — two voices,
and the heavier one is a *seal* voice spent on an act that seals nothing
(§6 item T-2). R-4 then splits the act in two (Ghost Preview, then
commit). The feel splits with it — this is the map's one firm ruling:

| Surface / moment | Feel | Reason |
|---|---|---|
| First tap on a non-resting stop → **Ghost Preview** (hollow ghost caps, ghost tally, consequence line incl. STUDY's sniper/blocker side-effects in print) | **Rotary detent, one tick** — and the cap's 260 ms Curve A travel stays silent | A preview commits *nothing*; its feel must be the lightest voice that says "noted". Not the firm click (that is the commit's voice — spending it here would make the second tap feel like a repetition), and never the seal. The travel is machine animation (Law 1): the ghost print is the feedback. |
| Second tap on the previewed stop → **commit** (every app's tier rewritten by category) | **ONE firm detent** — `playFirmClick`: 13 ms @ 225, 20 ms breath, 8 ms @ 135. Fired once, by one owner (§6 T-2) | This is the brief's pattern question, answered for the cap: **one firm detent, not a knock sequence.** The act is heavy — the whole book rewrites — but it is a *settings rewrite*: reversible, unsealed, nobody is locked in by it. The firm click is the engine's documented voice for "heavy Canvas controls". A knock sequence (latch / seat-knock / seal shapes) is reserved for physical seats and the seal itself; spending one here would counterfeit a sealing and teach the hand to under-read the real Throw. |
| Tap on the stop the cap already rests on | **Nothing** — no preview, no re-apply, no voice | Law 5. Today this tap re-applies the loadout *and* replays the seal voice: a no-op wearing a ceremony. The builder strikes both the sound and the redundant apply for this case. |
| Cap travel crosses the middle stop (STUDY → SLEEP) | **Nothing** | No crossing tick is invented for the cap. The sled's crossing tick stands under R-10's acquittal because the sled is *finger-dragged*; the cap's travel is machine-animated after a tap, and Law 1 keeps machine travel silent. Different mechanism, different feel — deliberately. |
| Cap re-derives because a routine fired, or its source print changes ("BY SLEEP · UNTIL 03:30", R-4b) | **Nothing** — the cap moves and re-prints silently, whenever it happens, screen on or off | A cap that buzzes at 22:30 because SLEEP fired is an alarm clock he never set. The source line is the receipt; he reads it when he next looks. |
| Preview abandoned (he taps elsewhere / scrolls on / picks a different stop) | **Nothing** | Abandoning a preview is not an act. The ghost print dissolving (SCHWUNG's motion) is the whole event. |

### 1.3 Routines — toggles, loadout cycle, day chips, time

The routine verbs are VM-owned, one voice each, fired from the VM —
the cleanest ownership on the screen (the chips themselves,
`BlockitActionChip`, are silent furniture). Acquitted in structure;
two truth fixes ride (§6 T-4, T-5).

| Surface / moment | Feel (as shipped) | Reason / ruling |
|---|---|---|
| Row tap → editor expands / collapses | **Nothing** | Opening furniture is not a commit. The expand/collapse motion is SCHWUNG's (280 A / 160 Throw); the hand gets nothing, correctly — he will be tapping *inside* the editor next, and those taps speak for themselves. |
| ON/OFF cap → routine enabled | **Latch, engaging** — `playMechanicalLatch(true)` | Arming a schedule is an engagement. Acquitted. |
| ON/OFF cap → routine disabled | **Latch, releasing** — `playMechanicalLatch(false)` | Direction is in the shape (release seats heavier). Acquitted. |
| ON/OFF cap → enabled, but the resulting state is **WON'T FIRE** (daysMask = 0) | **Rotary detent instead of the latch** — §6 T-4 (builder change) | R-6 cured the *print* lie (the cap used to say ON for a routine that can never fire); the latch-engage voice is the same lie in the hand — it claims "armed" for a schedule the chain will skip. Feel must follow `RoutinePrintState`, not the raw `enabled` bit: latch only when the result is genuinely ON (live); detent — the neutral value-step voice — when the result is WON'T FIRE; latch-release when the result is OFF, always. |
| Loadout chip cycles STUDY → WORK → SLEEP (`cycleRoutineLoadout`) | **Firm click** — `playFirmClick`, one per tap | A cycle is neither a latch (nothing engages) nor a detent step (the value is a word, not a quantity). One firm click per cycle-tap is the right weight. Acquitted. Note the contrast the hand learns: the *routine's* loadout cycle is a firm click; the *screen's* loadout commit (§1.2) is also a firm detent — same act class, same voice, two homes. Correct. |
| Day chip adds a day | **Rotary detent @ 680 Hz** — `playRotaryDetent()` | A small value stepped on. Acquitted in family; pitch split proposed below. |
| Day chip removes a day | **Rotary detent @ 680 Hz** (today, identical) | **Proposed, small (§6 T-5): remove-day detent pitched down (~520 Hz), add-day stays 680 Hz.** Opposite acts should not feel identical when the engine already pitches detents for free (the Blueprint ladder is the house precedent: pitch may carry direction). Not a new pattern — one parameter at one call site, ×2. |
| Day chip removes the **last** day → cap flips to WON'T FIRE | The remove-day detent, and **nothing extra** | FORM §3.3 already rules the cap *is* the notice — no confirmation, no extra copy. The same economy holds in the hand: no warning buzz. A special vibration here would be an alarm about a reversible, self-inflicted, fully printed state. |
| ±30 chip steps a time (until the wheels land) | **Rotary detent per tap** — `playRotaryDetent()` | Each tap *is* one 30-minute step; a detent per step is the honest 1:1 feel for a stepped control. Acquitted for the interim — and it is exactly the feel the wheels generalise (§5). |
| ±30 chip tap at the span clamp (window would leave 30 min–18 h) | **Nothing — and print must appear** (§6 T-6) | Today `adjustRoutineTime` returns silently before the sound call: no voice (correct — a detent would claim a step that never happened) but also no print (the defect — a dead tap, the worst feel on the screen: the finger did something and the world did not even refuse). R-8's W-1 already rules the cure in print: the clamp names what it does. Feel's half of the ruling: the refusal stays **silent** (Law 4). Never fix a dead tap by giving it a buzz. |
| Routine toggle ever refused by a future ratchet gate | **Nothing; print at the cap** | Conditional ruling, stated now so the gate is built right: in `toggleRoutine` the latch fires *before* persist. Any refusal check must be evaluated **before** the latch call, or the hand will hear an engagement the machine refused — the sled leak (§6 T-1) in a second home. |

### 1.4 The tally and the group jumps (R-1, as built by GLANZ)

In the tree I read, `StandingTallyLine` is still print-only; GLANZ §3
builds the words as jumps (segments with their own touch targets,
counted-index scroll). The feel ruling for the built form:

| Surface / moment | Feel | Reason |
|---|---|---|
| Tap SHUT / PEEK / OPEN in the tally → scroll to that group | **Nothing** — at the tap, in travel, and at the landing | A jump changes where his eyes are, not the world. SCHWUNG M-5 already rules "no haptic invented here" for the landing; I extend the silence to the tap for one reason: the scroll itself (420 ms EasingB, header sticking at the top) is immediate, visible, and unfakeable feedback — the only tap on this screen whose result is a *place*, and a place proves itself by arriving. Contrast the filter words below: choosing a filter changes what the catalogue *is*, so it takes a detent; a jump changes only where he *stands in it*, so it does not. |
| Tally re-strikes after a flip (250 ms coalesce, whole line, 260 ms Curve A) | **Nothing** | Numbers changing is print. The flip that caused them already spoke at the sled; a second voice at the tally would double-speak one act (Law 2) — and a burst of flips would machine-gun the tally. |
| Group headers (SHUT n / PEEK n / OPEN n) | Not tappable; **nothing** | Headers are signposts, not doors — the same doctrine as the sled's name (R-10). The tally above them is the door, and it says so in print (FORM: the FilterWord bar treatment). |
| Jump target segment's touch target | n/a — target size is the touch ruling | GLANZ gives each segment its own target; the target must be **padded to at least the chip height (42 dp)**, not the 11.5 sp text height. A jump a tired thumb can't hit at 1 AM is a printed lie about a door. This is priced into the builder's segment work, not extra. |

### 1.5 The rest of the screen — the quiet surfaces, acquitted

| Surface / moment | Feel (as shipped) | Reason |
|---|---|---|
| Status sentence changes state (any direction, any proof) | **Nothing — ever, from Shield** | §2 in full. HAUT's choreography owns this moment and rules it silent on Shield's side; I adopt and extend it. |
| RUNNING NOW marker appears/disappears on a routine row | **Nothing** | A derived fact arriving on a 1-second derivation beat. Buzzing it would mean the phone vibrates because a clock crossed a boundary — the definition of an alarm he never set. |
| Category filter word selected/deselected | **Rotary detent @ 580 Hz**, VM-owned, once | A value choice (the catalogue's contents change), one voice, one owner. Acquitted. This is the detent the tally jump deliberately does *not* take (§1.4) — selection speaks, travel doesn't. |
| Search typing; results changing under the query | **Nothing** | Keystrokes never buzz in this house. The re-print is the feedback. |
| Website ADD succeeds | **Latch, engaging**, fired *after* the repository confirms the add | The model citizen of this screen: voice on proof, not on tap. Acquitted and cited as the pattern §6 T-1's fix should imitate. |
| Website ADD fails (invalid / rejected) | **Nothing** — and per R-3 the field keeps the text and prints why | Law 4. Today the field clears silently, which is a *print* defect (R-3 charges it); the silence is correct and stays. |
| Website REMOVE | **Latch, releasing** (fired at tap, before the repository call completes) | Single voice, right direction. Minor asymmetry with ADD (which sounds on proof) — recorded, not charged: removal is his own already-listed fact being struck, the tap is unambiguous, and every other toggle on the screen voices at tap. Leave it. |
| Reset note / prune note appears or is dismissed | **Nothing** | One-time print strips are read, not felt. Dismissal is a tap on print; the print leaving is the feedback. |
| Ratchet banner (Deck-origin refusals) arrives on Shield | **Nothing** | The banner is a print (VM's own comment, Law 4). It is also not *his* Shield gesture — it reports a refusal from another room. Silent twice over. |

---

## 2. The go-live moment — the signature question, ruled

The brief puts the question directly: when blocking actually goes live,
does the moment deserve a haptic signature, and if so, which pattern —
**one firm detent** or **a knock sequence**?

**Ruling: the knock sequence is rejected outright, and no new signature
is fired from Shield at all. The moment already has its voice, and it
is not Shield's to add to.**

The cases, separately, because they are different events:

1. **A hand-armed session seals (he arms on the Deck; t = 0).** The
   signature exists: **the Throw** — haptic twin [0, 24, 38, 16] @
   [0, 255, 0, 175], a true knock sequence (strike, *gap*, seat),
   bit-identical at every seal, hash-pinned, fired once as the seal
   act's last statement, on whatever screen he stands. HAUT's SETTLE
   choreography already synchronises Shield around it (sentence
   strikes, palette completes, Throw decays — all rest by 1,000 ms)
   and rules: *"Shield adds nothing: no latch, no click, no second
   haptic."* I adopt that ruling in full. A knock sequence is therefore
   **already spent** — by the seal, on purpose, exactly once. Any
   second knock from Shield would be a counterfeit of the one sound in
   the app that must never be counterfeited.
2. **Shield is open and the proof flips to LIVE without a seal** —
   a re-proof: witnesses catching up, a service rebinding, a stale
   beat recovering. **No haptic.** This is HAUT's SILENT RE-PROOF, and
   the touch reason is the sharpest one in this file: a detent fired
   here would timestamp *the proof's arrival* as if it were
   *blocking's start*. Those are different instants, and the hand
   cannot be told a lie the sentence itself was built (R-5, FUNKE's
   conjunction) never to print. The sentence re-strikes; the room
   settles; the hand hears nothing, because nothing was *done*.
3. **A routine fires while Shield is not in front of him** (phone
   pocketed, screen off, 22:30 SLEEP). **No haptic — not the detent,
   not a knock, nothing invented here.** An unattended vibration for a
   scheduled event is an alarm, and this app's feel law is events,
   never alarms. Whether routine materialisation speaks through the
   seal machinery's own existing law is the seal lane's question, not
   a Shield surface event; this spec invents no voice for it. His
   receipt is print, read on his next visit: BLOCKING NOW + the
   routine's RUNNING NOW.
4. **Proof degrades or a session lawfully ends while he watches**
   (LIVE → STALE / UNPROVEN / NO_SESSION). **No haptic.** HAUT's SLIP
   and RELEASE: the sentence dissents or exhales, every other channel
   holds. A buzz on degradation would be the app crying wolf in his
   hand at the exact moment its print is calmly naming the failed
   link.

**If Ren overrules and demands a Shield-side go-live signature
anyway**, the only admissible pattern is **one firm detent**
(`playFirmClick`, §0) — fired once per session id, only on a
NO_SESSION → LIVE transition while Shield is composed, never on a
re-proof. The reasoning, for the record: the firm detent is the
house's sober commit voice — 41 ms of pattern, no 255 strike, no
triple tick — heavy enough to register, plainly distinguishable from
the Throw (78 ms, full strike, gapped seat) and from the completion
pulse. A knock sequence is inadmissible under any overrule: every
knock shape in the inventory already *means* something physical (a
wheel seating, a latch engaging, the seal throwing), and a status
derivation is none of those things. But TAST's recommendation stands
against spending even the detent: the seal's Throw is this app's
go-live signature, it already fires wherever he stands, and a second,
weaker signature for the same fact would dilute the first — the exact
failure the tally's de-duplication (R-1) was ordered to cure, in the
hand instead of on the page.

---

## 3. Reserved voices — what Shield must never spend

- **The Throw** (`playSealThrow`): one call site, the seal act only.
  Never a status flip, never a loadout, never a "blocking started"
  courtesy copy.
- **Harmonic seal** (`playHarmonicSeal`): arming/disarming a session,
  from the doors that do it. Struck from `applySmartLoadout` (§6 T-2).
- **Completion pulse** (`playCompletionPulse`): the finish's reserved
  voice (E4 census — the ratchet warning's spend of it already died in
  the VM for exactly this reason). Not for "loadout applied", not for
  "routine saved", not for anything on this screen.
- **Seat knock** (`playSeatKnock`): weighted physical seats. The Deck's
  navigation-refusal tick (6 ms @ 0.2, Band's "tries and stays") is
  *navigation's* grammar in its own room; it is **not imported** to
  Shield refusals (§4 N-1) — Shield's refusals have a fuller channel
  (the printed word at the control) than navigation ever gets, and
  print is the senior channel.

---

## 4. THE NEVER-BUZZ LIST — consolidated, binding on the builder

- **N-1 · Any refusal, anywhere on Shield.** Ratchet-weakening at the
  sled; a clamped time step; a failed website ADD; any future routine
  gate. Print at the point of refusal *first* (R-7) — and on Shield,
  print *only*: no commit voice, no error pattern, no low knock, and
  no voice on the re-seat that follows a refused throw. (Grounding:
  R-7 + the VM's standing comment — "The warning is a print; it sounds
  nothing" — extended from the banner to every control.)
- **N-2 · No-op taps.** Same sled stop, same cap stop, a day chip's
  neighbours he didn't touch — anything whose state after the tap is
  its state before it.
- **N-3 · Derived-state arrivals.** Status-sentence flips (§2),
  RUNNING NOW, source-print changes, tally re-strikes, CAN'T CHECK
  appearing (uncertainty must never vibrate — it would read as the
  alarm the sentence is carefully not printing).
- **N-4 · Machine-animated travel.** Cap slide, sled re-seats (cancel /
  refusal / external), ghost-preview travel, group-jump travel and
  landing, provisional-cap hollow states.
- **N-5 · Other people's acts.** A routine firing, a loadout landing
  from anywhere but his own cap tap, an external re-tier — anything
  whose cause is not his finger on this screen, in this visit.
- **N-6 · Typing and reading.** Search keystrokes, scrolling, row
  expansion, note dismissals, name taps, header taps.

---

## 5. Forward feel spec — the wheels (R-8) and the Day Rail

R-8 merges the routine-edit lane's dual snap wheels *with Vega's W-3:
per-detent feel*. W-3 is my domain; here is the spec Voss's lane
builds once, so the merge doesn't ship silent wheels and retrofit.

**Wheels (this merge):**
- **Per detent crossed under the finger: one rotary detent** —
  `playRotaryDetent`, the sled-crossing voice's family. Finger-driven
  only (Law 1): a wheel settling after release ticks for the detents
  its settle *passes*, because the settle is the finger's throw
  landing — same doctrine as the sled's tap-throw crossing tick. A
  wheel re-seating because an external value landed: silent.
- **Pitch follows the resulting value**, monotone — the house ladder
  precedent (Blueprint's wheel ladder; the VM's own
  `520.0 + startHour × 14.0` for profile windows). The minute wheel
  pitches across its hour, the hour wheel across the day-part. Pitch
  is shading, never data he must decode — the print carries the time.
- **Cyclic wrap (W-2, 59 → 0): the same detent, nothing special.** A
  wrap is a detent, not an event. A special wrap buzz would fire sixty
  times an hour of scrolling and mean nothing by the third.
- **Settle = commit-once-per-settle (R-8) gets NO extra voice.** The
  last detent tick already spoke; a firm click per settle would
  double-voice every wheel stop and punish slow, careful scrolling
  with a drumroll of commits that haven't happened. If the editor
  grows an explicit DONE, DONE takes the firm detent — a button he
  presses to commit is a commit; a wheel coming to rest is a value.
- **Span clamp (W-1): silent, in print.** When dragging one end moves
  the other, the moved end prints its name (W-1's rule); the hand
  feels the detents of *his* wheel only. One gesture, one voice
  stream — the other wheel's detents do not tick for a motion he
  didn't make.

**Day Rail (V-1, named successor — spec filed now, built later):**
edge drags tick at the 15-minute lattice crossings; body drags
(duration preserved by construction) tick at hour crossings only —
ticking every lattice point on a body shift would chatter for a
gesture whose whole point is that nothing internal changes; release
with a changed value: one rotary detent; release back at the origin
value: nothing (Law 5).

---

## 6. Builder patch list (MONTEUR) — priced, feel only

- **T-1 · Close the sled's refusal leak.** Today `commitTo` fires the
  landing voice in the view *before* the VM's ratchet can refuse, so a
  refused weakening feels committed, then silently re-seats.
  Fix shape: the landing voice fires on **confirmation**, not on
  throw — the pattern `addBlockedSite` already proves (voice after the
  repository says yes). Cheapest honest build: pass the screen's known
  `isSessionActive` + current tier into the view (or gate in the
  screen's `onTierSelected` wrapper): when a session is active and the
  target weakens, do not animate the commit, do not voice — print the
  refusal at the row (R-7's builder work) and stop. Any async remainder
  re-seats silently. *Small.*
- **T-2 · One owner for the cap.** Strike `playHarmonicSeal(true)`
  from `applySmartLoadout` (the VM function machines and other callers
  also reach — a voice living there will eventually fire for a
  non-gesture; Law 1). The selector owns cap feel: preview tap →
  `playRotaryDetent`; commit tap → `playFirmClick`; resting-stop tap →
  nothing (and no re-apply). *Small — deletions plus two call sites.*
- **T-3 · Silence the cancel re-seat's crossing tick.** `noteTravel`
  fires during *every* `animateRockerTo`, including ACTION_CANCEL
  re-seats and external re-seats; a crossing on the way back tells the
  hand something "passed" during an event that did not occur.
  Suppress the tick for re-seat animations; keep it for his drags and
  his tap-throws (R-10's acquitted dialect is untouched). *Tiny.*
- **T-4 · Latch follows print state for routines.** In
  `toggleRoutine`, choose the voice from the *resulting*
  `RoutinePrintState`: ON (live) → latch-engage; WON'T FIRE → rotary
  detent; OFF → latch-release. Any future refusal gate in this
  function is evaluated before any voice. *Tiny.*
- **T-5 · Day-chip pitch split.** `toggleRoutineDay`: detent ~680 Hz
  when the day is added, ~520 Hz when removed (the function's existing
  rotary call, parameterised by the mask's result). *Tiny.*
- **T-6 · The ±30 clamp prints.** When `adjustRoutineTime` refuses on
  the span clamp, surface the refusal in print at the row (W-1's
  wording cure, interim form) — and keep the current silence. Retire
  with the chips when the wheels land. *Small.*
- **T-7 · Tally jump targets ≥ 42 dp tall** (rides GLANZ §3's segment
  build; §1.4). *Free inside that work.*
- **Explicitly NOT in this list:** any new haptic pattern, any new
  engine function, any voice for the status sentence, the tally, the
  jumps, or routine firings. The absence is the spec.

---

## 7. Draft GitHub comment (for VOGT to post)

— TAST (FEEL · TOUCH), for VOGT's issue #7 cell.

Filed `studio/cells/issue-7/feel-tast.md` (touch/haptic spec only, no code, DECLARED). The full surface → feel → reason map is in the file; the rulings in brief:

**The sled stands.** R-10's acquittal holds in the hand: 1:1 drag, one 440 Hz crossing tick at PEEK, directional landings (SHUT = latch engaging, PEEK = a deliberately weak 520 Hz detent, OPEN = latch releasing), silent cancel re-seat, tap = short throw in the same dialect, same-stop tap = silence. S-1's 56-vs-54 dp fix is a *feel* fix — at the far stop the thumb currently arrives before the cap does.

**Two double voices to kill (builder list T-1/T-2).** (1) A ratchet-refused sled throw currently fires its landing voice in the view *before* the VM refuses — the hand hears a commit the machine then un-happens. Landing voices must fire on confirmation (the website-ADD path already does it this way), and a refusal itself gets **no vibration at all**: per R-7 it reads as print at the control, and on Shield print is the *only* channel — no error buzz, no low knock, no re-seat voice. (2) A loadout-cap tap fires `playFirmClick` in the selector *and* `playHarmonicSeal` in `applySmartLoadout` — a seal voice spent on an act that seals nothing. Strike the seal from the VM function; the selector owns cap feel alone.

**The cap, under R-4's Ghost Preview:** preview tap = one rotary detent (it commits nothing and must not sound committed); commit tap = **one firm detent** (`playFirmClick`) — the pattern question answered: one firm detent, not a knock sequence, because a settings rewrite is heavy but reversible and unsealed, and knock shapes are reserved for physical seats and the seal. Resting-stop tap = silence and no re-apply. A cap moved by a routine firing = silence, always.

**The go-live signature: none from Shield — and the knock sequence is rejected outright.** A hand seal already owns the only knock sequence that may exist (the Throw, bit-identical, fired by the seal act wherever he stands; HAUT's SETTLE adds nothing to it, and neither do I). A proof flipping to LIVE on Shield is a *re-proof*, not a start: buzzing it would timestamp proof-lag as blocking's start — a lie in the hand to match the one R-5 killed in print. Routine firings with Shield not in front of him: silent; print is the receipt. If Ren overrules and demands a Shield-side signature, the only admissible pattern is one firm detent, once per session, on a genuine NO_SESSION → LIVE — never a knock sequence, never on a re-proof. My recommendation is to spend nothing.

**Routines:** latch for the ON/OFF cap acquitted — but it must follow the *print state*, not the raw bit: toggling into WON'T FIRE takes a neutral detent, not the engage voice (R-6's print lie, cured in the hand too). Day chips: detent stands, pitched 680 add / 520 remove. The ±30 clamp's silent return is half right — keep the silence, add the print (a dead tap is the worst feel on the screen; never cure it with a buzz). Wheels spec for R-8's W-3 is filed in §5: per-detent rotary ticks, pitch following value, wrap = ordinary detent, settle-commit = no extra voice, clamp = print only. Day Rail forward spec included.

**Never buzz (binding list in §4):** refusals, no-op taps, derived-state arrivals (status flips, RUNNING NOW, tally re-strikes, CAN'T CHECK), machine-animated travel (cap slide, re-seats, jump travel + landing — tally jumps are silent throughout; selection speaks, travel doesn't), other people's acts, typing and reading. Reserved and unspendable on Shield: the Throw, the harmonic seal, the completion pulse, the seat knock.

— TAST

---

*TAST, FEEL SPECIALIST 3 (TOUCH), VOGT's issue #7 cell — Oct 30, 2026.
DECLARED: specification only; no app code edited, nothing compiled, no
tests run, nothing committed or pushed.*
