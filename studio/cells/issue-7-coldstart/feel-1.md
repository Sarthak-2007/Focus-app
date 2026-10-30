# FEEL-1 — The cold open as an experience, second by second (issue #7, cold-start cure cell)

**Seat:** FEEL-1, FEEL SPECIALIST, BINDUNG's cold-start cure cell. Report to BINDUNG only.
**Branch read:** `agent/issue-7-coldstart` @ `1b641aa` (the landed cure) on `4378dae` main.
**Method:** READ-ONLY on code. Read end-to-end: `TactileDeckScreen.kt` (Deck composition,
sentence-slot call site, proof strip, imprint), `MonolithComponentLogic.kt`
(`refusalSentenceFor` cured table, `seamMarksFor`, `imprintTenantText`,
`jamSentenceFor`/`provingSentenceFor`), `MonolithCustomComponents.kt`
(`RefusalSentenceSlot`, `ProofStripBand`, `ImprintSlot`), `WatchStore.kt`
(`RESIDENCY_GRACE_MS`, `noteResidencyStart`, `deriveWarrant`, `printState`),
`MonolithAccessibilityService.kt` (`probePipelineNow`/`requestPipelineProbe`),
`MainActivity.kt` (residency trigger, wiring), `MonolithViewModel.kt`
(warrant collector, `playHandBackTick`), `ShieldMatrixScreen.kt` (where the
260 ms Curve-A re-strike actually lives), `BlockitCoreViews.kt` (slide's
warrant gate). Nothing compiled, nothing run.
**His bar, applied throughout (SARTHAK-WORDS.md):** "feeling good is priority…
the design, the presentation, that's the beauty when u feel" (08:23);
"i dont want clutter" (08:57); Monolith exists because he wants "design and
feel and what not" over Blockit (10:44). Mediocre = not delivered.

**The scenario judged:** 04:55, half-awake, opens Monolith to arm a fasted
study block. Before the cure the Deck's resting face was
`CAN'T CHECK BLOCKING — TAP TO RETRY`. The cure as briefed: probe at open,
4 s silent grace, then either nothing (LIVE) or `BLOCKING IS OFF — TAP TO FIX`.
The cure as *landed* matches that brief on exactly one of its two races —
see Charge F-1, which is this report's headline.

---

## 0. What the landed cure actually does (the facts every beat below rests on)

1. **Probe:** `WatchStore.noteResidencyStart()` (fired from `MainActivity.kt`
   `LaunchedEffect(currentDestination)` on Deck entry) stamps
   `residencyStartMono`, pokes, and calls
   `MonolithAccessibilityService.instance?.requestPipelineProbe()` immediately,
   plus one re-probe at +1500 ms if the same residency is still unstamped
   (`WatchStore.kt:270–295`). `probePipelineNow()` stamps `lastRootMono` +
   pokes iff `rootInActiveWindow != null` (`MonolithAccessibilityService.kt`,
   cure hunk). `onServiceConnected` re-probes when a residency is already
   active. Null/throw stamps nothing — the wire reads dead, which is the truth.
2. **Grace:** `RESIDENCY_GRACE_MS = 4_000` (`WatchStore.kt:186`). It is spent
   in exactly ONE branch of the cured sentence table
   (`MonolithComponentLogic.kt:242–273`): `UNPROVEN && !a11yStampedThisResidency`
   with usage ALLOWED and age ≤ 4000 ms → `null` (silence). Every other row
   is immediate.
3. **The fog is deleted, not reworded:** no path in the cured table returns
   `CAN'T CHECK…` or `RETRY_CHECK`. `RefusalAction.RETRY_CHECK` survives only
   as an enum value (`MonolithComponentLogic.kt:178`) with zero producers.
4. **The Deck has no seam.** `TactileDeckScreen.kt:1002–1003` passes
   `stampLeft = null, stampRight = null` to `ProofStripBand` (issue #6 F8:
   seam stamps evicted from the Deck to PARK). `seamMarksFor()` is dead code
   as far as the Deck is concerned. **There is no
   `BIND HELD · UNSEEN → BIND HELD · THIS VISIT` transition to watch** —
   question 1's premise about the seam describes a surface that no longer
   exists on this screen. The proof's landing is told by three other prints
   (§1.2).
