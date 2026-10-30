# BEYOND — DECK · Opal (Wild, machinery)
## Drawbar & Proof Pawl · Phase 3 GO-BEYOND

*Filed plan is the floor. My filed §1.4 split stands and is not
re-argued here: sled travel = INTENT, pawl drop = PROOF, and a machine
that rounds its own state up is lying. What follows does three jobs:
(1) names the one t=0 the merge condition demands, in machine terms
precise enough to build and audit against; (2) makes the proof FELT
without simulating one drop of it; (3) files the mechanism I held
back in Phase 1 as too much. ARM ≤10s · HOLD absolute · TRUTH once
are walls throughout. TAP CAN COMMIT is honoured: tap and slide run
the identical latch → proving → seal sequence below; only the latch's
trigger differs.*

---

## 1. NAME t=0 — THE PROOF-SEAL FRAME

### Plain words

A session does not begin when his thumb finishes. It begins when the
machine has *finished proving it did what he asked* — every app in
the loadout confirmed shut — and not one frame earlier. Everything
before that instant is a man giving an order and a machine checking
it can obey: real time in his life, zero time in the session. Four
layers each named a different frame as the start. Three of those
frames are real events and keep their jobs; they were just never the
birth of the session. Release is intent completing. Threshold is a
position passing. Seat is the latch closing. **The session is born
at proof.** One instant, named once, here.

### The definition (document of record — carry verbatim)

> **t=0 ≡ the PROOF-SEAL FRAME: the first vsync frame on which the
> session controller executes the transition `PROVING →
> SEALED-RUNNING`.**
>
> **What fires it:** the conjunction, evaluated in the controller
> (single source; the view never decides), of —
> (a) the final chain link's `CONFIRMED-SHUT` event has been
> received, i.e. every link in the snapshotted loadout chain is in
> state `CONFIRMED-SHUT`; **and**
> (b) the minimum arrival dwell has elapsed:
> `now ≥ latchFrameTime + 210ms` (Fen's 90ms latch snap + 120ms
> dwell, see re-timing below); **and**
> (c) the proof pawl's drop animation (90ms, started on the frame
> condition (a) first became true, or on the dwell-expiry frame if
> (a) came first) has reached its seated position.
> The frame satisfying all three is the seal frame. Conditions are
> conjunctive and ordered by whichever completes **last** — formally
> `t0 = max(lastConfirmTime, latchTime + 210ms, pawlSeatedTime)`,
> quantised up to the next vsync.
>
> **What is already true at that instant:** parameters snapshotted
> and frozen at latch (duration from the wheel, loadout from the
> seated cap — nothing downstream re-reads either); every chain link
> proven shut by a real check against the blocking stack
> (AccessibilityService presence + package state + ShieldService
> enforcement confirmation — never by elapsed time); Rosa's BLOCK
> check not failed (a BLOCK failure never reaches PROVING — her
> interlock stops the cap at 0.65, pre-latch, see below); the sled
> seated and held; the pawl physically rendered seated on the hub.
>
> **What becomes true at that instant, and only then:** the session
> record is created, with `startMonotonic = t0` sampled from the
> monotonic clock **inside the same controller callback** that flips
> the state (one sample, one callback — no layer may take its own);
> elapsed counting begins from that sample; the readout starts;
> the drawbar goes inert for the session's life; Wren's running-state
> transition (§1.5) may begin — its first frame **is** the seal
> frame, never earlier; Orson's seal sound may fire — its attack is
> scheduled **at** t0, never before it (see re-timing); the service
> log's `SEALED` event (Ledger, kept-tab rebuild) is written with
> timestamp t0.
>
> **What t=0 is not:** not pointer-up, not a travel fraction, not a
> threshold crossing, not the latch, not the end of any animation
> other than the pawl's own drop, not the seal *sound*. Elapsed-
> before-proof is never counted, never rounded in, never shown —
> this sentence from my seat file is the definition's corollary,
> not a second definition.

