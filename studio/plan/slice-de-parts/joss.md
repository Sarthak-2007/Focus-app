# JOSS — Slice D/E joint: THE OFF-STOP (0.65) as a workflow

*Joss — Wild, seat 20, kitchens (workflow discipline, pure efficiency and
ergonomics). Re-seated by Marlowe. Joint: the OFF-STOP under Live Wire
(Ines §3) — the clamp, the moment, mid-drag death, and recovery, specced
as one kitchen sequence against the real code. Law sources: REGISTER
(OFF-STOP 0.65 = gate −0.03), BEYOND-deck W4 interlock passage,
Ines §3 (Live Wire), Fen's seat-park / return-spring filings, Rosa's
A2 split. No code edited; no gradle run.*

Kitchen framing, one line, because it decides every answer below: **a
stop is a wall with an address, not a suggestion with a slope — and
once the pass refuses a ticket, the refusal is finished by the
kitchen, not negotiated with the waiter still standing there.**

---

## 1. Verdict

**ADOPT the registered law exactly as filed — hard stop at 0.65,
dull knock 10ms @ 0.40, sentence, 300ms hold, spring home — with six
workflow amendments (A1–A6) that complete behaviour W4 never
specified.** No registered number moves: GATE 0.68 · CATCH 0.84 ·
OFF-STOP 0.65 · TRAVEL 180dp · knock 10ms @ 0.40 · hold 300ms.

The amendments, in brief (full spec §3):

- **A1 — Overshoot is discarded, never stored.** The clamp is
  `displayed = min(raw, limit)` recomputed per frame. Finger travel
  past 0.65 leaves no balance the spring-back or a return drag must
  "consume." A pan stop does not accumulate.
- **A2 — Refusal is a machine sequence, not a touch state.** Once it
  starts, it runs to completion (retreat if needed → knock → 300ms
  hold → spring home) regardless of what the finger does. The touch
  that triggered it is dead from the sequence's first frame.
- **A3 — Mid-drag death above the stop retreats under power:**
  displayed cap travels to 0.65 over **160ms, Curve A, no
  overshoot**; the knock fires at *arrival*, not at the death frame.
- **A4 — The limit is stateless per frame until it isn't:**
  `limit(frame) = live ? full : 0.65`, re-read every MOVE. Revival
  before sequence-start restores full travel *in the same press*;
  revival after sequence-start is honoured only by the *next* press.
- **A5 — Frame order is law:** read wire → set limit → clamp →
  ticks/commit. The gate tick and the commit check only ever see the
  clamped, displayed fraction and that frame's verdict.
- **A6 — Spring home is the universal return spring**
  (dampingRatio 0.78, stiffness 260, ~320ms — Fen), **not** Cato's
  seat spring (400/16, 110ms), which only ever travels *toward* the
  seat, and not today's 280ms overshoot animator.

One fork for Ren (§7): the knock's amplitude — filed law 10ms @ 0.40
vs Zug's refusal floor (≥12ms @ 0.70) and Reyer's valence-parity
attack. The clamp machinery is amplitude-independent; settle the
voice before the slide is built, not after his hand has learned it.

---

## 2. Against the real code (file:line)

All paths `Focus-app/app/src/main/java/com/example/`.

**The drag today — `ui/views/BlockitCoreViews.kt`, `SlideToStartView`
(class at :1046):**

- `onTouchEvent` at :1153. DOWN (:1159–1167): cancels the spring,
  sets `isDraggingThumb`, fires `fireFeel(4L)`, and calls
  `updateDrag(event.x, …)` — **no thumb hit-test; the cap teleports
  to the finger.** MOVE (:1168–1173) calls `updateDrag` while
  dragging. UP/CANCEL (:1174–1194): commit is **release-only** —
  `dragFraction >= 0.82f` at lift sets `dragFraction = 1f`, plays
  the seal, invokes `onSlideCommitted`, then **springs back to 0
  even on success** (:1177–1189). Every one of those facts is
  superseded by W4/Live Wire (commit = 0.84 crossing *under
  contact*; powered seat; no return on success) — noted here only
  because the OFF-STOP must be built into the replacement drag, not
  bolted onto this one.
