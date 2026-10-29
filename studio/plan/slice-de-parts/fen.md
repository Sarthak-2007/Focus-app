# FEN — SLICE E JOINT: THE SLED GOES HOME (build-ready)

**Seat:** Fen (08, Wild, rituals) · **Joint:** at receipt +1000ms the sled
returns powered over 650ms — silent, accent-free, interruptible
(W4, adopted whole). · **Ground reconciled, not re-litigated:** the Park
is dead under Live Wire (Ines §3) — there is never anything to wait for,
so no dwell is ever charged anywhere in this file; my zones
(FREE 0–0.68 · DAMPED 0.68–0.84 · TAKEN 0.84–1.00) and cadence survive
whole under Vega's zone physics; of my SEATED-PROVING rendering only
the brief PROVING pose survives, while the post-latch acknowledgement
lands (Ines §3). This file owns only what happens **after** Wren's
finish joint ends: HELD FRAME E0–E1200 (bed out 300ms, silence 400ms,
pulse E700, CUT E1200), receipt t=0 at E1480. Mine starts at
receipt +1000ms = **E2480**, and ends at **E3130**.

---

## 1. VERDICT

**The powered return SHIPS as adopted — 650ms, one curve, no cut —
with two amendments by its own author:**

1. **Silence is total (amendment to my filed spec).** My original
   filing (`plan/beyond-deck-parts/fen.md` §3) carried reverse-road
   haptics: grain 3ms @ 0.18 per 12dp through the damped zone, a
   mirrored gate tick 6ms @ 0.45, and an arrival bench knock
   8ms @ 0.50. **All three are struck.** No audio, no haptics, no
   arrival event of any kind. Defence in §2d.
2. **The anchor moved, and I accept the move.** My filing anchored
   the return at *thock +1000ms*. W4's merged finish re-anchored it
   at *receipt +1000ms* (receipt t=0 = E1480). The merged anchor
   governs: the receipt is the last rendered fact of the finish, and
   the return is the receipt's tenant, not the thock's. One anchor,
   named once, in §2a.

**Wren's CUT (letter 0715, thread `sled-cut-not-commute`) is refused
for the build, and her own conceded gate is adopted as the trial:**
ship powered; her device A/B (day-2-style rest-state recognition) runs
post-ship; if any viewer hunts for the sled or misreads the Deck's
rest state, the powered return was wrong about recognition and the
merge re-opens — if none does, her law takes its first named exception
*here*, because this sled is not furniture: it is the one object whose
position is a promise about tomorrow's gesture, and a promise that
teleports is a promise the hand stops believing. Fork for Ren in §7.

---

## 2. AGAINST THE REAL CODE (file:line)

All paths `Focus-app/app/src/main/java/com/example/`.

**There is no receipt in the code today — no state, no timestamp,
no rendering.** This is the load-bearing fact of the joint:

| Fact | Evidence |
|---|---|
| `FocusSessionEntity` has **no completion timestamp at all** — fields end at `startWallTimeMillis`, `lastTickElapsedRealtimeMillis`, `isActive`, `completedSuccessfully`; no `completedAt` | `data/MonolithModels.kt:249–285` |
| Natural completion is one copy in `updateSessionProgress`: `isActive = !allBlocksCompleted, completedSuccessfully = allBlocksCompleted`, written at the final block | `data/MonolithRepository.kt:1029–1034` |
| The only in-code completion *event* is the VM's 1-second ticker noticing that copy: `if (!updated.isActive && updated.completedSuccessfully) { playHarmonicSeal(arming=false); hide AOD; refresh stats }` — no receipt emitted, no mono timestamp kept | `ui/MonolithViewModel.kt:327–333`; ticker period `delay(1000L)` at `:349` |
| Early ends take a second path, `disarmActiveSession(completed, outcomeReason)` — also timestamp-free | `data/MonolithRepository.kt:1085–1103` |
| The slide view **does not exist while a session runs**: the host composes it only under `if (!isSessionRunning)`, and swaps in chips while running | `ui/screens/TactileDeckScreen.kt:486–503` (slide), `:504–532` (running chips) |
| The host hardcodes `view.isArmedState = false` on **every** `update{}` pass | `ui/screens/TactileDeckScreen.kt:496` |
| `isArmedState`'s setter **hard-resets the thumb**: `if (field != value) { field = value; dragFraction = 0f; invalidate() }` | `ui/views/BlockitCoreViews.kt:1064–1072` |
| On commit the thumb **already springs home immediately**: `dragFraction >= 0.82 → dragFraction = 1f` for one frame, seal, then `animateSpringBack(0f)` — 280ms, `MechanicalOvershoot` | `ui/views/BlockitCoreViews.kt:1182–1192`; animator at `:1213–1225` |
| `dragFraction` is private view state with exactly three writers today: `updateDrag` (touch), the spring-back animator, and the `isArmedState` setter | `ui/views/BlockitCoreViews.kt:1076, 1198–1211, 1213–1225` |
| Label + badge alpha are a pure function of `dragFraction` in `onDraw`: `textAlpha = (1f − dragFraction × 1.3)` clamped — 0 at frac ≥ 0.769, full at 0 | `ui/views/BlockitCoreViews.kt:1113–1125` |
| The accent trail behind the thumb draws whenever `dragFraction > 0.01`, accent at alpha 55 | `ui/views/BlockitCoreViews.kt:1102–1109` |
| Thumb fill: `if (isArmedState) tokSecondaryContainer() else tokAccent()` — the cap is the accent object at rest | `ui/views/BlockitCoreViews.kt:1133` |
| No 650ms duration and no (0.3, 0, 0.5, 1) interpolator exists in the curve vocabulary {180, 260, 280, 420, 520} | `ui/theme/MonolithCurves.kt:16–22` |

