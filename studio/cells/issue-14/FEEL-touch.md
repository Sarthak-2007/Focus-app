# FEEL — TOUCH: EVERY PRESS ON THE RECEPTION SHEET
## Issue #14 cell · Haut (feel specialist, seat 2) · for Pforte's merge

Ground read whole: `ReceptionSheet.kt` (every `pointerInput` /
`detectTapGestures` site), `service/TactileSoundEngine.kt` (what
the named voices physically are), `studio/plan/AUDIT-setup-sheet.md`
§3 (F-3/F-4/F-5 micro-pass, Letzt F-1/F-2 tear, F-3 key-exit pair)
and §8 acquittals, Funke's `INNOVATION-mechanisms.md` §1 (the
Return Chit). Paper only; nobody compiled.

House standard applied throughout: a press must answer with
**mass** — a haptic waveform and/or a PCM transient and/or real
travel — in proportion to the act it commits. Nothing weightless,
nothing cartoon. One act, one voice. A voice that fires for an
act that never happened is a lie; an act that happens with no
voice is a dead press.

A note on the engine's vocabulary, because everything below is
priced in it: every `play*` call is **two channels at once** — a
`VibrationEffect` waveform under the finger *and* a float-PCM
transient in the ear (`TactileSoundEngine.kt`). With sound
muted the waveform still fires (only `hapticsEnabled` gates it),
so "voice" in this file always means *felt + heard*, never
audio-only. A key with **no engine call at all** is dead in both
channels at once.

---

## §1 · THE CENSUS — every pressable on the sheet

**Count: 4 gesture sites · 10 pressable instances · at most 6
live at any one moment** (4 door keys + OPEN + DONE, in the
all-MISSING or all-unread state; 2 in the all-SET state).

| # | Pressable | Instances | Target | As built: down-feel | As built: act fires | Released-outside / cancelled |
|---|---|---|---|---|---|---|
| 1 | FIX — `<door>` (MISSING doors only) | 4 | full width × 48dp (`ReceptionSheet.kt:669`) | seat knock (quietest voice in the engine) + 1dp dip | on **DOWN**, inside `onPress` (`:675–687`) | identical to a clean tap — departure already gone |
| 2 | CHECK — `<door>` (STALE / UNMEASURED doors only) | 4 | full width × 48dp (`:708`) | **nothing — no engine call, no dip, no visual change** (`:713–717`) | on **DOWN** (`onCheck()` in `onPress`) | identical to a clean tap |
| 3 | OPEN THE LIST | 1 | full width × 48dp (`:486`) | **nothing — no engine call, no dip** (`:491–495`) | on **DOWN** (`onOpenShield()` in `onPress`) | identical to a clean tap — already navigated away |
| 4 | DONE — `TO THE DECK →` | 1 | full width × 52dp (`:556`) | `playFirmClick()` — the heavy-Canvas-control voice | on **DOWN** (`playFirmClick(); onDone()` `:562–565`) | identical to a clean tap — **the setup is already stamped** |

Not pressables, excluded by name: the door cards themselves
(a SET door carries no key at all — `else -> Unit`,
`:731` — correct: a granted door owes no press); all text;
the scroll surface (a drag, not a press — but see T-3, it
interacts); system BACK (host law, no sheet handler).

Shared gate, acquitted with a note: every site's
`pointerInput` exists only while `interactive = visible &&
settled` (the sheet fully seated after its 280ms rise). A
finger landing during the rise gets nothing — no knock, no
act. Lawful: a moving target must not commit. It also cannot
*queue*: the press is swallowed whole, and at 04:00 a man
who tapped early will tap again. No cure priced; the rise is
280ms and the target is visibly moving.

**Target sizes acquitted.** 48dp is the platform minimum met
exactly, three times; DONE at 52dp is the only key above the
minimum, and it is the stamp — the size hierarchy already
matches the weight hierarchy. Full width everywhere means a
lateral miss is impossible; the only miss geometry on this
sheet is vertical (release-outside) and temporal (the rise).
That is why the release behaviour, not the aim, is where
this sheet's touch fails.

---

## §2 · WHAT THE VOICES ACTUALLY ARE (engine evidence)