- `updateDrag` at :1198–1212 — **the clamp site.** Today:
  `newFrac = ((touchX − inset − thumbW·0.5f) / maxTravel).coerceIn(0f, 1f)`
  (:1199), assigned straight to `dragFraction`; gate bookkeeping
  compares against **0.82f** (:1200, :1205 — the dead coordinate;
  REGISTER's gate is 0.68, catch 0.84); tick buckets at :1207–1211.
  There is **no limit parameter anywhere** — the travel ceiling is
  the literal `1f` in `coerceIn`. The OFF-STOP is one change to this
  function's contract: ceiling becomes a field, and the verdict read
  happens before the clamp (A5).
- `animateSpringBack` at :1214–1225: `ValueAnimator`,
  `MonolithCurves.DURATION_280` (280ms,
  `ui/theme/MonolithCurves.kt:20`) with
  `MonolithCurves.MechanicalOvershoot` (OvershootInterpolator 1.25,
  `MonolithCurves.kt:28`). **This is neither registered spring.**
  Cato's seat spring (400/16, 110ms — REGISTER) and Fen's universal
  return spring (0.78/260, ~320ms — Fen's file, the "not committed"
  voice) both have to be built; the refusal path uses the universal
  one (A6).

**The wire today — there isn't one:**

- `data/MonolithRepository.kt:525–538` `isAccessibilityEnabled()`:
  reads `Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES` and checks
  the string *contains the package name*. It proves a toggle was
  once flipped — Ines §3's diagnosis, confirmed in code. This is
  the read `liveWarrant()` replaces *on the arm path* (it stays for
  Guard display). Its synchronous neighbours are the wire's other
  legs: `hasUsageStatsPermission()` at :493–515 (AppOps, synchronous).
- The registries Ines §3 requires **do not exist yet**:
  `service/MonolithAccessibilityService.kt` has no companion
  instance and no `onServiceConnected` override at all;
  `onAccessibilityEvent` (:39) launches a coroutine whose first acts
  are `getActiveSessionSnapshot() ?: return@launch` and
  `if (!activeSession.isActive) return@launch` — `rootInActiveWindow`
  is first acquired at :51, *after* the no-session early return, so
  today the pipeline stamps nothing pre-session (Ines cited this as
  :48–58 in her numbering; same code). `service/MonolithShieldService.kt`:
  `onBind` returns `null` (:178), monitor loop polls on
  `delay(2000L)` (:133), companion at :186 — no instance, no
  `protecting` flag. Priced in Ines's §3, not re-priced here (§5).
- The only pre-arm check today runs *after* the paid gesture:
  `ui/MonolithViewModel.kt:879–887` `requestArmWithPreFlightCheck()`
  shows a modal when the Settings string says off; `armMonolithSession`
  (:892–907) plays `playHarmonicSeal` *before* the coroutine that
  inserts the session. Under Live Wire both facts die on the arm
  path (verdict at DOWN, in the hand; seal voice at t=0 only) —
  Ines's joint; my joint only consumes the verdict.

**The knock's voice today — the nearest existing pattern:**

- `fireFeel(hapticMs, freqHz)` at `BlockitCoreViews.kt:204–227` is a
  *canned ladder*, not a (ms, amplitude) voice: 1L →
  `playRotaryDetent`, 4L → `playMechanicalLatch(true)`, 8L →
  `playFirmClick()`, **else → `playCompletionPulse()`**. There is no
  bucket for 10ms @ 0.40 — and `fireFeel(10L)` would play the
  *completion* voice (`service/TactileSoundEngine.kt:360–372`:
  waveform [0,10,24,10,24,14]ms @ [0,175,0,125,0,80], 185Hz, gain
  0.68) for a refusal. The view's own comment at :1181–1184 already
  names this sin (completion sound spent on starting). **The refusal
  knock must never go through `fireFeel`.**
- The one parametric voice in the codebase is
  `fireWeightedKnock(durationMs, amplitude01, pitchHz)` at
  `BlockitCoreViews.kt:239–252` →
  `TactileSoundEngine.playSeatKnock` at `TactileSoundEngine.kt:294–310`:
  amplitude maps linearly (`0.40 → 102/255`), duration coerced to
  6–32ms, waveform `[0, strike, strike/2] @ [0, amp, amp·0.35]`,
  transient gain `0.22 + 0.3·amplitude01` (= 0.34 at 0.40), pitch
  clamped 160–420Hz. **Spec: the refusal knock is
  `fireWeightedKnock(10, 0.40f, 220f)`** — 10ms primary strike at
  0.40, 220Hz (below the seat knock's 300Hz default; dull = low,
  short, tailed at 0.35 — Fen's "dull" rendered with the only
  honest dials the engine owns). No engine change. One honest
  residue: the waveform's tail makes the full envelope 15ms for a
  "10ms" spec — the 10ms names the primary strike, as it does in
  every filed use. Device-check item (§6).

---

## 3. Amended spec — the OFF-STOP, exact

State added to the slide view, per attempt (an attempt = one DOWN →
UP/CANCEL lifecycle): `travelLimit: Float` (1.0 or 0.65),
`refusalFired: Boolean` (reset at DOWN), and a refusal sequencer
with four phases: `RETREAT → HOLD → SPRING → DONE` (RETREAT skipped
when the cap is already at the stop). The wire verdict is obtained
from a provider the host/VM injects (`liveWarrant()` under the
hood); the view owns no evidence logic.

### (a) THE CLAMP — hard stop, overshoot discarded

- **Hard clamp.** On every MOVE (and on DOWN):
  `raw = ((touchX − inset − thumbW/2) / maxTravel).coerceIn(0f, 1f)`;
  `displayed = min(raw, travelLimit)`. When the wire is dead,
  `travelLimit = 0.65` and the cap stops there. Finger travel past
  the stop changes nothing: no creep, no compression, no stored
  overshoot (A1).
- **Resistance asymptote: rejected, in the open.** The DAMPED zone
  (0.68–0.84) already owns resistance — Fen's lag spring
  (dampingRatio 1.0, stiffness 900, drawn lag ≤6dp). The OFF-STOP
  sits 0.03 *below* the gate: at 180dp travel that gap is **5.4dp**.
  Any softness at the stop lets a hard push creep across those
  5.4dp, tick a gate that must never tick on a corpse, and blur the
  one distinction the A2 split exists to keep — refusal (0.65,
  proven dead) vs resistance (0.68+, alive but weighted). A stop
  that yields under pressure is a suggestion. Hard clamp.
- **Return direction:** if the finger drags back below the stop
  *before* the refusal sequence has started (possible only in the
  same frame race — see (b); in practice the sequence starts on the
  clamp frame, so this is the pre-clamp drag only), the cap follows
  1:1 with no dead band — because nothing was stored, there is
  nothing to unwind. This is the kitchen test the asymptote and the
  overshoot-bank both fail: the cap's position is a pure function
  of the finger and the limit, every frame, until the sequence or
  the latch takes the cap away from the finger entirely.
- **Rendering at the stop:** the cap keeps its FREE-zone rendering.
  No gate tick (the gate keys off *displayed* fraction — A5 — and
  displayed never reaches 0.68), no damped grain, **no COMMIT
  orange** (F-ORANGE: orange is a commit fill; a refused cap has
  committed nothing). The stop is felt (knock) and read (sentence),
  never coloured.

### (b) THE MOMENT — refusal fires at the clamp's first frame

Frame F0 = the first frame on which `raw ≥ 0.65` while the wire
verdict for that frame is DEAD. On F0, atomically:

> **AMENDMENT (Oct 29, Marlowe, on Ren's ruling —
> mail/marlowe/1115): the DOWN-frame F0 is struck, in the open.**
> This paragraph once claimed a DOWN landing past the stop is
> itself F0 ("F0 is then the DOWN frame", with the knock
> replacing the pickup tick). That claim died with the
> teleporting drag it was specced against: Rhea's grab offset is
> computed from the DOWN event itself, so the fraction readable
> at DOWN is identically the cap's standing fraction (0 at
> rest) for any touch x — a DOWN-frame F0 is mathematically
> unreachable, and the branch built for it in
> `beginAttempt` was dead code claiming a behaviour that
> cannot happen. The branch is deleted; the strike is
> recorded in the code at the deletion site. **F0 is a MOVE
> frame on the drag path; on the tap path its F0 is the UP
> frame (§(b) tail / A2). A far-right DOWN on a dead wire is
> an ordinary grab: the finger drags, and the MOVE-frame F0
> catches it at the stop.** The §4 code-sites table row 2
> ("DOWN-past-stop = F0") is struck by the same amendment.
> What does NOT change: the atomic F0 sequence below, the
> knock, the hold, the spring, A2's refused-dead life.

