# ISSUE #9 — FEEL 2 (SOUND): PARK'S SOUND, AND WHAT CHECK SOUNDS LIKE

*Feel 2 (sound), issue-9 cell (for Hof). Ground: the cell tree's built
`ParkDrawer.kt` (FIX key, CHECK key, both verdict effects),
`TactileSoundEngine.kt` (the whole voice inventory),
`BlockitSuitcaseAndSwitchesViews.kt` (the sleds seated in PARK),
`MainActivity.kt` (the parkOpen host effect), `WatchStore.kt`
(sweep + stamp machinery), `AUDIT-park.md` §2 P-5 / §7,
`issue-9-plan.md` §1, `issue-9-designer.md` §1.5,
`issue-9-finisher-keys.md` §2/§5. Paper only — nobody compiles.*

*PARK is the app's quietest screen, held to the highest calm.
This spec's bias follows from that: every sound PARK makes must
be earned by a press of his, and every silence must mean one
thing. Where the built code already honours that, this document
is a fence around it, not a redesign.*

---

## §0 · VERDICT, IN ONE PARAGRAPH

**Borrowing FIX's voices for CHECK is right. Proof does not get
its own voice — it gets its own silence-and-print pairing, which
is the stronger distinction.** Both keys share the seat knock on
press (identical call, character for character) and the firm
click on a whole landing (identical call, no parameters). That
is not poverty of invention; it is the drawer's grammar. FIX and
CHECK are one physical object wearing two verbs (Finisher 2's
law), operating on the same board, fired by the same sweep
machinery, judged by the same predicate — GRANTED. They differ
in *scope* (one named door vs the counted board) and in *where
the man stands while it resolves* (gone to Settings vs present
in the room), never in *kind*. Scope is already spoken — by the
key's word, by the board re-printing under his eyes, by the
NOT PROVED line. A new timbre would have to claim a new kind of
event, and CHECK is not one: it is a re-measurement, SELF-REPORT
grade, repeatable at will. The engine's genuinely distinct
voices each mark an irreversible or terminal act — the Seal
Throw (a session locking), the tear (a counterfoil destroyed),
the completion pulse (a finish). Spending any of that weight on
a ~300ms re-check would overclaim exactly the grade the
Designer's §1.1 copy law forbids CHECK to imply, and would
pre-sell the witnessed proof that belongs to the parked
Live-Fire Strip (issue #21). **No new voice. No retune. The
PATCH-READY section (§6) is therefore mostly a freeze — stated
as values, so the freeze itself is checkable.**

---

## §1 · INVENTORY — EVERY SOUND PARK MAKES TODAY, AND EVERY ONE IT DOESN'T

### 1.1 The complete sounding inventory (four call sites, two voices)

| # | Act | Call (as built) | Voice |
|---|---|---|---|
| 1 | FIX press (DOWN) | `playSeatKnock(10, if (isNight) 0.06f else 0.1f, 300f)` | seat knock, floor weight |
| 2 | CHECK press (DOWN) | `playSeatKnock(10, if (isNight) 0.06f else 0.1f, 300f)` — the identical call | seat knock, floor weight |
| 3 | FIX return, named item reads GRANTED at the live return read | `playFirmClick()` | firm click |
| 4 | CHECK set lands, counted four all GRANTED | `playFirmClick()` | firm click |
| 5 | Any sled throw inside PARK (Sound, Shorts + Reels, Social Sites) | sled's `fireFeel(8L)` → `playFirmClick()` on ACTION_UP | firm click |

Row 5 matters to the borrowing question: the firm click is
*already* PARK's general "a control seated, a fact landed"
voice — the shipped sleds, moved whole, spend it on every
toggle. CHECK's whole-landing click is therefore not a FIX
voice borrowed into a stranger's room; it is the drawer's own
house voice spent on the drawer's most important landing. If
anything diluted the click, it would be the sleds — and the
sleds are acquitted shipped furniture whose contract is
"control unchanged." The hierarchy survives anyway, because
context separates them: a sled click answers his finger in the
same instant on the control he is touching; the CHECK click
arrives after a held dip, at the completion of a set he asked
to be proved, with the board and LAST PROVED moving in the
same frame. Nobody confuses the two at 1 AM, because only one
of them required waiting.

### 1.2 The complete silent inventory (by construction — no call exists)

- The drawer's rise (280 SEAT) and leave (160 THROW), the grab
  strip's drag and 40% settle, the grip's 24→28dp growth, the
  scrim tap, PARK re-tap dismissal: **no sound call anywhere on
  the path** — not in `ParkDrawer.kt`, not in the host.
- Opening PARK fires the Watch sweep (`MainActivity`'s
  `LaunchedEffect(parkOpen)` → `refreshSystemGuard()` +
  `runWatchSweep()`): **silent, and it raises no CHECKING state
  and can play no click**, however the set lands (see §4, fence 3).
- Every board row re-print, including each of the six sweep
  landings during CHECKING: silent (see §4, fence 1).
- LAST PROVED advancing: silent in itself; when a CHECK caused
  it and the set is whole, it coincides with the §1.1 row-4
  click — one sound for the event, never one per witness.
- FIX appearing / disappearing (its `AnimatedVisibility`),
  CHECK's dress, the NOT PROVED line appearing, rows crossing
  into STALE on the drawer's 5-second re-derivation beat, the
  Theme readout changing: silent.
- Both failure landings (FIX unrepaired, CHECK unwhole): no
  call — the verdict is the *withheld* click plus print (§5).

That is the whole acoustic surface of the quietest screen:
**two knocks that say "heard," one click that says "whole,"
and silence for everything else.** This spec changes none of it.

---

## §2 · WHY THE BORROWING IS RIGHT, VOICE BY VOICE

### 2.1 The press knock — identity is the meaning

The seat knock on DOWN says exactly one thing in this house:
*your hand landed; the act has started.* It is an receipt, not
a description of the act. FIX and CHECK need the identical
receipt because the man's finger must learn **one** PARK key
feel — same body, same dip (Finisher 2 §1–§2), same voice on
the way down. Give CHECK a different press sound and the
drawer teaches a false lesson: that the keys are different
instruments, when the truth the finish worked to build is that
they are one instrument with two destinations. The divergence
belongs *after* the press — in the dip's duration (FIX: the
whole absence; CHECK: the sweep's landings) and in what ends
it — never at the press itself.

The chosen weight is also correct in absolute terms. At
amplitude 0.10 / 0.06 this is the engine's floor: Vega's knock
family runs 0.5–1.0 elsewhere (gate landings, the Loaded
Wheel). PARK's keys knock at a fifth to a tenth of that —
the pawl heard through a closed door. On the quietest screen,
the press receipt should be barely above the threshold of
notice, and it is (numbers in §3).

### 2.2 The firm click — same predicate, same voice

The firm click's engine definition is generic by design:
"for heavy Canvas controls (sled throws, knob detents): a hard
strike, a breath, a softer seat." Its semantic across the app
is **a thing seating true**. FIX-repaired is one permission
line seating true. CHECK-whole is the counted board seating
true. Same predicate (GRANTED), same voice; the scope
difference is carried by everything around the sound — he
watched six lines re-stamp, the dip released only when the
last stamp landed, LAST PROVED took a new time in the same
instant. A listener who hears the click *after that* does not
need a bigger click to know it meant the board.

### 2.3 The alternatives, priced and refused

- **Completion pulse for CHECK-whole** (`playCompletionPulse()`:
  185 Hz, 64 ms, gain 0.68, triple softening ticks): the
  loudest, longest voice in the drawer would belong to its
  most repeatable act. It says *the machine is winding down
  from a finish*. A man can press CHECK six times in a minute;
  the sixth finish fanfare is a lie about what happened.
  REFUSED — wrong tense (terminal) for a present-tense verb.
- **Mechanical latch** (`playMechanicalLatch`, 760/520 Hz,
  gain 0.54): says a two-stage mechanism engaged. Nothing
  engages on a check; the warrant does not move because he
  measured it. REFUSED — claims a state change measurement
  never makes.
- **A new bespoke CHECK voice** (e.g. a double-knock at proof
  pitch): a new engine voice is a machinery amendment priced
  to Ren, and its only possible message — "proof is a
  different *kind* of event from repair" — is false (§0). It
  would also break the one-voice-per-predicate economy that
  lets him read this app by ear: today, in PARK, click =
  seated-true, everywhere, always. REFUSED — no new voice for
  issue #9, and none should be briefed for PARK without
  overturning this paragraph by name.
- **A failure voice** (buzz, low thud, detuned knock) for
  either verb's unwhole landing: no negative voice exists
  anywhere in the engine, and PARK — the calm screen, the
  1 AM screen — is the worst room in the app to invent one.
  Failure here is print in full ink plus a withheld
  affirmation (§5). REFUSED, permanently for this drawer.

### 2.4 The one distinction proof *does* own

CHECK's verdict is distinct from FIX's in **time-shape**, and
that is enough: FIX's click is a *return* verdict — it arrives
after an absence, judged at a single live read 800 ms past the
host's return tick. CHECK's click is a *completion* verdict —
it arrives in the room, at the exact emission where the last
of six stamps reaches the press instant, with the board
visibly re-printed around it. Same voice, different sentence
structure. Ears distinguish events by when a sound lands and
what the eyes confirm in that frame, not by timbre alone.

---

## §3 · DAY / NIGHT LEVELS — THE EXACT NUMBERS

Derived from `TactileSoundEngine` — knock:
`gain = 0.22 + 0.3 × amplitude01`, audio length `durationMs + 6`,
haptic amplitude `(amplitude01 × 255)` with a 0.35× tail segment;
click: fixed, parameterless.

| Voice | Mode | Haptic waveform | Acoustic transient |
|---|---|---|---|
| Seat knock (both keys, press) | day (`isNight=false`) | `[0, 10, 5] ms` @ `[0, 25, 9]` — 15 ms total | 300 Hz, chord 0.9, 16 ms, **gain 0.250** |
| Seat knock (both keys, press) | night (`isNight=true`) | `[0, 10, 5] ms` @ `[0, 15, 5]` — 15 ms total | 300 Hz, chord 0.9, 16 ms, **gain 0.238** |
| Firm click (both verdicts + sleds) | day **and** night — no variant | `[0, 13, 20, 8] ms` @ `[0, 225, 0, 135]` — 41 ms total | 620 Hz, chord 1.1, 12 ms, **gain 0.500** |

Rulings on these numbers:

1. **The knock's day/night split is a haptic split wearing an
   acoustic costume.** Night gain 0.238 vs day 0.250 is
   −0.4 dB — below reliable perception. The real night act is
   the haptic strike falling 25 → 15 (−40%). That is the right
   shape for this drawer: at night PARK is *felt* more than
   heard, and the felt part is what softens. KEEP both values
   exactly; do not "fix" the inaudible acoustic half into a
   larger split — a knock that sounds like a different object
   at night is two knocks to learn.
2. **The firm click has no night variant, and must not grow
   one.** `playFirmClick()` takes no parameters; every surface
   in the app (sleds included) hears the identical click. A
   PARK-only night click would require an engine amendment
   and would make the verdict's identity depend on the clock —
   the opposite of the Seal Throw's bit-identical law, in the
   one drawer whose whole job is stable testimony. The click
   *should* stand clearly above the knock in both modes —
   roughly 2× acoustic amplitude, ~9× haptic strike — because
   it is the drawer's only affirmative verdict and must be
   unmissable even half-asleep. Day and night, the **ratio**
   is the spec; the absolute level is the engine's. KEEP.
3. **`isNight` here is `effectiveNight` (`darkTheme || sealed`)
   from the host.** Under the seal the point is moot: both keys
   are absent (Finisher 2 §8), so the sealed disjunct can never
   reach a sounding key. No sealed-state sound ruling is
   needed, and none may be invented — a sealed PARK makes no
   key sounds because it has no keys.
4. **SOUND EFFECTS OFF is an acoustic mute, not a felt mute —
   say so plainly.** By engine law (the Throw's S0 comment:
   "muting loses timbre, never the event"), `soundEnabled=false`
   gates `enqueueTransient` only; the haptic half of both
   voices still fires. So with the Sound sled OFF, a CHECK
   press is still *felt* (15@night / 25@day strike) and a whole
   landing is still *felt* (the 225 click waveform) — silently.
   That is shipped engine law, not a PARK defect, and this
   spec does not propose changing it. But no future wording
   may promise "silence" for the OFF state without the word
   *sound*: what OFF guarantees is **no acoustic transient**,
   felt ticks included in the guarantee's exclusions.
5. **Known sled asymmetry, recorded, not patched:** a sled
   fires its click on ACTION_UP *before* its toggle lands, so
   turning Sound OFF sounds one farewell click, while turning
   it ON arrives acoustically silent (the engine was still
   muted at fire time; haptic only). This is the shipped
   control's behaviour, moved whole into PARK; it is honest
   (the click belongs to the state being left) and it is not
   CHECK's business. No patch in this lane.

---

## §4 · WHAT MUST NEVER SOUND — THE FENCES

Each fence names a sound that does not exist today. Any patch
that introduces one overturns this section by name or does
not land.

1. **Per-row re-stamps during a check: NEVER.** The sweep's six
   queries land in parallel, in no guaranteed order, each
   re-stamping its row as it lands (`WatchStore.runSweep`).
   Sounding them would be up to six ticks in ~300 ms — a
   rattle that (a) shatters the calm the drawer exists for,
   (b) fabricates a progress narrative — order, pace, count —
   the store never promised (its own comment: "no fixed
   stagger — a fast grant never waits for a slow one"), and
   (c) spends the knock, the press receipt, on events that are
   not presses. Rows re-print **atomically and silently**; the
   key alone carries CHECKING; one click, at most, carries the
   completion. This is the single most important fence in this
   document.
2. **The drawer's open and close: NEVER.** Three independent
   reasons, any one sufficient. (a) PARK opens uninvited —
   the first-run auto-open, the host's arrival paths — and
   unsolicited motion must not make unsolicited sound, least
   of all at night. (b) Opening already fires a sweep; a sound
   on open would be heard as that sweep's receipt, pre-spending
   the verdict voice on an act he never pressed. (c) KIN-DRAWER
   is a visual motion law (280 SEAT / 160 THROW); no other
   sheet motion in the app sounds, and PARK does not start.
   The same fence covers the grab-strip drag, the 40% settle,
   the scrim tap, and dismissal by PARK re-tap.
3. **Any sweep he did not press CHECK for: NEVER a click.**
   Sweeps fire on PARK open, on ON_RESUME (the FIX-return road
   included), on Deck entry. The built code already fences
   this correctly by *where the click lives*: inside the
   `checking` derivation's landing effect, gated by
   `checkPressedMono > 0`, and inside the FIX return effect,
   gated by `fixPressed`. An unattributed click — a whole set
   landing from a lifecycle sweep while he is merely looking
   — would be the drawer applauding itself. Protect both
   gates exactly as built; a refactor that moves the click
   into the store, the sweep, or the snapshot collector
   breaks this fence by construction.
4. **A second press of CHECK while CHECKING: nothing.** The
   built gate (`interactive && !checking`) already swallows
   it — no second knock, no queued sweep, no double verdict.
   The dipped key is visibly and audibly *held*; keep it so.
5. **State drift with no press behind it: NEVER.** STALE
   crossings on the 5 s beat, interceptor re-derivations,
   heartbeat/BIND re-stamps, FIX's entry/exit animation,
   CHECK's dress promotion when the board ages, the NOT
   PROVED line's appearance, LAST PROVED advancing off a
   non-CHECK sweep: all silent, forever. PARK never speaks
   first. (Its one present-tense voice is print.)
6. **A timeout or failure tone for a sweep that never
   completes: NEVER.** Finisher 2 §5 refused the Designer's
   2,500 ms FAILED window — a clock may not mint a verdict.
   The sound corollary: the key staying dipped at CHECKING
   for the drawer's life is also *silent* for the drawer's
   life. No escalation, no reminder, no dying-fall. Silence
   plus a held dip is the honest face of "still measuring."
7. **Under the seal: nothing, because there is nothing to
   press.** Both keys absent, sleds print under their
   touch-swallowing overlays. Any sound from a sealed PARK
   would be a control claiming to exist. (Sweeps that land
   under the seal re-print the board silently, per fence 5.)

---

## §5 · SILENCE SEMANTICS — AFTER CHECK vs AFTER FIX, AND THE P-5 JUDGMENT

### 5.1 The two silences are not the same silence

| | FIX | CHECK |
|---|---|---|
| Press receipt | knock (day/night, §3) | knock — identical |
| Dip held for | the whole absence in Settings | the sweep's landings (~300 ms class) |
| Whole / repaired | **click**, no word prints from the key | **click**, no success word added |
| Unwhole landing | **silence**, no word prints from the key ("no word prints, ever" — built comment) | **silence + print**: `NOT PROVED` + first unproved counted door, board-row grammar, persistent until next press / close / board reads whole |
| What the silence is disambiguated by | the key's next face — it names the next lack or absents itself — and the board, on re-reading | the NOT PROVED line itself, in the same instant, without re-reading anything |
| Scope of the verdict | the one named item (any of the six permissions) | the **counted four** for the click/lack decision; completion (dip release) waits for **all six** permission stamps |

The last row's seam must be stated plainly because the ear
cannot hear it: CHECKING ends on six stamps (the acquitted
derivation), but the click is decided on the counted four —
a set landing with OVERLAY or BATTERY missing still clicks,
and NOT PROVED never names an uncounted door. That matches
the Guard line's counted grammar and the Designer's §1.5
("counted 4/4"), and the FIX key seated directly above still
names the uncounted lack, so nothing goes unspoken. I record
it as correct — but it is a *counted* "whole," and any future
copy or comment that calls the click "the whole board proved"
without the counted qualifier overstates what sounded.

### 5.2 Judging the audit's split verdict (P-5), now that NOT PROVED exists

AUDIT-park P-5 charged the FIX return: the verdict is
sound-only, "no word prints, ever," and silence reads
identical for fixed and still-broken. Dens acquitted the
*silent verdict* for sound; Muede overruled him for the
drawer's real hour — **the click stays (the acquittal holds
for sound), a print is ADDED.**

My judgment, from the sound seat:

1. **The sound acquittal stands, unchanged, and NOT PROVED
   strengthens it.** Silence-after-unwhole was never an
   ambiguous *sound* — it is the consistent withholding of the
   one affirmative voice, the same grammar as CHECK's. What
   made P-5 bite was that silence was the *only* channel. The
   house rule this drawer now demonstrates is the right one:
   **failure is never a tone; failure is a withheld tone plus
   print.** No failure sound for FIX either — §2.3's refusal
   applies with full force. Sound's half of P-5: CLOSED,
   acquittal final.
2. **The print half is PARTLY answered — by CHECK, not by
   FIX.** The NOT PROVED line proves the drawer *can* print a
   landing verdict, in its own row grammar, persistently,
   without blame inflation (it names the measured lack; the
   plan's correction of the Designer's "DID NOT ANSWER" —
   every door answers, MISSING is an answer — is honoured in
   the built line). A man returning from a failed FIX trip
   now has a same-room road to a printed verdict: press
   CHECK, read NOT PROVED. That is a real cure of the
   *symptom* (he need not take the trip twice blind — one
   press tells him).
3. **But it does not close P-5's charge as filed.** The charge
   is against the FIX *return*: at the return instant, the
   visited door still gets no FIXED / STILL OPEN print — the
   800 ms return read still speaks only in click-or-silence.
   And the two verdicts are not interchangeable: NOT PROVED
   names the first unproved **counted** door from a fresh
   sweep; it does not certify the door he just visited (an
   OVERLAY or BATTERY fix is invisible to it, §5.1), and it
   costs him an extra deliberate press the audit's cure
   (§4.6) promised he would not need. Verdict: **P-5 print
   half — mitigated, not discharged.** It closes only when
   §4.6's printed return verdict lands on the FIX row itself.
   Nothing in this sound spec blocks that print, and §4
   fence 5 already guarantees it will arrive silently, as
   print should.
4. One asymmetry to protect while §4.6 is pending: CHECK's
   unwhole silence is *semantically complete* (print speaks
   in the same instant); FIX's is *semantically incomplete*
   (print is owed). Do not "balance" them by removing NOT
   PROVED or by adding a FIX failure tone. The asymmetry is
   the accurate map of what has been built.

### 5.3 Silence after a whole CHECK vs silence after a whole FIX — a final note

There is none: whole landings are never silent, on either
verb. In this drawer silence has exactly three lawful
meanings — *nothing was pressed* (§4), *the landing was
unwhole* (§5.1), *sound is muted and only the felt half
remains* (§3.4) — and a whole landing is none of them. If a
future build ever lands a whole set in total silence (a
dropped `playFirmClick()` in a refactor), that is not a
calmer design; it is a broken verdict, and acceptance-by-ear
(§6.3, item 2) exists to catch it.

---

## §6 · PATCH-READY — THE FREEZE, AND ONE PRICED REFUSAL

### 6.1 Value changes to existing calls: NONE

The two sounding call sites are frozen at their built values.
Any patch touching one must touch the other identically or
cite this document's overturn by name:

```kotlin
// FIX press AND CHECK press — identical, both places in ParkDrawer.kt:
soundEngine?.playSeatKnock(
    durationMs = 10,
    amplitude01 = if (isNight) 0.06f else 0.1f,
    pitchHz = 300f
)

// FIX return (repaired) AND CHECK landing (counted whole) — parameterless:
soundEngine?.playFirmClick()
```

Engine-side freeze: `playSeatKnock`'s gain law
(`0.22 + 0.3 × amplitude01`), its `(durationMs + 6)` audio
length, its 0.35 tail ratio; `playFirmClick`'s waveform
(`[0, 13, 20, 8]` @ `[0, 225, 0, 135]`), 620 Hz / 12 ms /
gain 0.5 / chord 1.1. PARK owns no private copy of any of
these numbers and must not grow one.

### 6.2 The priced-but-refused option (recorded so it is not re-briefed as new)

A night-softened firm click for PARK only — e.g. a new
`playFirmClick(gainScale)` engine parameter, night scale
0.8 (gain 0.40, haptic strike 225 → 180). Cost: one engine
signature change + every existing call site audited + a
second click identity for the ear to learn + divergence from
the sleds' click *in the same drawer at the same hour*.
Benefit: −1.9 dB on a 12 ms verdict at night. **REFUSED**
(§3.2). Filed here at full price so a future seat spends
nothing re-deriving it.

### 6.3 Acceptance, by ear (on the phone, sound ON, then OFF)

1. Open PARK cold: rise silent; rows land silent. Close it
   by scrim, by drag, by re-tap: silent every way.
2. Press CHECK on a whole board: one floor knock felt more
   than heard; dip holds; rows re-print in silence; on the
   last landing, exactly one firm click with LAST PROVED's
   new time. Never two sounds, never a tick per row.
3. Press CHECK with a counted lack standing: knock, dip,
   silent landings, **no click** — and NOT PROVED printed
   under the key, naming the door. Close and reopen: the
   open-sweep lands the board in silence, no click, however
   it lands.
4. Press FIX, repair the door in Settings, return: one click
   as the dip releases. Return without repairing: silence,
   dip released, key naming the next lack or gone. Then
   press CHECK: NOT PROVED confirms in print what the
   silence said.
5. Throw each sled: one firm click each. Turn Sound OFF by
   its own sled: one farewell click, then presses are
   felt-only (knock and click waveforms, no audio). Turn it
   back ON: the throw itself is felt-only; the next CHECK
   verdict sounds again.
6. At night (or sealed-theme night): the knock is softer in
   the hand (15 vs 25) and indistinguishable in the ear; the
   verdict click is the same click as at noon. Hold CHECK
   mid-flight with a second press: nothing — no knock, no
   queue, one verdict at most.

---

*Sound inventory closed: two voices, four call sites, three
lawful silences. PARK stays the quietest screen — and now its
verb of proof is exactly as loud as its verb of repair, which
is to say: barely, and only when it has something true to say.*

— Feel 2 (sound), issue-9 cell (for Hof)