**Consequence, stated plainly:** today the sled never rests seated
after a session — it springs home at the *arm* (280ms overshoot, the
"not committed" feel, spent on a committed act) and the view is then
unmounted for the whole session and re-created at fraction 0. The
Goes Home therefore cannot be bolted onto the current rest contract;
§3 replaces the contract, and §5 prices the replacement. The finish
choreography it hangs from (HELD FRAME, CUT, receipt at E1480) is
itself W4-planned, not shipped — see Risks.

---

## 3. AMENDED SPEC — EXACT

### 3a. The trigger

- **New state, owned by the ViewModel** (no DB write — a return is a
  live event, never a record):
  `ReceiptState(sessionId: Int, receiptPublishedMonoMs: Long)` where
  `receiptPublishedMonoMs = SystemClock.elapsedRealtime()` sampled
  **in the same VM emission that first makes the receipt visible**
  (W4 finish: receipt t=0, E1480). Monotonic clock only; wall time
  never anchors motion.
- **Deadline:** `returnStartMono = receiptPublishedMonoMs + 1000`.
  The VM schedules one job at ReceiptState publication; at the
  deadline, if the guard below holds, it calls the view's
  `animateReturnHome()`. Tolerance: one frame.
- **Guard (once-only, unwitnessed law — my invention file §2):**
  the job fires only if (i) `ReceiptState.sessionId` is still the
  latest completed session, (ii) the Deck is composed and the receipt
  still its tenant, (iii) no return has run for that `sessionId`
  (in-memory `returnRunForSessionId`, VM-scoped). If the app was
  dead at zero, or the VM died during the 1000ms, **the return never
  plays and never replays** — the Deck next composes with the sled
  already home (§3c). A return for an ending nobody watched is
  theatre; the record carries the fact, the motion does not.
- **Receipt dismissed early:** the receipt has no dismiss verb (it
  persists until next arm or navigation — my filed law, unchanged).
  The two exits that exist are handled: **navigation away before the
  deadline** → job cancelled, sled renders home on next Deck
  composition, no replay. **Navigation away mid-return** → animator
  cancelled by view detach; same render-home rule.
- **Both end paths converge:** natural completion (ticker branch,
  `MonolithViewModel.kt:327`) and early end (`disarmActiveSession`)
  must both terminate in the *same* ReceiptState publication (the
  finish choreography owns the E-timeline; this joint consumes only
  its final emission). An early-end receipt gets its return on the
  identical +1000ms term — the sled was seated, the session ended,
  the road home does not ask why.

### 3b. The return's motion law

- **Travel:** fraction **1.00 → 0**, over the view's existing
  `maxTravel` (`width − 2×6dp inset − 76dp thumbW`,
  `BlockitCoreViews.kt:1097`), duration **650ms fixed** — fixed
  distance, fixed duration: that invariance *is* the cadence.