1. `displayed` pins at **0.65**; `refusalFired = true`.
2. **Knock:** `fireWeightedKnock(10, 0.40f, 220f)` — once per
   attempt, guaranteed by `refusalFired`. (Knock amplitude as
   later re-ruled at the build gate — PA-S1-17 raised the
   shipped refusal knock to 12ms @ 0.70 at Zug's perceptibility
   floor; the value printed here is the original filing.)
3. **Sentence:** `BLOCKING IS OFF — NOTHING WILL BE HELD.` —
   Atkinson 700 15sp, glyph ink, in Rosa's reserved 22dp slot 8dp
   below the track (F-TENANCY: interlock sentences live in the
   below-track slot, not chrome/imprint; the slot is reserved
   always, so the refusal moves nothing by a single dp). The
   chrome standing line (`…— TAP TO FIX`) is a different tenant at
   a different moment and is not this joint's to fire.
4. **The 300ms hold begins.** The hold holds three things: the cap
   at displayed 0.65, the sentence in its slot, and the
   input-dead state. It is a *time* hold, not a contact hold:
   finger release during the hold does not shorten it, finger
   movement during the hold does not move the cap, a second
   pointer during the hold is ignored (not a buttonhole abort —
   the verdict already ended this gesture; there is nothing left
   to abort).