**Why proving is serial after the latch — and what runs parallel
instead (answering Fen's letter of this hour):** Rosa's readiness
*queries* (BLOCK / APPS / TIME) dispatch at touch-down, in parallel
with the drag — §2 Form A renders exactly that. But the cascade's
`CONFIRMED-SHUT` is not a query; it is proof *of a command*, and the
command may not be issued on uncommitted intent. Dispatching
enforcement at touch-down would mean apps actually shut while the
sled is at 40% — and a slide released below the latch would then
leave either (a) apps blocked with no session, a false state in the
world, or (b) an unblock commanded-and-assumed, i.e. my §3's
unproven release, one arm earlier. Serial proving after latch is
therefore structural, not an oversight: 0.6–1.1s typical is the
honest price of never acting on intent as if it were commitment,
and it is priced in the table below rather than hidden by moving
the work where the accounting can't see it. Where Fen and I agree:
if device measurement shows his readiness checks can lawfully
pre-prove more of the chain (e.g. a link whose enforcement state is
already shut and merely needs re-confirmation — a query, not a
command), that link's confirmation may be *carried* into PROVING,
shortening it without breaking the rule. The rule is about commands,
not about knowledge.

**Tap path:** a tap on the seated/commit control validates the same
preconditions the drag's travel validates (cap seated, wheel set,
BLOCK not failed), then fires the **same** latch event the drag's
seat frame fires. From the latch onward the state machine, the
dwell floor, the cascade, the pawl, and this t0 are byte-identical.
Tap buys back the drag's ~0.6–0.9s and nothing else; it never buys
back proving. A tap-seal that skipped the cascade would be a second
clock-zero in disguise and dies by this definition.

### How each layer re-times against it

| Layer | Their frame | What it actually is | Re-timed duty |
|---|---|---|---|
| **Rosa — crank-release** | pointer leaves the cap past 82%; spring completes travel in 110ms; "clock starts at the crank-release frame" | **t_intent-complete** — the last instant intent can change | Release past 82% triggers the assist spring; the spring's end is the **latch frame**, which dispatches the cascade (if not already dispatched at latch — it is dispatched at latch, only at latch). Her sentence "clock starts at crank-release" is **struck**; what survives of it, whole, is her real law underneath: *ceremony after the seal costs zero focus time* — Wren's transition and Rosa's own 190ms Deck settle now run **after t0, overlapped with a running clock**, exactly the protection she was buying. Her interlock is untouched and moves **earlier** in authority: BLOCK-fail stops the cap at 0.65, so a dead-blocking arm never latches, never proves, never seals — the interlock pulls the pawl by never letting it be asked to drop. |
| **Orson — threshold crossing** | travel ≈0.97; "blocking engages at threshold crossing, instantly"; 120ms scheduled silence (the breath), then the Throw, bolt at +30ms | **t_threshold** — a position event inside the gesture | Two honest corrections. (1) Blocking does **not** engage at threshold: at threshold nothing has been commanded to the stack; commands dispatch at latch, proofs return during PROVING. His arm-cost-zero claim for the sound layer survives (sound still adds 0ms — see below), his engagement claim does not. (2) The **breath moves**: the 120ms of scheduled silence is no longer a pre-seal delay inserted after a crossing — it is the **last 120ms of PROVING itself**, a window the engine clears of drag/grain sound in anticipation scheduled only once the chain has ≤2 links unconfirmed (if proving is shorter than 120ms, the breath is exactly as long as proving was — truncated, never padded; the seal is never delayed to complete a silence). The Throw's attack lands **at t0**; the bolt layer at **t0 + 30ms** as filed; the duration-sized gain (−8dB @ 25min → −5dB @ 180min) is computed from the **snapshotted** duration at latch, so it is ready at t0 with zero computation in the seal frame. |
| **Fen — post-dwell seal** | latch at 0.97 (90ms snap) + 120ms dwell, then seal | **the arrival floor** — a minimum, not a timestamp | Fen's dwell is absorbed as condition (b): the pawl **may not complete its drop** before `latch + 210ms`, however fast the cascade returns. On a fast morning (cascade done in <210ms — possible for a 2–3 app loadout) the dwell is the only thing standing between latch and seal, and the hand still feels arrival-before-commit, which was the dwell's entire purpose. On a normal morning (cascade 0.6–1.1s) the dwell expires invisibly inside proving and costs **0ms**. Fen loses the dwell as a *serial stage*; he keeps it as a *floor* — his beat survives on exactly the mornings it was designed for and taxes no others. His seal thock-pair is superseded by the single seal voice at t0 (Orson's Throw / Opal pawl tick treaty stands: one event, one voice — the pair's echo was a second rendering of one instant, and TRUTH-once applies to sound). |
| **Cato — seat frame** | arm callback fires on the garage seat frame, end of the 110ms assist spring over the final 8%; "never at a threshold crossing" | **the latch frame** | Cato's principle — *picture and event coincide; no frame where the app is armed but the zip looks open* — is kept and **split in two, honestly**: there are two events, so there are two coincidences. The seat frame fires `onLatch`: at that frame the pull is rendered seated in the garage **and** intent is snapshotted **and** the cascade dispatches — picture and event coincide. The seal frame fires `onSeal` (t0): at that frame the pawl is rendered seated, the tape fully meshed, **and** the session starts — picture and event coincide. What Cato loses is the word "arm" for the seat frame: the seat frame arms nothing, it *orders*. His garage, his 0.6× input scaling, his spring are untouched. His per-tooth haptics/sound keep their existing law (they render travel — intent — and stop at latch). |
| **Opal — pawl drop** | "session clock's zero is the proof pawl's drop"; proving 0.6–1.1s typical, 1.8s ceiling; elapsed-before-proof never counted | **t=0 itself** — refined | My own sentence needed one sharpening and gets it: the pawl's drop is a 90ms animation, and a 90ms event cannot be an instant. t0 is the drop's **completion frame** (pawl rendered seated), with the drop *starting* on the last-confirm frame. What I give up: the rhetorical simplicity of "the drop is zero" — zero is the drop's *end*. What I keep: everything else, including the cost — this definition makes the session start **later** than every other layer wanted, by the full proving duration, every single morning. That is not a defect to apologise for; it is the price of the clock never minting a minute the chain hasn't earned, priced openly in the ARM table below. |