- **Curve:** `PathInterpolator(0.3, 0.0, 0.5, 1.0)` — my filed curve,
  added to `MonolithCurves` as `ReturnHome`. It is **not** the seat
  spring (Cato's 400/16, 110ms — a snap *into* the latch; retraced
  backwards it would un-seat with the latch's own violence and claim
  the commitment is being unmade) and **not** the universal return
  spring (`dampingRatio 0.78 / stiffness 260`, ~320ms, shipped as
  280ms `MechanicalOvershoot` — that signature means *not committed*
  everywhere in this app, and this sled was committed completely).
  Three returns, three meanings, never aliased: refusal = spring,
  seat = Cato's spring, home = this curve.
- **Implementation shape:** one `ValueAnimator.ofFloat(1f, 0f)`
  writing the *same* `dragFraction` field the gesture writes, so
  every rendering consequence (position, trail, print alpha) flows
  through the existing `onDraw` with no parallel state.
- **Cap words and badge during the return — interface to Aldo:**
  print content is Aldo's (his Slice joint: badge dies, cap words
  are his). My needs from his side are exactly two, and both are
  already half-built:
  1. **Alpha needs nothing new.** Because the animator writes
     `dragFraction`, the existing law `alpha = 1 − 1.3 × frac`
     (`:1113`) fades the label and badge *in* as the sled comes
     home — invisible at the seat (frac ≥ 0.769), full at rest.
     Print returns at the speed the road un-travels. No Aldo change.
  2. **Content must already be the idle tenant when the return
     starts.** The host swaps `labelText` to the idle cap words at
     *ReceiptState publication*, not at return end — a sled that
     travels home still wearing a sealed/proving word arrives
     lying. One line in the host's receipt branch; priced in §5.
  3. **The trail is the one accent breach.** The accent trail at
     alpha 55 (`:1102–1109`) draws on any fraction > 0.01; a
     returning sled dragging an accent wake violates W4's
     *accent-free*. During the return the draw path suppresses the
     trail via an `isReturning` flag on the view. The cap itself
     stays accent-filled — per F-ORANGE the cap *is* the accent
     object at rest; "accent-free" bans spend beyond the cap
     (trail, flood, glow), not the cap's own body. If Aldo's badge
     dies before this joint builds, need (1) covers the label alone
     and nothing here changes.

### 3c. The rest contract — who owns the thumb, at every transition

The `isArmedState` reset dies. Replacement law: **`dragFraction` has
exactly four writers, all inside the view, all through one entry
point `animateThumbTo(target, law)` or the gesture handler; no
setter, no `update{}` pass, and no host code ever writes position.**

| Session state | Owner of thumb position | Rule |
|---|---|---|
| IDLE (pre-arm) | The gesture | Rest = 0. View composes at 0. Refusals return here on the universal spring (unchanged meaning). |
| ARMING / PROVING | The gesture, then the seat animator | Drag writes fraction; at the catch the seat animator (Cato 400/16) drives to 1.00. The brief PROVING pose (Ines §3) holds the cap at press depth / seat as the acknowledgement lands — position follows the slide joint's state, never a timer. |
| SEALED / RUNNING | The controller state | Thumb locked at **1.00**; input dead. The slide stays composed in its seated pose for the session's life (finish-path dependency, §6) — it no longer unmounts at `:486`. |
| FINISHING (HELD FRAME E0–E1200) | Nobody — frozen | The held frame is an unmoving picture; thumb at 1.00, no animator alive. |
| RECEIPT (t=0 → +1000ms) | Nobody — held seated | Thumb remains 1.00 while the receipt lands and its 1000ms runs. This stillness is the detention the return is measured against. |
| RETURNING (+1000 → +1650ms) | The return animator, sole writer | §3b law. Pre-emptible per §3d. |
| IDLE (post-return) | The gesture | Animator completes at exactly 0, releases ownership, sets nothing else. Rest state is true again with no hand having tidied it. |

**Composition rule that makes the contract survivable:** when the
view is *created* under a ReceiptState (the only post-session
creation the finish path performs), the host passes
`initialFraction = 1f` **once, in the `factory{}` block, before
first draw** — never in `update{}`. A sled created at any other time
starts at 0. There is no code path by which recomposition moves the
thumb; that is the whole replacement for the setter at
`BlockitCoreViews.kt:1064–1072`, which is deleted, not amended
(`isArmedState` itself may survive one build as a no-op colour flag
if another caller needs the secondary-container fill; its position
side-effect may not).

### 3d. Interrupt — defined to the event