5. **The Deck has no 260 ms re-strike.** FORM's whole-line 260 ms Curve-A
   re-strike lives on the *Shield* status sentence
   (`ShieldMatrixScreen.kt:1321–1322, 1450–1451`, `AnimatedContent` fade
   260 ms `EasingA`). On the Deck: the proof strip's own law is
   *"Density flips land in one frame — no easing, no pulse, ever: the strip
   is print"* (`MonolithCustomComponents.kt`, `ProofStripBand` doc), and the
   sentence slot appears instantly and leaves by a 500 ms stability beat +
   90 ms fade + one 5 ms tick (`MonolithCustomComponents.kt:534–576`,
   `RefusalSentenceSlot`). Judged against FORM below (§1.3) — the Deck's
   manners are different from Shield's, and on the strip they are *right*.

---

## 1. HEALTHY phone — beat by beat, t = 0 → t = 5 s

Assumes the common healthy case the cure was built for: accessibility
service bound (or binding within the first frames), usage ALLOWED, root
answers. The unbound-at-t=0 sub-case diverges — it is Charge F-1, charted
in §1.4.

| Beat | Sentence slot (22 dp, reserved) | Proof strip (BLOCK / APPS / TIME) | Imprint | Sound / touch |
|---|---|---|---|---|
| **t = 0** — Deck composes; `noteResidencyStart()` + sweep fire; probe requested | **Empty.** `deckWarrant` is still `null` in this process → `refusalSentenceFor(null) = null` (`MonolithComponentLogic.kt:243`). Reserved emptiness from the first frame — the fog's old resting face is gone at birth, not after a wait. | BLOCK **UNKNOWN**, APPS count at UNKNOWN density (or UNKNOWN), TIME `ENDS HH:MM` at UNKNOWN density (`TactileDeckScreen.kt` `blockCellFor`/`idleTimeCell`: PASS tone is refused beside a non-LIVE warrant). The strip opens *honestly unproven*, never falsely calm. | **Empty** — `imprintTenantText` returns `null` unless the wire is LIVE (`MonolithComponentLogic.kt:122–138`). No `WILL SHUT` over an unproven wire. | Silence. |
| **t ≈ 0–300 ms** — probe lands: root non-null → `lastRootMono` stamped, `poke()` → WatchStore emission → VM collector re-reads `standingWarrant()` → LIVE (bound ∧ stamped ∧ usage ALLOWED) | **Empty — and it never spoke.** This is the cure's whole feel argument: on a healthy phone the slot's biography is *nothing, then nothing*. | **One frame, all three cells:** BLOCK UNKNOWN → **ON** (PASS), APPS density UNKNOWN → PASS (the numeral never changes — it was a count, not a verdict), TIME density UNKNOWN → PASS. | Tenant **appears**: `"{n} WILL SHUT · {m} PEEK"` (split grammar, zero segments omitted). It pops — the Strike is reserved for the seal and does not fire here. | **Silence. No haptic.** (Ruled correct, §3.) |
| **t = 300 ms → 5 s** — steady state | Empty. | Standing prints. The 5 s mono ticker (`TactileDeckScreen.kt:369–376`) re-judges freshness; nothing moves. | Tenant stands. | Silence. His hand is free the whole time: the slide reads the warrant **synchronously at DOWN/gate** via `warrantProvider` → `armWarrantNow()` (`BlockitCoreViews.kt:1736`, `MainActivity.kt:678`) — the grace never detains arming. He can be mid-slide at t = 1 s on a fully proven wire. |

### 1.2 What visibly "lands" when the probe lands — the honest inventory

There is **no event** on the healthy Deck. There are three simultaneous
print changes in a single frame (strip densities ×3 cells, imprint tenant
appearing) and no motion, no sound, no seam change (no seam exists), no
sentence change (it was empty; it stays empty). At 04:55, half-awake, the
perceptual read is: *the app was never broken; the numbers were grey for a
blink and now they're ink.* That is the best possible choreography for a
self-repair the user never asked about — **the cure is invisible when it
works, which is exactly what "feeling good is priority" demands of a
machine fixing itself.** Any added ceremony here (a re-strike, a check
animation, a "proving" flash) would be the app congratulating itself in
front of a man who wants to study.