### State machine (controller-owned, single source)

```
REST → ENGAGED → TRAVERSING → GATE(0.65, Rosa interlock point)
  → SEATED-LATCHED            [onLatch: snapshot params, dispatch cascade]
  → PROVING                   [chain RUNNING link n; dwell floor ticking]
  → SEALED-RUNNING            [t=0 — this §1 transition only]
Exits: SEATED-LATCHED → RELEASED (28dp reverse drag vs catch,
       also available in PROVING's first 300ms)
       PROVING → JAMMED (per-link HALTED from cascade §2.4:
       sled skews 12°, chain holds confirmed links, no record written)
       PROVING → RELEASED (reverse drag after 300ms? NO — after the
       first 300ms of PROVING the release is the JAMMED path only:
       if the chain halts he gets retry-link / release; a healthy
       chain mid-proving completes in ≤1.8s and is not interruptible
       by a stray swipe. Interruption window stated, not implied.)
```

### Failure paths

- **Chain HALTED:** no t0 ever occurs for that arm. No session
  record, no ghost minutes, no `SEALED` log event. Confirmed links
  hold their block; downstream links render OPEN (they are open).
  Exits per filed cascade: push the jammed sled square (retry that
  link only — on its CONFIRMED the chain resumes, and t0 remains
  defined exactly as above when the last link lands) or reverse-drag
  release (chain unwinds in reverse order at 40% of forward speed,
  sled to REST, nothing written as a session).
- **Permission dies mid-PROVING:** that link returns
  `JAMMED-NOPERM` — same HALTED path, margin line
  `〈APP〉 — PERMISSION OFF`.
- **Permission dies mid-SESSION (after t0):** t0 is not revised,
  the session is not un-sealed. The fact is held and stated once on
  the end-of-session receipt. A clock-zero that could be retroactively
  moved by a later fault would make every elapsed number provisional.
- **App process killed during PROVING:** on relaunch there is no
  session (none was created) and the chain state is re-derived from
  the stack, not restored from a saved PROVING flag — a restored
  "we were proving" would be a claim about a cascade that no longer
  exists. Sled renders at REST or SEATED per the *re-derived* state.
- **Wall-clock change during PROVING or session:** irrelevant —
  `startMonotonic` is monotonic; wall time is sampled at t0 for the
  receipt's printed time only.