At F0 + 300ms: **spring home** — cap animates 0.65 → 0 on the
**universal return spring (dampingRatio 0.78, stiffness 260,
~320ms)** (A6). This is the app's one "not committed" voice — the
same spring Fen files for a drag released early and a tap abort —
*not* Cato's seat spring (400/16, 110ms), which is the SEAT spring
and only ever travels toward the seat; giving a refusal the seat's
snap would lend it the seal's authority in the hand. (Code note:
today's `animateSpringBack` — 280ms + OvershootInterpolator 1.25 —
is neither spring and does not survive into this path.)

**The rest of that touch's life (A2):** from F0 the touch is
**REFUSED-DEAD**. MOVE events are read for nothing — no fraction
updates, no ticks, no wire re-reads that could revive it. UP/CANCEL
ends the touch silently; if the spring is still running it
continues to 0 uninterrupted. A DOWN arriving while a refused
touch is still active is ignored. **A new attempt begins only at a
DOWN after the previous touch's UP/CANCEL**, and it takes a fresh
wire read at that DOWN, per Ines §3. The tap path inherits this
whole paragraph unchanged, with its F0 at the UP frame: wire dead
at DOWN → on UP (≥90ms brush floor still applies) the knock and
sentence fire with the cap at its press pose (it never travelled),
the 300ms hold holds the sentence and the dead state, and there is
no spring because the cap is already home.

### (c) MID-DRAG DEATH — collapse, retreat, and the exact boundary

**Detection.** The view re-reads the verdict on **every MOVE
frame** (A5 frame order: the read precedes the clamp), and the VM's
bind-callback additionally pushes a limit collapse to the view on
the next vsync when the registry clears with no MOVE in flight.
Per-MOVE re-read is the correctness leg (the verdict consulted is
never older than the frame that uses it); the callback is only
promptness for a finger holding still — a still finger commits
nothing, so no correctness rides on it. Amendment on Ines's read
for cost safety: the *volatile legs* (a11y instance + residency
stamp + shield `protecting`) are read per MOVE; the AppOps leg
(`hasUsageStatsPermission`, :493) is read at DOWN and at the gate
crossing only — it is the one leg whose per-frame cost is
unproven on device (§6), and a usage-access revocation mid-drag
that left both services bound is not a morning this app has ever
met. If device timing shows the AppOps read inside the ≤2ms
budget at drag rates, restore the full read per MOVE — the spec's
intent is Ines's four legs every frame; the split is a priced
concession, flagged, not buried.

**Collapse cases, by displayed fraction at the death frame D:**