**FIX's seat knock, as called** (`:680–685`):
`playSeatKnock(durationMs = 10, amplitude01 = 0.10 day /
0.06 night, pitchHz = 300f)`. The engine expands it
(`TactileSoundEngine.kt:665–679`) to:

- haptic waveform `[0, 10, 5]ms` at amplitudes
  `[0, 25, 8]` by day, `[0, 15, 5]` by night
  (25 = ⌊0.10 × 255⌋; tail at 35%);
- PCM transient 300Hz, 16ms, gain **0.25 day / 0.238 night**,
  chordRatio 0.9. This is the engine's floor — the comment at
  the call site says so itself: "PARK's seat knock at its
  floor — the engine's quietest existing voice."

**The firm click** (`TactileSoundEngine.kt:644–656`):
waveform `[0, 13, 20, 8]ms` at `[0, 225, 0, 135]`; PCM 620Hz,
12ms, gain **0.50**, chordRatio 1.1. Its own doc: *"Firm click
for heavy Canvas controls (sled throws, knob detents)."* Nine
times FIX's haptic amplitude, twice its gain. This is the
voice DONE spends today, and the voice the FIX return verdict
spends when a door repaired.

**The tear pulse** (`TactileSoundEngine.kt:817–829`):
waveform `[0, 8, 14, 8]ms` at `[0, 160, 0, 110]`; PCM 240Hz,
40ms, gain **0.68**, chordRatio 1.21. Its doc is explicit that
it exists so a tear is *not* mistaken for a completion. The
audit (Letzt F-1, §3) has already ruled it DONE's voice; as
built, DONE has never used it.

**The seat knock's weight ladder** (engine doc, `:658–664`):
the same voice speaks at other weights elsewhere — FLOW toll
10ms @ 0.5, FLOW shore 14ms @ 0.8, the wheel 16–24ms @
0.85–1.0. The ladder is the house's own mass scale, and §4
prices OPEN on it.

---

## §3 · CHARGES — dead, doubled, or lying about its weight

**T-1 · CHECK IS A DEAD PRESS — STANDS.**
`ReceptionSheet.kt:713–717`: the whole gesture is
`onCheck(); tryAwaitRelease()`. No engine call, no dip, no
colour change — in either channel, at any hour. The act is a
question (re-sweep one door), and its result is often *no
visible change at all* (an UNMEASURED door that stays
unmeasured), so the press's entire evidence is a knock that
does not exist. A dead question reads as a broken key; he
will press it twice and trust the sheet less. **Cure already
ruled** (audit §3, F-4/F-5 micro-pass): CHECK takes FIX's
knock values, voice only, no dip — specced exactly in §4.2.
The charge stands against the built tree, where the
micro-pass has not landed.

**T-2 · OPEN THE LIST IS A DEAD DEPARTURE — STANDS. The
worst press on the sheet.**
`ReceptionSheet.kt:491–495`: same empty gesture as CHECK —
`onOpenShield(); tryAwaitRelease()`, nothing else. But where
CHECK stays on the sheet, OPEN **commits a departure**: the
sheet closes unstamped and the app navigates to Shield (and
in the built tree, per audit H-2, the sheet then forgets him
for the sitting — Funke §4's re-raise law is the cure for the
trap, not for the touch). The largest silent act on the
sheet: zero haptic, zero sound, zero travel, and the scene
change is the only receipt, arriving a drawer-animation
later, unattributable to the press. No seat and no ruling
anywhere gives this key a voice — it is not a cured charge
waiting to land, it is an unpriced one. Cured in §4.3.

**T-3 · EVERY KEY LIES ABOUT ITS RELEASE — STANDS, all four
sites.**
Every site calls `tryAwaitRelease()` and **discards the
result** (`:494`, `:565`, `:686`, `:716`), having already
fired its act inside `onPress`. Consequences, in felt order:

1. **Drag-off does not abort.** Press DONE, realise, slide
   the finger off the key, lift: the setup is stamped. Press
   FIX and drag off: he is in Android Settings. A real key
   commits on a clean release inside its travel; these
   commit on contact.