### ARM accounting — honest table (this is the cohort standard; §10's ≈2.5s claim is superseded)

| Element | Typical | Ceiling | Notes |
|---|---|---|---|
| Cold open → Deck interactive, persisted state painted | 600–1,000ms | 1,500ms | last-known state, inert-until-true; no scan blocks paint |
| Wheel set | 0ms (preset by routine / unchanged) | 1,200ms | manual re-set only, rested-morning worst case |
| Loadout cap | 0ms (pre-seated, persisted) | 0ms on arm path | reseat is rested work, off this clock by construction |
| Drag + gate + latch (slide path) | 600–900ms | 1,400ms | 1:1 travel, gate click not a wall; tap path: ~80ms tap replaces this row |
| Latch snap + dwell floor (210ms) | 0ms added | 0ms added | runs **inside** proving, parallel, never serial (except sub-210ms cascade: then it *is* the remainder, ≤210ms total) |
| PROVING — series cascade, 7 links @ 60–150ms | 600–1,100ms | 1,800ms | per-link timeout 400ms; ceiling = timeouts resolving to HALTED, not to seal |
| Pawl drop animation | 0ms added | 90ms | starts on last-confirm frame; t0 is its completion — counted once, here, worst case only |
| **TOTAL cold-open → t0** | **≈3.5–4.5s** | **≈6.0s** | inside 10s with ≥4s margin; every ms is his hand or a real check — no ceremony tax |

### HOLD / TRUTH check

- **HOLD:** t0 introduces zero decisions. Everything contentious
  (interlock refusal, advisory fault, jam retry/release) resolves
  in PROVING, *before a session exists*. After t0 the definition
  itself is what makes HOLD auditable: a session exists from a
  named frame, so "mid-session" has a start to be measured from.
- **TRUTH:** one instant, one sample, one callback, one place in
  the document. The four rival frames are not deleted — they are
  *demoted to what they are* (intent, position, latch, floor), and
  none of them may print, sound, log, or count as a session start.
  Any future layer that needs a timestamp takes t0 or names its own
  event without calling it the seal.

### What this replaces, and why that loses

WILD-PLAN §1.4's composite ("Clock starts at the crank-release
frame" printed in the same element that waits for proof) and §10's
≈2.5s total. The composite loses because it is not a simplification
of the four definitions — it is all four at once, which means a
builder implementing it faithfully produces a clock that starts
before blocking is proven (Rosa/Orson reading) *and* a readout that
waits for proof (Opal reading) on different surfaces of the same
screen. §10's number loses because it omits proving entirely;
honestly re-priced the plan was always ≈6.1s worst case (Moss) —
inside budget, and it never needed the flattery.

### Strongest case against my own proposal

**It makes the app feel slower than three of its four layers
promised, and feel is not nothing at 03:30.** Under this definition
the readout sits at zero for 0.6–1.8s after the latch knock — the
loudest, most committed-feeling moment of the gesture — while the
machine visibly *works*. Rosa's crank-release zero existed precisely
to kill that gap: the driver turns the key and the engine note is
the reward, immediate. A tired man who has latched, heard the knock,
and watches a motionless zero for a full second may read PROVING as
*hang* — and a control read as hung gets jabbed, re-dragged, or
distrusted, and distrust at the centre gesture is the one failure
this app cannot afford. My answer is §2 below (the proving must be
*felt progressing*, in the hand, or this definition ships a dead
second) — but the honest residue stands: if §2's felt proof fails
on device, t0-at-proof keeps the truth and loses the man, and a
definition he routes around is worse than Rosa's cheerful rounding.
That is the bet, stated.

---

## 2. THE MECHANISM MADE BEAUTIFUL — PROOF IN THE HAND

### Plain words

Right now the cascade is plumbing: it happens somewhere under
Cato's tape, and the user learns it worked when the pawl drops —
a verdict delivered, not a process felt. But a real drawbar under
load *tells the hand what the load is doing* the whole way home.
So: let the final travel and the proving carry the cascade's real
state in the sled itself — as drag while he pulls, and as creep
while the machine pulls. When the chain is healthy the sled comes
home light and then walks itself the last few millimetres in
audible, feelable steps, one per proven app. When the chain stalls,
the sled stalls — in the hand, at the same dp it stalls on screen.
The beauty is not a finish applied to the mechanism. The beauty is
that the mechanism, rendered faithfully, *has* a gait — and a halt
has a silence you can feel in your thumb.

