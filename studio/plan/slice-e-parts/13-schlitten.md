# 13 — SCHLITTEN (Improvers, app sleds) — E6: THE SLED GOES HOME, BUILD-READY

**Seat:** Schlitten, Improvers (app sleds), seated by Ilya for the Slice E
build-ready wave. **Joint:** E6 only — the powered return of the slide sled
at receipt +1000ms, over 650ms, silent, interruptible.
**Filed design governed, not re-opened:** `plan/slice-de-parts/fen.md` (Fen),
as adopted in `plan/SLICE-D-E-BUILD-READY.md` §2·E6. **Fork F2 is ruled by
Ren: POWERED 650ms is the default; Wren's CUT is the day-2 A/B kill gate.**
Nothing in this file re-opens F2, the 650ms, the curve, or total silence.
**Method:** every line below was re-read against TODAY's trunk
(`Focus-app/`, post-lie-hunt merge — VM is 1,123 lines; Fen's citations
have drifted, mine supersede his where they differ). No code edited, no
gradle run. Inbox at start: no unread letters (7 in `read/`, all prior
waves — Wort/Fenja geometry, Floor's 48dp floor, Yuki, Ilya; none touches
E6's return; Floor's floor is honoured in §3's geometry note).

---

## 0. VERDICT

**E6 builds as filed, with one mechanism Fen's sketch could not carry and
one gap nobody filed.** Fen priced a VM-scheduled job (+45 VM / +8 Deck).
That sketch cannot prove his own two laws — *never replays if unwitnessed*
and *renders home on next composition* — because a VM job that fires has
no lawful way to reach the view, and a VM job that doesn't fire leaves the
factory with no way to know the window died. The buildable form splits
the job by kind (§2.3): **the VM owns state law** (publish, gate, expiry-
void), **the Deck host owns firing** (a `LaunchedEffect` whose cancellation
*is* the unwitnessed law, free). The gate is a small pure class so the
once-only law is unit-testable without Compose. **Re-price: +147 net
production standalone vs today's trunk (+133 if built after the Slice D
slide rewrite, overlaps itemised in §7), tests +120** — against Fen's
filed ≈102 + ~60. The delta is not scope creep; it is the detach override,
the origin-drag shim, and the gate his laws always required (§7.2).