### 1.3 Judged against FORM's 260 ms Curve-A re-strike

FORM filed the re-strike for the **Shield sentence** — a *verdict line*
whose text itself changes (`CAN'T CHECK…` → `BLOCKING NOW…`). The Deck's
cold-open prints are not that animal:

- **Strip densities:** a 260 ms crossfade on BLOCK UNKNOWN → ON would smear
  a verdict through 260 ms of neither-true. The strip's one-frame law is
  correct print behaviour and consistent with the tally manners FORM
  herself specced ("never a roll"). **No charge.**
- **Imprint tenant appearing:** a pop is right; the Strike's scale/settle
  is the *seal's* signature (his act). Spending it on a probe would forge
  that signature. **No charge.**
- **Sentence slot (dead path only, §2):** appearance is instant, departure
  is 500 ms + 90 ms fade. The asymmetry is correct feel — an actionable
  lack should arrive as print, not drift in; a recovery should leave
  gently, under the tick. **No charge.**

### 1.4 The divergent healthy sub-case — the flicker that survives (see F-1)

If the service is **not yet bound** at the first warrant read — the very
race the cure's own comments name ("on a cold open the service may not be
bound yet when the Deck appears", `WatchStore.kt` noteResidencyStart doc) —
the choreography breaks:

- t ≈ 0: `deriveWarrant` maps `!a11yBound → OFF`; the table's first row
  (`!warrant.a11yBound → BLOCKING IS OFF`, `MonolithComponentLogic.kt:246–247`)
  has **no grace test**. The OFF sentence appears **instantly**, and the
  strip prints BLOCK **OFF** (FAULT) in the same frame.
- t ≈ bind + probe (hundreds of ms to ~1.5 s): warrant → LIVE; the slot's
  stability beat holds the false sentence a further **500 ms**, then fades
  it over 90 ms **and fires the hand-back tick** (5 ms seat knock,
  `MonolithViewModel.kt:2489–2491`).
- Net felt experience on a *healthy* phone: **a false alarm, held, then
  ticked away** — the fog is dead, but its ghost still crosses the glass
  on the unbound path, now wearing a more alarming sentence (`OFF — TAP
  TO FIX` invites a tap at a healthy phone) and the app's one recovery
  sound spent on its own false positive.

---

## 2. DEAD pipeline — is silence-then-verdict the right feel?

**Case chart (bound-but-blind — root never answers):**

| Beat | What happens |
|---|---|
| t = 0 | Slot empty; strip UNKNOWN densities; imprint empty. Probes fire (t=0, +1500 ms re-probe). |
| t = 0 → grace end | **Silence in the slot.** The strip's UNKNOWN + the empty imprint are the only "not yet proven" evidence — ambient, not verbal. |
| Grace end | `BLOCKING IS OFF — TAP TO FIX` appears, instantly, as print, with its FIX door (`FIX_ACCESSIBILITY` → rebind pane). It then *stands* — no fade, no repetition. |
| (Truly unbound / disabled service) | OFF at the **first** read, t ≈ 0 — no grace, and that immediacy is *correct* there: a disabled guard is not a race, it's a fact with a door. (But see F-1: the code cannot tell this case from §1.4's at t = 0.) |

**When does OFF actually land?** Not at 4.0 s on the glass. The table is
evaluated with `nowMono` from the Deck's **5-second ticker**
(`TactileDeckScreen.kt:369–376`), and re-evaluation needs a recomposition
(warrant emission or tick). Residency and ticker are both born at Deck
entry, so in practice OFF lands at the **≈5 s tick** — 4 s in law, ~5 s in
glass (Charge F-2). A half-awake man staring at the Deck sees one extra
second of nothing. Tolerable; not what the spec says.

### Verdict: silence-then-verdict is RIGHT. Do NOT print a "proving" state.