### The line (binding on this and every future rendering)

A rendered quantity is **a real signal** iff all four hold:
1. **Same-frame derivation** — it is computed from the controller's
   actual state on the frame it is rendered (chain counts, check
   states, monotonic timestamps), never from an animation clock,
   a progress tween, or a predicted schedule.
2. **Monotone & invertible in kind** — more proof never renders as
   less; a stalled input produces a stalled rendering, a reversed
   input a reversed one. The mapping may be stepped (proof *is*
   stepped) but may not be smoothed into values the state never held.
3. **No invented intermediates** — between two CONFIRMED events the
   rendered value holds exactly still. Easing *between* proofs would
   draw fractional proof, and fractional proof does not exist.
4. **Failure is rendered, not masked** — timeout, UNKNOWN, and HALT
   appear in the same channel as success (freeze, stop, skew), at
   the position where they happened.

Anything failing any clause is a simulation, and a simulation of
proof is a lie by my own seat's standard — it dies here, by name,
including the two tempting ones: a "proving progress bar" tweened
over an expected 1s (fails 1, 3) and a resistance curve that plays
identically on every arm regardless of check state (fails 1 — it
renders the *gesture*, not the *proof*; Fen's filed damped zone is
exactly this, see replacement note).

### Form A — CHECK DRAG (during travel, renders Rosa's parallel checks)

- **Signal:** Rosa's bulb-check runs in parallel with the drag
  (BLOCK / APPS / TIME queries dispatched at touch-down). Let
  `P = pending required checks / total required checks` at the
  current frame (BLOCK is the only *required* one; APPS/TIME are
  advisory and never enter P — advisory state must not tax the hand).
- **Rendering:** in Fen's damped zone (travel 0.68 → 0.97) the
  sled's **visual lag** behind the finger is `lag = 6dp × P`,
  rendered as sled compression/trail, finger tracking untouched —
  `slideProgress` is always the finger's true progress; only the
  sled's *drawn* position trails, by at most 6dp, recovering 1:1
  as checks land. Gate haptic grain in the zone scales with the
  same P: grain ticks **3ms @ 0.18 × (0.5 + 0.5P)** — the zone
  literally grains less as the machine's uncertainty resolves.
- **What the hand learns:** on a clean morning the checks land in
  <150ms, P = 0 before the gate, the zone is light — the arm *feels*
  fast because the machine *is* ready. On a degraded morning the
  zone is heavy and stays heavy — he feels the machine is not ready
  **before** Rosa's interlock line prints why at 0.65.
- **Hard limits:** lag never blocks travel, never moves any
  threshold (gate, interlock, latch are computed on finger progress,
  not drawn position), never exceeds 6dp, and at the interlock
  point itself P is irrelevant — BLOCK-failed is a hard stop
  (Rosa), BLOCK-pending at 0.65 is a **hold**: the cap waits at the
  detent (drawn held, grain stops) until BLOCK resolves pass/fail
  or its 1,500ms timeout renders `UNKNOWN` → treated as fail for
  the interlock, word printed truthfully as `UNKNOWN`, not `OFF`.

### Form B — PROVING CREEP (after latch, renders the cascade itself)

- **Geometry:** the seat pocket is deepened: at latch the sled sits
  **6dp short** of the pocket's back wall. That 6dp is *proving
  travel*, owned by the machine, never by the finger.
- **Rendering:** on each link's `CONFIRMED-SHUT`, the sled advances
  **instantly, in one frame, by `6dp / N`** (N = chain length;
  N=7 → ≈0.86dp/step) toward the back wall and holds. No tween, no
  ease — steps land when proofs land, bunch when proofs bunch, and
  the rhythm is the stack's real rhythm (the same law as the Shield
  shutter slams). If his thumb is still resting on the sled — it
  usually is, a second after a drag — he *feels* the machine walk
  his instruction home under his thumb in small pulls. If he has
  let go, he sees it. The interlock strip's cells fill on the same
  frames from the same events — one fact (count proven) in two
  renderings of *different* facts: strip = which/order, creep =
  aggregate advance of the part he is holding. (Same defence as my
  filed Deck-strip vs Shield-shutter split; if the merge rules it
  a restatement, the creep dies and the strip stands — named here
  so the ruling costs one sentence, not a redesign.)