- **Displayed < 0.65:** nothing visible happens. The limit is now
  0.65; the drag continues under it and (a)/(b) govern from here.
  No knock yet — no stop has been reached. (If he drags home and
  lifts without ever touching 0.65, the attempt ends as an
  ordinary uncommitted release on the universal spring, plus the
  sentence — the standing chrome truth already names the corpse;
  the interlock sentence fires only at the stop. One refusal, one
  rendering.)
- **0.65 ≤ displayed < 0.84 (the DAMPED zone — committed to
  nothing; commit is 0.84 under contact): RETREAT (A3).** The
  refusal sequence starts at D, but the knock waits for the stop.
  The cap retreats under power from its displayed position to
  **0.65 over 160ms on Curve A** (`MonolithCurves.CurveA`,
  PathInterpolator 0.2/0/0/1.0 — already in the codebase), **no
  overshoot** (overshoot would dip *below* the stop and draw the
  refusal position as a place the cap passes through). During the
  retreat the touch is already REFUSED-DEAD for commitment
  purposes — the commit check is gated on this frame's verdict
  (A5), so a retreating cap can never cross-commit — but the
  finger retains one honest power: if `raw` falls below the
  retreating displayed position, the retreat animator cancels and
  the cap rejoins the finger (he is retreating faster than the
  machine; the hand wins). If the finger lifts during the retreat,
  the retreat *continues* to 0.65 without it — the sequence is the
  machine's now (A2). On the arrival frame at 0.65: knock
  (10ms @ 0.40, as (b)), sentence, 300ms hold, universal spring
  home — identical tail to (b). Worst case death→knock latency:
  1 frame (collapse) + 160ms (retreat from just under the catch,
  0.189 of travel ≈ 34dp) — inside any honest accounting of the
  gesture, and the delay is *felt truth*: the knock lands when the
  stop is reached, not while the cap is still visibly travelling.
  A snap was rejected: it would teleport the cap up to 34dp out
  from under his thumb, breaking the 1:1 / ≤6dp-lag law by five
  times its allowance in a single frame — the hand would feel the
  machine lie about where his finger is.
- **Displayed ≥ 0.84 with that frame's verdict LIVE — THE
  BOUNDARY.** The latch frame L = the first frame on which
  displayed fraction ≥ 0.84, contact held, and *that frame's*
  wire read is LIVE. **At L, enforcement dispatches** (Opal's
  dispatch law: commands at latch, on the latch-time verdict) and
  **the OFF-STOP is dead for this attempt** — the limit variable
  is ignored from L onward, the powered seat (Cato's spring,
  110ms) runs, and a wire death arriving at L+1 or later — during
  the seat, during PROVING, at any point after dispatch — **does
  not stop, pause, or un-dispatch anything at this joint.** The
  seal proceeds on the latch-time verdict; post-latch death is
  the cascade's problem (Opal's joint: PROVING → SEALED-RUNNING
  gating, per-link CONFIRMED, t=0 at the proof-seal frame; a link
  found dead there is a chain-jam under cascade law, refused from
  the seat under Fen's seated-refusal grammar — a different
  refusal, at a different coordinate, not this stop). Stated as
  the law the brief asked for: **the OFF-STOP guards the road to
  the latch; it has no jurisdiction past it.** The symmetric edge
  is equally exact: if the death frame D *is* the would-be latch
  frame (displayed crosses 0.84 on the same MOVE whose read is
  DEAD), the crossing is void — A5's order (read before commit)
  makes the verdict win the tie, the cap is in the retreat case
  above, and no latch dispatches. A verdict is read-time truth:
  a latch whose frame read LIVE is lawful even if the service
  dies microseconds later; that death belongs to the cascade.

### (d) RECOVERY within one press — honesty cuts both ways

- **Revival before sequence-start: full recovery, same press.**
  The limit is a pure function of the current verdict on every
  frame until the refusal sequence starts or the latch fires
  (A4). Wire dead at DOWN, finger at 0.50, service rebinds → the
  next MOVE reads LIVE, limit is full, and the same press may
  drag through the gate to a lawful latch at 0.84. No re-press,
  no penalty, no memory. A limit that only ever fell within a
  press would be a ratchet the facts don't support — the wire is
  alive *now*, and the slide's whole post-Live-Wire discipline is
  to believe the present frame.