2. **A scroll that starts on a key fires it.** The sheet is
   one long `verticalScroll`; a swipe beginning on a door
   key or on DONE enters `onPress` before the scroll can
   cancel the press — and the act is already spent. On a
   1,700–2,200dp document he scrolls constantly; the first
   swipe that begins on a FIX key buys him an excursion he
   never chose. (The feel side-effects — knock, dip — firing
   on a grazed key are honest travel; the *act* firing is
   the lie.)
3. **The release itself is theatre.** `tryAwaitRelease()` is
   awaited purely to consume the gesture. The finger's lift
   — the moment every physical button in his life uses to
   decide — decides nothing here.

Cure: the release law in §5 — feel on DOWN, act on
release-true, per key. DONE takes its own form of the law
(§4.4).

**T-4 · DONE SPENDS A KNOB'S VOICE, AND THE TEAR WOULD
DOUBLE IT — STANDS against the built tree.**
As built, DONE fires `playFirmClick()` — by the engine's own
doc a voice for *sled throws and knob detents*
(`TactileSoundEngine.kt:642–643`). DONE is not a knob. It is
a stamp over a tear: it books the setup and the sheet
separates. The audit's ruling (Letzt F-1) already assigns it
the tear pulse *in replacement*. The doubling hazard is
named here so the merge cannot miss it: if the tear lands
**on top of** the firm click instead of instead of it, one
press pays two voices ~in the same instant — a heavy click
*and* a rip — which reads as a machine stutter, the one
cartoon outcome the house forbids. One act, one voice:
`playFirmClick` leaves this call site the same commit
`playTearPulse` enters it. (The firm click keeps its lawful
seat on this sheet: the FIX return verdict, §4.1.)