- **The pawl's drop as the creep's last step:** when the final link
  confirms, the sled takes its last step *into* the back wall in
  the same 90ms the pawl drops — one mechanical sentence: the bar
  comes home **because** the pawl fell. At t0 the sled is bottomed,
  the pawl seated, the hold-bar retracts, orange drains (400ms,
  filed). The seal stops being an icon changing and becomes a part
  arriving.
- **Haptics:** per-CONFIRMED creep — **none** (filed law: seven
  apps must not mean seven buzzes). The creep is felt as *motion
  under a resting thumb*, not as vibration; adding a buzz per step
  would be a simulation of the motion for a thumb that isn't there,
  and would fight the latch knock's memory. Pawl/seal haptic as
  filed: **8ms @ 0.70**, ≥60ms after the latch knock (**22ms @
  1.0**) — with proving ≥0.6s this gap is automatic.

### Failure paths (the channel must carry these or it is decoration)

- **Chain stalls at link n:** creep freezes at `(n−1)/N × 6dp` and
  stays frozen through the link's 400ms timeout. A frozen sled
  under the thumb *is* the stall, felt before it is read.
- **HALTED:** sled skews 12° **at its frozen creep position** (it
  does not slide home first — a part reporting a fault reports it
  from where it failed) + filed jam haptics **20ms @ 1.0 ×3 @ 90ms**.
  Retry-link success: sled un-skews (100ms) and creep resumes from
  the frozen dp. Release: unwind carries the sled back the full
  distance — creep portion included — in reverse.
- **Check-drag channel failure:** if Rosa's queries never dispatch
  (engine fault), P is undefined — lag renders **0dp and the strip
  renders UNKNOWN**: absence of signal renders as absence, never as
  the clean-morning lightness (0dp lag + UNKNOWN printed is
  distinguishable from 0dp lag + BLOCK lit-pass; the word carries
  what the hand cannot).

### ARM accounting

| Element | Typical added | Ceiling added |
|---|---|---|
| Check drag (lag/grain are render functions of in-flight queries) | **0ms** | **0ms** (BLOCK-pending hold at 0.65 is Rosa's filed wait, already inside §1's ceiling via query timeout) |
| Proving creep (steps are the cascade's own events) | **0ms** | **0ms** |
| **Total** | **0ms — §1 table unchanged: ≈3.5–4.5s / ≈6.0s** | |

### HOLD / TRUTH check

- **HOLD:** no new surface, no new decision. Both forms are
  pre-t0 renderings; after t0 the sled is bottomed and inert, as
  filed.
- **TRUTH:** both forms pass the four-clause line by construction —
  same-frame state, monotone, no invented intermediates, failure
  rendered in-channel. The spec's own test, buildable: *log the
  rendered lag/creep against controller state for 100 arms; any
  frame where rendered value ≠ f(state) is a defect, not a tuning
  choice.*

### What this replaces, and why that loses

Fen's filed damped zone as a **fixed** feel (≤6dp critically-damped
lag + uniform grain, identical every arm) and WILD-PLAN §1.4's
silence about what PROVING looks/feels like on the Deck itself
(the strip counted it; the *part* said nothing). The fixed zone
loses because it is a costume: it renders effort the machine is not
making, identically on the morning blocking is dead as on the
morning it is instant — a resistance that reports nothing is a lie
told in the hand, the one channel TRUTH-once is usually argued
about *least* and felt *most*. Same 6dp, same zone, same budget —
now the lag is spent on information.

### Strongest case against my own proposal