- **Revival after sequence-start: no recovery in this press.**
  Once F0 (or D, in the retreat case) has fired, the knock has
  sounded and the sentence has rendered — both are facts he has
  perceived. Un-firing them because the service rebound during
  the 300ms hold would retroactively make the sentence a lie
  (TRUTH once), and a flapping service (bind/unbind cycling)
  would strobe the cap between the stop and free travel. The
  sequence completes; revival is honoured at the next DOWN's
  fresh read, which is at most one lift-and-press away. The line
  is bright and stateable in one sentence: **the wire may change
  its mind freely until the machine has spoken; after it has
  spoken, only a new press asks again.**

---

## 4. Code sites

| # | Site | Change |
|---|---|---|
| 1 | `ui/views/BlockitCoreViews.kt` — `SlideToStartView.updateDrag` (:1198) | Ceiling becomes `travelLimit` field; verdict read → clamp → tick/commit order (A5); gate comparison moves 0.82f → registered 0.68/0.84 in the replacement drag this is built into |
| 2 | `ui/views/BlockitCoreViews.kt` — `SlideToStartView.onTouchEvent` (:1153) | DOWN: fresh provider read, `refusalFired` reset, ~~DOWN-past-stop = F0~~ (struck Oct 29 — see the §(b) amendment; F0 is a MOVE/UP frame). MOVE: per-MOVE volatile-leg read. UP/CANCEL: REFUSED-DEAD handling (silent, spring uninterrupted); DOWN-while-refused ignored |
| 3 | `ui/views/BlockitCoreViews.kt` — `SlideToStartView` (new members) | `travelLimit` + wire-provider lambda + refusal sequencer (RETREAT/HOLD/SPRING) + retreat animator (Curve A, 160ms) + universal-spring home animator replacing `animateSpringBack` (:1214) on this path + `onRefusal` callback to host |
| 4 | `ui/views/BlockitCoreViews.kt` — knock call site (new, in view) | `fireWeightedKnock(10, 0.40f, 220f)` (:239) at sequence arrival — **not** `fireFeel` (:204) |
| 5 | `ui/MonolithViewModel.kt` | Exposes verdict provider + refusal-sentence state; bind-callback pushes limit collapse (next-vsync) to the view; pre-flight modal path (:879) already dies under Ines §3 — this joint consumes, doesn't re-decide |
| 6 | `ui/screens/TactileDeckScreen.kt` — slide host (:489–499) | Wires provider + `onRefusal` into the view; renders the sentence in the reserved 22dp below-track slot (slot furniture itself is W4/Rosa-priced, §5 note) |
| — | `service/TactileSoundEngine.kt` | **No change.** `playSeatKnock` (:294) already speaks the knock |
| — | `data/MonolithRepository.kt` :525 | `isAccessibilityEnabled` untouched; superseded on the arm path by `liveWarrant()` (Ines §3's site, shared dependency) |

## 5. Price in lines

**This joint only** (the OFF-STOP as a workflow), gross, against the
code as read:

- View: limit field + provider + per-frame read/clamp reorder
  **~25** · refusal sequencer + dead-touch lifecycle **~70** ·
  retreat animator (160ms Curve A, finger-retake cancel) **~30** ·
  universal-spring home animator on this path **~25** · knock call
  + DOWN-frame replacement rule **~5** → **~155**
- VM: provider exposure + collapse push + refusal state **~20**
- Host: wiring + sentence state hand-off **~15**
- **Total ≈ 190 gross / ≈ 175 net, 3 files touched** (view, VM,
  host). Tests **~90** (clamp math, frame-order tie at 0.84,
  sequence timing, revival windows — view-animator tests are
  Robolectric/device-flavoured, §6).

**Not in this price, stated so nobody double-counts or under-counts:**
the registries + `liveWarrant()` (~130 of Ines §3's ≈665 gross B
total) are a *shared dependency* — this joint is unbuildable without
them and does not re-price them. The reserved 22dp sentence slot's
furniture sits in Ines's W4 table item 8 (~180, shared with the
imprint). The replacement drag itself (zones, damping, powered
seat) is the slide build's, not this joint's. Cross-check: Ines
priced W4's Park — both stops plus the UNKNOWN machine — at ~220;
the OFF-STOP alone, with UNKNOWN dead under Live Wire, landing at
~190 with a retreat the Park never had is consistent, not padded.

