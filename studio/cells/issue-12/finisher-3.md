# FINISHER-3 — Issue #12, Tasks tear finish: the F-7 respin, to the millimetre

*Cell: AMT (lead), issue #12, Sarthak-2007/Focus-app (Monolith). Seat:
Finisher-3 (tear finish). Paper only — no code edited, nothing compiled,
no Gradle/tests run. This seat specifies; the Builder lands it.*

*Sources read in full: `studio/plan/AUDIT-tasks.md` items 8, 13, 17 and
fix F-7; `studio/plan/PLAN-issue-12-tasks-f1.md` (ZIP-TEAR ruling);
the tear as built on branch `agent/issue-12` —
`FocusSequencerScreen.kt` (814 lines at filing — see the drift note below) and
`app/src/main/java/com/example/ui/theme/MonolithCurves.kt`.
Curve values below were computed from the Bézier control points in
code, not eyeballed.*

**Scope, exactly:** the F-7 respin only, plus the unified perforation
tone the brief assigns to this slice (audit item 17). NOT in this
slice: the TEAR rename and 48dp target (item 13's other half, priced
under F-4), ZIP-TEAR (see §4), pad-edge re-anchoring / leaf gap /
foot rules (F-8's remainder). The header-count rewire (F-3) landed
in this file *while this seat was filing* — §2 R-6 records what
that landing did to the ghost window.

*Drift note: line numbers below are verified against the
814-line post-F-3 file. Sibling slices are landing in the same
file; if a number has drifted by a few lines when the Builder
reads this, the symbol and snippet beside it are the anchor.*

---

## 0. Verification — every Weiss number against the code as built

Weiss's charge and spec, checked line by line. Every charge lands.

| # | Weiss / audit | Code as built (verified) | Spec (finished) |
|---|---|---|---|
| V-1 | Curve A spends 0.878 of the fade by 90 ms | `tearProgress`: `specA(DURATION_180)` at FocusSequencerScreen.kt:480–484. Curve A = `CubicBezierEasing(0.2, 0, 0, 1)` (MonolithCurves.kt:55). Computed: at 90 ms the clock fraction is 0.5 and the easing returns **0.878** — the audit's figure reproduces exactly from the control points | Accelerating cubic, §1 C-4 |
| V-2 | Drop 10dp against a 12dp inter-leaf gap | :524 `translationY = tearProgress * 10.dp.toPx()`; LazyColumn gap 12dp (:167, `spacedBy(12.dp)`) — the leaf falls less than the distance to the next leaf; it never clears anything | **30dp** = 2.5× the gap |
| V-3 | Alpha reaches 0 at 180 ms — the tear's second half is invisible by construction | :523 `alpha = 1f - tearProgress`, same 180 ms animator. At 90 ms alpha is already 0.122 | Alpha **floored at 0.40**: `1f - 0.6f * p`, never 0 in-window |
| V-4 | Dashes settle over 260 ms (curve B) *inside* the fading layer | Second animator `dashSettle`, `specB(DURATION_260)` at :485–489 — curve B is even more front-loaded than A (computed 0.95 at its midpoint). And `PerforationRow` is the **first child of the fading Column** (:529, inside the `graphicsLayer` at :522–525), so the dashes fade and translate with the body they are supposed to detach from | One 300 ms clock; perforation **outside** the layer, §1 C-5/C-6 |
| V-5 | Two clocks, 180 + 260, neither equal to the ghost's life | Ghost held by `delay(DURATION_260)` at :118–123. Three durations run one event: 180 (body), 260 (dashes), 260 (ghost) | **One clock: 300 ms** for body, dashes, and ghost |
| V-6 | Head leaf's cut is paradoxically the weakest (item 17) | PerforationRow :765–769: `raised` (head) → `scheme.background` at full alpha, measured **1.23:1 day / 1.35:1 night**; queued → `outlineVariant @ 0.30`, measured **1.50 / 1.56:1**. The comment at :758–764 claims the head's cut is "at full strength" — full alpha of the *ground's own tone* on bright paper is precisely why it vanishes | **One tone for all leaves: `outlineVariant @ 0.55`** (audit-measured 2.18–2.46:1) |
| V-7 | The paper does not move — only the print does | Modifier order at :521–525: `.background(paper)` sits **before** `.graphicsLayer { }` in the chain, so the paper rectangle is drawn *outside* the layer. Today the title/plate/verbs fade and slide over a stationary, fully opaque sheet of their own paper | Paper travels and fades **with** the body, §1 C-6 |

V-7 is this seat's addition to Weiss's list: it is not a seventh
charge against the respin — the restructure in C-6 cures it as a
consequence — but any Builder who changes only the numbers inside
the existing `graphicsLayer` will leave it standing, and the result
will still read as print dissolving on paper, not paper leaving.

**What is NOT broken and must not be touched:** the booking order.
`onTearLeaf` (:149–159) dispatches `onCompleteTask` first, then the
sound, then sets the ghost (G-01, acquitted by the audit). The respin
changes motion only; no line of C-1…C-9 below moves a booking, a
sound, or the seal gate (`tear()` at :491–497, the tear verb's
absence while sealed at :626–634).

---

## 1. (a) The exact change list — line-level

All lines are FocusSequencerScreen.kt on `agent/issue-12` unless
another file is named. Snippets are the finished form.

### C-1 · One duration token — MonolithCurves.kt

Insert after :27 (`DURATION_280`), keeping the ascending vocabulary:

```kotlin
    /** F-7 (Tasks tear respin): the tear's one clock — body,
     *  dashes and ghost all live and die on this duration. */
    const val DURATION_300 = 300
```

No other duration in the tear may survive: not 180, not 260, not a
literal `300` at a call site. (The file's header law — durations
strictly chosen from the vocabulary — is why the token exists
rather than a literal.)

### C-2 · The accelerating cubic — reuse THROW, do not invent a curve

The finished animator uses **`MonolithKin.EasingThrow`** =
`CubicBezierEasing(0.5f, 0.0f, 1.0f, 0.4f)` (MonolithCurves.kt:108),
the house's existing dismissal easing, documented at :107 as
*"a dismissal accelerates away."* A tear-off is a dismissal; no new
control points are coined in this pass. One bookkeeping cost,
priced honestly: MonolithKin's header (:76–82) scopes it to the
rail and the PARK drawer — amend that header by one clause
(*"…and the Tasks tear (F-7)"*) so the scope statement stays true.
If AMT prefers the token homed in `MonolithCurves` instead, an
`EasingTear` with **identical points** is an equivalent landing;
the points and the thresholds in §3 are the law, the home is not.

Computed progress of the finished curve (the acceptance numbers):

| Time | Progress p | Travel (30dp × p) | Alpha (1 − 0.6p) | Dash (3dp × (1 − 0.5p)) |
|---|---|---|---|---|
| 0 ms | 0.000 | 0.0dp | 1.000 | 3.00dp |
| 90 ms | 0.048 | 1.4dp | 0.971 | 2.93dp |
| 150 ms | 0.136 | 4.1dp | 0.918 | 2.80dp |
| 225 ms | 0.339 | 10.2dp | 0.797 | 2.49dp |
| 270 ms | 0.561 | 16.8dp | 0.663 | 2.16dp |
| 300 ms | 1.000 | 30.0dp | **0.400** | 1.50dp |

Against today at 150 ms: travel ≈ 9.9dp of 10 with alpha ≈ 0.01
(curve A at fraction 0.833 ≈ 0.99) and the dash settle already
≈ 97% spent. The finished tear at 150 ms has barely let go. That
inversion — *slow to let go, fast to leave* — is the whole respin.

### C-3 · Ghost lifetime 260 → 300 ms — :118–123

```kotlin
    LaunchedEffect(tornTask) {
        if (tornTask != null) {
            delay(MonolithCurves.DURATION_300.toLong())
            tornTask = null
        }
    }
```

The ghost is removed **by cut at 300 ms while still at alpha
0.40** — removal, not transparency, ends it. The two clocks
(animation start at the ghost's first composition vs. this delay
starting when `tornTask` is set in the parent) start within the
same frame in practice; §3 allows ±1 frame of clip at the tail
and no more.

### C-4 · Two animators become ONE — :480–489

Delete the `dashSettle` animator (:485–489) whole. The surviving
animator, finished form:

```kotlin
    val tearProgress by animateFloatAsState(
        targetValue = if (tearing) 1f else 0f,
        animationSpec = tween(
            durationMillis = MonolithCurves.DURATION_300,
            easing = MonolithKin.EasingThrow
        ),
        label = "counterfoilTear"
    )
    // The dashes settle on the SAME progress — derived, never
    // animated: one clock cannot drift against itself.
    val dashSettle = 1f - 0.5f * tearProgress
```

Imports: add `androidx.compose.animation.core.tween` and
`com.example.ui.theme.MonolithKin`. (`specA`/`specB` lose their
only tear call sites; the helpers themselves stay — other screens
spend them.) After this change there is exactly **one**
`animateFloatAsState` in `TaskLeafItem`, and `tearProgress` is the
only animated value the tear owns.

### C-5 · The motion formulas — :522–525

Inside the body's `graphicsLayer` (relocated by C-6):

```kotlin
                .graphicsLayer {
                    alpha = 1f - 0.6f * tearProgress
                    translationY = tearProgress * 30.dp.toPx()
                }
```

- `alpha`: floor 0.40, reached only in the final frame. The leaf
  is *seen leaving*; it never dissolves to nothing in-window.
- `translationY`: 30dp, draw-only — the ghost's layout slot does
  not collapse during the window (the pad does not jump mid-tear;
  what happens at removal is §2 R-4, outside this pass).
- No rotation, no scale, no overshoot. Straight down is the ruled
  motion, not an oversight (§2 R-2).

### C-6 · Perforation lifted OUT of the fading layer — restructure :501–529

This is the structural change; the rest is numbers. Today one
Column carries background → layer → padding, with
`PerforationRow` as its first child (:529). Finished structure —
two stacked regions inside the same outer `Box`, geometry at rest
**pixel-identical** to today:

```
Box (unchanged: fillMaxWidth, testTag task_item_<id>)
├── Column   ← STATIC. No background of its own except the stub,
│     modifier: fillMaxWidth + padding(end = (leavesBehind * 3 + 8).dp)
│   ├── Stub: Box/Column, modifier .background(paper).padding(top = 4.dp)
│   │     └── PerforationRow(sealed = isSessionActive, dashScale = dashSettle)
│   └── Body: Column
│         modifier: fillMaxWidth
│             .graphicsLayer { alpha / translationY per C-5 }
│             .background(paper)          ← INSIDE the layer (cures V-7)
│             .padding(top = 8.dp, bottom = 4.dp)
│         arrangement: spacedBy(8.dp)
│         children: plate Row, title, sub-steps, verb Row — unchanged
└── Pad-edge Canvas (unchanged, matchParentSize over the whole slot)
```

Geometry accounting (top → bottom), before = after, to the dp:

| Segment | Today | Finished |
|---|---|---|
| Top padding | 4dp (Column vertical padding) | 4dp (stub top padding, static) |
| Perforation | 1dp, inside layer | 1dp, static stub |
| Perf → plate gap | 8dp (`spacedBy`) | 8dp (body top padding, moves with body) |
| Plate / title / sub-steps / verbs | `spacedBy(8.dp)` | `spacedBy(8.dp)`, unchanged |
| Bottom padding | 4dp | 4dp (body bottom padding, moves) |

Why the stub carries paper: at rest the stub's 5dp strip
(4 + 1) and the body's paper are the same token
(`surfaceBright` if `isCued`, else `surface` — the :521 expression
moves, unedited, to both regions), contiguous, seam-invisible.
During the tear the body detaches *below a stationary paper stub
that holds the perforation* — the counterfoil stays on the pad,
the leaf leaves. That is the physical read Weiss's "lifted out"
buys, and it is why the perforation must not merely exit the
`graphicsLayer` but keep its paper: a perforation floating on the
screen ground would read as a printed rule, not a cut in a sheet.

`dashSettle` is read in the static region's composition, so the
perforation redraws per frame while the tear runs — by design;
it is a 1dp Canvas, and it is the only part of the ghost meant to
be watched for the first ~150 ms.

### C-7 · Unified perforation tone — PerforationRow :758–769

Delete the `raised` branch and the comment that carries its lie.
Finished form:

```kotlin
    // One tone for every leaf (audit item 17): the cut is the
    // same mark on the head leaf as on any queued leaf —
    // outlineVariant at 0.55. The old head-leaf tone
    // (scheme.background, "a cut shows what's beneath") measured
    // 1.23:1 day / 1.35:1 night, WEAKER than the queued leaves'
    // 0.30 print: the one tearable leaf had the invisible cut.
    val color = scheme.outlineVariant.copy(alpha = 0.55f)
```

Consequences, all intended:

- The `raised: Boolean` parameter is **deleted** from
  `PerforationRow` (:752–757). Call sites: :529 drops its
  `raised = isCued` argument (the only site that passed it);
  the blank leaf's call at :363 passes nothing today and compiles
  unchanged — and its perforation strengthens to the same 0.55,
  deliberately: the held blank is the pad's next leaf and takes
  the pad's one tone.
- The sealed overprint (solid rule drawn in the same `color`
  inside the Canvas) inherits 0.55 automatically. Sealed vs.
  unsealed still reads at a glance — continuous line vs. dashes —
  by continuity, not by tone. §3 checks this explicitly.
- Live leaves change at rest: head perforation goes from
  ground-tone to `outlineVariant @ 0.55`; queued from 0.30 to
  0.55. This is item 17 landing, not a side effect.

### C-8 · Comments and KDoc — the printed lies die with the numbers

A comment that keeps the old numbers after the respin is the
exact class of lie this audit exists to kill. Rewrite, do not
append:

- :109–115 (ghost comment): "the tear's 260ms" → 300 ms.
- :417–421 (`LeafSlot` doc): "held for the tear's 260ms" → 300 ms.
- :431–433 and :445–447 (TaskLeafItem KDoc, both motion
  sentences): replace "(drops 10dp, fades, 180ms curve A; the
  torn edge's dashes halve over 260ms curve B)" with the finished
  sentence, once, in the file's existing voice: *the leaf
  detaches at the perforation — the perforation and its stub
  stay on the pad at full strength while the body drops 30dp on
  one 300 ms accelerating clock, alpha held to a 40% floor, the
  dashes halving on the same progress; the ghost is removed by
  cut at 300 ms, never by reaching transparency.* The G-01
  paragraphs (booking order) stay untouched.
- :748 (PerforationRow doc, "[dashScale] is the torn edge
  settling (3dp → 1.5dp) during a tear"): still true — keep,
  add only that the row now lives outside the fading layer.
- :758–764: deleted by C-7.

### C-9 · Line tally, honestly

Weiss priced the respin at ~15 lines; that priced the numbers.
The lift-out restructure (C-6) is real additional work in the
same file: ≈ +12/−4 to re-home the perforation, stub, and body
background. Tally: MonolithCurves.kt +3 (token; +1 clause if the
THROW scope amendment lands there), FocusSequencerScreen.kt
≈ +25/−20 including comment rewrites, net motion logic ≈ 15 as
priced. No other file changes. No schema, no ViewModel, no
repository change. **Status of this slice: SPEC-LEVEL** — none of
C-1…C-8 is in the clone yet (the tear code is untouched by the
F-1/F-3 landings that reached the branch during filing); `grep DURATION_300 app/src/` returns
zero hits at the time of filing, and §3 is written to be run
against the Builder's diff when it lands.

---

## 2. (b) What would still read as FADE rather than TEAR after the respin

The respin is a mitigation with a floor, not a transformation.
Finished honestly means naming what survives it:

- **R-1 · A 60% alpha fall is still a fade component — capped,
  not cured.** Weiss's own spec holds alpha to a 40% floor; the
  leaf still goes translucent as it leaves, and translucent text
  sliding over the successor leaf beneath it is the visual
  signature of a fade. The respin makes travel dominate (30dp,
  back-loaded) and ends the ghost by cut, so the *balance* of the
  read flips — but a reviewer who expects opaque paper ripping
  will not see it in this pass. That read belongs to ZIP-TEAR
  (§4). Do not "fix" R-1 by re-tuning past Weiss's numbers.
- **R-2 · No pivot, no sag.** The body translates straight down,
  parallel, both ends level. Real detached paper tips from one
  end. A level slide can read as a card being dismissed. Ruled,
  not missed: rotation is outside the respin and the feel seat's
  table for this motion forbids rotate/scale outright.
- **R-3 · The settle is uniform across the width.** All dashes
  shorten simultaneously, everywhere. A real tear *propagates* —
  separation starts somewhere and runs. Uniform settling reads,
  at worst, as an edge dissolving evenly. Progressive consumption
  behind a finger is ZIP-TEAR's mechanism, next build.
- **R-4 · The landing snaps.** The ghost's slot does not collapse
  during the window (correct), but at 300 ms the slot vanishes in
  one frame and every leaf below jumps up by a full leaf height
  + 12dp gap; the successor also gains the head's raised paper in
  that same frame, untransitioned. The tear itself will read
  true; the instant after it is a teleport, and it can
  retroactively cheapen the tear. No successor-rise or
  item-animation is priced in F-7 — flagged here for AMT/Ren,
  **not** silently absorbed into this slice.
- **R-5 · The sound arrives ~200 ms before the separation is
  visible.** `playTearPulse()` fires at the tap (G-01 order,
  acquitted — do not move it). On the accelerating curve the
  leaf has moved 1.4dp at 90 ms and 4.1dp at 150 ms: the ear
  hears "torn" while the eye still sees an intact leaf, with
  the visible leave compressed into the last ~100 ms. Inherent
  to tap-sound + accelerating curve; a device judgement (§3),
  not a code change in this pass.
- **R-6 · The header count — CURED mid-filing by F-3, verified.**
  When this seat began, the header count derived from
  `orderedQueue.size`, which drops at the tap: for the ghost's
  whole life the header declared one leaf fewer than the pad
  visibly held (audit item 8). The Builder's F-3 landing reached
  this file while this spec was being written: the QUEUE header
  now prints `displayQueue.size` (FocusSequencerScreen.kt:286,
  ghost included — the same list `#NN` numbers, :131–138,
  :402, :560–562). Item 8's ghost-window freeze is therefore
  already true in the branch, and widening the window to
  300 ms costs nothing. §3 D-9 keeps the check as a regression
  guard, not a dependency.
- **R-7 · The final surge is ~3 frames on a 60 Hz device.**
  THROW puts 44% of the travel (16.8dp → 30dp) into the last
  30 ms. At 120 Hz that is smooth acceleration; at 60 Hz it is
  roughly two frames and may strobe as a snap. Mitigated by the
  removal cut landing in the same frame — but it is the first
  thing to check on the actual phone (§3, step D-3).

**Verified NOT fade-traps after the respin:** the perforation
stays at full unified strength for the entire window (alpha 1 ×
0.55 tone, static position); the dash settle completes exactly
at the clock's end, in the open; the body never reaches alpha 0
in-window; no second clock exists to drift against the ghost's
removal.

---

## 3. (c) Acceptance checklist — run against a device build later

### Static (against the Builder's diff, before any device)

- [ ] `grep -n "DURATION_180\|DURATION_260\|specA\|specB" FocusSequencerScreen.kt` → **zero hits in tear code**; `grep -rn "DURATION_300" app/src/` hits exactly: the token, the ghost delay, the one animator.
- [ ] Exactly **one** `animateFloatAsState` inside `TaskLeafItem`; `dashSettle` is a derived `val`, not an animator.
- [ ] `graphicsLayer` reads exactly: `alpha = 1f - 0.6f * tearProgress`, `translationY = tearProgress * 30.dp.toPx()`; easing is THROW's points (0.5, 0, 1.0, 0.4) wherever the token is homed.
- [ ] `PerforationRow` is **not** a descendant of any `graphicsLayer` subtree; its colour is the single expression `outlineVariant.copy(alpha = 0.55f)`; no `raised` parameter or `scheme.background` branch remains anywhere in the file's perforation code.
- [ ] Rest geometry: screenshot a 3-leaf queue before/after the diff, overlay — plate rows, titles, verb rows pixel-identical; only the perforation tone differs.
- [ ] No KDoc/comment in the file still prints 10dp, 180 ms, 260 ms, curve A, or curve B for the tear (C-8 list).

### Device (phone build; screen-record at the highest refresh available, step through frames)

Setup: queue 3 named leaves. Repeat the whole D-list in **day
and night**, because every convicted number in this slice was
tone- or curve-dependent.

- [ ] **D-1 Travel & clock.** Tear the head leaf. From the tap frame: leaf still present at 270 ms; gone at 300 ms (±1 frame). Total body travel ≈ **30dp ± 2dp** (measure title baseline, first frame vs. last frame before removal).
- [ ] **D-2 Acceleration.** Travel by 150 ms ≤ **6dp** (spec: 4.1dp) and alpha at 150 ms still ≥ **0.85** (spec: 0.918) — title fully legible at half-time. If the leaf is half-gone at half-time, the old curve is still in the build.
- [ ] **D-3 The leave.** In the last ~100 ms the leaf visibly *accelerates* away and is removed **while still plainly visible** (alpha floor) — a cut, not a dissolve. Note any strobe/snap on this device (R-7) and report it as a device fact, not a tuning request.
- [ ] **D-4 The perforation stays.** Through the whole recording: the torn leaf's perforation row does not move (pixel row constant), does not fade, and its dashes visibly shorten (3dp → ~1.5dp). The stub strip above the departing body stays on the pad.
- [ ] **D-5 One tone.** Screenshot head leaf and a queued leaf at rest, both themes: perforations indistinguishable in tone; the head's cut is at least as visible as the queued leaves' — the item-17 inversion is gone. Sealed session: perforation reads solid/closed at a glance against the unsealed dashed leaves.
- [ ] **D-6 Middle and sole leaves.** Tear a middle leaf (motion identical; neighbours do not move during the window) and the sole leaf (ghost plays over the held blank's address; blank leaf returns only after the ghost's cut).
- [ ] **D-7 Booking truth (G-01 regression guard).** Tear, then immediately navigate away mid-tear: the completion is already booked (LAST-FINISHED shows the leaf; reopen works). Kill/restart the app mid-tear on a second run: the task does not resurrect.
- [ ] **D-8 Seal.** With a session active: no TEAR verb on any leaf, perforation overprinted solid, nothing in D-1…D-5 can be triggered.
- [ ] **D-9 Count honesty (regression guard, R-6).** During a tear, count the visible leaves and read the QUEUE header: they agree, ghost included, for the whole 300 ms — F-3 landed this in the branch; a disagreement here is a regression in either landing.
- [ ] **D-10 Sound order.** Tear pulse at the tap, once; no second sound at removal.

---

## 4. Scope guard — ZIP-TEAR does not enter this pass

The plan's ruling stands and this slice obeys it: ZIP-TEAR's
85%-crossing standard (finger traces the perforation in a 32dp
band, dashes consume behind the finger, booking at 85% in G-01
order, tap retained as the accessibility floor) is the house
standard for the **next** tear build. It is not implemented,
specced further, or priced here. This pass carries the F-7
respin only — the tap-tear, finished. Nothing in §1 borrows
ZIP-TEAR's gesture, band, or threshold, and nothing in §2's
residual list is an argument to pull it forward.

**Status: DECLARED** — specified to the millimetre, verified
against the branch as built, no code edited, nothing compiled.

— Finisher-3 (issue #12 cell)

## Ready-to-post comment for issue #12 (AMT posts this)

F-7 tear respin — finished to the millimetre (Finisher-3, tear finish). Full spec: `finisher-3.md` in the cell work dir. Paper only, DECLARED — no code edited, nothing compiled.

**Verified against the branch as built** (`FocusSequencerScreen.kt`): every one of Weiss's numbers reproduces from the code. Curve A returns exactly 0.878 at 90 ms (the audit's figure, computed from its control points); alpha `1f − p` over 180 ms; drop 10dp against the 12dp leaf gap; a second animator settling the dashes over 260 ms *inside* the fading layer — `PerforationRow` is the fading Column's first child. Two further finds: (1) the paper itself never moves today — `.background()` sits before `.graphicsLayer` in the modifier chain, so the print fades and slides over a stationary sheet; (2) the head leaf's perforation is `scheme.background` at full alpha (1.23:1 day / 1.35:1 night) against the queued leaves' `outlineVariant @ 0.30` (1.50 / 1.56:1) — the comment calling the head's cut "full strength" is the inversion's hiding place.

**The finished respin (C-1…C-9, line-level in the spec):** one new token `DURATION_300`; ghost lifetime 260 → 300 ms; the two animators collapse to one — dashes become a derived value (`1f − 0.5f·p`) so no second clock can drift; easing is the house's existing THROW (`CubicBezierEasing(0.5, 0, 1.0, 0.4)`, "a dismissal accelerates away" — no new curve coined; computed profile: 1.4dp travelled and alpha 0.971 at 90 ms, 4.1dp / 0.918 at 150 ms, 30dp / 0.40 floor at 300 ms); travel 10dp → 30dp; alpha `1f − 0.6f·p`, floored at 0.40, ghost removed by cut, never by transparency. Perforation lifted out of the layer as a paper stub (top 4dp + the 1dp row, static, same paper token) with the body — background now *inside* its `graphicsLayer` — detaching below it; rest geometry is pixel-identical, segment-by-segment accounting in the spec. Unified tone: `outlineVariant @ 0.55` for all leaves, the `raised` branch and its comment deleted (blank leaf's perforation takes the same tone deliberately). KDoc/comments carrying the old numbers are listed for rewrite, not append. Tally stated honestly: Weiss's ~15 lines prices the motion logic; the lift-out restructure is ≈ +12/−4 more in the same file.

**Still-fade residuals, named so nobody oversells the respin:** a 60% alpha fall is a capped fade component, not a cured one (R-1); no pivot/sag (R-2, ruled out, not missed); dash settle is uniform across the width — no propagation (R-3); the landing still snaps — ghost slot vanishes in one frame, no successor rise is priced in F-7 (R-4, flagged for AMT/Ren, not absorbed); the tear sound precedes visible separation by ~200 ms on the accelerating curve (R-5, G-01 order, acquitted); the ghost-window header count, which F-3's landing cured in this file while this spec was being filed — the QUEUE header now counts `displayQueue`, ghost included, so item 8's print freeze is already true and the widened window costs nothing (R-6); THROW's final surge is ~3 frames at 60 Hz (R-7, device-check item).

**Acceptance (§3 of the spec):** static greps (zero `DURATION_180/260`, `specA/specB` in tear code; exactly one animator in `TaskLeafItem`; perforation outside any `graphicsLayer` subtree) plus a frame-stepped device protocol in day and night — travel 30dp ± 2, removal at 300 ms ± 1 frame, travel ≤ 6dp and title legible at 150 ms, perforation pixel-stationary with visibly halving dashes, one tone head-vs-queued, G-01 booking regression (navigate-away and process-death mid-tear), seal gate intact.

ZIP-TEAR: untouched, per the lead plan's ruling — the audit's 85%-crossing standard stands for the next tear build; this pass carries the F-7 respin only.

— Finisher-3 (issue #12 cell)