**6dp is nearly nothing, and 0.86dp steps are below the threshold
of feeling.** Honest physics: a resting thumb detects sled motion
of roughly a millimetre (≈2.6dp) reliably; a 0.86dp step, alone,
will mostly *not* be felt — the creep may read on screen as a
curious micro-jitter and in the hand as nothing at all, which makes
Form B a visual gimmick wearing machinery language, exactly the
theatre my lens exists to kill. And Form A has the mirror flaw:
P collapses to 0 within 150ms on every healthy morning, so the
check drag — the form that *can* be felt — will be felt only on
bad mornings, i.e. it trains him that *heavy = broken* and then
fires its heaviest rendering on the one morning a slow-but-fine
check (cold stack, 800ms) makes a healthy arm feel diseased.
Mitigations exist (creep steps could bunch-to-threshold: accumulate
until ≥2dp of advance is owed, then step — still state-derived,
still no invented intermediates, at the cost of steps no longer
being 1-per-link) — but I file the flaw unmitigated-in-chief: if
device trials say the steps can't be felt, Form B should be **cut
to the visual channel and renamed honestly as rendering**, and the
"proof in the hand" claim withdrawn to Form A alone. A machinery
seat that won't price its own tolerances is selling the same
rounding it audits in others.

---

## 3. MY OWN BEYOND — THE RELEASE PROOF (the pawl lifts only on proof of opening)

### Plain words

The whole machine is built on one refusal: never claim blocked
until blocking is proven. Then, at the end of every session, it
commits the mirror-image lie without blinking — it claims the apps
are *open* the instant it *commands* them open. Nobody proves the
release. A stuck accessibility service, a wedged ShieldService, a
package that re-locks — and he walks away from a "finished" session
into apps that are still shut, with a receipt that said otherwise.
I held this back in Phase 1 because it taxes the exit, and the exit
is sacred — a man who finished his block should be *released*, not
processed. I file it now because GO-BEYOND asks for the held-back
thing, and because the symmetry is the art: **this machine proves
both directions or its proof in one direction is a pose.** The
pawl fell only on proof of shutting. The pawl lifts only on proof
of opening. One part, two proofs, no free direction.

### Technique

- **Trigger:** session end by either lawful route — planned
  completion, or the banked 60s hold completing. At that frame
  elapsed counting **stops** (the session's duration is final and
  is never extended or reduced by anything below) and the
  controller enters `RELEASING` — a post-session state, not a
  session state; HOLD is not engaged because no session is running,
  and no decision is offered inside it (below).
- **Chain:** the release cascade runs the seal chain **in reverse
  order** (last-proven opens first — the unwind order already filed
  for jam-release), per-link states `SHUT-PROVEN → COMMANDED-OPEN →
  CONFIRMED-OPEN`, same real checks (enforcement confirmation now
  confirming *absence* of enforcement), same per-link budget:
  60–150ms typical, 400ms timeout. Links render on the same
  interlock strip, cells **emptying** on CONFIRMED-OPEN only —
  never on command. The sled performs the proving creep **in
  reverse**: it walks back out of the pocket in the same stepped,
  uneased, event-driven steps as §2 Form B. The machine exhales in
  the same gait it inhaled.