**T-5 · THE RETURN CLICK FLOATS UNBOUND, AND THE DIP IS
RELEASED BY A TIMER — STANDS against the built tree;
touch seconds Funke's F-1/F-3 from the finger's side.**
The firm click (on repair) and the dip's release both hang
off the fixed 800 ms `LaunchedEffect(returnTick)` at
`ReceptionSheet.kt:288–300`: the verdict waits a fixed
sleep, judges whatever snapshot exists, and pays in sound
alone. Under the finger that reads as: return, look at the
sheet, *nothing* — then, a second later, a click in the
hand with no visible event attached to it, or silence and a
dip that quietly pops up. The sound arrives too late to bind
to the door he just fixed; it floats. **Cure already
specced**: Funke §1 — the chit judges on the first
post-departure measurement, the host plays the firm click at
*resolution* (his §1.5, Letzt A-5's second channel), and the
dip is released by the chit leaving DEPARTED, never by a
timer. §6 of this file sets the simultaneity condition that
makes that release honest.

**T-6 · THE DIP IS TWO OBJECTS WEARING ONE OFFSET —
JUDGED, kept with conditions (§6).**
The 1dp offset at `:670` is at once (a) press travel — the
answer to a finger, which should live ~a tap's length — and
(b) an absence latch, held for the whole Settings excursion
and, as built, until the 800 ms verdict. One unnamed offset
doing a tap's job and a minutes-long job is a conflation.
It is *not* a kill: the audit acquitted the dip for FIX
(§8, Letzt), Funke §1.1 keeps it expressly ("it is dress,
not verdict"), and §6 rules how it survives the chit. No
other key takes a dip, under any cure in this file.
---

## §4 · THE CURED PRESS-FEEL, KEY BY KEY

Universal grammar (the §5 law applied): **feel fires on
DOWN; the act commits only on a clean release inside the
key** (`tryAwaitRelease()` returning true). What each key
does on a miss follows from that grammar and is stated per
key — a miss must feel like an aborted press, never like a
successful one and never like nothing.

### 4.1 · FIX — the departure (heaviest door act)

- **Target:** full width × 48dp — unchanged.
- **On DOWN:** `playSeatKnock(durationMs = 10,
  amplitude01 = 0.10, pitchHz = 300f)`, night
  `amplitude01 = 0.06` — *exactly as built* (waveform
  `[0,10,5]ms @ [0,25,8]` day / `[0,15,5]` night; PCM 300Hz,
  16ms, gain 0.25 / 0.238). The knock's lightness is
  acquitted, not cured: it is a *press* voice, and this
  act's weight is deliberately carried by its second voice
  (below) and by the dip latch — a departure that shouted
  at press-time would spend the verdict's thunder early.
- **Dip:** 1dp, instant on DOWN (a knock has no ramp; an
  animated dip would feel like rubber). Held through the
  release and the whole absence as the latch (§6).
- **Commit (release-true):** Funke §1.5's order, moved to
  the commit instant — departure chit written first
  (`writeDepartureChit`), then `onFixClick(item)` opens the
  pane. The chit's departure mono is the commit instant:
  nearer the true departure than the DOWN ever was.
- **Released-outside / scroll-cancelled:** the knock has
  sounded and the dip has travelled — both honest, the
  finger *did* press — then the dip lifts in the
  cancellation frame, no chit is written, no pane opens, no
  second sound. Felt verdict: a key half-pressed and let
  go. Nothing departed, and the hand knows it.
- **On return (the act's second half):** the dip stays
  latched until the chit resolves. At resolution, in one
  frame: the dip releases, the chit line prints in the
  visited card. RETURNED_REPAIRED adds `playFirmClick()`
  (`[0,13,20,8]ms @ [0,225,0,135]`; PCM 620Hz, 12ms, gain
  0.50) — the departure whispered, the *verdict* speaks at
  full mass, and now it lands bound to a measurement and a
  printed line, not to a timer. UNCHANGED / UNREAD / DARK:
  **no voice** (A-5's law — there is no failure voice);
  the dip's release simultaneous with the printed line is
  the answer, and silence next to new print does not read
  as dead.

### 4.2 · CHECK — the question (lightest act)

- **Target:** full width × 48dp — unchanged.
- **On DOWN:** FIX's knock, **identical call, identical
  parameters** — `playSeatKnock(durationMs = 10,
  amplitude01 = if (isNight) 0.06f else 0.1f, pitchHz =
  300f)` (audit §3 micro-pass, landed exactly). One question
  voice for STALE and UNMEASURED alike: same verb, same
  knock.
- **Dip: none, ever.** The audit's "voice only, no dip" is
  right in touch terms, not just economy: a dip is travel
  toward a latch, and CHECK latches nothing, leaves
  nowhere, and is answered in place. A dipped CHECK would
  promise a departure the sweep never makes.
- **Commit (release-true):** `onCheck()` — the sweep.
- **Released-outside:** knock spent, no sweep. The finger
  heard a question *almost* asked; the sheet owes nothing
  further.
- **The answer's feel:** the re-print is the answer; there
  is **no second voice** when the sweep lands — not on
  repair, not on no-change. A question gets one knock; its
  result is print. (This is what separates CHECK from FIX:
  FIX's second voice is lawful because FIX has a *return*;
  CHECK never left, so a verdict voice would be a second
  tap he didn't make.) If the sweep changes nothing, the
  knock alone stands as the receipt — which is precisely
  why T-1's cure matters: with the knock, a no-change CHECK
  reads as "asked, nothing moved"; without it, as "broken".

### 4.3 · OPEN THE LIST — the in-app departure (Haut's price; no prior ruling gives it a voice)

- **Target:** full width × 48dp — unchanged.
- **On DOWN:** `playSeatKnock(durationMs = 14,
  amplitude01 = 0.50, pitchHz = 300f)`, night
  `amplitude01 = 0.30` (the 0.6 night ratio FIX's
  0.10 → 0.06 establishes). Engine expansion: waveform
  `[0,14,7]ms @ [0,127,44]` day / `[0,76,26]` night; PCM
  300Hz, 20ms, gain **0.37 day / 0.31 night**, chordRatio
  0.9. Priced off the engine's own ladder (§2): longer and
  heavier than FIX's floor-knock, below the FLOW shore's
  14ms @ 0.8 — a gate landing, not a toll tap.
- **Why heavier than FIX, stated before anyone files the
  inversion:** FIX's press-knock is light because FIX's
  act pays its mass later — dip latch, return verdict
  click, printed chit. OPEN has no return verdict, no
  latch, no second channel (until Funke §4's re-raise, the
  sheet doesn't even come back for him). A departure whose
  whole felt weight must live in its single press cannot
  borrow FIX's whisper. One press, all its mass up front.
- **Dip: none.** The surface itself leaves on commit; a
  1dp travel on a box that vanishes in the same commit is
  unobservable, and unobservable feedback is decoration.
  The voice carries this key alone.
- **Commit (release-true):** `onOpenShield()`.
- **Released-outside:** knock spent, sheet stays, nothing
  navigates. At 04:00 that distinction — heard the press,
  stayed on the sheet — is the whole difference between
  "I almost left" and T-2's silent disappearance.

### 4.4 · DONE / TO THE DECK → — the stamp (the sheet's one terminal act)

- **Target:** full width × 52dp — unchanged; the largest
  target is the terminal act, already true.
- **On DOWN: no voice, and a 1dp dip** — the FIX offset,
  taken for this key for the first time, as pure travel.
  A stamp press is silent while it falls; its sound is the
  strike. A voice on DOWN here would either be the strike
  arriving before the stamp has committed (a lie under the
  §5 law) or a second voice beside the tear (T-4's
  doubling). The dip is the travel the finger feels with
  its eyes.
- **Commit (release-true), one continuous motion, never
  two:** the stamp books first (audit's G-01 order inside
  Letzt F-1), then `playTearPulse()` — waveform
  `[0,8,14,8]ms @ [0,160,0,110]`; PCM 240Hz, 40ms, gain
  **0.68**, chordRatio 1.21 — and the dip **does not
  return**: the below-the-rule block (this key in it)
  continues from the dip's 1dp into the tear's 10dp drop +
  fade over 180ms (specA) while the rule's dashes halve
  over 260ms (specB), everything above the rule motionless,
  then the 160ms THROW — 420ms total, inside the house's
  duration vocabulary. Dip and tear are one fall read at
  two scales; a dip that snapped back to 0 before the drop
  would double the motion.
- **`playFirmClick` is removed from this call site in the
  same commit** (T-4). With sound muted or the engine
  absent (`soundEngine == null` — every call on this sheet
  is nullable, and the cured spec must survive that), the
  dip + tear geometry still carry the stamp: DONE never
  depends on the speaker to have mass.
- **Released-outside:** the dip snaps back, no stamp, no
  tear, no voice. Silence is the truth here — the paper is
  unstruck, and a man who dragged off the stamp can feel
  that nothing happened because, in every channel, nothing
  did. This is the exact inverse of the built behaviour,
  where the drag-off stamps him (T-3, consequence 1).
- **No night variant, by ruling of this seat:** the tear
  fires at full gain at 04:00. It is the sheet's single
  terminal act, it can happen once per setup, and a stamp
  that whispers is a stamp that can be doubted. FIX's night
  softening governs a repeated errand knock; it does not
  govern the seal of the errand itself.

---

## §5 · THE RELEASE LAW (cure for T-3, all keys)

One law, stated once, for the builder:

> **Feel on DOWN. Act on release-true. The discarded
> `tryAwaitRelease()` result is the bug: read it.**

Concretely, per site: the `onPress` body splits — knock and
dip fire immediately; `val released = tryAwaitRelease()`;
the act (`onFixClick` + chit, `onCheck`, `onOpenShield`,
`onDone`) runs only if `released` is true. Cost: the act
lands at finger-lift, one tap-length (~80–120ms) after
contact — the timing of every physical key he owns, and of
the platform's own buttons. No key on this sheet is a
latency instrument; none may plead the DOWN-commit as
responsiveness. Gain: drag-off aborts, scroll-starts stop
committing (a swipe beginning on a key spends only the
honest travel — a knock, a 1dp graze — and no act), and the
release recovers its meaning.

Two interactions, priced so the builder doesn't discover
them: (1) Funke §1.5's departure wiring sits inside the FIX
`onFix` callback — under this law that callback *is* the
commit, so the chit-then-pane order survives untouched; only
the instant moves, from DOWN to release. (2) DONE's host
path (`stampSetupAndEnter` + the §3 handover and §2 ledger
writes in Funke's spec) hangs off `onDone`, unchanged — it
simply fires at the commit, which is where a stamp's
bookkeeping belongs anyway.

---

## §6 · THE DIP'S FATE UNDER THE RETURN CHIT — Haut's judgment

Funke §1.1 keeps the dip and re-keys its release: "released
by the chit leaving DEPARTED, never by a timer." The
question put to this seat: should a 1dp *press* offset
survive being held down for an entire Settings excursion?

**Judgment: it survives — but only once it is honest about
which of its two jobs it is doing, and only under three
conditions.**

While he is in Settings the dip is invisible — the sheet is
not under his eyes, and after process death the `remember`
holding it is gone entirely (the chit, being persisted, is
not; that asymmetry is fine — the chit is the record, the
dip is only its local shadow). The dip earns its keep in
exactly one interval: **the return**. He comes back to a
sheet where one key sits 1dp low — the visited key, marked
in the hand's own language, before a word is read. At 04:00
that is not decoration; it is the only state on the sheet
expressed as *position* rather than print.

The conditions, all three binding on the merge:

1. **Two phases, one offset, no bounce.** Press phase
   (DOWN → release) and latch phase (release → resolution)
   are the same continuous 1dp. If the finger's release
   lifted the dip and the departure re-dipped it, the key
   would visibly nod — a cartoon bounce on the sheet's most
   serious key.
2. **Release is simultaneous with the verdict print, or it
   does not happen.** The dip lifts in the same frame the
   chit line prints in the visited card (and, on REPAIRED,
   the same instant the firm click fires). A dip that lifts
   alone — before the line, after the line, or on the 3s
   deadline with no line in that frame — reads as a stuck
   key unsticking, the precise doubt T-5 charged. Funke
   §1.5's release effect (keyed on `chit.state`) satisfies
   this if the line and the dip read the same state in the
   same composition — the builder verifies that, not assumes
   it, at the merge render check.
3. **The key's exit is the dip's other lawful end.** Under
   audit F-6 a repaired door's card collapses and under
   Letzt F-3 its key leaves through the 160/160 visibility
   pair. If the key exits before or as the chit resolves,
   the dip dies with the key — no release event is owed, and
   none may be faked on a successor key.

Rejected alternatives, for the record: animating the dip
(a tweened 1dp is rubber, not mass); deepening it for the
latch phase (2dp would re-price the acquitted travel); a
separate latch visual (colour, outline — print's language,
spent on a hand problem print already solves with the chit
line).

The Return Chit's print itself: **print, not touch** — this
seat claims no part of its wording, its type, or its scroll
(Funke §1.6, Halbwach's seat). The dip is where the chit
touches the hand; the line is where it touches the eye.
They meet at condition 2, and nowhere else.

---

## §7 · ACQUITTALS — what this seat will defend unchanged

1. **Target geometry.** 48dp × full width on every door
   key and OPEN, 52dp on DONE (§1). No miss is an aim miss
   on this sheet.
2. **FIX's knock values themselves.** 10ms @ 0.10 / 0.06,
   300Hz. The whisper is the press voice of an act whose
   mass is paid at its verdict — and the night value is the
   sheet thinking about the hour it will be used at.
3. **The two-voice FIX structure** (knock out, click back),
   once the click is bound to a measurement instead of a
   timer. Two acts, two voices — that is not doubling.
4. **No key on a SET door.** The absence of a pressable is
   itself correct touch: nothing to ask, nothing to feel.
5. **No failure voice, anywhere.** A-5's law survives this
   audit whole: the sheet never buzzes a man for coming
   back empty-handed.
6. **The interactive gate during the rise.** A moving sheet
   takes no presses (§1) — restraint, not deadness.

**Bottom line for the merge:** six charges — two dead
presses (T-1, T-2), one release that lies on every key
(T-3), one wrong-and-doublable voice (T-4), one unbound
return (T-5), one conflated dip, judged and kept (T-6).
Four cured press specs, all priced in parameters the engine
already speaks; no new voice needs synthesising, no new
constant needs inventing. The sheet's hands are nearly
right — its keys are big, its one dip is honest stock, its
errand knock knows the hour. What it owes the finger is
smaller and harder: a question that knocks, a departure
that can be heard leaving, a release that means something,
and a stamp that tears instead of clicking.

— Haut (feel), issue #14 cell