The cascade's `DIDN'T ANSWER — STILL PROVING.` (`jamSentenceFor`,
`MonolithComponentLogic.kt:283–295`) and `CHECKING THE SEAL — n OF 3
PROVED` (`provingSentenceFor`) are the obvious temptation. They must not
cross to the cold open, for four grounded reasons:

1. **Whose act is it?** The jam/proving sentences exist because *he acted*
   — he slid to arm, the machine owes him a progress word inside its own
   1500 ms annulment deadline, and silence there read as a hang (issue
   #98's closed black box). At cold open he has asked for nothing and no
   deadline of his is running. A progress sentence for a job he never
   commissioned is the app narrating itself.
2. **Truth law.** Inside the grace, nothing is known — that is precisely
   why the slot is empty. `STILL PROVING` would be *technically* true
   (two probes do fire) but it prints a machine-mood, not a fact about
   blocking, in the slot whose entire discipline is: **a sentence = an
   actionable lack with a door; emptiness = everything else.** The house
   already runs this pattern three times on this very screen: the
   imprint's PENDING silence ("null until the first read… never a healthy
   count printed on a store nobody has read"), the stale-read silence
   (slot empties rather than demand a fix off an old warrant), and
   healthy LIVE itself. The grace silence is the fourth instance of the
   house's own grammar, not an omission.
3. **Clutter — his named enemy.** A proving line would appear on *every
   healthy cold open* for up to ~4 s (the probe usually lands in
   <300 ms, but the sentence's own appearance/disappearance machinery —
   instant-in, 500 ms stability, fade — would stretch and smear it into
   a visible flicker on fast phones and a standing line on slow ones).
   Printing it trains him to ignore the slot; the slot's power is that
   when it *does* speak, it has never cried wolf. That is the same
   argument that killed the fog.
4. **The information is already in the glass, at the right volume.**
   BLOCK **UNKNOWN** (not ON), APPS/TIME at UNKNOWN density, imprint
   empty: a looking eye can read "not yet proven" without being
   addressed. At 04:55 the difference between UNKNOWN-grey and a
   sentence is the difference between a room tone and a voice. The room
   tone is correct for a state that resolves itself either way within
   seconds and offers him no useful verb in the meantime — the only
   verb (FIX) is a lie until the verdict hardens, because on the
   healthy path there is nothing to fix.

** admitted cost, priced:** for the grace span, a dead phone and a
healthy phone are indistinguishable *in the slot* (they differ in the
strip). If the pipeline is dead, he waits ~5 s for his door. Accepted:
the alternative — an early FIX door — is the pre-cure disease (a tap
that fixes nothing, at a phone that was about to prove itself). The
verdict hardens once, speaks once, and its door works. That trade is
the cure itself; the feel follows the truth.

---

## 3. Sound / haptics at the proof landing — verdict: SILENCE, both

**Standing law in this app: sounds are earned by proof — and the proof
that earns sound is the SEAL, his act.** The Throw (`playSealThrow`,
fired by `SessionSealTransition` at the cascade's t=0) is the sound of
*his* commitment being proved, and HAUT's filed anti-laws for this exact
neighbourhood are explicit: *no Shield-local seal sound, no composable
haptics at proof changes, silent re-proof — the Throw must not fire
twice for one seal.* A cold-open probe landing is a **re-proof of
plumbing he never touched**, closer to HAUT's SILENT RE-PROOF than to
any seal. No code path in the cure connects the warrant transition to
the sound engine or to any haptic — and that absence is correct, not a
gap:

- A landing sound at 04:55 would carry alarm acoustics (this app's
  voices are mechanical knocks/throws, not chimes) attached to *good*
  news he didn't request — the exact "certainty without alarm" line,
  crossed from the wrong side.
- It would spend the Throw's exclusivity: if plumbing can make seal-
  family noise, the seal's sound stops meaning *you did this*.
- Haptics: a buzz for a state he cannot act on is a demand for
  attention with no verb attached. Refused on the same ground.

**Priced both ways, as ordered:**
- *Silence (recommended):* 0 builder time, 0 new assets, consistent with
  HAUT + the seal economy. Risk: no audible confirmation — mitigated to
  zero in this scenario because he is *looking at the phone he just
  opened*; the strip + imprint are the confirmation, in ink.
- *Sound/haptic on landing:* ≥ ½ builder-day (new earned voice or a
  demoted reuse of an existing knock, wiring into the warrant collector
  with a once-per-residency latch, reduced-motion/silent-mode rulings)
  **plus** a panel decision to amend the seal-sound economy — and it
  buys a signal whose only message is "the app fixed itself," which
  the glass already says. **Not worth it at any price. Do not build.**

**One asymmetry charged (F-3):** the only sound the cold open *can*
currently make is the hand-back tick — and on the §1.4 flicker path it
fires as the *reward for a false alarm*. Fix F-1 and the tick becomes
unreachable on a cold open (it remains earned where it belongs: a real
OFF that *he* fixed via the FIX door, returning to a cleared slot). No
separate sound work needed.

---

## 4. TalkBack — ruling

**Facts:** `RefusalSentenceSlot`'s `Text` carries **no semantics at
all** — no `liveRegion`, no `stateDescription`; when the sentence has
an action it is `clickable(role = Role.Button)`, otherwise bare text
(`MonolithCustomComponents.kt:560–575`). A codebase-wide search finds
**no `liveRegion` anywhere in `app/src/main/java`**. The strip cells
and imprint are likewise unannounced.

**What that means today:**
- The transient is **safe by omission**: no state that dies in 300 ms —
  or 500 ms, or ever — is announced, because nothing is announced. The
  brief's hard requirement ("a screen reader must not announce a state
  that dies 300 ms later") is met, accidentally and completely.
- But the **settled** verdict is equally silent: on a dead pipeline,
  `BLOCKING IS OFF — TAP TO FIX` stands forever and a TalkBack user is
  never told; they must navigate onto the slot (where, to its credit,
  it *is* a labelled button) or the strip to learn the guard is dead.
  For the one sentence in the app whose entire job is "the guard is
  dead, here is the door," that is a real miss (Charge F-4).

**Ruling — do NOT simply add `LiveRegionMode.Polite` to the slot.**
Naively doing so re-opens the transient wound from the other side: the
slot's `displayed` state is set **immediately on appearance**
(`LaunchedEffect(sentence)` — the 500 ms stability beat debounces only
*disappearance*), so a polite region would announce §1.4's false OFF in
full before it dies. The correct construction, priced small (≈¼
builder-day, this composable only):

1. Announce from a **stability-gated** copy of the sentence: a sentence
   becomes announceable only after it has stood for the same 500 ms
   the hand-back beat already uses (one shared constant, one law:
   *nothing is said — in ink or in voice — until it has survived the
   beat*). Appearance keeps its instant ink (sighted users get the
   door immediately); only the *announcement* waits.
2. `LiveRegionMode.Polite`, never Assertive — a cold-open verdict is
   status, not interruption; Assertive is reserved for nothing on this
   screen today and stays that way.
3. The LIVE landing announces **nothing** (empty slot, nothing to
   say) — TalkBack's healthy cold open is as silent as the sighted
   one. Symmetry is the design.
4. The strip's density flips stay unannounced: three cells changing
   tone in one frame is print, and announcing BLOCK ON at every cold
   open would be the clutter argument again, in audio.

Until (1) lands, the current silence is the lesser evil and must not
be "fixed" by the naive one-liner.

---

## 5. The RETRY tap dies — what else lost its meaning?

**Audit of everything the fog sentence's tap used to do, and its fate:**

| Gesture / function | Before | After the cure | Verdict |
|---|---|---|---|
| `RETRY_CHECK` tap → `refreshDeckWarrant()` (re-read the wire) | The fog's only verb | **Function fully automated, gesture dead.** The re-read now happens on *every* WatchStore emission (VM collector → `refreshDeckWarrant()`, `MonolithViewModel.kt` snapshots collector), on the grant sweep (Deck entry / resume / FIX return), and print freshness is re-judged every 5 s by the mono ticker. No producer of `RETRY_CHECK` remains; the Deck handler (`TactileDeckScreen.kt` slot `onAction`, `REFUSAL` branch) and the `MainActivity.kt:691` wiring are **unreachable dead code** (Charge F-5, hygiene only). | ✅ Nothing he relies on was removed — the machine re-reads itself faster than his tap ever did. |
| Warrant re-read at the moment it matters — the slide's gate | Read at DOWN/gate | **Unchanged and fresher than RETRY ever was:** `armWarrantNow()` synchronous read at DOWN, gate, every MOVE, latch (`BlockitCoreViews.kt:1736`). | ✅ Intact. |
| FIX doors | `FIX_ACCESSIBILITY` / `FIX_USAGE` on their sentences | **Alive and now truthful:** they are the *only* actions the cured table produces, each wired to the pane that owns the lack (`TactileDeckScreen.kt` slot `onAction`; UNSET-usage still reroutes RETRY→FIX logic vestigially, harmless). | ✅ Intact — and the OFF door now actually rebinds/proves, per the cure doc. |
| Hand-back tick (recovery sound) | Fired when a standing sentence cleared | **Alive** (`playHandBackTick`), reachable via genuine FIX returns. (Its flicker-path firing is F-3.) | ✅ Intact. |
| Seam marks as a re-read surface | `BIND HELD · UNSEEN / PROOF: …` on the Deck | Already evicted by issue #6 F8 *before* this cure (`stampLeft/Right = null`); their receiving home is PARK's board, which re-reads via the same sweep triggers. **Not a loss attributable to this cure** — but BINDUNG should note the Deck now has *no* residency-tense print at all: "THIS VISIT" vs "UNSEEN" is invisible on the screen the scenario is about. The strip's UNKNOWN densities carry that information in tone, not words. Accepted (words would be clutter; tone is the house dialect) — flagged so the loss is a decision, not an accident. | ⚠️ Pre-existing, recorded. |
| `CAN'T CHECK` as a concept | Standing fog sentence | **Deleted, and nothing else used it as a signal:** the cascade's jam/proving sentences (his-act states) are untouched; the stale-read path still empties the slot by the UNA2-03 law; the Shield sentence is a different screen's table. | ✅ Clean deletion. |