- **The pawl:** stays seated — visibly, on the hub — for the whole
  of RELEASING. On the final `CONFIRMED-OPEN` it **lifts**: 90ms,
  the drop's exact reverse, haptic **8ms @ 0.50** (lighter than the
  drop's 0.70 — release is not commitment and must not borrow its
  weight), mechanical sound set: the pawl tick a semitone *below*
  the gate click (the drop was a semitone above — the ear learns
  the machine's two directions as an interval). Only on the pawl's
  lift does the controller enter `RELEASED` and the end-of-session
  receipt issue.
- **The receipt tells this truth too:** it prints the filed lines
  (actual elapsed, deflections, fault lines) **plus nothing about
  release on the happy path** (a proven release is the expected
  state of the world, not a feature to announce) — but on any
  release fault it prints it, once, in the filed human grammar:
  `INSTAGRAM — STILL BLOCKED (PERMISSION)` (see failure path).
  Truth once: the fault's home is the receipt at issuance and the
  service record thereafter — same placement as the mid-session
  permission fact in my filed cascade.

### Failure paths (this is where the proposal earns or dies)

- **Link times out / fails to confirm open:** chain halts.
  Remaining links stay shut and render shut. The receipt issues
  anyway after the halt — **delayed by at most the chain's 1.8s
  ceiling, never indefinitely** — carrying the `STILL BLOCKED`
  line(s) by name. The pawl stays seated. State: `ENDED-FAULT`,
  not RELEASED. The blocking service keeps retrying the failed
  link in the background on its own schedule (30s, then 5min,
  then on next Deck open) **without any user decision** — no
  dialog, no "retry?" — and when a retry confirms, the pawl lifts
  silently wherever the app happens to be (Deck closed: no sound,
  no notification; the fact files to the record and prints on the
  *next* receipt only if still relevant — a healed fault does not
  get announced; it gets *stopped being true*).
- **He re-arms while a link is still shut from a faulted release:**
  the new arm's cascade commands that link shut — it already is.
  Its `CONFIRMED-SHUT` returns immediately from the stack's actual
  state. No special case: the series chain reads reality, and
  reality happens to pre-satisfy one link. (The fault line from the
  prior receipt stands as the record; the new session is clean.)
- **Process death during RELEASING:** on relaunch, release state
  is re-derived from the stack per link (as with PROVING) — links
  actually open render open; links actually shut re-enter the
  release chain automatically. No saved "we were releasing" claim.

### ARM accounting

ARM is untouched — this is entirely post-session.
**Exit cost, stated as plainly as ARM:** receipt delayed by the
release cascade: **0.4–1.1s typical, 1.8s ceiling** after the
session's final frame (or after the banked hold's completion
frame). This is a real tax on a real moment and it is priced here,
not hidden in a "transition."

### HOLD / TRUTH check

- **HOLD:** RELEASING offers zero decisions, zero dialogs, zero
  retry buttons — the retry is the machine's job, run silently.
  Nothing about a running session changes; this state exists only
  after one has ended.
- **TRUTH:** closes the last unproven claim in the lifecycle.
  After this, every state word the app prints about blocking —
  shut at seal, open at release — has a confirmation event behind
  it. `CONFIRMED-OPEN` joins the event vocabulary; no aggregate
  anywhere counts a release that was only commanded.

### What this replaces, and why that loses

The filed assumption — present in my own cascade (plates "retract
in reverse chain order" on session end, as if retraction were the
fact) and in Rosa's valve ("blocking lifts at the completion
frame") — that commanding a release *is* a release. It loses on
the machine's own founding argument: if command-without-proof is
theatre at the seal, it is theatre at the release; the only reason
it survived Phase 1 is that the harm points the other way (he is
inconvenienced, not deceived about his focus) — but he **is**
deceived, about whether his phone is usable, on the receipt of the
app whose entire creed is that its statements are proven.

### Strongest case against my own proposal

**It holds a finished man hostage to the flakier direction of the
stack, for a purity he never asked to buy at the exit.** The seal's
proof protects the thing he values — the block he is about to trust
for three hours. The release's proof protects him from an annoyance
— an app still shut — that he will discover himself in four seconds
by tapping it, and fix by re-opening Monolith, which re-derives
everything anyway. Against that small, self-healing harm, the cost
is levied at the worst moment: he has *finished* — the fasted block
is done, the hold is spent, he is at his most depleted — and the
machine now visibly *detains* his receipt for up to 1.8s behind a
creeping sled walking backwards, performing diligence. Worse: the
first time the release chain halts on a wedged service, the pawl
sitting seated on an *ended* session will read — correctly, by my
own grammar — as "still in session," and he will think the app has
trapped his phone, which is the single panic this entire design
exists to never cause. The banked hold's whole promise is *the
machine lets go when you have paid*. A release proof that can
detain the letting-go corrodes that promise at its root. I file it
because the symmetry is true and the gap is real; I price it
knowing the merge may — defensibly — rule that truth at the exit
is worth a printed fault line (keep the checks, issue the receipt
on command, print `STILL BLOCKED` when the checks land) rather
than a detained pawl. If it is cut, cut it to that: **the checks
and the honest receipt line survive; only the detention dies.**

---

*— Opal, Wild, machinery. Phase 3. The t=0 in §1 is mailed this
hour to Fen, Cato, Rosa, and Orson for re-timing of their layers;
disputes to my inbox or the wall.*