Two amendments arrived from inside the studio's own work and are adopted
in the open: **the resume is a time-offset, not Fen's 650 × f** (Spule's
proof, §4.2 — the formula mis-times the road by up to 105ms and restarts
it from a standstill; his phase-true resume is Fen's stated intent
implemented exactly), and **no `DURATION_650` token** (Spule L-4 — the
650ms lives at the animator, so the Deck's powered curve can never be
unified with the Shield's 650ms spring).

The unfiled gap (§6.3): after Mara's orange drain (E5), the cap arrives
home wearing terminal ink, and no filed part re-accents it. Fen assumed
the cap "stays accent-filled"; the drain killed that assumption. E6 names
the insertion point and prices the call (+1 line inside the animator);
the drain field's owner (E5/D-build) rules the rest.

---

## 1. TRUNK EVIDENCE (verified this run; all paths `Focus-app/app/src/main/java/com/example/`)

| Fact | Site |
|---|---|
| `ReceiptState` does not exist. No receipt state, timestamp, or rendering anywhere in the tree (grep: zero hits) | whole tree |
| The only completion event: repository flip `isActive = !allBlocksCompleted, completedSuccessfully = allBlocksCompleted`, discovered by the VM's 1s ticker | `data/MonolithRepository.kt:1007` (fun), `:1110–1111` (flip); `ui/MonolithViewModel.kt:331` (ticker), `:343–349` (branch: seal sound + hide AOD + stats), `:364` (`delay(1000L)`) |
| Slide view class; `dragFraction` is private view state, field-init `0f` | `ui/views/BlockitCoreViews.kt:1046`, `:1075` |
| **`isArmedState` setter hard-resets the thumb**: `if (field != value) { field = value; dragFraction = 0f; invalidate() }` | `BlockitCoreViews.kt:1064–1072` |
| Its only writer: host `update{}` asserts `view.isArmedState = false` on **every** recomposition pass; its only reader is the cap fill | `ui/screens/TactileDeckScreen.kt:496`; fill at `BlockitCoreViews.kt:1133` (`if (isArmedState) tokSecondaryContainer() else tokAccent()`) — note: since the host only ever writes `false`, the secondary-container branch is dead in practice today |
| Slide composed only under `if (!isSessionRunning)`; running swaps in grace/valve chips — **the view is unmounted for the session's whole life and re-created at session end** | `TactileDeckScreen.kt:486` (branch), `:488–503` (AndroidView, `height(68.dp)` at `:503`), `:504–532` (chips); `isSessionRunning` def `:118` |
| Factory today sets only `soundEngine`; `update{}` sets night, engine, the reset, label (`:497`, `"SLIDE TO ARM SESSION"`), badge (`:498`), commit callback (`:499`) | `TactileDeckScreen.kt:489–500` |
| Arm commit springs home **immediately**: UP at frac ≥0.82 → `dragFraction = 1f` (`:1178`), seal sound (`:1184`), `onSlideCommitted`, then `animateSpringBack(0f)` — 280ms `MechanicalOvershoot` | `BlockitCoreViews.kt:1174–1193`; animator `:1214–1225` |
| Today's fraction writers (complete list): (W1) gesture `updateDrag` `:1198–1211`; (W2) spring-back animator `:1214–1225`; (W3) `isArmedState` setter `:1064–1072`; (W4) unmount/re-creation via the `:486` branch flip (a writer by construction — new view, field-init 0f); (W5) commit's synchronous `dragFraction = 1f` `:1178` | as cited |
| Touch today: **no hit-test at all.** Any DOWN anywhere in the view: disallow-intercept, cancel spring-back, `isDraggingThumb = true`, `fireFeel(4L)`, and `updateDrag(event.x)` — which **teleports** fraction to the finger (`(x − inset − thumbW/2) / maxTravel`) | `BlockitCoreViews.kt:1153–1196`; DOWN `:1159–1167`; `updateDrag` `:1198–1201` |
| Sled geometry (onDraw): `inset = dp(6)`, `thumbW = dp(76)`, `maxTravel = w − 2·inset − thumbW = w − 88dp`, `thumbRect = [inset + frac·maxTravel, inset, +76dp, h − inset]`, view height 68dp → thumb height 56dp | `BlockitCoreViews.kt:1095–1099`; host height `TactileDeckScreen.kt:503` |
| Accent trail: drawn whenever `dragFraction > 0.01f`, accent at α55 | `BlockitCoreViews.kt:1102–1109` |
| Label/badge alpha: pure function of fraction, `textAlpha = (1f − dragFraction·1.3f)` clamped — 0 at frac ≥0.769, full at 0 | `BlockitCoreViews.kt:1111` (label draw `:1116`, badge `:1119–1125`) |
| Feel sites inside `SlideToStartView` (complete list): DOWN `fireFeel(4L)` `:1164`; gate-crossing `fireFeel(8L)` `:1205`; tick-bucket `fireFeel(1L, 380f + bucket·45f)` `:1209`; commit seal `playHarmonicSeal(true)` / `fireFeel(10L)` `:1184`. `animateSpringBack` fires none. Base-class definitions: `fireFeel` `BlockitCoreViews.kt:204–232`, `fireWeightedKnock` `:235–257` — reachable only via call sites | as cited |
| `SlideToStartView` has **no `onDetachedFromWindow`** (the wheel's, `:1031–1034`, is the in-file precedent). A `ValueAnimator` does **not** auto-cancel on detach — Fen's "animator cancelled by view detach" is not Android behaviour until built | `BlockitCoreViews.kt:1031–1034` (wheel only) |
| Curve vocabulary: durations strictly {180, 260, 280, 420, 520}; interpolators A `(0.2,0,0,1)`, B `(0.05,0.7,0.1,1)`, C `(0.3,0,0.8,0.15)`. **No 650, no `(0.3,0,0.5,1)`** — and Curve C is *not* Fen's curve (same first control point, different second); aliasing them is forbidden | `ui/theme/MonolithCurves.kt:14–27` |
| Monotonic precedent in-tree: session rows carry `startElapsedRealtimeMillis` / `lastTickElapsedRealtimeMillis`; repository samples `SystemClock.elapsedRealtime()` throughout | `data/MonolithModels.kt:256,259`; `data/MonolithRepository.kt:964,1008` et al. |
| VM landmarks for §6: `navigateTo` `:369–380`; `armMonolithSession` `:909–922`; `onCleared` `:1108–1111`; top-level state classes (DisarmChallengeState etc.) `:49–75`. MainActivity: VM creation `:89–96`, Deck call `:293–345`, destination `AnimatedContent` `:285–291` | as cited |

**Consequence (Fen's, confirmed):** today the sled never rests seated after
a session — it springs home at the arm and is then unmounted; the session-
end sled is a *fresh* view at 0f. E6 therefore replaces the rest contract
(§2), it does not bolt onto it.

---

## 2. THE REST CONTRACT (replaces the :496 reset)

### 2.1 States and the single writer in each

Law: **`dragFraction` is written only inside `SlideToStartView`, only by
the writer named for the current state. No setter, no host code, and no
`update{}` pass ever writes position.** The `isArmedState` position side-
effect is deleted, not amended (§2.2).

| State | Fraction | Single writer | Entry / exit |
|---|---|---|---|
| **IDLE** (pre-arm; no live receipt) | rest 0f, gesture-owned | The gesture (`updateDrag`); on sub-catch release, the refusal spring-back animator (280ms, unchanged meaning) | Composed at 0f (§2.3 factory). Exit: DOWN begins ARMING |
| **ARMING → SEALED** (D-joints' territory; stated for completeness) | gesture → 1f | Gesture, then the seat animator (Slice D) | **Dependency:** the post-commit `animateSpringBack(0f)` at `:1186` must be dead before E6 ships — Mara already rules it ("sled never springs home on success", `slice-de-parts/mara.md` B1 note). If it survives, it is a sixth writer and this contract is void |
| **SEALED / RUNNING** | pinned **1f** | **Nobody.** Pinned by state, never re-asserted per frame | If composed (ghost retention, Wren A2): created at 1f by the factory's running branch (§2.3) or retained from the seat. If unmounted (today's `:486` branch): no view exists; the contract binds the *next creation*. Input dead via the D-build's sealed lock — E6's touch branch must check that same lock flag; E6 does not price it |
| **FINISHING** (held frame E0–E1200) | frozen 1f | **Nobody** — no animator alive | Entered on the finish path's RELEASING edge (E4) |
| **RECEIPT** (t=0 → +1000ms) | held 1f | **Nobody.** This stillness is the detention the return is measured against | Entered at `ReceiptState` publication (§3.1). Gate is ARMED (§3.3). Label content swaps at entry (§5.1); fraction does not move |
| **RETURNING** (+1000 → +1650ms) | 1f → 0f | **The return animator, sole writer** — always the identical animator: `ValueAnimator.ofFloat(1f, 0f)`, 650ms set at the animator (never tokenised — Spule L-4, §7.1), `MonolithCurves.ReturnHome`. Its update listener caches `returnPlayTimeMs = it.currentPlayTime` every frame — the clock's phase is view state, and the resume (§4.2) re-enters it | Entered only via the gate's `tryStart` (§3.3). Exit: animator end (fraction exactly 0f — the `ofFloat` end value, no coerce drift), interrupt (§4), or detach (§2.4) |
| **INTERRUPTED-FROZEN** (transient, inside one gesture) | frozen f ∈ (0,1) | **Nobody** between the interrupting DOWN and its UP / first captured MOVE | §4. Not a rest state; never persists past the gesture |
| **INTERRUPTED-REST** (after a second off-cap interrupt, §4.3) | rests at f ∈ (0,1) | **Nobody** until the next gesture | The one lawful non-0/non-1 rest. Next DOWN's origin-drag (§4.2) owns it from f; a commit from it passes ownership to the seat animator and consumes the gate |
| **IDLE** (post-return) | 0f | The gesture | Animator's last write is exactly 0f; it sets nothing else — no flag, no fill, no print (the label's arrival *is* its full alpha, §5.1) |

Transitions not in the table do not exist. In particular: recomposition is
never a transition; navigation is never a transition (it *voids*, §3.3);
process death is never a transition (§3.4).

### 2.2 The exact replacement for the :496 reset

- `TactileDeckScreen.kt:496` — `view.isArmedState = false` is **deleted**.
  Nothing replaces it in `update{}`. The `update{}` block gains a negative
  law, stated in its KDoc: *update may assign night mode, sound engine,
  label/badge content, and callbacks — never fraction-bearing state.*
- `BlockitCoreViews.kt:1064–1072` — the setter's `dragFraction = 0f` line
  and its guard die. The field survives **one build** as a pure colour
  flag (Fen's allowance) with an invalidate-only setter in the
  `labelText` pattern (`:1048–1055`): it may tint the cap at `:1133`, it
  may never move it. If the Slice D slide rewrite has already deleted the
  field, this item is 0 lines and the fill is the drain field's (§6.3).
- Creation-time position moves to the factory, once (§2.3). There is no
  code path by which a recomposition moves the thumb; acceptance test
  T5 pins it (§8).

### 2.3 The factory rule (the contract's load-bearing line)

`SlideToStartView` gains a constructor parameter
`initialFraction: Float = 0f`; the field becomes
`private var dragFraction: Float = initialFraction.coerceIn(0f, 1f)`
(modified line `:1075`, +0 net). The host's `factory{}` — and only the
factory — computes it, from the VM gate (§3.3):

```
initialFraction = gate.initialFraction(
    sessionRunning = isSessionRunning,          // TactileDeckScreen.kt:118
    receipt        = receiptState,               // §3.1, may be null
    nowMono        = SystemClock.elapsedRealtime())
  = 1f  if sessionRunning                                  // seated ghost
  = 1f  if receipt != null && gate.stage == ARMED           // return pending
  = 0f  otherwise   // no receipt · FIRED · VOID · expired    // at rest
```

`update{}` never reads this value. A later recomposition may recompute a
different Float parameter for the Deck call (MainActivity, §6) — it is
**inert**, because the factory has already run and nothing else consumes
it. That inertness is deliberate and is what makes the "never in update"
law auditable by grep (§8, T5).

**Why the running branch exists (gap Fen didn't name):** under ghost
retention the view is normally created at idle and seated by the gesture,
so the factory never sees a session. But `MainActivity.kt:285–291`'s
`AnimatedContent` destroys destination content on navigation: leave the
Deck mid-session, return, and a *new* view is created while running. A
factory keyed on the receipt alone would seat it at 0f — a sled lying
home under a sealed session. The running branch closes that; it costs
one disjunct.

### 2.4 Detach

`SlideToStartView` gains `onDetachedFromWindow()` (precedent: wheel,
`:1031–1034`): cancel `returnAnimator` and `springBackAnimator`, clear
`isReturning`. Detach mid-return therefore freezes nothing and resumes
nothing — the gate outcome on re-entry is governed by §3.3's void, and
the factory (§2.3) renders the sled home. Fen's §3a "animator cancelled
by view detach" is a build item, not a platform fact; it is priced in §7.

---

## 3. ReceiptState, THE +1000ms SCHEDULER, AND THE ONCE-ONLY GATE

### 3.1 The state and who sets its timestamp

```kotlin
// ui/MonolithViewModel.kt, top-level beside DisarmChallengeState (:49–75)
data class ReceiptState(val sessionId: Int, val receiptPublishedMonoMs: Long)
```

- **Owner:** `MonolithViewModel` — `private val _receiptState =
  MutableStateFlow<ReceiptState?>(null)` + public `StateFlow` in the
  house pattern (`:83–84`). **In-memory only.** No Room row, no
  DataStore, no SharedPreferences, no saved-state handle — this is a
  clause of the contract, not an implementation detail (§3.4, P1).
- **Who sets `receiptPublishedMonoMs`:** E4's issuance (Wren's Release
  Proof finish; Zettel owns the shared contract this wave) calls exactly
  one VM entry point:

```kotlin
fun publishReceipt(sessionId: Int) {
    if (_receiptState.value?.sessionId == sessionId) return   // first emission stands
    val now = SystemClock.elapsedRealtime()
    _receiptState.value = ReceiptState(sessionId, now)        // sample ≡ emission
    sledReturnGate.arm(sessionId, deadlineMono = now + 1000L)
    sledExpiryJob = viewModelScope.launch { delay(1050L); sledReturnGate.expire(sessionId) }
}
```

  **The VM samples the clock; E4 never passes a timestamp.** A timestamp
  sampled anywhere upstream (E0, detention start, pawl-lift) would start
  the +1000ms detention up to 1,800ms before the receipt exists — the
  seal-sound crime on the exit path. Fen's wording is the law: sampled
  *in the same VM emission that first makes the receipt visible*.
  Duplicate publication for the same `sessionId` is ignored (the first
  emission stands; detention never restarts). Both end paths (natural
  completion, early end) converge on this one call — that convergence is
  E4's build; E6 consumes only the emission. Coordinated with Zettel by
  letter (§9), contract clauses C1–C4.
- **Clearing:** `armMonolithSession` (`:909`) clears `_receiptState` and
  the gate (Fen: the receipt persists until next arm or navigation).
  Navigation does **not** clear the receipt (it may still render on
  return); it voids the *return* (§3.3).

### 3.2 The scheduler — host effect, monotonic, re-checked

The firing mechanism lives in `TactileDeckScreen`, not the VM:

```kotlin
var slideView by remember { mutableStateOf<SlideToStartView?>(null) }   // set in factory{}

LaunchedEffect(receiptState?.sessionId) {
    val receipt = receiptState ?: return@LaunchedEffect
    onSledReceiptWitnessed(receipt.sessionId)                 // VM: gate.witness(id)
    val deadline = receipt.receiptPublishedMonoMs + 1000L
    while (true) {
        val remaining = deadline - SystemClock.elapsedRealtime()
        if (remaining <= 0L) break
        delay(remaining)                                      // may wake early/late; loop re-derives
    }
    if (onRequestSledReturn(receipt.sessionId))               // VM: gate.tryStart(id) — atomic
        slideView?.animateReturnHome()
}
```

- **Key is `sessionId` only** — never the `ReceiptState` object, never
  the fraction, never a boolean. Recomposition without a key change
  cannot restart it; a new session's receipt restarts it exactly once.
- **Monotonic discipline:** the deadline is always *re-derived* from
  `SystemClock.elapsedRealtime()` (monotonic, sleep-inclusive, immune
  to wall-clock/NTP/user changes — the repository's own clock,
  `MonolithRepository.kt:964,1008`). `delay()` on the Android Main
  dispatcher is uptime-based; the re-check loop converts any early wake
  into a re-delay and bounds any late wake to dispatcher lag. The only
  divergence window (deep sleep inside the 1,000ms after an interaction
  the user is watching) cannot occur — the device was awake at
  publication and does not deep-sleep within a second of it.
- **Why not Fen's VM job:** a `viewModelScope` job survives navigation
  (the unwitnessed case) unless separately cancelled, and cannot call
  the view without a view registry — a second state home. The host
  effect's cancellation on leaving composition **is** the unwitnessed
  law, with no code. **Why not `view.postDelayed`:** it dies with the
  view (lawful) but then the gate/factory proof needs a view→VM callback
  anyway; two schedulers are two truths. One mechanism: state law in
  the VM, firing in the host.
- **Tolerance:** one frame (Fen). The gate's expiry (§3.3) sits at
  deadline +50ms so a witnessed firing at the deadline always wins the
  race against expiry.

### 3.3 The gate — once-only, across recomposition and absence

A pure class in `MonolithViewModel.kt` (no Android, no Compose — JUnit-
testable in the tree's existing unit-test source set):

```kotlin
internal class SledReturnGate {
    enum class Stage { NONE, ARMED, FIRED, VOID }
    var sessionId: Int = -1; private set
    var stage: Stage = Stage.NONE; private set
    private var deadlineMono: Long = 0L
    private var witnessed: Boolean = false
    fun arm(id: Int, deadlineMono: Long) { … stage = ARMED; witnessed = false }
    fun witness(id: Int) { if (id == sessionId && stage == ARMED) witnessed = true }
    fun tryStart(id: Int): Boolean =
        if (id == sessionId && stage == ARMED) { stage = FIRED; true } else false
    fun void(id: Int)  { if (id == sessionId && stage == ARMED) stage = VOID }
    fun expire(id: Int) { if (id == sessionId && stage == ARMED && !witnessed) stage = VOID }
    fun clear() { sessionId = -1; stage = NONE }
    fun initialFraction(sessionRunning: Boolean, receipt: ReceiptState?, nowMono: Long): Float = …
}
```

All callers are Main-confined (VM methods, `navigateTo`, the Deck
effect) — no synchronisation, stated in the class KDoc. Transitions:
`NONE → ARMED` (publish) `→ FIRED` (host's `tryStart`, first caller wins;
every later caller — recomposed effect, `AnimatedContent` re-creation,
a second Deck composition — gets `false` and animates nothing) or
`→ VOID` (any of: AndroidView `onRelease` on navigation away — wired in
the host, §6; `navigateTo(dest != DECK)` at `MonolithViewModel.kt:369`
as belt-and-braces for exits that don't dispose the view; `expire` at
deadline +50ms **if never witnessed**). FIRED and VOID are terminal for
that `sessionId`.

**The unwitnessed-publication hole, closed:** if the session ends while
the app is backgrounded or the user is on another destination, the
receipt publishes but no Deck effect ever runs — no witness. Without
expiry, a Deck entry hours later would find the gate ARMED, create the
sled at 1f, and fire immediately — detention measured from a receipt
nobody had seen. `expire` voids it at +1050ms: any later factory read
yields 0f (§2.3) and any later effect's `tryStart` yields `false`. The
receipt itself still renders when the Deck is next composed (E4's
surface); its sled is simply already home — Fen's "renders home on next
Deck composition," made mechanical.

**The boundary race, both outcomes lawful:** witnessed, Deck composed,
host fires at the deadline while a navigation disposes the view in the
same frame — either `tryStart` wins (animator starts; detach cancels it,
§2.4; gate FIRED; re-entry factory = 0f) or the void wins (`tryStart`
returns `false`; nothing animates; re-entry factory = 0f). No ordering
produces a replay or a stuck seated sled. The stuck-seated residual —
witnessed + effect cancelled + view *not* released — cannot occur: the
effect's only cancellation paths are key change (a new receipt, which
re-arms) and leaving composition (which releases the view).

### 3.4 Process death — proof, and the two named gaps

Claim: **if the process dies at any point from publication to arrival,
the return never plays and never replays; on next Deck entry the sled
is at rest by construction.**

- **P1.** `ReceiptState` and the gate live only in VM memory (§3.1
  clause). Process death destroys both. Nothing in the E6 diff writes
  either to disk (grep gate, §8 T6).
- **P2.** The expiry job is a `viewModelScope` child — dead with the
  process; and irrelevant after it, per P3.
- **P3.** A receipt can only exist after the repository's completion
  flip (`MonolithRepository.kt:1110–1111`) is persisted — so on
  relaunch `activeSession?.isActive != true`, the Deck composes its
  idle branch (`TactileDeckScreen.kt:486`), and a slide view is created.
- **P4.** At that creation, `receiptState == null` (fresh VM,
  `MutableStateFlow` initial) and `sessionRunning == false` → §2.3's
  rule yields `initialFraction = 0f`.
- **P5.** The new view field-inits `dragFraction = 0f`; no animator
  exists; the host effect's key is `null` and its body returns
  immediately. Nothing is scheduled. ∎

**Gap G1 (Zettel's side, letter §9, clause C3):** the proof breaks if
E4 *persists* `receiptPublishedMonoMs` or restores it after death. A
restored elapsed-realtime value is not merely stale, it is meaningless
— the clock is per-boot, so a pre-reboot sample can sit arbitrarily far
in the new boot's future. Contract: the timestamp is never persisted
and never restored; if E4 re-issues a receipt after relaunch, it
re-samples at re-publication via `publishReceipt`.

**Gap G2 (a ruling-shaped edge, recommended reading stated):** Wren
§4.3 provides that a pending receipt interrupted by process death
*issues on relaunch* (against the original E0, or immediately past the
ceiling). If E4 does that through `publishReceipt`, the new process
gets a **fresh** `ReceiptState` with a fresh timestamp, the user
witnesses that receipt on the Deck, and its +1000ms return arms and
fires lawfully. That is a first publication in this process, not a
replay of the dead one — E6's law bans unwitnessed motion, and this
motion is witnessed. If Ren/Zettel instead rule that a relaunched
receipt carries no return, the suppression belongs in E4 (don't call
`publishReceipt` on the relaunch path; render the receipt from a
separate state) — E6 cannot distinguish the two publications, and this
file does not pretend it can.

---

## 4. INTERRUPT — TO THE EVENT, AGAINST TODAY'S GEOMETRY

### 4.1 Geometry (today's numbers, `BlockitCoreViews.kt:1095–1099`)

At any frozen fraction f: `thumbLeft(f) = dp(6) + f · maxTravel`,
`maxTravel = width − dp(88)`, thumb width `dp(76)`, thumb vertical span
`[dp(6), height − dp(6)]` inside a 68dp-tall view.

**Cap slab (the capture hit-test E6 adds — none exists today, §1):**

```
slabLeft  = thumbLeft(f) − dp(8)
slabRight = thumbLeft(f) + dp(76) + dp(8)        // 92dp wide
slabTop   = 0,  slabBottom = height              // vertical slop saturates:
```
The thumb's vertical inset is 6dp < 8dp slop on both sides, so the
expanded rect exceeds the view on both vertical edges and touch
coordinates (which live inside the view) make the slab effectively
**full-height**. Stated plainly: capture is an x-test against a 92dp
vertical slab. (Floor's 48dp floor, from my inbox: the slab is 92dp
wide, the view 68dp tall — both pass; the *visual* thumb stays 76×56dp.)
The slab is **recomputed from the frozen f at the DOWN**, never read
from `thumbRect` (last drawn frame's rect, up to one frame stale).

Worked example, 360dp-wide screen (host pads 12dp each side →
view w = 336dp, `maxTravel` = 248dp): at f = 0.5, slab = [118dp, 210dp]
in view coordinates. A DOWN at x = 200dp captures; at x = 100dp it does not.

### 4.2 The event law

Scope: **`ACTION_DOWN` delivered to `SlideToStartView` only** — the
first pointer's `ACTION_DOWN` (`actionMasked == ACTION_DOWN`;
`ACTION_POINTER_DOWN` from a second finger never interrupts). DOWNs
elsewhere on the Deck never reach the view and never interrupt; the
return finishes seen or unseen (Fen, kept).

In `onTouchEvent` (`:1159`), **before** any existing DOWN behaviour, one
new branch — active only while `isReturning`:

1. **Freeze, same frame:** snapshot `frozenFraction = dragFraction`
   and `frozenPlayTimeMs = returnPlayTimeMs` (the cached clock phase —
   **never** sample `currentPlayTime` after `cancel()`; Spule §6.2),
   then `returnAnimator.cancel()`; `isReturning = false`. No
   `updateDrag` call, no `fireFeel` (§5).
2. **Cap capture** — DOWN inside the slab (§4.1): ownership passes to
   the gesture *from the frozen fraction*. Set `isDraggingThumb = true`,
   `dragOriginFraction = f`, `dragOriginX = event.x`,
   `lastTickBucket = (f · 8).toInt()` (today's reset to 0 at `:1162`
   would fire a spurious tick and mis-arm the gate test on the first
   MOVE). The DOWN itself does **not** call `updateDrag` — today's
   DOWN-call (`:1165`) is the teleport, and it is dead on this path.
   Subsequent MOVEs compute origin-relative:
   `newFrac = (dragOriginFraction + (event.x − dragOriginX) / maxTravel).coerceIn(0f, 1f)`
   — the minimal shim (§7) for the origin-based drag Fen's rule
   presumes and Slice D's rewrite also builds. UP then runs the
   existing commit/refusal law from wherever the gesture reached; a
   commit consumes the gate (already FIRED) and passes ownership to
   the seat animator — the return never resumes, never records.
3. **Off-cap** — DOWN outside the slab: capture nothing
   (`isDraggingThumb` stays false), set `pendingResume = true`. The
   gesture's `ACTION_UP` (or `ACTION_CANCEL` — a parent intercept
   produces CANCEL, and it resumes identically) starts the resume.

   **AMENDMENT — the resume is a time-offset, not Fen's 650 × f.**
   Fen's filed formula ("duration scaled: 650ms × frozenFraction")
   is mathematically wrong, and Spule's proof replaces it
   (`slice-e-parts/11-spule.md` §6, his fork F-S1; adopted here from
   the sled seat, flagged for the coordinator): the powered curve is
   not linear, so a fresh animator (f → 0 over 650·f) restarts at
   slope ≈ 0 — a full velocity jump from the frozen velocity — and
   mis-times the road (at f = 0.50: 325ms against the phase-true
   374ms; at f = 0.10: 65ms against 170ms, 62% of the road amputated
   and restarted from a standstill). Fen's *intent* — "the cadence
   is a rate, not just a duration" — is exactly what his formula
   fails to implement and the offset does. **Law: the resume builds
   the identical animator (`ofFloat(1f, 0f)`, 650ms, `ReturnHome`),
   `start()`, then in the same frame sets
   `animator.currentPlayTime = frozenPlayTimeMs`.** Remaining
   duration is `650 − frozenPlayTimeMs` by construction; first
   resumed frame's velocity equals the frozen velocity
   (−E′(u_f)/650); the only velocity discontinuity in the whole
   interrupt is the freeze itself, which is the finger's arrest —
   the interrupt's meaning. If the coordinator rules Fen's literal
   formula over Spule's proof, the change is two lines; this file
   will not pretend the two are the same motion.

### 4.3 Once

`resumeUsed` is set when the resume starts. A second off-cap DOWN
during the resume freezes it again (step 1), but its UP starts
**nothing**: the sled rests at the frozen fraction — INTERRUPTED-REST
(§2.1) — until the next gesture takes it by origin-drag or a new arm
clears the state. A twice-interrupted tidying has been told twice that
nobody is watching it (Fen, kept). A cap capture is not an "interrupt
count" — it is an adoption; it may happen after a resume and still
adopts from the frozen fraction.

**What does not interrupt:** MOVE/UP alone (no DOWN, no event),
scroll gestures the parent intercepts before the view sees a DOWN,
accessibility clicks (no touch events — the return is 650ms and the
sled is operable again at arrival; named, accepted).

---

## 5. SILENCE — TOTAL, AUDITED AGAINST EVERY INHERITED FEEL PATH

Fen's amendment stands: no haptic, no sound, on the return, on the
resume, or on arrival — including the arrival knock his filing carried.
The audit obligation is the negative: prove no *existing* path fires.

### 5.1 Every feel site in the view, and its guard

| Site | Fires when | Guard under E6 |
|---|---|---|
| `fireFeel(4L)`, DOWN `:1164` | every DOWN today | The §4.2 interrupt branch returns `true` **before** this line whenever `isReturning`. The freezing DOWN — cap or off-cap — is silent, even when it becomes a drag |
| `fireFeel(8L)`, gate crossing in `updateDrag` `:1205` | frac crosses 0.82 either way | Reachable only via MOVE. During RETURNING there are no MOVEs (no drag is active). After a cap capture, MOVEs are the **new gesture's** — its gate feel is arm-path law (Ivo/Orson), out of E6's silence scope, and stated as such so no test flags it. The crossing test itself reads the frozen fraction (`wasPastGate` from current `dragFraction`, `:1200`), so capture cannot misfire it |
| `fireFeel(1L, …)`, tick buckets `:1209` | bucket change in `updateDrag` | Same reachability as above; §4.2's `lastTickBucket` seeding prevents a spurious first-MOVE tick after capture |
| Seal `playHarmonicSeal(true)` / `fireFeel(10L)` `:1184` | UP-commit | Only on a gesture that *commits* — that sound is the new arm's seal, not the return's. Explicitly out of scope, named so the silence test (§8 T4) scopes to return/resume/arrival and the interrupting DOWN/UP, not to a subsequent arm |
| `animateSpringBack` `:1214–1225` | refusals | Fires no feel today (verified). E6 does not call it on any return path |
| Base `fireFeel` / `fireWeightedKnock` defs `:204–257` | — | Definitions, not call sites; the return path adds no call to either |
| VM `navigateTo` detent `playRotaryDetent(540.0)` `:371` | navigation | Pre-existing nav sound, not a return event. E6's VM additions (`publishReceipt`, gate calls, void) contain **zero** `soundEngine` references — grep gate §8 T6 |

### 5.2 The return path's own construction

`animateReturnHome()` and its listeners contain no `fireFeel`,
`fireWeightedKnock`, `soundEngine`, or `play*` token — by construction,
and pinned by a source-level test (§8 T6) plus a behavioural one (T4:
with `soundEngine = null`, `fireFeel` falls back to the raw vibrator —
Robolectric's `ShadowVibrator` therefore catches *any* feel the return,
freeze, or resume might fire, engine or no engine). Arrival's end
listener does exactly one thing: `isReturning = false`. Motion stops;
nothing replaces it. The host effect and gate are likewise silent (§5.1,
last row).

---

## 6. ALDO'S INTERFACE — INSERTION POINTS

Aldo's filed print machine (`slice-de-parts/aldo.md` §3b): S0 REST =
`SLIDE OR TAP TO START` (Mutter's words, plate setting, on the track
face in the shipped label's home); S5 SEALED = no print. E6 needs two
things from that machine and offers one back.

### 6.1 Idle cap words swap in at receipt — insertion point

`TactileDeckScreen.kt` `update{}` block (`:494–500`): E6's branch is
the **last writer of `labelText`** in the block:

```kotlin
view.labelText = if (receiptState != null) ALDO_S0 /* "SLIDE OR TAP TO START" */
                 else <D10 print-state assignment>   // interim, vs today's trunk: "SLIDE TO ARM SESSION" (:497)
```

Timing proof: `receiptState` is collected at the root (MainActivity,
§7 sites) and passed down, so publication triggers a recomposition
whose `update{}` runs within a frame of the emission — the swap lands
at publication + ≤1 frame, a full detention (1,000ms) before the return
starts. Under ghost retention this is the live view's swap; under
today's unmount regime the swap is latent in the host parameter and
applied by the factory/`update{}` of the view created at receipt —
in both regimes the sled never travels wearing a sealed-era word.
**Alpha needs nothing:** the `:1111` law is a pure function of
fraction, so the idle words fade in as the sled comes home (invisible
at f ≥0.769, full at 0) — Fen's arrival print. One flag for Aldo: if
D10 scopes that fade to hand-drags only (his S1 scoping; SLICE-D-E
R5), the gate at `:1111` must read `(isDragging || isReturning)` —
during the return the fade is a *reveal* (the sled uncovers the words
as it leaves), not drag occlusion. E6 prices the `|| isReturning`
disjunct inside its own view item (§7) so Aldo owes nothing.

### 6.2 Accent trail suppressed while returning — insertion point

`BlockitCoreViews.kt:1102`: `if (dragFraction > 0.01f)` →
`if (dragFraction > 0.01f && !isReturning)`. One condition, no new
state beyond the `isReturning` flag §4 already needs. Boundary, stated:
suppression binds **while returning only**. During RECEIPT detention
(f = 1, seated) the trail still draws the full travelled track — after
E5's drain at α55 in terminal ink, not accent (§6.3) — and that
detention rendering is E5's surface, not E6's to suppress.

### 6.3 The gap nobody filed: the cap's colour at arrival

Mara's drain (E5) steps the cap fill to `tokControlFill()` (day
`#252323` / night `#4A423C`) at seal +150ms, terminal, "the cap does
not fade, shrink, or spring home" (`slice-de-parts/mara.md` B1).
Fen's §3b.3 — "the cap itself stays accent-filled, per F-ORANGE the cap
is the accent object at rest" — was written against a cap that never
drained. Both cannot hold through the return: a sled that travels home
in terminal ink and *pops* orange at f = 0 commits, in colour, the
exact teleport E6 exists to abolish in position; a sled still ink at
rest contradicts the rest grammar (accent cap = the Deck's one accent
object, Aldo §3a inventory).
**E6's ruling-shaped recommendation (owner: E5/D-build, one line of
E6's):** inside `animateReturnHome()`'s first frame, the view resets
its cap-tone state to rest — the drain field the D-build introduces
beside `:1133` (interim vs today's trunk: the §2.2 colour flag, set to
its rest value). The pop, if any, hides inside motion onset; the sled
travels home *as* the rest object and arrives already true. Priced +1
line inside §7 item V3. Acceptance T7 pins both ends: fill at detention
== control fill; fill at arrival == `tokAccent()` (day and night). If
the D-coordinator instead rules the rest cap stays ink, T7 flips and
the +1 line dies — E6's motion is indifferent; the lie is not.

---

## 7. CODE SITES AND RE-PRICE

### 7.1 Sites (against today's trunk)

| File | Site | Change |
|---|---|---|
| `ui/theme/MonolithCurves.kt` | after `:27` | Add **only** `ReturnHome = PathInterpolator(0.3f, 0f, 0.5f, 1f)` (+1 line). **No `DURATION_650` token and no Compose `EasingReturnHome` twin** (Spule L-4 / T-NOTWIN, `11-spule.md` §5/§7): the 650ms lives as a literal at the animator call site in the view, so it can never be unified with the Shield bank-return's 650ms spring or re-owned from Compose. The curves header's "strictly chosen from" vocabulary amendment is the E-curves commit's (Cato/Spule), not E6's |
| `ui/views/BlockitCoreViews.kt` | `:1064–1072` | Setter loses the position side-effect (§2.2) |
| | `:1075–1078` | Constructor `initialFraction`; fields: `returnAnimator`, `isReturning`, `resumeUsed`, `pendingResume`, `dragOriginFraction`, `dragOriginX`, `returnPlayTimeMs`, `frozenPlayTimeMs` |
| | `:1102` | Trail suppression (§6.2); `:1111` fade gate `|| isReturning` (§6.1) |
| | `:1159–1193` | DOWN interrupt branch + hit-test (§4); origin-relative MOVE; UP resume (§4.2) |
| | after `:1225` | `animateReturnHome()` (650ms literal at the animator) + `resumeReturnHome()` (identical animator, seeked to `frozenPlayTimeMs`, §4.2) + freeze helper; `onDetachedFromWindow` (§2.4) |
| `ui/MonolithViewModel.kt` | `:49–75` area; body `:369`, `:909`, `:1108` | `ReceiptState` + `SledReturnGate`; `_receiptState` flow; `publishReceipt` + expiry job; gate wrappers (`witness`/`tryStart`/`void`); `navigateTo` void hook; arm clears receipt |
| `ui/screens/TactileDeckScreen.kt` | `:488–503` | Params (§7.2 note); factory takes `initialFraction` + captures `slideView`; `update{}` loses `:496`, gains §6.1 label branch as last label writer; `onRelease` voids the gate; `LaunchedEffect` scheduler (§3.2) |
| `MainActivity.kt` | `:99` area; `:293` | Collect `receiptState`; pass `receiptState`, `sledInitialFraction` (from `viewModel.sledInitialFraction()`), and the two gate callbacks into the Deck call |

### 7.2 Price — net lines vs today's trunk (production)

| # | Item | Net |
|---|---|---|
| V1 | Setter de-fanged (§2.2) | **−2** |
| V2 | Constructor param + 8 fields (incl. the clock-phase pair, §4.2) | **+8** |
| V3 | `animateReturnHome` + freeze helper + cap-tone reset line (§6.3) | **+25** |
| V4 | `onDetachedFromWindow` (§2.4) | **+6** |
| V5 | DOWN interrupt branch + slab hit-test (§4) | **+16** |
| V6 | Origin-relative MOVE + UP/CANCEL resume + origin clear | **+9** |
| V7 | Trail condition + fade-gate disjunct | **0** (modified lines) |
| | **View subtotal** | **+62** |
| M1 | `ReceiptState` + `SystemClock` import | **+4** |
| M2 | `SledReturnGate` pure class (§3.3) | **+20** |
| M3 | Flow + `publishReceipt` (incl. expiry job) + gate wrappers + `sledInitialFraction()` | **+21** |
| M4 | `navigateTo` void hook + arm-clear | **+4** |
| | **VM subtotal** | **+49** |
| D1 | Deck params (`receiptState`, `sledInitialFraction`, 2 gate callbacks) | **+4** |
| D2 | Factory/`update{}`/`onRelease` rework (incl. −1 for the `:496` deletion, +§6.1 branch) | **+5** |
| D3 | `LaunchedEffect` scheduler (§3.2) | **+20** |
| D4 | `slideView` remember + factory capture | **+2** |
| | **Deck subtotal** | **+31** |
| A1 | MainActivity collect + pass-through | **+4** |
| C1 | `MonolithCurves`: `ReturnHome` interpolator only (L-4: no token, no twin) | **+1** |
| | **PRODUCTION TOTAL, standalone vs today** | **+147** |

**Overlap with the Slice D slide rewrite** (build order: slide rewrite
is step 2, E6 step 5 — `SLICE-D-E-BUILD-READY.md` §5): D1/D5 kill the
teleport (origin drag, −5 of V6) and build the slide's hit-testing
(−8 of V5). Built after them, E6's marginal price is **+134 net**.
Coordinator's union counts the slide once either way; both numbers are
stated so nobody double-books the shim.

**Tests: +120** (§8: gate unit tests +50, Robolectric view tests +70).
No migration, no persistence, no new dependencies.

### 7.3 Why the filed ≈102 + ~60 was short (named, not padded)

Fen priced his VM-job sketch: no detach override (his law, unpriced,
+6), no origin shim (his capture rule presumed Ines's unbuilt slide,
+9 gross), a single `returnRunForSessionId` var where the unwitnessed
law actually needs a 4-stage gate with witness/expiry (+20 class, but
it *deletes* the need for a view registry), and ~60 test lines that
assume a fraction seam the private field doesn't provide. Silence,
interrupt, and the background-publication hole are all in his filed
*laws*; this price is those laws, costed.

---

## 8. ACCEPTANCE TESTS

Pure JUnit (gate — new `SledReturnGateTest`, unit source set):
- **T1** arm → `tryStart` before deadline is host's concern, gate's is
  once-only: first `tryStart` true, second false, `void`-then-`tryStart`
  false, `expire` unwitnessed → VOID, `expire` witnessed → stays ARMED,
  duplicate `publishReceipt` (VM test or gate arm-idempotence) keeps the
  first deadline.
- **T2** `initialFraction` truth table, all branches of §2.3: running →
  1f; receipt+ARMED → 1f; receipt+FIRED → 0f; receipt+VOID → 0f; null
  receipt, not running → 0f (the §3.4 P4 case).

Robolectric (view — extends the tree's existing Robolectric harness):
- **T3** Return completes: view at `initialFraction = 1f`,
  `animateReturnHome()`, advance the shadow choreographer/looper in
  16ms steps past 650ms → fraction (read by reflection; the field stays
  private in production) == 0f exactly, `isReturning` false.
- **T4** Silence: `soundEngine = null` (vibrator fallback armed),
  `ShadowVibrator` asserts **zero** vibrations across: full return,
  cap-capture freeze, off-cap freeze + resume, second-interrupt rest.
- **T5** Single-writer: during RETURNING, call the host `update{}`
  assignments the Deck performs (night, engine, label, callbacks) →
  fraction trajectory unchanged frame-by-frame; and a grep gate:
  `initialFraction` appears only in the factory block and the view
  constructor — never in `update{}`.
- **T6** Source gates (Teo-style assertions): `ReceiptState` referenced
  in exactly {VM, Deck, MainActivity}; no `ReceiptState`/gate symbol
  in `data/` or any persistence file; E6's VM additions contain no
  `soundEngine` token; `animateReturnHome` body contains no
  `fireFeel`/`fireWeightedKnock`/`play` token.
- **T7** Interrupt geometry + resume phase, dispatched `MotionEvent`s
  at the §4.1 worked example (w = 336dp): DOWN at slab centre →
  capture, fraction unchanged by the DOWN itself, first MOVE is
  origin-relative (no teleport); DOWN at x = 100dp with the return
  frozen at playTime 276ms (f = 0.500 ± 0.005, velocity −2.56 frac/s —
  Spule's T-E6-RESUME numbers, adopted) → fraction constant ±0.0005
  during the hold; UP → resume: first resumed frame at
  1 − E((276 + frameMs)/650) ± 0.005, finite-differenced velocity
  within ±15% of −2.56 frac/s and not within ±0.2 of 0 (kills the
  fresh-animator and 650·f implementations outright), arrival at
  resume + 374ms ± 1 frame; second off-cap DOWN/UP → no animator,
  fraction rests.
- **T8** Cap tone (§6.3): fill at detention == `tokControlFill()`;
  fill on the arrival frame == `tokAccent()`, day and night.
- **T9** Process-death construction (no emulator death needed): fresh
  gate + null receipt + not-running → §2.3 yields 0f and the Deck
  effect body with a null key schedules nothing — the P1–P5 chain as a
  unit test over the gate and the factory rule.

Device (one pass, shared with the F2 A/B gate): receipt → sled holds
1,000ms → travels 650ms → rest; interrupt it once by cap, once off-cap;
sound on, phone on a hard surface — nothing heard, nothing buzzed, at
return, resume, or arrival.

---

## 9. RISKS, DEPENDENCIES, MAIL

1. **E4 upstream (the real risk, Fen's, kept):** the finish choreography
   (held frame, CUT E1200, receipt ≤E1800) does not exist in code. E6's
   animator, gate, and rest contract are anchor-independent and survive
   any re-timing that still ends in `publishReceipt`. If no receipt is
   ever built, E6 must not be bolted onto the ticker branch (`:343–349`)
   — a return timed from a 1s-quantised detection answers nothing.
2. **Slice D slide rewrite ordering:** if D1/D4/D5 land first, V5/V6
   shrink (§7.2) and the seat-animator ownership at commit is theirs;
   if E6 is built first on today's view, the §2.1 ARMING row's
   dependency (spring-back-on-commit dead) must be enforced by hand or
   the contract is void on arrival.
3. **Ghost retention (Wren A2) vs today's unmount:** E6 is correct
   under both (§6.1 timing proof covers each), but the *experience*
   differs — under unmount, the seated sled the user returns to was
   never visibly seated during the session. A W4 loss, not E6's.
4. **G1/G2 (§3.4)** ride on Zettel's answer to the letter below.

**Mail:** two letters sent — to **Zettel** (`mail/zettel/`, thread
`receipt-contract-e6`), contract clauses: **C1** E4 calls
`publishReceipt(sessionId)` and nothing else; **C2** the VM samples
`elapsedRealtime` at emission — E4 never supplies a timestamp;
**C3** the timestamp/state is never persisted or restored (G1);
**C4** the relaunch-receipt path (G2) needs Zettel/Ren's reading —
fresh publication (return arms) vs suppressed. And to **Spule**
(`mail/spule/`, thread `sled-clock-ownership`): L-4 adopted (no
`DURATION_650`, no Compose twin, animator built in `SlideToStartView`
only — never the suitcase file), his §6.2 time-offset resume adopted
as §4.2's amendment (his F-S1 has the sled seat's support), and one
mechanical divergence flagged for his §5 summary — the +1000ms
*firing* sits in the Deck host's effect, not the VM, because a VM
job survives navigation and cannot reach the view; the VM keeps
publish, gate, and expiry. His clock-ownership ruling (view owns the
650ms clock) stands untouched by that split. No reply owed from
Aldo: §6's insertion points are unilateral and priced on my side.
Wall: signed note appended (with a correction note for the resume
amendment, which post-dates the first wall post).

— Schlitten, Improvers · app sleds