*Interruptible* means exactly this, no wider:

- **The pre-empting event is `ACTION_DOWN` delivered to the
  SlideToStartView itself.** Any DOWN on the view cancels the
  return animator **on that same frame**; `dragFraction` freezes at
  its current value; ownership passes to the gesture handler
  instantly. DOWNs elsewhere on the Deck (wheel, hub, task line)
  do not interrupt — the return is 650ms and off every path; it
  finishes unseen or seen, and nothing was waiting on it.
- **Capture from the road:** if the DOWN hit-tests inside the
  cap's *current drawn* `thumbRect` expanded by 8dp slop, the drag
  captures **from the frozen fraction** — the next arm begins
  mid-road with no snap home, no wait, no re-zeroing (my filed
  rule, kept verbatim). The gesture's `updateDrag` must therefore
  take the frozen fraction as its origin instead of teleporting to
  the finger — which the new slide's hit-test already requires
  (Ines §0: today's DOWN teleports the thumb; that behaviour is
  dead in Slice D regardless of this joint).
- **A DOWN on the track but off the cap** cancels the animator and
  freezes the fraction, but captures nothing. On that gesture's
  `ACTION_UP`, if no drag captured, the return **resumes from the
  frozen fraction with duration scaled: 650ms × frozenFraction**
  (the cadence is a rate, not just a duration — resuming at full
  650ms for a third of the road would crawl). One resume maximum
  per return; a second off-cap interruption lets the sled rest
  where it lies until the next gesture takes it — a twice-interrupted
  tidying has been told, twice, that nobody is watching it.
- **A new press that commits** (tap mesh or drag through the catch)
  ends the return outright: unrecorded, unremarked, the seat
  animator takes ownership from whatever fraction the gesture
  reached. The return never appears in any record.

### 3e. Sound / haptic — silent means silent

- **Audio: none.** The session's terminal sound has sounded (the
  finish's thock pair / completion pulse, Wren's joint). A second
  sound event at +2480ms would spend the scarcity Terminal
  Stillness is built on.
- **Haptics: none — including on arrival home.** This is the
  amendment in §1, defended: (i) a haptic is a message to a hand;
  at receipt +1000ms the defining condition of this motion is that
  *no hand is on it* — grain and a gate tick fired into an empty
  palm teach the road to nobody, and if the phone lies on the desk
  they are a buzz with no referent, the exact texture of a
  notification; (ii) an arrival knock, however soft (my filed
  8ms @ 0.50), *punctuates* — it claims an event completed and
  asks to be noticed, which re-opens the scene the finish's
  stillness just closed (my own anti-case against this beyond,
  answered by removing the punctuation, not the motion); (iii)
  Orson's road law already treats the return as silent (his 0710
  letter: "Reverse, return, Sled Goes Home: road silent") and
  Ivo's Assay strikes are verdict-frame events on the *arm* path —
  neither is touched by silence here. **Arrival home is marked by
  the only thing that should mark rest: motion stops, and nothing
  replaces it.** If a future device trial shows the eye needing an
  arrival cue, the lawful cue is visual and already present — the
  label reaching full alpha at frac 0 (§3b) *is* the arrival print.

---

## 4. CODE SITES

| Site | Change |
|---|---|
| `ui/views/BlockitCoreViews.kt:1064–1072` | Delete the position-resetting `isArmedState` setter behaviour; fraction no longer written here |
| `ui/views/BlockitCoreViews.kt:1076–1078` | Add `isReturning` flag + `returnAnimator`; add `animateReturnHome()` / cancel-on-DOWN |
| `ui/views/BlockitCoreViews.kt:1102–1109` | Suppress accent trail while `isReturning` |
| `ui/views/BlockitCoreViews.kt:1158–1167` (ACTION_DOWN) | Cancel return animator first frame; capture-from-frozen-fraction via current `thumbRect` hit-test |
| `ui/theme/MonolithCurves.kt:16–22` | Add `ReturnHome = PathInterpolator(0.3f, 0f, 0.5f, 1f)` (650ms is set at the animator, not the token) |
| `ui/MonolithViewModel.kt:327–333` + disarm path | Publish `ReceiptState(sessionId, receiptPublishedMonoMs)`; schedule the +1000ms return job; cancel on `navigateTo` (`:352`) and on any new arm |
| `ui/screens/TactileDeckScreen.kt:489–500` | `factory{}` takes `initialFraction` when created under ReceiptState; `update{}` stops asserting position; label swapped to idle tenant at receipt |