## 6. Risks / device-only unknowns

1. **The universal spring's implementation.** Fen's (0.78 / 260,
   ~320ms) is a physics spec; the codebase has no `SpringAnimation`
   usage — only `ValueAnimator` + interpolators
   (`MonolithCurves.kt`). Whether androidx.dynamicanimation is on
   the classpath is a build-file fact I was barred from chasing
   with gradle this run; fallback is a 320ms tween approximation,
   which will *not* feel identical under a moving start velocity.
   Settle at build, verify in the hand.
2. **Knock perceptibility.** 10ms @ 0.40 under a trembling 05:00
   thumb is exactly Zug's filed objection (Barriere's background
   tick needs ~10ms @ 0.55). The 15ms real envelope (strike + tail)
   may help or not. Device-only; see fork §7.
3. **"Dull" at 220Hz is a tuning claim, not a measurement.**
   `playSeatKnock`'s pitch clamp admits it; whether it reads dull
   against the seat knock's 300Hz on his device's motor/speaker is
   Ton's pass, on hardware.
4. **Per-MOVE read cost.** Volatile legs are free; the AppOps leg
   is the risk — hence the DOWN/gate split in §3(c), with the
   restore condition stated. If even the volatile + callback path
   janks at drag MOVE rates on his device, the fallback is
   gate-read + callback only (Ines's filed minimum), at the cost
   of collapse latency for above-stop deaths — measure, don't
   guess.
5. **Registry timing vs the boundary.** `onUnbind` clearing is
   asynchronous to the MOVE stream. The law is read-time truth
   (§3c): a frame that read LIVE was live *as read*. A death that
   lands between the read and the latch dispatch *within the same
   frame* is indistinguishable from a death at L+1 — both belong
   to the cascade. Accepted, stated, not papered over.
6. **Sequence survival.** The sequencer is view-side; if the view
   detaches mid-sequence (call, backgrounding), the sequence must
   cancel and the cap must reset on re-attach (the
   `isArmedState`-style reset at :1064 is the existing pattern);
   the sentence state lives in the VM and survives. Behaviour
   specced here in principle; the detach path is untested by
   construction until device.

## 7. Fork for Ren

**The refusal knock's amplitude — one fork, Ren's to settle before
the slide build, with Ton in the room.** Filed law (REGISTER/W4):
**10ms @ 0.40**. Filed attacks: Zug's refusal floor — no
refusal-class haptic below **12ms @ 0.70**, priced from Barriere's
measured tick; Reyer's valence parity — the dull knock dies
entirely, refusal re-voiced as the Incomplete Seal. **My
recommendation:** keep 10ms @ 0.40 *in this slice's spec* (it is
registered law and the machinery above is amplitude-independent —
one constant changes), but **do not let the slide reach his hand
before Ren settles the floor question with Ton on device.** My
kitchen vote, for the record: Zug is right on the only ground that
matters at 05:00 — a stop you cannot feel is not a stop, and the
sentence under the track is the channel least likely to be read by
a man whose thumb just got refused. If the floor wins, the change
is `fireWeightedKnock(14, 0.70f, 220f)` and one REGISTER line;
nothing else in this file moves. What must not happen is shipping
0.40 untested and retraining the hand later — feel is the one
retrofit this app cannot afford twice.

No other fork. The boundary in §3(c) is stated as law, not offered
as a question: post-latch death is Opal's cascade joint, and I have
mailed her the boundary line so her side of it is built to meet
mine.

## 8. Mail / wall note

Inbox at start: two unread letters — Pia (prime-glide dies; the
primed frame leaps, never arrives — noted, does not touch this
joint) and Aldo (chrome numerals fight on my 04:58 frame — noted,
not this joint's table). Both read and moved to `read/`, never
deleted; neither needed a reply from this seat on this task.
Sent: one letter to Marlowe (coordinator) filing this joint's
verdict + the §7 fork for routing to Ren/Ton; one letter to Opal
stating the latch boundary (her cascade owns everything from the
latch frame onward; my stop owns everything before it) so the two
sides are built to the same line. Wall: one signed post appended
(`## [08:12 IST] Joss — Wild, kitchens`) with the verdict, the six
amendments in one breath, and the fork named in the open.

— Joss, kitchens