**Conclusion for Q5:** the RETRY tap was the only death, and it died
because its job was automated out from under it — probe + poke +
collector now re-read the wire continuously. No other Deck gesture
lost meaning in this cure.

---

## 6. Charges against the landed implementation

**F-1 — HIGH — The grace covers the wrong race; a healthy phone can
still be told BLOCKING IS OFF.**
`MonolithComponentLogic.kt:246–247`: the `!warrant.a11yBound` row
returns `BLOCKING IS OFF — TAP TO FIX` with no residency-age test,
while `WatchStore.deriveWarrant` maps every unbound read to `OFF` —
so the cold open's *most likely* transient (service binding after the
Deck composes; named in `WatchStore.kt`'s own `noteResidencyStart`
doc) gets **zero** of the 4 s grace that the bound-but-unstamped race
gets. Felt result (§1.4): instant false OFF + BLOCK OFF in the strip,
held ≥500 ms by the slot's stability beat, dismissed with the
hand-back tick. The brief's promise — "4 s silent grace, then either
nothing or OFF" — is kept for one race and broken for the other.
*Smallest cure (for BINDUNG to price, not built here):* give the
unbound row the same residency-age test as the unstamped row
(silence while `age ≤ RESIDENCY_GRACE_MS`, OFF past it). Admitted
trade: a genuinely *disabled* service then waits the grace for its
sentence — the strip already prints BLOCK OFF (FAULT) from t = 0 in
that case, so the fact is on the glass instantly and only the
*sentence + door* wait, which is the same trade §2 already defends.
If BINDUNG judges disabled-vs-binding distinguishable at t = 0 (e.g.
via the Settings secure string the substrate retired), file the
evidence; on the code as read, it is not.