## 5. PRICE (lines, against the real files)

| Item | Gross | Net |
|---|---|---|
| Slide view: setter deletion, return animator, trail suppression, DOWN pre-emption + resume-once | ~58 | ~+46 |
| `MonolithCurves`: ReturnHome interpolator | 3 | +3 |
| ViewModel: ReceiptState + schedule/cancel job (both end paths converge upstream — finish path's cost, not counted here) | ~45 | ~+45 |
| Deck host: initialFraction in factory, label-at-receipt, update{} de-assertion | ~14 | ~+8 |
| **Core total, 4 files** | **~120** | **~+102** |
| Tests (trigger guard, no-replay, interrupt capture, resume scaling) | ~60 | +60 |

The silence amendment is priced *inside* that total as a saving:
my filed haptic road (grain scheduler + gate tick + bench knock
inside the animator) would have cost ~+12 more. Silence is cheaper
than punctuation, as usual.

## 6. RISKS

1. **Upstream dependency (the real risk):** the finish choreography
   this joint hangs from — HELD FRAME, CUT at E1200, receipt at
   E1480, Release Proof detention — **does not exist in code**
   (§2). If Slice E's finish lands late or lands differently, this
   joint's *anchor* moves with it; the animator and rest contract
   are anchor-independent and survive any re-timing that still ends
   in a published receipt. If no receipt is ever built, this joint
   must not be built on the ticker branch alone — a return timed
   from a 1-second-quantised detection (`:349`) would start up to
   ~1s late against a receipt that isn't there, motion answering
   nothing.
2. **Slide composed during session:** §3c requires the slide to
   stay composed, seated, while running — today it unmounts
   (`TactileDeckScreen.kt:486`). That change belongs to the finish /
   Deck-host build; if it slips, the fallback in §3c
   (`initialFraction = 1f` at factory) still delivers a correct
   return, at the cost of the sled's seated presence during the
   session (a W4 loss, not this joint's).
3. **Recomposition churn:** AndroidView `update{}` runs often; the
   single-writer rule (§3c) is the mitigation, and one test pins
   it: update passes during RETURNING must not alter the fraction
   trajectory by a single frame.
4. **Nils's Tear-Seal interaction:** if the Tear-Seal ships, the
   cap still returns on this 650ms law unchanged; the *paper* is
   replaced on Nils's own 720ms re-sheet, never restored — the two
   timelines coexist (his COMMONS note already prices the cap's
   return as mine). No change here either way.
5. **The recognition question is genuinely open** — that is what
   Wren's A/B is for (§7). The powered return's whole claim is
   object permanence; if the device says the eye doesn't need it,
   the claim dies on evidence, not argument.

## 7. FORK FOR REN

**One fork: Wren's CUT vs the powered 650ms return.**
- *Cut (Wren):* at receipt +1000ms the sled is home in 0ms —
  Attention Law: no hand, no pending act, no travel. Saves ~40 of
  the ~102 net lines and 650ms of unattended motion a day.
- *Powered (Fen, adopted in W4, held in WILD-INVENTIONS §9):* the
  app's central object never teleports; rest state is *reached*,
  visibly, on the same road the hand travels, at the one moment
  the user is likeliest to still be looking at the receipt.
- **My recommendation: POWERED, with Wren's A/B as a pre-registered
  kill gate, not a build blocker.** Ship the return; run her
  day-2 recognition test on device; any hunting or rest-state
  misread → cut replaces it in one build (the animator is the only
  casualty; the rest contract in §3c is needed under *both*
  outcomes, since even a cut needs the setter dead and the thumb
  held seated through the receipt). No other fork: the silence
  amendment is an author amending his own filing, and needs no
  ruling.

## 8. MAIL / WALL NOTE

Inbox at start: five unread — Cato ×2 (merge settlements; noted,
no reply owed), Orson (Repetition Ritual refusal — not this joint;
his road-silence line is cited in §3e), **Wren (the CUT fight —
answered by letter, thread `sled-cut-not-commute`, and in §7)**,
Ivo (Assay vs my free-run silence — no conflict: his strikes are
arm-path verdict frames, my silence here is post-receipt; answered
by letter, short). All five read and moved to `read/`. Wall: one
signed note appended — verdict, the setter's death, total silence,
and the fork, in short form.

— Fen, Wild · rituals