**F-2 — MEDIUM — The 4 s grace lands at ≈5 s on the glass.**
`TactileDeckScreen.kt:369–376`: the `nowMono` fed to
`refusalSentenceFor(deckWarrant, nowMono)` advances only on a 5 s
ticker. The grace boundary (4000 ms) is therefore first *observed* at
the ≈5 s tick in the typical alignment (ticker and residency born
together at Deck entry), and the verdict's arrival is quantized to
that grid rather than the law's number. Spec says 4 s; glass says ~5.
*Cure options:* judge the grace against a frame/immediate clock at
the slot call site, or add a one-shot delay keyed to
`residencyStartMono + RESIDENCY_GRACE_MS`. Small.

**F-3 — LOW — The hand-back tick can fire as a false-alarm reward.**
`MonolithCustomComponents.kt:554–562` + `MonolithViewModel.kt:2489`:
any displayed sentence that clears collects the 5 ms tick, including
F-1's never-true OFF. Consequence of F-1; dies with it. No separate
fix.

**F-4 — MEDIUM — The settled OFF verdict is never announced to
TalkBack.** No `liveRegion` exists anywhere in the app (§4). The
transient requirement is met by omission; the settled-state duty is
unmet. Cure as ruled in §4 (stability-gated polite region), **not**
the naive one-liner, which would announce F-1's transient in full.

**F-5 — LOW — Dead RETRY machinery left standing.**
`RefusalAction.RETRY_CHECK` (`MonolithComponentLogic.kt:178`), the
Deck slot's `RETRY_CHECK` branch with its UNSET reroute
(`TactileDeckScreen.kt`, slot `onAction`), `onRetryBlockingCheck`
plumbing through `MainActivity.kt:691–692` → `refreshDeckWarrant`:
all unreachable now that no sentence produces the action. Harmless
at runtime, but the next hand will read a door that no wall holds.
Delete or mark retired in the cure's own cleanup pass.

**Explicit non-charges (checked, cleared):** no 260 ms re-strike on
the Deck strip/imprint (§1.3 — one-frame print is correct); no sound
or haptic at proof landing (§3 — silence is the law-abiding state);
no proving sentence in the grace (§2 — silence defended); the
imprint's pop-in without the Strike (seal signature not spent);
arming never detained by the grace (slide reads the wire itself).

---

## 7. The spec, in one breath (for BINDUNG's merge sheet)

- **Healthy:** t=0 slot empty, strip UNKNOWN, imprint empty → probe
  lands <~300 ms → one frame: BLOCK ON, densities PASS, imprint
  tenant in → nothing said, nothing sounded, nothing struck, ever.
  *Conditional on F-1's cure for the unbound-at-open sub-case.*
- **Dead (bound-but-blind):** identical glass for the grace →
  `BLOCKING IS OFF — TAP TO FIX` as instant print at grace end
  (≈5 s as landed, 4 s as specced — F-2), standing, with a working
  door. No proving sentence at any point.
- **Dead (unbound/disabled):** OFF sentence + BLOCK OFF from the
  first read; under F-1's cure the *sentence* joins the grace while
  the strip's OFF stands from t=0.
- **Sound/haptic:** none at landing, none in the grace; the Throw
  stays the seal's alone; the hand-back tick only after a *true*
  sentence he fixed.
- **TalkBack:** nothing announced until a sentence has survived the
  500 ms beat; then Polite, once; LIVE never announced.

---

## Efficiency account (standing order — artefacts, idling, waste)

- **Artefacts:** this file (`studio/cells/issue-7-coldstart/feel-1.md`)
  + signed comment on issue #7. Read-only on code; the only commit is
  this file.
- **Method cost:** 9 source files read end-to-end or at the load-
  bearing hunks + 4 targeted greps (liveRegion, RETRY producers,
  re-strike sites, warrant wiring). No compiles, no test runs, no
  browser, no connector (gh only, per standing law).
- **Idling:** none — no waiting on other seats; every question in the
  brief was answerable from the landed tree.
- **Admitted waste / near-misses:** one early grep chased the seam on
  the Deck before the F8 eviction (`stampLeft = null`) surfaced —
  ~1 call. The brief's seam/re-strike premises described Shield
  surfaces; establishing that cost two greps and is itself reported
  above as a finding, so the spend converted to evidence.
- **What I would do next, unassigned:** (a) the same second-by-second
  treatment for the *Shield* sentence's cold entry, where FORM's
  re-strike actually lives and the grace now also applies by table
  sharing — does Shield inherit F-1? (b) an on-device timing capture
  protocol for BINDUNG's phone pass: cold-open × {bound, late-bind,
  disabled} with stopwatch frames, to verify F-2's ≈5 s landing
  against real tick alignment.

— FEEL-1, FEEL SPECIALIST, cold-start cure cell — Oct 30, 2026. DECLARED (read-only pass; nothing compiled, per cell law).
